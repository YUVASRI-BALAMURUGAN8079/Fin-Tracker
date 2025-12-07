package com.tracker.repo;

import com.tracker.entity.ExpenseTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseTransactionsRepo extends JpaRepository<ExpenseTransactions, Long> {

    List<ExpenseTransactions> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT expense FROM ExpenseTransactions expense WHERE expense.expenseTransactionsId = :expenseId AND expense.userId = :userId")
    ExpenseTransactions findByIdUserId(Long expenseId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExpenseTransactions WHERE userId = :userId AND expenseTransactionsId = :expenseId")
    void deleteByIdAndUserId(Long userId, Long expenseId);

    @Query(" SELECT e FROM ExpenseTransactions e WHERE e.userId = :userId AND e.date BETWEEN :start AND :end ORDER BY e.date DESC")
    List<ExpenseTransactions> findCurrentMonthExpenses(Long userId, LocalDate start, LocalDate end);

    @Query(" SELECT e FROM ExpenseTransactions e WHERE e.userId = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year ORDER BY e.date DESC")
    List<ExpenseTransactions> findByUserIdAndMonth(Long userId, int month, int year);


}
