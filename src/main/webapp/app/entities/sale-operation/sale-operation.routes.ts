import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SaleOperationResolve from './route/sale-operation-routing-resolve.service';

const saleOperationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sale-operation.component').then(m => m.SaleOperationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sale-operation-detail.component').then(m => m.SaleOperationDetailComponent),
    resolve: {
      saleOperation: SaleOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sale-operation-update.component').then(m => m.SaleOperationUpdateComponent),
    resolve: {
      saleOperation: SaleOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sale-operation-update.component').then(m => m.SaleOperationUpdateComponent),
    resolve: {
      saleOperation: SaleOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default saleOperationRoute;
