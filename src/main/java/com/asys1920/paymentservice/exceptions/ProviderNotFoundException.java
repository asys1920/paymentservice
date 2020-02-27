package com.asys1920.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "PaymentProvider not found!")
public class ProviderNotFoundException extends Exception {
}
