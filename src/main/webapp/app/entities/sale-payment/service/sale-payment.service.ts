import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISalePayment, NewSalePayment } from '../sale-payment.model';

export type PartialUpdateSalePayment = Partial<ISalePayment> & Pick<ISalePayment, 'id'>;

type RestOf<T extends ISalePayment | NewSalePayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestSalePayment = RestOf<ISalePayment>;

export type NewRestSalePayment = RestOf<NewSalePayment>;

export type PartialUpdateRestSalePayment = RestOf<PartialUpdateSalePayment>;

export type EntityResponseType = HttpResponse<ISalePayment>;
export type EntityArrayResponseType = HttpResponse<ISalePayment[]>;

@Injectable({ providedIn: 'root' })
export class SalePaymentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sale-payments');

  create(salePayment: NewSalePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salePayment);
    return this.http
      .post<RestSalePayment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(salePayment: ISalePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salePayment);
    return this.http
      .put<RestSalePayment>(`${this.resourceUrl}/${this.getSalePaymentIdentifier(salePayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(salePayment: PartialUpdateSalePayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salePayment);
    return this.http
      .patch<RestSalePayment>(`${this.resourceUrl}/${this.getSalePaymentIdentifier(salePayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSalePayment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSalePayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSalePaymentIdentifier(salePayment: Pick<ISalePayment, 'id'>): number {
    return salePayment.id;
  }

  compareSalePayment(o1: Pick<ISalePayment, 'id'> | null, o2: Pick<ISalePayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getSalePaymentIdentifier(o1) === this.getSalePaymentIdentifier(o2) : o1 === o2;
  }

  addSalePaymentToCollectionIfMissing<Type extends Pick<ISalePayment, 'id'>>(
    salePaymentCollection: Type[],
    ...salePaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const salePayments: Type[] = salePaymentsToCheck.filter(isPresent);
    if (salePayments.length > 0) {
      const salePaymentCollectionIdentifiers = salePaymentCollection.map(salePaymentItem => this.getSalePaymentIdentifier(salePaymentItem));
      const salePaymentsToAdd = salePayments.filter(salePaymentItem => {
        const salePaymentIdentifier = this.getSalePaymentIdentifier(salePaymentItem);
        if (salePaymentCollectionIdentifiers.includes(salePaymentIdentifier)) {
          return false;
        }
        salePaymentCollectionIdentifiers.push(salePaymentIdentifier);
        return true;
      });
      return [...salePaymentsToAdd, ...salePaymentCollection];
    }
    return salePaymentCollection;
  }

  protected convertDateFromClient<T extends ISalePayment | NewSalePayment | PartialUpdateSalePayment>(salePayment: T): RestOf<T> {
    return {
      ...salePayment,
      date: salePayment.date?.toJSON() ?? null,
      createdDate: salePayment.createdDate?.toJSON() ?? null,
      lastModifiedDate: salePayment.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSalePayment: RestSalePayment): ISalePayment {
    return {
      ...restSalePayment,
      date: restSalePayment.date ? dayjs(restSalePayment.date) : undefined,
      createdDate: restSalePayment.createdDate ? dayjs(restSalePayment.createdDate) : undefined,
      lastModifiedDate: restSalePayment.lastModifiedDate ? dayjs(restSalePayment.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSalePayment>): HttpResponse<ISalePayment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSalePayment[]>): HttpResponse<ISalePayment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
