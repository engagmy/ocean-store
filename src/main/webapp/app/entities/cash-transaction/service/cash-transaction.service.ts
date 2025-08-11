import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICashTransaction, NewCashTransaction } from '../cash-transaction.model';

export type PartialUpdateCashTransaction = Partial<ICashTransaction> & Pick<ICashTransaction, 'id'>;

type RestOf<T extends ICashTransaction | NewCashTransaction> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestCashTransaction = RestOf<ICashTransaction>;

export type NewRestCashTransaction = RestOf<NewCashTransaction>;

export type PartialUpdateRestCashTransaction = RestOf<PartialUpdateCashTransaction>;

export type EntityResponseType = HttpResponse<ICashTransaction>;
export type EntityArrayResponseType = HttpResponse<ICashTransaction[]>;

@Injectable({ providedIn: 'root' })
export class CashTransactionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cash-transactions');

  create(cashTransaction: NewCashTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashTransaction);
    return this.http
      .post<RestCashTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cashTransaction: ICashTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashTransaction);
    return this.http
      .put<RestCashTransaction>(`${this.resourceUrl}/${this.getCashTransactionIdentifier(cashTransaction)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cashTransaction: PartialUpdateCashTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashTransaction);
    return this.http
      .patch<RestCashTransaction>(`${this.resourceUrl}/${this.getCashTransactionIdentifier(cashTransaction)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCashTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCashTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCashTransactionIdentifier(cashTransaction: Pick<ICashTransaction, 'id'>): number {
    return cashTransaction.id;
  }

  compareCashTransaction(o1: Pick<ICashTransaction, 'id'> | null, o2: Pick<ICashTransaction, 'id'> | null): boolean {
    return o1 && o2 ? this.getCashTransactionIdentifier(o1) === this.getCashTransactionIdentifier(o2) : o1 === o2;
  }

  addCashTransactionToCollectionIfMissing<Type extends Pick<ICashTransaction, 'id'>>(
    cashTransactionCollection: Type[],
    ...cashTransactionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cashTransactions: Type[] = cashTransactionsToCheck.filter(isPresent);
    if (cashTransactions.length > 0) {
      const cashTransactionCollectionIdentifiers = cashTransactionCollection.map(cashTransactionItem =>
        this.getCashTransactionIdentifier(cashTransactionItem),
      );
      const cashTransactionsToAdd = cashTransactions.filter(cashTransactionItem => {
        const cashTransactionIdentifier = this.getCashTransactionIdentifier(cashTransactionItem);
        if (cashTransactionCollectionIdentifiers.includes(cashTransactionIdentifier)) {
          return false;
        }
        cashTransactionCollectionIdentifiers.push(cashTransactionIdentifier);
        return true;
      });
      return [...cashTransactionsToAdd, ...cashTransactionCollection];
    }
    return cashTransactionCollection;
  }

  protected convertDateFromClient<T extends ICashTransaction | NewCashTransaction | PartialUpdateCashTransaction>(
    cashTransaction: T,
  ): RestOf<T> {
    return {
      ...cashTransaction,
      date: cashTransaction.date?.toJSON() ?? null,
      createdDate: cashTransaction.createdDate?.toJSON() ?? null,
      lastModifiedDate: cashTransaction.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCashTransaction: RestCashTransaction): ICashTransaction {
    return {
      ...restCashTransaction,
      date: restCashTransaction.date ? dayjs(restCashTransaction.date) : undefined,
      createdDate: restCashTransaction.createdDate ? dayjs(restCashTransaction.createdDate) : undefined,
      lastModifiedDate: restCashTransaction.lastModifiedDate ? dayjs(restCashTransaction.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCashTransaction>): HttpResponse<ICashTransaction> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCashTransaction[]>): HttpResponse<ICashTransaction[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
