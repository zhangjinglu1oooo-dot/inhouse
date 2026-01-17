package com.inhouse.iam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录认证控制器。
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    // 用户存储
    private final IamStore store;
    // Token 服务
    private final TokenService tokenService;

    public AuthController(IamStore store, TokenService tokenService) {
        this.store = store;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        // 简单用户名/密码校验
        for (User user : store.getUsers().values()) {
            if (user.getUsername().equals(request.getUsername())
                    && user.getPassword().equals(request.getPassword())) {
                return tokenService.issueToken(user.getId());
            }
        }
        throw new IllegalArgumentException("Invalid credentials");
    }

    @PostMapping("/validate")
    public String validate(@RequestBody TokenRequest request) {
        // 校验 access token
        return tokenService.validate(request.getAccessToken());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthError(IllegalArgumentException ex) {
        // 认证失败返回 401
        return ex.getMessage();
    }
}
