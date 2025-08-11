import dayjs from 'dayjs/esm';

import { IProductCategory, NewProductCategory } from './product-category.model';

export const sampleWithRequiredData: IProductCategory = {
  id: 14928,
  name: 'jam-packed retract',
};

export const sampleWithPartialData: IProductCategory = {
  id: 22188,
  name: 'instead amidst bustling',
};

export const sampleWithFullData: IProductCategory = {
  id: 13570,
  name: 'happy possession widow',
  active: false,
  createdBy: 'yowza pfft',
  createdDate: dayjs('2025-07-27T07:12'),
  lastModifiedBy: 'chilly yippee',
  lastModifiedDate: dayjs('2025-07-26T22:33'),
};

export const sampleWithNewData: NewProductCategory = {
  name: 'suspiciously',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
