package com.transvoz.module.user.service;

import com.transvoz.config.JwtConfig;
import com.transvoz.module.user.dto.LoginRequest;
import com.transvoz.module.user.dto.LoginResponse;
import com.transvoz.module.user.dto.UserResponse;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.repository.UserRepository;
import com.transvoz.shared.exception.TransVozException;
import com.transvoz.shared.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtConfig jwtConfig;

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt for email: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> new TransVozException("Invalid credentials"));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        // Set refresh token as HttpOnly cookie
        setRefreshTokenCookie(response, refreshToken);

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getId());

        UserResponse userResponse = userService.getCurrentUser(request.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                .user(userResponse)
                .build();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear refresh token cookie
        clearRefreshTokenCookie(response);
        log.info("User logged out successfully");
    }

    public LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);
        
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new TransVozException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new TransVozException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        setRefreshTokenCookie(response, newRefreshToken);

        UserResponse userResponse = userService.getCurrentUser(email);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenExpiration() / 1000)
                .user(userResponse)
                .build();
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtConfig.getRefreshTokenExpiration() / 1000));
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
