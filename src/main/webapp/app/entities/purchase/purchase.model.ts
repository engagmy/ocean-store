import dayjs from 'dayjs/esm';
import { IPurchaseOperation } from 'app/entities/purchase-operation/purchase-operation.model';
import { IProduct } from 'app/entities/product/product.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IPurchase {
  id: number;
  productName?: string | null;
  quantity?: number | null;
  unitPrice?: number | null;
  lineTotal?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  purchaseOperation?: Pick<IPurchaseOperation, 'id'> | null;
  product?: Pick<IProduct, 'id'> | null;
  supplier?: Pick<ISupplier, 'id'> | null;
}

export type NewPurchase = Omit<IPurchase, 'id'> & { id: null };
