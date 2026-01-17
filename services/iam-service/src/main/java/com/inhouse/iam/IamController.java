package com.inhouse.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iam")
public class IamController {
    private final IamStore store;

    public IamController(IamStore store) {
        this.store = store;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setCreatedAt(new Date());
        store.getUsers().put(id, user);
        return user;
    }

    @GetMapping("/users")
    public List<User> listUsers() {
        return new ArrayList<User>(store.getUsers().values());
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@RequestBody Role role) {
        String id = UUID.randomUUID().toString();
        role.setId(id);
        role.setCreatedAt(new Date());
        store.getRoles().put(id, role);
        return role;
    }

    @GetMapping("/roles")
    public List<Role> listRoles() {
        return new ArrayList<Role>(store.getRoles().values());
    }
}
