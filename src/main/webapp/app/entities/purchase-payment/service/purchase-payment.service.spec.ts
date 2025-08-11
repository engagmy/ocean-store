import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPurchasePayment } from '../purchase-payment.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../purchase-payment.test-samples';

import { PurchasePaymentService, RestPurchasePayment } from './purchase-payment.service';

const requireRestSample: RestPurchasePayment = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('PurchasePayment Service', () => {
  let service: PurchasePaymentService;
  let httpMock: HttpTestingController;
  let expectedResult: IPurchasePayment | IPurchasePayment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PurchasePaymentService);
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

    it('should create a PurchasePayment', () => {
      const purchasePayment = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(purchasePayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PurchasePayment', () => {
      const purchasePayment = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(purchasePayment).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PurchasePayment', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PurchasePayment', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PurchasePayment', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPurchasePaymentToCollectionIfMissing', () => {
      it('should add a PurchasePayment to an empty array', () => {
        const purchasePayment: IPurchasePayment = sampleWithRequiredData;
        expectedResult = service.addPurchasePaymentToCollectionIfMissing([], purchasePayment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchasePayment);
      });

      it('should not add a PurchasePayment to an array that contains it', () => {
        const purchasePayment: IPurchasePayment = sampleWithRequiredData;
        const purchasePaymentCollection: IPurchasePayment[] = [
          {
            ...purchasePayment,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPurchasePaymentToCollectionIfMissing(purchasePaymentCollection, purchasePayment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PurchasePayment to an array that doesn't contain it", () => {
        const purchasePayment: IPurchasePayment = sampleWithRequiredData;
        const purchasePaymentCollection: IPurchasePayment[] = [sampleWithPartialData];
        expectedResult = service.addPurchasePaymentToCollectionIfMissing(purchasePaymentCollection, purchasePayment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchasePayment);
      });

      it('should add only unique PurchasePayment to an array', () => {
        const purchasePaymentArray: IPurchasePayment[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const purchasePaymentCollection: IPurchasePayment[] = [sampleWithRequiredData];
        expectedResult = service.addPurchasePaymentToCollectionIfMissing(purchasePaymentCollection, ...purchasePaymentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const purchasePayment: IPurchasePayment = sampleWithRequiredData;
        const purchasePayment2: IPurchasePayment = sampleWithPartialData;
        expectedResult = service.addPurchasePaymentToCollectionIfMissing([], purchasePayment, purchasePayment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchasePayment);
        expect(expectedResult).toContain(purchasePayment2);
      });

      it('should accept null and undefined values', () => {
        const purchasePayment: IPurchasePayment = sampleWithRequiredData;
        expectedResult = service.addPurchasePaymentToCollectionIfMissing([], null, purchasePayment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchasePayment);
      });

      it('should return initial array if no PurchasePayment is added', () => {
        const purchasePaymentCollection: IPurchasePayment[] = [sampleWithRequiredData];
        expectedResult = service.addPurchasePaymentToCollectionIfMissing(purchasePaymentCollection, undefined, null);
        expect(expectedResult).toEqual(purchasePaymentCollection);
      });
    });

    describe('comparePurchasePayment', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePurchasePayment(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18697 };
        const entity2 = null;

        const compareResult1 = service.comparePurchasePayment(entity1, entity2);
        const compareResult2 = service.comparePurchasePayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18697 };
        const entity2 = { id: 7942 };

        const compareResult1 = service.comparePurchasePayment(entity1, entity2);
        const compareResult2 = service.comparePurchasePayment(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18697 };
        const entity2 = { id: 18697 };

        const compareResult1 = service.comparePurchasePayment(entity1, entity2);
        const compareResult2 = service.comparePurchasePayment(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
