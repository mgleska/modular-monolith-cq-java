# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A demo **modular monolith** (Spring Boot 4, Java 25) implementing the **Command & Query (CQRS)** pattern with strict, machine-enforced module boundaries. Domain: a buying group where mobile users browse per-store product offers and CMS employees toggle offer visibility. See `README.md` for the full domain narrative and API walk-through (curl examples for the mobile and CMS flows).

## Commands

Use `mvnw.cmd` on Windows (PowerShell), `./mvnw` under the Bash tool.

- **Build (jar):** `mvnw.cmd clean package -DskipTests`
- **Verify module boundaries (most important check):** `mvnw.cmd test -Dtest=ApplicationStructureTest` — runs Spring Modulith's `ApplicationModules.verify()`. Run this after any change that adds/moves imports across modules or layers.
- **Run all tests:** `mvnw.cmd test`
- **Run a single test:** `mvnw.cmd test -Dtest=ClassName` or `-Dtest=ClassName#methodName`
- **Regenerate `src/main/resources/openapi/openapi.yaml`:** `mvnw.cmd verify -DskipTests` (the springdoc plugin starts the app on :8080 and scrapes `/api-docs.yaml`).
- **Full stack (app + MariaDB + demo data):** `docker compose up` — builds the image, runs Flyway migrations, then seeds via the CLI import commands in `start-app.sh`. Requires a `.env` file with `DB_DATABASE`, `DB_USER`, `DB_PASSWORD`.
- **Swagger UI (when running):** http://127.0.0.1:8080/swagger-ui/index.html

The app is dual-mode (see `MmCqJava2Application`): started with a CLI keyword (an arg containing `:`, or `-h/--help/-V/--version/app-cli`) it runs as a picocli command and exits; otherwise it starts the web server. CLI commands: `store:import`, `product:import`, `product:quantity`, `offer:import <storeRid>`.

## Architecture — the rules that matter

The whole point of this codebase is the enforced structure. Getting an edit wrong here breaks `ApplicationStructureTest`, not just style.

**Package = module = boundary.** Each top-level package under `com.mgleska.mmcqjava2` is a Spring Modulith module (`customer`, `offer`, `product`, `store`, `user`). `shared` is NOT a module — it's an OPEN application module (`shared/package-info.java`) holding common infrastructure any module may import.

**Three layers per module, with strict allowed-import directions:**
- `access/` — adapters translating the outside world into actions. Sub-packages `controller/` (REST) and `console/` (picocli CLI). May import only its own module's `action/` layer + `shared`. Controllers are thin: they just call an action's `handle(...)`.
- `action/` — the domain core. Sub-packages `command/`, `query/`, `enums/`. May import: own `action`/`model`/`support`, **other modules' exposed actions**, and `shared`.
- `model/` + `support/` — JPA entities, repositories, module-internal services. The lowest layer.

**Cross-module access goes through actions only.** A module never imports another module's `model` or `access`. It imports the other module's `action` classes directly (no events required). Which action packages are visible across modules is declared by `@NamedInterface("<ModuleName>")` in `package-info.java` (e.g. `offer/action/query/package-info.java` exposes the `Offer` named interface). If you add a new `action` sub-package that other modules must call, it needs a matching `package-info.java`.

## Conventions

- **Action class naming by suffix:** `*Cmd` (command), `*Qry` (query), `*Con` (picocli console command), `*Controller` (REST). One action = one class = one public `handle(...)` method + private helpers.
- **DTOs are nested records inside the action:** `ParamDto` (input), `ResultDto` / `ResultItemDto` (output), annotated with Jakarta Validation. These records are the *only* contract between modules and the outside world — no entity ever leaves a module. This is what makes the OpenAPI generation work (`springdoc.use-fqn=true`).
- **Zero CRUD / zero PUT/PATCH/DELETE.** All mutations are POST commands; all reads are queries. Don't add REST verbs other than GET/POST. Rationale is in `README.md` ("Zero CRUD — why?").
- **Optimistic locking is manual, not JPA `@Version`.** Entities carry a plain `int version`. Commands load with a pessimistic lock (`findWithLockById`), compare `dto.version` to the entity's, throw `AppEntityVersionException` on mismatch, then increment. Follow this pattern for new mutating commands (see `offer/.../ChangeVisibilityCmd`).
- **Entity table names are prefixed, class names are not:** class `Offer` → table `ofr_offer` (via `@Table(name=...)`). Group a module's tables under a common prefix.
- **Cross-module JPQL joins use the `JoinJpqlDto` builder pattern.** A module exposes a `Join<Entity>ByIdJpqlQry` action returning a `JoinJpqlDto` (fragment + provided-columns map). The consuming query composes JPQL strings and calls `confirmRequiredColumns(...)` to fail fast if the provider's columns changed. See `AdminGetOfferListQry` joining Store and Product.
- **Errors:** throw the `shared/exception/App*Exception` types; `CustomExceptionHandler` maps them to HTTP responses (and also handles exceptions thrown inside security filters).

## Security

Three ordered `SecurityFilterChain`s in `shared/SecurityConfig.java`:
1. `/api/customer/login`, `/api/admin/user/login` — public.
2. `/api/admin/**` — CMS users, `ROLE_USER`, opaque token validated by `user` module's `ValidateUserTokenCmd`.
3. `/api/**` — mobile users, **JWT** (auth0 java-jwt) validated by `customer` module's `ValidateAccessTokenCmd`. The JWT carries `uid` (user id) and `stid` (selected store id) so requests need no DB lookup. Signing secret and TTL come from `app.secret` / `app.mm-cq.access-token-ttl` in `application.properties`.

All chains are stateless; auth filters set the `SecurityContext` from the `Authorization: Bearer` header.

## Database

MariaDB, schema managed by **Flyway** (`src/main/resources/db/migration/V*.sql`) — `ddl-auto=none`, so schema changes are hand-written migrations, never Hibernate-generated. Standalone Flyway CLI config is in `flyway.conf`. `spring.jpa.open-in-view=false` (queries must run inside their action's transaction).
