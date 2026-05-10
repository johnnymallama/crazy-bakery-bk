// URL base del servicio en Cloud Run
export const BASE_URL = __ENV.BASE_URL || 'https://crazy-bakery-bk-835393530868.us-central1.run.app';

// Token Firebase para endpoints protegidos (pasar via: -e FIREBASE_TOKEN=...)
export const FIREBASE_TOKEN = __ENV.FIREBASE_TOKEN || '';

export function authHeaders() {
  return {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${FIREBASE_TOKEN}`,
  };
}

export function publicHeaders() {
  return { 'Content-Type': 'application/json' };
}

// Thresholds globales basados en el SLA documentado:
// - Tiempo de respuesta objetivo: 3 segundos para páginas estándar
// - Tasa de error aceptable: < 1%
// p99 en 15s para absorber cold starts de Cloud Run (primera petición tras inactividad).
// En escenarios de carga real el p99 será mucho menor porque las instancias ya están calientes.
export const THRESHOLDS = {
  http_req_duration: ['p(95)<3000', 'p(99)<15000'],
  http_req_failed: ['rate<0.01'],
};
