import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDailyCashReconciliation, NewDailyCashReconciliation } from '../daily-cash-reconciliation.model';

export type PartialUpdateDailyCashReconciliation = Partial<IDailyCashReconciliation> & Pick<IDailyCashReconciliation, 'id'>;

type RestOf<T extends IDailyCashReconciliation | NewDailyCashReconciliation> = Omit<T, 'date' | 'createdDate' | 'lastModifiedDate'> & {
  date?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestDailyCashReconciliation = RestOf<IDailyCashReconciliation>;

export type NewRestDailyCashReconciliation = RestOf<NewDailyCashReconciliation>;

export type PartialUpdateRestDailyCashReconciliation = RestOf<PartialUpdateDailyCashReconciliation>;

export type EntityResponseType = HttpResponse<IDailyCashReconciliation>;
export type EntityArrayResponseType = HttpResponse<IDailyCashReconciliation[]>;

@Injectable({ providedIn: 'root' })
export class DailyCashReconciliationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/daily-cash-reconciliations');

  create(dailyCashReconciliation: NewDailyCashReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashReconciliation);
    return this.http
      .post<RestDailyCashReconciliation>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dailyCashReconciliation: IDailyCashReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashReconciliation);
    return this.http
      .put<RestDailyCashReconciliation>(`${this.resourceUrl}/${this.getDailyCashReconciliationIdentifier(dailyCashReconciliation)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dailyCashReconciliation: PartialUpdateDailyCashReconciliation): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashReconciliation);
    return this.http
      .patch<RestDailyCashReconciliation>(
        `${this.resourceUrl}/${this.getDailyCashReconciliationIdentifier(dailyCashReconciliation)}`,
        copy,
        { observe: 'response' },
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDailyCashReconciliation>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDailyCashReconciliation[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDailyCashReconciliationIdentifier(dailyCashReconciliation: Pick<IDailyCashReconciliation, 'id'>): number {
    return dailyCashReconciliation.id;
  }

  compareDailyCashReconciliation(
    o1: Pick<IDailyCashReconciliation, 'id'> | null,
    o2: Pick<IDailyCashReconciliation, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getDailyCashReconciliationIdentifier(o1) === this.getDailyCashReconciliationIdentifier(o2) : o1 === o2;
  }

  addDailyCashReconciliationToCollectionIfMissing<Type extends Pick<IDailyCashReconciliation, 'id'>>(
    dailyCashReconciliationCollection: Type[],
    ...dailyCashReconciliationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dailyCashReconciliations: Type[] = dailyCashReconciliationsToCheck.filter(isPresent);
    if (dailyCashReconciliations.length > 0) {
      const dailyCashReconciliationCollectionIdentifiers = dailyCashReconciliationCollection.map(dailyCashReconciliationItem =>
        this.getDailyCashReconciliationIdentifier(dailyCashReconciliationItem),
      );
      const dailyCashReconciliationsToAdd = dailyCashReconciliations.filter(dailyCashReconciliationItem => {
        const dailyCashReconciliationIdentifier = this.getDailyCashReconciliationIdentifier(dailyCashReconciliationItem);
        if (dailyCashReconciliationCollectionIdentifiers.includes(dailyCashReconciliationIdentifier)) {
          return false;
        }
        dailyCashReconciliationCollectionIdentifiers.push(dailyCashReconciliationIdentifier);
        return true;
      });
      return [...dailyCashReconciliationsToAdd, ...dailyCashReconciliationCollection];
    }
    return dailyCashReconciliationCollection;
  }

  protected convertDateFromClient<T extends IDailyCashReconciliation | NewDailyCashReconciliation | PartialUpdateDailyCashReconciliation>(
    dailyCashReconciliation: T,
  ): RestOf<T> {
    return {
      ...dailyCashReconciliation,
      date: dailyCashReconciliation.date?.format(DATE_FORMAT) ?? null,
      createdDate: dailyCashReconciliation.createdDate?.toJSON() ?? null,
      lastModifiedDate: dailyCashReconciliation.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDailyCashReconciliation: RestDailyCashReconciliation): IDailyCashReconciliation {
    return {
      ...restDailyCashReconciliation,
      date: restDailyCashReconciliation.date ? dayjs(restDailyCashReconciliation.date) : undefined,
      createdDate: restDailyCashReconciliation.createdDate ? dayjs(restDailyCashReconciliation.createdDate) : undefined,
      lastModifiedDate: restDailyCashReconciliation.lastModifiedDate ? dayjs(restDailyCashReconciliation.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDailyCashReconciliation>): HttpResponse<IDailyCashReconciliation> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDailyCashReconciliation[]>): HttpResponse<IDailyCashReconciliation[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
