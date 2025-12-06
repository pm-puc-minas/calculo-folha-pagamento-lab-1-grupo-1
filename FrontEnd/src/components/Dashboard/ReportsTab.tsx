import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ReportHistory } from "@/components/Reports/ReportHistory";
import { EmployeeEditor } from "@/components/Employee/EmployeeEditor";
import { FileText, History, Edit, Users, Plus, RefreshCcw } from "lucide-react";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchEmployees, createEmployee, updateEmployee } from "@/store/slices/employeeSlice";
import { Employee } from "@/types/employee";

export const ReportsTab = () => {
  const dispatch = useAppDispatch();
  const { employees, isLoading } = useAppSelector((s) => s.employee);
  const [showEmployeeEditor, setShowEmployeeEditor] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);

  useEffect(() => {
    dispatch(fetchEmployees());
  }, [dispatch]);

  const handleNewEmployee = () => {
    setEditingEmployee(null);
    setShowEmployeeEditor(true);
  };

  const handleEditEmployee = (employee: Employee) => {
    setEditingEmployee(employee);
    setShowEmployeeEditor(true);
  };

  const handleSaveEmployee = (employeeData: Employee) => {
    if (editingEmployee && editingEmployee.id) {
      dispatch(updateEmployee({ id: editingEmployee.id, data: { ...editingEmployee, ...employeeData } as any }));
    } else {
      dispatch(createEmployee(employeeData));
    }
    setShowEmployeeEditor(false);
    setEditingEmployee(null);
  };

  const handleCancelEdit = () => {
    setShowEmployeeEditor(false);
    setEditingEmployee(null);
  };

  if (showEmployeeEditor) {
    return <EmployeeEditor employee={editingEmployee as any} onSave={handleSaveEmployee} onCancel={handleCancelEdit} />;
  }

  return (
    <div className="space-y-6">
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <FileText className="w-5 h-5" />
            <span>Relatórios e Gestão</span>
          </CardTitle>
          <p className="text-muted-foreground">Acesse o histórico de relatórios e gerencie dados dos funcionários</p>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button onClick={handleNewEmployee} className="h-16 bg-gradient-to-br from-primary to-accent text-white hover:opacity-90">
              <div className="flex flex-col items-center space-y-2">
                <Plus className="w-6 h-6" />
                <span>Novo Funcionário</span>
              </div>
            </Button>

            <Button variant="outline" onClick={() => dispatch(fetchEmployees())} className="h-16 hover:bg-accent/10">
              <div className="flex flex-col items-center space-y-2">
                <RefreshCcw className="w-6 h-6" />
                <span>Atualizar Lista</span>
              </div>
            </Button>

            <Button variant="outline" className="h-16 hover:bg-accent/10" disabled>
              <div className="flex flex-col items-center space-y-2">
                <Users className="w-6 h-6" />
                <span>Lista de Funcionários</span>
              </div>
            </Button>
          </div>
        </CardContent>
      </Card>

      <Tabs defaultValue="history" className="space-y-6">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="history" className="flex items-center space-x-2">
            <History className="w-4 h-4" />
            <span>Histórico de Relatórios</span>
          </TabsTrigger>
          <TabsTrigger value="management" className="flex items-center space-x-2">
            <Edit className="w-4 h-4" />
            <span>Gestão de Funcionários</span>
          </TabsTrigger>
        </TabsList>

        <TabsContent value="history">
          <ReportHistory items={[]} loading={isLoading} />
        </TabsContent>

        <TabsContent value="management">
          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardHeader>
              <CardTitle>Funcionários (API)</CardTitle>
              <p className="text-muted-foreground">Clique em um funcionário para editar.</p>
            </CardHeader>
            <CardContent>
              {employees.length === 0 ? (
                <p className="text-sm text-muted-foreground">Nenhum funcionário encontrado.</p>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {employees.map((emp) => (
                    <Card
                      key={emp.id}
                      className="cursor-pointer hover:shadow-md transition-all border border-border/50 hover:border-primary/30"
                      onClick={() => handleEditEmployee(emp as Employee)}
                    >
                      <CardContent className="p-4">
                        <div className="space-y-1">
                          <div className="flex items-start justify-between">
                            <h4 className="font-semibold text-sm">{emp.name}</h4>
                            <Edit className="w-3 h-3 text-muted-foreground" />
                          </div>
                          <p className="text-xs text-muted-foreground">{emp.position}</p>
                          <p className="text-xs text-muted-foreground">CPF: {emp.cpf}</p>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};
