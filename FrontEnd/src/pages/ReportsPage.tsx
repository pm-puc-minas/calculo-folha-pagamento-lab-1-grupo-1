import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Calendar } from "lucide-react";
import { ReportHistory, ReportHistoryEntry } from "@/components/Reports/ReportHistory";

const ReportsPage = () => {
  const [items, setItems] = useState<ReportHistoryEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [month, setMonth] = useState("");
  const [employee, setEmployee] = useState("");
  const [type, setType] = useState<string | undefined>(undefined);

  useEffect(() => {
    // TODO: Integrar com API: GET /reports/history
    setItems([]);
  }, []);

  const handleGenerate = () => {
    // TODO: Integrar com API geração de relatório
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <Calendar className="w-5 h-5" />
            <span>Relatórios</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Input placeholder="Mês (YYYY-MM)" value={month} onChange={(e) => setMonth(e.target.value)} />
            <Input placeholder="Funcionário (nome ou ID)" value={employee} onChange={(e) => setEmployee(e.target.value)} />
            <Select onValueChange={setType}>
              <SelectTrigger>
                <SelectValue placeholder="Tipo de relatório" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="payroll">Folha de Pagamento</SelectItem>
                <SelectItem value="employee">Dados do Funcionário</SelectItem>
                <SelectItem value="summary">Resumido</SelectItem>
              </SelectContent>
            </Select>
            <Button onClick={handleGenerate}>Gerar Relatório</Button>
          </div>
        </CardContent>
      </Card>

      <ReportHistory items={items} loading={loading} />
    </div>
  );
};

export default ReportsPage;