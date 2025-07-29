import dayjs from 'dayjs/esm';
import { IProduct } from 'app/entities/product/product.model';
import { InventoryActionType } from 'app/entities/enumerations/inventory-action-type.model';

export interface IInventoryTransaction {
  id: number;
  date?: dayjs.Dayjs | null;
  type?: keyof typeof InventoryActionType | null;
  quantity?: number | null;
  active?: boolean | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  product?: Pick<IProduct, 'id'> | null;
}

export type NewInventoryTransaction = Omit<IInventoryTransaction, 'id'> & { id: null };
