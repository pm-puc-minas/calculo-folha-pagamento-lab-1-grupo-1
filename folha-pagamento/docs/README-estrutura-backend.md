# Estrutura do Backend (Folha de Pagamento)

Este documento descreve detalhadamente a estrutura de pastas e arquivos do backend, suas responsabilidades e como se relacionam. O backend é uma aplicação Spring Boot organizada em camadas (controller, service, repository) com segurança (JWT) e JPA/Hibernate.

## Raiz do Módulo Backend

- `folha-pagamento/pom.xml`: Arquivo Maven com dependências (Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Jackson etc.), plugins e configurações de build.
- `folha-pagamento/mvnw` e `folha-pagamento/mvnw.cmd`: Wrappers do Maven para builds reproduzíveis sem precisar de Maven instalado.
- `folha-pagamento/.gitignore` e `folha-pagamento/.gitattributes`: Configurações de versionamento Git para ignorar artefatos e normalizar atributos.
- `folha-pagamento/docs/`: Materiais complementares do projeto (diagramas, wireframes, requisitos, testes). Este arquivo faz parte desta pasta.

## Código Fonte Java (`src/main/java/com/payroll`)

- `FolhaPagamentoApplication.java`: Classe principal que inicializa o Spring Boot (`@SpringBootApplication`). Ponto de entrada da aplicação.

### Pacote `config`

- `SecurityConfig.java`: Configuração de segurança da aplicação (Spring Security). Define autenticação, autorização, filtros, rotas públicas/privadas e políticas de CORS.
- `JwtAuthenticationFilter.java`: Filtro que intercepta requisições, extrai e valida o token JWT, popula o contexto de segurança quando válido.
- `JwtUtil.java`: Utilitário para criação, validação e parsing de tokens JWT (claims, subject, expirations).

- `DataLoader.java`: Carga de dados iniciais (seed). Cria usuários padrão (ex.: `admin/admin123`, `user/user123`) e quaisquer registros necessários para inicialização.

### Pacote `controller`

- `AuthController.java` e `IAuthController.java`: Endpoints de autenticação (`/api/auth`). Implementam login, refresh de token e registro de usuário. O contrato está em `IAuthController` e a implementação em `AuthController`.
- `EmployeeController.java` e `IEmployeeController.java`: Endpoints de CRUD de funcionários (`/api/employees`). Criam, listam, atualizam e removem colaboradores. Operam sobre o Entity `Employee` no corpo do request.
- `PayrollController.java` e `IPayrollController.java`: Endpoints de cálculo de folha (`/api/payroll`). Recebem parâmetros (funcionário, horas, benefícios) e retornam `PayrollDTO` com remuneração e descontos.
- `DashboardController.java` e `IDashboardController.java`: Endpoints de relatórios e agregações para dashboards (ex.: totais por departamento/posição, média salarial, indicadores).

Observação: As interfaces (`I...Controller`) descrevem assinaturas e contratos (paths, métodos, tipos), enquanto as classes concretas contêm a lógica e delegam para serviços.

### Pacote `dto`

- `EmployeeDTO.java`: Objeto de transferência usado principalmente como resposta (response) para dados de funcionário. Pode incluir campos derivados, mascaramento e estrutura amigável ao frontend.
- `PayrollDTO.java`: Resultado de um cálculo de folha (salário bruto, INSS, IRRF/IRRF, adicionais, salário líquido, observações). É a resposta dos endpoints de cálculo.

### Pacote `entity` (JPA)

- `Employee.java`: Entidade persistente de funcionário (JPA). Campos principais: `fullName`, `cpf`, `rg`, `position`, `admissionDate`, `salary`, `weeklyHours`, além de flags/valores de benefícios e condições de trabalho (`transportVoucher`, `mealVoucher`, `mealVoucherValue`, `dangerousWork`, `unhealthyWork`, `unhealthyLevel`). Participa dos CRUDs.
- `PayrollCalculation.java`: Entidade que guarda cálculos de folha realizados, permitindo auditoria/histórico (valores de descontos, adicionais, líquido, período, vínculo com funcionário).
- `User.java`: Entidade de usuário do sistema (credenciais, roles/perfis). Usada para autenticação/autorização via Spring Security.
- `.LCKEmployee.java~`: Arquivo de lock temporário (artefato de editor). Não faz parte do código.

### Pacote `exception`

- `base/BusinessException.java` e `base/AbstractBusinessException.java`: Hierarquia base de exceções de negócio. Padroniza código, mensagem e mapeamento para respostas HTTP.
- `InputValidationException.java`: Erro de validação de entrada (ex.: campos obrigatórios ausentes, formato inválido). Mapeado para respostas 400.
- `NotFoundBusinessException.java`: Erros de recurso não encontrado (ex.: funcionário inexistente). Normalmente mapeado para 404.
- `InternalServerBusinessException.java`: Erros gerais de processamento não previsto. Mapeado para 500.
- `DataIntegrityBusinessException.java`: Quebras de integridade (ex.: violação de chave única, relacionamento). Normalmente 409.
- `DatabaseConnectionException.java`: Problemas de conexão com banco de dados. Pode ser mapeado para 503/500 conforme política.

### Pacote `model` (Domínio não persistente)

- `Employee.java`: Modelo de domínio (não JPA) usado em cálculos/serviços quando não se quer carregar entidades JPA. Evita efeitos colaterais de persistência durante simulações.
- `Department.java` e `Position.java`: Estruturas de domínio para departamento e cargo, utilizadas em relatórios/agrupamentos.
- `Payroll.java`: Estrutura agregadora para cálculo de folha (entradas/saídas). Base para gerar `PayrollDTO`.
- `Authentication.java`: Modelo para credenciais e fluxo de autenticação.
- `Report.java`: Modelo de dados para relatórios e dashboards (linhas, métricas, agregações).
- `.LCKEmployee.java~`: Artefato temporário; ignorar.

### Pacote `repository`

- `BaseRepository.java`: Repositório base genérico com utilidades comuns. Anotado com `@NoRepositoryBean` para evitar instância direta pelo Spring (corrige o erro de bean genérico). Deve ser estendido por repositórios específicos.
- `EmployeeRepository.java`: Repositório JPA para `Employee` (consultas, paginação, filtros específicos).
- `PayrollCalculationRepository.java`: Repositório JPA para histórico de cálculos de folha.
- `UserRepository.java`: Repositório JPA para usuários (consultas por `username`/`email`, etc.).

### Pacote `serialization`

- `SerializationService.java`: Contrato para serviços de serialização (padronização de `toJson`, formatação customizada etc.).
- `AbstractJacksonSerializationService.java`: Implementação base usando Jackson para serialização/deserialização controlada.
- `EmployeeSerializationService.java`: Serialização específica de `Employee` (ex.: mascaramento de `cpf/rg`, inclusão/exclusão de campos, normalização de nomes).
- `DepartmentSerializationService.java`: Serialização de departamentos, adequada para respostas de relatórios.
- `PayrollCalculationSerializationService.java`: Serialização de cálculos de folha, garantindo consistência em relatórios e auditoria.

### Pacote `service`

- `EmployeeService.java` e `IEmployeeService.java`: Regras de negócio de funcionários (criar, atualizar, validar, excluir, consultar). Concentra validações de `fullName`, `rg`, `salary`, datas, horas semanais etc.
- `UserService.java` e `IUserService.java`: Regras de usuário (registro, busca, atualização de perfis, integridade). Pode interagir com `DataLoader` para seeds.
- `CustomUserDetailsService.java`: Integração com Spring Security; carrega `User` do banco para autenticação.
- `PayrollService.java` e `IPayrollService.java`: Orquestração do cálculo de folha. Aplica descontos, adicionais e compõe `PayrollDTO`.
- `SheetCalculator.java`: Núcleo de cálculo da folha; aplica regras de negócio (proventos, descontos) sobre entradas do funcionário.
- `PayrollConstants.java`: Constantes e alíquotas (ex.: faixas de INSS, IRRF/IRPF, limites de benefícios). Centraliza parâmetros do cálculo.
- `IDesconto.java`: Interface para componentes de desconto no cálculo (contrato comum).
- `INSS.java`, `IRRF.java`, `IRPF.java`: Implementações de descontos específicos conforme legislação vigente.

### Pacote `web`

- `GlobalExceptionHandler.java`: Mapeia exceções de negócio para respostas HTTP padronizadas. Gera envelopes e códigos apropriados (400, 404, 409, 500 etc.).
- `ApiError.java`: Estrutura de erro usada nas respostas (código, mensagem, detalhes, timestamp).
- `ApiEnvelope.java`: Envelope padrão de resposta para sucesso/erro, com metadados.

### Pacote `collections`

- `CollectionOps.java`: Utilitários para operações com coleções (map/filter/reduce customizados sobre listas/streams).
- `FilterSpec.java`: Especificações de filtro composáveis para consultas/relatórios.
- `GroupBySpec.java`: Especificações para agrupamentos (ex.: por departamento, posição), úteis em dashboards.

## Recursos (`src/main/resources`)

- `application.properties`: Configuração padrão da aplicação (porta, contexto, JPA/Hibernate, segurança, JWT, datasource). Pode selecionar perfis (`spring.profiles.active`).
- `application-postgres.properties`: Configuração específica para PostgreSQL (URL, usuário, senha, dialeto, pool de conexão).
- `application-dev.properties.properties` e `application-prod.properties.properties`: Perfis de desenvolvimento e produção (logs, otimizações, CORS, caches). Observação: a dupla extensão `.properties.properties` indica arquivos nomeados com redundância; funcionalmente são arquivos de propriedades.

## Fluxo Geral

- Controllers recebem requisições e validam entradas mínimas, delegando para Services.
- Services aplicam regras de negócio, conversões e interagem com Repositories.
- Repositories persistem e consultam entidades JPA.
- Config define segurança (JWT) e documentação (OpenAPI).
- Web padroniza respostas e tratamento de exceções.
- Serialization cuida de como dados são formatados para saída.
- Models são usados quando é desejável trabalhar com objetos de domínio sem acoplamento JPA.

## Observações Importantes

- `EmployeeController` espera o corpo do request com a entidade `Employee` (campos como `fullName`, `rg`, `salary`). Não use campos do DTO para criação.
- `BaseRepository` está anotado com `@NoRepositoryBean` para evitar instanciação indevida pelo Spring (corrige erro de `BeanCreationException`).
- Credenciais padrão de seed: `admin/admin123` (`admin@payroll.com`) e `user/user123` (`user@payroll.com`), definidas em `DataLoader`.
- Detalhes de endpoints, exemplos de payloads e respostas estão em `docs/README.md` (API) e complementados nas seções de autenticação e funcionários.