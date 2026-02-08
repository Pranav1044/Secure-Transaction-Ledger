[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/harsh15520/Secure-Transaction-Ledger)

---
# Secure Transaction Ledger

A Spring Boot MVP application for managing secure financial transactions with PostgreSQL database.

## Features

- **Account Management**: Create and manage accounts with balances
- **Secure Transactions**: Transfer funds between accounts with ACID guarantees
- **Transaction History**: All transfers are recorded with timestamps
- **Balance Inquiry**: Check account balances via REST API

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (running on localhost:5432)

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE ledger_db;
```

2. Update `src/main/resources/application.properties` with your PostgreSQL credentials if different from defaults:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ledger_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Running the Application

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Transfer Funds
**POST** `/api/transfer`

Request body:
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 100.50
}
```

Success Response (200 OK):
```json
{
  "message": "Transfer completed successfully",
  "transactionId": 1,
  "fromAccount": 1,
  "toAccount": 2,
  "amount": 100.50,
  "timestamp": "2024-01-15T10:30:00"
}
```

Error Response (400 Bad Request):
```json
{
  "error": "Insufficient funds in account 1. Current balance: 500.00, Required: 1000.00"
}
```

### Get Balance
**GET** `/api/balance/{id}`

Example: `GET /api/balance/1`

Success Response (200 OK):
```json
{
  "accountId": 1,
  "balance": 1000.00
}
```

Error Response (404 Not Found):
```json
{
  "error": "Account with ID 999 not found"
}
```

## Initial Test Data

The application comes with 5 pre-configured accounts (loaded from `data.sql`):
- Account 1: Alice - $1000.00
- Account 2: Bob - $2000.00
- Account 3: Charlie - $1500.50
- Account 4: Diana - $500.00
- Account 5: Eve - $3000.75

## Transaction Safety

The `transferFunds` method ensures data integrity through:

- **@Transactional**: Ensures atomicity - all operations succeed or all fail
- **Pessimistic Locking**: Prevents concurrent modifications using `PESSIMISTIC_WRITE` lock mode
- **Automatic Rollback**: If the system crashes mid-way, the transaction is rolled back automatically
- **Balance Consistency**: Total balance across all accounts remains constant even during failures

## Error Handling

The application includes custom exceptions:
- `AccountNotFoundException`: When an account ID doesn't exist
- `InsufficientFundsException`: When attempting to transfer more than available balance

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok** (for reducing boilerplate code)
- **Maven** (build tool)

## Project Structure

```
src/main/java/com/ledger/
├── SecureTransactionLedgerApplication.java  # Main application class
├── controller/
│   └── TransactionController.java           # REST endpoints
├── service/
│   └── TransactionService.java              # Business logic with @Transactional
├── repository/
│   ├── AccountRepository.java               # Account data access
│   └── TransactionRepository.java           # Transaction data access
├── model/
│   ├── Account.java                         # Account entity
│   └── Transaction.java                     # Transaction entity
└── exception/
    ├── AccountNotFoundException.java         # Custom exception
    └── InsufficientFundsException.java      # Custom exception
```

## Example Usage

### Transfer funds from Account 1 to Account 2:
```bash
curl -X POST http://localhost:8080/api/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromId": 1,
    "toId": 2,
    "amount": 250.00
  }'
```

### Check balance of Account 1:
```bash
curl http://localhost:8080/api/balance/1
```

## Database Schema

### Accounts Table
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `balance` (DECIMAL(19,2), NOT NULL)
- `owner` (VARCHAR, NOT NULL)

### Transactions Table
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
- `from_account` (BIGINT, NOT NULL)
- `to_account` (BIGINT, NOT NULL)
- `amount` (DECIMAL(19,2), NOT NULL)
- `timestamp` (TIMESTAMP, NOT NULL)
