import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!req.url.startsWith(environment.apiUrl)) {
        return next(req);
    }

    const isRefreshCall = req.url.includes('/auth/refresh');
    const isLoginCall = req.url.includes('/auth/login');
    const withCreds = req.clone({ withCredentials: true });

    return next(withCreds).pipe(
        catchError((error: HttpErrorResponse) => {
            const shouldRefresh = error.status === 401 && !isRefreshCall && !isLoginCall && authService.isAuthenticated();

            if (shouldRefresh) {
                return authService.refresh().pipe(
                    switchMap(() => next(withCreds)),
                    catchError((refreshError: HttpErrorResponse) => {
                        authService.clearUser();
                        router.navigate(['/auth/login']);

                        return throwError(() => refreshError);
                    })
                );
            }

            return throwError(() => error);
        })
    );
};
