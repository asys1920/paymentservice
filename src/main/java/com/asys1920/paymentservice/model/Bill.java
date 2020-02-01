package com.asys1920.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Bill {

    @Id
    private long id;
    private int amount;
    private PaymentStatus paymentStatus;

    private String iban;
}
