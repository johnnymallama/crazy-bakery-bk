/**
 * Escenario Base IA — Generación de imágenes custom-cake (GPT Image 1.5)
 *
 * Contexto del documento (Capacity Sizing, pág. 7):
 *   "Si diez usuarios concurrentes están simultáneamente en el paso de
 *    personalización y cada uno regenera dos o tres propuestas visuales,
 *    se requieren entre 25 y 30 llamadas concurrentes a la API de OpenAI."
 *
 * Este escenario simula el caso base: 10 VUs, cada uno generando 1 imagen.
 * Latencia esperada por imagen: 5–15 segundos (operación bloqueante en OpenAI).
 * SLA para este endpoint: < 30 segundos p95 (SLA independiente del resto de la API).
 *
 * NOTA DE COSTO: cada ejecución consume ~10 llamadas a GPT Image 1.5.
 * Ejecutar solo con presupuesto disponible en OpenAI.
 */
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';
import { BASE_URL, publicHeaders } from './config.js';

const duracionGeneracion = new Trend('duracion_ia_generacion_ms');
const tasaExitoIA = new Rate('tasa_exito_ia');
const imagenesGeneradas = new Counter('imagenes_generadas_total');
const erroresIA = new Counter('errores_ia_total');

export const options = {
  stages: [
    { duration: '1m', target: 5 },   // ramp-up suave (IA es costosa)
    { duration: '3m', target: 10 },  // carga sostenida base: 10 VUs
    { duration: '1m', target: 0 },   // ramp-down
  ],
  thresholds: {
    // SLA independiente: la IA puede tomar hasta 30s — distinto al resto de la API
    duracion_ia_generacion_ms: ['p(95)<30000', 'p(99)<45000'],
    http_req_failed:           ['rate<0.05'],   // hasta 5% tolerado (rate limits OpenAI)
    tasa_exito_ia:             ['rate>0.95'],
  },
};

// Variedad de payloads para simular diferentes usuarios del wizard
const PAYLOADS = [
  {
    tipoReceta: 'TORTA',
    tamano: 'Pequeña',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Esencia de vainilla',   composicion: 'Esencia artificial sabor vainilla' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Ganache de chocolate',  composicion: 'Mezcla de chocolate y crema de leche' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Azúcar pulverizada',    composicion: 'Azúcar glass refinada' },
    ],
    detalle: 'Decoración minimalista con flores de azúcar para cumpleaños',
  },
  {
    tipoReceta: 'TORTA',
    tamano: 'Mediana',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Chocolate oscuro',      composicion: 'Bizcocho húmedo de cacao 70%' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Mermelada de fresa',    composicion: 'Mermelada artesanal de fresa con trozos' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Buttercream',           composicion: 'Crema de mantequilla con colorante rosado' },
    ],
    detalle: 'Estilo romántico para aniversario con rosas y corazones',
  },
  {
    tipoReceta: 'TORTA',
    tamano: 'Grande',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Zanahoria',             composicion: 'Bizcocho esponjoso con zanahoria rallada y canela' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Cream cheese',          composicion: 'Crema de queso Philadelphia con vainilla' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Fondant blanco',        composicion: 'Fondant liso color marfil con textura satinada' },
    ],
    detalle: 'Temática de boda elegante, decoración floral en blanco y dorado',
  },
  {
    tipoReceta: 'TORTA',
    tamano: 'Pequeña',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Red velvet',            composicion: 'Bizcocho rojo aterciopelado con colorante natural' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Dulce de leche',        composicion: 'Arequipe colombiano artesanal' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Merengue italiano',     composicion: 'Merengue firme con picos decorativos' },
    ],
    detalle: 'Graduación universitaria con colores azul y dorado',
  },
  {
    tipoReceta: 'TORTA',
    tamano: 'Mediana',
    ingredientes: [
      { tipoIngrediente: 'BIZCOCHO',  nombre: 'Limón',                 composicion: 'Bizcocho cítrico con ralladura de limón Meyer' },
      { tipoIngrediente: 'RELLENO',   nombre: 'Lemon curd',            composicion: 'Crema ácida de limón con mantequilla y huevo' },
      { tipoIngrediente: 'CUBERTURA', nombre: 'Glaseado espejo',       composicion: 'Glaseado brillante color amarillo pálido' },
    ],
    detalle: 'Diseño moderno geométrico para cumpleaños adulto',
  },
];

export default function () {
  // Cada VU selecciona un payload distinto según su índice para simular usuarios diferentes
  const payload = PAYLOADS[(__VU - 1) % PAYLOADS.length];

  group('generacion_imagen_ia', () => {
    const res = http.post(
      `${BASE_URL}/generate-image/custom-cake`,
      JSON.stringify(payload),
      {
        headers: publicHeaders(),
        timeout: '60s',  // timeout generoso para la IA
      }
    );

    duracionGeneracion.add(res.timings.duration);

    const exito = res.status === 200;
    tasaExitoIA.add(exito);

    if (exito) {
      imagenesGeneradas.add(1);
    } else {
      erroresIA.add(1);
    }

    check(res, {
      'IA: status 200':          (r) => r.status === 200,
      'IA: tiene imageUrl':      (r) => { try { return !!r.json().imageUrl; } catch { return false; } },
      'IA: tiene prompt':        (r) => { try { return !!r.json().prompt; } catch { return false; } },
      'IA: tiempo < 30s':        (r) => r.timings.duration < 30000,
    });
  });

  // Pausa entre generaciones — simula tiempo del usuario revisando la imagen
  sleep(5);
}
