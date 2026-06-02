import { HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { TimeoutError, throwError } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';

const TIMEOUT_MS = 30_000;

export const httpTimeoutInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) =>
    next(req).pipe(
        timeout(TIMEOUT_MS),
        catchError((err) => {
            if (err instanceof TimeoutError) {
                return throwError(() => new Error(`A requisição excedeu ${TIMEOUT_MS / 1000}s sem resposta.`));
            }

            return throwError(() => err);
        })
    );
