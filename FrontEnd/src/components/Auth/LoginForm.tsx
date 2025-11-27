import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Building, Lock, User } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import rhProLogo from "@/assets/rh-pro-logo.png";

interface LoginFormProps {
  onLogin: (username: string, password: string) => void;
}

export const LoginForm = ({ onLogin }: LoginFormProps) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!username || !password) {
      toast({
        title: "Campos obrigatórios",
        description: "Por favor, preencha usuário e senha.",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);
    
    // Credenciais válidas para teste offline
    const validCredentials = [
      { username: "admin", password: "123456", email: "admin@payroll.com", role: "ADMIN" },
      { username: "user", password: "123456", email: "user@payroll.com", role: "USER" },
      { username: "gerente", password: "123456", email: "gerente@payroll.com", role: "ADMIN" },
    ];

    setTimeout(() => {
      const validUser = validCredentials.find(
        u => u.username === username && u.password === password
      );

      if (validUser) {
        // Armazena dados de teste no localStorage
        const mockUser = {
          id: Math.floor(Math.random() * 1000),
          username: validUser.username,
          email: validUser.email,
          role: validUser.role,
        };
        
        localStorage.setItem('user', JSON.stringify(mockUser));
        localStorage.setItem('accessToken', 'mock-token-' + Date.now());
        
        onLogin(validUser.username, validUser.password);
        toast({
          title: "Login realizado com sucesso!",
          description: `Bem-vindo ${validUser.username}! (Modo offline)`,
        });
      } else {
        toast({
          title: "Credenciais inválidas",
          description: "Usuário ou senha incorretos. Tente 'admin' / '123456'",
          variant: "destructive",
        });
      }
      setIsLoading(false);
    }, 1000);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary/5 via-background to-accent/5 p-4">
      <div className="w-full max-w-md space-y-6">
        {/* Logo/Header */}
        <div className="text-center space-y-4">
          <div className="mx-auto w-20 h-20 rounded-xl overflow-hidden shadow-lg ring-2 ring-primary/20">
            <img 
              src={rhProLogo} 
              alt="RH Pro Logo" 
              className="w-full h-full object-cover"
            />
          </div>
          <div>
            <h1 className="text-3xl font-bold bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
              RH Pro
            </h1>
            <p className="text-muted-foreground mt-2">
              Sistema de Gestão de Folha de Pagamento
            </p>
          </div>
        </div>

        {/* Login Card */}
        <Card className="border-0 shadow-xl bg-card/50 backdrop-blur-sm">
          <CardHeader className="space-y-2 text-center">
            <CardTitle className="text-2xl">Fazer Login</CardTitle>
            <CardDescription>
              Digite suas credenciais para acessar o sistema
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Usuário</Label>
                <div className="relative">
                  <User className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="username"
                    type="text"
                    placeholder="Digite seu usuário"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="pl-10 transition-all focus:ring-2 focus:ring-primary/20"
                  />
                </div>
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="password">Senha</Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="password"
                    type="password"
                    placeholder="Digite sua senha"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="pl-10 transition-all focus:ring-2 focus:ring-primary/20"
                  />
                </div>
              </div>

              <Button 
                type="submit" 
                className="w-full bg-gradient-to-r from-primary to-primary-hover hover:from-primary-hover hover:to-primary transition-all duration-300 shadow-lg hover:shadow-xl"
                disabled={isLoading}
              >
                {isLoading ? "Entrando..." : "Entrar no Sistema"}
              </Button>
            </form>

            <div className="mt-6 text-center text-sm text-muted-foreground space-y-3">
              <p className="font-semibold">Credenciais de Demonstração (Modo Offline):</p>
              <div className="bg-blue-50 p-3 rounded-lg space-y-2">
                <div className="font-mono text-xs">
                  <p><strong>Admin:</strong> admin / 123456</p>
                  <p><strong>User:</strong> user / 123456</p>
                  <p><strong>Gerente:</strong> gerente / 123456</p>
                </div>
              </div>
              <p className="text-xs italic">Não é necessário PostgreSQL ou servidor Java para testar</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
