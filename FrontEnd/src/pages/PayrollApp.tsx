import { useState } from "react";
import { AuthForm } from "@/components/Auth/AuthForm";
import { Sidebar } from "@/components/Layout/Sidebar";
import { DashboardView } from "@/components/Dashboard/DashboardView";
import { EmployeeRegistration } from "@/components/Employee/EmployeeRegistration";
import { PayrollCalculation } from "@/components/Payroll/PayrollCalculation";
import ReportsPage from "@/pages/ReportsPage";
import HistoryFilesPage from "@/pages/HistoryFilesPage";
import SettingsPage from "@/pages/SettingsPage";

type UserInfo = {
  username?: string | null;
  email?: string | null;
};

const PayrollApp = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<UserInfo | null>(null);
  const [activeView, setActiveView] = useState("dashboard");

  const handleLogin = (userInfo: UserInfo) => {
    setIsAuthenticated(true);
    setUser(userInfo);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUser(null);
    setActiveView("dashboard");
  };

  const renderContent = () => {
    switch (activeView) {
      case "employees":
        return <EmployeeRegistration onViewChange={setActiveView} user={user} />;
      case "payroll":
        return <PayrollCalculation user={user} />;
      case "reports":
        return <ReportsPage />;
      case "history":
        return <HistoryFilesPage />;
      case "settings":
        return <SettingsPage />;
      default:
        return <DashboardView user={user} onViewChange={setActiveView} onLogout={handleLogout} />;
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
