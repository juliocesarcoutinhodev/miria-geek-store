package br.com.miriageekstore.identity.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieFactory {

    @Value("${app.cookie.domain:localhost}")
    private String domain;

    @Value("${app.cookie.secure:false}")
    private boolean secure;

    @Value("${app.jwt.refresh-token-expiry-seconds:604800}")
    private long refreshTokenExpirySeconds;

    public ResponseCookie accessToken(String value) {
        return ResponseCookie.from("access_token", value)
                .httpOnly(true).secure(secure).path("/")
                .domain(domain).sameSite("Strict").maxAge(900).build();
    }

    public ResponseCookie refreshToken(String value) {
        return ResponseCookie.from("refresh_token", value)
                .httpOnly(true).secure(secure).path("/api/v1/auth/refresh")
                .domain(domain).sameSite("Strict").maxAge(refreshTokenExpirySeconds).build();
    }

    public ResponseCookie clearAccessToken() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(secure).path("/")
                .domain(domain).sameSite("Strict").maxAge(0).build();
    }

    public ResponseCookie clearRefreshToken() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(secure).path("/api/v1/auth/refresh")
                .domain(domain).sameSite("Strict").maxAge(0).build();
    }
}
