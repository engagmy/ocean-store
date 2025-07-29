import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOutgoingPayment } from '../outgoing-payment.model';
import { OutgoingPaymentService } from '../service/outgoing-payment.service';

const outgoingPaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | IOutgoingPayment> => {
  const id = route.params.id;
  if (id) {
    return inject(OutgoingPaymentService)
      .find(id)
      .pipe(
        mergeMap((outgoingPayment: HttpResponse<IOutgoingPayment>) => {
          if (outgoingPayment.body) {
            return of(outgoingPayment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default outgoingPaymentResolve;
