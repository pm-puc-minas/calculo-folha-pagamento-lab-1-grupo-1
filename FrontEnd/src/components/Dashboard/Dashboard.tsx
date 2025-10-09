import { useState } from "react";
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
  Building,
  DollarSign,
  Clock,
  Archive
} from "lucide-react";
import { EmployeeForm } from "@/components/Employee/EmployeeForm";
import { PayrollCalculator } from "@/components/Payroll/PayrollCalculator";
import { PayrollReport } from "@/components/Payroll/PayrollReport";
import { ReportsTab } from "@/components/Dashboard/ReportsTab";
import { Employee, PayrollCalculation } from "@/types/employee";
import rhProLogo from "@/assets/rh-pro-logo.png";

interface DashboardProps {
  onLogout: () => void;
}

export const Dashboard = ({ onLogout }: DashboardProps) => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [payrollResult, setPayrollResult] = useState<PayrollCalculation | null>(null);
  const [activeTab, setActiveTab] = useState("overview");

  const handleAddEmployee = (employee: Employee) => {
    setEmployees(prev => [...prev, employee]);
  };

  const handleCalculatePayroll = (calculation: PayrollCalculation) => {
    setPayrollResult(calculation);
    setActiveTab("report");
  };

  const statsCards = [
    {
      title: "Funcionários Cadastrados",
      value: employees.length,
      icon: Users,
      color: "text-primary",
      bgColor: "bg-primary/5"
    },
    {
      title: "Folhas Calculadas",
      value: payrollResult ? 1 : 0,
      icon: Calculator,
      color: "text-accent",
      bgColor: "bg-accent/5"
    },
    {
      title: "Total Salários Brutos",
      value: `R$ ${employees.reduce((sum, emp) => sum + emp.grossSalary, 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
      icon: DollarSign,
      color: "text-success",
      bgColor: "bg-success/5"
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-muted/30 to-accent/5">
      {/* Header */}
      <header className="bg-card/50 backdrop-blur-sm border-b shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-lg overflow-hidden ring-1 ring-primary/20">
                <img 
                  src={rhProLogo} 
                  alt="RH Pro Logo" 
                  className="w-full h-full object-cover"
                />
              </div>
              <div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
                  RH Pro
                </h1>
                <p className="text-sm text-muted-foreground">Sistema de Folha de Pagamento</p>
              </div>
            </div>
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
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-6">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-5 bg-card/50 backdrop-blur-sm">
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
              <span>Relatório</span>
            </TabsTrigger>
            <TabsTrigger value="reports-management" className="flex items-center space-x-2">
              <Archive className="w-4 h-4" />
              <span>Relatórios e Gestão</span>
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
                        <p className="text-sm font-medium text-muted-foreground">
                          {stat.title}
                        </p>
                        <h3 className="text-2xl font-bold">{stat.value}</h3>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>

            {/* Recent Employees */}
            <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
              <CardHeader>
                <CardTitle className="flex items-center space-x-2">
                  <Users className="w-5 h-5" />
                  <span>Funcionários Recentes</span>
                </CardTitle>
                <CardDescription>
                  Últimos funcionários cadastrados no sistema
                </CardDescription>
              </CardHeader>
              <CardContent>
                {employees.length === 0 ? (
                  <div className="text-center py-8 text-muted-foreground">
                    <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>Nenhum funcionário cadastrado ainda.</p>
                    <p className="text-sm">Acesse a aba "Funcionários" para começar.</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {employees.slice(-5).map((employee) => (
                      <div key={employee.id} className="flex items-center justify-between p-4 bg-muted/30 rounded-lg">
                        <div>
                          <h4 className="font-semibold">{employee.name}</h4>
                          <p className="text-sm text-muted-foreground">{employee.position}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold text-success">
                            R$ {employee.grossSalary.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
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
          </TabsContent>

          <TabsContent value="employees">
            <EmployeeForm 
              onAddEmployee={handleAddEmployee}
              employees={employees}
              onSelectEmployee={setSelectedEmployee}
            />
          </TabsContent>

          <TabsContent value="calculate">
            <PayrollCalculator 
              employees={employees}
              onCalculate={handleCalculatePayroll}
            />
          </TabsContent>

          <TabsContent value="report">
            {payrollResult ? (
              <PayrollReport calculation={payrollResult} />
            ) : (
              <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                <CardContent className="text-center py-12">
                  <FileText className="w-16 h-16 mx-auto mb-4 text-muted-foreground opacity-50" />
                  <h3 className="text-xl font-semibold mb-2">Nenhum Relatório Disponível</h3>
                  <p className="text-muted-foreground mb-4">
                    Calcule uma folha de pagamento para visualizar o relatório detalhado.
                  </p>
                  <Button 
                    onClick={() => setActiveTab("calculate")}
                    className="bg-gradient-to-r from-primary to-accent hover:from-primary-hover hover:to-accent/90"
                  >
                    <Calculator className="w-4 h-4 mr-2" />
                    Calcular Folha
                  </Button>
                </CardContent>
              </Card>
            )}
          </TabsContent>
          <TabsContent value="reports-management">
            <ReportsTab />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
};