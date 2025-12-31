package com.icube.sim.tichu.auth;

import com.icube.sim.tichu.auth.jwt.JwtConfig;
import com.icube.sim.tichu.auth.jwt.JwtIssueResult;
import com.icube.sim.tichu.auth.jwt.JwtResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        var jwtIssueResult = authService.login(request);

        var cookie = createRefreshTokenCookie(jwtIssueResult);
        response.addCookie(cookie);

        return new JwtResponse(jwtIssueResult.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse response
    ) {
        var jwtIssueResult = authService.refreshTokens(refreshToken);

        var cookie = createRefreshTokenCookie(jwtIssueResult);
        response.addCookie(cookie);

        return new JwtResponse(jwtIssueResult.getAccessToken().toString());
    }

    private Cookie createRefreshTokenCookie(JwtIssueResult jwtIssueResult) {
        var cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, jwtIssueResult.getRefreshToken().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        return cookie;
    }

    @GetMapping("/me")
    public String getMe() {
        var user = authService.getCurrentUser();
        return user.getName();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull Void> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
