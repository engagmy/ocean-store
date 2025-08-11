import dayjs from 'dayjs/esm';

import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  name: 'hm sleepily',
  code: 'yippee whoever',
  quantity: 16147,
  unitPrice: 2897.4,
  costPrice: 30230.54,
};

export const sampleWithPartialData: IProduct = {
  id: 28292,
  name: 'delightfully mild',
  code: 'unnecessarily ferret',
  quantity: 23498,
  unitPrice: 14081.32,
  costPrice: 6162.36,
  createdBy: 'mutate nor',
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'cafe',
  code: 'along amongst',
  quantity: 12918,
  unitPrice: 110.1,
  costPrice: 32035.31,
  active: true,
  createdBy: 'alongside corral',
  createdDate: dayjs('2025-07-27T12:00'),
  lastModifiedBy: 'agitated',
  lastModifiedDate: dayjs('2025-07-27T08:24'),
};

export const sampleWithNewData: NewProduct = {
  name: 'anti inject why',
  code: 'anenst narrow apropo',
  quantity: 7532,
  unitPrice: 30544.22,
  costPrice: 22971.06,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
