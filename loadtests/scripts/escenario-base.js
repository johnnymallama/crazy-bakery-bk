/**
 * Escenario Base — Operación normal documentada en el Capacity Sizing.
 *
 * Parámetros (Tabla 2 y Tabla 4 del documento):
 *   - Usuarios concurrentes: 10 VUs
 *   - Throughput objetivo: 2.6 hits/seg
 *   - Tiempo de respuesta SLA: < 3 segundos (p95)
 *
 * Flujo simulado: Home (últimas imágenes) → Departamentos → Tamaños → Catálogo tortas
 * Duración: 5 min ramp-up + 10 min sostenido + 2 min ramp-down
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { BASE_URL, FIREBASE_TOKEN, authHeaders, publicHeaders, THRESHOLDS } from './config.js';

const duracionHome = new Trend('duracion_home_ms');
const duracionCatalogo = new Trend('duracion_catalogo_ms');
const duracionGeografia = new Trend('duracion_geografia_ms');
const duracionTamanos = new Trend('duracion_tamanos_ms');
const erroresAuth = new Counter('errores_auth_401');

export const options = {
  stages: [
    { duration: '2m', target: 10 },  // ramp-up gradual a 10 VUs
    { duration: '8m', target: 10 },  // carga sostenida
    { duration: '1m', target: 0 },   // ramp-down
  ],
  thresholds: {
    ...THRESHOLDS,
    duracion_home_ms: ['p(95)<3000'],
    duracion_catalogo_ms: ['p(95)<3000'],
    duracion_geografia_ms: ['p(95)<2000'],
    duracion_tamanos_ms: ['p(95)<2000'],
  },
};

export default function () {
  // Paso 1: Home — últimas recetas/imágenes (endpoint público)
  group('home', () => {
    const res = http.get(`${BASE_URL}/receta/ultimas-imagenes`, { headers: publicHeaders() });
    duracionHome.add(res.timings.duration);
    check(res, {
      'home: status 200': (r) => r.status === 200,
      'home: tiempo < 3s': (r) => r.timings.duration < 3000,
    });
  });

  sleep(2);

  // Paso 2: Selección de departamento para checkout (público)
  group('geografia', () => {
    const res = http.get(`${BASE_URL}/geografia/departamentos`, { headers: publicHeaders() });
    duracionGeografia.add(res.timings.duration);
    check(res, {
      'departamentos: status 200': (r) => r.status === 200,
    });
  });

  sleep(1);

  // Paso 3: Tamaños disponibles por tipo de torta (público)
  group('tamanos', () => {
    const res = http.get(`${BASE_URL}/tamanos/tipo-receta/TORTA`, { headers: publicHeaders() });
    duracionTamanos.add(res.timings.duration);
    check(res, {
      'tamanos: status 200': (r) => r.status === 200,
    });
  });

  // Paso 4: Ingredientes compatibles — flujo del wizard de personalización (público)
  group('ingredientes_wizard', () => {
    const tipos = ['BIZCOCHO', 'RELLENO', 'CUBERTURA'];
    const tipo = tipos[Math.floor(Math.random() * tipos.length)];
    const res = http.get(
      `${BASE_URL}/ingredientes/search?tipoReceta=TORTA&tamanoId=8&tipoIngrediente=${tipo}`,
      { headers: publicHeaders() }
    );
    check(res, { 'ingredientes/search: status 200': (r) => r.status === 200 });
  });

  sleep(2);

  // Paso 5: Catálogo de tortas — requiere autenticación
  if (FIREBASE_TOKEN) {
    group('catalogo_tortas', () => {
      const res = http.get(`${BASE_URL}/torta`, { headers: authHeaders() });
      duracionCatalogo.add(res.timings.duration);
      if (res.status === 401) erroresAuth.add(1);
      check(res, {
        'torta: status 200': (r) => r.status === 200,
        'torta: tiempo < 3s': (r) => r.timings.duration < 3000,
      });
    });

    sleep(1);

    group('tamanos_admin', () => {
      const res = http.get(`${BASE_URL}/tamanos`, { headers: authHeaders() });
      if (res.status === 401) erroresAuth.add(1);
      check(res, { 'tamanos admin: status 200': (r) => r.status === 200 });
    });
  }

  // Pausa entre iteraciones para simular tiempo de lectura del usuario
  sleep(3);
}
