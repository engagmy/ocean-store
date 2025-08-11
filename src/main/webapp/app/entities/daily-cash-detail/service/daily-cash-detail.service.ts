import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDailyCashDetail, NewDailyCashDetail } from '../daily-cash-detail.model';

export type PartialUpdateDailyCashDetail = Partial<IDailyCashDetail> & Pick<IDailyCashDetail, 'id'>;

type RestOf<T extends IDailyCashDetail | NewDailyCashDetail> = Omit<T, 'timestamp' | 'createdDate' | 'lastModifiedDate'> & {
  timestamp?: string | null;
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestDailyCashDetail = RestOf<IDailyCashDetail>;

export type NewRestDailyCashDetail = RestOf<NewDailyCashDetail>;

export type PartialUpdateRestDailyCashDetail = RestOf<PartialUpdateDailyCashDetail>;

export type EntityResponseType = HttpResponse<IDailyCashDetail>;
export type EntityArrayResponseType = HttpResponse<IDailyCashDetail[]>;

@Injectable({ providedIn: 'root' })
export class DailyCashDetailService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/daily-cash-details');

  create(dailyCashDetail: NewDailyCashDetail): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashDetail);
    return this.http
      .post<RestDailyCashDetail>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(dailyCashDetail: IDailyCashDetail): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashDetail);
    return this.http
      .put<RestDailyCashDetail>(`${this.resourceUrl}/${this.getDailyCashDetailIdentifier(dailyCashDetail)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(dailyCashDetail: PartialUpdateDailyCashDetail): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(dailyCashDetail);
    return this.http
      .patch<RestDailyCashDetail>(`${this.resourceUrl}/${this.getDailyCashDetailIdentifier(dailyCashDetail)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDailyCashDetail>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDailyCashDetail[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDailyCashDetailIdentifier(dailyCashDetail: Pick<IDailyCashDetail, 'id'>): number {
    return dailyCashDetail.id;
  }

  compareDailyCashDetail(o1: Pick<IDailyCashDetail, 'id'> | null, o2: Pick<IDailyCashDetail, 'id'> | null): boolean {
    return o1 && o2 ? this.getDailyCashDetailIdentifier(o1) === this.getDailyCashDetailIdentifier(o2) : o1 === o2;
  }

  addDailyCashDetailToCollectionIfMissing<Type extends Pick<IDailyCashDetail, 'id'>>(
    dailyCashDetailCollection: Type[],
    ...dailyCashDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dailyCashDetails: Type[] = dailyCashDetailsToCheck.filter(isPresent);
    if (dailyCashDetails.length > 0) {
      const dailyCashDetailCollectionIdentifiers = dailyCashDetailCollection.map(dailyCashDetailItem =>
        this.getDailyCashDetailIdentifier(dailyCashDetailItem),
      );
      const dailyCashDetailsToAdd = dailyCashDetails.filter(dailyCashDetailItem => {
        const dailyCashDetailIdentifier = this.getDailyCashDetailIdentifier(dailyCashDetailItem);
        if (dailyCashDetailCollectionIdentifiers.includes(dailyCashDetailIdentifier)) {
          return false;
        }
        dailyCashDetailCollectionIdentifiers.push(dailyCashDetailIdentifier);
        return true;
      });
      return [...dailyCashDetailsToAdd, ...dailyCashDetailCollection];
    }
    return dailyCashDetailCollection;
  }

  protected convertDateFromClient<T extends IDailyCashDetail | NewDailyCashDetail | PartialUpdateDailyCashDetail>(
    dailyCashDetail: T,
  ): RestOf<T> {
    return {
      ...dailyCashDetail,
      timestamp: dailyCashDetail.timestamp?.toJSON() ?? null,
      createdDate: dailyCashDetail.createdDate?.toJSON() ?? null,
      lastModifiedDate: dailyCashDetail.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDailyCashDetail: RestDailyCashDetail): IDailyCashDetail {
    return {
      ...restDailyCashDetail,
      timestamp: restDailyCashDetail.timestamp ? dayjs(restDailyCashDetail.timestamp) : undefined,
      createdDate: restDailyCashDetail.createdDate ? dayjs(restDailyCashDetail.createdDate) : undefined,
      lastModifiedDate: restDailyCashDetail.lastModifiedDate ? dayjs(restDailyCashDetail.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDailyCashDetail>): HttpResponse<IDailyCashDetail> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDailyCashDetail[]>): HttpResponse<IDailyCashDetail[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
