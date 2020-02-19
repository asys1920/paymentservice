package com.asys1920.paymentservice.controller;

import com.asys1920.model.Bill;
import com.asys1920.paymentservice.adapter.AccountingServiceAdapter;
import com.asys1920.paymentservice.exceptions.BillAlreadyPayedException;
import com.asys1920.paymentservice.service.PaymentService;
import com.asys1920.model.PaymentProvider;
import com.asys1920.paymentservice.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PaymentController {

    @Autowired
    BillRepository billRepository;
    @Autowired
    PaymentService paymentService;
    @Autowired
    AccountingServiceAdapter accountingServiceAdapter;

    @PostMapping(value = {"/pay/{provider}/{billId}/{iban}", "/pay/{provider}/{billId}/{paypalParam}"})
    public ResponseEntity<Bill> payBill(@PathVariable(name = "billId") Long billId,
                                        @PathVariable(name = "provider") PaymentProvider provider,
                                        @PathVariable(name = "iban") Optional<String> iban,
                                        @PathVariable(name = "paypalParam") Optional<String> paypalParam) {

        Bill billToPay = accountingServiceAdapter.getBill(billId);

        if(billToPay.getIsPayed())
            throw new BillAlreadyPayedException();

        switch (provider) {
            case SEPA:
                paymentService.handleSepaPayment(billToPay, iban.get());
                break;
            case PAYPAL:
                paymentService.handlePaypalPayment(billToPay, paypalParam.get());
                break;
            case NONE:
                break;
        }

        return new ResponseEntity<>(billToPay, HttpStatus.CREATED);
    }


}
