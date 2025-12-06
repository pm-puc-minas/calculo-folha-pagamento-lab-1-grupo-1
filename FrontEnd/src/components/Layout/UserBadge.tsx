import { cn } from "@/lib/utils";
import { getInitials } from "@/utils/formatters";

interface UserBadgeProps {
  name?: string | null;
  email?: string | null;
  className?: string;
}

export const UserBadge = ({ name, email, className }: UserBadgeProps) => {
  const displayName = name?.trim() || email || "Usuário";
  const initials = getInitials(name || email) || "U";

  return (
    <div className={cn("flex items-center space-x-2 bg-blue-50 px-3 py-2 rounded-lg", className)}>
      <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
        <span className="text-white text-sm font-medium">{initials}</span>
      </div>
      <span className="text-sm font-medium truncate max-w-[160px]">{displayName}</span>
    </div>
  );
};
