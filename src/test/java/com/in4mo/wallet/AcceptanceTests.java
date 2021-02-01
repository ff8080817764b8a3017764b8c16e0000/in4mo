package com.in4mo.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * A demo environment with the following registers already existing:
     * a. “Wallet” register with a balance of 1000
     * b. “Savings” register with a balance of 5000
     * c. “Insurance policy” register with a balance of 0
     * d. “Food expenses” register with a balance of 0
     */
    @Test
    void shouldFindRegistersWithInitialAmount_AfterStartup () throws Exception {
        mockMvc
                .perform(get("/api/budget/1/registry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].label", is("Wallet")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].amount", is(1000)))
                .andExpect(jsonPath("$[1].label", is("Savings")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].amount", is(5000)))
                .andExpect(jsonPath("$[2].label", is("Insurance policy")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].amount", is(0)))
                .andExpect(jsonPath("$[3].label", is("Food expenses")))
                .andExpect(jsonPath("$[3].id", is(4)))
                .andExpect(jsonPath("$[3].amount", is(0)));
    }

    /**
     * A recharge is executed for the “Wallet” register with an amount of 2500.This should increase
     * the register’s balance to 3500.
     * 2. A transfer of 1500 from “Wallet” to “Food expenses” registry is executed. This should bring
     * “Wallet” balance to 2000 and “Food expenses” balance to 1500.
     * 3. A transfer of 500 from “Savings” to “Insurance policy” registry is executed. This should bring
     * “Savings” balance to 4500 and “Insurance policy” balance to 500.
     * 4. A transfer of 1000 from “Wallet” to “Savings” registry is executed. This should bring “Wallet”
     * balance to 1000 and “Savings” balance to 5500.
     * 5. Balance info on all registries is executed: this should print the list of all registries
     * accompanied by their balance, for example:
     * Wallet: 1000
     * Savings: 5500
     * Insurance policy: 500
     * Food expenses: 1500
     */
}
