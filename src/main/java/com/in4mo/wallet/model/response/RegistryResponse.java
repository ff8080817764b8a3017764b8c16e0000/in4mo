package com.in4mo.wallet.model.response;

import com.in4mo.wallet.model.Registry;
import lombok.Data;

@Data
public class RegistryResponse {
    private String id;
    private String label;
    private int amount;

    public RegistryResponse(Registry registry) {
        this.id = registry.getId();
        this.label = registry.getLabel();
        this.amount = registry.getAmount();
    }
}
