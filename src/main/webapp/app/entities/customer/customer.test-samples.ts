import dayjs from 'dayjs/esm';

import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 3366,
  name: 'slake splurge',
};

export const sampleWithPartialData: ICustomer = {
  id: 30079,
  name: 'modulo wildly',
  createdDate: dayjs('2025-07-27T10:49'),
  lastModifiedBy: 'smoggy yearly deliberately',
};

export const sampleWithFullData: ICustomer = {
  id: 4149,
  name: 'where known',
  phone: '779-272-6432',
  address: 'instead',
  active: true,
  createdBy: 'next',
  createdDate: dayjs('2025-07-27T01:28'),
  lastModifiedBy: 'airbrush',
  lastModifiedDate: dayjs('2025-07-27T07:04'),
};

export const sampleWithNewData: NewCustomer = {
  name: 'thyme inasmuch',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
