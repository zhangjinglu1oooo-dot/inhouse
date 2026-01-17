const { createApp } = Vue;

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8080',
      navItems: [
        { id: 'overview', label: '总览' },
        { id: 'org', label: '组织架构' },
        { id: 'users', label: '用户管理' },
        { id: 'policies', label: '权限策略' },
        { id: 'apps', label: '应用注册' },
        { id: 'audits', label: '审计日志' },
      ],
      currentView: 'users',
      stats: [
        { label: '在职员工', value: '2,418', note: '本月新增 36 人' },
        { label: '应用接入', value: '86', note: '通过审批 4 个' },
        { label: '策略数量', value: '312', note: '待审核 7 条' },
        { label: '风险预警', value: '3', note: '高危 1 条' },
      ],
      employeeFields: [
        '员工编号',
        '姓名',
        '邮箱',
        '手机号',
        '部门',
        '职位',
        '直属上级',
        '工位地点',
        '入职日期',
        '状态',
      ],
      audits: [
        { title: '审批通过：客户数据访问策略', meta: '张颖 · 2 分钟前' },
        { title: '角色变更：市场部内容负责人', meta: '系统 · 25 分钟前' },
        { title: '异常提醒：多地登录检测', meta: '安全中心 · 1 小时前' },
      ],
      userForm: {
        employeeId: '',
        username: '',
        displayName: '',
        email: '',
        phone: '',
        departmentId: '',
        title: '',
        managerId: '',
        location: '',
        status: 'active',
        hireDate: '',
        password: '',
      },
      users: [],
      userLoading: false,
      userError: '',
      userSuccess: '',
      searchText: '',
    };
  },
  computed: {
    viewMeta() {
      const map = {
        overview: {
          breadcrumb: '总览',
          title: '组织与权限运营控制台',
          subtitle: '统一管理员工信息、权限策略与应用接入状态。',
        },
        users: {
          breadcrumb: '用户管理',
          title: '用户管理中心',
          subtitle: '维护账号档案，统一组织与权限基础数据。',
        },
        org: {
          breadcrumb: '组织架构',
          title: '组织架构',
          subtitle: '规划部门与汇报关系，支撑权限体系。',
        },
        policies: {
          breadcrumb: '权限策略',
          title: '权限策略',
          subtitle: '建立策略模板，统一权限申请流程。',
        },
        apps: {
          breadcrumb: '应用注册',
          title: '应用注册',
          subtitle: '登记应用资产，追踪接入进度。',
        },
        audits: {
          breadcrumb: '审计日志',
          title: '审计日志',
          subtitle: '追踪关键操作与合规记录。',
        },
      };
      return map[this.currentView] || map.overview;
    },
    filteredUsers() {
      if (!this.searchText.trim()) {
        return this.users;
      }
      const keyword = this.searchText.trim().toLowerCase();
      return this.users.filter((user) => {
        return [
          user.displayName,
          user.username,
          user.employeeId,
          user.email,
          user.phone,
          user.departmentId,
          user.title,
        ]
          .filter(Boolean)
          .some((value) => value.toLowerCase().includes(keyword));
      });
    },
  },
  methods: {
    setView(viewId) {
      this.currentView = viewId;
      if (viewId === 'users') {
        this.loadUsers();
      }
    },
    async loadUsers() {
      this.userLoading = true;
      this.userError = '';
      try {
        const response = await fetch(`${this.apiBase}/iam/users`);
        if (!response.ok) {
          throw new Error('加载用户列表失败');
        }
        this.users = await response.json();
      } catch (error) {
        this.userError = error.message || '加载用户失败，请稍后再试。';
      } finally {
        this.userLoading = false;
      }
    },
    async createUser() {
      this.userError = '';
      this.userSuccess = '';
      const requiredFields = ['employeeId', 'username', 'displayName', 'password'];
      const missing = requiredFields.filter((key) => !this.userForm[key]);
      if (missing.length > 0) {
        this.userError = '请补全员工编号、账号、姓名与初始密码。';
        return;
      }
      const payload = {
        employeeId: this.userForm.employeeId.trim(),
        username: this.userForm.username.trim(),
        displayName: this.userForm.displayName.trim(),
        email: this.userForm.email.trim() || null,
        phone: this.userForm.phone.trim() || null,
        departmentId: this.userForm.departmentId.trim() || null,
        title: this.userForm.title.trim() || null,
        managerId: this.userForm.managerId.trim() || null,
        location: this.userForm.location.trim() || null,
        status: this.userForm.status || 'active',
        hireDate: this.userForm.hireDate || null,
        password: this.userForm.password,
      };
      this.userLoading = true;
      try {
        const response = await fetch(`${this.apiBase}/iam/users`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        });
        if (!response.ok) {
          throw new Error('创建用户失败');
        }
        await response.json();
        this.userSuccess = '用户已创建并写入数据库。';
        this.resetUserForm();
        await this.loadUsers();
      } catch (error) {
        this.userError = error.message || '创建用户失败，请稍后再试。';
      } finally {
        this.userLoading = false;
      }
    },
    resetUserForm() {
      this.userForm = {
        employeeId: '',
        username: '',
        displayName: '',
        email: '',
        phone: '',
        departmentId: '',
        title: '',
        managerId: '',
        location: '',
        status: 'active',
        hireDate: '',
        password: '',
      };
    },
    formatDate(value) {
      if (!value) {
        return '-';
      }
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) {
        return value;
      }
      return date.toLocaleDateString('zh-CN');
    },
    statusLabel(status) {
      const map = {
        active: '启用',
        inactive: '停用',
        locked: '锁定',
      };
      return map[status] || status || '-';
    },
  },
  mounted() {
    this.loadUsers();
  },
}).mount('#admin');
