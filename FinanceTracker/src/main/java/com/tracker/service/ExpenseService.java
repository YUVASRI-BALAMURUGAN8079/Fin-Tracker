package com.tracker.service;

import com.tracker.DTO.ExpenseDTO;
import com.tracker.DTO.ExpensesGroupByBill;
import com.tracker.DTO.ExpensesGroupByCategory;
import com.tracker.DTO.ExpensesGroupByPaymentMethod;
import com.tracker.entity.Bills;
import com.tracker.entity.Category;
import com.tracker.entity.ExpenseTransactions;
import com.tracker.entity.PaymentMethod;
import com.tracker.error.ReusableConstants;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.CategoryRepo;
import com.tracker.repo.ExpenseTransactionsRepo;
import com.tracker.repo.PaymentMethodRepo;
import net.mguenther.idem.flake.Flake64L;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired private ExpenseTransactionsRepo expenseRepo;
    @Autowired private ModelMapper modelMapper;
    @Autowired private Flake64L idGenerator;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private PaymentMethodRepo paymentMethodRepo;
    @Autowired private UserService userService;

    public List<ExpenseDTO> getExpenses(Long userId){
        return expenseRepo.findByUserIdOrderByDateDesc(userId).stream().map(this::handleDTO).toList();
    }

    public ExpenseDTO createExpense(Long userId, Map<String, ?> input){
        var hasCategoryInInput = false;
        Category category = null;
        if (input.containsKey(ReusableConstants.CATEGORY_ID)) {
            hasCategoryInInput = true;
            category= categoryRepo.findByUserIdAndCategoryCategoryId(Long.valueOf(input.remove(ReusableConstants.CATEGORY_ID).toString()),userService.getCurrentUserId(),0L);
        } else if (input.containsKey(ReusableConstants.CATEGORY_NAME)) {
            hasCategoryInInput = true;
            category= categoryRepo.findByUserIdAndCategoryName(input.remove(ReusableConstants.CATEGORY_NAME).toString(),userService.getCurrentUserId(),0L);
        }
        if(hasCategoryInInput && category == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);

        var hasPaymentMethodInInput = false;
        PaymentMethod paymentMethod = null;
        if (input.containsKey(ReusableConstants.PAYMENT_METHOD_ID)) {
            hasPaymentMethodInInput = true;
            paymentMethod= paymentMethodRepo.findByUserIdAndPaymentId(userService.getCurrentUserId(),Long.valueOf(input.remove(ReusableConstants.PAYMENT_METHOD_ID).toString()),0L);
        } else if (input.containsKey(ReusableConstants.PAYMENT_METHOD_TYPE)) {
            hasPaymentMethodInInput = true;
            paymentMethod= paymentMethodRepo.findByUserIdAndPaymentType(userService.getCurrentUserId(),input.remove(ReusableConstants.PAYMENT_METHOD_TYPE).toString(),0L);
        }
        if(hasPaymentMethodInInput && paymentMethod == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);

        ExpenseTransactions expenseTransactions = modelMapper.map(input,ExpenseTransactions.class);
        expenseTransactions.setExpenseTransactionsId(idGenerator.nextId());
        expenseTransactions.setUserId(userId);
        expenseTransactions.setCategory(category);
        expenseTransactions.setPaymentMethod(paymentMethod);
        expenseTransactions.setAmount(Double.valueOf(input.remove(ReusableConstants.AMOUNT).toString()));
        expenseTransactions.setDate(LocalDate.parse(input.remove(ReusableConstants.DATE).toString()));

        return handleDTO(expenseRepo.save(expenseTransactions));
    }

    public ExpenseDTO updateExpense(Long userId, Long expenseId,Map<String, ?> input){
        var expense = expenseRepo.findByIdUserId(expenseId,userId);
        if(expense == null) throw  new FinTrackerException(ErrorConstants.INVALID_INPUT);

        if(input.containsKey(ReusableConstants.CATEGORY_ID) || input.containsKey(ReusableConstants.CATEGORY_NAME)){
            Category category = null;
            if (input.containsKey(ReusableConstants.CATEGORY_ID)) {
                category= categoryRepo.findByUserIdAndCategoryCategoryId(Long.valueOf(input.remove(ReusableConstants.CATEGORY_ID).toString()),userService.getCurrentUserId(),0L);
            } else if (input.containsKey(ReusableConstants.CATEGORY_NAME)) {
                category= categoryRepo.findByUserIdAndCategoryName(input.remove(ReusableConstants.CATEGORY_NAME).toString(),userService.getCurrentUserId(),0L);
            }
            if(category == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);
            expense.setCategory(category);
        }

        if(input.containsKey(ReusableConstants.PAYMENT_METHOD_ID) || input.containsKey(ReusableConstants.PAYMENT_METHOD_TYPE)){
            PaymentMethod paymentMethod = null;
            if (input.containsKey(ReusableConstants.PAYMENT_METHOD_ID)) {
                paymentMethod= paymentMethodRepo.findByUserIdAndPaymentId(userService.getCurrentUserId(),Long.valueOf(input.remove(ReusableConstants.PAYMENT_METHOD_ID).toString()),0L);
            } else if (input.containsKey(ReusableConstants.PAYMENT_METHOD_TYPE)) {
                paymentMethod= paymentMethodRepo.findByUserIdAndPaymentType(userService.getCurrentUserId(),input.remove(ReusableConstants.PAYMENT_METHOD_TYPE).toString(),0L);
            }
            if(paymentMethod == null) throw new FinTrackerException(ErrorConstants.INVALID_INPUT);
            expense.setPaymentMethod(paymentMethod);
        }

        if(input.containsKey(ReusableConstants.AMOUNT)){
            expense.setAmount(Double.valueOf(input.get(ReusableConstants.AMOUNT).toString()));
        }
        if(input.containsKey(ReusableConstants.DATE)){
            expense.setDate(LocalDate.parse(input.get(ReusableConstants.DATE).toString()));
        }

        return handleDTO(expenseRepo.save(expense));
    }

    public Long deleteExpense(Long userId,Long expenseId) {
        expenseRepo.deleteByIdAndUserId(userId, expenseId);
        return expenseId;
    }

    public List<ExpensesGroupByCategory> getExpensesGroupByCategory() {
        var expenses = expenseRepo.findByUserIdAndMonth(userService.getCurrentUserId(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        Map<Category, List<ExpenseTransactions>> groupedByCategory = expenses.stream().collect(Collectors.groupingBy(ExpenseTransactions::getCategory));
        return handleExpensesGroupByCategoryDTO(groupedByCategory);
    }

    public List<ExpensesGroupByBill> getExpensesGroupByBill() {
        var expenses = expenseRepo.findByUserIdAndMonth(userService.getCurrentUserId(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        Map<Bills, List<ExpenseTransactions>> groupedByBill = expenses.stream()
                .filter(e -> e.getBill() != null).collect(Collectors.groupingBy(ExpenseTransactions::getBill));
        return handleExpensesGroupByBillDTO(groupedByBill);
    }

    public List<ExpensesGroupByPaymentMethod> getExpensesGroupByPaymentMethod() {
        var expenses = expenseRepo.findByUserIdAndMonth(userService.getCurrentUserId(), LocalDate.now().getMonthValue(), LocalDate.now().getYear());
        Map<PaymentMethod, List<ExpenseTransactions>> groupedByPaymentMethod = expenses.stream()
                .filter(e -> e.getPaymentMethod() != null).collect(Collectors.groupingBy(ExpenseTransactions::getPaymentMethod));
        return handleExpensesGroupByPaymentMethodDTO(groupedByPaymentMethod);
    }


    public ExpenseDTO handleDTO(ExpenseTransactions expenseTransactions){
        return new ExpenseDTO(expenseTransactions.getExpenseTransactionsId(),expenseTransactions.getCategory(),expenseTransactions.getPaymentMethod(),expenseTransactions.getAmount(),expenseTransactions.getDescription(),expenseTransactions.getDate());
    }

    public List<ExpenseDTO> handleDTO(List<ExpenseTransactions> expenseTransactionsList) {
        if (expenseTransactionsList == null) {
            return List.of();
        }
        return expenseTransactionsList.stream().map(this::handleDTO).toList();
    }


    public List<ExpensesGroupByCategory> handleExpensesGroupByCategoryDTO(Map<Category, List<ExpenseTransactions>> groupedByCategory){
        return groupedByCategory.entrySet()
                .stream().map(entry -> handleExpensesGroupByCategoryDTO(entry.getKey(), entry.getValue())).toList();
    }

    public List<ExpensesGroupByBill> handleExpensesGroupByBillDTO(Map<Bills, List<ExpenseTransactions>> groupedByCategory){
        return groupedByCategory.entrySet().stream().map(entry -> handleExpensesGroupByBillDTO(entry.getKey(), entry.getValue())).toList();
    }

    public List<ExpensesGroupByPaymentMethod> handleExpensesGroupByPaymentMethodDTO(Map<PaymentMethod, List<ExpenseTransactions>> groupedByPaymentMethod){
        return groupedByPaymentMethod.entrySet().stream().map(entry -> handleExpensesGroupByPaymentMethodDTO(entry.getKey(), entry.getValue())).toList();
    }

    public ExpensesGroupByCategory handleExpensesGroupByCategoryDTO(Category category, List<ExpenseTransactions> expenses) {
        List<ExpenseDTO> expenseDTOs = expenses.stream()
                .map(tx -> new ExpenseDTO(
                        tx.getExpenseTransactionsId(),
                        tx.getCategory(),
                        tx.getPaymentMethod(),
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getDate()
                ))
                .toList();

        double totalSum = expenses.stream().mapToDouble(ExpenseTransactions::getAmount).sum();
        Double percentSpent = null;
        if( category.getCurrentMonthlyLimit() != null && category.getCurrentMonthlyLimit() != 0L) {
            percentSpent = ((totalSum*100) / category.getCurrentMonthlyLimit());
        }
        return new ExpensesGroupByCategory(category, expenseDTOs, totalSum,percentSpent);
    }

    public ExpensesGroupByBill handleExpensesGroupByBillDTO(Bills bills, List<ExpenseTransactions> expenses) {
        List<ExpenseDTO> expenseDTOs = expenses.stream()
                .map(tx -> new ExpenseDTO(
                        tx.getExpenseTransactionsId(),
                        tx.getCategory(),
                        tx.getPaymentMethod(),
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getDate()
                ))
                .toList();

        double totalSum = expenses.stream().mapToDouble(ExpenseTransactions::getAmount).sum();
        return new ExpensesGroupByBill(bills, expenseDTOs, totalSum);
    }

    public ExpensesGroupByPaymentMethod handleExpensesGroupByPaymentMethodDTO(PaymentMethod paymentMethod, List<ExpenseTransactions> expenses) {
        List<ExpenseDTO> expenseDTOs = expenses.stream()
                .map(tx -> new ExpenseDTO(
                        tx.getExpenseTransactionsId(),
                        tx.getCategory(),
                        tx.getPaymentMethod(),
                        tx.getAmount(),
                        tx.getDescription(),
                        tx.getDate()
                ))
                .toList();

        double totalSum = expenses.stream().mapToDouble(ExpenseTransactions::getAmount).sum();
        return new ExpensesGroupByPaymentMethod(paymentMethod, expenseDTOs, totalSum);
    }
}
