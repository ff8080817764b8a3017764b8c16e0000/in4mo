package com.in4mo.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/budget")
public class RegistryController {

    @GetMapping("{userId}/registry")
    public ResponseEntity<String> getRegistries() {
        return ResponseEntity.ok("[\n" +
                "    {\n" +
                "        \"label\" : \"Wallet\",\n" +
                "        \"id\" : 1,\n" +
                "        \"amount\" : 1000 \n" +
                "    },\n" +
                "    {\n" +
                "        \"label\" : \"Savings\",\n" +
                "        \"id\" : 2,\n" +
                "        \"amount\" : 5000 \n" +
                "    },\n" +
                "    {\n" +
                "        \"label\" : \"Insurance policy\",\n" +
                "        \"id\" : 3,\n" +
                "        \"amount\" : 0 \n" +
                "    },\n" +
                "    {\n" +
                "        \"label\" : \"Food expenses\",\n" +
                "        \"id\" : 4,\n" +
                "        \"amount\" : 0 \n" +
                "    }\n" +
                "]");
    }
}
