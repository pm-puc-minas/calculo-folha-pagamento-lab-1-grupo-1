import { useState } from "react";
import { AuthForm } from "@/components/Auth/AuthForm";
import { Sidebar } from "@/components/Layout/Sidebar";
import { DashboardView } from "@/components/Dashboard/DashboardView";
import { EmployeeRegistration } from "@/components/Employee/EmployeeRegistration";
import { PayrollCalculation } from "@/components/Payroll/PayrollCalculation";

const PayrollApp = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<{ username: string } | null>(null);
  const [activeView, setActiveView] = useState("dashboard");

  const handleLogin = (username: string, password: string) => {
    setIsAuthenticated(true);
    setUser({ username });
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUser(null);
    setActiveView("dashboard");
  };

  const renderContent = () => {
    switch (activeView) {
      case "employees":
        return <EmployeeRegistration onViewChange={setActiveView} />;
      case "payroll":
        return <PayrollCalculation />;
      case "reports":
        return (
          <div className="flex-1 bg-gray-50 min-h-screen flex items-center justify-center">
            <h2 className="text-2xl font-semibold text-gray-600">Relatórios - Em desenvolvimento</h2>
          </div>
        );
      case "history":
        return (
          <div className="flex-1 bg-gray-50 min-h-screen flex items-center justify-center">
            <h2 className="text-2xl font-semibold text-gray-600">Histórico & Arquivos - Em desenvolvimento</h2>
          </div>
        );
      case "settings":
        return (
          <div className="flex-1 bg-gray-50 min-h-screen flex items-center justify-center">
            <h2 className="text-2xl font-semibold text-gray-600">Configurações - Em desenvolvimento</h2>
          </div>
        );
      default:
        return <DashboardView onViewChange={setActiveView} onLogout={handleLogout} />;
    }
  };

  if (!isAuthenticated) {
    return <AuthForm onLogin={handleLogin} />;
  }

  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar activeView={activeView} onViewChange={setActiveView} />
      {renderContent()}
    </div>
  );
};

export default PayrollApp;