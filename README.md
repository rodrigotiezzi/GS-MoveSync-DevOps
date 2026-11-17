# ☁️ Deploy na Azure — MoveSync

## 📌 Descrição da Solução

A ideia principal do **MoveSync** é um sistema de **semáforos inteligentes com IoT**: sensores e câmeras instalados nos cruzamentos leem o fluxo de veículos e pedestres e o sistema **ajusta o tempo de verde** de cada semáforo conforme o trânsito real — fazendo com que o cidadão passe menos tempo parado no semáforo.

Para sustentar essa proposta, foi desenvolvida uma **API RESTful em Java com Spring Boot**, com persistência em **Azure SQL Database**, que funciona como o back-end de gestão da solução: centraliza o cadastro de **cruzamentos**, **semáforos** e **dispositivos IoT** (câmeras e sensores), vincula os dispositivos a cada semáforo, registra as **leituras de tráfego** (quantidade de veículos, pedestres e velocidade média) e calcula os **ajustes de tempo** — guardando o histórico de cada ajuste (tempo anterior, tempo novo, média de veículos e motivo).

A regra do ajuste usa a média das 5 últimas leituras do semáforo: **verde = 15 s + 1,5 s por veículo**, sempre entre **15 s e 90 s**.

> ⚠️ **Os dados de IoT são fictícios.** Não há hardware real: as leituras são geradas pelo **simulador** (`POST /leitura/simular` ou botão *Simular leituras* no painel) e pelos dados de exemplo do `scripts/script-bd.sql`.

A solução expõe endpoints REST documentados via **Swagger/OpenAPI**, com suporte a busca por parâmetros, paginação e ordenação de resultados, validação de dados de entrada e tratamento global de exceções — cobrindo todo o fluxo, do cadastro do cruzamento até o ajuste automático do semáforo.

## 💼 Descrição dos Benefícios para o Negócio

- **Menos tempo parado no trânsito**: o tempo de verde acompanha o fluxo real de cada via, em vez de um ciclo fixo.
- **Menos poluição e consumo de combustível**: menos tempo de carro parado em marcha lenta nos cruzamentos.
- **Pedestres também atendidos**: semáforos de pedestre têm o ciclo ajustado pelo movimento de pessoas.
- **Manutenção mais rápida**: semáforos em manutenção e sensores offline ficam visíveis no painel.
- **Decisão baseada em dados**: o histórico de leituras e ajustes mostra onde e quando o trânsito mais trava.
- **Escalabilidade e disponibilidade**: hospedagem em nuvem (Azure App Service + Azure SQL Database) permite crescer o número de cruzamentos e leituras sem infraestrutura própria.
- **Integração facilitada**: por ser uma API REST documentada (Swagger) e com coleção Postman disponível, o sistema pode ser integrado a outros sistemas (centrais de trânsito, apps de mobilidade, painéis da prefeitura) com baixo esforço.

---

## Arquitetura
Adicionar FOTO DA ARQUITETURA

---

## Variáveis

```bash
# Gerais
RESOURCE_GROUP_NAME="rg-movesync"
LOCATION="eastus2"            # Web App - menor latência para o Brasil entre as regiões permitidas
SQL_LOCATION="centralus"      # Azure SQL - East US 2 não aceita criação de novos servidores SQL

# Web App
WEBAPP_NAME="webapp-movesync-rm562975"
APP_SERVICE_PLAN="plan-movesync"
SKU="B1"                      # F1 estoura a cota de CPU (60 min/dia)
RUNTIME="TOMCAT:11.0-java21"  # projeto é Spring Boot 4 empacotado como .war

# Banco de Dados (Azure SQL)
SQL_SERVER_NAME="sqlserver-movesync-rm562975"
SQL_DB_NAME="movesyncdb"
SQL_ADMIN_USER="movesyncadmin"
SQL_ADMIN_PASSWORD="<defina-uma-senha-forte>"   # não versionar a senha real

# GitHub
GITHUB_REPO_NAME="rodrigotiezzi/GS-MoveSync-DevOps"
BRANCH="main"
```

> **Regiões:** a assinatura Azure for Students só permite `eastus2`, `mexicocentral`, `centralus`, `southafricanorth` e `canadacentral` (consulte com `az policy assignment list --query "[].parameters" -o json`). O Web App fica em **East US 2** (única com cota de B1) e o banco em **Central US**.

---

## Comandos

> Rode **um comando por vez** e espere a resposta (JSON) antes de colar o próximo.

### 1. Resource Group

```bash
az group create --name rg-movesync --location "eastus2"
```

### 2. Banco de dados (Azure SQL)

```bash
az sql server create \
  --name "sqlserver-movesync-rm562975" \
  --resource-group "rg-movesync" \
  --location "centralus" \
  --admin-user "movesyncadmin" \
  --admin-password "<defina-uma-senha-forte>"

az sql db create \
  --resource-group rg-movesync \
  --server sqlserver-movesync-rm562975 \
  --name movesyncdb \
  --service-objective Basic

az sql server firewall-rule create \
  --resource-group rg-movesync \
  --server sqlserver-movesync-rm562975 \
  --name AllowAzureServices \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255
```

> Se o `az sql db create` der `ResourceNotFound`, o servidor ainda está sendo criado: aguarde alguns segundos e rode de novo.

### 3. App Service Plan + Web App

```bash
az appservice plan create \
  --name plan-movesync \
  --resource-group rg-movesync \
  --location eastus2 \
  --sku B1 \
  --is-linux

az webapp create \
  --name webapp-movesync-rm562975 \
  --resource-group rg-movesync \
  --plan plan-movesync \
  --runtime "TOMCAT:11.0-java21"

az resource update \
  --resource-group rg-movesync \
  --namespace Microsoft.Web \
  --resource-type basicPublishingCredentialsPolicies \
  --name scm \
  --parent sites/webapp-movesync-rm562975 \
  --set properties.allow=true
```

### 4. Variáveis de ambiente do Web App

```bash
az webapp config appsettings set \
  --name "webapp-movesync-rm562975" \
  --resource-group "rg-movesync" \
  --settings \
    SPRING_DATASOURCE_USERNAME="movesyncadmin" \
    SPRING_DATASOURCE_PASSWORD="<senha-do-banco>" \
    SPRING_DATASOURCE_URL="jdbc:sqlserver://sqlserver-movesync-rm562975.database.windows.net:1433;database=movesyncdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;" \
    JWT_SECRET="<texto-aleatorio-com-32-caracteres-ou-mais>" \
    ADMIN_USERNAME="admin" \
    ADMIN_PASSWORD="<senha-do-admin>" \
    OPERADOR_USERNAME="operador" \
    OPERADOR_PASSWORD="<senha-do-operador>"

az webapp restart --name webapp-movesync-rm562975 --resource-group rg-movesync
```

> Todos esses comandos também estão no script **`scripts/infra-azure.sh`**, que lê as senhas de variáveis de ambiente e ainda executa o `scripts/script-bd.sql` (DDL das 6 tabelas + dados fictícios).

### 5. Release (Azure DevOps)

Na task **Deploy War to Azure App Service** da Release, selecione o **Runtime Stack `TOMCAT|11.0-java21`**. Se ficar vazio ou em Java SE, o container sobe sem encontrar o `.jar` e o site retorna **503**.

---

Agora vamos fazer login!
Se só entrarmos na página, vamos ter uma tela em branco. É necessário digitar na URL **"/web/login"**.
EX: https://webapp-movesync-rm562975.azurewebsites.net/web/login

Os usuários de teste são **"admin"** (acesso total) e **"operador"** (leituras, simulação e ajustes), com as senhas definidas nas variáveis de ambiente `ADMIN_PASSWORD` e `OPERADOR_PASSWORD` do passo 4.
