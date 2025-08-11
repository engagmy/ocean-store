import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IOutgoingPayment } from '../outgoing-payment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../outgoing-payment.test-samples';

import { OutgoingPaymentService, RestOutgoingPayment } from './outgoing-payment.service';

const requireRestSample: RestOutgoingPayment = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('OutgoingPayment Service', () => {
  let service: OutgoingPaymentService;
  let httpMock: HttpTestingController;
  let expectedResult: IOutgoingPayment | IOutgoingPayment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OutgoingPaymentService);
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

    it('should create a OutgoingPayment', () => {
      const outgoingPayment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(outgoingPayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OutgoingPayment', () => {
      const outgoingPayment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(outgoingPayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OutgoingPayment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OutgoingPayment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OutgoingPayment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOutgoingPaymentToCollectionIfMissing', () => {
      it('should add a OutgoingPayment to an empty array', () => {
        const outgoingPayment: IOutgoingPayment = sampleWithRequiredData;
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing([], outgoingPayment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(outgoingPayment);
      });

      it('should not add a OutgoingPayment to an array that contains it', () => {
        const outgoingPayment: IOutgoingPayment = sampleWithRequiredData;
        const outgoingPaymentCollection: IOutgoingPayment[] = [
          {
            ...outgoingPayment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing(outgoingPaymentCollection, outgoingPayment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OutgoingPayment to an array that doesn't contain it", () => {
        const outgoingPayment: IOutgoingPayment = sampleWithRequiredData;
        const outgoingPaymentCollection: IOutgoingPayment[] = [sampleWithPartialData];
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing(outgoingPaymentCollection, outgoingPayment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(outgoingPayment);
      });

      it('should add only unique OutgoingPayment to an array', () => {
        const outgoingPaymentArray: IOutgoingPayment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const outgoingPaymentCollection: IOutgoingPayment[] = [sampleWithRequiredData];
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing(outgoingPaymentCollection, ...outgoingPaymentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const outgoingPayment: IOutgoingPayment = sampleWithRequiredData;
        const outgoingPayment2: IOutgoingPayment = sampleWithPartialData;
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing([], outgoingPayment, outgoingPayment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(outgoingPayment);
        expect(expectedResult).toContain(outgoingPayment2);
      });

      it('should accept null and undefined values', () => {
        const outgoingPayment: IOutgoingPayment = sampleWithRequiredData;
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing([], null, outgoingPayment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(outgoingPayment);
      });

      it('should return initial array if no OutgoingPayment is added', () => {
        const outgoingPaymentCollection: IOutgoingPayment[] = [sampleWithRequiredData];
        expectedResult = service.addOutgoingPaymentToCollectionIfMissing(outgoingPaymentCollection, undefined, null);
        expect(expectedResult).toEqual(outgoingPaymentCollection);
      });
    });

    describe('compareOutgoingPayment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOutgoingPayment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29929 };
        const entity2 = null;

        const compareResult1 = service.compareOutgoingPayment(entity1, entity2);
        const compareResult2 = service.compareOutgoingPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29929 };
        const entity2 = { id: 18513 };

        const compareResult1 = service.compareOutgoingPayment(entity1, entity2);
        const compareResult2 = service.compareOutgoingPayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29929 };
        const entity2 = { id: 29929 };

        const compareResult1 = service.compareOutgoingPayment(entity1, entity2);
        const compareResult2 = service.compareOutgoingPayment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
