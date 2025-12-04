import { useEffect, useState } from "react";
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

interface DashboardViewProps {
  onViewChange: (view: string) => void;
  onLogout: () => void;
}

// Dados simulados para demonstração
const generateChartData = () => {
  return [
    { month: "Jan", payrolls: 120, processed: 120, pending: 0 },
    { month: "Fev", payrolls: 135, processed: 133, pending: 2 },
    { month: "Mar", payrolls: 125, processed: 125, pending: 0 },
    { month: "Abr", payrolls: 140, processed: 138, pending: 2 },
    { month: "Mai", payrolls: 128, processed: 126, pending: 2 },
    { month: "Jun", payrolls: 142, processed: 141, pending: 1 },
  ];
};

const generateSalaryDistribution = () => {
  return [
    { name: "Até 3k", value: 45, fill: "#3b82f6" },
    { name: "3k - 5k", value: 55, fill: "#8b5cf6" },
    { name: "5k - 8k", value: 35, fill: "#ec4899" },
    { name: "Acima de 8k", value: 20, fill: "#f59e0b" },
  ];
};

export const DashboardView = ({ onViewChange, onLogout }: DashboardViewProps) => {
  const [dashboardData, setDashboardData] = useState({
    totalEmployees: 0,
    lastPayroll: "",
    pendingCalculations: 0,
    totalCosts: ""
  });
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = typeof localStorage !== 'undefined' ? localStorage.getItem('accessToken') : null;
        const res = await fetch('/api/dashboard', {
          headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
        });
        if (!res.ok) {
          if (res.status === 401) setErrorMsg('Sessão expirada. Faça login novamente.');
          else if (res.status === 403) setErrorMsg('Acesso negado. Permissão insuficiente para o dashboard.');
          else setErrorMsg('Falha ao carregar dashboard.');
          return;
        }
        const data = await res.json();
        const totalEmployees = data.totalEmployees ?? 0;
        const totalPayrolls = data.totalPayrolls ?? 0;
        setDashboardData({
          totalEmployees,
          lastPayroll: `${totalPayrolls} folhas`,
          pendingCalculations: 0,
          totalCosts: ""
        });
      } catch (e) {
        setErrorMsg('Erro de rede ao carregar dashboard.');
      }
    };
    fetchData();
  }, []);

  const chartData = generateChartData();
  const salaryData = generateSalaryDistribution();
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
      {errorMsg && (
        <div className="mx-6 mt-4">
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {errorMsg}
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
                  <p className="text-3xl font-bold text-gray-900">{dashboardData.totalEmployees}</p>
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
                  <p className="text-sm text-gray-600 mb-1">Última folha de pagamento</p>
                  <p className="text-3xl font-bold text-gray-900">{dashboardData.lastPayroll.split(" ")[0]}</p>
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
                  <p className="text-sm text-gray-600 mb-1">Cálculos pendentes</p>
                  <p className="text-3xl font-bold text-gray-900">{dashboardData.pendingCalculations}</p>
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
                <span>Tendência de Cálculos (6 meses)</span>
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
