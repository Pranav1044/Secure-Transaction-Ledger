package com.ledger.service;

import com.ledger.exception.AccountNotFoundException;
import com.ledger.exception.InsufficientFundsException;
import com.ledger.model.Account;
import com.ledger.model.Transaction;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

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
    void transferFunds_success() {
        Transaction tx = transactionService.transferFunds(
                alice.getId(), bob.getId(), new BigDecimal("250.00"));

        assertNotNull(tx.getId());
        assertEquals(alice.getId(), tx.getFromAccount());
        assertEquals(bob.getId(), tx.getToAccount());
        assertEquals(0, new BigDecimal("250.00").compareTo(tx.getAmount()));
        assertNotNull(tx.getTimestamp());

        BigDecimal aliceBalance = transactionService.getBalance(alice.getId());
        BigDecimal bobBalance = transactionService.getBalance(bob.getId());
        assertEquals(0, new BigDecimal("750.00").compareTo(aliceBalance));
        assertEquals(0, new BigDecimal("750.00").compareTo(bobBalance));
    }

    @Test
    void transferFunds_preservesTotalBalance() {
        BigDecimal totalBefore = transactionService.getBalance(alice.getId())
                .add(transactionService.getBalance(bob.getId()));

        transactionService.transferFunds(alice.getId(), bob.getId(), new BigDecimal("100.00"));
        transactionService.transferFunds(bob.getId(), alice.getId(), new BigDecimal("50.00"));

        BigDecimal totalAfter = transactionService.getBalance(alice.getId())
                .add(transactionService.getBalance(bob.getId()));

        assertEquals(0, totalBefore.compareTo(totalAfter));
    }

    @Test
    void transferFunds_insufficientFunds() {
        assertThrows(InsufficientFundsException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), bob.getId(), new BigDecimal("1500.00")));

        // Balances unchanged
        assertEquals(0, new BigDecimal("1000.00").compareTo(
                transactionService.getBalance(alice.getId())));
        assertEquals(0, new BigDecimal("500.00").compareTo(
                transactionService.getBalance(bob.getId())));
    }

    @Test
    void transferFunds_exactBalance() {
        Transaction tx = transactionService.transferFunds(
                alice.getId(), bob.getId(), new BigDecimal("1000.00"));

        assertNotNull(tx.getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(
                transactionService.getBalance(alice.getId())));
        assertEquals(0, new BigDecimal("1500.00").compareTo(
                transactionService.getBalance(bob.getId())));
    }

    @Test
    void transferFunds_nullAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(alice.getId(), bob.getId(), null));
    }

    @Test
    void transferFunds_zeroAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), bob.getId(), BigDecimal.ZERO));
    }

    @Test
    void transferFunds_negativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), bob.getId(), new BigDecimal("-100.00")));
    }

    @Test
    void transferFunds_sameAccount() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), alice.getId(), new BigDecimal("100.00")));
    }

    @Test
    void transferFunds_nullFromId() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(
                        null, bob.getId(), new BigDecimal("100.00")));
    }

    @Test
    void transferFunds_nullToId() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), null, new BigDecimal("100.00")));
    }

    @Test
    void transferFunds_nonExistentFromAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transactionService.transferFunds(
                        9999L, bob.getId(), new BigDecimal("100.00")));
    }

    @Test
    void transferFunds_nonExistentToAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transactionService.transferFunds(
                        alice.getId(), 9999L, new BigDecimal("100.00")));
    }

    @Test
    void transferFunds_bigDecimalPrecision() {
        // Verify that 0.1 + 0.2 style operations don't lose precision
        transactionService.transferFunds(
                alice.getId(), bob.getId(), new BigDecimal("0.10"));
        transactionService.transferFunds(
                alice.getId(), bob.getId(), new BigDecimal("0.20"));

        BigDecimal aliceBalance = transactionService.getBalance(alice.getId());
        assertEquals(0, new BigDecimal("999.70").compareTo(aliceBalance));
    }

    @Test
    void getBalance_nonExistentAccount() {
        assertThrows(AccountNotFoundException.class, () ->
                transactionService.getBalance(9999L));
    }
}
