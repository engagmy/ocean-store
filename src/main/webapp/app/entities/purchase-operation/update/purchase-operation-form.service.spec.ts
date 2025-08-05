import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../purchase-operation.test-samples';

import { PurchaseOperationFormService } from './purchase-operation-form.service';

describe('PurchaseOperation Form Service', () => {
  let service: PurchaseOperationFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PurchaseOperationFormService);
  });

  describe('Service methods', () => {
    describe('createPurchaseOperationFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPurchaseOperationFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            supplierInvoiceNo: expect.any(Object),
            totalQuantity: expect.any(Object),
            totalAmount: expect.any(Object),
            grandTotal: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            bill: expect.any(Object),
          }),
        );
      });

      it('passing IPurchaseOperation should create a new form with FormGroup', () => {
        const formGroup = service.createPurchaseOperationFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            supplierInvoiceNo: expect.any(Object),
            totalQuantity: expect.any(Object),
            totalAmount: expect.any(Object),
            grandTotal: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            bill: expect.any(Object),
          }),
        );
      });
    });

    describe('getPurchaseOperation', () => {
      it('should return NewPurchaseOperation for default PurchaseOperation initial value', () => {
        const formGroup = service.createPurchaseOperationFormGroup(sampleWithNewData);

        const purchaseOperation = service.getPurchaseOperation(formGroup) as any;

        expect(purchaseOperation).toMatchObject(sampleWithNewData);
      });

      it('should return NewPurchaseOperation for empty PurchaseOperation initial value', () => {
        const formGroup = service.createPurchaseOperationFormGroup();

        const purchaseOperation = service.getPurchaseOperation(formGroup) as any;

        expect(purchaseOperation).toMatchObject({});
      });

      it('should return IPurchaseOperation', () => {
        const formGroup = service.createPurchaseOperationFormGroup(sampleWithRequiredData);

        const purchaseOperation = service.getPurchaseOperation(formGroup) as any;

        expect(purchaseOperation).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPurchaseOperation should not enable id FormControl', () => {
        const formGroup = service.createPurchaseOperationFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPurchaseOperation should disable id FormControl', () => {
        const formGroup = service.createPurchaseOperationFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
