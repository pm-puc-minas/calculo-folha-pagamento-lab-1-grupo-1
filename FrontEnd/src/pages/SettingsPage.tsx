import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setUser } from "@/store/slices/authSlice";

const SettingsPage = () => {
  const dispatch = useAppDispatch();
  const { user, accessToken } = useAppSelector((state) => state.auth);

  const [username, setUsername] = useState(user?.username ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async () => {
    setStatus(null);
    setError(null);

    if (newPassword && newPassword !== confirmPassword) {
      setError("Senhas não conferem");
      return;
    }

    try {
      const response = await fetch("/api/users/me", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
        },
        body: JSON.stringify({
          username,
          email,
          password: newPassword || undefined,
        }),
      });

      if (!response.ok) {
        throw new Error("Falha ao atualizar perfil");
      }

      const updated = await response.json();
      dispatch(setUser(updated));
      setStatus("Dados atualizados com sucesso.");
      setNewPassword("");
      setConfirmPassword("");
    } catch (e: any) {
      setError(e?.message || "Erro ao salvar dados");
    }
  };

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm max-w-3xl">
        <CardHeader>
          <CardTitle>Meus Dados</CardTitle>
          <p className="text-sm text-muted-foreground">Edite seus dados de usuário.</p>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm text-muted-foreground">Nome de usuário</label>
              <Input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="seu_usuario" />
            </div>
            <div>
              <label className="text-sm text-muted-foreground">E-mail</label>
              <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="voce@empresa.com" />
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Nova senha</label>
              <Input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Confirmar nova senha</label>
              <Input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
            </div>
          </div>

          {status && <p className="text-sm text-success mt-4">{status}</p>}
          {error && <p className="text-sm text-destructive mt-4">{error}</p>}

          <div className="mt-6">
            <Button onClick={handleSave}>Salvar alterações</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default SettingsPage;
