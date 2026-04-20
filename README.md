# 🚀 SyncSpace API

> **Enterprise-grade Room Reservation System** built with Java 21 and Spring Boot 3. 
> Desenvolvido com foco em alta disponibilidade, integridade de dados e segurança, simulando os desafios técnicos de uma Fintech.

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)

**SyncSpace** resolve o problema de agendamentos simultâneos em ambientes corporativos. O diferencial deste projeto é a implementação de **Optimistic Locking** para evitar o "Double Booking" (reserva duplicada) e uma arquitetura que isola totalmente as regras de negócio.

---

## 🛠 Business Rules (Regras de Negócio)
Para garantir a consistência do sistema, as seguintes regras foram implementadas:
- [x] **Prevenção de Conflitos:** O sistema utiliza controle de versão JPA para impedir que dois usuários reservem a mesma sala no exato milissegundo.
- [x] **Validação Temporal:** Reservas não podem ser feitas em horários retroativos.
- [x] **Segurança por Camada:** Endpoints de administração são protegidos via JWT, garantindo que apenas usuários autorizados gerenciem as salas.

---

## ✨ Features Técnicas

- ⛑ **Concurrency Control** — Implementação de `@Version` para integridade de dados.
- 🔐 **Stateless Security** — Autenticação robusta com Spring Security e JWT.
- 🐳 **Infrastructure as Code** — Ambiente 100% conteinerizado com Docker Compose.
- 📐 **Domain-Driven Design (Lite)** — Camadas de serviço puras, isolando a lógica da infraestrutura.
- 📖 **Swagger UI** — Documentação interativa disponível em `/swagger-ui.html`.

---

## 🚦 Endpoints Principais (Exemplos)

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Autenticação e geração de Token | Público |
| `GET` | `/api/rooms` | Lista todas as salas disponíveis | Público |
| `POST` | `/api/reservations` | Cria uma nova reserva | Autenticado |
| `DELETE` | `/api/rooms/{id}` | Remove uma sala do sistema | Admin |

---

## 🚀 Como Executar

### Via Docker (Recomendado)
```bash
# Clone e entre na pasta
git clone [https://github.com/kalebzaki4/room-reservation-api](https://github.com/kalebzaki4/room-reservation-api)
cd room-reservation-api

# Suba todo o ecossistema (App + DB)
docker-compose up -d
```

🧪 Qualidade de Código

O projeto conta com uma suíte de testes focada no caminho feliz e em casos de erro (Edge cases):

Testes Unitários: JUnit 5 e Mockito.

Validação de DTOs: Bean Validation para garantir que dados sujos não cheguem ao banco.

👨‍💻 Autor
Kaleb Zaki
