import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DividerModule } from 'primeng/divider';
import { DrawerModule } from 'primeng/drawer';
import { IconFieldModule } from 'primeng/iconfield';
import { ImageModule } from 'primeng/image';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule, TablePageEvent } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ProductService } from '../../../core/catalog/product.service';
import { ProductDetail, ProductImageInfo, ProductSummary } from '../../../core/catalog/product.model';

@Component({
    selector: 'app-products',
    standalone: true,
    providers: [ConfirmationService],
    imports: [
        ReactiveFormsModule,
        DatePipe,
        ButtonModule,
        CardModule,
        ConfirmDialogModule,
        DividerModule,
        DrawerModule,
        IconFieldModule,
        ImageModule,
        InputIconModule,
        InputTextModule,
        SelectModule,
        SkeletonModule,
        TableModule,
        TagModule,
        TooltipModule
    ],
    template: `
        <p-confirm-dialog />

        <p-card>
            <ng-template #header>
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 px-6 pt-6 pb-0">
                    <div>
                        <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">Produtos</h2>
                        <p class="text-muted-color text-sm mt-1 mb-0">Gerencie os produtos do catálogo</p>
                    </div>
                </div>
            </ng-template>

            <form [formGroup]="filterForm" (ngSubmit)="applyFilters()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3 mb-4">
                <p-iconfield>
                    <p-inputicon class="pi pi-box" />
                    <input pInputText formControlName="nome" placeholder="Filtrar por nome" (keyup.enter)="applyFilters()" class="w-full" />
                </p-iconfield>

                <p-select formControlName="status" [options]="statusOptions" optionLabel="label" optionValue="value" placeholder="Status" class="w-full" />

                <p-select formControlName="destaque" [options]="destaqueOptions" optionLabel="label" optionValue="value" placeholder="Destaque" class="w-full" />

                <p-select formControlName="sort" [options]="sortOptions" optionLabel="label" optionValue="value" placeholder="Ordenar" class="w-full" />

                <div class="flex gap-2">
                    <p-button type="submit" icon="pi pi-search" label="Filtrar" [loading]="loading()" />
                    <p-button type="button" icon="pi pi-times" label="Limpar" severity="secondary" [outlined]="true" (onClick)="clearFilters()" [disabled]="loading()" />
                </div>
            </form>

            <p-table
                [value]="products()"
                dataKey="id"
                [lazy]="true"
                [paginator]="true"
                [rows]="pageSize"
                [totalRecords]="totalElements()"
                [loading]="loading()"
                [rowHover]="true"
                styleClass="p-datatable-striped"
                paginatorTemplate="RowsPerPageDropdown FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink"
                currentPageReportTemplate="Exibindo {first} a {last} de {totalRecords} produtos"
                [rowsPerPageOptions]="[10, 20, 50]"
                (onPage)="onPage($event)"
                [tableStyle]="{ 'min-width': '60rem' }"
            >
                <ng-template #empty>
                    <div class="text-center py-8 text-muted-color">
                        <i class="pi pi-box text-4xl mb-3 block"></i>
                        Nenhum produto encontrado.
                    </div>
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th style="min-width: 18rem">Produto</th>
                        <th style="min-width: 10rem">Categoria</th>
                        <th style="min-width: 8rem">Status</th>
                        <th style="min-width: 7rem; text-align: center">Destaque</th>
                        <th style="min-width: 10rem; text-align: center">Variantes / Estoque</th>
                        <th style="min-width: 7rem; text-align: center">Imagens</th>
                        <th style="min-width: 10rem">Criado em</th>
                        <th style="width: 6rem; text-align: center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-product>
                    <tr class="cursor-pointer" (click)="onRowClick(product)">
                        <td>
                            <p class="font-medium m-0">{{ product.name }}</p>
                            <p class="text-muted-color text-xs font-mono m-0 mt-1">{{ product.slug }}</p>
                        </td>
                        <td class="text-muted-color">{{ product.category.name }}</td>
                        <td>
                            <p-tag [value]="product.status === 'ACTIVE' ? 'Ativo' : 'Inativo'" [severity]="product.status === 'ACTIVE' ? 'success' : 'danger'" />
                        </td>
                        <td class="text-center">
                            @if (product.featured) {
                                <i class="pi pi-star-fill text-yellow-500 text-lg"></i>
                            } @else {
                                <i class="pi pi-star text-muted-color text-lg"></i>
                            }
                        </td>
                        <td class="text-center">
                            <div class="flex flex-col items-center gap-0">
                                <span class="font-medium text-sm">{{ product.totalVariants }} var.</span>
                                <span class="text-xs text-muted-color">{{ product.totalStock }} em estoque</span>
                            </div>
                        </td>
                        <td class="text-center">
                            <div class="flex items-center justify-center gap-1 text-muted-color">
                                <i class="pi pi-images"></i>
                                <span class="text-sm">{{ product.totalImages }}</span>
                            </div>
                        </td>
                        <td class="text-muted-color">{{ product.createdAt | date: 'dd/MM/yyyy' }}</td>
                        <td>
                            <div class="flex items-center justify-center">
                                <p-button
                                    [icon]="product.status === 'ACTIVE' ? 'pi pi-eye-slash' : 'pi pi-eye'"
                                    [rounded]="true"
                                    [text]="true"
                                    [severity]="product.status === 'ACTIVE' ? 'danger' : 'success'"
                                    [pTooltip]="product.status === 'ACTIVE' ? 'Inativar' : 'Ativar'"
                                    tooltipPosition="top"
                                    (onClick)="$event.stopPropagation(); confirmToggleStatus(product)"
                                />
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>
        </p-card>

        <!-- Detail drawer -->
        <p-drawer [(visible)]="drawerVisible" position="right" [style]="{ width: '100%', 'max-width': '46rem' }" [modal]="true" (onHide)="closeDrawer()">
            <ng-template #header>
                <div class="flex items-center gap-2 flex-wrap min-w-0">
                    @if (detailLoading()) {
                        <p-skeleton width="14rem" height="1.5rem" />
                    } @else if (selectedDetail()) {
                        <span class="font-semibold text-lg truncate">{{ selectedDetail()!.name }}</span>
                        <p-tag [value]="selectedDetail()!.status === 'ACTIVE' ? 'Ativo' : 'Inativo'" [severity]="selectedDetail()!.status === 'ACTIVE' ? 'success' : 'danger'" />
                        @if (selectedDetail()!.featured) {
                            <p-tag value="Destaque" severity="warn" icon="pi pi-star-fill" />
                        }
                    }
                </div>
            </ng-template>

            @if (detailLoading()) {
                <div class="flex flex-col gap-4 pt-2">
                    @for (_ of [1, 2, 3, 4, 5]; track $index) {
                        <p-skeleton height="1.75rem" />
                    }
                    <p-skeleton height="5rem" />
                    <p-skeleton height="1.75rem" />
                    <div class="flex gap-3">
                        @for (_ of [1, 2, 3]; track $index) {
                            <p-skeleton width="6rem" height="6rem" />
                        }
                    </div>
                </div>
            } @else if (selectedDetail()) {
                <div class="flex flex-col gap-5">
                    <!-- Info section -->
                    <section>
                        <p class="text-xs font-semibold text-muted-color uppercase tracking-wider mb-3">Informações</p>
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <p class="text-sm text-muted-color mb-1">Categoria</p>
                                <p class="font-medium m-0">{{ selectedDetail()!.category.name }}</p>
                            </div>
                            <div>
                                <p class="text-sm text-muted-color mb-1">Criado em</p>
                                <p class="font-medium m-0">{{ selectedDetail()!.createdAt | date: 'dd/MM/yyyy' }}</p>
                            </div>
                            <div class="col-span-2">
                                <p class="text-sm text-muted-color mb-1">Slug</p>
                                <p class="font-mono text-sm bg-surface-100 dark:bg-surface-800 rounded px-2 py-1 m-0 inline-block">{{ selectedDetail()!.slug }}</p>
                            </div>
                            @if (selectedDetail()!.description) {
                                <div class="col-span-2">
                                    <p class="text-sm text-muted-color mb-1">Descrição</p>
                                    <p class="m-0 text-sm leading-relaxed">{{ selectedDetail()!.description }}</p>
                                </div>
                            }
                        </div>
                    </section>

                    <p-divider />

                    <!-- Images section -->
                    <section>
                        <p class="text-xs font-semibold text-muted-color uppercase tracking-wider mb-3">
                            Imagens
                            <span class="font-normal normal-case ml-1 text-surface-500">({{ selectedDetail()!.images.length }})</span>
                        </p>
                        @if (sortedImages().length > 0) {
                            <div class="flex flex-wrap gap-3">
                                @for (img of sortedImages(); track img.id) {
                                    <div class="relative">
                                        <p-image [src]="img.url" [preview]="true" imageClass="w-24 h-24 object-cover rounded-lg border border-surface-200 dark:border-surface-700 block" [imageStyle]="{ cursor: 'zoom-in' }" />
                                        @if (img.principal) {
                                            <span class="absolute -top-1.5 -right-1.5 bg-primary text-white rounded-full w-5 h-5 flex items-center justify-center" pTooltip="Imagem principal" tooltipPosition="top">
                                                <i class="pi pi-star-fill text-xs"></i>
                                            </span>
                                        }
                                    </div>
                                }
                            </div>
                        } @else {
                            <p class="text-muted-color text-sm">Nenhuma imagem cadastrada.</p>
                        }
                    </section>

                    <p-divider />

                    <!-- Variants section -->
                    <section>
                        <p class="text-xs font-semibold text-muted-color uppercase tracking-wider mb-3">
                            Variantes
                            <span class="font-normal normal-case ml-1 text-surface-500">({{ selectedDetail()!.variants.length }})</span>
                        </p>
                        @if (selectedDetail()!.variants.length > 0) {
                            <p-table [value]="selectedDetail()!.variants" styleClass="p-datatable-sm p-datatable-striped" [tableStyle]="{ 'min-width': '100%' }">
                                <ng-template #header>
                                    <tr>
                                        <th>Atributo</th>
                                        <th style="text-align: right">Preço</th>
                                        <th style="text-align: right">Estoque</th>
                                        <th>SKU</th>
                                        <th>Status</th>
                                    </tr>
                                </ng-template>
                                <ng-template #body let-variant>
                                    <tr>
                                        <td>
                                            <span class="font-medium">{{ variant.attributeName }}</span>
                                            <span class="text-muted-color"> / {{ variant.attributeValue }}</span>
                                        </td>
                                        <td style="text-align: right" class="font-medium">{{ formatPrice(variant.price) }}</td>
                                        <td style="text-align: right">
                                            <span [class]="variant.stock === 0 ? 'text-red-500 font-medium' : ''">{{ variant.stock }}</span>
                                        </td>
                                        <td class="font-mono text-xs text-muted-color">{{ variant.sku }}</td>
                                        <td>
                                            <p-tag [value]="variant.active ? 'Ativa' : 'Inativa'" [severity]="variant.active ? 'success' : 'danger'" />
                                        </td>
                                    </tr>
                                </ng-template>
                            </p-table>
                        } @else {
                            <p class="text-muted-color text-sm">Nenhuma variante cadastrada.</p>
                        }
                    </section>
                </div>
            }

            <ng-template #footer>
                @if (selectedDetail() && !detailLoading()) {
                    <p-button
                        [label]="selectedDetail()!.status === 'ACTIVE' ? 'Inativar produto' : 'Ativar produto'"
                        [icon]="selectedDetail()!.status === 'ACTIVE' ? 'pi pi-eye-slash' : 'pi pi-eye'"
                        [severity]="selectedDetail()!.status === 'ACTIVE' ? 'danger' : 'success'"
                        [outlined]="true"
                        (onClick)="confirmToggleStatusFromDetail()"
                    />
                }
            </ng-template>
        </p-drawer>
    `
})
export class Products implements OnInit {
    private readonly service = inject(ProductService);
    private readonly messageService = inject(MessageService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly appRef = inject(ApplicationRef);
    private readonly fb = inject(FormBuilder);

    readonly products = signal<ProductSummary[]>([]);
    readonly totalElements = signal(0);
    readonly loading = signal(false);
    readonly drawerVisible = signal(false);
    readonly detailLoading = signal(false);
    readonly selectedDetail = signal<ProductDetail | null>(null);

    readonly sortedImages = computed<ProductImageInfo[]>(() =>
        [...(this.selectedDetail()?.images ?? [])].sort((a, b) => {
            if (a.principal !== b.principal) return a.principal ? -1 : 1;

            return a.imageOrder - b.imageOrder;
        })
    );

    readonly pageSize = 20;
    private currentPage = 0;

    readonly statusOptions = [
        { label: 'Todos', value: null },
        { label: 'Ativo', value: 'ACTIVE' },
        { label: 'Inativo', value: 'INACTIVE' }
    ];

    readonly destaqueOptions = [
        { label: 'Todos', value: null },
        { label: 'Em destaque', value: true },
        { label: 'Sem destaque', value: false }
    ];

    readonly sortOptions = [
        { label: 'Mais recente', value: 'mais_recente' },
        { label: 'Nome A→Z', value: 'nome_asc' },
        { label: 'Nome Z→A', value: 'nome_desc' },
        { label: 'Status', value: 'status' },
        { label: 'Destaque', value: 'destaque' }
    ];

    readonly filterForm = this.fb.group({
        nome: [''],
        status: [null as string | null],
        destaque: [null as boolean | null],
        sort: ['mais_recente']
    });

    async ngOnInit(): Promise<void> {
        await this.loadProducts();
    }

    async loadProducts(): Promise<void> {
        this.loading.set(true);
        const { nome, status, destaque, sort } = this.filterForm.value;

        try {
            const result = await firstValueFrom(
                this.service.list({
                    nome: nome ?? undefined,
                    status: status ?? undefined,
                    destaque: destaque ?? undefined,
                    page: this.currentPage,
                    size: this.pageSize,
                    sort: sort ?? 'mais_recente'
                })
            );

            this.products.set(result.content);
            this.totalElements.set(Number(result.totalElements));
        } catch (err) {
            this.toast('error', 'Erro ao carregar', this.errorMessage(err));
        } finally {
            this.loading.set(false);
        }
    }

    async onPage(event: TablePageEvent): Promise<void> {
        this.currentPage = Math.floor((event.first ?? 0) / this.pageSize);
        await this.loadProducts();
    }

    async applyFilters(): Promise<void> {
        this.currentPage = 0;
        await this.loadProducts();
    }

    async clearFilters(): Promise<void> {
        this.filterForm.reset({ nome: '', status: null, destaque: null, sort: 'mais_recente' });
        this.currentPage = 0;
        await this.loadProducts();
    }

    async onRowClick(product: ProductSummary): Promise<void> {
        this.drawerVisible.set(true);
        this.detailLoading.set(true);
        this.selectedDetail.set(null);

        try {
            const detail = await firstValueFrom(this.service.getById(product.id));

            this.selectedDetail.set(detail);
        } catch (err) {
            this.drawerVisible.set(false);
            this.toast('error', 'Erro ao carregar detalhes', this.errorMessage(err));
        } finally {
            this.detailLoading.set(false);
        }
    }

    closeDrawer(): void {
        this.drawerVisible.set(false);
        this.selectedDetail.set(null);
    }

    confirmToggleStatus(product: ProductSummary): void {
        const nextStatus = product.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        const action = nextStatus === 'ACTIVE' ? 'ativar' : 'inativar';

        this.confirmationService.confirm({
            message: `Deseja ${action} o produto <strong>${product.name}</strong>?`,
            header: `${nextStatus === 'ACTIVE' ? 'Ativar' : 'Inativar'} produto`,
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Confirmar', severity: nextStatus === 'INACTIVE' ? 'danger' : 'success' },
            accept: () => this.toggleStatus(product.id, product.name, nextStatus)
        });
        this.appRef.tick();
    }

    confirmToggleStatusFromDetail(): void {
        const detail = this.selectedDetail();

        if (!detail) return;

        const nextStatus = detail.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        const action = nextStatus === 'ACTIVE' ? 'ativar' : 'inativar';

        this.confirmationService.confirm({
            message: `Deseja ${action} o produto <strong>${detail.name}</strong>?`,
            header: `${nextStatus === 'ACTIVE' ? 'Ativar' : 'Inativar'} produto`,
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Confirmar', severity: nextStatus === 'INACTIVE' ? 'danger' : 'success' },
            accept: () => this.toggleStatus(detail.id, detail.name, nextStatus)
        });
        this.appRef.tick();
    }

    async toggleStatus(id: string, name: string, status: 'ACTIVE' | 'INACTIVE'): Promise<void> {
        try {
            await firstValueFrom(this.service.updateStatus(id, status));

            const label = status === 'ACTIVE' ? 'ativado' : 'inativado';

            this.toast('success', `Produto ${label}`, `"${name}" foi ${label} com sucesso.`);

            const detail = this.selectedDetail();

            if (detail?.id === id) {
                this.selectedDetail.set({ ...detail, status });
            }

            await this.loadProducts();
        } catch (err) {
            this.toast('error', 'Erro ao atualizar status', this.errorMessage(err));
        }
    }

    formatPrice(price: number): string {
        return price.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
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
