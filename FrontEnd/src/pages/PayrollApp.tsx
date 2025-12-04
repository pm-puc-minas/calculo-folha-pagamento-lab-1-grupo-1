import { useState } from "react";
import { AuthForm } from "@/components/Auth/AuthForm";
import { Sidebar } from "@/components/Layout/Sidebar";
import { DashboardView } from "@/components/Dashboard/DashboardView";
import { EmployeeRegistration } from "@/components/Employee/EmployeeRegistration";
import { PayrollCalculation } from "@/components/Payroll/PayrollCalculation";
import ReportsPage from "@/pages/ReportsPage";
import HistoryFilesPage from "@/pages/HistoryFilesPage";
import SettingsPage from "@/pages/SettingsPage";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { logoutUser } from "@/store/slices/authSlice";

const PayrollApp = () => {
  const dispatch = useAppDispatch();
  const isAuthenticated = useAppSelector((s) => s.auth.isAuthenticated);
  const user = useAppSelector((s) => s.auth.user);
  const [activeView, setActiveView] = useState("dashboard");

  const handleLogout = () => {
    dispatch(logoutUser());
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
    return <AuthForm />;
  }

  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar activeView={activeView} onViewChange={setActiveView} />
      {renderContent()}
    </div>
  );
};

export default PayrollApp;
