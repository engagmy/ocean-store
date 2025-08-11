import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import InventoryTransactionResolve from './route/inventory-transaction-routing-resolve.service';

const inventoryTransactionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/inventory-transaction.component').then(m => m.InventoryTransactionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/inventory-transaction-detail.component').then(m => m.InventoryTransactionDetailComponent),
    resolve: {
      inventoryTransaction: InventoryTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/inventory-transaction-update.component').then(m => m.InventoryTransactionUpdateComponent),
    resolve: {
      inventoryTransaction: InventoryTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/inventory-transaction-update.component').then(m => m.InventoryTransactionUpdateComponent),
    resolve: {
      inventoryTransaction: InventoryTransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default inventoryTransactionRoute;
