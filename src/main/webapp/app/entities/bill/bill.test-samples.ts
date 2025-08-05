import dayjs from 'dayjs/esm';

import { IBill, NewBill } from './bill.model';

export const sampleWithRequiredData: IBill = {
  id: 5076,
  billNumber: 'kiddingly warp',
  date: dayjs('2025-07-27T08:55'),
  totalAmount: 9838.03,
};

export const sampleWithPartialData: IBill = {
  id: 13568,
  billNumber: 'mmm',
  date: dayjs('2025-07-27T03:42'),
  totalAmount: 15267.15,
  discountAmount: 2874.71,
  paidAmount: 21039.36,
  active: false,
  createdDate: dayjs('2025-07-26T19:41'),
  lastModifiedBy: 'tenement faithfully',
  lastModifiedDate: dayjs('2025-07-27T09:28'),
};

export const sampleWithFullData: IBill = {
  id: 9481,
  billNumber: 'oof',
  date: dayjs('2025-07-27T06:47'),
  totalAmount: 21255.21,
  taxAmount: 5095.24,
  discountAmount: 10463.89,
  paidAmount: 31112.27,
  dueAmount: 15742.09,
  notes: 'daintily appropriate',
  active: false,
  createdBy: 'vice requirement anneal',
  createdDate: dayjs('2025-07-27T16:07'),
  lastModifiedBy: 'brr',
  lastModifiedDate: dayjs('2025-07-27T07:40'),
};

export const sampleWithNewData: NewBill = {
  billNumber: 'afore once',
  date: dayjs('2025-07-27T15:39'),
  totalAmount: 3069.07,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
