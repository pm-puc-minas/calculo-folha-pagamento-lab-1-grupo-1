# TESTES UNITÁRIOS – FOLHA DE PAGAMENTO

---

## Método: autenticar(login, senha)

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Login correto | login="usuarioValido", senha="senhaValida" | chamar `autenticar` | true | Usuário válido |
| Login incorreto | login="usuarioInvalido", senha="senhaValida" | chamar `autenticar` | false | Usuário não existe |
| Senha incorreta | login="usuarioValido", senha="senhaInvalida" | chamar `autenticar` | false | Senha inválida |
| Login e senha vazios | login="", senha="" | chamar `autenticar` | false | Deve ser tratado |

---

## Método: ajustarSalario()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Salário positivo | salário atual: 3000, novo: 3500 | chamar `ajustarSalario` | salário atualizado para 3500 | Deve atualizar corretamente |
| Salário zero | salário atual: 3000, novo: 0 | chamar `ajustarSalario` | operação aceita ou rejeitada | Conforme regra do sistema |
| Salário negativo | salário atual: 3000, novo: -500 | chamar `ajustarSalario` | erro ou rejeitado | Sistema não permite salário negativo |
| Salário decimal | salário atual: 3000, novo: 3000.75 | chamar `ajustarSalario` | salário atualizado com precisão decimal | Deve manter precisão correta |

---

## Método: adicionarFuncionario()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Funcionário novo | João | chamar `adicionarFuncionario` | funcionário adicionado | Lista deve conter João |
| Funcionário duplicado | João | chamar `adicionarFuncionario` | rejeitado | Não deve permitir duplicação |
| Dados incompletos | João sem CPF | chamar `adicionarFuncionario` | erro ou validação | Sistema deve tratar |

---

## Método: removerFuncionario()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Funcionário existente | João | chamar `removerFuncionario` | funcionário removido | Lista deve atualizar |
| Funcionário inexistente | Maria | chamar `removerFuncionario` | nenhuma alteração | Sem erro |
| Funcionário nulo | null | chamar `removerFuncionario` | erro tratado | Sistema não quebra |

---

## Método: listarFuncionario()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Lista com funcionários | João e Maria | chamar `listarFuncionario` | retorna João e Maria | Lista completa |
| Lista vazia | nenhuma entrada | chamar `listarFuncionario` | retorna lista vazia | Deve tratar lista vazia |
| Após adicionar/remover | adiciona João, remove Maria | chamar `listarFuncionario` | lista atualizada | Confirma consistência |

---

## Método: exibirInformações()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Funcionário existente | João | chamar `exibirInformações` | retorna dados de João | Deve mostrar todas as informações |
| Sem funcionários | nenhuma entrada | chamar `exibirInformações` | retorno vazio ou mensagem | Sistema não quebra |

---

## Método: exibirDados()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Funcionário existente | João | chamar `exibirDados` | retorna dados completos | Consistência dos dados |
| Sem funcionários | nenhuma entrada | chamar `exibirDados` | retorno vazio ou mensagem | Sistema não quebra |

---

## Método: temDireitoInsalubridade()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Direito a insalubridade | funcionário com direito | chamar `temDireitoInsalubridade` | true | Deve identificar corretamente |
| Sem direito | funcionário sem direito | chamar `temDireitoInsalubridade` | false | Correto |
| Dados inconsistentes | dados incompletos | chamar `temDireitoInsalubridade` | false ou erro tratado | Sistema deve lidar |

---

## Método: calcularTempo()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Tempo correto | funcionário com datas válidas | chamar `calcularTempo` | valor correto | Deve calcular tempo de trabalho |
| Dados inválidos | datas ausentes ou inválidas | chamar `calcularTempo` | 0 ou erro tratado | Sistema não deve quebrar |

---

## Método: adicionarBeneficio()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Benefício válido | Vale Alimentação | chamar `adicionarBeneficio` | benefício adicionado | Lista deve atualizar |
| Benefício duplicado | Vale Alimentação | chamar `adicionarBeneficio` | rejeitado | Não duplicar |
| Benefício inválido | dados incompletos | chamar `adicionarBeneficio` | erro ou rejeição | Sistema trata |

---

## Método: ListarBeneficios()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Benefícios existentes | Vale Alimentação, Vale Transporte | chamar `ListarBeneficios` | retorna os dois | Lista correta |
| Sem benefícios | nenhuma entrada | chamar `ListarBeneficios` | lista vazia | Sistema não quebra |

---

## Método: calcular()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Dados completos | proventos e descontos válidos | chamar `calcular` | valores atualizados corretamente | Todos os campos devem ser atualizados |
| Dados faltando | sem proventos ou descontos | chamar `calcular` | valores inconsistentes tratados | Sistema trata dados incompletos |

---

## Método: adicionarDesconto()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Desconto válido | 200 | chamar `adicionarDesconto` | desconto adicionado | Lista atualizada |
| Desconto duplicado | 200 | chamar `adicionarDesconto` | não duplicar | Sistema rejeita |
| Desconto negativo | -50 | chamar `adicionarDesconto` | erro ou rejeição | Validação necessária |

---

## Método: calcularTotalDesconto()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Descontos múltiplos | 100, 50 | chamar `calcularTotalDesconto` | 150 | Soma correta |
| Sem descontos | nenhuma entrada | chamar `calcularTotalDesconto` | 0 | Lista vazia |

---

## Método: calcularTotalProventos()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Proventos múltiplos | 2000, 500 | chamar `calcularTotalProventos` | 2500 | Soma correta |
| Sem proventos | nenhuma entrada | chamar `calcularTotalProventos` | 0 | Lista vazia |

---

## Método: calcularSalarioLiquido()

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Descontos e proventos | Salário 3000, Desconto 500, Provento 200 | chamar `calcularSalarioLiquido` | 2700 | Correto |
| Sem descontos | Salário 3000, Descontos 0 | chamar `calcularSalarioLiquido` | 3000 | Salário líquido = bruto |
| Sem proventos | Salário 3000, Proventos 0 | chamar `calcularSalarioLiquido` | 2500 (considerando desconto) | Verificar consistência |

---

## Método: calcularSalarioHora(salarioBruto, horasSemanais)

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Horas normais | Salário 3000, Horas 40 | chamar `calcularSalarioHora` | 18,75 | Valor por hora correto |
| Horas zero | Salário 3000, Horas 0 | chamar `calcularSalarioHora` | 0 | Sem horas, resultado zero |
| Horas negativas | Salário 3000, Horas -5 | chamar `calcularSalarioHora` | erro ou tratamento | Sistema deve lidar |

---

## Método: calcularAdicionalPericulosidade(salarioBase)

| Cenário | Entrada | Ação | Saída Esperada | Observações |
|---------|---------|------|----------------|-------------|
| Salário positivo | 2000 | chamar `calcularAdicionalPericulosidade` | 400 (20%) | Cálculo correto |
| Salário zero | 0 | chamar `calcularAdicionalPericulosidade` | 0 | Sem adicional |
| Salário negativo | -500 | chamar `calcularAdicionalPericulosidade` | erro tratado | Sistema valida |

---

## Método: calcularAdicionalInsalubridade(salarioMinimo, grau)

| Cenário | Entrada | Ação
