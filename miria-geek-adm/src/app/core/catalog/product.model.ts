export interface ProductCategoryInfo {
    id: string;
    name: string;
}

export interface ProductSummary {
    id: string;
    name: string;
    slug: string;
    category: ProductCategoryInfo;
    status: 'ACTIVE' | 'INACTIVE';
    featured: boolean;
    totalVariants: number;
    totalImages: number;
    totalStock: number;
    principalImageUrl: string | null;
    createdAt: string;
}

export interface ProductImageInfo {
    id: string;
    url: string;
    principal: boolean;
    imageOrder: number;
}

export interface ProductVariantInfo {
    id: string;
    attributeName: string;
    attributeValue: string;
    price: number;
    stock: number;
    sku: string;
    active: boolean;
    createdAt: string;
    weight: number | null;
    width: number | null;
    height: number | null;
    depth: number | null;
}

export interface ProductDetail {
    id: string;
    name: string;
    slug: string;
    description: string;
    category: ProductCategoryInfo;
    status: 'ACTIVE' | 'INACTIVE';
    featured: boolean;
    createdAt: string;
    images: ProductImageInfo[];
    variants: ProductVariantInfo[];
}

export interface ProductPagedResponse {
    content: ProductSummary[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface ProductListParams {
    nome?: string;
    categoriaId?: string;
    status?: string | null;
    destaque?: boolean | null;
    page: number;
    size: number;
    sort: string;
}

export interface UpdateProductStatusResponse {
    id: string;
    name: string;
    status: string;
}

export interface CreateVariantRequest {
    attributeName: string;
    attributeValue: string;
    price: number;
    stock: number;
    sku?: string;
    weight: number;
    width: number;
    height: number;
    depth: number;
}

export interface CreateProductRequest {
    name: string;
    description: string;
    categoryId: string;
    featured: boolean;
    variants: CreateVariantRequest[];
}

export interface UpdateProductRequest {
    name: string;
    description: string;
    categoryId: string;
    featured: boolean;
}

export interface UpdateVariantRequest {
    attributeName: string;
    attributeValue: string;
    price: number;
    stock: number;
    sku?: string;
    weight: number;
    width: number;
    height: number;
    depth: number;
}

export interface AdjustStockRequest {
    tipo: 'ENTRADA' | 'SAIDA';
    quantidade: number;
    motivo: string;
}

export interface ProductResponse {
    id: string;
    name: string;
    slug: string;
    description: string;
    categoryId: string;
    status: string;
    featured: boolean;
    createdAt: string;
    variants: ProductVariantInfo[];
}

export interface ReorderImagesRequest {
    images: { id: string; imageOrder: number }[];
}
