# API Técnica — Folha de Pagamento

Documento técnico da API do backend (Spring Boot) do sistema de Cálculo de Folha de Pagamento. Este guia cobre requisitos, build/execução, principais endpoints, exemplos de requisição/resposta e configuração de banco de dados PostgreSQL.

## Visão Geral
- Framework: Spring Boot 3.5.x (Java 21)
- Banco (dev): H2 em memória (profile `dev`)
- Banco (prod): PostgreSQL

## Pré-requisitos
- Java 21 (JDK 21)
- Maven 3.9+
- PostgreSQL 14+ (para ambiente de produção)
- Opcional: cURL ou uma ferramenta como Postman para testar chamadas


### Ambiente de produção (PostgreSQL)
1) Configure o `application.properties` (ver seção “Configuração do PostgreSQL”).
2) Defina o profile ativo para `prod` (por exemplo, variável de ambiente `SPRING_PROFILES_ACTIVE=prod`).
3) Build: `mvn -DskipTests package`
4) Executar: `java -jar target\folha-pagamento-0.0.1-SNAPSHOT.jar`

## Dependências
- Spring Boot Web, Spring Data JPA
- Banco de dados: H2 (dev), PostgreSQL (prod)
- Spring Security (JWT) para autenticação
 

## Principais Endpoints

### Funcionários (CRUD)
Base: `/api/employees`
- `GET /api/employees` — Lista funcionários (DTO simplificado)
- `POST /api/employees` — Cria funcionário
- `GET /api/employees/{id}` — Detalha funcionário por ID
- `PUT /api/employees/{id}` — Atualiza funcionário existente
- `DELETE /api/employees/{id}` — Remove funcionário

Exemplo de payload de criação/atualização (`application/json`):
```json
{
  "fullName": "Maria da Silva",
  "cpf": "12345678901",
  "rg": "1234567",
  "position": "Analista",
  "admissionDate": "2023-01-10",
  "salary": 5500.00,
  "weeklyHours": 40,
  "transportVoucher": true,
  "mealVoucher": true,
  "mealVoucherValue": 25.00,
  "dangerousWork": false,
  "unhealthyWork": false,
  "unhealthyLevel": "NONE"
}
[
  {
    "fullName": "João Pereira Souza",
    "cpf": "98765432100",
    "rg": "4567890",
    "position": "Desenvolvedor Backend",
    "admissionDate": "2022-05-15",
    "salary": 7200.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 30.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Ana Beatriz Costa",
    "cpf": "34567890122",
    "rg": "6789012",
    "position": "Engenheira de Software",
    "admissionDate": "2021-03-08",
    "salary": 9800.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 35.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Carlos Henrique Almeida",
    "cpf": "65432198700",
    "rg": "9988776",
    "position": "Técnico de Segurança",
    "admissionDate": "2020-11-12",
    "salary": 4200.00,
    "weeklyHours": 44,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 20.00,
    "dangerousWork": true,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Fernanda Oliveira Ramos",
    "cpf": "74125896300",
    "rg": "1122334",
    "position": "Recursos Humanos",
    "admissionDate": "2023-04-03",
    "salary": 4800.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 25.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Ricardo Lima Barbosa",
    "cpf": "85236974100",
    "rg": "4455667",
    "position": "Eletricista Industrial",
    "admissionDate": "2019-08-19",
    "salary": 3900.00,
    "weeklyHours": 44,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 22.50,
    "dangerousWork": true,
    "unhealthyWork": true,
    "unhealthyLevel": "MEDIO"
  },
  {
    "fullName": "Juliana Castro Fernandes",
    "cpf": "96385274100",
    "rg": "7788990",
    "position": "Assistente Administrativo",
    "admissionDate": "2023-02-21",
    "salary": 3500.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 18.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Eduardo Santos Menezes",
    "cpf": "15975348620",
    "rg": "3344556",
    "position": "Analista Financeiro",
    "admissionDate": "2021-07-01",
    "salary": 6400.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 28.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Patrícia Gomes Duarte",
    "cpf": "25896314711",
    "rg": "2233445",
    "position": "Coordenadora de Projetos",
    "admissionDate": "2020-10-05",
    "salary": 8900.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 32.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  },
  {
    "fullName": "Felipe Andrade Moura",
    "cpf": "32165498700",
    "rg": "6677889",
    "position": "Operador de Máquinas",
    "admissionDate": "2018-09-10",
    "salary": 3100.00,
    "weeklyHours": 44,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 19.00,
    "dangerousWork": true,
    "unhealthyWork": true,
    "unhealthyLevel": "ALTO"
  },
  {
    "fullName": "Camila Rocha Martins",
    "cpf": "78945612300",
    "rg": "5544332",
    "position": "Analista de Qualidade",
    "admissionDate": "2022-12-01",
    "salary": 5200.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 27.50,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  }
]
```

Resposta (DTO simplificado):
```json
{
  "id": 1,
  "name": "Maria da Silva",
  "cpf": "12345678901",
  "position": "Analista",
  "department": "TI",
  "admissionDate": "2023-01-10",
  "baseSalary": 5500.00,
  "dependents": 1,
  "hasHazardPay": false,
  "insalubrity": "NONE",
  "transportVoucherValue": 200.00,
  "mealVoucherDaily": 25.00,
  "workDaysMonth": 22,
  "weeklyHours": 40
}
```

### Folha de Pagamento
Base: `/api/payroll`
- `GET /api/payroll` — Lista cálculos de folha (DTO)
- `POST /api/payroll/calculate` — Calcula folha para funcionário/mês
- `GET /api/payroll/{id}` — Detalhe de um cálculo por ID
- `GET /api/payroll/employee/{employeeId}` — Listagem de folhas de um funcionário

Exemplo de requisição de cálculo (`application/json`):
```json
{
  "employeeId": "1",
  "referenceMonth": "2024-10"
}
```
### Folha de Pagamento — Carga para `POST /api/payroll/calculate`
- Utilize os exemplos abaixo como corpo (`application/json`) para calcular folhas. O campo `employeeId` segue a ordem da carga de funcionários listada neste arquivo: 1=João, 2=Ana, 3=Carlos, 4=Fernanda, 5=Ricardo, 6=Juliana, 7=Eduardo, 8=Patrícia, 9=Felipe, 10=Camila.

Funcionário 1 — João Pereira Souza
```json
{ "employeeId": "1", "referenceMonth": "2024-01" }
```

Funcionário 2 — Ana Beatriz Costa
```json
{ "employeeId": "2", "referenceMonth": "2024-02" }
```

Funcionário 3 — Carlos Henrique Almeida
```json
{ "employeeId": "3", "referenceMonth": "2024-03" }
```

Funcionário 4 — Fernanda Oliveira Ramos
```json
{ "employeeId": "4", "referenceMonth": "2024-04" }
```

Funcionário 5 — Ricardo Lima Barbosa
```json
{ "employeeId": "5", "referenceMonth": "2024-05" }
```

Funcionário 6 — Juliana Castro Fernandes
```json
{ "employeeId": "6", "referenceMonth": "2024-06" }
```

Funcionário 7 — Eduardo Santos Menezes
```json
{ "employeeId": "7", "referenceMonth": "2024-07" }
```

Funcionário 8 — Patrícia Gomes Duarte
```json
{ "employeeId": "8", "referenceMonth": "2024-08" }
```

Funcionário 9 — Felipe Andrade Moura
```json
{ "employeeId": "9", "referenceMonth": "2024-09" }
```

Funcionário 10 — Camila Rocha Martins
```json
{ "employeeId": "10", "referenceMonth": "2024-10" }

Exemplo de resposta (DTO de folha):
```json
{
  "id": 10,
  "employeeId": 1,
  "employeeName": "Maria da Silva",
  "month": "2024-10",
  "hourlyRate": 31.25,
  "totalEarnings": 6100.00,
  "totalDeductions": 800.00,
  "netSalary": 5300.00,
  "hazardPayValue": 0.00,
  "insalubrityValue": 0.00,
  "mealVoucherValue": 550.00,
  "transportVoucherDiscount": 200.00,
  "inssDiscount": 400.00,
  "fgtsValue": 440.00,
  "irrfDiscount": 200.00,
  "inssBase": 5500.00,
  "fgtsBase": 5500.00,
  "irrfBase": 5300.00,
  "calculatedAt": "2024-10-31T10:15:30Z",
  "generatedBy": {
    "id": 1,
    "username": "admin"
  }
}
```

### Relatórios (Dashboard)
Base: `/api/dashboard`
- `GET /api/dashboard` — Estatísticas agregadas para uso no frontend

Exemplo de resposta:
```json
{
  "totalEmployees": 12,
  "totalPayrolls": 34,
  "recentEmployees": [ /* lista de funcionários */ ],
  "recentPayrolls": [ /* lista de folhas */ ]
}
```

## Validações e Respostas
- `400 Bad Request`: formato inválido (ex.: `referenceMonth` fora de `yyyy-MM`, dados obrigatórios ausentes)
- `404 Not Found`: recurso não encontrado (ex.: funcionário inexistente)
- `409 Conflict`: conflito de dados (ex.: CPF já cadastrado)
- `500 Internal Server Error`: erro inesperado durante processamento

Observação: endpoints de criação/atualização podem depender de autenticação; quando habilitado, inclua `Authorization: Bearer <token>`.

## Exemplos de chamadas (cURL)

Listar funcionários:
```bash
curl -X GET http://localhost:8080/api/employees
```

Criar funcionário:
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Maria da Silva",
    "cpf": "12345678901",
    "rg": "1234567",
    "position": "Analista",
    "admissionDate": "2023-01-10",
    "salary": 5500.00,
    "weeklyHours": 40,
    "transportVoucher": true,
    "mealVoucher": true,
    "mealVoucherValue": 25.00,
    "dangerousWork": false,
    "unhealthyWork": false,
    "unhealthyLevel": "NONE"
  }'
```

Atualizar funcionário:
```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{ "position": "Pleno", "salary": 6000.00 }'
```

Excluir funcionário:
```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

Calcular folha:
```bash
curl -X POST http://localhost:8080/api/payroll/calculate \
  -H "Content-Type: application/json" \
  -d '{ "employeeId": "1", "referenceMonth": "2024-10" }'
```

Listar folhas de um funcionário:
```bash
curl -X GET http://localhost:8080/api/payroll/employee/1
```


### Exemplo de `application.properties` (profile prod)
```properties
# Profile
spring.profiles.active=prod

# Datasource
spring.datasource.url=jdbc:postgresql://localhost:5432/payrolldb
spring.datasource.username=postgres
spring.datasource.password=senha
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server
server.port=8080
```

> Nota: Em desenvolvimento, o profile `dev` utiliza H2 em memória. Para produção, configure o profile `prod` e garanta que o PostgreSQL esteja acessível.

## Autenticação (JWT)
- Endpoints de autenticação em `/api/auth`:
  - `POST /api/auth/login` — Gera `accessToken` e `refreshToken`
  - `POST /api/auth/refresh` — Gera novo `accessToken`
- Utilize `Authorization: Bearer <accessToken>` em chamadas que exigem usuário autenticado.

## Erros Padrão
- Respostas de erro seguem envelope JSON (quando aplicável) e tratadores globais.

## Carga de Dados (raw body para Postman)
- Utilize `Content-Type: application/json` e cole os exemplos abaixo no corpo da requisição (Body > raw > JSON) para testar diretamente no Postman.

Credenciais padrão (DataLoader):
- `admin / admin123` (ADMIN)
- `user / user123` (USER)

Login por username
```json
{
  "username": "admin",
  "password": "admin123"
}
```

Login por email
```json
{
  "email": "user@payroll.com",
  "password": "user123"
}
```

Refresh de token
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Registro (mínimo obrigatório)
```json
{
  "email": "novo.usuario@payroll.com",
  "password": "senha123"
}
```

Registro (com username e perfil)
```json
{
  "username": "novousuario",
  "email": "novo.usuario@payroll.com",
  "password": "senha123",
  "role": "USER"
}
```