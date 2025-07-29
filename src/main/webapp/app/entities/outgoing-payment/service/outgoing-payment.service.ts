import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOutgoingPayment, NewOutgoingPayment } from '../outgoing-payment.model';

export type PartialUpdateOutgoingPayment = Partial<IOutgoingPayment> & Pick<IOutgoingPayment, 'id'>;

type RestOf<T extends IOutgoingPayment | NewOutgoingPayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestOutgoingPayment = RestOf<IOutgoingPayment>;

export type NewRestOutgoingPayment = RestOf<NewOutgoingPayment>;

export type PartialUpdateRestOutgoingPayment = RestOf<PartialUpdateOutgoingPayment>;

export type EntityResponseType = HttpResponse<IOutgoingPayment>;
export type EntityArrayResponseType = HttpResponse<IOutgoingPayment[]>;

@Injectable({ providedIn: 'root' })
export class OutgoingPaymentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/outgoing-payments');

  create(outgoingPayment: NewOutgoingPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(outgoingPayment);
    return this.http
      .post<RestOutgoingPayment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(outgoingPayment: IOutgoingPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(outgoingPayment);
    return this.http
      .put<RestOutgoingPayment>(`${this.resourceUrl}/${this.getOutgoingPaymentIdentifier(outgoingPayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(outgoingPayment: PartialUpdateOutgoingPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(outgoingPayment);
    return this.http
      .patch<RestOutgoingPayment>(`${this.resourceUrl}/${this.getOutgoingPaymentIdentifier(outgoingPayment)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOutgoingPayment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOutgoingPayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOutgoingPaymentIdentifier(outgoingPayment: Pick<IOutgoingPayment, 'id'>): number {
    return outgoingPayment.id;
  }

  compareOutgoingPayment(o1: Pick<IOutgoingPayment, 'id'> | null, o2: Pick<IOutgoingPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getOutgoingPaymentIdentifier(o1) === this.getOutgoingPaymentIdentifier(o2) : o1 === o2;
  }

  addOutgoingPaymentToCollectionIfMissing<Type extends Pick<IOutgoingPayment, 'id'>>(
    outgoingPaymentCollection: Type[],
    ...outgoingPaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const outgoingPayments: Type[] = outgoingPaymentsToCheck.filter(isPresent);
    if (outgoingPayments.length > 0) {
      const outgoingPaymentCollectionIdentifiers = outgoingPaymentCollection.map(outgoingPaymentItem =>
        this.getOutgoingPaymentIdentifier(outgoingPaymentItem),
      );
      const outgoingPaymentsToAdd = outgoingPayments.filter(outgoingPaymentItem => {
        const outgoingPaymentIdentifier = this.getOutgoingPaymentIdentifier(outgoingPaymentItem);
        if (outgoingPaymentCollectionIdentifiers.includes(outgoingPaymentIdentifier)) {
          return false;
        }
        outgoingPaymentCollectionIdentifiers.push(outgoingPaymentIdentifier);
        return true;
      });
      return [...outgoingPaymentsToAdd, ...outgoingPaymentCollection];
    }
    return outgoingPaymentCollection;
  }

  protected convertDateFromClient<T extends IOutgoingPayment | NewOutgoingPayment | PartialUpdateOutgoingPayment>(
    outgoingPayment: T,
  ): RestOf<T> {
    return {
      ...outgoingPayment,
      date: outgoingPayment.date?.toJSON() ?? null,
      createdDate: outgoingPayment.createdDate?.toJSON() ?? null,
      lastModifiedDate: outgoingPayment.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOutgoingPayment: RestOutgoingPayment): IOutgoingPayment {
    return {
      ...restOutgoingPayment,
      date: restOutgoingPayment.date ? dayjs(restOutgoingPayment.date) : undefined,
      createdDate: restOutgoingPayment.createdDate ? dayjs(restOutgoingPayment.createdDate) : undefined,
      lastModifiedDate: restOutgoingPayment.lastModifiedDate ? dayjs(restOutgoingPayment.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOutgoingPayment>): HttpResponse<IOutgoingPayment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOutgoingPayment[]>): HttpResponse<IOutgoingPayment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
