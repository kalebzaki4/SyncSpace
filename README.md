# 🚀 SyncSpace API — Enterprise Room Reservation System

> Backend profissional para reservas corporativas, desenvolvido com Java 21, Spring Boot 3.2 e MySQL.  
> Projetado com foco em segurança, escalabilidade, alta disponibilidade e boas práticas de engenharia de software.

A aplicação simula desafios reais de sistemas corporativos, incluindo autenticação JWT, controle de permissões, integridade transacional e prevenção de conflitos em ambientes concorrentes através de Optimistic Locking.

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-24.0.5-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/Swagger-UI-brightgreen?style=for-the-badge&logo=swagger)](https://swagger.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

# 📊 Métricas do Projeto

- 🔐 Autenticação JWT Stateless
- 🧪 Testes automatizados com JUnit + Mockito
- 🐳 Ambiente 100% containerizado com Docker
- 📖 Documentação completa com Swagger UI
- ⚡ Controle de concorrência com Optimistic Locking
- 🏗️ Arquitetura escalável em camadas
- 📦 Integração Contínua com GitHub Actions
- 🚀 Estrutura preparada para ambiente corporativo real

---

# 💼 Impacto de Negócio

O SyncSpace foi desenvolvido para simular desafios reais encontrados em sistemas corporativos onde confiabilidade, segurança e concorrência são essenciais.

Mais do que um CRUD, o projeto representa cenários comuns em empresas de médio e grande porte, como:

- prevenção de conflitos em reservas simultâneas
- autenticação segura e controle de acesso
- manutenção sustentável de código
- escalabilidade da aplicação
- facilidade de deploy e automação operacional

O objetivo foi construir uma aplicação com mentalidade de produto e não apenas de exercício técnico.

---

# 🧠 Por que este projeto é diferenciado?

## ✅ Controle real de concorrência

Uso de `@Version` no JPA com Optimistic Locking para evitar sobrescrições silenciosas e reservas duplicadas em ambientes com múltiplos usuários concorrendo pelo mesmo recurso.

---

## 🔐 Segurança Stateless com JWT

Autenticação robusta com tokens JWT assinados, integrados ao Spring Security e preparados para ambientes distribuídos e escaláveis.

---

## 📐 Arquitetura orientada à manutenção

Separação clara entre Controller, Service e Repository, com uso de DTOs imutáveis via Java Records, garantindo desacoplamento e maior previsibilidade entre camadas.

---

## 🐳 Infraestrutura containerizada

Docker Compose orquestrando aplicação + banco de dados, permitindo setup rápido, previsível e replicável em qualquer ambiente.

---

## 📖 Documentação interativa

Swagger UI disponível para testes rápidos, inspeção de endpoints e validação da API sem dependência de ferramentas externas.

---

## 🧪 Qualidade e testes automatizados

Cobertura de testes com JUnit 5 + Mockito, garantindo validação das regras de negócio e maior confiabilidade na evolução do sistema.

---

# 🏗️ Arquitetura da Aplicação

```text
syncspace-api/
├── src/main/java/com/syncspace/api/
│
├── config/                 # Segurança (SecurityConfig, SecurityFilter)
├── controller/             # REST Controllers
├── dto/                    # DTOs e Java Records
├── exception/              # Tratamento global de exceções
├── model/                  # Entidades JPA
├── repository/             # Interfaces JPA
├── service/                # Regras de negócio
│
├── src/main/resources/
│   ├── application.properties
│   └── static/
│
├── .env
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
````

---

# 🧠 Decisões Técnicas

## Por que JWT ao invés de Session?

Como a aplicação foi projetada com foco em escalabilidade e arquitetura stateless, JWT permite autenticação desacoplada do servidor, reduzindo dependência de sessão e facilitando deploy em ambientes distribuídos.

---

## Por que Optimistic Locking?

Em sistemas corporativos com múltiplos usuários concorrendo por recursos compartilhados, o bloqueio otimista evita perda silenciosa de dados e protege a integridade das operações sem comprometer performance.

---

## Por que arquitetura em camadas?

A separação entre Controller, Service e Repository melhora manutenção, testabilidade, organização do código e facilita futura evolução para microsserviços.

---

## Por que Java Records para DTOs?

Records reduzem boilerplate, aumentam imutabilidade e tornam a comunicação entre camadas mais segura, limpa e previsível.

---

# 🛠️ Regras de Negócio Implementadas

| Regra                    | Implementação                                     |
| ------------------------ | ------------------------------------------------- |
| Validação de horário     | Reservas não podem ser feitas para datas passadas |
| Segurança escalonada     | Apenas admin pode excluir ou atualizar salas      |
| Isolamento de DTOs       | Nenhuma entidade é exposta diretamente            |
| Controle de concorrência | Proteção contra conflitos simultâneos             |
| Integridade de cadastro  | E-mails duplicados são bloqueados                 |

---

# 🔐 Segurança & JWT

* Filtro `SecurityFilter` intercepta todas as requisições
* Token extraído do header `Authorization: Bearer <token>`
* Senhas criptografadas com BCrypt (`cost = 10`)
* Controle de autorização por perfil de acesso
* Endpoints públicos e privados segregados no Spring Security

### Endpoints públicos

* `POST /usuarios`
* `POST /auth/login`
* `/swagger-ui/**`
* `/v3/api-docs/**`

### Endpoints protegidos

Todos os demais endpoints exigem autenticação JWT válida.

---

# 🚦 Endpoints da API

> Documentação completa disponível em:
> `http://localhost:8080/swagger-ui.html`

| Método | Endpoint       | Descrição            | Auth    |
| ------ | -------------- | -------------------- | ------- |
| POST   | /auth/login    | Geração de token JWT | Pública |
| GET    | /salas         | Lista salas          | Sim     |
| GET    | /salas/{id}    | Detalhes da sala     | Sim     |
| POST   | /salas         | Criar sala           | Sim     |
| PUT    | /salas/{id}    | Atualizar sala       | Sim     |
| DELETE | /salas/{id}    | Remover sala         | Admin   |
| GET    | /usuarios      | Lista usuários       | Sim     |
| GET    | /usuarios/{id} | Detalhes do usuário  | Sim     |
| POST   | /usuarios      | Criar usuário        | Pública |
| PUT    | /usuarios/{id} | Atualizar usuário    | Sim     |
| DELETE | /usuarios/{id} | Excluir usuário      | Sim     |

---

# ⚡ Tecnologias Utilizadas

| Categoria       | Tecnologia                  |
| --------------- | --------------------------- |
| Linguagem       | Java 21                     |
| Framework       | Spring Boot 3.2             |
| Persistência    | Spring Data JPA + Hibernate |
| Banco de Dados  | MySQL 8                     |
| Segurança       | Spring Security + JWT       |
| Validação       | Bean Validation             |
| Documentação    | Swagger UI + OpenAPI        |
| Testes          | JUnit 5 + Mockito           |
| Containerização | Docker + Docker Compose     |
| Build           | Maven Wrapper               |
| CI/CD           | GitHub Actions              |

---

# 🚀 Como Executar o Projeto

## Pré-requisitos

* Java 21
* Docker + Docker Compose (recomendado)
* Maven (opcional)

---

## Execução com Docker

```bash
git clone https://github.com/kalebzaki4/room-reservation-api.git

cd room-reservation-api

docker-compose up -d
```

Aplicação disponível em:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

---

## Execução local (modo desenvolvimento)

```bash
git clone https://github.com/kalebzaki4/room-reservation-api.git

cd room-reservation-api

./mvnw spring-boot:run
```

Certifique-se de possuir um banco MySQL configurado.

---

# 🧪 Testes e Qualidade

O projeto possui testes automatizados focados nas principais regras de negócio.

```bash
./mvnw test
```

## Cobertura principal

* Service Layer
* TokenService
* UsuarioService
* SalaService
* Bean Validation
* Tratamento de exceções
* Validação de conflitos
* Regras de autenticação

## Edge Cases cobertos

* Reserva duplicada
* Acesso sem token
* Cadastro com e-mail duplicado
* Conflitos de atualização concorrente

---

# 📦 Variáveis de Ambiente

Configuração via `.env`

| Variável              | Descrição               |
| --------------------- | ----------------------- |
| JWT_SECRET            | Chave de assinatura JWT |
| MYSQL_ROOT_PASSWORD   | Senha root MySQL        |
| SPRING_DATASOURCE_URL | URL de conexão do banco |

---

# 📈 Roadmap

* [ ] Refresh Token
* [ ] Recuperação de senha por e-mail
* [ ] Rate Limiting
* [ ] Monitoramento com Prometheus + Grafana
* [ ] Logs centralizados
* [ ] Frontend React para consumo da API
* [ ] Deploy em produção (AWS / Railway / Render)

---

# 🤝 Contribuindo

Contribuições são bem-vindas.

```bash
git checkout -b feat/minha-feature
git commit -m "feat: nova funcionalidade"
git push origin feat/minha-feature
```

Depois, abra um Pull Request.

---

# 👨‍💻 Autor

## Kaleb Santos

Desenvolvedor Backend Java
Focado em sistemas escaláveis, APIs seguras e arquitetura profissional.

GitHub:
[https://github.com/kalebzaki4](https://github.com/kalebzaki4)

LinkedIn:
[https://www.linkedin.com/in/kaleb-z-santos/](https://www.linkedin.com/in/kaleb-z-santos/)

---

# 📄 Licença

Este projeto está sob a licença MIT.

Veja o arquivo `LICENSE` para mais detalhes.

---

⭐ Se este projeto te ajudou ou te inspirou, considere deixar uma estrela.

```
```
