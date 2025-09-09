# Sistema de Votação

## Objetivo
No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Este projeto implementa uma solução para gerenciar e participar dessas sessões de votação através de uma API REST.

### Funcionalidades Implementadas

#### **Funcionalidades**
- **Gerenciamento de Pautas**: CRUD completo com soft delete e busca por título
- **Cadastro de Associados**: CRUD com validação de CPF e controle de duplicidade
- **Sessões de Votação**: Criação, início, fechamento manual e automático por expiração
- **Sistema de Votação**: Voto único por associado (SIM/NÃO) com validações
- **Contabilização de Votos**: Resultados em tempo real com status da votação
- **Integração Externa**: Validação de CPF via serviço externo com fallback
- **Cache Inteligente**: Sistema de cache para validação de CPF (performance)
- **Versionamento API**: Estratégia V1/V2 com novas funcionalidades
- **Fechamento Automático**: Scheduler para encerrar sessões expiradas
- **Monitoramento**: Health checks e endpoints do Actuator

#### **Recursos Técnicos**
- **Documentação Swagger**: API totalmente documentada e testável
- **Validações**: Bean Validation com grupos de validação
- **Tratamento de Exceções**: Handler global com status HTTP padronizados
- **Auditoria**: Campos de deleção lógica, criação e atualização automáticos
- **Configurações**: Externalizadas via application.yml

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot 3.1.0**
- **Spring Data JPA**
- **Spring Cache** (gerenciamento de cache)
- **Spring Scheduling** (tarefas agendadas)
- **H2 Database** (em memória)
- **Liquibase** (versionamento do banco)
- **Maven** (gerenciamento de dependências)
- **Lombok** (redução de boilerplate)
- **SpringDoc OpenAPI** (documentação da API)
- **Model Mapper** (conversão de objetos)
- **RestTemplate** (cliente HTTP)

## Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Executando a Aplicação
```bash
# Clone o repositório
git clone <repository-url>

# Execute a aplicação
mvn spring-boot:run

🥳 e só ser feliz...
```

### Acessos Disponíveis
- **Aplicação**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **H2 Console**: http://localhost:8080/h2-console
  - URL: `jdbc:h2:file:./data/voting_system;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
  - User: `sa`
  - Password: (vazio)

## Estrutura do Banco de Dados

### Tabelas Implementadas:
1. **agenda** - Pautas de votação com soft delete
2. **associate** - Cadastro de associados com validação de CPF
3. **voting_session** - Sessões de votação com controle de duração
4. **vote** - Registro individual de votos com timestamp

## Endpoints da API

- **Agendas**: `/api/v1/agendas` - CRUD completo com busca
- **Associados**: `/api/v1/associates` - CRUD com validação de CPF
- **Sessões**: `/api/v1/voting-sessions` - Gerenciamento completo
- **Votos**: `/api/v1/votes` - Registro e consulta de votos

### **V2 - API Aprimorada** 
- **Associados V2**: `/api/v2/associates` - Com validação externa de CPF
- **Novos Endpoints**: 
  - `GET /api/v2/associates/cpf/{cpf}` - Buscar por CPF
  - `GET /api/v2/associates/cpf/{cpf}/validate` - Validar CPF externo

### **Monitoramento**
- **Health**: `/actuator/health` - Status da aplicação
- **Swagger**: `/swagger-ui/index.html` - Documentação interativa

## Arquitetura e Boas Práticas Implementadas

### **Padrões de Projeto**
- **Repository Pattern**: Camada de acesso a dados
- **Dependency Injection**: Inversão de controle
- **DTO Pattern**: Transfer objects para API
- **Strategy Pattern**: Versionamento de API
- **Facade Pattern**: Cliente CPF simplificado

### **Princípios SOLID**
- **Single Responsibility**: Classes com responsabilidade única
- **Open/Closed**: Extensível via interfaces
- **Liskov Substitution**: Polimorfismo adequado
- **Interface Segregation**: Interfaces específicas
- **Dependency Inversion**: Abstrações sobre implementações

### **Práticas de Segurança**
- Validação dos dados de entrada
- Sanitização de dados
- Controle de integridade referencial
- Timeouts em chamadas externas

### **Performance e Confiabilidade**
- Cache inteligente para APIs externas
- Scheduled tasks para automação
- Connection pooling (HikariCP)
- Paginação em todas as listagens
- Health checks para monitoramento

### **Observabilidade**
- Logs estruturados com níveis adequados
- Métricas via Spring Actuator
- Documentação automática (Swagger)
- Versionamento semântico da API

**Aplicação Rodando**: http://localhost:8080