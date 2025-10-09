import { useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Calculator, FileText, User } from "lucide-react";
import { Employee, PayrollCalculation } from "@/types/employee";
import { PayrollCalculator as PayrollCalc } from "@/utils/payrollCalculations";
import { useToast } from "@/hooks/use-toast";

interface PayrollCalculatorProps {
  employees: Employee[];
  onCalculate: (calculation: PayrollCalculation) => void;
}

export const PayrollCalculator = ({ employees, onCalculate }: PayrollCalculatorProps) => {
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<string>("");
  const [referenceMonth, setReferenceMonth] = useState(
    new Intl.DateTimeFormat('pt-BR', { year: 'numeric', month: '2-digit' }).format(new Date())
  );
  const [isCalculating, setIsCalculating] = useState(false);
  const { toast } = useToast();

  const selectedEmployee = employees.find(emp => emp.id === selectedEmployeeId);

  const handleCalculate = async () => {
    if (!selectedEmployee) {
      toast({
        title: "Selecione um funcionário",
        description: "É necessário selecionar um funcionário para calcular a folha.",
        variant: "destructive",
      });
      return;
    }

    if (!referenceMonth) {
      toast({
        title: "Mês de referência obrigatório",
        description: "É necessário informar o mês de referência para o cálculo.",
        variant: "destructive",
      });
      return;
    }

    setIsCalculating(true);

    // Simular um pequeno delay para melhor UX
    setTimeout(() => {
      const calculation = PayrollCalc.calculatePayroll(selectedEmployee, referenceMonth);
      onCalculate(calculation);
      
      toast({
        title: "Cálculo realizado com sucesso!",
        description: `Folha de pagamento de ${selectedEmployee.name} calculada para ${referenceMonth}.`,
      });
      
      setIsCalculating(false);
    }, 1500);
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  return (
    <div className="space-y-6">
      {/* Calculation Form */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calculator className="w-5 h-5" />
            <span>Calcular Folha de Pagamento</span>
          </CardTitle>
          <CardDescription>
            Selecione o funcionário e o mês de referência para calcular a folha de pagamento
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {employees.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              <User className="w-12 h-12 mx-auto mb-4 opacity-50" />
              <p>Nenhum funcionário cadastrado.</p>
              <p className="text-sm">Cadastre funcionários primeiro para calcular a folha.</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <Label htmlFor="employee">Funcionário</Label>
                  <Select value={selectedEmployeeId} onValueChange={setSelectedEmployeeId}>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione um funcionário" />
                    </SelectTrigger>
                    <SelectContent>
                      {employees.map((employee) => (
                        <SelectItem key={employee.id} value={employee.id}>
                          <div className="flex items-center space-x-2">
                            <span>{employee.name}</span>
                            <Badge variant="outline" className="text-xs">
                              {employee.position}
                            </Badge>
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="referenceMonth">Mês de Referência</Label>
                  <Select value={referenceMonth} onValueChange={setReferenceMonth}>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione o mês" />
                    </SelectTrigger>
                    <SelectContent>
                      {Array.from({ length: 12 }, (_, i) => {
                        const date = new Date();
                        date.setMonth(i);
                        const value = new Intl.DateTimeFormat('pt-BR', { year: 'numeric', month: '2-digit' }).format(date);
                        const label = new Intl.DateTimeFormat('pt-BR', { year: 'numeric', month: 'long' }).format(date);
                        return (
                          <SelectItem key={value} value={value}>
                            {label}
                          </SelectItem>
                        );
                      })}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              {selectedEmployee && (
                <Card className="bg-muted/30 border-primary/20">
                  <CardHeader className="pb-3">
                    <CardTitle className="text-lg">Dados do Funcionário Selecionado</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                      <div>
                        <p className="text-sm font-medium text-muted-foreground">Nome</p>
                        <p className="font-semibold">{selectedEmployee.name}</p>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-muted-foreground">Cargo</p>
                        <p className="font-semibold">{selectedEmployee.position}</p>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-muted-foreground">Salário Bruto</p>
                        <p className="font-semibold text-success">{formatCurrency(selectedEmployee.grossSalary)}</p>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-muted-foreground">Dependentes</p>
                        <p className="font-semibold">{selectedEmployee.dependents}</p>
                      </div>
                    </div>

                    {/* Adicionais */}
                    {(selectedEmployee.isDangerous || selectedEmployee.unhealthyLevel !== 'none') && (
                      <div className="pt-2">
                        <p className="text-sm font-medium text-muted-foreground mb-2">Adicionais</p>
                        <div className="flex space-x-2">
                          {selectedEmployee.isDangerous && (
                            <Badge className="bg-warning text-warning-foreground">
                              Periculosidade (30%)
                            </Badge>
                          )}
                          {selectedEmployee.unhealthyLevel !== 'none' && (
                            <Badge variant="outline">
                              Insalubridade {selectedEmployee.unhealthyLevel} (
                              {selectedEmployee.unhealthyLevel === 'low' ? '10%' : 
                               selectedEmployee.unhealthyLevel === 'medium' ? '20%' : '40%'})
                            </Badge>
                          )}
                        </div>
                      </div>
                    )}

                    {/* Benefícios */}
                    {(selectedEmployee.transportVoucherValue > 0 || selectedEmployee.mealVoucherDaily > 0) && (
                      <div className="pt-2">
                        <p className="text-sm font-medium text-muted-foreground mb-2">Benefícios</p>
                        <div className="grid grid-cols-2 gap-4 text-sm">
                          {selectedEmployee.transportVoucherValue > 0 && (
                            <div>
                              <span className="text-muted-foreground">Vale Transporte:</span>
                              <span className="ml-1 font-medium">{formatCurrency(selectedEmployee.transportVoucherValue)}</span>
                            </div>
                          )}
                          {selectedEmployee.mealVoucherDaily > 0 && (
                            <div>
                              <span className="text-muted-foreground">Vale Alimentação:</span>
                              <span className="ml-1 font-medium">{formatCurrency(selectedEmployee.mealVoucherDaily)}/dia</span>
                            </div>
                          )}
                        </div>
                      </div>
                    )}
                  </CardContent>
                </Card>
              )}

              <div className="flex justify-end">
                <Button 
                  onClick={handleCalculate}
                  disabled={!selectedEmployee || !referenceMonth || isCalculating}
                  className="bg-gradient-to-r from-primary to-accent hover:from-primary-hover hover:to-accent/90 px-8"
                >
                  {isCalculating ? (
                    <>
                      <Calculator className="w-4 h-4 mr-2 animate-spin" />
                      Calculando...
                    </>
                  ) : (
                    <>
                      <FileText className="w-4 h-4 mr-2" />
                      Calcular Folha de Pagamento
                    </>
                  )}
                </Button>
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
};