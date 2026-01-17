const { createApp } = Vue;

const resolveAuthToken = () => {
  return localStorage.getItem('inhouse_token') || sessionStorage.getItem('inhouse_token');
};

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8080',
      portalMeta: {
        breadcrumb: '系统工作台',
        title: '员工工作台',
        subtitle: '根据后台配置展示导航、应用与功能入口。',
      },
      navItems: [],
      applications: [],
      features: [],
      userProfile: {
        name: '未登录用户',
        role: '',
        avatar:
          'https://images.unsplash.com/photo-1544723795-3fb6469f5b39?q=80&w=200&auto=format&fit=crop',
      },
      settingsUrl: 'system.html#settings',
      adminPortalUrl: '../admin-portal/index.html',
      canAccessAdmin: false,
      portalLoading: false,
      portalError: '',
      sidebarCollapsed: false,
    };
  },
  computed: {
    visibleNavItems() {
      return this.navItems.filter((item) => item && item.enabled !== false);
    },
    visibleApplications() {
      return this.applications.filter((item) => item && item.enabled !== false);
    },
    visibleFeatures() {
      return this.features.filter((item) => item && item.enabled !== false);
    },
  },
  methods: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
    },
    async loadPortalConfig() {
      this.portalLoading = true;
      this.portalError = '';
      try {
        const token = resolveAuthToken();
        if (!token) {
          throw new Error('请先登录后查看工作台');
        }
        const response = await fetch(`${this.apiBase}/iam/portal-config`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error('加载门户配置失败');
        }
        const payload = await response.json();
        this.portalMeta = {
          breadcrumb: payload?.meta?.breadcrumb || '系统工作台',
          title: payload?.meta?.title || '员工工作台',
          subtitle: payload?.meta?.subtitle || '根据后台配置展示导航、应用与功能入口。',
        };
        this.navItems = payload?.navigation || [];
        this.applications = payload?.applications || [];
        this.features = payload?.features || [];
        this.userProfile = {
          name: payload?.user?.name || '未登录用户',
          role: payload?.user?.role || '',
          avatar:
            payload?.user?.avatar ||
            'https://images.unsplash.com/photo-1544723795-3fb6469f5b39?q=80&w=200&auto=format&fit=crop',
        };
        this.settingsUrl = payload?.user?.settingsUrl || 'system.html#settings';
        this.adminPortalUrl = payload?.user?.adminPortalUrl || '../admin-portal/index.html';
        this.canAccessAdmin = Boolean(payload?.user?.canAccessAdmin);
      } catch (error) {
        this.portalError = error?.message || '加载配置失败，请稍后再试。';
        this.navItems = [];
        this.applications = [];
        this.features = [];
      } finally {
        this.portalLoading = false;
      }
    },
  },
  mounted() {
    this.loadPortalConfig();
  },
}).mount('#app');
