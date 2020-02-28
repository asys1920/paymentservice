package com.asys1920.paymentservice.advice;

import com.asys1920.paymentservice.exceptions.BillAlreadyPaidException;
import com.asys1920.paymentservice.exceptions.MissingProviderInformationException;
import com.asys1920.paymentservice.exceptions.NoBillFoundException;
import com.asys1920.paymentservice.exceptions.ProviderNotFoundException;
import lombok.Data;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler(value = {BillAlreadyPaidException.class})
    @ResponseBody
    public ResponseEntity<String> handleBillAlreadyPaid(Exception ex) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(jsonFromException(ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {NoBillFoundException.class})
    @ResponseBody
    public ResponseEntity<String> handleNoSuchBill(Exception ex) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(jsonFromException(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ProviderNotFoundException.class, MissingProviderInformationException.class})
    @ResponseBody
    public ResponseEntity<String> handleUnknownProvider(Exception ex) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(jsonFromException(ex), HttpStatus.BAD_REQUEST);
    }


    @Data
    private static class ErrorMessage {
        private final String cause = "VALIDATION FAILED";
        private List<String> description;

        public ErrorMessage(List<String> description) {
            this.description = description;
        }
    }

    private String jsonFromException(Exception ex) {
        JSONObject response = new JSONObject();
        response.put("message", ex.getMessage());
        return response.toJSONString();
    }
}
