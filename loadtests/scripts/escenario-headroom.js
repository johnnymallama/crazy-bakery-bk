/**
 * Escenario con Headroom 25% — Configuración recomendada para producción.
 *
 * Parámetros (Tabla 4 y Tabla 6 del documento):
 *   - Usuarios concurrentes objetivo: 14 VUs (10 × 1.25)
 *   - Throughput objetivo: 3.5 hits/seg
 *   - Tiempo de respuesta SLA: < 3 segundos (p95)
 *
 * Nota: POST /orden se valida en test-orden-funcional.js (test dedicado de corta
 * duración) porque requiere un token Firebase válido durante toda la ejecución.
 * Este escenario cubre el flujo de navegación y consulta que representa
 * la carga real sostenida del sistema.
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { BASE_URL, FIREBASE_TOKEN, authHeaders, publicHeaders, THRESHOLDS } from './config.js';

const duracionCatalogo = new Trend('duracion_catalogo_ms');
const duracionOrdenes = new Trend('duracion_ordenes_ms');
const tasaErrores = new Rate('tasa_errores_general');

export const options = {
  stages: [
    { duration: '2m', target: 7 },   // ramp-up primera mitad
    { duration: '1m', target: 14 },  // ramp-up a 14 VUs
    { duration: '8m', target: 14 },  // carga sostenida al nivel headroom
    { duration: '2m', target: 0 },   // ramp-down
  ],
  thresholds: {
    ...THRESHOLDS,
    duracion_catalogo_ms: ['p(95)<3000'],
    duracion_ordenes_ms: ['p(95)<3000'],
    tasa_errores_general: ['rate<0.01'],
  },
};

export default function () {
  // Flujo 1: navegación pública (sin token)
  group('navegacion_publica', () => {
    let res = http.get(`${BASE_URL}/receta/ultimas-imagenes`, { headers: publicHeaders() });
    tasaErrores.add(res.status !== 200);
    check(res, { 'home: 200': (r) => r.status === 200 });

    sleep(1);

    res = http.get(`${BASE_URL}/geografia/departamentos`, { headers: publicHeaders() });
    tasaErrores.add(res.status !== 200);
    check(res, { 'departamentos: 200': (r) => r.status === 200 });

    sleep(1);

    res = http.get(`${BASE_URL}/tamanos/tipo-receta/TORTA`, { headers: publicHeaders() });
    tasaErrores.add(res.status !== 200);
    check(res, { 'tamanos: 200': (r) => r.status === 200 });

    sleep(1);

    res = http.get(
      `${BASE_URL}/ingredientes/search?tipoReceta=TORTA&tamanoId=8&tipoIngrediente=BIZCOCHO`,
      { headers: publicHeaders() }
    );
    tasaErrores.add(res.status !== 200);
    check(res, { 'ingredientes/search: 200': (r) => r.status === 200 });
  });

  sleep(2);

  if (!FIREBASE_TOKEN) {
    sleep(4);
    return;
  }

  // Flujo 2: usuario autenticado — catálogo y consulta de órdenes
  group('catalogo_autenticado', () => {
    let res = http.get(`${BASE_URL}/torta`, { headers: authHeaders() });
    duracionCatalogo.add(res.timings.duration);
    tasaErrores.add(res.status !== 200);
    check(res, {
      'torta catalogo: 200': (r) => r.status === 200,
      'torta catalogo: < 3s': (r) => r.timings.duration < 3000,
    });

    sleep(1);

    res = http.get(`${BASE_URL}/tamanos`, { headers: authHeaders() });
    tasaErrores.add(res.status !== 200);
    check(res, { 'tamanos admin: 200': (r) => r.status === 200 });
  });

  sleep(2);

  // Flujo 3: consulta de órdenes existentes (carga típica de usuario autenticado)
  group('consulta_ordenes', () => {
    const res = http.get(`${BASE_URL}/orden`, { headers: authHeaders() });
    duracionOrdenes.add(res.timings.duration);
    tasaErrores.add(res.status !== 200);
    check(res, {
      'ordenes: 200': (r) => r.status === 200,
      'ordenes: < 3s': (r) => r.timings.duration < 3000,
    });
  });

  sleep(3);
}
