import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchase, NewPurchase } from '../purchase.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchase for edit and NewPurchaseFormGroupInput for create.
 */
type PurchaseFormGroupInput = IPurchase | PartialWithRequiredKeyOf<NewPurchase>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchase | NewPurchase> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type PurchaseFormRawValue = FormValueOf<IPurchase>;

type NewPurchaseFormRawValue = FormValueOf<NewPurchase>;

type PurchaseFormDefaults = Pick<NewPurchase, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type PurchaseFormGroupContent = {
  id: FormControl<PurchaseFormRawValue['id'] | NewPurchase['id']>;
  productName: FormControl<PurchaseFormRawValue['productName']>;
  quantity: FormControl<PurchaseFormRawValue['quantity']>;
  unitPrice: FormControl<PurchaseFormRawValue['unitPrice']>;
  lineTotal: FormControl<PurchaseFormRawValue['lineTotal']>;
  active: FormControl<PurchaseFormRawValue['active']>;
  createdBy: FormControl<PurchaseFormRawValue['createdBy']>;
  createdDate: FormControl<PurchaseFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<PurchaseFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<PurchaseFormRawValue['lastModifiedDate']>;
  purchaseOperation: FormControl<PurchaseFormRawValue['purchaseOperation']>;
  product: FormControl<PurchaseFormRawValue['product']>;
  supplier: FormControl<PurchaseFormRawValue['supplier']>;
};

export type PurchaseFormGroup = FormGroup<PurchaseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchaseFormService {
  createPurchaseFormGroup(purchase: PurchaseFormGroupInput = { id: null }): PurchaseFormGroup {
    const purchaseRawValue = this.convertPurchaseToPurchaseRawValue({
      ...this.getFormDefaults(),
      ...purchase,
    });
    return new FormGroup<PurchaseFormGroupContent>({
      id: new FormControl(
        { value: purchaseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      productName: new FormControl(purchaseRawValue.productName),
      quantity: new FormControl(purchaseRawValue.quantity, {
        validators: [Validators.required],
      }),
      unitPrice: new FormControl(purchaseRawValue.unitPrice, {
        validators: [Validators.required],
      }),
      lineTotal: new FormControl(purchaseRawValue.lineTotal, {
        validators: [Validators.required],
      }),
      active: new FormControl(purchaseRawValue.active),
      createdBy: new FormControl(purchaseRawValue.createdBy),
      createdDate: new FormControl(purchaseRawValue.createdDate),
      lastModifiedBy: new FormControl(purchaseRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(purchaseRawValue.lastModifiedDate),
      purchaseOperation: new FormControl(purchaseRawValue.purchaseOperation),
      product: new FormControl(purchaseRawValue.product),
      supplier: new FormControl(purchaseRawValue.supplier),
    });
  }

  getPurchase(form: PurchaseFormGroup): IPurchase | NewPurchase {
    return this.convertPurchaseRawValueToPurchase(form.getRawValue() as PurchaseFormRawValue | NewPurchaseFormRawValue);
  }

  resetForm(form: PurchaseFormGroup, purchase: PurchaseFormGroupInput): void {
    const purchaseRawValue = this.convertPurchaseToPurchaseRawValue({ ...this.getFormDefaults(), ...purchase });
    form.reset(
      {
        ...purchaseRawValue,
        id: { value: purchaseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchaseFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertPurchaseRawValueToPurchase(rawPurchase: PurchaseFormRawValue | NewPurchaseFormRawValue): IPurchase | NewPurchase {
    return {
      ...rawPurchase,
      createdDate: dayjs(rawPurchase.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawPurchase.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertPurchaseToPurchaseRawValue(
    purchase: IPurchase | (Partial<NewPurchase> & PurchaseFormDefaults),
  ): PurchaseFormRawValue | PartialWithRequiredKeyOf<NewPurchaseFormRawValue> {
    return {
      ...purchase,
      createdDate: purchase.createdDate ? purchase.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: purchase.lastModifiedDate ? purchase.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
