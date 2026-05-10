/**
 * Smoke Test — Verificación básica antes de pruebas de carga reales.
 * Objetivo: confirmar que Cloud Run responde y los endpoints públicos están activos.
 * Escenario: 1 VU durante 1 minuto.
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, FIREBASE_TOKEN, authHeaders, publicHeaders, THRESHOLDS } from './config.js';

export const options = {
  vus: 1,
  duration: '1m',
  thresholds: THRESHOLDS,
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

export default function () {
  // --- Endpoints públicos (sin autenticación) ---

  let res = http.get(`${BASE_URL}/receta/ultimas-imagenes`, { headers: publicHeaders() });
  check(res, {
    'ultimas-imagenes: status 200': (r) => r.status === 200,
    'ultimas-imagenes: tiempo < 3s': (r) => r.timings.duration < 3000,
  });

  sleep(1);

  res = http.get(`${BASE_URL}/geografia/departamentos`, { headers: publicHeaders() });
  check(res, {
    'departamentos: status 200': (r) => r.status === 200,
  });

  sleep(1);

  res = http.get(`${BASE_URL}/tamanos/tipo-receta/TORTA`, { headers: publicHeaders() });
  check(res, {
    'tamanos/tipo-receta: status 200': (r) => r.status === 200,
  });

  sleep(1);

  res = http.get(`${BASE_URL}/ingredientes/search?tipoReceta=TORTA&tamanoId=8&tipoIngrediente=BIZCOCHO`, { headers: publicHeaders() });
  check(res, {
    'ingredientes/search: status 200': (r) => r.status === 200,
  });

  // --- Endpoints protegidos (requieren FIREBASE_TOKEN) ---
  if (FIREBASE_TOKEN) {
    sleep(1);

    res = http.get(`${BASE_URL}/torta`, { headers: authHeaders() });
    check(res, {
      'torta: status 200': (r) => r.status === 200,
      'torta: tiempo < 3s': (r) => r.timings.duration < 3000,
    });

    sleep(1);

    res = http.get(`${BASE_URL}/tamanos`, { headers: authHeaders() });
    check(res, {
      'tamanos: status 200': (r) => r.status === 200,
    });
  }

  sleep(2);
}
