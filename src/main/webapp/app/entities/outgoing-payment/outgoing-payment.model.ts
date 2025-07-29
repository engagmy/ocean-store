import dayjs from 'dayjs/esm';

export interface IOutgoingPayment {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  reason?: string | null;
  notes?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewOutgoingPayment = Omit<IOutgoingPayment, 'id'> & { id: null };
