import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISale, NewSale } from '../sale.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISale for edit and NewSaleFormGroupInput for create.
 */
type SaleFormGroupInput = ISale | PartialWithRequiredKeyOf<NewSale>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISale | NewSale> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type SaleFormRawValue = FormValueOf<ISale>;

type NewSaleFormRawValue = FormValueOf<NewSale>;

type SaleFormDefaults = Pick<NewSale, 'id' | 'active' | 'createdDate' | 'lastModifiedDate'>;

type SaleFormGroupContent = {
  id: FormControl<SaleFormRawValue['id'] | NewSale['id']>;
  productName: FormControl<SaleFormRawValue['productName']>;
  quantity: FormControl<SaleFormRawValue['quantity']>;
  unitPrice: FormControl<SaleFormRawValue['unitPrice']>;
  discount: FormControl<SaleFormRawValue['discount']>;
  lineTotal: FormControl<SaleFormRawValue['lineTotal']>;
  active: FormControl<SaleFormRawValue['active']>;
  createdBy: FormControl<SaleFormRawValue['createdBy']>;
  createdDate: FormControl<SaleFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<SaleFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<SaleFormRawValue['lastModifiedDate']>;
  saleOperation: FormControl<SaleFormRawValue['saleOperation']>;
  product: FormControl<SaleFormRawValue['product']>;
};

export type SaleFormGroup = FormGroup<SaleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SaleFormService {
  createSaleFormGroup(sale: SaleFormGroupInput = { id: null }): SaleFormGroup {
    const saleRawValue = this.convertSaleToSaleRawValue({
      ...this.getFormDefaults(),
      ...sale,
    });
    return new FormGroup<SaleFormGroupContent>({
      id: new FormControl(
        { value: saleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      productName: new FormControl(saleRawValue.productName),
      quantity: new FormControl(saleRawValue.quantity, {
        validators: [Validators.required],
      }),
      unitPrice: new FormControl(saleRawValue.unitPrice, {
        validators: [Validators.required],
      }),
      discount: new FormControl(saleRawValue.discount),
      lineTotal: new FormControl(saleRawValue.lineTotal, {
        validators: [Validators.required],
      }),
      active: new FormControl(saleRawValue.active),
      createdBy: new FormControl(saleRawValue.createdBy),
      createdDate: new FormControl(saleRawValue.createdDate),
      lastModifiedBy: new FormControl(saleRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(saleRawValue.lastModifiedDate),
      saleOperation: new FormControl(saleRawValue.saleOperation),
      product: new FormControl(saleRawValue.product),
    });
  }

  getSale(form: SaleFormGroup): ISale | NewSale {
    return this.convertSaleRawValueToSale(form.getRawValue() as SaleFormRawValue | NewSaleFormRawValue);
  }

  resetForm(form: SaleFormGroup, sale: SaleFormGroupInput): void {
    const saleRawValue = this.convertSaleToSaleRawValue({ ...this.getFormDefaults(), ...sale });
    form.reset(
      {
        ...saleRawValue,
        id: { value: saleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SaleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      active: false,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertSaleRawValueToSale(rawSale: SaleFormRawValue | NewSaleFormRawValue): ISale | NewSale {
    return {
      ...rawSale,
      createdDate: dayjs(rawSale.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawSale.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertSaleToSaleRawValue(
    sale: ISale | (Partial<NewSale> & SaleFormDefaults),
  ): SaleFormRawValue | PartialWithRequiredKeyOf<NewSaleFormRawValue> {
    return {
      ...sale,
      createdDate: sale.createdDate ? sale.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: sale.lastModifiedDate ? sale.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
