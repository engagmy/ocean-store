import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sale-operation.test-samples';

import { SaleOperationFormService } from './sale-operation-form.service';

describe('SaleOperation Form Service', () => {
  let service: SaleOperationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SaleOperationFormService);
  });

  describe('Service methods', () => {
    describe('createSaleOperationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSaleOperationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            totalQuantity: expect.any(Object),
            totalAmount: expect.any(Object),
            totalDiscount: expect.any(Object),
            grandTotal: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            bill: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });

      it('passing ISaleOperation should create a new form with FormGroup', () => {
        const formGroup = service.createSaleOperationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            totalQuantity: expect.any(Object),
            totalAmount: expect.any(Object),
            totalDiscount: expect.any(Object),
            grandTotal: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            bill: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });
    });

    describe('getSaleOperation', () => {
      it('should return NewSaleOperation for default SaleOperation initial value', () => {
        const formGroup = service.createSaleOperationFormGroup(sampleWithNewData);

        const saleOperation = service.getSaleOperation(formGroup) as any;

        expect(saleOperation).toMatchObject(sampleWithNewData);
      });

      it('should return NewSaleOperation for empty SaleOperation initial value', () => {
        const formGroup = service.createSaleOperationFormGroup();

        const saleOperation = service.getSaleOperation(formGroup) as any;

        expect(saleOperation).toMatchObject({});
      });

      it('should return ISaleOperation', () => {
        const formGroup = service.createSaleOperationFormGroup(sampleWithRequiredData);

        const saleOperation = service.getSaleOperation(formGroup) as any;

        expect(saleOperation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISaleOperation should not enable id FormControl', () => {
        const formGroup = service.createSaleOperationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSaleOperation should disable id FormControl', () => {
        const formGroup = service.createSaleOperationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
