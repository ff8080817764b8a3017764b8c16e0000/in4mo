package com.in4mo.wallet;

import com.in4mo.wallet.model.Registry;
import com.in4mo.wallet.repository.RegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistryRepository registryRepository;

    @BeforeEach
    void init() {
        registryRepository.deleteAll();
    }

    /**
     * A demo environment with the following registers already existing:
     * a. “Wallet” register with a balance of 1000
     * b. “Savings” register with a balance of 5000
     * c. “Insurance policy” register with a balance of 0
     * d. “Food expenses” register with a balance of 0
     */
    @Test
    void shouldFindRegistersWithInitialAmount_AfterStartup() throws Exception {
        Registry wallet = new Registry("Wallet", "1", 1000);
        Registry savings = new Registry("Savings", "1", 5000);
        Registry insurance = new Registry("Insurance policy", "1", 0);
        Registry food = new Registry("Food expenses", "1", 0);

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

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

    @Test
    void shouldReturnNotFound_WhenAccountWithGivenIdWasNotFound() throws Exception {
        Registry wallet = new Registry("Wallet", "1", 1000);
        Registry savings = new Registry("Savings", "1", 5000);
        Registry insurance = new Registry("Insurance policy", "1", 0);
        Registry food = new Registry("Food expenses", "1", 0);

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

        mockMvc
                .perform(get("/api/budget/2/registry"))
                .andExpect(status().isNotFound());
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
    @Test
    void shouldAddFunds_WhenRechargeIsPerformed() throws Exception {
        //given
        Registry wallet = new Registry("Wallet", "1", 1000);
        Registry saved = registryRepository.save(wallet);

        //when
        mockMvc
                .perform(post("/api/budget/1/registry/" + saved.getId() + "/recharge")
                        .content("{\"amount\":2500}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //then
        Registry updatedWallet = registryRepository.findByUserId("1").get(0);
        assertThat(updatedWallet.getAmount()).isEqualTo(3500);
        assertThat(updatedWallet.getId()).isNotBlank();
        assertThat(updatedWallet.getLabel()).isEqualTo("Wallet");
        assertThat(updatedWallet.getUserId()).isEqualTo("1");
    }

    @Test
    void shouldReturnBadRequest_WhenRechargeAmountIsNegativeNumber() throws Exception {
        //given
        Registry wallet = new Registry("Wallet", "1", 1000);
        Registry saved = registryRepository.save(wallet);

        //when
        mockMvc
                .perform(post("/api/budget/1/registry/" + saved.getId() + "/recharge")
                        .content("{\"amount\":-2500}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("MethodArgumentNotValidException")))
                .andExpect(jsonPath("$.message", is("Recharge amount must be greater or equal to 0")))
                .andExpect(jsonPath("$.path", is("/api/budget/1/registry/" + saved.getId() + "/recharge")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void shouldReturnNotFound_WhenRegistryWasNotFound() throws Exception {
        //given
        Registry wallet = new Registry("Wallet", "1", 1000);

        //when
        mockMvc
                .perform(post("/api/budget/1/registry/not_existing/recharge")
                        .content("{\"amount\":2500}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("RegistryNotFoundException")))
                .andExpect(jsonPath("$.message", is("Registry 'not_existing' not found for user: '1'")))
                .andExpect(jsonPath("$.path", is("/api/budget/1/registry/not_existing/recharge")))
                .andExpect(jsonPath("$.status", is(404)));
    }
}
