package com.inhouse.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/portal-config")
    public PortalConfigResponse loadPortalConfig(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        User user = repository.findUserById(userId)
                .map(this::sanitizeUser)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PortalConfigResponse response = new PortalConfigResponse();
        PortalConfigResponse.PortalMeta meta = new PortalConfigResponse.PortalMeta();
        meta.setBreadcrumb("系统工作台");
        meta.setTitle("员工工作台");
        meta.setSubtitle("根据后台配置展示导航、应用与功能入口。");
        response.setMeta(meta);

        response.setNavigation(buildNavigation());
        response.setApplications(buildApplications(user));
        response.setFeatures(buildFeatures(user));
        response.setUser(buildUserProfile(user));
        return response;
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

    private List<PortalConfigResponse.PortalNavItem> buildNavigation() {
        List<PortalConfigResponse.PortalNavItem> items = new ArrayList<PortalConfigResponse.PortalNavItem>();
        items.add(navItem("overview", "概览", "system.html", "🏠", true));
        items.add(navItem("workspace", "个人空间", "system.html#workspace", "🧭", false));
        items.add(navItem("apps", "协作应用", "system.html#apps", "🧩", false));
        items.add(navItem("knowledge", "知识库", "system.html#features", "📚", false));
        items.add(navItem("support", "服务支持", "system.html#support", "🛠️", false));
        return items;
    }

    private PortalConfigResponse.PortalNavItem navItem(
            String id,
            String label,
            String href,
            String icon,
            boolean active) {
        PortalConfigResponse.PortalNavItem item = new PortalConfigResponse.PortalNavItem();
        item.setId(id);
        item.setLabel(label);
        item.setHref(href);
        item.setIcon(icon);
        item.setActive(active);
        return item;
    }

    private List<PortalConfigResponse.PortalApp> buildApplications(User user) {
        List<PortalConfigResponse.PortalApp> apps = new ArrayList<PortalConfigResponse.PortalApp>();
        apps.add(appItem("collab", "项目协作中心", "跨团队协作、任务拆解与进度追踪。", "📌", "system.html#apps"));
        apps.add(appItem("meeting", "会议纪要智能化", "自动生成纪要、行动项与关键决策。", "📝", "system.html#apps"));
        apps.add(appItem("insight", "客户洞察看板", "汇总客户生命周期数据与运营指标。", "📊", "system.html#apps"));
        if (hasAdminAccess(user)) {
            PortalConfigResponse.PortalApp admin = appItem(
                    "admin",
                    "后台管理",
                    "进入权限与员工管理后台。",
                    "🛡️",
                    "../admin-portal/index.html");
            admin.setActionLabel("进入后台");
            apps.add(admin);
        }
        return apps;
    }

    private PortalConfigResponse.PortalApp appItem(
            String id,
            String name,
            String description,
            String icon,
            String url) {
        PortalConfigResponse.PortalApp app = new PortalConfigResponse.PortalApp();
        app.setId(id);
        app.setName(name);
        app.setDescription(description);
        app.setIcon(icon);
        app.setUrl(url);
        app.setActionLabel("进入");
        return app;
    }

    private List<PortalConfigResponse.PortalFeature> buildFeatures(User user) {
        List<PortalConfigResponse.PortalFeature> features = new ArrayList<PortalConfigResponse.PortalFeature>();
        features.add(featureItem("workflow", "流程中心", "查看待办与流程进度。", "🧾", "system.html#features"));
        features.add(featureItem("assistant", "智能助手", "快速访问业务辅助能力。", "⚡", "system.html#features"));
        if (hasAdminAccess(user)) {
            PortalConfigResponse.PortalFeature feature = featureItem(
                    "admin-settings",
                    "管理设置",
                    "仅管理员可见的后台入口。",
                    "🔐",
                    "../admin-portal/index.html");
            feature.setActionLabel("进入后台");
            features.add(feature);
        }
        return features;
    }

    private PortalConfigResponse.PortalFeature featureItem(
            String id,
            String name,
            String description,
            String icon,
            String url) {
        PortalConfigResponse.PortalFeature feature = new PortalConfigResponse.PortalFeature();
        feature.setId(id);
        feature.setName(name);
        feature.setDescription(description);
        feature.setIcon(icon);
        feature.setUrl(url);
        feature.setActionLabel("打开");
        return feature;
    }

    private PortalConfigResponse.PortalUserProfile buildUserProfile(User user) {
        PortalConfigResponse.PortalUserProfile profile = new PortalConfigResponse.PortalUserProfile();
        profile.setName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        profile.setRole(buildRoleLabel(user));
        profile.setAvatar(user.getAvatarUrl());
        profile.setSettingsUrl("system.html#settings");
        profile.setAdminPortalUrl("../admin-portal/index.html");
        profile.setCanAccessAdmin(hasAdminAccess(user));
        return profile;
    }

    private String buildRoleLabel(User user) {
        String title = user.getTitle();
        String department = user.getDepartmentId();
        if (title != null && department != null) {
            return title + " · " + department;
        }
        if (title != null) {
            return title;
        }
        if (department != null) {
            return department;
        }
        return "员工";
    }

    private boolean hasAdminAccess(User user) {
        if (user.getRoles() != null && user.getRoles().contains("admin")) {
            return true;
        }
        return "admin".equalsIgnoreCase(user.getUsername());
    }
}
