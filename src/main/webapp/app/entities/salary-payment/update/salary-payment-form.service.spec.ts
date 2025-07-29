import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../salary-payment.test-samples';

import { SalaryPaymentFormService } from './salary-payment-form.service';

describe('SalaryPayment Form Service', () => {
  let service: SalaryPaymentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SalaryPaymentFormService);
  });

  describe('Service methods', () => {
    describe('createSalaryPaymentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSalaryPaymentFormGroup();

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
            employee: expect.any(Object),
          }),
        );
      });

      it('passing ISalaryPayment should create a new form with FormGroup', () => {
        const formGroup = service.createSalaryPaymentFormGroup(sampleWithRequiredData);

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
            employee: expect.any(Object),
          }),
        );
      });
    });

    describe('getSalaryPayment', () => {
      it('should return NewSalaryPayment for default SalaryPayment initial value', () => {
        const formGroup = service.createSalaryPaymentFormGroup(sampleWithNewData);

        const salaryPayment = service.getSalaryPayment(formGroup) as any;

        expect(salaryPayment).toMatchObject(sampleWithNewData);
      });

      it('should return NewSalaryPayment for empty SalaryPayment initial value', () => {
        const formGroup = service.createSalaryPaymentFormGroup();

        const salaryPayment = service.getSalaryPayment(formGroup) as any;

        expect(salaryPayment).toMatchObject({});
      });

      it('should return ISalaryPayment', () => {
        const formGroup = service.createSalaryPaymentFormGroup(sampleWithRequiredData);

        const salaryPayment = service.getSalaryPayment(formGroup) as any;

        expect(salaryPayment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISalaryPayment should not enable id FormControl', () => {
        const formGroup = service.createSalaryPaymentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSalaryPayment should disable id FormControl', () => {
        const formGroup = service.createSalaryPaymentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
