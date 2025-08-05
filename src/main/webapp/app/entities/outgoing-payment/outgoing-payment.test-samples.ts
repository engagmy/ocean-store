import dayjs from 'dayjs/esm';

import { IOutgoingPayment, NewOutgoingPayment } from './outgoing-payment.model';

export const sampleWithRequiredData: IOutgoingPayment = {
  id: 2227,
  date: dayjs('2025-07-27T01:05'),
  amount: 11155.79,
};

export const sampleWithPartialData: IOutgoingPayment = {
  id: 18158,
  date: dayjs('2025-07-26T23:03'),
  amount: 1046.46,
  reason: 'interchange unto',
  notes: 'hmph where',
  active: true,
  createdBy: 'whisper',
  createdDate: dayjs('2025-07-27T12:07'),
  lastModifiedDate: dayjs('2025-07-27T02:10'),
};

export const sampleWithFullData: IOutgoingPayment = {
  id: 8180,
  date: dayjs('2025-07-27T00:11'),
  amount: 8437.98,
  reason: 'bad',
  notes: 'chasuble vain readmit',
  active: true,
  createdBy: 'from',
  createdDate: dayjs('2025-07-27T03:47'),
  lastModifiedBy: 'once courageous',
  lastModifiedDate: dayjs('2025-07-26T17:24'),
};

export const sampleWithNewData: NewOutgoingPayment = {
  date: dayjs('2025-07-27T15:26'),
  amount: 23300.18,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
