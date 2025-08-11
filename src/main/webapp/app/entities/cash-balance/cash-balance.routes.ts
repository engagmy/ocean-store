import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CashBalanceResolve from './route/cash-balance-routing-resolve.service';

const cashBalanceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cash-balance.component').then(m => m.CashBalanceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cash-balance-detail.component').then(m => m.CashBalanceDetailComponent),
    resolve: {
      cashBalance: CashBalanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cash-balance-update.component').then(m => m.CashBalanceUpdateComponent),
    resolve: {
      cashBalance: CashBalanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cash-balance-update.component').then(m => m.CashBalanceUpdateComponent),
    resolve: {
      cashBalance: CashBalanceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cashBalanceRoute;
