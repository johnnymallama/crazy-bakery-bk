#!/bin/bash
# Escenario Pico IA — custom-cake (25-30 VUs, ~5 min)
# COSTO ESTIMADO: ~USD 1.20 por ejecución
# Uso: ./run-ia-pico.sh

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando escenario pico IA — custom-cake (25-30 VUs, ~5 min)..."
echo "⚠️  Costo estimado: ~USD 1.20 en OpenAI"
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/escenario-ia-pico.js \
  --out json="$RESULTS_DIR/ia-pico_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/ia-pico_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/ia-pico_${TIMESTAMP}.log"
