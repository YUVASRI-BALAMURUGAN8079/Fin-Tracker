package com.tracker.resolver;

import com.tracker.DTO.ExpenseDTO;
import com.tracker.DTO.ExpensesGroupByCategory;
import com.tracker.service.ExpenseService;
import com.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ExpenseResolver {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private UserService userService;

    @QueryMapping
    public List<ExpenseDTO> getExpenses() {
        return expenseService.getExpenses(userService.getCurrentUserId());
    }

    @MutationMapping
    public ExpenseDTO createExpense(@Argument Map<String,?> input) {
        return expenseService.createExpense(userService.getCurrentUserId(), input);
    }

    @MutationMapping
    public ExpenseDTO updateExpense(@Argument Long expenseId ,@Argument Map<String,?> input) {
        return expenseService.updateExpense(userService.getCurrentUserId(), expenseId, input);
    }

    @MutationMapping
    public Long deleteExpense(@Argument Long expenseId) {
        return expenseService.deleteExpense(userService.getCurrentUserId(), expenseId);
    }

    @QueryMapping
    public List<ExpensesGroupByCategory> getExpensesGroupByCategory(){
        return expenseService.getExpensesGroupByCategory();
    }

}
