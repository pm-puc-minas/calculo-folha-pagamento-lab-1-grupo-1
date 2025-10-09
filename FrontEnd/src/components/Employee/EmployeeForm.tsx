import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Users, Plus, Edit } from "lucide-react";
import { Employee } from "@/types/employee";
import { useToast } from "@/hooks/use-toast";

interface EmployeeFormProps {
  onAddEmployee: (employee: Employee) => void;
  employees: Employee[];
  onSelectEmployee: (employee: Employee) => void;
}

export const EmployeeForm = ({ onAddEmployee, employees, onSelectEmployee }: EmployeeFormProps) => {
  const [formData, setFormData] = useState<Partial<Employee>>({
    name: "",
    cpf: "",
    position: "",
    admissionDate: "",
    grossSalary: 0,
    hoursPerDay: 8,
    daysPerWeek: 5,
    dependents: 0,
    transportVoucherValue: 0,
    mealVoucherDaily: 0,
    workDaysInMonth: 22,
    isDangerous: false,
    unhealthyLevel: "none",
    pensionAlimony: 0
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { toast } = useToast();

  const handleInputChange = (field: keyof Employee, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name || !formData.cpf || !formData.position || !formData.admissionDate) {
      toast({
        title: "Campos obrigatórios",
        description: "Por favor, preencha todos os campos obrigatórios.",
        variant: "destructive",
      });
      return;
    }

    setIsSubmitting(true);

    const employee: Employee = {
      id: Date.now().toString(),
      name: formData.name || "",
      cpf: formData.cpf || "",
      position: formData.position || "",
      admissionDate: formData.admissionDate || "",
      grossSalary: formData.grossSalary || 0,
      hoursPerDay: formData.hoursPerDay || 8,
      daysPerWeek: formData.daysPerWeek || 5,
      dependents: formData.dependents || 0,
      transportVoucherValue: formData.transportVoucherValue || 0,
      mealVoucherDaily: formData.mealVoucherDaily || 0,
      workDaysInMonth: formData.workDaysInMonth || 22,
      isDangerous: formData.isDangerous || false,
      unhealthyLevel: formData.unhealthyLevel || "none",
      pensionAlimony: formData.pensionAlimony || 0
    };

    onAddEmployee(employee);
    
    toast({
      title: "Funcionário cadastrado com sucesso!",
      description: `${employee.name} foi adicionado ao sistema.`,
    });

    // Reset form
    setFormData({
      name: "",
      cpf: "",
      position: "",
      admissionDate: "",
      grossSalary: 0,
      hoursPerDay: 8,
      daysPerWeek: 5,
      dependents: 0,
      transportVoucherValue: 0,
      mealVoucherDaily: 0,
      workDaysInMonth: 22,
      isDangerous: false,
      unhealthyLevel: "none",
      pensionAlimony: 0
    });
    
    setIsSubmitting(false);
  };

  return (
    <div className="space-y-6">
      {/* Employee List */}
      {employees.length > 0 && (
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <Users className="w-5 h-5" />
              <span>Funcionários Cadastrados ({employees.length})</span>
            </CardTitle>
            <CardDescription>
              Lista de todos os funcionários cadastrados no sistema
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {employees.map((employee) => (
                <Card 
                  key={employee.id} 
                  className="cursor-pointer hover:shadow-md transition-all border border-border/50 hover:border-primary/30"
                  onClick={() => onSelectEmployee(employee)}
                >
                  <CardContent className="p-4">
                    <div className="space-y-2">
                      <div className="flex items-start justify-between">
                        <h4 className="font-semibold text-sm">{employee.name}</h4>
                        <Edit className="w-3 h-3 text-muted-foreground" />
                      </div>
                      <p className="text-xs text-muted-foreground">{employee.position}</p>
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-medium text-success">
                          R$ {employee.grossSalary.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                        </span>
                        <Badge variant="outline" className="text-xs">
                          {employee.admissionDate}
                        </Badge>
                      </div>
                      {(employee.isDangerous || employee.unhealthyLevel !== 'none') && (
                        <div className="flex space-x-1">
                          {employee.isDangerous && (
                            <Badge variant="secondary" className="text-xs">Periculosidade</Badge>
                          )}
                          {employee.unhealthyLevel !== 'none' && (
                            <Badge variant="outline" className="text-xs">
                              Insalubridade {employee.unhealthyLevel}
                            </Badge>
                          )}
                        </div>
                      )}
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Employee Form */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Plus className="w-5 h-5" />
            <span>Cadastrar Novo Funcionário</span>
          </CardTitle>
          <CardDescription>
            Preencha os dados do funcionário para cadastro no sistema
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Dados Pessoais */}
            <div className="space-y-4">
              <h3 className="text-lg font-semibold flex items-center">
                📋 Dados Pessoais
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="name">Nome Completo *</Label>
                  <Input
                    id="name"
                    value={formData.name}
                    onChange={(e) => handleInputChange('name', e.target.value)}
                    placeholder="Nome completo do funcionário"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cpf">CPF *</Label>
                  <Input
                    id="cpf"
                    value={formData.cpf}
                    onChange={(e) => handleInputChange('cpf', e.target.value)}
                    placeholder="000.000.000-00"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="position">Cargo *</Label>
                  <Input
                    id="position"
                    value={formData.position}
                    onChange={(e) => handleInputChange('position', e.target.value)}
                    placeholder="Cargo do funcionário"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="admissionDate">Data de Admissão *</Label>
                  <Input
                    id="admissionDate"
                    type="date"
                    value={formData.admissionDate}
                    onChange={(e) => handleInputChange('admissionDate', e.target.value)}
                    required
                  />
                </div>
              </div>
            </div>

            <Separator />

            {/* Dados Salariais */}
            <div className="space-y-4">
              <h3 className="text-lg font-semibold flex items-center">
                💰 Dados Salariais e Jornada
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="grossSalary">Salário Bruto (R$)</Label>
                  <Input
                    id="grossSalary"
                    type="number"
                    step="0.01"
                    value={formData.grossSalary}
                    onChange={(e) => handleInputChange('grossSalary', parseFloat(e.target.value) || 0)}
                    placeholder="0.00"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="hoursPerDay">Horas por Dia</Label>
                  <Input
                    id="hoursPerDay"
                    type="number"
                    value={formData.hoursPerDay}
                    onChange={(e) => handleInputChange('hoursPerDay', parseInt(e.target.value) || 0)}
                    placeholder="8"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="daysPerWeek">Dias por Semana</Label>
                  <Input
                    id="daysPerWeek"
                    type="number"
                    value={formData.daysPerWeek}
                    onChange={(e) => handleInputChange('daysPerWeek', parseInt(e.target.value) || 0)}
                    placeholder="5"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="workDaysInMonth">Dias Úteis no Mês</Label>
                  <Input
                    id="workDaysInMonth"
                    type="number"
                    value={formData.workDaysInMonth}
                    onChange={(e) => handleInputChange('workDaysInMonth', parseInt(e.target.value) || 0)}
                    placeholder="22"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="dependents">Dependentes</Label>
                  <Input
                    id="dependents"
                    type="number"
                    value={formData.dependents}
                    onChange={(e) => handleInputChange('dependents', parseInt(e.target.value) || 0)}
                    placeholder="0"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="pensionAlimony">Pensão Alimentícia (R$)</Label>
                  <Input
                    id="pensionAlimony"
                    type="number"
                    step="0.01"
                    value={formData.pensionAlimony}
                    onChange={(e) => handleInputChange('pensionAlimony', parseFloat(e.target.value) || 0)}
                    placeholder="0.00"
                  />
                </div>
              </div>
            </div>

            <Separator />

            {/* Adicionais e Benefícios */}
            <div className="space-y-4">
              <h3 className="text-lg font-semibold flex items-center">
                🎁 Benefícios e Adicionais
              </h3>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div className="flex items-center space-x-2">
                    <Checkbox
                      id="isDangerous"
                      checked={formData.isDangerous}
                      onCheckedChange={(checked) => handleInputChange('isDangerous', checked)}
                    />
                    <Label htmlFor="isDangerous">Trabalho com Periculosidade (30%)</Label>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="unhealthyLevel">Nível de Insalubridade</Label>
                    <Select 
                      value={formData.unhealthyLevel} 
                      onValueChange={(value) => handleInputChange('unhealthyLevel', value)}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="none">Sem Insalubridade</SelectItem>
                        <SelectItem value="low">Baixo (10%)</SelectItem>
                        <SelectItem value="medium">Médio (20%)</SelectItem>
                        <SelectItem value="high">Alto (40%)</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="transportVoucherValue">Vale Transporte Mensal (R$)</Label>
                    <Input
                      id="transportVoucherValue"
                      type="number"
                      step="0.01"
                      value={formData.transportVoucherValue}
                      onChange={(e) => handleInputChange('transportVoucherValue', parseFloat(e.target.value) || 0)}
                      placeholder="0.00"
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="mealVoucherDaily">Vale Alimentação Diário (R$)</Label>
                    <Input
                      id="mealVoucherDaily"
                      type="number"
                      step="0.01"
                      value={formData.mealVoucherDaily}
                      onChange={(e) => handleInputChange('mealVoucherDaily', parseFloat(e.target.value) || 0)}
                      placeholder="0.00"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div className="flex justify-end space-x-4 pt-6">
              <Button 
                type="submit"
                disabled={isSubmitting}
                className="bg-gradient-to-r from-primary to-accent hover:from-primary-hover hover:to-accent/90 px-8"
              >
                {isSubmitting ? "Cadastrando..." : "Cadastrar Funcionário"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};