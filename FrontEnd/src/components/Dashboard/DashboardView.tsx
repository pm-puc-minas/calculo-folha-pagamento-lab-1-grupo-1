import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { 
  Users, 
  Calendar, 
  Clock, 
  UserPlus, 
  Calculator, 
  FileText, 
  Settings,
  TrendingUp
} from "lucide-react";

interface DashboardViewProps {
  onViewChange: (view: string) => void;
  onLogout: () => void;
}

export const DashboardView = ({ onViewChange, onLogout }: DashboardViewProps) => {
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

      {/* Main Content */}
      <main className="p-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total de funcionários</p>
                  <p className="text-3xl font-bold text-gray-900">124</p>
                </div>
                <div className="bg-blue-100 p-3 rounded-lg">
                  <Users className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Última folha de pagamento</p>
                  <p className="text-3xl font-bold text-gray-900">Dec 2024</p>
                </div>
                <div className="bg-green-100 p-3 rounded-lg">
                  <Calendar className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Cálculos pendentes</p>
                  <p className="text-3xl font-bold text-gray-900">8</p>
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
      </main>
    </div>
  );
};