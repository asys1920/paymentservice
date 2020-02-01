package com.asys1920.paymentservice;

import com.asys1920.paymentservice.model.Bill;
import com.asys1920.paymentservice.model.PaymentStatus;
import com.asys1920.paymentservice.repository.BillRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentserviceApplicationTests {

    static final private String validIBAN = "DE08700901001234567890";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillRepository billRepository;

    private String payEndpoint = "/pay";
    private String paymentStatusEndpoint = "/paymentStatus";

    @Test
    void should_Return_Error_when_no_Bill_is_saved() throws Exception {

        mockMvc.perform(get(paymentStatusEndpoint + "?id=" + 404))
                .andExpect(status().is(404));
    }

    @Test
    void should_get_paymentStatus_when_get_is_valid() throws Exception {
        Bill bill = new Bill();
        bill.setIban("DE08700901001234567890");
        bill.setAmount(250);
        bill.setId(15);
        bill.setPaymentStatus(PaymentStatus.PAID);
        billRepository.save(bill);

        mockMvc.perform(get(paymentStatusEndpoint + "?id=" + bill.getId()))
                .andExpect(content().string("\"PAID\""))
                .andExpect(status().is(200));
    }

    @Test
    void value_should_be_zero_after_paying() throws Exception {
        JSONObject body = new JSONObject();
        body.put("id", "12");
        body.put("amount", "19025");
        body.put("iban", validIBAN);
        mockMvc.perform(post(payEndpoint + "?type=sepa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount", is(0)))
                .andExpect(status().isCreated());
    }

    @Test
    void should_return_ok_when_Post_is_valid() throws Exception {
        JSONObject body = new JSONObject();
        body.put("id", "12");
        body.put("amount", "125");
        body.put("iban", validIBAN);
        mockMvc.perform(post(payEndpoint + "?type=sepa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void should_return_Everything_works() throws Exception {
        int billid = 12;
        JSONObject body = new JSONObject();
        body.put("id", billid);
        body.put("amount", "125");
        body.put("iban", validIBAN);
        mockMvc.perform(post(payEndpoint + "?type=sepa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get(paymentStatusEndpoint + "?id=" + billid))
                .andExpect(content().string("\"PAID\""))
                .andExpect(status().is(200));
    }

}