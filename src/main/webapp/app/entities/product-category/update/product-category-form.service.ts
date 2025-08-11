import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProductCategory, NewProductCategory } from '../product-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProductCategory for edit and NewProductCategoryFormGroupInput for create.
 */
type ProductCategoryFormGroupInput = IProductCategory | PartialWithRequiredKeyOf<NewProductCategory>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProductCategory | NewProductCategory> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type ProductCategoryFormRawValue = FormValueOf<IProductCategory>;

type NewProductCategoryFormRawValue = FormValueOf<NewProductCategory>;

type ProductCategoryFormDefaults = Pick<NewProductCategory, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type ProductCategoryFormGroupContent = {
  id: FormControl<ProductCategoryFormRawValue['id'] | NewProductCategory['id']>;
  name: FormControl<ProductCategoryFormRawValue['name']>;
  active: FormControl<ProductCategoryFormRawValue['active']>;
  createdBy: FormControl<ProductCategoryFormRawValue['createdBy']>;
  createdDate: FormControl<ProductCategoryFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<ProductCategoryFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<ProductCategoryFormRawValue['lastModifiedDate']>;
};

export type ProductCategoryFormGroup = FormGroup<ProductCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProductCategoryFormService {
  createProductCategoryFormGroup(productCategory: ProductCategoryFormGroupInput = { id: null }): ProductCategoryFormGroup {
    const productCategoryRawValue = this.convertProductCategoryToProductCategoryRawValue({
      ...this.getFormDefaults(),
      ...productCategory,
    });
    return new FormGroup<ProductCategoryFormGroupContent>({
      id: new FormControl(
        { value: productCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(productCategoryRawValue.name, {
        validators: [Validators.required],
      }),
      active: new FormControl(productCategoryRawValue.active),
      createdBy: new FormControl(productCategoryRawValue.createdBy),
      createdDate: new FormControl(productCategoryRawValue.createdDate),
      lastModifiedBy: new FormControl(productCategoryRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(productCategoryRawValue.lastModifiedDate),
    });
  }

  getProductCategory(form: ProductCategoryFormGroup): IProductCategory | NewProductCategory {
    return this.convertProductCategoryRawValueToProductCategory(
      form.getRawValue() as ProductCategoryFormRawValue | NewProductCategoryFormRawValue,
    );
  }

  resetForm(form: ProductCategoryFormGroup, productCategory: ProductCategoryFormGroupInput): void {
    const productCategoryRawValue = this.convertProductCategoryToProductCategoryRawValue({ ...this.getFormDefaults(), ...productCategory });
    form.reset(
      {
        ...productCategoryRawValue,
        id: { value: productCategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProductCategoryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertProductCategoryRawValueToProductCategory(
    rawProductCategory: ProductCategoryFormRawValue | NewProductCategoryFormRawValue,
  ): IProductCategory | NewProductCategory {
    return {
      ...rawProductCategory,
      createdDate: dayjs(rawProductCategory.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawProductCategory.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertProductCategoryToProductCategoryRawValue(
    productCategory: IProductCategory | (Partial<NewProductCategory> & ProductCategoryFormDefaults),
  ): ProductCategoryFormRawValue | PartialWithRequiredKeyOf<NewProductCategoryFormRawValue> {
    return {
      ...productCategory,
      createdDate: productCategory.createdDate ? productCategory.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: productCategory.lastModifiedDate ? productCategory.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
