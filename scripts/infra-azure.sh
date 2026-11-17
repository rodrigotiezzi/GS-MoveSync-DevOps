#!/usr/bin/env bash
# =====================================================================================
# MoveSync - Provisionamento da infraestrutura no Azure (Azure CLI)
# Cria: Resource Group, Azure SQL (servidor + banco + firewall), App Service Plan B1,
#       Web App Linux (Tomcat 11 / Java 21), credenciais de publicação e variáveis de ambiente.
#
# Uso (Azure Cloud Shell - Bash):
#   export SQL_ADMIN_PASSWORD='...' ADMIN_PASSWORD='...' OPERADOR_PASSWORD='...'
#   bash scripts/infra-azure.sh
#
# Regiões: a assinatura Azure for Students só permite eastus2, mexicocentral, centralus,
# southafricanorth e canadacentral. eastus2 tem a menor latência para o Brasil; o SQL fica em
# centralus porque eastus2 costuma recusar servidores SQL novos.
# =====================================================================================
set -euo pipefail

# ---------- Variáveis ----------
RM="${RM:-562975}"
RESOURCE_GROUP="rg-movesync"
LOCATION="${LOCATION:-eastus2}"
SQL_LOCATION="${SQL_LOCATION:-centralus}"

APP_SERVICE_PLAN="plan-movesync"
APP_SKU="${APP_SKU:-B1}"
WEBAPP_NAME="webapp-movesync-rm${RM}"
RUNTIME="TOMCAT:11.0-java21"          # projeto é Spring Boot 4 empacotado como .war

SQL_SERVER="sqlserver-movesync-rm${RM}"
SQL_DB="movesyncdb"
SQL_ADMIN_USER="${SQL_ADMIN_USER:-movesyncadmin}"

# Segredos: obrigatórios via variável de ambiente (nunca ficam no código nem no repositório)
: "${SQL_ADMIN_PASSWORD:?Defina SQL_ADMIN_PASSWORD antes de rodar o script}"
: "${ADMIN_PASSWORD:?Defina ADMIN_PASSWORD (senha do usuário admin da aplicação)}"
: "${OPERADOR_PASSWORD:?Defina OPERADOR_PASSWORD (senha do usuário operador da aplicação)}"
JWT_SECRET="${JWT_SECRET:-$(openssl rand -base64 48)}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "==> 1/7 Resource Group ${RESOURCE_GROUP} (${LOCATION})"
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" -o none

echo "==> 2/7 Azure SQL Server ${SQL_SERVER} (${SQL_LOCATION})"
az sql server create \
  --name "$SQL_SERVER" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$SQL_LOCATION" \
  --admin-user "$SQL_ADMIN_USER" \
  --admin-password "$SQL_ADMIN_PASSWORD" -o none

# O servidor demora alguns segundos para ficar visível; espera antes de criar o banco
az sql server wait --name "$SQL_SERVER" --resource-group "$RESOURCE_GROUP" --exists

echo "==> 3/7 Banco ${SQL_DB} (Basic) + firewall"
az sql db create \
  --resource-group "$RESOURCE_GROUP" \
  --server "$SQL_SERVER" \
  --name "$SQL_DB" \
  --service-objective Basic -o none

az sql server firewall-rule create \
  --resource-group "$RESOURCE_GROUP" \
  --server "$SQL_SERVER" \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255 -o none

echo "==> 4/7 App Service Plan ${APP_SERVICE_PLAN} (${APP_SKU}, Linux)"
az appservice plan create \
  --name "$APP_SERVICE_PLAN" \
  --resource-group "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --sku "$APP_SKU" \
  --is-linux -o none

echo "==> 5/7 Web App ${WEBAPP_NAME} (${RUNTIME})"
az webapp create \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --plan "$APP_SERVICE_PLAN" \
  --runtime "$RUNTIME" -o none

# Libera a publicação (SCM) usada pelo deploy da Release
az resource update \
  --resource-group "$RESOURCE_GROUP" \
  --namespace Microsoft.Web \
  --resource-type basicPublishingCredentialsPolicies \
  --name scm \
  --parent "sites/${WEBAPP_NAME}" \
  --set properties.allow=true -o none

echo "==> 6/7 Variáveis de ambiente do Web App"
JDBC_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP" \
  --settings \
    SPRING_DATASOURCE_URL="$JDBC_URL" \
    SPRING_DATASOURCE_USERNAME="$SQL_ADMIN_USER" \
    SPRING_DATASOURCE_PASSWORD="$SQL_ADMIN_PASSWORD" \
    JWT_SECRET="$JWT_SECRET" \
    ADMIN_USERNAME="admin" \
    ADMIN_PASSWORD="$ADMIN_PASSWORD" \
    OPERADOR_USERNAME="operador" \
    OPERADOR_PASSWORD="$OPERADOR_PASSWORD" -o none

az webapp log config --name "$WEBAPP_NAME" --resource-group "$RESOURCE_GROUP" \
  --docker-container-logging filesystem -o none

echo "==> 7/7 DDL e dados fictícios (script-bd.sql)"
if command -v sqlcmd >/dev/null 2>&1; then
  sqlcmd -S "${SQL_SERVER}.database.windows.net" -d "$SQL_DB" \
    -U "$SQL_ADMIN_USER" -P "$SQL_ADMIN_PASSWORD" -i "${SCRIPT_DIR}/script-bd.sql"
else
  echo "    sqlcmd não encontrado: rode scripts/script-bd.sql pelo Query editor do banco no Portal Azure."
fi

echo
echo "Pronto!"
echo "  Web App : https://${WEBAPP_NAME}.azurewebsites.net/web/login"
echo "  Swagger : https://${WEBAPP_NAME}.azurewebsites.net/swagger-ui.html"
echo "  Banco   : ${SQL_SERVER}.database.windows.net / ${SQL_DB}"
echo "Agora rode a pipeline de CI (merge na main) para publicar o .war pela Release."
