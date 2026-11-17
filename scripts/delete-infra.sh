#!/usr/bin/env bash
# MoveSync - Remove todos os recursos (use depois da apresentação para não gastar crédito)
set -euo pipefail
az group delete --name rg-movesync --yes --no-wait
echo "Exclusão do rg-movesync solicitada (leva alguns minutos)."
