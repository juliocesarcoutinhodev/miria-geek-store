# miria-geek-adm

Painel administrativo da **Miria Geek Store**, construído com Angular 21, PrimeNG 19 e Tailwind CSS 4.

---

## Stack

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Angular | ~21.2 | Framework principal (Zoneless) |
| PrimeNG | ^19 | Biblioteca de componentes UI |
| Tailwind CSS | ^4 | Utilitários de estilo |
| TypeScript | ~5.9 | Linguagem (strict mode ativo) |
| RxJS | ~7.8 | Programação reativa |
| pnpm | 11.x | Gerenciador de pacotes |

---

## Pré-requisitos

- **Node.js** 22 LTS (recomendado) — a versão 25.x funciona mas não é LTS
- **pnpm** 11.x — `npm install -g pnpm`
- **Angular CLI** 21 — `npm install -g @angular/cli@21`
- **Backend** `miria-geek-api` rodando em `http://localhost:8080`

---

## Instalação

```bash
# na pasta miria-geek-adm
pnpm install
```

> O `pnpm-workspace.yaml` já está configurado com `allowBuilds` para os pacotes nativos (`esbuild`, `@parcel/watcher`, etc.). Não é necessário nenhuma configuração adicional.

---

## Rodando em desenvolvimento

```bash
pnpm start
```

O app sobe em **http://localhost:4200** e o proxy de desenvolvimento encaminha automaticamente as chamadas `/api/*` para o backend em `http://localhost:8080`. Não é necessário configurar CORS.

### Credenciais de acesso (dev)

Por padrão, o seed do backend cria um admin com:

- **Email:** `admin@miriageek.com`
- **Senha:** `Admin@123`

> Verifique os valores reais nas variáveis `ADMIN_SEED_EMAIL` e `ADMIN_SEED_PASSWORD` do backend.

---

## Scripts disponíveis

| Comando | Descrição |
|---|---|
| `pnpm start` | Serve em modo desenvolvimento com proxy |
| `pnpm build` | Build de produção em `dist/miria-geek-adm` |
| `pnpm watch` | Build contínuo em modo desenvolvimento |
| `pnpm lint` | Executa ESLint em todos os arquivos |
| `pnpm test` | Executa os testes unitários com Karma |

---

## Funcionalidades implementadas

| Área | O que está pronto |
|---|---|
| **Autenticação** | Login, logout, recuperação de senha, redefinição de senha |
| **Sessão** | Hidratação automática no F5 (APP_INITIALIZER + sessionReady guard) |
| **Token refresh** | Refresh automático transparente quando o access token expira |
| **Perfil** | Visualização, edição de nome e alteração de senha |
| **Layout** | Sidebar, topbar com dark mode, configurador de tema |
| **Usuários** | Listagem paginada com filtros, criar, editar, ativar/inativar |
| **Categorias** | Listagem paginada com filtros, criar, editar, ativar/inativar, excluir |
| **Produtos — listagem** | Tabela com thumbnail, filtros (nome/status/destaque/ordenação), drawer lateral com detalhes completos (imagens com lupa, variantes), toggle de status |
| **Produtos — formulário** | Criar (info + variantes inline); editar em 3 seções independentes: informações, variantes (add/editar/ativar/estoque), imagens (upload/principal/remover) |

---

## Estrutura do projeto

```
src/
├── app/
│   ├── core/
│   │   ├── auth/
│   │   │   ├── auth.guard.ts              # Guard async: aguarda sessionReady antes de verificar auth
│   │   │   ├── auth.interceptor.ts        # withCredentials + refresh automático de token em 401
│   │   │   ├── auth.model.ts              # Interfaces (AuthUser, LoginRequest, ResetPasswordRequest)
│   │   │   └── auth.service.ts            # Estado do usuário, login/logout/refresh, initializeSession
│   │   ├── admin/
│   │   │   ├── admin-user.model.ts        # Interfaces (UserSummary, AdminUserDetail, PagedResponse, ...)
│   │   │   └── admin-user.service.ts      # CRUD de usuários admin (/admin/users)
│   │   ├── catalog/
│   │   │   ├── category.model.ts          # Interfaces (Category, CategoryPagedResponse, CreateCategoryRequest, ...)
│   │   │   ├── category.service.ts        # CRUD de categorias (/admin/categories)
│   │   │   ├── product.model.ts           # Interfaces (ProductSummary, ProductDetail, ProductImageInfo,
│   │   │   │                              #   ProductVariantInfo, CreateProductRequest, UpdateVariantRequest,
│   │   │   │                              #   AdjustStockRequest, ReorderImagesRequest, ...)
│   │   │   └── product.service.ts         # CRUD completo de produtos, variantes e imagens
│   │   ├── errors/
│   │   │   └── app-error-handler.ts       # ErrorHandler global — loga em dev, trata ChunkLoadError
│   │   ├── interceptors/
│   │   │   └── http-timeout.interceptor.ts  # Timeout de 30s em todas as requests HTTP
│   │   ├── profile/
│   │   │   ├── profile.model.ts           # Interfaces (UserProfile, UpdateProfileRequest, ChangePasswordRequest)
│   │   │   └── profile.service.ts         # GET/PATCH /users/me + PATCH /users/me/password
│   │   └── router/
│   │       └── app-title-strategy.ts      # TitleStrategy: "Título da Rota | Miria Geek Store"
│   ├── layout/
│   │   ├── component/                     # Shell do painel (topbar, sidebar, menu, footer)
│   │   └── service/
│   │       └── layout.service.ts          # Estado do layout (tema, modo menu, dark mode)
│   └── pages/
│       ├── auth/
│       │   ├── login.ts                   # Tela de login + dialog "Esqueceu a senha?"
│       │   ├── reset-password.ts          # Tela de redefinição de senha (via token por e-mail)
│       │   ├── access.ts                  # Página de acesso negado
│       │   └── auth.routes.ts             # Rotas do módulo auth (lazy)
│       ├── cadastros/
│       │   └── usuarios/
│       │       └── usuarios.ts            # Listagem paginada + criar/editar/ativar/inativar usuários
│       ├── catalog/
│       │   ├── categories/
│       │   │   └── categories.ts          # Listagem paginada + criar/editar/toggle/excluir categorias
│       │   └── products/
│       │       ├── products.ts            # Listagem com thumbnail, filtros, drawer de detalhes
│       │       └── product-form.ts        # Criar e editar produto (3 seções: info, variantes, imagens)
│       ├── dashboard/                     # Dashboard inicial (widgets de exemplo)
│       └── profile/
│           └── profile.ts                 # Tela de perfil: dados pessoais + alteração de senha
├── environments/
│   ├── environment.ts                     # Produção: apiUrl = '/api/v1'
│   └── environment.development.ts        # Dev: apiUrl = '/api/v1' (proxy resolve o host)
├── app.config.ts                          # Bootstrap: todos os providers, interceptors, estratégias
├── app.routes.ts                          # Rotas raiz com lazy loading + títulos por rota
└── app.component.ts                       # Componente raiz (só RouterOutlet)
```

---

## Arquitetura e decisões técnicas

### Zoneless Angular

O app usa `provideZonelessChangeDetection()` — **zone.js não está nos polyfills**. Isso significa:

- Change detection é disparada exclusivamente por **signals** e `ApplicationRef.tick()`
- Prefira signals (`signal()`, `computed()`, `effect()`) a variáveis simples em componentes
- Ao chamar APIs de terceiros que dependem de Zone.js (ex: PrimeNG `MessageService`), chame `appRef.tick()` logo após para forçar a atualização da UI
- Use `firstValueFrom()` + `async/await` em vez de `.subscribe()` em operações de formulário — comportamento mais previsível com Zoneless

### Autenticação e sessão

- Fluxo baseado em **HttpOnly cookies** — os tokens `access_token` e `refresh_token` são gerenciados pelo browser, nunca acessíveis via JavaScript
- `APP_INITIALIZER` chama `GET /api/v1/users/me` no bootstrap para hidratar o estado do usuário — o F5/Ctrl+F5 não causa logout se os cookies ainda forem válidos
- O `authGuard` é **async** e aguarda `auth.sessionReady` antes de verificar `isAuthenticated()` — garante que o guard nunca roda antes da hidratação terminar (necessário em Zoneless, onde `withEnabledBlockingInitialNavigation` pode não respeitar a Promise do initializer)
- O `authInterceptor` adiciona `withCredentials: true` e, em caso de 401 em rota protegida, chama `/auth/refresh` automaticamente e retenta a request original
- Apenas usuários com `ROLE_ADMIN` conseguem acessar o painel

### Tratamento de erros HTTP

- O backend sempre retorna `{ code: string, message: string, timestamp: string }` em erros
- Use `err.error?.message` para exibir a mensagem ao usuário — não reinvente as mensagens no frontend
- Fallback hardcoded apenas para `status === 0` (sem conexão com o servidor)
- Notificações de erro via `PrimeNG Toast` com `appRef.tick()` após `messageService.add()`

### MessageService e Toast

- `MessageService` é fornecido **globalmente** em `app.config.ts` — todos os componentes autenticados dentro do `AppLayout` compartilham a mesma instância e o mesmo `<p-toast>` do layout
- A página de **login** possui seu próprio `providers: [MessageService]` + `<p-toast>` local, pois fica fora do `AppLayout`
- `provideAnimationsAsync()` é **obrigatório** — sem ele, o Toast quebra com `NG05105` ao tentar renderizar a primeira mensagem

### Lazy loading

Todas as rotas usam `loadComponent` ou `loadChildren` — nenhuma feature é carregada no bundle inicial:

```
Bundle inicial:  ~544 KB   (Angular core + PrimeNG core + layout básico)
dashboard:       carrega ao acessar /dashboard
profile:         carrega ao acessar /profile
auth-routes:     carrega ao acessar /auth/*
usuarios:        carrega ao acessar /registrations/users
categories:      carrega ao acessar /catalog/categories
products:        carrega ao acessar /catalog/products
product-form:    carrega ao acessar /catalog/products/new e /catalog/products/:id/edit
```

---

## Padrões enterprise

| Padrão | Arquivo | Comportamento |
|---|---|---|
| **Global error handler** | `core/errors/app-error-handler.ts` | Captura erros não tratados; em prod silencia, em dev loga. `ChunkLoadError` dispara reload automático (deploy sem perder usuário) |
| **HTTP timeout** | `core/interceptors/http-timeout.interceptor.ts` | Aborta requests após 30s — sem requests penduradas indefinidamente |
| **Title strategy** | `core/router/app-title-strategy.ts` | Atualiza o título do browser por rota: `Dashboard \| Miria Geek Store` |
| **Session hydration** | `auth.service.ts → initializeSession()` | APP_INITIALIZER que restaura sessão via cookie no F5 |
| **Async guard** | `auth.guard.ts` | Aguarda Promise da sessão antes de decidir — nunca redireciona erroneamente |
| **Token refresh** | `auth.interceptor.ts` | Refresh transparente em 401 sem re-login do usuário |

---

## Proxy de desenvolvimento

O arquivo `proxy.conf.json` encaminha todas as chamadas `/api/*` para o backend:

```json
{
    "/api": {
        "target": "http://localhost:8080",
        "secure": false,
        "changeOrigin": true
    }
}
```

Para apontar para outro ambiente em dev, altere o `target` localmente (não commitar).

---

## Adicionando novas páginas

### 1. Criar o componente

```typescript
// src/app/pages/minha-feature/minha-feature.ts
@Component({
    selector: 'app-minha-feature',
    standalone: true,
    imports: [...],
    template: `...`
})
export class MinhaFeature {}
```

### 2. Registrar a rota com lazy loading e título

```typescript
// em app.routes.ts, dentro do children do AppLayout:
{
    path: 'minha-feature',
    loadComponent: () => import('./app/pages/minha-feature/minha-feature').then(m => m.MinhaFeature),
    title: 'Minha Feature'
}
```

### 3. Adicionar ao menu lateral

Edite `src/app/layout/component/app.menu.ts` e inclua o item com `routerLink` e ícone do [PrimeIcons](https://primeng.org/icons).

---

## Linting e formatação

O projeto usa **ESLint 9** (flat config) com `@angular-eslint` e **Prettier**.

```bash
# verificar
pnpm lint

# formatar um arquivo
npx prettier --write src/app/pages/minha-feature/minha-feature.ts

# formatar tudo
npx prettier --write "src/**/*.ts"
```

Regras principais:

- Indentação com **4 espaços** (sem tabs)
- Aspas **simples** no TypeScript
- Vírgula final desativada (`trailingComma: "none"`)
- `printWidth` de 250 — sem quebra forçada de linha
- Seletores de componentes: prefixo `app-`, formato `kebab-case`

---

## Build de produção

```bash
pnpm build
```

Os artefatos são gerados em `dist/miria-geek-adm/browser/`. Sirva a pasta como SPA — configure o servidor web para redirecionar todas as rotas para `index.html`.

Em produção, configure um **reverse proxy** que encaminhe `/api` para o backend — o frontend não conhece o host do backend em runtime.

---

## Dependências notáveis

| Pacote | Motivo |
|---|---|
| `tailwindcss-primeui` | Integração de tokens de tema do PrimeNG com Tailwind v4 |
| `@primeuix/themes` | Preset de tema Aura para PrimeNG 19 |
| `chart.js` | Gráficos nos widgets do dashboard |
| `eslint-plugin-prettier` | Prettier como regra de ESLint |
