import dayjs from 'dayjs/esm';

export interface IDailyCashReconciliation {
  id: number;
  date?: dayjs.Dayjs | null;
  openingBalance?: number | null;
  totalSales?: number | null;
  totalPurchases?: number | null;
  totalSalaryPaid?: number | null;
  ownerDeposits?: number | null;
  withdrawals?: number | null;
  closingBalance?: number | null;
  notes?: string | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewDailyCashReconciliation = Omit<IDailyCashReconciliation, 'id'> & { id: null };
