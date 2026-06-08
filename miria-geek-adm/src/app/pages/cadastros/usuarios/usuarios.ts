import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { DividerModule } from 'primeng/divider';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule, TablePageEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { AdminUserService } from '../../../core/admin/admin-user.service';
import { AdminUserDetail, UserSummary } from '../../../core/admin/admin-user.model';

interface SortEvent {
    field?: string | null;
    order?: number | null;
}

@Component({
    selector: 'app-usuarios',
    standalone: true,
    providers: [ConfirmationService],
    imports: [
        ReactiveFormsModule,
        DatePipe,
        AvatarModule,
        ButtonModule,
        CardModule,
        ConfirmDialogModule,
        DialogModule,
        DividerModule,
        IconFieldModule,
        InputIconModule,
        InputTextModule,
        PasswordModule,
        SelectModule,
        SkeletonModule,
        TableModule,
        TagModule,
        TooltipModule
    ],
    template: `
        <p-confirm-dialog />

        <p-card>
            <!-- Cabeçalho -->
            <ng-template #header>
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 px-6 pt-6 pb-0">
                    <div>
                        <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">Usuários</h2>
                        <p class="text-muted-color text-sm mt-1 mb-0">Gerencie os usuários cadastrados no sistema</p>
                    </div>
                    <p-button icon="pi pi-user-plus" label="Novo usuário" (onClick)="openCreateDialog()" />
                </div>
            </ng-template>

            <!-- Filtros -->
            <form [formGroup]="filterForm" (ngSubmit)="applyFilters()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 mb-4">
                <p-iconfield>
                    <p-inputicon class="pi pi-user" />
                    <input pInputText formControlName="nome" placeholder="Filtrar por nome" (keyup.enter)="applyFilters()" class="w-full" />
                </p-iconfield>

                <p-iconfield>
                    <p-inputicon class="pi pi-envelope" />
                    <input pInputText formControlName="email" placeholder="Filtrar por e-mail" (keyup.enter)="applyFilters()" class="w-full" />
                </p-iconfield>

                <p-select formControlName="status" [options]="statusOptions" optionLabel="label" optionValue="value" placeholder="Status" class="w-full" />

                <p-select formControlName="role" [options]="roleOptions" optionLabel="label" optionValue="value" placeholder="Cargo" class="w-full" />

                <div class="flex gap-2 sm:col-span-2 lg:col-span-4 lg:justify-end">
                    <p-button type="submit" icon="pi pi-search" label="Filtrar" [loading]="loading()" />
                    <p-button type="button" icon="pi pi-times" label="Limpar" severity="secondary" [outlined]="true" (onClick)="clearFilters()" [disabled]="loading()" />
                </div>
            </form>

            <!-- Tabela -->
            <p-table
                [value]="users()"
                dataKey="id"
                [lazy]="true"
                [paginator]="true"
                [first]="first"
                [rows]="pageSize"
                [totalRecords]="totalElements()"
                [loading]="loading()"
                [rowHover]="true"
                styleClass="p-datatable-striped"
                paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
                currentPageReportTemplate="Exibindo {first} a {last} de {totalRecords} usuários"
                [rowsPerPageOptions]="[10, 20, 50]"
                [sortField]="sortField()"
                [sortOrder]="sortOrder()"
                (onPage)="onPage($event)"
                (onSort)="onSort($event)"
                [tableStyle]="{ 'min-width': '50rem' }"
            >
                <ng-template #empty>
                    <div class="text-center py-8 text-muted-color">
                        <i class="pi pi-users text-4xl mb-3 block"></i>
                        Nenhum usuário encontrado.
                    </div>
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th pSortableColumn="fullName" style="min-width: 12rem">Nome <p-sortIcon field="fullName" /></th>
                        <th style="min-width: 14rem">E-mail</th>
                        <th style="min-width: 9rem">Cargo</th>
                        <th style="min-width: 8rem">Status</th>
                        <th pSortableColumn="createdAt" style="min-width: 10rem">Criado em <p-sortIcon field="createdAt" /></th>
                        <th style="width: 7rem; text-align: center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-user>
                    <tr class="cursor-pointer" (click)="onRowClick(user)">
                        <td>
                            <div class="flex items-center gap-2">
                                <p-avatar [label]="initials(user.fullName)" shape="circle" size="normal" />
                                <span class="font-medium">{{ user.fullName }}</span>
                            </div>
                        </td>
                        <td class="text-muted-color">{{ user.email }}</td>
                        <td>
                            <p-tag [value]="formatRole(user.role)" severity="contrast" />
                        </td>
                        <td>
                            <p-tag [value]="statusLabel(user.status)" [severity]="statusSeverity(user.status)" />
                        </td>
                        <td class="text-muted-color">{{ user.createdAt | date: 'dd/MM/yyyy' }}</td>
                        <td>
                            <div class="flex items-center justify-center gap-1">
                                <p-button icon="pi pi-pencil" [rounded]="true" [text]="true" severity="warn" pTooltip="Editar usuário" tooltipPosition="top" (onClick)="$event.stopPropagation(); openEditDialog(user)" />
                                <p-button
                                    [icon]="user.status === 'ACTIVE' ? 'pi pi-user-minus' : 'pi pi-user-plus'"
                                    [rounded]="true"
                                    [text]="true"
                                    [severity]="user.status === 'ACTIVE' ? 'danger' : 'success'"
                                    [pTooltip]="user.status === 'ACTIVE' ? 'Desativar usuário' : 'Ativar usuário'"
                                    tooltipPosition="top"
                                    (onClick)="$event.stopPropagation(); confirmToggleStatus(user)"
                                />
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>
        </p-card>

        <!-- Dialog: Detalhes -->
        <p-dialog header="Detalhes do usuário" [(visible)]="detailsVisible" [modal]="true" [style]="{ width: '36rem' }" [draggable]="false" [resizable]="false" (onHide)="closeDetails()">
            @if (detailLoading()) {
                <div class="flex flex-col gap-3 py-2">
                    @for (_ of [1, 2, 3, 4, 5]; track $index) {
                        <p-skeleton height="1.5rem" />
                    }
                </div>
            } @else if (selectedUser()) {
                <div class="flex items-center gap-3 mb-4">
                    <p-avatar [label]="initials(selectedUser()!.fullName)" shape="circle" size="xlarge" styleClass="text-xl font-bold" />
                    <div>
                        <p class="text-lg font-semibold m-0">{{ selectedUser()!.fullName }}</p>
                        <p class="text-muted-color text-sm m-0">{{ selectedUser()!.email }}</p>
                    </div>
                </div>

                <p-divider />

                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <p class="text-sm text-muted-color mb-1">Cargo</p>
                        <p-tag [value]="formatRole(selectedUser()!.role)" severity="contrast" />
                    </div>
                    <div>
                        <p class="text-sm text-muted-color mb-1">Status</p>
                        <p-tag [value]="statusLabel(selectedUser()!.status)" [severity]="statusSeverity(selectedUser()!.status)" />
                    </div>
                    <div>
                        <p class="text-sm text-muted-color mb-1">Membro desde</p>
                        <p class="font-medium m-0">{{ selectedUser()!.createdAt | date: 'dd/MM/yyyy' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-muted-color mb-1">Último login</p>
                        <p class="font-medium m-0">{{ selectedUser()!.ultimoLogin ? (selectedUser()!.ultimoLogin | date: 'dd/MM/yyyy HH:mm') : 'Nunca' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-muted-color mb-1">Total de pedidos</p>
                        <p class="font-medium m-0">{{ selectedUser()!.totalPedidos }}</p>
                    </div>
                </div>
            }

            <ng-template #footer>
                <p-button label="Fechar" severity="secondary" [outlined]="true" (onClick)="closeDetails()" />
            </ng-template>
        </p-dialog>

        <!-- Dialog: Criar usuário -->
        <p-dialog header="Novo usuário" [(visible)]="createVisible" [modal]="true" [style]="{ width: '32rem' }" [draggable]="false" [resizable]="false" (onHide)="closeCreateDialog()">
            <form [formGroup]="createForm" (ngSubmit)="onSaveCreate()" class="flex flex-col gap-4 pt-2">
                <div>
                    <label for="c-name" class="block font-medium mb-2">Nome completo</label>
                    <input pInputText id="c-name" formControlName="fullName" class="w-full" placeholder="Nome do usuário" />
                    @if (createForm.controls.fullName.invalid && createForm.controls.fullName.touched) {
                        <small class="text-red-500">{{ nameError(createForm.controls.fullName) }}</small>
                    }
                </div>
                <div>
                    <label for="c-email" class="block font-medium mb-2">E-mail</label>
                    <input pInputText id="c-email" formControlName="email" type="email" class="w-full" placeholder="email@exemplo.com" />
                    @if (createForm.controls.email.invalid && createForm.controls.email.touched) {
                        <small class="text-red-500">E-mail inválido</small>
                    }
                </div>
                <div>
                    <label for="c-role" class="block font-medium mb-2">Cargo</label>
                    <p-select id="c-role" formControlName="role" [options]="roleSelectOptions" optionLabel="label" optionValue="value" class="w-full" placeholder="Selecione o cargo" appendTo="body" />
                    @if (createForm.controls.role.invalid && createForm.controls.role.touched) {
                        <small class="text-red-500">Cargo é obrigatório</small>
                    }
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeCreateDialog()" [disabled]="savingCreate()" />
                <p-button label="Criar" icon="pi pi-check" [loading]="savingCreate()" (onClick)="onSaveCreate()" />
            </ng-template>
        </p-dialog>

        <!-- Dialog: Editar usuário -->
        <p-dialog header="Editar usuário" [(visible)]="editVisible" [modal]="true" [style]="{ width: '32rem' }" [draggable]="false" [resizable]="false" (onHide)="closeEditDialog()">
            <form [formGroup]="editForm" (ngSubmit)="onSaveEdit()" class="flex flex-col gap-4 pt-2">
                <div>
                    <label for="e-name" class="block font-medium mb-2">Nome completo</label>
                    <input pInputText id="e-name" formControlName="fullName" class="w-full" placeholder="Nome do usuário" />
                    @if (editForm.controls.fullName.invalid && editForm.controls.fullName.touched) {
                        <small class="text-red-500">{{ nameError(editForm.controls.fullName) }}</small>
                    }
                </div>
                <div>
                    <label for="e-email" class="block font-medium mb-2">E-mail</label>
                    <input pInputText id="e-email" formControlName="email" type="email" class="w-full" placeholder="email@exemplo.com" />
                    @if (editForm.controls.email.invalid && editForm.controls.email.touched) {
                        <small class="text-red-500">E-mail inválido</small>
                    }
                </div>
                <div>
                    <label for="e-role" class="block font-medium mb-2">Cargo</label>
                    <p-select id="e-role" formControlName="role" [options]="roleSelectOptions" optionLabel="label" optionValue="value" class="w-full" placeholder="Selecione o cargo" appendTo="body" />
                    @if (editForm.controls.role.invalid && editForm.controls.role.touched) {
                        <small class="text-red-500">Cargo é obrigatório</small>
                    }
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeEditDialog()" [disabled]="savingEdit()" />
                <p-button label="Salvar" icon="pi pi-check" [loading]="savingEdit()" (onClick)="onSaveEdit()" />
            </ng-template>
        </p-dialog>
    `
})
export class Usuarios implements OnInit {
    private readonly service = inject(AdminUserService);
    private readonly messageService = inject(MessageService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly appRef = inject(ApplicationRef);
    private readonly fb = inject(FormBuilder);

    readonly users = signal<UserSummary[]>([]);
    readonly totalElements = signal(0);
    readonly loading = signal(false);
    readonly detailLoading = signal(false);
    readonly savingCreate = signal(false);
    readonly savingEdit = signal(false);
    readonly selectedUser = signal<AdminUserDetail | null>(null);
    readonly detailsVisible = signal(false);
    readonly createVisible = signal(false);
    readonly editVisible = signal(false);
    readonly sortField = signal('createdAt');
    readonly sortOrder = signal(-1);

    pageSize = 20;
    first = 0;
    private currentPage = 0;
    private editingUserId = '';

    readonly statusOptions = [
        { label: 'Todos', value: null },
        { label: 'Ativo', value: 'ACTIVE' },
        { label: 'Inativo', value: 'INACTIVE' },
        { label: 'Pendente', value: 'PENDING_VERIFICATION' }
    ];

    readonly roleOptions = [
        { label: 'Todos', value: null },
        { label: 'Administrador', value: 'ROLE_ADMIN' },
        { label: 'Cliente', value: 'ROLE_CUSTOMER' }
    ];

    readonly roleSelectOptions = [
        { label: 'Administrador', value: 'ROLE_ADMIN' },
        { label: 'Cliente', value: 'ROLE_CUSTOMER' }
    ];

    readonly filterForm = this.fb.group({
        nome: [''],
        email: [''],
        status: [null as string | null],
        role: [null as string | null]
    });

    readonly createForm = this.fb.group({
        fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]],
        email: ['', [Validators.required, Validators.email]],
        role: ['', Validators.required]
    });

    readonly editForm = this.fb.group({
        fullName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]],
        email: ['', [Validators.required, Validators.email]],
        role: ['', Validators.required]
    });

    async ngOnInit(): Promise<void> {
        await this.loadUsers();
    }

    async loadUsers(): Promise<void> {
        this.loading.set(true);

        const { nome, email, status, role } = this.filterForm.value;

        try {
            const result = await firstValueFrom(
                this.service.listUsers({
                    nome: nome ?? undefined,
                    email: email ?? undefined,
                    status: status ?? undefined,
                    role: role ?? undefined,
                    page: this.currentPage,
                    size: this.pageSize,
                    sort: this.sortField(),
                    direction: this.sortOrder() === 1 ? 'asc' : 'desc'
                })
            );

            this.users.set(result.content);
            this.totalElements.set(Number(result.totalElements));
        } catch (err) {
            this._toast('error', 'Erro ao carregar', this._errorMsg(err));
        } finally {
            this.loading.set(false);
        }
    }

    async onPage(event: TablePageEvent): Promise<void> {
        this.pageSize = event.rows ?? this.pageSize;
        this.first = event.first ?? 0;
        this.currentPage = Math.floor(this.first / this.pageSize);
        await this.loadUsers();
    }

    onSort(event: SortEvent): void {
        this.sortField.set(event.field ?? 'createdAt');
        this.sortOrder.set(event.order ?? -1);
        this.currentPage = 0;
        this.first = 0;
        this.loadUsers();
    }

    async onRowClick(user: UserSummary): Promise<void> {
        this.detailsVisible.set(true);
        this.detailLoading.set(true);
        this.selectedUser.set(null);

        try {
            const detail = await firstValueFrom(this.service.getUserById(user.id));

            this.selectedUser.set(detail);
        } catch (err) {
            this.detailsVisible.set(false);
            this._toast('error', 'Erro ao carregar detalhes', this._errorMsg(err));
        } finally {
            this.detailLoading.set(false);
        }
    }

    async applyFilters(): Promise<void> {
        this.currentPage = 0;
        this.first = 0;
        await this.loadUsers();
    }

    async clearFilters(): Promise<void> {
        this.filterForm.reset({ nome: '', email: '', status: null, role: null });
        this.currentPage = 0;
        this.first = 0;
        await this.loadUsers();
    }

    openCreateDialog(): void {
        this.createForm.reset({ fullName: '', email: '', role: '' });
        this.createVisible.set(true);
    }

    closeCreateDialog(): void {
        this.createVisible.set(false);
        this.createForm.reset();
    }

    openEditDialog(user: UserSummary | AdminUserDetail): void {
        this.editingUserId = user.id;
        this.editForm.patchValue({
            fullName: user.fullName,
            email: user.email,
            role: user.role
        });
        this.editVisible.set(true);
    }

    closeEditDialog(): void {
        this.editVisible.set(false);
        this.editForm.reset();
        this.editingUserId = '';
    }

    closeDetails(): void {
        this.detailsVisible.set(false);
        this.selectedUser.set(null);
    }

    async onSaveCreate(): Promise<void> {
        if (this.createForm.invalid) {
            this.createForm.markAllAsTouched();

            return;
        }

        this.savingCreate.set(true);

        const { fullName, email, role } = this.createForm.value;

        try {
            await firstValueFrom(this.service.createUser({ fullName: fullName!, email: email!, role: role! }));
            this.closeCreateDialog();
            this._toast('success', 'Usuário criado', 'Um e-mail de boas-vindas foi enviado.');
            await this.loadUsers();
        } catch (err) {
            this._toast('error', 'Erro ao criar usuário', this._errorMsg(err));
        } finally {
            this.savingCreate.set(false);
        }
    }

    async onSaveEdit(): Promise<void> {
        if (this.editForm.invalid) {
            this.editForm.markAllAsTouched();

            return;
        }

        this.savingEdit.set(true);

        const { fullName, email, role } = this.editForm.value;

        try {
            await firstValueFrom(this.service.updateUser(this.editingUserId, { fullName: fullName!, email: email!, role: role! }));
            this.closeEditDialog();
            this._toast('success', 'Usuário atualizado', 'As informações foram salvas com sucesso.');
            await this.loadUsers();
        } catch (err) {
            this._toast('error', 'Erro ao salvar', this._errorMsg(err));
        } finally {
            this.savingEdit.set(false);
        }
    }

    confirmToggleStatus(user: UserSummary): void {
        const nextStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        const action = nextStatus === 'INACTIVE' ? 'desativar' : 'ativar';

        this.confirmationService.confirm({
            message: `Deseja ${action} o usuário <strong>${user.fullName}</strong>?`,
            header: `${nextStatus === 'INACTIVE' ? 'Desativar' : 'Ativar'} usuário`,
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Confirmar', severity: nextStatus === 'INACTIVE' ? 'danger' : 'success' },
            accept: () => this.toggleStatus(user, nextStatus as 'ACTIVE' | 'INACTIVE')
        });
        this.appRef.tick();
    }

    async toggleStatus(user: UserSummary, status: 'ACTIVE' | 'INACTIVE'): Promise<void> {
        try {
            await firstValueFrom(this.service.toggleStatus(user.id, status));
            const label = status === 'ACTIVE' ? 'ativado' : 'desativado';

            this._toast('success', `Usuário ${label}`, `${user.fullName} foi ${label} com sucesso.`);
            await this.loadUsers();
        } catch (err) {
            this._toast('error', 'Erro ao atualizar status', this._errorMsg(err));
        }
    }

    initials(name: string): string {
        const parts = name.trim().split(/\s+/);

        return parts.length >= 2 ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase() : name.slice(0, 2).toUpperCase();
    }

    formatRole(role: string): string {
        const map: Record<string, string> = { ROLE_ADMIN: 'Admin', ROLE_CUSTOMER: 'Cliente' };

        return map[role] ?? role.replace('ROLE_', '');
    }

    statusLabel(status: string): string {
        const map: Record<string, string> = { ACTIVE: 'Ativo', INACTIVE: 'Inativo', PENDING_VERIFICATION: 'Pendente' };

        return map[status] ?? status;
    }

    statusSeverity(status: string): 'success' | 'danger' | 'warn' | 'info' {
        const map: Record<string, 'success' | 'danger' | 'warn' | 'info'> = { ACTIVE: 'success', INACTIVE: 'danger', PENDING_VERIFICATION: 'warn' };

        return map[status] ?? 'info';
    }

    nameError(ctrl: AbstractControl): string {
        if (ctrl.errors?.['required']) return 'Nome é obrigatório';
        if (ctrl.errors?.['minlength']) return 'Mínimo 3 caracteres';
        if (ctrl.errors?.['maxlength']) return 'Máximo 255 caracteres';

        return 'Nome inválido';
    }

    private _toast(severity: 'success' | 'error' | 'warn', summary: string, detail: string): void {
        this.messageService.add({ severity, summary, detail, life: 6000 });
        this.appRef.tick();
    }

    private _errorMsg(err: unknown): string {
        if (err instanceof HttpErrorResponse) {
            return err.status === 0 ? 'Sem conexão com o servidor.' : (err.error?.message ?? 'Erro inesperado.');
        }

        return 'Erro inesperado.';
    }
}
