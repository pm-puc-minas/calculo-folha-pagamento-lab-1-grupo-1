import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Separator } from "@/components/ui/separator";
import { Save, X, Eye } from "lucide-react";
import { toast } from "sonner";
import { UserBadge } from "@/components/Layout/UserBadge";
import { formatCPF } from "@/utils/formatters";
import { useAppDispatch } from "@/store/hooks";
import { createEmployee } from "@/store/slices/employeeSlice";
import { Employee } from "@/types/employee";

interface EmployeeRegistrationProps {
  onViewChange: (view: string) => void;
  user?: { username?: string | null; email?: string | null } | null;
}

export const EmployeeRegistration = ({ onViewChange, user }: EmployeeRegistrationProps) => {
  const dispatch = useAppDispatch();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    fullName: "",
    cpf: "",
    position: "",
    grossSalary: "",
    weeklyHours: "40",
    dangerousWork: false,
    unhealthyLevel: "none",
    transportVoucher: false,
    transportVoucherValue: "",
    mealVoucher: false,
    mealVoucherValue: "",
    healthPlan: false,
    healthPlanValue: "",
    dentalPlan: false,
    dentalPlanValue: "",
    gym: false,
    gymValue: "",
    timeBank: false,
    timeBankHours: "",
    overtime: false,
    overtimeHours: "",
    hourlyRate: "0.00",
    monthlyHours: "0h",
    inss: "0.00",
    irrf: "0.00",
    totalAdditions: "0.00",
    totalDiscounts: "0.00"
  });

  const [netSalary, setNetSalary] = useState("0.00");

  const handleInputChange = (field: string, value: string | boolean) => {
    const newFormData = { ...formData, [field]: value };
    setFormData(newFormData);
    
    // Calculate net salary for any change that affects calculation
    calculateNetSalary(newFormData);
  };

  const calculateNetSalary = (data: typeof formData) => {
    const gross = parseFloat(data.grossSalary) || 0;
    let additions = 0;
    let discounts = 0;

    // 1. Calculate Hourly Rate and Monthly Hours
    const weeklyHours = parseFloat(data.weeklyHours) || 40;
    const monthlyHours = weeklyHours * 5; // Simple approximation (weeks * 5) or (weekly / 6 * 30) -> Standard is 220 for 44h
    // Standard calculation: (Weekly / 6) * 30 = Monthly Hours. 
    // For 40h: (40/6)*30 = 200h. For 44h: 220h.
    const calculatedMonthlyHours = (weeklyHours / 6) * 30;
    const hourlyRate = calculatedMonthlyHours > 0 ? gross / calculatedMonthlyHours : 0;

    // 2. Additions
    // Periculosidade (30% do salário base)
    if (data.dangerousWork) {
      additions += gross * 0.30;
    }

    // Insalubridade (baseado no salário mínimo R$ 1.412,00 - assumindo base 2024)
    const minSalary = 1412.00;
    if (data.unhealthyLevel === 'low') additions += minSalary * 0.10;
    if (data.unhealthyLevel === 'medium') additions += minSalary * 0.20;
    if (data.unhealthyLevel === 'high') additions += minSalary * 0.40;

    // Horas Extras (50% por padrão)
    if (data.overtime && data.overtimeHours) {
      const otHours = parseFloat(data.overtimeHours) || 0;
      additions += (hourlyRate * 1.5) * otHours;
    }

    // 3. INSS Calculation (Progressive 2024)
    const inssBase = gross + additions;
    let inss = 0;
    
    if (inssBase <= 1412.00) {
      inss = inssBase * 0.075;
    } else if (inssBase <= 2666.68) {
      inss = (1412.00 * 0.075) + ((inssBase - 1412.00) * 0.09);
    } else if (inssBase <= 4000.03) {
      inss = (1412.00 * 0.075) + ((2666.68 - 1412.00) * 0.09) + ((inssBase - 2666.68) * 0.12);
    } else if (inssBase <= 7786.02) {
      inss = (1412.00 * 0.075) + ((2666.68 - 1412.00) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((inssBase - 4000.03) * 0.14);
    } else {
      // Teto
      inss = (1412.00 * 0.075) + ((2666.68 - 1412.00) * 0.09) + ((4000.03 - 2666.68) * 0.12) + ((7786.02 - 4000.03) * 0.14);
    }
    discounts += inss;

    // 4. IRRF Calculation (Progressive 2024)
    const irrfBase = inssBase - inss; // Simplificado (sem dependentes por enquanto)
    let irrf = 0;

    if (irrfBase <= 2259.20) {
      irrf = 0;
    } else if (irrfBase <= 2826.65) {
      irrf = (irrfBase * 0.075) - 169.44;
    } else if (irrfBase <= 3751.05) {
      irrf = (irrfBase * 0.15) - 381.44;
    } else if (irrfBase <= 4664.68) {
      irrf = (irrfBase * 0.225) - 662.77;
    } else {
      irrf = (irrfBase * 0.275) - 896.00;
    }
    if (irrf < 0) irrf = 0;
    discounts += irrf;

    // 5. Benefits Discounts
    // VT (até 6% do salário base, limitado ao valor do benefício se informado)
    if (data.transportVoucher) {
        const vtValue = parseFloat(data.transportVoucherValue) || 0;
        const maxDiscount = gross * 0.06;
        // Se valor do VT informado, desconto é menor entre 6% e valor. Se não informado valor, assume desconto de 6%.
        // O usuário pediu para inserir o valor. Assumindo que é o valor DO CUSTO ou DO DESCONTO?
        // Geralmente insere-se o custo. O desconto é limitado a 6%.
        // Vou assumir que o valor inserido é o CUSTO MENSAL.
        if (vtValue > 0) {
            discounts += Math.min(maxDiscount, vtValue);
        } else {
            // Se não tem valor, não desconta nada na simulação ou desconta 6% cheio?
            // Melhor assumir 0 se não preenchido.
        }
    }

    if (data.mealVoucher) {
        // VR geralmente tem desconto de até 20%. Vou usar o valor inserido como o valor de desconto direto se o usuário quiser, ou assumir desconto padrão.
        // Vou assumir que o valor inserido é o valor de DESCONTO MENSAL (coparticipação).
        discounts += parseFloat(data.mealVoucherValue) || 0;
    }

    if (data.healthPlan) discounts += parseFloat(data.healthPlanValue) || 0;
    if (data.dentalPlan) discounts += parseFloat(data.dentalPlanValue) || 0;
    if (data.gym) discounts += parseFloat(data.gymValue) || 0;

    const net = inssBase - discounts;

    // Update derived state in formData for UI display (keeping them as strings)
    setFormData(prev => ({
        ...prev,
        ...data, // Ensure we have latest inputs
        hourlyRate: hourlyRate.toFixed(2),
        monthlyHours: Math.round(calculatedMonthlyHours).toString() + "h",
        inss: inss.toFixed(2),
        irrf: irrf.toFixed(2),
        totalAdditions: additions.toFixed(2),
        totalDiscounts: discounts.toFixed(2)
    }));
    
    setNetSalary(net.toFixed(2));
  };

  const handleSave = async () => {
    if (!formData.fullName || !formData.cpf || !formData.position || !formData.grossSalary) {
      toast.error("Por favor, preencha todos os campos obrigatórios");
      return;
    }
    
    setIsSubmitting(true);

    try {
      // Map form data to Employee interface
      const employeeData: Employee = {
        name: formData.fullName,
        cpf: formData.cpf,
        position: formData.position,
        admissionDate: new Date().toISOString().split('T')[0], // Default to today
        grossSalary: parseFloat(formData.grossSalary),
        hoursPerDay: 8, // Default assumption based on 40h week
        daysPerWeek: 5, // Default assumption based on 40h week
        dependents: 0,
        transportVoucherValue: parseFloat(formData.transportVoucherValue) || 0,
        mealVoucherDaily: parseFloat(formData.mealVoucherValue) || 0,
        workDaysInMonth: 22,
        isDangerous: formData.dangerousWork,
        unhealthyLevel: formData.unhealthyLevel as 'none' | 'low' | 'medium' | 'high',
        pensionAlimony: 0,
        hasHealthPlan: formData.healthPlan,
        healthPlanValue: parseFloat(formData.healthPlanValue) || 0,
        hasDentalPlan: formData.dentalPlan,
        dentalPlanValue: parseFloat(formData.dentalPlanValue) || 0,
        hasGym: formData.gym,
        gymValue: parseFloat(formData.gymValue) || 0,
        hasTimeBank: formData.timeBank,
        timeBankHours: parseFloat(formData.timeBankHours) || 0,
        hasOvertime: formData.overtime,
        overtimeHours: parseFloat(formData.overtimeHours) || 0
      };

      const resultAction = await dispatch(createEmployee(employeeData));

      if (createEmployee.fulfilled.match(resultAction)) {
        toast.success("Funcionário salvo com sucesso!");
        // Reset form
        setFormData({
          fullName: "",
          cpf: "",
          position: "",
          grossSalary: "",
          weeklyHours: "40",
          dangerousWork: false,
          unhealthyLevel: "none",
          transportVoucher: false,
          mealVoucher: false,
          healthPlan: false,
          dentalPlan: false,
          gym: false,
          timeBank: false,
          overtime: false,
          hourlyRate: "0.00",
          monthlyHours: "0h",
          inss: "0.00",
          irrf: "0.00"
        });
        setNetSalary("0.00");
      } else {
        const msg = (resultAction.payload as string) || resultAction.error?.message || "Erro desconhecido";
        toast.error("Erro ao salvar funcionário: " + msg);
      }
    } catch (error) {
      console.error("Failed to save employee:", error);
      toast.error("Erro ao salvar funcionário");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    onViewChange("dashboard");
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen">
      {/* Header */}
      <header className="bg-white border-b px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Registro de Funcionários</h1>
            <p className="text-gray-600">Adicionar novos funcionários</p>
          </div>
          <UserBadge name={user?.username} email={user?.email} />
        </div>
      </header>

      {/* Main Content */}
      <main className="p-6">
        <Card>
          <CardHeader>
            <CardTitle>Informações do funcionário</CardTitle>
            <p className="text-sm text-gray-600">Preencha os detalhes do funcionário abaixo</p>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Basic Information */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="fullName">Nome completo*</Label>
                <Input
                  id="fullName"
                  placeholder="Enter full name"
                  value={formData.fullName}
                  onChange={(e) => handleInputChange("fullName", e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="cpf">CPF *</Label>
                <Input
                  id="cpf"
                  placeholder="000.000.000-00"
                  value={formData.cpf}
                  onChange={(e) => handleInputChange("cpf", formatCPF(e.target.value))}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="position">Cargo *</Label>
                <Input
                  id="position"
                  placeholder="Enter job title"
                  value={formData.position}
                  onChange={(e) => handleInputChange("position", e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="grossSalary">Salário Bruto(R$) *</Label>
                <Input
                  id="grossSalary"
                  placeholder="0.00"
                  type="number"
                  step="0.01"
                  value={formData.grossSalary}
                  onChange={(e) => handleInputChange("grossSalary", e.target.value)}
                />
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="weeklyHours">Horas trabalhadas (Semanais)*</Label>
              <Input
                id="weeklyHours"
                placeholder="40"
                type="number"
                value={formData.weeklyHours}
                onChange={(e) => handleInputChange("weeklyHours", e.target.value)}
              />
            </div>

            <Separator />

            {/* Benefits and Additionals */}
            <div>
              <h3 className="text-lg font-semibold mb-4">Adicionais e Benefícios</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {/* Adicionais Obrigatórios/Legais */}
                <div className="space-y-3">
                  <h4 className="font-medium text-gray-700">Adicionais Legais</h4>
                  <div className="flex items-center space-x-2">
                    <Checkbox
                      id="dangerousWork"
                      checked={formData.dangerousWork}
                      onCheckedChange={(checked) => handleInputChange("dangerousWork", checked as boolean)}
                    />
                    <Label htmlFor="dangerousWork">Periculosidade (30%)</Label>
                  </div>
                  <div className="space-y-2">
                     <Label htmlFor="unhealthyLevel">Insalubridade</Label>
                     <select 
                        id="unhealthyLevel"
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        value={formData.unhealthyLevel}
                        onChange={(e) => handleInputChange("unhealthyLevel", e.target.value)}
                     >
                        <option value="none">Nenhuma</option>
                        <option value="low">Baixa (10%)</option>
                        <option value="medium">Média (20%)</option>
                        <option value="high">Alta (40%)</option>
                     </select>
                  </div>
                </div>

                {/* Benefícios */}
                <div className="space-y-3">
                  <h4 className="font-medium text-gray-700">Benefícios (Valores em R$)</h4>
                  
                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="transportVoucher"
                        checked={formData.transportVoucher}
                        onCheckedChange={(checked) => handleInputChange("transportVoucher", checked as boolean)}
                      />
                      <Label htmlFor="transportVoucher">Vale Transporte</Label>
                    </div>
                    {formData.transportVoucher && (
                       <Input
                          type="number"
                          placeholder="Valor Mensal (Custo)"
                          value={formData.transportVoucherValue}
                          onChange={(e) => handleInputChange("transportVoucherValue", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="mealVoucher"
                        checked={formData.mealVoucher}
                        onCheckedChange={(checked) => handleInputChange("mealVoucher", checked as boolean)}
                      />
                      <Label htmlFor="mealVoucher">Vale Alimentação</Label>
                    </div>
                    {formData.mealVoucher && (
                       <Input
                          type="number"
                          placeholder="Valor Desconto"
                          value={formData.mealVoucherValue}
                          onChange={(e) => handleInputChange("mealVoucherValue", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="healthPlan"
                        checked={formData.healthPlan}
                        onCheckedChange={(checked) => handleInputChange("healthPlan", checked as boolean)}
                      />
                      <Label htmlFor="healthPlan">Plano de Saúde</Label>
                    </div>
                    {formData.healthPlan && (
                       <Input
                          type="number"
                          placeholder="Valor Mensal"
                          value={formData.healthPlanValue}
                          onChange={(e) => handleInputChange("healthPlanValue", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="dentalPlan"
                        checked={formData.dentalPlan}
                        onCheckedChange={(checked) => handleInputChange("dentalPlan", checked as boolean)}
                      />
                      <Label htmlFor="dentalPlan">Plano Odontológico</Label>
                    </div>
                    {formData.dentalPlan && (
                       <Input
                          type="number"
                          placeholder="Valor Mensal"
                          value={formData.dentalPlanValue}
                          onChange={(e) => handleInputChange("dentalPlanValue", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="gym"
                        checked={formData.gym}
                        onCheckedChange={(checked) => handleInputChange("gym", checked as boolean)}
                      />
                      <Label htmlFor="gym">Academia</Label>
                    </div>
                    {formData.gym && (
                       <Input
                          type="number"
                          placeholder="Valor Mensal"
                          value={formData.gymValue}
                          onChange={(e) => handleInputChange("gymValue", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>
                </div>

                {/* Outros Adicionais */}
                <div className="space-y-3">
                  <h4 className="font-medium text-gray-700">Outros Adicionais</h4>
                  
                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="timeBank"
                        checked={formData.timeBank}
                        onCheckedChange={(checked) => handleInputChange("timeBank", checked as boolean)}
                      />
                      <Label htmlFor="timeBank">Banco de Horas</Label>
                    </div>
                    {formData.timeBank && (
                       <Input
                          type="number"
                          placeholder="Saldo de Horas (Ex: 10)"
                          value={formData.timeBankHours}
                          onChange={(e) => handleInputChange("timeBankHours", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>

                  <div className="space-y-2">
                    <div className="flex items-center space-x-2">
                      <Checkbox
                        id="overtime"
                        checked={formData.overtime}
                        onCheckedChange={(checked) => handleInputChange("overtime", checked as boolean)}
                      />
                      <Label htmlFor="overtime">Elegível a Horas Extras</Label>
                    </div>
                    {formData.overtime && (
                       <Input
                          type="number"
                          placeholder="Horas Extras Mês (Simulação)"
                          value={formData.overtimeHours}
                          onChange={(e) => handleInputChange("overtimeHours", e.target.value)}
                          className="h-8"
                       />
                    )}
                  </div>
                </div>
              </div>
            </div>

            <Separator />

            {/* Calculation Sections */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Base Calculation */}
              <Card className="border-blue-200">
                <CardHeader className="pb-3">
                  <CardTitle className="text-base text-blue-700 flex items-center">
                    Cálculo Base (Simulado)
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">Salário Hora:</span>
                    <span className="font-medium">R$ {formData.hourlyRate}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">Horas Mensais:</span>
                    <span className="font-medium">{formData.monthlyHours}</span>
                  </div>
                  <div className="flex justify-between pt-2 border-t">
                    <span className="text-sm font-semibold text-gray-700">Total Adicionais:</span>
                    <span className="font-medium text-blue-600">+ R$ {formData.totalAdditions}</span>
                  </div>
                </CardContent>
              </Card>

              {/* Deductions */}
              <Card className="border-red-200">
                <CardHeader className="pb-3">
                  <CardTitle className="text-base text-red-700">Descontos (Simulado)</CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">INSS:</span>
                    <span className="font-medium">R$ {formData.inss}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">IRRF:</span>
                    <span className="font-medium">R$ {formData.irrf}</span>
                  </div>
                  <div className="flex justify-between pt-2 border-t">
                    <span className="text-sm font-semibold text-gray-700">Total Descontos:</span>
                    <span className="font-medium text-red-600">- R$ {formData.totalDiscounts}</span>
                  </div>
                </CardContent>
              </Card>

              {/* Net Salary */}
              <Card className="border-green-200">
                <CardHeader className="pb-3">
                  <CardTitle className="text-base text-green-700">Salário Líquido</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-green-600">R$ {netSalary}</div>
                    <div className="text-sm text-gray-500">After deductions</div>
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Action Buttons */}
            <div className="flex justify-between pt-6">
              <div className="flex space-x-3">
                <Button onClick={handleSave} disabled={isSubmitting} className="bg-blue-600 hover:bg-blue-700">
                  <Save className="w-4 h-4 mr-2" />
                  {isSubmitting ? "Salvando..." : "Salvar funcionário"}
                </Button>
                <Button variant="outline">
                  <Eye className="w-4 h-4 mr-2" />
                  Contracheque
                </Button>
              </div>
              <Button variant="outline" onClick={handleCancel}>
                <X className="w-4 h-4 mr-2" />
                Cancelar
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  );
};
