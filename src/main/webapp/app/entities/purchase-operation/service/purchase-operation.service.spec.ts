import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPurchaseOperation } from '../purchase-operation.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../purchase-operation.test-samples';

import { PurchaseOperationService, RestPurchaseOperation } from './purchase-operation.service';

const requireRestSample: RestPurchaseOperation = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('PurchaseOperation Service', () => {
  let service: PurchaseOperationService;
  let httpMock: HttpTestingController;
  let expectedResult: IPurchaseOperation | IPurchaseOperation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PurchaseOperationService);
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

    it('should create a PurchaseOperation', () => {
      const purchaseOperation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(purchaseOperation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PurchaseOperation', () => {
      const purchaseOperation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(purchaseOperation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PurchaseOperation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PurchaseOperation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PurchaseOperation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPurchaseOperationToCollectionIfMissing', () => {
      it('should add a PurchaseOperation to an empty array', () => {
        const purchaseOperation: IPurchaseOperation = sampleWithRequiredData;
        expectedResult = service.addPurchaseOperationToCollectionIfMissing([], purchaseOperation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchaseOperation);
      });

      it('should not add a PurchaseOperation to an array that contains it', () => {
        const purchaseOperation: IPurchaseOperation = sampleWithRequiredData;
        const purchaseOperationCollection: IPurchaseOperation[] = [
          {
            ...purchaseOperation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPurchaseOperationToCollectionIfMissing(purchaseOperationCollection, purchaseOperation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PurchaseOperation to an array that doesn't contain it", () => {
        const purchaseOperation: IPurchaseOperation = sampleWithRequiredData;
        const purchaseOperationCollection: IPurchaseOperation[] = [sampleWithPartialData];
        expectedResult = service.addPurchaseOperationToCollectionIfMissing(purchaseOperationCollection, purchaseOperation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchaseOperation);
      });

      it('should add only unique PurchaseOperation to an array', () => {
        const purchaseOperationArray: IPurchaseOperation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const purchaseOperationCollection: IPurchaseOperation[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseOperationToCollectionIfMissing(purchaseOperationCollection, ...purchaseOperationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const purchaseOperation: IPurchaseOperation = sampleWithRequiredData;
        const purchaseOperation2: IPurchaseOperation = sampleWithPartialData;
        expectedResult = service.addPurchaseOperationToCollectionIfMissing([], purchaseOperation, purchaseOperation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchaseOperation);
        expect(expectedResult).toContain(purchaseOperation2);
      });

      it('should accept null and undefined values', () => {
        const purchaseOperation: IPurchaseOperation = sampleWithRequiredData;
        expectedResult = service.addPurchaseOperationToCollectionIfMissing([], null, purchaseOperation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchaseOperation);
      });

      it('should return initial array if no PurchaseOperation is added', () => {
        const purchaseOperationCollection: IPurchaseOperation[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseOperationToCollectionIfMissing(purchaseOperationCollection, undefined, null);
        expect(expectedResult).toEqual(purchaseOperationCollection);
      });
    });

    describe('comparePurchaseOperation', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePurchaseOperation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 5527 };
        const entity2 = null;

        const compareResult1 = service.comparePurchaseOperation(entity1, entity2);
        const compareResult2 = service.comparePurchaseOperation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 5527 };
        const entity2 = { id: 30521 };

        const compareResult1 = service.comparePurchaseOperation(entity1, entity2);
        const compareResult2 = service.comparePurchaseOperation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 5527 };
        const entity2 = { id: 5527 };

        const compareResult1 = service.comparePurchaseOperation(entity1, entity2);
        const compareResult2 = service.comparePurchaseOperation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
