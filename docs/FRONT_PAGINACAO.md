# Front — Paginação das listagens

Guia do que o frontend precisa alterar para consumir as rotas que passaram a retornar **lista paginada** (20 itens por página).

---

## Rotas afetadas

| Rota | Antes | Agora |
|------|--------|--------|
| `GET /api/v1/ramossomar/apoiadores` | Array `[...]` | Objeto paginado |
| `GET /api/v1/ramossomar/usuarios` | Array `[...]` | Objeto paginado |
| `GET /api/v1/ramossomar/publicacoes` | Array `[...]` | Objeto paginado |

Demais filtros (`nome`, `perfil`, `id_lider`, etc.) **continuam iguais**.

---

## O que mudou na resposta

### Antes

```json
[
  { "id": "...", "nome": "..." },
  { "id": "...", "nome": "..." }
]
```

### Agora

```json
{
  "itens": [
    { "id": "...", "nome": "..." },
    { "id": "...", "nome": "..." }
  ],
  "num_itens": 20,
  "max_itens": 45,
  "num_pagina": 1,
  "max_paginas": 3
}
```

### Campos da paginação (snake_case)

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `itens` | array | Registros da página atual |
| `num_itens` | number | Quantidade de itens **nesta página** |
| `max_itens` | number | Total de itens que batem com o filtro |
| `num_pagina` | number | Página atual (**começa em 1**) |
| `max_paginas` | number | Total de páginas |

---

## Novo query param

| Param | Obrigatório | Padrão | Descrição |
|-------|-------------|--------|-----------|
| `pagina` | Não | `1` | Número da página (base 1). Valor menor que 1 retorna erro `400`. |

Tamanho da página é **fixo em 20** — não há param de `size` / `limite`.

### Exemplos

```
GET /api/v1/ramossomar/apoiadores?pagina=1
GET /api/v1/ramossomar/apoiadores?cidade=Campinas&pagina=2
GET /api/v1/ramossomar/usuarios?perfil=LIDER&pagina=1
GET /api/v1/ramossomar/publicacoes?pagina=3
```

---

## O que alterar no front

### 1. Parar de tratar a resposta como array

**Antes:**
```ts
const lista = await api.get('/apoiadores')
// lista = [ {...}, {...} ]
setApoiadores(lista)
```

**Agora:**
```ts
const data = await api.get('/apoiadores', { params: { pagina: 1 } })
// data = { itens, num_itens, max_itens, num_pagina, max_paginas }
setApoiadores(data.itens)
setPagina(data.num_pagina)
setTotalPaginas(data.max_paginas)
setTotalItens(data.max_itens)
```

### 2. Tipagem sugerida

```ts
type Paginacao<T> = {
  itens: T[]
  num_itens: number
  max_itens: number
  num_pagina: number
  max_paginas: number
}
```

Use `Paginacao<Apoiador>`, `Paginacao<Usuario>`, `Paginacao<Publicacao>` nas três telas.

### 3. Controles de página na UI

- Botão **Anterior**: `pagina = num_pagina - 1` (desabilitar se `num_pagina <= 1`)
- Botão **Próxima**: `pagina = num_pagina + 1` (desabilitar se `num_pagina >= max_paginas`)
- Ao mudar filtro (`nome`, `perfil`, etc.), **voltar para `pagina=1`**
- Exibir algo como: `Página {num_pagina} de {max_paginas}` e/ou `Total: {max_itens}`

### 4. Checklist por tela

- [ ] **Apoiadores** — ler `data.itens` em vez do array raiz; enviar `pagina` na query
- [ ] **Usuários / Líderes** (`perfil=LIDER`) — mesma adaptação
- [ ] **Publicações** — mesma adaptação
- [ ] Remover qualquer lógica que faça `.map` / `.length` direto no root da resposta
- [ ] Se houver cache/store (Redux, Zustand, etc.), guardar o objeto paginado ou pelo menos `itens` + metadados

### 5. Lista vazia

Quando não houver registros:

```json
{
  "itens": [],
  "num_itens": 0,
  "max_itens": 0,
  "num_pagina": 1,
  "max_paginas": 0
}
```

Tratar `itens.length === 0` / `max_itens === 0` como “nenhum resultado”.

---

## O que NÃO muda

- Autenticação (`Bearer` token)
- Shape de cada item dentro de `itens` (campos do apoiador/usuário/publicação iguais)
- Filtros já existentes nas rotas
- Rotas de criar / alterar / apagar / upload
- Exportação de relatório de apoiadores (continua retornando a lista completa no arquivo, sem paginação HTTP)
