import dayjs from 'dayjs/esm';

export interface ICustomer {
  id: number;
  name?: string | null;
  phone?: string | null;
  address?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
