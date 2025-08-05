import dayjs from 'dayjs/esm';

import { ISupplier, NewSupplier } from './supplier.model';

export const sampleWithRequiredData: ISupplier = {
  id: 22717,
  name: 'menacing',
};

export const sampleWithPartialData: ISupplier = {
  id: 4240,
  name: 'upon',
  active: false,
  createdDate: dayjs('2025-07-26T17:55'),
  lastModifiedBy: 'but merrily',
};

export const sampleWithFullData: ISupplier = {
  id: 3840,
  name: 'cheerfully near',
  phone: '720.565.7764 x42290',
  address: 'sans hamburger',
  active: false,
  createdBy: 'hm unless',
  createdDate: dayjs('2025-07-26T23:28'),
  lastModifiedBy: 'role injunction',
  lastModifiedDate: dayjs('2025-07-27T00:03'),
};

export const sampleWithNewData: NewSupplier = {
  name: 'snack',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
