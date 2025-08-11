import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOutgoingPayment, NewOutgoingPayment } from '../outgoing-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOutgoingPayment for edit and NewOutgoingPaymentFormGroupInput for create.
 */
type OutgoingPaymentFormGroupInput = IOutgoingPayment | PartialWithRequiredKeyOf<NewOutgoingPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOutgoingPayment | NewOutgoingPayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type OutgoingPaymentFormRawValue = FormValueOf<IOutgoingPayment>;

type NewOutgoingPaymentFormRawValue = FormValueOf<NewOutgoingPayment>;

type OutgoingPaymentFormDefaults = Pick<NewOutgoingPayment, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type OutgoingPaymentFormGroupContent = {
  id: FormControl<OutgoingPaymentFormRawValue['id'] | NewOutgoingPayment['id']>;
  date: FormControl<OutgoingPaymentFormRawValue['date']>;
  amount: FormControl<OutgoingPaymentFormRawValue['amount']>;
  reason: FormControl<OutgoingPaymentFormRawValue['reason']>;
  notes: FormControl<OutgoingPaymentFormRawValue['notes']>;
  active: FormControl<OutgoingPaymentFormRawValue['active']>;
  createdBy: FormControl<OutgoingPaymentFormRawValue['createdBy']>;
  createdDate: FormControl<OutgoingPaymentFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<OutgoingPaymentFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<OutgoingPaymentFormRawValue['lastModifiedDate']>;
};

export type OutgoingPaymentFormGroup = FormGroup<OutgoingPaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OutgoingPaymentFormService {
  createOutgoingPaymentFormGroup(outgoingPayment: OutgoingPaymentFormGroupInput = { id: null }): OutgoingPaymentFormGroup {
    const outgoingPaymentRawValue = this.convertOutgoingPaymentToOutgoingPaymentRawValue({
      ...this.getFormDefaults(),
      ...outgoingPayment,
    });
    return new FormGroup<OutgoingPaymentFormGroupContent>({
      id: new FormControl(
        { value: outgoingPaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(outgoingPaymentRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(outgoingPaymentRawValue.amount, {
        validators: [Validators.required],
      }),
      reason: new FormControl(outgoingPaymentRawValue.reason),
      notes: new FormControl(outgoingPaymentRawValue.notes),
      active: new FormControl(outgoingPaymentRawValue.active),
      createdBy: new FormControl(outgoingPaymentRawValue.createdBy),
      createdDate: new FormControl(outgoingPaymentRawValue.createdDate),
      lastModifiedBy: new FormControl(outgoingPaymentRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(outgoingPaymentRawValue.lastModifiedDate),
    });
  }

  getOutgoingPayment(form: OutgoingPaymentFormGroup): IOutgoingPayment | NewOutgoingPayment {
    return this.convertOutgoingPaymentRawValueToOutgoingPayment(
      form.getRawValue() as OutgoingPaymentFormRawValue | NewOutgoingPaymentFormRawValue,
    );
  }

  resetForm(form: OutgoingPaymentFormGroup, outgoingPayment: OutgoingPaymentFormGroupInput): void {
    const outgoingPaymentRawValue = this.convertOutgoingPaymentToOutgoingPaymentRawValue({ ...this.getFormDefaults(), ...outgoingPayment });
    form.reset(
      {
        ...outgoingPaymentRawValue,
        id: { value: outgoingPaymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): OutgoingPaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertOutgoingPaymentRawValueToOutgoingPayment(
    rawOutgoingPayment: OutgoingPaymentFormRawValue | NewOutgoingPaymentFormRawValue,
  ): IOutgoingPayment | NewOutgoingPayment {
    return {
      ...rawOutgoingPayment,
      date: dayjs(rawOutgoingPayment.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawOutgoingPayment.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawOutgoingPayment.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertOutgoingPaymentToOutgoingPaymentRawValue(
    outgoingPayment: IOutgoingPayment | (Partial<NewOutgoingPayment> & OutgoingPaymentFormDefaults),
  ): OutgoingPaymentFormRawValue | PartialWithRequiredKeyOf<NewOutgoingPaymentFormRawValue> {
    return {
      ...outgoingPayment,
      date: outgoingPayment.date ? outgoingPayment.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: outgoingPayment.createdDate ? outgoingPayment.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: outgoingPayment.lastModifiedDate ? outgoingPayment.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
