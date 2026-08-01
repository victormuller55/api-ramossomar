# Front — Cidades e Locais de Votação (carga completa)

Guia para o frontend/app consumir as rotas que retornam **todos** os registros de uma vez, **sem paginação** e **sem filtros**.

Use essas rotas quando precisar popular selects, caches locais ou listas offline. A resposta pode demorar se o volume for grande — configure timeout alto no cliente.

---

## Autenticação

Ambas as rotas exigem usuário autenticado.

| Header | Valor |
|--------|--------|
| `Authorization` | `Bearer {access_token}` |
| `Content-Type` | `application/json` (opcional no GET) |
| `Accept` | `application/json` |

Sem token válido → `401`.

---

## Base URL

```
{BASE_URL}/api/v1/ramossomar
```

Exemplo local: `http://localhost:7000/api/v1/ramossomar`

---

## 1. Todas as cidades

### Request

```
GET /api/v1/ramossomar/cidades/todos
```

Sem query params. Sem body.

### Response `200`

Array JSON de cidades, ordenado por `nome`.

```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "codigo_ibge": "5208707",
    "nome": "Goiânia",
    "uf": "GO",
    "data_criacao": "2026-07-01T10:00:00",
    "data_atualizacao": "2026-07-01T10:00:00"
  }
]
```

### Campos

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID (string) | Identificador da cidade |
| `codigo_ibge` | string | Código IBGE do município |
| `nome` | string | Nome do município |
| `uf` | string | UF (2 caracteres) |
| `data_criacao` | datetime | Criação do registro |
| `data_atualizacao` | datetime | Última atualização |

---

## 2. Todos os locais de votação

### Request

```
GET /api/v1/ramossomar/locais-votacao/todos
```

Sem query params. Sem body.

### Response `200`

Array JSON de locais, ordenado por `nome`. Cada item já traz dados da cidade vinculada.

```json
[
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
    "codigo_tse": "123456",
    "nome": "Escola Municipal Exemplo",
    "endereco": "Rua Exemplo, 100",
    "bairro": "Centro",
    "cep": "74000000",
    "zona_eleitoral": "001",
    "latitude": -16.6869,
    "longitude": -49.2648,
    "ativo": true,
    "id_cidade": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "nome_cidade": "Goiânia",
    "codigo_ibge": "5208707",
    "uf": "GO",
    "data_criacao": "2026-07-01T10:00:00",
    "data_atualizacao": "2026-07-01T10:00:00"
  }
]
```

### Campos

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID (string) | Identificador do local |
| `codigo_tse` | string | Código TSE do local |
| `nome` | string | Nome do local |
| `endereco` | string | Endereço |
| `bairro` | string \| null | Bairro |
| `cep` | string \| null | CEP |
| `zona_eleitoral` | string | Zona eleitoral |
| `latitude` | number \| null | Latitude |
| `longitude` | number \| null | Longitude |
| `ativo` | boolean | Se o local está ativo |
| `id_cidade` | UUID (string) | FK da cidade |
| `nome_cidade` | string | Nome da cidade |
| `codigo_ibge` | string | Código IBGE da cidade |
| `uf` | string | UF da cidade |
| `data_criacao` | datetime | Criação do registro |
| `data_atualizacao` | datetime | Última atualização |

---

## Exemplo (fetch)

```js
const baseUrl = "http://localhost:7000/api/v1/ramossomar";
const token = "..."; // JWT de acesso

async function carregarCidadesELocais() {
  const headers = {
    Authorization: `Bearer ${token}`,
    Accept: "application/json",
  };

  const [cidadesRes, locaisRes] = await Promise.all([
    fetch(`${baseUrl}/cidades/todos`, { headers }),
    fetch(`${baseUrl}/locais-votacao/todos`, { headers }),
  ]);

  if (!cidadesRes.ok || !locaisRes.ok) {
    throw new Error("Falha ao carregar cidades/locais");
  }

  const cidades = await cidadesRes.json();
  const locais = await locaisRes.json();

  return { cidades, locais };
}
```

### Filtrar locais por cidade no front

```js
function locaisDaCidade(locais, idCidade) {
  return locais.filter((local) => local.id_cidade === idCidade);
}
```

---

## Erros

Formato padrão:

```json
{
  "status_code": 401,
  "erro": "...",
  "mensagem": "Mensagem amigável",
  "timestamp": "2026-08-01T14:00:00"
}
```

| HTTP | Situação |
|------|----------|
| `401` | Token ausente/inválido/expirado |
| `500` | Erro interno |

---

## Observações para o front

1. **Sem paginação** — a resposta é o array completo.
2. **Sem filtros na API** — filtre no cliente (ex.: por `id_cidade`, `ativo`, busca por nome).
3. **Timeout** — use timeout generoso (ex.: 60–120s), especialmente em `/locais-votacao/todos`.
4. **Cache** — ideal carregar uma vez (login/splash) e guardar em memória/storage; refrescar sob demanda.
5. **JSON em `snake_case`** — use os nomes exatamente como na tabela de campos.
6. **Rotas filtradas** (`GET /cidades` e `GET /locais-votacao`) continuam existindo para buscas pontuais; para carga total, prefira `/todos`.
