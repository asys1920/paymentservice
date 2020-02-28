package com.asys1920.paymentservice.service;

import com.asys1920.model.Bill;
import com.asys1920.paymentservice.adapter.AccountingServiceAdapter;
import com.asys1920.paymentservice.exceptions.BillAlreadyPaidException;
import com.asys1920.paymentservice.exceptions.NoBillFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.math.BigInteger;

@Service
public class PaymentService {
    private final AccountingServiceAdapter accountingServiceAdapter;

    private static final Logger LOG = LoggerFactory.getLogger(AccountingServiceAdapter.class);

    public PaymentService(AccountingServiceAdapter accountingServiceAdapter) {
        this.accountingServiceAdapter = accountingServiceAdapter;
    }

    /**
     * Validates that the bill needs payment
     * @param bill the bill that gets validated
     * @throws BillAlreadyPaidException gets thrown if the bill is already payed
     * @throws NoBillFoundException gets thrown if there is no matching bill found
     */
    public void validateBillNeedsPayment(Bill bill) throws BillAlreadyPaidException, NoBillFoundException {
        LOG.trace(String.format("SERVICE %s initiated", "validateBillNeedsPayment"));
        if (bill != null) {
            if (bill.isPaid()) {
                throw new BillAlreadyPaidException("The submitted bill is already payed.");
            }
        } else {
            throw new NoBillFoundException("The submitted billId is unknown");
        }
    }

    /**
     * Handles the full process of a SEPA payment over the given iban for the given billid
     * @param billId the id of the bill that gets paid
     * @param iban the iban the bill is payed from
     * @return the payed bill
     * @throws BillAlreadyPaidException gets thrown if the bill is already payed
     * @throws NoBillFoundException gets thrown if there is no matching bill found
     * @throws ServiceUnavailableException gets thrown if the bill service is currently not available
     */
    public Bill handleSepaPayment(Long billId, String iban) throws BillAlreadyPaidException, NoBillFoundException, ServiceUnavailableException {
        LOG.trace(String.format("SERVICE %s initiated", "handleSepaPayment"));
        Bill billToPay = getBill(billId);
        if (isIbanValid(iban)) {
            billToPay.setPaid(true);
        }
        return accountingServiceAdapter.saveBill(billToPay);
    }

    /**
     * Checks if the paypal payment was successful
     * @param magicNumber the argument that needs to be matched (SPOILER: it is 5)
     * @return if the payment was successful
     */
    public boolean isPayPalPaymentSuccessful(int magicNumber) {
        // Do Paypal Magic
        return magicNumber == 5;
    }

    /**
     * Checks if the submitted iban is in the right format
     * @param iban the iban that gets checks
     * @return
     */
    public boolean isIbanValid(String iban) {
        LOG.trace(String.format("SERVICE %s %s initiated", "isIbanValid", iban));
        if (!(Character.isLetter(iban.charAt(0)) && Character.isLetter(iban.charAt(1)))) {
            return false;
        }

        for (int i = 2; i < iban.length(); i++) {
            if (Character.isLetter(iban.charAt(i))) {
                return false;
            }
        }

        char[] country = iban.substring(0, 2).toCharArray();
        String checksum = iban.substring(2, 4);
        iban = iban.substring(4);
        String countryNumerical = "";
        for (char c :
                country) {
            int numOfLetterInAlphabet = c - 64;
            numOfLetterInAlphabet += 9;
            countryNumerical = countryNumerical.concat(Integer.toString(numOfLetterInAlphabet));
        }
        iban = iban.concat(countryNumerical).concat(checksum);

        BigInteger ibanNumerical = new BigInteger(iban);
        BigInteger modulo = ibanNumerical.mod(new BigInteger("97"));
        LOG.trace(String.format("SERVICE %s %s completed", "isIbanValid", iban));
        return modulo.intValue() == 1;
    }

    /**
     * Handles the full process of a PayPal payment over the given paypalParam for the given billid
     * @param billId the id of the bill that gets paid
     * @param paypalParam the PayPal the bill is payed with
     * @return the payed bill
     * @throws BillAlreadyPaidException gets thrown if the bill is already payed
     * @throws NoBillFoundException gets thrown if there is no matching bill found
     * @throws ServiceUnavailableException gets thrown if the bill service is currently not available
     */
    public Bill handlePaypalPayment(Long billId, String paypalParam) throws BillAlreadyPaidException, NoBillFoundException, ServiceUnavailableException {
        LOG.trace(String.format("SERVICE %s %s initiated", "handlePaypalPayment", paypalParam));
        Bill billToPay = getBill(billId);
        if (isPayPalPaymentSuccessful(paypalParam.length())) {
            billToPay.setPaid(true);
        }
        return accountingServiceAdapter.saveBill(billToPay);
    }

    /**
     * Retrieves a bill from the repository for the given id
     * @param billId the id the bill is associated with
     * @return the retrieved bill
     * @throws BillAlreadyPaidException gets thrown if the bill is already payed
     * @throws NoBillFoundException gets thrown if there is no bill known with the given id
     * @throws ServiceUnavailableException gets thrown if the accounting service is currently unavailable
     */
    private Bill getBill(long billId) throws BillAlreadyPaidException, NoBillFoundException, ServiceUnavailableException {
        LOG.trace(String.format("SERVICE %s initiated", "getBill"));
        Bill bill = accountingServiceAdapter.getBill(billId);
        validateBillNeedsPayment(bill);
        return bill;
    }
}
