# SnapBuy - Backend

A production-ready, multi-role (Admin / Merchant / Customer) e-commerce REST API built with Java 21, Spring Boot 3, Spring Security, JWT, Redis, and Razorpay.

## Tech Stack

- **Language/Framework:** Java 21, Spring Boot 3.3
- **Security:** Spring Security, JWT (access + rotating refresh tokens), BCrypt
- **Persistence:** Spring Data JPA / Hibernate, MySQL 8
- **Cache/Session support:** Redis (OTP, login rate limiting, token blacklist, response caching)
- **Payments:** Razorpay
- **Mapping:** MapStruct
- **Docs:** SpringDoc OpenAPI (Swagger UI)
- **Email:** Spring Mail + Thymeleaf HTML templates
- **Testing:** JUnit 5, Mockito, AssertJ
- **Build:** Maven

## Architecture

Feature-based package structure, layered internally (`Controller → Service → Repository → Database`). See `docs/` for the ER diagram and further notes generated during development.

```
com.snapBuy
├── common / config / security / exception / notification / audit   # cross-cutting
├── user / auth / admin / merchant / customer                       # identity & role modules
├── category / product / cart / order / payment                     # commerce modules
└── util
```

## Roles & Core Flows

| Role | Highlights |
|---|---|
| **Admin** | Seeded on startup via `CommandLineRunner`. Manages merchants (create with temp password, block/unblock), customers (block/unblock), categories, and product approval. |
| **Merchant** | Created only by Admin. Forced password change on first login (enforced by a dedicated security filter). Manages own products, images, stock. |
| **Customer** | Self-registers with email OTP verification (Redis-backed). Browses/searches products, manages cart and addresses, checks out via Razorpay, views order history. |

## Getting Started

### Option A — Docker Compose (recommended)

```bash
cp .env.example .env
# edit .env with real values (JWT_SECRET, ADMIN_PASSWORD, MAIL_*, RAZORPAY_*)

docker compose up --build
```

This starts MySQL, Redis, and the app together. The API is available at `http://localhost:8080`.

### Option B — Run locally

Prerequisites: JDK 21, Maven 3.9+, MySQL 8 running locally, Redis running locally.

```bash
cp .env.example .env
export $(cat .env | xargs)   # or use your IDE's env file support

mvn clean install
mvn spring-boot:run
```

The `dev` profile is active by default (`spring.profiles.active=dev` in `application.yml`) and points at `localhost` for MySQL/Redis with sensible defaults if env vars are unset.

## Environment Variables

See `.env.example` for the full list. Required for a real deployment: `JWT_SECRET`, `ADMIN_PASSWORD`, `DB_PASSWORD`, `MAIL_USERNAME`/`MAIL_PASSWORD`, `RAZORPAY_KEY_ID`/`RAZORPAY_KEY_SECRET`.

## API Documentation

Once running:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (grouped by Auth / Public / Customer / Merchant / Admin, with a JWT "Authorize" button)
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Postman collection: `docs/postman-collection.json` (import into Postman; run "Login" first to auto-populate the `accessToken` variable used by every other request)

## Running Tests

```bash
mvn test
```

Unit tests (JUnit 5 + Mockito) cover the highest-risk business logic: JWT generation/validation, authentication (registration, login rate limiting, refresh token rotation), cart stock validation, and order checkout/cancellation (stock reservation and release).

## Build for Production

```bash
mvn clean package -DskipTests
java -jar target/snapBuy-1.0.0.jar --spring.profiles.active=prod
```

Or build the Docker image directly: `docker build -t snapBuy .`

## Deployment Notes

- `prod` profile uses `ddl-auto: validate` (not `update`) - the schema must already match the entities; run a proper migration tool or apply DDL manually before deploying schema changes. This is a deliberate gap: the project doesn't include Flyway/Liquibase, so schema evolution in production is a manual step.
- All secrets are environment-variable driven in `prod` - nothing sensitive is hardcoded or committed.
- `/actuator/**` beyond `health`/`info` requires `ROLE_ADMIN`.
- File uploads are stored on local disk (`uploads/`, mounted as a Docker volume). Swapping to S3/Cloudinary means editing `ProductImageStorageService` - no other code changes needed.

## Known Scope Notes

A few things were intentionally deferred or scoped out during development (see conversation history for full reasoning):
- **Brand, wishlist, and review modules** were cut from the original spec to keep scope manageable.
- **Merchant/Admin order visibility** (viewing orders that contain their products) was not built - only customer-facing order management exists.
- **Schema migrations** (Flyway/Liquibase) are not included; `ddl-auto` handles dev, manual DDL is expected for prod schema changes.

## License

Proprietary / portfolio project.
