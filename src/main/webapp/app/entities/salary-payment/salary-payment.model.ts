import dayjs from 'dayjs/esm';
import { IEmployee } from 'app/entities/employee/employee.model';

export interface ISalaryPayment {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  employee?: Pick<IEmployee, 'id'> | null;
}

export type NewSalaryPayment = Omit<ISalaryPayment, 'id'> & { id: null };
