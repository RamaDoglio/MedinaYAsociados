# Medina & Asociados — AGENTS.md

## Project structure

- **Spring Boot 3.3.5 / Java 21** backend in `Medina-Asociados/`.
- Layered: `controller` → `service` (business logic) → `repo` (JPA) → PostgreSQL.
- Package: `com.medina.asocDev.Medina.Asociados`.
- Application entrypoint: `Medina-Asociados/src/main/java/.../Application.java`.

## Essential commands (run from `Medina-Asociados/`)

| What | Command |
|------|---------|
| Build & test | `./mvnw clean verify` |
| Run locally | `./mvnw spring-boot:run` (port 8081, configured via `SERVER_PORT`) |
| Single test | `./mvnw test -Dtest=ApplicationTests` |

## Database

- PostgreSQL via Docker: `docker compose up -d` in `Medina-Asociados/`.
- DDL mode: `spring.jpa.hibernate.ddl-auto=update` (Hibernate manages schema).
- All credentials come from `Medina-Asociados/.env` — never hardcode.
- Expects env vars: `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`.

## Key architecture rules

- **No logic in Controllers** — just receive request, delegate to Service, return `ResponseEntity<T>`.
- **No business logic in Repositories** — queries only (derived → JPQL → native).
- **Entity↔DTO conversion** via `Utils` class only — no MapStruct, no ModelMapper.
- **Field injection** (`@Autowired`) everywhere — do not switch to constructor injection. (Exception: `PagoWebhookController` uses constructor injection, but follow the `@Autowired` convention for new code.)
- **IDs follow `idEntidad` pattern** (`idUsuario`, `idTurno`, `idEstado`, etc.).
- **`@Transactional`** on all write operations.
- **`orElseThrow(...)`** for Optional returns — never bare `.get()`.
- **`@PreAuthorize("@securityService.canAccessXxx(...)")`** for endpoint authorization.
- **`SLF4J`** for logging — never `System.out.println`.

## Turn state machine

```
RESERVADO → PAGADO → EN_CURSO → FINALIZADO
```

Extra states: `REPROGRAMADO`, `NO_ASISTIO`, `PENDIENTE_COBRO`, `CANCELADO_CON_REEMBOLSO`, `CANCELADO_SIN_REEMBOLSO`, `EXPIRO_PAGO`.

- Every state change MUST log via `historialTurnoService.registrarCambio(...)`.
- Configurable values go through `parametroService.getValor(...)`.

## Service responsibilities

| Service | Owns |
|---------|-------|
| `MercadoPagoService` | All Mercado Pago integration |
| `NotificacionTurnoService` | Email notifications |
| `CobroService` | Payment/charge logic |
| `SecurityService` | Authorization helpers (`canAccessClienteTurnos`, `canAccessAbogadoTurnos`) |
| `HistorialTurnoService` | State change history |
| `ParametroService` | System parameter lookups |

## DTO patterns

- `Response` DTO with `statusCode`, `message`, `token`, `roles`, `data` (generic `Object`) is used in `UsuarioService` for auth endpoints.
- `MensajeResponse` for simple string-only responses.
- Prefer DTO over Entity in API responses, but existing endpoints that return entities directly (e.g. `TurnoController.listarTurnos()`) should not be refactored without request.
- DTO naming: `XxxDTO`, `XxxCreateRequest`, `XxxListadoDTO`, `XxxDetalleDTO`.

## Known quirks (preserve, don't fix)

1. Package typo: `excepetion` (not `exception`).
2. `DataInitializer` has a flawed cast `(List<Rol>) rol` — preserve as-is unless asked.
3. `SecurityConfig` permits all requests under `/api/turnos/**` and `/api/abogados/**` publicly — auth via `@PreAuthorize` on individual endpoints instead.
4. `CorsConfig` allows all origins — do not restrict without instruction.

## Tests

- Only one test exists (`ApplicationTests.contextLoads()`).
- No integration, fixture, or snapshot test conventions established.

## Existing instruction files

- `Medina-Asociados/AGENTSmio.md` — detailed Spanish agent prompt (preserve, don't overwrite).
- `Medina-Asociados/tareasManu.md` and `tareasRamardo.md` — task checklists, read before implementing.

## Dependencies (don't add new ones without explicit request)

- `spring-boot-starter-web`, `-data-jpa`, `-security`, `-validation`, `-mail`, `-thymeleaf`
- PostgreSQL driver, Lombok, jjwt (0.13.0), AWS S3 SDK (1.12.782), Mercado Pago SDK (2.1.7)

## Workflow rules

- **Do NOT run the project** (`./mvnw spring-boot:run`, `./mvnw compile`, etc.) unless the user explicitly asks. Do not verify compilation after changes.
