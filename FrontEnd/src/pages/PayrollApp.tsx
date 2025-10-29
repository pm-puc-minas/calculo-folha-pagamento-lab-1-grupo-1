import { useState } from "react";
import { AuthForm } from "@/components/Auth/AuthForm";
import { Sidebar } from "@/components/Layout/Sidebar";
import { DashboardView } from "@/components/Dashboard/DashboardView";
import { EmployeeRegistration } from "@/components/Employee/EmployeeRegistration";
import { PayrollCalculation } from "@/components/Payroll/PayrollCalculation";
import ReportsPage from "@/pages/ReportsPage";
import HistoryFilesPage from "@/pages/HistoryFilesPage";
import SettingsPage from "@/pages/SettingsPage";

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
        return <ReportsPage />;
      case "history":
        return <HistoryFilesPage />;
      case "settings":
        return <SettingsPage />;
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
