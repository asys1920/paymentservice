package com.asys1920.paymentservice;

import com.asys1920.model.Bill;
import com.asys1920.model.PaymentProvider;
import com.asys1920.model.User;
import com.asys1920.paymentservice.adapter.AccountingServiceAdapter;
import com.asys1920.paymentservice.controller.PaymentRequest;
import com.asys1920.paymentservice.repository.BillRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.Period;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentserviceApplicationTests {

    static final private String validIBAN = "DE08700901001234567890";

    @MockBean
    private AccountingServiceAdapter accountingServiceAdapter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillRepository billRepository;

    private String payEndpoint = "/pay";

    @Test
    void should_() {

    }

    /*
    TODO
        @ApiResponse(code = 200, message = "Successfully payed bill"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
       */

    @Test
    public void shouldReturnOk_whenSubmittingValidPayPalPayment() throws Exception {
        JSONObject body = jsonFromPayment(getValidPayPalPayment());
        when(accountingServiceAdapter.getBill(1L)).thenReturn(getValidBill());
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOk_whenSubmittingValidSepaPayment() throws Exception {
        JSONObject body = jsonFromPayment(getValidSepaPayment());
        when(accountingServiceAdapter.getBill(1L)).thenReturn(getValidBill());
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnError_whenPayPalPayingAlreadyPayingBill() throws Exception {
        JSONObject body = jsonFromPayment(getValidPayPalPayment());
        Bill validBill = getValidBill();
        validBill.setPaid(true);
        when(accountingServiceAdapter.getBill(1L)).thenReturn(validBill);
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnError_whenPayPalPayingUnknownBill() throws Exception {
        JSONObject body = jsonFromPayment(getValidPayPalPayment());
        when(accountingServiceAdapter.getBill(1L)).thenReturn(null);
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnErrorMessage_whenSubmittingInvalidPaymentProvider() throws Exception {
        PaymentRequest validPayPalPayment = getValidPayPalPayment();
        JSONObject body = jsonFromPayment(validPayPalPayment);
        body.put("paymentProvider", "BITCOIN");
        when(accountingServiceAdapter.getBill(1L)).thenReturn(getValidBill());
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldReturnErrorMessage_whenNotSubmittingPaymentProvider() throws Exception {
        PaymentRequest validPayPalPayment = getValidPayPalPayment();
        JSONObject body = jsonFromPayment(validPayPalPayment);
        body.put("paymentProvider", "");
        when(accountingServiceAdapter.getBill(1L)).thenReturn(getValidBill());
        mockMvc.perform(patch(payEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private JSONObject jsonFromPayment(PaymentRequest pay) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("billId", pay.getBillId());
        if (PaymentProvider.PAYPAL.equals(pay.getPaymentProvider())) {
            body.put("paymentProvider", "PAYPAL");
            body.put("paypalParam", pay.getPaypalParam());
        } else if (PaymentProvider.SEPA.equals(pay.getPaymentProvider())) {
            body.put("paymentProvider", "SEPA");
            body.put("iban", pay.getIban());
        }
        return body;
    }


    private PaymentRequest getValidPayment() {
        PaymentRequest pay = new PaymentRequest();
        pay.setBillId(1L);
        return pay;
    }

    private PaymentRequest getValidPayPalPayment() {
        PaymentRequest pay = getValidPayment();
        pay.setPaypalParam("5");
        pay.setPaymentProvider(PaymentProvider.PAYPAL);
        return pay;
    }

    private PaymentRequest getValidSepaPayment() {
        PaymentRequest pay = getValidPayment();
        pay.setIban("DE02120300000000202051");
        pay.setPaymentProvider(PaymentProvider.SEPA);
        return pay;
    }

    private Bill getValidBill() throws JSONException {
        Bill bill = new Bill();
        User user = createUser();
        bill.setValue(200.0);
        bill.setId(1L);
        bill.setUserId(user.getId());
        bill.setCity(user.getCity());
        bill.setCountry(user.getCountry());
        bill.setName(user.getName());
        bill.setZipCode(user.getZipCode());
        bill.setCreationDate(Instant.now());
        bill.setPaymentDeadlineDate(Instant.now().plus(Period.ofDays(90)));
        bill.setPaid(false);
        return bill;
    }

    private User createUser() throws JSONException {
        long userId = 1L;
        User user = new User();
        JSONObject validUser = getValidUser();
        user.setId(userId);
        user.setFirstName(validUser.getString("firstName"));
        user.setLastName(validUser.getString("lastName"));
        user.setUserName(validUser.getString("userName"));
        user.setEmailAddress(validUser.getString("emailAddress"));
        user.setExpirationDateDriversLicense(Instant.now());
        return user;
    }

    private JSONObject getValidUser() throws JSONException {
        JSONObject body = new JSONObject();
        body.put("firstName", "Alexander");
        body.put("lastName", "Meier");
        body.put("userName", "Fussballgott");
        body.put("emailAddress", "a@b.c");
        body.put("expirationDateDriversLicense", Instant.now().toString());
        body.put("street", "Mörfelder Landstraße 362");
        body.put("zipCode", "60528");
        body.put("city", "Frankfurt am Main");
        body.put("country", "Germany");
        return body;
    }
}