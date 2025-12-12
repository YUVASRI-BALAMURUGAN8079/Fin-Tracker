package com.tracker.repo;

import com.tracker.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepo  extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserIdAndDeletedTime(Long userId, Long deletedTime);
    Optional<SavingsGoal> findByUserIdAndGoalIdAndDeletedTime(Long userId, Long goalId, Long deletedTime);

    @QueryMapping("SELECT savingsGoal FROM SavingsGoal savingsGoal WHERE savingsGoal.userId = :userId AND savingsGoal.goalId = :goalId AND savingsGoal.deletedTime = :deletedTime")
    SavingsGoal getByUserIdAndGoalIdAndDeletedTime(Long userId, Long goalId,Long deletedTime);
}
