import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDailyCashDetail, NewDailyCashDetail } from '../daily-cash-detail.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDailyCashDetail for edit and NewDailyCashDetailFormGroupInput for create.
 */
type DailyCashDetailFormGroupInput = IDailyCashDetail | PartialWithRequiredKeyOf<NewDailyCashDetail>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDailyCashDetail | NewDailyCashDetail> = Omit<T, 'timestamp' | 'createdDate' | 'lastModifiedDate'> & {
  timestamp?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type DailyCashDetailFormRawValue = FormValueOf<IDailyCashDetail>;

type NewDailyCashDetailFormRawValue = FormValueOf<NewDailyCashDetail>;

type DailyCashDetailFormDefaults = Pick<NewDailyCashDetail, 'id' | 'timestamp' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type DailyCashDetailFormGroupContent = {
  id: FormControl<DailyCashDetailFormRawValue['id'] | NewDailyCashDetail['id']>;
  type: FormControl<DailyCashDetailFormRawValue['type']>;
  referenceId: FormControl<DailyCashDetailFormRawValue['referenceId']>;
  referenceType: FormControl<DailyCashDetailFormRawValue['referenceType']>;
  amount: FormControl<DailyCashDetailFormRawValue['amount']>;
  description: FormControl<DailyCashDetailFormRawValue['description']>;
  timestamp: FormControl<DailyCashDetailFormRawValue['timestamp']>;
  active: FormControl<DailyCashDetailFormRawValue['active']>;
  createdBy: FormControl<DailyCashDetailFormRawValue['createdBy']>;
  createdDate: FormControl<DailyCashDetailFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<DailyCashDetailFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<DailyCashDetailFormRawValue['lastModifiedDate']>;
  dailyCashReconciliation: FormControl<DailyCashDetailFormRawValue['dailyCashReconciliation']>;
};

export type DailyCashDetailFormGroup = FormGroup<DailyCashDetailFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DailyCashDetailFormService {
  createDailyCashDetailFormGroup(dailyCashDetail: DailyCashDetailFormGroupInput = { id: null }): DailyCashDetailFormGroup {
    const dailyCashDetailRawValue = this.convertDailyCashDetailToDailyCashDetailRawValue({
      ...this.getFormDefaults(),
      ...dailyCashDetail,
    });
    return new FormGroup<DailyCashDetailFormGroupContent>({
      id: new FormControl(
        { value: dailyCashDetailRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(dailyCashDetailRawValue.type, {
        validators: [Validators.required],
      }),
      referenceId: new FormControl(dailyCashDetailRawValue.referenceId, {
        validators: [Validators.required],
      }),
      referenceType: new FormControl(dailyCashDetailRawValue.referenceType, {
        validators: [Validators.required],
      }),
      amount: new FormControl(dailyCashDetailRawValue.amount, {
        validators: [Validators.required],
      }),
      description: new FormControl(dailyCashDetailRawValue.description),
      timestamp: new FormControl(dailyCashDetailRawValue.timestamp, {
        validators: [Validators.required],
      }),
      active: new FormControl(dailyCashDetailRawValue.active),
      createdBy: new FormControl(dailyCashDetailRawValue.createdBy),
      createdDate: new FormControl(dailyCashDetailRawValue.createdDate),
      lastModifiedBy: new FormControl(dailyCashDetailRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(dailyCashDetailRawValue.lastModifiedDate),
      dailyCashReconciliation: new FormControl(dailyCashDetailRawValue.dailyCashReconciliation),
    });
  }

  getDailyCashDetail(form: DailyCashDetailFormGroup): IDailyCashDetail | NewDailyCashDetail {
    return this.convertDailyCashDetailRawValueToDailyCashDetail(
      form.getRawValue() as DailyCashDetailFormRawValue | NewDailyCashDetailFormRawValue,
    );
  }

  resetForm(form: DailyCashDetailFormGroup, dailyCashDetail: DailyCashDetailFormGroupInput): void {
    const dailyCashDetailRawValue = this.convertDailyCashDetailToDailyCashDetailRawValue({ ...this.getFormDefaults(), ...dailyCashDetail });
    form.reset(
      {
        ...dailyCashDetailRawValue,
        id: { value: dailyCashDetailRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DailyCashDetailFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      timestamp: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertDailyCashDetailRawValueToDailyCashDetail(
    rawDailyCashDetail: DailyCashDetailFormRawValue | NewDailyCashDetailFormRawValue,
  ): IDailyCashDetail | NewDailyCashDetail {
    return {
      ...rawDailyCashDetail,
      timestamp: dayjs(rawDailyCashDetail.timestamp, DATE_TIME_FORMAT),
      createdDate: dayjs(rawDailyCashDetail.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawDailyCashDetail.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertDailyCashDetailToDailyCashDetailRawValue(
    dailyCashDetail: IDailyCashDetail | (Partial<NewDailyCashDetail> & DailyCashDetailFormDefaults),
  ): DailyCashDetailFormRawValue | PartialWithRequiredKeyOf<NewDailyCashDetailFormRawValue> {
    return {
      ...dailyCashDetail,
      timestamp: dailyCashDetail.timestamp ? dailyCashDetail.timestamp.format(DATE_TIME_FORMAT) : undefined,
      createdDate: dailyCashDetail.createdDate ? dailyCashDetail.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: dailyCashDetail.lastModifiedDate ? dailyCashDetail.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
