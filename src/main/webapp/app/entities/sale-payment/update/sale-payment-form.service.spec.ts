import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sale-payment.test-samples';

import { SalePaymentFormService } from './sale-payment-form.service';

describe('SalePayment Form Service', () => {
  let service: SalePaymentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SalePaymentFormService);
  });

  describe('Service methods', () => {
    describe('createSalePaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSalePaymentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });

      it('passing ISalePayment should create a new form with FormGroup', () => {
        const formGroup = service.createSalePaymentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });
    });

    describe('getSalePayment', () => {
      it('should return NewSalePayment for default SalePayment initial value', () => {
        const formGroup = service.createSalePaymentFormGroup(sampleWithNewData);

        const salePayment = service.getSalePayment(formGroup) as any;

        expect(salePayment).toMatchObject(sampleWithNewData);
      });

      it('should return NewSalePayment for empty SalePayment initial value', () => {
        const formGroup = service.createSalePaymentFormGroup();

        const salePayment = service.getSalePayment(formGroup) as any;

        expect(salePayment).toMatchObject({});
      });

      it('should return ISalePayment', () => {
        const formGroup = service.createSalePaymentFormGroup(sampleWithRequiredData);

        const salePayment = service.getSalePayment(formGroup) as any;

        expect(salePayment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISalePayment should not enable id FormControl', () => {
        const formGroup = service.createSalePaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSalePayment should disable id FormControl', () => {
        const formGroup = service.createSalePaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
