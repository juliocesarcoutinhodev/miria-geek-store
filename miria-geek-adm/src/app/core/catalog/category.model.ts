export interface Category {
    id: string;
    name: string;
    slug: string;
    description: string;
    totalProducts: number;
    active: boolean;
    createdAt: string;
}

export interface CategoryPagedResponse {
    content: Category[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface CategoryListParams {
    name?: string;
    active?: boolean | null;
    page: number;
    size: number;
    sort: string;
    direction: string;
}

export interface CreateCategoryRequest {
    name: string;
    description: string;
}

export interface UpdateCategoryRequest {
    name: string;
    description: string;
    active: boolean;
}
