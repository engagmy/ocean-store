import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'inventorySystemApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'brand',
    data: { pageTitle: 'inventorySystemApp.brand.home.title' },
    loadChildren: () => import('./brand/brand.routes'),
  },
  {
    path: 'product-category',
    data: { pageTitle: 'inventorySystemApp.productCategory.home.title' },
    loadChildren: () => import('./product-category/product-category.routes'),
  },
  {
    path: 'product',
    data: { pageTitle: 'inventorySystemApp.product.home.title' },
    loadChildren: () => import('./product/product.routes'),
  },
  {
    path: 'inventory-transaction',
    data: { pageTitle: 'inventorySystemApp.inventoryTransaction.home.title' },
    loadChildren: () => import('./inventory-transaction/inventory-transaction.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'inventorySystemApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'sale-operation',
    data: { pageTitle: 'inventorySystemApp.saleOperation.home.title' },
    loadChildren: () => import('./sale-operation/sale-operation.routes'),
  },
  {
    path: 'sale',
    data: { pageTitle: 'inventorySystemApp.sale.home.title' },
    loadChildren: () => import('./sale/sale.routes'),
  },
  {
    path: 'sale-payment',
    data: { pageTitle: 'inventorySystemApp.salePayment.home.title' },
    loadChildren: () => import('./sale-payment/sale-payment.routes'),
  },
  {
    path: 'purchase-operation',
    data: { pageTitle: 'inventorySystemApp.purchaseOperation.home.title' },
    loadChildren: () => import('./purchase-operation/purchase-operation.routes'),
  },
  {
    path: 'purchase',
    data: { pageTitle: 'inventorySystemApp.purchase.home.title' },
    loadChildren: () => import('./purchase/purchase.routes'),
  },
  {
    path: 'purchase-payment',
    data: { pageTitle: 'inventorySystemApp.purchasePayment.home.title' },
    loadChildren: () => import('./purchase-payment/purchase-payment.routes'),
  },
  {
    path: 'bill',
    data: { pageTitle: 'inventorySystemApp.bill.home.title' },
    loadChildren: () => import('./bill/bill.routes'),
  },
  {
    path: 'supplier',
    data: { pageTitle: 'inventorySystemApp.supplier.home.title' },
    loadChildren: () => import('./supplier/supplier.routes'),
  },
  {
    path: 'employee',
    data: { pageTitle: 'inventorySystemApp.employee.home.title' },
    loadChildren: () => import('./employee/employee.routes'),
  },
  {
    path: 'salary-payment',
    data: { pageTitle: 'inventorySystemApp.salaryPayment.home.title' },
    loadChildren: () => import('./salary-payment/salary-payment.routes'),
  },
  {
    path: 'cash-transaction',
    data: { pageTitle: 'inventorySystemApp.cashTransaction.home.title' },
    loadChildren: () => import('./cash-transaction/cash-transaction.routes'),
  },
  {
    path: 'outgoing-payment',
    data: { pageTitle: 'inventorySystemApp.outgoingPayment.home.title' },
    loadChildren: () => import('./outgoing-payment/outgoing-payment.routes'),
  },
  {
    path: 'cash-balance',
    data: { pageTitle: 'inventorySystemApp.cashBalance.home.title' },
    loadChildren: () => import('./cash-balance/cash-balance.routes'),
  },
  {
    path: 'daily-cash-reconciliation',
    data: { pageTitle: 'inventorySystemApp.dailyCashReconciliation.home.title' },
    loadChildren: () => import('./daily-cash-reconciliation/daily-cash-reconciliation.routes'),
  },
  {
    path: 'daily-cash-detail',
    data: { pageTitle: 'inventorySystemApp.dailyCashDetail.home.title' },
    loadChildren: () => import('./daily-cash-detail/daily-cash-detail.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
