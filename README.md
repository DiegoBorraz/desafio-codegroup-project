
# Sistema de Gerenciamento de Portfólio de Projetos
Sistema desenvolvido para o processo seletivo de Desenvolvedor Java, com o objetivo de gerenciar o portfólio de projetos de uma empresa, permitindo o acompanhamento completo do ciclo de vida de cada projeto.

## 📋 Funcionalidades
### ✅ Requisitos Implementados
- #### CRUD Completo de Projetos ####

    -  Nome, datas de início, previsão de término e término real

    - Orçamento total (BigDecimal)

    - Descrição, gerente responsável e status atual

    - Classificação de risco calculada dinamicamente

- #### Gestão de Membros ####

    - Integração com API externa mockada para membros

    - Associação de membros aos projetos

    - Validação de atribuição ("funcionário" para membros comuns)

    - Limite de 3 projetos ativos por membro

- #### Status e Transições ####

    - Fluxo definido: em análise → análise realizada → análise aprovada → iniciado → planejado → em andamento → encerrado

    -  Status "cancelado" pode ser aplicado a qualquer momento

    - Validação de transições de status

- #### Relatórios ####

    - Quantidade de projetos por status

    - Total orçado por status

    - Média de duração dos projetos encerrados

    - Total de membros únicos alocados

- #### Funcionalidades Técnicas ####

    - Paginação e filtros para listagem

    - Documentação Swagger/OpenAPI

    - Segurança básica com Spring Security

    - Tratamento global de exceções


## 🛠️ Tecnologias Utilizadas
- #### Java 17 ####

- #### Spring Boot 3.5.5 ####

- #### Spring Data JPA + Hibernate ####

- #### PostgreSQL ####

- #### Spring Security ####

- #### ModelMapper ####

- #### Springdoc OpenAPI ####

- #### Flyway (para migrações de banco) ####

- #### Lombok ####

- #### JUnit 5 + Mockito (testes unitários) ####

- #### Maven ####

## 📦 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/br/codegroup/
│   │   ├── config/          # Configurações
│   │   ├── controller/      # Controladores REST
│   │   ├── domain/          # Entidades JPA
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Exceções customizadas
│   │   ├── repository/      # Interfaces de repositório
│   │   ├── service/         # Lógica de negócio
│   │   └── util             # Funções unitárias
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       └── application.yml  # Configurações
└── test/                    # Testes unitários

```

## 🚀 Como Executar
### Pré-requisitos ###
- Java 17

- Maven 3.6+

- PostgreSQL

#### Configuração do Banco de Dados #### 
1. Crie um banco PostgreSQL chamado **code_group**

2. Configure as credenciais no **application.yml**:

```
spring.application.name=codegroup

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/code_group
spring.datasource.username=sua senha
spring.datasource.password=sua senha
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

# Disable DevTools in-memory database detection
spring.devtools.restart.enabled=false

# Swagger config
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.operationsSorter=alpha
springdoc.show-actuator=true

# Personalização
springdoc.info.title=API do DESAFIO TÉCNICO CODE GROUP
springdoc.info.version=1.0.0
springdoc.info.description=Documentação da API do Desafio técnico da CODE GROUP
springdoc.info.contact.name=Diego Avila
springdoc.info.contact.email=diego.avila.dev@gmail.com

# Para ver logs detalhados do erro
logging.level.org.springframework.boot.autoconfigure.jdbc=DEBUG

# Logging para Security
logging.level.org.springframework.security=DEBUG
logging.level.com.company.portfoliomanager.config=DEBUG

```

### Executando a Aplicação ###

```
# Clone o repositório
git clone git@github.com:DiegoBorraz/desafio-codegroup-project.git

# Navegue até o diretório
cd codeGroup

# Execute com Maven
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

## 📚 Documentação da API
A documentação completa da API está disponível via Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html

- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

**Endpoints Principais**

***Projetos***

- `GET /api/projects` - Listar projetos (com paginação e filtro)

- `GET /api/projects/{id}` - Buscar projeto por ID

- `POST /api/projects` - Criar novo projeto

- `PUT /api/projects/{id}` - Atualizar projeto

- `DELETE /api/projects/{id}` - Excluir projeto

- `PATCH /api/projects/{id}/status` - Atualizar status do projeto

***Relatórios***
- `GET /api/reports/portfolio` - Gerar relatório do portfólio

***Membros***
- `GET /api/members` - Listar membros (API mockada)

- `POST /api/members` - Criar membro (API mockada)

## 🔐 Autenticação
A aplicação utiliza autenticação básica com Spring Security:

### Usuário Administrador: ###

- **Username**: admin

- **Password**: admin123

### Usuário Manager: ###

- **Username**: manager

- **Password**: manager123

### Usuário: ###

- **Username**: user

- **Password**: user123

## 🧪 Testes
### Executando os Testes ###

```
# Executar todos os testes
mvn test
```


## 📊 Regras de Negócio Implementadas
### Classificação de Risco###
- **Baixo risco**: Orçamento ≤ R$ 100.000 e prazo ≤ 3 meses

- **Médio risco**: Orçamento entre R$ 100.001-500.000 OU prazo 3-6 meses

- **Alto risco**: Orçamento > R$ 500.000 OU prazo > 6 meses

### Limites de Projetos ###
- ✅ Mínimo 1 membro por projeto

- ✅ Máximo 10 membros por projeto

- ✅ Máximo 3 projetos ativos por membro

- ✅ Apenas funcionários podem ser associados a projetos

### Status e Transições ###

```
STATUS_ORDER = [
    UNDER_REVIEW,          // em análise
    REVIEW_COMPLETED,      // análise realizada
    REVIEW_APPROVED,       // análise aprovada
    STARTED,               // iniciado
    PLANNED,               // planejado
    IN_PROGRESS,           // em andamento
    COMPLETED              // encerrado
]
```

## 📝 Licença
Este projeto foi desenvolvido para fins de processo seletivo.

### 👨‍💻 Desenvolvedor ### 
Desenvolvido como parte do processo seletivo para vaga de Desenvolvedor Java.

**Nota**: Este projeto atende todos os requisitos técnicos solicitados, incluindo arquitetura MVC, princípios SOLID, tratamento de exceções, testes unitários e documentação completa da API.
