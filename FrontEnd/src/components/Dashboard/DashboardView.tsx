import { useEffect, useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from "recharts";
import { 
  Users, 
  Calendar, 
  Clock, 
  UserPlus, 
  Calculator, 
  FileText, 
  Settings,
  TrendingUp,
  AlertCircle
} from "lucide-react";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchDashboardData } from "@/store/slices/dashboardSlice";

interface DashboardViewProps {
  onViewChange: (view: string) => void;
  onLogout: () => void;
}

export const DashboardView = ({ onViewChange, onLogout }: DashboardViewProps) => {
  const dispatch = useAppDispatch();
  const { stats, isLoading, error, recentPayrolls, recentEmployees } = useAppSelector((state) => state.dashboard);

  useEffect(() => {
    dispatch(fetchDashboardData());
  }, [dispatch]);

  const chartData = useMemo(() => {
    if (!recentPayrolls || recentPayrolls.length === 0) return [];
    const byMonth: Record<string, number> = {};
    recentPayrolls.forEach((p: any) => {
      const key = p.month || "N/A";
      byMonth[key] = (byMonth[key] || 0) + 1;
    });
    return Object.entries(byMonth).map(([month, count]) => ({
      month,
      payrolls: count,
      processed: count,
      pending: 0,
    }));
  }, [recentPayrolls]);

  const salaryData = useMemo(() => {
    if (!recentEmployees || recentEmployees.length === 0) return [];
    const buckets = {
      low: { name: "Até 3k", value: 0, fill: "#3b82f6" },
      mid: { name: "3k - 5k", value: 0, fill: "#8b5cf6" },
      upper: { name: "5k - 8k", value: 0, fill: "#ec4899" },
      high: { name: "Acima de 8k", value: 0, fill: "#f59e0b" },
    };
    recentEmployees.forEach((e: any) => {
      const salary = typeof e.baseSalary === "number" ? e.baseSalary : Number(e.baseSalary || 0);
      if (salary <= 3000) buckets.low.value += 1;
      else if (salary <= 5000) buckets.mid.value += 1;
      else if (salary <= 8000) buckets.upper.value += 1;
      else buckets.high.value += 1;
    });
    return Object.values(buckets);
  }, [recentEmployees]);

  return (
    <div className="flex-1 bg-gray-50 min-h-screen">
      {/* Header */}
      <header className="bg-white border-b px-6 py-4">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Painel</h1>
            <p className="text-gray-600">Visão geral do sistema</p>
          </div>
          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 bg-blue-50 px-3 py-2 rounded-lg">
              <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                <span className="text-white text-sm font-medium">JA</span>
              </div>
              <span className="text-sm font-medium">John Admin</span>
            </div>
            <Button variant="outline" onClick={onLogout}>
              Sair
            </Button>
          </div>
        </div>
      </header>

      {/* Alert */}
      {error && (
        <div className="mx-6 mt-4">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        </div>
      )}

      {/* Main Content */}
      <main className="p-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total de funcionários</p>
                  <p className="text-3xl font-bold text-gray-900">
                    {isLoading ? "..." : stats.totalEmployees}
                  </p>
                </div>
                <div className="bg-blue-100 p-3 rounded-lg">
                  <Users className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Folhas processadas</p>
                  <p className="text-3xl font-bold text-gray-900">
                    {isLoading ? "..." : stats.totalPayrolls}
                  </p>
                </div>
                <div className="bg-green-100 p-3 rounded-lg">
                  <Calendar className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total bruto</p>
                  <p className="text-3xl font-bold text-gray-900">
                    {isLoading ? "..." : stats.totalGrossSalary.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                  </p>
                </div>
                <div className="bg-yellow-100 p-3 rounded-lg">
                  <Clock className="w-6 h-6 text-yellow-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Action Cards */}
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Ações rápidas</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card 
              className="cursor-pointer hover:shadow-lg transition-shadow bg-blue-600 text-white"
              onClick={() => onViewChange("employees")}
            >
              <CardContent className="p-6 text-center">
                <UserPlus className="w-8 h-8 mx-auto mb-4" />
                <h3 className="font-semibold">Registrar Funcionário</h3>
              </CardContent>
            </Card>

            <Card 
              className="cursor-pointer hover:shadow-lg transition-shadow bg-green-600 text-white"
              onClick={() => onViewChange("payroll")}
            >
              <CardContent className="p-6 text-center">
                <Calculator className="w-8 h-8 mx-auto mb-4" />
                <h3 className="font-semibold">Cálculo da folha</h3>
              </CardContent>
            </Card>

            <Card 
              className="cursor-pointer hover:shadow-lg transition-shadow bg-purple-600 text-white"
              onClick={() => onViewChange("reports")}
            >
              <CardContent className="p-6 text-center">
                <FileText className="w-8 h-8 mx-auto mb-4" />
                <h3 className="font-semibold">Relatórios</h3>
              </CardContent>
            </Card>

            <Card 
              className="cursor-pointer hover:shadow-lg transition-shadow bg-gray-600 text-white"
              onClick={() => onViewChange("settings")}
            >
              <CardContent className="p-6 text-center">
                <Settings className="w-8 h-8 mx-auto mb-4" />
                <h3 className="font-semibold">Configurações</h3>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* Charts Section */}
        <div className="mt-8 space-y-6">
          <h2 className="text-xl font-semibold text-gray-900">Análise de Dados</h2>
          
          {/* Payroll Trend Chart */}
          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <TrendingUp className="w-5 h-5" />
                <span>Tendência de Cálculos (recentes)</span>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="processed" stroke="#3b82f6" name="Processados" strokeWidth={2} />
                  <Line type="monotone" dataKey="pending" stroke="#ef4444" name="Pendentes" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Distribution Charts */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Bar Chart */}
            <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
              <CardHeader>
                <CardTitle>Cálculos por Mês</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={250}>
                  <BarChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="payrolls" fill="#3b82f6" name="Total" />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>

            {/* Pie Chart */}
            <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
              <CardHeader>
                <CardTitle>Distribuição Salarial</CardTitle>
              </CardHeader>
              <CardContent className="flex justify-center">
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie
                      data={salaryData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, value }) => `${name}: ${value}`}
                      outerRadius={80}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {salaryData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.fill} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
};
