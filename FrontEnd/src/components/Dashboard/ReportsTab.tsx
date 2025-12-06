import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ReportHistory } from "@/components/Reports/ReportHistory";
import { EmployeeEditor } from "@/components/Employee/EmployeeEditor";
import { FileText, History, Edit, Users, Plus, RefreshCcw, Calendar as CalendarIcon, Check, ChevronsUpDown } from "lucide-react";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchEmployees, createEmployee, updateEmployee } from "@/store/slices/employeeSlice";
import { addReport, removeReport, fetchReports, generateReport, deleteReport } from "@/store/slices/payrollSlice";
import { Employee } from "@/types/employee";
import { useToast } from "@/hooks/use-toast";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command";
import { cn } from "@/lib/utils";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";

export const ReportsTab = () => {
  const dispatch = useAppDispatch();
  const { employees, isLoading: isEmployeesLoading } = useAppSelector((s) => s.employee);
  const { reportHistory, isLoading: isPayrollLoading } = useAppSelector((s) => s.payroll);
  const [showEmployeeEditor, setShowEmployeeEditor] = useState(false);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  
  // Report Generation State
  const [selectedMonth, setSelectedMonth] = useState("");
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<string>("");
  const [reportType, setReportType] = useState<string>("summary");
  const [openCombobox, setOpenCombobox] = useState(false);
  const { toast } = useToast();

  useEffect(() => {
    dispatch(fetchEmployees());
    dispatch(fetchReports());
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

  const handleGenerateReport = async () => {
    if (!selectedMonth || !selectedEmployeeId) {
      toast({
        title: "Campos obrigatórios",
        description: "Selecione um funcionário e um mês para gerar o relatório.",
        variant: "destructive"
      });
      return;
    }

    const employee = employees.find(e => e.id?.toString() === selectedEmployeeId);
    if (!employee) return;

    try {
        await dispatch(generateReport({
            type: reportType,
            employeeId: Number(selectedEmployeeId),
            month: selectedMonth
        })).unwrap();

        toast({
            title: "Relatório Gerado",
            description: `Relatório de ${employee.name} referente a ${selectedMonth} foi gerado com sucesso.`
        });

    } catch (error) {
        toast({
            title: "Erro",
            description: "Não foi possível gerar o relatório.",
            variant: "destructive"
        });
    }
  };

  const handleDownloadReport = async (id: string) => {
      try {
        const token = localStorage.getItem('accessToken');
        const response = await fetch(`/api/reports/${id}/download`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : undefined,
        });

        if (!response.ok) throw new Error("Erro ao baixar relatório");

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `relatorio-${id}.pdf`; // Backend sends PDF
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        toast({
            title: "Download Concluído",
            description: "O relatório foi baixado com sucesso."
        });
      } catch (error) {
        console.error(error);
        toast({
            title: "Erro no Download",
            description: "Não foi possível baixar o relatório.",
            variant: "destructive"
        });
      }
  };

  const handleDeleteReport = async (id: string) => {
      try {
        await dispatch(deleteReport(id)).unwrap();
        toast({
            title: "Relatório Excluído",
            description: "O relatório foi removido do histórico."
        });
      } catch (error) {
        toast({
            title: "Erro",
            description: "Não foi possível excluir o relatório.",
            variant: "destructive"
        });
      }
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

        <TabsContent value="history" className="space-y-6">
          {/* Report Generation Form */}
          <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
            <CardHeader>
              <CardTitle className="text-lg">Gerar Novo Relatório</CardTitle>
              <CardDescription>Selecione os parâmetros para gerar um relatório detalhado</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
                <div className="space-y-2">
                  <Label htmlFor="report-month">Mês de Referência</Label>
                  <Popover>
                    <PopoverTrigger asChild>
                      <Button
                        variant={"outline"}
                        className={cn(
                          "w-full justify-start text-left font-normal",
                          !selectedMonth && "text-muted-foreground"
                        )}
                      >
                        <CalendarIcon className="mr-2 h-4 w-4" />
                        {selectedMonth ? (
                          format(new Date(selectedMonth + "-01T00:00:00"), "MMMM 'de' yyyy", { locale: ptBR })
                        ) : (
                          <span>Selecione o mês</span>
                        )}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-auto p-0" align="start">
                      <Calendar
                        mode="single"
                        selected={selectedMonth ? new Date(selectedMonth + "-01T00:00:00") : undefined}
                        onSelect={(date) => date && setSelectedMonth(format(date, "yyyy-MM"))}
                        initialFocus
                        locale={ptBR}
                      />
                    </PopoverContent>
                  </Popover>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="report-employee">Funcionário</Label>
                  <Popover open={openCombobox} onOpenChange={setOpenCombobox}>
                    <PopoverTrigger asChild>
                      <Button
                        variant="outline"
                        role="combobox"
                        aria-expanded={openCombobox}
                        className="w-full justify-between"
                      >
                        {selectedEmployeeId
                          ? employees.find((employee) => employee.id?.toString() === selectedEmployeeId)?.name
                          : "Selecione..."}
                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent className="w-[200px] p-0">
                      <Command>
                        <CommandInput placeholder="Buscar funcionário..." />
                        <CommandList>
                          <CommandEmpty>Nenhum funcionário encontrado.</CommandEmpty>
                          <CommandGroup>
                            {employees.map((employee) => (
                              <CommandItem
                                key={employee.id}
                                value={employee.name}
                                onSelect={() => {
                                  setSelectedEmployeeId(employee.id?.toString() || "");
                                  setOpenCombobox(false);
                                }}
                              >
                                <Check
                                  className={cn(
                                    "mr-2 h-4 w-4",
                                    selectedEmployeeId === employee.id?.toString() ? "opacity-100" : "opacity-0"
                                  )}
                                />
                                {employee.name}
                              </CommandItem>
                            ))}
                          </CommandGroup>
                        </CommandList>
                      </Command>
                    </PopoverContent>
                  </Popover>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="report-type">Tipo de Relatório</Label>
                  <Select value={reportType} onValueChange={setReportType}>
                    <SelectTrigger id="report-type">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="summary">Resumido</SelectItem>
                      <SelectItem value="payroll">Folha de Pagamento</SelectItem>
                      <SelectItem value="employee">Dados Cadastrais</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <Button onClick={handleGenerateReport} className="bg-primary text-primary-foreground hover:bg-primary/90">
                  <FileText className="w-4 h-4 mr-2" />
                  Gerar Relatório
                </Button>
              </div>
            </CardContent>
          </Card>

          <ReportHistory 
            items={reportHistory} 
            loading={isPayrollLoading} 
            onDownload={handleDownloadReport}
            onDelete={handleDeleteReport}
          />
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
