const { createApp } = Vue;

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8081',
      accessToken: '',
      tokenExpiresAt: '',
      loginForm: {
        account: '',
        password: '',
      },
      authLoading: false,
      authError: '',
      navItems: [
        { id: 'overview', label: '总览' },
        { id: 'org', label: '组织架构' },
        {
          id: 'user-management',
          label: '用户管理',
          children: [
            { id: 'users', label: '用户列表' },
            { id: 'roles', label: '角色列表' },
            { id: 'permissions', label: '权限列表' },
          ],
        },
        { id: 'policies', label: '权限策略' },
        { id: 'apps', label: '应用注册' },
        { id: 'audits', label: '审计日志' },
      ],
      openNavs: ['user-management'],
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
      showUserForm: false,
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
      editingUserId: null,
      users: [],
      userPagination: {
        page: 1,
        size: 8,
        total: 0,
      },
      userLoading: false,
      userError: '',
      userSuccess: '',
      showRoleForm: false,
      roleForm: {
        name: '',
        description: '',
      },
      editingRoleId: null,
      roles: [],
      rolePagination: {
        page: 1,
        size: 8,
        total: 0,
      },
      roleLoading: false,
      roleError: '',
      roleSuccess: '',
      showPermissionForm: false,
      permissionForm: {
        app: '',
        feature: '',
        resource: '',
        description: '',
      },
      editingPermissionId: null,
      permissions: [],
      permissionPagination: {
        page: 1,
        size: 8,
        total: 0,
      },
      permissionLoading: false,
      permissionError: '',
      permissionSuccess: '',
      searchText: '',
    };
  },
  computed: {
    isAuthenticated() {
      return Boolean(this.accessToken);
    },
    viewMeta() {
      const map = {
        overview: {
          breadcrumb: '总览',
          title: '组织与权限运营控制台',
          subtitle: '统一管理员工信息、权限策略与应用接入状态。',
        },
        users: {
          breadcrumb: '用户管理 / 用户列表',
          title: '用户管理中心',
          subtitle: '维护账号档案，统一组织与权限基础数据。',
        },
        roles: {
          breadcrumb: '用户管理 / 角色列表',
          title: '角色管理',
          subtitle: '沉淀岗位角色，复用权限授权模板。',
        },
        permissions: {
          breadcrumb: '用户管理 / 权限列表',
          title: '权限清单',
          subtitle: '集中维护可授权的应用与功能点。',
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
    userTotalPages() {
      return Math.max(1, Math.ceil(this.userPagination.total / this.userPagination.size));
    },
    roleTotalPages() {
      return Math.max(1, Math.ceil(this.rolePagination.total / this.rolePagination.size));
    },
    permissionTotalPages() {
      return Math.max(1, Math.ceil(this.permissionPagination.total / this.permissionPagination.size));
    },
  },
  methods: {
    setView(viewId) {
      this.currentView = viewId;
      if (!this.isAuthenticated) {
        return;
      }
      if (viewId === 'users') {
        this.loadUsers();
      }
      if (viewId === 'roles') {
        this.loadRoles();
      }
      if (viewId === 'permissions') {
        this.loadPermissions();
      }
    },
    toggleNav(navId) {
      if (this.openNavs.includes(navId)) {
        this.openNavs = this.openNavs.filter((id) => id !== navId);
      } else {
        this.openNavs = [...this.openNavs, navId];
      }
    },
    isNavOpen(navId) {
      return this.openNavs.includes(navId);
    },
    isGroupActive(navItem) {
      if (!navItem.children) {
        return this.currentView === navItem.id;
      }
      return navItem.children.some((child) => child.id === this.currentView);
    },
    buildHeaders() {
      const headers = {
        'Content-Type': 'application/json',
      };
      if (this.accessToken) {
        headers.Authorization = `Bearer ${this.accessToken}`;
      }
      return headers;
    },
    async login() {
      this.authError = '';
      if (!this.loginForm.account.trim() || !this.loginForm.password) {
        this.authError = '请输入账号和密码。';
        return;
      }
      this.authLoading = true;
      try {
        const response = await fetch(`${this.apiBase}/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            account: this.loginForm.account.trim(),
            password: this.loginForm.password,
          }),
        });
        if (!response.ok) {
          throw new Error('登录失败，请检查账号或密码。');
        }
        const payload = await response.json();
        this.accessToken = payload.accessToken;
        this.tokenExpiresAt = payload.expiresAt;
        localStorage.setItem('iamToken', this.accessToken);
        if (this.tokenExpiresAt) {
          localStorage.setItem('iamTokenExpiresAt', this.tokenExpiresAt);
        }
        this.loginForm.password = '';
        this.currentView = 'users';
        await this.loadUsers();
      } catch (error) {
        this.authError = error.message || '登录失败，请稍后再试。';
      } finally {
        this.authLoading = false;
      }
    },
    logout() {
      this.accessToken = '';
      this.tokenExpiresAt = '';
      localStorage.removeItem('iamToken');
      localStorage.removeItem('iamTokenExpiresAt');
      this.users = [];
      this.roles = [];
      this.permissions = [];
      this.showUserForm = false;
      this.showRoleForm = false;
      this.showPermissionForm = false;
    },
    loadStoredToken() {
      const token = localStorage.getItem('iamToken');
      const expiresAt = localStorage.getItem('iamTokenExpiresAt');
      if (!token) {
        return;
      }
      if (expiresAt) {
        const expiresAtDate = new Date(expiresAt);
        if (!Number.isNaN(expiresAtDate.getTime()) && Date.now() > expiresAtDate.getTime()) {
          this.logout();
          return;
        }
      }
      this.accessToken = token;
      this.tokenExpiresAt = expiresAt || '';
    },
    handleAuthFailure(error, fallbackMessage) {
      if (error && error.message === 'UNAUTHORIZED') {
        this.logout();
        this.authError = '登录已失效，请重新登录。';
        return;
      }
      return error?.message || fallbackMessage;
    },
    async loadUsers() {
      this.userLoading = true;
      this.userError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/users?page=${this.userPagination.page}&size=${this.userPagination.size}`,
          { headers: this.buildHeaders() },
        );
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('加载用户列表失败');
        }
        const payload = await response.json();
        this.users = payload.items || [];
        this.userPagination.page = payload.page || 1;
        this.userPagination.size = payload.size || this.userPagination.size;
        this.userPagination.total = payload.total || 0;
      } catch (error) {
        this.userError = this.handleAuthFailure(error, '加载用户失败，请稍后再试。');
      } finally {
        this.userLoading = false;
      }
    },
    async submitUser() {
      this.userError = '';
      this.userSuccess = '';
      const requiredFields = ['employeeId', 'username', 'displayName'];
      const missing = requiredFields.filter((key) => !this.userForm[key]);
      if (missing.length > 0) {
        this.userError = '请补全员工编号、账号与姓名。';
        return;
      }
      if (!this.editingUserId && !this.userForm.password) {
        this.userError = '创建新账号需要填写初始密码。';
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
      };
      if (this.userForm.password) {
        payload.password = this.userForm.password;
      }
      this.userLoading = true;
      try {
        const requestUrl = this.editingUserId
          ? `${this.apiBase}/iam/users/${this.editingUserId}`
          : `${this.apiBase}/iam/users`;
        const method = this.editingUserId ? 'PUT' : 'POST';
        const response = await fetch(requestUrl, {
          method,
          headers: this.buildHeaders(),
          body: JSON.stringify(payload),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error(this.editingUserId ? '更新用户失败' : '创建用户失败');
        }
        await response.json();
        this.userSuccess = this.editingUserId ? '用户信息已更新。' : '用户已创建并写入数据库。';
        this.resetUserForm();
        this.showUserForm = false;
        await this.loadUsers();
      } catch (error) {
        this.userError = this.handleAuthFailure(error, '操作失败，请稍后再试。');
      } finally {
        this.userLoading = false;
      }
    },
    startEditUser(user) {
      this.editingUserId = user.id;
      this.showUserForm = true;
      this.userForm = {
        employeeId: user.employeeId || '',
        username: user.username || '',
        displayName: user.displayName || '',
        email: user.email || '',
        phone: user.phone || '',
        departmentId: user.departmentId || '',
        title: user.title || '',
        managerId: user.managerId || '',
        location: user.location || '',
        status: user.status || 'active',
        hireDate: user.hireDate ? user.hireDate.slice(0, 10) : '',
        password: '',
      };
    },
    async deleteUser(user) {
      if (!window.confirm(`确认删除用户 ${user.displayName || user.username} 吗？`)) {
        return;
      }
      this.userLoading = true;
      this.userError = '';
      try {
        const response = await fetch(`${this.apiBase}/iam/users/${user.id}`, {
          method: 'DELETE',
          headers: this.buildHeaders(),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('删除用户失败');
        }
        this.userSuccess = '用户已删除。';
        await this.loadUsers();
      } catch (error) {
        this.userError = this.handleAuthFailure(error, '删除用户失败，请稍后再试。');
      } finally {
        this.userLoading = false;
      }
    },
    changeUserPage(page) {
      if (page < 1 || page > this.userTotalPages) {
        return;
      }
      this.userPagination.page = page;
      this.loadUsers();
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
      this.editingUserId = null;
    },
    toggleUserForm() {
      if (this.showUserForm) {
        this.resetUserForm();
      }
      this.showUserForm = !this.showUserForm;
    },
    async loadRoles() {
      this.roleLoading = true;
      this.roleError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/roles?page=${this.rolePagination.page}&size=${this.rolePagination.size}`,
          { headers: this.buildHeaders() },
        );
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('加载角色列表失败');
        }
        const payload = await response.json();
        this.roles = payload.items || [];
        this.rolePagination.page = payload.page || 1;
        this.rolePagination.size = payload.size || this.rolePagination.size;
        this.rolePagination.total = payload.total || 0;
      } catch (error) {
        this.roleError = this.handleAuthFailure(error, '加载角色失败，请稍后再试。');
      } finally {
        this.roleLoading = false;
      }
    },
    async submitRole() {
      this.roleError = '';
      this.roleSuccess = '';
      if (!this.roleForm.name.trim()) {
        this.roleError = '请填写角色名称。';
        return;
      }
      this.roleLoading = true;
      try {
        const requestUrl = this.editingRoleId
          ? `${this.apiBase}/iam/roles/${this.editingRoleId}`
          : `${this.apiBase}/iam/roles`;
        const method = this.editingRoleId ? 'PUT' : 'POST';
        const response = await fetch(requestUrl, {
          method,
          headers: this.buildHeaders(),
          body: JSON.stringify({
            name: this.roleForm.name.trim(),
            description: this.roleForm.description.trim() || null,
          }),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error(this.editingRoleId ? '更新角色失败' : '创建角色失败');
        }
        await response.json();
        this.roleSuccess = this.editingRoleId ? '角色已更新。' : '角色已创建。';
        this.resetRoleForm();
        this.showRoleForm = false;
        await this.loadRoles();
      } catch (error) {
        this.roleError = this.handleAuthFailure(error, '角色操作失败，请稍后再试。');
      } finally {
        this.roleLoading = false;
      }
    },
    startEditRole(role) {
      this.editingRoleId = role.id;
      this.showRoleForm = true;
      this.roleForm = {
        name: role.name || '',
        description: role.description || '',
      };
    },
    async deleteRole(role) {
      if (!window.confirm(`确认删除角色 ${role.name} 吗？`)) {
        return;
      }
      this.roleLoading = true;
      this.roleError = '';
      try {
        const response = await fetch(`${this.apiBase}/iam/roles/${role.id}`, {
          method: 'DELETE',
          headers: this.buildHeaders(),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('删除角色失败');
        }
        this.roleSuccess = '角色已删除。';
        await this.loadRoles();
      } catch (error) {
        this.roleError = this.handleAuthFailure(error, '删除角色失败，请稍后再试。');
      } finally {
        this.roleLoading = false;
      }
    },
    changeRolePage(page) {
      if (page < 1 || page > this.roleTotalPages) {
        return;
      }
      this.rolePagination.page = page;
      this.loadRoles();
    },
    resetRoleForm() {
      this.roleForm = {
        name: '',
        description: '',
      };
      this.editingRoleId = null;
    },
    toggleRoleForm() {
      if (this.showRoleForm) {
        this.resetRoleForm();
      }
      this.showRoleForm = !this.showRoleForm;
    },
    async loadPermissions() {
      this.permissionLoading = true;
      this.permissionError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/permissions?page=${this.permissionPagination.page}&size=${this.permissionPagination.size}`,
          { headers: this.buildHeaders() },
        );
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('加载权限列表失败');
        }
        const payload = await response.json();
        this.permissions = payload.items || [];
        this.permissionPagination.page = payload.page || 1;
        this.permissionPagination.size = payload.size || this.permissionPagination.size;
        this.permissionPagination.total = payload.total || 0;
      } catch (error) {
        this.permissionError = this.handleAuthFailure(error, '加载权限失败，请稍后再试。');
      } finally {
        this.permissionLoading = false;
      }
    },
    async submitPermission() {
      this.permissionError = '';
      this.permissionSuccess = '';
      if (!this.permissionForm.app.trim() || !this.permissionForm.feature.trim()) {
        this.permissionError = '请填写应用名称与功能点。';
        return;
      }
      this.permissionLoading = true;
      try {
        const requestUrl = this.editingPermissionId
          ? `${this.apiBase}/iam/permissions/${this.editingPermissionId}`
          : `${this.apiBase}/iam/permissions`;
        const method = this.editingPermissionId ? 'PUT' : 'POST';
        const response = await fetch(requestUrl, {
          method,
          headers: this.buildHeaders(),
          body: JSON.stringify({
            app: this.permissionForm.app.trim(),
            feature: this.permissionForm.feature.trim(),
            resource: this.permissionForm.resource.trim() || null,
            description: this.permissionForm.description.trim() || null,
          }),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error(this.editingPermissionId ? '更新权限失败' : '创建权限失败');
        }
        await response.json();
        this.permissionSuccess = this.editingPermissionId ? '权限已更新。' : '权限已创建。';
        this.resetPermissionForm();
        this.showPermissionForm = false;
        await this.loadPermissions();
      } catch (error) {
        this.permissionError = this.handleAuthFailure(error, '权限操作失败，请稍后再试。');
      } finally {
        this.permissionLoading = false;
      }
    },
    startEditPermission(permission) {
      this.editingPermissionId = permission.id;
      this.showPermissionForm = true;
      this.permissionForm = {
        app: permission.app || '',
        feature: permission.feature || '',
        resource: permission.resource || '',
        description: permission.description || '',
      };
    },
    async deletePermission(permission) {
      if (!window.confirm(`确认删除权限 ${permission.app}:${permission.feature} 吗？`)) {
        return;
      }
      this.permissionLoading = true;
      this.permissionError = '';
      try {
        const response = await fetch(`${this.apiBase}/iam/permissions/${permission.id}`, {
          method: 'DELETE',
          headers: this.buildHeaders(),
        });
        if (response.status === 401) {
          throw new Error('UNAUTHORIZED');
        }
        if (!response.ok) {
          throw new Error('删除权限失败');
        }
        this.permissionSuccess = '权限已删除。';
        await this.loadPermissions();
      } catch (error) {
        this.permissionError = this.handleAuthFailure(error, '删除权限失败，请稍后再试。');
      } finally {
        this.permissionLoading = false;
      }
    },
    changePermissionPage(page) {
      if (page < 1 || page > this.permissionTotalPages) {
        return;
      }
      this.permissionPagination.page = page;
      this.loadPermissions();
    },
    resetPermissionForm() {
      this.permissionForm = {
        app: '',
        feature: '',
        resource: '',
        description: '',
      };
      this.editingPermissionId = null;
    },
    togglePermissionForm() {
      if (this.showPermissionForm) {
        this.resetPermissionForm();
      }
      this.showPermissionForm = !this.showPermissionForm;
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
    this.loadStoredToken();
    if (this.isAuthenticated) {
      this.loadUsers();
    }
  },
}).mount('#admin');
