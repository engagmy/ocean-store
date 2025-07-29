import dayjs from 'dayjs/esm';

import { IInventoryTransaction, NewInventoryTransaction } from './inventory-transaction.model';

export const sampleWithRequiredData: IInventoryTransaction = {
  id: 27092,
  date: dayjs('2025-07-27T04:54'),
  type: 'STOCK_IN',
  quantity: 14078,
};

export const sampleWithPartialData: IInventoryTransaction = {
  id: 3519,
  date: dayjs('2025-07-27T11:53'),
  type: 'RETURN',
  quantity: 28270,
};

export const sampleWithFullData: IInventoryTransaction = {
  id: 9514,
  date: dayjs('2025-07-27T16:30'),
  type: 'STOCK_OUT',
  quantity: 3849,
  active: true,
  createdBy: 'motivate convalesce',
  createdDate: dayjs('2025-07-27T14:53'),
  lastModifiedBy: 'boohoo yowza spherical',
  lastModifiedDate: dayjs('2025-07-26T20:25'),
};

export const sampleWithNewData: NewInventoryTransaction = {
  date: dayjs('2025-07-27T02:40'),
  type: 'STOCK_OUT',
  quantity: 13984,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
