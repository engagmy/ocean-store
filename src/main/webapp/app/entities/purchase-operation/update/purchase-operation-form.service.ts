import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchaseOperation, NewPurchaseOperation } from '../purchase-operation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchaseOperation for edit and NewPurchaseOperationFormGroupInput for create.
 */
type PurchaseOperationFormGroupInput = IPurchaseOperation | PartialWithRequiredKeyOf<NewPurchaseOperation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchaseOperation | NewPurchaseOperation> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type PurchaseOperationFormRawValue = FormValueOf<IPurchaseOperation>;

type NewPurchaseOperationFormRawValue = FormValueOf<NewPurchaseOperation>;

type PurchaseOperationFormDefaults = Pick<NewPurchaseOperation, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type PurchaseOperationFormGroupContent = {
  id: FormControl<PurchaseOperationFormRawValue['id'] | NewPurchaseOperation['id']>;
  date: FormControl<PurchaseOperationFormRawValue['date']>;
  supplierInvoiceNo: FormControl<PurchaseOperationFormRawValue['supplierInvoiceNo']>;
  totalQuantity: FormControl<PurchaseOperationFormRawValue['totalQuantity']>;
  totalAmount: FormControl<PurchaseOperationFormRawValue['totalAmount']>;
  grandTotal: FormControl<PurchaseOperationFormRawValue['grandTotal']>;
  active: FormControl<PurchaseOperationFormRawValue['active']>;
  createdBy: FormControl<PurchaseOperationFormRawValue['createdBy']>;
  createdDate: FormControl<PurchaseOperationFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<PurchaseOperationFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<PurchaseOperationFormRawValue['lastModifiedDate']>;
  bill: FormControl<PurchaseOperationFormRawValue['bill']>;
};

export type PurchaseOperationFormGroup = FormGroup<PurchaseOperationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseOperationFormService {
  createPurchaseOperationFormGroup(purchaseOperation: PurchaseOperationFormGroupInput = { id: null }): PurchaseOperationFormGroup {
    const purchaseOperationRawValue = this.convertPurchaseOperationToPurchaseOperationRawValue({
      ...this.getFormDefaults(),
      ...purchaseOperation,
    });
    return new FormGroup<PurchaseOperationFormGroupContent>({
      id: new FormControl(
        { value: purchaseOperationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(purchaseOperationRawValue.date, {
        validators: [Validators.required],
      }),
      supplierInvoiceNo: new FormControl(purchaseOperationRawValue.supplierInvoiceNo),
      totalQuantity: new FormControl(purchaseOperationRawValue.totalQuantity, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(purchaseOperationRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      grandTotal: new FormControl(purchaseOperationRawValue.grandTotal, {
        validators: [Validators.required],
      }),
      active: new FormControl(purchaseOperationRawValue.active),
      createdBy: new FormControl(purchaseOperationRawValue.createdBy),
      createdDate: new FormControl(purchaseOperationRawValue.createdDate),
      lastModifiedBy: new FormControl(purchaseOperationRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(purchaseOperationRawValue.lastModifiedDate),
      bill: new FormControl(purchaseOperationRawValue.bill),
    });
  }

  getPurchaseOperation(form: PurchaseOperationFormGroup): IPurchaseOperation | NewPurchaseOperation {
    return this.convertPurchaseOperationRawValueToPurchaseOperation(
      form.getRawValue() as PurchaseOperationFormRawValue | NewPurchaseOperationFormRawValue,
    );
  }

  resetForm(form: PurchaseOperationFormGroup, purchaseOperation: PurchaseOperationFormGroupInput): void {
    const purchaseOperationRawValue = this.convertPurchaseOperationToPurchaseOperationRawValue({
      ...this.getFormDefaults(),
      ...purchaseOperation,
    });
    form.reset(
      {
        ...purchaseOperationRawValue,
        id: { value: purchaseOperationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchaseOperationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertPurchaseOperationRawValueToPurchaseOperation(
    rawPurchaseOperation: PurchaseOperationFormRawValue | NewPurchaseOperationFormRawValue,
  ): IPurchaseOperation | NewPurchaseOperation {
    return {
      ...rawPurchaseOperation,
      date: dayjs(rawPurchaseOperation.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawPurchaseOperation.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawPurchaseOperation.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertPurchaseOperationToPurchaseOperationRawValue(
    purchaseOperation: IPurchaseOperation | (Partial<NewPurchaseOperation> & PurchaseOperationFormDefaults),
  ): PurchaseOperationFormRawValue | PartialWithRequiredKeyOf<NewPurchaseOperationFormRawValue> {
    return {
      ...purchaseOperation,
      date: purchaseOperation.date ? purchaseOperation.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: purchaseOperation.createdDate ? purchaseOperation.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: purchaseOperation.lastModifiedDate ? purchaseOperation.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
