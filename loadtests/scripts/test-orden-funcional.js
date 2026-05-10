/**
 * Test Funcional — POST /orden
 *
 * Valida la creación de órdenes de forma aislada con 1 VU durante 2 minutos.
 * Se mantiene separado del escenario de carga porque requiere un token Firebase
 * válido durante toda la ejecución (vigencia 1 hora).
 *
 * Mide: latencia de creación, tasa de éxito y consulta de la orden creada.
 * Uso: ./run-orden-funcional.sh <FIREBASE_TOKEN>
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { BASE_URL, authHeaders, THRESHOLDS } from './config.js';

const duracionCrear = new Trend('duracion_crear_orden_ms');
const duracionConsultar = new Trend('duracion_consultar_orden_ms');
const tasaExito = new Rate('tasa_exito_orden');

export const options = {
  vus: 1,
  duration: '2m',
  thresholds: {
    duracion_crear_orden_ms: ['p(95)<3000'],
    duracion_consultar_orden_ms: ['p(95)<2000'],
    http_req_failed: ['rate<0.01'],
    tasa_exito_orden: ['rate>0.99'],
  },
};

// recetaIds vacío: válido según el dominio — la orden se crea primero
// y las recetas se asocian posteriormente vía PATCH /{id}/receta.
// Usar recetaIds con IDs fijos falla si la receta no existe en producción.
const ORDEN_PAYLOAD = JSON.stringify({
  usuarioId: __ENV.USUARIO_ID || 'vQ81bGA34pOxnijOZcT9XBThBOq1',
  recetaIds: [],
  notas: ['Prueba funcional k6 — POST /orden'],
});

export default function () {
  group('crear_orden', () => {
    const res = http.post(`${BASE_URL}/orden`, ORDEN_PAYLOAD, { headers: authHeaders() });
    duracionCrear.add(res.timings.duration);
    tasaExito.add(res.status === 200 || res.status === 201);

    check(res, {
      'orden creada: status 200 o 201': (r) => r.status === 200 || r.status === 201,
      'orden creada: < 3s':       (r) => r.timings.duration < 3000,
      'orden creada: tiene id':   (r) => {
        try { return r.json()?.id != null; } catch { return false; }
      },
    });

    // Consultar la orden recién creada
    if (res.status === 200 || res.status === 201) {
      sleep(0.5);
      const ordenId = res.json()?.id;
      if (ordenId) {
        group('consultar_orden', () => {
          const consultaRes = http.get(`${BASE_URL}/orden/${ordenId}`, { headers: authHeaders() });
          duracionConsultar.add(consultaRes.timings.duration);
          check(consultaRes, {
            'consulta orden: 200': (r) => r.status === 200,
            'consulta orden: id coincide': (r) => r.json()?.id === ordenId,
          });
        });
      }
    }
  });

  sleep(3);
}
