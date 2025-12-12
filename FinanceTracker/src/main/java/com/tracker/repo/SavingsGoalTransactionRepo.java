package com.tracker.repo;

import com.tracker.entity.SavingsGoal;
import com.tracker.entity.SavingsGoalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalTransactionRepo extends JpaRepository<SavingsGoalTransaction, Long> {
    List<SavingsGoalTransaction> findBySavingsGoalGoalId(Long goalId);
    Optional<SavingsGoalTransaction> findByUserIdAndTxnIdAndDeletedTime(Long userId, Long txnId, Long deletedTime);
}
