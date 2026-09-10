<div align="center">

# 🏢 SyncSpace

### API corporativa de reserva de salas — segura, concorrente e pronta para produção

*Desenvolvida do zero para simular os desafios reais de um sistema back-end escalável: autenticação, concorrência, testes automatizados e deploy containerizado.*

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/Auth-JWT-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)](LICENSE)

[Sobre](#-sobre-o-projeto) •
[Funcionalidades](#-funcionalidades) •
[Arquitetura](#-arquitetura) •
[Como rodar](#-como-rodar-o-projeto) •
[Endpoints](#-endpoints-principais) •
[Testes](#-testes) •
[Roadmap](#-roadmap)

</div>

---

## 📌 Sobre o projeto

**SyncSpace** é uma API REST para **reserva de salas corporativas** (escritórios, coworkings, clínicas, salas de reunião), construída com **Java 21** e **Spring Boot**, pensada para resolver um problema clássico e nada trivial de sistemas distribuídos: **evitar que duas pessoas reservem a mesma sala no mesmo horário**, mesmo sob acesso concorrente.

O projeto nasceu como um exercício de aprofundamento em back-end sério — não é um CRUD de tutorial. Ele simula cenários reais de produção:

- 🔐 **Autenticação e autorização** via JWT, com controle de acesso por papel de usuário.
- 🔁 **Controle de concorrência otimista** para impedir conflitos de reserva (dupla marcação da mesma sala/horário).
- 🧪 **Testes automatizados** de controllers e regras de negócio (JUnit 5 + Mockito).
- 🐳 **Containerização** com Docker, pronta para subir o ambiente completo com um único comando.
- 📄 **Documentação interativa** da API via Swagger/OpenAPI.
- ⚙️ **CI** com GitHub Actions, rodando a suíte de testes a cada push/PR.

> O objetivo é ser um projeto que reflita, na prática, como uma API corporativa real deveria ser construída — com segurança, consistência de dados e cobertura de testes como prioridade, não como extra.

---

## ✨ Funcionalidades

- **Cadastro e autenticação de usuários** com geração e validação de token JWT.
- **CRUD completo de salas** (nome, descrição, capacidade e disponibilidade).
- **Criação e gerenciamento de reservas**, com validação de:
  - conflito de horário na mesma sala;
  - capacidade da sala;
  - regras de negócio de domínio (ex: sala inválida, reserva inválida).
- **Bloqueio de concorrência (optimistic locking)** para garantir que reservas simultâneas não corrompam o estado do sistema.
- **Tratamento de exceções centralizado**, com respostas de erro padronizadas e claras para o cliente da API.
- **Camada de segurança** com Spring Security protegendo rotas sensíveis.

---

## 🏗️ Arquitetura

O projeto segue uma separação clara de responsabilidades, alinhada aos princípios de **Clean Architecture / camadas em Spring**:

```
src/main/java/com/br/syncspace
├── api/            # Controllers REST e DTOs de entrada/saída
├── domain/         # Entidades, regras e exceções de domínio
├── security/        # Configuração de autenticação/autorização (JWT, filtros)
├── repository/       # Camada de persistência (Spring Data JPA)
└── service/          # Regras de negócio e orquestração dos casos de uso
```

**Decisões técnicas de destaque:**

| Decisão | Por quê |
|---|---|
| **Optimistic Locking** nas reservas | Evita duplo agendamento sem travar o banco com locks pessimistas, mantendo a API responsiva sob carga. |
| **DTOs dedicados por operação** (ex: `SalaRequestDTO`) | Desacopla o contrato da API do modelo de domínio, evitando overposting e facilitando validação. |
| **Exceções de domínio customizadas** (ex: `SalaInvalidaException`) | Mensagens de erro semânticas em vez de stack traces genéricos. |
| **JWT stateless** | API sem sessão em servidor, pronta para escalar horizontalmente. |

---

## 🛠️ Tech Stack

| Categoria | Tecnologias |
|---|---|
| **Linguagem / Runtime** | Java 21 |
| **Framework** | Spring Boot, Spring Web, Spring Security, Spring Data JPA |
| **Banco de dados** | MySQL + Hibernate/JPA |
| **Autenticação** | JWT (java-jwt) |
| **Build** | Maven (com Maven Wrapper) |
| **Testes** | JUnit 5, Mockito, Spring Security Test |
| **Infraestrutura** | Docker |
| **Documentação da API** | Swagger / OpenAPI |
| **CI/CD** | GitHub Actions |

---

## 🚀 Como rodar o projeto

### Pré-requisitos

- Java 21+
- MySQL 8+ (ou Docker, para subir via container)
- Maven (opcional — o projeto já inclui o Maven Wrapper)

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/kalebzaki4/SyncSpace.git
cd SyncSpace

# 2. Configure as variáveis de ambiente do banco (application.properties / application.yml)
#    ou defina via variáveis de ambiente:
#    DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# 3. Rode com o Maven Wrapper
./mvnw spring-boot:run
```

A API sobe por padrão em `http://localhost:8080`.

Com a documentação interativa ativa, o Swagger UI fica disponível em:

```
http://localhost:8080/swagger-ui.html
```

---

## 📡 Endpoints principais

| Método | Rota | Descrição | Autenticação |
|---|---|---|---|
| `POST` | `/auth/login` | Autentica o usuário e retorna o token JWT | ❌ |
| `POST` | `/usuarios` | Cadastra um novo usuário | ❌ |
| `GET` | `/salas` | Lista todas as salas disponíveis | ✅ |
| `POST` | `/salas` | Cadastra uma nova sala | ✅ |
| `POST` | `/reservas` | Cria uma nova reserva, validando conflito de horário | ✅ |
| `GET` | `/reservas` | Lista as reservas do usuário autenticado | ✅ |

> Consulte o Swagger para o contrato completo, com todos os payloads e códigos de resposta.

---

## 🧪 Testes

O projeto conta com testes automatizados cobrindo controllers e regras de negócio críticas (como a validação de conflito de horários), usando **JUnit 5** e **Mockito**, integrados ao pipeline de **GitHub Actions** — todo push e pull request roda a suíte de testes antes do merge.

```bash
./mvnw test
```

---

## 🗺️ Roadmap

- [ ] Integração com Google (autenticação social)
- [ ] Mensageria assíncrona para notificações de reserva
- [ ] Dashboard de ocupação de salas
- [ ] Multi-tenant (suporte a múltiplas empresas/clientes na mesma instância)

---

## 👨‍💻 Autor

Feito por **Kaleb Zufanetti Santos**, estudante de programação em formação, construindo o SyncSpace como base técnica para futuramente oferecê-lo como um serviço SaaS para coworkings, clínicas e pequenos negócios.

[![GitHub](https://img.shields.io/badge/GitHub-kalebzaki4-181717?style=flat-square&logo=github)](https://github.com/kalebzaki4)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
