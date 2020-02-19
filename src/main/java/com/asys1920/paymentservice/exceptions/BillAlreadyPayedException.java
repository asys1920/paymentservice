package com.asys1920.paymentservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bill already payed")
public class BillAlreadyPayedException extends RuntimeException {
}
