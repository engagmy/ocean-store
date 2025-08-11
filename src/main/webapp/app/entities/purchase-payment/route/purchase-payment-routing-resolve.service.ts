import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPurchasePayment } from '../purchase-payment.model';
import { PurchasePaymentService } from '../service/purchase-payment.service';

const purchasePaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | IPurchasePayment> => {
  const id = route.params.id;
  if (id) {
    return inject(PurchasePaymentService)
      .find(id)
      .pipe(
        mergeMap((purchasePayment: HttpResponse<IPurchasePayment>) => {
          if (purchasePayment.body) {
            return of(purchasePayment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default purchasePaymentResolve;
