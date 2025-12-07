package com.kohia.galaxy.resolver;

import com.kohia.galaxy.DTO.PaymentMethodDTO;
import com.kohia.galaxy.service.PaymentMethodService;
import com.kohia.galaxy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

@Controller
public class PaymentMethodResolver {
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private UserService userService;

    @QueryMapping
    public List<PaymentMethodDTO> getPaymentMethodList() {
        return paymentMethodService.getPaymentMethodList(userService.getCurrentUserId());
    }

    @MutationMapping
    public PaymentMethodDTO createPaymentMethod(@Argument Map<String, ?> input) {
        return paymentMethodService.createPaymentMethod(userService.getCurrentUserId(),input);
    }

    @MutationMapping
    public PaymentMethodDTO updatePaymentMethod(@Argument Long paymentMethodId,@Argument Map<String, ?> input) {
        return paymentMethodService.updatePaymentMethod(userService.getCurrentUserId(), paymentMethodId,input);
    }

    @MutationMapping
    public long deletePaymentMethod(@Argument Long paymentMethodId) {
        paymentMethodService.deletePaymentMethod(userService.getCurrentUserId(),paymentMethodId);
        return paymentMethodId;
    }

}
