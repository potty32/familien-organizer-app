import { Routes } from '@angular/router';
import { sessionGuard } from './core/guards/session.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/profile-select/profile-select.component').then(m => m.ProfileSelectComponent)
  },
  {
    path: 'app',
    canActivate: [sessionGuard],
    loadComponent: () =>
      import('./features/app-shell/app-shell.component').then(m => m.AppShellComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'tasks',
        loadComponent: () =>
          import('./features/tasks/task-list/task-list.component').then(m => m.TaskListComponent)
      },
      {
        path: 'tasks/new',
        loadComponent: () =>
          import('./features/tasks/create-task/create-task.component').then(m => m.CreateTaskComponent)
      },
      {
        path: 'meal-wishes',
        loadComponent: () =>
          import('./features/meal-wishes/meal-wishes.component').then(m => m.MealWishesComponent)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
