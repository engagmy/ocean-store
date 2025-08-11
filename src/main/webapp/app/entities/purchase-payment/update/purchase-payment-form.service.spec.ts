import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../purchase-payment.test-samples';

import { PurchasePaymentFormService } from './purchase-payment-form.service';

describe('PurchasePayment Form Service', () => {
  let service: PurchasePaymentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PurchasePaymentFormService);
  });

  describe('Service methods', () => {
    describe('createPurchasePaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPurchasePaymentFormGroup();

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
            purchase: expect.any(Object),
          }),
        );
      });

      it('passing IPurchasePayment should create a new form with FormGroup', () => {
        const formGroup = service.createPurchasePaymentFormGroup(sampleWithRequiredData);

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
            purchase: expect.any(Object),
          }),
        );
      });
    });

    describe('getPurchasePayment', () => {
      it('should return NewPurchasePayment for default PurchasePayment initial value', () => {
        const formGroup = service.createPurchasePaymentFormGroup(sampleWithNewData);

        const purchasePayment = service.getPurchasePayment(formGroup) as any;

        expect(purchasePayment).toMatchObject(sampleWithNewData);
      });

      it('should return NewPurchasePayment for empty PurchasePayment initial value', () => {
        const formGroup = service.createPurchasePaymentFormGroup();

        const purchasePayment = service.getPurchasePayment(formGroup) as any;

        expect(purchasePayment).toMatchObject({});
      });

      it('should return IPurchasePayment', () => {
        const formGroup = service.createPurchasePaymentFormGroup(sampleWithRequiredData);

        const purchasePayment = service.getPurchasePayment(formGroup) as any;

        expect(purchasePayment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPurchasePayment should not enable id FormControl', () => {
        const formGroup = service.createPurchasePaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPurchasePayment should disable id FormControl', () => {
        const formGroup = service.createPurchasePaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
