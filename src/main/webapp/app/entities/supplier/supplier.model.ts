import dayjs from 'dayjs/esm';

export interface ISupplier {
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

export type NewSupplier = Omit<ISupplier, 'id'> & { id: null };
