import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (): Promise<boolean | UrlTree> => {
    const auth = inject(AuthService);
    const router = inject(Router);

    await auth.sessionReady;

    if (auth.isAuthenticated()) {
        return true;
    }

    return router.createUrlTree(['/auth/login']);
};
