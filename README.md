# 🚀 SyncSpace API — Enterprise Room Reservation System

> **Back-end de alta disponibilidade para reservas corporativas**, construído com Java 21, Spring Boot 3.2 e MySQL.  
> Arquitetura limpa, segurança JWT e controle de concorrência com Bloqueio Otimista (Optimistic Locking) para evitar reservas duplicadas.

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-24.0.5-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-brightgreen?style=for-the-badge&logo=swagger)](https://swagger.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 🧠 Por que este projeto é diferenciado?

- ✅ **Concurrency Control de verdade** — uso de `@Version` no JPA para evitar sobrescrições em ambientes de alta concorrência (Double Booking).
- 🔐 **Autenticação Stateless com JWT** — tokens assinados, renováveis, integrados ao Spring Security sem estado.
- 📐 **Domain-Driven Design (Lite)** — separação rígida entre Web, Service e Repository, com DTOs imutáveis (Records).
- 🐳 **Infrastructure as Code** — Docker Compose orquestrando app + banco de dados.
- 📖 **Documentação interativa** — Swagger UI acessível em `/swagger-ui.html`.
- 🧪 **Testes automatizados** — JUnit 5 + Mockito para services e validações de bean.

---

## 🏗️ Arquitetura da Aplicação

```
room-reservation-api/
├── src/main/java/com/syncspace/api/
│   ├── config/                 # Segurança (SecurityConfig, SecurityFilter)
│   ├── controller/             # REST controllers (Auth, Sala, Usuario)
│   ├── dto/                    # Objetos de transferência (records e DTOs)
│   ├── exception/              # Handler global de exceções
│   ├── model/                  # Entidades JPA (Usuario, Sala)
│   ├── repository/             # Interfaces JPA com métodos customizados
│   └── service/                # Lógica de negócio pura (TokenService, UsuarioService, etc.)
├── src/main/resources/
│   ├── application.properties  # Configuração centralizada
│   └── static/                 # (opcional) assets estáticos
├── .env                        # Variáveis de ambiente sensíveis
├── docker-compose.yml          # Orquestração de containers
├── Dockerfile                  # Build da imagem da aplicação
└── pom.xml                     # Dependências gerenciadas pelo Maven
```

---

## 🛠️ Regras de Negócio Implementadas

| Regra | Implementação |
|-------|---------------|
| **Prevenção de reserva dupla** | Campo `@Version` na entidade `Sala` gera exceção `ObjectOptimisticLockingFailureException` em conflitos. |
| **Validação de horário** | Reservas não podem ser feitas para datas passadas (validação via Bean Validation). |
| **Segurança escalonada** | Apenas admin pode excluir/atualizar salas; endpoints de autenticação abertos. |
| **Isolamento de DTOs** | Records Java 17+ usados para entrada/saída, nunca expõem a entidade diretamente. |

---

## 🔐 Segurança & JWT

- Filtro `SecurityFilter` intercepta todas as requisições.
- Token extraído do header `Authorization: Bearer <token>`.
- Senhas criptografadas com **BCrypt** (cost = 10).
- Segregação de endpoints na configuração do Spring Security:
  - `/usuarios`, `/auth/login` → público (POST)
  - `/swagger-ui/**`, `/v3/api-docs/**` → público
  - demais endpoints → autenticado

```java
// Exemplo do fluxo de autenticação (AuthController + TokenService)
var auth = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(dados.email(), dados.senha())
);
var token = tokenService.gerarToken((Usuario) auth.getPrincipal());
return ResponseEntity.ok(new DadosToken(token));
```

---

## 🚦 Endpoints da API

> Documentação completa e testável via Swagger UI em `http://localhost:8080/swagger-ui.html`

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/auth/login` | Gera token JWT | Pública |
| `GET` | `/salas` | Lista todas as salas | Autenticado |
| `GET` | `/salas/{id}` | Detalhes de uma sala | Autenticado |
| `POST` | `/salas` | Cria nova sala | Autenticado |
| `PUT` | `/salas/{id}` | Atualiza sala (com controle de versão) | Autenticado |
| `DELETE` | `/salas/{id}` | Remove sala | Autenticado (Admin) |
| `GET` | `/usuarios` | Lista usuários | Autenticado |
| `GET` | `/usuarios/{id}` | Detalhes de um usuário | Autenticado |
| `POST` | `/usuarios` | Cadastra novo usuário | Pública |
| `PUT` | `/usuarios/{id}` | Atualiza próprio cadastro | Autenticado |
| `DELETE` | `/usuarios/{id}` | Remove usuário | Autenticado |

---

## ⚡ Tecnologias e Dependências

| Categoria | Tecnologia |
|-----------|------------|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de Dados | MySQL 8.0 |
| Segurança | Spring Security + Auth0 JWT |
| Validação | Bean Validation (Jakarta) |
| Documentação | SpringDoc OpenAPI 2 + Swagger UI |
| Testes | JUnit 5, Mockito, Spring Test |
| Containerização | Docker + Docker Compose |
| Build | Maven Wrapper (mvnw) |
| Integração Contínua | GitHub Actions (workflow de CI) |

---

## 🚀 Como executar o projeto

### Pré-requisitos

- Java 21 JDK (ou Docker)
- Maven (opcional, o projeto já inclui Maven Wrapper)
- Docker e Docker Compose (recomendado)

### Via Docker (modo rápido)

```bash
# Clone o repositório
git clone https://github.com/kalebzaki4/room-reservation-api.git
cd room-reservation-api

# Suba o ecossistema (app + MySQL)
docker-compose up -d
```

A aplicação estará disponível em `http://localhost:8080` e o Swagger em `http://localhost:8080/swagger-ui.html`.

### Via linha de comando (dev local)

```bash
git clone https://github.com/kalebzaki4/room-reservation-api.git
cd room-reservation-api

# Crie um banco MySQL chamado 'syncspace' (ou ajuste application.properties)
# O banco roda na porta 3306 com user/password root/root

./mvnw spring-boot:run
```

### Usando a imagem Docker publicada

```bash
docker pull kalebzaki/room-reservation-api:v1
docker run -p 8080:8080 kalebzaki/room-reservation-api:v1
```

---

## 🧪 Testes e Qualidade

O projeto inclui uma suíte de testes unitários e de integração.

```bash
./mvnw test
```

**Cobertura dos testes:**
- Service layer (TokenService, UsuarioService, SalaService) com Mockito.
- Validação de DTOs (Bean Validation) – são testados cenários de campos obrigatórios, formatos inválidos, etc.
- Testes de repositório (caso haja data layer customizado).

**Foco em edge cases:**
- Reserva duplicada (conflito de versão).
- Acesso a endpoints sem token (HTTP 403).
- Criação de usuário com e-mail duplicado (exceção `DataIntegrityViolationException` capturada).

---

## 📦 Variáveis de Ambiente

Configure no arquivo `.env` (já incluso no repositório) ou diretamente no ambiente:

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `JWT_SECRET` | Chave HMAC para assinatura de tokens | (hash de 256 bits) |
| `MYSQL_ROOT_PASSWORD` | Senha do root do MySQL | `root` |
| `SPRING_DATASOURCE_URL` | URL do banco de dados | `jdbc:mysql://db:3306/syncspace...` |

Essas variáveis são injetadas no Docker Compose e no `application.properties` via `${}`.

---

## 📋 Roadmap (próximas features)

- [ ] Renovação de token (refresh token)
- [ ] Recuperação de senha por e-mail
- [ ] Monitoramento com Prometheus + Grafana
- [ ] Rate limiting para proteção contra brute-force
- [ ] Frontend React para consumo da API

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Siga os passos:

1. Fork o projeto
2. Crie uma branch: `git checkout -b feat/minha-feature`
3. Commit suas mudanças: `git commit -m 'feat: adiciona funcionalidade X'`
4. Push para a branch: `git push origin feat/minha-feature`
5. Abra um Pull Request

---

## 👨‍💻 Autor

**Kaleb Zaki**  
Desenvolvedor Back-end Java | Foco em Sistemas de Alta Disponibilidade  
[GitHub](https://github.com/kalebzaki4) | [LinkedIn](https://www.linkedin.com/in/kaleb-z-santos/)

---

## 📄 Licença

Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

⭐ Se este projeto te ajudou, considere deixar uma estrela!
