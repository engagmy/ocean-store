import dayjs from 'dayjs/esm';

import { IBrand, NewBrand } from './brand.model';

export const sampleWithRequiredData: IBrand = {
  id: 20780,
  name: 'sandy cheerfully unto',
};

export const sampleWithPartialData: IBrand = {
  id: 9260,
  name: 'deplore',
  lastModifiedDate: dayjs('2025-07-26T20:23'),
};

export const sampleWithFullData: IBrand = {
  id: 9187,
  name: 'er',
  active: false,
  createdBy: 'accelerator eulogise',
  createdDate: dayjs('2025-07-27T11:11'),
  lastModifiedBy: 'tame definitive',
  lastModifiedDate: dayjs('2025-07-27T04:07'),
};

export const sampleWithNewData: NewBrand = {
  name: 'delete',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
