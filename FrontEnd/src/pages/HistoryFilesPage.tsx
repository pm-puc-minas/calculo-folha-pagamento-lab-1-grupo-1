import { useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Upload, File, Trash2, Download, ChevronLeft, ChevronRight, FileText, Search } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { apiFetch } from "@/lib/apiClient";

interface FileEntry {
  id: number;
  name: string;
  uploadedAt?: string;
  size?: number;
  type?: string;
  uploadedBy?: string;
}

const ITEMS_PER_PAGE = 8;

const HistoryFilesPage = () => {
  const [files, setFiles] = useState<FileEntry[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterType, setFilterType] = useState<string | null>(null);
  const [fileToUpload, setFileToUpload] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(false);

  const loadFiles = async () => {
    try {
      setLoading(true);
      const res = await apiFetch("/api/files");
      const data = await res.json();
      const normalized: FileEntry[] = (data || []).map((f: any) => ({
        id: f.id,
        name: f.filename || f.name || `file-${f.id}`,
        type: (f.contentType || "").split("/").pop(),
        uploadedAt: f.createdAt,
        size: f.size,
        uploadedBy: f.uploadedBy
      }));
      setFiles(normalized);
    } catch (err: any) {
      toast({ title: "Erro ao carregar arquivos", description: err.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFiles();
  }, []);

  const filteredFiles = useMemo(() => {
    return files
      .filter(f => {
        const matchesSearch = f.name.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesType = !filterType || f.type === filterType;
        return matchesSearch && matchesType;
      })
      .sort((a, b) => new Date(b.uploadedAt || 0).getTime() - new Date(a.uploadedAt || 0).getTime());
  }, [files, searchTerm, filterType]);

  const totalPages = Math.ceil(filteredFiles.length / ITEMS_PER_PAGE) || 1;
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const paginatedFiles = filteredFiles.slice(startIndex, endIndex);

  useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterType]);

  const formatFileSize = (bytes?: number): string => {
    if (!bytes) return "-";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
  };

  const formatDate = (dateStr?: string): string => {
    if (!dateStr) return "-";
    return new Date(dateStr).toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  const getFileIcon = (type?: string) => {
    switch (type) {
      case "pdf":
        return <File className="w-4 h-4 text-red-500" />;
      case "xlsx":
        return <File className="w-4 h-4 text-green-500" />;
      default:
        return <File className="w-4 h-4 text-gray-500" />;
    }
  };

  const getFileTypeBadge = (type?: string) => {
    const badgeConfig = {
      pdf: { bg: "bg-red-100", text: "text-red-800", label: "PDF" },
      xlsx: { bg: "bg-green-100", text: "text-green-800", label: "Excel" }
    } as const;
    const config = (type && badgeConfig[type as keyof typeof badgeConfig]) || { bg: "bg-gray-100", text: "text-gray-800", label: (type || "--").toUpperCase() };
    return <Badge className={`${config.bg} ${config.text}`}>{config.label}</Badge>;
  };

  const handleUpload = async () => {
    if (!fileToUpload) {
      toast({ title: "Selecione um arquivo", variant: "destructive" });
      return;
    }
    try {
      setUploading(true);
      const formData = new FormData();
      formData.append("file", fileToUpload);
      await apiFetch("/api/files", {
        method: "POST",
        body: formData,
      });
      toast({ title: "Upload concluído" });
      setFileToUpload(null);
      await loadFiles();
    } catch (err: any) {
      toast({ title: "Erro no upload", description: err.message, variant: "destructive" });
    } finally {
      setUploading(false);
    }
  };

  const handleDownload = async (fileId: number, fileName: string) => {
    try {
      const res = await apiFetch(`/api/files/${fileId}/download`, { method: "GET" });
      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      toast({ title: "Download iniciado", description: fileName });
    } catch (err: any) {
      toast({ title: "Erro ao baixar", description: err.message, variant: "destructive" });
    }
  };

  const handleDelete = async (fileId: number, fileName: string) => {
    try {
      await apiFetch(`/api/files/${fileId}`, { method: "DELETE" });
      setFiles(files.filter(f => f.id !== fileId));
      toast({ title: "Arquivo removido", description: `${fileName} foi deletado` });
    } catch (err: any) {
      toast({ title: "Erro ao remover", description: err.message, variant: "destructive" });
    }
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center space-x-3 mb-2">
          <div className="w-10 h-10 bg-gradient-to-br from-primary to-accent rounded-lg flex items-center justify-center">
            <FileText className="w-6 h-6 text-white" />
          </div>
          <h1 className="text-3xl font-bold">Histórico & Arquivos</h1>
        </div>
        <p className="text-muted-foreground ml-13">Gerencie seus arquivos de folha de pagamento e relatórios</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardContent className="p-6 text-center">
            <div className="text-2xl font-bold text-primary">{filteredFiles.length}</div>
            <div className="text-sm text-muted-foreground">Total de Arquivos</div>
          </CardContent>
        </Card>

        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardContent className="p-6 text-center">
            <div className="text-2xl font-bold text-success">{filteredFiles.filter(f => f.type === "xlsx").length}</div>
            <div className="text-sm text-muted-foreground">Arquivos Excel</div>
          </CardContent>
        </Card>

        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardContent className="p-6 text-center">
            <div className="text-2xl font-bold text-accent">{filteredFiles.filter(f => f.type === "pdf").length}</div>
            <div className="text-sm text-muted-foreground">Relatórios PDF</div>
          </CardContent>
        </Card>
      </div>

      {/* Upload e Filtros */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle>Gerenciar Arquivos</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center gap-2 mb-4">
            <Input type="file" onChange={(e) => setFileToUpload(e.target.files?.[0] || null)} />
            <Button onClick={handleUpload} disabled={uploading || !fileToUpload}>
              <Upload className="w-4 h-4 mr-2" />
              {uploading ? "Enviando..." : "Enviar Arquivo"}
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="relative col-span-1 md:col-span-2">
              <Search className="absolute left-3 top-3 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Buscar arquivo por nome..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            <select
              value={filterType || ""}
              onChange={(e) => setFilterType(e.target.value || null)}
              className="px-3 py-2 border rounded-lg bg-background"
            >
              <option value="">Todos os tipos</option>
              <option value="xlsx">Excel (.xlsx)</option>
              <option value="pdf">PDF (.pdf)</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* File List Table */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center space-x-2">
            <FileText className="w-5 h-5" />
            <span>Arquivos</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Nome do Arquivo</TableHead>
                <TableHead>Tipo</TableHead>
                <TableHead>Tamanho</TableHead>
                <TableHead>Enviado em</TableHead>
                <TableHead>Enviado por</TableHead>
                <TableHead className="text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-8 text-muted-foreground">Carregando...</TableCell>
                </TableRow>
              ) : paginatedFiles.length > 0 ? (
                paginatedFiles.map((file) => (
                  <TableRow key={file.id}>
                    <TableCell className="font-medium flex items-center gap-2">
                      {getFileIcon(file.type)}
                      {file.name}
                    </TableCell>
                    <TableCell>
                      {getFileTypeBadge(file.type)}
                    </TableCell>
                    <TableCell>{formatFileSize(file.size)}</TableCell>
                    <TableCell className="text-sm">{formatDate(file.uploadedAt)}</TableCell>
                    <TableCell>{file.uploadedBy || "-"}</TableCell>
                    <TableCell className="text-right space-x-2">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDownload(file.id, file.name)}
                        className="text-primary hover:text-primary"
                      >
                        <Download className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => handleDelete(file.id, file.name)}
                        className="text-destructive hover:text-destructive"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-8 text-muted-foreground">
                    Nenhum arquivo encontrado
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>

          {/* Pagination */}
          {filteredFiles.length > ITEMS_PER_PAGE && (
            <div className="flex items-center justify-between mt-6 pt-4 border-t">
              <div className="text-sm text-muted-foreground">
                Mostrando {startIndex + 1} a {Math.min(endIndex, filteredFiles.length)} de {filteredFiles.length}
              </div>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                  disabled={currentPage === 1}
                >
                  <ChevronLeft className="w-4 h-4" />
                </Button>

                <div className="flex items-center gap-1">
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                    <Button
                      key={page}
                      variant={currentPage === page ? "default" : "outline"}
                      size="sm"
                      onClick={() => setCurrentPage(page)}
                      className="min-w-10"
                    >
                      {page}
                    </Button>
                  ))}
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                  disabled={currentPage === totalPages}
                >
                  <ChevronRight className="w-4 h-4" />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default HistoryFilesPage;
