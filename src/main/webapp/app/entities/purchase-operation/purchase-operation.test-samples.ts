import dayjs from 'dayjs/esm';

import { IPurchaseOperation, NewPurchaseOperation } from './purchase-operation.model';

export const sampleWithRequiredData: IPurchaseOperation = {
  id: 31053,
  date: dayjs('2025-07-27T05:12'),
  totalQuantity: 26851,
  totalAmount: 18099.06,
  grandTotal: 26510.2,
};

export const sampleWithPartialData: IPurchaseOperation = {
  id: 9656,
  date: dayjs('2025-07-26T19:54'),
  supplierInvoiceNo: 'beyond',
  totalQuantity: 8572,
  totalAmount: 6372.39,
  grandTotal: 28627.03,
  active: true,
  createdBy: 'regarding pension upbeat',
  lastModifiedBy: 'hm interior imaginative',
  lastModifiedDate: dayjs('2025-07-26T22:57'),
};

export const sampleWithFullData: IPurchaseOperation = {
  id: 3035,
  date: dayjs('2025-07-27T05:40'),
  supplierInvoiceNo: 'redress',
  totalQuantity: 19395,
  totalAmount: 2536.35,
  grandTotal: 13933.09,
  active: true,
  createdBy: 'while',
  createdDate: dayjs('2025-07-26T18:34'),
  lastModifiedBy: 'frantically',
  lastModifiedDate: dayjs('2025-07-26T17:33'),
};

export const sampleWithNewData: NewPurchaseOperation = {
  date: dayjs('2025-07-27T01:21'),
  totalQuantity: 14731,
  totalAmount: 22217.71,
  grandTotal: 29031.05,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
