# Backend Test Cases - CryptoPeak Trading Platform

## Table of Contents
1. [Authentication & User Management](#authentication--user-management)
2. [Balance Management](#balance-management)
3. [Cryptocurrency Data](#cryptocurrency-data)
4. [Trading Operations](#trading-operations)
5. [Limit Orders](#limit-orders)
6. [Payment & Deposits](#payment--deposits)
7. [Security & Authorization](#security--authorization)

---

## Authentication & User Management

### TC-AUTH-001: User Registration - Valid Data
**Endpoint:** `POST /auth/users/register`
**Priority:** High

**Test Data:**
```json
{
  "userName": "testuser",
  "emailAddress": "test@example.com",
  "password": "SecurePass123"
}
```

**Expected Result:** User created successfully with encrypted password, balance=0, isActive=true

---

### TC-AUTH-002: User Registration - Duplicate Email
**Endpoint:** `POST /auth/users/register`
**Priority:** High
**Expected Result:** 400/409 error with "User already exists" message

---

### TC-AUTH-003: User Login - Valid Credentials
**Endpoint:** `POST /auth/users/login`
**Priority:** High
**Expected Result:** 200 OK with valid JWT token

---

### TC-AUTH-004: User Login - Invalid Credentials
**Endpoint:** `POST /auth/users/login`
**Priority:** High
**Expected Result:** 400 error with "Username or password is incorrect"

---

### TC-AUTH-005: Change Password
**Endpoint:** `PUT /auth/users/change-password`
**Priority:** Medium
**Expected Result:** Password updated, old password no longer works

---

## Balance Management

### TC-BAL-001: Get User Balance - Authenticated
**Endpoint:** `GET /api/balance`
**Priority:** High
**Expected Result:** Returns user balance and details

---

### TC-BAL-002: Get User Balance - Unauthenticated
**Endpoint:** `GET /api/balance`
**Priority:** High
**Expected Result:** 401 Unauthorized

---

## Cryptocurrency Data

### TC-CRYPTO-001: Get Top Cryptocurrencies
**Endpoint:** `GET /api/crypto/top?limit=10`
**Priority:** High
**Expected Result:** Returns array of 10 cryptocurrencies from Binance

---

### TC-CRYPTO-002: Get Cryptocurrency by ID
**Endpoint:** `GET /api/crypto/{id}`
**Priority:** High
**Expected Result:** Returns crypto details with current price

---

### TC-CRYPTO-003: Get Historical Prices
**Endpoint:** `GET /api/history/{cryptoId}?days=7`
**Priority:** High
**Expected Result:** Returns price history array from Binance

---

## Trading Operations

### TC-TRADE-001: Buy Cryptocurrency - Sufficient Balance
**Endpoint:** `POST /api/trading/buy`
**Priority:** High
**Expected Result:** Purchase successful, balance decreased, holdings increased

---

### TC-TRADE-002: Buy Cryptocurrency - Insufficient Balance
**Endpoint:** `POST /api/trading/buy`
**Priority:** High
**Expected Result:** 400 error with insufficient balance message

---

### TC-TRADE-003: Sell Cryptocurrency - Sufficient Holdings
**Endpoint:** `POST /api/trading/sell`
**Priority:** High
**Expected Result:** Sale successful, balance increased, holdings decreased

---

### TC-TRADE-004: Sell Cryptocurrency - Insufficient Holdings
**Endpoint:** `POST /api/trading/sell`
**Priority:** High
**Expected Result:** 400 error with insufficient holdings message

---

### TC-TRADE-005: Get User Holdings
**Endpoint:** `GET /api/trading/holdings`
**Priority:** High
**Expected Result:** Returns array of user's crypto holdings

---

### TC-TRADE-006: Get Transaction History
**Endpoint:** `GET /api/trading/transactions`
**Priority:** Medium
**Expected Result:** Returns array of transactions ordered by date

---

## Limit Orders

### TC-LIMIT-001: Create Limit Order - Buy
**Endpoint:** `POST /api/limit-orders`
**Priority:** High
**Expected Result:** Order created with status PENDING

---

### TC-LIMIT-002: Create Limit Order - Sell
**Endpoint:** `POST /api/limit-orders`
**Priority:** High
**Expected Result:** Sell order created, holdings reserved

---

### TC-LIMIT-003: Get User Limit Orders
**Endpoint:** `GET /api/limit-orders`
**Priority:** Medium
**Expected Result:** Returns all user limit orders

---

### TC-LIMIT-004: Cancel Limit Order
**Endpoint:** `DELETE /api/limit-orders/{id}`
**Priority:** High
**Expected Result:** Order cancelled, funds released

---

### TC-LIMIT-005: Limit Order Auto-Execution
**Priority:** High
**Expected Result:** Order executes when price reaches limit

---

## Payment & Deposits

### TC-PAY-001: Create Virtual Deposit
**Endpoint:** `POST /api/payments/deposit`
**Priority:** High
**Expected Result:** Deposit successful, balance increased

---

### TC-PAY-002: Get Deposit History
**Endpoint:** `GET /api/payments/deposits`
**Priority:** Medium
**Expected Result:** Returns deposit history

---

### TC-PAY-003: Reset Balance
**Endpoint:** `POST /api/payments/reset-balance`
**Priority:** Low
**Expected Result:** Balance set to 0

---

## Security & Authorization

### TC-SEC-001: JWT Token Validation
**Priority:** High
**Expected Result:** Valid tokens accepted, invalid rejected

---

### TC-SEC-002: Password Encryption
**Priority:** High
**Expected Result:** Passwords stored as BCrypt hash

---

### TC-SEC-003: User Data Isolation
**Priority:** High
**Expected Result:** Users can only access own data

---

### TC-SEC-004: CORS Configuration
**Priority:** Medium
**Expected Result:** Requests from localhost:3000 allowed

---

**Total Test Cases:** 35+  
**Document Version:** 1.0
