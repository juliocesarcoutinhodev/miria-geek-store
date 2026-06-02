import { ErrorHandler, Injectable, inject, isDevMode } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable()
export class AppErrorHandler implements ErrorHandler {
    private readonly router = inject(Router);

    handleError(error: unknown): void {
        if (error instanceof HttpErrorResponse) {
            return;
        }

        if (isDevMode()) {
            console.error('[AppErrorHandler]', error);
        }

        if (error instanceof Error && error.message.includes('ChunkLoadError')) {
            window.location.reload();

            return;
        }

        this.router.navigate(['/notfound']);
    }
}
