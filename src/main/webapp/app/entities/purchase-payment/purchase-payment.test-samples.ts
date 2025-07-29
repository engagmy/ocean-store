import dayjs from 'dayjs/esm';

import { IPurchasePayment, NewPurchasePayment } from './purchase-payment.model';

export const sampleWithRequiredData: IPurchasePayment = {
  id: 18913,
  date: dayjs('2025-07-27T00:51'),
  amount: 4572.83,
};

export const sampleWithPartialData: IPurchasePayment = {
  id: 7054,
  date: dayjs('2025-07-27T00:09'),
  amount: 1076.41,
  active: true,
  createdDate: dayjs('2025-07-27T08:45'),
  lastModifiedDate: dayjs('2025-07-26T20:16'),
};

export const sampleWithFullData: IPurchasePayment = {
  id: 15051,
  date: dayjs('2025-07-27T14:04'),
  amount: 3173.82,
  active: false,
  createdBy: 'inventory lest',
  createdDate: dayjs('2025-07-27T07:23'),
  lastModifiedBy: 'upbeat defiantly hidden',
  lastModifiedDate: dayjs('2025-07-26T22:12'),
};

export const sampleWithNewData: NewPurchasePayment = {
  date: dayjs('2025-07-26T17:19'),
  amount: 26439.14,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
