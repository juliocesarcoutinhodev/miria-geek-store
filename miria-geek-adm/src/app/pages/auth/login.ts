import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { Toast } from 'primeng/toast';
import { AuthService } from '../../core/auth/auth.service';
import { AppFloatingConfigurator } from '../../layout/component/app.floatingconfigurator';

@Component({
    selector: 'app-login',
    standalone: true,
    providers: [MessageService],
    imports: [ReactiveFormsModule, ButtonModule, CheckboxModule, DialogModule, InputTextModule, PasswordModule, RouterModule, AppFloatingConfigurator, Toast],
    template: `
        <p-toast position="top-right" />
        <app-floating-configurator />

        <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-screen overflow-hidden">
            <div class="flex flex-col items-center justify-center">
                <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                    <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px">
                        <div class="text-center mb-8">
                            <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" class="mb-8 w-16 shrink-0 mx-auto">
                                <circle cx="32" cy="34" r="18" stroke="var(--primary-color)" stroke-width="3" />
                                <path d="M18 24L26 10L30 24" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                                <path d="M46 24L38 10L34 24" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                                <rect x="20" y="30" width="10" height="8" rx="2" stroke="var(--primary-color)" stroke-width="3" />
                                <rect x="34" y="30" width="10" height="8" rx="2" stroke="var(--primary-color)" stroke-width="3" />
                                <path d="M30 34H34" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" />
                                <path d="M26 42Q32 46 38 42" stroke="var(--primary-color)" stroke-width="3" stroke-linecap="round" />
                                <path d="M50 14L52 18L56 19L52 20L50 24L48 20L44 19L48 18Z" fill="var(--primary-color)" />
                            </svg>
                            <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">Bem vindo!</div>
                            <span class="text-muted-color font-medium">Faça login para continuar</span>
                        </div>

                        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
                            <label for="email" class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Email</label>
                            <input pInputText id="email" type="email" placeholder="Digite seu email" class="w-full md:w-120 mb-1" formControlName="email" [class.ng-invalid]="loginForm.controls.email.invalid && loginForm.controls.email.touched" />
                            @if (loginForm.controls.email.invalid && loginForm.controls.email.touched) {
                                <small class="text-red-500 block mb-4">
                                    @if (loginForm.controls.email.errors?.['required']) {
                                        Email é obrigatório
                                    } @else {
                                        Email inválido
                                    }
                                </small>
                            } @else {
                                <div class="mb-4"></div>
                            }

                            <label for="password" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Senha</label>
                            <p-password id="password" formControlName="password" placeholder="Digite sua senha" [toggleMask]="true" styleClass="mb-1" [fluid]="true" [feedback]="false" />
                            @if (loginForm.controls.password.invalid && loginForm.controls.password.touched) {
                                <small class="text-red-500 block mb-3">Senha é obrigatória</small>
                            } @else {
                                <div class="mb-3"></div>
                            }

                            <div class="flex items-center justify-between mb-6">
                                <div class="flex items-center gap-2">
                                    <p-checkbox formControlName="rememberMe" inputId="rememberMe" [binary]="true" />
                                    <label for="rememberMe" class="cursor-pointer select-none">Lembre-me</label>
                                </div>
                                <span class="text-primary font-medium cursor-pointer hover:underline" (click)="openForgotPassword()">Esqueceu a senha?</span>
                            </div>

                            <p-button label="Entrar" styleClass="w-full" type="submit" [loading]="loading()" />
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <p-dialog header="Recuperar senha" [(visible)]="forgotPasswordVisible" [modal]="true" [style]="{ width: '28rem' }" [draggable]="false" [resizable]="false">
            <form [formGroup]="forgotForm" (ngSubmit)="onForgotPasswordSubmit()">
                <p class="text-muted-color mb-5">Informe seu e-mail e enviaremos um link para redefinir sua senha.</p>

                <label for="forgotEmail" class="block text-surface-900 dark:text-surface-0 font-medium mb-2">E-mail</label>
                <input pInputText id="forgotEmail" type="email" placeholder="Digite seu e-mail" class="w-full mb-1" formControlName="email" [class.ng-invalid]="forgotForm.controls.email.invalid && forgotForm.controls.email.touched" />
                @if (forgotForm.controls.email.invalid && forgotForm.controls.email.touched) {
                    <small class="text-red-500 block mb-4">
                        @if (forgotForm.controls.email.errors?.['required']) {
                            E-mail é obrigatório
                        } @else {
                            E-mail inválido
                        }
                    </small>
                } @else {
                    <div class="mb-4"></div>
                }

                <div class="flex justify-end gap-2 mt-2">
                    <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="forgotPasswordVisible.set(false)" />
                    <p-button label="Enviar link" type="submit" [loading]="forgotLoading()" />
                </div>
            </form>
        </p-dialog>
    `
})
export class Login {
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly fb = inject(FormBuilder);
    private readonly messageService = inject(MessageService);
    private readonly appRef = inject(ApplicationRef);

    readonly loading = signal(false);
    readonly forgotPasswordVisible = signal(false);
    readonly forgotLoading = signal(false);

    readonly loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        rememberMe: [false]
    });

    readonly forgotForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]]
    });

    async onSubmit(): Promise<void> {
        if (this.loginForm.invalid) {
            this.loginForm.markAllAsTouched();

            return;
        }

        this.loading.set(true);

        const { email, password } = this.loginForm.value;

        try {
            const user = await firstValueFrom(this.authService.login({ email: email!, password: password! }));

            if (!user.roles.includes('ROLE_ADMIN')) {
                this.authService.clearUser();
                this.loading.set(false);
                this._showError('Acesso negado', 'Esta área é exclusiva para administradores.');

                return;
            }

            this.router.navigate(['/dashboard']);
        } catch (err) {
            this.loading.set(false);
            this._showError('Erro ao entrar', this._resolveErrorMessage(err as HttpErrorResponse));
        }
    }

    openForgotPassword(): void {
        this.forgotForm.reset();
        this.forgotPasswordVisible.set(true);
    }

    async onForgotPasswordSubmit(): Promise<void> {
        if (this.forgotForm.invalid) {
            this.forgotForm.markAllAsTouched();

            return;
        }

        this.forgotLoading.set(true);

        const { email } = this.forgotForm.value;

        try {
            await firstValueFrom(this.authService.forgotPassword(email!));
            this.forgotLoading.set(false);
            this.forgotPasswordVisible.set(false);
            this._showSuccess('Link enviado', `Verifique sua caixa de entrada em ${email}.`);
        } catch (err) {
            this.forgotLoading.set(false);
            this._showError('Erro ao enviar', this._resolveErrorMessage(err as HttpErrorResponse));
        }
    }

    private _showError(summary: string, detail: string): void {
        this.messageService.add({ severity: 'error', summary, detail, life: 6000 });
        this.appRef.tick();
    }

    private _showSuccess(summary: string, detail: string): void {
        this.messageService.add({ severity: 'success', summary, detail, life: 6000 });
        this.appRef.tick();
    }

    private _resolveErrorMessage(err: HttpErrorResponse): string {
        if (err.status === 0) {
            return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
        }

        return err.error?.message ?? 'Ocorreu um erro inesperado. Tente novamente.';
    }
}
