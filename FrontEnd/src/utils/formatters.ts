export const formatCPF = (value: string) => {
  const digits = value.replace(/\D/g, "").slice(0, 11);
  const part1 = digits.slice(0, 3);
  const part2 = digits.slice(3, 6);
  const part3 = digits.slice(6, 9);
  const part4 = digits.slice(9, 11);

  let formatted = part1;
  if (part2) formatted += `.${part2}`;
  if (part3) formatted += `.${part3}`;
  if (part4) formatted += `-${part4}`;

  return formatted;
};

export const formatPhone = (value: string) => {
  const digits = value.replace(/\D/g, "").slice(0, 11);
  const area = digits.slice(0, 2);
  const remaining = digits.slice(2);

  if (!digits) return "";
  if (digits.length <= 2) return `(${area}`;
  if (digits.length <= 6) return `(${area}) ${remaining}`;
  if (digits.length <= 10) {
    return `(${area}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
  }

  return `(${area}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
};

export const getInitials = (value?: string) => {
  if (!value) return "";

  const cleaned = value.includes("@") ? value.split("@")[0] : value;
  const parts = cleaned.trim().split(/\s+/).filter(Boolean);

  if (parts.length === 0) return "";
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();

  return (parts[0][0] + parts[1][0]).toUpperCase();
};
