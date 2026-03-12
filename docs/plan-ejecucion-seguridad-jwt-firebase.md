# Plan de Ejecución: Seguridad JWT con Firebase Authentication

**Referencia:** `docs/feature-seguridad-jwt-firebase.md`
**Fecha:** 2026-03-11

---

## Resumen de Cambios

| Tipo | Archivo | Acción |
|------|---------|--------|
| Modificar | `infrastructure/web/config/SecurityConfig.java` | Reconfigurar Spring Security + CORS |
| Crear | `infrastructure/web/security/FirebaseTokenFilter.java` | Filtro de validación del token |

**Sin cambios en:** modelo de datos, DTOs, servicios, repositorios, controladores, pom.xml.

---

## Pasos de Ejecución

### Paso 1 — Crear `FirebaseTokenFilter`

Crear el filtro que intercepta cada request y valida el Firebase ID Token.

**Archivo:** `src/main/java/uan/edu/co/crazy_bakery/infrastructure/web/security/FirebaseTokenFilter.java`

**Lógica:**
1. Leer el header `Authorization`
2. Si no existe o no empieza con `Bearer ` → retornar `401`
3. Extraer el token (quitar el prefijo `Bearer `)
4. Llamar a `FirebaseAuth.getInstance().verifyIdToken(token)`
5. Si falla (token inválido, expirado, malformado) → retornar `401`
6. Si es válido → continuar con la cadena de filtros (`chain.doFilter`)

**Clase base:** `OncePerRequestFilter` de Spring

---

### Paso 2 — Modificar `SecurityConfig`

Reemplazar la configuración actual por una que:

1. Registre `FirebaseTokenFilter` antes del filtro de autenticación de Spring Security
2. Defina `POST /usuarios` como único endpoint público
3. Exija autenticación en todos los demás endpoints
4. Configure CORS con los orígenes permitidos:
   - `https://uan-crazy-bakery-ui--uan-especializacion.us-central1.hosted.app`
   - `http://localhost:9002`
5. Mantenga CSRF deshabilitado (API REST stateless)
6. Configure la sesión como `STATELESS`

---

### Paso 3 — Verificación Manual

Pruebas a realizar después de la implementación:

| Caso | Request | Resultado Esperado |
|------|---------|-------------------|
| Crear usuario sin token | `POST /usuarios` | `201 Created` |
| Listar usuarios sin token | `GET /usuarios` | `401 Unauthorized` |
| Listar usuarios con token inválido | `GET /usuarios` + `Authorization: Bearer token_falso` | `401 Unauthorized` |
| Listar usuarios con token válido | `GET /usuarios` + `Authorization: Bearer <token_real>` | `200 OK` |
| Preflight CORS | `OPTIONS /usuarios` | `200 OK` sin bloqueo |

---

## Orden de Implementación

```
1. FirebaseTokenFilter.java   (nuevo)
         ↓
2. SecurityConfig.java        (modificar)
         ↓
3. Verificación manual
```

---

## Criterios para dar por terminada la feature

- [ ] `POST /usuarios` responde sin token
- [ ] Endpoints protegidos retornan `401` sin token
- [ ] Endpoints protegidos retornan `401` con token inválido o expirado
- [ ] Endpoints protegidos responden correctamente con token válido de Firebase
- [ ] Preflight `OPTIONS` no es bloqueado
- [ ] Frontend en `localhost:9002` y en producción puede enviar el header `Authorization` sin errores CORS
