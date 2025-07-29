import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPurchaseOperation, NewPurchaseOperation } from '../purchase-operation.model';

export type PartialUpdatePurchaseOperation = Partial<IPurchaseOperation> & Pick<IPurchaseOperation, 'id'>;

type RestOf<T extends IPurchaseOperation | NewPurchaseOperation> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestPurchaseOperation = RestOf<IPurchaseOperation>;

export type NewRestPurchaseOperation = RestOf<NewPurchaseOperation>;

export type PartialUpdateRestPurchaseOperation = RestOf<PartialUpdatePurchaseOperation>;

export type EntityResponseType = HttpResponse<IPurchaseOperation>;
export type EntityArrayResponseType = HttpResponse<IPurchaseOperation[]>;

@Injectable({ providedIn: 'root' })
export class PurchaseOperationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/purchase-operations');

  create(purchaseOperation: NewPurchaseOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseOperation);
    return this.http
      .post<RestPurchaseOperation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(purchaseOperation: IPurchaseOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseOperation);
    return this.http
      .put<RestPurchaseOperation>(`${this.resourceUrl}/${this.getPurchaseOperationIdentifier(purchaseOperation)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(purchaseOperation: PartialUpdatePurchaseOperation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchaseOperation);
    return this.http
      .patch<RestPurchaseOperation>(`${this.resourceUrl}/${this.getPurchaseOperationIdentifier(purchaseOperation)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPurchaseOperation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPurchaseOperation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPurchaseOperationIdentifier(purchaseOperation: Pick<IPurchaseOperation, 'id'>): number {
    return purchaseOperation.id;
  }

  comparePurchaseOperation(o1: Pick<IPurchaseOperation, 'id'> | null, o2: Pick<IPurchaseOperation, 'id'> | null): boolean {
    return o1 && o2 ? this.getPurchaseOperationIdentifier(o1) === this.getPurchaseOperationIdentifier(o2) : o1 === o2;
  }

  addPurchaseOperationToCollectionIfMissing<Type extends Pick<IPurchaseOperation, 'id'>>(
    purchaseOperationCollection: Type[],
    ...purchaseOperationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const purchaseOperations: Type[] = purchaseOperationsToCheck.filter(isPresent);
    if (purchaseOperations.length > 0) {
      const purchaseOperationCollectionIdentifiers = purchaseOperationCollection.map(purchaseOperationItem =>
        this.getPurchaseOperationIdentifier(purchaseOperationItem),
      );
      const purchaseOperationsToAdd = purchaseOperations.filter(purchaseOperationItem => {
        const purchaseOperationIdentifier = this.getPurchaseOperationIdentifier(purchaseOperationItem);
        if (purchaseOperationCollectionIdentifiers.includes(purchaseOperationIdentifier)) {
          return false;
        }
        purchaseOperationCollectionIdentifiers.push(purchaseOperationIdentifier);
        return true;
      });
      return [...purchaseOperationsToAdd, ...purchaseOperationCollection];
    }
    return purchaseOperationCollection;
  }

  protected convertDateFromClient<T extends IPurchaseOperation | NewPurchaseOperation | PartialUpdatePurchaseOperation>(
    purchaseOperation: T,
  ): RestOf<T> {
    return {
      ...purchaseOperation,
      date: purchaseOperation.date?.toJSON() ?? null,
      createdDate: purchaseOperation.createdDate?.toJSON() ?? null,
      lastModifiedDate: purchaseOperation.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPurchaseOperation: RestPurchaseOperation): IPurchaseOperation {
    return {
      ...restPurchaseOperation,
      date: restPurchaseOperation.date ? dayjs(restPurchaseOperation.date) : undefined,
      createdDate: restPurchaseOperation.createdDate ? dayjs(restPurchaseOperation.createdDate) : undefined,
      lastModifiedDate: restPurchaseOperation.lastModifiedDate ? dayjs(restPurchaseOperation.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPurchaseOperation>): HttpResponse<IPurchaseOperation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPurchaseOperation[]>): HttpResponse<IPurchaseOperation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
