# Diagrama UML – Strategy de Descontos

Separei a refatoração assim:
- **Interfaces e contratos**: `DiscountStrategy` define o contrato; `DiscountType` enumera INSS, IRRF e VT.

- **Contexto**: `DiscountCalculationContext` reúne dados de entrada (salário bruto, dependentes, INSS prévio, VT etc.).

- **Estratégias concretas**: `InssDiscountStrategy`, `IrrfDiscountStrategy`, `TransportDiscountStrategy` cada uma calcula só o seu desconto.

- **Orquestração**: `PayrollService` injeta a lista de estratégias, monta o contexto e delega: `strategy(INSS)`, depois `strategy(IRRF)`, depois `strategy(TRANSPORT)`. O resultado alimenta `PayrollCalculation`.

- **Entidades**: `Employee` fornece dados (salário, dependentes, VT), `PayrollCalculation` armazena bruto, líquido e descontos calculados.


