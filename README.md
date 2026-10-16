# SyncSpace

### API REST para organizar a operação de clínicas — da recepção à agenda médica.

O **SyncSpace** é um backend em desenvolvimento para apoiar rotinas clínicas internas: cadastro de pacientes e profissionais, gestão de salas e agendamento de consultas. A aplicação foi pensada para o fluxo real de uma clínica: **pacientes são atendidos, não usuários do sistema**; o acesso fica com os funcionários, de acordo com suas responsabilidades.

> **Projeto de portfólio em evolução** — segurança por papéis, regras de disponibilidade e persistência relacional fazem parte da implementação atual. A documentação interativa com **Swagger e OpenAPI** está planejada para uma próxima etapa.

## O que a aplicação entrega

- **Autenticação stateless com JWT** e rotas protegidas por token Bearer.
- **Autorização por papéis** (`ADMIN`, `ATENDENTE`, `MEDICO`) aplicada na API.
- **Cadastro independente de pacientes**, sem conta ou credenciais de acesso.
- **Gestão de salas**, médicos e atendentes, com validação de duplicidades.
- **Agendamento e realocação de consultas**, associando paciente, médico, sala e intervalo de horário.
- **Verificação de conflitos de agenda** para evitar sobreposição para o paciente, o médico ou a sala.
- **Agenda do médico autenticado**, limitada às consultas vinculadas ao seu perfil.
- **Múltiplas especialidades por médico**, persistidas como coleção e devolvidas no DTO de resposta.
- **DTOs de entrada e saída**, validação de dados e tratamento centralizado de erros.
- **Migrações de banco versionadas com Flyway** para evoluir o esquema sem descartar dados existentes.

## Tecnologias

| Área | Tecnologias |
|---|---|
| Linguagem e runtime | Java (versão configurada no projeto: 21) |
| Framework | Spring Boot 4.1.1, Spring Web MVC |
| Segurança | Spring Security, JWT (`java-jwt` / Auth0), BCrypt |
| Persistência | Spring Data JPA, Hibernate, MySQL |
| Banco e evolução de esquema | Flyway |
| Validação | Jakarta Bean Validation |
| Produtividade | Lombok, Maven |
| Testes | JUnit, Spring Boot Test, Spring Security Test |

## Arquitetura

O código segue uma separação por responsabilidades:

```text
src/main/java/com/syncspace/api/
├── config/       # configuração de segurança e infraestrutura
├── controller/   # endpoints HTTP e contratos REST
├── dto/          # modelos de requisição e resposta
├── enums/        # papéis, especialidades e valores de domínio
├── exception/    # erros de domínio e resposta global
├── model/        # entidades JPA
├── repository/   # acesso a dados
├── security/     # filtro JWT e emissão/validação de tokens
└── service/      # regras de negócio
```

Os controllers cuidam da interface HTTP, enquanto os services concentram as validações do domínio — como duplicidade, vínculo do médico, intervalo válido e disponibilidade. As respostas usam DTOs em vez de expor diretamente as entidades de persistência.

## Acesso por perfil

| Papel | Responsabilidades principais |
|---|---|
| `ADMIN` | Acesso administrativo às salas, pacientes, médicos e atendentes; pode remover médicos e atendentes. |
| `ATENDENTE` | Operação da recepção: cadastro e manutenção de pacientes, salas e consultas, além de consultas aos cadastros de médicos e atendentes. |
| `MEDICO` | Consulta à própria agenda por `GET /consultas/minhas-consultas`. |

As rotas exigem JWT, exceto o login. Pacientes não recebem usuário, senha ou token.

## Rotas disponíveis

Todas as rotas abaixo, exceto `POST /auth/login`, precisam do cabeçalho:

```http
Authorization: Bearer <seu-token>
```

| Recurso | Método e rota | Acesso | Finalidade |
|---|---|---|---|
| Autenticação | `POST /auth/login` | Público | Valida email e senha e retorna um JWT. |
| Salas | `GET /salas` | ADMIN, ATENDENTE | Lista salas. |
| Salas | `POST /salas` | ADMIN, ATENDENTE | Cadastra uma sala. |
| Salas | `PUT /salas/{id}` | ADMIN, ATENDENTE | Atualiza uma sala. |
| Salas | `DELETE /salas/{id}` | ADMIN, ATENDENTE | Remove uma sala sem consultas vinculadas. |
| Médicos | `GET /medicos`, `GET /medicos/{id}` | ADMIN, ATENDENTE | Lista ou consulta médicos. |
| Médicos | `POST /medicos` | ADMIN, ATENDENTE | Cria perfil para uma conta existente com papel `MEDICO`. |
| Médicos | `PUT /medicos/{id}` | ADMIN, ATENDENTE | Atualiza CRM e especialidades, mantendo o vínculo da conta. |
| Médicos | `DELETE /medicos/{id}` | ADMIN | Remove um perfil médico. |
| Pacientes | `GET /pacientes`, `GET /pacientes/{id}` | ADMIN, ATENDENTE | Lista ou consulta pacientes. |
| Pacientes | `POST /pacientes` | ADMIN, ATENDENTE | Cadastra um paciente sem conta de acesso. |
| Pacientes | `PUT /pacientes/{id}` | ADMIN, ATENDENTE | Atualiza os dados cadastrais. |
| Pacientes | `DELETE /pacientes/{id}` | ADMIN, ATENDENTE | Remove paciente sem consultas vinculadas. |
| Atendentes | `GET /atendentes`, `GET /atendentes/{id}` | ADMIN, ATENDENTE | Lista ou consulta perfis de atendente. |
| Atendentes | `POST /atendentes` | ADMIN, ATENDENTE | Cria perfil para o próprio usuário autenticado. |
| Atendentes | `PUT /atendentes/{id}` | Próprio atendente ou ADMIN | Atualiza o setor do perfil. |
| Atendentes | `DELETE /atendentes/{id}` | ADMIN | Remove um perfil de atendente. |
| Consultas | `POST /consultas` | ADMIN, ATENDENTE | Agenda uma consulta e verifica conflitos de horário. |
| Consultas | `PUT /consultas/{id}` | ADMIN, ATENDENTE | Atualiza ou realoca uma consulta. |
| Consultas | `DELETE /consultas/{id}` | ADMIN, ATENDENTE | Cancela uma consulta. |
| Consultas | `GET /consultas/minhas-consultas` | MEDICO | Lista somente a agenda do médico autenticado. |

## Executar localmente

### Pré-requisitos

- Java 21 ou compatível com a configuração do projeto
- MySQL 8
- Maven Wrapper incluído no repositório

### Banco de dados

Crie ou disponibilize um banco MySQL chamado `syncspace_db`. Antes de iniciar, configure a URL e as credenciais do banco e um segredo próprio para assinatura JWT. O Spring Boot aceita, por exemplo, estas variáveis de ambiente:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/syncspace_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=<usuario-do-banco>
SPRING_DATASOURCE_PASSWORD=<senha-do-banco>
API_SECURITY_TOKEN_SECRET=<segredo-forte-e-privado>
```

Não use credenciais ou segredos de desenvolvimento em produção. O Flyway aplica as migrações versionadas durante a inicialização.

### Iniciar com o perfil local

O perfil `local` cria, de forma idempotente, um atendente de teste para facilitar a exploração da API:

```powershell
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_EMAIL = "atendente@clinica.local"
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_SENHA = "DefinaUmaSenhaLocal"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Os valores padrão do atendente inicial são somente para desenvolvimento local. Troque-os por variáveis de ambiente e não habilite esse perfil em produção.

## Primeiro fluxo de teste

### 1. Obter um token

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "atendente@clinica.local",
  "senha": "DefinaUmaSenhaLocal"
}
```

A resposta contém o token:

```json
{
  "token": "<jwt>"
}
```

Use-o no cabeçalho `Authorization` das próximas chamadas.

### 2. Cadastrar um paciente

```http
POST /pacientes
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "tipoSanguineo": "O_POSITIVO",
  "alergias": "NENHUMA",
  "doencasCronicas": "NENHUMA",
  "medicamentos": "NENHUM",
  "contatoDeEmergencia": "Ana Souza",
  "telefoneContatoEmergencia": "11987654321",
  "prontuario": "PRONT-TESTE-001"
}
```

### 3. Agendar uma consulta

Use IDs existentes para o paciente, a sala e o médico e escolha um horário livre:

```http
POST /consultas
Authorization: Bearer <jwt>
Content-Type: application/json
```

```json
{
  "pacienteId": 1,
  "salaId": 1,
  "medicoId": 8,
  "dataHoraInicio": "2026-11-02T12:00:00.000Z",
  "dataHoraFim": "2026-11-02T13:00:00.000Z"
}
```

## Médicos com várias especialidades

O cadastro/atualização do perfil recebe uma coleção `especialidades`. Os valores disponíveis estão no enum `Especialidade`:

```json
{
  "usuarioId": 8,
  "crm": "CRM-123456",
  "especialidades": [
    "CARDIOLOGIA",
    "DERMATOLOGIA",
    "ORTOPEDIA"
  ]
}
```

O `usuarioId` precisa pertencer a uma conta existente com papel `MEDICO`. A resposta também apresenta as especialidades como uma lista. A restrição de duplicidade de valores é aplicada pela coleção do domínio.

## Testes

Execute os testes automatizados com o Maven Wrapper:

```powershell
.\mvnw.cmd test
```

## Próximas etapas

- Documentação interativa e contrato da API com **Swagger / OpenAPI**.
- Ampliação dos testes automatizados de serviços, autorização e integração.
- Endurecimento da configuração de produção e externalização de segredos.
- Evolução dos fluxos de provisionamento de contas e perfis internos.

---

**SyncSpace** demonstra a aplicação prática de APIs REST, modelagem relacional, autenticação JWT e autorização por papéis em um domínio com regras de negócio reais. O foco do projeto é evoluir uma base funcional com segurança, consistência de dados e clareza arquitetural.
