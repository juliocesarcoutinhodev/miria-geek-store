import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminUserDetail, AdminUserResponse, CreateUserRequest, PagedResponse, UpdateUserRequest, UserListParams, UserSummary } from './admin-user.model';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/admin/users`;

    listUsers(params: UserListParams): Observable<PagedResponse<UserSummary>> {
        let httpParams = new HttpParams().set('page', params.page).set('size', params.size).set('sort', params.sort).set('direction', params.direction);

        if (params.nome) httpParams = httpParams.set('nome', params.nome);

        if (params.email) httpParams = httpParams.set('email', params.email);

        if (params.status) httpParams = httpParams.set('status', params.status);

        if (params.role) httpParams = httpParams.set('role', params.role);

        return this.http.get<PagedResponse<UserSummary>>(this.baseUrl, { params: httpParams });
    }

    getUserById(id: string): Observable<AdminUserDetail> {
        return this.http.get<AdminUserDetail>(`${this.baseUrl}/${id}`);
    }

    createUser(request: CreateUserRequest): Observable<AdminUserResponse> {
        return this.http.post<AdminUserResponse>(this.baseUrl, request);
    }

    updateUser(id: string, request: UpdateUserRequest): Observable<AdminUserResponse> {
        return this.http.put<AdminUserResponse>(`${this.baseUrl}/${id}`, request);
    }

    toggleStatus(id: string, status: 'ACTIVE' | 'INACTIVE'): Observable<void> {
        return this.http.patch<void>(`${this.baseUrl}/${id}/status`, { status });
    }
}
