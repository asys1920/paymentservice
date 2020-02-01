package com.asys1920.paymentservice.service;

import com.asys1920.paymentservice.model.Bill;
import com.asys1920.paymentservice.model.PaymentProvider;
import com.asys1920.paymentservice.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SepaPaymentTests {

    @Test
    public void testSepa() {
        Bill bill = new Bill();
        PaymentService payment = new PaymentService();
        payment.handlePayment(bill, PaymentProvider.SEPA);

        bill.setIban("DE08700901001234567890");
        assertEquals(PaymentStatus.PAID, bill.getPaymentStatus());
    }
}
