import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DailyCashReconciliationResolve from './route/daily-cash-reconciliation-routing-resolve.service';

const dailyCashReconciliationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/daily-cash-reconciliation.component').then(m => m.DailyCashReconciliationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/daily-cash-reconciliation-detail.component').then(m => m.DailyCashReconciliationDetailComponent),
    resolve: {
      dailyCashReconciliation: DailyCashReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/daily-cash-reconciliation-update.component').then(m => m.DailyCashReconciliationUpdateComponent),
    resolve: {
      dailyCashReconciliation: DailyCashReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/daily-cash-reconciliation-update.component').then(m => m.DailyCashReconciliationUpdateComponent),
    resolve: {
      dailyCashReconciliation: DailyCashReconciliationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dailyCashReconciliationRoute;
