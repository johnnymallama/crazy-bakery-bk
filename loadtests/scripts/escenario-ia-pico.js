/**
 * Escenario Pico IA — Generación de imágenes custom-cake en fechas comerciales
 *
 * Contexto del documento (Capacity Sizing, pág. 7–8):
 *   "Si diez usuarios concurrentes están en el paso de personalización y cada
 *    uno regenera dos o tres propuestas visuales, se requieren entre 25 y 30
 *    llamadas concurrentes a la API de OpenAI."
 *
 *   Pico estacional: 25–30 usuarios simultáneos (Día de la Madre, Navidad,
 *   San Valentín, Black Friday). Throughput objetivo: 7–8 hits/seg.
 *
 * Este escenario valida:
 *   1. Que Cloud Run no colapsa con múltiples hilos bloqueados en OpenAI.
 *   2. Que el rate limit de OpenAI no genera errores masivos.
 *   3. Que el p95 se mantiene por debajo del SLA extendido de 45 segundos.
 *
 * NOTA DE COSTO: cada ejecución consume ~25–30 llamadas a GPT Image 1.5.
 * Costo estimado: USD 1.00–1.20 por ejecución completa.
 * Ejecutar solo cuando sea necesario validar el escenario de pico.
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { BASE_URL, publicHeaders } from './config.js';

const duracionGeneracion = new Trend('duracion_ia_pico_ms');
const tasaExitoIA = new Rate('tasa_exito_ia_pico');
const imagenesGeneradas = new Counter('imagenes_generadas_pico');
const erroresRateLimit = new Counter('errores_rate_limit_openai');
const errores5xx = new Counter('errores_5xx_ia');

export const options = {
  stages: [
    { duration: '1m', target: 10 },  // ramp-up inicial
    { duration: '1m', target: 20 },  // ramp-up a pico intermedio
    { duration: '2m', target: 30 },  // pico máximo estacional
    { duration: '1m', target: 0 },   // ramp-down
  ],
  thresholds: {
    // En pico la IA puede tardar más por colas en OpenAI
    duracion_ia_pico_ms: ['p(95)<45000', 'p(99)<60000'],
    http_req_failed:     ['rate<0.15'],   // hasta 15% tolerado (rate limits OpenAI en pico)
    tasa_exito_ia_pico:  ['rate>0.85'],   // 85% éxito mínimo en pico
    errores_5xx_ia:      ['count<10'],    // errores de servidor (no rate limit)
  },
};

// Pool ampliado de payloads para mayor variedad en el pico
const PAYLOADS = [
  {
    tipoReceta: 'TORTA', tamano: 'Pequeña',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Esencia de vainilla',  composicion: 'Esencia artificial sabor vainilla' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Ganache de chocolate', composicion: 'Mezcla de chocolate y crema de leche' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Azúcar pulverizada',   composicion: 'Azúcar glass refinada' },
    ],
    detalle: 'Cumpleaños infantil con velas de colores',
  },
  {
    tipoReceta: 'TORTA', tamano: 'Grande',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Chocolate oscuro',    composicion: 'Bizcocho húmedo de cacao 70%' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Mermelada de fresa',  composicion: 'Mermelada artesanal de fresa con trozos' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Buttercream rosado',  composicion: 'Crema de mantequilla con colorante rosado' },
    ],
    detalle: 'Día de la Madre — flores naturales comestibles y lazo dorado',
  },
  {
    tipoReceta: 'TORTA', tamano: 'Mediana',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Red velvet',          composicion: 'Bizcocho rojo aterciopelado' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Cream cheese',        composicion: 'Crema de queso Philadelphia con vainilla' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Fondant rojo',        composicion: 'Fondant liso color rojo intenso' },
    ],
    detalle: 'San Valentín — corazones y rosas en fondant',
  },
  {
    tipoReceta: 'TORTA', tamano: 'Grande',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Zanahoria',           composicion: 'Bizcocho esponjoso con zanahoria y canela' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Dulce de leche',      composicion: 'Arequipe colombiano artesanal' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Fondant blanco',      composicion: 'Fondant liso color marfil' },
    ],
    detalle: 'Navidad — pinos nevados y estrellas doradas en fondant',
  },
  {
    tipoReceta: 'TORTA', tamano: 'Pequeña',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Limón',               composicion: 'Bizcocho cítrico con ralladura de limón Meyer' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Lemon curd',          composicion: 'Crema ácida de limón con mantequilla' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Merengue italiano',   composicion: 'Merengue firme con picos decorativos' },
    ],
    detalle: 'Halloween — decoración de fantasmas y calabazas en merengue',
  },
  {
    tipoReceta: 'TORTA', tamano: 'Mediana',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Café espresso',       composicion: 'Bizcocho con extracto de café concentrado' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Tiramisú',            composicion: 'Crema de mascarpone con cacao y café' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Glaseado espejo',     composicion: 'Glaseado brillante color marrón oscuro' },
    ],
    detalle: 'Black Friday — diseño moderno monocromático',
  },
];

export default function () {
  const payload = PAYLOADS[(__VU - 1) % PAYLOADS.length];

  group('generacion_imagen_ia_pico', () => {
    const res = http.post(
      `${BASE_URL}/generate-image/custom-cake`,
      JSON.stringify(payload),
      {
        headers: publicHeaders(),
        timeout: '90s',  // timeout extendido para pico con posibles colas en OpenAI
      }
    );

    duracionGeneracion.add(res.timings.duration);

    const exito = res.status === 200;
    tasaExitoIA.add(exito);

    if (exito) {
      imagenesGeneradas.add(1);
    } else {
      if (res.status === 429) erroresRateLimit.add(1);
      if (res.status >= 500) errores5xx.add(1);
    }

    check(res, {
      'IA pico: no error 5xx':    (r) => r.status < 500,
      'IA pico: status 200':      (r) => r.status === 200,
      'IA pico: tiene imageUrl':  (r) => { try { return !!r.json().imageUrl; } catch { return false; } },
      'IA pico: tiempo < 45s':    (r) => r.timings.duration < 45000,
    });
  });

  // Pausa entre regeneraciones — simula usuario revisando y decidiendo
  sleep(3);
}
