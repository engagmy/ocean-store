import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICashBalance, NewCashBalance } from '../cash-balance.model';

export type PartialUpdateCashBalance = Partial<ICashBalance> & Pick<ICashBalance, 'id'>;

type RestOf<T extends ICashBalance | NewCashBalance> = Omit<T, 'lastUpdated' | 'createdDate' | 'lastModifiedDate'> & {
  lastUpdated?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestCashBalance = RestOf<ICashBalance>;

export type NewRestCashBalance = RestOf<NewCashBalance>;

export type PartialUpdateRestCashBalance = RestOf<PartialUpdateCashBalance>;

export type EntityResponseType = HttpResponse<ICashBalance>;
export type EntityArrayResponseType = HttpResponse<ICashBalance[]>;

@Injectable({ providedIn: 'root' })
export class CashBalanceService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cash-balances');

  create(cashBalance: NewCashBalance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashBalance);
    return this.http
      .post<RestCashBalance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cashBalance: ICashBalance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashBalance);
    return this.http
      .put<RestCashBalance>(`${this.resourceUrl}/${this.getCashBalanceIdentifier(cashBalance)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cashBalance: PartialUpdateCashBalance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cashBalance);
    return this.http
      .patch<RestCashBalance>(`${this.resourceUrl}/${this.getCashBalanceIdentifier(cashBalance)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCashBalance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCashBalance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCashBalanceIdentifier(cashBalance: Pick<ICashBalance, 'id'>): number {
    return cashBalance.id;
  }

  compareCashBalance(o1: Pick<ICashBalance, 'id'> | null, o2: Pick<ICashBalance, 'id'> | null): boolean {
    return o1 && o2 ? this.getCashBalanceIdentifier(o1) === this.getCashBalanceIdentifier(o2) : o1 === o2;
  }

  addCashBalanceToCollectionIfMissing<Type extends Pick<ICashBalance, 'id'>>(
    cashBalanceCollection: Type[],
    ...cashBalancesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const cashBalances: Type[] = cashBalancesToCheck.filter(isPresent);
    if (cashBalances.length > 0) {
      const cashBalanceCollectionIdentifiers = cashBalanceCollection.map(cashBalanceItem => this.getCashBalanceIdentifier(cashBalanceItem));
      const cashBalancesToAdd = cashBalances.filter(cashBalanceItem => {
        const cashBalanceIdentifier = this.getCashBalanceIdentifier(cashBalanceItem);
        if (cashBalanceCollectionIdentifiers.includes(cashBalanceIdentifier)) {
          return false;
        }
        cashBalanceCollectionIdentifiers.push(cashBalanceIdentifier);
        return true;
      });
      return [...cashBalancesToAdd, ...cashBalanceCollection];
    }
    return cashBalanceCollection;
  }

  protected convertDateFromClient<T extends ICashBalance | NewCashBalance | PartialUpdateCashBalance>(cashBalance: T): RestOf<T> {
    return {
      ...cashBalance,
      lastUpdated: cashBalance.lastUpdated?.toJSON() ?? null,
      createdDate: cashBalance.createdDate?.toJSON() ?? null,
      lastModifiedDate: cashBalance.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCashBalance: RestCashBalance): ICashBalance {
    return {
      ...restCashBalance,
      lastUpdated: restCashBalance.lastUpdated ? dayjs(restCashBalance.lastUpdated) : undefined,
      createdDate: restCashBalance.createdDate ? dayjs(restCashBalance.createdDate) : undefined,
      lastModifiedDate: restCashBalance.lastModifiedDate ? dayjs(restCashBalance.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCashBalance>): HttpResponse<ICashBalance> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCashBalance[]>): HttpResponse<ICashBalance[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
