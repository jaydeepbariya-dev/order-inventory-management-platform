# Postman Collection Setup Guide

## Overview

The updated `order-inventory-management-system.postman_collection.json` includes dynamic environment variable management and automatic header handling. This allows for seamless testing across all microservices without manual header updates.

## Features

✅ **Dynamic Environment Variables** - Automatically extracted from responses
✅ **Automatic Header Management** - Headers populated from environment variables
✅ **Token Management** - Access and refresh tokens automatically managed
✅ **Variable Flow** - Data flows automatically between requests
✅ **Pre-configured Base URLs** - All service endpoints configured

## Environment Variables

The collection includes the following variables that are automatically managed:

| Variable | Purpose | Auto-populated |
|----------|---------|-----------------|
| `AUTH_BASE_URL` | Auth service endpoint | ❌ (Pre-set) |
| `PRODUCT_BASE_URL` | Product service endpoint | ❌ (Pre-set) |
| `ORDER_BASE_URL` | Order service endpoint | ❌ (Pre-set) |
| `INVENTORY_BASE_URL` | Inventory service endpoint | ❌ (Pre-set) |
| `PAYMENT_BASE_URL` | Payment service endpoint | ❌ (Pre-set) |
| `NOTIFICATION_BASE_URL` | Notification service endpoint | ❌ (Pre-set) |
| `accessToken` | JWT access token | ✅ (From login/register) |
| `refreshToken` | JWT refresh token | ✅ (From login/register) |
| `userId` | Current user ID | ✅ (From auth/product requests) |
| `tenantId` | Current tenant ID | ✅ (From tenant registration) |
| `sellerId` | Current seller ID | ✅ (Synced with userId) |
| `role` | User role (SELLER/BUYER) | ✅ (From auth) |
| `productId` | Last created product ID | ✅ (From product creation) |
| `orderId` | Last created order ID | ✅ (From order placement) |

## Quick Start

### Step 1: Import Collection

1. Open Postman
2. Click **Import** → **Upload Files**
3. Select `order-inventory-management-system.postman_collection.json`
4. Click **Import**

### Step 2: Run Authentication Flow

Execute requests in this order:

1. **register-tenant** - Creates a new tenant
   - Automatically sets: `tenantId`

2. **register-user** - Creates a new user (SELLER role)
   - Uses: `tenantId`
   - Automatically sets: `userId`, `sellerId`, `role`

3. **login** - Authenticates user
   - Uses: email/password from register-user
   - Automatically sets: `accessToken`, `refreshToken`, `userId`, `tenantId`

### Step 3: Test Product Service

Once authenticated:

1. **create-product** - Creates a new product
   - Automatically sets: `productId`

2. **get-products** - Lists all products

3. **get-products-by-id** - Gets specific product (uses `productId`)

4. **update-products-by-id** - Updates product (uses `productId`)

5. **search-product** - Searches products

6. **filter-product** - Filters products by criteria

### Step 4: Test Order Service

1. **place-order** - Creates a new order
   - Uses: `userId`, `productId`
   - Automatically sets: `orderId`

2. **get-order-by-id** - Retrieves order details (uses `orderId`)

3. **get-order-by-userId** - Lists all user's orders

## Request Flow Diagram

```
AUTHENTICATION FLOW:
  register-tenant
        ↓
  register-user
        ↓
     login
        ↓
    [Tokens & User Info Set]

PRODUCT FLOW:
  create-product → get-products → get-products-by-id
        ↓
     [productId stored]
        ↓
  update-products-by-id / search / filter

ORDER FLOW:
  place-order ← (requires productId & userId)
        ↓
  get-order-by-id ← (uses orderId)
        ↓
  get-order-by-userId
```

## Automatic Variable Population

### After `register-tenant`:
```json
{
  "tenantId": "auto-set"
}
```

### After `register-user`:
```json
{
  "userId": "auto-set",
  "sellerId": "auto-set (same as userId)",
  "role": "SELLER"
}
```

### After `login`:
```json
{
  "accessToken": "auto-set",
  "refreshToken": "auto-set",
  "userId": "auto-set",
  "tenantId": "auto-set"
}
```

### After `create-product`:
```json
{
  "productId": "auto-set"
}
```

### After `place-order`:
```json
{
  "orderId": "auto-set"
}
```

## Testing Headers

All requests automatically include necessary headers:

### Auth Requests
```
Content-Type: application/json
```

### Product Requests
```
sellerId: {{sellerId}}
tenantId: {{tenantId}}
role: {{role}}
Content-Type: application/json
```

### Order Requests
```
userId: {{userId}}
Content-Type: application/json
```

## Common Issues & Solutions

### Issue: "Variable not set" error
**Solution**: Make sure you've run the authentication flow first:
1. register-tenant
2. register-user
3. login

### Issue: "401 Unauthorized"
**Solution**: Token might have expired. Run `refresh-token` or login again.

### Issue: "Product ID not found"
**Solution**: Create a product first using `create-product` before placing orders.

### Issue: Headers not appearing in request
**Solution**: Make sure collection variables are properly imported. Check the Collection → Variables tab in Postman.

## Advanced Usage

### Refresh Tokens
When access token expires, use the `refresh-token` endpoint to get new tokens:
- Automatically updates: `accessToken`, `refreshToken`

### Multiple Test Runs
The collection supports sequential test runs. Each iteration will:
1. Create new tenant
2. Create new user
3. Authenticate
4. Create products
5. Place orders

### Custom Values
You can override any environment variable:
1. Click **Environment** dropdown
2. Select the collection variables
3. Edit any value
4. Click **Save**

## Collection Variables vs Environment

- **Collection Variables**: Built-in to this collection (recommended)
- **Environment Variables**: Postman workspace-level (optional)

The collection uses built-in variables, so it works standalone without additional environment setup.

## Testing Best Practices

1. ✅ **Run sequentially** - Don't skip authentication steps
2. ✅ **Monitor response status** - Check for 200/201 responses
3. ✅ **View console logs** - Click **Console** in Postman to see debug info
4. ✅ **Check variable values** - Click **Eye icon** → **Globals** to verify
5. ✅ **Test error cases** - Use invalid data to test error handling

## Service Ports Reference

| Service | Port | Base URL |
|---------|------|----------|
| Auth Service | 8081 | http://localhost:8081/api/v1/auth |
| Product Service | 8082 | http://localhost:8082/api/v1/products |
| Order Service | 8083 | http://localhost:8083/api/v1/orders |
| Inventory Service | 8084 | http://localhost:8084/api/v1/inventory |
| Payment Service | 8085 | http://localhost:8085/api/v1/payments |
| Notification Service | 8086 | http://localhost:8086/api/v1/notifications |

## Need Help?

Check the test scripts in each request (Tests tab) to see what variables are being set and when. The console logs will show exactly what values are being extracted from responses.
