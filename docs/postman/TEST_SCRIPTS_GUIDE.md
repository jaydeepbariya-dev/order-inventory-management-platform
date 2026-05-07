# Postman Test Scripts & Environment Variables Guide

## Overview

This document explains the test scripts and pre-request scripts used in the collection for automatic variable management.

## Test Scripts Used

### 1. Auth Service Test Scripts

#### register-tenant Test Script
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('tenantId', response.tenantId);
    console.log('Tenant ID set:', response.tenantId);
}
```
**Purpose**: Extract tenant ID from registration response and store it

#### register-user Test Script
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('userId', response.userId);
    pm.environment.set('sellerId', response.userId);
    pm.environment.set('role', response.role);
    console.log('User ID set:', response.userId);
}
```
**Purpose**: Extract user ID, role, and sync with seller ID

#### login Test Script
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('accessToken', response.accessToken);
    pm.environment.set('refreshToken', response.refreshToken);
    pm.environment.set('userId', response.userId);
    pm.environment.set('tenantId', response.tenantId);
    pm.environment.set('role', response.role);
    console.log('Tokens set for user:', response.userId);
}
```
**Purpose**: Extract JWT tokens and user information after login

#### refresh-token Test Script
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('accessToken', response.accessToken);
    pm.environment.set('refreshToken', response.refreshToken);
    console.log('Tokens refreshed');
}
```
**Purpose**: Update tokens when they expire

### 2. Product Service Test Scripts

#### create-product Test Script
```javascript
if (pm.response.code === 200 || pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set('productId', response.id);
    console.log('Product ID set:', response.id);
}
```
**Purpose**: Extract product ID after successful creation

### 3. Order Service Test Scripts

#### place-order Test Script
```javascript
if (pm.response.code === 200 || pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set('orderId', response.id);
    console.log('Order ID set:', response.id);
    console.log('Order Status:', response.status);
    console.log('Order Total:', response.totalAmount);
}
```
**Purpose**: Extract order ID and log order details for verification

## Using Variables in Requests

### In Headers
```
Header Name: userId
Header Value: {{userId}}
```

### In URL Path
```
URL: {{ORDER_BASE_URL}}/{{orderId}}
```

### In Request Body
```json
{
  "items": [
    {
      "productId": "{{productId}}",
      "quantity": 5,
      "price": 99.99
    }
  ]
}
```

## Setting Variables Programmatically

### Using pm.environment.set()
```javascript
// Set a variable
pm.environment.set('variableName', 'value');

// Set multiple variables
pm.environment.set('userId', response.userId);
pm.environment.set('tenantId', response.tenantId);
```

### Using pm.variables.set() (Collection Level)
```javascript
// Collection-level variable
pm.variables.set('productId', response.id);
```

### Getting Variables in Tests
```javascript
// Get a variable value
const userId = pm.environment.get('userId');
const tenantId = pm.variables.get('tenantId');
```

## Common Test Script Patterns

### Pattern 1: Extract from Response
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('userId', response.id);
}
```

### Pattern 2: Validate Status Code
```javascript
if (pm.response.code === 200 || pm.response.code === 201) {
    // Handle successful responses
} else {
    console.error('Request failed with status:', pm.response.code);
}
```

### Pattern 3: Extract Nested Values
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('userId', response.data.user.id);
    pm.environment.set('email', response.data.user.email);
}
```

### Pattern 4: Array Response Handling
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    if (response.length > 0) {
        pm.environment.set('firstProductId', response[0].id);
    }
}
```

### Pattern 5: Error Handling
```javascript
const response = pm.response.json();
if (response.error) {
    console.error('Error:', response.error.message);
} else {
    pm.environment.set('resourceId', response.id);
    console.log('Resource created:', response.id);
}
```

## Pre-Request Scripts (Optional)

Pre-request scripts run before a request. Currently, the collection uses environment variables in headers, but you can add pre-request scripts for advanced scenarios:

### Example: Add timestamp to request
```javascript
// Pre-request Script
pm.environment.set('requestTime', new Date().toISOString());
```

### Example: Validate required variables
```javascript
// Pre-request Script
const userId = pm.environment.get('userId');
if (!userId) {
    throw new Error('userId is not set. Please run authentication flow first.');
}
```

## Debugging Tips

### 1. View Console Logs
- Click **Console** at bottom of Postman
- Run a request
- See all console.log() outputs

### 2. Check Variable Values
- Click **Environment** dropdown
- Select the collection
- View all variables and their current values

### 3. Add Debug Logs
```javascript
// Add to any test script
console.log('Response received:', JSON.stringify(pm.response.json()));
console.log('Current userId:', pm.environment.get('userId'));
```

### 4. Verify Response Structure
```javascript
const response = pm.response.json();
console.log('Response keys:', Object.keys(response));
console.log('Full response:', JSON.stringify(response, null, 2));
```

## Request Execution Order

To ensure variables are properly set, execute requests in this order:

```
1. register-tenant (sets tenantId)
2. register-user (sets userId, sellerId, role)
3. login (sets accessToken, refreshToken)
4. create-product (sets productId)
5. place-order (sets orderId, uses productId & userId)
6. get-order-by-id (uses orderId)
```

## Adding Custom Test Scripts

### Steps to add a test script to any request:

1. Open a request
2. Click **Tests** tab
3. Add your JavaScript code
4. Click **Send** to test

### Example: Custom test for list API
```javascript
if (pm.response.code === 200) {
    const items = pm.response.json();
    if (Array.isArray(items) && items.length > 0) {
        pm.environment.set('firstItemId', items[0].id);
        console.log('List retrieved with', items.length, 'items');
    }
}
```

## Environment Variable Lifecycle

```
INITIALIZATION
    ↓
Set default values in Collection Variables
    ↓
AUTHENTICATION
    ↓
run register-tenant → tenantId stored
    ↓
run register-user → userId, sellerId, role stored
    ↓
run login → accessToken, refreshToken stored
    ↓
OPERATIONS
    ↓
Use variables in all subsequent requests
    ↓
Update variables as needed (create-product → productId)
    ↓
CLEANUP (optional)
    ↓
Clear variables for next test run
```

## Practical Scenarios

### Scenario 1: Test Multiple Users
```javascript
// After login for User 1
pm.variables.set('user1Token', pm.environment.get('accessToken'));
pm.variables.set('user1Id', pm.environment.get('userId'));

// Then register and login User 2
// New tokens and ID will be in accessToken & userId
```

### Scenario 2: Store Request Timestamp
```javascript
// In pre-request script
pm.environment.set('requestStart', new Date().getTime());

// In test script
const startTime = pm.environment.get('requestStart');
const responseTime = new Date().getTime() - startTime;
console.log('Response time:', responseTime, 'ms');
```

### Scenario 3: Data Validation
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    
    // Validate required fields
    if (!response.id || !response.userId) {
        throw new Error('Response missing required fields');
    }
    
    pm.environment.set('userId', response.userId);
}
```

## Best Practices

✅ **Always check response status** before extracting data
✅ **Use console.log()** for debugging
✅ **Handle null/undefined** values gracefully
✅ **Extract IDs** for use in subsequent requests
✅ **Validate response structure** before parsing
✅ **Use meaningful variable names** for clarity
✅ **Document custom scripts** in comments

## Troubleshooting

### Issue: Variable is undefined
```javascript
// Check if variable exists
const userId = pm.environment.get('userId');
if (!userId) {
    console.error('userId is not set');
} else {
    console.log('userId:', userId);
}
```

### Issue: Can't extract nested value
```javascript
// Debug response structure
const response = pm.response.json();
console.log('Full response:', JSON.stringify(response, null, 2));
// Then extract with correct path
```

### Issue: Test script not running
- Verify tab is labeled **Tests** (not Test)
- Check syntax for JavaScript errors
- Click **Send** after making changes
- Check Console for errors
