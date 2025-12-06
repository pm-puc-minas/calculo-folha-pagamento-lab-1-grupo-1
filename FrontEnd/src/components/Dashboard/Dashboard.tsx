import { useEffect, useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import {
  Users,
  Calculator,
  FileText,
  TrendingUp,
  LogOut,
  DollarSign,
  Archive,
  BarChart as BarChartIcon,
} from "lucide-react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend
} from 'recharts';
import { EmployeeForm } from "@/components/Employee/EmployeeForm";
import { PayrollCalculator } from "@/components/Payroll/PayrollCalculator";
import { PayrollReport } from "@/components/Payroll/PayrollReport";
import { ReportsTab } from "@/components/Dashboard/ReportsTab";
import rhProLogo from "@/assets/rh-pro-logo.png";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchEmployees, createEmployee } from "@/store/slices/employeeSlice";
import { fetchDashboardData } from "@/store/slices/dashboardSlice";
import { Employee, PayrollCalculation } from "@/types/employee";

interface DashboardProps {
  onLogout: () => void;
}

export const Dashboard = ({ onLogout }: DashboardProps) => {
  const dispatch = useAppDispatch();
  const { employees } = useAppSelector((state) => state.employee);
  const { data: dashboardData, isLoading: isDashboardLoading } = useAppSelector((state) => state.dashboard);
  const [activeTab, setActiveTab] = useState("overview");
  const [payrollResult, setPayrollResult] = useState<PayrollCalculation | null>(null);

  useEffect(() => {
    dispatch(fetchEmployees());
    dispatch(fetchDashboardData());
  }, [dispatch]);

  const handleAddEmployee = (employee: Employee) => {
    dispatch(createEmployee(employee)).then(() => {
        dispatch(fetchDashboardData()); // Refresh dashboard data after adding employee
    });
  };

  const handleCalculatePayroll = (calculation: PayrollCalculation) => {
    setPayrollResult(calculation);
    setActiveTab("report");
    // Refresh dashboard data after payroll calculation
    dispatch(fetchDashboardData());
  };

  const statsCards = [
    {
      title: "Funcionários Cadastrados",
      value: dashboardData?.totalEmployees ?? 0,
      icon: Users,
      color: "text-primary",
      bgColor: "bg-primary/5",
    },
    {
      title: "Última Folha",
      value: dashboardData?.lastPayrollDate ?? "N/A",
      icon: Calculator,
      color: "text-accent",
      bgColor: "bg-accent/5",
    },
    {
      title: "Total de Salários",
      value: dashboardData?.totalSalaries 
        ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(dashboardData.totalSalaries)
        : "R$ 0,00",
      icon: DollarSign,
      color: "text-amber-600",
      bgColor: "bg-amber-500/10",
    },
  ];

  // Use recent employees from dashboard data exclusively
  const recentEmployeesList = dashboardData?.recentEmployees || [];

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-muted/30 to-accent/5">
      {/* Header */}
      <header className="bg-card/50 backdrop-blur-sm border-b shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-lg overflow-hidden ring-1 ring-primary/20">
                <img src={rhProLogo} alt="RH Pro Logo" className="w-full h-full object-cover" />
              </div>
              <div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
                  RH Pro
                </h1>
                <p className="text-sm text-muted-foreground">Sistema de Folha de Pagamento</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
                {dashboardData?.currentUser && (
                    <span className="text-sm text-muted-foreground hidden md:inline-block">
                        Olá, {dashboardData.currentUser}
                    </span>
                )}
                <Button
                  onClick={onLogout}
                  variant="outline"
                  size="sm"
                  className="hover:bg-destructive hover:text-destructive-foreground transition-colors"
                >
                  <LogOut className="w-4 h-4 mr-2" />
                  Sair
                </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-4 bg-card/50 backdrop-blur-sm">
            <TabsTrigger value="overview" className="flex items-center space-x-2">
              <TrendingUp className="w-4 h-4" />
              <span>Visão Geral</span>
            </TabsTrigger>
            <TabsTrigger value="employees" className="flex items-center space-x-2">
              <Users className="w-4 h-4" />
              <span>Funcionários</span>
            </TabsTrigger>
            <TabsTrigger value="calculate" className="flex items-center space-x-2">
              <Calculator className="w-4 h-4" />
              <span>Calcular Folha</span>
            </TabsTrigger>
            <TabsTrigger value="report" className="flex items-center space-x-2">
              <FileText className="w-4 h-4" />
              <span>Relatórios</span>
            </TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {statsCards.map((stat, index) => (
                <Card key={index} className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                  <CardContent className="p-6">
                    <div className="flex items-center space-x-4">
                      <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                        <stat.icon className={`w-6 h-6 ${stat.color}`} />
                      </div>
                      <div>
                        <p className="text-sm font-medium text-muted-foreground">{stat.title}</p>
                        <h3 className="text-2xl font-bold">{stat.value}</h3>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Salary Distribution Chart */}
                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                  <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                      <BarChartIcon className="w-5 h-5" />
                      <span>Distribuição Salarial</span>
                    </CardTitle>
                    <CardDescription>Relação entre número de funcionários e faixas salariais</CardDescription>
                  </CardHeader>
                  <CardContent>
                    {dashboardData?.salaryDistribution ? (
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={dashboardData.salaryDistribution} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                    <XAxis 
                                        dataKey="range" 
                                        tick={{ fontSize: 12 }} 
                                        interval={0}
                                        angle={-45}
                                        textAnchor="end"
                                        height={70}
                                    />
                                    <YAxis allowDecimals={false} />
                                    <Tooltip 
                                        contentStyle={{ backgroundColor: 'hsl(var(--card))', borderColor: 'hsl(var(--border))', borderRadius: 'var(--radius)' }}
                                        itemStyle={{ color: 'hsl(var(--foreground))' }}
                                    />
                                    <Bar dataKey="count" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} name="Funcionários" />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    ) : (
                        <div className="h-[300px] flex items-center justify-center text-muted-foreground">
                            <p>Carregando dados do gráfico...</p>
                        </div>
                    )}
                  </CardContent>
                </Card>

                {/* Recent Employees */}
                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                  <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                      <Users className="w-5 h-5" />
                      <span>Funcionários Recentes</span>
                    </CardTitle>
                    <CardDescription>Últimos funcionários cadastrados no sistema</CardDescription>
                  </CardHeader>
                  <CardContent>
                    {recentEmployeesList.length === 0 ? (
                      <div className="text-center py-8 text-muted-foreground">
                        <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                        <p>Nenhum funcionário cadastrado ainda.</p>
                        <p className="text-sm">Acesse a aba "Funcionários" para começar.</p>
                      </div>
                    ) : (
                      <div className="space-y-4">
                        {recentEmployeesList.map((employee) => (
                          <div key={employee.id} className="flex items-center justify-between p-4 bg-muted/30 rounded-lg">
                            <div>
                              <h4 className="font-semibold">{employee.fullName}</h4>
                              <p className="text-sm text-muted-foreground">{employee.position}</p>
                            </div>
                            <div className="text-right">
                              <p className="font-semibold text-success">
                                R$ {(employee.salary).toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                              </p>
                              <Badge variant="secondary" className="text-xs">
                                {employee.admissionDate}
                              </Badge>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </CardContent>
                </Card>
            </div>
          </TabsContent>

          <TabsContent value="employees">
            <EmployeeForm onAddEmployee={handleAddEmployee} employees={employees} onSelectEmployee={() => {}} />
          </TabsContent>

          <TabsContent value="calculate">
            <PayrollCalculator employees={employees} onCalculate={handleCalculatePayroll} />
          </TabsContent>

          <TabsContent value="report">
            <ReportsTab />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};
