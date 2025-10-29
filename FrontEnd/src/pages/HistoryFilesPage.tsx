import { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Upload, File, Trash2, Download } from "lucide-react";

interface FileEntry {
  id: string;
  name: string;
  uploadedAt: string;
  size: number;
}

const HistoryFilesPage = () => {
  const [reports] = useState<any[]>([]); // TODO: carregar histórico real se necessário
  const [files, setFiles] = useState<FileEntry[]>([]);

  useEffect(() => {
    // TODO: Integrar com API: GET /files
    setFiles([]);
  }, []);

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle>Histórico & Arquivos</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="mb-4 flex items-center gap-2">
            <Button variant="outline" disabled>
              <Upload className="w-4 h-4 mr-2" />
              Enviar Arquivo (aguardando API)
            </Button>
          </div>

          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Arquivo</TableHead>
                <TableHead>Enviado em</TableHead>
                <TableHead>Tamanho</TableHead>
                <TableHead className="text-right">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {files.length === 0 && (
                <TableRow>
                  <TableCell colSpan={4} className="text-sm text-muted-foreground">
                    Nenhum arquivo enviado ainda
                  </TableCell>
                </TableRow>
              )}
              {files.map((f) => (
                <TableRow key={f.id}>
                  <TableCell className="flex items-center gap-2"><File className="w-4 h-4" /> {f.name}</TableCell>
                  <TableCell>{new Date(f.uploadedAt).toLocaleString()}</TableCell>
                  <TableCell>{(f.size / 1024).toFixed(1)} KB</TableCell>
                  <TableCell className="text-right space-x-2">
                    <Button variant="ghost" size="sm" disabled><Download className="w-4 h-4" /></Button>
                    <Button variant="ghost" size="sm" disabled><Trash2 className="w-4 h-4" /></Button>
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

export default HistoryFilesPage;