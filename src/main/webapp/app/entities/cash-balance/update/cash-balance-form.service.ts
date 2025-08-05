import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICashBalance, NewCashBalance } from '../cash-balance.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICashBalance for edit and NewCashBalanceFormGroupInput for create.
 */
type CashBalanceFormGroupInput = ICashBalance | PartialWithRequiredKeyOf<NewCashBalance>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICashBalance | NewCashBalance> = Omit<T, 'lastUpdated' | 'createdDate' | 'lastModifiedDate'> & {
  lastUpdated?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type CashBalanceFormRawValue = FormValueOf<ICashBalance>;

type NewCashBalanceFormRawValue = FormValueOf<NewCashBalance>;

type CashBalanceFormDefaults = Pick<NewCashBalance, 'id' | 'lastUpdated' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type CashBalanceFormGroupContent = {
  id: FormControl<CashBalanceFormRawValue['id'] | NewCashBalance['id']>;
  available: FormControl<CashBalanceFormRawValue['available']>;
  lastUpdated: FormControl<CashBalanceFormRawValue['lastUpdated']>;
  notes: FormControl<CashBalanceFormRawValue['notes']>;
  active: FormControl<CashBalanceFormRawValue['active']>;
  createdBy: FormControl<CashBalanceFormRawValue['createdBy']>;
  createdDate: FormControl<CashBalanceFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<CashBalanceFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<CashBalanceFormRawValue['lastModifiedDate']>;
};

export type CashBalanceFormGroup = FormGroup<CashBalanceFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CashBalanceFormService {
  createCashBalanceFormGroup(cashBalance: CashBalanceFormGroupInput = { id: null }): CashBalanceFormGroup {
    const cashBalanceRawValue = this.convertCashBalanceToCashBalanceRawValue({
      ...this.getFormDefaults(),
      ...cashBalance,
    });
    return new FormGroup<CashBalanceFormGroupContent>({
      id: new FormControl(
        { value: cashBalanceRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      available: new FormControl(cashBalanceRawValue.available, {
        validators: [Validators.required],
      }),
      lastUpdated: new FormControl(cashBalanceRawValue.lastUpdated),
      notes: new FormControl(cashBalanceRawValue.notes),
      active: new FormControl(cashBalanceRawValue.active),
      createdBy: new FormControl(cashBalanceRawValue.createdBy),
      createdDate: new FormControl(cashBalanceRawValue.createdDate),
      lastModifiedBy: new FormControl(cashBalanceRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(cashBalanceRawValue.lastModifiedDate),
    });
  }

  getCashBalance(form: CashBalanceFormGroup): ICashBalance | NewCashBalance {
    return this.convertCashBalanceRawValueToCashBalance(form.getRawValue() as CashBalanceFormRawValue | NewCashBalanceFormRawValue);
  }

  resetForm(form: CashBalanceFormGroup, cashBalance: CashBalanceFormGroupInput): void {
    const cashBalanceRawValue = this.convertCashBalanceToCashBalanceRawValue({ ...this.getFormDefaults(), ...cashBalance });
    form.reset(
      {
        ...cashBalanceRawValue,
        id: { value: cashBalanceRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CashBalanceFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      lastUpdated: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertCashBalanceRawValueToCashBalance(
    rawCashBalance: CashBalanceFormRawValue | NewCashBalanceFormRawValue,
  ): ICashBalance | NewCashBalance {
    return {
      ...rawCashBalance,
      lastUpdated: dayjs(rawCashBalance.lastUpdated, DATE_TIME_FORMAT),
      createdDate: dayjs(rawCashBalance.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawCashBalance.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertCashBalanceToCashBalanceRawValue(
    cashBalance: ICashBalance | (Partial<NewCashBalance> & CashBalanceFormDefaults),
  ): CashBalanceFormRawValue | PartialWithRequiredKeyOf<NewCashBalanceFormRawValue> {
    return {
      ...cashBalance,
      lastUpdated: cashBalance.lastUpdated ? cashBalance.lastUpdated.format(DATE_TIME_FORMAT) : undefined,
      createdDate: cashBalance.createdDate ? cashBalance.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: cashBalance.lastModifiedDate ? cashBalance.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
