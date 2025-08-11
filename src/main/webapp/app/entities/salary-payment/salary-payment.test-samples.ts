import dayjs from 'dayjs/esm';

import { ISalaryPayment, NewSalaryPayment } from './salary-payment.model';

export const sampleWithRequiredData: ISalaryPayment = {
  id: 28706,
  date: dayjs('2025-07-27T05:46'),
  amount: 1676.44,
};

export const sampleWithPartialData: ISalaryPayment = {
  id: 21384,
  date: dayjs('2025-07-27T05:39'),
  amount: 384.19,
  active: true,
  createdBy: 'uh-huh greedily instruction',
  lastModifiedBy: 'almost affectionate knotty',
};

export const sampleWithFullData: ISalaryPayment = {
  id: 27270,
  date: dayjs('2025-07-27T04:45'),
  amount: 30554.81,
  active: true,
  createdBy: 'jump above',
  createdDate: dayjs('2025-07-27T08:41'),
  lastModifiedBy: 'knowledgeable',
  lastModifiedDate: dayjs('2025-07-27T05:28'),
};

export const sampleWithNewData: NewSalaryPayment = {
  date: dayjs('2025-07-27T00:04'),
  amount: 13197.59,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
