# Music API (Spring Boot 4.0.2 + Java 21)

API REST para disponibilizar dados de **artistas e albuns** com:
- N:N Artista-Album
- Seguranca (CORS por dominio + JWT com refresh)
- Paginacao e filtros
- Upload de capas (MinIO / S3)
- Links pre-assinados (30 min)
- Flyway (criacao + carga inicial)
- OpenAPI/Swagger
- Health checks (liveness/readiness)
- WebSocket (notificacao a cada novo album)
- Rate limit: **10 requisicoes/minuto por usuario**

---

## Sumario
- [Como executar (Docker)](#1-como-executar-docker)
- [Como executar (local)](#2-como-executar-local)
- [Variaveis de ambiente](#3-variaveis-de-ambiente)
- [Auth (JWT 5 min + refresh)](#4-auth-jwt-5-min--refresh)
- [Endpoints principais](#5-endpoints-principais)
- [WebSocket](#6-websocket)
- [Rate limit](#7-rate-limit)
- [Testes](#8-testes)
- [Arquitetura e padroes](#9-arquitetura-e-padroes)
- [Evolucao do projeto (etapas)](#10-evolucao-do-projeto-etapas)
- [Historico de commits (sugestao)](#11-historico-de-commits-sugestao)

---

## 1) Como executar (Docker)

Pre-requisitos:
- Docker + Docker Compose

Subir tudo:
```bash
docker compose up -d --build
```

URLs:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health: http://localhost:8080/actuator/health
- Liveness: http://localhost:8080/actuator/health/liveness
- Readiness: http://localhost:8080/actuator/health/readiness
- MinIO Console: http://localhost:9001 (user/pass: minioadmin/minioadmin)

---

## 2) Como executar (local)

Pre-requisitos:
- Java 21
- Maven 3.9+
- Postgres (ou via Docker) e MinIO (ou via Docker)

Subir somente dependencias:
```bash
docker compose up -d db minio
```

Rodar a API:
```bash
mvn spring-boot:run
```

---

## 3) Variaveis de ambiente

Principais:
- `DB_URL`, `DB_USER`, `DB_PASS`
- `JWT_SECRET`, `JWT_ACCESS_TTL_MIN`, `JWT_REFRESH_TTL_DAYS`
- `CORS_ALLOWED_ORIGINS`
- `MINIO_URL`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`
- `DEMO_USER`, `DEMO_PASS`

---

## 4) Auth (JWT 5 min + refresh)

Usuario de demonstracao (para avaliacao):
- `admin / admin123` (configuravel por env `DEMO_USER` e `DEMO_PASS`)

Login:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Refresh:
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

---

## 5) Endpoints principais

### Artistas
- POST `/api/v1/artists`
- PUT `/api/v1/artists/{id}`
- GET `/api/v1/artists?nome=serj&sort=nome,asc`

### Albuns (paginacao + filtros)
- POST `/api/v1/albums`
- PUT `/api/v1/albums/{id}`
- GET `/api/v1/albums?page=0&size=10&sort=titulo,asc`
- GET `/api/v1/albums?artistName=mike`
- GET `/api/v1/albums?artistType=CANTOR` (ou `BANDA`)

### Upload de capas (1 ou mais)
- POST `/api/v1/albums/{id}/covers` (multipart `files`)
- GET `/api/v1/albums/{id}/covers` -> retorna **URLs pre-assinadas** (expiram 30 min)

Exemplo upload:
```bash
curl -X POST "http://localhost:8080/api/v1/albums/1/covers" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -F "files=@./capa1.jpg" \
  -F "files=@./capa2.png"
```

### Regionais (sincronizacao)
- POST `/api/v1/regionais/sync`
- GET `/api/v1/regionais?ativo=true`

Regras de sync:
1. Novo no endpoint -> inserir
2. Ausente no endpoint -> inativar
3. Atributo alterado -> inativar antigo e criar novo registro

---

## 6) WebSocket

Endpoint STOMP:
- `ws://localhost:8080/ws`

Topic:
- `/topic/albums`

Payload:
```json
{ "albumId": 123, "titulo": "Harakiri" }
```

---

## 7) Rate limit

- 10 requisicoes por minuto por usuario (username do JWT)
- Ao exceder: HTTP `429`

---

## 8) Testes

Rodar unit tests:
```bash
mvn test
```

---

## 9) Arquitetura e padroes

Camadas principais:
- `controller`: portas HTTP e validacao de entrada
- `service`: regras de negocio
- `repository`: acesso a dados
- `domain`: entidades e enums

Decisoes estruturais:
- N:N com tabela `artist_album`
- Upload no MinIO com `object_key` e metadata no banco (`album_cover`)
- URLs pre-assinadas para acesso temporario (30 min)
- Rate limit em memoria (simples). Para cluster real, trocar por Redis/Bucket4j distributed.
- Refresh token salvo no banco com rotacao simples (deleta tokens antigos do usuario)

---

## 10) Evolucao do projeto (etapas)

1. **Bootstrap do projeto**
   - Definicao da stack (Spring Boot 4 + Java 21)
   - Estrutura inicial com Maven e perfis de ambiente

2. **Modelagem do dominio**
   - Entidades `Artist`, `Album` e relacao N:N
   - Enums e validacoes iniciais

3. **Persistencia e migracoes**
   - Repositorios e consultas basicas
   - Flyway para criacao do schema e carga inicial

4. **Camada de servicos**
   - Regras de negocio para CRUD
   - Filtros e paginacao

5. **API e documentacao**
   - Controllers REST
   - OpenAPI/Swagger para inspecao dos endpoints

6. **Seguranca**
   - JWT com access/refresh
   - CORS por dominio
   - Rate limit basico por usuario

7. **Armazenamento de capas**
   - Upload no MinIO/S3
   - Geração de URLs pre-assinadas

8. **Observabilidade e resiliencia**
   - Health checks (liveness/readiness)
   - Tratamento de erros consistente

9. **Notificacoes em tempo real**
   - WebSocket STOMP
   - Evento de novo album

---

## 11) Historico de commits (sugestao)

> Modelo recomendado: **Conventional Commits**

1. `chore: bootstrap do projeto com Spring Boot 4 e Java 21`
   - cria estrutura Maven
   - adiciona configuracoes iniciais

2. `feat(domain): modela entidades Artist e Album com relacao N:N`
   - adiciona enums e validacoes

3. `feat(db): cria migracoes Flyway e carga inicial`
   - adiciona schema e seeds

4. `feat(artists): CRUD de artistas com filtros e paginacao`
   - endpoints e servicos

5. `feat(albums): CRUD de albuns com filtros e paginacao`
   - endpoints e servicos

6. `feat(auth): JWT access/refresh e CORS por dominio`
   - rota de login e refresh

7. `feat(upload): upload de capas no MinIO e URLs pre-assinadas`
   - endpoints multipart e geracao de links temporarios

8. `feat(ws): notificacao via WebSocket a cada novo album`
   - topicos STOMP

9. `feat(rate-limit): limita requisicoes por usuario`
   - resposta 429 ao exceder

10. `chore(obs): health checks e ajustes de observabilidade`
    - liveness e readiness

11. `docs: documenta uso, arquitetura e fluxo evolutivo`
    - atualiza README com instrucoes claras