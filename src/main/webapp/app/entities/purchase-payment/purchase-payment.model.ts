import dayjs from 'dayjs/esm';
import { IPurchase } from 'app/entities/purchase/purchase.model';

export interface IPurchasePayment {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  purchase?: Pick<IPurchase, 'id'> | null;
}

export type NewPurchasePayment = Omit<IPurchasePayment, 'id'> & { id: null };
