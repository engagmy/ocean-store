import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISalaryPayment, NewSalaryPayment } from '../salary-payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISalaryPayment for edit and NewSalaryPaymentFormGroupInput for create.
 */
type SalaryPaymentFormGroupInput = ISalaryPayment | PartialWithRequiredKeyOf<NewSalaryPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISalaryPayment | NewSalaryPayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type SalaryPaymentFormRawValue = FormValueOf<ISalaryPayment>;

type NewSalaryPaymentFormRawValue = FormValueOf<NewSalaryPayment>;

type SalaryPaymentFormDefaults = Pick<NewSalaryPayment, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type SalaryPaymentFormGroupContent = {
  id: FormControl<SalaryPaymentFormRawValue['id'] | NewSalaryPayment['id']>;
  date: FormControl<SalaryPaymentFormRawValue['date']>;
  amount: FormControl<SalaryPaymentFormRawValue['amount']>;
  active: FormControl<SalaryPaymentFormRawValue['active']>;
  createdBy: FormControl<SalaryPaymentFormRawValue['createdBy']>;
  createdDate: FormControl<SalaryPaymentFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<SalaryPaymentFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<SalaryPaymentFormRawValue['lastModifiedDate']>;
  employee: FormControl<SalaryPaymentFormRawValue['employee']>;
};

export type SalaryPaymentFormGroup = FormGroup<SalaryPaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SalaryPaymentFormService {
  createSalaryPaymentFormGroup(salaryPayment: SalaryPaymentFormGroupInput = { id: null }): SalaryPaymentFormGroup {
    const salaryPaymentRawValue = this.convertSalaryPaymentToSalaryPaymentRawValue({
      ...this.getFormDefaults(),
      ...salaryPayment,
    });
    return new FormGroup<SalaryPaymentFormGroupContent>({
      id: new FormControl(
        { value: salaryPaymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(salaryPaymentRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(salaryPaymentRawValue.amount, {
        validators: [Validators.required],
      }),
      active: new FormControl(salaryPaymentRawValue.active),
      createdBy: new FormControl(salaryPaymentRawValue.createdBy),
      createdDate: new FormControl(salaryPaymentRawValue.createdDate),
      lastModifiedBy: new FormControl(salaryPaymentRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(salaryPaymentRawValue.lastModifiedDate),
      employee: new FormControl(salaryPaymentRawValue.employee),
    });
  }

  getSalaryPayment(form: SalaryPaymentFormGroup): ISalaryPayment | NewSalaryPayment {
    return this.convertSalaryPaymentRawValueToSalaryPayment(form.getRawValue() as SalaryPaymentFormRawValue | NewSalaryPaymentFormRawValue);
  }

  resetForm(form: SalaryPaymentFormGroup, salaryPayment: SalaryPaymentFormGroupInput): void {
    const salaryPaymentRawValue = this.convertSalaryPaymentToSalaryPaymentRawValue({ ...this.getFormDefaults(), ...salaryPayment });
    form.reset(
      {
        ...salaryPaymentRawValue,
        id: { value: salaryPaymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SalaryPaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertSalaryPaymentRawValueToSalaryPayment(
    rawSalaryPayment: SalaryPaymentFormRawValue | NewSalaryPaymentFormRawValue,
  ): ISalaryPayment | NewSalaryPayment {
    return {
      ...rawSalaryPayment,
      date: dayjs(rawSalaryPayment.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawSalaryPayment.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawSalaryPayment.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertSalaryPaymentToSalaryPaymentRawValue(
    salaryPayment: ISalaryPayment | (Partial<NewSalaryPayment> & SalaryPaymentFormDefaults),
  ): SalaryPaymentFormRawValue | PartialWithRequiredKeyOf<NewSalaryPaymentFormRawValue> {
    return {
      ...salaryPayment,
      date: salaryPayment.date ? salaryPayment.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: salaryPayment.createdDate ? salaryPayment.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: salaryPayment.lastModifiedDate ? salaryPayment.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
