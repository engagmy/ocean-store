import dayjs from 'dayjs/esm';

import { ISaleOperation, NewSaleOperation } from './sale-operation.model';

export const sampleWithRequiredData: ISaleOperation = {
  id: 16189,
  date: dayjs('2025-07-27T08:58'),
  totalQuantity: 20581,
  totalAmount: 11546.63,
  grandTotal: 23306.4,
};

export const sampleWithPartialData: ISaleOperation = {
  id: 25399,
  date: dayjs('2025-07-27T00:25'),
  totalQuantity: 13117,
  totalAmount: 26418.91,
  grandTotal: 190.42,
  createdDate: dayjs('2025-07-27T13:33'),
  lastModifiedBy: 'jump',
  lastModifiedDate: dayjs('2025-07-27T13:54'),
};

export const sampleWithFullData: ISaleOperation = {
  id: 11858,
  date: dayjs('2025-07-27T05:29'),
  totalQuantity: 16586,
  totalAmount: 24857.6,
  totalDiscount: 21905.6,
  grandTotal: 24379.19,
  active: false,
  createdBy: 'given',
  createdDate: dayjs('2025-07-26T18:32'),
  lastModifiedBy: 'duh seldom ack',
  lastModifiedDate: dayjs('2025-07-26T18:20'),
};

export const sampleWithNewData: NewSaleOperation = {
  date: dayjs('2025-07-27T03:52'),
  totalQuantity: 24043,
  totalAmount: 27893.57,
  grandTotal: 30878.75,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
