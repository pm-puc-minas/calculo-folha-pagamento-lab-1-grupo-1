import { useState } from "react";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
    LayoutDashboard,
    UserPlus,
    Calculator,
    FileText,
    Archive,
    Settings,
    Calculator as CalcIcon
} from "lucide-react";
import { cn } from "@/lib/utils";

interface SidebarProps {
    activeView: string;
    onViewChange: (view: string) => void;
}

const menuItems = [
    { id: "dashboard", label: "Painel", icon: LayoutDashboard },
    { id: "employees", label: "Cadastrar Funcionário", icon: UserPlus },
    { id: "payroll", label: "Calcular Folha", icon: Calculator },
    { id: "reports", label: "Relatórios", icon: FileText },
    { id: "history", label: "Histórico & Arquivos", icon: Archive },
    { id: "settings", label: "Configurações", icon: Settings },
];

export const Sidebar = ({ activeView, onViewChange }: SidebarProps) => {
    return (
        <div className="w-64 bg-slate-900 text-white h-screen flex flex-col">
            {/* Header */}
            <div className="p-6 border-b border-slate-700">
                <div className="flex items-center space-x-3">
                    <div className="bg-blue-600 p-2 rounded-lg">
                        <CalcIcon className="w-6 h-6" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">PayrollPro</h1>
                    </div>
                </div>
            </div>

            {/* Navigation */}
            <ScrollArea className="flex-1 px-4 py-6">
                <nav className="space-y-2">
                    {menuItems.map((item) => {
                        const Icon = item.icon;
                        return (
                            <Button
                                key={item.id}
                                variant="ghost"
                                className={cn(
                                    "w-full justify-start text-left text-slate-300 hover:text-white hover:bg-slate-800",
                                    activeView === item.id && "bg-blue-600 text-white hover:bg-blue-700"
                                )}
                                onClick={() => onViewChange(item.id)}
                            >
                                <Icon className="w-5 h-5 mr-3" />
                                {item.label}
                            </Button>
                        );
                    })}
                </nav>
            </ScrollArea>
        </div>
    );
};