package com.inhouse.iam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * IAM 登录校验拦截器。
 */
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing token");
            return false;
        }
        String token = authorization.substring("Bearer ".length());
        try {
            tokenService.validate(token);
            return true;
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
            return false;
        }
    }
}
