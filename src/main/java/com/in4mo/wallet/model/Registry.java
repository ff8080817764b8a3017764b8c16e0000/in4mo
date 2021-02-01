package com.in4mo.wallet.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Registry {
    private String label;
    @Id
    @GeneratedValue
    private Integer id;
    private int amount;
}
