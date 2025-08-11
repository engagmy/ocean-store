import dayjs from 'dayjs/esm';

import { IDailyCashReconciliation, NewDailyCashReconciliation } from './daily-cash-reconciliation.model';

export const sampleWithRequiredData: IDailyCashReconciliation = {
  id: 844,
  date: dayjs('2025-07-26'),
};

export const sampleWithPartialData: IDailyCashReconciliation = {
  id: 22953,
  date: dayjs('2025-07-27'),
  openingBalance: 10066.11,
  totalSales: 6144.93,
  ownerDeposits: 29423.11,
  withdrawals: 2574.57,
  createdDate: dayjs('2025-07-26T18:31'),
  lastModifiedBy: 'damaged wide',
};

export const sampleWithFullData: IDailyCashReconciliation = {
  id: 3408,
  date: dayjs('2025-07-26'),
  openingBalance: 14146.77,
  totalSales: 30824.56,
  totalPurchases: 31057.08,
  totalSalaryPaid: 19641.8,
  ownerDeposits: 9873.73,
  withdrawals: 29991.03,
  closingBalance: 28154.45,
  notes: 'yippee a',
  active: true,
  createdBy: 'towards hawk profitable',
  createdDate: dayjs('2025-07-27T03:14'),
  lastModifiedBy: 'expansion',
  lastModifiedDate: dayjs('2025-07-26T22:12'),
};

export const sampleWithNewData: NewDailyCashReconciliation = {
  date: dayjs('2025-07-27'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
