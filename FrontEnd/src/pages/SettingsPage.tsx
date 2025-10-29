import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setUser } from "@/store/slices/authSlice";

const SettingsPage = () => {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);

  const [username, setUsername] = useState(user?.username ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleSave = () => {
    // Atualiza somente no estado (sem backend ainda)
    if (user) {
      dispatch(setUser({ ...user, username, email }));
    }
    // TODO: Se newPassword informada e confirmada, enviar no PATCH quando houver API
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
              <Input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} placeholder="••••••••" />
            </div>
            <div>
              <label className="text-sm text-muted-foreground">Confirmar nova senha</label>
              <Input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} placeholder="••••••••" />
            </div>
          </div>

          <div className="mt-6">
            <Button onClick={handleSave}>Salvar alterações</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default SettingsPage;