# Shop Warehouse API

RESTful API for managing shop items, variants, pricing, and stock.

Built as a technical assessment using Java and Spring Boot.

## Tech Stack

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation
- Maven
- JUnit 5
- Mockito

## Features

- Item CRUD
- Variant CRUD
- Variant pricing management
- Stock management
- Stock addition
- Product selling
- Out-of-stock protection
- Request validation
- Centralized exception handling
- Unit and integration tests

## Architecture

The application follows a simple layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Controller

Handles HTTP requests, request validation, and HTTP responses.

### Service

Contains business logic such as stock management and validation of business rules.

### Repository

Uses Spring Data JPA to communicate with the database.

### Database

Uses H2 in-memory database for simplicity and easy setup.

## Data Model

An `Item` can have multiple `Variant` records.

```text
Item
 └── Variant
      ├── name
      ├── price
      └── stock
```

Relationship:

```text
items 1 ─────── N variants
```

## Running the Application

### Requirements

- Java 17+
- Maven 3.9+

The project includes Maven Wrapper, so Maven does not need to be installed separately.

### Run

```bash
./mvnw spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

### Run Tests

```bash
./mvnw clean test
```

### H2 Console

The H2 console is available at:

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:mem:warehouse
User Name: sa
Password:
```

Because the application uses an in-memory database, data is reset when the application restarts.

---

## API Endpoints

### Items

| Method | Endpoint          | Description    |
| ------ | ----------------- | -------------- |
| GET    | `/api/items`      | Get all items  |
| GET    | `/api/items/{id}` | Get item by ID |
| POST   | `/api/items`      | Create item    |
| PUT    | `/api/items/{id}` | Update item    |
| DELETE | `/api/items/{id}` | Delete item    |

### Variants

| Method | Endpoint                       | Description              |
| ------ | ------------------------------ | ------------------------ |
| GET    | `/api/items/{itemId}/variants` | Get variants for an item |
| POST   | `/api/items/{itemId}/variants` | Create variant           |
| GET    | `/api/variants/{id}`           | Get variant              |
| PUT    | `/api/variants/{id}`           | Update variant           |
| DELETE | `/api/variants/{id}`           | Delete variant           |

### Stock

| Method | Endpoint                   | Description  |
| ------ | -------------------------- | ------------ |
| PATCH  | `/api/variants/{id}/stock` | Add stock    |
| POST   | `/api/variants/{id}/sell`  | Sell variant |

---

## API Examples

### Create Item

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "T-Shirt"
  }'
```

Response:

```json
{
  "id": 1,
  "name": "T-Shirt"
}
```

### Create Variant

```bash
curl -X POST http://localhost:8080/api/items/1/variants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Black - M",
    "price": 150000,
    "stock": 10
  }'
```

Response:

```json
{
  "id": 1,
  "itemId": 1,
  "itemName": "T-Shirt",
  "name": "Black - M",
  "price": 150000,
  "stock": 10
}
```

### Add Stock

```bash
curl -X PATCH http://localhost:8080/api/variants/1/stock \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 5
  }'
```

### Sell Variant

```bash
curl -X POST http://localhost:8080/api/variants/1/sell \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 3
  }'
```

The stock is decreased from `15` to `12`.

### Insufficient Stock

If the requested quantity is greater than the available stock:

```json
{
  "message": "Insufficient stock"
}
```

The API returns:

```text
409 Conflict
```

The stock is not changed.

---

## Stock Concurrency

Stock deduction uses an atomic database update:

```sql
UPDATE variants
SET stock = stock - :quantity
WHERE id = :id
AND stock >= :quantity
```

The service checks the number of affected rows.

If no row is updated, the requested quantity cannot be fulfilled and the API returns `409 Conflict`.

This approach avoids the classic race condition where two concurrent requests both read the same stock value and both successfully deduct it.

---

## Validation

The API validates incoming requests using Jakarta Bean Validation.

Examples:

- Item name cannot be blank.
- Item name must contain 2–100 characters.
- Variant name cannot be blank.
- Price must be greater than zero.
- Stock cannot be negative.
- Sell/add-stock quantity must be at least 1.

Validation errors return `400 Bad Request`.

Example:

```json
{
  "message": "Validation failed",
  "errors": {
    "name": "Name is required"
  }
}
```

---

## Design Decisions

### BigDecimal for Price

`BigDecimal` is used instead of `double` for monetary values to avoid floating-point precision issues.

### DTO for Variant Response

Variant responses use a dedicated DTO instead of exposing the JPA entity directly.

This avoids exposing persistence relationships and prevents lazy-loading/serialization issues.

### H2 In-Memory Database

H2 was selected because the assessment allows an in-memory database and it keeps the project easy to run without requiring an external database.

The trade-off is that data is reset when the application restarts.

### Variant Names

Variant names are not required to be unique within an item because uniqueness was not specified in the requirements.

### HTTP Status Codes

- `200 OK` — successful read/update/stock operation
- `201 Created` — successful creation
- `204 No Content` — successful deletion
- `400 Bad Request` — invalid request
- `404 Not Found` — resource does not exist
- `409 Conflict` — insufficient stock

---

## Testing

The project contains:

- Unit tests for stock business logic
- Integration tests for REST endpoints
- Validation tests
- Error handling tests
- Stock overselling tests

Run all tests with:

```bash
./mvnw clean test
```

## Assumptions

- Selling a variant immediately decreases its stock.
- A sell request must contain a positive quantity.
- Stock cannot become negative.
- Price must be greater than zero.
- Variant names do not need to be unique.
- Authentication and authorization are outside the scope of this assessment.
- Pagination is not required because it was not specified.
- H2 in-memory storage is sufficient for the assessment.
