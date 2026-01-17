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
    // 用户仓库
    private final IamRepository repository;
    // Token 服务
    private final TokenService tokenService;
    // 密码服务
    private final PasswordService passwordService;

    public AuthController(IamRepository repository, TokenService tokenService, PasswordService passwordService) {
        this.repository = repository;
        this.tokenService = tokenService;
        this.passwordService = passwordService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        // 姓名 + 密码校验
        return repository
                .findUserByAccount(request.getAccount())
                .filter(user -> passwordService.matches(
                        request.getPassword(),
                        user.getPasswordSalt(),
                        user.getPassword()))
                .map(user -> tokenService.issueToken(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterRequest request) {
        if (request.getAccount() == null || request.getAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("Account is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setEmployeeId("EMP-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        user.setUsername(request.getAccount().trim());
        user.setEmail(request.getEmail().trim());
        user.setDisplayName(request.getAccount().trim());
        user.setStatus("active");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        PasswordHash passwordHash = passwordService.hashPassword(request.getPassword());
        user.setPassword(passwordHash.getHash());
        user.setPasswordSalt(passwordHash.getSalt());
        repository.saveUser(user);
        return sanitizeUser(user);
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

    private User sanitizeUser(User user) {
        user.setPassword(null);
        user.setPasswordSalt(null);
        return user;
    }
}
