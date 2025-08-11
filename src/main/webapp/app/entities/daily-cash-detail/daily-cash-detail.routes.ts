import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DailyCashDetailResolve from './route/daily-cash-detail-routing-resolve.service';

const dailyCashDetailRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/daily-cash-detail.component').then(m => m.DailyCashDetailComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/daily-cash-detail-detail.component').then(m => m.DailyCashDetailDetailComponent),
    resolve: {
      dailyCashDetail: DailyCashDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/daily-cash-detail-update.component').then(m => m.DailyCashDetailUpdateComponent),
    resolve: {
      dailyCashDetail: DailyCashDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/daily-cash-detail-update.component').then(m => m.DailyCashDetailUpdateComponent),
    resolve: {
      dailyCashDetail: DailyCashDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default dailyCashDetailRoute;
