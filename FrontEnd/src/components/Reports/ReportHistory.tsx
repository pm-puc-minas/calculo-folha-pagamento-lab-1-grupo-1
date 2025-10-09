import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { FileText, Download, User, Calendar, Eye } from "lucide-react";
import { useAppSelector } from "@/store/hooks";

interface ReportHistoryEntry {
    id: string;
    reportType: 'payroll' | 'employee' | 'summary';
    employeeName: string;
    referenceMonth: string;
    generatedAt: string;
    generatedBy: {
        id: string;
        name: string;
        email: string;
        role: string;
    };
    status: 'completed' | 'pending' | 'error';
}

// Mock data - posteriormente será integrado com SQL Server
const mockReportHistory: ReportHistoryEntry[] = [
    {
        id: '1',
        reportType: 'payroll',
        employeeName: 'João Silva',
        referenceMonth: '2024-01',
        generatedAt: '2024-01-15T10:30:00',
        generatedBy: {
            id: '1',
            name: 'Maria Santos',
            email: 'maria.santos@empresa.com',
            role: 'Administrador RH'
        },
        status: 'completed'
    },
    {
        id: '2',
        reportType: 'payroll',
        employeeName: 'Ana Costa',
        referenceMonth: '2024-01',
        generatedAt: '2024-01-15T11:15:00',
        generatedBy: {
            id: '2',
            name: 'Carlos Oliveira',
            email: 'carlos.oliveira@empresa.com',
            role: 'Analista RH'
        },
        status: 'completed'
    },
    {
        id: '3',
        reportType: 'summary',
        employeeName: 'Relatório Geral',
        referenceMonth: '2024-01',
        generatedAt: '2024-01-16T09:00:00',
        generatedBy: {
            id: '1',
            name: 'Maria Santos',
            email: 'maria.santos@empresa.com',
            role: 'Administrador RH'
        },
        status: 'pending'
    }
];

export const ReportHistory = () => {
    const formatDate = (dateStr: string) => {
        return new Date(dateStr).toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatMonth = (monthStr: string) => {
        if (!monthStr || !monthStr.includes('-')) {
            return 'Mês inválido';
        }

        const [year, month] = monthStr.split('-');
        const date = new Date(parseInt(year), parseInt(month) - 1);
        return new Intl.DateTimeFormat('pt-BR', {
            year: 'numeric',
            month: 'long'
        }).format(date);
    };

    const getReportTypeLabel = (type: string) => {
        switch (type) {
            case 'payroll':
                return 'Folha de Pagamento';
            case 'employee':
                return 'Dados do Funcionário';
            case 'summary':
                return 'Relatório Resumido';
            default:
                return 'Relatório';
        }
    };

    const getStatusBadge = (status: string) => {
        switch (status) {
            case 'completed':
                return <Badge className="bg-success text-success-foreground">Concluído</Badge>;
            case 'pending':
                return <Badge variant="secondary">Pendente</Badge>;
            case 'error':
                return <Badge variant="destructive">Erro</Badge>;
            default:
                return <Badge variant="outline">Desconhecido</Badge>;
        }
    };

    const getUserInitials = (name: string) => {
        return name.split(' ').map(n => n[0]).join('').toUpperCase();
    };

    const handleViewReport = (reportId: string) => {
        console.log('Visualizar relatório:', reportId);
        // TODO: Implementar visualização do relatório
    };

    const handleDownloadReport = (reportId: string) => {
        console.log('Download relatório:', reportId);
        // TODO: Implementar download do relatório
    };

    return (
        <div className="space-y-6">
            {/* Header */}
            <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex items-center space-x-3">
                        <div className="w-12 h-12 bg-gradient-to-br from-primary to-accent rounded-lg flex items-center justify-center">
                            <FileText className="w-6 h-6 text-white" />
                        </div>
                        <div>
                            <CardTitle className="text-2xl">Histórico de Relatórios</CardTitle>
                            <p className="text-muted-foreground">
                                Acompanhe todos os relatórios gerados no sistema
                            </p>
                        </div>
                    </div>
                </CardHeader>
            </Card>

            {/* Filters and Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                    <CardContent className="p-6 text-center">
                        <div className="text-2xl font-bold text-primary">
                            {mockReportHistory.length}
                        </div>
                        <div className="text-sm text-muted-foreground">Total de Relatórios</div>
                    </CardContent>
                </Card>

                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                    <CardContent className="p-6 text-center">
                        <div className="text-2xl font-bold text-success">
                            {mockReportHistory.filter(r => r.status === 'completed').length}
                        </div>
                        <div className="text-sm text-muted-foreground">Concluídos</div>
                    </CardContent>
                </Card>

                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                    <CardContent className="p-6 text-center">
                        <div className="text-2xl font-bold text-warning">
                            {mockReportHistory.filter(r => r.status === 'pending').length}
                        </div>
                        <div className="text-sm text-muted-foreground">Pendentes</div>
                    </CardContent>
                </Card>

                <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                    <CardContent className="p-6 text-center">
                        <div className="text-2xl font-bold text-accent">
                            {new Set(mockReportHistory.map(r => r.generatedBy.id)).size}
                        </div>
                        <div className="text-sm text-muted-foreground">Usuários Ativos</div>
                    </CardContent>
                </Card>
            </div>

            {/* Report History Table */}
            <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="flex items-center space-x-2">
                        <Calendar className="w-5 h-5" />
                        <span>Histórico Detalhado</span>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Tipo</TableHead>
                                <TableHead>Funcionário</TableHead>
                                <TableHead>Mês Referência</TableHead>
                                <TableHead>Gerado por</TableHead>
                                <TableHead>Data/Hora</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {mockReportHistory.map((report) => (
                                <TableRow key={report.id}>
                                    <TableCell>
                                        <Badge variant="outline" className="font-medium">
                                            {getReportTypeLabel(report.reportType)}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="font-medium">
                                        {report.employeeName}
                                    </TableCell>
                                    <TableCell>{formatMonth(report.referenceMonth)}</TableCell>
                                    <TableCell>
                                        <div className="flex items-center space-x-2">
                                            <Avatar className="w-8 h-8">
                                                <AvatarFallback className="text-xs">
                                                    {getUserInitials(report.generatedBy.name)}
                                                </AvatarFallback>
                                            </Avatar>
                                            <div className="flex flex-col">
                                                <span className="text-sm font-medium">{report.generatedBy.name}</span>
                                                <span className="text-xs text-muted-foreground">{report.generatedBy.role}</span>
                                            </div>
                                        </div>
                                    </TableCell>
                                    <TableCell>{formatDate(report.generatedAt)}</TableCell>
                                    <TableCell>{getStatusBadge(report.status)}</TableCell>
                                    <TableCell className="text-right">
                                        <div className="flex items-center justify-end space-x-2">
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => handleViewReport(report.id)}
                                                className="text-primary hover:text-primary"
                                            >
                                                <Eye className="w-4 h-4" />
                                            </Button>
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onClick={() => handleDownloadReport(report.id)}
                                                className="text-accent hover:text-accent"
                                                disabled={report.status !== 'completed'}
                                            >
                                                <Download className="w-4 h-4" />
                                            </Button>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    );
};