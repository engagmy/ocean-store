import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchaseOperation } from '../purchase-operation.model';
import { PurchaseOperationService } from '../service/purchase-operation.service';

const purchaseOperationResolve = (route: ActivatedRouteSnapshot): Observable<null | IPurchaseOperation> => {
  const id = route.params.id;
  if (id) {
    return inject(PurchaseOperationService)
      .find(id)
      .pipe(
        mergeMap((purchaseOperation: HttpResponse<IPurchaseOperation>) => {
          if (purchaseOperation.body) {
            return of(purchaseOperation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default purchaseOperationResolve;
