import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cash-transaction.test-samples';

import { CashTransactionFormService } from './cash-transaction-form.service';

describe('CashTransaction Form Service', () => {
  let service: CashTransactionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CashTransactionFormService);
  });

  describe('Service methods', () => {
    describe('createCashTransactionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCashTransactionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            type: expect.any(Object),
            reason: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });

      it('passing ICashTransaction should create a new form with FormGroup', () => {
        const formGroup = service.createCashTransactionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            type: expect.any(Object),
            reason: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getCashTransaction', () => {
      it('should return NewCashTransaction for default CashTransaction initial value', () => {
        const formGroup = service.createCashTransactionFormGroup(sampleWithNewData);

        const cashTransaction = service.getCashTransaction(formGroup) as any;

        expect(cashTransaction).toMatchObject(sampleWithNewData);
      });

      it('should return NewCashTransaction for empty CashTransaction initial value', () => {
        const formGroup = service.createCashTransactionFormGroup();

        const cashTransaction = service.getCashTransaction(formGroup) as any;

        expect(cashTransaction).toMatchObject({});
      });

      it('should return ICashTransaction', () => {
        const formGroup = service.createCashTransactionFormGroup(sampleWithRequiredData);

        const cashTransaction = service.getCashTransaction(formGroup) as any;

        expect(cashTransaction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICashTransaction should not enable id FormControl', () => {
        const formGroup = service.createCashTransactionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCashTransaction should disable id FormControl', () => {
        const formGroup = service.createCashTransactionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
