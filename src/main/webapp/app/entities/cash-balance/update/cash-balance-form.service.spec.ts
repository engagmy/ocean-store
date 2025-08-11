import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cash-balance.test-samples';

import { CashBalanceFormService } from './cash-balance-form.service';

describe('CashBalance Form Service', () => {
  let service: CashBalanceFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CashBalanceFormService);
  });

  describe('Service methods', () => {
    describe('createCashBalanceFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCashBalanceFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            available: expect.any(Object),
            lastUpdated: expect.any(Object),
            notes: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });

      it('passing ICashBalance should create a new form with FormGroup', () => {
        const formGroup = service.createCashBalanceFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            available: expect.any(Object),
            lastUpdated: expect.any(Object),
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

    describe('getCashBalance', () => {
      it('should return NewCashBalance for default CashBalance initial value', () => {
        const formGroup = service.createCashBalanceFormGroup(sampleWithNewData);

        const cashBalance = service.getCashBalance(formGroup) as any;

        expect(cashBalance).toMatchObject(sampleWithNewData);
      });

      it('should return NewCashBalance for empty CashBalance initial value', () => {
        const formGroup = service.createCashBalanceFormGroup();

        const cashBalance = service.getCashBalance(formGroup) as any;

        expect(cashBalance).toMatchObject({});
      });

      it('should return ICashBalance', () => {
        const formGroup = service.createCashBalanceFormGroup(sampleWithRequiredData);

        const cashBalance = service.getCashBalance(formGroup) as any;

        expect(cashBalance).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICashBalance should not enable id FormControl', () => {
        const formGroup = service.createCashBalanceFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCashBalance should disable id FormControl', () => {
        const formGroup = service.createCashBalanceFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
