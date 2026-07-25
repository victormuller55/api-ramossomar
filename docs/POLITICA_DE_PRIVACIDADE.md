# Política de Privacidade — Ramos Somar

**Última atualização:** 25 de julho de 2026

Este documento descreve como os dados pessoais são tratados no ecossistema **Ramos Somar** (aplicativo móvel, painel web e API backend), com base nas funcionalidades implementadas no backend e no aplicativo móvel.

O controlador é uma **pessoa física** (ainda sem empresa constituída).

---

## 1. Quem somos e o que é o Ramos Somar

O **Ramos Somar** é uma plataforma de gestão de campanha política / articulação eleitoral, destinada a usuários autorizados (administradores e líderes), para:

- cadastro e gestão de apoiadores;
- registro de intenção de voto e observações de campanha;
- consulta a cidades e locais de votação (dados públicos IBGE/TSE);
- publicações internas com imagens;
- geração de relatórios (PDF/XLSX) para administradores;
- autenticação e controle de acesso dos usuários do sistema.

**Controlador dos dados (pessoa física):** Victor Muller da Luz  
**Cidade/UF:** Araucária/PR  
**E-mail para privacidade / LGPD:** victormuller050@gmail.com  

**Desenvolvimento técnico da plataforma:** Convertix — contato@convertix.net  

Não há Encarregado (DPO) formalmente nomeado neste momento. As solicitações sobre dados pessoais devem ser enviadas ao e-mail de privacidade acima.

### 1.1. Aplicativo móvel

| Item | Valor |
|------|--------|
| Nome do app | Ramos Somar |
| Plataformas | Android e iOS |
| Identificador Android (`applicationId`) | `com.net.convertix.ramossomar` |
| Identificador iOS (Bundle ID) | `com.net.convertix.ramossomar` |
| API consumida | `https://ramossomar.api.convertix.net.br` (ambiente de produção) |
| Identificador de cliente da API | `ramossomar-mobile` |

O app é de uso interno da campanha (administradores e líderes). **Não há auto-cadastro público de apoiadores** pelo aplicativo.

---

## 2. Abrangência

Esta política aplica-se aos dados pessoais tratados por meio da API Ramos Somar e dos clientes que a consomem (app e painel), incluindo:

- **Usuários do sistema** (perfil Administrador ou Líder);
- **Apoiadores** cujos dados são cadastrados por líderes ou administradores.

O cadastro de apoiadores é realizado por usuários autorizados da plataforma (não há, no backend atual, auto-cadastro público de apoiador).

---

## 3. Quais dados pessoais tratamos

### 3.1. Usuários do sistema (Administrador / Líder)

| Dado | Uso principal |
|------|----------------|
| Nome | Identificação e exibição no sistema |
| E-mail | Login e identificação |
| Senha | Autenticação (armazenada apenas de forma criptografada/hash) |
| Telefone | Contato operacional |
| Foto de perfil (imagem) | Identificação visual no sistema |
| Perfil de acesso (ADMIN / LIDER) | Controle de permissões |
| Status (ativo/inativo) | Gestão de acesso |
| Data do último login | Segurança e auditoria operacional |
| Datas de criação/atualização | Controle interno |

No app, o login é feito com **e-mail e senha**. A edição de perfil permite alterar nome, e-mail, telefone, senha e foto. Administradores podem cadastrar e gerenciar líderes (nome, e-mail, senha, telefone, status e foto).

### 3.2. Apoiadores

| Dado | Uso principal |
|------|----------------|
| Nome | Identificação |
| CPF | Identificação única / prevenção de duplicidade |
| Data de nascimento | Cadastro e organização da base |
| Telefone e WhatsApp | Contato de campanha |
| Endereço completo (CEP, logradouro, número, complemento, bairro, cidade) | Localização e articulação territorial |
| Local de votação | Organização eleitoral |
| Intenção de voto (ex.: indeciso, simpatizante, apoiador, confirmado) | Classificação política de campanha |
| Observações | Anotações operacionais |
| Vínculo com o líder responsável | Organização da equipe |
| Data de exclusão lógica (quando aplicável) | Controle de remoção |

**Atenção:** a **intenção de voto** constitui dado pessoal sensível relacionado a opinião política (art. 5º, II, e art. 11 da LGPD).

No app, o formulário de cadastro/edição de pessoa envia esses campos à API. Líderes cadastram apoiadores vinculados ao próprio usuário; administradores podem visualizar a base geral (conforme regras da API).

**Dados de apoiador que o app atual não coleta:** e-mail do apoiador e foto do apoiador.

### 3.3. Histórico de alterações de apoiadores

Alterações em campos do apoiador podem gerar registros de auditoria contendo o campo alterado, valor anterior e valor novo (incluindo, quando alterados, CPF, telefone, endereço e demais dados). Esses registros existem para rastreabilidade das mudanças.

No app móvel atual, a consulta visual desse histórico **não está exposta na interface** (os endpoints existem na API).

### 3.4. Tokens e sessão

- Token de acesso (JWT), contendo identificadores como id do usuário, e-mail, nome e perfil;
- Refresh token associado ao usuário, com data de expiração.

A autenticação é feita por **Bearer JWT** (sem cookies de sessão no backend).

**No aplicativo móvel:**

- o **token de acesso** é armazenado em armazenamento seguro do dispositivo (`FlutterSecureStorage`; no Android, SharedPreferences criptografado);
- dados do usuário logado (incluindo eventual refresh token retornado pela API) ficam em `SharedPreferences` locais;
- a sessão local do app é considerada válida **apenas no mesmo dia** do login; no dia seguinte, a sessão local é limpa e o usuário precisa autenticar novamente;
- no logout, token, dados do usuário e caches locais de listagens são removidos do aparelho.

### 3.5. Publicações

- Título e conteúdo;
- Até três imagens;
- Autor (usuário do sistema).

No app, publicações podem ser criadas por administradores; edição/exclusão segue regras de perfil (administrador ou autor). Imagens são selecionadas da **galeria** do aparelho.

### 3.6. Dados de referência (não são “conta”, mas podem ser consultados)

- Cidades (IBGE);
- Locais de votação (TSE), incluindo nome, endereço, CEP, zona e coordenadas geográficas públicas.

As coordenadas de locais de votação vêm da base de referência da API. O app **não solicita nem coleta geolocalização em tempo real** do aparelho do usuário.

### 3.7. Metadados técnicos do aplicativo

Em requisições à API, o app envia cabeçalhos técnicos, entre eles:

| Cabeçalho | Conteúdo |
|-----------|----------|
| `X-Client-Id` | Identificador do cliente (`ramossomar-mobile`) |
| `X-Client-Secret` | Segredo de cliente do app (validação de acesso à API) |
| `X-App-Version` | Versão do aplicativo |
| `X-Platform` | Plataforma (`android` ou `ios`) |
| `X-Device-Id` | Identificador gerado localmente e persistido no aparelho (UUID) |
| `Authorization` | Bearer JWT, quando o usuário está autenticado |

No backend atual, esses dados são usados para controle de acesso do cliente e registros de diagnóstico (logs), sem modelo próprio de persistência de telemetria.

### 3.8. Dados armazenados localmente no aparelho

Além do envio à API, o app pode manter temporariamente no dispositivo:

| Dado local | Onde | Finalidade |
|------------|------|------------|
| Token de acesso | Armazenamento seguro | Manter sessão autenticada |
| Dados do usuário logado | Preferências locais | Exibir perfil e controlar permissões na UI |
| Identificador do dispositivo (`device_id`) | Preferências locais | Header `X-Device-Id` |
| Cache de listagens (apoiadores, usuários, publicações) | Preferências locais (curta duração, cerca de 5 minutos) | Reduzir chamadas à API e melhorar desempenho |
| Arquivos de relatório exportados | Diretório do app / compartilhamento do sistema | Geração e compartilhamento de relatórios pelo administrador |

### 3.9. Dados que o ecossistema atual **não** trata (ou não trata no app)

Com base no código atual da API e do app móvel:

- cobrança ou pagamento;
- envio automatizado de e-mail ou SMS pela plataforma;
- login social (Google, Apple etc.);
- Firebase, Google Analytics, Crashlytics, Sentry, Facebook Ads ou SDK de publicidade;
- cookies de tracking/analytics no app;
- coleta de geolocalização em tempo real do usuário;
- acesso à agenda de contatos do aparelho;
- armazenamento em nuvem de terceiros tipo S3 no backend (uploads ficam em armazenamento local do servidor).

---

## 4. Para que usamos os dados (finalidades)

Tratamos dados pessoais para:

1. **Autenticar** usuários e controlar o acesso conforme o perfil (Administrador ou Líder);
2. **Cadastrar, consultar, atualizar e gerenciar** apoiadores vinculados a líderes;
3. **Classificar intenção de voto** e registrar observações de campanha;
4. **Manter histórico de alterações** dos dados de apoiadores para auditoria;
5. **Publicar conteúdos internos** (textos e imagens) para uso na plataforma;
6. **Consultar cidades e locais de votação** a partir de bases públicas (IBGE/TSE);
7. **Gerar e exportar relatórios** (PDF/XLSX) com dados de cadastrados, para administradores — inclusive compartilhamento pelo sistema operacional do aparelho (ex.: WhatsApp, e-mail, arquivos), sob responsabilidade de quem exporta;
8. **Armazenar imagens** de perfil e de publicações;
9. **Proteger a plataforma** (limitação de requisições, validação de cliente, medidas de segurança);
10. **Preencher endereço automaticamente** a partir do CEP informado (consulta ViaCEP — ver seção 7);
11. **Cumprir obrigações legais** e atender direitos dos titulares, quando aplicável.

---

## 5. Bases legais (LGPD)

As bases legais devem ser confirmadas pelo controlador. Em regra, para este tipo de sistema, podem aplicar-se, conforme o caso:

| Tratamento | Base legal típica |
|------------|-------------------|
| Conta de usuário (ADMIN/LIDER) | Execução de contrato ou procedimentos preliminares (art. 7º, V) e/ou legítimo interesse (art. 7º, IX), conforme relação jurídica |
| Cadastro de apoiadores por líderes | Consentimento do titular (art. 7º, I) e/ou outra base válida, **a ser definida e documentada pelo controlador** |
| Intenção de voto (dado sensível) | Consentimento específico e destacado (art. 11, I) ou outra hipótese do art. 11, **quando cabível** |
| Segurança, prevenção a fraudes e logs | Legítimo interesse (art. 7º, IX) e/ou cumprimento de obrigação legal |
| Dados públicos IBGE/TSE | Dados públicos / legítimo interesse operacional |
| Consulta de CEP (ViaCEP) | Legítimo interesse operacional / execução da funcionalidade de cadastro, com envio apenas do CEP |

**Importante:** como apoiadores são cadastrados por terceiros (líderes), o controlador deve assegurar que exista base legal adequada e, quando necessário, **consentimento** ou outra hipótese válida antes da inclusão dos dados na plataforma.

---

## 6. Como os dados são coletados

- **Diretamente do usuário do sistema**, no cadastro/login e atualização de perfil (incluindo foto selecionada na galeria do aparelho);
- **Por líderes/administradores**, ao cadastrar ou editar apoiadores no app;
- **Automaticamente**, em metadados técnicos de requisições (IP para rate limit, headers do app, tokens) e cache local no aparelho;
- **De fontes públicas**, na importação de cidades (IBGE) e locais de votação (TSE);
- **De serviço de CEP**, quando o usuário informa um CEP no formulário de apoiador (ViaCEP).

---

## 7. Compartilhamento de dados

### 7.1. Dentro da plataforma

- Líderes acessam os apoiadores sob sua responsabilidade;
- Administradores podem acessar a base e exportar relatórios;
- Imagens servidas em caminhos públicos de upload podem ser acessíveis por URL, conforme configuração do servidor.

### 7.2. Fora da plataforma

No backend atual **não há** envio automatizado de dados de usuários ou apoiadores para APIs de marketing, pagamento, e-mail ou SMS.

Podem ocorrer compartilhamentos nas hipóteses legais, por exemplo:

- exigência de autoridade competente;
- prestadores de infraestrutura (hospedagem, banco de dados MySQL) que processam dados sob instrução do controlador;
- exportações manuais (PDF/XLSX) realizadas por administradores — nesses casos, o uso posterior é de responsabilidade de quem exporta, nos termos da lei e das orientações internas da campanha/controlador;
- compartilhamento de arquivos de relatório pelo administrador, via apps do próprio aparelho (funcionalidade de compartilhar do sistema operacional).

### 7.3. Fontes públicas e serviços de terceiros consultados

| Serviço | O que ocorre |
|---------|----------------|
| **IBGE** | A API baixa dados públicos de municípios (referência) |
| **TSE** | A API baixa dados públicos de locais de votação (referência) |
| **ViaCEP** (`viacep.com.br`) | O **app** envia o CEP digitado para obter logradouro, bairro, cidade e UF e pré-preencher o formulário de endereço do apoiador |

As integrações IBGE/TSE **não enviam** dados pessoais de apoiadores ou usuários a esses órgãos. O ViaCEP recebe apenas o **CEP** informado no formulário.

Não há, no app atual, SDK de analytics, crash reporting ou publicidade que envie dados a terceiros para rastreamento.

---

## 8. Cookies, SDKs e tecnologias do aplicativo

### 8.1. Backend / API

- autenticação por **JWT** (Authorization Bearer);
- **sem cookies de sessão**;
- CORS configurado sem credenciais de cookie.

### 8.2. Aplicativo móvel

O app **não utiliza cookies** de navegador. Tecnologias relevantes no cliente:

| Tecnologia / pacote | Uso |
|---------------------|-----|
| Comunicação HTTP com a API Convertix | Envio e recebimento dos dados da plataforma |
| `flutter_secure_storage` | Token de autenticação |
| `shared_preferences` | Dados de sessão, device id e cache de páginas |
| `image_picker` / `image_cropper` | Seleção e recorte de imagens (perfil e publicações) |
| `share_plus` / `path_provider` | Exportação e compartilhamento de relatórios |
| ViaCEP (HTTP) | Autopreenchimento de endereço por CEP |

**Não há** Firebase, Google Analytics, Crashlytics, Ads ou login social no app atual.

Se o painel web utilizar cookies ou analytics no futuro, essa seção deverá ser atualizada.

### 8.3. Permissões do aparelho

#### Android

- Internet;
- Câmera;
- Leitura de imagens / armazenamento (conforme versão do sistema).

#### iOS

| Permissão | Finalidade declarada |
|-----------|----------------------|
| Câmera | Fotos de perfil e publicações |
| Microfone | Associado ao uso da câmera (declaração do sistema) |
| Galeria (leitura) | Anexar imagens de perfil e publicações |
| Galeria (gravação) | Salvar imagens ao compartilhar/exportar |

**Uso efetivo no código atual do app:** seleção de imagens a partir da **galeria** (perfil e publicações). Não há coleta de localização GPS do aparelho.

O app **não solicita** permissão de localização (`ACCESS_FINE_LOCATION` / equivalentes).

---

## 9. Armazenamento, segurança e localização

### 9.1. Onde ficam os dados

- Banco de dados **MySQL** (dados cadastrais, tokens, histórico etc.);
- Arquivos de imagem em **armazenamento local do servidor** (diretório de uploads);
- No aparelho do usuário: token seguro, preferências de sessão, cache temporário e eventuais arquivos de relatório gerados para compartilhamento.

A localização do servidor/hospedagem deve ser informada pelo controlador: \[PAÍS / REGIÃO DO DATACENTER\].

### 9.2. Medidas de segurança observadas no backend

Entre outras:

- senhas com hash (BCrypt);
- autenticação por token JWT com sessão sem estado no servidor;
- limitação de taxa de requisições (rate limiting);
- cabeçalhos de segurança HTTP;
- validação de tipos de arquivo em uploads;
- desativação de documentação interativa (Swagger) em produção;
- respostas de erro sem exposição detalhada em produção.

### 9.3. Medidas observadas no aplicativo

Entre outras:

- token de acesso em armazenamento seguro do sistema operacional;
- sessão local com validade diária e limpeza no logout;
- comunicação com a API em HTTPS no ambiente de produção;
- validação de cliente da API por identificadores de app (`X-Client-Id` / `X-Client-Secret`).

Nenhuma medida elimina totalmente riscos. Em caso de incidente relevante, o controlador avaliará comunicação aos titulares e à ANPD, nos termos da LGPD.

---

## 10. Retenção e exclusão

Com base no comportamento atual da API:

| Dado / recurso | Comportamento |
|----------------|---------------|
| Apoiador | Exclusão lógica (marca data de exclusão); o histórico de alterações pode permanecer |
| Usuário | Desativação lógica (conta inativa); dados podem permanecer armazenados |
| Publicação | Exclusão física, com remoção dos arquivos de imagem associados |
| Refresh token | Pode ser removido fisicamente |
| Histórico de apoiador | Pode ser removido fisicamente por operação administrativa |
| Dados locais no app | Removidos no logout / expiração diária da sessão; cache de listagens é de curta duração |

**Prazo de retenção:** \[DEFINIR — ex.: enquanto a campanha/conta estiver ativa + X meses/anos após encerramento, ou até solicitação de exclusão quando cabível\].

Não há, no backend atual, exclusão automática por prazo nem endpoint de “excluir minha conta” self-service para apoiadores. Solicitações de titulares devem ser atendidas pelos canais do controlador (seção 12).

Desinstalar o aplicativo remove os dados armazenados localmente no aparelho, mas **não apaga automaticamente** os dados já enviados ao servidor.

---

## 11. Direitos dos titulares (LGPD)

O titular pode solicitar, na medida aplicável à lei e ao caso concreto:

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

O sistema permite o cadastro de data de nascimento de apoiadores **sem validação automática de idade mínima** no backend nem no app.

O controlador deve orientar os usuários (líderes/administradores) a **não cadastrar menores** sem o cumprimento das regras legais aplicáveis (incluindo, quando couber, consentimento específico de pelo menos um dos pais ou responsável legal — art. 14 da LGPD).

Idade mínima recomendada de uso da plataforma pelos operadores (líderes/admins): **18 anos**, salvo definição diversa do controlador.

---

## 13. Transferência internacional

Se a hospedagem, backups ou prestadores estiverem fora do Brasil, haverá transferência internacional de dados, a ser informada e amparada nas hipóteses do art. 33 da LGPD.

A consulta de CEP ao ViaCEP pode envolver processamento conforme a infraestrutura desse prestador.

\[INFORMAR: se há ou não transferência internacional da hospedagem/banco e para quais países/serviços\]

---

## 14. Relatórios e exportações

Administradores podem exportar relatórios contendo dados de apoiadores (incluindo identificação, contato, endereço e intenção de voto). Esses arquivos saem do ambiente controlado da API e devem ser tratados com confidencialidade, acesso restrito e descarte seguro quando não forem mais necessários.

No app, a exportação pode gerar arquivo no aparelho e acionar o **compartilhamento nativo** do sistema operacional. Quem compartilha o arquivo é responsável pelo destino escolhido (outros apps, nuvens pessoais etc.).

---

## 15. Papéis e acesso no aplicativo

| Perfil | Acesso típico no app |
|--------|----------------------|
| **Administrador** | Cadastrados (visão geral), feed (incluindo criação de publicações), perfil, gestão de líderes e relatórios/exportações |
| **Líder** | Seus cadastrados, feed (consumo), perfil próprio; ao cadastrar apoiador, o vínculo é com o líder logado |

O escopo exato das listagens (todos vs. apenas os do líder) é aplicado pela API conforme o token e o perfil do usuário autenticado.

---

## 16. Alterações desta política

Esta política pode ser atualizada para refletir mudanças na plataforma, na legislação ou nas práticas do controlador. A data de “última atualização” será revisada a cada alteração relevante. Quando a mudança for substancial, o controlador poderá comunicar pelos canais habituais da plataforma.

---

## 17. Contato

| Assunto | Contato |
|---------|---------|
| Privacidade / LGPD (controlador) | Victor Muller da Luz — victormuller050@gmail.com |
| Suporte técnico / desenvolvimento | Convertix — contato@convertix.net |

O aplicativo atual **não exibe** tela própria de política de privacidade nem canal de contato embutido; o canal oficial permanece o e-mail acima.

---

## 18. Lei aplicável

Esta política é regida pela legislação brasileira, em especial a Lei nº 13.709/2018 (LGPD). Fica eleito o foro da comarca de **Araucária/PR**, salvo competência legal diversa.

---

## Anexo A — Mapa resumido de dados × finalidade (para o front)

Use esta tabela no app/site se quiser uma versão mais curta:

| Categoria | Exemplos | Finalidade |
|-----------|----------|------------|
| Identificação | Nome, CPF, e-mail | Cadastro, login, unicidade |
| Contato | Telefone, WhatsApp | Comunicação de campanha |
| Localização | Endereço, cidade, local de votação | Articulação territorial |
| Perfil político | Intenção de voto, observações | Gestão de campanha |
| Credenciais | Senha (hash), tokens | Segurança e acesso |
| Mídia | Fotos de perfil e publicações | Identificação e conteúdo interno |
| Técnicos | IP (rate limit), device/app headers, device_id local | Segurança e operação |
| Cache local | Listagens temporárias no aparelho | Desempenho do app |
| Terceiros de apoio | CEP enviado ao ViaCEP | Autopreenchimento de endereço |

---

## Anexo B — Ainda pendente (controlador)

Itens que ainda dependem de definição operacional do controlador (não inferíveis só pelo código):

- Telefone ou WhatsApp de contato institucional (além do e-mail);
- País/região onde o servidor/banco está hospedado;
- Se há transferência internacional de dados da hospedagem/backups e para quais países/serviços;
- Prazo de retenção após o fim da campanha/uso;
- Bases legais definitivas escolhidas para cadastro de apoiadores e intenção de voto (e como o consentimento será obtido/registrado, se for o caso);
- Eventual painel web: cookies/analytics, se forem adotados.

### Já coberto com base no app (revisão do front)

- Permissões do celular (câmera, galeria, armazenamento/internet; sem GPS);
- Ausência de SDKs de analytics/Firebase no app atual;
- Armazenamento local (token seguro, sessão, cache, device id);
- Headers técnicos enviados à API;
- Integração ViaCEP no cadastro de endereço;
- Compartilhamento de relatórios pelo administrador via SO do aparelho;
- Identificadores do app (`com.net.convertix.ramossomar`) e API de produção Convertix.
