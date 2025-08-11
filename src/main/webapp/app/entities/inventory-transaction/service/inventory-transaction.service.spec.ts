import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IInventoryTransaction } from '../inventory-transaction.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../inventory-transaction.test-samples';

import { InventoryTransactionService, RestInventoryTransaction } from './inventory-transaction.service';

const requireRestSample: RestInventoryTransaction = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('InventoryTransaction Service', () => {
  let service: InventoryTransactionService;
  let httpMock: HttpTestingController;
  let expectedResult: IInventoryTransaction | IInventoryTransaction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InventoryTransactionService);
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

    it('should create a InventoryTransaction', () => {
      const inventoryTransaction = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(inventoryTransaction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a InventoryTransaction', () => {
      const inventoryTransaction = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(inventoryTransaction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a InventoryTransaction', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of InventoryTransaction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a InventoryTransaction', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addInventoryTransactionToCollectionIfMissing', () => {
      it('should add a InventoryTransaction to an empty array', () => {
        const inventoryTransaction: IInventoryTransaction = sampleWithRequiredData;
        expectedResult = service.addInventoryTransactionToCollectionIfMissing([], inventoryTransaction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(inventoryTransaction);
      });

      it('should not add a InventoryTransaction to an array that contains it', () => {
        const inventoryTransaction: IInventoryTransaction = sampleWithRequiredData;
        const inventoryTransactionCollection: IInventoryTransaction[] = [
          {
            ...inventoryTransaction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInventoryTransactionToCollectionIfMissing(inventoryTransactionCollection, inventoryTransaction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a InventoryTransaction to an array that doesn't contain it", () => {
        const inventoryTransaction: IInventoryTransaction = sampleWithRequiredData;
        const inventoryTransactionCollection: IInventoryTransaction[] = [sampleWithPartialData];
        expectedResult = service.addInventoryTransactionToCollectionIfMissing(inventoryTransactionCollection, inventoryTransaction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inventoryTransaction);
      });

      it('should add only unique InventoryTransaction to an array', () => {
        const inventoryTransactionArray: IInventoryTransaction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const inventoryTransactionCollection: IInventoryTransaction[] = [sampleWithRequiredData];
        expectedResult = service.addInventoryTransactionToCollectionIfMissing(inventoryTransactionCollection, ...inventoryTransactionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const inventoryTransaction: IInventoryTransaction = sampleWithRequiredData;
        const inventoryTransaction2: IInventoryTransaction = sampleWithPartialData;
        expectedResult = service.addInventoryTransactionToCollectionIfMissing([], inventoryTransaction, inventoryTransaction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inventoryTransaction);
        expect(expectedResult).toContain(inventoryTransaction2);
      });

      it('should accept null and undefined values', () => {
        const inventoryTransaction: IInventoryTransaction = sampleWithRequiredData;
        expectedResult = service.addInventoryTransactionToCollectionIfMissing([], null, inventoryTransaction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(inventoryTransaction);
      });

      it('should return initial array if no InventoryTransaction is added', () => {
        const inventoryTransactionCollection: IInventoryTransaction[] = [sampleWithRequiredData];
        expectedResult = service.addInventoryTransactionToCollectionIfMissing(inventoryTransactionCollection, undefined, null);
        expect(expectedResult).toEqual(inventoryTransactionCollection);
      });
    });

    describe('compareInventoryTransaction', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInventoryTransaction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9318 };
        const entity2 = null;

        const compareResult1 = service.compareInventoryTransaction(entity1, entity2);
        const compareResult2 = service.compareInventoryTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9318 };
        const entity2 = { id: 2881 };

        const compareResult1 = service.compareInventoryTransaction(entity1, entity2);
        const compareResult2 = service.compareInventoryTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9318 };
        const entity2 = { id: 9318 };

        const compareResult1 = service.compareInventoryTransaction(entity1, entity2);
        const compareResult2 = service.compareInventoryTransaction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
