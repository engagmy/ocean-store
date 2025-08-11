import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDailyCashReconciliation, NewDailyCashReconciliation } from '../daily-cash-reconciliation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDailyCashReconciliation for edit and NewDailyCashReconciliationFormGroupInput for create.
 */
type DailyCashReconciliationFormGroupInput = IDailyCashReconciliation | PartialWithRequiredKeyOf<NewDailyCashReconciliation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDailyCashReconciliation | NewDailyCashReconciliation> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type DailyCashReconciliationFormRawValue = FormValueOf<IDailyCashReconciliation>;

type NewDailyCashReconciliationFormRawValue = FormValueOf<NewDailyCashReconciliation>;

type DailyCashReconciliationFormDefaults = Pick<NewDailyCashReconciliation, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type DailyCashReconciliationFormGroupContent = {
  id: FormControl<DailyCashReconciliationFormRawValue['id'] | NewDailyCashReconciliation['id']>;
  date: FormControl<DailyCashReconciliationFormRawValue['date']>;
  openingBalance: FormControl<DailyCashReconciliationFormRawValue['openingBalance']>;
  totalSales: FormControl<DailyCashReconciliationFormRawValue['totalSales']>;
  totalPurchases: FormControl<DailyCashReconciliationFormRawValue['totalPurchases']>;
  totalSalaryPaid: FormControl<DailyCashReconciliationFormRawValue['totalSalaryPaid']>;
  ownerDeposits: FormControl<DailyCashReconciliationFormRawValue['ownerDeposits']>;
  withdrawals: FormControl<DailyCashReconciliationFormRawValue['withdrawals']>;
  closingBalance: FormControl<DailyCashReconciliationFormRawValue['closingBalance']>;
  notes: FormControl<DailyCashReconciliationFormRawValue['notes']>;
  active: FormControl<DailyCashReconciliationFormRawValue['active']>;
  createdBy: FormControl<DailyCashReconciliationFormRawValue['createdBy']>;
  createdDate: FormControl<DailyCashReconciliationFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<DailyCashReconciliationFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<DailyCashReconciliationFormRawValue['lastModifiedDate']>;
};

export type DailyCashReconciliationFormGroup = FormGroup<DailyCashReconciliationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DailyCashReconciliationFormService {
  createDailyCashReconciliationFormGroup(
    dailyCashReconciliation: DailyCashReconciliationFormGroupInput = { id: null },
  ): DailyCashReconciliationFormGroup {
    const dailyCashReconciliationRawValue = this.convertDailyCashReconciliationToDailyCashReconciliationRawValue({
      ...this.getFormDefaults(),
      ...dailyCashReconciliation,
    });
    return new FormGroup<DailyCashReconciliationFormGroupContent>({
      id: new FormControl(
        { value: dailyCashReconciliationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(dailyCashReconciliationRawValue.date, {
        validators: [Validators.required],
      }),
      openingBalance: new FormControl(dailyCashReconciliationRawValue.openingBalance),
      totalSales: new FormControl(dailyCashReconciliationRawValue.totalSales),
      totalPurchases: new FormControl(dailyCashReconciliationRawValue.totalPurchases),
      totalSalaryPaid: new FormControl(dailyCashReconciliationRawValue.totalSalaryPaid),
      ownerDeposits: new FormControl(dailyCashReconciliationRawValue.ownerDeposits),
      withdrawals: new FormControl(dailyCashReconciliationRawValue.withdrawals),
      closingBalance: new FormControl(dailyCashReconciliationRawValue.closingBalance),
      notes: new FormControl(dailyCashReconciliationRawValue.notes),
      active: new FormControl(dailyCashReconciliationRawValue.active),
      createdBy: new FormControl(dailyCashReconciliationRawValue.createdBy),
      createdDate: new FormControl(dailyCashReconciliationRawValue.createdDate),
      lastModifiedBy: new FormControl(dailyCashReconciliationRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(dailyCashReconciliationRawValue.lastModifiedDate),
    });
  }

  getDailyCashReconciliation(form: DailyCashReconciliationFormGroup): IDailyCashReconciliation | NewDailyCashReconciliation {
    return this.convertDailyCashReconciliationRawValueToDailyCashReconciliation(
      form.getRawValue() as DailyCashReconciliationFormRawValue | NewDailyCashReconciliationFormRawValue,
    );
  }

  resetForm(form: DailyCashReconciliationFormGroup, dailyCashReconciliation: DailyCashReconciliationFormGroupInput): void {
    const dailyCashReconciliationRawValue = this.convertDailyCashReconciliationToDailyCashReconciliationRawValue({
      ...this.getFormDefaults(),
      ...dailyCashReconciliation,
    });
    form.reset(
      {
        ...dailyCashReconciliationRawValue,
        id: { value: dailyCashReconciliationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DailyCashReconciliationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertDailyCashReconciliationRawValueToDailyCashReconciliation(
    rawDailyCashReconciliation: DailyCashReconciliationFormRawValue | NewDailyCashReconciliationFormRawValue,
  ): IDailyCashReconciliation | NewDailyCashReconciliation {
    return {
      ...rawDailyCashReconciliation,
      createdDate: dayjs(rawDailyCashReconciliation.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawDailyCashReconciliation.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertDailyCashReconciliationToDailyCashReconciliationRawValue(
    dailyCashReconciliation: IDailyCashReconciliation | (Partial<NewDailyCashReconciliation> & DailyCashReconciliationFormDefaults),
  ): DailyCashReconciliationFormRawValue | PartialWithRequiredKeyOf<NewDailyCashReconciliationFormRawValue> {
    return {
      ...dailyCashReconciliation,
      createdDate: dailyCashReconciliation.createdDate ? dailyCashReconciliation.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: dailyCashReconciliation.lastModifiedDate
        ? dailyCashReconciliation.lastModifiedDate.format(DATE_TIME_FORMAT)
        : undefined,
    };
  }
}
