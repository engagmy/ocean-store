import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISaleOperation, NewSaleOperation } from '../sale-operation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISaleOperation for edit and NewSaleOperationFormGroupInput for create.
 */
type SaleOperationFormGroupInput = ISaleOperation | PartialWithRequiredKeyOf<NewSaleOperation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISaleOperation | NewSaleOperation> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type SaleOperationFormRawValue = FormValueOf<ISaleOperation>;

type NewSaleOperationFormRawValue = FormValueOf<NewSaleOperation>;

type SaleOperationFormDefaults = Pick<NewSaleOperation, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type SaleOperationFormGroupContent = {
  id: FormControl<SaleOperationFormRawValue['id'] | NewSaleOperation['id']>;
  date: FormControl<SaleOperationFormRawValue['date']>;
  totalQuantity: FormControl<SaleOperationFormRawValue['totalQuantity']>;
  totalAmount: FormControl<SaleOperationFormRawValue['totalAmount']>;
  totalDiscount: FormControl<SaleOperationFormRawValue['totalDiscount']>;
  grandTotal: FormControl<SaleOperationFormRawValue['grandTotal']>;
  active: FormControl<SaleOperationFormRawValue['active']>;
  createdBy: FormControl<SaleOperationFormRawValue['createdBy']>;
  createdDate: FormControl<SaleOperationFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<SaleOperationFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<SaleOperationFormRawValue['lastModifiedDate']>;
  bill: FormControl<SaleOperationFormRawValue['bill']>;
  customer: FormControl<SaleOperationFormRawValue['customer']>;
};

export type SaleOperationFormGroup = FormGroup<SaleOperationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleOperationFormService {
  createSaleOperationFormGroup(saleOperation: SaleOperationFormGroupInput = { id: null }): SaleOperationFormGroup {
    const saleOperationRawValue = this.convertSaleOperationToSaleOperationRawValue({
      ...this.getFormDefaults(),
      ...saleOperation,
    });
    return new FormGroup<SaleOperationFormGroupContent>({
      id: new FormControl(
        { value: saleOperationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(saleOperationRawValue.date, {
        validators: [Validators.required],
      }),
      totalQuantity: new FormControl(saleOperationRawValue.totalQuantity, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(saleOperationRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      totalDiscount: new FormControl(saleOperationRawValue.totalDiscount),
      grandTotal: new FormControl(saleOperationRawValue.grandTotal, {
        validators: [Validators.required],
      }),
      active: new FormControl(saleOperationRawValue.active),
      createdBy: new FormControl(saleOperationRawValue.createdBy),
      createdDate: new FormControl(saleOperationRawValue.createdDate),
      lastModifiedBy: new FormControl(saleOperationRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(saleOperationRawValue.lastModifiedDate),
      bill: new FormControl(saleOperationRawValue.bill),
      customer: new FormControl(saleOperationRawValue.customer),
    });
  }

  getSaleOperation(form: SaleOperationFormGroup): ISaleOperation | NewSaleOperation {
    return this.convertSaleOperationRawValueToSaleOperation(form.getRawValue() as SaleOperationFormRawValue | NewSaleOperationFormRawValue);
  }

  resetForm(form: SaleOperationFormGroup, saleOperation: SaleOperationFormGroupInput): void {
    const saleOperationRawValue = this.convertSaleOperationToSaleOperationRawValue({ ...this.getFormDefaults(), ...saleOperation });
    form.reset(
      {
        ...saleOperationRawValue,
        id: { value: saleOperationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SaleOperationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertSaleOperationRawValueToSaleOperation(
    rawSaleOperation: SaleOperationFormRawValue | NewSaleOperationFormRawValue,
  ): ISaleOperation | NewSaleOperation {
    return {
      ...rawSaleOperation,
      date: dayjs(rawSaleOperation.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawSaleOperation.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawSaleOperation.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertSaleOperationToSaleOperationRawValue(
    saleOperation: ISaleOperation | (Partial<NewSaleOperation> & SaleOperationFormDefaults),
  ): SaleOperationFormRawValue | PartialWithRequiredKeyOf<NewSaleOperationFormRawValue> {
    return {
      ...saleOperation,
      date: saleOperation.date ? saleOperation.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: saleOperation.createdDate ? saleOperation.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: saleOperation.lastModifiedDate ? saleOperation.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
