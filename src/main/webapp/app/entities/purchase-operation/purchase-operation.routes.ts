import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PurchaseOperationResolve from './route/purchase-operation-routing-resolve.service';

const purchaseOperationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/purchase-operation.component').then(m => m.PurchaseOperationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/purchase-operation-detail.component').then(m => m.PurchaseOperationDetailComponent),
    resolve: {
      purchaseOperation: PurchaseOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/purchase-operation-update.component').then(m => m.PurchaseOperationUpdateComponent),
    resolve: {
      purchaseOperation: PurchaseOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/purchase-operation-update.component').then(m => m.PurchaseOperationUpdateComponent),
    resolve: {
      purchaseOperation: PurchaseOperationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default purchaseOperationRoute;
