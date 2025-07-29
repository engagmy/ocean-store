import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../daily-cash-reconciliation.test-samples';

import { DailyCashReconciliationFormService } from './daily-cash-reconciliation-form.service';

describe('DailyCashReconciliation Form Service', () => {
  let service: DailyCashReconciliationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyCashReconciliationFormService);
  });

  describe('Service methods', () => {
    describe('createDailyCashReconciliationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            openingBalance: expect.any(Object),
            totalSales: expect.any(Object),
            totalPurchases: expect.any(Object),
            totalSalaryPaid: expect.any(Object),
            ownerDeposits: expect.any(Object),
            withdrawals: expect.any(Object),
            closingBalance: expect.any(Object),
            notes: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });

      it('passing IDailyCashReconciliation should create a new form with FormGroup', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            openingBalance: expect.any(Object),
            totalSales: expect.any(Object),
            totalPurchases: expect.any(Object),
            totalSalaryPaid: expect.any(Object),
            ownerDeposits: expect.any(Object),
            withdrawals: expect.any(Object),
            closingBalance: expect.any(Object),
            notes: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getDailyCashReconciliation', () => {
      it('should return NewDailyCashReconciliation for default DailyCashReconciliation initial value', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup(sampleWithNewData);

        const dailyCashReconciliation = service.getDailyCashReconciliation(formGroup) as any;

        expect(dailyCashReconciliation).toMatchObject(sampleWithNewData);
      });

      it('should return NewDailyCashReconciliation for empty DailyCashReconciliation initial value', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup();

        const dailyCashReconciliation = service.getDailyCashReconciliation(formGroup) as any;

        expect(dailyCashReconciliation).toMatchObject({});
      });

      it('should return IDailyCashReconciliation', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup(sampleWithRequiredData);

        const dailyCashReconciliation = service.getDailyCashReconciliation(formGroup) as any;

        expect(dailyCashReconciliation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDailyCashReconciliation should not enable id FormControl', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDailyCashReconciliation should disable id FormControl', () => {
        const formGroup = service.createDailyCashReconciliationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
