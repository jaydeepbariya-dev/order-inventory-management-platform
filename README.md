# Order & Inventory Management SaaS Platform

A multi-tenant, microservices-based SaaS platform for managing orders and inventory at scale. Built with Spring Boot, Spring Cloud, PostgreSQL, Kafka, and Redis.

## Overview

This project implements a distributed order and inventory management system designed to handle millions of daily active users (DAU). The platform uses event-driven architecture with microservices to ensure scalability, fault tolerance, and eventual consistency across services.

### Key Statistics(Assumed)
- **Scale**: Designed for 20M+ DAU
- **Traffic Pattern**: 20% writes (orders), 80% reads (queries)
- **Performance Targets**: 
  - POST/PUT requests: < 200ms
  - GET requests: < 100ms

## Problem

Modern e-commerce platforms face challenges with:
- **Order Processing at Scale**: Handling millions of concurrent orders while maintaining data consistency
- **Inventory Management**: Preventing overselling while ensuring fast stock availability updates
- **Payment Integration**: Processing payments reliably with retry mechanisms and failure handling
- **User Notifications**: Keeping users informed across multiple microservices
- **System Reliability**: Ensuring no data loss and graceful failure handling

This system solves these problems through a distributed architecture with eventual consistency, optimistic locking, event-driven communication, and intelligent caching.

## Key Features

✅ **Multi-Tenant Architecture** - Complete tenant isolation with role-based access control (BUYER/SELLER)

✅ **Distributed Order Processing** - Asynchronous order orchestration with state management

✅ **Intelligent Inventory Management** - Optimistic locking to prevent race conditions and overselling

✅ **Idempotent APIs** - Guaranteed exactly-once semantics for order creation using idempotency keys

✅ **Event-Driven Communication** - Kafka-based async messaging between services

✅ **Caching Strategy** - Redis for session management, rate limiting, and hot-data caching

✅ **API Gateway** - Centralized entry point with routing, authentication, and rate limiting

✅ **JWT Authentication** - Secure token-based authentication with refresh token support

✅ **Product Catalog** - Product search, filtering, and multi-seller support

✅ **Payment Processing** - Integration-ready payment service with status tracking

✅ **Real-Time Notifications** - Event-driven notification system for order updates

✅ **Docker & Container Orchestration** - Production-ready Docker setup with docker-compose

## Architecture

### High-Level System Design

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌──────────────────────────────────────────┐
│   API Gateway (Port 8000)                │
│  ┌────────────────────────────────────┐  │
│  │ - JWT Validation                   │  │
│  │ - Rate Limiting (Redis)            │  │
│  │ - Request Routing                  │  │
│  │ - Idempotency Check                │  │
│  └────────────────────────────────────┘  │
└──────────────────────────────────────────┘
       │
   ┌───┴──────────────────────────────────┬───────────┬──────────────┐
   │                                      │           │              │
   ▼                    ▼                 ▼           ▼              ▼
┌─────────────┐  ┌─────────────┐  ┌────────────┐ ┌─────────────┐ ┌────────────┐
│Auth Service │  │Order Service│  │Product Srv │ │Inventory Srv│ │Payment Srv │
│ (Port 8081) │  │ (Port 8083) │  │(Port 8082) │ │ (Port 8084) │ │(Port 8085) │
└─────────────┘  └─────────────┘  └────────────┘ └─────────────┘ └────────────┘
       │               │                │              │              │
       ▼               ▼                ▼              ▼              ▼
    ┌────────┐     ┌────────┐     ┌────────┐      ┌────────┐    ┌────────┐
    │Auth DB │     │Order DB│     │Product │      │Inventory   │Payment │
    │(PG)    │     │(PG)    │     │ DB(PG) │      │ DB(PG)  │  │ DB(PG)  │
    └────────┘     └────────┘     └────────┘      └────────┘    └────────┘
                        │
                        ▼
                ┌──────────────────┐
                │  Kafka Broker    │ (Event Bus)
                │  (Port 9092)     │
                └────────┬─────────┘
                         │
          ┌──────────────┘
          │
          ▼
    ┌─────────────────┐
    │Notification Srv │
    │(Port 8086)      │
    └─────────────────┘

    Shared Infrastructure:
    ┌─────────────────┐    ┌─────────────────┐
    │   Redis Cache   │    │ API Gateway     │
    │   (Port 6379)   │    │ Routing & Auth  │
    └─────────────────┘    └─────────────────┘
```

### Microservices Breakdown

| Service | Port | Responsibility |
|---------|------|-----------------|
| **API Gateway** | 8000 | Request routing, authentication, rate limiting |
| **Auth Service** | 8081 | User/tenant registration, JWT token management |
| **Product Service** | 8082 | Product catalog, search, filtering |
| **Order Service** | 8083 | Order orchestration, state management |
| **Inventory Service** | 8084 | Stock management with optimistic locking |
| **Payment Service** | 8085 | Payment processing and status tracking |
| **Notification Service** | 8086 | Event-driven user notifications |

### Data Flow: Order Processing

```
1. User submits order with idempotency key
   ↓
2. API Gateway validates JWT token
   ↓
3. Order Service receives request
   ├─ Checks Redis for idempotency key
   ├─ If exists → return cached response
   └─ If not → proceed
   ↓
4. Order Service creates Order (status: CREATED)
   ↓
5. Order Service calls Inventory Service to reserve stock
   ├─ Inventory checks availableQty >= requested qty
   ├─ Uses optimistic locking (version column)
   ├─ Updates: availableQty ↓, reservedQty ↑
   └─ Returns SUCCESS/FAILURE
   ↓
6a. If reservation FAILS
    ├─ Order status → FAILED
    └─ Return error response
   ↓
6b. If reservation SUCCEEDS
    ├─ Order status → RESERVED
    ├─ Publish OrderReservedEvent to Kafka
    └─ Continue
   ↓
7. Payment Service (async via Kafka)
   ├─ Consumes OrderReservedEvent
   ├─ Processes payment
   └─ Publishes PaymentSuccessEvent or PaymentFailedEvent
   ↓
8a. If payment SUCCESS
    ├─ Order status → CONFIRMED
    ├─ Inventory: reservedQty → deducted permanently
    ├─ Publish OrderConfirmedEvent
    └─ Notification Service sends "Order Confirmed" email
   ↓
8b. If payment FAILED
    ├─ Order status → FAILED
    ├─ Release inventory: reservedQty → availableQty
    ├─ Publish OrderFailedEvent
    └─ Notification Service sends "Order Failed" email
```

## Key Design Decisions

### 1. **Optimistic Locking for Inventory**
- **Why**: Prevents race conditions when multiple orders try to reserve the same stock simultaneously
- **Implementation**: 
  ```sql
  UPDATE inventory SET availableQty = ? WHERE productId = ? AND version = ?
  ```
- **Retry Logic**: If update fails (version mismatch), retry or return "Out of Stock"

### 2. **Eventual Consistency**
- **Approach**: Order status may not immediately reflect all changes across services
- **Benefits**: Improves availability and performance
- **Guarantee**: All services converge to consistent state within bounded time

### 3. **Idempotency for Order Creation**
- **Implementation**: Store idempotency key → response mapping in Redis (TTL: 10-30 min)
- **Benefit**: Safe to retry failed requests without creating duplicate orders
- **Scope**: Primary responsibility of Order Service

### 4. **Event-Driven Architecture**
- **Medium**: Apache Kafka for asynchronous communication
- **Events Published**: OrderCreated, OrderReserved, PaymentSuccess, PaymentFailed, OrderConfirmed, OrderFailed
- **Consumers**: Payment Service, Notification Service

### 5. **Redis Multi-Purpose Strategy**
| Purpose | Use Case |
|---------|----------|
| Idempotency | Store request → response mapping |
| Rate Limiting | Track API Gateway request counts |
| Session Cache | Store user session data |
| Product/Inventory Cache | Cache-aside pattern for hot reads |

### 6. **Multi-Tenancy**
- **Isolation**: Each tenant has separate data with `tenantId` as partition key
- **Authentication**: JWT includes `tenantId` for request validation
- **Data Segregation**: Queries filter by `tenantId` at database level

### 7. **Stateless Services**
- All services are stateless → scale horizontally behind load balancer
- State stored in PostgreSQL (transactional data) or Redis (session data)

## Core Entities

### Data Model

```sql
-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255),
  role ENUM('BUYER', 'SELLER'),
  tenant_id UUID,
  created_at TIMESTAMP
);

-- Products
CREATE TABLE products (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  price DECIMAL(10, 2),
  seller_id UUID REFERENCES users(id),
  tenant_id UUID,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

-- Inventory
CREATE TABLE inventory (
  id UUID PRIMARY KEY,
  product_id UUID REFERENCES products(id),
  available_qty INT,
  reserved_qty INT,
  version INT (for optimistic locking),
  updated_at TIMESTAMP
);

-- Orders
CREATE TABLE orders (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  total_amount DECIMAL(10, 2),
  status ENUM('CREATED', 'RESERVED', 'CONFIRMED', 'FAILED'),
  payment_status ENUM('PENDING', 'SUCCESS', 'FAILED'),
  tenant_id UUID,
  created_at TIMESTAMP
);

-- Order Items
CREATE TABLE order_items (
  id UUID PRIMARY KEY,
  order_id UUID REFERENCES orders(id),
  product_id UUID REFERENCES products(id),
  quantity INT,
  price DECIMAL(10, 2)
);

-- Payments
CREATE TABLE payments (
  id UUID PRIMARY KEY,
  order_id UUID REFERENCES orders(id),
  user_id UUID REFERENCES users(id),
  amount DECIMAL(10, 2),
  payment_method VARCHAR(50),
  status ENUM('PENDING', 'SUCCESS', 'FAILED'),
  created_at TIMESTAMP
);

-- Notifications
CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  message TEXT,
  event_type VARCHAR(100),
  read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP
);
```

## Tech Stack

### Backend Framework
- **Spring Boot 4.0.6** - Application framework
- **Spring Cloud 2025.1.1** - Microservices orchestration
- **Spring Cloud Gateway** - API Gateway with reactive routing
- **Java 17** - Language version

### Data & Storage
- **PostgreSQL 15** - Primary relational database
- **Redis 7-Alpine** - Cache and session store
- **Spring Data JPA** - ORM framework

### Messaging & Events
- **Apache Kafka 4.1.1** - Event streaming platform
- **Spring Kafka** - Kafka integration (in order/payment services)

### Authentication & Security
- **Spring Security** - Authentication framework
- **JWT (JJWT 0.11.5)** - Token-based authentication
- **BCrypt** - Password hashing

### API & Communication
- **Spring WebMVC** - REST API framework
- **Spring WebFlux** - Reactive web framework (gateway)
- **RestTemplate/WebClient** - HTTP client for inter-service communication

### Testing
- **Spring Boot Test** - Testing framework
- **Mockito** - Mocking library
- **TestContainers** (optional for integration tests)

### DevOps & Deployment
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Maven** - Build automation

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Maven 3.8+

### Quick Start with Docker Compose

```bash
# 1. Clone the repository
git clone <repository-url>
cd order-inventory-management-platform

# 2. Start all services
docker-compose up -d

# 3. Verify all services are healthy
docker-compose ps

# Check API Gateway health
curl http://localhost:8000/actuator/health
```

### Service URLs

Once running, access services at:
- **API Gateway**: http://localhost:8000
- **Auth Service**: http://localhost:8081
- **Product Service**: http://localhost:8082
- **Order Service**: http://localhost:8083
- **Inventory Service**: http://localhost:8084
- **Payment Service**: http://localhost:8085
- **Notification Service**: http://localhost:8086
- **Redis**: localhost:6379
- **Kafka**: localhost:9092

### Local Development Setup

```bash
# 1. Install Java 17
# Download from: https://jdk.java.net/17/

# 2. Install Maven
# macOS: brew install maven
# Windows: choco install maven
# Linux: sudo apt-get install maven

# 3. Start only PostgreSQL and Redis (optional)
docker-compose up -d postgres redis kafka-broker

# 4. Run a specific service locally
cd auth-service
mvn spring-boot:run

# 5. Build all services
mvn clean install

# 6. Run tests
mvn test
```

### Environment Configuration

Default configurations are in `application.properties` / `application.yml` in each service:

**Key Environment Variables**:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/service_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update

JWT_SECRET=base64_encoded_secret
JWT_ACCESS_TOKEN_EXPIRATION=900000    # 15 minutes
JWT_REFRESH_TOKEN_EXPIRATION=604800000 # 7 days

SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
```

## API Documentation

### Authentication Flow

#### 1. Register Tenant
```http
POST /api/v1/auth/tenant
Content-Type: application/json

{
  "name": "TenantName",
  "email": "tenant@example.com",
  "password": "password123"
}

Response:
{
  "tenantId": "uuid",
  "name": "TenantName",
  "email": "tenant@example.com"
}
```

#### 2. Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123",
  "role": "SELLER",
  "tenantId": "tenant-uuid"
}

Response:
{
  "userId": "uuid",
  "name": "John Doe",
  "email": "user@example.com",
  "role": "SELLER"
}
```

#### 3. Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token",
  "userId": "uuid",
  "tenantId": "uuid",
  "role": "SELLER"
}
```

### Product Management

#### Create Product
```http
POST /api/v1/products
Headers:
  - sellerId: uuid
  - tenantId: uuid
  - Content-Type: application/json

{
  "name": "Product Name",
  "description": "Product description",
  "price": 99.99,
  "quantity": 100,
  "stockStatus": "IN_STOCK"
}
```

#### Search Products
```http
GET /api/v1/products/search?keyword=laptop
Headers:
  - tenantId: uuid
```

#### Filter Products
```http
GET /api/v1/products/filter
Headers:
  - tenantId: uuid

Query params:
  - name=laptop
  - minPrice=500
  - maxPrice=2000
  - stockStatus=IN_STOCK
```

### Order Management

#### Place Order
```http
POST /api/v1/orders
Headers:
  - userId: uuid
  - Idempotency-Key: unique-key
  - Content-Type: application/json

{
  "items": [
    {
      "productId": "uuid",
      "quantity": 2,
      "price": 99.99
    }
  ]
}

Response:
{
  "id": "order-uuid",
  "userId": "uuid",
  "status": "CREATED",
  "totalAmount": 199.98,
  "items": [...],
  "createdAt": "2024-05-15T10:30:00Z"
}
```

#### Get Order by ID
```http
GET /api/v1/orders/{orderId}
Headers:
  - userId: uuid
```

### Inventory Management

#### Check Inventory
```http
GET /api/v1/inventory/product/{productId}
Headers:
  - tenantId: uuid

Response:
{
  "productId": "uuid",
  "availableQty": 50,
  "reservedQty": 10,
  "version": 5
}
```

### Payment Processing

#### Create Payment
```http
POST /api/v1/payments
Content-Type: application/json

{
  "orderId": "order-uuid",
  "userId": "user-uuid",
  "amount": 199.98,
  "paymentMethod": "CARD"
}
```

### Full API Endpoints
Detailed API documentation is available in Postman collections:
- **Direct Services**: `docs/postman/postman_collection.json`
- **Via API Gateway**: `docs/postman/api-gateway_collection.json`

## Testing

### Unit Tests
```bash
cd <service-name>
mvn test
```

### Integration Tests
Each service includes integration test suite testing service interactions.

### End-to-End Testing with Postman

1. **Import Collections**:
   - `docs/postman/postman_collection.json` - Direct service testing
   - `docs/postman/api-gateway_collection.json` - Gateway testing

2. **Set Environment Variables** in Postman

3. **Run Collections** in sequence to test complete workflows

4. **Recommended Test Flow**:
   - Register Tenant → Register User → Login
   - Create Product → Search Products
   - Place Order → Get Order Status
   - Create Payment → Verify Payment Status

### Performance Testing

The system is designed to handle:
- **20M DAU** with 20% writing 80% reading
- **P95 latency** for POST/PUT: < 200ms
- **P95 latency** for GET: < 100ms

Test these with tools like JMeter or k6:
```bash
# Example JMeter test
jmeter -n -t test-plan.jmx -l results.jtl
```

## Future Improvements

### Short Term (Next Release)
- [ ] Implement distributed tracing (Jaeger)
- [ ] Add service-to-service authentication (mTLS)
- [ ] Circuit breaker pattern (Hystrix/Resilience4j)
- [ ] Request validation middleware
- [ ] Comprehensive error handling and custom exceptions

### Medium Term (Q2-Q3)
- [ ] Analytics and reporting service
- [ ] Seller dashboard service
- [ ] Buyer recommendation service (ML-based)
- [ ] Audit logging service
- [ ] Advanced search (Elasticsearch)
- [ ] Multi-region deployment with data replication

### Long Term (Q4+)
- [ ] Microservices mesh (Istio) for advanced traffic management
- [ ] Machine learning for demand forecasting
- [ ] Real-time inventory synchronization across regions
- [ ] Mobile app support (native clients)
- [ ] Advanced security features (OAuth2, SAML)
- [ ] Kubernetes deployment with auto-scaling

## Documentation

- **System Design**: [docs/SYSTEM_DESIGN.md](docs/SYSTEM_DESIGN.md) - Detailed architecture, data models, and flow diagrams
- **API Gateway Guide**: [docs/postman/API_GATEWAY_COLLECTION_GUIDE.md](docs/postman/API_GATEWAY_COLLECTION_GUIDE.md) - Gateway testing documentation
- **Docker Compose**: [docker-compose.yaml](docker-compose.yaml) - Complete infrastructure setup

### Service Documentation
Each service has its own `HELP.md`:
- [api-gateway/HELP.md](api-gateway/HELP.md)
- [auth-service/HELP.md](auth-service/HELP.md) (available in service)
- [product-service/HELP.md](product-service/HELP.md)
- [order-service/HELP.md](order-service/HELP.md) (available in service)
- [inventory-service/HELP.md](inventory-service/HELP.md)
- [payment-service/HELP.md](payment-service/HELP.md)
- [notification-service/HELP.md](notification-service/HELP.md)

## Key Learnings

### Distributed Systems Challenges
1. **Race Condition Prevention**: Optimistic locking is superior to pessimistic locking for high-concurrency scenarios
2. **Data Consistency**: Eventual consistency significantly improves throughput compared to strong consistency
3. **Event Ordering**: Kafka topic partitioning critical for maintaining order of related events

### Microservices Best Practices
1. **Single Responsibility**: Each service owns specific domain logic and data
2. **API Contracts**: Versioning API endpoints prevents breaking changes
3. **Resilience**: Implement retry logic, timeouts, and circuit breakers
4. **Monitoring**: Distributed tracing is essential for debugging issues across services

### Technology Choices
1. **Spring Cloud Gateway**: Simplified routing and authentication vs custom implementation
2. **Kafka**: Excellent for decoupling services and handling backpressure
3. **Redis**: Exceptional performance for session storage and caching
4. **PostgreSQL**: ACID compliance crucial for inventory consistency

### Operational Insights
1. **Observability**: Structured logging (JSON format) makes analysis easier
2. **Docker**: Standardizes development and production environments
3. **Health Checks**: Critical for automated failure detection and recovery
4. **Scaling**: Stateless services scale horizontally; stateful resources (DB) need careful planning

### Performance Optimization
1. **Caching Strategy**: Implement cache-aside pattern for expensive operations
2. **Connection Pooling**: Critical for database performance under load
3. **Batch Operations**: Reduce network overhead for bulk operations
4. **Query Optimization**: Proper indexing on frequently queried columns

---

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Contributing

Contributions are welcome! Please create a pull request with your changes and include appropriate tests.

## Support

For issues or questions, please:
1. Check existing issues on GitHub
2. Create a new issue with detailed description
3. Include error logs and steps to reproduce

