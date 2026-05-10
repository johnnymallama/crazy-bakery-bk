#!/bin/bash
# Escenario Base (10 VUs) — guarda resultados en results/
# Uso: ./run-base.sh <FIREBASE_TOKEN>

FIREBASE_TOKEN="${1:-}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando escenario base (10 VUs, ~11 min)..."
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/escenario-base.js \
  -e FIREBASE_TOKEN="$FIREBASE_TOKEN" \
  --out json="$RESULTS_DIR/base_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/base_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/base_${TIMESTAMP}.log"
