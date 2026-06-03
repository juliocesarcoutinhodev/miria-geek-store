import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
    AdjustStockRequest,
    CreateProductRequest,
    ProductDetail,
    ProductImageInfo,
    ProductListParams,
    ProductPagedResponse,
    ProductResponse,
    ReorderImagesRequest,
    UpdateProductRequest,
    UpdateProductStatusResponse,
    UpdateVariantRequest
} from './product.model';

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

    create(request: CreateProductRequest): Observable<ProductResponse> {
        return this.http.post<ProductResponse>(this.baseUrl, request);
    }

    update(id: string, request: UpdateProductRequest): Observable<ProductResponse> {
        return this.http.put<ProductResponse>(`${this.baseUrl}/${id}`, request);
    }

    updateStatus(id: string, status: 'ACTIVE' | 'INACTIVE'): Observable<UpdateProductStatusResponse> {
        return this.http.patch<UpdateProductStatusResponse>(`${this.baseUrl}/${id}/status`, { status });
    }

    // ── Images ──────────────────────────────────────────────────────────────

    uploadImage(productId: string, file: File): Observable<ProductImageInfo> {
        const form = new FormData();

        form.append('arquivo', file);

        return this.http.post<ProductImageInfo>(`${this.baseUrl}/${productId}/images`, form);
    }

    setPrincipalImage(productId: string, imageId: string): Observable<ProductImageInfo> {
        return this.http.patch<ProductImageInfo>(`${this.baseUrl}/${productId}/images/${imageId}/principal`, {});
    }

    reorderImages(productId: string, request: ReorderImagesRequest): Observable<ProductImageInfo[]> {
        return this.http.patch<ProductImageInfo[]>(`${this.baseUrl}/${productId}/images/order`, request);
    }

    deleteImage(productId: string, imageId: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${productId}/images/${imageId}`);
    }

    // ── Variants ─────────────────────────────────────────────────────────────

    addVariant(productId: string, request: UpdateVariantRequest): Observable<ProductDetail['variants'][number]> {
        return this.http.post<ProductDetail['variants'][number]>(`${this.baseUrl}/${productId}/variants`, request);
    }

    updateVariant(productId: string, variantId: string, request: UpdateVariantRequest): Observable<ProductDetail['variants'][number]> {
        return this.http.put<ProductDetail['variants'][number]>(`${this.baseUrl}/${productId}/variants/${variantId}`, request);
    }

    toggleVariantStatus(productId: string, variantId: string, ativo: boolean): Observable<ProductDetail['variants'][number]> {
        return this.http.patch<ProductDetail['variants'][number]>(`${this.baseUrl}/${productId}/variants/${variantId}/status`, { ativo });
    }

    adjustStock(productId: string, variantId: string, request: AdjustStockRequest): Observable<{ variantId: string; sku: string; stock: number }> {
        return this.http.patch<{ variantId: string; sku: string; stock: number }>(`${this.baseUrl}/${productId}/variants/${variantId}/stock`, request);
    }
}
