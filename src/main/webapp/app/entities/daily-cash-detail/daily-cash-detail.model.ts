import dayjs from 'dayjs/esm';
import { IDailyCashReconciliation } from 'app/entities/daily-cash-reconciliation/daily-cash-reconciliation.model';
import { CashTransactionType } from 'app/entities/enumerations/cash-transaction-type.model';

export interface IDailyCashDetail {
  id: number;
  type?: keyof typeof CashTransactionType | null;
  referenceId?: number | null;
  referenceType?: string | null;
  amount?: number | null;
  description?: string | null;
  timestamp?: dayjs.Dayjs | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  dailyCashReconciliation?: Pick<IDailyCashReconciliation, 'id'> | null;
}

export type NewDailyCashDetail = Omit<IDailyCashDetail, 'id'> & { id: null };
