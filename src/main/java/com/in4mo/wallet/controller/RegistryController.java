package com.in4mo.wallet.controller;

import com.google.common.collect.ImmutableList;
import com.in4mo.wallet.model.Registry;
import com.in4mo.wallet.repository.RegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/budget")
public class RegistryController {

    @Autowired
    private RegistryRepository registryRepository;

    @GetMapping("{userId}/registry")
    public ResponseEntity<List<Registry>> getRegistries() {
        Registry wallet = new Registry();
        wallet.setAmount(1000);
        wallet.setLabel("Wallet");

        Registry savings = new Registry();
        savings.setAmount(5000);
        savings.setLabel("Savings");

        Registry insurance = new Registry();
        insurance.setAmount(0);
        insurance.setLabel("Insurance policy");

        Registry food = new Registry();
        food.setAmount(0);
        food.setLabel("Food expenses");

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

        return ResponseEntity.ok(ImmutableList.copyOf(registryRepository.findAll()));
    }
}
