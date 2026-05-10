#!/bin/bash
# Test Funcional POST /orden — 1 VU, 2 min
# Uso: ./run-orden-funcional.sh <FIREBASE_TOKEN>

FIREBASE_TOKEN="${1:-}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_DIR="results"
mkdir -p "$RESULTS_DIR"

echo "Ejecutando test funcional POST /orden (1 VU, 2 min)..."
echo "Timestamp: $TIMESTAMP"
echo ""

k6 run scripts/test-orden-funcional.js \
  -e FIREBASE_TOKEN="$FIREBASE_TOKEN" \
  --out json="$RESULTS_DIR/orden-funcional_${TIMESTAMP}.json" \
  --summary-export="$RESULTS_DIR/orden-funcional_${TIMESTAMP}_summary.json" \
  2>&1 | tee "$RESULTS_DIR/orden-funcional_${TIMESTAMP}.log"
