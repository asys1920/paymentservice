package com.asys1920.paymentservice.service;

import com.asys1920.model.Bill;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PaymentService {

    public void handleSepaPayment(Bill bill, String iban) {
        if(isIbanValid(iban)) {
            bill.setIsPayed(true);
        }
    }

    public boolean isPayPalPaymentSuccessful(int magicNumber) {
        // Do Paypal Magic
        return magicNumber == 5;
    }

    public boolean isIbanValid(String iban) {

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

        return modulo.intValue() == 1;
    }

    public void handlePaypalPayment(Bill billToPay, String paypalParam) {
        if(isPayPalPaymentSuccessful(paypalParam.length())) {
            billToPay.setIsPayed(true);
        }
    }
}
