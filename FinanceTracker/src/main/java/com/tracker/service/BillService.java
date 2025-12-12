package com.tracker.service;

import com.tracker.DTO.BillDTO;
import com.tracker.entity.Bills;
import com.tracker.entity.ExpenseTransactions;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.error.ReusableConstants;
import com.tracker.repo.BillRepo;
import com.tracker.repo.CategoryRepo;
import com.tracker.repo.PaymentMethodRepo;
import com.tracker.repo.ExpenseTransactionsRepo;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import net.mguenther.idem.flake.Flake64L;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class BillService {
    @Autowired private BillRepo billRepo;
    @Autowired private ModelMapper modelMapper;
    @Autowired private Flake64L idGenerator;
    @Autowired private UserService userService;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private PaymentMethodRepo paymentMethodRepo;
    @Autowired private CategoryService categoryService;
    @Autowired private ExpenseTransactionsRepo expenseTransactionsRepo;
    @Autowired private ExpenseService expenseService;

    public List<BillDTO> getBills(Long userId){
        return billRepo.getBillsByUserId(userId,0L).stream().map(this::handleDTO).toList();
    }

    public BillDTO addBill(Map<String, ?> input) {
        if ( !((Boolean) input.get(ReusableConstants.IS_RECURRING)) && ((Boolean) input.get(ReusableConstants.IS_RECURRING)).toString().equals(Bills.Frequency.NONE.name())) {
            throw new FinTrackerException(ErrorConstants.RECURRING_BILLS_MUST_HAVE_FREQUENCY);
        }
        Bills bill = new Bills();
        bill.setBillId(idGenerator.nextId());
        bill.setUserId(userService.getCurrentUserId());
        bill.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        fillTheBillDetails(bill,input);
        return handleDTO(billRepo.save(bill));
    }

    public BillDTO updateBill(Long billId, Map<String, ?> input) {
        Bills existingBill = billRepo.findByUserIdAndBillId(billId, userService.getCurrentUserId(), 0L);
        if(existingBill.getBillId() == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);
        existingBill.setUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        fillTheBillDetails(existingBill, input);
        return handleDTO(billRepo.save(existingBill));
    }

    public Long softDelete(Long billId) {
        billRepo.softDeleteBill(userService.getCurrentUserId(),billId,LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        return billId;
    }

    public LocalDate calculateNextDueDate(LocalDate lastDueDate, Bills.Frequency type) {
        if (lastDueDate == null) return null;
        int interval = 1;
        return switch (type) {
            case WEEKLY -> lastDueDate.plusWeeks(interval);
            case MONTHLY -> lastDueDate.plusMonths(interval);
            case QUARTERLY -> lastDueDate.plusMonths(3L * interval);
            case HALF_YEARLY -> lastDueDate.plusMonths(6L * interval);
            case YEARLY -> lastDueDate.plusYears(interval);
            default -> null;
        };
    }

    @Transactional
    public BillDTO markBillAsPaid(Long userId, Long billId, Double amountPaid, Long paymentMethodId, LocalDate paidDate) {
        Bills bill = billRepo.findByUserIdAndBillId(billId,userId, 0L);
        LocalDate nextDue = bill.getDueDate();
        boolean alreadyPaid = expenseTransactionsRepo.existsByUserIdAndBillIdAndDateBetween(userId, billId, paidDate.withDayOfMonth(1), paidDate.withDayOfMonth(paidDate.lengthOfMonth()));
        if (alreadyPaid) throw new IllegalStateException("This bill was already paid for " + nextDue.getMonth() + " " + nextDue.getYear());
        if(bill.getAutoCreateExpense()) {
            createExpenseFromBill(bill,paymentMethodId,amountPaid,paidDate,userId);
        }
        if (bill.isRecurring() && bill.getFrequency() != Bills.Frequency.NONE) {
            bill.setDueDate( calculateNextDueDate(nextDue, bill.getFrequency()));
            billRepo.save(bill);
        }
        return handleDTO(bill);
    }

    private void createExpenseFromBill(Bills bill,Long paymentId,Double amountPaid,LocalDate paidDate,Long userId){
        ExpenseTransactions expense = new ExpenseTransactions();
        expense.setExpenseTransactionsId(idGenerator.nextId());
        expense.setUserId(userId);
        expense.setBill(bill);
        expense.setAmount(amountPaid);
        expense.setDate(paidDate);
        expense.setPaymentMethod(paymentMethodRepo.findByUserIdAndPaymentId(userId, paymentId, 0L));
        expense.setDescription(bill.getName());
        expense.setCategory(bill.getCategory());
        expenseTransactionsRepo.save(expense);
        bill.setTransactions(List.of(expense));
    }

    private void fillTheBillDetails(Bills bill,Map<String, ?> input){
        if(input.containsKey(ReusableConstants.NAME)) bill.setName(input.get(ReusableConstants.NAME).toString());
        if(input.containsKey(ReusableConstants.CATEGORY_NAME)) bill.setCategory(categoryRepo.findByUserIdAndCategoryName(input.get(ReusableConstants.CATEGORY_NAME).toString(),userService.getCurrentUserId(),0L));
        if(input.containsKey(ReusableConstants.AMOUNT))bill.setAmount(Double.valueOf(input.get(ReusableConstants.AMOUNT).toString()));
        if(input.containsKey(ReusableConstants.IS_RECURRING))bill.setRecurring(Boolean.parseBoolean(input.get(ReusableConstants.IS_RECURRING).toString()));
        if(input.containsKey(ReusableConstants.DUE_DATE))bill.setDueDate(LocalDate.parse(input.get(ReusableConstants.DUE_DATE).toString()));
        if(input.containsKey(ReusableConstants.REMAINDER_BEFORE_DAYS))bill.setReminderBeforeDays((Integer) input.get(ReusableConstants.REMAINDER_BEFORE_DAYS));
        if(input.containsKey(ReusableConstants.AUTO_CREATE_EXPENSE))bill.setAutoCreateExpense((Boolean) input.get(ReusableConstants.AUTO_CREATE_EXPENSE));
        if(input.containsKey(ReusableConstants.FREQUENCY))bill.setFrequency(Bills.Frequency.valueOf(input.get(ReusableConstants.FREQUENCY).toString()));
    }

    public BillDTO handleDTO(Bills bill){
        return new BillDTO(bill.getBillId(),categoryService.handleDTO(bill.getCategory()),bill.getName(),bill.getAmount(),bill.isRecurring(),bill.getDueDate(),bill.getReminderBeforeDays(),bill.getAutoCreateExpense(),bill.getFrequency().toString(),bill.isPaid(),expenseService.handleDTO(bill.getTransactions()),getReminderDate(bill));
    }


    @Transient
    public LocalDate getReminderDate(Bills bill) {
        if (bill.getDueDate() == null || bill.getReminderBeforeDays() == null) {
            return null;
        }
        return bill.getDueDate().minusDays(bill.getReminderBeforeDays());
    }
}
