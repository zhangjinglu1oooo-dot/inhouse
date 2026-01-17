package com.inhouse.iam;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工门户配置返回对象。
 */
public class PortalConfigResponse {
    private PortalMeta meta;
    private List<PortalNavItem> navigation = new ArrayList<PortalNavItem>();
    private List<PortalApp> applications = new ArrayList<PortalApp>();
    private List<PortalFeature> features = new ArrayList<PortalFeature>();
    private PortalUserProfile user;

    public PortalMeta getMeta() {
        return meta;
    }

    public void setMeta(PortalMeta meta) {
        this.meta = meta;
    }

    public List<PortalNavItem> getNavigation() {
        return navigation;
    }

    public void setNavigation(List<PortalNavItem> navigation) {
        this.navigation = navigation;
    }

    public List<PortalApp> getApplications() {
        return applications;
    }

    public void setApplications(List<PortalApp> applications) {
        this.applications = applications;
    }

    public List<PortalFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<PortalFeature> features) {
        this.features = features;
    }

    public PortalUserProfile getUser() {
        return user;
    }

    public void setUser(PortalUserProfile user) {
        this.user = user;
    }

    public static class PortalMeta {
        private String breadcrumb;
        private String title;
        private String subtitle;

        public String getBreadcrumb() {
            return breadcrumb;
        }

        public void setBreadcrumb(String breadcrumb) {
            this.breadcrumb = breadcrumb;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }

    public static class PortalNavItem {
        private String id;
        private String label;
        private String href;
        private String icon;
        private boolean enabled = true;
        private boolean active;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    public static class PortalApp {
        private String id;
        private String name;
        private String description;
        private String icon;
        private String url;
        private String actionLabel;
        private boolean enabled = true;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getActionLabel() {
            return actionLabel;
        }

        public void setActionLabel(String actionLabel) {
            this.actionLabel = actionLabel;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class PortalFeature {
        private String id;
        private String name;
        private String description;
        private String icon;
        private String url;
        private String actionLabel;
        private boolean enabled = true;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getActionLabel() {
            return actionLabel;
        }

        public void setActionLabel(String actionLabel) {
            this.actionLabel = actionLabel;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class PortalUserProfile {
        private String name;
        private String role;
        private String avatar;
        private String settingsUrl;
        private String adminPortalUrl;
        private boolean canAccessAdmin;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSettingsUrl() {
            return settingsUrl;
        }

        public void setSettingsUrl(String settingsUrl) {
            this.settingsUrl = settingsUrl;
        }

        public String getAdminPortalUrl() {
            return adminPortalUrl;
        }

        public void setAdminPortalUrl(String adminPortalUrl) {
            this.adminPortalUrl = adminPortalUrl;
        }

        public boolean isCanAccessAdmin() {
            return canAccessAdmin;
        }

        public void setCanAccessAdmin(boolean canAccessAdmin) {
            this.canAccessAdmin = canAccessAdmin;
        }
    }
}
