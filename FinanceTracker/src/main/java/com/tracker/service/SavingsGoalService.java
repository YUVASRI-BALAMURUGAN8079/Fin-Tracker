package com.tracker.service;

import com.tracker.entity.Category;
import com.tracker.entity.SavingsGoal;
import com.tracker.entity.SavingsGoalTransaction;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.error.ReusableConstants;
import com.tracker.repo.SavingsGoalRepo;
import com.tracker.repo.SavingsGoalTransactionRepo;
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
public class SavingsGoalService {
    @Autowired private SavingsGoalRepo goalRepo;
    @Autowired private SavingsGoalTransactionRepo txnRepo;
    @Autowired private UserService userService;
    @Autowired private Flake64L idGenerator;
    @Autowired private ModelMapper modelMapper;

    public SavingsGoal createGoal(Map<String, ?> input) {
        input.remove(ReusableConstants.CURRENT_AMOUNT);
        SavingsGoal goal = modelMapper.map(input, SavingsGoal.class);
        goal.setUserId(userService.getCurrentUserId());
        goal.setGoalId(idGenerator.nextId());
        goal.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        return goalRepo.save(goal);
    }

    public SavingsGoal updateGoal(Long goalId, Map<String, ?> input) {
        SavingsGoal existing = goalRepo.findByUserIdAndGoalIdAndDeletedTime(userService.getCurrentUserId(), goalId, 0L)
                .orElseThrow(() -> new FinTrackerException((ErrorConstants.INVALID_INPUT)));
        fillGoals(existing,input);
        existing.setUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        return goalRepo.save(existing);
    }

    private void fillGoals(SavingsGoal goal, Map<String, ?> input){
        if (input.containsKey(ReusableConstants.NAME)) goal.setName(input.get(ReusableConstants.NAME).toString());
        if (input.containsKey(ReusableConstants.TARGET_AMOUNT)) goal.setTargetAmount(Double.valueOf(input.get(ReusableConstants.TARGET_AMOUNT).toString()));
        if (input.containsKey(ReusableConstants.TARGET_DATE)) goal.setTargetDate(LocalDate.parse(input.get(ReusableConstants.TARGET_DATE).toString()));
    }

    public Long softDeleteGoal(Long goalId) {
        SavingsGoal existing = goalRepo.findByUserIdAndGoalIdAndDeletedTime(userService.getCurrentUserId(), goalId, 0L).orElseThrow(() -> new FinTrackerException(ErrorConstants.INVALID_INPUT));
        existing.setDeletedTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        goalRepo.save(existing);
        return goalId;
    }

    public SavingsGoalTransaction addTransaction(Long goalId, Map<String, ?> input) {
        SavingsGoal goal = goalRepo.findByUserIdAndGoalIdAndDeletedTime(userService.getCurrentUserId(), goalId, 0L).orElseThrow(() -> new FinTrackerException((ErrorConstants.INVALID_INPUT)));
        SavingsGoalTransaction txn = new SavingsGoalTransaction();
        txn.setTxnId(idGenerator.nextId());
        txn.setSavingsGoal(goal);
        txn.setAmount(Double.valueOf(input.get(ReusableConstants.AMOUNT).toString()));
        fillSavingsTransaction(txn,input);
        goal.setCurrentAmount(goal.getCurrentAmount() + txn.getAmount());
        txn.setUserId(userService.getCurrentUserId());
        goalRepo.save(goal);
        return txnRepo.save(txn);
    }

    public SavingsGoalTransaction updateTransaction(Long txnId, Map<String, ?> input) {
        SavingsGoalTransaction txn = txnRepo.findByUserIdAndTxnIdAndDeletedTime(userService.getCurrentUserId(),txnId,0L).orElseThrow(() -> new FinTrackerException((ErrorConstants.INVALID_INPUT)));
        SavingsGoal goal = txn.getSavingsGoal();
        if (input.containsKey(ReusableConstants.AMOUNT)) {
            double oldAmount = txn.getAmount();
            double newAmount = Double.parseDouble(input.get(ReusableConstants.AMOUNT).toString());
            goal.setCurrentAmount(goal.getCurrentAmount() - oldAmount + newAmount);
            txn.setAmount(newAmount);
        }
        fillSavingsTransaction(txn,input);
        goalRepo.save(goal);
        return txnRepo.save(txn);
    }

    private void fillSavingsTransaction(SavingsGoalTransaction txn, Map<String, ?> input){
        if (input.containsKey(ReusableConstants.DATE)) txn.setTxnDate(LocalDate.parse(input.get(ReusableConstants.DATE).toString()));
        if (input.containsKey(ReusableConstants.NOTE)) txn.setNote(input.get(ReusableConstants.NOTE).toString());
    }

    public boolean softDeleteTransaction(Long txnId) {
        SavingsGoalTransaction txn = txnRepo.findByUserIdAndTxnIdAndDeletedTime(userService.getCurrentUserId(),txnId,0L).orElseThrow(() -> new FinTrackerException((ErrorConstants.INVALID_INPUT)));

        SavingsGoal goal = txn.getSavingsGoal();

        // Deduct transaction amount from saved total
        goal.setCurrentAmount(goal.getCurrentAmount() - txn.getAmount());
        goalRepo.save(goal);

        txn.setDeletedTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        txnRepo.save(txn);
        return true;
    }

    public List<SavingsGoal> getGoals(Long userId) {
        return goalRepo.findByUserIdAndDeletedTime(userId, 0L);
    }

    public SavingsGoal getGoalsAndTransactions(Long userId,Long goalId) {
        return goalRepo.getByUserIdAndGoalIdAndDeletedTime(userId,goalId, 0L);
    }
}
