# Project TODO

Task board for the Secure Transaction Ledger. Pick tasks matching your role.
Mark items `[x]` when done and push.

---

## Week 1 — Get It Running

- [x] Fix startup crash (Double → BigDecimal, H2 SQL syntax)
- [x] Add unit tests for service and controller
- [ ] Each team member: clone the repo, run `mvn spring-boot:run`, hit `/api/balance/1`
- [ ] Record a 30-second screen recording of the app working

## Week 2 — Core Features

### Final Year (Architect)
- [ ] Design the User/Auth schema (users table, password hashing)
- [ ] Define API contract for login/register endpoints
- [ ] Set up a shared dev branch strategy (e.g., `dev` branch, feature branches)

### 2nd Year (Feature Developer)
- [ ] Add `POST /api/accounts` — create a new account (owner name, initial balance)
- [ ] Add `GET /api/transactions/{accountId}` — list transaction history for an account
- [ ] Add input validation with `@Valid` annotations on request bodies

### 1st Year (UI & QA)
- [ ] Create a simple HTML page with a form to transfer funds (calls POST `/api/transfer`)
- [ ] Create a page that shows account balance (calls GET `/api/balance/{id}`)
- [ ] Run `mvn test` after every change and report failures as GitHub Issues

## Week 3 — Security & Polish

### Final Year
- [ ] Add Spring Security with JWT authentication
- [ ] Protect all `/api/*` endpoints behind login
- [ ] Switch from H2 to PostgreSQL for production

### 2nd Year
- [ ] Add pagination to transaction history endpoint
- [ ] Add `GET /api/accounts` — list all accounts
- [ ] Add error response for invalid JSON input (return 400, not 500)

### 1st Year
- [ ] Add a login page that stores the JWT token
- [ ] Style the pages with CSS
- [ ] Write 5 more test cases for edge cases (empty name, negative balance, etc.)

## Backlog (Future)

- [ ] Transaction categories/tags
- [ ] Monthly balance reports
- [ ] Export transactions as CSV
- [ ] Rate limiting on transfer endpoint
- [ ] Audit log for all account changes
