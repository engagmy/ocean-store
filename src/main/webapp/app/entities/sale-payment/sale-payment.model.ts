import dayjs from 'dayjs/esm';
import { ISale } from 'app/entities/sale/sale.model';

export interface ISalePayment {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  sale?: Pick<ISale, 'id'> | null;
}

export type NewSalePayment = Omit<ISalePayment, 'id'> & { id: null };
