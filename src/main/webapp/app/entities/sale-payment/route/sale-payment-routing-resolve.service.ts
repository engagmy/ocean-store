import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISalePayment } from '../sale-payment.model';
import { SalePaymentService } from '../service/sale-payment.service';

const salePaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | ISalePayment> => {
  const id = route.params.id;
  if (id) {
    return inject(SalePaymentService)
      .find(id)
      .pipe(
        mergeMap((salePayment: HttpResponse<ISalePayment>) => {
          if (salePayment.body) {
            return of(salePayment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default salePaymentResolve;
