import dayjs from 'dayjs/esm';

import { ICashTransaction, NewCashTransaction } from './cash-transaction.model';

export const sampleWithRequiredData: ICashTransaction = {
  id: 9433,
  date: dayjs('2025-07-26T19:19'),
  amount: 19115.08,
  type: 'OWNER_DEPOSIT',
};

export const sampleWithPartialData: ICashTransaction = {
  id: 4817,
  date: dayjs('2025-07-27T04:30'),
  amount: 20159.27,
  type: 'OWNER_DEPOSIT',
  reason: 'delirious',
  active: true,
  createdBy: 'behind',
  createdDate: dayjs('2025-07-27T10:21'),
  lastModifiedBy: 'mask that bliss',
  lastModifiedDate: dayjs('2025-07-26T23:02'),
};

export const sampleWithFullData: ICashTransaction = {
  id: 12298,
  date: dayjs('2025-07-26T21:13'),
  amount: 8698.34,
  type: 'OTHER',
  reason: 'yet tightly which',
  active: true,
  createdBy: 'unwieldy',
  createdDate: dayjs('2025-07-27T00:49'),
  lastModifiedBy: 'about snappy till',
  lastModifiedDate: dayjs('2025-07-27T14:46'),
};

export const sampleWithNewData: NewCashTransaction = {
  date: dayjs('2025-07-27T01:38'),
  amount: 17928.41,
  type: 'PRODUCT_PURCHASE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
