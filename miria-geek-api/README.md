# Miria Geek API

Backend da **Miria Geek Store** — loja de produtos geek/otaku.

Construído com **Spring Boot 4.0**, arquitetura **Hexagonal (Ports & Adapters)** e **DDD (Domain-Driven Design)**, organizado como módulos via **Spring Modulith**.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Persistência | Spring Data JPA + PostgreSQL 17 + Flyway |
| Mensageria | Apache Kafka 3.9 |
| Segurança | Spring Security + OAuth2 Resource Server + JJWT 0.12.6 (HS256) |
| E-mail | Spring Mail (Gmail SMTP / STARTTLS) |
| Armazenamento | MinIO (S3-compatible, SDK 8.5) |
| Documentação | SpringDoc OpenAPI 2.8 (Swagger UI) |
| Observabilidade | Spring Actuator + Micrometer + Prometheus |
| Módulos | Spring Modulith |
| Mapeamento | MapStruct 1.6.3 + Lombok |
| Variáveis | dotenv-java |

---

## Pré-requisitos

- Java 25+
- Maven 3.9+
- Docker + Docker Compose

---

## Subindo a infraestrutura local

Na raiz do monorepo (`miria-geek-store/`):

```bash
docker compose -f docker-compose.local.yml up -d
```

Serviços iniciados:

| Serviço | Porta | Descrição |
|---|---|---|
| PostgreSQL 17 | `5432` | Banco de dados principal |
| Apache Kafka 3.9 | `9092` | Mensageria |
| Kafka UI | `8090` | Painel web do Kafka |
| MinIO | `9000` / `9001` | Armazenamento de imagens (API / Console) |
| Mailpit | `1025` / `8025` | SMTP local para dev (captura e-mails sem enviá-los) |

> O arquivo `docker-compose.yml` na raiz é reservado para produção (VPS). Para desenvolvimento use sempre `docker-compose.local.yml`.

---

## Configuração

Crie o arquivo `.env` dentro de `miria-geek-api/` (nunca commitar com dados reais):

```env
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# Banco
DB_URL=jdbc:postgresql://localhost:5432/miria_geek_dev
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_GROUP_ID=miria-geek-dev

# JWT — chave Base64 HS256, mínimo 256 bits
# Gere com: openssl rand -base64 32
JWT_SECRET=<gere-aqui>

# Cookies
COOKIE_DOMAIN=localhost
COOKIE_SECURE=false

# E-mail (Gmail com App Password — https://myaccount.google.com/apppasswords)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<seu-email@gmail.com>
MAIL_PASSWORD=<app-password-16-digitos>
MAIL_FROM=<seu-email@gmail.com>

# MinIO (armazenamento de imagens)
MINIO_ENDPOINT=http://localhost:9000
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
MINIO_BUCKET_PRODUCTS=miria-products
MINIO_PUBLIC_URL=http://localhost:9000

# URL base da API (usada em links de verificação de e-mail)
BASE_URL=http://localhost:8080

# URL base do frontend (usada no link de redefinição de senha)
FRONTEND_URL=http://localhost:4200

# Primeiro admin criado automaticamente no perfil dev
ADMIN_SEED_EMAIL=admin@miriageek.com
ADMIN_SEED_PASSWORD=Admin@123
```

---

## Rodando a API

```bash
./mvnw spring-boot:run
```

No primeiro startup com perfil `dev`, o **AdminSeeder** cria automaticamente um usuário administrador com as credenciais de `ADMIN_SEED_EMAIL` e `ADMIN_SEED_PASSWORD`. As credenciais aparecem no log — **altere a senha após o primeiro login**.

---

## Documentação interativa

| URL | Descrição |
|---|---|
| `http://localhost:8080/swagger-ui` | Swagger UI |
| `http://localhost:8080/api-docs` | OpenAPI JSON |
| `http://localhost:8090` | Kafka UI |
| `http://localhost:9001` | MinIO Console |
| `http://localhost:8080/actuator/health` | Health check |

A collection Postman está em `doc/postman/miria-geek-api.postman_collection.json`.

---

## Arquitetura

```
src/main/java/br/com/miriageekstore/
├── identity/                          ← módulo Identity (EP-01)
│   ├── domain/
│   │   ├── model/                     ← agregados e value objects (zero Spring)
│   │   ├── port/
│   │   │   ├── in/                    ← interfaces de use case + commands + results
│   │   │   └── out/                   ← interfaces de repositórios e serviços externos
│   │   ├── event/                     ← eventos de domínio publicados no Kafka
│   │   └── exception/                 ← exceções de domínio
│   ├── application/
│   │   └── usecase/                   ← implementações dos use cases (@Service)
│   └── infrastructure/
│       ├── adapter/
│       │   ├── in/web/                ← controllers REST + DTOs + CategoryWebMapper
│       │   └── out/
│       │       ├── persistence/       ← JPA entities + Spring Data + adapters
│       │       ├── messaging/         ← KafkaEventPublisher
│       │       ├── mail/              ← JavaMailEmailSender (async)
│       │       ├── security/          ← BcryptPasswordHasher + JjwtTokenService
│       │       ├── ratelimit/         ← rate limiters in-memory
│       │       └── order/             ← NoOpActiveOrderChecker (placeholder)
│       └── config/                    ← SecurityConfig, CookieFactory, AdminSeeder
├── catalog/                           ← módulo Catalog (EP-02)
│   ├── domain/
│   │   ├── model/                     ← Category, Product, ProductVariant, ProductImage
│   │   │                                 ProductId, CategoryId, Slug, Sku
│   │   │                                 StockMovement, StockMovementType (ENTRADA|SAIDA)
│   │   ├── port/
│   │   │   ├── in/                    ← use cases + commands + results
│   │   │   │                             (categories, products, images, variants, stock)
│   │   │   └── out/                   ← CategoryRepository, ProductRepository,
│   │   │                                 ProductCatalogRepository, StockMovementRepository,
│   │   │                                 StoragePort, CatalogEventPublisher
│   │   ├── event/                     ← ProductUpdated, ProductStatusChanged, StockUpdated
│   │   └── exception/                 ← CategoryNotFoundException, CategoryHasProductsException,
│   │                                     ProductNotFoundException, VariantNotFoundException,
│   │                                     LastActiveVariantException, InsufficientStockException,
│   │                                     ProductCannotBeActivatedException, StorageException
│   ├── application/
│   │   └── usecase/                   ← use cases de categorias, produtos, imagens,
│   │                                     variantes e ajuste de estoque
│   └── infrastructure/
│       ├── adapter/
│       │   ├── in/web/                ← AdminCategoryController, PublicCategoryController
│       │   │                             AdminProductController, PublicProductController
│       │   │                             (DTOs, WebMappers — pacote package-private)
│       │   └── out/
│       │       ├── persistence/       ← CategoryEntity, ProductEntity, ProductVariantEntity,
│       │       │                         ProductImageEntity, StockMovementEntity + JPA + adapters
│       │       │                         ProductCatalogPersistenceAdapter (native SQL com aggregates)
│       │       └── storage/           ← MinioStorageAdapter (upload, delete, URL pública)
│       └── config/                    ← MinioConfig (criação do bucket no startup)
└── shared/
    └── infrastructure/config/         ← GlobalExceptionHandler
```

### Regras da arquitetura

| Camada | Pode depender de | Não pode depender de |
|---|---|---|
| `domain` | Nada externo (Java puro) | Spring, JPA, Kafka, qualquer infra |
| `application` | `domain` | `infrastructure` diretamente |
| `infrastructure` | `domain`, `application`, Spring | Outras features de domínio diretamente |

Os controllers delegam **100%** para os use cases — sem lógica de negócio no adapter de entrada.

---

## Segurança

- **JWT autossignado** (HS256) gerado pelo `JjwtTokenService` — sem dependência de Keycloak.
- Tokens entregues como **cookies HttpOnly / SameSite=Strict**:
  - `access_token` — validade 15 min, path `/`
  - `refresh_token` — validade 7 dias, path `/api/v1/auth/refresh`
- O `CookieBearerTokenResolver` lê o JWT do cookie `access_token`. Fallback para header `Authorization: Bearer <token>` (útil para testes no Postman).
- Rotas `/api/v1/admin/**` exigem `ROLE_ADMIN` via `JwtAuthenticationConverter` (lê o claim `roles` do JWT).
- Bloqueio de conta por **5 tentativas de login falhas** — desbloqueio automático após 15 min.
- Rotação de refresh token com **family ID** — roubo de token detectado e todas as sessões da família são revogadas.

---

## Banco de dados

Migrations gerenciadas pelo **Flyway** (`src/main/resources/db/migration/`):

| Versão | Descrição |
|---|---|
| V1 | Tabelas `users` e `user_roles` |
| V2 | Tabela `refresh_tokens` |
| V3 | Campos de rotação de refresh token |
| V4 | Campos de rotação com `IF NOT EXISTS` (idempotente) |
| V5 | Tabela `password_reset_tokens` |
| V6 | Tabela `addresses` |
| V7 | Correção do tipo da coluna `state` (CHAR → VARCHAR) |
| V8 | Coluna `created_by_admin_id` na tabela `users` |
| V9 | Coluna `last_login_at` na tabela `users` |
| V10 | Tabela `audit_log` (id, user_id, admin_id, action, created_at) |
| V11 | Tabela `categories` (id, name, slug, description, active, created_at) |
| V12 | Tabelas `products` e `product_variants` (id, name, slug, category_id, status, featured, variants com SKU único) |
| V13 | Tabela `product_images` (id, product_id, url, filename, principal, image_order, created_at) |
| V14 | Coluna `active boolean` em `product_variants` + Tabela `stock_movements` (id, variant_id, product_id, type, quantity, motivo, admin_id, created_at) |
| V15 | Tabelas `orders` e `order_items` + sequence `orders_order_number_seq` + trigger de geração automática de `order_number` (`ORD-00000001`) |
| V16 | Colunas `sku` e `principal_image` em `order_items` + `delivery_address_id` em `orders` + tabelas `payments` e `order_status_history` |
| V17 | Coluna `tracking_code`, `carrier`, `carrier_name`, `tracking_url` em `orders` |
| V18 | Colunas `weight`, `width`, `height`, `depth` em `product_variants` |
| V19 | Tabelas `carts` e `cart_items` (id, user_id único, items com price_snapshot e UNIQUE por variante por carrinho) |

---

## Mensageria (Kafka)

Tópicos publicados pelo módulo Identity:

| Tópico | Evento | Gatilho |
|---|---|---|
| `identity.user-registered` | `UserRegistered` | Cadastro de cliente |
| `identity.user-email-verified` | `UserEmailVerified` | Verificação de e-mail |
| `identity.user-logged-in` | `UserLoggedIn` | Login bem-sucedido |
| `identity.verification-resent` | `UserVerificationResent` | Reenvio de verificação |
| `identity.password-reset-requested` | `PasswordResetRequested` | Solicitação de recuperação de senha |
| `identity.admin-user-created` | `AdminUserCreated` | Criação de administrador |

Tópicos publicados pelo módulo Catalog:

| Tópico | Evento | Gatilho |
|---|---|---|
| `catalog.product-updated` | `ProductUpdated` | Atualização de produto (PUT/PATCH) |
| `catalog.product-status-changed` | `ProductStatusChanged` | Ativação ou inativação de produto |
| `catalog.stock-updated` | `StockUpdated` | Ajuste de estoque de variante (ENTRADA ou SAÍDA) |

---

## E-mails

Templates HTML em `src/main/resources/templates/email/` com tema anime/geek e CSS inline (compatível com Gmail):

| Template | Assunto | Gatilho |
|---|---|---|
| `email-verification.html` | ✨ Confirme seu e-mail | Cadastro e reenvio de verificação |
| `login-notification.html` | 🔐 Novo acesso detectado | Login bem-sucedido |
| `password-reset.html` | 🔑 Redefinição de senha | Solicitação de recuperação |
| `admin-welcome.html` | 👑 Bem-vindo à equipe administrativa | Criação de admin |

O envio é **assíncrono** (`@Async`) — nunca bloqueia a resposta HTTP. Falhas são logadas mas não propagadas.

---

## EP-01 · Identity — Status das stories

| Story | Descrição | Pontos | Endpoint(s) |
|---|---|---|---|
| US-01.01 | Cadastro de usuário (cliente) | 5 | `POST /api/v1/auth/register` |
| US-01.02 | Verificação de e-mail | 3 | `GET /api/v1/auth/verify-email` |
| US-01.03 | Reenvio de verificação | 2 | `POST /api/v1/auth/resend-verification` |
| US-01.04 | Login | 8 | `POST /api/v1/auth/login` |
| US-01.05 | Renovação de token (refresh) | 5 | `POST /api/v1/auth/refresh` |
| US-01.06 | Logout | 2 | `POST /api/v1/auth/logout` · `logout-all` |
| US-01.07 | Recuperação de senha | 5 | `POST /api/v1/auth/forgot-password` · `reset-password` |
| US-01.08 | Consultar perfil | 2 | `GET /api/v1/users/me` |
| US-01.09 | Atualizar perfil | 2 | `PATCH /api/v1/users/me` |
| US-01.10 | Alteração de senha autenticada | 3 | `PATCH /api/v1/users/me/password` |
| US-01.11 | Gestão de endereços | 3 | `GET POST /api/v1/users/me/addresses` · `PUT DELETE PATCH /{id}` |
| US-01.12 | Criação de admin | 3 | `POST /api/v1/admin/users` |
| US-01.13 | Ativar/desativar usuário | 2 | `PATCH /api/v1/admin/users/{id}/status` |
| US-01.14 | CRUD completo de usuários (admin) | 5 | `GET POST PUT PATCH /api/v1/admin/users` |

**14/14 stories · 50 pontos · 128 testes passando**

---

## EP-02 · Catalog — Status das stories

| Story | Descrição | Pontos | Endpoint(s) |
|---|---|---|---|
| US-02.01 | Configuração do MinIO | 3 | — (infra) |
| US-02.02 | Gestão de categorias (admin) | 3 | `GET POST /api/v1/admin/categories` · `GET PUT DELETE /{id}` · `GET /api/v1/categories` |
| US-02.03 | Cadastro de produto (admin) | 5 | `POST /api/v1/admin/products` |
| US-02.04 | Upload de imagens do produto (admin) | 5 | `POST /{id}/images` · `PATCH /{id}/images/{imageId}/principal` · `PATCH /{id}/images/order` · `DELETE /{id}/images/{imageId}` |
| US-02.05 | Atualização de produto (admin) | 3 | `PUT /api/v1/admin/products/{id}` · `PATCH /{id}` · `PATCH /{id}/status` |
| US-02.06 | Gestão de variantes (admin) | 5 | `GET POST /api/v1/admin/products/{id}/variants` · `PUT /{variantId}` · `PATCH /{variantId}/status` · `PATCH /{variantId}/stock` |
| US-02.07 | Listagem de produtos (loja) | 3 | `GET /api/v1/products` |
| US-02.08 | Detalhe do produto (loja) | 2 | `GET /api/v1/products/{slug}` |
| US-02.09 | Listagem de produtos (admin) | 2 | `GET /api/v1/admin/products` · `GET /api/v1/admin/products/{id}` |

**9/9 stories · 31 pontos · 230 testes passando**

> **Nota:** a query de listagem admin (`GET /api/v1/admin/products`) retorna `principalImageUrl` (nullable) com a URL pública da imagem principal de cada produto, obtida via subquery no PostgreSQL.

---

## EP-03 · Cart — Status das stories

| Story | Descrição | Pontos | Endpoint(s) |
|---|---|---|---|
| US-03.01 | Adicionar item ao carrinho | 5 | `POST /api/v1/cart/items` |

**1/? stories · 5 pontos implementados · 10 testes passando**

> **Nota:** o carrinho é criado automaticamente no primeiro item adicionado. Preço é snapshotado no momento da adição. Se a variante já existe no carrinho, a quantidade é somada (merge). Qualquer opção de frete selecionada é removida ao adicionar novo item.

---

## EP-04 · Order — Status das stories

| Story | Descrição | Pontos | Endpoint(s) |
|---|---|---|---|
| US-04.05 | Listagem de pedidos (admin) | 3 | `GET /api/v1/admin/orders` |
| US-04.06 | Detalhe do pedido (admin) | 2 | `GET /api/v1/admin/orders/{id}` |
| US-04.07 | Atualização de status do pedido (admin) | 3 | `PATCH /api/v1/admin/orders/{id}/status` |
| US-04.08 | Código de rastreamento (admin) | 3 | `PATCH /api/v1/admin/orders/{id}/tracking` |
| US-04.09 | Resumo de pedidos por status (admin) | 2 | `GET /api/v1/admin/orders/summary` |

**5/? stories · 13 pontos implementados**

---

## Referência completa de endpoints

### Auth — público

| Método | Path | Body | Resposta |
|---|---|---|---|
| POST | `/api/v1/auth/register` | `fullName, email, password, passwordConfirmation` | 201 |
| GET | `/api/v1/auth/verify-email` | `?token=<uuid>` | 200 |
| POST | `/api/v1/auth/resend-verification` | `email` | 204 |
| POST | `/api/v1/auth/login` | `email, password` | 200 + cookies |
| POST | `/api/v1/auth/refresh` | cookie `refresh_token` | 200 + cookies |
| POST | `/api/v1/auth/logout` | cookie `refresh_token` | 204 |
| POST | `/api/v1/auth/logout-all` | cookie `refresh_token` | 204 |
| POST | `/api/v1/auth/forgot-password` | `email` | 204 |
| POST | `/api/v1/auth/reset-password` | `token, newPassword, passwordConfirmation` | 204 |

### Users — requer `access_token`

| Método | Path | Body | Resposta |
|---|---|---|---|
| GET | `/api/v1/users/me` | — | 200 |
| PATCH | `/api/v1/users/me` | `fullName` | 200 |
| PATCH | `/api/v1/users/me/password` | `currentPassword, newPassword, passwordConfirmation` | 204 + cookies |
| GET | `/api/v1/users/me/addresses` | — | 200 lista |
| POST | `/api/v1/users/me/addresses` | endereço completo | 201 |
| PUT | `/api/v1/users/me/addresses/{id}` | endereço completo | 200 |
| DELETE | `/api/v1/users/me/addresses/{id}` | — | 204 |
| PATCH | `/api/v1/users/me/addresses/{id}/default` | — | 200 |

### Admin — requer `ROLE_ADMIN`

| Método | Path | Body / Params | Resposta |
|---|---|---|---|
| GET | `/api/v1/admin/users` | `?page, size, nome, email, status, role` | 200 paginado |
| GET | `/api/v1/admin/users/{id}` | — | 200 |
| POST | `/api/v1/admin/users` | `fullName, email, role` | 201 |
| PUT | `/api/v1/admin/users/{id}` | `fullName, email, role` | 200 |
| PATCH | `/api/v1/admin/users/{id}` | campos opcionais: `fullName, email, role` | 200 |
| PATCH | `/api/v1/admin/users/{id}/status` | `status (ACTIVE\|INACTIVE)` | 200 |

### Products — público

| Método | Path | Params | Resposta |
|---|---|---|---|
| GET | `/api/v1/products` | `nome?, categoriaId?, precoMin?, precoMax?, destaque?, page, size, sort` | 200 paginado |
| GET | `/api/v1/products/{slug}` | — | 200 detalhe / 404 |

> Retorna apenas produtos `ACTIVE` com ao menos 1 variante ativa com estoque > 0.
> Ordenações: `mais_recente` (padrão), `nome_asc`, `nome_desc`, `preco_asc`, `preco_desc`, `destaque`.
> Detalhe: produto `INACTIVE` retorna 404. Variantes com `estoque = 0` retornadas com `disponivel: false`.

### Categories — público

| Método | Path | Params | Resposta |
|---|---|---|---|
| GET | `/api/v1/categories` | — | 200 lista |

### Admin Categories — requer `ROLE_ADMIN`

| Método | Path | Body / Params | Resposta |
|---|---|---|---|
| GET | `/api/v1/admin/categories` | `?page, size, name, active, sort, direction` | 200 paginado |
| GET | `/api/v1/admin/categories/{id}` | — | 200 |
| POST | `/api/v1/admin/categories` | `name, description` | 201 |
| PUT | `/api/v1/admin/categories/{id}` | `name, description, active` | 200 |
| DELETE | `/api/v1/admin/categories/{id}` | — | 204 |

### Admin Products — requer `ROLE_ADMIN`

| Método | Path | Body / Params | Resposta |
|---|---|---|---|
| POST | `/api/v1/admin/products` | `name, description, categoryId, featured?, variants[{attributeName, attributeValue, price, stock, sku?}]` | 201 |
| PUT | `/api/v1/admin/products/{id}` | `name, description, categoryId, featured?` | 200 |
| PATCH | `/api/v1/admin/products/{id}` | campos opcionais: `name, description, categoryId, featured` | 200 |
| PATCH | `/api/v1/admin/products/{id}/status` | `status (ACTIVE\|INACTIVE)` | 200 |
| POST | `/api/v1/admin/products/{id}/images` | `multipart/form-data campo: arquivo` (JPEG/PNG/WebP, máx 5MB) | 201 |
| PATCH | `/api/v1/admin/products/{id}/images/{imageId}/principal` | — | 200 |
| PATCH | `/api/v1/admin/products/{id}/images/order` | `items[{imageId, order}]` | 200 lista |
| DELETE | `/api/v1/admin/products/{id}/images/{imageId}` | — | 204 |
| GET | `/api/v1/admin/products/{id}/variants` | — | 200 lista |
| POST | `/api/v1/admin/products/{id}/variants` | `attributeName, attributeValue, price, stock, sku?` | 201 |
| PUT | `/api/v1/admin/products/{id}/variants/{variantId}` | `attributeName, attributeValue, price, stock, sku` | 200 |
| PATCH | `/api/v1/admin/products/{id}/variants/{variantId}/status` | `ativo (true\|false)` | 200 |
| PATCH | `/api/v1/admin/products/{id}/variants/{variantId}/stock` | `tipo (ENTRADA\|SAIDA), quantidade, motivo` | 200 |
| GET | `/api/v1/admin/products` | `nome?, categoriaId?, status?, destaque?, page, size, sort` | 200 paginado |
| GET | `/api/v1/admin/products/{id}` | — | 200 detalhe / 404 |

### Cart — requer autenticação

| Método | Path | Body | Resposta |
|---|---|---|---|
| POST | `/api/v1/cart/items` | `variantId, quantity` | 200 carrinho completo |

> Requer `access_token` (qualquer role). Carrinho criado automaticamente no primeiro item.
> Validações 422: variante ou produto inativo, variante sem dimensões (`weight/width/height/depth`), estoque insuficiente (mensagem inclui quantidade disponível).
> Resposta inclui: `id`, `userId`, `items[]` (com `productName`, `attributeName`, `attributeValue`, `sku`, `principalImageUrl`, `priceSnapshot`, `subtotal`, `availableStock`), `subtotal` total e `itemCount`.

### Admin Orders — requer `ROLE_ADMIN`

| Método | Path | Params | Resposta |
|---|---|---|---|
| GET | `/api/v1/admin/orders` | `orderNumber?, customerName?, customerEmail?, status?, startDate?, endDate?, minValue?, maxValue?, page, size, sort` | 200 paginado |
| GET | `/api/v1/admin/orders/summary` | `startDate?, endDate?` (ISO 8601, default: hoje) | 200 |
| GET | `/api/v1/admin/orders/{id}` | — | 200 detalhe / 404 |
| PATCH | `/api/v1/admin/orders/{id}/status` | `status (required), note (optional)` | 200 detalhe / 404 / 422 |
| PATCH | `/api/v1/admin/orders/{id}/tracking` | `trackingCode, carrier, carrierName?, customTrackingUrl?` | 200 / 404 / 422 |

> Ordenações: `latest` (padrão), `oldest`, `value_asc`, `value_desc`, `status`.
> Status: `PENDING_PAYMENT`, `PAID`, `PREPARING`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
> Datas no formato ISO 8601 (ex: `2026-01-01T00:00:00Z`).
> Detalhe retorna: `customer`, `items[]` (com `sku` e `principalImage`), `deliveryAddress` (nullable), `payment` (nullable), `statusHistory[]` ordenado cronologicamente.
> Transições válidas: `PENDING_PAYMENT→PAID`, `PAID→PREPARING`, `PREPARING→SHIPPED`, `SHIPPED→DELIVERED`, `PENDING_PAYMENT→CANCELLED`, `PAID→CANCELLED`. Transição inválida retorna 422.
> Cancelamento: estoque das variantes restaurado automaticamente na mesma transação. Publica `order.status-changed` e `order.cancelled` no Kafka.
> Tracking: pedido deve estar em `SHIPPED` (422 caso contrário). Carriers: `CORREIOS`, `JADLOG`, `LOGGI`, `TOTAL_EXPRESS`, `AZUL_CARGO`, `OUTRO`. Para `OUTRO`, `customTrackingUrl` é obrigatória. Publica `order.tracking-updated` → consumer no módulo notification dispara e-mail ao cliente.
> Summary: retorna `period` com as datas efetivas usadas, `totals` com `totalOrders`, `totalRevenue` e `averageTicket`, e `byStatus[]` com `status`, `count`, `percentage` (1 decimal, soma 100%) e `totalValue`. Sem parâmetros usa início e fim do dia atual (UTC).

---

## Testes

```bash
# Todos os testes
./mvnw test

# Com relatório de cobertura
./mvnw verify
```

Os testes usam **H2 em memória** com Flyway desativado. Kafka, Mail e OAuth2 são excluídos via `autoconfigure.exclude` no `src/test/resources/application.yaml`.

---

## Observabilidade

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/modulith
```

---

## Onboarding para novos devs

### Setup em 5 passos

```bash
# 1. Clone e entre no projeto
git clone <repo> && cd miria-geek-store

# 2. Suba a infraestrutura
docker compose up -d

# 3. Configure o .env
cd miria-geek-api
cp .env.example .env   # edite com seus valores
# Gere o JWT_SECRET:
echo "JWT_SECRET=$(openssl rand -base64 32)" >> .env

# 4. Rode a API
./mvnw spring-boot:run

# 5. Veja o log — as credenciais do admin seed aparecem na inicialização
```

### Fluxo de teste básico no Postman

1. `POST /register` → cria um cliente
2. Copie o `verificationToken` do **Kafka UI** (`http://localhost:8090`) no tópico `identity.user-registered`
3. `GET /verify-email?token=<uuid>` → ativa a conta
4. `POST /login` → autentica (cookies setados automaticamente pelo Postman)
5. `GET /users/me` → confirma o perfil

### Como adicionar uma nova story

Siga o padrão das stories existentes:

```
1. domain/model/         → value object ou agregado (Java puro)
2. domain/exception/     → exceção específica do domínio
3. domain/event/         → evento publicado no Kafka
4. domain/port/in/       → interface UseCase + Command + Result
5. domain/port/out/      → interface de repositório ou serviço externo
6. application/usecase/  → implementação @Service @Transactional
7. infrastructure/
   adapter/in/web/       → @RestController + DTOs Request/Response
   adapter/out/          → implementação das portas (JPA, Kafka, Mail...)
8. GlobalExceptionHandler → mapeamento exception → HTTP status
9. Flyway migration       → V{n}__descricao.sql
10. Teste unitário        → UseCaseTest.java com Mockito
11. Postman collection    → doc/postman/miria-geek-api.postman_collection.json
```
