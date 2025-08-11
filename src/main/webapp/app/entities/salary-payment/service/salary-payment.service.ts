import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISalaryPayment, NewSalaryPayment } from '../salary-payment.model';

export type PartialUpdateSalaryPayment = Partial<ISalaryPayment> & Pick<ISalaryPayment, 'id'>;

type RestOf<T extends ISalaryPayment | NewSalaryPayment> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestSalaryPayment = RestOf<ISalaryPayment>;

export type NewRestSalaryPayment = RestOf<NewSalaryPayment>;

export type PartialUpdateRestSalaryPayment = RestOf<PartialUpdateSalaryPayment>;

export type EntityResponseType = HttpResponse<ISalaryPayment>;
export type EntityArrayResponseType = HttpResponse<ISalaryPayment[]>;

@Injectable({ providedIn: 'root' })
export class SalaryPaymentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/salary-payments');

  create(salaryPayment: NewSalaryPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salaryPayment);
    return this.http
      .post<RestSalaryPayment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(salaryPayment: ISalaryPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salaryPayment);
    return this.http
      .put<RestSalaryPayment>(`${this.resourceUrl}/${this.getSalaryPaymentIdentifier(salaryPayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(salaryPayment: PartialUpdateSalaryPayment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(salaryPayment);
    return this.http
      .patch<RestSalaryPayment>(`${this.resourceUrl}/${this.getSalaryPaymentIdentifier(salaryPayment)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSalaryPayment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSalaryPayment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSalaryPaymentIdentifier(salaryPayment: Pick<ISalaryPayment, 'id'>): number {
    return salaryPayment.id;
  }

  compareSalaryPayment(o1: Pick<ISalaryPayment, 'id'> | null, o2: Pick<ISalaryPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getSalaryPaymentIdentifier(o1) === this.getSalaryPaymentIdentifier(o2) : o1 === o2;
  }

  addSalaryPaymentToCollectionIfMissing<Type extends Pick<ISalaryPayment, 'id'>>(
    salaryPaymentCollection: Type[],
    ...salaryPaymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const salaryPayments: Type[] = salaryPaymentsToCheck.filter(isPresent);
    if (salaryPayments.length > 0) {
      const salaryPaymentCollectionIdentifiers = salaryPaymentCollection.map(salaryPaymentItem =>
        this.getSalaryPaymentIdentifier(salaryPaymentItem),
      );
      const salaryPaymentsToAdd = salaryPayments.filter(salaryPaymentItem => {
        const salaryPaymentIdentifier = this.getSalaryPaymentIdentifier(salaryPaymentItem);
        if (salaryPaymentCollectionIdentifiers.includes(salaryPaymentIdentifier)) {
          return false;
        }
        salaryPaymentCollectionIdentifiers.push(salaryPaymentIdentifier);
        return true;
      });
      return [...salaryPaymentsToAdd, ...salaryPaymentCollection];
    }
    return salaryPaymentCollection;
  }

  protected convertDateFromClient<T extends ISalaryPayment | NewSalaryPayment | PartialUpdateSalaryPayment>(salaryPayment: T): RestOf<T> {
    return {
      ...salaryPayment,
      date: salaryPayment.date?.toJSON() ?? null,
      createdDate: salaryPayment.createdDate?.toJSON() ?? null,
      lastModifiedDate: salaryPayment.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSalaryPayment: RestSalaryPayment): ISalaryPayment {
    return {
      ...restSalaryPayment,
      date: restSalaryPayment.date ? dayjs(restSalaryPayment.date) : undefined,
      createdDate: restSalaryPayment.createdDate ? dayjs(restSalaryPayment.createdDate) : undefined,
      lastModifiedDate: restSalaryPayment.lastModifiedDate ? dayjs(restSalaryPayment.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSalaryPayment>): HttpResponse<ISalaryPayment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSalaryPayment[]>): HttpResponse<ISalaryPayment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
