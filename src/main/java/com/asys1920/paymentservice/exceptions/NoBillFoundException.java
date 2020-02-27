package com.asys1920.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Bill not found")
public class NoBillFoundException extends Exception {
    public NoBillFoundException(String message) {
        super(message);
    }
}
