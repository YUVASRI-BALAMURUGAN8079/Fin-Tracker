package com.tracker.resolver;

import com.tracker.DTO.BillDTO;
import com.tracker.error.ReusableConstants;
import com.tracker.service.BillService;
import com.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class BillResolver {
    @Autowired private BillService billService;
    @Autowired private UserService userService;
    @QueryMapping
    public List<BillDTO> getBills(){
        return billService.getBills(userService.getCurrentUserId());
    }

    @MutationMapping
    public BillDTO addBill(@Argument Map<String, ?> input){
        return billService.addBill(input);
    }

    @MutationMapping
    public BillDTO updateBill(@Argument Long billId, @Argument Map<String, ?> input){
        return billService.updateBill(billId, input);
    }
    @MutationMapping
    public Long softDeleteBill(@Argument Long billId){
        return billService.softDelete(billId);
    }

    @MutationMapping
    public BillDTO markBillAsPaid(@Argument Map<String, ?> input){
        return billService.markBillAsPaid(userService.getCurrentUserId(), Long.valueOf(input.get(ReusableConstants.BILL_ID).toString()),Double.valueOf(input.get(ReusableConstants.AMOUNT).toString()),Long.valueOf(input.get(ReusableConstants.PAYMENT_METHOD_ID).toString()), LocalDate.parse(input.get(ReusableConstants.PAID_DATE).toString()));
    }

}
