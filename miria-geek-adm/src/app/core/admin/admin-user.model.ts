export interface UserSummary {
    id: string;
    fullName: string;
    email: string;
    role: string;
    status: string;
    createdAt: string;
}

export interface AdminUserDetail {
    id: string;
    fullName: string;
    email: string;
    role: string;
    status: string;
    createdAt: string;
    totalPedidos: number;
    ultimoLogin: string | null;
}

export interface AdminUserResponse {
    id: string;
    fullName: string;
    email: string;
    status: string;
    role: string;
    createdAt: string;
}

export interface PagedResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface UserListParams {
    nome?: string;
    email?: string;
    status?: string;
    role?: string;
    page: number;
    size: number;
    sort: string;
    direction: string;
}

export interface CreateUserRequest {
    fullName: string;
    email: string;
    role: string;
}

export interface UpdateUserRequest {
    fullName: string;
    email: string;
    role: string;
}
