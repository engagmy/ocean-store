import dayjs from 'dayjs/esm';

export interface IBill {
  id: number;
  billNumber?: string | null;
  date?: dayjs.Dayjs | null;
  totalAmount?: number | null;
  taxAmount?: number | null;
  discountAmount?: number | null;
  paidAmount?: number | null;
  dueAmount?: number | null;
  notes?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewBill = Omit<IBill, 'id'> & { id: null };
