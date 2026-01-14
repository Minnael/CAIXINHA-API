# � API Controle de Gastos - Guia Rápido

> **Base URLs:**  
> - Auth Service: `http://localhost:3000`  
> - API Service: `http://localhost:8080`

---

## 🔥 Quick Start

### 1️⃣ Criar Usuário
```http
POST http://localhost:3000/api/register
Content-Type: application/json

{
  "login": "seu_usuario",
  "password": "sua_senha"
}
```

### 2️⃣ Fazer Login (obter token)
```http
POST http://localhost:3000/api/login
Content-Type: application/json

{
  "login": "seu_usuario",
  "password": "sua_senha"
}
```
**Copie o `accessToken` da resposta!**

### 3️⃣ Configurar Header de Autenticação
**Em TODAS as requisições para porta 8080, adicione:**
```
Authorization: Bearer SEU_ACCESS_TOKEN_AQUI
```

---

## 📑 Índice
- [Autenticação](#-autenticação) (porta 3000)
- [Categorias](#-categorias) (porta 8080)
- [Gastos](#-gastos) (porta 8080)
- [Fluxo de Teste](#-fluxo-de-teste-completo)

---

# 🔐 AUTENTICAÇÃO
**Base URL:** `http://localhost:3000/api`

## Registrar Usuário
```http
POST /register
Content-Type: application/json

{
  "login": "string",
  "password": "string"
}
```
**Response 201:**
```json
{
  "id": "string",
  "login": "string"
}
```

## Login (Obter Token)
```http
POST /login
Content-Type: application/json

{
  "login": "string",
  "password": "string"
}
```
**Response 200:**
```json
{
  "perfil": { "id": "string", "login": "string" },
  "accessToken": "string",  // ← USE ESTE TOKEN
  "expiresIn": 3600
}
```

---

# 🏷️ CATEGORIAS
**Base URL:** `http://localhost:8080/api/categorias`  
**Auth:** Obrigatória (Bearer Token)

## Criar Categoria
```http
POST /categorias
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Alimentação",           // Obrigatório
  "icone": "🍔",                    // Opcional
  "descricao": "Descrição aqui",   // Opcional
  "gastoMensal": 800.00            // Opcional
}
```
**Response 201:**
```json
{
  "id": "string",
  "nome": "Alimentação",
  "icone": "🍔",
  "descricao": "Descrição aqui",
  "gastoMensal": 800.00,
  "gastoAtual": 0.00,
  "totalGastos": 0,
  "criadoEm": "timestamp",
  "atualizadoEm": "timestamp"
}
```

## Listar Categorias
```http
GET /categorias
Authorization: Bearer {token}
```
**Response 200:** Array de categorias (campo `gastos` vem `null`)

## Buscar Categoria (com gastos)
```http
GET /categorias/{id}
Authorization: Bearer {token}
```
**Response 200:** Categoria completa com array de `gastos`

## Atualizar Categoria
```http
PUT /categorias/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Novo Nome",
  "icone": "🍕",
  "descricao": "Nova descrição",
  "gastoMensal": 1000.00
}
```

## Deletar Categoria
```http
DELETE /categorias/{id}
Authorization: Bearer {token}
```
**⚠️ Atenção:** Categoria não pode ter gastos vinculados!

---

# 💰 GASTOS
**Base URL:** `http://localhost:8080/api/gastos`  
**Auth:** Obrigatória (Bearer Token)

## Criar Gasto
```http
POST /gastos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Almoço",              // Obrigatório
  "descricao": "Descrição",      // Opcional
  "valor": 45.50,                // Obrigatório (> 0)
  "categoriaId": "string"        // Obrigatório (deve existir)
}
```
**Response 201:**
```json
{
  "id": "string",
  "nome": "Almoço",
  "descricao": "Descrição",
  "valor": 45.50,
  "categoriaId": "string",
  "categoriaNome": "Alimentação",
  "criadoEm": "timestamp",
  "atualizadoEm": "timestamp"
}
```

## Listar Todos os Gastos
```http
GET /gastos
Authorization: Bearer {token}
```
**Response 200:** Array de gastos

## Buscar Gasto por ID
```http
GET /gastos/{id}
Authorization: Bearer {token}
```

## Listar Gastos por Categoria
```http
GET /gastos/categoria/{categoriaId}
Authorization: Bearer {token}
```

## Atualizar Gasto
```http
PUT /gastos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "string",
  "descricao": "string",
  "valor": 120.00,
  "categoriaId": "string"
}
```

## Deletar Gasto
```http
DELETE /gastos/{id}
Authorization: Bearer {token}
```
**Response 204:** No Content

---

# 🎯 Fluxo de Uso Recomendado

## 1️⃣ Criar Categorias
```bash
# Categoria 1: Alimentação
POST /api/categorias
{
  "nome": "Alimentação",
  "icone": "🍔",
  "descricao": "Gastos com alimentação",
  "gastoMensal": 800.00
}

# Categoria 2: Transporte
POST /api/categorias
{
  "nome": "Transporte",
  "icone": "🚗",
  "descricao": "Gastos com transporte",
  "gastoMensal": 400.00
}
```

## 2️⃣ Criar Gastos
```bash
# Gasto 1
POST /api/gastos
{
  "nome": "Almoço",
  "descricao": "Restaurante do centro",
  "valor": 45.50,
  "categoriaId": 1
}

# Gasto 2
POST /api/gastos
{
  "nome": "Combustível",
  "valor": 200.00,
  "categoriaId": 2
}
```

## 3️⃣ Consultar Dados
```bash
# Listar categorias
GET /api/categorias

# Ver categoria com gastos
GET /api/categorias/1

# Listar gastos de uma categoria
GET /api/gastos/categoria/1
```

## 4️⃣ Atualizar Dados
```bash
# Atualizar meta mensal
PUT /api/categorias/1
{
  "nome": "Alimentação",
  "gastoMensal": 1000.00
}

# Atualizar gasto
PUT /api/gastos/1
{
  "nome": "Jantar",
  "valor": 120.00,
  "categoriaId": 1
}
```

## 5️⃣ Deletar Dados
```bash
# Deletar gastos primeiro
DELETE /api/gastos/1

# Depois deletar categoria
DELETE /api/categorias/1
```

---

# 📊 Análise de Gastos

## Acompanhar Meta vs Realidade

```bash
GET /api/categorias/1
```

**Resposta:**
```json
{
  "id": 1,
  "nome": "Alimentação",
  "gastoMensal": 800.00,    // ← Meta definida
  "gastoAtual": 595.50,     // ← Quanto já gastou
  "totalGastos": 4          // ← Número de gastos
}
```

### **Análise:**
- ✅ Meta: R$ 800,00
- 📊 Gasto atual: R$ 595,50
- 💰 Restante: R$ 204,50
- 📈 Percentual usado: 74,4%

---

# 🔗 Links Úteis

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **API Base URL**: http://localhost:8080

---

# ⚠️ Códigos de Resposta HTTP

| Código | Status | Descrição |
|--------|--------|-----------|
| 200 | OK | Requisição bem-sucedida |
| 201 | Created | Recurso criado com sucesso |
| 204 | No Content | Requisição bem-sucedida sem conteúdo |
| 400 | Bad Request | Dados inválidos ou regra de negócio violada |
| 404 | Not Found | Recurso não encontrado |
| 500 | Internal Server Error | Erro interno do servidor |

---

**🎉 API pronta para uso! Explore todos os endpoints e gerencie seus gastos com eficiência!**
🧪 FLUXO DE TESTE COMPLETO

## Passo 1: Autenticação
```http
# 1. Registrar
POST http://localhost:3000/api/register
{"login": "testuser", "password": "123456"}

# 2. Login (copie o accessToken)
POST http://localhost:3000/api/login
{"login": "testuser", "password": "123456"}
```

## Passo 2: Criar Categorias
```http
# Configure o header: Authorization: Bearer {SEU_TOKEN}

POST http://localhost:8080/api/categorias
{"nome": "Alimentação", "icone": "🍔", "gastoMensal": 800}

POST http://localhost:8080/api/categorias
{"nome": "Transporte", "icone": "🚗", "gastoMensal": 400}
```

## Passo 3: Criar Gastos
```http
# Copie o ID da categoria retornado no passo anterior

POST http://localhost:8080/api/gastos
{"nome": "Almoço", "valor": 45.50, "categoriaId": "{ID_CATEGORIA}"}

POST http://localhost:8080/api/gastos
{"nome": "Uber", "valor": 25.00, "categoriaId": "{ID_CATEGORIA}"}
```

## Passo 4: Consultar Dados
```http
# Listar categorias
GET http://localhost:8080/api/categorias

# Ver categoria com gastos
GET http://localhost:8080/api/categorias/{id}

# Listar gastos
GET http://localhost:8080/api/gastos
```

## Passo 5: Testar Multi-Tenancy
```http
# 1. Crie outro usuário e faça login
POST http://localhost:3000/api/register
{"login": "usuario2", "password": "123456"}

# 2. Use o novo token e liste categorias
GET http://localhost:8080/api/categorias
# Deve retornar array vazio - isolamento funcionando! ✅
```

---

## 📊 Monitoramento de Gastos

Ao consultar uma categoria, você verá:
```json
{
  "nome": "Alimentação",
  "gastoMensal": 800.00,   // Meta
  "gastoAtual": 595.50,    // Gastou
  "totalGastos": 4         // Quantidade
}
```

---

## 🔗 Links Úteis

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Auth Service**: http://localhost:3000
- **API Service**: http://localhost:8080

---

## ⚠️ HTTP Status Codes

| Code | Significado |
|------|-------------|
| 200 | OK |
| 201 | Criado |
| 204 | Sem conteúdo (deletado) |
| 400 | Dados inválidos |
| 401 | Não autenticado |
| 404 | Não encontrado |

---

**✅ Sistema pronto para testes