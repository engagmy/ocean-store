import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDailyCashDetail } from '../daily-cash-detail.model';
import { DailyCashDetailService } from '../service/daily-cash-detail.service';

const dailyCashDetailResolve = (route: ActivatedRouteSnapshot): Observable<null | IDailyCashDetail> => {
  const id = route.params.id;
  if (id) {
    return inject(DailyCashDetailService)
      .find(id)
      .pipe(
        mergeMap((dailyCashDetail: HttpResponse<IDailyCashDetail>) => {
          if (dailyCashDetail.body) {
            return of(dailyCashDetail.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dailyCashDetailResolve;
