import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Calendar, FileText } from "lucide-react";
import { ReportHistory, ReportHistoryEntry } from "@/components/Reports/ReportHistory";
import { EmployeeCombobox } from "@/components/Reports/EmployeeCombobox";
import { reportService } from "@/services/reportService";
import { employeeService } from "@/services/employeeService";
import { useToast } from "@/hooks/use-toast";

const ReportsPage = () => {
  const [items, setItems] = useState<ReportHistoryEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [month, setMonth] = useState(new Date().toISOString().slice(0, 7));
  const [employeeId, setEmployeeId] = useState("");
  const [type, setType] = useState<string>("payroll");
  const [totalEmployees, setTotalEmployees] = useState(0);
  const { toast } = useToast();

  const fetchHistory = async () => {
    setLoading(true);
    try {
      const data = await reportService.getHistory(month);
      setItems(data);
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "Erro ao carregar histórico",
        description: error?.message || "Não foi possível carregar o histórico de relatórios. Verifique a conexão e tente novamente.",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const loadEmployees = async () => {
      try {
        const data = await employeeService.search("");
        setTotalEmployees(data.length);
      } catch (e) {
        console.error("Failed to load employees count", e);
      }
    };
    loadEmployees();
  }, []);

  useEffect(() => {
    fetchHistory();
  }, [month]);

  const handleGenerate = async () => {
    const faltantes: string[] = [];
    if (!employeeId) faltantes.push("Funcionário");
    if (!month) faltantes.push("Mês de referência");
    if (!type) faltantes.push("Tipo de relatório");

    if (faltantes.length > 0) {
      toast({
        variant: "destructive",
        title: "Campos obrigatórios",
        description: `Preencha: ${faltantes.join(", ")}.`,
      });
      return;
    }

    try {
      await reportService.generate({
        employeeId,
        referenceMonth: month,
        reportType: type,
      });
      toast({
        title: "Sucesso",
        description: "Relatório gerado com sucesso!",
      });
      fetchHistory();
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "Erro",
        description: error?.message || "Falha ao gerar o relatório. Tente novamente.",
      });
    }
  };

  const handleDownload = async (id: string) => {
    try {
      await reportService.download(id);
      toast({
        title: "Download iniciado",
        description: "O arquivo está sendo baixado.",
      });
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "Erro no download",
        description: error?.message || "Não foi possível baixar o relatório.",
      });
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await reportService.delete(id);
      toast({
        title: "Relatório excluído",
        description: "O relatório foi removido do histórico.",
      });
      fetchHistory();
    } catch (error: any) {
      toast({
        variant: "destructive",
        title: "Erro na exclusão",
        description: error?.message || "Não foi possível excluir o relatório.",
      });
    }
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <FileText className="w-5 h-5" />
            <span>Gerar Relatório</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
            <div className="space-y-2">
              <label className="text-sm font-medium">Mês de Referência</label>
              <Input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Funcionário</label>
              <EmployeeCombobox value={employeeId} onChange={setEmployeeId} />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Tipo de Relatório</label>
              <Select value={type} onValueChange={setType}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o tipo" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="payroll">Folha de Pagamento</SelectItem>
                  <SelectItem value="employee">Dados do Funcionário</SelectItem>
                  <SelectItem value="summary">Resumido</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <Button onClick={handleGenerate} className="w-full">
              Gerar Relatório
            </Button>
          </div>
        </CardContent>
      </Card>

      <ReportHistory
        items={items}
        totalEmployees={totalEmployees}
        loading={loading}
        onDownload={handleDownload}
        onDelete={handleDelete}
      />
    </div>
  );
};

export default ReportsPage;
