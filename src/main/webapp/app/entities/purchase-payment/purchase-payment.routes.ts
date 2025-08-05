import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PurchasePaymentResolve from './route/purchase-payment-routing-resolve.service';

const purchasePaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchase-payment.component').then(m => m.PurchasePaymentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchase-payment-detail.component').then(m => m.PurchasePaymentDetailComponent),
    resolve: {
      purchasePayment: PurchasePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchase-payment-update.component').then(m => m.PurchasePaymentUpdateComponent),
    resolve: {
      purchasePayment: PurchasePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchase-payment-update.component').then(m => m.PurchasePaymentUpdateComponent),
    resolve: {
      purchasePayment: PurchasePaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchasePaymentRoute;
