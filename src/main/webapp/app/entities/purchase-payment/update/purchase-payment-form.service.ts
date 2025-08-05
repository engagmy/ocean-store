import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPurchasePayment, NewPurchasePayment } from '../purchase-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPurchasePayment for edit and NewPurchasePaymentFormGroupInput for create.
 */
type PurchasePaymentFormGroupInput = IPurchasePayment | PartialWithRequiredKeyOf<NewPurchasePayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPurchasePayment | NewPurchasePayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type PurchasePaymentFormRawValue = FormValueOf<IPurchasePayment>;

type NewPurchasePaymentFormRawValue = FormValueOf<NewPurchasePayment>;

type PurchasePaymentFormDefaults = Pick<NewPurchasePayment, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type PurchasePaymentFormGroupContent = {
  id: FormControl<PurchasePaymentFormRawValue['id'] | NewPurchasePayment['id']>;
  date: FormControl<PurchasePaymentFormRawValue['date']>;
  amount: FormControl<PurchasePaymentFormRawValue['amount']>;
  active: FormControl<PurchasePaymentFormRawValue['active']>;
  createdBy: FormControl<PurchasePaymentFormRawValue['createdBy']>;
  createdDate: FormControl<PurchasePaymentFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<PurchasePaymentFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<PurchasePaymentFormRawValue['lastModifiedDate']>;
  purchase: FormControl<PurchasePaymentFormRawValue['purchase']>;
};

export type PurchasePaymentFormGroup = FormGroup<PurchasePaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PurchasePaymentFormService {
  createPurchasePaymentFormGroup(purchasePayment: PurchasePaymentFormGroupInput = { id: null }): PurchasePaymentFormGroup {
    const purchasePaymentRawValue = this.convertPurchasePaymentToPurchasePaymentRawValue({
      ...this.getFormDefaults(),
      ...purchasePayment,
    });
    return new FormGroup<PurchasePaymentFormGroupContent>({
      id: new FormControl(
        { value: purchasePaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(purchasePaymentRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(purchasePaymentRawValue.amount, {
        validators: [Validators.required],
      }),
      active: new FormControl(purchasePaymentRawValue.active),
      createdBy: new FormControl(purchasePaymentRawValue.createdBy),
      createdDate: new FormControl(purchasePaymentRawValue.createdDate),
      lastModifiedBy: new FormControl(purchasePaymentRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(purchasePaymentRawValue.lastModifiedDate),
      purchase: new FormControl(purchasePaymentRawValue.purchase),
    });
  }

  getPurchasePayment(form: PurchasePaymentFormGroup): IPurchasePayment | NewPurchasePayment {
    return this.convertPurchasePaymentRawValueToPurchasePayment(
      form.getRawValue() as PurchasePaymentFormRawValue | NewPurchasePaymentFormRawValue,
    );
  }

  resetForm(form: PurchasePaymentFormGroup, purchasePayment: PurchasePaymentFormGroupInput): void {
    const purchasePaymentRawValue = this.convertPurchasePaymentToPurchasePaymentRawValue({ ...this.getFormDefaults(), ...purchasePayment });
    form.reset(
      {
        ...purchasePaymentRawValue,
        id: { value: purchasePaymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PurchasePaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertPurchasePaymentRawValueToPurchasePayment(
    rawPurchasePayment: PurchasePaymentFormRawValue | NewPurchasePaymentFormRawValue,
  ): IPurchasePayment | NewPurchasePayment {
    return {
      ...rawPurchasePayment,
      date: dayjs(rawPurchasePayment.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawPurchasePayment.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawPurchasePayment.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertPurchasePaymentToPurchasePaymentRawValue(
    purchasePayment: IPurchasePayment | (Partial<NewPurchasePayment> & PurchasePaymentFormDefaults),
  ): PurchasePaymentFormRawValue | PartialWithRequiredKeyOf<NewPurchasePaymentFormRawValue> {
    return {
      ...purchasePayment,
      date: purchasePayment.date ? purchasePayment.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: purchasePayment.createdDate ? purchasePayment.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: purchasePayment.lastModifiedDate ? purchasePayment.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
