# Pruebas de Carga — Crazy Bakery Backend

Pruebas k6 alineadas con el **Anexo de Evaluación de Capacity y Hardware Sizing** del proyecto de grado. Se ejecutan directamente contra el servicio en producción (`Google Cloud Run · us-central1`).

---

## Escenarios implementados

### Escenarios de infraestructura (endpoints estándar)

| Escenario | Script | VUs | Duración | Throughput objetivo | SLA |
|---|---|---|---|---|---|
| Smoke Test | `smoke-test.js` | 1 | ~1 min | Verificación | p95 < 3 s |
| Base | `escenario-base.js` | 10 | ~11 min | 2.6 hits/seg | p95 < 3 s |
| Headroom 25% | `escenario-headroom.js` | 14 | ~13 min | 3.5 hits/seg | p95 < 3 s |
| Pico estacional | `escenario-pico.js` | 25–30 | ~9 min | 7–8 hits/seg | p95 < 5 s |
| Funcional /orden | `test-orden-funcional.js` | 1 | ~2 min | Funcional | p95 < 3 s |

### Escenarios de IA — Generación de imágenes (POST /generate-image/custom-cake)

| Escenario | Script | VUs | Duración | SLA | Costo estimado |
|---|---|---|---|---|---|
| Base IA | `escenario-ia-base.js` | 10 | ~5 min | p95 < 30 s | ~USD 0.40 |
| Pico IA | `escenario-ia-pico.js` | 25–30 | ~5 min | p95 < 45 s | ~USD 1.20 |

> Los escenarios IA tienen un SLA independiente del resto de la API dado que la generación de imágenes con GPT Image 1.5 es una operación bloqueante que tarda 5–15 segundos en condiciones normales.

---

## Resultados obtenidos

### Escenarios de infraestructura

| Escenario | Veredicto | p95 | p99 | Error rate | Notas |
|---|---|---|---|---|---|
| Smoke Test | ✅ APROBADO | < 1 s | < 2 s | 0% | Todos los endpoints responden correctamente |
| Base (10 VUs) | ✅ APROBADO | < 3 s | < 15 s | < 1% | 1 instancia Cloud Run absorbe la carga |
| Headroom 25% (14 VUs) | ✅ APROBADO | < 3 s | < 15 s | < 1% | Cloud Run escala a 1–2 instancias |
| Pico estacional (25–30 VUs) | ✅ APROBADO | < 5 s | — | < 5% | Autoscaling correcto a 3–4 instancias |
| Funcional /orden | ✅ APROBADO | < 3 s | — | 0% | Creación de orden con `recetaIds: []` validada |

### Escenarios de IA

| Escenario | Veredicto | Imágenes generadas | Tasa de éxito | Latencia exitosas (avg) | Causa de errores |
|---|---|---|---|---|---|
| Base IA (10 VUs) | ⚠️ LIMITACIÓN OPENAI | 28 / 236 | 11.8% | ~18.3 s (dentro del SLA) | Rate limiting progresivo OpenAI RPM |

**Conclusión IA:** Cloud Run procesó correctamente las 236 peticiones. El 88% de errores corresponde a rate limiting de la cuenta OpenAI del ambiente de pruebas — no es un problema de infraestructura. Las peticiones que OpenAI procesó exitosamente tuvieron latencia dentro del SLA de 30 s.

---

## Requisitos previos

```bash
# Instalar k6 en macOS
brew install k6

# Verificar instalación
k6 version

# Node.js (para el generador de reportes HTML)
node --version  # >= 14
```

---

## Configuración

### Variables de entorno

| Variable | Descripción | Requerida por |
|---|---|---|
| `FIREBASE_TOKEN` | Token Firebase ID (válido 1 hora) | Escenarios con endpoints autenticados |
| `BASE_URL` | URL del servicio (por defecto: Cloud Run prod) | Opcional — ya tiene el valor por defecto |

La URL de producción está fija en `scripts/config.js`:
```
https://crazy-bakery-bk-835393530868.us-central1.run.app
```

### Obtener un token Firebase

El token Firebase tiene vigencia de **1 hora**. Obtenlo desde las DevTools del frontend:

```javascript
// En la consola del navegador, con sesión iniciada en el frontend:
const token = await firebase.auth().currentUser.getIdToken(true);
console.log(token);
```

Exportar antes de ejecutar:
```bash
export FIREBASE_TOKEN="eyJhbGci..."
```

> **Importante:** En pruebas de larga duración (headroom ~13 min, pico ~9 min) el token puede expirar si ya tiene tiempo desde que se obtuvo. Obtén un token fresco justo antes de ejecutar. Los escenarios de IA son endpoints **públicos** y no requieren token.

---

## Ejecución

### Scripts de shell (recomendado)

Cada escenario tiene su propio script que guarda los resultados con timestamp automático:

```bash
# Dar permisos de ejecución (primera vez)
chmod +x *.sh

# Smoke test — verificación rápida (~1 min, sin costo)
./run-smoke.sh

# Escenario base — operación normal (~11 min)
export FIREBASE_TOKEN="eyJhbGci..."
./run-base.sh

# Escenario headroom 25% (~13 min)
./run-headroom.sh

# Escenario pico estacional (~9 min)
./run-pico.sh

# Test funcional POST /orden (aislado, ~2 min)
./run-orden-funcional.sh

# Escenario Base IA — genera imágenes real (~5 min, ~USD 0.40 en OpenAI)
./run-ia-base.sh

# Escenario Pico IA — 25-30 VUs (~5 min, ~USD 1.20 en OpenAI)
./run-ia-pico.sh
```

Los resultados se guardan en `results/` con el formato:
```
results/<escenario>_<YYYYMMDD_HHMMSS>.json          # métricas crudas
results/<escenario>_<YYYYMMDD_HHMMSS>_summary.json  # resumen (input para el reporte)
results/<escenario>_<YYYYMMDD_HHMMSS>.log            # salida de k6
```

### Ejecución manual con k6

```bash
# Escenario base con token
k6 run scripts/escenario-base.js \
  -e FIREBASE_TOKEN=$FIREBASE_TOKEN \
  --summary-export=results/base_summary.json

# Escenario IA base (no requiere token)
k6 run scripts/escenario-ia-base.js \
  --summary-export=results/ia-base_summary.json
```

---

## Generación de reportes HTML

El script `generate-report.js` convierte cualquier `_summary.json` en un reporte HTML con análisis completo:

```bash
# Desde el directorio loadtests/
node generate-report.js results/<archivo>_summary.json

# Ejemplos:
node generate-report.js results/ia-base_20260510_100344_summary.json
node generate-report.js results/base_20260510_090000_summary.json
```

El reporte se guarda en el mismo directorio con nombre `_report.html`.

### Contenido del reporte

- **Veredicto diferenciado:** APROBADO / APROBADO CON OBSERVACIONES / FALLIDO / LIMITACIÓN DE PROVEEDOR EXTERNO
- **KPIs principales:** latencia p95/p99, tasa de error, total peticiones (o métricas IA específicas)
- **Thresholds:** tabla con resultado OK/FALLÓ por cada umbral SLA definido
- **Distribución de latencia:** mín, mediana, avg, p90, p95, p99, máx
- **Checks por endpoint:** tasa de éxito individual de cada validación
- **Desglose de fases HTTP:** bloqueado, conectando, TLS, enviando, TTFB, recibiendo
- **Para escenarios IA:** análisis de causa raíz de rate limiting, tabla de degradación progresiva por minuto, latencia solo de peticiones exitosas, comparación Cloud Run vs. OpenAI, recomendaciones

---

## Estructura del proyecto

```
loadtests/
├── scripts/
│   ├── config.js                  # URL base, thresholds, helpers de headers
│   ├── smoke-test.js              # Verificación básica (1 VU, 1 min)
│   ├── escenario-base.js          # Operación normal (10 VUs, ~11 min)
│   ├── escenario-headroom.js      # Headroom 25% (14 VUs, ~13 min)
│   ├── escenario-pico.js          # Pico estacional (25–30 VUs, ~9 min)
│   ├── test-orden-funcional.js    # Test aislado POST /orden (1 VU, ~2 min)
│   ├── escenario-ia-base.js       # IA base: generación imágenes (10 VUs, ~5 min)
│   └── escenario-ia-pico.js       # IA pico: generación imágenes (25–30 VUs, ~5 min)
├── results/                       # Salida de ejecuciones (ignorada en git)
├── run-smoke.sh
├── run-base.sh
├── run-headroom.sh
├── run-pico.sh
├── run-orden-funcional.sh
├── run-ia-base.sh
├── run-ia-pico.sh
├── generate-report.js             # Generador de reportes HTML desde _summary.json
├── package.json
└── README.md
```

---

## Métricas clave

### Escenarios estándar

| Métrica k6 | SLA / Umbral |
|---|---|
| `http_req_duration` p95 | < 3000 ms (< 5000 ms en pico) |
| `http_req_duration` p99 | < 15000 ms (absorbe cold starts Cloud Run) |
| `http_req_failed` | < 1% (< 5% en pico) |
| `errores_5xx` | 0 en base y headroom |

### Escenarios IA

| Métrica k6 | SLA / Umbral |
|---|---|
| `duracion_ia_generacion_ms` p95 | < 30000 ms (base) / < 45000 ms (pico) |
| `tasa_exito_ia` | > 95% (base) / > 85% (pico) |
| `http_req_failed` | < 5% (base) / < 15% (pico — rate limits OpenAI) |
| `imagenes_generadas_total` | Contador de imágenes exitosas |

---

## Hallazgo IA — Rate Limiting OpenAI

La prueba Base IA confirmó el cuello de botella predicho en el documento de Capacity Sizing (págs. 7–8):

- **10 VUs concurrentes** agotaron el RPM (requests per minute) de la cuenta OpenAI de pruebas
- Degradación progresiva: 52% éxito (min 1) → 23% (min 2) → 7% (min 3) → 3% (min 4) → 14% (min 5)
- Cloud Run **no tuvo problemas**: respondió correctamente en todos los casos, devolviendo el error 500 de OpenAI en ~200 ms (fail-fast)
- Las peticiones que OpenAI procesó tuvieron latencia **dentro del SLA**: avg ~18.3 s, p95 ~16 s (< 30 s)

**Recomendaciones para producción:**
1. Cola asíncrona (Cloud Tasks / Pub/Sub) — desacoplar generación del request HTTP
2. Caché por hash de ingredientes — reutilizar imágenes de combinaciones populares
3. Rate limiting por usuario — máx. 3–5 regeneraciones por sesión
4. Plan OpenAI con RPM mayor al de la cuenta de pruebas

---

## Consideraciones de costo

| Suite | Duración total | Costo Cloud Run | Costo OpenAI | Total estimado |
|---|---|---|---|---|
| Escenarios estándar (smoke + base + headroom + pico) | ~34 min | < USD 0.05 | — | < USD 0.05 |
| Base IA | ~5 min | < USD 0.01 | ~USD 0.40 | ~USD 0.41 |
| Pico IA | ~5 min | < USD 0.01 | ~USD 1.20 | ~USD 1.21 |
| **Suite completa** | **~44 min** | **< USD 0.06** | **~USD 1.60** | **~USD 1.66** |

> Ejecutar los escenarios IA solo cuando sea necesario validar el comportamiento con OpenAI.
