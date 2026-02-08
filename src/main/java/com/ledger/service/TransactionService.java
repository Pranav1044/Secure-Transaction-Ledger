package com.ledger.service;

import com.ledger.exception.AccountNotFoundException;
import com.ledger.exception.InsufficientFundsException;
import com.ledger.model.Account;
import com.ledger.model.Transaction;
import com.ledger.repository.AccountRepository;
import com.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public Transaction transferFunds(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Account IDs must not be null");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }
        
        // Acquire locks in consistent ID order to prevent deadlocks when
        // concurrent transfers operate on the same pair of accounts.
        Long firstLockId = fromId < toId ? fromId : toId;
        Long secondLockId = fromId < toId ? toId : fromId;
        
        accountRepository.findByIdWithLock(firstLockId)
                .orElseThrow(() -> new AccountNotFoundException(firstLockId));
        accountRepository.findByIdWithLock(secondLockId)
                .orElseThrow(() -> new AccountNotFoundException(secondLockId));
        
        // Re-fetch by role after ordered locking
        Account fromAccount = accountRepository.findById(fromId)
                .orElseThrow(() -> new AccountNotFoundException(fromId));
        Account toAccount = accountRepository.findById(toId)
                .orElseThrow(() -> new AccountNotFoundException(toId));
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId, fromAccount.getBalance(), amount);
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromId);
        transaction.setToAccount(toId);
        transaction.setAmount(amount);
        
        return transactionRepository.save(transaction);
    }
    
    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return account.getBalance();
    }
}
