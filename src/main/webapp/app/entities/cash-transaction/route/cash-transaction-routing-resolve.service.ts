import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICashTransaction } from '../cash-transaction.model';
import { CashTransactionService } from '../service/cash-transaction.service';

const cashTransactionResolve = (route: ActivatedRouteSnapshot): Observable<null | ICashTransaction> => {
  const id = route.params.id;
  if (id) {
    return inject(CashTransactionService)
      .find(id)
      .pipe(
        mergeMap((cashTransaction: HttpResponse<ICashTransaction>) => {
          if (cashTransaction.body) {
            return of(cashTransaction.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default cashTransactionResolve;
