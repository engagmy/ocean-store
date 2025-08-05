import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICashBalance } from '../cash-balance.model';
import { CashBalanceService } from '../service/cash-balance.service';

const cashBalanceResolve = (route: ActivatedRouteSnapshot): Observable<null | ICashBalance> => {
  const id = route.params.id;
  if (id) {
    return inject(CashBalanceService)
      .find(id)
      .pipe(
        mergeMap((cashBalance: HttpResponse<ICashBalance>) => {
          if (cashBalance.body) {
            return of(cashBalance.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cashBalanceResolve;
