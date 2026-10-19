# SyncSpace

**API REST para gerenciamento de operações clínicas, da recepção à agenda médica.**

O **SyncSpace** é um backend desenvolvido com Java e Spring Boot para centralizar rotinas administrativas de uma clínica, incluindo o cadastro de pacientes, o gerenciamento de profissionais e salas e o agendamento de consultas.

A aplicação foi projetada considerando a separação entre pacientes e usuários do sistema: **pacientes são atendidos, enquanto funcionários autenticados operam a plataforma de acordo com seus papéis e permissões.**

> **Status:** Projeto de portfólio em desenvolvimento. A implementação contempla autenticação JWT, autorização baseada em papéis, persistência relacional, validações de domínio e gerenciamento de conflitos de agenda. A documentação interativa com Swagger/OpenAPI está prevista para uma próxima etapa.

## Índice

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Controle de acesso](#controle-de-acesso)
- [Endpoints](#endpoints)
- [Configuração e execução](#configuração-e-execução)
- [Primeiro fluxo de utilização](#primeiro-fluxo-de-utilização)
- [Médicos e especialidades](#médicos-e-especialidades)
- [Testes](#testes)
- [Próximas etapas](#próximas-etapas)

## Funcionalidades

### Autenticação e autorização
- Autenticação stateless com JWT.
- Proteção de endpoints por token Bearer.
- Autorização baseada em papéis: `ADMIN`, `ATENDENTE` e `MEDICO`.
- Senhas armazenadas com hash BCrypt.
- Controle de acesso aplicado às operações conforme o papel do usuário.

### Gestão clínica
- Cadastro, consulta, atualização e remoção de pacientes.
- Gerenciamento de salas e profissionais da clínica.
- Criação e manutenção de perfis médicos e de atendentes.
- Criação de contas de acesso por função: atendentes criam médicos e ADMIN cria atendentes.
- Associação de perfis profissionais a contas de usuário existentes, conforme as regras de negócio.
- Suporte a múltiplas especialidades por médico.
- Agendamento, atualização, realocação e cancelamento de consultas.
- Consulta à agenda do médico autenticado, sem acesso às agendas de outros médicos por esse endpoint.

### Consistência e manutenção
- Validação de dados com Jakarta Bean Validation.
- DTOs de entrada e saída para separar os contratos da API das entidades de persistência.
- Tratamento centralizado de exceções.
- Validação de duplicidades e integridade dos vínculos entre entidades.
- Verificação de conflitos de horário envolvendo paciente, médico e sala.
- Migrações de banco de dados versionadas com Flyway.

## Tecnologias

| Categoria | Tecnologias |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 |
| API HTTP | Spring Web MVC |
| Segurança | Spring Security, JWT (Auth0 `java-jwt`) e BCrypt |
| Persistência | Spring Data JPA e Hibernate |
| Banco de dados | MySQL 8 |
| Migrações | Flyway |
| Validação | Jakarta Bean Validation |
| Produtividade | Lombok e Maven |
| Testes | JUnit, Spring Boot Test e Spring Security Test |

## Arquitetura

O projeto utiliza uma arquitetura em camadas, separando a comunicação HTTP, as regras de negócio, a persistência e os mecanismos de segurança.

```text
src/main/java/com/syncspace/api/
├── config/       # Configurações de segurança e infraestrutura
├── controller/   # Endpoints HTTP e contratos REST
├── dto/          # Objetos de requisição e resposta
├── enums/        # Papéis, especialidades e valores de domínio
├── exception/    # Exceções e tratamento global de erros
├── model/        # Entidades JPA
├── repository/   # Acesso e consultas ao banco de dados
├── security/     # Emissão, validação e processamento de JWT
└── service/      # Regras de negócio e validações de domínio
```

### Responsabilidades das camadas

- **Controllers:** recebem as requisições HTTP e retornam as respostas da API.
- **Services:** concentram as regras de negócio, incluindo validações, vínculos entre profissionais e contas e disponibilidade de horários.
- **Repositories:** abstraem o acesso aos dados por meio do Spring Data JPA.
- **Models:** representam as entidades persistidas no banco de dados.
- **DTOs:** definem os dados aceitos e devolvidos pela API, evitando a exposição direta das entidades.
- **Security:** implementa o fluxo de autenticação e a validação dos tokens JWT.

Essa divisão favorece a manutenção do código, a evolução das regras de negócio e a testabilidade dos componentes.

## Controle de acesso

A aplicação diferencia os usuários internos conforme suas responsabilidades.

| Papel | Responsabilidades |
|---|---|
| `ADMIN` | Administra o sistema e pode criar contas de acesso para atendentes. Pode remover perfis médicos e de atendentes. |
| `ATENDENTE` | Operações de recepção, incluindo cadastro e manutenção de pacientes, salas e consultas. Pode criar contas de acesso MEDICO e cadastrar o perfil profissional com CRM e especialidades. |
| `MEDICO` | Consulta à própria agenda por meio de `GET /consultas/minhas-consultas`. |

**Princípios de acesso:**

- Todas as rotas exigem autenticação JWT, exceto `POST /auth/login`.
- As permissões são aplicadas de acordo com o papel exigido por cada operação.
- Pacientes não possuem contas de acesso, senhas ou tokens.
- A agenda pessoal é associada ao perfil do médico autenticado.

As permissões específicas de cada endpoint estão descritas na tabela a seguir.

## Endpoints

Todas as rotas abaixo, exceto `POST /auth/login`, exigem o cabeçalho:

```http
Authorization: Bearer <seu-token>
```

### Autenticação

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Autentica um usuário e retorna um JWT. |

### Contas de acesso

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/usuarios` | `ATENDENTE` para criar `MEDICO`; `ADMIN` para criar `ATENDENTE` | Cria uma conta de acesso conforme o papel do usuário autenticado. Não cria perfis ADMIN nem contas de paciente. |
| `GET` | `/api/usuarios/{id}` | `ADMIN` | Consulta os dados de uma conta sem retornar a senha. |

### Salas

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/salas` | `ADMIN`, `ATENDENTE` | Lista as salas cadastradas. |
| `POST` | `/salas` | `ADMIN`, `ATENDENTE` | Cadastra uma sala. |
| `PUT` | `/salas/{id}` | `ADMIN`, `ATENDENTE` | Atualiza uma sala. |
| `DELETE` | `/salas/{id}` | `ADMIN`, `ATENDENTE` | Remove uma sala sem consultas vinculadas. |

### Médicos

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/medicos` | `ADMIN`, `ATENDENTE` | Lista os médicos. |
| `GET` | `/medicos/{id}` | `ADMIN`, `ATENDENTE` | Consulta um médico pelo ID. |
| `POST` | `/medicos` | `ADMIN`, `ATENDENTE` | Cria um perfil médico para uma conta existente com papel `MEDICO`. |
| `PUT` | `/medicos/{id}` | `ADMIN`, `ATENDENTE` | Atualiza o CRM e as especialidades, mantendo o vínculo com a conta. |
| `DELETE` | `/medicos/{id}` | `ADMIN` | Remove um perfil médico. |

### Pacientes

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/pacientes` | `ADMIN`, `ATENDENTE` | Lista os pacientes. |
| `GET` | `/pacientes/{id}` | `ADMIN`, `ATENDENTE` | Consulta um paciente pelo ID. |
| `POST` | `/pacientes` | `ADMIN`, `ATENDENTE` | Cadastra um paciente sem criar uma conta de acesso. |
| `PUT` | `/pacientes/{id}` | `ADMIN`, `ATENDENTE` | Atualiza os dados cadastrais. |
| `DELETE` | `/pacientes/{id}` | `ADMIN`, `ATENDENTE` | Remove um paciente sem consultas vinculadas. |

### Atendentes

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `GET` | `/atendentes` | `ADMIN`, `ATENDENTE` | Lista os perfis de atendente. |
| `GET` | `/atendentes/{id}` | `ADMIN`, `ATENDENTE` | Consulta um perfil pelo ID. |
| `POST` | `/atendentes` | `ADMIN`, `ATENDENTE` | Atendente cria o próprio perfil; ADMIN cria o perfil de um usuário ATENDENTE informando `usuarioId`. |
| `PUT` | `/atendentes/{id}` | Próprio atendente ou `ADMIN` | Atualiza o setor do perfil. |
| `DELETE` | `/atendentes/{id}` | `ADMIN` | Remove um perfil de atendente. |

### Consultas

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/consultas` | `ADMIN`, `ATENDENTE` | Agenda uma consulta e verifica conflitos de horário. |
| `PUT` | `/consultas/{id}` | `ADMIN`, `ATENDENTE` | Atualiza ou realoca uma consulta. |
| `DELETE` | `/consultas/{id}` | `ADMIN`, `ATENDENTE` | Cancela uma consulta. |
| `GET` | `/consultas/minhas-consultas` | `MEDICO` | Lista a agenda do médico autenticado. |

## Configuração e execução

### Pré-requisitos

Antes de executar a aplicação, tenha instalado:

- Java 21;
- MySQL 8;
- Maven Wrapper, incluído no repositório.

### 1. Configurar o banco de dados

Crie ou disponibilize um banco MySQL chamado `syncspace_db`.

Configure as seguintes variáveis de ambiente:

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o MySQL. |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados. |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados. |
| `API_SECURITY_TOKEN_SECRET` | Segredo privado utilizado na assinatura dos tokens JWT. |

Exemplo de configuração para desenvolvimento local no PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/syncspace_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME = "seu_usuario"
$env:SPRING_DATASOURCE_PASSWORD = "sua_senha"
$env:API_SECURITY_TOKEN_SECRET = "seu_segredo_local"
```

Substitua os valores de exemplo pelas credenciais do ambiente local. O segredo JWT deve ser forte, privado e compatível com o algoritmo de assinatura configurado na aplicação.

**Importante:** não versione credenciais ou segredos reais no repositório. As configurações utilizadas em desenvolvimento não devem ser reutilizadas em produção.

### 2. Iniciar a aplicação

Para facilitar a exploração da API, o perfil `local` disponibiliza um mecanismo de inicialização de um atendente de teste.

Configure as credenciais desse usuário:

```powershell
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_EMAIL = "atendente@clinica.local"
$env:SYNCSPACE_BOOTSTRAP_ATENDENTE_SENHA = "DefinaUmaSenhaLocal"
```

Para inicializar também a primeira conta administrativa local, configure as variáveis abaixo. Não use essas credenciais de desenvolvimento em produção:

```powershell
$env:SYNCSPACE_BOOTSTRAP_ADMIN_EMAIL = "admin@syncspace.local"
$env:SYNCSPACE_BOOTSTRAP_ADMIN_SENHA = "DefinaUmaSenhaLocalForte"
```

Em seguida, execute a aplicação:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

O perfil local cria o atendente de teste e, quando as duas variáveis de bootstrap ADMIN estão definidas, cria também o administrador local. O bootstrap é idempotente e não altera a senha de uma conta já existente.

Durante a inicialização, o Flyway executa as migrações versionadas para preparar ou atualizar o esquema do banco de dados.

> **Atenção:** o perfil `local` e o mecanismo de inicialização de usuários destinam-se ao desenvolvimento. Não habilite esse perfil em produção.

### Criar conta MEDICO e cadastrar o perfil profissional

Autentique-se como ATENDENTE. Com o token retornado, crie a conta de acesso do médico em `POST /api/usuarios`:

```json
{
  "nome": "Dr. João Silva",
  "email": "joao.silva@syncspace.local",
  "senha": "DefinaUmaSenhaForte",
  "telefone": "11988887777",
  "dataNascimento": "1980-01-01T00:00:00.000Z",
  "cpf": "12345678901",
  "role": "MEDICO"
}
```

A resposta retorna o ID do usuário sem expor a senha. Use esse ID em `usuarioId` para cadastrar o perfil profissional em `POST /medicos`, informando também o CRM e as especialidades. O atendente pode criar pacientes, consultas, salas e contas MEDICO. O ADMIN local pode criar contas de ATENDENTE, mas não contas MEDICO.

Para criar um atendente, autentique-se como ADMIN, crie uma conta com `role: "ATENDENTE"` em `POST /api/usuarios` e use o ID retornado para criar o perfil em `POST /atendentes`:

```json
{
  "usuarioId": 12,
  "setor": "ATENDIMENTO"
}
```

## Primeiro fluxo de utilização

O fluxo abaixo demonstra como autenticar um atendente, cadastrar um paciente e agendar uma consulta.

### 1. Autenticar

Envie uma requisição para `POST /auth/login`:

```http
POST /auth/login
Content-Type: application/json
```

Corpo da requisição:

```json
{
  "email": "atendente@clinica.local",
  "senha": "DefinaUmaSenhaLocal"
}
```

A resposta contém o token JWT:

```json
{
  "token": "<jwt>"
}
```

Utilize o token retornado no cabeçalho `Authorization` das próximas requisições.

### 2. Cadastrar um paciente

Envie uma requisição para `POST /pacientes`:

```http
POST /pacientes
Authorization: Bearer <jwt>
Content-Type: application/json
```

Exemplo de corpo:

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

O exemplo utiliza valores fictícios para demonstrar a estrutura da requisição. Os campos e valores enviados devem respeitar as validações definidas pela aplicação.

### 3. Agendar uma consulta

Com um paciente, uma sala e um médico já cadastrados, envie uma requisição para `POST /consultas`:

```http
POST /consultas
Authorization: Bearer <jwt>
Content-Type: application/json
```

Exemplo de corpo:

```json
{
  "pacienteId": 1,
  "salaId": 1,
  "medicoId": 8,
  "dataHoraInicio": "2026-11-02T12:00:00.000Z",
  "dataHoraFim": "2026-11-02T13:00:00.000Z"
}
```

Os IDs são ilustrativos. Utilize identificadores existentes no banco de dados e selecione um intervalo disponível.

A aplicação valida o intervalo informado e verifica conflitos de agenda relacionados ao paciente, ao médico e à sala.

## Médicos e especialidades

Um perfil médico pode possuir várias especialidades. O cadastro e a atualização do perfil recebem uma coleção `especialidades`, cujos valores são definidos pelo enum `Especialidade`.

Exemplo de requisição:

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

O campo `usuarioId` deve corresponder a uma conta existente com o papel `MEDICO`.

As especialidades são persistidas como uma coleção e retornadas no DTO de resposta. A coleção do domínio também impede a duplicidade dos valores, conforme a implementação atual.

## Testes

Para executar os testes automatizados, utilize o Maven Wrapper.

**Windows — PowerShell:**

```powershell
.\mvnw.cmd test
```

**Linux ou macOS:**

```bash
./mvnw test
```

Os testes utilizam JUnit, Spring Boot Test e Spring Security Test.

## Próximas etapas

- [ ] Disponibilizar documentação interativa da API com Swagger/OpenAPI.
- [ ] Ampliar a cobertura de testes unitários, de autorização e de integração.

## Objetivo do projeto

O **SyncSpace** é um projeto de portfólio voltado à aplicação prática de conceitos de desenvolvimento backend em Java, com foco em:

- construção de APIs REST com Spring Boot;
- autenticação JWT e autorização baseada em papéis;
- modelagem de entidades e relacionamentos com JPA;
- persistência relacional e versionamento de esquema com Flyway;
- implementação de regras de negócio e validações de domínio;
- organização arquitetural, manutenção e testes automatizados.

Mais do que disponibilizar operações CRUD, o objetivo é construir uma base consistente para um sistema de gestão clínica, considerando as relações entre os dados, as permissões de acesso e as regras envolvidas na rotina de uma clínica.

---

*SyncSpace — organização, consistência e segurança no fluxo de operações clínicas.*
