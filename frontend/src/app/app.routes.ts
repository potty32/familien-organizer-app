import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/profile-select/profile-select.component').then(m => m.ProfileSelectComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
