package com.inhouse.iam;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IamStore store;
    private final TokenService tokenService;

    public AuthController(IamStore store, TokenService tokenService) {
        this.store = store;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
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
        return tokenService.validate(request.getAccessToken());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAuthError(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
