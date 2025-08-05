import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBrand, NewBrand } from '../brand.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBrand for edit and NewBrandFormGroupInput for create.
 */
type BrandFormGroupInput = IBrand | PartialWithRequiredKeyOf<NewBrand>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBrand | NewBrand> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type BrandFormRawValue = FormValueOf<IBrand>;

type NewBrandFormRawValue = FormValueOf<NewBrand>;

type BrandFormDefaults = Pick<NewBrand, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type BrandFormGroupContent = {
  id: FormControl<BrandFormRawValue['id'] | NewBrand['id']>;
  name: FormControl<BrandFormRawValue['name']>;
  active: FormControl<BrandFormRawValue['active']>;
  createdBy: FormControl<BrandFormRawValue['createdBy']>;
  createdDate: FormControl<BrandFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<BrandFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<BrandFormRawValue['lastModifiedDate']>;
};

export type BrandFormGroup = FormGroup<BrandFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BrandFormService {
  createBrandFormGroup(brand: BrandFormGroupInput = { id: null }): BrandFormGroup {
    const brandRawValue = this.convertBrandToBrandRawValue({
      ...this.getFormDefaults(),
      ...brand,
    });
    return new FormGroup<BrandFormGroupContent>({
      id: new FormControl(
        { value: brandRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(brandRawValue.name, {
        validators: [Validators.required],
      }),
      active: new FormControl(brandRawValue.active),
      createdBy: new FormControl(brandRawValue.createdBy),
      createdDate: new FormControl(brandRawValue.createdDate),
      lastModifiedBy: new FormControl(brandRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(brandRawValue.lastModifiedDate),
    });
  }

  getBrand(form: BrandFormGroup): IBrand | NewBrand {
    return this.convertBrandRawValueToBrand(form.getRawValue() as BrandFormRawValue | NewBrandFormRawValue);
  }

  resetForm(form: BrandFormGroup, brand: BrandFormGroupInput): void {
    const brandRawValue = this.convertBrandToBrandRawValue({ ...this.getFormDefaults(), ...brand });
    form.reset(
      {
        ...brandRawValue,
        id: { value: brandRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BrandFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertBrandRawValueToBrand(rawBrand: BrandFormRawValue | NewBrandFormRawValue): IBrand | NewBrand {
    return {
      ...rawBrand,
      createdDate: dayjs(rawBrand.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawBrand.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertBrandToBrandRawValue(
    brand: IBrand | (Partial<NewBrand> & BrandFormDefaults),
  ): BrandFormRawValue | PartialWithRequiredKeyOf<NewBrandFormRawValue> {
    return {
      ...brand,
      createdDate: brand.createdDate ? brand.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: brand.lastModifiedDate ? brand.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
