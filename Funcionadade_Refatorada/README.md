# Refatoração com Strategy (descontos da folha)

Escolhi o padrão **Strategy** para isolar o cálculo de descontos (INSS, IRRF e Vale Transporte). Antes, a lógica ficava toda em `PayrollService`, difícil de evoluir ou trocar. Com estratégias, cada regra fica em uma classe pequena e plugável.

### Classes criadas/modificadas
- Criadas: `discount/DiscountStrategy`, `DiscountType`, `DiscountCalculationContext`, `InssDiscountStrategy`, `IrrfDiscountStrategy`, `TransportDiscountStrategy`.
- Modificada: `PayrollService` passou a injetar a lista de estratégias e delegar os descontos para elas.

### Trechos de código
- Injeção e uso em `PayrollService`:
```java
@Autowired
private List<DiscountStrategy> discountStrategies;
private Map<DiscountType, DiscountStrategy> discountStrategyMap;

private DiscountStrategy strategy(DiscountType type) {
    if (discountStrategyMap == null) {
        discountStrategyMap = discountStrategies.stream()
                .collect(Collectors.toMap(DiscountStrategy::getType, s -> s));
    }
    return discountStrategyMap.get(type);
}
```

- Aplicando as estratégias no cálculo:
```java
DiscountCalculationContext ctx = new DiscountCalculationContext()
        .setGrossSalary(grossSalary)
        .setDependents(dependents)
        .setTransportEnabled(Boolean.TRUE.equals(employee.getTransportVoucher()))
        .setTransportVoucherValue(transportValue);

BigDecimal inssDiscount = strategy(DiscountType.INSS).calculate(ctx);
ctx.setInssDiscount(inssDiscount);
BigDecimal irrfDiscount = strategy(DiscountType.IRRF).calculate(ctx);
BigDecimal transportDiscount = strategy(DiscountType.TRANSPORT).calculate(ctx);
```

Agora cada desconto pode ser alterado ou substituído sem mexer no serviço principal, e novos descontos podem ser adicionados criando outra implementação de `DiscountStrategy`.
