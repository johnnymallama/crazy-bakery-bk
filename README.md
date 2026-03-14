# Crazy Bakery — Backend API

API REST para el sistema de gestión de una panadería/pastelería, construida con Java 21 y Spring Boot 3. Permite administrar tortas personalizadas, recetas, órdenes, ingredientes y usuarios, con generación automática de imágenes mediante OpenAI DALL-E 3 y almacenamiento en Firebase Storage.

---

## Tecnologías

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.9 |
| Base de datos (producción) | MySQL (Cloud SQL) |
| Base de datos (desarrollo) | H2 in-memory |
| Migraciones | Flyway (esquema aplicado manualmente) |
| Mapeo de objetos | MapStruct |
| Seguridad | Firebase Authentication (JWT) |
| Almacenamiento de imágenes | Firebase Storage |
| Generación de imágenes | OpenAI DALL-E 3 |
| Contenerización | Docker + Jib (sin daemon local) |
| Despliegue | Google Cloud Run |
| Registro de imágenes | Google Artifact Registry |
| Testing | JUnit 5 + JaCoCo |

---

## Arquitectura

El proyecto sigue una **arquitectura limpia (Clean Architecture)** dividida en tres capas:

```
src/main/java/uan/edu/co/crazy_bakery/
├── domain/
│   └── model/           # Entidades JPA (Torta, Receta, Orden, Ingrediente, Usuario, etc.)
├── application/
│   ├── services/        # Interfaces de servicios
│   ├── services/impl/   # Implementaciones de lógica de negocio
│   ├── dto/             # Objetos de transferencia de datos
│   └── mapper/          # Mappers MapStruct (entidad ↔ DTO)
└── infrastructure/
    ├── web/controllers/ # Controladores REST
    ├── repositories/    # Repositorios Spring Data JPA
    ├── config/          # Beans de configuración (Firebase, OpenAI, seguridad)
    └── client/          # Clientes REST externos (OpenAI)
```

**Flujo principal:**
```
Controller → Service → Repository / Cliente externo → DTO (MapStruct) → Response
```

**Flujo de generación de imágenes:**
```
ImageGenerationController → ImagenService → OpenAI (DALL-E 3) → Firebase Storage → URL pública
```

**Cálculo de costos:**
```
RecetaService → Vista BD `ingredientes_costos` + variables de entorno (mano de obra, operación, beneficio)
```

---

## Controladores

| Controlador | Ruta base | Endpoints principales |
|---|---|---|
| `UsuarioController` | `/usuarios` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| `TortaController` | `/torta` | `POST`, `GET` |
| `RecetaController` | `/receta` | `POST`, `GET /{id}`, `GET /ultimas-imagenes` |
| `OrdenController` | `/orden` | `POST`, `GET`, `GET /{id}`, `GET /usuario/{id}`, `GET /estado/{estado}`, `GET /fecha`, `PATCH /{id}/estado`, `PATCH /{id}/nota`, `PATCH /{id}/receta` |
| `IngredienteController` | `/ingredientes` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `GET /tipo/{tipo}`, `GET /search` |
| `IngredienteTamanoController` | `/ingrediente-tamano` | `POST`, `GET /{tamanoId}`, `DELETE /{id}` |
| `TamanoController` | `/tamanos` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `GET /tipo-receta/{tipo}` |
| `CostoController` | `/costo` | `POST /calcular` |
| `ImageGenerationController` | `/generate-image` | `POST`, `POST /custom-cake` |
| `ReportController` | `/generate-reports` | `POST` |
| `GeografiaController` | `/geografia` | `GET /departamentos`, `GET /ciudades` |

---

## Ejecutar localmente

### Requisitos

- Java 21 (JDK)
- Apache Maven 3.x

### Pasos

En desarrollo se usa H2 in-memory, por lo que **no se requiere MySQL ni ninguna base de datos externa**.

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd crazy-bakery-bk

# Ejecutar con el perfil de desarrollo (por defecto)
mvn spring-boot:run
```

La aplicación quedará disponible en:
- **API:** `http://localhost:8080`
- **Consola H2:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`

### Credenciales de Firebase en local

Para que la generación de imágenes funcione localmente, se necesita autenticar con Firebase. Descarga el archivo JSON de cuenta de servicio desde la consola de Firebase y declara la variable de entorno antes de arrancar:

```bash
# macOS / Linux
export GOOGLE_APPLICATION_CREDENTIALS="/ruta/absoluta/al/archivo/serviceAccountKey.json"
mvn spring-boot:run

# Windows (PowerShell)
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\ruta\al\archivo\serviceAccountKey.json"
mvn spring-boot:run
```

> **Nota:** Si no necesitas probar la generación de imágenes, puedes omitir esta variable. Los demás endpoints funcionarán sin ella.

### Ejecutar tests

```bash
mvn test
```

---

## Perfiles de configuración

| Perfil | Base de datos | Activación |
|---|---|---|
| `dev` (por defecto) | H2 in-memory | `mvn spring-boot:run` |
| `prod` | Cloud SQL (MySQL) | Configurado en Cloud Run |

---

## Variables de entorno (producción)

| Variable | Descripción |
|---|---|
| `OPENAI_API_KEY` | Clave de API de OpenAI para generación de imágenes |
| `GOOGLE_APPLICATION_CREDENTIALS` | En Cloud Run no se usa esta variable — las credenciales se aplican automáticamente vía la cuenta de servicio asignada al servicio |
| `SPRING_DATASOURCE_USERNAME` | Usuario de MySQL |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de MySQL |
| `DB_NAME` | Nombre de la base de datos MySQL |
| `INSTANCE_CONNECTION_NAME` | Identificador de instancia Cloud SQL |
| `LABOR_COST` | Costo de mano de obra para cálculo de precios |
| `OPERATING_COST` | Costo operativo para cálculo de precios |
| `BENEFIT_PERCENTAGE` | Porcentaje de beneficio aplicado al precio final |
| `HISTORY_MONTH` | Meses de historial de órdenes a retornar (por defecto: 3) |

---

## Despliegue en Google Cloud

### 1. Autenticación

```bash
gcloud auth login
gcloud auth application-default login
gcloud auth configure-docker us-central1-docker.pkg.dev
```

### 2. Construir y publicar imagen

```bash
mvn compile jib:build
```

Publica la imagen en:
```
us-central1-docker.pkg.dev/uan-especializacion/crazy-bakery/crazy-bakery-bk
```

### 3. Desplegar en Cloud Run

Desde la consola de Google Cloud, crear o actualizar el servicio con:
- La imagen recién publicada
- Conexión a Cloud SQL (`uan-especializacion:us-central1:uan-especializacion`)
- Las variables de entorno de producción listadas arriba

---

## Esquema de base de datos

El esquema inicial se encuentra en `script_db/V1__create_tables.sql`. Flyway está presente pero deshabilitado; los cambios se aplican manualmente. En producción, Hibernate opera en modo `validate` (sin modificaciones automáticas al esquema).
