import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../inventory-transaction.test-samples';

import { InventoryTransactionFormService } from './inventory-transaction-form.service';

describe('InventoryTransaction Form Service', () => {
  let service: InventoryTransactionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InventoryTransactionFormService);
  });

  describe('Service methods', () => {
    describe('createInventoryTransactionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInventoryTransactionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            type: expect.any(Object),
            quantity: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });

      it('passing IInventoryTransaction should create a new form with FormGroup', () => {
        const formGroup = service.createInventoryTransactionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            type: expect.any(Object),
            quantity: expect.any(Object),
            active: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            product: expect.any(Object),
          }),
        );
      });
    });

    describe('getInventoryTransaction', () => {
      it('should return NewInventoryTransaction for default InventoryTransaction initial value', () => {
        const formGroup = service.createInventoryTransactionFormGroup(sampleWithNewData);

        const inventoryTransaction = service.getInventoryTransaction(formGroup) as any;

        expect(inventoryTransaction).toMatchObject(sampleWithNewData);
      });

      it('should return NewInventoryTransaction for empty InventoryTransaction initial value', () => {
        const formGroup = service.createInventoryTransactionFormGroup();

        const inventoryTransaction = service.getInventoryTransaction(formGroup) as any;

        expect(inventoryTransaction).toMatchObject({});
      });

      it('should return IInventoryTransaction', () => {
        const formGroup = service.createInventoryTransactionFormGroup(sampleWithRequiredData);

        const inventoryTransaction = service.getInventoryTransaction(formGroup) as any;

        expect(inventoryTransaction).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInventoryTransaction should not enable id FormControl', () => {
        const formGroup = service.createInventoryTransactionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInventoryTransaction should disable id FormControl', () => {
        const formGroup = service.createInventoryTransactionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
