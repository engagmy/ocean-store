import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISaleOperation } from '../sale-operation.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sale-operation.test-samples';

import { RestSaleOperation, SaleOperationService } from './sale-operation.service';

const requireRestSample: RestSaleOperation = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('SaleOperation Service', () => {
  let service: SaleOperationService;
  let httpMock: HttpTestingController;
  let expectedResult: ISaleOperation | ISaleOperation[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SaleOperationService);
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

    it('should create a SaleOperation', () => {
      const saleOperation = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(saleOperation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SaleOperation', () => {
      const saleOperation = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(saleOperation).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SaleOperation', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SaleOperation', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SaleOperation', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSaleOperationToCollectionIfMissing', () => {
      it('should add a SaleOperation to an empty array', () => {
        const saleOperation: ISaleOperation = sampleWithRequiredData;
        expectedResult = service.addSaleOperationToCollectionIfMissing([], saleOperation);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(saleOperation);
      });

      it('should not add a SaleOperation to an array that contains it', () => {
        const saleOperation: ISaleOperation = sampleWithRequiredData;
        const saleOperationCollection: ISaleOperation[] = [
          {
            ...saleOperation,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSaleOperationToCollectionIfMissing(saleOperationCollection, saleOperation);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SaleOperation to an array that doesn't contain it", () => {
        const saleOperation: ISaleOperation = sampleWithRequiredData;
        const saleOperationCollection: ISaleOperation[] = [sampleWithPartialData];
        expectedResult = service.addSaleOperationToCollectionIfMissing(saleOperationCollection, saleOperation);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(saleOperation);
      });

      it('should add only unique SaleOperation to an array', () => {
        const saleOperationArray: ISaleOperation[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const saleOperationCollection: ISaleOperation[] = [sampleWithRequiredData];
        expectedResult = service.addSaleOperationToCollectionIfMissing(saleOperationCollection, ...saleOperationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const saleOperation: ISaleOperation = sampleWithRequiredData;
        const saleOperation2: ISaleOperation = sampleWithPartialData;
        expectedResult = service.addSaleOperationToCollectionIfMissing([], saleOperation, saleOperation2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(saleOperation);
        expect(expectedResult).toContain(saleOperation2);
      });

      it('should accept null and undefined values', () => {
        const saleOperation: ISaleOperation = sampleWithRequiredData;
        expectedResult = service.addSaleOperationToCollectionIfMissing([], null, saleOperation, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(saleOperation);
      });

      it('should return initial array if no SaleOperation is added', () => {
        const saleOperationCollection: ISaleOperation[] = [sampleWithRequiredData];
        expectedResult = service.addSaleOperationToCollectionIfMissing(saleOperationCollection, undefined, null);
        expect(expectedResult).toEqual(saleOperationCollection);
      });
    });

    describe('compareSaleOperation', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSaleOperation(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25388 };
        const entity2 = null;

        const compareResult1 = service.compareSaleOperation(entity1, entity2);
        const compareResult2 = service.compareSaleOperation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25388 };
        const entity2 = { id: 11698 };

        const compareResult1 = service.compareSaleOperation(entity1, entity2);
        const compareResult2 = service.compareSaleOperation(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25388 };
        const entity2 = { id: 25388 };

        const compareResult1 = service.compareSaleOperation(entity1, entity2);
        const compareResult2 = service.compareSaleOperation(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
