import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import OutgoingPaymentResolve from './route/outgoing-payment-routing-resolve.service';

const outgoingPaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/outgoing-payment.component').then(m => m.OutgoingPaymentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/outgoing-payment-detail.component').then(m => m.OutgoingPaymentDetailComponent),
    resolve: {
      outgoingPayment: OutgoingPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/outgoing-payment-update.component').then(m => m.OutgoingPaymentUpdateComponent),
    resolve: {
      outgoingPayment: OutgoingPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/outgoing-payment-update.component').then(m => m.OutgoingPaymentUpdateComponent),
    resolve: {
      outgoingPayment: OutgoingPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default outgoingPaymentRoute;
