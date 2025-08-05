import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBill, NewBill } from '../bill.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBill for edit and NewBillFormGroupInput for create.
 */
type BillFormGroupInput = IBill | PartialWithRequiredKeyOf<NewBill>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBill | NewBill> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type BillFormRawValue = FormValueOf<IBill>;

type NewBillFormRawValue = FormValueOf<NewBill>;

type BillFormDefaults = Pick<NewBill, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type BillFormGroupContent = {
  id: FormControl<BillFormRawValue['id'] | NewBill['id']>;
  billNumber: FormControl<BillFormRawValue['billNumber']>;
  date: FormControl<BillFormRawValue['date']>;
  totalAmount: FormControl<BillFormRawValue['totalAmount']>;
  taxAmount: FormControl<BillFormRawValue['taxAmount']>;
  discountAmount: FormControl<BillFormRawValue['discountAmount']>;
  paidAmount: FormControl<BillFormRawValue['paidAmount']>;
  dueAmount: FormControl<BillFormRawValue['dueAmount']>;
  notes: FormControl<BillFormRawValue['notes']>;
  active: FormControl<BillFormRawValue['active']>;
  createdBy: FormControl<BillFormRawValue['createdBy']>;
  createdDate: FormControl<BillFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<BillFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<BillFormRawValue['lastModifiedDate']>;
};

export type BillFormGroup = FormGroup<BillFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BillFormService {
  createBillFormGroup(bill: BillFormGroupInput = { id: null }): BillFormGroup {
    const billRawValue = this.convertBillToBillRawValue({
      ...this.getFormDefaults(),
      ...bill,
    });
    return new FormGroup<BillFormGroupContent>({
      id: new FormControl(
        { value: billRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      billNumber: new FormControl(billRawValue.billNumber, {
        validators: [Validators.required],
      }),
      date: new FormControl(billRawValue.date, {
        validators: [Validators.required],
      }),
      totalAmount: new FormControl(billRawValue.totalAmount, {
        validators: [Validators.required],
      }),
      taxAmount: new FormControl(billRawValue.taxAmount),
      discountAmount: new FormControl(billRawValue.discountAmount),
      paidAmount: new FormControl(billRawValue.paidAmount),
      dueAmount: new FormControl(billRawValue.dueAmount),
      notes: new FormControl(billRawValue.notes),
      active: new FormControl(billRawValue.active),
      createdBy: new FormControl(billRawValue.createdBy),
      createdDate: new FormControl(billRawValue.createdDate),
      lastModifiedBy: new FormControl(billRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(billRawValue.lastModifiedDate),
    });
  }

  getBill(form: BillFormGroup): IBill | NewBill {
    return this.convertBillRawValueToBill(form.getRawValue() as BillFormRawValue | NewBillFormRawValue);
  }

  resetForm(form: BillFormGroup, bill: BillFormGroupInput): void {
    const billRawValue = this.convertBillToBillRawValue({ ...this.getFormDefaults(), ...bill });
    form.reset(
      {
        ...billRawValue,
        id: { value: billRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BillFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertBillRawValueToBill(rawBill: BillFormRawValue | NewBillFormRawValue): IBill | NewBill {
    return {
      ...rawBill,
      date: dayjs(rawBill.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawBill.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawBill.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertBillToBillRawValue(
    bill: IBill | (Partial<NewBill> & BillFormDefaults),
  ): BillFormRawValue | PartialWithRequiredKeyOf<NewBillFormRawValue> {
    return {
      ...bill,
      date: bill.date ? bill.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: bill.createdDate ? bill.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: bill.lastModifiedDate ? bill.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
