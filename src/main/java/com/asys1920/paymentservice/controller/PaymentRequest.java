package com.asys1920.paymentservice.controller;

import com.asys1920.model.PaymentProvider;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PaymentRequest {
    @NotNull(message = "Bill id has to be provided")
    private Long billId;
    @NotNull(message = "Payment provider has to be provided")
    private PaymentProvider paymentProvider;

    private String iban;
    private String paypalParam;
}
