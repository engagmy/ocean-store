import dayjs from 'dayjs/esm';

import { ICashBalance, NewCashBalance } from './cash-balance.model';

export const sampleWithRequiredData: ICashBalance = {
  id: 4366,
  available: 32612.22,
};

export const sampleWithPartialData: ICashBalance = {
  id: 28388,
  available: 1309.11,
  notes: 'while',
  createdBy: 'wordy determined next',
  createdDate: dayjs('2025-07-26T18:33'),
  lastModifiedBy: 'peaceful',
};

export const sampleWithFullData: ICashBalance = {
  id: 10050,
  available: 23311.94,
  lastUpdated: dayjs('2025-07-27T13:48'),
  notes: 'quaintly lumpy hydrolyse',
  active: true,
  createdBy: 'commonly',
  createdDate: dayjs('2025-07-27T08:39'),
  lastModifiedBy: 'measly as',
  lastModifiedDate: dayjs('2025-07-27T07:22'),
};

export const sampleWithNewData: NewCashBalance = {
  available: 3285.31,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
