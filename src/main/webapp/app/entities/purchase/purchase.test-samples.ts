import dayjs from 'dayjs/esm';

import { IPurchase, NewPurchase } from './purchase.model';

export const sampleWithRequiredData: IPurchase = {
  id: 5165,
  quantity: 15694,
  unitPrice: 15863.02,
  lineTotal: 6439.9,
};

export const sampleWithPartialData: IPurchase = {
  id: 23377,
  productName: 'especially',
  quantity: 7887,
  unitPrice: 30266.89,
  lineTotal: 1079.69,
  active: false,
  lastModifiedDate: dayjs('2025-07-27T10:59'),
};

export const sampleWithFullData: IPurchase = {
  id: 28770,
  productName: 'flood',
  quantity: 23756,
  unitPrice: 25268.13,
  lineTotal: 8849.05,
  active: false,
  createdBy: 'hm affect duh',
  createdDate: dayjs('2025-07-26T17:02'),
  lastModifiedBy: 'unless lazy before',
  lastModifiedDate: dayjs('2025-07-26T19:41'),
};

export const sampleWithNewData: NewPurchase = {
  quantity: 3334,
  unitPrice: 20406.25,
  lineTotal: 31584.33,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
