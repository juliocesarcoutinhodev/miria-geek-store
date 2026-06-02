import { Routes } from '@angular/router';
import { authGuard } from './app/core/auth/auth.guard';

export const appRoutes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    {
        path: '',
        loadComponent: () => import('./app/layout/component/app.layout').then((m) => m.AppLayout),
        canActivate: [authGuard],
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./app/pages/dashboard/dashboard').then((m) => m.Dashboard),
                title: 'Dashboard'
            },
            {
                path: 'profile',
                loadComponent: () => import('./app/pages/profile/profile').then((m) => m.Profile),
                title: 'Meu Perfil'
            },
            {
                path: 'registrations/users',
                loadComponent: () => import('./app/pages/cadastros/usuarios/usuarios').then((m) => m.Usuarios),
                title: 'Usuários'
            },
            {
                path: 'pages',
                loadChildren: () => import('./app/pages/pages.routes')
            }
        ]
    },
    {
        path: 'notfound',
        loadComponent: () => import('./app/pages/notfound/notfound').then((m) => m.Notfound),
        title: 'Página não encontrada'
    },
    {
        path: 'auth',
        loadChildren: () => import('./app/pages/auth/auth.routes')
    },
    { path: '**', redirectTo: '/notfound' }
];
