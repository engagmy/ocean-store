import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInventoryTransaction, NewInventoryTransaction } from '../inventory-transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInventoryTransaction for edit and NewInventoryTransactionFormGroupInput for create.
 */
type InventoryTransactionFormGroupInput = IInventoryTransaction | PartialWithRequiredKeyOf<NewInventoryTransaction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInventoryTransaction | NewInventoryTransaction> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type InventoryTransactionFormRawValue = FormValueOf<IInventoryTransaction>;

type NewInventoryTransactionFormRawValue = FormValueOf<NewInventoryTransaction>;

type InventoryTransactionFormDefaults = Pick<NewInventoryTransaction, 'id' | 'date' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type InventoryTransactionFormGroupContent = {
  id: FormControl<InventoryTransactionFormRawValue['id'] | NewInventoryTransaction['id']>;
  date: FormControl<InventoryTransactionFormRawValue['date']>;
  type: FormControl<InventoryTransactionFormRawValue['type']>;
  quantity: FormControl<InventoryTransactionFormRawValue['quantity']>;
  active: FormControl<InventoryTransactionFormRawValue['active']>;
  createdBy: FormControl<InventoryTransactionFormRawValue['createdBy']>;
  createdDate: FormControl<InventoryTransactionFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<InventoryTransactionFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<InventoryTransactionFormRawValue['lastModifiedDate']>;
  product: FormControl<InventoryTransactionFormRawValue['product']>;
};

export type InventoryTransactionFormGroup = FormGroup<InventoryTransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InventoryTransactionFormService {
  createInventoryTransactionFormGroup(
    inventoryTransaction: InventoryTransactionFormGroupInput = { id: null },
  ): InventoryTransactionFormGroup {
    const inventoryTransactionRawValue = this.convertInventoryTransactionToInventoryTransactionRawValue({
      ...this.getFormDefaults(),
      ...inventoryTransaction,
    });
    return new FormGroup<InventoryTransactionFormGroupContent>({
      id: new FormControl(
        { value: inventoryTransactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(inventoryTransactionRawValue.date, {
        validators: [Validators.required],
      }),
      type: new FormControl(inventoryTransactionRawValue.type, {
        validators: [Validators.required],
      }),
      quantity: new FormControl(inventoryTransactionRawValue.quantity, {
        validators: [Validators.required],
      }),
      active: new FormControl(inventoryTransactionRawValue.active),
      createdBy: new FormControl(inventoryTransactionRawValue.createdBy),
      createdDate: new FormControl(inventoryTransactionRawValue.createdDate),
      lastModifiedBy: new FormControl(inventoryTransactionRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(inventoryTransactionRawValue.lastModifiedDate),
      product: new FormControl(inventoryTransactionRawValue.product),
    });
  }

  getInventoryTransaction(form: InventoryTransactionFormGroup): IInventoryTransaction | NewInventoryTransaction {
    return this.convertInventoryTransactionRawValueToInventoryTransaction(
      form.getRawValue() as InventoryTransactionFormRawValue | NewInventoryTransactionFormRawValue,
    );
  }

  resetForm(form: InventoryTransactionFormGroup, inventoryTransaction: InventoryTransactionFormGroupInput): void {
    const inventoryTransactionRawValue = this.convertInventoryTransactionToInventoryTransactionRawValue({
      ...this.getFormDefaults(),
      ...inventoryTransaction,
    });
    form.reset(
      {
        ...inventoryTransactionRawValue,
        id: { value: inventoryTransactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InventoryTransactionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertInventoryTransactionRawValueToInventoryTransaction(
    rawInventoryTransaction: InventoryTransactionFormRawValue | NewInventoryTransactionFormRawValue,
  ): IInventoryTransaction | NewInventoryTransaction {
    return {
      ...rawInventoryTransaction,
      date: dayjs(rawInventoryTransaction.date, DATE_TIME_FORMAT),
      createdDate: dayjs(rawInventoryTransaction.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawInventoryTransaction.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertInventoryTransactionToInventoryTransactionRawValue(
    inventoryTransaction: IInventoryTransaction | (Partial<NewInventoryTransaction> & InventoryTransactionFormDefaults),
  ): InventoryTransactionFormRawValue | PartialWithRequiredKeyOf<NewInventoryTransactionFormRawValue> {
    return {
      ...inventoryTransaction,
      date: inventoryTransaction.date ? inventoryTransaction.date.format(DATE_TIME_FORMAT) : undefined,
      createdDate: inventoryTransaction.createdDate ? inventoryTransaction.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: inventoryTransaction.lastModifiedDate ? inventoryTransaction.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
