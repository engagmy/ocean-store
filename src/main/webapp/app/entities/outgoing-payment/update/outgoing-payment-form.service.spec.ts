import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../outgoing-payment.test-samples';

import { OutgoingPaymentFormService } from './outgoing-payment-form.service';

describe('OutgoingPayment Form Service', () => {
  let service: OutgoingPaymentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OutgoingPaymentFormService);
  });

  describe('Service methods', () => {
    describe('createOutgoingPaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOutgoingPaymentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
            notes: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          }),
        );
      });

      it('passing IOutgoingPayment should create a new form with FormGroup', () => {
        const formGroup = service.createOutgoingPaymentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            amount: expect.any(Object),
            reason: expect.any(Object),
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

    describe('getOutgoingPayment', () => {
      it('should return NewOutgoingPayment for default OutgoingPayment initial value', () => {
        const formGroup = service.createOutgoingPaymentFormGroup(sampleWithNewData);

        const outgoingPayment = service.getOutgoingPayment(formGroup) as any;

        expect(outgoingPayment).toMatchObject(sampleWithNewData);
      });

      it('should return NewOutgoingPayment for empty OutgoingPayment initial value', () => {
        const formGroup = service.createOutgoingPaymentFormGroup();

        const outgoingPayment = service.getOutgoingPayment(formGroup) as any;

        expect(outgoingPayment).toMatchObject({});
      });

      it('should return IOutgoingPayment', () => {
        const formGroup = service.createOutgoingPaymentFormGroup(sampleWithRequiredData);

        const outgoingPayment = service.getOutgoingPayment(formGroup) as any;

        expect(outgoingPayment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOutgoingPayment should not enable id FormControl', () => {
        const formGroup = service.createOutgoingPaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOutgoingPayment should disable id FormControl', () => {
        const formGroup = service.createOutgoingPaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
