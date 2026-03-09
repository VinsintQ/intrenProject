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
**Prerequisites:** None

**Test Data:**
```json
{
  "userName": "testuser",
  "emailAddress": "test@example.com",
  "password": "SecurePass123"
}
```

**Steps:**
1. Send POST request with valid user data
2. Verify response status is 200 OK
3. Verify user object is returned with id
4. Verify password is encrypted in database
5. Verify balance is initialized to 0.0
6. Verify isActive is true
7. Verify accountVerified is true

**Expected Result:** User created successfully with encrypted password

---

### TC-AUTH-002: User Registration - Duplicate Email
**Endpoint:** `POST /auth/users/register`
**Priority:** High
**Prerequisites:** User with email already exists

**Test Data:**
```json
{
  "userName": "testuser2",
  "emailAddress": "existing@example.com",
  "password": "SecurePass123"
}
```

**Steps:**
1. Create a user with email "existing@example.com"
2. Attempt to register another user with same email
3. Verify response status is 400 or 409
4. Verify error message indicates user already exists

**Expected Result:** Registration fails with appropriate error message

---

### TC-AUTH-003: User Login - Valid Credentials
**Endpoint:** `POST /auth/users/login`
**Priority:** High
**Prerequisites:** User exists in database

**Test Data:**
```json
{
  "email": "test@example.com",
  "password": "SecurePass123"
}
```

**Steps:**
1. Send POST request with valid credentials
2. Verify response status is 200 OK
3. Verify JWT token is returned in response
4. Verify token contains user email in claims
5. Verify token expiration is set correctly
6. Verify token can be decoded successfully

**Expected Result:** Login successful with valid JWT token

---

### TC-AUTH-004: User Login - Invalid Email
**Endpoint:** `POST /auth/users/login`
**Priority:** High
**Prerequisites:** None

**Test Data:**
```json
{
  "email": "nonexistent@example.com",
  "password": "SecurePass123"
}
```

**Steps:**
1. Send POST request with non-existent email
2. Verify response status is 400
3. Verify error message indicates invalid credentials

**Expected Result:** Login fails with error message

---

### TC-AUTH-005: User Login - Invalid Password
**Endpoint:** `POST /auth/users/login`
**Priority:** High
**Prerequisites:** User exists in database

**Test Data:**
```json
{
  "email": "test@example.com",
  "password": "WrongPassword"
}
```

**Steps:**
1. Send POST request with wrong password
2. Verify response status is 400
3. Verify error message indicates invalid credentials

**Expected Result:** Login fails with error message

---

### TC-AUTH-006: Change Password - Valid Request
**Endpoint:** `PUT /auth/users/change-password`
**Priority:** Medium
**Prerequisites:** User is authenticated

**Test Data:**
```json
{
  "oldPass": "SecurePass123",
  "newPass": "NewSecurePass456"
}
```

**Steps:**
1. Authenticate user and get JWT token
2. Send PUT request with valid old and new passwords
3. Verify response status is 200 OK
4. Verify password is updated in database
5. Attempt login with old password (should fail)
6. Attempt login with new password (should succeed)

**Expected Result:** Password changed successfully

---

### TC-AUTH-007: Change Password - Wrong Old Password
**Endpoint:** `PUT /auth/users/change-password`
**Priority:** Medium
**Prerequisites:** User is authenticated

**Test Data:**
```json
{
  "oldPass": "WrongOldPassword",
  "newPass": "NewSecurePass456"
}
```

**Steps:**
1. Authenticate user and get JWT token
2. Send PUT request with incorrect old password
3. Verify response status is 400
4. Verify error message indicates old password is incorrect
5. Verify password remains unchanged in database

**Expected Result:** Password change fails with error

---

### TC-AUTH-008: JWT Token Validation
**Priority:** High
**Prerequisites:** User has valid JWT token

**Steps:**
1. Generate valid JWT token for user
2. Make authenticated request to protected endpoint
3. Verify request is successful
4. Modify token slightly (tamper with it)
5. Make request with tampered token
6. Verify request is rejected with 401 status

**Expected Result:** Valid tokens accepted, invalid tokens rejected

---

### TC-AUTH-009: JWT Token Expiration
**Priority:** Medium
**Prerequisites:** User has expired JWT token

**Steps:**
1. Generate JWT token with short expiration (or use expired token)
2. Wait for token to expire
3. Make authenticated request with expired token
4. Verify response status is 401
5. Verify error indicates token is expired

**Expected Result:** Expired tokens are rejected

---

## Balance Management

### TC-BAL-001: Get User Balance - Authenticated
**Endpoint:** `GET /api/balance`
**Priority:** High
**Prerequisites:** User is authenticated

**Steps:**
1. Authenticate user and get JWT token
2. Send GET request with Authorization header
3. Verify response status is 200 OK
4. Verify response contains user balance
5. Verify balance is a valid number
6. Verify other user details are included

**Expected Result:** User balance retrieved successfully

---

### TC-BAL-002: Get User Balance - Unauthenticated
**Endpoint:** `GET /api/balance`
**Priority:** High
**Prerequisites:** None

**Steps:**
1. Send GET request without Authorization header
2. Verify response status is 401
3. Verify error message indicates authentication required

**Expected Result:** Request rejected due to missing authentication

---

## Cryptocurrency Data

### TC-CRYPTO-001: Get Top Cryptocurrencies - Default Limit
**Endpoint:** `GET /api/crypto/top`
**Priority:** High
**Prerequisites:** None

**Steps:**
1. Send GET request without limit parameter
2. Verify response status is 200 OK
3. Verify response is an array
4. Verify array contains 10 items (default limit)
5. Verify each item has required fields: id, symbol, name, currentPrice, image
6. Verify prices are positive numbers
7. Verify data is from Binance API

**Expected Result:** Returns 10 cryptocurrencies with valid data

---

### TC-CRYPTO-002: Get Top Cryptocurrencies - Custom Limit
**Endpoint:** `GET /api/crypto/top?limit=20`
**Priority:** Medium
**Prerequisites:** None

**Steps:**
1. Send GET request with limit=20
2. Verify response status is 200 OK
3. Verify response array contains 20 items
4. Verify all items have valid data structure

**Expected Result:** Returns 20 cryptocurrencies

---

### TC-CRYPTO-003: Get Cryptocurrency by ID - Valid ID
**Endpoint:** `GET /api/crypto/{id}`
**Priority:** High
**Prerequisites:** None

**Test Data:** id = "bitcoin" or "btc"

**Steps:**
1. Send GET request with valid crypto ID
2. Verify response status is 200 OK
3. Verify response contains crypto details
4. Verify fields: id, symbol, name, currentPrice, priceChangePercentage24h
5. Verify price data is current (from Binance)

**Expected Result:** Returns cryptocurrency details

---

### TC-CRYPTO-004: Get Cryptocurrency by ID - Invalid ID
**Endpoint:** `GET /api/crypto/{id}`
**Priority:** Medium
**Prerequisites:** None

**Test Data:** id = "invalidcrypto123"

**Steps:**
1. Send GET request with invalid crypto ID
2. Verify response status is 404
3. Verify appropriate error message

**Expected Result:** Returns 404 not found

---

### TC-CRYPTO-005: Get Historical Prices - Valid Request
**Endpoint:** `GET /api/history/{cryptoId}?days=7`
**Priority:** High
**Prerequisites:** None

**Test Data:** cryptoId = "btc", days = 7

**Steps:**
1. Send GET request with valid parameters
2. Verify response status is 200 OK
3. Verify response contains prices array
4. Verify each price has timestamp and price fields
5. Verify timestamps are in chronological order
6. Verify number of data points matches requested timeframe
7. Verify data is from Binance klines API

**Expected Result:** Returns historical price data

---

### TC-CRYPTO-006: Get Historical Prices - Different Timeframes
**Endpoint:** `GET /api/history/{cryptoId}?days={days}`
**Priority:** Medium
**Prerequisites:** None

**Test Data:** 
- days = 1 (24 hours)
- days = 30 (1 month)
- days = 365 (1 year)

**Steps:**
1. Test with days=1, verify 15-minute intervals
2. Test with days=7, verify 1-hour intervals
3. Test with days=30, verify 4-hour intervals
4. Test with days=365, verify 1-day intervals
5. Verify appropriate number of data points for each

**Expected Result:** Returns correct interval data for each timeframe

---

## Trading Operations

### TC-TRADE-001: Buy Cryptocurrency - Sufficient Balance
**Endpoint:** `POST /api/trading/buy`
**Priority:** High
**Prerequisites:** User authenticated with sufficient balance

**Test Data:**
```json
{
  "cryptoId": "btc",
  "quantity": 0.001
}
```

**Steps:**
1. Check user's initial balance
2. Get current BTC price
3. Send POST request to buy BTC
4. Verify response status is 200 OK
5. Verify user balance decreased by (quantity * price)
6. Verify crypto holding created/updated in database
7. Verify transaction record created
8. Verify transaction type is "BUY"

**Expected Result:** Purchase successful, balance and holdings updated

---

### TC-TRADE-002: Buy Cryptocurrency - Insufficient Balance
**Endpoint:** `POST /api/trading/buy`
**Priority:** High
**Prerequisites:** User authenticated with low balance

**Test Data:**
```json
{
  "cryptoId": "btc",
  "quantity": 100
}
```

**Steps:**
1. Ensure user balance is less than required amount
2. Send POST request to buy large quantity
3. Verify response status is 400
4. Verify error message indicates insufficient balance
5. Verify no changes to balance or holdings

**Expected Result:** Purchase fails with insufficient balance error

---

### TC-TRADE-003: Buy Cryptocurrency - Invalid Quantity
**Endpoint:** `POST /api/trading/buy`
**Priority:** Medium
**Prerequisites:** User authenticated

**Test Data:**
```json
{
  "cryptoId": "btc",
  "quantity": -0.001
}
```

**Steps:**
1. Send POST request with negative quantity
2. Verify response status is 400
3. Verify error message indicates invalid quantity
4. Test with quantity = 0
5. Verify same error response

**Expected Result:** Purchase fails with validation error

---

### TC-TRADE-004: Sell Cryptocurrency - Sufficient Holdings
**Endpoint:** `POST /api/trading/sell`
**Priority:** High
**Prerequisites:** User has crypto holdings

**Test Data:**
```json
{
  "cryptoId": "btc",
  "quantity": 0.0005
}
```

**Steps:**
1. Ensure user has at least 0.0005 BTC
2. Check initial balance and holdings
3. Get current BTC price
4. Send POST request to sell BTC
5. Verify response status is 200 OK
6. Verify user balance increased by (quantity * price)
7. Verify crypto holding decreased
8. Verify transaction record created with type "SELL"

**Expected Result:** Sale successful, balance and holdings updated

---

### TC-TRADE-005: Sell Cryptocurrency - Insufficient Holdings
**Endpoint:** `POST /api/trading/sell`
**Priority:** High
**Prerequisites:** User authenticated

**Test Data:**
```json
{
  "cryptoId": "btc",
  "quantity": 10
}
```

**Steps:**
1. Ensure user has less than 10 BTC
2. Send POST request to sell large quantity
3. Verify response status is 400
4. Verify error message indicates insufficient holdings
5. Verify no changes to balance or holdings

**Expected Result:** Sale fails with insufficient holdings error

---

### TC-TRADE-006: Get User Holdings
**Endpoint:** `GET /api/trading/holdings`
**Priority:** High
**Prerequisites:** User authenticated with holdings

**Steps:**
1. User has purchased some cryptocurrencies
2. Send GET request with authentication
3. Verify response status is 200 OK
4. Verify response is array of holdings
5. Verify each holding has: cryptoId, cryptoSymbol, quantity, averageBuyPrice
6. Verify current prices are included
7. Verify profit/loss calculations are correct

**Expected Result:** Returns all user holdings with current values

---

### TC-TRADE-007: Get Transaction History
**Endpoint:** `GET /api/trading/transactions`
**Priority:** Medium
**Prerequisites:** User authenticated with transaction history

**Steps:**
1. User has made some trades
2. Send GET request with authentication
3. Verify response status is 200 OK
4. Verify response is array of transactions
5. Verify transactions are ordered by date (newest first)
6. Verify each transaction has: type, cryptoId, quantity, pricePerUnit, totalAmount, createdAt
7. Verify both BUY and SELL transactions are included

**Expected Result:** Returns complete transaction history

---

## Limit Orders

### TC-LIMIT-001: Create Limit Order - Buy Order
**Endpoint:** `POST /api/limit-orders`
**Priority:** High
**Prerequisites:** User authenticated with sufficient balance

**Test Data:**
```json
{
  "cryptoId": "btc",
  "cryptoSymbol": "BTC",
  "cryptoName": "Bitcoin",
  "orderType": "BUY",
  "quantity": 0.001,
  "limitPrice": 50000.00
}
```

**Steps:**
1. Check user balance is sufficient for order
2. Send POST request to create limit order
3. Verify response status is 200 OK
4. Verify order created with status "PENDING"
5. Verify order details match request
6. Verify expiry date is set (if applicable)
7. Verify user balance is reserved/locked

**Expected Result:** Limit order created successfully

---

### TC-LIMIT-002: Create Limit Order - Sell Order
**Endpoint:** `POST /api/limit-orders`
**Priority:** High
**Prerequisites:** User authenticated with crypto holdings

**Test Data:**
```json
{
  "cryptoId": "btc",
  "cryptoSymbol": "BTC",
  "cryptoName": "Bitcoin",
  "orderType": "SELL",
  "quantity": 0.0005,
  "limitPrice": 60000.00
}
```

**Steps:**
1. Ensure user has sufficient BTC holdings
2. Send POST request to create sell limit order
3. Verify response status is 200 OK
4. Verify order created with status "PENDING"
5. Verify crypto quantity is reserved

**Expected Result:** Sell limit order created successfully

---

### TC-LIMIT-003: Get User Limit Orders
**Endpoint:** `GET /api/limit-orders`
**Priority:** Medium
**Prerequisites:** User authenticated with limit orders

**Steps:**
1. User has created some limit orders
2. Send GET request with authentication
3. Verify response status is 200 OK
4. Verify response is array of orders
5. Verify orders include all statuses: PENDING, FILLED, CANCELLED, EXPIRED
6. Verify order details are complete

**Expected Result:** Returns all user limit orders

---

### TC-LIMIT-004: Cancel Limit Order
**Endpoint:** `DELETE /api/limit-orders/{id}`
**Priority:** High
**Prerequisites:** User has pending limit order

**Steps:**
1. Create a pending limit order
2. Send DELETE request with order ID
3. Verify response status is 200 OK
4. Verify order status changed to "CANCELLED"
5. Verify reserved balance/holdings are released
6. Verify order cannot be executed after cancellation

**Expected Result:** Order cancelled and funds released

---

### TC-LIMIT-005: Limit Order Execution - Price Reached
**Priority:** High
**Prerequisites:** Pending limit order exists

**Steps:**
1. Create buy limit order at specific price
2. Simulate price reaching limit price
3. Trigger order execution check
4. Verify order status changed to "FILLED"
5. Verify trade executed at limit price
6. Verify holdings updated
7. Verify balance updated
8. Verify filled date is set

**Expected Result:** Order executes automatically when price reached

---

### TC-LIMIT-006: Limit Order Expiration
**Priority:** Medium
**Prerequisites:** Limit order with expiry date

**Steps:**
1. Create limit order with expiry date
2. Simulate time passing beyond expiry
3. Trigger expiration check
4. Verify order status changed to "EXPIRED"
5. Verify reserved funds released
6. Verify order cannot be executed

**Expected Result:** Order expires and funds released

---

## Payment & Deposits

### TC-PAY-001: Create Virtual Deposit
**Endpoint:** `POST /api/payments/deposit`
**Priority:** High
**Prerequisites:** User authenticated

**Test Data:**
```json
{
  "amount": 1000.00,
  "currency": "USD"
}
```

**Steps:**
1. Check user's initial balance
2. Send POST request to create deposit
3. Verify response status is 200 OK
4. Verify deposit record created with status "COMPLETED"
5. Verify user balance increased by deposit amount
6. Verify deposit has createdAt timestamp
7. Verify deposit appears in history

**Expected Result:** Deposit successful, balance updated

---

### TC-PAY-002: Create Deposit - Invalid Amount
**Endpoint:** `POST /api/payments/deposit`
**Priority:** Medium
**Prerequisites:** User authentisers with various balance levels
- Sample cryptocurrency data
- Historical price data
- Various order scenarios

### Environment Setup
- Test database (H2 or separate PostgreSQL)
- Mock Binance API responses
- Test JWT secret key
- Isolated test environment

---

**Document Version:** 1.0  
**Last Updated:** 2026-03-09  
**Author:** QA Team
s: 60+
- Critical: 30
- High Priority: 20
- Medium Priority: 15
- Low Priority: 5

### Testing Tools Recommended
- **Unit Testing:** JUnit 5, Mockito
- **Integration Testing:** Spring Boot Test, TestRestTemplate
- **API Testing:** Postman, REST Assured
- **Load Testing:** JMeter, Gatling
- **Security Testing:** OWASP ZAP

### Test Data Requirements
- Test uDatabase failures handled gracefully

---

## Test Execution Summary

### Test Coverage
- Total Test Case*Prerequisites:** None

**Steps:**
1. Simulate Binance API timeout
2. Verify graceful error handling
3. Verify user receives appropriate error message
4. Verify system remains stable

**Expected Result:** External API failures handled gracefully

---

### TC-EDGE-005: Database Connection Loss
**Priority:** High
**Prerequisites:** None

**Steps:**
1. Simulate database connection loss
2. Verify appropriate error responses
3. Verify system attempts reconnection
4. Verify no data corruption

**Expected Result:** 2. Attempt SQL/script injection characters
3. Verify system sanitizes or rejects input
4. Verify no errors or security issues

**Expected Result:** Special characters handled safely

---

### TC-EDGE-004: Network Timeout Handling
**Priority:** Medium
*d

**Steps:**
1. Attempt to buy quantity = 0.00000001
2. Verify system handles precision correctly
3. Verify calculations remain accurate

**Expected Result:** Small numbers handled with proper precision

---

### TC-EDGE-003: Special Characters in Input
**Priority:** Medium
**Prerequisites:** None

**Steps:**
1. Register with username containing special chars

**Priority:** Low
**Prerequisites:** User authenticatel responses under acceptable thresholds

**Expected Result:** All APIs respond within SLA

---

## Edge Cases & Error Handling

### TC-EDGE-001: Extremely Large Numbers
**Priority:** Low
**Prerequisites:** User authenticated

**Steps:**
1. Attempt to buy with quantity = 999999999
2. Attempt to deposit amount = 999999999999
3. Verify system handles large numbers gracefully
4. Verify no overflow errors

**Expected Result:** Large numbers handled or rejected gracefully

---

### TC-EDGE-002: Extremely Small Numbers* Low
**Prerequisites:** None

**Steps:**
1. Measure response time for crypto list endpoint
2. Measure response time for historical data
3. Measure response time for trading operations
4. Verify alted Result:** System handles concurrent logins

---

### TC-PERF-002: High Volume Trading
**Priority:** Medium
**Prerequisites:** Users with balance

**Steps:**
1. Simulate 50 concurrent buy/sell requests
2. Verify all transactions process correctly
3. Verify no race conditions in balance updates
4. Verify database consistency

**Expected Result:** System handles high trading volume

---

### TC-PERF-003: API Response Times
**Priority:*times are acceptable (<2s)
4. Verify no database deadlocks

**Expeceps:**
1. Create new user with password
2. Query database directly
3. Verify password is hashed (BCrypt)
4. Verify password is not stored in plain text
5. Verify hash is different for same password (salt)

**Expected Result:** Passwords properly encrypted

---

## Performance & Load Testing

### TC-PERF-001: Concurrent User Logins
**Priority:** Medium
**Prerequisites:** Multiple user accounts

**Steps:**
1. Simulate 100 concurrent login requests
2. Verify all requests complete successfully
3. Verify response ers are present in responses

**Expected Result:** CORS properly configured

---

### TC-SEC-007: Password Encryption
**Priority:** High
**Prerequisites:** None

**Stempt to inject HTML in crypto names
3. Verify all inputs are sanitized
4. Verify no script execution occurs

**Expected Result:** All XSS attempts prevented

---

### TC-SEC-006: CORS Configuration
**Priority:** Medium
**Prerequisites:** None

**Steps:**
1. Send request from allowed origin (localhost:3000)
2. Verify request is accepted
3. Send request from disallowed origin
4. Verify request is blocked by CORS
5. Verify CORS head

**Steps:**
1. Attempt to inject script tags in username
2. Attsult:** Users can only access their own data

---

### TC-SEC-004: SQL Injection Prevention
**Priority:** High
**Prerequisites:** None

**Steps:**
1. Attempt SQL injection in email field during login
2. Attempt SQL injection in crypto ID parameter
3. Attempt SQL injection in search parameters
4. Verify all attempts are safely handled
5. Verify no database errors or data leakage

**Expected Result:** All SQL injection attempts blocked

---

### TC-SEC-005: XSS Prevention
**Priority:** High
**Prerequisites:** Noney User B cannot see User A's orders
6. Verify User B cannot modify User A's data

**Expected Retatus is 401
3. Send request with expired token
4. Verify response status is 401
5. Send request with token for non-existent user
6. Verify response status is 401

**Expected Result:** All invalid tokens rejected

---

### TC-SEC-003: User Can Only Access Own Data
**Priority:** High
**Prerequisites:** Two users exist

**Steps:**
1. User A creates holdings/orders
2. User B attempts to access User A's data
3. Verify User B cannot see User A's holdings
4. Verify User B cannot see User A's transactions
5. Verif** High
**Prerequisites:** None

**Steps:**
1. Send request with malformed JWT token
2. Verify response se confirms new balance

**Expected Result:** Balance reset to zero successfully

---

## Security & Authorization

### TC-SEC-001: Access Protected Endpoint Without Token
**Priority:** High
**Prerequisites:** None

**Steps:**
1. Send request to protected endpoint without Authorization header
2. Verify response status is 401
3. Verify error message indicates authentication required

**Expected Result:** Request rejected with 401 Unauthorized

---

### TC-SEC-002: Access Protected Endpoint With Invalid Token
**Priority:us is 200 OK
4. Verify user balance is set to 0.00
5. Verify responsponse status is 200 OK
4. Verify response is array of deposits
5. Verify deposits ordered by date (newest first)
6. Verify each deposit has: amount, currency, status, createdAt
7. Verify all statuses are included

**Expected Result:** Returns complete deposit history

---

### TC-PAY-004: Reset Balance to Zero
**Endpoint:** `POST /api/payments/reset-balance`
**Priority:** Low
**Prerequisites:** User authenticated

**Steps:**
1. User has non-zero balance
2. Send POST request to reset balance
3. Verify response statosits
2. Send GET request with authentication
3. Verify rescated

**Test Data:**
```json
{
  "amount": -100.00,
  "currency": "USD"
}
```

**Steps:**
1. Send POST request with negative amount
2. Verify response status is 400
3. Verify error message indicates invalid amount
4. Test with amount = 0
5. Verify same error response

**Expected Result:** Deposit fails with validation error

---

### TC-PAY-003: Get Deposit History
**Endpoint:** `GET /api/payments/deposits`
**Priority:** Medium
**Prerequisites:** User authenticated with deposits

**Steps:**
1. User has made some dep