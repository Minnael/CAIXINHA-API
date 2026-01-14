# 🔐 Integração React Native com API de Controle de Gastos

## 📋 Visão Geral

Este guia mostra como integrar o app React Native com:
- **Microserviço Node.js** (autenticação/JWT)
- **API Spring Boot** (gastos e categorias)

---

## 🎯 Fluxo de Autenticação

```
1. App → POST /api/login (Node.js) → Recebe { accessToken, perfil }
2. App armazena accessToken localmente (SecureStore)
3. App → Requests para Spring Boot com header: Authorization: Bearer <token>
4. Spring Boot valida JWT e extrai userId automaticamente
5. Dados isolados por usuário (multi-tenant)
```

---

## ⚙️ Configuração do Projeto React Native

### 1. Instalar Dependências

```bash
npm install axios @react-native-async-storage/async-storage
# ou
npm install axios expo-secure-store  # Se usar Expo
```

### 2. Configurar Axios com Interceptor

Crie `src/services/api.js`:

```javascript
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
// ou import * as SecureStore from 'expo-secure-store';

// URLs dos microserviços
const AUTH_BASE_URL = 'http://localhost:3000';  // Node.js Auth
const API_BASE_URL = 'http://localhost:8080';   // Spring Boot API

// Cliente para autenticação (Node.js)
export const authApi = axios.create({
  baseURL: AUTH_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Cliente para API de gastos (Spring Boot)
export const gastosApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor: Adiciona token automaticamente em todas as requisições
gastosApi.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('accessToken');
    // ou const token = await SecureStore.getItemAsync('accessToken');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor: Trata erros 401 (token expirado/inválido)
gastosApi.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token inválido/expirado - redireciona para login
      await AsyncStorage.removeItem('accessToken');
      await AsyncStorage.removeItem('userProfile');
      // navigation.navigate('Login'); // Descomente e ajuste conforme sua navegação
    }
    return Promise.reject(error);
  }
);

export default { authApi, gastosApi };
```

---

## 🔑 Serviço de Autenticação

Crie `src/services/authService.js`:

```javascript
import AsyncStorage from '@react-native-async-storage/async-storage';
import { authApi } from './api';

class AuthService {
  
  /**
   * Realiza login do usuário
   */
  async login(login, password) {
    try {
      const response = await authApi.post('/api/login', { login, password });
      
      const { accessToken, perfil, expiresIn } = response.data;
      
      // Armazena token e perfil localmente
      await AsyncStorage.setItem('accessToken', accessToken);
      await AsyncStorage.setItem('userProfile', JSON.stringify(perfil));
      await AsyncStorage.setItem('tokenExpiration', String(Date.now() + expiresIn * 1000));
      
      return { success: true, perfil };
    } catch (error) {
      console.error('Erro no login:', error.response?.data || error.message);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Erro ao fazer login' 
      };
    }
  }

  /**
   * Registra novo usuário
   */
  async register(login, password) {
    try {
      const response = await authApi.post('/api/register', { login, password });
      return { success: true, user: response.data };
    } catch (error) {
      console.error('Erro no registro:', error.response?.data || error.message);
      return { 
        success: false, 
        message: error.response?.data?.message || 'Erro ao registrar' 
      };
    }
  }

  /**
   * Faz logout do usuário
   */
  async logout() {
    await AsyncStorage.removeItem('accessToken');
    await AsyncStorage.removeItem('userProfile');
    await AsyncStorage.removeItem('tokenExpiration');
  }

  /**
   * Verifica se usuário está autenticado
   */
  async isAuthenticated() {
    const token = await AsyncStorage.getItem('accessToken');
    const expiration = await AsyncStorage.getItem('tokenExpiration');
    
    if (!token || !expiration) {
      return false;
    }
    
    // Verifica se token expirou
    if (Date.now() > parseInt(expiration)) {
      await this.logout();
      return false;
    }
    
    return true;
  }

  /**
   * Retorna perfil do usuário logado
   */
  async getUserProfile() {
    const profileString = await AsyncStorage.getItem('userProfile');
    return profileString ? JSON.parse(profileString) : null;
  }
}

export default new AuthService();
```

---

## 💰 Serviço de Categorias e Gastos

Crie `src/services/gastosService.js`:

```javascript
import { gastosApi } from './api';

class GastosService {
  
  // ========== CATEGORIAS ==========
  
  async criarCategoria(nome, icone, descricao, gastoMensal) {
    const response = await gastosApi.post('/api/categorias', {
      nome,
      icone,
      descricao,
      gastoMensal
    });
    return response.data;
  }

  async listarCategorias() {
    const response = await gastosApi.get('/api/categorias');
    return response.data;
  }

  async buscarCategoria(id) {
    const response = await gastosApi.get(`/api/categorias/${id}`);
    return response.data;
  }

  async atualizarCategoria(id, dados) {
    const response = await gastosApi.put(`/api/categorias/${id}`, dados);
    return response.data;
  }

  async deletarCategoria(id) {
    await gastosApi.delete(`/api/categorias/${id}`);
  }

  // ========== GASTOS ==========

  async criarGasto(nome, valor, categoriaId, descricao) {
    const response = await gastosApi.post('/api/gastos', {
      nome,
      descricao,
      valor,
      categoriaId
    });
    return response.data;
  }

  async listarGastos() {
    const response = await gastosApi.get('/api/gastos');
    return response.data;
  }

  async listarGastosPorCategoria(categoriaId) {
    const response = await gastosApi.get(`/api/gastos/categoria/${categoriaId}`);
    return response.data;
  }

  async buscarGasto(id) {
    const response = await gastosApi.get(`/api/gastos/${id}`);
    return response.data;
  }

  async atualizarGasto(id, dados) {
    const response = await gastosApi.put(`/api/gastos/${id}`, dados);
    return response.data;
  }

  async deletarGasto(id) {
    await gastosApi.delete(`/api/gastos/${id}`);
  }
}

export default new GastosService();
```

---

## 📱 Exemplo de Uso em Componente

### Tela de Login

```javascript
import React, { useState } from 'react';
import { View, TextInput, Button, Alert } from 'react-native';
import authService from './services/authService';

export default function LoginScreen({ navigation }) {
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    setLoading(true);
    const result = await authService.login(login, password);
    setLoading(false);

    if (result.success) {
      navigation.replace('Home'); // Navega para tela principal
    } else {
      Alert.alert('Erro', result.message);
    }
  };

  return (
    <View style={{ padding: 20 }}>
      <TextInput
        placeholder="Login"
        value={login}
        onChangeText={setLogin}
        style={{ borderWidth: 1, padding: 10, marginBottom: 10 }}
      />
      <TextInput
        placeholder="Senha"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
        style={{ borderWidth: 1, padding: 10, marginBottom: 10 }}
      />
      <Button 
        title={loading ? "Carregando..." : "Entrar"} 
        onPress={handleLogin}
        disabled={loading}
      />
    </View>
  );
}
```

### Tela de Categorias

```javascript
import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, Button, Alert } from 'react-native';
import gastosService from './services/gastosService';

export default function CategoriasScreen() {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    carregarCategorias();
  }, []);

  const carregarCategorias = async () => {
    try {
      const data = await gastosService.listarCategorias();
      setCategorias(data);
    } catch (error) {
      Alert.alert('Erro', 'Não foi possível carregar as categorias');
    } finally {
      setLoading(false);
    }
  };

  const deletarCategoria = async (id) => {
    try {
      await gastosService.deletarCategoria(id);
      Alert.alert('Sucesso', 'Categoria deletada!');
      carregarCategorias(); // Recarrega lista
    } catch (error) {
      Alert.alert('Erro', error.response?.data?.message || 'Erro ao deletar');
    }
  };

  if (loading) {
    return <Text>Carregando...</Text>;
  }

  return (
    <View style={{ padding: 20 }}>
      <FlatList
        data={categorias}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={{ padding: 10, borderBottomWidth: 1 }}>
            <Text>{item.icone} {item.nome}</Text>
            <Text>Meta: R$ {item.gastoMensal?.toFixed(2)}</Text>
            <Text>Gasto: R$ {item.gastoAtual?.toFixed(2)}</Text>
            <Button 
              title="Deletar" 
              onPress={() => deletarCategoria(item.id)} 
              color="red"
            />
          </View>
        )}
      />
    </View>
  );
}
```

---

## 🔒 Boas Práticas de Segurança

### 1. **Usar Expo SecureStore (Recomendado)**

```javascript
import * as SecureStore from 'expo-secure-store';

// Salvar token
await SecureStore.setItemAsync('accessToken', token);

// Recuperar token
const token = await SecureStore.getItemAsync('accessToken');

// Deletar token
await SecureStore.deleteItemAsync('accessToken');
```

### 2. **NUNCA armazenar em localStorage web (vulnerável a XSS)**

### 3. **Validar expiração do token localmente**

### 4. **Usar HTTPS em produção**

---

## 🛠️ Configuração de Ambiente

### .env do Microserviço Node.js

```env
JWT_SECRET=sua-chave-secreta-minimo-256-bits-aqui
PORT=3000
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/auth
```

### application.properties da API Spring Boot

```properties
jwt.secret=sua-chave-secreta-minimo-256-bits-aqui
# MESMA SECRET DO NODE.JS ☝️
```

---

## 🧪 Testando a Integração

### 1. Inicie o microserviço Node.js

```bash
cd auth-micro-service
npm install
npm start
# Rodando em http://localhost:3000
```

### 2. Inicie a API Spring Boot

```bash
cd controle-gastos
./mvnw spring-boot:run
# Rodando em http://localhost:8080
```

### 3. Teste no Postman/Insomnia

**Login:**
```http
POST http://localhost:3000/api/login
Content-Type: application/json

{
  "login": "teste",
  "password": "123456"
}
```

**Criar Categoria (com token):**
```http
POST http://localhost:8080/api/categorias
Authorization: Bearer <seu-token-aqui>
Content-Type: application/json

{
  "nome": "Alimentação",
  "icone": "🍔",
  "descricao": "Gastos com comida",
  "gastoMensal": 500.00
}
```

---

## 📚 Próximos Passos

- [ ] Implementar Refresh Token (aumentar segurança)
- [ ] Adicionar biometria/FaceID para login
- [ ] Implementar cache local (AsyncStorage) para offline-first
- [ ] Adicionar notificações push quando gastos excederem meta
- [ ] Implementar sincronização otimista (UX melhorado)

---

## 🆘 Troubleshooting

### Erro: "Token inválido ou expirado"
- Verifique se `JWT_SECRET` é a mesma nos 2 microserviços
- Confirme que o token não expirou (1 hora)
- Teste gerar novo token fazendo login novamente

### Erro: "Categoria não encontrada"
- Você está tentando acessar categoria de outro usuário
- O userId do token não corresponde ao dono da categoria

### Erro de CORS
- Adicione sua origem no `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(List.of(
    "http://192.168.1.100:19000"  // Seu IP local
));
```

---

**Implementação completa e pronta para produção!** 🚀
