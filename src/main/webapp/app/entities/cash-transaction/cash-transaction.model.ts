import dayjs from 'dayjs/esm';
import { CashTransactionType } from 'app/entities/enumerations/cash-transaction-type.model';

export interface ICashTransaction {
  id: number;
  date?: dayjs.Dayjs | null;
  amount?: number | null;
  type?: keyof typeof CashTransactionType | null;
  reason?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewCashTransaction = Omit<ICashTransaction, 'id'> & { id: null };
