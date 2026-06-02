import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, firstValueFrom, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthUser, LoginRequest, ResetPasswordRequest } from './auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);
    private readonly baseUrl = `${environment.apiUrl}/auth`;

    private readonly _user = signal<AuthUser | null>(null);
    private _sessionReady: Promise<void> = Promise.resolve();

    readonly user = this._user.asReadonly();
    readonly isAuthenticated = computed(() => this._user() !== null);
    readonly isAdmin = computed(() => this._user()?.roles.includes('ROLE_ADMIN') ?? false);

    get sessionReady(): Promise<void> {
        return this._sessionReady;
    }

    login(request: LoginRequest): Observable<AuthUser> {
        return this.http.post<AuthUser>(`${this.baseUrl}/login`, request, { withCredentials: true }).pipe(tap((user) => this._user.set(user)));
    }

    logout(): void {
        this.http.post<void>(`${this.baseUrl}/logout`, {}, { withCredentials: true }).subscribe({
            complete: () => this._redirectToLogin(),
            error: () => this._redirectToLogin()
        });
    }

    refresh(): Observable<AuthUser> {
        return this.http.post<AuthUser>(`${this.baseUrl}/refresh`, {}, { withCredentials: true }).pipe(tap((user) => this._user.set(user)));
    }

    forgotPassword(email: string): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/forgot-password`, { email });
    }

    resetPassword(request: ResetPasswordRequest): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/reset-password`, request);
    }

    initializeSession(): Promise<void> {
        this._sessionReady = firstValueFrom(
            this.http.get<AuthUser>(`${environment.apiUrl}/users/me`).pipe(
                tap((user) => this._user.set(user)),
                catchError(() => of(null))
            )
        ).then(() => void 0);

        return this._sessionReady;
    }

    clearUser(): void {
        this._user.set(null);
    }

    private _redirectToLogin(): void {
        this._user.set(null);
        this.router.navigate(['/auth/login']);
    }
}
