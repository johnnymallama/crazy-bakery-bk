#!/bin/bash
# Escenario Base IA — custom-cake (10 VUs, ~5 min)
# COSTO ESTIMADO: ~USD 0.40 por ejecución
# Uso: ./run-ia-base.sh

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando escenario base IA — custom-cake (10 VUs, ~5 min)..."
echo "⚠️  Costo estimado: ~USD 0.40 en OpenAI"
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/escenario-ia-base.js \
  --out json="$RESULTS_DIR/ia-base_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/ia-base_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/ia-base_${TIMESTAMP}.log"
