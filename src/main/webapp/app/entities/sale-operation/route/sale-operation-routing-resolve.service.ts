import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISaleOperation } from '../sale-operation.model';
import { SaleOperationService } from '../service/sale-operation.service';

const saleOperationResolve = (route: ActivatedRouteSnapshot): Observable<null | ISaleOperation> => {
  const id = route.params.id;
  if (id) {
    return inject(SaleOperationService)
      .find(id)
      .pipe(
        mergeMap((saleOperation: HttpResponse<ISaleOperation>) => {
          if (saleOperation.body) {
            return of(saleOperation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default saleOperationResolve;
