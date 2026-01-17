package com.inhouse.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户与角色管理控制器。
 */
@RestController
@RequestMapping("/iam")
@CrossOrigin(origins = "*")
public class IamController {
    // 用户/角色仓库
    private final IamRepository repository;

    public IamController(IamRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        // 写入用户信息
        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setCreatedAt(new Date());
        repository.saveUser(user);
        return user;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return new ArrayList<User>(repository.listUsers());
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
}
