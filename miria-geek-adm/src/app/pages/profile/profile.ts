import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, OnInit, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SkeletonModule } from 'primeng/skeleton';
import { TagModule } from 'primeng/tag';
import { ProfileService } from '../../core/profile/profile.service';
import { UserProfile } from '../../core/profile/profile.model';

const passwordMatchValidator: ValidatorFn = (group: AbstractControl) => {
    const newPw = group.get('newPassword')?.value;
    const confirm = group.get('passwordConfirmation')?.value;

    return newPw && confirm && newPw !== confirm ? { passwordMismatch: true } : null;
};

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [ReactiveFormsModule, DatePipe, AvatarModule, ButtonModule, CardModule, DividerModule, InputTextModule, PasswordModule, SkeletonModule, TagModule],
    template: `
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Dados pessoais -->
            <p-card>
                <ng-template #header>
                    <div class="px-6 pt-6 pb-0">
                        <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">Dados pessoais</h2>
                        <p class="text-muted-color text-sm mt-1 mb-0">Atualize o nome exibido na sua conta</p>
                    </div>
                </ng-template>

                @if (loading()) {
                    <div class="flex flex-col items-center gap-4 pt-4">
                        <p-skeleton shape="circle" size="5rem" />
                        <p-skeleton width="10rem" height="1.5rem" />
                        <p-skeleton width="14rem" height="1rem" />
                        <p-skeleton width="100%" height="2.5rem" />
                        <p-skeleton width="100%" height="2.5rem" />
                    </div>
                } @else if (profile()) {
                    <div class="flex flex-col items-center gap-2 pt-4 pb-2">
                        <p-avatar [label]="initials()" size="xlarge" shape="circle" styleClass="text-xl font-bold" />
                        <div class="text-center">
                            <p class="text-lg font-semibold text-surface-900 dark:text-surface-0 m-0">{{ profile()!.name }}</p>
                            <p class="text-muted-color text-sm m-0">{{ profile()!.email }}</p>
                        </div>
                        <div class="flex gap-2 flex-wrap justify-center">
                            @for (role of profile()!.roles; track role) {
                                <p-tag [value]="formatRole(role)" severity="info" />
                            }
                            <p-tag [value]="profile()!.status" [severity]="profile()!.status === 'ACTIVE' ? 'success' : 'danger'" />
                        </div>
                        <p class="text-muted-color text-xs m-0">Membro desde {{ profile()!.createdAt | date: 'dd/MM/yyyy' }}</p>
                    </div>

                    <p-divider />

                    <form [formGroup]="nameForm" (ngSubmit)="onSaveName()">
                        <label for="fullName" class="block font-medium mb-2">Nome completo</label>
                        <input pInputText id="fullName" formControlName="fullName" class="w-full mb-1" placeholder="Seu nome completo" />
                        @if (nameForm.controls.fullName.invalid && nameForm.controls.fullName.touched) {
                            <small class="text-red-500 block mb-3">
                                @if (nameForm.controls.fullName.errors?.['required']) {
                                    Nome é obrigatório
                                } @else if (nameForm.controls.fullName.errors?.['minlength']) {
                                    Mínimo de 3 caracteres
                                } @else {
                                    Máximo de 255 caracteres
                                }
                            </small>
                        } @else {
                            <div class="mb-3"></div>
                        }

                        <label class="block font-medium mb-2">E-mail</label>
                        <input pInputText [value]="profile()!.email" class="w-full mb-4" [disabled]="true" />

                        <p-button label="Salvar" type="submit" [loading]="savingName()" styleClass="w-full" />
                    </form>
                }
            </p-card>

            <!-- Alterar senha -->
            <p-card>
                <ng-template #header>
                    <div class="px-6 pt-6 pb-0">
                        <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">Alterar senha</h2>
                        <p class="text-muted-color text-sm mt-1 mb-0">Use uma senha forte com letras, números e símbolos</p>
                    </div>
                </ng-template>

                @if (loading()) {
                    <div class="flex flex-col gap-4 pt-4">
                        <p-skeleton width="100%" height="2.5rem" />
                        <p-skeleton width="100%" height="2.5rem" />
                        <p-skeleton width="100%" height="2.5rem" />
                        <p-skeleton width="100%" height="2.5rem" />
                    </div>
                } @else {
                    <form [formGroup]="passwordForm" (ngSubmit)="onChangePassword()" class="pt-2">
                        <label for="currentPassword" class="block font-medium mb-2">Senha atual</label>
                        <p-password id="currentPassword" formControlName="currentPassword" placeholder="Digite a senha atual" [toggleMask]="true" [feedback]="false" [fluid]="true" styleClass="mb-1" />
                        @if (passwordForm.controls.currentPassword.invalid && passwordForm.controls.currentPassword.touched) {
                            <small class="text-red-500 block mb-3">Senha atual é obrigatória</small>
                        } @else {
                            <div class="mb-3"></div>
                        }

                        <label for="newPassword" class="block font-medium mb-2">Nova senha</label>
                        <p-password id="newPassword" formControlName="newPassword" placeholder="Digite a nova senha" [toggleMask]="true" [feedback]="true" [fluid]="true" styleClass="mb-1" />
                        @if (passwordForm.controls.newPassword.invalid && passwordForm.controls.newPassword.touched) {
                            <small class="text-red-500 block mb-3">Nova senha é obrigatória</small>
                        } @else {
                            <div class="mb-3"></div>
                        }

                        <label for="passwordConfirmation" class="block font-medium mb-2">Confirmar nova senha</label>
                        <p-password id="passwordConfirmation" formControlName="passwordConfirmation" placeholder="Repita a nova senha" [toggleMask]="true" [feedback]="false" [fluid]="true" styleClass="mb-1" />
                        @if (passwordForm.controls.passwordConfirmation.invalid && passwordForm.controls.passwordConfirmation.touched) {
                            <small class="text-red-500 block mb-3">Confirmação é obrigatória</small>
                        } @else if (passwordForm.errors?.['passwordMismatch'] && passwordForm.controls.passwordConfirmation.dirty) {
                            <small class="text-red-500 block mb-3">As senhas não coincidem</small>
                        } @else {
                            <div class="mb-3"></div>
                        }

                        <p-button label="Alterar senha" type="submit" [loading]="savingPassword()" styleClass="w-full" severity="secondary" />
                    </form>
                }
            </p-card>
        </div>
    `
})
export class Profile implements OnInit {
    private readonly profileService = inject(ProfileService);
    private readonly messageService = inject(MessageService);
    private readonly appRef = inject(ApplicationRef);
    private readonly fb = inject(FormBuilder);

    readonly loading = signal(true);
    readonly savingName = signal(false);
    readonly savingPassword = signal(false);
    readonly profile = signal<UserProfile | null>(null);

    readonly nameForm = this.fb.group({
        fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]]
    });

    readonly passwordForm = this.fb.group(
        {
            currentPassword: ['', Validators.required],
            newPassword: ['', Validators.required],
            passwordConfirmation: ['', Validators.required]
        },
        { validators: passwordMatchValidator }
    );

    async ngOnInit(): Promise<void> {
        try {
            const profile = await firstValueFrom(this.profileService.getProfile());

            this.profile.set(profile);
            this.nameForm.patchValue({ fullName: profile.name });
        } catch {
            this._toast('error', 'Erro ao carregar', 'Não foi possível carregar os dados do perfil.');
        } finally {
            this.loading.set(false);
        }
    }

    initials(): string {
        const name = this.profile()?.name ?? '';
        const parts = name.trim().split(/\s+/);

        if (parts.length >= 2) {
            return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
        }

        return name.slice(0, 2).toUpperCase();
    }

    formatRole(role: string): string {
        return role.replace('ROLE_', '');
    }

    async onSaveName(): Promise<void> {
        if (this.nameForm.invalid) {
            this.nameForm.markAllAsTouched();

            return;
        }

        this.savingName.set(true);

        try {
            const updated = await firstValueFrom(this.profileService.updateProfile({ fullName: this.nameForm.value.fullName! }));

            this.profile.set(updated);
            this._toast('success', 'Nome atualizado', 'Seus dados foram salvos com sucesso.');
        } catch (err) {
            this._toast('error', 'Erro ao salvar', this._errorMessage(err));
        } finally {
            this.savingName.set(false);
        }
    }

    async onChangePassword(): Promise<void> {
        if (this.passwordForm.invalid) {
            this.passwordForm.markAllAsTouched();

            return;
        }

        this.savingPassword.set(true);

        const { currentPassword, newPassword, passwordConfirmation } = this.passwordForm.value;

        try {
            await firstValueFrom(this.profileService.changePassword({ currentPassword: currentPassword!, newPassword: newPassword!, passwordConfirmation: passwordConfirmation! }));
            this.passwordForm.reset();
            this._toast('success', 'Senha alterada', 'Sua senha foi alterada com sucesso.');
        } catch (err) {
            this._toast('error', 'Erro ao alterar senha', this._errorMessage(err));
        } finally {
            this.savingPassword.set(false);
        }
    }

    private _toast(severity: 'success' | 'error', summary: string, detail: string): void {
        this.messageService.add({ severity, summary, detail, life: 6000 });
        this.appRef.tick();
    }

    private _errorMessage(err: unknown): string {
        if (err instanceof HttpErrorResponse) {
            return err.status === 0 ? 'Sem conexão com o servidor.' : (err.error?.message ?? 'Ocorreu um erro inesperado.');
        }

        return 'Ocorreu um erro inesperado.';
    }
}
