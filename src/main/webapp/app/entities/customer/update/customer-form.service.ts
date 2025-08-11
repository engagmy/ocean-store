import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICustomer, NewCustomer } from '../customer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomer for edit and NewCustomerFormGroupInput for create.
 */
type CustomerFormGroupInput = ICustomer | PartialWithRequiredKeyOf<NewCustomer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICustomer | NewCustomer> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type CustomerFormRawValue = FormValueOf<ICustomer>;

type NewCustomerFormRawValue = FormValueOf<NewCustomer>;

type CustomerFormDefaults = Pick<NewCustomer, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type CustomerFormGroupContent = {
  id: FormControl<CustomerFormRawValue['id'] | NewCustomer['id']>;
  name: FormControl<CustomerFormRawValue['name']>;
  phone: FormControl<CustomerFormRawValue['phone']>;
  address: FormControl<CustomerFormRawValue['address']>;
  active: FormControl<CustomerFormRawValue['active']>;
  createdBy: FormControl<CustomerFormRawValue['createdBy']>;
  createdDate: FormControl<CustomerFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<CustomerFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<CustomerFormRawValue['lastModifiedDate']>;
};

export type CustomerFormGroup = FormGroup<CustomerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerFormService {
  createCustomerFormGroup(customer: CustomerFormGroupInput = { id: null }): CustomerFormGroup {
    const customerRawValue = this.convertCustomerToCustomerRawValue({
      ...this.getFormDefaults(),
      ...customer,
    });
    return new FormGroup<CustomerFormGroupContent>({
      id: new FormControl(
        { value: customerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(customerRawValue.name, {
        validators: [Validators.required],
      }),
      phone: new FormControl(customerRawValue.phone),
      address: new FormControl(customerRawValue.address),
      active: new FormControl(customerRawValue.active),
      createdBy: new FormControl(customerRawValue.createdBy),
      createdDate: new FormControl(customerRawValue.createdDate),
      lastModifiedBy: new FormControl(customerRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(customerRawValue.lastModifiedDate),
    });
  }

  getCustomer(form: CustomerFormGroup): ICustomer | NewCustomer {
    return this.convertCustomerRawValueToCustomer(form.getRawValue() as CustomerFormRawValue | NewCustomerFormRawValue);
  }

  resetForm(form: CustomerFormGroup, customer: CustomerFormGroupInput): void {
    const customerRawValue = this.convertCustomerToCustomerRawValue({ ...this.getFormDefaults(), ...customer });
    form.reset(
      {
        ...customerRawValue,
        id: { value: customerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CustomerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertCustomerRawValueToCustomer(rawCustomer: CustomerFormRawValue | NewCustomerFormRawValue): ICustomer | NewCustomer {
    return {
      ...rawCustomer,
      createdDate: dayjs(rawCustomer.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawCustomer.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertCustomerToCustomerRawValue(
    customer: ICustomer | (Partial<NewCustomer> & CustomerFormDefaults),
  ): CustomerFormRawValue | PartialWithRequiredKeyOf<NewCustomerFormRawValue> {
    return {
      ...customer,
      createdDate: customer.createdDate ? customer.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: customer.lastModifiedDate ? customer.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
