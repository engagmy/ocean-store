import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProductCategory, NewProductCategory } from '../product-category.model';

export type PartialUpdateProductCategory = Partial<IProductCategory> & Pick<IProductCategory, 'id'>;

type RestOf<T extends IProductCategory | NewProductCategory> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestProductCategory = RestOf<IProductCategory>;

export type NewRestProductCategory = RestOf<NewProductCategory>;

export type PartialUpdateRestProductCategory = RestOf<PartialUpdateProductCategory>;

export type EntityResponseType = HttpResponse<IProductCategory>;
export type EntityArrayResponseType = HttpResponse<IProductCategory[]>;

@Injectable({ providedIn: 'root' })
export class ProductCategoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/product-categories');

  create(productCategory: NewProductCategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(productCategory);
    return this.http
      .post<RestProductCategory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(productCategory: IProductCategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(productCategory);
    return this.http
      .put<RestProductCategory>(`${this.resourceUrl}/${this.getProductCategoryIdentifier(productCategory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(productCategory: PartialUpdateProductCategory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(productCategory);
    return this.http
      .patch<RestProductCategory>(`${this.resourceUrl}/${this.getProductCategoryIdentifier(productCategory)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProductCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProductCategory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProductCategoryIdentifier(productCategory: Pick<IProductCategory, 'id'>): number {
    return productCategory.id;
  }

  compareProductCategory(o1: Pick<IProductCategory, 'id'> | null, o2: Pick<IProductCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getProductCategoryIdentifier(o1) === this.getProductCategoryIdentifier(o2) : o1 === o2;
  }

  addProductCategoryToCollectionIfMissing<Type extends Pick<IProductCategory, 'id'>>(
    productCategoryCollection: Type[],
    ...productCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const productCategories: Type[] = productCategoriesToCheck.filter(isPresent);
    if (productCategories.length > 0) {
      const productCategoryCollectionIdentifiers = productCategoryCollection.map(productCategoryItem =>
        this.getProductCategoryIdentifier(productCategoryItem),
      );
      const productCategoriesToAdd = productCategories.filter(productCategoryItem => {
        const productCategoryIdentifier = this.getProductCategoryIdentifier(productCategoryItem);
        if (productCategoryCollectionIdentifiers.includes(productCategoryIdentifier)) {
          return false;
        }
        productCategoryCollectionIdentifiers.push(productCategoryIdentifier);
        return true;
      });
      return [...productCategoriesToAdd, ...productCategoryCollection];
    }
    return productCategoryCollection;
  }

  protected convertDateFromClient<T extends IProductCategory | NewProductCategory | PartialUpdateProductCategory>(
    productCategory: T,
  ): RestOf<T> {
    return {
      ...productCategory,
      createdDate: productCategory.createdDate?.toJSON() ?? null,
      lastModifiedDate: productCategory.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProductCategory: RestProductCategory): IProductCategory {
    return {
      ...restProductCategory,
      createdDate: restProductCategory.createdDate ? dayjs(restProductCategory.createdDate) : undefined,
      lastModifiedDate: restProductCategory.lastModifiedDate ? dayjs(restProductCategory.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProductCategory>): HttpResponse<IProductCategory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProductCategory[]>): HttpResponse<IProductCategory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
