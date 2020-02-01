package com.asys1920.paymentservice.controller;

import com.asys1920.paymentservice.service.PaymentService;
import com.asys1920.paymentservice.exceptions.NoBillFoundException;
import com.asys1920.paymentservice.model.Bill;
import com.asys1920.paymentservice.model.PaymentProvider;
import com.asys1920.paymentservice.model.PaymentStatus;
import com.asys1920.paymentservice.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @Autowired
    BillRepository billRepository;
    @Autowired
    PaymentService paymentService;

    @GetMapping("/paymentStatus")
    public PaymentStatus getStatusOfPayment(@RequestParam(name = "id") long id) {
        if (billRepository.findById(id).isPresent()) {
            Bill bill = billRepository.findById(id).get();
            return bill.getPaymentStatus();
        } else {
            throw new NoBillFoundException();
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<Bill> payBill(@RequestBody Bill bill, @RequestParam(name="type") String type) {
        //TODO: Check if Bill actually exists!
        bill.setPaymentStatus(PaymentStatus.PENDING);

        PaymentProvider provider = PaymentProvider.NONE;

        if(type.equalsIgnoreCase("SEPA")) {
            provider = PaymentProvider.SEPA;
        } else if(type.equalsIgnoreCase("PAYPAL")) {
            provider = PaymentProvider.PAYPAL;
        }

        billRepository.save(bill);
        paymentService.handlePayment(bill, provider);
        billRepository.save(bill);

        return new ResponseEntity<>(bill, HttpStatus.CREATED);
    }


}
