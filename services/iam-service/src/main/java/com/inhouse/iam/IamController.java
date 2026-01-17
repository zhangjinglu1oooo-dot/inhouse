package com.inhouse.iam;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public PageResponse<User> listUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageSlice slice = PageSlice.of(page, size);
        List<User> users = repository.listUsersPage(slice.getOffset(), slice.getLimit());
        users.forEach(this::sanitizeUser);
        int total = repository.countUsers();
        return new PageResponse<User>(users, slice.getPage(), slice.getSize(), total);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable("id") String id, @RequestBody User updates) {
        Optional<User> existingOptional = repository.findUserById(id);
        if (!existingOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        User existing = existingOptional.get();
        mergeUser(existing, updates);
        existing.setUpdatedAt(new Date());
        if (updates.getPassword() != null && !updates.getPassword().trim().isEmpty()) {
            existing.setPassword(updates.getPassword());
            existing.setPasswordSalt(null);
            ensurePasswordHashed(existing);
        }
        repository.updateUser(existing);
        if (updates.getRoles() != null) {
            repository.replaceUserRoles(existing.getId(), updates.getRoles());
        }
        if (updates.getAttributes() != null) {
            repository.replaceUserAttributes(existing.getId(), updates.getAttributes());
        }
        return sanitizeUser(existing);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        repository.deleteUser(id);
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
    public PageResponse<Role> listRoles(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageSlice slice = PageSlice.of(page, size);
        List<Role> roles = repository.listRolesPage(slice.getOffset(), slice.getLimit());
        int total = repository.countRoles();
        return new PageResponse<Role>(roles, slice.getPage(), slice.getSize(), total);
    }

    @PutMapping("/roles/{id}")
    public Role updateRole(@PathVariable("id") String id, @RequestBody Role updates) {
        Optional<Role> existingOptional = repository.findRoleById(id);
        if (!existingOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "角色不存在");
        }
        Role existing = existingOptional.get();
        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getPermissions() != null) {
            existing.setPermissions(updates.getPermissions());
        }
        repository.updateRole(existing);
        return existing;
    }

    @DeleteMapping("/roles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable("id") String id) {
        repository.deleteRole(id);
    }

    @PostMapping("/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    public Permission createPermission(@RequestBody Permission permission) {
        String id = UUID.randomUUID().toString();
        permission.setId(id);
        permission.setCreatedAt(new Date());
        repository.savePermission(permission);
        return permission;
    }

    @GetMapping("/permissions")
    public PageResponse<Permission> listPermissions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageSlice slice = PageSlice.of(page, size);
        List<Permission> permissions = repository.listPermissionsPage(slice.getOffset(), slice.getLimit());
        int total = repository.countPermissions();
        return new PageResponse<Permission>(permissions, slice.getPage(), slice.getSize(), total);
    }

    @PutMapping("/permissions/{id}")
    public Permission updatePermission(@PathVariable("id") String id, @RequestBody Permission updates) {
        Optional<Permission> existingOptional = repository.findPermissionById(id);
        if (!existingOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "权限不存在");
        }
        Permission existing = existingOptional.get();
        if (updates.getApp() != null) {
            existing.setApp(updates.getApp());
        }
        if (updates.getFeature() != null) {
            existing.setFeature(updates.getFeature());
        }
        if (updates.getResource() != null) {
            existing.setResource(updates.getResource());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        repository.updatePermission(existing);
        return existing;
    }

    @DeleteMapping("/permissions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermission(@PathVariable("id") String id) {
        repository.deletePermission(id);
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

    private void mergeUser(User target, User updates) {
        if (updates.getEmployeeId() != null) {
            target.setEmployeeId(updates.getEmployeeId());
        }
        if (updates.getUsername() != null) {
            target.setUsername(updates.getUsername());
        }
        if (updates.getDisplayName() != null) {
            target.setDisplayName(updates.getDisplayName());
        }
        if (updates.getEmail() != null) {
            target.setEmail(updates.getEmail());
        }
        if (updates.getPhone() != null) {
            target.setPhone(updates.getPhone());
        }
        if (updates.getDepartmentId() != null) {
            target.setDepartmentId(updates.getDepartmentId());
        }
        if (updates.getTitle() != null) {
            target.setTitle(updates.getTitle());
        }
        if (updates.getManagerId() != null) {
            target.setManagerId(updates.getManagerId());
        }
        if (updates.getLocation() != null) {
            target.setLocation(updates.getLocation());
        }
        if (updates.getStatus() != null) {
            target.setStatus(updates.getStatus());
        }
        if (updates.getHireDate() != null) {
            target.setHireDate(updates.getHireDate());
        }
    }

    private static class PageSlice {
        private final int page;
        private final int size;
        private final int offset;
        private final int limit;

        private PageSlice(int page, int size) {
            this.page = page;
            this.size = size;
            this.offset = (page - 1) * size;
            this.limit = size;
        }

        public static PageSlice of(int page, int size) {
            int normalizedPage = page <= 0 ? 1 : page;
            int normalizedSize = size <= 0 ? 10 : size;
            return new PageSlice(normalizedPage, normalizedSize);
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }

        public int getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }
    }
}
