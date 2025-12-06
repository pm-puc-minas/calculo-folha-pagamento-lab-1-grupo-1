import * as React from "react"
import { Check, ChevronsUpDown } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { employeeService } from "@/services/employeeService"
import { Employee } from "@/types/employee"

interface EmployeeComboboxProps {
  value: string
  onChange: (value: string) => void
}

export function EmployeeCombobox({ value, onChange }: EmployeeComboboxProps) {
  const [open, setOpen] = React.useState(false)
  const [employees, setEmployees] = React.useState<Employee[]>([])
  const [loading, setLoading] = React.useState(false)
  const [search, setSearch] = React.useState("")

  React.useEffect(() => {
    const fetchEmployees = async () => {
        setLoading(true)
        try {
            const data = await employeeService.search(search)
            setEmployees(data)
        } catch (error) {
            console.error(error)
        } finally {
            setLoading(false)
        }
    }
    
    const debounce = setTimeout(() => {
        fetchEmployees()
    }, 300)

    return () => clearTimeout(debounce)
  }, [search])

  const selectedEmployee = employees.find((employee) => employee.id?.toString() === value)

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-full justify-between"
        >
          {value
            ? employees.find((employee) => employee.id?.toString() === value)?.name || selectedEmployee?.name || "Funcionário selecionado"
            : "Selecione um funcionário..."}
          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[300px] p-0">
        <Command shouldFilter={false}>
          <CommandInput placeholder="Buscar funcionário..." onValueChange={setSearch} />
          <CommandList>
            <CommandEmpty>{loading ? "Buscando..." : "Nenhum funcionário encontrado."}</CommandEmpty>
            <CommandGroup>
              {employees.map((employee) => (
                <CommandItem
                  key={employee.id}
                  value={employee.id?.toString()}
                  onSelect={(currentValue) => {
                    onChange(currentValue === value ? "" : currentValue)
                    setOpen(false)
                  }}
                >
                  <Check
                    className={cn(
                      "mr-2 h-4 w-4",
                      value === employee.id?.toString() ? "opacity-100" : "opacity-0"
                    )}
                  />
                  {employee.name}
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}
