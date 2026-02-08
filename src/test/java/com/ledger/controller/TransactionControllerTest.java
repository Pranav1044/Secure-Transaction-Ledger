package com.ledger.controller;

import com.ledger.model.Account;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account alice;
    private Account bob;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        alice = new Account();
        alice.setBalance(new BigDecimal("1000.00"));
        alice.setOwner("Alice");
        alice = accountRepository.save(alice);

        bob = new Account();
        bob.setBalance(new BigDecimal("500.00"));
        bob.setOwner("Bob");
        bob = accountRepository.save(bob);
    }

    @Test
    void transfer_success() throws Exception {
        String body = String.format(
                "{\"fromId\": %d, \"toId\": %d, \"amount\": 200.00}",
                alice.getId(), bob.getId());

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer completed successfully"))
                .andExpect(jsonPath("$.transactionId").isNumber())
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void transfer_insufficientFunds_returns400() throws Exception {
        String body = String.format(
                "{\"fromId\": %d, \"toId\": %d, \"amount\": 5000.00}",
                alice.getId(), bob.getId());

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void transfer_nonExistentAccount_returns404() throws Exception {
        String body = String.format(
                "{\"fromId\": 9999, \"toId\": %d, \"amount\": 100.00}",
                bob.getId());

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void transfer_sameAccount_returns400() throws Exception {
        String body = String.format(
                "{\"fromId\": %d, \"toId\": %d, \"amount\": 100.00}",
                alice.getId(), alice.getId());

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getBalance_success() throws Exception {
        mockMvc.perform(get("/api/balance/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(alice.getId()))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void getBalance_nonExistentAccount_returns404() throws Exception {
        mockMvc.perform(get("/api/balance/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
