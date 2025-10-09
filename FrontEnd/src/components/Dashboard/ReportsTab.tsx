import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ReportHistory } from "@/components/Reports/ReportHistory";
import { EmployeeEditor } from "@/components/Employee/EmployeeEditor";
import { FileText, History, Edit, Users, Plus } from "lucide-react";

export const ReportsTab = () => {
  const [showEmployeeEditor, setShowEmployeeEditor] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState(null);

  const handleNewEmployee = () => {
    setEditingEmployee(null);
    setShowEmployeeEditor(true);
  };

  const handleEditEmployee = (employee: any) => {
    setEditingEmployee(employee);
    setShowEmployeeEditor(true);
  };

  const handleSaveEmployee = (employeeData: any) => {
    console.log('Salvando funcionário:', employeeData);
    // TODO: Implement save logic
    setShowEmployeeEditor(false);
    setEditingEmployee(null);
  };

  const handleCancelEdit = () => {
    setShowEmployeeEditor(false);
    setEditingEmployee(null);
  };

  if (showEmployeeEditor) {
    return (
      <EmployeeEditor
        employee={editingEmployee}
        onSave={handleSaveEmployee}
        onCancel={handleCancelEdit}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Quick Actions */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <FileText className="w-5 h-5" />
            <span>Relatórios e Gestão</span>
          </CardTitle>
          <p className="text-muted-foreground">
            Acesse o histórico de relatórios e gerencie dados dos funcionários
          </p>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              onClick={handleNewEmployee}
              className="h-16 bg-gradient-to-br from-primary to-accent text-white hover:opacity-90"
            >
              <div className="flex flex-col items-center space-y-2">
                <Plus className="w-6 h-6" />
                <span>Novo Funcionário</span>
              </div>
            </Button>
            
            <Button
              variant="outline"
              onClick={() => handleEditEmployee({
                id: '1',
                name: 'João Silva',
                cpf: '123.456.789-00',
                position: 'Desenvolvedor',
                grossSalary: 5000
              })}
              className="h-16 hover:bg-accent/10"
            >
              <div className="flex flex-col items-center space-y-2">
                <Edit className="w-6 h-6" />
                <span>Editar Funcionário</span>
              </div>
            </Button>
            
            <Button
              variant="outline"
              className="h-16 hover:bg-accent/10"
            >
              <div className="flex flex-col items-center space-y-2">
                <Users className="w-6 h-6" />
                <span>Lista de Funcionários</span>
              </div>
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Tabs for different report views */}
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
          <ReportHistory />
        </TabsContent>

        <TabsContent value="management">
          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardHeader>
              <CardTitle>Gestão de Funcionários</CardTitle>
              <p className="text-muted-foreground">
                Funcionalidade em desenvolvimento. Em breve você poderá:
              </p>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-muted-foreground">
                <li>• Visualizar lista completa de funcionários</li>
                <li>• Editar informações pessoais e profissionais</li>
                <li>• Gerenciar status de funcionários (ativo/inativo)</li>
                <li>• Exportar dados para SQL Server</li>
                <li>• Sincronização automática com banco de dados</li>
              </ul>
              
              <div className="mt-6 flex space-x-4">
                <Button onClick={handleNewEmployee}>
                  <Plus className="w-4 h-4 mr-2" />
                  Cadastrar Novo Funcionário
                </Button>
                <Button 
                  variant="outline"
                  onClick={() => handleEditEmployee({
                    id: '1',
                    name: 'João Silva',
                    cpf: '123.456.789-00',
                    position: 'Desenvolvedor',
                    grossSalary: 5000
                  })}
                >
                  <Edit className="w-4 h-4 mr-2" />
                  Editar Funcionário (Demo)
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};