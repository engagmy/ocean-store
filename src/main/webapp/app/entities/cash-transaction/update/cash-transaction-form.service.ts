import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICashTransaction, NewCashTransaction } from '../cash-transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICashTransaction for edit and NewCashTransactionFormGroupInput for create.
 */
type CashTransactionFormGroupInput = ICashTransaction | PartialWithRequiredKeyOf<NewCashTransaction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICashTransaction | NewCashTransaction> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type CashTransactionFormRawValue = FormValueOf<ICashTransaction>;

type NewCashTransactionFormRawValue = FormValueOf<NewCashTransaction>;

type CashTransactionFormDefaults = Pick<NewCashTransaction, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type CashTransactionFormGroupContent = {
  id: FormControl<CashTransactionFormRawValue['id'] | NewCashTransaction['id']>;
  date: FormControl<CashTransactionFormRawValue['date']>;
  amount: FormControl<CashTransactionFormRawValue['amount']>;
  type: FormControl<CashTransactionFormRawValue['type']>;
  reason: FormControl<CashTransactionFormRawValue['reason']>;
  active: FormControl<CashTransactionFormRawValue['active']>;
  createdBy: FormControl<CashTransactionFormRawValue['createdBy']>;
  createdDate: FormControl<CashTransactionFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<CashTransactionFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<CashTransactionFormRawValue['lastModifiedDate']>;
};

export type CashTransactionFormGroup = FormGroup<CashTransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CashTransactionFormService {
  createCashTransactionFormGroup(cashTransaction: CashTransactionFormGroupInput = { id: null }): CashTransactionFormGroup {
    const cashTransactionRawValue = this.convertCashTransactionToCashTransactionRawValue({
      ...this.getFormDefaults(),
      ...cashTransaction,
    });
    return new FormGroup<CashTransactionFormGroupContent>({
      id: new FormControl(
        { value: cashTransactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(cashTransactionRawValue.date, {
        validators: [Validators.required],
      }),
      amount: new FormControl(cashTransactionRawValue.amount, {
        validators: [Validators.required],
      }),
      type: new FormControl(cashTransactionRawValue.type, {
        validators: [Validators.required],
      }),
      reason: new FormControl(cashTransactionRawValue.reason),
      active: new FormControl(cashTransactionRawValue.active),
      createdBy: new FormControl(cashTransactionRawValue.createdBy),
      createdDate: new FormControl(cashTransactionRawValue.createdDate),
      lastModifiedBy: new FormControl(cashTransactionRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(cashTransactionRawValue.lastModifiedDate),
    });
  }

  getCashTransaction(form: CashTransactionFormGroup): ICashTransaction | NewCashTransaction {
    return this.convertCashTransactionRawValueToCashTransaction(
      form.getRawValue() as CashTransactionFormRawValue | NewCashTransactionFormRawValue,
    );
  }

  resetForm(form: CashTransactionFormGroup, cashTransaction: CashTransactionFormGroupInput): void {
    const cashTransactionRawValue = this.convertCashTransactionToCashTransactionRawValue({ ...this.getFormDefaults(), ...cashTransaction });
    form.reset(
      {
        ...cashTransactionRawValue,
        id: { value: cashTransactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CashTransactionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertCashTransactionRawValueToCashTransaction(
    rawCashTransaction: CashTransactionFormRawValue | NewCashTransactionFormRawValue,
  ): ICashTransaction | NewCashTransaction {
    return {
      ...rawCashTransaction,
      date: dayjs(rawCashTransaction.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawCashTransaction.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawCashTransaction.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertCashTransactionToCashTransactionRawValue(
    cashTransaction: ICashTransaction | (Partial<NewCashTransaction> & CashTransactionFormDefaults),
  ): CashTransactionFormRawValue | PartialWithRequiredKeyOf<NewCashTransactionFormRawValue> {
    return {
      ...cashTransaction,
      date: cashTransaction.date ? cashTransaction.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: cashTransaction.createdDate ? cashTransaction.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: cashTransaction.lastModifiedDate ? cashTransaction.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
