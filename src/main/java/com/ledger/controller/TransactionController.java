package com.ledger.controller;

import com.ledger.exception.AccountNotFoundException;
import com.ledger.exception.InsufficientFundsException;
import com.ledger.model.Transaction;
import com.ledger.service.TransactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transactionService.transferFunds(
                    request.getFromId(),
                    request.getToId(),
                    request.getAmount()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Transfer completed successfully",
                    "transactionId", transaction.getId(),
                    "fromAccount", transaction.getFromAccount(),
                    "toAccount", transaction.getToAccount(),
                    "amount", transaction.getAmount(),
                    "timestamp", transaction.getTimestamp()
            ));
        } catch (IllegalArgumentException | InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
    
    @GetMapping("/balance/{id}")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        try {
            BigDecimal balance = transactionService.getBalance(id);
            return ResponseEntity.ok(Map.of(
                    "accountId", id,
                    "balance", balance
            ));
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
    
    @Data
    static class TransferRequest {
        private Long fromId;
        private Long toId;
        private BigDecimal amount;
    }
}
