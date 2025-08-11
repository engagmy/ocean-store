import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInventoryTransaction, NewInventoryTransaction } from '../inventory-transaction.model';

export type PartialUpdateInventoryTransaction = Partial<IInventoryTransaction> & Pick<IInventoryTransaction, 'id'>;

type RestOf<T extends IInventoryTransaction | NewInventoryTransaction> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestInventoryTransaction = RestOf<IInventoryTransaction>;

export type NewRestInventoryTransaction = RestOf<NewInventoryTransaction>;

export type PartialUpdateRestInventoryTransaction = RestOf<PartialUpdateInventoryTransaction>;

export type EntityResponseType = HttpResponse<IInventoryTransaction>;
export type EntityArrayResponseType = HttpResponse<IInventoryTransaction[]>;

@Injectable({ providedIn: 'root' })
export class InventoryTransactionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/inventory-transactions');

  create(inventoryTransaction: NewInventoryTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inventoryTransaction);
    return this.http
      .post<RestInventoryTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(inventoryTransaction: IInventoryTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inventoryTransaction);
    return this.http
      .put<RestInventoryTransaction>(`${this.resourceUrl}/${this.getInventoryTransactionIdentifier(inventoryTransaction)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(inventoryTransaction: PartialUpdateInventoryTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(inventoryTransaction);
    return this.http
      .patch<RestInventoryTransaction>(`${this.resourceUrl}/${this.getInventoryTransactionIdentifier(inventoryTransaction)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInventoryTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInventoryTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInventoryTransactionIdentifier(inventoryTransaction: Pick<IInventoryTransaction, 'id'>): number {
    return inventoryTransaction.id;
  }

  compareInventoryTransaction(o1: Pick<IInventoryTransaction, 'id'> | null, o2: Pick<IInventoryTransaction, 'id'> | null): boolean {
    return o1 && o2 ? this.getInventoryTransactionIdentifier(o1) === this.getInventoryTransactionIdentifier(o2) : o1 === o2;
  }

  addInventoryTransactionToCollectionIfMissing<Type extends Pick<IInventoryTransaction, 'id'>>(
    inventoryTransactionCollection: Type[],
    ...inventoryTransactionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const inventoryTransactions: Type[] = inventoryTransactionsToCheck.filter(isPresent);
    if (inventoryTransactions.length > 0) {
      const inventoryTransactionCollectionIdentifiers = inventoryTransactionCollection.map(inventoryTransactionItem =>
        this.getInventoryTransactionIdentifier(inventoryTransactionItem),
      );
      const inventoryTransactionsToAdd = inventoryTransactions.filter(inventoryTransactionItem => {
        const inventoryTransactionIdentifier = this.getInventoryTransactionIdentifier(inventoryTransactionItem);
        if (inventoryTransactionCollectionIdentifiers.includes(inventoryTransactionIdentifier)) {
          return false;
        }
        inventoryTransactionCollectionIdentifiers.push(inventoryTransactionIdentifier);
        return true;
      });
      return [...inventoryTransactionsToAdd, ...inventoryTransactionCollection];
    }
    return inventoryTransactionCollection;
  }

  protected convertDateFromClient<T extends IInventoryTransaction | NewInventoryTransaction | PartialUpdateInventoryTransaction>(
    inventoryTransaction: T,
  ): RestOf<T> {
    return {
      ...inventoryTransaction,
      date: inventoryTransaction.date?.toJSON() ?? null,
      createdDate: inventoryTransaction.createdDate?.toJSON() ?? null,
      lastModifiedDate: inventoryTransaction.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restInventoryTransaction: RestInventoryTransaction): IInventoryTransaction {
    return {
      ...restInventoryTransaction,
      date: restInventoryTransaction.date ? dayjs(restInventoryTransaction.date) : undefined,
      createdDate: restInventoryTransaction.createdDate ? dayjs(restInventoryTransaction.createdDate) : undefined,
      lastModifiedDate: restInventoryTransaction.lastModifiedDate ? dayjs(restInventoryTransaction.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInventoryTransaction>): HttpResponse<IInventoryTransaction> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInventoryTransaction[]>): HttpResponse<IInventoryTransaction[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
