import { Component, inject } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StyleClassModule } from 'primeng/styleclass';
import { AppConfigurator } from './app.configurator';
import { LayoutService } from '@/app/layout/service/layout.service';
import { AuthService } from '@/app/core/auth/auth.service';

@Component({
    selector: 'app-topbar',
    standalone: true,
    imports: [RouterModule, CommonModule, StyleClassModule, AppConfigurator],
    template: ` <div class="layout-topbar">
        <div class="layout-topbar-logo-container">
            <button class="layout-menu-button layout-topbar-action" (click)="layoutService.onMenuToggle()">
                <i class="pi pi-bars"></i>
            </button>
            <a class="layout-topbar-logo" routerLink="/dashboard">
                <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="32" cy="34" r="18" stroke="var(--primary-color)" stroke-width="3" />
                    <path d="M18 24L26 10L30 24" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                    <path d="M46 24L38 10L34 24" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                    <rect x="20" y="30" width="10" height="8" rx="2" stroke="var(--primary-color)" stroke-width="3" />
                    <rect x="34" y="30" width="10" height="8" rx="2" stroke="var(--primary-color)" stroke-width="3" />
                    <path d="M30 34H34" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" />
                    <path d="M26 42Q32 46 38 42" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" />
                    <path d="M50 14L52 18L56 19L52 20L50 24L48 20L44 19L48 18Z" fill="var(--primary-color)" />
                </svg>
                <span>Miria Geek Store</span>
            </a>
        </div>

        <div class="layout-topbar-actions">
            <div class="layout-config-menu">
                <button type="button" class="layout-topbar-action" (click)="toggleDarkMode()">
                    <i [ngClass]="{ 'pi ': true, 'pi-moon': layoutService.isDarkTheme(), 'pi-sun': !layoutService.isDarkTheme() }"></i>
                </button>
                <div class="relative">
                    <button
                        class="layout-topbar-action layout-topbar-action-highlight"
                        pStyleClass="@next"
                        enterFromClass="hidden"
                        enterActiveClass="animate-scalein"
                        leaveToClass="hidden"
                        leaveActiveClass="animate-fadeout"
                        [hideOnOutsideClick]="true"
                    >
                        <i class="pi pi-palette"></i>
                    </button>
                    <app-configurator />
                </div>
            </div>

            <button class="layout-topbar-menu-button layout-topbar-action" pStyleClass="@next" enterFromClass="hidden" enterActiveClass="animate-scalein" leaveToClass="hidden" leaveActiveClass="animate-fadeout" [hideOnOutsideClick]="true">
                <i class="pi pi-ellipsis-v"></i>
            </button>

            <div class="layout-topbar-menu hidden lg:block">
                <div class="layout-topbar-menu-content">
                    <button type="button" class="layout-topbar-action">
                        <i class="pi pi-calendar"></i>
                        <span>Calendar</span>
                    </button>
                    <button type="button" class="layout-topbar-action">
                        <i class="pi pi-inbox"></i>
                        <span>Messages</span>
                    </button>
                    <button type="button" class="layout-topbar-action" routerLink="/profile">
                        <i class="pi pi-user"></i>
                        <span>{{ authService.user()?.name }}</span>
                    </button>
                    <button type="button" class="layout-topbar-action" (click)="authService.logout()">
                        <i class="pi pi-sign-out"></i>
                        <span>Sair</span>
                    </button>
                </div>
            </div>
        </div>
    </div>`
})
export class AppTopbar {
    items!: MenuItem[];

    layoutService = inject(LayoutService);
    authService = inject(AuthService);

    toggleDarkMode() {
        this.layoutService.layoutConfig.update((state) => ({
            ...state,
            darkTheme: !state.darkTheme
        }));
    }
}
