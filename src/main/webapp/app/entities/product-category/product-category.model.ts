import dayjs from 'dayjs/esm';

export interface IProductCategory {
  id: number;
  name?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewProductCategory = Omit<IProductCategory, 'id'> & { id: null };
