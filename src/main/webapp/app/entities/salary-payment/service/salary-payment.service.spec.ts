import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISalaryPayment } from '../salary-payment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../salary-payment.test-samples';

import { RestSalaryPayment, SalaryPaymentService } from './salary-payment.service';

const requireRestSample: RestSalaryPayment = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('SalaryPayment Service', () => {
  let service: SalaryPaymentService;
  let httpMock: HttpTestingController;
  let expectedResult: ISalaryPayment | ISalaryPayment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SalaryPaymentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a SalaryPayment', () => {
      const salaryPayment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(salaryPayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SalaryPayment', () => {
      const salaryPayment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(salaryPayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SalaryPayment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SalaryPayment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SalaryPayment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSalaryPaymentToCollectionIfMissing', () => {
      it('should add a SalaryPayment to an empty array', () => {
        const salaryPayment: ISalaryPayment = sampleWithRequiredData;
        expectedResult = service.addSalaryPaymentToCollectionIfMissing([], salaryPayment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salaryPayment);
      });

      it('should not add a SalaryPayment to an array that contains it', () => {
        const salaryPayment: ISalaryPayment = sampleWithRequiredData;
        const salaryPaymentCollection: ISalaryPayment[] = [
          {
            ...salaryPayment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSalaryPaymentToCollectionIfMissing(salaryPaymentCollection, salaryPayment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SalaryPayment to an array that doesn't contain it", () => {
        const salaryPayment: ISalaryPayment = sampleWithRequiredData;
        const salaryPaymentCollection: ISalaryPayment[] = [sampleWithPartialData];
        expectedResult = service.addSalaryPaymentToCollectionIfMissing(salaryPaymentCollection, salaryPayment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salaryPayment);
      });

      it('should add only unique SalaryPayment to an array', () => {
        const salaryPaymentArray: ISalaryPayment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const salaryPaymentCollection: ISalaryPayment[] = [sampleWithRequiredData];
        expectedResult = service.addSalaryPaymentToCollectionIfMissing(salaryPaymentCollection, ...salaryPaymentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const salaryPayment: ISalaryPayment = sampleWithRequiredData;
        const salaryPayment2: ISalaryPayment = sampleWithPartialData;
        expectedResult = service.addSalaryPaymentToCollectionIfMissing([], salaryPayment, salaryPayment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salaryPayment);
        expect(expectedResult).toContain(salaryPayment2);
      });

      it('should accept null and undefined values', () => {
        const salaryPayment: ISalaryPayment = sampleWithRequiredData;
        expectedResult = service.addSalaryPaymentToCollectionIfMissing([], null, salaryPayment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salaryPayment);
      });

      it('should return initial array if no SalaryPayment is added', () => {
        const salaryPaymentCollection: ISalaryPayment[] = [sampleWithRequiredData];
        expectedResult = service.addSalaryPaymentToCollectionIfMissing(salaryPaymentCollection, undefined, null);
        expect(expectedResult).toEqual(salaryPaymentCollection);
      });
    });

    describe('compareSalaryPayment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSalaryPayment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29431 };
        const entity2 = null;

        const compareResult1 = service.compareSalaryPayment(entity1, entity2);
        const compareResult2 = service.compareSalaryPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29431 };
        const entity2 = { id: 12182 };

        const compareResult1 = service.compareSalaryPayment(entity1, entity2);
        const compareResult2 = service.compareSalaryPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29431 };
        const entity2 = { id: 29431 };

        const compareResult1 = service.compareSalaryPayment(entity1, entity2);
        const compareResult2 = service.compareSalaryPayment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
