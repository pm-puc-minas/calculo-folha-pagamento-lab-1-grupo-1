import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { Plus, Minus, Play, Save, FileDown } from "lucide-react";
import { toast } from "sonner";
import { UserBadge } from "@/components/Layout/UserBadge";

interface PayrollCalculationProps {
  user?: { username?: string | null; email?: string | null } | null;
}

export const PayrollCalculation = ({ user }: PayrollCalculationProps) => {
  const [selectedEmployee, setSelectedEmployee] = useState("");
  const [payrollPeriod, setPayrollPeriod] = useState("");
  const [grossSalary, setGrossSalary] = useState("5000.00");
  const [workHours, setWorkHours] = useState("220");
  const [hourlyRate, setHourlyRate] = useState("22.73");

  // Additionals
  const [additionals, setAdditionals] = useState([
    { type: "Horas extras", amount: 10, value: 341.00 },
    { type: "Subsídio de turno noturno", amount: 200, value: 200.00 },
    { type: "Pagamento de risco", amount: 150, value: 150.00 },
    { type: "Subsídio de Risco de Saúde", amount: 100, value: 100.00 }
  ]);

  // Benefits
  const [benefits, setBenefits] = useState([
    { type: "Auxílio-transporte", amount: 120, value: 120.00 },
    { type: "Subsídio de refeição", amount: 180, value: 180.00 }
  ]);

  // Deductions
  const [deductions, setDeductions] = useState([
    { type: "INSS (8%)", value: 400.00 },
    { type: "FGTS (8%)", value: 400.00 },
    { type: "IRRF (15%)", value: 750.00 },
    { type: "Ausências e atrasos", amount: 50, value: 50.00 }
  ]);

  const totalAdditionals = additionals.reduce((sum, item) => sum + item.value, 0);
  const totalBenefits = benefits.reduce((sum, item) => sum + item.value, 0);
  const totalDeductions = deductions.reduce((sum, item) => sum + item.value, 0);
  const baseSalary = 5000.00;
  const netSalary = baseSalary + totalAdditionals - totalDeductions;

  const handleGeneratePayroll = () => {
    toast.success("Folha de pagamento gerada com sucesso!");
  };

  const handleSave = () => {
    toast.success("Cálculo salvo com sucesso!");
  };

  const handleExportPDF = () => {
    toast.success("PDF exportado com sucesso!");
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen">
      {/* Header */}
      <header className="bg-white border-b px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Cálculo da Folha de Pagamento</h1>
            <p className="text-gray-600">Cálculo automático da folha de pagamento</p>
          </div>
          <div className="flex items-center space-x-4">
            <Button onClick={handleExportPDF} className="bg-red-600 hover:bg-red-700">
              <FileDown className="w-4 h-4 mr-2" />
              Export PDF
            </Button>
            <UserBadge name={user?.username} email={user?.email} />
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="p-6 space-y-6">
        {/* Employee Selection */}
        <Card>
          <CardHeader>
            <CardTitle>Informações do funcionário</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="space-y-2">
              <Label>Selecione Funcionário</Label>
              <Select value={selectedEmployee} onValueChange={setSelectedEmployee}>
                <SelectTrigger>
                  <SelectValue placeholder="John Smith - ID: 001" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="john-smith">John Smith - ID: 001</SelectItem>
                  <SelectItem value="maria-silva">Maria Silva - ID: 002</SelectItem>
                  <SelectItem value="pedro-santos">Pedro Santos - ID: 003</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Período de pagamento</Label>
              <Select value={payrollPeriod} onValueChange={setPayrollPeriod}>
                <SelectTrigger>
                  <SelectValue placeholder="January 2024" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="jan-2024">January 2024</SelectItem>
                  <SelectItem value="feb-2024">February 2024</SelectItem>
                  <SelectItem value="mar-2024">March 2024</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Salário Bruto</Label>
              <Input value={grossSalary} onChange={(e) => setGrossSalary(e.target.value)} />
            </div>
          </CardContent>
        </Card>

        {/* Hourly Calculation */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Cálculo do Salário por Hora</CardTitle>
            <div className="bg-blue-100 p-2 rounded-full">
              <Play className="w-4 h-4 text-blue-600" />
            </div>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-right">
              <div className="text-sm text-gray-600">Horas de trabalho/mês:</div>
              <div className="text-xl font-bold">{workHours} horas</div>
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-600">Taxa horária:</div>
              <div className="text-xl font-bold text-green-600">R$ {hourlyRate}</div>
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-600">Salário Bruto</div>
              <div className="text-xl font-bold text-green-600">R$ {grossSalary}</div>
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Additionals */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-green-700">Adicionais</CardTitle>
              <div className="bg-green-100 p-2 rounded-full">
                <Plus className="w-4 h-4 text-green-600" />
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {additionals.map((item, index) => (
                <div key={index} className="flex justify-between items-center">
                  <div>
                    <div className="font-medium">{item.type}:</div>
                    {item.amount && <div className="text-sm text-gray-600">{item.amount}</div>}
                  </div>
                  <div className="text-green-600 font-bold">R$ {item.value.toFixed(2)}</div>
                </div>
              ))}
              <Separator />
              <div className="flex justify-between items-center font-bold text-lg">
                <span>Total adicionais</span>
                <span className="text-green-600">R$ {totalAdditionals.toFixed(2)}</span>
              </div>
            </CardContent>
          </Card>

          {/* Deductions */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-red-700">Descontos</CardTitle>
              <div className="bg-red-100 p-2 rounded-full">
                <Minus className="w-4 h-4 text-red-600" />
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {deductions.map((item, index) => (
                <div key={index} className="flex justify-between items-center">
                  <div>
                    <div className="font-medium">{item.type}:</div>
                    {item.amount && <div className="text-sm text-gray-600">{item.amount}</div>}
                  </div>
                  <div className="text-red-600 font-bold">-R$ {item.value.toFixed(2)}</div>
                </div>
              ))}
              <Separator />
              <div className="flex justify-between items-center font-bold text-lg">
                <span>Total Descontos:</span>
                <span className="text-red-600">-R$ {totalDeductions.toFixed(2)}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Benefits */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-blue-700">Benefícios</CardTitle>
            <div className="bg-blue-100 p-2 rounded-full">
              <Plus className="w-4 h-4 text-blue-600" />
            </div>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {benefits.map((item, index) => (
              <div key={index} className="flex justify-between items-center">
                <div>
                  <div className="font-medium">{item.type}:</div>
                  <div className="text-sm text-gray-600">{item.amount}</div>
                </div>
                <div className="text-blue-600 font-bold">R$ {item.value.toFixed(2)}</div>
              </div>
            ))}
            <div className="md:col-span-2">
              <Separator />
              <div className="flex justify-between items-center font-bold text-lg mt-2">
                <span>Total Benefícios:</span>
                <span className="text-blue-600">R$ {totalBenefits.toFixed(2)}</span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Summary */}
        <Card className="bg-gradient-to-r from-blue-50 to-purple-50">
          <CardHeader>
            <CardTitle className="text-center">Resumo da folha de pagamento</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
              <div className="bg-white p-4 rounded-lg">
                <div className="text-2xl font-bold text-gray-900">R$ {baseSalary.toFixed(2)}</div>
                <div className="text-sm text-gray-600">Base Salary</div>
              </div>
              <div className="bg-white p-4 rounded-lg">
                <div className="text-2xl font-bold text-green-600">+R$ {totalAdditionals.toFixed(2)}</div>
                <div className="text-sm text-gray-600">Total Adicionais</div>
              </div>
              <div className="bg-white p-4 rounded-lg">
                <div className="text-2xl font-bold text-red-600">-R$ {totalDeductions.toFixed(2)}</div>
                <div className="text-sm text-gray-600">Total Descontos</div>
              </div>
              <div className="bg-blue-600 text-white p-4 rounded-lg">
                <div className="text-2xl font-bold">R$ {netSalary.toFixed(2)}</div>
                <div className="text-sm">Salário Líquido</div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Action Buttons */}
        <div className="flex justify-center space-x-4">
          <Button onClick={handleGeneratePayroll} className="bg-green-600 hover:bg-green-700">
            <Play className="w-4 h-4 mr-2" />
            Gerar Folha
          </Button>
          <Button onClick={handleSave} variant="outline">
            <Save className="w-4 h-4 mr-2" />
            Salvar
          </Button>
          <Button onClick={handleExportPDF} className="bg-red-600 hover:bg-red-700">
            <FileDown className="w-4 h-4 mr-2" />
            Exportar PDF
          </Button>
        </div>
      </main>
    </div>
  );
};
