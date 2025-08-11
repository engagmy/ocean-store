import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SalePaymentResolve from './route/sale-payment-routing-resolve.service';

const salePaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sale-payment.component').then(m => m.SalePaymentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sale-payment-detail.component').then(m => m.SalePaymentDetailComponent),
    resolve: {
      salePayment: SalePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sale-payment-update.component').then(m => m.SalePaymentUpdateComponent),
    resolve: {
      salePayment: SalePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sale-payment-update.component').then(m => m.SalePaymentUpdateComponent),
    resolve: {
      salePayment: SalePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default salePaymentRoute;
