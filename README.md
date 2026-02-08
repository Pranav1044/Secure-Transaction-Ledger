[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/harsh15520/Secure-Transaction-Ledger)

---
# Secure Transaction Ledger

A Spring Boot MVP for managing secure financial transactions. Uses an H2 in-memory database so there's nothing to install — just run and go.

## Features

- Transfer funds between accounts with ACID guarantees
- View account balances via REST API
- All transfers recorded with timestamps
- 5 pre-loaded test accounts

## Prerequisites

- Java 17+
- Maven 3.6+

No database setup needed — H2 runs in-memory automatically.

## Running the Application

```bash
mvn spring-boot:run
```

The app starts on port 8080. The H2 console is available at `/h2-console` (JDBC URL: `jdbc:h2:mem:ledger_db`, user: `sa`, no password).

## Running Tests

```bash
mvn test
```

20 tests cover the service layer and controller endpoints.

---

## How the Code is Organized (MVC Pattern)

This project follows the **Model-View-Controller** pattern. Every web app you'll encounter uses some version of this. Here's how a request flows through the code:

```
User sends HTTP request
        │
        ▼
   Controller          ← receives the request, validates input
        │
        ▼
     Service           ← contains business logic (transfer rules, balance checks)
        │
        ▼
    Repository         ← talks to the database
        │
        ▼
      Model            ← defines the data shape (Account, Transaction)
```

### File Map

| Folder | What it does | Key files |
|--------|-------------|-----------|
| `model/` | Defines the data (database tables) | `Account.java`, `Transaction.java` |
| `repository/` | Reads/writes data to the database | `AccountRepository.java`, `TransactionRepository.java` |
| `service/` | Business logic — the "rules" | `TransactionService.java` |
| `controller/` | HTTP endpoints — what the user calls | `TransactionController.java` |
| `exception/` | Custom error types | `AccountNotFoundException.java`, `InsufficientFundsException.java` |
| `resources/` | Configuration and seed data | `application.properties`, `data.sql` |

### Reading order for newcomers

1. `Account.java` — see what an account looks like (id, balance, owner)
2. `Transaction.java` — see what a transaction record looks like
3. `TransactionController.java` — see the two API endpoints
4. `TransactionService.java` — see the transfer logic
5. `data.sql` — see the test accounts loaded at startup

---

## Team Roles

### Final Year — Architect / PM
- Owns system design, database schema, and high-level logic
- Reviews all pull requests before merge
- Defines what features to build next (see `TODO.md`)

### 2nd Year — Feature Developer
- Picks a module from `TODO.md` and implements it
- Works inside the existing MVC structure (add a new controller, service, etc.)
- Uses AI tools to generate code within the defined patterns

### 1st Year — UI & QA
- Builds frontend pages (HTML/CSS/JS) that call the API
- Writes and runs tests (`mvn test`)
- Reports bugs as GitHub Issues

---

## API Endpoints

### Transfer Funds
**POST** `/api/transfer`

```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 100.50
}
```

| Status | Meaning |
|--------|---------|
| 200 | Transfer succeeded |
| 400 | Validation error or insufficient funds |
| 404 | Account not found |
| 500 | Unexpected server error |

### Get Balance
**GET** `/api/balance/{id}`

```json
{
  "accountId": 1,
  "balance": 1000.00
}
```

## Test Accounts (loaded at startup)

| ID | Owner | Balance |
|----|-------|---------|
| 1 | Alice | $1,000.00 |
| 2 | Bob | $2,000.00 |
| 3 | Charlie | $1,500.50 |
| 4 | Diana | $500.00 |
| 5 | Eve | $3,000.75 |

## Example Usage

```bash
# Transfer $250 from Alice to Bob
curl -X POST http://localhost:8080/api/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromId": 1, "toId": 2, "amount": 250.00}'

# Check Alice's balance
curl http://localhost:8080/api/balance/1
```

## Technology Stack

- Java 17, Spring Boot 3.2.0, Spring Data JPA
- H2 (in-memory database)
- Lombok, Maven

## Database Schema

### accounts
| Column | Type | Constraint |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| balance | DECIMAL(19,2) | NOT NULL |
| owner | VARCHAR | NOT NULL |

### transactions
| Column | Type | Constraint |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| from_account | BIGINT | NOT NULL |
| to_account | BIGINT | NOT NULL |
| amount | DECIMAL(19,2) | NOT NULL |
| timestamp | TIMESTAMP | NOT NULL |
