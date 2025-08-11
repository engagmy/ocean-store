import dayjs from 'dayjs/esm';
import { IBill } from 'app/entities/bill/bill.model';

export interface IPurchaseOperation {
  id: number;
  date?: dayjs.Dayjs | null;
  supplierInvoiceNo?: string | null;
  totalQuantity?: number | null;
  totalAmount?: number | null;
  grandTotal?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  bill?: Pick<IBill, 'id'> | null;
}

export type NewPurchaseOperation = Omit<IPurchaseOperation, 'id'> & { id: null };
