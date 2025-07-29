import dayjs from 'dayjs/esm';

import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 8899,
  name: 'blushing institute',
  salary: 32487.47,
};

export const sampleWithPartialData: IEmployee = {
  id: 29906,
  name: 'psst gently',
  jobTitle: 'Product Accounts Manager',
  salary: 32309.77,
  joinDate: dayjs('2025-07-27T14:26'),
  active: false,
  createdBy: 'fiercely',
  lastModifiedBy: 'compassionate yuck rebel',
};

export const sampleWithFullData: IEmployee = {
  id: 19019,
  name: 'sharply questionably softly',
  jobTitle: 'District Interactions Liaison',
  salary: 21306.43,
  joinDate: dayjs('2025-07-26T22:26'),
  active: false,
  createdBy: 'urban forenenst whoa',
  createdDate: dayjs('2025-07-26T20:28'),
  lastModifiedBy: 'duh yum mothball',
  lastModifiedDate: dayjs('2025-07-26T20:34'),
};

export const sampleWithNewData: NewEmployee = {
  name: 'unless',
  salary: 24300.98,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
