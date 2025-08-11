import dayjs from 'dayjs/esm';

export interface IEmployee {
  id: number;
  name?: string | null;
  jobTitle?: string | null;
  salary?: number | null;
  joinDate?: dayjs.Dayjs | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
