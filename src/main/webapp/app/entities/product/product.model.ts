import dayjs from 'dayjs/esm';
import { IBrand } from 'app/entities/brand/brand.model';
import { IProductCategory } from 'app/entities/product-category/product-category.model';

export interface IProduct {
  id: number;
  name?: string | null;
  code?: string | null;
  quantity?: number | null;
  unitPrice?: number | null;
  costPrice?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  brand?: Pick<IBrand, 'id'> | null;
  productCategory?: Pick<IProductCategory, 'id'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
