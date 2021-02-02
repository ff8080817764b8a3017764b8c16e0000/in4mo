package com.in4mo.wallet;

import com.in4mo.wallet.model.Registry;
import com.in4mo.wallet.repository.RegistryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTests {

    private final static String ERROR_PATH = "$.error";
    private final static String MESSAGE_PATH = "$.message";
    private final static String PATH_PATH = "$.path";
    private final static String STATUS_PATH = "$.status";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegistryRepository registryRepository;

    @BeforeEach
    void init() {
        registryRepository.deleteAll();
    }

    @Test
    void shouldFindRegistersWithInitialAmount_WhenCreated() throws Exception {
        final String userId = "1";
        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry savings = new Registry("Savings", userId, 5000);
        Registry insurance = new Registry("Insurance policy", userId, 0);
        Registry food = new Registry("Food expenses", userId, 0);

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

        mockMvc
                .perform(get("/api/budget/1/registry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].label", is("Wallet")))
                .andExpect(jsonPath("$[0].id", is(wallet.getId())))
                .andExpect(jsonPath("$[0].amount", is(1000)))
                .andExpect(jsonPath("$[1].label", is("Savings")))
                .andExpect(jsonPath("$[1].id", is(savings.getId())))
                .andExpect(jsonPath("$[1].amount", is(5000)))
                .andExpect(jsonPath("$[2].label", is("Insurance policy")))
                .andExpect(jsonPath("$[2].id", is(insurance.getId())))
                .andExpect(jsonPath("$[2].amount", is(0)))
                .andExpect(jsonPath("$[3].label", is("Food expenses")))
                .andExpect(jsonPath("$[3].id", is(food.getId())))
                .andExpect(jsonPath("$[3].amount", is(0)));
    }

    @Test
    void shouldReturnNotFound_WhenAccountWithGivenIdWasNotFound() throws Exception {
        final String userId = "1";
        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry savings = new Registry("Savings", userId, 5000);
        Registry insurance = new Registry("Insurance policy", userId, 0);
        Registry food = new Registry("Food expenses", userId, 0);

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

        mockMvc
                .perform(get("/api/budget/2/registry"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_PATH, is("RegistryNotFoundException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("No registries found for userId: '2'")))
                .andExpect(jsonPath(PATH_PATH, is("/api/budget/2/registry")))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void shouldAddFunds_OnRecharge() throws Exception {
        final String userId = "1";
        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry saved = registryRepository.save(wallet);

        mockMvc
                .perform(post(composeRechargeUrl(userId, saved.getId()))
                        .content(composeRechargeBody(2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        Registry updatedWallet = registryRepository.findByUserId(userId).get(0);
        assertThat(updatedWallet.getAmount()).isEqualTo(3500);
        assertThat(updatedWallet.getId()).isNotBlank();
        assertThat(updatedWallet.getLabel()).isEqualTo("Wallet");
        assertThat(updatedWallet.getUserId()).isEqualTo(userId);
    }

    @Test
    void shouldReturnBadRequest_WhenAmountIsNegativeNumber_OnRecharge() throws Exception {
        final String userId = "1";

        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry saved = registryRepository.save(wallet);

        mockMvc
                .perform(post(composeRechargeUrl(userId, saved.getId()))
                        .content(composeRechargeBody(-2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_PATH, is("MethodArgumentNotValidException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Recharge amount must be greater or equal to 0")))
                .andExpect(jsonPath(PATH_PATH, is(composeRechargeUrl(userId, saved.getId()))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void shouldReturnNotFound_WhenRegistryWasNotFound_OnRecharge() throws Exception {
        final String userId = "1";

        mockMvc
                .perform(post(composeRechargeUrl(userId, "not_existing_registry_id"))
                        .content(composeRechargeBody(2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_PATH, is("RegistryNotFoundException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Registry 'not_existing_registry_id' not found for user: '1'")))
                .andExpect(jsonPath(PATH_PATH, is(composeRechargeUrl(userId, "not_existing_registry_id"))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void shouldReturnNotFound_WhenSourceRegistryWasNotFound_OnTransfer() throws Exception {
        final String userId = "1";

        mockMvc
                .perform(post(composeTransferUrl(userId, "not_existing_registry_id"))
                        .content(composeTransferBody("existing", 2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_PATH, is("RegistryNotFoundException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Source registry 'not_existing_registry_id' not found for user: '1'")))
                .andExpect(jsonPath(PATH_PATH, is(composeTransferUrl(userId, "not_existing_registry_id"))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void shouldReturnNotFound_WhenTargetRegistryWasNotFound_OnTransfer() throws Exception {
        final String userId = "1";
        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry saved = registryRepository.save(wallet);

        mockMvc
                .perform(post(composeTransferUrl(userId, saved.getId()))
                        .content(composeTransferBody("not_existing_registry_id", 2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(ERROR_PATH, is("RegistryNotFoundException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Target registry 'not_existing_registry_id' not found for user: '1'")))
                .andExpect(jsonPath(PATH_PATH, is(composeTransferUrl(userId, saved.getId()))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void shouldReturnBadRequest_WhenSourceHasNotEnoughFunds_OnTransfer() throws Exception {
        final String userId = "1";

        Registry saved = registryRepository.save(new Registry("Source", userId, 1000));
        Registry target = registryRepository.save(new Registry("Target", userId, 1000));

        mockMvc
                .perform(post(composeTransferUrl(userId, saved.getId()))
                        .content(composeTransferBody(target.getId(), 2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_PATH, is("InvalidRequestException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Not enough funds for the transfer. Source amount: 1000, requested transfer: 2500")))
                .andExpect(jsonPath(PATH_PATH, is(composeTransferUrl(userId, saved.getId()))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void shouldReturnBadRequest_WhenAmountIsNegative_OnTransfer() throws Exception {
        final String userId = "1";

        Registry source = new Registry("Source", userId, 1000);
        Registry target = new Registry("Target", userId, 1000);

        Registry saved = registryRepository.save(source);

        mockMvc
                .perform(post(composeTransferUrl(userId, saved.getId()))
                        .content(composeTransferBody(target.getId(), -2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_PATH, is("MethodArgumentNotValidException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("Transfer amount must be greater or equal to 0")))
                .andExpect(jsonPath(PATH_PATH, is(composeTransferUrl(userId, saved.getId()))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void shouldReturnBadRequest_WhenRequestBodyIsEmpty_OnTransfer() throws Exception {
        final String userId = "1";

        Registry source = new Registry("Source", userId, 1000);
        Registry target = new Registry("Target", userId, 1000);

        Registry saved = registryRepository.save(source);

        mockMvc
                .perform(post(composeTransferUrl(userId, saved.getId()))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_PATH, is("MethodArgumentNotValidException")))
                .andExpect(jsonPath(MESSAGE_PATH, is("TargetRegistryId can not be null")))
                .andExpect(jsonPath(PATH_PATH, is(composeTransferUrl(userId, saved.getId()))))
                .andExpect(jsonPath(STATUS_PATH, is(HttpStatus.BAD_REQUEST.value())));
    }

    /**
     * 1. A recharge is executed for the “Wallet” register with an amount of 2500.This should increase
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
    void acceptanceTest() throws Exception {
        final String userId = "1";

        Registry wallet = new Registry("Wallet", userId, 1000);
        Registry savings = new Registry("Savings", userId, 5000);
        Registry insurance = new Registry("Insurance policy", userId, 0);
        Registry food = new Registry("Food expenses", userId, 0);

        registryRepository.save(wallet);
        registryRepository.save(savings);
        registryRepository.save(insurance);
        registryRepository.save(food);

        mockMvc
                .perform(post(composeRechargeUrl(userId, wallet.getId()))
                        .content(composeRechargeBody(2500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(registryRepository.findByIdAndUserId(wallet.getId(), userId).getAmount()).isEqualTo(3500);

        mockMvc
                .perform(post(composeTransferUrl(userId, wallet.getId()))
                        .content(composeTransferBody(food.getId(), 1500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(registryRepository.findByIdAndUserId(wallet.getId(), userId).getAmount()).isEqualTo(2000);
        assertThat(registryRepository.findByIdAndUserId(food.getId(), userId).getAmount()).isEqualTo(1500);

        mockMvc
                .perform(post(composeTransferUrl(userId, savings.getId()))
                        .content(composeTransferBody(insurance.getId(), 500))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(registryRepository.findByIdAndUserId(savings.getId(), userId).getAmount()).isEqualTo(4500);
        assertThat(registryRepository.findByIdAndUserId(insurance.getId(), userId).getAmount()).isEqualTo(500);

        mockMvc
                .perform(post(composeTransferUrl(userId, wallet.getId()))
                        .content(composeTransferBody(savings.getId(), 1000))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(registryRepository.findByIdAndUserId(savings.getId(), userId).getAmount()).isEqualTo(5500);
        assertThat(registryRepository.findByIdAndUserId(wallet.getId(), userId).getAmount()).isEqualTo(1000);

        mockMvc
                .perform(get("/api/budget/1/registry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].label", is("Wallet")))
                .andExpect(jsonPath("$[0].id", is(wallet.getId())))
                .andExpect(jsonPath("$[0].amount", is(1000)))
                .andExpect(jsonPath("$[1].label", is("Savings")))
                .andExpect(jsonPath("$[1].id", is(savings.getId())))
                .andExpect(jsonPath("$[1].amount", is(5500)))
                .andExpect(jsonPath("$[2].label", is("Insurance policy")))
                .andExpect(jsonPath("$[2].id", is(insurance.getId())))
                .andExpect(jsonPath("$[2].amount", is(500)))
                .andExpect(jsonPath("$[3].label", is("Food expenses")))
                .andExpect(jsonPath("$[3].id", is(food.getId())))
                .andExpect(jsonPath("$[3].amount", is(1500)));
    }

    private String composeTransferUrl(String userId, String registryId) {
        return String.format("/api/budget/%s/registry/%s/transfer", userId, registryId);
    }

    private String composeRechargeUrl(String userId, String registryId) {
        return String.format("/api/budget/%s/registry/%s/recharge", userId, registryId);
    }

    private String composeRechargeBody(int amount) {
        return String.format("{\"amount\":%s}", amount);
    }

    private String composeTransferBody(String targetRegistryId, int amount) {
        return String.format("{\"amount\":%s, \"targetRegistryId\" : \"%s\"}", amount, targetRegistryId);
    }
}
