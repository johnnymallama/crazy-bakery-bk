# Feature: Seguridad JWT con Firebase Authentication

## Contexto

El backend de Crazy Bakery actualmente tiene todos los endpoints públicos, sin ningún mecanismo de autenticación ni autorización. Spring Security está presente en el proyecto pero configurado para permitir todo el tráfico sin restricciones.

La autenticación de usuarios está delegada a **Firebase Authentication** en el frontend. El UID generado por Firebase es el identificador principal del usuario en el backend (campo `id` de la entidad `Usuario`).

## Objetivo

Proteger todos los endpoints del backend mediante la validación de **Firebase ID Tokens**, enviados por el frontend en el header HTTP `Authorization`. El Firebase Admin SDK ya está instalado en el proyecto, por lo que no se requieren librerías adicionales de JWT.

---

## Flujo de Autenticación

```
Frontend                        Firebase Auth              Backend
   |                                  |                        |
   |-- 1. createUserWithEmailAndPw -->|                        |
   |<-- UID + Firebase ID Token ------|                        |
   |                                  |                        |
   |-- 2. POST /usuarios (UID + datos) --------------------- >|
   |       (endpoint público, sin token)                       |
   |<--------------------- 201 Created --------------------- -|
   |                                  |                        |
   |-- 3. Cualquier petición posterior --------------------- >|
   |       Header: Authorization: Bearer <Firebase ID Token>  |
   |                                  |                        |
   |                    Backend valida token con Firebase SDK  |
   |                                  |<-- verifyIdToken() ---|
   |                                  |--- UID + claims ------>|
   |<--------------------- 200 OK / respuesta --------------- |
```

---

## Endpoints Públicos (sin autenticación)

| Método | Path | Descripción |
|--------|------|-------------|
| `POST` | `/usuarios` | Crear usuario (se llama justo después del registro en Firebase) |

Todos los demás endpoints requieren token válido.

---

## Endpoints Protegidos (requieren `Authorization: Bearer <token>`)

| Controlador | Endpoints |
|-------------|-----------|
| **UsuarioController** | `GET /usuarios`, `GET /usuarios/{id}`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}` |
| **TortaController** | `POST /torta`, `GET /torta` |
| **RecetaController** | `POST /receta`, `GET /receta/{id}`, `GET /receta/ultimas-imagenes` |
| **OrdenController** | `POST /orden`, `GET /orden`, `GET /orden/{id}`, `GET /orden/usuario/{usuarioId}`, `GET /orden/estado/{estado}`, `GET /orden/fecha`, `PATCH /orden/{id}/estado`, `PATCH /orden/{id}/nota`, `PATCH /orden/{id}/receta` |
| **IngredienteController** | `POST /ingredientes`, `GET /ingredientes`, `GET /ingredientes/{id}`, `PUT /ingredientes/{id}`, `DELETE /ingredientes/{id}`, `GET /ingredientes/tipo/{tipo}`, `GET /ingredientes/search` |
| **TamanoController** | `POST /tamanos`, `GET /tamanos`, `GET /tamanos/{id}`, `PUT /tamanos/{id}`, `DELETE /tamanos/{id}`, `GET /tamanos/tipo-receta/{tipoReceta}` |
| **GeografiaController** | `GET /geografia/departamentos`, `GET /geografia/ciudades` |
| **ImageGenerationController** | `POST /generate-image`, `POST /generate-image/custom-cake` |
| **IngredienteTamanoController** | `GET /ingrediente-tamano/{tamanoId}`, `POST /ingrediente-tamano`, `DELETE /ingrediente-tamano/{id}` |
| **CostoController** | `POST /costo/calcular` |
| **ReportController** | `POST /generate-reports` |

---

## Arquitectura de la Solución

### Componentes nuevos a crear

```
infrastructure/
  web/
    config/
      SecurityConfig.java         ← MODIFICAR (reemplazar configuración actual)
    security/
      FirebaseTokenFilter.java    ← NUEVO (filtro que valida el token en cada request)
```

### Flujo interno del filtro

```
Request entrante
      |
      ↓
FirebaseTokenFilter
      |
      ├── ¿Es POST /usuarios? → Continuar sin validar
      |
      ├── ¿Tiene header Authorization: Bearer <token>?
      |       NO → Retornar 401 Unauthorized
      |
      ├── FirebaseAuth.getInstance().verifyIdToken(token)
      |       ERROR → Retornar 401 Unauthorized
      |
      └── Token válido → Continuar con la request
```

---

## Configuración CORS

La configuración actual usa `allowedOrigins("*")`, lo cual es **incompatible con el envío de headers de autenticación** en algunos navegadores. Se debe ajustar para:

- `allowedOrigins`:
  - Producción: `https://uan-crazy-bakery-ui--uan-especializacion.us-central1.hosted.app`
  - Desarrollo local: `http://localhost:9002`
- `allowedHeaders`: incluir explícitamente `Authorization`, `Content-Type`.
- `allowCredentials`: `false` (usamos JWT en header, no cookies, por lo que no es necesario).
- `allowedMethods`: `GET, POST, PUT, PATCH, DELETE, OPTIONS`.
- `exposedHeaders`: `Authorization` (por si el frontend necesita leer el header de respuesta).

> **Nota:** Cuando `allowedOrigins` es `"*"` y `allowCredentials` es `true`, el navegador rechaza la request (restricción CORS del navegador). Como usamos JWT en header (no cookies), `allowCredentials` queda en `false` y se especifica el origen exacto del frontend.

---

## Cambios en el Modelo

**No se requieren cambios al modelo de datos.** La entidad `Usuario`, sus DTOs y la base de datos permanecen igual. Firebase maneja las credenciales de autenticación de forma independiente.

---

## Dependencias

No se requieren dependencias adicionales. El **Firebase Admin SDK** (`firebase-admin`) ya está en el `pom.xml` y provee `FirebaseAuth.getInstance().verifyIdToken()`.

---

## Criterios de Aceptación

- [ ] `POST /usuarios` es accesible sin token → responde normalmente
- [ ] Cualquier endpoint protegido sin token → responde `401 Unauthorized`
- [ ] Cualquier endpoint protegido con token inválido o expirado → responde `401 Unauthorized`
- [ ] Cualquier endpoint protegido con token válido de Firebase → responde normalmente
- [ ] Las peticiones `OPTIONS` (preflight CORS) no son bloqueadas por el filtro
- [ ] El frontend puede enviar el header `Authorization` sin ser bloqueado por CORS

---

## Consideraciones Adicionales

- **Stateless:** La aplicación no almacena sesiones. Cada request se valida de forma independiente con Firebase.
- **CSRF:** Permanece deshabilitado (correcto para APIs REST stateless con JWT).
- **Expiración del token:** Firebase ID Tokens expiran en 1 hora. El frontend es responsable de refrescar el token. El backend simplemente rechaza tokens expirados con `401`.
- **Roles/Autorización:** Esta feature cubre solo **autenticación** (¿quién eres?). La autorización por roles (`TipoUsuario.ADMINISTRADOR` vs `CONSUMIDOR`) queda fuera del alcance de esta feature y puede implementarse como siguiente paso.
