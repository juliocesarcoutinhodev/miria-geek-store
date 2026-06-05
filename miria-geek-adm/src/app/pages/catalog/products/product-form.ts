import { HttpErrorResponse } from '@angular/common/http';
import { ApplicationRef, Component, OnInit, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { DividerModule } from 'primeng/divider';
import { IconFieldModule } from 'primeng/iconfield';
import { ImageModule } from 'primeng/image';
import { InputIconModule } from 'primeng/inputicon';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { TooltipModule } from 'primeng/tooltip';
import { ProductService } from '../../../core/catalog/product.service';
import { CategoryService } from '../../../core/catalog/category.service';
import { AdjustStockRequest, CreateProductRequest, CreateVariantRequest, ProductDetail, ProductImageInfo, ProductVariantInfo, UpdateProductRequest, UpdateVariantRequest } from '../../../core/catalog/product.model';
import { Category } from '../../../core/catalog/category.model';

@Component({
    selector: 'app-product-form',
    standalone: true,
    providers: [ConfirmationService],
    imports: [
        ReactiveFormsModule,
        RouterModule,
        ButtonModule,
        CardModule,
        ConfirmDialogModule,
        DialogModule,
        DividerModule,
        IconFieldModule,
        ImageModule,
        InputIconModule,
        InputNumberModule,
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

        <!-- Hidden file input for image upload -->
        @if (isEditMode()) {
            <input #fileInput type="file" accept="image/jpeg,image/png,image/webp" class="hidden" (change)="onFileSelected($event)" />
        }

        <div class="flex flex-col gap-5">
            <!-- Page header -->
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <div>
                    <h2 class="text-xl font-semibold text-surface-900 dark:text-surface-0 m-0">
                        {{ isEditMode() ? 'Editar produto' : 'Novo produto' }}
                    </h2>
                    @if (isEditMode() && product()) {
                        <div class="flex items-center gap-2 mt-1">
                            <p-tag [value]="product()!.status === 'ACTIVE' ? 'Ativo' : 'Inativo'" [severity]="product()!.status === 'ACTIVE' ? 'success' : 'danger'" />
                            @if (product()!.featured) {
                                <p-tag value="Destaque" severity="warn" icon="pi pi-star-fill" />
                            }
                            <span class="text-muted-color text-sm font-mono">{{ product()!.slug }}</span>
                        </div>
                    }
                </div>
                <p-button icon="pi pi-arrow-left" label="Voltar" severity="secondary" [outlined]="true" (onClick)="goBack()" />
            </div>

            @if (loadingProduct()) {
                <!-- Skeleton -->
                <div class="border border-surface-200 dark:border-surface-700 rounded-xl p-5 flex flex-col gap-4">
                    @for (_ of [1, 2, 3, 4, 5]; track $index) {
                        <p-skeleton height="2.5rem" />
                    }
                </div>
            } @else {
                <!-- ── Section 1: Informações básicas ──────────────────────── -->
                <section class="border border-surface-200 dark:border-surface-700 rounded-xl p-5">
                    <div class="flex items-center justify-between mb-4">
                        <p class="font-semibold text-lg m-0">{{ isEditMode() ? '1. Informações básicas' : 'Dados do produto' }}</p>
                        @if (isEditMode()) {
                            <p-button label="Salvar informações" icon="pi pi-check" [loading]="savingInfo()" (onClick)="saveInfo()" />
                        }
                    </div>

                    <form [formGroup]="infoForm" class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="md:col-span-2">
                            <label for="pf-name" class="block font-medium mb-2">Nome <span class="text-red-500">*</span></label>
                            <input pInputText id="pf-name" formControlName="name" class="w-full" placeholder="Nome do produto" />
                            @if (infoForm.controls.name.invalid && infoForm.controls.name.touched) {
                                <small class="text-red-500">{{ nameError(infoForm.controls.name) }}</small>
                            }
                        </div>

                        <div class="md:col-span-2">
                            <label for="pf-desc" class="block font-medium mb-2">Descrição <span class="text-red-500">*</span></label>
                            <textarea pTextarea id="pf-desc" formControlName="description" rows="4" class="w-full" placeholder="Descreva o produto"></textarea>
                            @if (infoForm.controls.description.invalid && infoForm.controls.description.touched) {
                                <small class="text-red-500">Descrição é obrigatória</small>
                            }
                        </div>

                        <div>
                            <label for="pf-cat" class="block font-medium mb-2">Categoria <span class="text-red-500">*</span></label>
                            <p-select
                                id="pf-cat"
                                formControlName="categoryId"
                                [options]="activeCategories()"
                                optionLabel="name"
                                optionValue="id"
                                placeholder="Selecione a categoria"
                                class="w-full"
                                appendTo="body"
                                [filter]="true"
                                filterPlaceholder="Buscar..."
                            />
                            @if (infoForm.controls.categoryId.invalid && infoForm.controls.categoryId.touched) {
                                <small class="text-red-500">Categoria é obrigatória</small>
                            }
                        </div>

                        <div class="flex items-center gap-3 pt-6">
                            <p-toggleswitch formControlName="featured" inputId="pf-featured" />
                            <label for="pf-featured" class="font-medium cursor-pointer">Produto em destaque</label>
                        </div>
                    </form>
                </section>

                <!-- ── Section 2: Variantes ────────────────────────────────── -->
                <section class="border border-surface-200 dark:border-surface-700 rounded-xl p-5">
                    <div class="flex items-center justify-between mb-4">
                        <div>
                            <p class="font-semibold text-lg m-0">
                                {{ isEditMode() ? '2. Variantes' : 'Variantes' }}
                                @if (isEditMode()) {
                                    <span class="text-muted-color text-sm font-normal ml-1">({{ product()?.variants?.length ?? 0 }})</span>
                                } @else {
                                    <span class="text-red-500">*</span>
                                    <span class="text-muted-color text-sm font-normal ml-1">({{ localVariants().length }})</span>
                                }
                            </p>
                            @if (!isEditMode()) {
                                <p class="text-muted-color text-sm m-0">Adicione ao menos uma variante antes de criar o produto.</p>
                            }
                        </div>
                        <p-button icon="pi pi-plus" label="Adicionar variante" severity="secondary" [outlined]="true" (onClick)="openAddVariantDialog()" />
                    </div>

                    @if (isEditMode()) {
                        @if ((product()?.variants ?? []).length > 0) {
                            <p-table [value]="product()!.variants" styleClass="p-datatable-sm p-datatable-striped" [tableStyle]="{ 'min-width': '100%' }">
                                <ng-template #header>
                                    <tr>
                                        <th>Atributo</th>
                                        <th style="text-align: right">Preço</th>
                                        <th style="text-align: right">Estoque</th>
                                        <th>SKU</th>
                                        <th>Peso / Dimensões</th>
                                        <th>Status</th>
                                        <th style="width: 10rem; text-align: center">Ações</th>
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
                                        <td class="text-xs text-muted-color">{{ formatDimensions(variant.weight, variant.width, variant.height, variant.depth) }}</td>
                                        <td>
                                            <p-tag [value]="variant.active ? 'Ativa' : 'Inativa'" [severity]="variant.active ? 'success' : 'danger'" />
                                        </td>
                                        <td>
                                            <div class="flex items-center justify-center gap-1">
                                                <p-button icon="pi pi-pencil" [rounded]="true" [text]="true" severity="warn" pTooltip="Editar" tooltipPosition="top" (onClick)="openEditVariantDialog(variant)" />
                                                <p-button
                                                    [icon]="variant.active ? 'pi pi-eye-slash' : 'pi pi-eye'"
                                                    [rounded]="true"
                                                    [text]="true"
                                                    [severity]="variant.active ? 'danger' : 'success'"
                                                    [pTooltip]="variant.active ? 'Inativar' : 'Ativar'"
                                                    tooltipPosition="top"
                                                    (onClick)="confirmToggleVariant(variant)"
                                                />
                                                <p-button icon="pi pi-sort-alt" [rounded]="true" [text]="true" severity="info" pTooltip="Ajustar estoque" tooltipPosition="top" (onClick)="openStockDialog(variant)" />
                                            </div>
                                        </td>
                                    </tr>
                                </ng-template>
                            </p-table>
                        } @else {
                            <p class="text-muted-color text-sm text-center py-6">Nenhuma variante cadastrada ainda.</p>
                        }
                    } @else {
                        @if (localVariants().length > 0) {
                            <p-table [value]="localVariants()" styleClass="p-datatable-sm p-datatable-striped" [tableStyle]="{ 'min-width': '100%' }">
                                <ng-template #header>
                                    <tr>
                                        <th>Atributo</th>
                                        <th style="text-align: right">Preço</th>
                                        <th style="text-align: right">Estoque</th>
                                        <th>SKU</th>
                                        <th>Peso / Dimensões</th>
                                        <th style="width: 5rem; text-align: center">Ação</th>
                                    </tr>
                                </ng-template>
                                <ng-template #body let-v let-i="rowIndex">
                                    <tr>
                                        <td>
                                            <span class="font-medium">{{ v.attributeName }}</span>
                                            <span class="text-muted-color"> / {{ v.attributeValue }}</span>
                                        </td>
                                        <td style="text-align: right">{{ formatPrice(v.price) }}</td>
                                        <td style="text-align: right">{{ v.stock }}</td>
                                        <td class="font-mono text-xs text-muted-color">{{ v.sku || '—' }}</td>
                                        <td class="text-xs text-muted-color">{{ formatDimensions(v.weight, v.width, v.height, v.depth) }}</td>
                                        <td class="text-center">
                                            <p-button icon="pi pi-trash" [rounded]="true" [text]="true" severity="danger" pTooltip="Remover" tooltipPosition="top" (onClick)="removeLocalVariant(i)" />
                                        </td>
                                    </tr>
                                </ng-template>
                            </p-table>
                        } @else {
                            <p class="text-muted-color text-sm text-center py-6">Nenhuma variante adicionada ainda.</p>
                        }
                    }
                </section>

                <!-- ── Section 3: Imagens (somente no modo edição) ─────────── -->
                @if (isEditMode()) {
                    <section class="border border-surface-200 dark:border-surface-700 rounded-xl p-5">
                        <div class="flex items-center justify-between mb-4">
                            <p class="font-semibold text-lg m-0">
                                3. Imagens
                                <span class="text-muted-color text-sm font-normal ml-1">({{ product()?.images?.length ?? 0 }})</span>
                            </p>
                            <p-button icon="pi pi-upload" label="Enviar imagem" [loading]="uploadingImage()" (onClick)="triggerFileInput()" />
                        </div>

                        @if (sortedImages().length > 0) {
                            <div class="flex flex-wrap gap-5">
                                @for (img of sortedImages(); track img.id) {
                                    <div class="flex flex-col items-center gap-2">
                                        <div class="relative">
                                            <p-image [src]="img.url" [preview]="true" imageClass="w-32 h-32 object-cover rounded-xl border border-surface-200 dark:border-surface-700 block" [imageStyle]="{ cursor: 'zoom-in' }" />
                                            @if (img.principal) {
                                                <span class="absolute -top-2 -right-2 bg-primary text-white rounded-full w-6 h-6 flex items-center justify-center shadow" pTooltip="Imagem principal" tooltipPosition="top">
                                                    <i class="pi pi-star-fill text-xs"></i>
                                                </span>
                                            }
                                        </div>
                                        <div class="flex gap-1">
                                            @if (!img.principal) {
                                                <p-button icon="pi pi-star" [rounded]="true" [text]="true" severity="warn" pTooltip="Definir como principal" tooltipPosition="top" (onClick)="setPrincipal(img.id)" />
                                            }
                                            <p-button icon="pi pi-trash" [rounded]="true" [text]="true" severity="danger" pTooltip="Remover imagem" tooltipPosition="top" (onClick)="confirmDeleteImage(img)" />
                                        </div>
                                    </div>
                                }
                            </div>
                        } @else {
                            <div class="text-center py-8 text-muted-color">
                                <i class="pi pi-images text-4xl mb-3 block"></i>
                                <p class="m-0">Nenhuma imagem cadastrada.</p>
                                <p class="text-sm m-0 mt-1">Use o botão "Enviar imagem" para adicionar.</p>
                            </div>
                        }
                    </section>
                }

                <!-- ── Create button (somente no modo criação) ─────────────── -->
                @if (!isEditMode()) {
                    <div class="flex justify-end">
                        <p-button label="Criar produto" icon="pi pi-check" size="large" [loading]="savingInfo()" (onClick)="createProduct()" />
                    </div>
                }
            }
        </div>

        <!-- ── Variant dialog ─────────────────────────────────────────────── -->
        <p-dialog [header]="editingVariant() ? 'Editar variante' : 'Nova variante'" [(visible)]="variantDialogVisible" [modal]="true" [style]="{ width: '36rem' }" [draggable]="false" [resizable]="false" (onHide)="closeVariantDialog()">
            <form [formGroup]="variantForm" class="grid grid-cols-2 gap-4 pt-2">
                <div>
                    <label for="vf-attr-name" class="block font-medium mb-2">Nome do atributo <span class="text-red-500">*</span></label>
                    <input pInputText id="vf-attr-name" formControlName="attributeName" class="w-full" placeholder="Ex: Cor, Tamanho" />
                    @if (variantForm.controls.attributeName.invalid && variantForm.controls.attributeName.touched) {
                        <small class="text-red-500">Campo obrigatório</small>
                    }
                </div>
                <div>
                    <label for="vf-attr-val" class="block font-medium mb-2">Valor do atributo <span class="text-red-500">*</span></label>
                    <input pInputText id="vf-attr-val" formControlName="attributeValue" class="w-full" placeholder="Ex: Vermelho, P" />
                    @if (variantForm.controls.attributeValue.invalid && variantForm.controls.attributeValue.touched) {
                        <small class="text-red-500">Campo obrigatório</small>
                    }
                </div>
                <div>
                    <label for="vf-price" class="block font-medium mb-2">Preço (R$) <span class="text-red-500">*</span></label>
                    <p-inputnumber id="vf-price" formControlName="price" mode="decimal" locale="pt-BR" [minFractionDigits]="2" [maxFractionDigits]="2" [min]="0.01" class="w-full" />
                    @if (variantForm.controls.price.invalid && variantForm.controls.price.touched) {
                        <small class="text-red-500">Preço inválido</small>
                    }
                </div>
                <div>
                    <label for="vf-stock" class="block font-medium mb-2">Estoque inicial</label>
                    <p-inputnumber id="vf-stock" formControlName="stock" [min]="0" [showButtons]="true" class="w-full" />
                </div>
                <div>
                    <label for="vf-weight" class="block font-medium mb-2">Peso (kg) <span class="text-red-500">*</span></label>
                    <p-inputnumber id="vf-weight" formControlName="weight" mode="decimal" [min]="0.001" [minFractionDigits]="3" [maxFractionDigits]="3" class="w-full" placeholder="Ex: 0.350" />
                    @if (variantForm.controls.weight.invalid && variantForm.controls.weight.touched) {
                        <small class="text-red-500">Informe um peso válido</small>
                    }
                </div>
                <div>
                    <label for="vf-width" class="block font-medium mb-2">Largura (cm) <span class="text-red-500">*</span></label>
                    <p-inputnumber id="vf-width" formControlName="width" mode="decimal" [min]="0.01" [minFractionDigits]="1" [maxFractionDigits]="2" class="w-full" placeholder="Ex: 15" />
                    @if (variantForm.controls.width.invalid && variantForm.controls.width.touched) {
                        <small class="text-red-500">Informe uma largura válida</small>
                    }
                </div>
                <div>
                    <label for="vf-height" class="block font-medium mb-2">Altura (cm) <span class="text-red-500">*</span></label>
                    <p-inputnumber id="vf-height" formControlName="height" mode="decimal" [min]="0.01" [minFractionDigits]="1" [maxFractionDigits]="2" class="w-full" placeholder="Ex: 10" />
                    @if (variantForm.controls.height.invalid && variantForm.controls.height.touched) {
                        <small class="text-red-500">Informe uma altura válida</small>
                    }
                </div>
                <div>
                    <label for="vf-depth" class="block font-medium mb-2">Comprimento (cm) <span class="text-red-500">*</span></label>
                    <p-inputnumber id="vf-depth" formControlName="depth" mode="decimal" [min]="0.01" [minFractionDigits]="1" [maxFractionDigits]="2" class="w-full" placeholder="Ex: 20" />
                    @if (variantForm.controls.depth.invalid && variantForm.controls.depth.touched) {
                        <small class="text-red-500">Informe um comprimento válido</small>
                    }
                </div>
                <div class="col-span-2">
                    <label for="vf-sku" class="block font-medium mb-2">
                        SKU
                        <span class="text-muted-color text-sm font-normal">(opcional — gerado automaticamente se não informado)</span>
                    </label>
                    <input pInputText id="vf-sku" formControlName="sku" class="w-full" placeholder="Ex: PROD-COR-VERMELHO" />
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeVariantDialog()" [disabled]="savingVariant()" />
                <p-button [label]="editingVariant() ? 'Salvar alterações' : 'Adicionar'" icon="pi pi-check" [loading]="savingVariant()" (onClick)="saveVariant()" />
            </ng-template>
        </p-dialog>

        <!-- ── Stock dialog ───────────────────────────────────────────────── -->
        <p-dialog header="Ajustar estoque" [(visible)]="stockDialogVisible" [modal]="true" [style]="{ width: '30rem' }" [draggable]="false" [resizable]="false" (onHide)="closeStockDialog()">
            @if (stockTargetVariant()) {
                <div class="flex items-center gap-3 p-3 mb-4 bg-surface-50 dark:bg-surface-800 rounded-lg">
                    <div>
                        <p class="font-medium m-0">{{ stockTargetVariant()!.attributeName }} / {{ stockTargetVariant()!.attributeValue }}</p>
                        <p class="text-muted-color text-sm m-0">
                            Estoque atual: <span class="font-semibold">{{ stockTargetVariant()!.stock }} un.</span>
                        </p>
                    </div>
                </div>
            }
            <form [formGroup]="stockForm" class="flex flex-col gap-4">
                <div>
                    <label for="sf-tipo" class="block font-medium mb-2">Operação <span class="text-red-500">*</span></label>
                    <p-select id="sf-tipo" formControlName="tipo" [options]="stockTypes" optionLabel="label" optionValue="value" placeholder="Selecione" class="w-full" />
                    @if (stockForm.controls.tipo.invalid && stockForm.controls.tipo.touched) {
                        <small class="text-red-500">Selecione uma operação</small>
                    }
                </div>
                <div>
                    <label for="sf-qty" class="block font-medium mb-2">Quantidade <span class="text-red-500">*</span></label>
                    <p-inputnumber id="sf-qty" formControlName="quantidade" [min]="1" [showButtons]="true" class="w-full" />
                </div>
                <div>
                    <label for="sf-motivo" class="block font-medium mb-2">Motivo <span class="text-red-500">*</span></label>
                    <input pInputText id="sf-motivo" formControlName="motivo" class="w-full" placeholder="Ex: Recebimento do fornecedor" />
                    @if (stockForm.controls.motivo.invalid && stockForm.controls.motivo.touched) {
                        <small class="text-red-500">Motivo é obrigatório</small>
                    }
                </div>
            </form>

            <ng-template #footer>
                <p-button label="Cancelar" severity="secondary" [outlined]="true" (onClick)="closeStockDialog()" [disabled]="savingStock()" />
                <p-button label="Confirmar ajuste" icon="pi pi-check" [loading]="savingStock()" (onClick)="saveStock()" />
            </ng-template>
        </p-dialog>
    `
})
export class ProductForm implements OnInit {
    private readonly productService = inject(ProductService);
    private readonly categoryService = inject(CategoryService);
    private readonly messageService = inject(MessageService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly appRef = inject(ApplicationRef);
    private readonly fb = inject(FormBuilder);
    private readonly route = inject(ActivatedRoute);
    private readonly router = inject(Router);

    readonly product = signal<ProductDetail | null>(null);
    readonly activeCategories = signal<Category[]>([]);
    readonly loadingProduct = signal(false);
    readonly savingInfo = signal(false);
    readonly savingVariant = signal(false);
    readonly savingStock = signal(false);
    readonly uploadingImage = signal(false);

    readonly variantDialogVisible = signal(false);
    readonly stockDialogVisible = signal(false);
    readonly editingVariant = signal<ProductVariantInfo | null>(null);
    readonly stockTargetVariant = signal<ProductVariantInfo | null>(null);
    readonly localVariants = signal<CreateVariantRequest[]>([]);

    readonly isEditMode = computed(() => !!this.productId);
    readonly sortedImages = computed<ProductImageInfo[]>(() =>
        [...(this.product()?.images ?? [])].sort((a, b) => {
            if (a.principal !== b.principal) return a.principal ? -1 : 1;

            return a.imageOrder - b.imageOrder;
        })
    );

    private productId: string | null = null;
    private fileInputEl: HTMLInputElement | null = null;

    readonly stockTypes = [
        { label: 'Entrada (recebimento)', value: 'ENTRADA' },
        { label: 'Saída (ajuste / baixa)', value: 'SAIDA' }
    ];

    readonly infoForm = this.fb.group({
        name: ['', [Validators.required, Validators.maxLength(255)]],
        description: ['', [Validators.required]],
        categoryId: ['', [Validators.required]],
        featured: [false]
    });

    readonly variantForm = this.fb.group({
        attributeName: ['', [Validators.required]],
        attributeValue: ['', [Validators.required]],
        price: [null as number | null, [Validators.required, Validators.min(0.01)]],
        stock: [0, [Validators.min(0)]],
        sku: [''],
        weight: [null as number | null, [Validators.required, Validators.min(0.001)]],
        width: [null as number | null, [Validators.required, Validators.min(0.01)]],
        height: [null as number | null, [Validators.required, Validators.min(0.01)]],
        depth: [null as number | null, [Validators.required, Validators.min(0.01)]]
    });

    readonly stockForm = this.fb.group({
        tipo: ['', [Validators.required]],
        quantidade: [1, [Validators.required, Validators.min(1)]],
        motivo: ['', [Validators.required]]
    });

    async ngOnInit(): Promise<void> {
        this.productId = this.route.snapshot.paramMap.get('id');
        await Promise.all([this.loadCategories(), this.productId ? this.loadProduct() : Promise.resolve()]);
    }

    private async loadCategories(): Promise<void> {
        try {
            const result = await firstValueFrom(this.categoryService.list({ page: 0, size: 200, sort: 'name', direction: 'asc', active: true }));

            this.activeCategories.set(result.content);
        } catch {
            this.toast('warn', 'Aviso', 'Não foi possível carregar as categorias.');
        }
    }

    async loadProduct(): Promise<void> {
        if (!this.productId) return;

        this.loadingProduct.set(true);

        try {
            const detail = await firstValueFrom(this.productService.getById(this.productId));

            this.product.set(detail);
            this.infoForm.patchValue({
                name: detail.name,
                description: detail.description,
                categoryId: detail.category.id,
                featured: detail.featured
            });
        } catch (err) {
            this.toast('error', 'Erro ao carregar produto', this.errorMessage(err));
            this.goBack();
        } finally {
            this.loadingProduct.set(false);
        }
    }

    goBack(): void {
        this.router.navigate(['/catalog/products']);
    }

    // ── Create ───────────────────────────────────────────────────────────────

    async createProduct(): Promise<void> {
        if (this.infoForm.invalid) {
            this.infoForm.markAllAsTouched();

            return;
        }

        if (this.localVariants().length === 0) {
            this.toast('warn', 'Variantes obrigatórias', 'Adicione ao menos uma variante antes de criar o produto.');

            return;
        }

        this.savingInfo.set(true);
        const { name, description, categoryId, featured } = this.infoForm.value;
        const request: CreateProductRequest = {
            name: name!,
            description: description!,
            categoryId: categoryId!,
            featured: featured ?? false,
            variants: this.localVariants()
        };

        try {
            const created = await firstValueFrom(this.productService.create(request));

            this.toast('success', 'Produto criado', `"${created.name}" foi criado com sucesso.`);
            this.router.navigate(['/catalog/products', created.id, 'edit']);
        } catch (err) {
            this.toast('error', 'Erro ao criar produto', this.errorMessage(err));
        } finally {
            this.savingInfo.set(false);
        }
    }

    // ── Update info ──────────────────────────────────────────────────────────

    async saveInfo(): Promise<void> {
        if (this.infoForm.invalid) {
            this.infoForm.markAllAsTouched();

            return;
        }

        if (!this.productId) return;

        this.savingInfo.set(true);
        const { name, description, categoryId, featured } = this.infoForm.value;
        const request: UpdateProductRequest = {
            name: name!,
            description: description!,
            categoryId: categoryId!,
            featured: featured ?? false
        };

        try {
            await firstValueFrom(this.productService.update(this.productId, request));
            this.toast('success', 'Produto atualizado', 'As informações foram salvas com sucesso.');
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao salvar', this.errorMessage(err));
        } finally {
            this.savingInfo.set(false);
        }
    }

    // ── Variants ─────────────────────────────────────────────────────────────

    openAddVariantDialog(): void {
        this.editingVariant.set(null);
        this.variantForm.reset({ attributeName: '', attributeValue: '', price: null, stock: 0, sku: '', weight: null, width: null, height: null, depth: null });
        this.variantDialogVisible.set(true);
    }

    openEditVariantDialog(variant: ProductVariantInfo): void {
        this.editingVariant.set(variant);
        this.variantForm.patchValue({
            attributeName: variant.attributeName,
            attributeValue: variant.attributeValue,
            price: variant.price,
            stock: variant.stock,
            sku: variant.sku,
            weight: variant.weight,
            width: variant.width,
            height: variant.height,
            depth: variant.depth
        });
        this.variantDialogVisible.set(true);
    }

    closeVariantDialog(): void {
        this.variantDialogVisible.set(false);
        this.editingVariant.set(null);
        this.variantForm.reset();
    }

    async saveVariant(): Promise<void> {
        if (this.variantForm.invalid) {
            this.variantForm.markAllAsTouched();

            return;
        }

        const { attributeName, attributeValue, price, stock, sku, weight, width, height, depth } = this.variantForm.value;
        const payload: UpdateVariantRequest = {
            attributeName: attributeName!,
            attributeValue: attributeValue!,
            price: price!,
            stock: stock ?? 0,
            sku: sku || undefined,
            weight: weight!,
            width: width!,
            height: height!,
            depth: depth!
        };

        if (!this.isEditMode()) {
            this.localVariants.update((list) => [...list, payload as CreateVariantRequest]);
            this.closeVariantDialog();

            return;
        }

        if (!this.productId) return;

        this.savingVariant.set(true);

        try {
            const editing = this.editingVariant();

            if (editing) {
                await firstValueFrom(this.productService.updateVariant(this.productId, editing.id, payload));
                this.toast('success', 'Variante atualizada', 'Alterações salvas com sucesso.');
            } else {
                await firstValueFrom(this.productService.addVariant(this.productId, payload));
                this.toast('success', 'Variante adicionada', 'Nova variante criada com sucesso.');
            }

            this.closeVariantDialog();
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao salvar variante', this.errorMessage(err));
        } finally {
            this.savingVariant.set(false);
        }
    }

    removeLocalVariant(index: number): void {
        this.localVariants.update((list) => list.filter((_, i) => i !== index));
    }

    confirmToggleVariant(variant: ProductVariantInfo): void {
        const nextActive = !variant.active;
        const action = nextActive ? 'ativar' : 'inativar';

        this.confirmationService.confirm({
            message: `Deseja ${action} a variante <strong>${variant.attributeName} / ${variant.attributeValue}</strong>?`,
            header: `${nextActive ? 'Ativar' : 'Inativar'} variante`,
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Confirmar', severity: nextActive ? 'success' : 'danger' },
            accept: () => this.toggleVariantStatus(variant, nextActive)
        });
        this.appRef.tick();
    }

    async toggleVariantStatus(variant: ProductVariantInfo, active: boolean): Promise<void> {
        if (!this.productId) return;

        try {
            await firstValueFrom(this.productService.toggleVariantStatus(this.productId, variant.id, active));
            const label = active ? 'ativada' : 'inativada';

            this.toast('success', `Variante ${label}`, `${variant.attributeName} / ${variant.attributeValue} foi ${label}.`);
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao atualizar variante', this.errorMessage(err));
        }
    }

    // ── Stock ────────────────────────────────────────────────────────────────

    openStockDialog(variant: ProductVariantInfo): void {
        this.stockTargetVariant.set(variant);
        this.stockForm.reset({ tipo: '', quantidade: 1, motivo: '' });
        this.stockDialogVisible.set(true);
    }

    closeStockDialog(): void {
        this.stockDialogVisible.set(false);
        this.stockTargetVariant.set(null);
        this.stockForm.reset();
    }

    async saveStock(): Promise<void> {
        if (this.stockForm.invalid) {
            this.stockForm.markAllAsTouched();

            return;
        }

        const variant = this.stockTargetVariant();

        if (!this.productId || !variant) return;

        this.savingStock.set(true);
        const { tipo, quantidade, motivo } = this.stockForm.value;
        const request: AdjustStockRequest = {
            tipo: tipo as 'ENTRADA' | 'SAIDA',
            quantidade: quantidade!,
            motivo: motivo!
        };

        try {
            const result = await firstValueFrom(this.productService.adjustStock(this.productId, variant.id, request));

            this.toast('success', 'Estoque ajustado', `Novo estoque: ${result.stock} unidades.`);
            this.closeStockDialog();
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao ajustar estoque', this.errorMessage(err));
        } finally {
            this.savingStock.set(false);
        }
    }

    // ── Images ───────────────────────────────────────────────────────────────

    triggerFileInput(): void {
        if (!this.fileInputEl) {
            this.fileInputEl = document.querySelector('input[type="file"]');
        }

        this.fileInputEl?.click();
    }

    async onFileSelected(event: Event): Promise<void> {
        const input = event.target as HTMLInputElement;
        const file = input.files?.[0];

        if (!file || !this.productId) return;

        this.uploadingImage.set(true);
        input.value = '';

        try {
            await firstValueFrom(this.productService.uploadImage(this.productId, file));
            this.toast('success', 'Imagem enviada', 'Imagem adicionada ao produto com sucesso.');
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao enviar imagem', this.errorMessage(err));
        } finally {
            this.uploadingImage.set(false);
        }
    }

    async setPrincipal(imageId: string): Promise<void> {
        if (!this.productId) return;

        try {
            await firstValueFrom(this.productService.setPrincipalImage(this.productId, imageId));
            this.toast('success', 'Imagem principal definida', 'A imagem principal foi atualizada.');
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao definir imagem principal', this.errorMessage(err));
        }
    }

    confirmDeleteImage(img: ProductImageInfo): void {
        this.confirmationService.confirm({
            message: 'Deseja remover esta imagem? A ação não pode ser desfeita.',
            header: 'Remover imagem',
            icon: 'pi pi-exclamation-triangle',
            rejectButtonProps: { label: 'Cancelar', severity: 'secondary', outlined: true },
            acceptButtonProps: { label: 'Remover', severity: 'danger' },
            accept: () => this.deleteImage(img.id)
        });
        this.appRef.tick();
    }

    async deleteImage(imageId: string): Promise<void> {
        if (!this.productId) return;

        try {
            await firstValueFrom(this.productService.deleteImage(this.productId, imageId));
            this.toast('success', 'Imagem removida', 'A imagem foi removida com sucesso.');
            await this.loadProduct();
        } catch (err) {
            this.toast('error', 'Erro ao remover imagem', this.errorMessage(err));
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    formatPrice(price: number): string {
        return price.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    }

    formatDimensions(weight: number | null, width: number | null, height: number | null, depth: number | null): string {
        if (weight == null || width == null || height == null || depth == null) return '—';

        const weightLabel = `${weight.toLocaleString('pt-BR', { minimumFractionDigits: 3, maximumFractionDigits: 3 })} kg`;
        const sizeLabel = `${width.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 2 })} x ${height.toLocaleString('pt-BR', {
            minimumFractionDigits: 1,
            maximumFractionDigits: 2
        })} x ${depth.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 2 })} cm`;

        return `${weightLabel} | ${sizeLabel}`;
    }

    nameError(ctrl: AbstractControl): string {
        if (ctrl.errors?.['required']) return 'Nome é obrigatório';
        if (ctrl.errors?.['maxlength']) return 'Máximo 255 caracteres';

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
