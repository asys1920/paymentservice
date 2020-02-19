package com.asys1920.paymentservice.adapter;

import com.asys1920.dto.BillDTO;
import com.asys1920.mapper.BillMapper;
import com.asys1920.model.Bill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountingServiceAdapter {

    @Value("${accountingService.url}")
    private String accountingServiceUrl;
    private final RestTemplate restTemplate;

    public AccountingServiceAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Bill getBill(Long billId) {
        BillDTO billDTO = restTemplate.getForObject(accountingServiceUrl + "/bills/" + billId, BillDTO.class);
        return BillMapper.INSTANCE.billDTOtoBill(billDTO);
    }
}
