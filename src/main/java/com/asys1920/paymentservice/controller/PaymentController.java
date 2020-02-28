package com.asys1920.paymentservice.controller;

import com.asys1920.model.Bill;
import com.asys1920.paymentservice.exceptions.BillAlreadyPaidException;
import com.asys1920.paymentservice.exceptions.MissingProviderInformationException;
import com.asys1920.paymentservice.exceptions.NoBillFoundException;
import com.asys1920.paymentservice.exceptions.ProviderNotFoundException;
import com.asys1920.paymentservice.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;

@RestController
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
    private static final String PATH = "/pay";
    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ApiOperation(value = "Pays a bill", response = PaymentRequest.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully payed bill"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @PatchMapping(value = {PATH})
    public ResponseEntity<Bill> payBill(@RequestBody PaymentRequest request) throws BillAlreadyPaidException, NoBillFoundException, MissingProviderInformationException, ProviderNotFoundException, ServiceUnavailableException {
        LOG.trace(String.format("PATCH %s initiated", PATH));
        switch (request.getPaymentProvider()) {
            case SEPA:
                if (request.getIban() == null || request.getIban().isEmpty()) {
                    throw new MissingProviderInformationException("You have to provide an IBAN when choosing SEPA");
                }
                return new ResponseEntity<>(
                        paymentService.handleSepaPayment(request.getBillId(), request.getIban()),
                        HttpStatus.OK);
            case PAYPAL:
                if (request.getPaypalParam() == null || request.getPaypalParam().isEmpty()) {
                    throw new MissingProviderInformationException("You have to provide a paypalParam (Tipp: 5 is the answer)");
                }
                LOG.trace(String.format("PATCH %s completed", PATH));
                return new ResponseEntity<>(paymentService.handlePaypalPayment(request.getBillId(),
                        request.getPaypalParam()),
                        HttpStatus.OK);
            default:
                throw new ProviderNotFoundException("You have to provide a valid paymentProvider (SEPA or PAYPAL");
        }

    }


}
