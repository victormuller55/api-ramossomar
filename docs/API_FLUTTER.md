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
- Locais de votação usam **inativação lógica** (`ativo = false`)
- Cidades são **somente leitura** (importadas automaticamente)
- Imagens de perfil e publicações são **arquivos enviados** (multipart), salvos no servidor e **comprimidas automaticamente** (JPEG)
- Publicações aceitam **somente imagens** (1 a 3), nunca links externos
- Arquivos estáticos públicos em `/uploads/**`

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
| POST | `/usuarios/upload-imagem?id={uuid}` | Upload da foto de perfil |
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
  "ativo": true
}
```

> `senha` no PUT é opcional. Se omitida ou vazia, a senha atual é mantida.  
> **Não envie URL de imagem** no JSON. Use o upload abaixo.

#### Upload de imagem de perfil

```http
POST /usuarios/upload-imagem?id={uuid}
Content-Type: multipart/form-data
Authorization: Bearer <token>

campo: imagem  (arquivo JPG/PNG/WEBP/GIF)
```

- A imagem é **comprimida** no servidor e salva como `.jpg`
- O campo `imagem` na resposta fica assim: `/uploads/usuarios/{uuid}.jpg`
- URL completa no app: `{host}/uploads/usuarios/{uuid}.jpg` (rota pública, sem token)

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
| POST | `/publicacoes/novo` | Criar (texto) |
| GET | `/publicacoes` | Listar |
| PUT | `/publicacoes/alterar-dados` | Alterar (texto) |
| POST | `/publicacoes/upload-imagens?id={uuid}` | Upload de 1 a 3 imagens |
| DELETE | `/publicacoes/apagar?id={uuid}` | Apagar |

Filtros GET: `titulo`, `id_autor`

Request criar (somente texto — **sem links de imagem**):

```json
{
  "id_autor": "uuid-do-usuario",
  "titulo": "Campanha de rua",
  "conteudo": "Texto da publicação..."
}
```

Request alterar:

```json
{
  "id": "uuid-da-publicacao",
  "id_autor": "uuid-do-usuario",
  "titulo": "Campanha de rua",
  "conteudo": "Texto atualizado..."
}
```

#### Upload de imagens da publicação (obrigatório se quiser fotos)

```http
POST /publicacoes/upload-imagens?id={uuid}
Content-Type: multipart/form-data
Authorization: Bearer <token>

campo: imagens  (1 a 3 arquivos — somente imagem)
```

Regras:

- **Somente imagens** (JPG, PNG, WEBP, GIF) — vídeos e links externos não são aceitos
- Máximo **3 imagens** por publicação
- Cada imagem é **comprimida** no servidor (redimensionada até 1920px e salva em JPEG qualidade 0.75)
- O upload **substitui** as imagens anteriores da publicação
- Response traz `imagens` como lista de caminhos relativos

Response exemplo:

```json
{
  "id": "uuid-da-publicacao",
  "id_autor": "uuid-do-usuario",
  "nome_autor": "João Silva",
  "titulo": "Campanha de rua",
  "conteudo": "Texto da publicação...",
  "imagens": [
    "/uploads/publicacoes/a1.jpg",
    "/uploads/publicacoes/a2.jpg"
  ],
  "data_criacao": "2026-07-11T00:00:00",
  "data_atualizacao": "2026-07-11T00:05:00"
}
```

Fluxo no Flutter:

1. `POST /publicacoes/novo` → guarda o `id`
2. `POST /publicacoes/upload-imagens?id=...` com `FormData` e até 3 arquivos no campo `imagens`
3. Exibir com `Image.network('$host${caminho}')` (ex.: `http://10.0.2.2:7000/uploads/publicacoes/a1.jpg`)

Exemplo Dio (upload):

```dart
Future<Map<String, dynamic>> uploadImagensPublicacao(
  String id,
  List<String> caminhosArquivos,
) async {
  final formData = FormData();
  for (final caminho in caminhosArquivos.take(3)) {
    formData.files.add(MapEntry(
      'imagens',
      await MultipartFile.fromFile(caminho),
    ));
  }
  final response = await dio.post(
    '/publicacoes/upload-imagens',
    queryParameters: {'id': id},
    data: formData,
  );
  return Map<String, dynamic>.from(response.data);
}

Future<Map<String, dynamic>> uploadImagemPerfil(String id, String caminhoArquivo) async {
  final formData = FormData.fromMap({
    'imagem': await MultipartFile.fromFile(caminhoArquivo),
  });
  final response = await dio.post(
    '/usuarios/upload-imagem',
    queryParameters: {'id': id},
    data: formData,
  );
  return Map<String, dynamic>.from(response.data);
}
```

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

### 5.7 Cidades — `/cidades`

Dados importados automaticamente na inicialização da API (municípios de Goiás via IBGE).  
**Somente leitura** — não há criar/editar/apagar.

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/cidades` | Listar / pesquisar por nome |
| GET | `/cidades/por-id?id={uuid}` | Buscar por ID |
| GET | `/cidades/por-codigo-ibge?codigo_ibge={codigo}` | Buscar por código IBGE |

Filtros GET `/cidades`: `nome`, `uf`

Exemplos:

```text
GET /cidades
GET /cidades?nome=goiânia
GET /cidades?uf=GO
GET /cidades/por-id?id=550e8400-e29b-41d4-a716-446655440000
GET /cidades/por-codigo-ibge?codigo_ibge=5208707
```

Response item (`200`):

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "codigo_ibge": "5208707",
  "nome": "Goiânia",
  "uf": "GO",
  "data_criacao": "2026-07-10T23:00:00",
  "data_atualizacao": "2026-07-10T23:00:00"
}
```

> `GET /cidades` retorna **lista**.  
> `GET /cidades/por-id` e `GET /cidades/por-codigo-ibge` retornam **objeto único** (ou `404`).

---

### 5.8 Locais de votação — `/locais-votacao`

Locais oficiais do TSE, vinculados a uma cidade (`id_cidade`).  
Importados automaticamente na inicialização. O app pode consultar e também cadastrar/editar/inativar.

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/locais-votacao/novo` | Criar |
| GET | `/locais-votacao` | Listar / filtrar |
| GET | `/locais-votacao/por-id?id={uuid}` | Buscar por ID |
| PUT | `/locais-votacao/alterar-dados` | Atualizar |
| DELETE | `/locais-votacao/apagar?id={uuid}` | Inativar (`ativo = false`) |

Filtros GET `/locais-votacao`:

| Query | Exemplo | Uso |
|-------|---------|-----|
| `nome` | `escola` | Pesquisa por nome |
| `id_cidade` | `uuid` | Locais de uma cidade |
| `codigo_ibge` | `5208707` | Locais pelo IBGE da cidade |
| `zona_eleitoral` | `1` | Filtrar por zona |
| `ativo` | `true` | Só ativos (recomendado na UI) |

Exemplos:

```text
GET /locais-votacao
GET /locais-votacao?ativo=true
GET /locais-votacao?nome=escola
GET /locais-votacao?id_cidade=550e8400-e29b-41d4-a716-446655440000
GET /locais-votacao?codigo_ibge=5208707
GET /locais-votacao?zona_eleitoral=1&ativo=true
GET /locais-votacao/por-id?id=uuid-do-local
```

Request criar (`POST /locais-votacao/novo`):

```json
{
  "codigo_tse": "93734-1-1015",
  "nome": "ESCOLA MUNICIPAL CENTRO",
  "endereco": "RUA DAS FLORES, 100",
  "bairro": "CENTRO",
  "cep": "74000000",
  "zona_eleitoral": "1",
  "latitude": -16.6869,
  "longitude": -49.2648,
  "ativo": true,
  "id_cidade": "550e8400-e29b-41d4-a716-446655440000"
}
```

Request alterar (`PUT /locais-votacao/alterar-dados`) — inclui `id`:

```json
{
  "id": "uuid-do-local",
  "codigo_tse": "93734-1-1015",
  "nome": "ESCOLA MUNICIPAL CENTRO",
  "endereco": "RUA DAS FLORES, 100",
  "bairro": "CENTRO",
  "cep": "74000000",
  "zona_eleitoral": "1",
  "latitude": -16.6869,
  "longitude": -49.2648,
  "ativo": true,
  "id_cidade": "550e8400-e29b-41d4-a716-446655440000"
}
```

Campos obrigatórios no create/update: `codigo_tse`, `nome`, `endereco`, `zona_eleitoral`, `id_cidade`.  
No update, `ativo` também é obrigatório.  
Opcionais: `bairro`, `cep`, `latitude`, `longitude` (e `ativo` no create; default `true`).

Response item (`200` / `201`):

```json
{
  "id": "uuid-do-local",
  "codigo_tse": "93734-1-1015",
  "nome": "ESCOLA MUNICIPAL CENTRO",
  "endereco": "RUA DAS FLORES, 100",
  "bairro": "CENTRO",
  "cep": "74000000",
  "zona_eleitoral": "1",
  "latitude": -16.68690000,
  "longitude": -49.26480000,
  "ativo": true,
  "id_cidade": "550e8400-e29b-41d4-a716-446655440000",
  "nome_cidade": "Goiânia",
  "codigo_ibge": "5208707",
  "uf": "GO",
  "data_criacao": "2026-07-10T23:00:00",
  "data_atualizacao": "2026-07-10T23:00:00"
}
```

> `DELETE` retorna `204` sem body. O registro **não é apagado fisicamente** — apenas `ativo` vira `false`.  
> Na listagem do app, use sempre `?ativo=true` para ocultar inativos.  
> Nunca envie o nome da cidade no body do local: use apenas `id_cidade`.

Fluxo sugerido no Flutter (cadastro de apoiador / seleção de local):

1. `GET /cidades?nome=...` → usuário escolhe a cidade  
2. `GET /locais-votacao?id_cidade={id}&ativo=true` → lista os locais da cidade  
3. Opcional: filtrar por `zona_eleitoral` ou `nome`

---

## 6. Permissões (resumo)

| Recurso | ADMIN | LIDER |
|---------|-------|-------|
| Usuários (CRUD completo) | Sim | Não (só alterar o próprio) |
| Apoiadores | Todos | Só os do próprio `id_lider` |
| Publicações | Todas | Só as próprias |
| Histórico | Completo | Leitura dos seus apoiadores |
| Tokens | Todos | Só os próprios |
| Cidades | Leitura | Leitura |
| Locais de votação | CRUD | CRUD |

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

  // --- Cidades ---

  Future<List<dynamic>> listarCidades({String? nome, String? uf}) async {
    final response = await dio.get(
      '/cidades',
      queryParameters: {
        if (nome != null) 'nome': nome,
        if (uf != null) 'uf': uf,
      },
    );
    return List<dynamic>.from(response.data);
  }

  Future<Map<String, dynamic>> buscarCidadePorId(String id) async {
    final response = await dio.get(
      '/cidades/por-id',
      queryParameters: {'id': id},
    );
    return Map<String, dynamic>.from(response.data);
  }

  Future<Map<String, dynamic>> buscarCidadePorCodigoIbge(String codigoIbge) async {
    final response = await dio.get(
      '/cidades/por-codigo-ibge',
      queryParameters: {'codigo_ibge': codigoIbge},
    );
    return Map<String, dynamic>.from(response.data);
  }

  // --- Locais de votação ---

  Future<List<dynamic>> listarLocaisVotacao({
    String? nome,
    String? idCidade,
    String? codigoIbge,
    String? zonaEleitoral,
    bool? ativo,
  }) async {
    final response = await dio.get(
      '/locais-votacao',
      queryParameters: {
        if (nome != null) 'nome': nome,
        if (idCidade != null) 'id_cidade': idCidade,
        if (codigoIbge != null) 'codigo_ibge': codigoIbge,
        if (zonaEleitoral != null) 'zona_eleitoral': zonaEleitoral,
        if (ativo != null) 'ativo': ativo,
      },
    );
    return List<dynamic>.from(response.data);
  }

  Future<Map<String, dynamic>> buscarLocalVotacaoPorId(String id) async {
    final response = await dio.get(
      '/locais-votacao/por-id',
      queryParameters: {'id': id},
    );
    return Map<String, dynamic>.from(response.data);
  }

  Future<Map<String, dynamic>> criarLocalVotacao(Map<String, dynamic> body) async {
    final response = await dio.post('/locais-votacao/novo', data: body);
    return Map<String, dynamic>.from(response.data);
  }

  Future<Map<String, dynamic>> alterarLocalVotacao(Map<String, dynamic> body) async {
    final response = await dio.put('/locais-votacao/alterar-dados', data: body);
    return Map<String, dynamic>.from(response.data);
  }

  Future<void> apagarLocalVotacao(String id) async {
    await dio.delete(
      '/locais-votacao/apagar',
      queryParameters: {'id': id},
    );
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

@JsonSerializable(fieldRename: FieldRename.snake)
class Cidade {
  final String id;
  final String codigoIbge;
  final String nome;
  final String uf;
  final String dataCriacao;
  final String dataAtualizacao;

  Cidade({
    required this.id,
    required this.codigoIbge,
    required this.nome,
    required this.uf,
    required this.dataCriacao,
    required this.dataAtualizacao,
  });

  factory Cidade.fromJson(Map<String, dynamic> json) => _$CidadeFromJson(json);
}

@JsonSerializable(fieldRename: FieldRename.snake)
class LocalVotacao {
  final String id;
  final String codigoTse;
  final String nome;
  final String endereco;
  final String? bairro;
  final String? cep;
  final String zonaEleitoral;
  final double? latitude;
  final double? longitude;
  final bool ativo;
  final String idCidade;
  final String nomeCidade;
  final String codigoIbge;
  final String uf;
  final String dataCriacao;
  final String dataAtualizacao;

  LocalVotacao({
    required this.id,
    required this.codigoTse,
    required this.nome,
    required this.endereco,
    this.bairro,
    this.cep,
    required this.zonaEleitoral,
    this.latitude,
    this.longitude,
    required this.ativo,
    required this.idCidade,
    required this.nomeCidade,
    required this.codigoIbge,
    required this.uf,
    required this.dataCriacao,
    required this.dataAtualizacao,
  });

  factory LocalVotacao.fromJson(Map<String, dynamic> json) =>
      _$LocalVotacaoFromJson(json);
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
