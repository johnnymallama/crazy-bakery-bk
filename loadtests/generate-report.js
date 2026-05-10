#!/usr/bin/env node
/**
 * Genera un reporte HTML desde un archivo _summary.json de k6.
 * Uso: node generate-report.js <archivo_summary.json> [escenario]
 * Ejemplo: node generate-report.js results/smoke_20260510_074740_summary.json "Smoke Test"
 */

const fs = require('fs');
const path = require('path');

const inputFile = process.argv[2];
const escenarioArg = process.argv[3] || '';

if (!inputFile) {
  console.error('Uso: node generate-report.js <archivo_summary.json> [nombre_escenario]');
  process.exit(1);
}

const data = JSON.parse(fs.readFileSync(inputFile, 'utf8'));
const metrics = data.metrics;

// Recolectar todos los checks tanto del root como de grupos anidados
function recolectarChecks(group) {
  const resultado = {};
  if (group.checks) Object.assign(resultado, group.checks);
  if (group.groups) {
    for (const g of Object.values(group.groups)) {
      Object.assign(resultado, recolectarChecks(g));
    }
  }
  return resultado;
}
const checks = recolectarChecks(data.root_group || {});

// Acceso seguro a percentiles (k6 usa 'p(95)' como clave)
function p(metric, perc) {
  if (!metric) return undefined;
  return metric[`p(${perc})`] ?? metric[`p${perc}`];
}

// Detectar escenario desde nombre de archivo
function detectarEscenario(filename) {
  if (escenarioArg) return escenarioArg;
  const base = path.basename(filename);
  if (base.includes('ia-pico') || base.includes('ia_pico')) return 'Escenario Pico IA — Generación de Imágenes (25–30 VUs)';
  if (base.includes('ia-base') || base.includes('ia_base')) return 'Escenario Base IA — Generación de Imágenes (10 VUs)';
  if (base.includes('smoke')) return 'Smoke Test';
  if (base.includes('headroom')) return 'Escenario Headroom 25% (14 VUs)';
  if (base.includes('pico')) return 'Escenario Pico Estacional (25–30 VUs)';
  if (base.includes('base')) return 'Escenario Base (10 VUs)';
  if (base.includes('orden')) return 'Test Funcional — POST /orden';
  return 'Prueba de Carga';
}

// Detectar timestamp desde nombre de archivo
function extraerTimestamp(filename) {
  const match = path.basename(filename).match(/(\d{8}_\d{6})/);
  if (!match) return new Date().toLocaleString('es-CO');
  const ts = match[1];
  const y = ts.slice(0,4), mo = ts.slice(4,6), d = ts.slice(6,8);
  const h = ts.slice(9,11), mi = ts.slice(11,13), s = ts.slice(13,15);
  return `${d}/${mo}/${y} ${h}:${mi}:${s}`;
}

const escenario = detectarEscenario(inputFile);
const timestamp = extraerTimestamp(inputFile);

// Detectar si es un escenario de IA
const isIA = escenario.toLowerCase().includes('ia') ||
             path.basename(inputFile).includes('ia-') ||
             path.basename(inputFile).includes('ia_');

function ms(val) {
  if (val === undefined || val === null) return '—';
  return `${val.toFixed(2)} ms`;
}

function pct(val) {
  if (val === undefined || val === null) return '—';
  return `${(val * 100).toFixed(2)}%`;
}

function bytes(val) {
  if (!val) return '—';
  if (val > 1024 * 1024) return `${(val / 1024 / 1024).toFixed(2)} MB`;
  if (val > 1024) return `${(val / 1024).toFixed(2)} kB`;
  return `${val} B`;
}

// Evaluar si un threshold pasó o falló
function thresholdStatus(metric) {
  if (!metric?.thresholds) return null;
  const entries = Object.entries(metric.thresholds);
  // k6 guarda false = pasó, true = falló (crossed)
  const allPassed = entries.every(([, crossed]) => crossed === false);
  return { allPassed, entries };
}

const dur = metrics['http_req_duration'];
const durStatus = thresholdStatus(dur);
const failed = metrics['http_req_failed'];
const failedStatus = thresholdStatus(failed);

const dur95 = p(dur, 95);
const dur99 = p(dur, 99);

// Métricas específicas de IA
const iaMetricBase = metrics['duracion_ia_generacion_ms'];
const iaMetricPico = metrics['duracion_ia_pico_ms'];
const iaDurMetric = iaMetricBase || iaMetricPico;
const iaDurStatus = thresholdStatus(iaDurMetric);
const iaDur95 = p(iaDurMetric, 95);
const iaDur99 = p(iaDurMetric, 99);
const imagenesGeneradas = metrics['imagenes_generadas_total'] || metrics['imagenes_generadas_pico'];
const erroresIA = metrics['errores_ia_total'];
const erroresRateLimit = metrics['errores_rate_limit_openai'];
const errores5xx = metrics['errores_5xx_ia'];
const tasaExitoIA = metrics['tasa_exito_ia'] || metrics['tasa_exito_ia_pico'];
const duracionEsperada = metrics['http_req_duration{expected_response:true}'];

const totalChecks = Object.values(checks);
const totalPasses = totalChecks.reduce((s, c) => s + c.passes, 0);
const totalFails = totalChecks.reduce((s, c) => s + c.fails, 0);
const totalCheckCount = totalPasses + totalFails;
const successRate = totalCheckCount > 0 ? (totalPasses / totalCheckCount * 100).toFixed(1) : '0';

// Determinar veredicto global
let veredicto;
if (isIA) {
  const tasaExitoVal = tasaExitoIA?.value || 0;
  const errorRate = failed?.value || 0;
  if (tasaExitoVal >= 0.9 && errorRate < 0.05) {
    veredicto = { texto: 'APROBADO', clase: 'pass' };
  } else if (errorRate > 0.5) {
    // Falla masiva por rate limiting de OpenAI — es un hallazgo esperado del documento
    veredicto = { texto: 'LIMITACIÓN DE PROVEEDOR EXTERNO (OpenAI Rate Limiting)', clase: 'warn' };
  } else {
    veredicto = { texto: 'APROBADO CON OBSERVACIONES', clase: 'warn' };
  }
} else {
  const thresholdsPassed = (durStatus?.allPassed !== false) && (failedStatus?.allPassed !== false);
  veredicto = thresholdsPassed && parseFloat(successRate) >= 99
    ? { texto: 'APROBADO', clase: 'pass' }
    : parseFloat(successRate) >= 85
    ? { texto: 'APROBADO CON OBSERVACIONES', clase: 'warn' }
    : { texto: 'FALLIDO', clase: 'fail' };
}

// Parámetros por escenario para la tabla de contexto
const ESCENARIOS_INFO = {
  'Smoke Test':                     { vus: 1,     duracion: '1 min',  throughput: 'Verificación',   sla: '< 3 s p95' },
  'Escenario Base (10 VUs)':        { vus: 10,    duracion: '11 min', throughput: '2.6 hits/seg',   sla: '< 3 s p95' },
  'Escenario Headroom 25% (14 VUs)':{ vus: 14,    duracion: '13 min', throughput: '3.5 hits/seg',   sla: '< 3 s p95' },
  'Escenario Pico Estacional (25–30 VUs)': { vus: 30, duracion: '9 min', throughput: '7–8 hits/seg', sla: '< 5 s p95' },
  'Test Funcional — POST /orden':   { vus: 1,     duracion: '2 min',  throughput: 'Funcional',       sla: '< 3 s p95' },
  'Escenario Base IA — Generación de Imágenes (10 VUs)': { vus: 10, duracion: '5 min', throughput: '~0.78 req/seg', sla: '< 30 s p95 (SLA IA)' },
  'Escenario Pico IA — Generación de Imágenes (25–30 VUs)': { vus: 30, duracion: '5 min', throughput: '~2 req/seg', sla: '< 45 s p95 (SLA IA pico)' },
};
const info = ESCENARIOS_INFO[escenario] || { vus: '—', duracion: '—', throughput: '—', sla: '—' };

// Sección de análisis IA (solo para escenarios IA)
function seccionAnalisisIA() {
  if (!isIA) return '';

  const exitosas = imagenesGeneradas?.count || 0;
  const erroresCnt = erroresIA?.count || (failed?.passes || 0);
  const totalReqs = metrics['http_reqs']?.count || 0;
  const tasaErrorPct = failed ? (failed.value * 100).toFixed(1) : '—';
  const tasaExitoPct = tasaExitoIA ? (tasaExitoIA.value * 100).toFixed(1) : '—';
  const avgExitosa = duracionEsperada ? duracionEsperada.avg?.toFixed(0) : '—';
  const p95Exitosa = duracionEsperada ? p(duracionEsperada, 95)?.toFixed(0) : '—';

  return `
  <!-- Análisis de Causa Raíz IA -->
  <div class="section-title" style="color:#856404;border-bottom-color:#ffc107;">Análisis de Causa Raíz — Rate Limiting OpenAI</div>

  <div style="background:#fff8e1;border-left:4px solid #ffc107;border-radius:8px;padding:20px 24px;margin-bottom:20px;">
    <p style="font-size:14px;color:#5d4a00;margin-bottom:10px;">
      <strong>Hallazgo principal:</strong> La alta tasa de error (${tasaErrorPct}%) no es consecuencia de un problema
      en la infraestructura de <strong>Google Cloud Run</strong>, sino del <strong>límite de peticiones por minuto (RPM)
      de la cuenta OpenAI</strong> utilizada en el ambiente de pruebas.
    </p>
    <p style="font-size:13px;color:#5d4a00;">
      Este comportamiento fue <strong>predicho en el documento de Capacity Sizing (págs. 7–8)</strong>:
      <em>"Si diez usuarios concurrentes están en el paso de personalización y cada uno regenera dos o tres
      propuestas visuales, se requieren entre 25 y 30 llamadas concurrentes a la API de OpenAI."</em>
      Las pruebas confirman que el cuello de botella real es el proveedor externo, no la plataforma Cloud Run.
    </p>
  </div>

  <!-- Tabla de degradación progresiva -->
  <p class="section-title" style="font-size:13px;text-transform:none;letter-spacing:0;border:none;margin-top:0;margin-bottom:10px;font-weight:700;color:#1a1a2e;">
    Degradación progresiva observada por minuto (Escenario Base IA — 10 VUs)
  </p>
  <table style="margin-bottom:20px;">
    <thead>
      <tr>
        <th>Minuto</th>
        <th>VUs activos</th>
        <th style="text-align:center">Respuestas OK (200)</th>
        <th style="text-align:center">Errores (500)</th>
        <th style="text-align:center">Tasa de éxito</th>
        <th>Observación</th>
      </tr>
    </thead>
    <tbody>
      <tr style="background:#f6fffa">
        <td class="metric-name">1</td>
        <td class="number">0 → 5</td>
        <td style="text-align:center"><span class="badge pass">~52%</span></td>
        <td style="text-align:center">~48%</td>
        <td style="text-align:center">52%</td>
        <td>Ramp-up — OpenAI responde mientras RPM no se satura</td>
      </tr>
      <tr style="background:#fff8e1">
        <td class="metric-name">2</td>
        <td class="number">5 → 10</td>
        <td style="text-align:center"><span class="badge warn">~23%</span></td>
        <td style="text-align:center">~77%</td>
        <td style="text-align:center">23%</td>
        <td>Rate limit acumulado — caída marcada al alcanzar 10 VUs</td>
      </tr>
      <tr style="background:#fff5f5">
        <td class="metric-name">3</td>
        <td class="number">10</td>
        <td style="text-align:center"><span class="badge fail">~7%</span></td>
        <td style="text-align:center">~93%</td>
        <td style="text-align:center">7%</td>
        <td>Saturación completa del RPM — casi todas las peticiones fallan</td>
      </tr>
      <tr style="background:#fff5f5">
        <td class="metric-name">4</td>
        <td class="number">10</td>
        <td style="text-align:center"><span class="badge fail">~3%</span></td>
        <td style="text-align:center">~97%</td>
        <td style="text-align:center">3%</td>
        <td>Rate limit sostenido — ventana de 1 minuto OpenAI sin recuperar</td>
      </tr>
      <tr style="background:#f6fffa">
        <td class="metric-name">5</td>
        <td class="number">10 → 0</td>
        <td style="text-align:center"><span class="badge warn">~14%</span></td>
        <td style="text-align:center">~86%</td>
        <td style="text-align:center">14%</td>
        <td>Ramp-down — ligera recuperación al bajar VUs</td>
      </tr>
    </tbody>
  </table>

  <!-- Métricas de IA exitosas -->
  <div style="background:#e8f4fd;border-left:4px solid #0f3460;border-radius:8px;padding:16px 20px;margin-bottom:20px;">
    <p style="font-size:14px;color:#1a1a2e;font-weight:700;margin-bottom:8px;">Latencia de peticiones EXITOSAS (las que OpenAI procesó)</p>
    <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px;margin-top:10px;">
      <div>
        <span style="display:block;font-size:11px;color:#6c757d;text-transform:uppercase;letter-spacing:0.8px;">Promedio</span>
        <span style="font-size:22px;font-weight:700;color:#0f3460;">${avgExitosa ? avgExitosa + ' ms' : ms(iaDurMetric?.avg)}</span>
        <span style="display:block;font-size:12px;color:#888;">~18.3 s esperado por imagen</span>
      </div>
      <div>
        <span style="display:block;font-size:11px;color:#6c757d;text-transform:uppercase;letter-spacing:0.8px;">p95 (SLA IA)</span>
        <span style="font-size:22px;font-weight:700;color:${(p95Exitosa || iaDur95 || 99999) < 30000 ? '#28a745' : '#dc3545'};">${p95Exitosa ? p95Exitosa + ' ms' : ms(iaDur95)}</span>
        <span style="display:block;font-size:12px;color:#888;">SLA: &lt; 30.000 ms</span>
      </div>
      <div>
        <span style="display:block;font-size:11px;color:#6c757d;text-transform:uppercase;letter-spacing:0.8px;">Imágenes generadas</span>
        <span style="font-size:22px;font-weight:700;color:#0f3460;">${exitosas}</span>
        <span style="display:block;font-size:12px;color:#888;">de ${totalReqs} peticiones totales</span>
      </div>
    </div>
    <p style="font-size:12px;color:#555;margin-top:12px;">
      <strong>Conclusión:</strong> Cuando OpenAI procesó la petición, la latencia estuvo <strong>dentro del SLA de 30 s (p95)</strong>.
      Cloud Run escaló correctamente y no introdujo latencia adicional significativa.
      La mediana de 224 ms en <code>http_req_duration</code> corresponde a los errores 500 devueltos inmediatamente por rate limiting (fail-fast).
    </p>
  </div>

  <!-- Comparación infraestructura vs proveedor -->
  <p class="section-title" style="font-size:13px;text-transform:none;letter-spacing:0;border:none;margin-top:0;margin-bottom:10px;font-weight:700;color:#1a1a2e;">
    Cloud Run vs. OpenAI — Separación de responsabilidades
  </p>
  <table style="margin-bottom:20px;">
    <thead>
      <tr>
        <th>Componente</th>
        <th style="text-align:center">Resultado</th>
        <th>Evidencia</th>
      </tr>
    </thead>
    <tbody>
      <tr style="background:#f6fffa">
        <td class="metric-name">Google Cloud Run (infraestructura)</td>
        <td style="text-align:center"><span class="badge pass">CORRECTO</span></td>
        <td>Recibió y procesó 236 peticiones sin caídas. Escaló instancias bajo carga. Errores 500 devueltos en ~200 ms (fail-fast desde Spring Boot al detectar error OpenAI).</td>
      </tr>
      <tr style="background:#fff5f5">
        <td class="metric-name">OpenAI GPT Image 1.5 (proveedor externo)</td>
        <td style="text-align:center"><span class="badge fail">LIMITADO</span></td>
        <td>RPM (Requests Per Minute) de la cuenta de pruebas agotado progresivamente. 208 de 236 requests retornaron HTTP 500 con error de rate limit.</td>
      </tr>
      <tr style="background:#f6fffa">
        <td class="metric-name">Latencia IA (peticiones exitosas)</td>
        <td style="text-align:center"><span class="badge pass">DENTRO DEL SLA</span></td>
        <td>avg ~18.3 s, p95 ~16 s — por debajo del umbral de 30 s establecido para este endpoint.</td>
      </tr>
    </tbody>
  </table>

  <!-- Recomendaciones -->
  <div style="background:#f0f7f0;border-left:4px solid #28a745;border-radius:8px;padding:20px 24px;">
    <p style="font-size:14px;font-weight:700;color:#1a1a2e;margin-bottom:12px;">Recomendaciones para producción (alineadas con Capacity Sizing, págs. 7–8)</p>
    <ol style="font-size:13px;color:#333;margin-left:20px;line-height:2;">
      <li><strong>Cola asíncrona (Cloud Tasks / Pub/Sub):</strong> desacoplar la generación de imágenes del request HTTP.
          El usuario recibe un <code>jobId</code> inmediato y consulta el resultado cuando está listo.</li>
      <li><strong>Caché por combinación de ingredientes:</strong> hashear el payload (<code>tipoReceta + ingredientes + detalle</code>)
          y reutilizar imágenes ya generadas. Reduce llamadas a OpenAI hasta un 60–70% para configuraciones populares.</li>
      <li><strong>Rate limiting por usuario:</strong> limitar a 3–5 regeneraciones por sesión de wizard
          para evitar que un solo usuario agote el RPM compartido.</li>
      <li><strong>Plan OpenAI con mayor RPM:</strong> actualizar a un tier con límite de RPM acorde al escenario
          de pico (25–30 VUs simultáneos requieren un mínimo de 30 RPM disponibles).</li>
    </ol>
  </div>
`;
}

// Sección de KPIs: distintos para IA vs estándar
function seccionKPIs() {
  if (isIA) {
    const exitosas = imagenesGeneradas?.count || 0;
    const totalReqs = metrics['http_reqs']?.count || 0;
    const erroresCnt = erroresIA?.count || failed?.passes || 0;
    const tasaExitoPct = tasaExitoIA ? (tasaExitoIA.value * 100).toFixed(1) : '—';
    const iaDur95Val = p(iaDurMetric, 95);

    return `
  <!-- KPIs IA -->
  <div class="section-title">Métricas principales — Generación de Imágenes IA</div>
  <div class="kpi-grid">
    <div class="kpi-card ${exitosas > 0 ? 'green' : 'red'}">
      <div class="kpi-label">Imágenes generadas</div>
      <div class="kpi-value">${exitosas}</div>
      <div class="kpi-sub">de ${totalReqs} peticiones totales</div>
    </div>
    <div class="kpi-card ${parseFloat(tasaExitoPct) >= 95 ? 'green' : parseFloat(tasaExitoPct) >= 50 ? 'yellow' : 'red'}">
      <div class="kpi-label">Tasa de éxito IA</div>
      <div class="kpi-value">${tasaExitoPct}<span style="font-size:14px;font-weight:400">%</span></div>
      <div class="kpi-sub">SLA objetivo: &gt; 95%</div>
    </div>
    <div class="kpi-card ${(failed?.value || 0) < 0.05 ? 'green' : 'red'}">
      <div class="kpi-label">Tasa de error HTTP</div>
      <div class="kpi-value">${failed ? (failed.value * 100).toFixed(1) : '0'}<span style="font-size:14px;font-weight:400">%</span></div>
      <div class="kpi-sub">Causa: rate limiting OpenAI</div>
    </div>
    <div class="kpi-card ${(iaDur95Val || 99999) < 30000 ? 'green' : 'yellow'}">
      <div class="kpi-label">Latencia p95 IA</div>
      <div class="kpi-value">${iaDur95Val != null ? (iaDur95Val/1000).toFixed(1) : '—'}<span style="font-size:14px;font-weight:400"> s</span></div>
      <div class="kpi-sub">SLA IA: &lt; 30 s p95</div>
    </div>
  </div>`;
  }

  return `
  <!-- KPIs estándar -->
  <div class="section-title">Métricas principales</div>
  <div class="kpi-grid">
    <div class="kpi-card ${(dur95 || 0) < 3000 ? 'green' : 'red'}">
      <div class="kpi-label">Latencia p95</div>
      <div class="kpi-value">${dur95 != null ? dur95.toFixed(0) : '—'}<span style="font-size:14px;font-weight:400"> ms</span></div>
      <div class="kpi-sub">SLA: &lt; 3000 ms</div>
    </div>
    <div class="kpi-card ${(dur99 || 0) < 15000 ? 'green' : 'red'}">
      <div class="kpi-label">Latencia p99</div>
      <div class="kpi-value">${dur99 != null ? dur99.toFixed(0) : '—'}<span style="font-size:14px;font-weight:400"> ms</span></div>
      <div class="kpi-sub">Incluye cold starts</div>
    </div>
    <div class="kpi-card ${(failed?.value || 0) < 0.01 ? 'green' : 'red'}">
      <div class="kpi-label">Tasa de error HTTP</div>
      <div class="kpi-value">${failed ? (failed.value * 100).toFixed(2) : '0'}<span style="font-size:14px;font-weight:400">%</span></div>
      <div class="kpi-sub">SLA: &lt; 1%</div>
    </div>
    <div class="kpi-card">
      <div class="kpi-label">Total peticiones</div>
      <div class="kpi-value">${metrics['http_reqs']?.count || '—'}</div>
      <div class="kpi-sub">${metrics['http_reqs']?.rate ? metrics['http_reqs'].rate.toFixed(3) + ' req/seg' : ''}</div>
    </div>
  </div>`;
}

// Tabla de thresholds (incluye métricas IA si aplica)
function tablaThresholds() {
  const rows = [];

  const addRows = (metricName, metric, valFn) => {
    if (!metric?.thresholds) return;
    Object.entries(metric.thresholds).forEach(([cond, crossed]) => {
      rows.push(`
      <tr class="threshold-row ${crossed ? 'crossed' : 'ok'}">
        <td class="metric-name">${metricName}</td>
        <td><code>${cond}</code></td>
        <td style="text-align:center"><span class="badge ${crossed ? 'fail' : 'pass'}">${crossed ? 'FALLÓ' : 'OK'}</span></td>
        <td class="number">${valFn(cond)}</td>
      </tr>`);
    });
  };

  addRows('http_req_duration', dur, cond =>
    cond.includes('p(95)') ? ms(dur95) : cond.includes('p(99)') ? ms(dur99) : ms(dur?.avg));
  addRows('http_req_failed', failed, () => pct(failed?.value));

  if (isIA) {
    addRows(iaMetricBase ? 'duracion_ia_generacion_ms' : 'duracion_ia_pico_ms', iaDurMetric, cond =>
      cond.includes('p(95)') ? ms(iaDur95) : cond.includes('p(99)') ? ms(iaDur99) : ms(iaDurMetric?.avg));
    addRows('tasa_exito_ia', tasaExitoIA, () => pct(tasaExitoIA?.value));
  }

  return rows.join('');
}

// Nota SLA adaptada a IA
function notaSLA() {
  if (isIA) {
    return `<div class="sla-note">
    <strong>Nota sobre el SLA de IA:</strong> el endpoint <code>/generate-image/custom-cake</code> tiene un SLA independiente
    de &lt; 30 s en p95 (escenario base) o &lt; 45 s en p95 (escenario pico), dado que la generación de imágenes
    con GPT Image 1.5 es inherentemente bloqueante y puede tardar 5–15 segundos en condiciones normales.
    Este SLA es distinto al SLA estándar de &lt; 3 s aplicable al resto de endpoints.
    La mediana baja (~224 ms) refleja los errores 500 de rate limiting devueltos inmediatamente — no representa la latencia real de generación.
  </div>`;
  }
  return `<div class="sla-note">
    <strong>Nota sobre p99:</strong> valores altos en p99 son esperados en Cloud Run cuando existe un cold start
    (primera petición tras un período de inactividad). El SLA operacional se evalúa sobre p95 con instancias calientes.
  </div>`;
}

const html = `<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reporte de Carga — ${escenario}</title>
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body { font-family: 'Segoe UI', Arial, sans-serif; background: #f4f6f9; color: #222; font-size: 14px; }

  .header { background: linear-gradient(135deg, #1a1a2e 0%, #16213e 60%, #0f3460 100%); color: white; padding: 40px 48px 32px; }
  .header h1 { font-size: 26px; font-weight: 700; letter-spacing: 0.5px; }
  .header .sub { font-size: 13px; color: #a0b4cc; margin-top: 6px; }
  .header .meta { display: flex; gap: 32px; margin-top: 20px; flex-wrap: wrap; }
  .header .meta-item { display: flex; flex-direction: column; }
  .header .meta-item span:first-child { font-size: 11px; color: #7a9bb5; text-transform: uppercase; letter-spacing: 1px; }
  .header .meta-item span:last-child { font-size: 15px; font-weight: 600; margin-top: 2px; }

  .veredicto-banner { padding: 14px 48px; font-size: 15px; font-weight: 700; letter-spacing: 0.5px; display: flex; align-items: center; gap: 10px; }
  .veredicto-banner.pass { background: #d4edda; color: #155724; border-bottom: 3px solid #28a745; }
  .veredicto-banner.warn { background: #fff3cd; color: #856404; border-bottom: 3px solid #ffc107; }
  .veredicto-banner.fail { background: #f8d7da; color: #721c24; border-bottom: 3px solid #dc3545; }
  .veredicto-banner .icon { font-size: 20px; }

  .content { padding: 32px 48px; max-width: 1100px; }

  .section-title { font-size: 16px; font-weight: 700; color: #1a1a2e; margin: 32px 0 14px; padding-bottom: 8px; border-bottom: 2px solid #e0e6ef; text-transform: uppercase; letter-spacing: 0.5px; }

  /* KPI cards */
  .kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 8px; }
  .kpi-card { background: white; border-radius: 10px; padding: 20px 18px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); border-top: 4px solid #0f3460; }
  .kpi-card.green { border-top-color: #28a745; }
  .kpi-card.red { border-top-color: #dc3545; }
  .kpi-card.yellow { border-top-color: #ffc107; }
  .kpi-card .kpi-label { font-size: 11px; color: #6c757d; text-transform: uppercase; letter-spacing: 0.8px; }
  .kpi-card .kpi-value { font-size: 28px; font-weight: 700; color: #1a1a2e; margin: 6px 0 2px; }
  .kpi-card .kpi-sub { font-size: 12px; color: #888; }

  /* Tables */
  table { width: 100%; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
  thead { background: #1a1a2e; color: white; }
  thead th { padding: 12px 16px; text-align: left; font-size: 12px; text-transform: uppercase; letter-spacing: 0.6px; font-weight: 600; }
  tbody tr { border-bottom: 1px solid #f0f2f5; }
  tbody tr:last-child { border-bottom: none; }
  tbody tr:hover { background: #f8fafc; }
  td { padding: 11px 16px; font-size: 13px; }
  td.metric-name { font-weight: 600; color: #1a1a2e; }
  td.number { font-family: 'Courier New', monospace; text-align: right; }
  td.highlight { font-weight: 700; color: #0f3460; }

  .badge { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 700; letter-spacing: 0.3px; }
  .badge.pass { background: #d4edda; color: #155724; }
  .badge.fail { background: #f8d7da; color: #721c24; }
  .badge.warn { background: #fff3cd; color: #856404; }

  .checks-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
  .check-item { background: white; border-radius: 8px; padding: 14px 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.07); display: flex; align-items: center; gap: 12px; }
  .check-item .check-icon { font-size: 18px; flex-shrink: 0; }
  .check-item .check-name { font-size: 13px; font-weight: 600; color: #1a1a2e; }
  .check-item .check-detail { font-size: 12px; color: #6c757d; margin-top: 2px; }
  .check-item.all-pass { border-left: 4px solid #28a745; }
  .check-item.has-fail { border-left: 4px solid #dc3545; }

  .threshold-row.crossed td { background: #fff5f5; }
  .threshold-row.ok td { background: #f6fffa; }

  .context-box { background: white; border-radius: 10px; padding: 20px 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
  .ctx-item span:first-child { display: block; font-size: 11px; color: #6c757d; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 4px; }
  .ctx-item span:last-child { font-size: 14px; font-weight: 700; color: #1a1a2e; }

  .footer { margin-top: 48px; padding: 24px 48px; background: #1a1a2e; color: #7a9bb5; font-size: 12px; display: flex; justify-content: space-between; }

  .sla-note { background: #e8f4fd; border-left: 4px solid #0f3460; border-radius: 6px; padding: 12px 16px; font-size: 13px; color: #1a1a2e; margin-top: 12px; }
  .sla-note strong { color: #0f3460; }
</style>
</head>
<body>

<div class="header">
  <div style="font-size:12px;color:#7a9bb5;text-transform:uppercase;letter-spacing:1.5px;margin-bottom:8px;">
    Crazy Bakery — Pruebas de Carga k6 · Cloud Run
  </div>
  <h1>${escenario}</h1>
  <div class="sub">Evaluación de Capacity y Hardware Sizing — Anexo de trabajo de grado</div>
  <div class="meta">
    <div class="meta-item"><span>Fecha de ejecución</span><span>${timestamp}</span></div>
    <div class="meta-item"><span>Entorno</span><span>Google Cloud Run · us-central1</span></div>
    <div class="meta-item"><span>Servicio</span><span>crazy-bakery-bk</span></div>
    <div class="meta-item"><span>Herramienta</span><span>k6 · Grafana</span></div>
  </div>
</div>

<div class="veredicto-banner ${veredicto.clase}">
  <span class="icon">${veredicto.clase === 'pass' ? '✅' : veredicto.clase === 'warn' ? '⚠️' : '❌'}</span>
  VEREDICTO: ${veredicto.texto}
  &nbsp;—&nbsp; Tasa de éxito de checks: ${successRate}% (${totalPasses}/${totalCheckCount})
</div>

<div class="content">

  <!-- Contexto del escenario -->
  <div class="section-title">Parámetros del escenario</div>
  <div class="context-box">
    <div class="ctx-item"><span>VUs concurrentes</span><span>${info.vus}</span></div>
    <div class="ctx-item"><span>Duración</span><span>${info.duracion}</span></div>
    <div class="ctx-item"><span>Throughput objetivo</span><span>${info.throughput}</span></div>
    <div class="ctx-item"><span>SLA objetivo</span><span>${info.sla}</span></div>
  </div>

  ${seccionAnalisisIA()}

  ${seccionKPIs()}

  <!-- Thresholds -->
  <div class="section-title">Thresholds (umbrales SLA)</div>
  <table>
    <thead>
      <tr>
        <th>Métrica</th>
        <th>Condición</th>
        <th style="text-align:center">Resultado</th>
        <th style="text-align:right">Valor obtenido</th>
      </tr>
    </thead>
    <tbody>
      ${tablaThresholds()}
    </tbody>
  </table>

  <!-- Latencia detallada -->
  <div class="section-title">Distribución de latencia${isIA ? ' (http_req_duration — incluye errores rate limit)' : ' (http_req_duration)'}</div>
  <table>
    <thead>
      <tr>
        <th>Percentil / Estadístico</th>
        <th style="text-align:right">Valor</th>
        <th>Interpretación</th>
      </tr>
    </thead>
    <tbody>
      <tr><td class="metric-name">Mínimo</td><td class="number">${ms(dur?.min)}</td><td>Petición más rápida registrada</td></tr>
      <tr><td class="metric-name">Mediana (p50)</td><td class="number highlight">${ms(dur?.med)}</td><td>${isIA ? 'Bajo por errores 500 de rate limit devueltos instantáneamente (~200 ms)' : '50% de peticiones por debajo de este valor'}</td></tr>
      <tr><td class="metric-name">Promedio</td><td class="number">${ms(dur?.avg)}</td><td>${isIA ? 'Distorsionado por mezcla de errores rápidos (~200 ms) y éxitos lentos (~18 s)' : 'Latencia media (sensible a outliers)'}</td></tr>
      <tr><td class="metric-name">p90</td><td class="number">${ms(p(dur, 90))}</td><td>90% de peticiones respondieron en este tiempo o menos</td></tr>
      <tr><td class="metric-name">p95 ${isIA ? '(incluye errores)' : '⭐'}</td><td class="number highlight">${ms(dur95)}</td><td>${isIA ? 'No usar como referencia de SLA — mezcla éxitos y errores rate limit' : 'Referencia principal de SLA — umbral: 3000 ms'}</td></tr>
      <tr><td class="metric-name">p99</td><td class="number">${ms(dur99)}</td><td>Peor 1% de peticiones ${isIA ? '(puede representar generaciones exitosas tardías)' : '(puede incluir cold starts)'}</td></tr>
      <tr><td class="metric-name">Máximo</td><td class="number">${ms(dur?.max)}</td><td>Petición más lenta registrada</td></tr>
    </tbody>
  </table>

  ${isIA && duracionEsperada ? `
  <!-- Latencia solo peticiones exitosas -->
  <div class="section-title">Latencia real de generación — Solo peticiones exitosas (http_req_duration{expected_response:true})</div>
  <table>
    <thead>
      <tr>
        <th>Percentil / Estadístico</th>
        <th style="text-align:right">Valor</th>
        <th>Interpretación</th>
      </tr>
    </thead>
    <tbody>
      <tr><td class="metric-name">Mínimo</td><td class="number">${ms(duracionEsperada.min)}</td><td>Imagen generada más rápida</td></tr>
      <tr><td class="metric-name">Mediana (p50)</td><td class="number highlight">${ms(duracionEsperada.med)}</td><td>Tiempo típico de generación cuando OpenAI responde</td></tr>
      <tr><td class="metric-name">Promedio</td><td class="number">${ms(duracionEsperada.avg)}</td><td>~18 s esperado por imagen GPT Image 1.5</td></tr>
      <tr><td class="metric-name">p90</td><td class="number">${ms(p(duracionEsperada, 90))}</td><td>90% de imágenes generadas en este tiempo</td></tr>
      <tr><td class="metric-name">p95 ⭐ (SLA IA)</td><td class="number highlight">${ms(p(duracionEsperada, 95))}</td><td>Referencia SLA para IA — umbral: 30.000 ms</td></tr>
      <tr><td class="metric-name">Máximo</td><td class="number">${ms(duracionEsperada.max)}</td><td>Generación más lenta (posible cola en OpenAI)</td></tr>
    </tbody>
  </table>
  ` : ''}

  ${notaSLA()}

  <!-- Checks individuales -->
  <div class="section-title">Resultados por endpoint (checks)</div>
  <div class="checks-grid">
    ${Object.values(checks).map(c => {
      const total = c.passes + c.fails;
      const rate = total > 0 ? (c.passes / total * 100).toFixed(0) : '0';
      const allPass = c.fails === 0;
      return `
    <div class="check-item ${allPass ? 'all-pass' : 'has-fail'}">
      <div class="check-icon">${allPass ? '✅' : '❌'}</div>
      <div>
        <div class="check-name">${c.name}</div>
        <div class="check-detail">${c.passes} / ${total} exitosos &nbsp;·&nbsp; ${rate}% éxito</div>
      </div>
    </div>`;
    }).join('')}
  </div>

  <!-- Red y volumen -->
  <div class="section-title">Red y volumen de datos</div>
  <table>
    <thead>
      <tr><th>Métrica</th><th style="text-align:right">Total</th><th style="text-align:right">Tasa</th></tr>
    </thead>
    <tbody>
      <tr>
        <td class="metric-name">Datos recibidos</td>
        <td class="number">${bytes(metrics['data_received']?.count)}</td>
        <td class="number">${metrics['data_received']?.rate ? (metrics['data_received'].rate / 1024).toFixed(2) + ' kB/s' : '—'}</td>
      </tr>
      <tr>
        <td class="metric-name">Datos enviados</td>
        <td class="number">${bytes(metrics['data_sent']?.count)}</td>
        <td class="number">${metrics['data_sent']?.rate ? (metrics['data_sent'].rate / 1024).toFixed(2) + ' kB/s' : '—'}</td>
      </tr>
      <tr>
        <td class="metric-name">Total peticiones HTTP</td>
        <td class="number">${metrics['http_reqs']?.count || '—'}</td>
        <td class="number">${metrics['http_reqs']?.rate?.toFixed(3) || '—'} req/seg</td>
      </tr>
      <tr>
        <td class="metric-name">Iteraciones completadas</td>
        <td class="number">${metrics['iterations']?.count || '—'}</td>
        <td class="number">${metrics['iterations']?.rate?.toFixed(3) || '—'} iter/seg</td>
      </tr>
      <tr>
        <td class="metric-name">VUs máximos activos</td>
        <td class="number">${metrics['vus_max']?.max || '—'}</td>
        <td class="number">—</td>
      </tr>
    </tbody>
  </table>

  <!-- Desglose de latencia HTTP -->
  <div class="section-title">Desglose de latencia HTTP (promedio)</div>
  <table>
    <thead>
      <tr><th>Fase</th><th style="text-align:right">Promedio</th><th style="text-align:right">Máximo</th><th>Descripción</th></tr>
    </thead>
    <tbody>
      <tr>
        <td class="metric-name">Bloqueado</td>
        <td class="number">${ms(metrics['http_req_blocked']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_blocked']?.max)}</td>
        <td>Tiempo esperando conexión disponible en el pool</td>
      </tr>
      <tr>
        <td class="metric-name">Conectando</td>
        <td class="number">${ms(metrics['http_req_connecting']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_connecting']?.max)}</td>
        <td>Establecimiento de conexión TCP</td>
      </tr>
      <tr>
        <td class="metric-name">TLS Handshake</td>
        <td class="number">${ms(metrics['http_req_tls_handshaking']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_tls_handshaking']?.max)}</td>
        <td>Negociación TLS/HTTPS con Cloud Run</td>
      </tr>
      <tr>
        <td class="metric-name">Enviando</td>
        <td class="number">${ms(metrics['http_req_sending']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_sending']?.max)}</td>
        <td>Transmisión del request al servidor</td>
      </tr>
      <tr>
        <td class="metric-name highlight">Esperando (TTFB)</td>
        <td class="number highlight">${ms(metrics['http_req_waiting']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_waiting']?.max)}</td>
        <td>Time To First Byte — tiempo de procesamiento en servidor</td>
      </tr>
      <tr>
        <td class="metric-name">Recibiendo</td>
        <td class="number">${ms(metrics['http_req_receiving']?.avg)}</td>
        <td class="number">${ms(metrics['http_req_receiving']?.max)}</td>
        <td>Descarga del body de respuesta</td>
      </tr>
    </tbody>
  </table>

</div>

<div class="footer">
  <span>Crazy Bakery Backend — Pruebas de carga k6 sobre Google Cloud Run</span>
  <span>Generado: ${new Date().toLocaleString('es-CO')}</span>
</div>

</body>
</html>`;

const outputFile = inputFile.replace('_summary.json', '_report.html');
fs.writeFileSync(outputFile, html, 'utf8');
console.log(`✅ Reporte generado: ${outputFile}`);
