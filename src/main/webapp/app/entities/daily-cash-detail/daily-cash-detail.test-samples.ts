import dayjs from 'dayjs/esm';

import { IDailyCashDetail, NewDailyCashDetail } from './daily-cash-detail.model';

export const sampleWithRequiredData: IDailyCashDetail = {
  id: 2606,
  type: 'PRODUCT_PURCHASE',
  referenceId: 4804,
  referenceType: 'duh',
  amount: 21402.95,
  timestamp: dayjs('2025-07-27T10:00'),
};

export const sampleWithPartialData: IDailyCashDetail = {
  id: 29816,
  type: 'OTHER',
  referenceId: 8190,
  referenceType: 'quarterly gadzooks finally',
  amount: 10460.34,
  description: 'stranger excepting bore',
  timestamp: dayjs('2025-07-27T14:21'),
  active: false,
  createdBy: 'as jubilantly unfortunately',
  lastModifiedBy: 'premium intently small',
  lastModifiedDate: dayjs('2025-07-27T07:42'),
};

export const sampleWithFullData: IDailyCashDetail = {
  id: 27607,
  type: 'SALARY_PAYMENT',
  referenceId: 30915,
  referenceType: 'readies brace',
  amount: 22044.71,
  description: 'now',
  timestamp: dayjs('2025-07-27T14:56'),
  active: true,
  createdBy: 'incidentally',
  createdDate: dayjs('2025-07-26T19:01'),
  lastModifiedBy: 'the mid than',
  lastModifiedDate: dayjs('2025-07-27T13:19'),
};

export const sampleWithNewData: NewDailyCashDetail = {
  type: 'PRODUCT_PURCHASE',
  referenceId: 14152,
  referenceType: 'near going er',
  amount: 28532.66,
  timestamp: dayjs('2025-07-27T03:19'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
