# CARTÕES CRC – FOLHA DE PAGAMENTO

---

## Classe: FolhaPagamento

**Responsabilidades:**
- Gerenciar cadastro de funcionários
- Calcular salário líquido
- Aplicar descontos e proventos
- Exibir demonstrativo completo
- Controlar benefícios e adicionais (periculosidade, insalubridade, vale alimentação, transporte)
- Autenticar usuário

**Colaboradores:**
- Funcionario
- Beneficio
- Desconto
- SistemaAutenticacao
- TabelaINSS
- TabelaIRRF
- FGTS

---

## Classe: Funcionario

**Responsabilidades:**
- Armazenar dados pessoais (nome, CPF, cargo, salário base)
- Registrar tempo de serviço
- Indicar direito a insalubridade/periculosidade
- Gerenciar benefícios individuais
- Exibir informações do funcionário

**Colaboradores:**
- Beneficio
- Desconto
- FolhaPagamento

---

## Classe: Beneficio

**Responsabilidades:**
- Registrar tipos de benefícios (vale alimentação, transporte, etc.)
- Controlar valores dos benefícios
- Listar benefícios disponíveis para cada funcionário

**Colaboradores:**
- Funcionario
- FolhaPagamento

---

## Classe: Desconto

**Responsabilidades:**
- Registrar tipos de descontos (vale transporte, INSS, IRRF)
- Calcular valor de cada desconto
- Listar descontos aplicáveis a cada funcionário

**Colaboradores:**
- Funcionario
- FolhaPagamento
- TabelaINSS
- TabelaIRRF

---

## Classe: SistemaAutenticacao

**Responsabilidades:**
- Validar login e senha do usuário
- Garantir acesso seguro ao sistema de Folha de Pagamento

**Colaboradores:**
- FolhaPagamento

---

## Classe: TabelaINSS

**Responsabilidades:**
- Fornecer alíquotas e faixas de contribuição INSS
- Calcular valor de INSS para um salário específico

**Colaboradores:**
- Desconto
- FolhaPagamento

---

## Classe: TabelaIRRF

**Responsabilidades:**
- Fornecer alíquotas e deduções de IRRF
- Calcular valor de IRRF considerando dependentes e descontos

**Colaboradores:**
- Desconto
- FolhaPagamento

---

## Classe: FGTS

**Responsabilidades:**
- Calcular contribuição de FGTS sobre salário bruto
- Armazenar valores para cada funcionário

**Colaboradores:**
- FolhaPagamento
- Funcionario
