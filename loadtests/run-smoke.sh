#!/bin/bash
# Smoke Test — guarda resultados en results/
# Uso: ./run-smoke.sh <FIREBASE_TOKEN>

FIREBASE_TOKEN="${1:-}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando smoke test..."
echo "Timestamp: $TIMESTAMP"
echo "Resultados en: $RESULTS_DIR/smoke_${TIMESTAMP}.*"
echo ""

k6 run scripts/smoke-test.js \
  -e FIREBASE_TOKEN="$FIREBASE_TOKEN" \
  --out json="$RESULTS_DIR/smoke_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/smoke_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/smoke_${TIMESTAMP}.log"
