import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CashTransactionResolve from './route/cash-transaction-routing-resolve.service';

const cashTransactionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cash-transaction.component').then(m => m.CashTransactionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cash-transaction-detail.component').then(m => m.CashTransactionDetailComponent),
    resolve: {
      cashTransaction: CashTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cash-transaction-update.component').then(m => m.CashTransactionUpdateComponent),
    resolve: {
      cashTransaction: CashTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cash-transaction-update.component').then(m => m.CashTransactionUpdateComponent),
    resolve: {
      cashTransaction: CashTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default cashTransactionRoute;
