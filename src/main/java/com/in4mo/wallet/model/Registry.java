package com.in4mo.wallet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Registry {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    private String label;
    private String userId;
    private int amount;

    public Registry(String label, String userId, int amount) {
        this.label = label;
        this.userId = userId;
        this.amount = amount;
    }
}
