# E-commerce API (Spring Boot)

A simple RESTful API for managing products in an e-commerce context. Built with Spring Boot, Spring Data JPA, H2 database, Bean Validation, and springdoc-openapi for interactive API docs.

This repository contains a minimal CRUD service around a Product resource with validation, a file-based H2 database, and Swagger UI for exploration.

## Tech Stack
- Language: Java 17
- Framework: Spring Boot 3.5.6 (Web, Data JPA, Validation)
- Database: H2 (file-based)
- Build/Package Manager: Maven (with Maven Wrapper)
- Annotation processing: Lombok
- API Documentation: springdoc-openapi (Swagger UI)

## Requirements
- Java 17 (JDK)
- No need to install Maven explicitly; the repo includes the Maven Wrapper (mvnw / mvnw.cmd).

## Getting Started

### Clone
- Ensure you are in a directory without spaces in critical tooling if you hit issues; otherwise spaces are generally fine on modern setups.
- Clone or download this repository to your local machine.

### Configure (optional)
Default configuration uses an H2 file database at `./data/testdb` and enables the H2 console.
You can override any property via environment variables (Spring Boot convention) or JVM system properties. See Environment Variables section below.

Key defaults from `src/main/resources/application.properties`:
- H2 console enabled at `/h2-console`
- Datasource URL: `jdbc:h2:file:./data/testdb`
- Username: `sa`, Password: (empty)
- Hibernate DDL auto: `update`
- JPA show SQL: `true`
- Springdoc OpenAPI JSON: `/v3/api-docs`, UI: `/swagger-ui`

### Run (development)
Using the Maven Wrapper on Windows PowerShell/Command Prompt from the project root:

- Start the app with live reload (devtools present):
  - `mvnw.cmd spring-boot:run`

- Or build and run the jar:
  - `mvnw.cmd clean package`
  - `java -jar target\demo-0.0.1-SNAPSHOT.jar`

Once running:
- API base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui` (or `/swagger-ui/index.html`)
- OpenAPI spec (JSON): `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/testdb`, user `sa`, empty password)

## API Overview

Base path: `/api/products`

- POST `/api/products` — Create a product
  - Request body (JSON):
    - `name` (string, required)
    - `description` (string, optional)
    - `price` (number >= 0, required)
    - `stock` (integer >= 0, required)
  - Returns: 201 Created with `Location` header and created product

- GET `/api/products/{id}` — Get product by id
- GET `/api/products` — List all products
- PUT `/api/products/{id}` — Update product by id
- DELETE `/api/products/{id}` — Delete product by id

Validation: Bean Validation (Jakarta) is used. Example messages include "name required", "price must be >= 0", "stock must be >= 0".

### Example cURL

- Create:
```
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical",
    "price": 49.99,
    "stock": 100
  }'
```

- List:
```
curl http://localhost:8080/api/products
```

## Environment Variables and Configuration
Spring Boot properties can be overridden via environment variables using `SPRING_*` mappings or via `--property=value` command-line args.

Common overrides:
- `SPRING_DATASOURCE_URL` (e.g., `jdbc:h2:file:./data/testdb` or external DB URL)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_HIBERNATE_DDL_AUTO` (e.g., `validate`, `update`, `none`)
- `SERVER_PORT` (default 8080)

Example run with alternate port:
```
java -jar target\demo-0.0.1-SNAPSHOT.jar --server.port=9090
```

## Scripts and Useful Maven Commands
Using the Maven Wrapper (recommended):
- Run app: `mvnw.cmd spring-boot:run`
- Run tests: `mvnw.cmd test`
- Package jar: `mvnw.cmd clean package`
- Format/Check (if plugins added later): TODO

If you have Maven installed, you can replace `mvnw.cmd` with `mvn` on Windows or `./mvnw` on Unix-like systems.

## Tests
There is a basic context load test at:
- `src/test/java/com/gabr/ecommerce/EcommerceApiApplicationTests.java`

Run tests:
- `mvnw.cmd test`

## Project Structure
```
E:/Projects/Spring boot Test/demo
├─ pom.xml
├─ mvnw, mvnw.cmd
├─ src
│  ├─ main
│  │  ├─ java/com/gabr/ecommerce
│  │  │  ├─ EcommerceApiApplication.java
│  │  │  ├─ config/OpenApiConfig.java
│  │  │  ├─ controllers/ProductController.java
│  │  │  ├─ dto/ProductDto.java
│  │  │  ├─ entity/Product.java
│  │  │  ├─ exception/GlobalExceptionHandler.java
│  │  │  ├─ repository/ProductRepository.java
│  │  │  ├─ service/ProductService.java
│  │  │  └─ service/impl/ProductServiceImpl.java
│  │  └─ resources/application.properties
│  └─ test/java/com/gabr/ecommerce/EcommerceApiApplicationTests.java
├─ data/ (H2 database files created at runtime)
└─ target/ (build output)
```

## Notes
- The H2 database is configured as file-based in `./data/`. Data persists between runs unless you delete the `data` directory.
- Lombok is used; ensure your IDE has Lombok plugin enabled and annotation processing turned on.

## License
- TODO: Add license information or a LICENSE file to clarify usage terms.

## Roadmap / TODOs
- Add Dockerfile and containerization instructions.
- Add database migration tool (e.g., Flyway or Liquibase).
- Add more comprehensive tests (controller/service/repository levels).
- Configure CI (e.g., GitHub Actions) for build and test.
- Document error response formats from `GlobalExceptionHandler`.
- Provide sample Postman collection.


# REDIS 
- redis-cli ping
- sudo service redis-server start
