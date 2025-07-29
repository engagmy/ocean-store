import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ICashBalance } from '../cash-balance.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cash-balance.test-samples';

import { CashBalanceService, RestCashBalance } from './cash-balance.service';

const requireRestSample: RestCashBalance = {
  ...sampleWithRequiredData,
  lastUpdated: sampleWithRequiredData.lastUpdated?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('CashBalance Service', () => {
  let service: CashBalanceService;
  let httpMock: HttpTestingController;
  let expectedResult: ICashBalance | ICashBalance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CashBalanceService);
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

    it('should create a CashBalance', () => {
      const cashBalance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cashBalance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CashBalance', () => {
      const cashBalance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cashBalance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CashBalance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CashBalance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CashBalance', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCashBalanceToCollectionIfMissing', () => {
      it('should add a CashBalance to an empty array', () => {
        const cashBalance: ICashBalance = sampleWithRequiredData;
        expectedResult = service.addCashBalanceToCollectionIfMissing([], cashBalance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cashBalance);
      });

      it('should not add a CashBalance to an array that contains it', () => {
        const cashBalance: ICashBalance = sampleWithRequiredData;
        const cashBalanceCollection: ICashBalance[] = [
          {
            ...cashBalance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCashBalanceToCollectionIfMissing(cashBalanceCollection, cashBalance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CashBalance to an array that doesn't contain it", () => {
        const cashBalance: ICashBalance = sampleWithRequiredData;
        const cashBalanceCollection: ICashBalance[] = [sampleWithPartialData];
        expectedResult = service.addCashBalanceToCollectionIfMissing(cashBalanceCollection, cashBalance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashBalance);
      });

      it('should add only unique CashBalance to an array', () => {
        const cashBalanceArray: ICashBalance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const cashBalanceCollection: ICashBalance[] = [sampleWithRequiredData];
        expectedResult = service.addCashBalanceToCollectionIfMissing(cashBalanceCollection, ...cashBalanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cashBalance: ICashBalance = sampleWithRequiredData;
        const cashBalance2: ICashBalance = sampleWithPartialData;
        expectedResult = service.addCashBalanceToCollectionIfMissing([], cashBalance, cashBalance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cashBalance);
        expect(expectedResult).toContain(cashBalance2);
      });

      it('should accept null and undefined values', () => {
        const cashBalance: ICashBalance = sampleWithRequiredData;
        expectedResult = service.addCashBalanceToCollectionIfMissing([], null, cashBalance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cashBalance);
      });

      it('should return initial array if no CashBalance is added', () => {
        const cashBalanceCollection: ICashBalance[] = [sampleWithRequiredData];
        expectedResult = service.addCashBalanceToCollectionIfMissing(cashBalanceCollection, undefined, null);
        expect(expectedResult).toEqual(cashBalanceCollection);
      });
    });

    describe('compareCashBalance', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCashBalance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21194 };
        const entity2 = null;

        const compareResult1 = service.compareCashBalance(entity1, entity2);
        const compareResult2 = service.compareCashBalance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21194 };
        const entity2 = { id: 17265 };

        const compareResult1 = service.compareCashBalance(entity1, entity2);
        const compareResult2 = service.compareCashBalance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21194 };
        const entity2 = { id: 21194 };

        const compareResult1 = service.compareCashBalance(entity1, entity2);
        const compareResult2 = service.compareCashBalance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
