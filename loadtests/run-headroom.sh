#!/bin/bash
# Escenario Headroom 25% (14 VUs) — guarda resultados en results/
# Uso: ./run-headroom.sh <FIREBASE_TOKEN>

FIREBASE_TOKEN="${1:-}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando escenario headroom 25% (14 VUs, ~13 min)..."
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/escenario-headroom.js \
  -e FIREBASE_TOKEN="$FIREBASE_TOKEN" \
  --out json="$RESULTS_DIR/headroom_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/headroom_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/headroom_${TIMESTAMP}.log"
