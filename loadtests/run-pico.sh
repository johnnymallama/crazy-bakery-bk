#!/bin/bash
# Escenario Pico Estacional (25-30 VUs) — guarda resultados en results/
# Uso: ./run-pico.sh <FIREBASE_TOKEN>

FIREBASE_TOKEN="${1:-}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando escenario pico estacional (25-30 VUs, ~9 min)..."
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/escenario-pico.js \
  -e FIREBASE_TOKEN="$FIREBASE_TOKEN" \
  --out json="$RESULTS_DIR/pico_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/pico_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/pico_${TIMESTAMP}.log"
