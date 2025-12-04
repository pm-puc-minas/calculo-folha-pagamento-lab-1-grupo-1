import { useEffect, useMemo, useRef, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import { Plus, Minus, Play, Save, FileDown } from "lucide-react";
import { toast } from "sonner";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchEmployees } from "@/store/slices/employeeSlice";
import { calculatePayroll, fetchPayrollById, fetchPayrolls } from "@/store/slices/payrollSlice";
import jsPDF from "jspdf";

export const PayrollCalculation = () => {
  const dispatch = useAppDispatch();
  const employees = useAppSelector((s) => s.employee.employees);
  const payrolls = useAppSelector((s) => s.payroll.payrolls);
  const selectedPayroll = useAppSelector((s) => s.payroll.selectedPayroll);
  const [selectedEmployee, setSelectedEmployee] = useState("");
  const [payrollPeriod, setPayrollPeriod] = useState("");
  const pdfAreaRef = useRef<HTMLDivElement | null>(null);
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

  const dynamicAdditionals = useMemo(() => {
    if (!selectedPayroll) return [] as { type: string; amount?: number; value: number }[];
    const items: { type: string; amount?: number; value: number }[] = [];
    if (selectedPayroll.hazardPayValue) items.push({ type: "Adicional de Periculosidade", value: selectedPayroll.hazardPayValue });
    if (selectedPayroll.insalubrityValue) items.push({ type: "Adicional de Insalubridade", value: selectedPayroll.insalubrityValue });
    if (selectedPayroll.mealVoucherValue) items.push({ type: "Vale Alimentação", value: selectedPayroll.mealVoucherValue });
    return items;
  }, [selectedPayroll]);

  const dynamicDeductions = useMemo(() => {
    if (!selectedPayroll) return [] as { type: string; value: number }[];
    const items: { type: string; value: number }[] = [];
    if (selectedPayroll.inssDiscount) items.push({ type: "INSS", value: selectedPayroll.inssDiscount });
    if (selectedPayroll.irrfDiscount) items.push({ type: "IRRF", value: selectedPayroll.irrfDiscount });
    if (selectedPayroll.transportVoucherDiscount) items.push({ type: "Vale Transporte", value: selectedPayroll.transportVoucherDiscount });
    if (selectedPayroll.fgtsValue) items.push({ type: "FGTS", value: selectedPayroll.fgtsValue });
    return items;
  }, [selectedPayroll]);

  const totalAdditionals = dynamicAdditionals.reduce((sum, item) => sum + item.value, 0);
  const totalDeductions = dynamicDeductions.reduce((sum, item) => sum + item.value, 0);
  const baseSalary = selectedPayroll?.inssBase ?? 5000.0;
  const netSalary = selectedPayroll?.netSalary ?? (baseSalary + totalAdditionals - totalDeductions);
  const [filterMonth, setFilterMonth] = useState("");
  const [minNet, setMinNet] = useState("");
  const [maxNet, setMaxNet] = useState("");
  const filteredPayrolls = useMemo(() => {
    return payrolls.filter(p => {
      const monthOk = !filterMonth || filterMonth === "all" || p.month === filterMonth;
      const minOk = !minNet || p.netSalary >= parseFloat(minNet);
      const maxOk = !maxNet || p.netSalary <= parseFloat(maxNet);
      return monthOk && minOk && maxOk;
    });
  }, [payrolls, filterMonth, minNet, maxNet]);

  useEffect(() => {
    dispatch(fetchEmployees());
    dispatch(fetchPayrolls());
  }, [dispatch]);

  useEffect(() => {
    const emp = employees.find(e => `${e.id}` === selectedEmployee);
    if (emp) {
      const salary = typeof emp.baseSalary === "number" ? emp.baseSalary : Number(emp.baseSalary);
      const monthlyHours = Math.round((typeof emp.weeklyHours === "number" ? emp.weeklyHours : Number(emp.weeklyHours)) * 5);
      if (!Number.isNaN(salary)) setGrossSalary(salary.toFixed(2));
      if (monthlyHours > 0) setWorkHours(String(monthlyHours));
      if (!Number.isNaN(salary) && monthlyHours > 0) setHourlyRate((salary / monthlyHours).toFixed(2));
    }
  }, [selectedEmployee, employees]);

  useEffect(() => {
    const gs = parseFloat(grossSalary);
    const hours = Number(workHours);
    if (!Number.isNaN(gs) && hours > 0) {
      setHourlyRate((gs / hours).toFixed(2));
    }
  }, [grossSalary, workHours]);

  const handleGeneratePayroll = async () => {
    try {
      const emp = employees.find(e => `${e.id}` === selectedEmployee);
      if (!emp || !payrollPeriod) {
        toast.error("Selecione funcionário e período");
        return;
      }
      await dispatch(calculatePayroll({ employeeId: emp.id as number, referenceMonth: payrollPeriod })).unwrap();
      toast.success("Folha de pagamento gerada com sucesso!");
    } catch (err: any) {
      toast.error(err?.message || "Erro ao calcular folha");
    }
  };

  const handleSave = async () => {
    try {
      if (!selectedPayroll?.id) {
        toast.error("Nenhum cálculo selecionado para salvar");
        return;
      }
      await dispatch(fetchPayrollById(selectedPayroll.id)).unwrap();
      toast.success("Cálculo salvo e verificado no banco com sucesso!");
    } catch (err: any) {
      toast.error(err?.message || "Falha ao verificar salvamento");
    }
  };

  const handleExportPDF = async () => {
    try {
      if (!selectedPayroll) {
        toast.error("Nenhum cálculo selecionado. Gere a folha primeiro.");
        return;
      }
      const p = selectedPayroll;
      const pdf = new jsPDF("p", "mm", "a4");

      const marginLeft = 15;
      let y = 20;
      const lh = 7;

      pdf.setFontSize(18);
      pdf.text("Folha de Pagamento", marginLeft, y);
      y += lh * 2;

      pdf.setFontSize(12);
      pdf.text(`Funcionário: ${p.employeeName} (ID: ${p.employeeId})`, marginLeft, y); y += lh;
      pdf.text(`Mês de referência: ${p.month}`, marginLeft, y); y += lh;
      if (p.calculatedAt) { pdf.text(`Calculado em: ${p.calculatedAt}`, marginLeft, y); y += lh; }
      y += lh;

      pdf.setFontSize(14);
      pdf.text("Bases de cálculo", marginLeft, y); y += lh;
      pdf.setFontSize(12);
      pdf.text(`Base INSS: R$ ${Number(p.inssBase).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Base IRRF: R$ ${Number(p.irrfBase).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Base FGTS: R$ ${Number(p.fgtsBase).toFixed(2)}`, marginLeft, y); y += lh * 2;

      pdf.setFontSize(14);
      pdf.text("Proventos", marginLeft, y); y += lh;
      pdf.setFontSize(12);
      pdf.text(`Adic. Periculosidade: R$ ${Number(p.hazardPayValue).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Adic. Insalubridade: R$ ${Number(p.insalubrityValue).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Vale Alimentação: R$ ${Number(p.mealVoucherValue).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Total Proventos: R$ ${Number(p.totalEarnings).toFixed(2)}`, marginLeft, y); y += lh * 2;

      pdf.setFontSize(14);
      pdf.text("Descontos", marginLeft, y); y += lh;
      pdf.setFontSize(12);
      pdf.text(`Vale Transporte: R$ ${Number(p.transportVoucherDiscount).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`INSS: R$ ${Number(p.inssDiscount).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`IRRF: R$ ${Number(p.irrfDiscount).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Total Descontos: R$ ${Number(p.totalDeductions).toFixed(2)}`, marginLeft, y); y += lh * 2;

      pdf.setFontSize(14);
      pdf.text("Contribuições", marginLeft, y); y += lh;
      pdf.setFontSize(12);
      pdf.text(`FGTS: R$ ${Number(p.fgtsValue).toFixed(2)}`, marginLeft, y); y += lh * 2;

      pdf.setFontSize(14);
      pdf.text("Resumo", marginLeft, y); y += lh;
      pdf.setFontSize(12);
      pdf.text(`Taxa Horária: R$ ${Number(p.hourlyRate).toFixed(2)}`, marginLeft, y); y += lh;
      pdf.text(`Salário Líquido: R$ ${Number(p.netSalary).toFixed(2)}`, marginLeft, y); y += lh;

      const filename = `folha_${p.employeeName}_${p.month}.pdf`;
      pdf.save(filename);
      toast.success("PDF exportado com dados calculados!");
    } catch (err: any) {
      toast.error(err?.message || "Erro ao exportar PDF");
    }
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
            <div className="flex items-center space-x-2 bg-blue-50 px-3 py-2 rounded-lg">
              <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">JA</span>
              </div>
              <span className="text-sm font-medium">John Admin</span>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="p-6 space-y-6" ref={pdfAreaRef}>
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
                  <SelectValue placeholder="Selecione" />
                </SelectTrigger>
                <SelectContent>
                  {employees.map((e) => (
                    <SelectItem key={e.id} value={`${e.id}`}>{e.name} - ID: {e.id}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Período de pagamento</Label>
              <Select value={payrollPeriod} onValueChange={setPayrollPeriod}>
                <SelectTrigger>
                  <SelectValue placeholder="2024-11" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="2024-10">2024-10</SelectItem>
                  <SelectItem value="2024-11">2024-11</SelectItem>
                  <SelectItem value="2024-12">2024-12</SelectItem>
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
              <div className="text-xl font-bold text-green-600">R$ {(selectedPayroll?.hourlyRate ?? parseFloat(hourlyRate)).toFixed(2)}</div>
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-600">Salário Bruto</div>
              <div className="text-xl font-bold text-green-600">R$ {Number(selectedPayroll?.inssBase ?? parseFloat(grossSalary)).toFixed(2)}</div>
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
              {dynamicAdditionals.map((item, index) => (
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
              {dynamicDeductions.map((item, index) => (
                <div key={index} className="flex justify-between items-center">
                  <div>
                    <div className="font-medium">{item.type}:</div>
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

        {/* Filtros e lista */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-blue-700">Filtrar cálculos</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="space-y-2">
              <Label>Mês</Label>
              <Select value={filterMonth} onValueChange={setFilterMonth}>
                <SelectTrigger>
                  <SelectValue placeholder="Todos" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos</SelectItem>
                  <SelectItem value="2024-10">2024-10</SelectItem>
                  <SelectItem value="2024-11">2024-11</SelectItem>
                  <SelectItem value="2024-12">2024-12</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Salário líquido mínimo</Label>
              <Input value={minNet} onChange={(e) => setMinNet(e.target.value)} placeholder="0" />
            </div>
            <div className="space-y-2">
              <Label>Salário líquido máximo</Label>
              <Input value={maxNet} onChange={(e) => setMaxNet(e.target.value)} placeholder="10000" />
            </div>
            <div className="flex items-end">
              <Button variant="outline" onClick={() => { setFilterMonth(""); setMinNet(""); setMaxNet(""); }}>Limpar filtros</Button>
            </div>
            <div className="md:col-span-4">
              <Separator />
              <div className="mt-4 space-y-2">
                {filteredPayrolls.map((p) => (
                  <div key={p.id} className="flex justify-between bg-white rounded p-3 border">
                    <div className="font-medium">{p.employeeName}</div>
                    <div className="text-sm text-gray-600">{p.month}</div>
                    <div className="font-bold">R$ {p.netSalary.toFixed(2)}</div>
                  </div>
                ))}
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
