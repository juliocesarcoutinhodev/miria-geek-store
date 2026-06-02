import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { APP_INITIALIZER, ApplicationConfig, ErrorHandler, provideZonelessChangeDetection } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { TitleStrategy, provideRouter, withComponentInputBinding, withEnabledBlockingInitialNavigation, withInMemoryScrolling } from '@angular/router';
import Aura from '@primeuix/themes/aura';
import { MessageService } from 'primeng/api';
import { providePrimeNG } from 'primeng/config';
import { AuthService } from './app/core/auth/auth.service';
import { AppErrorHandler } from './app/core/errors/app-error-handler';
import { authInterceptor } from './app/core/auth/auth.interceptor';
import { httpTimeoutInterceptor } from './app/core/interceptors/http-timeout.interceptor';
import { AppTitleStrategy } from './app/core/router/app-title-strategy';
import { appRoutes } from './app.routes';

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(appRoutes, withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' }), withEnabledBlockingInitialNavigation(), withComponentInputBinding()),
        provideHttpClient(withFetch(), withInterceptors([authInterceptor, httpTimeoutInterceptor])),
        provideAnimationsAsync(),
        provideZonelessChangeDetection(),
        MessageService,
        { provide: ErrorHandler, useClass: AppErrorHandler },
        { provide: TitleStrategy, useClass: AppTitleStrategy },
        {
            provide: APP_INITIALIZER,
            useFactory: (auth: AuthService) => () => auth.initializeSession(),
            deps: [AuthService],
            multi: true
        },
        providePrimeNG({
            theme: { preset: Aura, options: { darkModeSelector: '.app-dark' } },
            translation: {
                weak: 'Fraca',
                medium: 'Média',
                strong: 'Forte',
                passwordPrompt: 'Digite uma senha'
            }
        })
    ]
};
