const { createApp } = Vue;

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8081',
      adminRoleId: '00000000-0000-0000-0000-000000000101',
      adminPortalUrl: '../admin-portal/index.html',
      currentUser: null,
      userLoading: false,
      userError: '',
      highlights: [
        {
          title: '项目协作中心',
          description: '跨团队协作、任务拆解与进度自动追踪。',
          icon: '📌',
          action: '进入',
        },
        {
          title: '会议纪要智能化',
          description: '自动生成纪要、行动项与关键决策。',
          icon: '📝',
          action: '开始整理',
        },
        {
          title: '客户洞察看板',
          description: '汇总客户生命周期数据与运营指标。',
          icon: '📊',
          action: '查看',
        },
        {
          title: '知识资产地图',
          description: '组织知识结构化归档与检索。',
          icon: '🧠',
          action: '探索',
        },
      ],
      chips: ['高频推荐', '跨部门协作', '自动化流程', '智能总结'],
      assistants: [
        {
          name: '业务汇报助手',
          summary: '一键生成周报、月报与 OKR 追踪。',
          icon: '📅',
        },
        {
          name: '数据洞察助手',
          summary: '自动识别指标异常并给出建议。',
          icon: '📈',
        },
        {
          name: '客户沟通助手',
          summary: '整理客户需求并生成跟进清单。',
          icon: '💬',
        },
      ],
    };
  },
  computed: {
    userDisplayName() {
      if (!this.currentUser) {
        return '未登录';
      }
      return this.currentUser.displayName || this.currentUser.username || '未命名用户';
    },
    userMeta() {
      if (!this.currentUser) {
        return '请先登录';
      }
      if (this.hasAdminAccess) {
        return '系统管理员';
      }
      const segments = [this.currentUser.title, this.currentUser.departmentId].filter(Boolean);
      if (segments.length > 0) {
        return segments.join(' · ');
      }
      return '未分配岗位';
    },
    hasAdminAccess() {
      if (!this.currentUser || !Array.isArray(this.currentUser.roles)) {
        return false;
      }
      return this.currentUser.roles.includes(this.adminRoleId);
    },
  },
  methods: {
    async loadCurrentUser() {
      const token = localStorage.getItem('inhouse_token');
      if (!token) {
        this.userError = '未找到登录凭证，请先登录。';
        return;
      }
      this.userLoading = true;
      this.userError = '';
      try {
        const validateResponse = await fetch(`${this.apiBase}/auth/validate`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ accessToken: token }),
        });
        if (!validateResponse.ok) {
          throw new Error('登录已失效，请重新登录。');
        }
        const userId = (await validateResponse.text()).trim();
        const userResponse = await fetch(`${this.apiBase}/iam/users/${userId}`);
        if (!userResponse.ok) {
          throw new Error('无法加载用户资料，请稍后重试。');
        }
        this.currentUser = await userResponse.json();
      } catch (error) {
        this.userError = error.message || '加载用户信息失败。';
      } finally {
        this.userLoading = false;
      }
    },
    logout() {
      localStorage.removeItem('inhouse_token');
      window.location.href = 'index.html';
    },
  },
  mounted() {
    this.loadCurrentUser();
  },
}).mount('#app');
