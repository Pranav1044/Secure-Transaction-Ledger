package com.ledger.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(Long accountId, BigDecimal balance, BigDecimal amount) {
        super(String.format("Insufficient funds in account %d. Current balance: %s, Required: %s", 
                accountId, balance.toPlainString(), amount.toPlainString()));
    }
}
