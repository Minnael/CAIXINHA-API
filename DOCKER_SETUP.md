# 🐳 Executando o Projeto com Docker

## 📋 Pré-requisitos

- Docker instalado (versão 20.10+)
- Docker Compose instalado (versão 2.0+)

Para verificar:
```bash
docker --version
docker-compose --version
```

---

## 🚀 Execução Rápida (3 passos)

### 1. Configurar variáveis de ambiente

```bash
# Copie o arquivo de exemplo
cp .env.example .env

# Edite o .env e configure JWT_SECRET
# IMPORTANTE: Gere uma chave aleatória segura
```

**Gerar JWT_SECRET seguro:**
```bash
# No terminal (se tiver Node.js):
node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"

# Ou use este online: https://www.grc.com/passwords.htm
```

Copie a chave gerada e cole no arquivo `.env`:
```env
JWT_SECRET=sua-chave-gerada-aqui-muito-longa-e-segura
```

### 2. Construir e iniciar os containers

```bash
# Constrói as imagens e inicia os serviços
docker-compose up --build -d
```

### 3. Verificar status

```bash
# Ver logs em tempo real
docker-compose logs -f

# Verificar containers rodando
docker-compose ps
```

**Serviços disponíveis:**
- 🔐 **Auth Service**: http://localhost:3000
- 💰 **API Service**: http://localhost:8080
- 📚 **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 🧪 Testando a Aplicação

### 1. Criar usuário

```bash
curl -X POST http://localhost:3000/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "usuario_teste",
    "password": "senha123"
  }'
```

### 2. Fazer login e obter token

```bash
curl -X POST http://localhost:3000/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "usuario_teste",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "perfil": {
    "id": "677e123abc...",
    "login": "usuario_teste"
  },
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**Copie o `accessToken` para usar nas próximas requisições!**

### 3. Criar uma categoria (com token)

```bash
TOKEN="cole-seu-token-aqui"

curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Alimentação",
    "icone": "🍔",
    "descricao": "Gastos com comida",
    "gastoMensal": 500.00
  }'
```

### 4. Listar categorias

```bash
curl -X GET http://localhost:8080/api/categorias \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Criar um gasto

```bash
# Primeiro pegue o ID da categoria criada no passo 3
CATEGORIA_ID="cole-id-da-categoria-aqui"

curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Almoço no restaurante",
    "descricao": "Pizza",
    "valor": 45.50,
    "categoriaId": "'$CATEGORIA_ID'"
  }'
```

---

## 📊 Comandos Úteis do Docker

### Gerenciar containers

```bash
# Iniciar serviços
docker-compose up -d

# Parar serviços
docker-compose down

# Reiniciar um serviço específico
docker-compose restart api-service

# Ver logs de um serviço específico
docker-compose logs -f auth-service
docker-compose logs -f api-service

# Verificar saúde dos containers
docker-compose ps
```

### Rebuild após mudanças no código

```bash
# Rebuild e restart
docker-compose up --build -d

# Rebuild apenas um serviço
docker-compose build api-service
docker-compose up -d api-service
```

### Limpar tudo

```bash
# Para e remove containers, redes
docker-compose down

# Remove também imagens
docker-compose down --rmi all

# Remove volumes (CUIDADO: deleta dados!)
docker-compose down -v
```

### Acessar shell dos containers

```bash
# Auth Service (Node.js)
docker exec -it auth-service sh

# API Service (Spring Boot)
docker exec -it api-service sh
```

---

## 🔍 Troubleshooting

### Container não inicia

```bash
# Ver logs detalhados
docker-compose logs auth-service
docker-compose logs api-service

# Verificar se portas estão em uso
netstat -an | findstr 3000
netstat -an | findstr 8080
```

### Erro de conexão MongoDB

- Verifique se as credenciais do MongoDB Atlas estão corretas
- Confirme que seu IP está na whitelist do MongoDB Atlas
- Teste a conexão: https://cloud.mongodb.com

### Erro "Token inválido"

- Confirme que `JWT_SECRET` é EXATAMENTE a mesma nos 2 serviços
- Verifique arquivo `.env`
- Rebuild os containers: `docker-compose up --build -d`

### API não aceita requisições

- Aguarde 60s após iniciar (Spring Boot demora para subir)
- Verifique health: `curl http://localhost:8080/actuator/health`
- Veja logs: `docker-compose logs -f api-service`

---

## 🌐 Endpoints Disponíveis

### Auth Service (Node.js) - porta 3000

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/register | Registrar novo usuário |
| POST | /api/login | Login e obter JWT |
| POST | /api/check | Validar token |
| GET | /health | Health check |

### API Service (Spring Boot) - porta 8080

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | /swagger-ui.html | Documentação interativa | Não |
| GET | /actuator/health | Health check | Não |
| GET | /api/categorias | Listar categorias | Sim |
| POST | /api/categorias | Criar categoria | Sim |
| GET | /api/categorias/{id} | Buscar categoria | Sim |
| PUT | /api/categorias/{id} | Atualizar categoria | Sim |
| DELETE | /api/categorias/{id} | Deletar categoria | Sim |
| GET | /api/gastos | Listar gastos | Sim |
| POST | /api/gastos | Criar gasto | Sim |
| GET | /api/gastos/{id} | Buscar gasto | Sim |
| PUT | /api/gastos/{id} | Atualizar gasto | Sim |
| DELETE | /api/gastos/{id} | Deletar gasto | Sim |
| GET | /api/gastos/categoria/{id} | Gastos por categoria | Sim |

---

## 📱 Conectando o App React Native

No seu app React Native, configure as URLs:

```javascript
// src/services/api.js
const AUTH_BASE_URL = 'http://SEU_IP_LOCAL:3000';  // Ex: http://192.168.1.100:3000
const API_BASE_URL = 'http://SEU_IP_LOCAL:8080';   // Ex: http://192.168.1.100:8080
```

**Para descobrir seu IP:**
```bash
# Windows
ipconfig

# Linux/Mac
ifconfig
```

---

## 🔐 Segurança em Produção

- [ ] Use HTTPS (configure reverse proxy com Nginx)
- [ ] Armazene secrets em serviço dedicado (AWS Secrets Manager, Azure Key Vault)
- [ ] Configure firewall e limitação de taxa (rate limiting)
- [ ] Implemente refresh tokens
- [ ] Ative logs de auditoria
- [ ] Configure backup automático do MongoDB

---

## 📈 Monitoramento

### Health Checks

```bash
# Auth Service
curl http://localhost:3000/health

# API Service
curl http://localhost:8080/actuator/health
```

### Métricas

Acesse http://localhost:8080/actuator para ver endpoints de monitoramento disponíveis.

---

**Projeto pronto para desenvolvimento e testes! 🚀**

Para produção, considere usar Kubernetes, Docker Swarm ou plataformas gerenciadas (AWS ECS, Azure Container Instances, Google Cloud Run).
