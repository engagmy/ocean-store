import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../daily-cash-detail.test-samples';

import { DailyCashDetailFormService } from './daily-cash-detail-form.service';

describe('DailyCashDetail Form Service', () => {
  let service: DailyCashDetailFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyCashDetailFormService);
  });

  describe('Service methods', () => {
    describe('createDailyCashDetailFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDailyCashDetailFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            referenceId: expect.any(Object),
            referenceType: expect.any(Object),
            amount: expect.any(Object),
            description: expect.any(Object),
            timestamp: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            dailyCashReconciliation: expect.any(Object),
          }),
        );
      });

      it('passing IDailyCashDetail should create a new form with FormGroup', () => {
        const formGroup = service.createDailyCashDetailFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            type: expect.any(Object),
            referenceId: expect.any(Object),
            referenceType: expect.any(Object),
            amount: expect.any(Object),
            description: expect.any(Object),
            timestamp: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            dailyCashReconciliation: expect.any(Object),
          }),
        );
      });
    });

    describe('getDailyCashDetail', () => {
      it('should return NewDailyCashDetail for default DailyCashDetail initial value', () => {
        const formGroup = service.createDailyCashDetailFormGroup(sampleWithNewData);

        const dailyCashDetail = service.getDailyCashDetail(formGroup) as any;

        expect(dailyCashDetail).toMatchObject(sampleWithNewData);
      });

      it('should return NewDailyCashDetail for empty DailyCashDetail initial value', () => {
        const formGroup = service.createDailyCashDetailFormGroup();

        const dailyCashDetail = service.getDailyCashDetail(formGroup) as any;

        expect(dailyCashDetail).toMatchObject({});
      });

      it('should return IDailyCashDetail', () => {
        const formGroup = service.createDailyCashDetailFormGroup(sampleWithRequiredData);

        const dailyCashDetail = service.getDailyCashDetail(formGroup) as any;

        expect(dailyCashDetail).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDailyCashDetail should not enable id FormControl', () => {
        const formGroup = service.createDailyCashDetailFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDailyCashDetail should disable id FormControl', () => {
        const formGroup = service.createDailyCashDetailFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
