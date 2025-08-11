import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SalaryPaymentResolve from './route/salary-payment-routing-resolve.service';

const salaryPaymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/salary-payment.component').then(m => m.SalaryPaymentComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/salary-payment-detail.component').then(m => m.SalaryPaymentDetailComponent),
    resolve: {
      salaryPayment: SalaryPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/salary-payment-update.component').then(m => m.SalaryPaymentUpdateComponent),
    resolve: {
      salaryPayment: SalaryPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/salary-payment-update.component').then(m => m.SalaryPaymentUpdateComponent),
    resolve: {
      salaryPayment: SalaryPaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default salaryPaymentRoute;
