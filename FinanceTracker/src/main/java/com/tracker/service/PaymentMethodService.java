package com.tracker.service;

import com.tracker.DTO.PaymentMethodDTO;
import com.tracker.entity.PaymentMethod;
import com.tracker.error.ReusableConstants;
import com.tracker.error.ErrorConstants;
import com.tracker.error.FinTrackerException;
import com.tracker.repo.PaymentMethodRepo;
import net.mguenther.idem.flake.Flake64L;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class PaymentMethodService {

    @Autowired private PaymentMethodRepo paymentMethodRepo;
    @Autowired private ModelMapper modelMapper;
    @Autowired private Flake64L idGenerator;

    public List<PaymentMethodDTO> getPaymentMethodList(Long userId) {
        return paymentMethodRepo.findByUserIdAndDeletedTime(userId,0L).stream().map(this::handleDTO).toList();
    }

    public PaymentMethodDTO createPaymentMethod(Long userId,Map<String, ?> input) {
        PaymentMethod paymentMethod = modelMapper.map(input,PaymentMethod.class);
        paymentMethod.setPaymentMethodId(idGenerator.nextId());
        paymentMethod.setType((String) input.get(ReusableConstants.TYPE));
        paymentMethod.setUserId(userId);
        paymentMethod.setCreatedTime(LocalDateTime.now(ZoneOffset.UTC));
        return handleDTO(paymentMethodRepo.save(paymentMethod));
    }

    public PaymentMethodDTO updatePaymentMethod(Long userId,Long paymentMethodId,Map<String, ?> input) {
        PaymentMethod paymentMethod = paymentMethodRepo.findByUserIdAndPaymentId(userId, paymentMethodId, 0L);
        if (paymentMethod == null) {
            throw new FinTrackerException(ErrorConstants.INVALID_INPUT);
        }
        if (input.containsKey(ReusableConstants.TYPE)) {
            paymentMethod.setType(input.get(ReusableConstants.TYPE).toString());
        }

        paymentMethod.setUpdatedTime(LocalDateTime.now(ZoneOffset.UTC));
        return handleDTO(paymentMethodRepo.save(paymentMethod));
    }

    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        paymentMethodRepo.softDeletePaymentMethodId(userId,paymentMethodId, LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    public PaymentMethodDTO handleDTO(PaymentMethod paymentMethod){
        return new PaymentMethodDTO(paymentMethod.getPaymentMethodId(),paymentMethod.getType());
    }
}
