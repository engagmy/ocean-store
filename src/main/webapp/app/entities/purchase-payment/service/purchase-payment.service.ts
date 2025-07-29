import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPurchasePayment, NewPurchasePayment } from '../purchase-payment.model';

export type PartialUpdatePurchasePayment = Partial<IPurchasePayment> & Pick<IPurchasePayment, 'id'>;

type RestOf<T extends IPurchasePayment | NewPurchasePayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestPurchasePayment = RestOf<IPurchasePayment>;

export type NewRestPurchasePayment = RestOf<NewPurchasePayment>;

export type PartialUpdateRestPurchasePayment = RestOf<PartialUpdatePurchasePayment>;

export type EntityResponseType = HttpResponse<IPurchasePayment>;
export type EntityArrayResponseType = HttpResponse<IPurchasePayment[]>;

@Injectable({ providedIn: 'root' })
export class PurchasePaymentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/purchase-payments');

  create(purchasePayment: NewPurchasePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchasePayment);
    return this.http
      .post<RestPurchasePayment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(purchasePayment: IPurchasePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchasePayment);
    return this.http
      .put<RestPurchasePayment>(`${this.resourceUrl}/${this.getPurchasePaymentIdentifier(purchasePayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(purchasePayment: PartialUpdatePurchasePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(purchasePayment);
    return this.http
      .patch<RestPurchasePayment>(`${this.resourceUrl}/${this.getPurchasePaymentIdentifier(purchasePayment)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPurchasePayment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPurchasePayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPurchasePaymentIdentifier(purchasePayment: Pick<IPurchasePayment, 'id'>): number {
    return purchasePayment.id;
  }

  comparePurchasePayment(o1: Pick<IPurchasePayment, 'id'> | null, o2: Pick<IPurchasePayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getPurchasePaymentIdentifier(o1) === this.getPurchasePaymentIdentifier(o2) : o1 === o2;
  }

  addPurchasePaymentToCollectionIfMissing<Type extends Pick<IPurchasePayment, 'id'>>(
    purchasePaymentCollection: Type[],
    ...purchasePaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const purchasePayments: Type[] = purchasePaymentsToCheck.filter(isPresent);
    if (purchasePayments.length > 0) {
      const purchasePaymentCollectionIdentifiers = purchasePaymentCollection.map(purchasePaymentItem =>
        this.getPurchasePaymentIdentifier(purchasePaymentItem),
      );
      const purchasePaymentsToAdd = purchasePayments.filter(purchasePaymentItem => {
        const purchasePaymentIdentifier = this.getPurchasePaymentIdentifier(purchasePaymentItem);
        if (purchasePaymentCollectionIdentifiers.includes(purchasePaymentIdentifier)) {
          return false;
        }
        purchasePaymentCollectionIdentifiers.push(purchasePaymentIdentifier);
        return true;
      });
      return [...purchasePaymentsToAdd, ...purchasePaymentCollection];
    }
    return purchasePaymentCollection;
  }

  protected convertDateFromClient<T extends IPurchasePayment | NewPurchasePayment | PartialUpdatePurchasePayment>(
    purchasePayment: T,
  ): RestOf<T> {
    return {
      ...purchasePayment,
      date: purchasePayment.date?.toJSON() ?? null,
      createdDate: purchasePayment.createdDate?.toJSON() ?? null,
      lastModifiedDate: purchasePayment.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restPurchasePayment: RestPurchasePayment): IPurchasePayment {
    return {
      ...restPurchasePayment,
      date: restPurchasePayment.date ? dayjs(restPurchasePayment.date) : undefined,
      createdDate: restPurchasePayment.createdDate ? dayjs(restPurchasePayment.createdDate) : undefined,
      lastModifiedDate: restPurchasePayment.lastModifiedDate ? dayjs(restPurchasePayment.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPurchasePayment>): HttpResponse<IPurchasePayment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPurchasePayment[]>): HttpResponse<IPurchasePayment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
