package com.in4mo.wallet.controller;

import com.google.common.collect.ImmutableList;
import com.in4mo.wallet.model.request.RechargeRequest;
import com.in4mo.wallet.model.request.TransferRequest;
import com.in4mo.wallet.model.response.RegistryResponse;
import com.in4mo.wallet.service.RegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/budget")
public class RegistryController {

    private final RegistryService registryService;

    public RegistryController(RegistryService registryService) {
        this.registryService = registryService;
    }

    @GetMapping("{userId}/registry")
    public ResponseEntity<List<RegistryResponse>> getRegistries(@PathVariable String userId) {
        return ResponseEntity.ok(ImmutableList.copyOf(registryService.findByUserId(userId)));
    }

    @PostMapping("{userId}/registry/{registryId}/recharge")
    public ResponseEntity recharge(@PathVariable String userId,
                                   @PathVariable String registryId,
                                   @RequestBody @Valid RechargeRequest rechargeRequest) {
        registryService.recharge(userId, registryId, rechargeRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{userId}/registry/{registryId}/transfer")
    public ResponseEntity transfer(@PathVariable String userId,
                                   @PathVariable String registryId,
                                   @RequestBody @Valid TransferRequest transferRequest) {
        registryService.transfer(userId, registryId, transferRequest);
        return ResponseEntity.ok().build();
    }
}
