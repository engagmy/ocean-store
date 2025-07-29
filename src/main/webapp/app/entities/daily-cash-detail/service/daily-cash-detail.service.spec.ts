import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDailyCashDetail } from '../daily-cash-detail.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../daily-cash-detail.test-samples';

import { DailyCashDetailService, RestDailyCashDetail } from './daily-cash-detail.service';

const requireRestSample: RestDailyCashDetail = {
  ...sampleWithRequiredData,
  timestamp: sampleWithRequiredData.timestamp?.toJSON(),
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('DailyCashDetail Service', () => {
  let service: DailyCashDetailService;
  let httpMock: HttpTestingController;
  let expectedResult: IDailyCashDetail | IDailyCashDetail[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DailyCashDetailService);
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

    it('should create a DailyCashDetail', () => {
      const dailyCashDetail = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dailyCashDetail).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DailyCashDetail', () => {
      const dailyCashDetail = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dailyCashDetail).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DailyCashDetail', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DailyCashDetail', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DailyCashDetail', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDailyCashDetailToCollectionIfMissing', () => {
      it('should add a DailyCashDetail to an empty array', () => {
        const dailyCashDetail: IDailyCashDetail = sampleWithRequiredData;
        expectedResult = service.addDailyCashDetailToCollectionIfMissing([], dailyCashDetail);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyCashDetail);
      });

      it('should not add a DailyCashDetail to an array that contains it', () => {
        const dailyCashDetail: IDailyCashDetail = sampleWithRequiredData;
        const dailyCashDetailCollection: IDailyCashDetail[] = [
          {
            ...dailyCashDetail,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDailyCashDetailToCollectionIfMissing(dailyCashDetailCollection, dailyCashDetail);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DailyCashDetail to an array that doesn't contain it", () => {
        const dailyCashDetail: IDailyCashDetail = sampleWithRequiredData;
        const dailyCashDetailCollection: IDailyCashDetail[] = [sampleWithPartialData];
        expectedResult = service.addDailyCashDetailToCollectionIfMissing(dailyCashDetailCollection, dailyCashDetail);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyCashDetail);
      });

      it('should add only unique DailyCashDetail to an array', () => {
        const dailyCashDetailArray: IDailyCashDetail[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dailyCashDetailCollection: IDailyCashDetail[] = [sampleWithRequiredData];
        expectedResult = service.addDailyCashDetailToCollectionIfMissing(dailyCashDetailCollection, ...dailyCashDetailArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dailyCashDetail: IDailyCashDetail = sampleWithRequiredData;
        const dailyCashDetail2: IDailyCashDetail = sampleWithPartialData;
        expectedResult = service.addDailyCashDetailToCollectionIfMissing([], dailyCashDetail, dailyCashDetail2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dailyCashDetail);
        expect(expectedResult).toContain(dailyCashDetail2);
      });

      it('should accept null and undefined values', () => {
        const dailyCashDetail: IDailyCashDetail = sampleWithRequiredData;
        expectedResult = service.addDailyCashDetailToCollectionIfMissing([], null, dailyCashDetail, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dailyCashDetail);
      });

      it('should return initial array if no DailyCashDetail is added', () => {
        const dailyCashDetailCollection: IDailyCashDetail[] = [sampleWithRequiredData];
        expectedResult = service.addDailyCashDetailToCollectionIfMissing(dailyCashDetailCollection, undefined, null);
        expect(expectedResult).toEqual(dailyCashDetailCollection);
      });
    });

    describe('compareDailyCashDetail', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDailyCashDetail(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16183 };
        const entity2 = null;

        const compareResult1 = service.compareDailyCashDetail(entity1, entity2);
        const compareResult2 = service.compareDailyCashDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16183 };
        const entity2 = { id: 13376 };

        const compareResult1 = service.compareDailyCashDetail(entity1, entity2);
        const compareResult2 = service.compareDailyCashDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16183 };
        const entity2 = { id: 16183 };

        const compareResult1 = service.compareDailyCashDetail(entity1, entity2);
        const compareResult2 = service.compareDailyCashDetail(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
