import dayjs from 'dayjs/esm';

export interface IBrand {
  id: number;
  name?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewBrand = Omit<IBrand, 'id'> & { id: null };
