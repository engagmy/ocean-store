import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDailyCashReconciliation } from '../daily-cash-reconciliation.model';
import { DailyCashReconciliationService } from '../service/daily-cash-reconciliation.service';

const dailyCashReconciliationResolve = (route: ActivatedRouteSnapshot): Observable<null | IDailyCashReconciliation> => {
  const id = route.params.id;
  if (id) {
    return inject(DailyCashReconciliationService)
      .find(id)
      .pipe(
        mergeMap((dailyCashReconciliation: HttpResponse<IDailyCashReconciliation>) => {
          if (dailyCashReconciliation.body) {
            return of(dailyCashReconciliation.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dailyCashReconciliationResolve;
