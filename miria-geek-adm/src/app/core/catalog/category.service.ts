import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Category, CategoryListParams, CategoryPagedResponse, CreateCategoryRequest, UpdateCategoryRequest } from './category.model';

@Injectable({ providedIn: 'root' })
export class CategoryService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/admin/categories`;

    list(params: CategoryListParams): Observable<CategoryPagedResponse> {
        let p = new HttpParams().set('page', params.page).set('size', params.size).set('sort', params.sort).set('direction', params.direction);

        if (params.name) p = p.set('name', params.name);
        if (params.active != null) p = p.set('active', String(params.active));

        return this.http.get<CategoryPagedResponse>(this.baseUrl, { params: p });
    }

    getById(id: string): Observable<Category> {
        return this.http.get<Category>(`${this.baseUrl}/${id}`);
    }

    create(request: CreateCategoryRequest): Observable<Category> {
        return this.http.post<Category>(this.baseUrl, request);
    }

    update(id: string, request: UpdateCategoryRequest): Observable<Category> {
        return this.http.put<Category>(`${this.baseUrl}/${id}`, request);
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
