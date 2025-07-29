import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISupplier, NewSupplier } from '../supplier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISupplier for edit and NewSupplierFormGroupInput for create.
 */
type SupplierFormGroupInput = ISupplier | PartialWithRequiredKeyOf<NewSupplier>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISupplier | NewSupplier> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type SupplierFormRawValue = FormValueOf<ISupplier>;

type NewSupplierFormRawValue = FormValueOf<NewSupplier>;

type SupplierFormDefaults = Pick<NewSupplier, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type SupplierFormGroupContent = {
  id: FormControl<SupplierFormRawValue['id'] | NewSupplier['id']>;
  name: FormControl<SupplierFormRawValue['name']>;
  phone: FormControl<SupplierFormRawValue['phone']>;
  address: FormControl<SupplierFormRawValue['address']>;
  active: FormControl<SupplierFormRawValue['active']>;
  createdBy: FormControl<SupplierFormRawValue['createdBy']>;
  createdDate: FormControl<SupplierFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<SupplierFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<SupplierFormRawValue['lastModifiedDate']>;
};

export type SupplierFormGroup = FormGroup<SupplierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SupplierFormService {
  createSupplierFormGroup(supplier: SupplierFormGroupInput = { id: null }): SupplierFormGroup {
    const supplierRawValue = this.convertSupplierToSupplierRawValue({
      ...this.getFormDefaults(),
      ...supplier,
    });
    return new FormGroup<SupplierFormGroupContent>({
      id: new FormControl(
        { value: supplierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(supplierRawValue.name, {
        validators: [Validators.required],
      }),
      phone: new FormControl(supplierRawValue.phone),
      address: new FormControl(supplierRawValue.address),
      active: new FormControl(supplierRawValue.active),
      createdBy: new FormControl(supplierRawValue.createdBy),
      createdDate: new FormControl(supplierRawValue.createdDate),
      lastModifiedBy: new FormControl(supplierRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(supplierRawValue.lastModifiedDate),
    });
  }

  getSupplier(form: SupplierFormGroup): ISupplier | NewSupplier {
    return this.convertSupplierRawValueToSupplier(form.getRawValue() as SupplierFormRawValue | NewSupplierFormRawValue);
  }

  resetForm(form: SupplierFormGroup, supplier: SupplierFormGroupInput): void {
    const supplierRawValue = this.convertSupplierToSupplierRawValue({ ...this.getFormDefaults(), ...supplier });
    form.reset(
      {
        ...supplierRawValue,
        id: { value: supplierRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SupplierFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertSupplierRawValueToSupplier(rawSupplier: SupplierFormRawValue | NewSupplierFormRawValue): ISupplier | NewSupplier {
    return {
      ...rawSupplier,
      createdDate: dayjs(rawSupplier.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawSupplier.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertSupplierToSupplierRawValue(
    supplier: ISupplier | (Partial<NewSupplier> & SupplierFormDefaults),
  ): SupplierFormRawValue | PartialWithRequiredKeyOf<NewSupplierFormRawValue> {
    return {
      ...supplier,
      createdDate: supplier.createdDate ? supplier.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: supplier.lastModifiedDate ? supplier.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
