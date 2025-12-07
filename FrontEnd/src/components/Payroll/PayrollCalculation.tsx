import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { Plus, Minus, Play, Save, FileDown, Calculator, Check, ChevronsUpDown, CalendarIcon } from "lucide-react";
import { toast } from "sonner";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import { UserBadge } from "@/components/Layout/UserBadge";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchEmployees } from "@/store/slices/employeeSlice";
import { calculatePayroll, setCalculationInProgress } from "@/store/slices/payrollSlice";
import { cn } from "@/lib/utils";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";

interface PayrollCalculationProps {
  user?: { username?: string | null; email?: string | null } | null;
}

export const PayrollCalculation = ({ user }: PayrollCalculationProps) => {
  const dispatch = useAppDispatch();
  const { employees } = useAppSelector((state) => state.employee);
  const { calculationInProgress } = useAppSelector((state) => state.payroll);

  const [selectedEmployeeId, setSelectedEmployeeId] = useState("");
  const [payrollPeriod, setPayrollPeriod] = useState("");
  const [grossSalary, setGrossSalary] = useState("0.00");
  const [openEmployee, setOpenEmployee] = useState(false);
  const [date, setDate] = useState<Date>();
  
  // Calculation Results State
  const [calculationResult, setCalculationResult] = useState<any>(null);

  useEffect(() => {
    dispatch(fetchEmployees());
  }, [dispatch]);

  useEffect(() => {
    if (selectedEmployeeId) {
      const emp = employees.find(e => e.id?.toString() === selectedEmployeeId);
      if (emp) {
        setGrossSalary(emp.grossSalary.toFixed(2));
      }
    }
  }, [selectedEmployeeId, employees]);

  useEffect(() => {
    if (date) {
      setPayrollPeriod(format(date, "yyyy-MM"));
    }
  }, [date]);

  const handleCalculate = async () => {
    if (!selectedEmployeeId || !payrollPeriod) {
      toast.error("Por favor, selecione um funcionário e o período.");
      return;
    }

    try {
      const resultAction = await dispatch(calculatePayroll({ 
        employeeId: parseInt(selectedEmployeeId), 
        referenceMonth: payrollPeriod 
      }));

      if (calculatePayroll.fulfilled.match(resultAction)) {
        setCalculationResult(resultAction.payload);
        toast.success("Cálculo realizado com sucesso!");
      } else {
        toast.error("Erro ao calcular folha de pagamento.");
      }
    } catch (error) {
      toast.error("Erro ao calcular folha de pagamento.");
      console.error(error);
    }
  };

  const handleGeneratePayroll = () => {
    toast.success("Folha de pagamento gerada com sucesso!");
  };

  const handleSave = () => {
    toast.success("Cálculo salvo com sucesso!");
  };

  const handleExportPDF = async () => {
    if (!calculationResult) {
      toast.error("Calcule a folha antes de exportar o PDF.");
      return;
    }

    try {
      toast.success("Gerando PDF...");
      const element = document.getElementById("payroll-calculation-content");
      if (!element) {
        throw new Error("Conteúdo do cálculo não encontrado.");
      }

      const canvas = await html2canvas(element, { scale: 2, useCORS: true, backgroundColor: "#ffffff" });
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF("p", "mm", "a4");
      const imgWidth = 210;
      const pageHeight = 297;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;

      pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      const filename = `calculo-folha-${calculationResult.employee?.name || "funcionario"}-${payrollPeriod || "mes"}.pdf`;
      pdf.save(filename);
      toast.success("PDF exportado com sucesso!");
    } catch (err: any) {
      console.error(err);
      toast.error(err?.message || "Erro ao exportar PDF.");
    }
  };

  // Derived values for display
  const workHours = "220"; // Standard monthly hours
  const hourlyRate = calculationResult?.hourlyRate 
    ? calculationResult.hourlyRate.toFixed(2) 
    : (parseFloat(grossSalary) / 220).toFixed(2);

  const additionals = calculationResult ? [
    { type: "Periculosidade", value: calculationResult.hazardPayValue },
    { type: "Insalubridade", value: calculationResult.insalubrityValue },
    { type: "Vale Alimentação", value: calculationResult.mealVoucherValue },
    { type: "Horas Extras", value: calculationResult.overtimeValue || 0 }
  ].filter(item => item.value > 0) : [];

  const benefits = calculationResult ? [
    // Benefits that are discounts
    { type: "Plano de Saúde", value: calculationResult.healthPlanDiscount || 0 },
    { type: "Plano Odontológico", value: calculationResult.dentalPlanDiscount || 0 },
    { type: "Academia", value: calculationResult.gymDiscount || 0 }
  ].filter(item => item.value > 0) : [];

  const deductions = calculationResult ? [
    { type: "INSS", value: calculationResult.inssDiscount },
    { type: "IRRF", value: calculationResult.irrfDiscount },
    { type: "Vale Transporte", value: calculationResult.transportVoucherDiscount },
    { type: "FGTS", value: calculationResult.fgtsValue },
    ...benefits
  ].filter(item => item.value > 0) : [];

  const totalAdditionals = calculationResult ? calculationResult.totalEarnings - parseFloat(grossSalary) : 0; // Approximate
  const totalDeductions = calculationResult ? calculationResult.totalDeductions : 0;
  const netSalary = calculationResult ? calculationResult.netSalary : 0;
  const baseSalary = parseFloat(grossSalary);

  return (
    <div className="flex-1 bg-gray-50 min-h-screen" id="payroll-calculation-content">
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
            <div className="space-y-2 flex flex-col">
              <Label className="mb-2">Selecione Funcionário</Label>
              <Popover open={openEmployee} onOpenChange={setOpenEmployee}>
                <PopoverTrigger asChild>
                  <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={openEmployee}
                    className="justify-between w-full"
                  >
                    {selectedEmployeeId
                      ? employees.find((emp) => emp.id?.toString() === selectedEmployeeId)?.name
                      : "Selecione um funcionário..."}
                    <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-[300px] p-0">
                  <Command>
                    <CommandInput placeholder="Pesquisar funcionário..." />
                    <CommandList>
                      <CommandEmpty>Nenhum funcionário encontrado.</CommandEmpty>
                      <CommandGroup>
                        {employees.map((emp) => (
                          <CommandItem
                            key={emp.id}
                            value={emp.name}
                            onSelect={() => {
                              setSelectedEmployeeId(emp.id?.toString() || "");
                              setOpenEmployee(false);
                            }}
                          >
                            <Check
                              className={cn(
                                "mr-2 h-4 w-4",
                                selectedEmployeeId === emp.id?.toString() ? "opacity-100" : "opacity-0"
                              )}
                            />
                            {emp.name} - CPF: {emp.cpf}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    </CommandList>
                  </Command>
                </PopoverContent>
              </Popover>
            </div>
            <div className="space-y-2 flex flex-col">
              <Label className="mb-2">Período de pagamento</Label>
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant={"outline"}
                    className={cn(
                      "w-full justify-start text-left font-normal",
                      !date && "text-muted-foreground"
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {date ? format(date, "MMMM yyyy", { locale: ptBR }) : <span>Selecione o período</span>}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar
                    mode="single"
                    selected={date}
                    onSelect={setDate}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
            <div className="space-y-2">
              <Label>Salário Bruto</Label>
              <Input value={grossSalary} onChange={(e) => setGrossSalary(e.target.value)} disabled />
            </div>
          </CardContent>
        </Card>

        {/* Hourly Calculation */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Cálculo do Salário por Hora</CardTitle>
            <Button onClick={handleCalculate} disabled={calculationInProgress} className="bg-blue-600 hover:bg-blue-700">
              {calculationInProgress ? "Calculando..." : "Calcular"}
              <Calculator className="w-4 h-4 ml-2" />
            </Button>
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
              <div className="text-xl font-bold text-green-600">R$ {parseFloat(grossSalary).toFixed(2)}</div>
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
                  </div>
                  <div className="text-green-600 font-bold">R$ {item.value.toFixed(2)}</div>
                </div>
              ))}
              {additionals.length === 0 && <div className="text-gray-500 text-sm">Nenhum adicional.</div>}
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
                  </div>
                  <div className="text-red-600 font-bold">-R$ {item.value.toFixed(2)}</div>
                </div>
              ))}
              {deductions.length === 0 && <div className="text-gray-500 text-sm">Nenhum desconto.</div>}
              <Separator />
              <div className="flex justify-between items-center font-bold text-lg">
                <span>Total Descontos:</span>
                <span className="text-red-600">-R$ {totalDeductions.toFixed(2)}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Benefits Section - Optional separate view if needed, but they are included in deductions */}
        {benefits.length > 0 && (
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle className="text-blue-700">Detalhamento de Benefícios (Descontos)</CardTitle>
              <div className="bg-blue-100 p-2 rounded-full">
                <Plus className="w-4 h-4 text-blue-600" />
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
               {benefits.map((item, index) => (
                <div key={index} className="flex justify-between items-center">
                  <div>
                    <div className="font-medium">{item.type}:</div>
                  </div>
                  <div className="text-blue-600 font-bold">R$ {item.value.toFixed(2)}</div>
                </div>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Summary */}
        <Card className="bg-gradient-to-r from-blue-50 to-purple-50">
          <CardHeader>
            <CardTitle className="text-center">Resumo da folha de pagamento</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
              <div className="bg-white p-4 rounded-lg">
                <div className="text-2xl font-bold text-gray-900">R$ {baseSalary.toFixed(2)}</div>
                <div className="text-sm text-gray-600">Salário Base</div>
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
