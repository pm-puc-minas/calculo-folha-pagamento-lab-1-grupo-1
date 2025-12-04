import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { createEmployee, fetchEmployees } from "@/store/slices/employeeSlice";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Separator } from "@/components/ui/separator";
import { Save, X, Eye } from "lucide-react";
import { toast } from "sonner";
import { EmployeeDto } from "@/types/api";

interface EmployeeRegistrationProps {
  onViewChange: (view: string) => void;
}

export const EmployeeRegistration = ({ onViewChange }: EmployeeRegistrationProps) => {
  const dispatch = useAppDispatch();
  const { isLoading, error } = useAppSelector((state) => state.employee);
  const [formData, setFormData] = useState({
    fullName: "",
    cpf: "",
    rg: "",
    position: "",
    grossSalary: "",
    weeklyHours: "40",
    dangerousWork: false,
    nonDangerousWork: false,
    hourlyRate: "0.00",
    monthlyHours: "0h",
    inss: "0.00",
    irrf: "0.00"
  });

  const [admissionDate, setAdmissionDate] = useState("");

  const [netSalary, setNetSalary] = useState("0.00");

  useEffect(() => {
    dispatch(fetchEmployees());
  }, [dispatch]);

  const handleInputChange = (field: string, value: string | boolean) => {
    const newFormData = { ...formData, [field]: value };
    setFormData(newFormData);
    
    // Calculate net salary when gross salary or deductions change
    if (field === 'grossSalary' || field === 'inss' || field === 'irrf') {
      calculateNetSalary(newFormData);
    }
  };

  const calculateNetSalary = (data: typeof formData) => {
    const gross = parseFloat(data.grossSalary) || 0;
    const inss = parseFloat(data.inss) || 0;
    const irrf = parseFloat(data.irrf) || 0;
    const net = gross - inss - irrf;
    setNetSalary(net.toFixed(2));
  };

  const handleSave = async () => {
    if (!formData.fullName || !formData.cpf || !formData.position || !formData.grossSalary || !admissionDate) {
      toast.error("Por favor, preencha todos os campos obrigatórios");
      return;
    }
    try {
      const salaryNumber = parseFloat(formData.grossSalary);
      const weeklyHoursNumber = parseInt(formData.weeklyHours, 10);

      const payload: EmployeeDto = {
        name: formData.fullName,
        cpf: formData.cpf,
        position: formData.position,
        department: "Geral",
        admissionDate,
        baseSalary: isNaN(salaryNumber) ? 0 : salaryNumber,
        dependents: 0,
        hasHazardPay: formData.dangerousWork,
        insalubrity: "NONE",
        transportVoucherValue: 0,
        mealVoucherDaily: 0,
        workDaysMonth: 22,
        weeklyHours: isNaN(weeklyHoursNumber) ? 40 : weeklyHoursNumber,
      };

      await dispatch(createEmployee(payload)).unwrap();
      toast.success("Funcionário salvo com sucesso!");
      setFormData({
        fullName: "",
        cpf: "",
        rg: "",
        position: "",
        grossSalary: "",
        weeklyHours: "40",
        dangerousWork: false,
        nonDangerousWork: false,
        hourlyRate: "0.00",
        monthlyHours: "0h",
        inss: "0.00",
        irrf: "0.00"
      });
      setAdmissionDate("");
      setNetSalary("0.00");
    } catch (err: any) {
      toast.error(err?.message || "Erro ao salvar funcionário");
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
          <div className="flex items-center space-x-2 bg-blue-50 px-3 py-2 rounded-lg">
            <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
              <span className="text-white text-sm font-medium">AU</span>
            </div>
            <span className="text-sm font-medium">Admin User</span>
          </div>
        </div>
      </header>

      {error && (
        <div className="mx-6 mt-4 text-sm text-destructive bg-destructive/10 border border-destructive/20 px-4 py-3 rounded">
          {error}
        </div>
      )}

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
                  onChange={(e) => handleInputChange("cpf", e.target.value)}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <Label htmlFor="rg">RG *</Label>
                <Input
                  id="rg"
                  placeholder="MG1234567"
                  value={formData.rg}
                  onChange={(e) => handleInputChange("rg", e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="admissionDate">Data de Admissão *</Label>
                <Input
                  id="admissionDate"
                  type="date"
                  value={admissionDate}
                  onChange={(e) => setAdmissionDate(e.target.value)}
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

            {/* Work Conditions */}
            <div>
              <h3 className="text-lg font-semibold mb-4">Condições de trabalho</h3>
              <div className="space-y-3">
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="dangerousWork"
                    checked={formData.dangerousWork}
                    onCheckedChange={(checked) => handleInputChange("dangerousWork", checked as boolean)}
                  />
                  <Label htmlFor="dangerousWork">Elegibilidade para Trabalho Perigoso</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="nonDangerousWork"
                    checked={formData.nonDangerousWork}
                    onCheckedChange={(checked) => handleInputChange("nonDangerousWork", checked as boolean)}
                  />
                  <Label htmlFor="nonDangerousWork">Elegibilidade para Trabalho Não Perigoso</Label>
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
                    Cálculo Base
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">Hourly Rate:</span>
                    <span className="font-medium">R$ {formData.hourlyRate}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-sm text-gray-600">Monthly Hours:</span>
                    <span className="font-medium">{formData.monthlyHours}</span>
                  </div>
                </CardContent>
              </Card>

              {/* Deductions */}
              <Card className="border-red-200">
                <CardHeader className="pb-3">
                  <CardTitle className="text-base text-red-700">Descontos</CardTitle>
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
                <Button onClick={handleSave} className="bg-blue-600 hover:bg-blue-700" disabled={isLoading}>
                  <Save className="w-4 h-4 mr-2" />
                  {isLoading ? "Salvando..." : "Salvar funcionário"}
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
