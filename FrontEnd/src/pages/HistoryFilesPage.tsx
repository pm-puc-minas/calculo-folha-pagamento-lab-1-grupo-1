
import { useEffect, useState } from "react";
import { ReportHistory, ReportHistoryEntry } from "@/components/Reports/ReportHistory";
import { reportService } from "@/services/reportService";
import { employeeService } from "@/services/employeeService";
import { useToast } from "@/hooks/use-toast";

const HistoryFilesPage = () => {
  const [items, setItems] = useState<ReportHistoryEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalEmployees, setTotalEmployees] = useState(0);
  const { toast } = useToast();

  const loadHistory = async () => {
    setLoading(true);
    try {
      const data = await reportService.getHistory();
      setItems(data);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Erro ao carregar histórico",
        description: "Não foi possível carregar os relatórios.",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory();
    employeeService
      .search("")
      .then((res) => setTotalEmployees(res.length))
      .catch(() => setTotalEmployees(0));
  }, []);

  const handleDownload = async (id: string) => {
    try {
      await reportService.download(id);
      toast({ title: "Download iniciado", description: "O arquivo está sendo baixado." });
    } catch (error) {
      toast({ variant: "destructive", title: "Erro no download", description: "Não foi possível baixar o relatório." });
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await reportService.delete(id);
      toast({ title: "Relatório excluído", description: "Removido do histórico." });
      loadHistory();
    } catch (error) {
      toast({ variant: "destructive", title: "Erro", description: "Não foi possível excluir o relatório." });
    }
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
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

export default HistoryFilesPage;
