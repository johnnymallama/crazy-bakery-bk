# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Crazy Bakery Backend** — a Spring Boot 3.5.9 REST API for a bakery management system, deployed on Google Cloud Run. Built with Java 21, MySQL (production), H2 (development), Firebase storage, and OpenAI DALL-E 3 for AI-powered cake image generation.

## Common Commands

```bash
# Run locally (uses dev profile with H2 in-memory DB)
mvn spring-boot:run

# Build JAR
mvn clean package

# Run tests with JaCoCo coverage
mvn test

# Build and push Docker image to Google Artifact Registry
mvn compile jib:build
```

To run a single test class:
```bash
mvn test -Dtest=ClassName
```

## Architecture

The project uses a **Clean Architecture** with three main layers inside `src/main/java/uan/edu/co/crazy_bakery/`:

- **`domain/model/`** — JPA entities (Usuario, Orden, Torta, Receta, Ingrediente, Nota, Tamano, etc.)
- **`application/`** — Business logic: services (`services/impl/`), DTOs (`dto/`), MapStruct mappers (`mapper/`), and service interfaces (`services/`)
- **`infrastructure/`** — Controllers (`web/controllers/`), Spring Data JPA repositories (`repositories/`), config beans (`config/`), and external REST clients (`client/`)

**Key flows:**
- Controllers receive requests → call application services → services use repositories/external clients → return DTOs via MapStruct mappers
- Image generation: controller → `ImagenService` → OpenAI client (DALL-E 3) → Firebase Storage → returns public URL stored on the entity
- Cost calculation: `RecetaService` aggregates ingredient costs from `ingredientes_costos` DB view + labor/operating cost env vars

## Profiles & Configuration

| Profile | Database | How to activate |
|---------|----------|-----------------|
| `dev` (default) | H2 in-memory + H2 console at `/h2-console` | `mvn spring-boot:run` |
| `prod` | Cloud SQL (MySQL via Unix socket) | Set in Cloud Run environment |

**Required environment variables (production):**

| Variable | Purpose |
|----------|---------|
| `OPENAI_API_KEY` | OpenAI API key for image generation |
| `GOOGLE_APPLICATION_CREDENTIALS` | Firebase service account JSON path |
| `SPRING_DATASOURCE_USERNAME` / `_PASSWORD` | MySQL credentials |
| `DB_NAME` | MySQL database name |
| `INSTANCE_CONNECTION_NAME` | Cloud SQL instance identifier |
| `LABOR_COST`, `OPERATING_COST`, `BENEFIT_PERCENTAGE` | Cost calculation parameters |
| `HISTORY_MONTH` | Months of order history to return (default: 3) |

## Key Domain Concepts

- **Torta**: A cake definition combining bizcocho (sponge), relleno (filling), cubertura (topping), and tamano (size)
- **Receta**: An instance of a torta with calculated costs and a generated image
- **Orden**: Customer order linked to one or more recetas, with status tracking (`EstadoOrden` enum)
- **Ingrediente**: Raw ingredients with cost per gram; combined in a `ingredientes_costos` DB view
- **Tamano**: Cake size with dimensions, portions, and gram quantities per ingredient type

## Database

Schema is in `script_db/V1__create_tables.sql`. Flyway is present but disabled; schema changes are applied manually. In production, Hibernate DDL is set to `validate` (no auto-changes).

## Deployment

Jib pushes to:
```
us-central1-docker.pkg.dev/uan-especializacion/crazy-bakery/crazy-bakery-bk
```
Cloud Run is configured to use the `prod` Spring profile.
