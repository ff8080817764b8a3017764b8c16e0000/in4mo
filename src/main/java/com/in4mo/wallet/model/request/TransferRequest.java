package com.in4mo.wallet.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class TransferRequest {

    @Min(value = 0, message = "Transfer amount must be greater or equal to 0")
    private int amount;

    @NotNull(message = "TargetRegistryId can not be null")
    private String targetRegistryId;
}
