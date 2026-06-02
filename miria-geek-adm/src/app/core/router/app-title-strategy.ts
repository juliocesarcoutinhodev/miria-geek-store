import { Injectable, inject } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';

const APP_NAME = 'Miria Geek Store';

@Injectable({ providedIn: 'root' })
export class AppTitleStrategy extends TitleStrategy {
    private readonly title = inject(Title);

    override updateTitle(snapshot: RouterStateSnapshot): void {
        const routeTitle = this.buildTitle(snapshot);

        this.title.setTitle(routeTitle ? `${routeTitle} | ${APP_NAME}` : APP_NAME);
    }
}
