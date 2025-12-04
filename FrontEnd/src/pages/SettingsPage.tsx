import { useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setUser } from "@/store/slices/authSlice";
import { Settings, Lock, User, Mail, Shield, Bell, Palette } from "lucide-react";
import { toast } from "@/hooks/use-toast";

const SettingsPage = () => {
  const dispatch = useAppDispatch();
  const user = useAppSelector((state) => state.auth.user);

  const [username, setUsername] = useState(user?.username ?? "");
  const [email, setEmail] = useState(user?.email ?? "");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [preferences, setPreferences] = useState({
    darkMode: false,
    emailNotifications: true,
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSaving, setIsSaving] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [sessionError, setSessionError] = useState<string | null>(null);

  const accessToken = useMemo(
    () => (typeof localStorage !== "undefined" ? localStorage.getItem("accessToken") : null),
    []
  );

  useEffect(() => {
    const savedUser = typeof localStorage !== "undefined" ? localStorage.getItem("user") : null;
    if (!user && savedUser) {
      try {
        const parsed = JSON.parse(savedUser);
        dispatch(setUser(parsed));
      } catch {
        /* ignore */
      }
    }
    const fetchProfile = async () => {
      if (!accessToken) return;
      try {
        const res = await fetch("/api/users/me", {
          headers: { Authorization: `Bearer ${accessToken}` },
        });
        if (res.status === 401) {
          setSessionError("Sessão expirada. Faça login novamente.");
          return;
        }
        if (!res.ok) return;
        const data = await res.json();
        dispatch(setUser(data));
        setUsername(data.username ?? "");
        setEmail(data.email ?? "");
        if (data.preferences) {
          setPreferences({
            darkMode: !!data.preferences.darkMode,
            emailNotifications: !!data.preferences.emailNotifications,
          });
        }
      } catch {
        /* ignore */
      }
    };
    fetchProfile();
  }, [user, dispatch, accessToken]);

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!username.trim()) {
      newErrors.username = "Nome de usuário é obrigatório";
    } else if (/\s/.test(username)) {
      newErrors.username = "Não use espaços no nome de usuário";
    }

    if (!email.trim()) {
      newErrors.email = "E-mail é obrigatório";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newErrors.email = "E-mail inválido";
    }

    if (newPassword || confirmPassword) {
      if (!currentPassword) {
        newErrors.currentPassword = "Digite sua senha atual para alterar a senha";
      }
      if (newPassword.length < 6) {
        newErrors.newPassword = "Nova senha deve ter no mínimo 6 caracteres";
      }
      if (newPassword !== confirmPassword) {
        newErrors.confirmPassword = "Senhas não conferem";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSave = async () => {
    if (!validateForm()) {
      toast({
        title: "Erro na validação",
        description: "Por favor, corrija os erros antes de salvar",
        variant: "destructive"
      });
      return;
    }

    setIsSaving(true);
    setSuccessMessage("");

    try {
      const payload: Record<string, any> = {
        username,
        email,
        preferences,
      };
      if (newPassword) {
        payload.currentPassword = currentPassword;
        payload.newPassword = newPassword;
      }

      const res = await fetch("/api/users/me", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
        },
        body: JSON.stringify(payload),
      });
      if (res.status === 401) {
        setSessionError("Sessão expirada. Faça login novamente.");
        toast({
          title: "Sessão expirada",
          description: "Faça login novamente para salvar as alterações.",
          variant: "destructive",
        });
        setIsSaving(false);
        return;
      }
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Falha ao salvar configurações.");
      }

      const updated = await res.json();
      dispatch(setUser(updated));

      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
      setSuccessMessage("Configurações salvas com sucesso!");

      toast({
        title: "Sucesso!",
        description: "Suas configurações foram atualizadas",
      });

      setTimeout(() => setSuccessMessage(""), 5000);
    } catch (error) {
      const msg = (error as Error)?.message || "Falha ao salvar configurações. Tente novamente.";
      toast({
        title: "Erro",
        description: msg,
        variant: "destructive"
      });
    } finally {
      setIsSaving(false);
    }
  };

  const isFormInvalid = Object.values(errors).some(Boolean);

  if (!user && !accessToken) {
    return (
      <div className="flex-1 bg-gray-50 min-h-screen p-6">
        <div className="text-center mt-10">
          <p className="text-lg font-semibold">Faça login para acessar as configurações.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 bg-gray-50 min-h-screen p-6 space-y-6">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center space-x-3 mb-2">
          <div className="w-10 h-10 bg-gradient-to-br from-primary to-accent rounded-lg flex items-center justify-center">
            <Settings className="w-6 h-6 text-white" />
          </div>
          <h1 className="text-3xl font-bold">Configurações</h1>
        </div>
        <p className="text-muted-foreground ml-13">Gerencie suas preferências e dados de conta</p>
      </div>

      {sessionError && (
        <Alert className="bg-destructive/10 border-destructive text-destructive">
          <AlertDescription>{sessionError}</AlertDescription>
        </Alert>
      )}

      {/* Success Message */}
      {successMessage && (
        <Alert className="bg-success/10 border-success text-success">
          <AlertDescription>{successMessage}</AlertDescription>
        </Alert>
      )}

      {/* Dados Pessoais */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader className="pb-4">
          <div className="flex items-center space-x-2">
            <User className="w-5 h-5 text-primary" />
            <CardTitle>Dados Pessoais</CardTitle>
          </div>
          <p className="text-sm text-muted-foreground">Edite suas informações de perfil</p>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <Label htmlFor="username">Nome de Usuário</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => {
                  setUsername(e.target.value);
                  if (errors.username) setErrors({ ...errors, username: "" });
                }}
                placeholder="seu_usuario"
                className={errors.username ? "border-destructive" : ""}
              />
              {errors.username && (
                <p className="text-xs text-destructive">{errors.username}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">E-mail</Label>
              <Input
                id="email"
                type="email"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (errors.email) setErrors({ ...errors, email: "" });
                }}
                placeholder="voce@empresa.com"
                className={errors.email ? "border-destructive" : ""}
              />
              {errors.email && (
                <p className="text-xs text-destructive">{errors.email}</p>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Segurança */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader className="pb-4">
          <div className="flex items-center space-x-2">
            <Lock className="w-5 h-5 text-primary" />
            <CardTitle>Segurança</CardTitle>
          </div>
          <p className="text-sm text-muted-foreground">Altere sua senha e configure autenticação</p>
        </CardHeader>
        <CardContent className="space-y-4">
          {(newPassword || confirmPassword) && (
            <div className="space-y-2">
              <Label htmlFor="currentPassword">Senha Atual</Label>
              <Input
                id="currentPassword"
                type="password"
                value={currentPassword}
                onChange={(e) => {
                  setCurrentPassword(e.target.value);
                  if (errors.currentPassword) setErrors({ ...errors, currentPassword: "" });
                }}
                placeholder="Digite sua senha atual"
                className={errors.currentPassword ? "border-destructive" : ""}
              />
              {errors.currentPassword && (
                <p className="text-xs text-destructive">{errors.currentPassword}</p>
              )}
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <Label htmlFor="newPassword">Nova Senha</Label>
              <Input
                id="newPassword"
                type="password"
                value={newPassword}
                onChange={(e) => {
                  setNewPassword(e.target.value);
                  if (errors.newPassword) setErrors({ ...errors, newPassword: "" });
                }}
                placeholder="••••••••"
                className={errors.newPassword ? "border-destructive" : ""}
              />
              {errors.newPassword && (
                <p className="text-xs text-destructive">{errors.newPassword}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirmar Senha</Label>
              <Input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => {
                  setConfirmPassword(e.target.value);
                  if (errors.confirmPassword) setErrors({ ...errors, confirmPassword: "" });
                }}
                placeholder="••••••••"
                className={errors.confirmPassword ? "border-destructive" : ""}
              />
              {errors.confirmPassword && (
                <p className="text-xs text-destructive">{errors.confirmPassword}</p>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Preferências */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader className="pb-4">
          <div className="flex items-center space-x-2">
            <Palette className="w-5 h-5 text-primary" />
            <CardTitle>Preferências</CardTitle>
          </div>
          <p className="text-sm text-muted-foreground">Personalize sua experiência</p>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between py-2">
            <div>
              <p className="font-medium">Tema Escuro</p>
              <p className="text-sm text-muted-foreground">Use tema escuro no sistema</p>
            </div>
            <input
              type="checkbox"
              className="w-5 h-5"
              checked={preferences.darkMode}
              onChange={(e) => setPreferences((p) => ({ ...p, darkMode: e.target.checked }))}
            />
          </div>
          <Separator />
          <div className="flex items-center justify-between py-2">
            <div>
              <p className="font-medium">Notificações por E-mail</p>
              <p className="text-sm text-muted-foreground">Receba notificações sobre folhas de pagamento</p>
            </div>
            <input
              type="checkbox"
              className="w-5 h-5"
              checked={preferences.emailNotifications}
              onChange={(e) => setPreferences((p) => ({ ...p, emailNotifications: e.target.checked }))}
            />
          </div>
        </CardContent>
      </Card>

      {/* Ações */}
      <div className="flex items-center gap-2">
        <Button
          onClick={handleSave}
          disabled={isSaving || isFormInvalid}
          className="bg-gradient-to-r from-primary to-accent hover:opacity-90"
        >
          {isSaving ? "Salvando..." : "Salvar Alterações"}
        </Button>
        <Button variant="outline">
          Cancelar
        </Button>
      </div>
    </div>
  );
};

export default SettingsPage;
