import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICashTransaction } from '../cash-transaction.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cash-transaction.test-samples';

import { CashTransactionService, RestCashTransaction } from './cash-transaction.service';

const requireRestSample: RestCashTransaction = {
  ...sampleWithRequiredData,
  date: sampleWithRequiredData.date?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('CashTransaction Service', () => {
  let service: CashTransactionService;
  let httpMock: HttpTestingController;
  let expectedResult: ICashTransaction | ICashTransaction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CashTransactionService);
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

    it('should create a CashTransaction', () => {
      const cashTransaction = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cashTransaction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CashTransaction', () => {
      const cashTransaction = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cashTransaction).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CashTransaction', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CashTransaction', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CashTransaction', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCashTransactionToCollectionIfMissing', () => {
      it('should add a CashTransaction to an empty array', () => {
        const cashTransaction: ICashTransaction = sampleWithRequiredData;
        expectedResult = service.addCashTransactionToCollectionIfMissing([], cashTransaction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cashTransaction);
      });

      it('should not add a CashTransaction to an array that contains it', () => {
        const cashTransaction: ICashTransaction = sampleWithRequiredData;
        const cashTransactionCollection: ICashTransaction[] = [
          {
            ...cashTransaction,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCashTransactionToCollectionIfMissing(cashTransactionCollection, cashTransaction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CashTransaction to an array that doesn't contain it", () => {
        const cashTransaction: ICashTransaction = sampleWithRequiredData;
        const cashTransactionCollection: ICashTransaction[] = [sampleWithPartialData];
        expectedResult = service.addCashTransactionToCollectionIfMissing(cashTransactionCollection, cashTransaction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashTransaction);
      });

      it('should add only unique CashTransaction to an array', () => {
        const cashTransactionArray: ICashTransaction[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cashTransactionCollection: ICashTransaction[] = [sampleWithRequiredData];
        expectedResult = service.addCashTransactionToCollectionIfMissing(cashTransactionCollection, ...cashTransactionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cashTransaction: ICashTransaction = sampleWithRequiredData;
        const cashTransaction2: ICashTransaction = sampleWithPartialData;
        expectedResult = service.addCashTransactionToCollectionIfMissing([], cashTransaction, cashTransaction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashTransaction);
        expect(expectedResult).toContain(cashTransaction2);
      });

      it('should accept null and undefined values', () => {
        const cashTransaction: ICashTransaction = sampleWithRequiredData;
        expectedResult = service.addCashTransactionToCollectionIfMissing([], null, cashTransaction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cashTransaction);
      });

      it('should return initial array if no CashTransaction is added', () => {
        const cashTransactionCollection: ICashTransaction[] = [sampleWithRequiredData];
        expectedResult = service.addCashTransactionToCollectionIfMissing(cashTransactionCollection, undefined, null);
        expect(expectedResult).toEqual(cashTransactionCollection);
      });
    });

    describe('compareCashTransaction', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCashTransaction(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 22223 };
        const entity2 = null;

        const compareResult1 = service.compareCashTransaction(entity1, entity2);
        const compareResult2 = service.compareCashTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 22223 };
        const entity2 = { id: 32174 };

        const compareResult1 = service.compareCashTransaction(entity1, entity2);
        const compareResult2 = service.compareCashTransaction(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 22223 };
        const entity2 = { id: 22223 };

        const compareResult1 = service.compareCashTransaction(entity1, entity2);
        const compareResult2 = service.compareCashTransaction(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
