package com.asys1920.paymentservice.adapter;

import com.asys1920.dto.BillDTO;
import com.asys1920.mapper.BillMapper;
import com.asys1920.model.Bill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;

@Component
public class AccountingServiceAdapter {

    @Value("${bill.url}")
    private String accountingServiceUrl;
    private final RestTemplate restTemplate;

    public AccountingServiceAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Bill getBill(Long billId) throws ServiceUnavailableException {
        try {
            BillDTO billDTO = restTemplate.getForObject(accountingServiceUrl + billId, BillDTO.class);
            return BillMapper.INSTANCE.billDTOtoBill(billDTO);
        } catch (Exception ex) {
            throw new ServiceUnavailableException("AccountingService is currently unavailable. Please try again later.");
        }
    }

    public Bill saveBill(Bill bill) throws ServiceUnavailableException {
        try {
            BillDTO billDTO = BillMapper.INSTANCE.billToBillDTO(bill);
            HttpEntity<BillDTO> request = new HttpEntity<>(billDTO);
            return BillMapper.INSTANCE.billDTOtoBill(restTemplate.postForObject(accountingServiceUrl, request, BillDTO.class));
        } catch (Exception ex) {
            throw new ServiceUnavailableException("AccountingService is currently unavailable. Please try again later.");
        }
    }
}
