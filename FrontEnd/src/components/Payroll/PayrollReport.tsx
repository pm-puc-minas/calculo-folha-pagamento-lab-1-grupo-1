import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { FileText, Download, User, Calendar, Building } from "lucide-react";
import { PayrollCalculation } from "@/types/employee";
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { toast } from "@/hooks/use-toast";

interface PayrollReportProps {
  calculation: PayrollCalculation;
}

export const PayrollReport = ({ calculation }: PayrollReportProps) => {
  const { employee } = calculation;
  
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const formatPercentage = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'percent',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(value);
  };

  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString('pt-BR');
  };

  const formatMonth = (monthStr: string) => {
    if (!monthStr || !monthStr.includes('-')) {
      return 'Mês inválido';
    }
    
    const [year, month] = monthStr.split('-');
    const yearNum = parseInt(year);
    const monthNum = parseInt(month);
    
    if (isNaN(yearNum) || isNaN(monthNum) || monthNum < 1 || monthNum > 12) {
      return 'Mês inválido';
    }
    
    const date = new Date(yearNum, monthNum - 1);
    return new Intl.DateTimeFormat('pt-BR', { 
      year: 'numeric', 
      month: 'long' 
    }).format(date);
  };

  const handlePrint = () => {
    window.print();
  };

  const handleExportPDF = async () => {
    try {
      // Show loading toast
      toast({
        title: "Gerando PDF",
        description: "Por favor, aguarde enquanto o PDF é gerado...",
      });

      // Create the PDF
      const pdf = new jsPDF('p', 'mm', 'a4');
      const element = document.getElementById('payroll-report-content');
      
      if (!element) {
        throw new Error('Elemento do relatório não encontrado');
      }

      // Generate canvas from HTML element
      const canvas = await html2canvas(element, {
        scale: 2,
        useCORS: true,
        allowTaint: true,
        backgroundColor: '#ffffff',
        height: element.scrollHeight,
        width: element.scrollWidth
      });

      const imgData = canvas.toDataURL('image/png');
      const imgWidth = 210; // A4 width in mm
      const pageHeight = 297; // A4 height in mm
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;

      let position = 0;

      // Add first page
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      // Add additional pages if needed
      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      // Generate filename
      const filename = `folha-pagamento-${calculation.employee.name.replace(/\s+/g, '-').toLowerCase()}-${calculation.referenceMonth}.pdf`;
      
      // Download the PDF
      pdf.save(filename);

      toast({
        title: "PDF gerado com sucesso!",
        description: `O relatório foi baixado como "${filename}"`,
      });
    } catch (error) {
      console.error('Erro ao gerar PDF:', error);
      toast({
        title: "Erro ao gerar PDF",
        description: "Ocorreu um erro durante a geração do PDF. Tente novamente.",
        variant: "destructive"
      });
    }
  };

  return (
    <div className="space-y-6" id="payroll-report-content">
      {/* Header */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-12 h-12 bg-gradient-to-br from-primary to-accent rounded-lg flex items-center justify-center">
                <FileText className="w-6 h-6 text-white" />
              </div>
              <div>
                <CardTitle className="text-2xl">Demonstrativo de Pagamento</CardTitle>
                <CardDescription>
                  Folha de Pagamento - {formatMonth(calculation.referenceMonth)}
                </CardDescription>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <Button 
                onClick={handleExportPDF} 
                className="bg-gradient-to-r from-primary to-accent text-white hover:opacity-90"
              >
                <Download className="w-4 h-4 mr-2" />
                <span>Exportar PDF</span>
              </Button>
              <Button 
                onClick={handlePrint} 
                variant="outline"
                className="hidden md:flex items-center space-x-2"
              >
                <FileText className="w-4 h-4" />
                <span>Imprimir</span>
              </Button>
            </div>
          </div>
        </CardHeader>
      </Card>

      {/* Employee Information */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <User className="w-5 h-5" />
            <span>Dados do Funcionário</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Nome Completo</p>
                <p className="text-lg font-semibold">{employee.name}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-muted-foreground">CPF</p>
                <p className="font-mono">{employee.cpf}</p>
              </div>
            </div>
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Cargo</p>
                <p className="font-semibold">{employee.position}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-muted-foreground">Data de Admissão</p>
                <p>{formatDate(employee.admissionDate)}</p>
              </div>
            </div>
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium text-muted-foreground">Mês de Referência</p>
                <Badge className="bg-primary text-primary-foreground">
                  <Calendar className="w-3 h-3 mr-1" />
                  {formatMonth(calculation.referenceMonth)}
                </Badge>
              </div>
              <div>
                <p className="text-sm font-medium text-muted-foreground">Dependentes</p>
                <p>{employee.dependents}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Salary Breakdown */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Income */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-lg text-success">💰 Proventos</CardTitle>
            <CardDescription>Valores que compõem o salário bruto</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center">
              <span>Salário Base</span>
              <span className="font-semibold text-success">{formatCurrency(employee.grossSalary)}</span>
            </div>
            
            {calculation.dangerousBonus > 0 && (
              <div className="flex justify-between items-center">
                <span>Adicional de Periculosidade (30%)</span>
                <span className="font-semibold text-success">{formatCurrency(calculation.dangerousBonus)}</span>
              </div>
            )}
            
            {calculation.unhealthyBonus > 0 && (
              <div className="flex justify-between items-center">
                <span>Adicional de Insalubridade</span>
                <span className="font-semibold text-success">{formatCurrency(calculation.unhealthyBonus)}</span>
              </div>
            )}
            
            <Separator />
            <div className="flex justify-between items-center font-bold text-lg">
              <span>Total Bruto</span>
              <span className="text-success">{formatCurrency(calculation.grossTotal)}</span>
            </div>
          </CardContent>
        </Card>

        {/* Deductions */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-lg text-destructive">📉 Descontos</CardTitle>
            <CardDescription>Valores descontados do salário</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between items-center">
              <div>
                <span>INSS</span>
                <span className="text-xs text-muted-foreground ml-1">
                  ({formatPercentage(calculation.inssEffectiveRate)})
                </span>
              </div>
              <span className="font-semibold text-destructive">-{formatCurrency(calculation.inssDiscount)}</span>
            </div>
            
            {calculation.irpfDiscount > 0 && (
              <div className="flex justify-between items-center">
                <div>
                  <span>IRRF</span>
                  <span className="text-xs text-muted-foreground ml-1">
                    ({formatPercentage(calculation.irpfEffectiveRate)})
                  </span>
                </div>
                <span className="font-semibold text-destructive">-{formatCurrency(calculation.irpfDiscount)}</span>
              </div>
            )}
            
            {calculation.transportVoucherDiscount > 0 && (
              <div className="flex justify-between items-center">
                <span>Vale Transporte (máx. 6%)</span>
                <span className="font-semibold text-destructive">-{formatCurrency(calculation.transportVoucherDiscount)}</span>
              </div>
            )}
            
            <Separator />
            <div className="flex justify-between items-center font-bold text-lg">
              <span>Total Descontos</span>
              <span className="text-destructive">
                -{formatCurrency(calculation.inssDiscount + calculation.irpfDiscount + calculation.transportVoucherDiscount)}
              </span>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Benefits */}
      {(calculation.transportVoucher > 0 || calculation.mealVoucher > 0) && (
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-lg text-accent">🎁 Benefícios</CardTitle>
            <CardDescription>Benefícios concedidos ao funcionário</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {calculation.transportVoucher > 0 && (
                <div className="space-y-2">
                  <p className="font-medium">Vale Transporte</p>
                  <div className="text-sm text-muted-foreground">
                    <p>Valor concedido: {formatCurrency(calculation.transportVoucher)}</p>
                    <p>Desconto aplicado: {formatCurrency(calculation.transportVoucherDiscount)}</p>
                  </div>
                </div>
              )}
              
              {calculation.mealVoucher > 0 && (
                <div className="space-y-2">
                  <p className="font-medium">Vale Alimentação</p>
                  <div className="text-sm text-muted-foreground">
                    <p>Valor mensal: {formatCurrency(calculation.mealVoucher)}</p>
                    <p>Diário: {formatCurrency(employee.mealVoucherDaily)} × {employee.workDaysInMonth} dias</p>
                  </div>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Summary and Details */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Final Summary */}
        <Card className="border-0 shadow-lg bg-gradient-to-br from-primary/5 to-accent/5 border-primary/20">
          <CardHeader>
            <CardTitle className="text-xl">💼 Resumo Final</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div className="flex justify-between items-center text-lg">
                <span className="font-medium">Salário Bruto</span>
                <span className="font-bold text-success">{formatCurrency(calculation.grossTotal)}</span>
              </div>
              <div className="flex justify-between items-center text-lg">
                <span className="font-medium">Total Descontos</span>
                <span className="font-bold text-destructive">
                  -{formatCurrency(calculation.inssDiscount + calculation.irpfDiscount + calculation.transportVoucherDiscount)}
                </span>
              </div>
              <Separator className="bg-primary/20" />
              <div className="flex justify-between items-center text-2xl font-bold">
                <span>Salário Líquido</span>
                <span className="text-primary">{formatCurrency(calculation.netSalary)}</span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Calculation Details */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-lg">⏰ Detalhes do Cálculo</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Horas por dia:</span>
                <span className="font-medium">{employee.hoursPerDay}h</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Dias por semana:</span>
                <span className="font-medium">{employee.daysPerWeek} dias</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Horas semanais:</span>
                <span className="font-medium">{calculation.weeklyHours}h</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Horas mensais:</span>
                <span className="font-medium">{calculation.monthlyHours.toFixed(1)}h</span>
              </div>
              <Separator />
              <div className="flex justify-between">
                <span className="text-muted-foreground">Valor da hora:</span>
                <span className="font-semibold text-primary">{formatCurrency(calculation.hourlyWage)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Base INSS:</span>
                <span className="font-medium">{formatCurrency(calculation.inssCalculationBase)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">Base IRPF:</span>
                <span className="font-medium">{formatCurrency(calculation.irpfCalculationBase)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">FGTS (patronal):</span>
                <span className="font-medium text-accent">{formatCurrency(calculation.fgts)}</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Action buttons for mobile */}
      <div className="md:hidden space-y-2">
        <Button 
          onClick={handleExportPDF} 
          className="w-full bg-gradient-to-r from-primary to-accent"
        >
          <Download className="w-4 h-4 mr-2" />
          Exportar PDF
        </Button>
        <Button 
          onClick={handlePrint} 
          variant="outline"
          className="w-full"
        >
          <FileText className="w-4 h-4 mr-2" />
          Imprimir Demonstrativo
        </Button>
      </div>
    </div>
  );
};