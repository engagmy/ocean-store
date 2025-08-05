import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISaleOperation, NewSaleOperation } from '../sale-operation.model';

export type PartialUpdateSaleOperation = Partial<ISaleOperation> & Pick<ISaleOperation, 'id'>;

type RestOf<T extends ISaleOperation | NewSaleOperation> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestSaleOperation = RestOf<ISaleOperation>;

export type NewRestSaleOperation = RestOf<NewSaleOperation>;

export type PartialUpdateRestSaleOperation = RestOf<PartialUpdateSaleOperation>;

export type EntityResponseType = HttpResponse<ISaleOperation>;
export type EntityArrayResponseType = HttpResponse<ISaleOperation[]>;

@Injectable({ providedIn: 'root' })
export class SaleOperationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sale-operations');

  create(saleOperation: NewSaleOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(saleOperation);
    return this.http
      .post<RestSaleOperation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(saleOperation: ISaleOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(saleOperation);
    return this.http
      .put<RestSaleOperation>(`${this.resourceUrl}/${this.getSaleOperationIdentifier(saleOperation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(saleOperation: PartialUpdateSaleOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(saleOperation);
    return this.http
      .patch<RestSaleOperation>(`${this.resourceUrl}/${this.getSaleOperationIdentifier(saleOperation)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSaleOperation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSaleOperation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSaleOperationIdentifier(saleOperation: Pick<ISaleOperation, 'id'>): number {
    return saleOperation.id;
  }

  compareSaleOperation(o1: Pick<ISaleOperation, 'id'> | null, o2: Pick<ISaleOperation, 'id'> | null): boolean {
    return o1 && o2 ? this.getSaleOperationIdentifier(o1) === this.getSaleOperationIdentifier(o2) : o1 === o2;
  }

  addSaleOperationToCollectionIfMissing<Type extends Pick<ISaleOperation, 'id'>>(
    saleOperationCollection: Type[],
    ...saleOperationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const saleOperations: Type[] = saleOperationsToCheck.filter(isPresent);
    if (saleOperations.length > 0) {
      const saleOperationCollectionIdentifiers = saleOperationCollection.map(saleOperationItem =>
        this.getSaleOperationIdentifier(saleOperationItem),
      );
      const saleOperationsToAdd = saleOperations.filter(saleOperationItem => {
        const saleOperationIdentifier = this.getSaleOperationIdentifier(saleOperationItem);
        if (saleOperationCollectionIdentifiers.includes(saleOperationIdentifier)) {
          return false;
        }
        saleOperationCollectionIdentifiers.push(saleOperationIdentifier);
        return true;
      });
      return [...saleOperationsToAdd, ...saleOperationCollection];
    }
    return saleOperationCollection;
  }

  protected convertDateFromClient<T extends ISaleOperation | NewSaleOperation | PartialUpdateSaleOperation>(saleOperation: T): RestOf<T> {
    return {
      ...saleOperation,
      date: saleOperation.date?.toJSON() ?? null,
      createdDate: saleOperation.createdDate?.toJSON() ?? null,
      lastModifiedDate: saleOperation.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSaleOperation: RestSaleOperation): ISaleOperation {
    return {
      ...restSaleOperation,
      date: restSaleOperation.date ? dayjs(restSaleOperation.date) : undefined,
      createdDate: restSaleOperation.createdDate ? dayjs(restSaleOperation.createdDate) : undefined,
      lastModifiedDate: restSaleOperation.lastModifiedDate ? dayjs(restSaleOperation.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSaleOperation>): HttpResponse<ISaleOperation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSaleOperation[]>): HttpResponse<ISaleOperation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
