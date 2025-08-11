import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISalePayment } from '../sale-payment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sale-payment.test-samples';

import { RestSalePayment, SalePaymentService } from './sale-payment.service';

const requireRestSample: RestSalePayment = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('SalePayment Service', () => {
  let service: SalePaymentService;
  let httpMock: HttpTestingController;
  let expectedResult: ISalePayment | ISalePayment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SalePaymentService);
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

    it('should create a SalePayment', () => {
      const salePayment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(salePayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SalePayment', () => {
      const salePayment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(salePayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SalePayment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SalePayment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SalePayment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSalePaymentToCollectionIfMissing', () => {
      it('should add a SalePayment to an empty array', () => {
        const salePayment: ISalePayment = sampleWithRequiredData;
        expectedResult = service.addSalePaymentToCollectionIfMissing([], salePayment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salePayment);
      });

      it('should not add a SalePayment to an array that contains it', () => {
        const salePayment: ISalePayment = sampleWithRequiredData;
        const salePaymentCollection: ISalePayment[] = [
          {
            ...salePayment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSalePaymentToCollectionIfMissing(salePaymentCollection, salePayment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SalePayment to an array that doesn't contain it", () => {
        const salePayment: ISalePayment = sampleWithRequiredData;
        const salePaymentCollection: ISalePayment[] = [sampleWithPartialData];
        expectedResult = service.addSalePaymentToCollectionIfMissing(salePaymentCollection, salePayment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salePayment);
      });

      it('should add only unique SalePayment to an array', () => {
        const salePaymentArray: ISalePayment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const salePaymentCollection: ISalePayment[] = [sampleWithRequiredData];
        expectedResult = service.addSalePaymentToCollectionIfMissing(salePaymentCollection, ...salePaymentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const salePayment: ISalePayment = sampleWithRequiredData;
        const salePayment2: ISalePayment = sampleWithPartialData;
        expectedResult = service.addSalePaymentToCollectionIfMissing([], salePayment, salePayment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(salePayment);
        expect(expectedResult).toContain(salePayment2);
      });

      it('should accept null and undefined values', () => {
        const salePayment: ISalePayment = sampleWithRequiredData;
        expectedResult = service.addSalePaymentToCollectionIfMissing([], null, salePayment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(salePayment);
      });

      it('should return initial array if no SalePayment is added', () => {
        const salePaymentCollection: ISalePayment[] = [sampleWithRequiredData];
        expectedResult = service.addSalePaymentToCollectionIfMissing(salePaymentCollection, undefined, null);
        expect(expectedResult).toEqual(salePaymentCollection);
      });
    });

    describe('compareSalePayment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSalePayment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11117 };
        const entity2 = null;

        const compareResult1 = service.compareSalePayment(entity1, entity2);
        const compareResult2 = service.compareSalePayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11117 };
        const entity2 = { id: 26899 };

        const compareResult1 = service.compareSalePayment(entity1, entity2);
        const compareResult2 = service.compareSalePayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11117 };
        const entity2 = { id: 11117 };

        const compareResult1 = service.compareSalePayment(entity1, entity2);
        const compareResult2 = service.compareSalePayment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
