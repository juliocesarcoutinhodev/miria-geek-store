import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { Toast } from 'primeng/toast';
import { AuthService } from '../../core/auth/auth.service';
import { AppFloatingConfigurator } from '../../layout/component/app.floatingconfigurator';

@Component({
    selector: 'app-reset-password',
    standalone: true,
    providers: [MessageService],
    imports: [ReactiveFormsModule, ButtonModule, PasswordModule, RouterModule, AppFloatingConfigurator, Toast],
    template: `
        <p-toast position="top-right" />
        <app-floating-configurator />

        <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-screen overflow-hidden">
            <div class="flex flex-col items-center justify-center">
                <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                    <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20" style="border-radius: 53px; min-width: 28rem">
                        <div class="text-center mb-8">
                            <div class="flex justify-center items-center border-2 border-primary rounded-full mx-auto mb-6" style="width: 3.5rem; height: 3.5rem">
                                <i class="pi pi-lock-open text-primary text-xl"></i>
                            </div>
                            <div class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-2">Nova senha</div>
                            <span class="text-muted-color">Escolha uma senha segura para sua conta</span>
                        </div>

                        @if (invalidToken()) {
                            <div class="text-center">
                                <p class="text-red-500 mb-6">Este link é inválido ou já foi utilizado.</p>
                                <p-button label="Voltar ao login" [outlined]="true" routerLink="/auth/login" />
                            </div>
                        } @else {
                            <form [formGroup]="form" (ngSubmit)="onSubmit()">
                                <label for="newPassword" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Nova senha</label>
                                <p-password id="newPassword" formControlName="newPassword" placeholder="Digite a nova senha" [toggleMask]="true" styleClass="mb-1" [fluid]="true" [feedback]="true" />
                                @if (form.controls.newPassword.invalid && form.controls.newPassword.touched) {
                                    <small class="text-red-500 block mb-4">Senha é obrigatória</small>
                                } @else {
                                    <div class="mb-4"></div>
                                }

                                <label for="passwordConfirmation" class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Confirmar senha</label>
                                <p-password id="passwordConfirmation" formControlName="passwordConfirmation" placeholder="Repita a nova senha" [toggleMask]="true" styleClass="mb-1" [fluid]="true" [feedback]="false" />
                                @if (form.controls.passwordConfirmation.invalid && form.controls.passwordConfirmation.touched) {
                                    <small class="text-red-500 block mb-4">Confirmação é obrigatória</small>
                                } @else if (form.errors?.['passwordMismatch'] && form.controls.passwordConfirmation.dirty) {
                                    <small class="text-red-500 block mb-4">As senhas não coincidem</small>
                                } @else {
                                    <div class="mb-4"></div>
                                }

                                <p-button label="Redefinir senha" styleClass="w-full" type="submit" [loading]="loading()" />

                                <div class="text-center mt-4">
                                    <a routerLink="/auth/login" class="text-primary font-medium cursor-pointer hover:underline">Voltar ao login</a>
                                </div>
                            </form>
                        }
                    </div>
                </div>
            </div>
        </div>
    `
})
export class ResetPassword implements OnInit {
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);
    private readonly fb = inject(FormBuilder);
    private readonly messageService = inject(MessageService);
    private readonly appRef = inject(ApplicationRef);

    readonly loading = signal(false);
    readonly invalidToken = signal(false);

    private token = '';

    readonly form = this.fb.group(
        {
            newPassword: ['', Validators.required],
            passwordConfirmation: ['', Validators.required]
        },
        { validators: passwordMatchValidator }
    );

    ngOnInit(): void {
        const token = this.route.snapshot.queryParamMap.get('token');

        if (!token) {
            this.invalidToken.set(true);

            return;
        }

        this.token = token;
    }

    async onSubmit(): Promise<void> {
        if (this.form.invalid) {
            this.form.markAllAsTouched();

            return;
        }

        this.loading.set(true);

        const { newPassword, passwordConfirmation } = this.form.value;

        try {
            await firstValueFrom(
                this.authService.resetPassword({
                    token: this.token,
                    newPassword: newPassword!,
                    passwordConfirmation: passwordConfirmation!
                })
            );
            this.messageService.add({
                severity: 'success',
                summary: 'Senha redefinida',
                detail: 'Sua senha foi alterada com sucesso. Faça login.',
                life: 5000
            });
            this.appRef.tick();
            setTimeout(() => this.router.navigate(['/auth/login']), 2000);
        } catch (err) {
            this.loading.set(false);
            const msg = err instanceof HttpErrorResponse && err.status !== 0 ? (err.error?.message ?? 'Erro inesperado.') : 'Não foi possível conectar ao servidor.';

            this.messageService.add({ severity: 'error', summary: 'Erro ao redefinir', detail: msg, life: 6000 });
            this.appRef.tick();
        }
    }
}

function passwordMatchValidator(group: import('@angular/forms').AbstractControl) {
    const pw = group.get('newPassword')?.value;
    const confirm = group.get('passwordConfirmation')?.value;

    return pw && confirm && pw !== confirm ? { passwordMismatch: true } : null;
}
