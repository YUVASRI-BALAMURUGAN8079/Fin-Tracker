package com.kohia.galaxy.repo;

import com.kohia.galaxy.entity.PaymentMethod;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;

@Repository
public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserId(Long userId);

    @Query("SELECT p FROM PaymentMethod p WHERE p.userId = :userId AND p.deletedTime = :deletedTime")
    List<PaymentMethod> findByUserIdAndDeletedTime(Long userId,Long deletedTime);

    @Query("SELECT paymentMethod FROM PaymentMethod paymentMethod WHERE userId = :userId AND paymentMethodId= :paymentId AND deletedTime = :deletedTime ")
    PaymentMethod findByUserIdAndPaymentId(Long userId, Long paymentId, Long deletedTime);

    @Query("SELECT paymentMethod FROM PaymentMethod paymentMethod WHERE userId = :userId AND type= :paymentType AND deletedTime = :deletedTime ")
    PaymentMethod findByUserIdAndPaymentType(Long userId, String paymentType, Long deletedTime);

    @Modifying
    @Transactional
    @Query("UPDATE PaymentMethod SET deletedTime = :deletedTime WHERE userId = :userId AND paymentMethodId = :paymentMethodId")
    void softDeletePaymentMethodId(Long userId, Long paymentMethodId,Long deletedTime);
}
