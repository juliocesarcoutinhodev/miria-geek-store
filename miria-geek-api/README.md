# miria-geek-api

Backend da **Miria Geek Store** — API REST construída com Spring Boot 4, seguindo Arquitetura Hexagonal (Ports & Adapters) e Domain-Driven Design (DDD).

---

## Stack

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 4.0 + Spring Framework 7 |
| Persistência | Spring Data JPA + PostgreSQL + Flyway |
| Segurança | Spring Security + OAuth2 Resource Server (JWT) |
| Mensageria | Apache Kafka (Spring Kafka) |
| Observabilidade | Spring Actuator + Micrometer + Prometheus |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Módulos | Spring Modulith |
| Mapeamento | MapStruct |
| Utilitários | Lombok, dotenv-java |
| Testes | JUnit 5, Mockito, Spring Boot Test |

---

## Arquitetura

O projeto segue **Hexagonal Architecture** (Ports & Adapters) combinada com **DDD**. Cada módulo de negócio é independente e segue a mesma estrutura interna.

```
br.com.miriageekstore/
│
├── shared/                         ← Shared Kernel (tipos e exceções comuns)
│   ├── domain/
│   │   ├── model/                  ← Value Objects, base de entidades
│   │   └── exception/              ← Exceções de domínio base
│   └── infrastructure/
│       └── config/                 ← Configurações globais (CORS, Security, etc.)
│
├── identity/                       ← Módulo: Usuários e autenticação
├── catalog/                        ← Módulo: Produtos e categorias
├── cart/                           ← Módulo: Carrinho de compras
├── order/                          ← Módulo: Pedidos
├── payment/                        ← Módulo: Pagamentos
├── notification/                   ← Módulo: Notificações (e-mail, push)
└── backoffice/                     ← Módulo: Painel administrativo
```

### Estrutura interna de cada módulo

```
{modulo}/
├── domain/                         ← Núcleo — sem dependências de framework
│   ├── model/                      ← Entidades, Aggregates, Value Objects
│   ├── service/                    ← Domain Services
│   └── port/
│       ├── in/                     ← Input Ports (interfaces de casos de uso)
│       └── out/                    ← Output Ports (interfaces de repositórios/gateways)
│
├── application/
│   └── usecase/                    ← Implementações dos casos de uso (orquestra o domínio)
│
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   └── web/                ← Controllers REST (entrada via HTTP)
    │   └── out/
    │       └── persistence/        ← Repositórios JPA, entidades de banco
    └── config/                     ← Beans Spring específicos do módulo
```

### Regra de dependência

```
infrastructure → application → domain
```

O domínio não conhece nenhuma camada externa. As dependências sempre apontam para dentro.

---

## Pré-requisitos

- Java 25+
- Maven 3.9+
- Docker (para PostgreSQL, Kafka e Keycloak em dev)

---

## Setup

### 1. Clone e configure o ambiente

```bash
git clone <repo-url>
cd miria-geek-api
cp .env.example .env
# Edite o .env com suas credenciais locais
```

### 2. Suba a infra local com Docker

```bash
# Na raiz do monorepo (docker-compose.yml virá em próxima story)
docker compose up -d postgres kafka keycloak mailpit
```

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

Ou em modo debug:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

---

## Endpoints úteis

| Recurso | URL |
|---|---|
| Health check | `GET /actuator/health` |
| Métricas Prometheus | `GET /actuator/metrics` |
| Módulos (Modulith) | `GET /actuator/modulith` |
| Swagger UI | `GET /swagger-ui` |
| OpenAPI JSON | `GET /api-docs` |

---

## Variáveis de ambiente

Todas as credenciais são lidas via variáveis de ambiente (nunca hardcoded). Veja `.env.example` para a lista completa.

Em desenvolvimento, crie um arquivo `.env` na raiz do projeto — ele é carregado automaticamente pela biblioteca `dotenv-java` ao iniciar a aplicação.

Em produção, injete as variáveis via secrets do seu orquestrador (Kubernetes Secrets, Railway, etc.).

---

## Testes

```bash
# Todos os testes
./mvnw test

# Verificação de módulos (Spring Modulith)
./mvnw test -Dtest=ModularityTests
```

---

## Perfis

| Perfil | Descrição |
|---|---|
| `dev` | Logs detalhados, Swagger habilitado, health com detalhes |
| `prod` | Logs reduzidos, Swagger desabilitado, health protegido |

Para trocar o perfil, defina `SPRING_PROFILES_ACTIVE=prod` no `.env`.
