import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISalePayment, NewSalePayment } from '../sale-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISalePayment for edit and NewSalePaymentFormGroupInput for create.
 */
type SalePaymentFormGroupInput = ISalePayment | PartialWithRequiredKeyOf<NewSalePayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISalePayment | NewSalePayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type SalePaymentFormRawValue = FormValueOf<ISalePayment>;

type NewSalePaymentFormRawValue = FormValueOf<NewSalePayment>;

type SalePaymentFormDefaults = Pick<NewSalePayment, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type SalePaymentFormGroupContent = {
  id: FormControl<SalePaymentFormRawValue['id'] | NewSalePayment['id']>;
  date: FormControl<SalePaymentFormRawValue['date']>;
  amount: FormControl<SalePaymentFormRawValue['amount']>;
  active: FormControl<SalePaymentFormRawValue['active']>;
  createdBy: FormControl<SalePaymentFormRawValue['createdBy']>;
  createdDate: FormControl<SalePaymentFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<SalePaymentFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<SalePaymentFormRawValue['lastModifiedDate']>;
  sale: FormControl<SalePaymentFormRawValue['sale']>;
};

export type SalePaymentFormGroup = FormGroup<SalePaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SalePaymentFormService {
  createSalePaymentFormGroup(salePayment: SalePaymentFormGroupInput = { id: null }): SalePaymentFormGroup {
    const salePaymentRawValue = this.convertSalePaymentToSalePaymentRawValue({
      ...this.getFormDefaults(),
      ...salePayment,
    });
    return new FormGroup<SalePaymentFormGroupContent>({
      id: new FormControl(
        { value: salePaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(salePaymentRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(salePaymentRawValue.amount, {
        validators: [Validators.required],
      }),
      active: new FormControl(salePaymentRawValue.active),
      createdBy: new FormControl(salePaymentRawValue.createdBy),
      createdDate: new FormControl(salePaymentRawValue.createdDate),
      lastModifiedBy: new FormControl(salePaymentRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(salePaymentRawValue.lastModifiedDate),
      sale: new FormControl(salePaymentRawValue.sale),
    });
  }

  getSalePayment(form: SalePaymentFormGroup): ISalePayment | NewSalePayment {
    return this.convertSalePaymentRawValueToSalePayment(form.getRawValue() as SalePaymentFormRawValue | NewSalePaymentFormRawValue);
  }

  resetForm(form: SalePaymentFormGroup, salePayment: SalePaymentFormGroupInput): void {
    const salePaymentRawValue = this.convertSalePaymentToSalePaymentRawValue({ ...this.getFormDefaults(), ...salePayment });
    form.reset(
      {
        ...salePaymentRawValue,
        id: { value: salePaymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SalePaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertSalePaymentRawValueToSalePayment(
    rawSalePayment: SalePaymentFormRawValue | NewSalePaymentFormRawValue,
  ): ISalePayment | NewSalePayment {
    return {
      ...rawSalePayment,
      date: dayjs(rawSalePayment.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawSalePayment.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawSalePayment.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertSalePaymentToSalePaymentRawValue(
    salePayment: ISalePayment | (Partial<NewSalePayment> & SalePaymentFormDefaults),
  ): SalePaymentFormRawValue | PartialWithRequiredKeyOf<NewSalePaymentFormRawValue> {
    return {
      ...salePayment,
      date: salePayment.date ? salePayment.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: salePayment.createdDate ? salePayment.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: salePayment.lastModifiedDate ? salePayment.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
