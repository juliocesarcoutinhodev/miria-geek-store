import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductDetail, ProductListParams, ProductPagedResponse, UpdateProductStatusResponse } from './product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/admin/products`;

    list(params: ProductListParams): Observable<ProductPagedResponse> {
        let p = new HttpParams().set('page', params.page).set('size', params.size).set('sort', params.sort);

        if (params.nome) p = p.set('nome', params.nome);
        if (params.categoriaId) p = p.set('categoriaId', params.categoriaId);
        if (params.status != null) p = p.set('status', params.status);
        if (params.destaque != null) p = p.set('destaque', String(params.destaque));

        return this.http.get<ProductPagedResponse>(this.baseUrl, { params: p });
    }

    getById(id: string): Observable<ProductDetail> {
        return this.http.get<ProductDetail>(`${this.baseUrl}/${id}`);
    }

    updateStatus(id: string, status: 'ACTIVE' | 'INACTIVE'): Observable<UpdateProductStatusResponse> {
        return this.http.patch<UpdateProductStatusResponse>(`${this.baseUrl}/${id}/status`, { status });
    }
}
