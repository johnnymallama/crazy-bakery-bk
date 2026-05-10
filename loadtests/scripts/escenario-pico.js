/**
 * Escenario de Pico Estacional — Fechas comerciales clave (Día de la Madre,
 * San Valentín, Black Friday, Navidad).
 *
 * Parámetros (Tabla 4 del documento):
 *   - Usuarios concurrentes: 25 a 30 VUs
 *   - Throughput objetivo: 7 a 8 hits/seg
 *   - Cloud Run debería escalar automáticamente a 3-4 instancias activas
 *
 * Este escenario valida que:
 *   1. El autoscaling de Cloud Run reacciona correctamente.
 *   2. Los tiempos de respuesta se mantienen dentro del SLA incluso en pico.
 *   3. No hay errores por agotamiento de conexiones en Cloud SQL.
 *
 * Duración: ramp-up agresivo + 5 min en pico + ramp-down
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { BASE_URL, FIREBASE_TOKEN, authHeaders, publicHeaders, THRESHOLDS } from './config.js';

const duracionP99 = new Trend('duracion_p99_pico_ms');
const errores5xx = new Counter('errores_5xx');
const tasaErrores = new Rate('tasa_errores_pico');

export const options = {
  stages: [
    { duration: '1m', target: 15 },  // ramp-up inicial
    { duration: '1m', target: 25 },  // ramp-up a pico mínimo
    { duration: '2m', target: 30 },  // pico máximo estacional
    { duration: '3m', target: 30 },  // sostenido en pico
    { duration: '1m', target: 15 },  // ramp-down parcial
    { duration: '1m', target: 0 },   // ramp-down completo
  ],
  thresholds: {
    // En pico estacional se permite una degradación moderada del SLA
    http_req_duration: ['p(95)<5000', 'p(99)<8000'],
    http_req_failed: ['rate<0.05'],  // hasta 5% errores aceptable en pico
    errores_5xx: ['count<10'],
    tasa_errores_pico: ['rate<0.05'],
  },
};

export default function () {
  // Flujo comprimido de navegación pública (simula tráfico de pico)
  group('flujo_pico_publico', () => {
    let res = http.get(`${BASE_URL}/receta/ultimas-imagenes`, { headers: publicHeaders() });
    duracionP99.add(res.timings.duration);
    tasaErrores.add(res.status >= 400);
    if (res.status >= 500) errores5xx.add(1);
    check(res, {
      'home pico: no error 5xx': (r) => r.status < 500,
      'home pico: < 5s': (r) => r.timings.duration < 5000,
    });

    sleep(0.5);

    res = http.get(`${BASE_URL}/geografia/departamentos`, { headers: publicHeaders() });
    tasaErrores.add(res.status >= 400);
    if (res.status >= 500) errores5xx.add(1);
    check(res, { 'departamentos pico: no 5xx': (r) => r.status < 500 });

    sleep(0.5);

    res = http.get(`${BASE_URL}/tamanos/tipo-receta/TORTA`, { headers: publicHeaders() });
    duracionP99.add(res.timings.duration);
    tasaErrores.add(res.status >= 400);
    if (res.status >= 500) errores5xx.add(1);
    check(res, { 'tamanos pico: no 5xx': (r) => r.status < 500 });

    res = http.get(
      `${BASE_URL}/ingredientes/search?tipoReceta=TORTA&tamanoId=8&tipoIngrediente=RELLENO`,
      { headers: publicHeaders() }
    );
    tasaErrores.add(res.status >= 400);
    if (res.status >= 500) errores5xx.add(1);
    check(res, { 'ingredientes/search pico: no 5xx': (r) => r.status < 500 });
  });

  sleep(1);

  if (!FIREBASE_TOKEN) {
    sleep(2);
    return;
  }

  // Flujo autenticado en pico (30% de VUs simulan usuarios con sesión activa)
  if (Math.random() < 0.3) {
    group('flujo_pico_autenticado', () => {
      let res = http.get(`${BASE_URL}/torta`, { headers: authHeaders() });
      duracionP99.add(res.timings.duration);
      tasaErrores.add(res.status >= 400);
      if (res.status >= 500) errores5xx.add(1);
      check(res, {
        'torta pico: no 5xx': (r) => r.status < 500,
        'torta pico: < 5s': (r) => r.timings.duration < 5000,
      });

      sleep(1);

      // Consulta de órdenes existentes (carga típica en pico de fechas)
      res = http.get(`${BASE_URL}/orden`, { headers: authHeaders() });
      tasaErrores.add(res.status >= 400);
      if (res.status >= 500) errores5xx.add(1);
      check(res, { 'ordenes pico: no 5xx': (r) => r.status < 500 });
    });
  }

  sleep(2);
}
