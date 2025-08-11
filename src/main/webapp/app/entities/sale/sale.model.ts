import dayjs from 'dayjs/esm';
import { ISaleOperation } from 'app/entities/sale-operation/sale-operation.model';
import { IProduct } from 'app/entities/product/product.model';

export interface ISale {
  id: number;
  productName?: string | null;
  quantity?: number | null;
  unitPrice?: number | null;
  discount?: number | null;
  lineTotal?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  saleOperation?: Pick<ISaleOperation, 'id'> | null;
  product?: Pick<IProduct, 'id'> | null;
}

export type NewSale = Omit<ISale, 'id'> & { id: null };
