package com.tracker.repo;

import com.tracker.entity.Bills;
import com.tracker.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepo extends JpaRepository<Bills, Long> {

    @Query("SELECT bill FROM Bills bill WHERE bill.billId = :billId AND bill.userId = :userId AND bill.deletedTime = :deletedTime")
    Bills findByUserIdAndBillId(Long billId, Long userId, Long deletedTime);

    @Modifying
    @Transactional
    @Query("UPDATE Bills b SET b.deletedTime = :deletedTime WHERE b.billId = :billId AND b.userId = :userId")
    void softDeleteBill(@Param("userId") Long userId, @Param("billId") Long billId, @Param("deletedTime") Long deletedTime);

    @Query("SELECT bill FROM Bills bill WHERE bill.userId = :userId AND bill.deletedTime = :deletedTime")
    List<Bills> getBillsByUserId(Long userId, Long deletedTime);

}
