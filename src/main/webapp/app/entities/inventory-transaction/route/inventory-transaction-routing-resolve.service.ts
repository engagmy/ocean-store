import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInventoryTransaction } from '../inventory-transaction.model';
import { InventoryTransactionService } from '../service/inventory-transaction.service';

const inventoryTransactionResolve = (route: ActivatedRouteSnapshot): Observable<null | IInventoryTransaction> => {
  const id = route.params.id;
  if (id) {
    return inject(InventoryTransactionService)
      .find(id)
      .pipe(
        mergeMap((inventoryTransaction: HttpResponse<IInventoryTransaction>) => {
          if (inventoryTransaction.body) {
            return of(inventoryTransaction.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default inventoryTransactionResolve;
