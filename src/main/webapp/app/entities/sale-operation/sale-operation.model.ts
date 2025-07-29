import dayjs from 'dayjs/esm';
import { IBill } from 'app/entities/bill/bill.model';
import { ICustomer } from 'app/entities/customer/customer.model';

export interface ISaleOperation {
  id: number;
  date?: dayjs.Dayjs | null;
  totalQuantity?: number | null;
  totalAmount?: number | null;
  totalDiscount?: number | null;
  grandTotal?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  bill?: Pick<IBill, 'id'> | null;
  customer?: Pick<ICustomer, 'id'> | null;
}

export type NewSaleOperation = Omit<ISaleOperation, 'id'> & { id: null };
