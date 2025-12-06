import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { User, MapPin, Phone, Mail, Calendar, Briefcase, DollarSign, Users, Settings, Save, X } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { formatCPF, formatPhone } from "@/utils/formatters";

interface EmployeeData {
  id: string;
  name: string;
  cpf: string;
  rg: string;
  email: string;
  phone: string;
  address: {
    street: string;
    number: string;
    complement?: string;
    neighborhood: string;
    city: string;
    state: string;
    zipCode: string;
  };
  position: string;
  department: string;
  admissionDate: string;
  grossSalary: number;
  hoursPerDay: number;
  daysPerWeek: number;
  dependents: number;
  transportVoucherValue: number;
  mealVoucherDaily: number;
  workDaysInMonth: number;
  isDangerous: boolean;
  unhealthyLevel: 'none' | 'low' | 'medium' | 'high';
  pensionAlimony: number;
  bankAccount: {
    bank: string;
    agency: string;
    account: string;
    accountType: 'checking' | 'savings';
  };
  emergencyContact: {
    name: string;
    relationship: string;
    phone: string;
  };
  isActive: boolean;
}

interface EmployeeEditorProps {
  employee?: EmployeeData;
  onSave: (employee: EmployeeData) => void;
  onCancel: () => void;
}

export const EmployeeEditor = ({ employee, onSave, onCancel }: EmployeeEditorProps) => {
  const [formData, setFormData] = useState<EmployeeData>({
    id: employee?.id || '',
    name: employee?.name || '',
    cpf: employee?.cpf || '',
    rg: employee?.rg || '',
    email: employee?.email || '',
    phone: employee?.phone || '',
    address: {
      street: employee?.address?.street || '',
      number: employee?.address?.number || '',
      complement: employee?.address?.complement || '',
      neighborhood: employee?.address?.neighborhood || '',
      city: employee?.address?.city || '',
      state: employee?.address?.state || '',
      zipCode: employee?.address?.zipCode || ''
    },
    position: employee?.position || '',
    department: employee?.department || '',
    admissionDate: employee?.admissionDate || '',
    grossSalary: employee?.grossSalary || 0,
    hoursPerDay: employee?.hoursPerDay || 8,
    daysPerWeek: employee?.daysPerWeek || 5,
    dependents: employee?.dependents || 0,
    transportVoucherValue: employee?.transportVoucherValue || 0,
    mealVoucherDaily: employee?.mealVoucherDaily || 0,
    workDaysInMonth: employee?.workDaysInMonth || 22,
    isDangerous: employee?.isDangerous || false,
    unhealthyLevel: employee?.unhealthyLevel || 'none',
    pensionAlimony: employee?.pensionAlimony || 0,
    bankAccount: {
      bank: employee?.bankAccount?.bank || '',
      agency: employee?.bankAccount?.agency || '',
      account: employee?.bankAccount?.account || '',
      accountType: employee?.bankAccount?.accountType || 'checking'
    },
    emergencyContact: {
      name: employee?.emergencyContact?.name || '',
      relationship: employee?.emergencyContact?.relationship || '',
      phone: employee?.emergencyContact?.phone || ''
    },
    isActive: employee?.isActive ?? true
  });

  const handleInputChange = (field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleNestedInputChange = (parent: keyof EmployeeData, field: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [parent]: {
        ...(prev[parent] as any),
        [field]: value
      }
    }));
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const getUserInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  const handleSave = () => {
    if (!formData.name || !formData.cpf || !formData.position) {
      toast({
        title: "Erro na validação",
        description: "Por favor, preencha os campos obrigatórios: Nome, CPF e Cargo.",
        variant: "destructive"
      });
      return;
    }

    onSave(formData);
    toast({
      title: "Funcionário salvo",
      description: `Os dados de ${formData.name} foram salvos com sucesso.`
    });
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {/* Header */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <Avatar className="w-16 h-16">
                <AvatarFallback className="text-lg font-bold bg-gradient-to-br from-primary to-accent text-white">
                  {getUserInitials(formData.name || 'NN')}
                </AvatarFallback>
              </Avatar>
              <div>
                <CardTitle className="text-2xl">
                  {employee ? 'Editar Funcionário' : 'Novo Funcionário'}
                </CardTitle>
                <p className="text-muted-foreground">
                  {employee ? `Editando dados de ${formData.name}` : 'Cadastrar novo funcionário no sistema'}
                </p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <Badge variant={formData.isActive ? "default" : "secondary"}>
                {formData.isActive ? 'Ativo' : 'Inativo'}
              </Badge>
            </div>
          </div>
        </CardHeader>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Personal Information */}
        <Card className="lg:col-span-2 border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <User className="w-5 h-5" />
              <span>Informações Pessoais</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="name">Nome Completo *</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => handleInputChange('name', e.target.value)}
                  placeholder="Digite o nome completo"
                />
              </div>
              <div>
                <Label htmlFor="cpf">CPF *</Label>
                <Input
                  id="cpf"
                  value={formData.cpf}
                  onChange={(e) => handleInputChange('cpf', formatCPF(e.target.value))}
                  placeholder="000.000.000-00"
                />
              </div>
              <div>
                <Label htmlFor="rg">RG</Label>
                <Input
                  id="rg"
                  value={formData.rg}
                  onChange={(e) => handleInputChange('rg', e.target.value)}
                  placeholder="00.000.000-0"
                />
              </div>
              <div>
                <Label htmlFor="phone">Telefone</Label>
                <Input
                  id="phone"
                  value={formData.phone}
                  onChange={(e) => handleInputChange('phone', formatPhone(e.target.value))}
                  placeholder="(00) 00000-0000"
                />
              </div>
              <div className="md:col-span-2">
                <Label htmlFor="email">E-mail</Label>
                <Input
                  id="email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  placeholder="email@empresa.com"
                />
              </div>
            </div>

            <Separator />

            {/* Address */}
            <div className="space-y-4">
              <h3 className="font-semibold flex items-center space-x-2">
                <MapPin className="w-4 h-4" />
                <span>Endereço</span>
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="md:col-span-2">
                  <Label htmlFor="street">Rua</Label>
                  <Input
                    id="street"
                    value={formData.address.street}
                    onChange={(e) => handleNestedInputChange('address', 'street', e.target.value)}
                    placeholder="Nome da rua"
                  />
                </div>
                <div>
                  <Label htmlFor="number">Número</Label>
                  <Input
                    id="number"
                    value={formData.address.number}
                    onChange={(e) => handleNestedInputChange('address', 'number', e.target.value)}
                    placeholder="000"
                  />
                </div>
                <div>
                  <Label htmlFor="zipCode">CEP</Label>
                  <Input
                    id="zipCode"
                    value={formData.address.zipCode}
                    onChange={(e) => handleNestedInputChange('address', 'zipCode', e.target.value)}
                    placeholder="00000-000"
                  />
                </div>
                <div>
                  <Label htmlFor="complement">Complemento</Label>
                  <Input
                    id="complement"
                    value={formData.address.complement}
                    onChange={(e) => handleNestedInputChange('address', 'complement', e.target.value)}
                    placeholder="Apto, sala, etc."
                  />
                </div>
                <div>
                  <Label htmlFor="neighborhood">Bairro</Label>
                  <Input
                    id="neighborhood"
                    value={formData.address.neighborhood}
                    onChange={(e) => handleNestedInputChange('address', 'neighborhood', e.target.value)}
                    placeholder="Nome do bairro"
                  />
                </div>
                <div>
                  <Label htmlFor="city">Cidade</Label>
                  <Input
                    id="city"
                    value={formData.address.city}
                    onChange={(e) => handleNestedInputChange('address', 'city', e.target.value)}
                    placeholder="Nome da cidade"
                  />
                </div>
                <div>
                  <Label htmlFor="state">Estado</Label>
                  <Select
                    value={formData.address.state}
                    onValueChange={(value) => handleNestedInputChange('address', 'state', value)}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="UF" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="AC">AC</SelectItem>
                      <SelectItem value="AL">AL</SelectItem>
                      <SelectItem value="AP">AP</SelectItem>
                      <SelectItem value="AM">AM</SelectItem>
                      <SelectItem value="BA">BA</SelectItem>
                      <SelectItem value="CE">CE</SelectItem>
                      <SelectItem value="DF">DF</SelectItem>
                      <SelectItem value="ES">ES</SelectItem>
                      <SelectItem value="GO">GO</SelectItem>
                      <SelectItem value="MA">MA</SelectItem>
                      <SelectItem value="MT">MT</SelectItem>
                      <SelectItem value="MS">MS</SelectItem>
                      <SelectItem value="MG">MG</SelectItem>
                      <SelectItem value="PA">PA</SelectItem>
                      <SelectItem value="PB">PB</SelectItem>
                      <SelectItem value="PR">PR</SelectItem>
                      <SelectItem value="PE">PE</SelectItem>
                      <SelectItem value="PI">PI</SelectItem>
                      <SelectItem value="RJ">RJ</SelectItem>
                      <SelectItem value="RN">RN</SelectItem>
                      <SelectItem value="RS">RS</SelectItem>
                      <SelectItem value="RO">RO</SelectItem>
                      <SelectItem value="RR">RR</SelectItem>
                      <SelectItem value="SC">SC</SelectItem>
                      <SelectItem value="SP">SP</SelectItem>
                      <SelectItem value="SE">SE</SelectItem>
                      <SelectItem value="TO">TO</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Professional Information */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <Briefcase className="w-5 h-5" />
              <span>Informações Profissionais</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="position">Cargo *</Label>
              <Input
                id="position"
                value={formData.position}
                onChange={(e) => handleInputChange('position', e.target.value)}
                placeholder="Cargo do funcionário"
              />
            </div>
            <div>
              <Label htmlFor="department">Departamento</Label>
              <Select
                value={formData.department}
                onValueChange={(value) => handleInputChange('department', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o departamento" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="rh">Recursos Humanos</SelectItem>
                  <SelectItem value="financeiro">Financeiro</SelectItem>
                  <SelectItem value="ti">Tecnologia da Informação</SelectItem>
                  <SelectItem value="vendas">Vendas</SelectItem>
                  <SelectItem value="marketing">Marketing</SelectItem>
                  <SelectItem value="operacoes">Operações</SelectItem>
                  <SelectItem value="juridico">Jurídico</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div>
              <Label htmlFor="admissionDate">Data de Admissão</Label>
              <Input
                id="admissionDate"
                type="date"
                value={formData.admissionDate}
                onChange={(e) => handleInputChange('admissionDate', e.target.value)}
              />
            </div>
            <div>
              <Label htmlFor="grossSalary">Salário Bruto</Label>
              <Input
                id="grossSalary"
                type="number"
                step="0.01"
                value={formData.grossSalary}
                onChange={(e) => handleInputChange('grossSalary', parseFloat(e.target.value) || 0)}
                placeholder="0.00"
              />
              <p className="text-xs text-muted-foreground mt-1">
                {formatCurrency(formData.grossSalary)}
              </p>
            </div>
            <Separator />
            <div className="space-y-3">
              <h4 className="font-medium">Jornada de Trabalho</h4>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <Label htmlFor="hoursPerDay">Horas/Dia</Label>
                  <Input
                    id="hoursPerDay"
                    type="number"
                    value={formData.hoursPerDay}
                    onChange={(e) => handleInputChange('hoursPerDay', parseInt(e.target.value) || 0)}
                  />
                </div>
                <div>
                  <Label htmlFor="daysPerWeek">Dias/Semana</Label>
                  <Input
                    id="daysPerWeek"
                    type="number"
                    value={formData.daysPerWeek}
                    onChange={(e) => handleInputChange('daysPerWeek', parseInt(e.target.value) || 0)}
                  />
                </div>
                <div className="col-span-2">
                  <Label htmlFor="workDaysInMonth">Dias Úteis/Mês</Label>
                  <Input
                    id="workDaysInMonth"
                    type="number"
                    value={formData.workDaysInMonth}
                    onChange={(e) => handleInputChange('workDaysInMonth', parseInt(e.target.value) || 0)}
                  />
                </div>
              </div>
            </div>
            <div className="flex items-center justify-between">
              <Label htmlFor="isActive">Funcionário Ativo</Label>
              <Switch
                id="isActive"
                checked={formData.isActive}
                onCheckedChange={(checked) => handleInputChange('isActive', checked)}
              />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Additional Information */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Benefits and Deductions */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <DollarSign className="w-5 h-5" />
              <span>Benefícios e Descontos</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="dependents">Dependentes</Label>
              <Input
                id="dependents"
                type="number"
                value={formData.dependents}
                onChange={(e) => handleInputChange('dependents', parseInt(e.target.value) || 0)}
              />
            </div>
            <div>
              <Label htmlFor="transportVoucherValue">Vale Transporte (mensal)</Label>
              <Input
                id="transportVoucherValue"
                type="number"
                step="0.01"
                value={formData.transportVoucherValue}
                onChange={(e) => handleInputChange('transportVoucherValue', parseFloat(e.target.value) || 0)}
              />
            </div>
            <div>
              <Label htmlFor="mealVoucherDaily">Vale Alimentação (diário)</Label>
              <Input
                id="mealVoucherDaily"
                type="number"
                step="0.01"
                value={formData.mealVoucherDaily}
                onChange={(e) => handleInputChange('mealVoucherDaily', parseFloat(e.target.value) || 0)}
              />
            </div>
            <div>
              <Label htmlFor="pensionAlimony">Pensão Alimentícia</Label>
              <Input
                id="pensionAlimony"
                type="number"
                step="0.01"
                value={formData.pensionAlimony}
                onChange={(e) => handleInputChange('pensionAlimony', parseFloat(e.target.value) || 0)}
              />
            </div>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <Label htmlFor="isDangerous">Adicional de Periculosidade (30%)</Label>
                <Switch
                  id="isDangerous"
                  checked={formData.isDangerous}
                  onCheckedChange={(checked) => handleInputChange('isDangerous', checked)}
                />
              </div>
              <div>
                <Label htmlFor="unhealthyLevel">Nível de Insalubridade</Label>
                <Select
                  value={formData.unhealthyLevel}
                  onValueChange={(value) => handleInputChange('unhealthyLevel', value)}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="none">Nenhum</SelectItem>
                    <SelectItem value="low">Grau Mínimo (10%)</SelectItem>
                    <SelectItem value="medium">Grau Médio (20%)</SelectItem>
                    <SelectItem value="high">Grau Máximo (40%)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Bank and Emergency */}
        <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <Settings className="w-5 h-5" />
              <span>Dados Bancários e Contato</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-4">
              <h4 className="font-medium">Dados Bancários</h4>
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <Label htmlFor="bank">Banco</Label>
                  <Input
                    id="bank"
                    value={formData.bankAccount.bank}
                    onChange={(e) => handleNestedInputChange('bankAccount', 'bank', e.target.value)}
                    placeholder="Nome do banco"
                  />
                </div>
                <div>
                  <Label htmlFor="agency">Agência</Label>
                  <Input
                    id="agency"
                    value={formData.bankAccount.agency}
                    onChange={(e) => handleNestedInputChange('bankAccount', 'agency', e.target.value)}
                    placeholder="0000"
                  />
                </div>
                <div>
                  <Label htmlFor="account">Conta</Label>
                  <Input
                    id="account"
                    value={formData.bankAccount.account}
                    onChange={(e) => handleNestedInputChange('bankAccount', 'account', e.target.value)}
                    placeholder="00000-0"
                  />
                </div>
                <div className="col-span-2">
                  <Label htmlFor="accountType">Tipo de Conta</Label>
                  <Select
                    value={formData.bankAccount.accountType}
                    onValueChange={(value) => handleNestedInputChange('bankAccount', 'accountType', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="checking">Conta Corrente</SelectItem>
                      <SelectItem value="savings">Conta Poupança</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>

            <Separator />

            <div className="space-y-4">
              <h4 className="font-medium flex items-center space-x-2">
                <Users className="w-4 h-4" />
                <span>Contato de Emergência</span>
              </h4>
              <div>
                <Label htmlFor="emergencyName">Nome</Label>
                <Input
                  id="emergencyName"
                  value={formData.emergencyContact.name}
                  onChange={(e) => handleNestedInputChange('emergencyContact', 'name', e.target.value)}
                  placeholder="Nome completo"
                />
              </div>
              <div>
                <Label htmlFor="emergencyRelationship">Parentesco</Label>
                <Input
                  id="emergencyRelationship"
                  value={formData.emergencyContact.relationship}
                  onChange={(e) => handleNestedInputChange('emergencyContact', 'relationship', e.target.value)}
                  placeholder="Ex: Cônjuge, Pai, Mãe, etc."
                />
              </div>
              <div>
                <Label htmlFor="emergencyPhone">Telefone</Label>
                <Input
                  id="emergencyPhone"
                  value={formData.emergencyContact.phone}
                  onChange={(e) => handleNestedInputChange('emergencyContact', 'phone', formatPhone(e.target.value))}
                  placeholder="(00) 00000-0000"
                />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Action Buttons */}
      <Card className="border-0 shadow-lg bg-card/50 backdrop-blur-sm">
        <CardContent className="p-6">
          <div className="flex items-center justify-end space-x-4">
            <Button variant="outline" onClick={onCancel} className="flex items-center space-x-2">
              <X className="w-4 h-4" />
              <span>Cancelar</span>
            </Button>
            <Button onClick={handleSave} className="flex items-center space-x-2 bg-gradient-to-r from-primary to-accent">
              <Save className="w-4 h-4" />
              <span>{employee ? 'Salvar Alterações' : 'Cadastrar Funcionário'}</span>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
