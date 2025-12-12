package com.tracker.resolver;

import com.tracker.entity.SavingsGoal;
import com.tracker.entity.SavingsGoalTransaction;
import com.tracker.service.SavingsGoalService;
import com.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class SavingsGoalResolver {
    @Autowired private SavingsGoalService goalService;
    @Autowired private UserService userService;

    @MutationMapping
    public SavingsGoal createSavingsGoal(@Argument Map<String, Object> input) {
        return goalService.createGoal(input);
    }
    @MutationMapping
    public SavingsGoal updateSavingsGoal(@Argument Long goalId, @Argument Map<String, Object> input) {
        return goalService.updateGoal(goalId, input);
    }

    @MutationMapping
    public Long softDeleteSavingsGoal(@Argument Long goalId) {
        return goalService.softDeleteGoal(goalId);
    }

    @MutationMapping
    public SavingsGoalTransaction addSavingsGoalTransaction(@Argument Long goalId, @Argument Map<String, Object> input) {
        return goalService.addTransaction(goalId, input);
    }

    @MutationMapping
    public SavingsGoalTransaction updateSavingsGoalTransaction(@Argument Long txnId, @Argument Map<String, Object> input) {
        return goalService.updateTransaction(txnId, input);
    }

    @MutationMapping
    public Boolean softDeleteSavingsGoalTransaction(@Argument Long txnId) {
        return goalService.softDeleteTransaction(txnId);
    }

    @QueryMapping
    public List<SavingsGoal> getSavingsGoals() {
        return goalService.getGoals(userService.getCurrentUserId());
    }

    @QueryMapping
    public SavingsGoal getGoalsAndTransactions(@Argument Long goalId) {
        return goalService.getGoalsAndTransactions(userService.getCurrentUserId(),goalId);
    }
}
