import dayjs from 'dayjs/esm';

import { ISale, NewSale } from './sale.model';

export const sampleWithRequiredData: ISale = {
  id: 2433,
  quantity: 13918,
  unitPrice: 3265.17,
  lineTotal: 12029.72,
};

export const sampleWithPartialData: ISale = {
  id: 13933,
  quantity: 25488,
  unitPrice: 10248.02,
  lineTotal: 1157.62,
  active: true,
  createdBy: 'properly',
  lastModifiedDate: dayjs('2025-07-27T00:41'),
};

export const sampleWithFullData: ISale = {
  id: 29002,
  productName: 'though',
  quantity: 31892,
  unitPrice: 10103.46,
  discount: 31469.77,
  lineTotal: 30639.51,
  active: true,
  createdBy: 'worth so',
  createdDate: dayjs('2025-07-27T12:45'),
  lastModifiedBy: 'whoever',
  lastModifiedDate: dayjs('2025-07-26T19:27'),
};

export const sampleWithNewData: NewSale = {
  quantity: 10215,
  unitPrice: 11647.53,
  lineTotal: 30641.01,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
