# Order Inventory Management System

## 1. Functional Requirements
- User should be able to place an order with product and quantity details
- System should validate inventory, reserve/deduct/release stock and create order accordingly
- User should be able to track their order history and order status (CREATED, FAILED, etc.)
- Seller should be able to track orders, sales and manage inventory

## 2. Non-Functional Requirements
- Scale: 20M DAU (Assumed). 20% Places Order. 80% Reads.
- Eventual Consistency in orders
- Strong consistency in Inventory data
- High Availability in products data.
- Latency: every POST/PUT request must be complete under 200ms ideally. GET request under 100ms.
- Reliability: System should ensure no data loss in order processing and handle failures with retries and recovery mechanisms

## 3. High Level Design - HLD

```
Client -> API Gateway -> Services -> DB
```

### Services
- Auth Service
- Order Service
- Inventory Service
- Product Service
- Notification Service

### Infrastructure
- Kafka (event-driven communication)
- Redis (cache + locking)

### Flow
```
Client -> API Gateway -> Order Service -> Inventory Service (sync or async) -> Order Service -> Kafka -> Notification Service
```

### Data Layer
- PostgreSQL (primary DB)
- Read replicas for product/catalog
- Sharding for orders/inventory when needed

### Caching
- Redis (cache-aside for product + inventory reads)

### Deployment
- Services behind load balancer
- All components inside VPC

## 4. Low Level Design (LLD)

### 4.1 Data Model

**User**
- id (PK)
- name
- email
- password
- role (BUYER / SELLER)
- createdAt

**Product**
- id (PK)
- name
- description
- price
- sellerId (FK -> User)
- createdAt
- updatedAt

**Inventory**
- id (PK)
- productId (FK -> Product)
- availableQty
- reservedQty
- version (for optimistic locking)
- updatedAt

**Order**
- id (PK)
- userId (FK -> User)
- totalAmount
- status (CREATED / FAILED / CONFIRMED)
- paymentStatus (SUCCESS / FAILED / PENDING)
- createdAt

**OrderItem**
- id (PK)
- orderId (FK -> Order)
- productId (FK -> Product)
- quantity
- price

### 4.2 Order Processing Flow

1. User -> API Gateway -> Order Service

2. Order Service:
   - Creates Order (status = CREATED)
   - Calls Inventory Service -> reserve(productId, qty)

3. Inventory Service:
   - Checks availableQty
   - Uses optimistic locking (version)
   - Deducts from availableQty, adds to reservedQty
   - Returns success/failure

4. If reservation fails:
   - Order -> FAILED
   - Return response

5. If success:
   - Order Service -> triggers Payment (async or sync)

6. Payment Success:
   - Order -> CONFIRMED
   - Inventory: reservedQty -> deducted permanently

7. Payment Failure:
   - Order -> FAILED
   - Inventory: release reservedQty back to availableQty

### 4.3 Idempotency for Order Requests

1. Client sends request with Idempotency-Key

2. API Gateway:
   - Checks Redis for key (optional optimization)
   - Forwards request

3. Order Service (source of truth):
   - Checks Redis:
     - IF key exists -> return stored response
     - ELSE proceed

4. Store key in Redis:
   - key -> response OR orderId
   - TTL: 10�30 minutes (or business dependent)

5. Process order normally

6. Save final response in Redis for reuse

### 4.4 Handle Concurrency at Inventory Update

1. **Read**  
   Service reads inventory + version

2. **Update attempt**  
   Update happens only if version matches  
   `WHERE productId = -> AND version = ->`

3. **Success case**  
   Quantity updated  
   Version incremented

4. **Failure case**  
   No rows updated -> conflict detected  
   Means someone else updated first  
   What happens on failure->  
   Retry request OR  
   Return "Out of stock" OR  
   Trigger re-fetch + retry logic

### 4.5 Inventory Reservation Flow

Order Service -> Inventory Service (reserve)

**Inventory Service:**
```
IF availableQty >= request:
    availableQty -= qty
    reservedQty += qty
    return SUCCESS
ELSE:
    return FAILED
```

### 4.6 Full Flow Demo

**Full Order Management System Flow**

1. **Client Request**  
   User sends Place Order request  
   Includes:  
   - productId(s), quantity  
   - Idempotency-Key

2. **API Gateway**  
   - Auth (JWT validation)  
   - Rate limiting (Redis)  
   - Optional idempotency pre-check (Redis)  
   - Routes request -> Order Service

3. **Order Service (Core Orchestrator)**  

   **Step 1:** Idempotency check (Redis)  
   If key exists -> return stored response  
   Else continue  

   **Step 2:** Create Order  
   Save in DB:  
   status = CREATED  

   **Step 3:** Call Inventory Service  
   reserve(orderId, productId, qty)

4. **Inventory Service**  

   **Step 1:** Check availability  
   Uses PostgreSQL + optimistic locking (version column)  
   ```
   IF availableQty >= qty:
       availableQty -= qty
       reservedQty += qty
       version++
       SUCCESS
   ELSE:
       FAIL
   ```
   **Step 2:**  
   Cache inventory in Redis (read optimization)

5. **Inventory Response**  
   If SUCCESS:  
   Order Service updates:  
   status = RESERVED  

   If FAIL:  
   Order Service updates:  
   status = FAILED  
   Response returned to user

6. **Event Publication (Kafka)**  

   Order Service publishes:  
   - OrderCreatedEvent  
   - OrderReservedEvent

7. **Payment Service (Async Consumer)**  

   Consumes from Kafka:  
   - processes payment

8. **Payment Outcome Handling**  

   **SUCCESS:**  
   - Order -> CONFIRMED  
   - Inventory -> finalize deduction (reserved -> committed)  

   **FAILURE:**  
   - Order -> FAILED  
   - Inventory -> release stock:  
     reservedQty -> availableQty

9. **Inventory Release Flow**  
   ```
   reservedQty -= qty
   availableQty += qty
   ```

10. **Notification Service (Async via Kafka)**  

    Consumes events:  
    - OrderCreated -> "Order placed"  
    - OrderReserved -> "Stock reserved"  
    - PaymentSuccess -> "Order confirmed"  
    - PaymentFailed -> "Order failed"

11. **Redis Responsibilities**  

    Used across system:  
    - Idempotency keys (Order Service / Gateway)  
    - Rate limiting (API Gateway)  
    - Product cache (read optimization)  
    - Inventory cache (hot reads)

12. **Database Layer**  
    PostgreSQL (Source of truth)  
    - Users  
    - Products  
    - Orders  
    - OrderItems  
    - Inventory  
    - Read replicas  
    - Product catalog queries

### FINAL ARCHITECTURE FLOW
```
Client
 -> API Gateway (Auth + Rate limit + Redis)
 -> Order Service
 -> Inventory Service (Optimistic Locking + DB + Redis cache)
 -> Kafka
      -> Payment Service
      -> Notification Service
      -> Order state updates
      -> Inventory finalization/release
```

## Key Design Principles Demonstrated
- Idempotency handling
- Inventory reservation pattern
- Event-driven architecture (Kafka)
- Strong vs eventual consistency separation
- Optimistic locking for concurrency
- Async notification system
- Cache-aside strategy (Redis)
- Microservices decomposition
