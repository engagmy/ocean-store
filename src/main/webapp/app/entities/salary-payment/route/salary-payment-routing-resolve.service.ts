import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISalaryPayment } from '../salary-payment.model';
import { SalaryPaymentService } from '../service/salary-payment.service';

const salaryPaymentResolve = (route: ActivatedRouteSnapshot): Observable<null | ISalaryPayment> => {
  const id = route.params.id;
  if (id) {
    return inject(SalaryPaymentService)
      .find(id)
      .pipe(
        mergeMap((salaryPayment: HttpResponse<ISalaryPayment>) => {
          if (salaryPayment.body) {
            return of(salaryPayment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default salaryPaymentResolve;
