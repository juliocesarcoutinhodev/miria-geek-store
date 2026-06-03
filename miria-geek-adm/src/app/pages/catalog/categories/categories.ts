import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule, TablePageEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { TooltipModule } from 'primeng/tooltip';
import { CategoryService } from '../../../core/catalog/category.service';
import { Category } from '../../../core/catalog/category.model';

interface SortEvent {
    field?: string | null;
    order?: number | null;
}

@Component({
    selector: 'app-categories',
    standalone: true,
    providers: [ConfirmationService],
    imports: [
        ReactiveFormsModule,
        DatePipe,
        ButtonModule,
        CardModule,
        ConfirmDialogModule,
        DialogModule,
        IconFieldModule,
        InputIconModule,
        InputTextModule,
        SelectModule,
        SkeletonModule,
        TableModule,
        TagModule,
        TextareaModule,
        ToggleSwitchModule,
        TooltipModule
    ],
    template: `
        <p-confirm-dialog />

        <p-card>
            <ng-template #header>
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 px-6 pt-6 pb-0">
                    <div>
                        <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">Categorias</h2>
                        <p class="text-muted-color text-sm mt-1 mb-0">Gerencie as categorias de produtos do catálogo</p>
                    </div>
                    <p-button icon="pi pi-plus" label="Nova categoria" (onClick)="openCreateDialog()" />
                </div>
            </ng-template>

            <form [formGroup]="filterForm" (ngSubmit)="applyFilters()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 mb-4">
                <p-iconfield>
                    <p-inputicon class="pi pi-tags" />
                    <input pInputText formControlName="name" placeholder="Filtrar por nome" (keyup.enter)="applyFilters()" class="w-full" />
                </p-iconfield>

                <p-select formControlName="active" [options]="activeOptions" optionLabel="label" optionValue="value" placeholder="Status" class="w-full" />

                <div class="flex gap-2">
                    <p-button type="submit" icon="pi pi-search" label="Filtrar" [loading]="loading()" />
                    <p-button type="button" icon="pi pi-times" label="Limpar" severity="secondary" [outlined]="true" (onClick)="clearFilters()" [disabled]="loading()" />
                </div>
            </form>

            <p-table
                [value]="categories()"
                dataKey="id"
                [lazy]="true"
                [paginator]="true"
                [rows]="pageSize"
                [totalRecords]="totalElements()"
                [loading]="loading()"
                [rowHover]="true"
                styleClass="p-datatable-striped"
                paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
                currentPageReportTemplate="Exibindo {first} a {last} de {totalRecords} categorias"
                [rowsPerPageOptions]="[10, 20, 50]"
                [sortField]="sortField()"
                [sortOrder]="sortOrder()"
                (onPage)="onPage($event)"
                (onSort)="onSort($event)"
                [tableStyle]="{ 'min-width': '50rem' }"
            >
                <ng-template #empty>
                    <div class="text-center py-8 text-muted-color">
                        <i class="pi pi-tags text-4xl mb-3 block"></i>
                        Nenhuma categoria encontrada.
                    </div>
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th pSortableColumn="name" style="min-width: 12rem">Nome <p-sortIcon field="name" /></th>
                        <th style="min-width: 10rem">Slug</th>
                        <th style="min-width: 14rem">Descrição</th>
                        <th style="min-width: 7rem; text-align: center">Produtos</th>
                        <th style="min-width: 8rem">Status</th>
                        <th pSortableColumn="createdAt" style="min-width: 10rem">Criado em <p-sortIcon field="createdAt" /></th>
                        <th style="width: 8rem; text-align: center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-category>
                    <tr>
                        <td class="font-medium">{{ category.name }}</td>
                        <td class="text-muted-color text-sm font-mono">{{ category.slug }}</td>
                        <td class="text-muted-color">{{ category.description }}</td>
                        <td class="text-center">{{ category.totalProducts }}</td>
                        <td>
                            <p-tag [value]="category.active ? 'Ativa' : 'Inativa'" [severity]="category.active ? 'success' : 'danger'" />
                        </td>
                        <td class="text-muted-color">{{ category.createdAt | date: 'dd/MM/yyyy' }}</td>
                        <td>
                            <div class="flex items-center justify-center gap-1">
                                <p-button icon="pi pi-pencil" [rounded]="true" [text]="true" severity="warn" pTooltip="Editar" tooltipPosition="top" (onClick)="openEditDialog(category)" />
                                <p-button
                                    [icon]="category.active ? 'pi pi-eye-slash' : 'pi pi-eye'"
                                    [rounded]="true"
                                    [text]="true"
                                    [severity]="category.active ? 'danger' : 'success'"
                                    [pTooltip]="category.active ? 'Inativar' : 'Ativar'"
                                    tooltipPosition="top"
                                    (onClick)="confirmToggleStatus(category)"
                                />
                                <p-button icon="pi pi-trash" [rounded]="true" [text]="true" severity="danger" pTooltip="Excluir" tooltipPosition="top" [disabled]="category.totalProducts > 0" (onClick)="confirmDelete(category)" />
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>
        </p-card>

        <!-- Create dialog -->
        <p-dialog header="Nova categoria" [(visible)]="createVisible" [modal]="true" [style]="{ width: '32rem' }" [draggable]="false" [resizable]="false" (onHide)="closeCreateDialog()">
            <form [formGroup]="createForm" (ngSubmit)="saveCreate()" class="flex flex-col gap-4 pt-2">
                <div>
                    <label for="c-name" class="block font-medium mb-2">Nome <span class="text-red-500">*</span></label>
                    <input pInputText id="c-name" formControlName="name" class="w-full" placeholder="Ex: Funko Pop" />
                    @if (createForm.controls.name.invalid && createForm.controls.name.touched) {
                        <small class="text-red-500">{{ nameError(createForm.controls.name) }}</small>
                    }
                </div>
                <div>
                    <label for="c-description" class="block font-medium mb-2">Descrição <span class="text-red-500">*</span></label>
                    <textarea pTextarea id="c-description" formControlName="description" rows="3" class="w-full" placeholder="Descreva o que essa categoria agrupa"></textarea>
                    @if (createForm.controls.description.invalid && createForm.controls.description.touched) {
                        <small class="text-red-500">Descrição é obrigatória</small>
                    }
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeCreateDialog()" [disabled]="savingCreate()" />
                <p-button label="Criar" icon="pi pi-check" [loading]="savingCreate()" (onClick)="saveCreate()" />
            </ng-template>
        </p-dialog>

        <!-- Edit dialog -->
        <p-dialog header="Editar categoria" [(visible)]="editVisible" [modal]="true" [style]="{ width: '32rem' }" [draggable]="false" [resizable]="false" (onHide)="closeEditDialog()">
            <form [formGroup]="editForm" (ngSubmit)="saveEdit()" class="flex flex-col gap-4 pt-2">
                <div>
                    <label for="e-name" class="block font-medium mb-2">Nome <span class="text-red-500">*</span></label>
                    <input pInputText id="e-name" formControlName="name" class="w-full" placeholder="Ex: Funko Pop" />
                    @if (editForm.controls.name.invalid && editForm.controls.name.touched) {
                        <small class="text-red-500">{{ nameError(editForm.controls.name) }}</small>
                    }
                </div>
                <div>
                    <label for="e-description" class="block font-medium mb-2">Descrição <span class="text-red-500">*</span></label>
                    <textarea pTextarea id="e-description" formControlName="description" rows="3" class="w-full" placeholder="Descreva o que essa categoria agrupa"></textarea>
                    @if (editForm.controls.description.invalid && editForm.controls.description.touched) {
                        <small class="text-red-500">Descrição é obrigatória</small>
                    }
                </div>
                <div class="flex items-center gap-3">
                    <p-toggleswitch formControlName="active" inputId="e-active" />
                    <label for="e-active" class="font-medium cursor-pointer">Categoria ativa</label>
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeEditDialog()" [disabled]="savingEdit()" />
                <p-button label="Salvar" icon="pi pi-check" [loading]="savingEdit()" (onClick)="saveEdit()" />
            </ng-template>
        </p-dialog>
    `
})
export class Categories implements OnInit {
    private readonly service = inject(CategoryService);
    private readonly messageService = inject(MessageService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly appRef = inject(ApplicationRef);
    private readonly fb = inject(FormBuilder);

    readonly categories = signal<Category[]>([]);
    readonly totalElements = signal(0);
    readonly loading = signal(false);
    readonly savingCreate = signal(false);
    readonly savingEdit = signal(false);
    readonly createVisible = signal(false);
    readonly editVisible = signal(false);
    readonly sortField = signal('name');
    readonly sortOrder = signal(1);

    readonly pageSize = 20;
    private currentPage = 0;
    private editingId = '';

    readonly activeOptions = [
        { label: 'Todos', value: null },
        { label: 'Ativas', value: true },
        { label: 'Inativas', value: false }
    ];

    readonly filterForm = this.fb.group({
        name: [''],
        active: [null as boolean | null]
    });

    readonly createForm = this.fb.group({
        name: ['', [Validators.required, Validators.maxLength(100)]],
        description: ['', [Validators.required]]
    });

    readonly editForm = this.fb.group({
        name: ['', [Validators.required, Validators.maxLength(100)]],
        description: ['', [Validators.required]],
        active: [true, Validators.required]
    });

    async ngOnInit(): Promise<void> {
        await this.loadCategories();
    }

    async loadCategories(): Promise<void> {
        this.loading.set(true);
        const { name, active } = this.filterForm.value;

        try {
            const result = await firstValueFrom(
                this.service.list({
                    name: name ?? undefined,
                    active: active ?? undefined,
                    page: this.currentPage,
                    size: this.pageSize,
                    sort: this.sortField(),
                    direction: this.sortOrder() === 1 ? 'asc' : 'desc'
                })
            );

            this.categories.set(result.content);
            this.totalElements.set(Number(result.totalElements));
        } catch (err) {
            this.toast('error', 'Erro ao carregar', this.errorMessage(err));
        } finally {
            this.loading.set(false);
        }
    }

    async onPage(event: TablePageEvent): Promise<void> {
        this.currentPage = Math.floor((event.first ?? 0) / this.pageSize);
        await this.loadCategories();
    }

    onSort(event: SortEvent): void {
        this.sortField.set(event.field ?? 'name');
        this.sortOrder.set(event.order ?? 1);
        this.currentPage = 0;
        this.loadCategories();
    }

    async applyFilters(): Promise<void> {
        this.currentPage = 0;
        await this.loadCategories();
    }

    async clearFilters(): Promise<void> {
        this.filterForm.reset({ name: '', active: null });
        this.currentPage = 0;
        await this.loadCategories();
    }

    openCreateDialog(): void {
        this.createForm.reset({ name: '', description: '' });
        this.createVisible.set(true);
    }

    closeCreateDialog(): void {
        this.createVisible.set(false);
        this.createForm.reset();
    }

    openEditDialog(category: Category): void {
        this.editingId = category.id;
        this.editForm.patchValue({
            name: category.name,
            description: category.description,
            active: category.active
        });
        this.editVisible.set(true);
    }

    closeEditDialog(): void {
        this.editVisible.set(false);
        this.editForm.reset();
        this.editingId = '';
    }

    async saveCreate(): Promise<void> {
        if (this.createForm.invalid) {
            this.createForm.markAllAsTouched();

            return;
        }

        this.savingCreate.set(true);
        const { name, description } = this.createForm.value;

        try {
            await firstValueFrom(this.service.create({ name: name!, description: description! }));
            this.closeCreateDialog();
            this.toast('success', 'Categoria criada', `"${name}" foi criada com sucesso.`);
            await this.loadCategories();
        } catch (err) {
            this.toast('error', 'Erro ao criar categoria', this.errorMessage(err));
        } finally {
            this.savingCreate.set(false);
        }
    }

    async saveEdit(): Promise<void> {
        if (this.editForm.invalid) {
            this.editForm.markAllAsTouched();

            return;
        }

        this.savingEdit.set(true);
        const { name, description, active } = this.editForm.value;

        try {
            await firstValueFrom(this.service.update(this.editingId, { name: name!, description: description!, active: active! }));
            this.closeEditDialog();
            this.toast('success', 'Categoria atualizada', 'As informações foram salvas com sucesso.');
            await this.loadCategories();
        } catch (err) {
            this.toast('error', 'Erro ao salvar', this.errorMessage(err));
        } finally {
            this.savingEdit.set(false);
        }
    }

    confirmToggleStatus(category: Category): void {
        const nextActive = !category.active;
        const action = nextActive ? 'ativar' : 'inativar';

        this.confirmationService.confirm({
            message: `Deseja ${action} a categoria <strong>${category.name}</strong>?`,
            header: `${nextActive ? 'Ativar' : 'Inativar'} categoria`,
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Confirmar', severity: nextActive ? 'success' : 'danger' },
            accept: () => this.toggleStatus(category, nextActive)
        });
        this.appRef.tick();
    }

    async toggleStatus(category: Category, active: boolean): Promise<void> {
        try {
            await firstValueFrom(this.service.update(category.id, { name: category.name, description: category.description, active }));
            const label = active ? 'ativada' : 'inativada';

            this.toast('success', `Categoria ${label}`, `"${category.name}" foi ${label} com sucesso.`);
            await this.loadCategories();
        } catch (err) {
            this.toast('error', 'Erro ao atualizar status', this.errorMessage(err));
        }
    }

    confirmDelete(category: Category): void {
        this.confirmationService.confirm({
            message: `Deseja excluir a categoria <strong>${category.name}</strong>? Esta ação não pode ser desfeita.`,
            header: 'Excluir categoria',
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Excluir', severity: 'danger' },
            accept: () => this.deleteCategory(category)
        });
        this.appRef.tick();
    }

    async deleteCategory(category: Category): Promise<void> {
        try {
            await firstValueFrom(this.service.delete(category.id));
            this.toast('success', 'Categoria excluída', `"${category.name}" foi removida com sucesso.`);
            await this.loadCategories();
        } catch (err) {
            this.toast('error', 'Erro ao excluir', this.errorMessage(err));
        }
    }

    nameError(ctrl: AbstractControl): string {
        if (ctrl.errors?.['required']) return 'Nome é obrigatório';
        if (ctrl.errors?.['maxlength']) return 'Máximo 100 caracteres';

        return 'Nome inválido';
    }

    private toast(severity: 'success' | 'error' | 'warn', summary: string, detail: string): void {
        this.messageService.add({ severity, summary, detail, life: 6000 });
        this.appRef.tick();
    }

    private errorMessage(err: unknown): string {
        if (err instanceof HttpErrorResponse) {
            return err.status === 0 ? 'Sem conexão com o servidor.' : (err.error?.message ?? 'Erro inesperado.');
        }

        return 'Erro inesperado.';
    }
}
