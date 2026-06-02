import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ChangePasswordRequest, UpdateProfileRequest, UserProfile } from './profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/users`;

    getProfile(): Observable<UserProfile> {
        return this.http.get<UserProfile>(`${this.baseUrl}/me`);
    }

    updateProfile(request: UpdateProfileRequest): Observable<UserProfile> {
        return this.http.patch<UserProfile>(`${this.baseUrl}/me`, request);
    }

    changePassword(request: ChangePasswordRequest): Observable<void> {
        return this.http.patch<void>(`${this.baseUrl}/me/password`, request);
    }
}
