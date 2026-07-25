# Segurança da API — guia para o app mobile (Flutter / iOS / Android)

O que o app precisa enviar em cada requisição para a API Ramos Somar em produção.

CORS **não se aplica** a apps nativos. Não use `Origin`, `Referer` ou `User-Agent` como autenticação.

---

## 1. Ambiente e credenciais

| Item | Valor (produção) |
|------|------------------|
| Base URL | `https://ramossomar.api.convertix.net.br` |
| Prefixo das rotas | `/api/v1/ramossomar` |
| `X-Client-Id` | `ramossomar-mobile` |
| `X-Client-Secret` | `a7K9xP2mQ5vL8R1wZ4nB6yT0C3dE2fG8hJ4kM7nP9qR1sT3uV5wX7yZ9aB1cD3eF` |

Exemplo completo de URL de login:

```text
https://ramossomar.api.convertix.net.br/api/v1/ramossomar/auth/login
```

Em **local** (perfil `local` da API), client credentials podem estar desligados — aí o app pode omitir `X-Client-Id` / `X-Client-Secret`. Em **produção**, são obrigatórios.

---

## 2. Headers em toda requisição

### Obrigatórios (produção)

```http
X-Client-Id: ramossomar-mobile
X-Client-Secret: a7K9xP2mQ5vL8R1wZ4nB6yT0C3dE2fG8hJ4kM7nP9qR1sT3uV5wX7yZ9aB1cD3eF
Content-Type: application/json
Accept: application/json
```

### Em rotas autenticadas (depois do login)

Além dos headers acima:

```http
Authorization: Bearer <access_token>
```

### Opcionais (telemetria — não autorizam nada)

```http
X-App-Version: 1.4.2
X-Platform: android
X-Device-Id: <uuid-estavel-da-instalacao>
```

`X-Platform`: `android` ou `ios`.

---

## 3. Fluxo mínimo no app

1. Chamar `POST /auth/login` com e-mail/senha **e** headers de client.
2. Guardar `access_token` em storage seguro (`flutter_secure_storage` / Keychain / Keystore).
3. Em todas as outras rotas, enviar client headers + `Authorization: Bearer …`.
4. Se receber **401** em rota protegida → limpar sessão e ir para login.
5. Se receber **429** → ler header `Retry-After` e esperar antes de tentar de novo.

---

## 4. Login

```http
POST /api/v1/ramossomar/auth/login
X-Client-Id: ramossomar-mobile
X-Client-Secret: a7K9xP2mQ5vL8R1wZ4nB6yT0C3dE2fG8hJ4kM7nP9qR1sT3uV5wX7yZ9aB1cD3eF
Content-Type: application/json

{
  "email": "usuario@email.com",
  "senha": "senha1234"
}
```

- `email`: obrigatório, formato válido  
- `senha`: 8 a 128 caracteres  

### Response 200

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "uuid...",
  "tipo_token": "Bearer",
  "expira_em": "2026-07-26T12:00:00",
  "id_usuario": "c22cf335-e634-4f01-8d7a-6a8bdcd9df17",
  "nome": "João",
  "email": "usuario@email.com",
  "perfil": "LIDER",
  "telefone": "62999999999",
  "imagem": "/uploads/usuarios/...."
}
```

Token válido por **24 horas** (`expira_em`).

### Erros comuns

| HTTP | `erro` | Significado | Ação no app |
|------|--------|-------------|-------------|
| 401 | `CLIENTE_INVALIDO` | Client id/secret errados ou ausentes | Corrigir headers do app |
| 401 | `CREDENCIAIS_INVALIDAS` | Usuário/senha inválidos | Mensagem genérica |
| 401 | `NAO_AUTENTICADO` | JWT ausente/inválido/expirado | Logout → login |
| 400 | `VALIDACAO` | Body inválido | Mostrar mensagem |
| 403 | `ACESSO_NEGADO` | Sem permissão no recurso | Bloquear ação |
| 429 | `RATE_LIMIT` | Limite por IP | Esperar `Retry-After` |

Mensagem de login inválido (sempre a mesma):

```text
Usuário ou senha inválidos.
```

---

## 5. Exemplo de interceptor (Dio / Flutter)

```dart
class AppConfig {
  static const baseUrl = 'https://ramossomar.api.convertix.net.br';
  static const clientId = 'ramossomar-mobile';
  static const clientSecret =
      'a7K9xP2mQ5vL8R1wZ4nB6yT0C3dE2fG8hJ4kM7nP9qR1sT3uV5wX7yZ9aB1cD3eF';
}

dio.options.baseUrl = '${AppConfig.baseUrl}/api/v1/ramossomar';

dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) {
    options.headers['X-Client-Id'] = AppConfig.clientId;
    options.headers['X-Client-Secret'] = AppConfig.clientSecret;
    options.headers['Accept'] = 'application/json';

    // Opcional
    options.headers['X-App-Version'] = AppConfig.version;
    options.headers['X-Platform'] = Platform.isIOS ? 'ios' : 'android';
    options.headers['X-Device-Id'] = AppConfig.deviceId;

    final token = AppAuth.accessToken;
    if (token != null && token.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  },
  onError: (error, handler) async {
    final status = error.response?.statusCode;
    if (status == 401) {
      await AppAuth.logout();
      // navegar para login
    }
    if (status == 429) {
      final retryAfter =
          int.tryParse(error.response?.headers.value('retry-after') ?? '1') ?? 1;
      await Future.delayed(Duration(seconds: retryAfter));
      // retry controlado, se fizer sentido
    }
    handler.next(error);
  },
));
```

### Login (exemplo)

```dart
final response = await dio.post('/auth/login', data: {
  'email': email,
  'senha': senha,
});

final accessToken = response.data['access_token'] as String;
await AppAuth.saveToken(accessToken);
```

---

## 6. Rate limit (referência)

| Regra | Limite / minuto | Quando |
|-------|-----------------|--------|
| LOGIN | 5 | `POST /auth/login` |
| CADASTRO | 10 | criação sensível |
| API | 100 | demais |

Headers de resposta: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `Retry-After`.

---

## 7. Perfis

| Perfil | Papel |
|--------|-------|
| `ADMIN` | Acesso total |
| `LIDER` | Só o próprio escopo |

O backend valida no servidor. Em 403, não insistir na mesma chamada.

---

## 8. Uploads

- `multipart/form-data`
- JPEG, PNG, WEBP ou GIF
- Máx. 5 MB
- Em produção: mesmos headers `X-Client-Id`, `X-Client-Secret` e `Authorization`

---

## 9. Checklist

- [ ] Base URL HTTPS de produção
- [ ] Interceptor com `X-Client-Id` + `X-Client-Secret` em **todas** as calls
- [ ] `Authorization: Bearer` após login
- [ ] Token em storage seguro
- [ ] 401 → logout
- [ ] 429 → respeitar `Retry-After`
- [ ] Não logar token/secret em release
- [ ] Swagger **não** está disponível em produção

---

## 10. O que não fazer

1. Não depender de CORS/Origin  
2. Não omitir client headers em produção  
3. Não guardar JWT em preferências sem criptografia  
4. Não colocar `JWT_SECRET` do servidor no app  
5. Não apontar o app para `/swagger-ui` em produção (está desligado)
