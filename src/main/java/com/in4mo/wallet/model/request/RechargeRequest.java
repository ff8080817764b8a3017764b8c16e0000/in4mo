package com.in4mo.wallet.model.request;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class RechargeRequest {

    @Min(value = 0, message = "Recharge amount must be greater or equal to 0")
    private int amount;
}
