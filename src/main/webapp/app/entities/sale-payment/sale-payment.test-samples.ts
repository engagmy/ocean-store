import dayjs from 'dayjs/esm';

import { ISalePayment, NewSalePayment } from './sale-payment.model';

export const sampleWithRequiredData: ISalePayment = {
  id: 15050,
  date: dayjs('2025-07-26T19:11'),
  amount: 19314.89,
};

export const sampleWithPartialData: ISalePayment = {
  id: 3118,
  date: dayjs('2025-07-27T14:07'),
  amount: 29129.22,
  active: true,
  createdBy: 'drat yahoo freely',
  lastModifiedBy: 'institute yahoo',
  lastModifiedDate: dayjs('2025-07-27T13:25'),
};

export const sampleWithFullData: ISalePayment = {
  id: 19702,
  date: dayjs('2025-07-26T21:31'),
  amount: 16863.55,
  active: true,
  createdBy: 'hm',
  createdDate: dayjs('2025-07-27T10:23'),
  lastModifiedBy: 'beyond gladly',
  lastModifiedDate: dayjs('2025-07-27T14:40'),
};

export const sampleWithNewData: NewSalePayment = {
  date: dayjs('2025-07-26T18:15'),
  amount: 11990.73,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
