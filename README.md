# API de Controle de Gastos

API REST para gerenciamento de gastos por categoria, desenvolvida com Spring Boot 4.0.1 e Java 21 usando MongoDB Atlas.

## 🚀 Tecnologias

- **Java 21** (LTS)
- **Spring Boot 4.0.1**
- **Spring Data MongoDB**
- **MongoDB Atlas** (Cloud Database)
- **Lombok**
- **Bean Validation**
- **SpringDoc OpenAPI 3**
- **Docker**
- **Maven**

## 📋 Funcionalidades

### Categorias
- ✅ Criar categoria com meta mensal de gastos
- ✅ Listar todas as categorias
- ✅ Buscar categoria por ID (com gastos vinculados)
- ✅ Atualizar categoria e meta mensal
- ✅ Deletar categoria (apenas se não houver gastos vinculados)
- ✅ Cálculo automático do gasto atual vs meta mensal

### Gastos
- ✅ Criar gasto vinculado a categoria
- ✅ Listar todos os gastos
- ✅ Listar gastos por categoria
- ✅ Buscar gasto por ID
- ✅ Atualizar gasto (incluindo mudança de categoria)
- ✅ Deletar gasto
- ✅ Cálculo em tempo real do gasto atual da categoria

## 🏗️ Arquitetura

```
src/main/java/com/minnael/controle_gastos/
├── controller/       # Endpoints REST
├── service/          # Lógica de negócio
├── repository/       # Acesso a dados (JPA)
├── entity/           # Entidades JPA
├── dto/              # Data Transfer Objects
├── mapper/           # Conversão Entity <-> DTO
└── exception/        # Tratamento de exceções
```

## 🐳 Como Executar com Docker

### Pré-requisitos
- Docker
- Conta no MongoDB Atlas com cluster configurado

### Configuração do MongoDB Atlas

1. **Crie um cluster gratuito** em [mongodb.com/atlas](https://www.mongodb.com/atlas)
2. **Configure o acesso**: 
   - Adicione seu IP à lista de IPs permitidos (ou use `0.0.0.0/0` para permitir qualquer IP)
   - Crie um usuário do banco de dados
3. **Obtenha a connection string** no formato:
   ```
   mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>
   ```
4. **Configure no `application.properties`**:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/controle_gastos
   ```

### Executar a aplicação

```bash
# Clone o repositório (se necessário)
cd controle-gastos

# Construir e iniciar o container
docker-compose up -d --build

# Verificar logs
docker-compose logs -f app

# Parar o container
docker-compose down
```

A aplicação estará disponível em: **http://localhost:8080**

### 📚 Documentação Swagger

Acesse a documentação interativa da API:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📡 Endpoints da API

### Categorias

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/categorias` | Criar nova categoria |
| GET | `/api/categorias` | Listar todas as categorias |
| GET | `/api/categorias/{id}` | Buscar categoria por ID |
| PUT | `/api/categorias/{id}` | Atualizar categoria |
| DELETE | `/api/categorias/{id}` | Deletar categoria |

### Gastos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/gastos` | Criar novo gasto |
| GET | `/api/gastos` | Listar todos os gastos |
| GET | `/api/gastos/{id}` | Buscar gasto por ID |
| GET | `/api/gastos/categoria/{categoriaId}` | Listar gastos por categoria |
| PUT | `/api/gastos/{id}` | Atualizar gasto |
| DELETE | `/api/gastos/{id}` | Deletar gasto |

## 📝 Exemplos de Uso

### Criar Categoria

```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Alimentação",
    "icone": "🍔",
    "descricao": "Gastos com alimentação",
    "gastoMensal": 500.00
  }'
```

**Resposta:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "nome": "Alimentação",
  "icone": "🍔",
  "descricao": "Gastos com alimentação",
  "gastoMensal": 500.00,
  "gastoAtual": 0.00,
  "totalGastos": 0,
  "criadoEm": "2026-01-13T20:00:00",
  "atualizadoEm": "2026-01-13T20:00:00"
}
```

### Criar Gasto

```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Almoço",
    "descricao": "Restaurante do centro",
    "valor": 45.50,
    "categoriaId": "507f1f77bcf86cd799439011"
  }'
```

### Listar Categorias

```bash
curl http://localhost:8080/api/categorias
```

### Buscar Categoria com Gastos

```bash
curl http://localhost:8080/api/categorias/507f1f77bcf86cd799439011
```

## 🗄️ Modelo de Dados

### Categoria
```json
{
  "id": "507f1f77bcf86cd799439011",
  "nome": "Alimentação",
  "icone": "🍔",
  "descricao": "Gastos com alimentação",
  "gastoMensal": 500.00,
  "gastoAtual": 250.00,
  "totalGastos": 5,
  "criadoEm": "2026-01-13T10:00:00",
  "atualizadoEm": "2026-01-13T15:30:00",
  "gastos": [...]
}
```

**Campos:**
- **gastoMensal**: Meta/limite mensal definido pelo usuário
- **gastoAtual**: Quanto já foi gasto no mês (calculado automaticamente)
- **totalGastos**: Número de gastos vinculados

### Gasto
```json
{
  "id": "507f191e810c19729de860ea",
  "nome": "Almoço",
  "descricao": "Restaurante do centro",
  "valor": 45.50,
  "categoriaId": "507f1f77bcf86cd799439011",
  "categoriaNome": "Alimentação",
  "criadoEm": "2026-01-13T12:00:00",
  "atualizadoEm": "2026-01-13T12:00:00"
}
```

## ✅ Validações

### Categoria
- **Nome**: obrigatório, máximo 100 caracteres
- **Ícone**: opcional, máximo 50 caracteres
- **Descrição**: opcional, máximo 500 caracteres
- **Gasto Mensal**: opcional, deve ser ≥ 0 (meta/limite mensal)

### Gasto
- **Nome**: obrigatório, máximo 100 caracteres
- **Descrição**: opcional, máximo 500 caracteres
- **Valor**: obrigatório, deve ser > 0
- **Categoria ID**: obrigatório, categoria deve existir

## 🛡️ Tratamento de Erros

A API retorna erros padronizados:

```json
{
  "timestamp": "2026-01-13T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Categoria não encontrada com o ID: 99",
  "path": "/api/categorias/99"
}
```

## 🔧 Configuração do Banco de Dados

As configurações do PostgreSQL estão no `docker-compose.yml`:

- **Database**: controle_gastos
- **Username**: admin
- **Password**: admin123
- **Port**: 5432

## Meta vs Realidade**: `gastoMensal` (meta) vs `gastoAtual` (calculado)
- **Cálculo Dinâmico**: Gasto atual calculado em tempo real
- **Timestamps Automáticos**: createdAt e updatedAt gerenciados pelo JPA
- **Connection Pool**: HikariCP configurado para melhor performance
- **Health Checks**: Containers com verificação de saúde
- **Documentação Swagger**: Interface interativa completadas
- **Lazy Loading**: Otimização de queries com fetch LAZY
- **Cálculo Automático**: Gasto mensal calculado automaticamente
- **Timestamps Automáticos**: createdAt e updatedAt gerenciados pelo JPA
- **Connection Pool**: HikariCP configurado para melhor performance
- **Health Checks**: Containers com verificação de saúde

## 🏆 Melhores Práticas Implementadas

✅ Separação de responsabilidades (Controller → Service → Repository)  
✅ DTOs para não expor entidades diretamente  
✅ Validação de entrada com Bean Validation  
✅ Tratamento global de exceções  
✅ Logs estruturados com SLF4J  
✅ Docker multi-stage build (reduz tamanho da imagem)  
✅ Usuario não-root no container (segurança)  
✅ Health checks configurados  
✅ Relacionamentos JPA bem gerenciados  

## 📞 Suporte

Para dúvidas ou sugestões, abra uma issue no repositório.

---

**Desenvolvido com ❤️ usando Spring Boot**
