package com.asys1920.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Either IBAN or PayPal information missing!")
public class MissingProviderInformationException extends RuntimeException {
}
