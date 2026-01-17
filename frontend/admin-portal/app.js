const { createApp } = Vue;

createApp({
  data() {
    return {
      apiBase: 'http://localhost:8080',
      isAuthenticated: false,
      loginForm: {
        username: '',
        password: '',
        remember: true,
      },
      loginError: '',
      loginLoading: false,
      adminProfile: {
        name: '系统管理员',
        role: '安全与合规中心',
      },
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
    initializeAuth() {
      const savedSession = window.localStorage.getItem('inhouse-admin-session');
      if (!savedSession) {
        this.isAuthenticated = false;
        return;
      }
      try {
        const parsed = JSON.parse(savedSession);
        if (parsed && parsed.name) {
          this.adminProfile = {
            name: parsed.name,
            role: parsed.role || this.adminProfile.role,
          };
          this.isAuthenticated = true;
          return;
        }
      } catch (error) {
        window.localStorage.removeItem('inhouse-admin-session');
      }
      this.isAuthenticated = false;
    },
    async submitLogin() {
      this.loginError = '';
      if (!this.loginForm.username.trim() || !this.loginForm.password) {
        this.loginError = '请输入管理员账号与密码。';
        return;
      }
      this.loginLoading = true;
      try {
        const demoAccount = {
          username: 'admin',
          password: 'Admin@123',
          name: '系统管理员',
          role: '安全与合规中心',
        };
        if (
          this.loginForm.username.trim() !== demoAccount.username ||
          this.loginForm.password !== demoAccount.password
        ) {
          throw new Error('账号或密码错误，请重试。');
        }
        this.adminProfile = {
          name: demoAccount.name,
          role: demoAccount.role,
        };
        if (this.loginForm.remember) {
          window.localStorage.setItem('inhouse-admin-session', JSON.stringify(this.adminProfile));
        }
        this.isAuthenticated = true;
        this.loginForm.password = '';
        this.loadUsers();
      } catch (error) {
        this.loginError = error.message || '登录失败，请稍后再试。';
      } finally {
        this.loginLoading = false;
      }
    },
    logout() {
      window.localStorage.removeItem('inhouse-admin-session');
      this.isAuthenticated = false;
      this.loginForm = {
        username: '',
        password: '',
        remember: true,
      };
      this.searchText = '';
      this.currentView = 'users';
    },
    setView(viewId) {
      this.currentView = viewId;
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
    async loadUsers() {
      this.userLoading = true;
      this.userError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/users?page=${this.userPagination.page}&size=${this.userPagination.size}`,
        );
        if (!response.ok) {
          throw new Error('加载用户列表失败');
        }
        const payload = await response.json();
        this.users = payload.items || [];
        this.userPagination.page = payload.page || 1;
        this.userPagination.size = payload.size || this.userPagination.size;
        this.userPagination.total = payload.total || 0;
      } catch (error) {
        this.userError = error.message || '加载用户失败，请稍后再试。';
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
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        });
        if (!response.ok) {
          throw new Error(this.editingUserId ? '更新用户失败' : '创建用户失败');
        }
        await response.json();
        this.userSuccess = this.editingUserId ? '用户信息已更新。' : '用户已创建并写入数据库。';
        this.resetUserForm();
        await this.loadUsers();
      } catch (error) {
        this.userError = error.message || '操作失败，请稍后再试。';
      } finally {
        this.userLoading = false;
      }
    },
    startEditUser(user) {
      this.editingUserId = user.id;
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
        });
        if (!response.ok) {
          throw new Error('删除用户失败');
        }
        this.userSuccess = '用户已删除。';
        await this.loadUsers();
      } catch (error) {
        this.userError = error.message || '删除用户失败，请稍后再试。';
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
    async loadRoles() {
      this.roleLoading = true;
      this.roleError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/roles?page=${this.rolePagination.page}&size=${this.rolePagination.size}`,
        );
        if (!response.ok) {
          throw new Error('加载角色列表失败');
        }
        const payload = await response.json();
        this.roles = payload.items || [];
        this.rolePagination.page = payload.page || 1;
        this.rolePagination.size = payload.size || this.rolePagination.size;
        this.rolePagination.total = payload.total || 0;
      } catch (error) {
        this.roleError = error.message || '加载角色失败，请稍后再试。';
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
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            name: this.roleForm.name.trim(),
            description: this.roleForm.description.trim() || null,
          }),
        });
        if (!response.ok) {
          throw new Error(this.editingRoleId ? '更新角色失败' : '创建角色失败');
        }
        await response.json();
        this.roleSuccess = this.editingRoleId ? '角色已更新。' : '角色已创建。';
        this.resetRoleForm();
        await this.loadRoles();
      } catch (error) {
        this.roleError = error.message || '角色操作失败，请稍后再试。';
      } finally {
        this.roleLoading = false;
      }
    },
    startEditRole(role) {
      this.editingRoleId = role.id;
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
        });
        if (!response.ok) {
          throw new Error('删除角色失败');
        }
        this.roleSuccess = '角色已删除。';
        await this.loadRoles();
      } catch (error) {
        this.roleError = error.message || '删除角色失败，请稍后再试。';
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
    async loadPermissions() {
      this.permissionLoading = true;
      this.permissionError = '';
      try {
        const response = await fetch(
          `${this.apiBase}/iam/permissions?page=${this.permissionPagination.page}&size=${this.permissionPagination.size}`,
        );
        if (!response.ok) {
          throw new Error('加载权限列表失败');
        }
        const payload = await response.json();
        this.permissions = payload.items || [];
        this.permissionPagination.page = payload.page || 1;
        this.permissionPagination.size = payload.size || this.permissionPagination.size;
        this.permissionPagination.total = payload.total || 0;
      } catch (error) {
        this.permissionError = error.message || '加载权限失败，请稍后再试。';
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
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            app: this.permissionForm.app.trim(),
            feature: this.permissionForm.feature.trim(),
            resource: this.permissionForm.resource.trim() || null,
            description: this.permissionForm.description.trim() || null,
          }),
        });
        if (!response.ok) {
          throw new Error(this.editingPermissionId ? '更新权限失败' : '创建权限失败');
        }
        await response.json();
        this.permissionSuccess = this.editingPermissionId ? '权限已更新。' : '权限已创建。';
        this.resetPermissionForm();
        await this.loadPermissions();
      } catch (error) {
        this.permissionError = error.message || '权限操作失败，请稍后再试。';
      } finally {
        this.permissionLoading = false;
      }
    },
    startEditPermission(permission) {
      this.editingPermissionId = permission.id;
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
        });
        if (!response.ok) {
          throw new Error('删除权限失败');
        }
        this.permissionSuccess = '权限已删除。';
        await this.loadPermissions();
      } catch (error) {
        this.permissionError = error.message || '删除权限失败，请稍后再试。';
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
    this.initializeAuth();
    if (this.isAuthenticated) {
      this.loadUsers();
    }
  },
}).mount('#admin');
