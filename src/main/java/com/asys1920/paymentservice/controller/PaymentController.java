package com.asys1920.paymentservice.controller;

import com.asys1920.model.Bill;
import com.asys1920.paymentservice.adapter.AccountingServiceAdapter;
import com.asys1920.paymentservice.exceptions.BillAlreadyPaidException;
import com.asys1920.paymentservice.exceptions.MissingProviderInformationException;
import com.asys1920.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {


    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService, AccountingServiceAdapter accountingServiceAdapter) {
        this.paymentService = paymentService;
    }

    @PatchMapping(value = {"/pay"})
    public ResponseEntity<Bill> payBill(@RequestBody PaymentRequest request) {

        if(paymentService.isBillPaid(request.getBillId())) {
            throw new BillAlreadyPaidException();
        }
        Bill returnBill = null;

        switch (request.getPaymentProvider()) {
            case SEPA:
                if(request.getIban() == null || request.getIban().isEmpty()) {
                    throw new MissingProviderInformationException();
                }
                returnBill = paymentService.handleSepaPayment(request.getBillId(), request.getIban());
                break;
            case PAYPAL:
                if(request.getPaypalParam() == null || request.getPaypalParam().isEmpty()) {
                    throw new MissingProviderInformationException();
                }
                returnBill = paymentService.handlePaypalPayment(request.getBillId(), request.getPaypalParam());
                break;
            case NONE:
                break;
        }

        return new ResponseEntity<>(returnBill, HttpStatus.OK);
    }


}
