# API Ramos Somar — Guia para Flutter

Documentação para integração do app Flutter com o backend Spring Boot.

---

## 1. Base URL (emulador / dispositivo)

A API roda na máquina host na porta **8080**.

| Ambiente | Base URL |
|----------|----------|
| **Emulador Android** | `http://10.0.2.2:8080` |
| Simulador iOS | `http://localhost:8080` |
| Dispositivo físico (mesma rede Wi-Fi) | `http://IP_DA_SUA_MAQUINA:8080` |
| Swagger (navegador no PC) | `http://localhost:8080/swagger-ui/index.html` |

> No emulador Android, `localhost` aponta para o próprio emulador. Use sempre `10.0.2.2` para acessar o PC.

Prefixo de todas as rotas:

```text
/api/v1/ramossomar
```

Exemplo completo (Android emulator):

```text
http://10.0.2.2:8080/api/v1/ramossomar
```

---

## 2. Regras gerais

- JSON sempre em **snake_case**
- Content-Type: `application/json`
- Datas: `yyyy-MM-dd` ou `yyyy-MM-ddTHH:mm:ss`
- IDs: UUID (`"c22cf335-e634-4f01-8d7a-6a8bdcd9df17"`)
- Senhas nunca retornam na resposta
- Apoiadores usam **soft delete** (`data_exclusao`)

### Autenticação JWT

1. Faça login em `/auth/login` (única rota pública)
2. Guarde o `access_token`
3. Em todas as outras rotas, envie:

```http
Authorization: Bearer <access_token>
```

O token é válido **até o fim do dia** (`expira_em`).

### Usuário de teste

| Campo | Valor |
|-------|-------|
| E-mail | `admin@ramossomar.com` |
| Senha | `admin123` |
| Perfil | `ADMIN` |

---

## 3. Erro padrão

Todas as falhas seguem este formato:

```json
{
  "status_code": 401,
  "erro": "CREDENCIAIS_INVALIDAS",
  "mensagem": "E-mail ou senha inválidos",
  "timestamp": "2026-07-09T19:39:29"
}
```

| Campo | Uso no Flutter |
|-------|----------------|
| `status_code` | Código HTTP |
| `erro` | Código interno para lógica |
| `mensagem` | Texto para exibir ao usuário |
| `timestamp` | Momento do erro |

Códigos comuns: `400`, `401`, `403`, `404`, `500`.

---

## 4. Enums

### Perfil

```text
ADMIN
LIDER
```

### Intenção de voto

```text
INDECISO
SIMPATIZANTE
APOIADOR
CONFIRMADO
```

### Tipo de mídia

```text
IMAGEM
VIDEO
```

---

## 5. Endpoints

### 5.1 Autenticação

#### POST `/auth/login`

**Pública** — não envia token.

Request:

```json
{
  "email": "admin@ramossomar.com",
  "senha": "admin123"
}
```

Response `200`:

```json
{
  "access_token": "eyJhbGciOiJIUzUxMiJ9...",
  "refresh_token": "bf753c95-e5ea-4644-b56a-c2d5369fe068",
  "tipo_token": "Bearer",
  "expira_em": "2026-07-09T23:59:59",
  "id_usuario": "c22cf335-e634-4f01-8d7a-6a8bdcd9df17",
  "nome": "Administrador",
  "email": "admin@ramossomar.com",
  "perfil": "ADMIN"
}
```

---

### 5.2 Usuários — `/usuarios`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/usuarios/novo` | Criar |
| GET | `/usuarios` | Listar / filtrar |
| PUT | `/usuarios/alterar-dados` | Atualizar |
| DELETE | `/usuarios/apagar?id={uuid}` | Inativar |

Filtros GET (query): `nome`, `email`, `perfil`, `ativo`

Request criar:

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "perfil": "LIDER",
  "telefone": "41999999999",
  "imagem": "/uploads/usuarios/joao.jpg",
  "ativo": true
}
```

Request alterar (inclui `id`):

```json
{
  "id": "uuid-do-usuario",
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "novaSenha123",
  "perfil": "LIDER",
  "telefone": "41999999999",
  "imagem": "/uploads/usuarios/joao.jpg",
  "ativo": true
}
```

> `senha` no PUT é opcional. Se omitida ou vazia, a senha atual é mantida.

---

### 5.3 Apoiadores — `/apoiadores`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/apoiadores/novo` | Criar |
| GET | `/apoiadores` | Listar / filtrar |
| PUT | `/apoiadores/alterar-dados` | Atualizar (+ histórico automático) |
| DELETE | `/apoiadores/apagar?id={uuid}` | Soft delete |

Filtros GET: `nome`, `cidade`, `id_lider`, `intencao_voto`, `cpf`

Request criar:

```json
{
  "id_lider": "uuid-do-lider",
  "nome": "Maria Souza",
  "cpf": "12345678901",
  "data_nascimento": "1990-05-20",
  "telefone": "41988887777",
  "whatsapp": "41988887777",
  "cep": "83700000",
  "endereco": "Rua das Flores",
  "numero": "100",
  "complemento": "Apto 12",
  "bairro": "Centro",
  "cidade": "Araucária",
  "local_votacao": "Escola Municipal X",
  "intencao_voto": "CONFIRMADO",
  "observacoes": "Contato preferencial WhatsApp"
}
```

Exemplos de filtro:

```text
GET /apoiadores?nome=maria
GET /apoiadores?cidade=Araucaria
GET /apoiadores?id_lider=uuid
GET /apoiadores?intencao_voto=CONFIRMADO
```

---

### 5.4 Histórico de apoiadores — `/historico-apoiadores`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/historico-apoiadores/novo` | Criar (admin) |
| GET | `/historico-apoiadores` | Listar |
| PUT | `/historico-apoiadores/alterar-dados` | Alterar (admin) |
| DELETE | `/historico-apoiadores/apagar?id={uuid}` | Apagar (admin) |

Filtros GET: `id_apoiador`, `id_usuario`, `campo_alterado`

> O histórico é gerado automaticamente ao alterar/apagar apoiador. O POST manual é só administrativo.

---

### 5.5 Publicações — `/publicacoes`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/publicacoes/novo` | Criar |
| GET | `/publicacoes` | Listar |
| PUT | `/publicacoes/alterar-dados` | Alterar |
| DELETE | `/publicacoes/apagar?id={uuid}` | Apagar |

Filtros GET: `titulo`, `id_autor`

Request criar:

```json
{
  "id_autor": "uuid-do-usuario",
  "titulo": "Campanha de rua",
  "conteudo": "Texto da publicação...",
  "midia": "/uploads/publicacoes/foto.jpg",
  "tipo_midia": "IMAGEM"
}
```

> Se enviar `midia`, envie também `tipo_midia` (e vice-versa).

---

### 5.6 Tokens refresh — `/tokens-refresh`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/tokens-refresh/novo` | Criar |
| GET | `/tokens-refresh` | Listar |
| PUT | `/tokens-refresh/alterar-dados` | Alterar |
| DELETE | `/tokens-refresh/apagar?id={uuid}` | Apagar |

Filtros GET: `id_usuario`, `token`

---

## 6. Permissões (resumo)

| Recurso | ADMIN | LIDER |
|---------|-------|-------|
| Usuários (CRUD completo) | Sim | Não (só alterar o próprio) |
| Apoiadores | Todos | Só os do próprio `id_lider` |
| Publicações | Todas | Só as próprias |
| Histórico | Completo | Leitura dos seus apoiadores |
| Tokens | Todos | Só os próprios |

---

## 7. Configuração sugerida no Flutter

### `lib/core/api/api_config.dart`

```dart
import 'dart:io';

class ApiConfig {
  static String get baseUrl {
    if (Platform.isAndroid) {
      // Emulador Android -> host da máquina
      return 'http://10.0.2.2:8080/api/v1/ramossomar';
    }
    // iOS Simulator / desktop
    return 'http://localhost:8080/api/v1/ramossomar';
  }
}
```

> Em dispositivo físico Android/iOS, troque para o IP da máquina, ex.: `http://192.168.0.10:8080/api/v1/ramossomar`.

### Android — HTTP claro (necessário em debug)

Em `android/app/src/main/AndroidManifest.xml`, dentro de `<application>`:

```xml
android:usesCleartextTraffic="true"
```

### Exemplo com Dio

```dart
import 'package:dio/dio.dart';
import 'api_config.dart';

class ApiClient {
  late final Dio dio;

  ApiClient({String? token}) {
    dio = Dio(
      BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        headers: {
          'Content-Type': 'application/json',
          if (token != null) 'Authorization': 'Bearer $token',
        },
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
      ),
    );
  }

  Future<Map<String, dynamic>> login(String email, String senha) async {
    final response = await dio.post(
      '/auth/login',
      data: {'email': email, 'senha': senha},
    );
    return Map<String, dynamic>.from(response.data);
  }

  Future<List<dynamic>> listarApoiadores({
    String? nome,
    String? cidade,
    String? idLider,
    String? intencaoVoto,
  }) async {
    final response = await dio.get(
      '/apoiadores',
      queryParameters: {
        if (nome != null) 'nome': nome,
        if (cidade != null) 'cidade': cidade,
        if (idLider != null) 'id_lider': idLider,
        if (intencaoVoto != null) 'intencao_voto': intencaoVoto,
      },
    );
    return List<dynamic>.from(response.data);
  }
}
```

### Tratamento de erro

```dart
try {
  final login = await api.login(email, senha);
  // salvar login['access_token']
} on DioException catch (e) {
  final data = e.response?.data;
  final mensagem = data is Map ? data['mensagem'] : 'Erro de conexão';
  // exibir mensagem na UI
}
```

### Models: use snake_case no JSON

Com `json_serializable`:

```dart
@JsonSerializable(fieldRename: FieldRename.snake)
class LoginResponse {
  final String accessToken;
  final String refreshToken;
  final String tipoToken;
  final String expiraEm;
  final String idUsuario;
  final String nome;
  final String email;
  final String perfil;

  LoginResponse({
    required this.accessToken,
    required this.refreshToken,
    required this.tipoToken,
    required this.expiraEm,
    required this.idUsuario,
    required this.nome,
    required this.email,
    required this.perfil,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) =>
      _$LoginResponseFromJson(json);
}
```

---

## 8. Checklist rápido para o emulador

1. Backend rodando no PC (`http://localhost:8080`)
2. No Flutter, base URL = `http://10.0.2.2:8080/api/v1/ramossomar`
3. `usesCleartextTraffic="true"` no Android
4. Login com `admin@ramossomar.com` / `admin123`
5. Salvar `access_token` e enviar no header `Authorization: Bearer ...`
6. Em erros, ler o campo `mensagem` do JSON padrão

---

## 9. Swagger

Documentação interativa (no PC):

- UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI: http://localhost:8080/v3/api-docs

No Swagger, use **Authorize** e cole o `access_token` (sem a palavra `Bearer`; o Swagger adiciona).
