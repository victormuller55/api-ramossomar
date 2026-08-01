# Política de Privacidade — Ramos Somar

**Última atualização:** 25 de julho de 2026  
**Escopo desta versão:** backend (API Ramos Somar) + aplicativo mobile Flutter (`app_ramos_candidatura`). Itens ainda pendentes do controlador (hospedagem, retenção, bases legais definitivas) permanecem marcados com `[DEFINIR]`.

---

## 1. Quem somos e o que é o Ramos Somar

O **Ramos Somar** é uma plataforma de gestão de campanha política / articulação eleitoral. A API backend (`API Ramos Somar`) oferece gestão de usuários, apoiadores, publicações, tokens de autenticação, cidades/locais de votação e relatórios.

Funcionalidades tratadas pela API:

- cadastro e gestão de usuários (Administrador e Líder);
- cadastro e gestão de apoiadores (incluindo intenção de voto);
- publicações internas com imagens;
- consulta a cidades e locais de votação (dados públicos IBGE/TSE);
- histórico de alterações de apoiadores;
- geração de relatórios (PDF/XLSX) para administradores;
- autenticação e controle de acesso.

**Controlador dos dados (pessoa física):** Victor Muller da Luz  
**Cidade/UF:** Araucária/PR  
**E-mail para privacidade / LGPD:** victormuller050@gmail.com  

**Desenvolvimento técnico da plataforma:** Convertix — contato@convertix.net  

Não há Encarregado (DPO) formalmente nomeado neste momento. Solicitações sobre dados pessoais devem ser enviadas ao e-mail de privacidade acima.

### 1.1. Clientes da API

| Item | Valor |
|------|--------|
| Nome do produto | Ramos Somar |
| Plataformas do app | Android e iOS (Flutter) |
| Identificador Android | `com.net.convertix.ramossomar` |
| Identificador iOS | `com.net.convertix.ramossomar` |
| URL da API (produção) | `https://ramossomar.api.convertix.net.br` |
| Identificador de cliente da API | `ramossomar-mobile` |
| Painel web | Não há painel web publicado; o cliente oficial é o aplicativo mobile |

O uso é interno da campanha (administradores e líderes). **Não há auto-cadastro público de apoiadores** na API.

---

## 2. Abrangência

Esta política aplica-se aos dados pessoais tratados pela API Ramos Somar e pelo aplicativo mobile oficial (Android/iOS), incluindo:

- **Usuários do sistema** (perfil Administrador ou Líder);
- **Apoiadores** cujos dados são cadastrados por líderes ou administradores.

O cadastro de apoiadores é realizado por usuários autorizados (não há endpoint público de auto-cadastro de apoiador).

---

## 3. Quais dados pessoais tratamos

### 3.1. Usuários do sistema (Administrador / Líder)

| Dado | Uso principal |
|------|----------------|
| Nome | Identificação e exibição no sistema |
| E-mail | Login e identificação (único) |
| Senha | Autenticação (armazenada apenas com hash BCrypt) |
| Telefone | Contato operacional |
| Foto de perfil (imagem) | Identificação visual; arquivo no servidor de uploads |
| Perfil de acesso (`ADMIN` / `LIDER`) | Controle de permissões |
| Status (ativo/inativo) | Gestão de acesso |
| Data do último login | Segurança e auditoria operacional |
| Datas de criação/atualização | Controle interno |

Login na API: `e-mail` + `senha` → tokens de acesso.

### 3.2. Apoiadores

| Dado | Uso principal |
|------|----------------|
| Nome | Identificação |
| CPF | Identificação / prevenção de duplicidade |
| Data de nascimento | Cadastro e organização da base |
| Telefone e WhatsApp | Contato de campanha |
| Endereço completo (CEP, logradouro, número, complemento, bairro, cidade) | Localização e articulação territorial |
| Local de votação | Organização eleitoral |
| Intenção de voto (`INDECISO`, `SIMPATIZANTE`, `APOIADOR`, `CONFIRMADO`) | Classificação política de campanha |
| Observações | Anotações operacionais |
| Vínculo com o líder responsável | Organização da equipe |
| Data de exclusão lógica (quando aplicável) | Controle de remoção |

**Atenção:** a **intenção de voto** constitui dado pessoal sensível relacionado a opinião política (art. 5º, II, e art. 11 da LGPD).

**Dados de apoiador que a API atual não modela:** e-mail do apoiador e foto do apoiador.

### 3.3. Histórico de alterações de apoiadores

Alterações em campos do apoiador podem gerar registros de auditoria com:

- campo alterado;
- valor anterior;
- valor novo;
- usuário responsável;
- data da alteração.

Esses registros podem conter CPF, telefone, endereço e demais dados pessoais quando esses campos forem alterados.

### 3.4. Tokens e sessão

- **Token de acesso (JWT):** contém id do usuário, e-mail, nome e perfil; validade padrão de 24 horas; autenticação via `Authorization: Bearer`;
- **Refresh token:** UUID associado ao usuário, com data de expiração, armazenado em banco.

A API é **stateless** (sem sessão de servidor e **sem cookies** de sessão). CORS está configurado com `allow-credentials=false`.

**No aplicativo mobile (Flutter):**

- **Token de acesso (JWT):** armazenado em armazenamento seguro do aparelho (`FlutterSecureStorage`; no Android, SharedPreferences criptografadas);
- **Dados do usuário logado** (id, nome, e-mail, perfil, telefone, foto, e eventualmente `refresh_token` retornado no login): em `SharedPreferences` (sem o JWT de acesso no JSON persistido);
- **Controle de sessão local:** o app grava o dia do login e **expira a sessão local ao virar o dia civil** (mesmo que o JWT ainda seja válido na API); nesse caso limpa token, usuário e cache;
- **Logout:** remove JWT, dados do usuário logado, marcação do dia da sessão e cache de listagens; o `device_id` local permanece no aparelho;
- **Sessão inválida / 401:** o app limpa a sessão local e redireciona para o login.

### 3.5. Publicações

- Título e conteúdo;
- Até três imagens;
- Autor (usuário do sistema).

Imagens ficam no armazenamento de uploads do servidor e podem ser acessíveis por URL pública (`/uploads/**`), conforme configuração.

### 3.6. Dados de referência (fontes públicas)

- **Cidades** (IBGE): código, nome, UF;
- **Locais de votação** (TSE): nome, endereço, bairro, CEP, zona eleitoral, coordenadas geográficas públicas, vínculo com cidade.

Esses dados são importados pela API a partir de fontes públicas. **Não são dados de “conta”**, mas podem ser associados a apoiadores no cadastro.

### 3.7. Metadados técnicos das requisições

A API pode receber/utilizar:

| Dado / cabeçalho | Uso |
|------------------|-----|
| `Authorization` | JWT do usuário autenticado |
| `X-Client-Id` / `X-Client-Secret` | Validação do cliente da API |
| `X-App-Version` | Diagnóstico (log de debug) |
| `X-Platform` | Diagnóstico (log de debug) |
| `X-Device-Id` | Diagnóstico (log de debug) |
| Endereço IP | Limitação de taxa de requisições (rate limit); não há entidade própria de telemetria no banco |

Não há modelo de persistência dedicado a telemetria de dispositivo no backend.

**No aplicativo mobile:** todas as requisições autenticadas (e as de login) enviam:

| Cabeçalho | Origem no app |
|-----------|----------------|
| `Authorization: Bearer` | JWT salvo no armazenamento seguro (quando logado) |
| `X-Client-Id` / `X-Client-Secret` | Credenciais do client `ramossomar-mobile` embutidas no app |
| `X-App-Version` | Versão do app (ex.: `1.0.0`) |
| `X-Platform` | `android` ou `ios` |
| `X-Device-Id` | Identificador gerado no aparelho e persistido em `SharedPreferences` (não é ID de publicidade; permanece após logout; é apagado na desinstalação) |

### 3.8. Dados que a API atual **não** trata

Com base no código do backend:

- cobrança ou pagamento;
- envio automatizado de e-mail ou SMS;
- login social (Google, Apple etc.);
- Firebase, analytics, crash reporting ou SDKs de publicidade no servidor;
- cookies de tracking;
- coleta de geolocalização em tempo real do usuário;
- armazenamento em nuvem tipo S3 (uploads ficam em disco local do servidor);
- endpoint self-service de “excluir minha conta” ou “exportar meus dados” para o titular apoiador.

**No aplicativo mobile também não há:**

- GPS / geolocalização em tempo real;
- acesso a agenda, contatos, microfone com finalidade de áudio (há declaração iOS de microfone associada ao uso de câmera do seletor de imagens, sem uso de gravação de áudio própria do produto);
- Firebase, Google Analytics, Crashlytics, ads ou SDKs de publicidade/tracking;
- cookies de navegador (é app nativo, não WebView de produto);
- login social (Google, Apple etc.);
- cobrança ou pagamento.

**Coleta só no cliente (além do que vai à API):** `device_id` local; cache temporário de listagens; arquivos temporários de relatórios/exportação; imagens selecionadas da galeria antes do upload.

---

## 4. Para que usamos os dados (finalidades)

1. **Autenticar** usuários e controlar o acesso conforme o perfil (Administrador ou Líder);
2. **Cadastrar, consultar, atualizar e gerenciar** apoiadores vinculados a líderes;
3. **Classificar intenção de voto** e registrar observações de campanha;
4. **Manter histórico de alterações** dos dados de apoiadores para auditoria;
5. **Publicar conteúdos internos** (textos e imagens);
6. **Consultar cidades e locais de votação** a partir de bases públicas (IBGE/TSE);
7. **Gerar e exportar relatórios** (PDF/XLSX) com dados de apoiadores, para administradores;
8. **Armazenar imagens** de perfil e de publicações;
9. **Proteger a plataforma** (rate limiting, validação de cliente, medidas de segurança);
10. **Cumprir obrigações legais** e atender direitos dos titulares, quando aplicável;
11. **Preencher automaticamente endereço** no cadastro de apoiador a partir do CEP, mediante consulta do app ao serviço público ViaCEP (apenas o CEP é enviado a esse serviço).

---

## 5. Bases legais (LGPD)

As bases legais definitivas devem ser confirmadas pelo controlador. Em regra, podem aplicar-se:

| Tratamento | Base legal típica |
|------------|-------------------|
| Conta de usuário (ADMIN/LIDER) | Execução de contrato ou procedimentos preliminares (art. 7º, V) e/ou legítimo interesse (art. 7º, IX) |
| Cadastro de apoiadores por líderes | Consentimento do titular (art. 7º, I) e/ou outra base válida, **a ser definida pelo controlador** |
| Intenção de voto (dado sensível) | Consentimento específico e destacado (art. 11, I) ou outra hipótese do art. 11, **quando cabível** |
| Segurança, prevenção a fraudes e logs | Legítimo interesse (art. 7º, IX) e/ou cumprimento de obrigação legal |
| Dados públicos IBGE/TSE | Dados públicos / legítimo interesse operacional |

**Importante:** como apoiadores são cadastrados por terceiros (líderes), o controlador deve assegurar base legal adequada e, quando necessário, **consentimento** (ou outra hipótese válida) antes da inclusão dos dados.

---

## 6. Como os dados são coletados

- **Diretamente do usuário do sistema**, no cadastro/login e atualização de perfil (incluindo upload de foto);
- **Por líderes/administradores**, ao cadastrar ou editar apoiadores via API;
- **Automaticamente**, em metadados técnicos de requisições (IP para rate limit, headers de cliente, tokens);
- **De fontes públicas**, na importação de cidades (IBGE) e locais de votação (TSE).

**No aplicativo mobile:**

- **Galeria de fotos:** o usuário pode escolher imagens para foto de perfil e publicações (`image_picker` + recorte local com `image_cropper`); as imagens são enviadas à API no upload;
- **Câmera:** permissão declarada no Android/iOS para captura de fotos de perfil/publicações (o fluxo atual da UI usa principalmente a galeria);
- **ViaCEP:** ao informar CEP no cadastro de apoiador, o app consulta `https://viacep.com.br/ws/{cep}/json/` e preenche logradouro/bairro/cidade/UF; **somente o CEP** é enviado ao ViaCEP;
- **Cache local:** listagens (cadastrados, feed, líderes) podem ficar em `SharedPreferences` por até 5 minutos;
- **Arquivos locais:** relatórios PDF/XLSX baixados ficam em diretório do app (temporário no Android; Documents no iOS) para abrir/compartilhar;
- **Internet:** necessária para login, sync e uploads.

---

## 7. Compartilhamento de dados

### 7.1. Dentro da plataforma

- Líderes acessam apoiadores conforme regras de vínculo/perfil aplicadas pela API;
- Administradores podem acessar a base e exportar relatórios;
- Imagens em `/uploads/**` podem ser acessíveis por URL (GET público), conforme configuração do servidor.

### 7.2. Fora da plataforma

No backend atual **não há** envio automatizado de dados de usuários ou apoiadores para APIs de marketing, pagamento, e-mail ou SMS.

Podem ocorrer compartilhamentos nas hipóteses legais, por exemplo:

- exigência de autoridade competente;
- prestadores de infraestrutura (hospedagem, banco MySQL) que processam dados sob instrução do controlador;
- exportações manuais (PDF/XLSX) realizadas por administradores — o uso posterior é de responsabilidade de quem exporta.

**No aplicativo mobile:** administradores podem gerar relatório de cadastrados (PDF/XLSX) e **compartilhar o arquivo pelo compartilhamento nativo do sistema operacional** (`share_plus` — WhatsApp, e-mail, Files, Drive etc., conforme apps instalados no aparelho). O app não envia o arquivo automaticamente a nenhum destino; o usuário escolhe o destino no sheet do SO.

### 7.3. Fontes públicas e serviços de terceiros

| Serviço | O que ocorre na API |
|---------|---------------------|
| **IBGE** | Download de dados públicos de municípios (referência). **Não envia** dados de usuários/apoiadores |
| **TSE** | Download de dados públicos de locais de votação. **Não envia** dados de usuários/apoiadores |
| **ViaCEP** | **Não integrado no backend Java.** O **app mobile** consulta o ViaCEP no formulário de endereço do apoiador; **apenas o CEP (8 dígitos)** é enviado. Não são enviados nome, CPF, telefone nem demais dados pessoais |

---

## 8. Cookies e tecnologias

### 8.1. Backend / API

- autenticação por **JWT** (`Authorization: Bearer`);
- **sem cookies** de sessão;
- CORS sem credenciais de cookie;
- cabeçalho `Permissions-Policy` restringindo geolocation, microphone, camera, payment e usb no contexto HTTP da API.

### 8.2. Aplicativo mobile (Flutter)

| Item | Situação |
|------|----------|
| Cookies de navegador | Não aplicável (app nativo). A política HTML pode ser aberta em navegador in-app (`url_launcher`), sem cookies de tracking do produto |
| SDKs de terceiros | Sem Firebase, Analytics, Ads ou crash reporting. Dependências relevantes: `http`, `shared_preferences`, `flutter_secure_storage`, `image_picker`, `image_cropper`, `share_plus`, `path_provider`, `url_launcher`, `open_filex` |
| Armazenamento local | JWT em armazenamento seguro; usuário logado, `device_id`, dia da sessão e cache de listagens (TTL 5 min) em `SharedPreferences`; arquivos de relatório/exportação no diretório do app |
| Permissões do aparelho | Internet; câmera; leitura de imagens/galeria (e storage legado no Android conforme versão). **Sem** permissão de localização/GPS |

---

## 9. Armazenamento, segurança e localização

### 9.1. Onde ficam os dados (backend)

- Banco de dados **MySQL** (usuários, apoiadores, histórico, tokens, publicações, cidades, locais de votação);
- Arquivos de imagem em **armazenamento local do servidor** (diretório de uploads; tamanho máximo típico 5 MB; compressão JPEG);
- Logs da aplicação (diagnóstico/erros), sem painel de analytics dedicado.

Localização do servidor/hospedagem: `[DEFINIR — PAÍS / REGIÃO DO DATACENTER]`.

**Dados que ficam só no aparelho (app mobile):**

- JWT de acesso (armazenamento seguro);
- cópia local do perfil do usuário logado e, se retornado no login, refresh token no JSON local;
- `device_id` gerado localmente;
- cache de listagens (até 5 minutos);
- arquivos temporários/locais de relatórios e cópias para compartilhamento;
- imagens selecionadas/recortadas antes do upload (espaço temporário do SO/app).

### 9.2. Medidas de segurança observadas no backend

- senhas com hash (BCrypt, strength 12);
- autenticação JWT com sessão sem estado;
- rate limiting por IP (ex.: login 5/min, cadastro 10/min, API 100/min);
- validação de cliente (`X-Client-Id` / `X-Client-Secret`) em produção;
- cabeçalhos de segurança HTTP;
- validação de tipos de arquivo em uploads;
- documentação interativa (Swagger) desativável em produção;
- respostas de erro sem detalhe excessivo em produção.

Nenhuma medida elimina totalmente riscos. Em incidente relevante, o controlador avaliará comunicação aos titulares e à ANPD, nos termos da LGPD.

---

## 10. Retenção e exclusão

Comportamento atual da API:

| Dado / recurso | Comportamento |
|----------------|---------------|
| Apoiador | Exclusão lógica (`data_exclusao`); o histórico pode permanecer |
| Usuário | Desativação lógica (`ativo = false`); dados podem permanecer |
| Publicação | Exclusão física, com remoção dos arquivos de imagem associados |
| Refresh token | Pode ser removido fisicamente |
| Histórico de apoiador | Pode ser removido por operação administrativa |

**Prazo de retenção:** `[DEFINIR — ex.: enquanto a campanha/conta estiver ativa + X meses/anos após encerramento, ou até solicitação de exclusão quando cabível]`.

Não há exclusão automática por prazo nem endpoint self-service de exclusão/portabilidade para o titular apoiador. Solicitações devem ser feitas pelo canal da seção 12.

**No aplicativo mobile:**

- **Logout / sessão expirada (dia civil ou 401):** remove JWT, dados do usuário logado, marcação do dia da sessão e cache de listagens. O `device_id` permanece até desinstalar ou limpar dados do app;
- **Excluir conta (tela Perfil):** o app solicita exclusão/desativação via API e, em seguida, limpa a sessão local e volta ao login;
- **Desinstalar o app / limpar dados do app:** o sistema operacional remove armazenamento local do app (incluindo token, preferências, `device_id` e arquivos locais). Isso **não apaga** automaticamente os dados já gravados no servidor.

---

## 11. Direitos dos titulares (LGPD)

O titular pode solicitar, na medida aplicável à lei:

- confirmação da existência de tratamento;
- acesso aos dados;
- correção de dados incompletos, inexatos ou desatualizados;
- anonimização, bloqueio ou eliminação de dados desnecessários ou excessivos;
- portabilidade, quando cabível;
- informação sobre compartilhamentos;
- revogação de consentimento, quando o tratamento se basear em consentimento;
- oposição a tratamento em hipóteses legais.

**Canal de solicitações:** victormuller050@gmail.com  
**Prazo de resposta:** até 15 dias, nos termos da LGPD.

Para apoiadores cadastrados por líderes, o pedido pode ser feito ao controlador, que avaliará a solicitação e a viabilidade técnica/operacional (incluindo histórico e exclusão lógica).

---

## 12. Crianças e adolescentes

A API permite cadastrar data de nascimento de apoiadores **sem validação automática de idade mínima**.

O controlador deve orientar líderes/administradores a **não cadastrar menores** sem o cumprimento das regras legais aplicáveis (incluindo, quando couber, consentimento específico de pelo menos um dos pais ou responsável legal — art. 14 da LGPD).

Idade mínima recomendada de uso da plataforma pelos operadores (líderes/admins): **18 anos**, salvo definição diversa do controlador.

---

## 13. Transferência internacional

Se a hospedagem, backups ou prestadores estiverem fora do Brasil, haverá transferência internacional de dados, a ser informada e amparada nas hipóteses do art. 33 da LGPD.

`[INFORMAR: se há ou não transferência internacional da hospedagem/banco e para quais países/serviços]`

**No cliente mobile:** a consulta ao **ViaCEP** (`viacep.com.br`) envia apenas o CEP. Trata-se de serviço externo utilizado para autocompletar endereço; não envia demais dados pessoais do apoiador ou do usuário.

---

## 14. Relatórios e exportações

Administradores podem exportar relatórios (PDF/XLSX) contendo, entre outros: nome, CPF, data de nascimento, telefones, endereço completo, local de votação, intenção de voto, líder, observações e data de cadastro.

Esses arquivos saem do ambiente controlado da API e devem ser tratados com confidencialidade, acesso restrito e descarte seguro quando não forem mais necessários.

**No aplicativo mobile:** o relatório é baixado para o armazenamento do app e aberto no **compartilhamento nativo do SO**. O administrador escolhe o destino (WhatsApp, e-mail, nuvem etc.). Cópias podem permanecer no aparelho até limpeza/desinstalação; o uso posterior fora do app é de responsabilidade de quem exportou/compartilhou.

---

## 15. Papéis e acesso

| Perfil | Acesso típico via API |
|--------|------------------------|
| **Administrador** | Gestão de usuários, visão ampla de apoiadores, publicações, relatórios/exportações |
| **Líder** | Gestão dos apoiadores sob seu vínculo, consumo/participação conforme regras de publicações e perfil |

O escopo exato das listagens é aplicado pela API conforme o token e o perfil do usuário autenticado.

**Espelho na UI do app mobile:**

| Perfil | Telas / ações típicas no app |
|--------|------------------------------|
| **Administrador** | Cadastrados (visão ampla), Feed (com criação de publicações), Perfil, Líderes (gestão) e Relatórios (exportação/compartilhamento) |
| **Líder** | Cadastrados (apoiadores sob seu vínculo / “Seus cadastrados”) e Feed (consumo; gestão de publicação apenas quando for o autor, se aplicável). Perfil acessível a partir do fluxo do líder |

---

## 16. Alterações desta política

Esta política pode ser atualizada para refletir mudanças na plataforma, na legislação ou nas práticas do controlador. A data de “última atualização” será revisada a cada alteração relevante. Quando a mudança for substancial, o controlador poderá comunicar pelos canais habituais da plataforma.

**Exibição no app mobile:**

- **Login:** checkbox obrigatório “Li e Aceito a Política de Privacidade”, com link que abre a URL pública da política;
- **Perfil:** botão “Política de privacidade” que abre a mesma URL;
- **URL publicada:** `https://convertix.net.br/pages/politica-privacidade-ramos-somar.html` (abertura preferencial em navegador in-app).

---

## 17. Contato

| Assunto | Contato |
|---------|---------|
| Privacidade / LGPD (controlador) | Victor Muller da Luz — victormuller050@gmail.com |
| Suporte técnico / desenvolvimento | Convertix — contato@convertix.net |

No app mobile **não há** tela própria de contato nem telefone/WhatsApp institucional exibido. O login exibe a marca “Powered by Convertix”. Contato de privacidade permanece pelo e-mail acima.

---

## 18. Lei aplicável

Esta política é regida pela legislação brasileira, em especial a Lei nº 13.709/2018 (LGPD). Fica eleito o foro da comarca de **Araucária/PR**, salvo competência legal diversa.

---

## Anexo A — Mapa resumido de dados × finalidade (para o front)

Versão curta para tela de privacidade / FAQ:

| Categoria | Exemplos | Finalidade |
|-----------|----------|------------|
| Identificação | Nome, CPF, e-mail | Cadastro, login, unicidade |
| Contato | Telefone, WhatsApp | Comunicação de campanha |
| Localização | Endereço, cidade, local de votação | Articulação territorial |
| Perfil político | Intenção de voto, observações | Gestão de campanha |
| Credenciais | Senha (hash), tokens JWT/refresh | Segurança e acesso |
| Mídia | Fotos de perfil e publicações | Identificação e conteúdo interno |
| Técnicos | IP (rate limit), headers de app/dispositivo | Segurança e operação |
| Referência pública | Cidades IBGE, locais TSE | Apoio ao cadastro eleitoral |

---

## Anexo B — Checklist do front (preenchido)

Status com base no app Flutter `app_ramos_candidatura` (25/07/2026):

1. Identificadores do app (Android/iOS), URL da API e client id — **preenchido** (§1.1);
2. Existência e URL do painel web — **preenchido** (não há painel web; só app mobile);
3. Armazenamento local de token/sessão/cache/device_id — **preenchido** (§3.4, §8.2, §9.1);
4. Permissões do aparelho e uso real (câmera, galeria, GPS etc.) — **preenchido** (§6, §8.2);
5. SDKs de terceiros no cliente (ou ausência) — **preenchido** (§3.8, §8.2);
6. Cookies/analytics no painel web — **N/A** (sem painel web);
7. Integração ViaCEP no formulário de endereço — **preenchido** (§4, §6, §7.3);
8. Compartilhamento de relatórios pelo SO — **preenchido** (§7.2, §14);
9. Comportamento no logout e na desinstalação — **preenchido** (§10);
10. Onde a política é exibida no app — **preenchido** (§16).

### Pendências do controlador (não do código)

- Telefone ou WhatsApp de contato institucional (além do e-mail);
- País/região da hospedagem do servidor/banco;
- Se há transferência internacional e para quais países/serviços;
- Prazo de retenção após o fim da campanha/uso;
- Bases legais definitivas para cadastro de apoiadores e intenção de voto (e como o consentimento será obtido/registrado, se for o caso).
