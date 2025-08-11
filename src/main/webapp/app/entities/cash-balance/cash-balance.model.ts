import dayjs from 'dayjs/esm';

export interface ICashBalance {
  id: number;
  available?: number | null;
  lastUpdated?: dayjs.Dayjs | null;
  notes?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewCashBalance = Omit<ICashBalance, 'id'> & { id: null };
