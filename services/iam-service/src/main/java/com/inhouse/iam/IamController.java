package com.inhouse.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 用户与角色管理控制器。
 */
@RestController
@RequestMapping("/iam")
@CrossOrigin(origins = "*")
public class IamController {
    // 用户/角色仓库
    private final IamRepository repository;
    // 密码服务
    private final PasswordService passwordService;

    public IamController(IamRepository repository, PasswordService passwordService) {
        this.repository = repository;
        this.passwordService = passwordService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        // 写入用户信息
        String id = UUID.randomUUID().toString();
        user.setId(id);
        Date now = new Date();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        ensurePasswordHashed(user);
        repository.saveUser(user);
        return sanitizeUser(user);
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        List<User> users = new ArrayList<User>(repository.listUsers());
        users.forEach(this::sanitizeUser);
        return users;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") String id) {
        return repository
                .findUserById(id)
                .map(this::sanitizeUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@RequestBody Role role) {
        // 写入角色信息
        String id = UUID.randomUUID().toString();
        role.setId(id);
        role.setCreatedAt(new Date());
        repository.saveRole(role);
        return role;
    }

    @GetMapping("/roles")
    public List<Role> listRoles() {
        return new ArrayList<Role>(repository.listRoles());
    }

    private void ensurePasswordHashed(User user) {
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getPasswordSalt() == null || user.getPasswordSalt().trim().isEmpty()) {
            PasswordHash passwordHash = passwordService.hashPassword(user.getPassword());
            user.setPassword(passwordHash.getHash());
            user.setPasswordSalt(passwordHash.getSalt());
        }
    }

    private User sanitizeUser(User user) {
        user.setPassword(null);
        user.setPasswordSalt(null);
        return user;
    }
}
