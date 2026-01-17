-- Inhouse 系统初始化数据库与表结构
CREATE DATABASE IF NOT EXISTS inhouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inhouse;

-- IAM 用户主表（认证信息）
CREATE TABLE IF NOT EXISTS iam_users (
    id VARCHAR(64) PRIMARY KEY COMMENT '用户 ID',
    employee_id VARCHAR(64) NOT NULL COMMENT '员工编号',
    username VARCHAR(64) NOT NULL COMMENT '登录名',
    password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希',
    password_salt VARCHAR(128) NOT NULL COMMENT '密码盐值',
    display_name VARCHAR(128) COMMENT '展示名称',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(32) COMMENT '手机号',
    status VARCHAR(32) DEFAULT 'active' COMMENT '账户状态',
    avatar_url VARCHAR(255) COMMENT '头像地址',
    last_login_at DATETIME COMMENT '最近登录时间',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME COMMENT '更新时间',
    UNIQUE KEY uq_iam_users_username (username),
    UNIQUE KEY uq_iam_users_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 用户认证主表';

-- IAM 部门表
CREATE TABLE IF NOT EXISTS iam_departments (
    id VARCHAR(64) PRIMARY KEY COMMENT '部门 ID',
    name VARCHAR(128) NOT NULL COMMENT '部门名称',
    parent_id VARCHAR(64) COMMENT '上级部门 ID',
    created_at DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 组织部门表';

-- IAM 用户档案表（组织与雇佣信息）
CREATE TABLE IF NOT EXISTS iam_user_profiles (
    user_id VARCHAR(64) PRIMARY KEY COMMENT '用户 ID',
    department_id VARCHAR(64) COMMENT '部门 ID',
    title VARCHAR(128) COMMENT '职位名称',
    manager_id VARCHAR(64) COMMENT '直属上级 ID',
    location VARCHAR(128) COMMENT '工作地点',
    hire_date DATE COMMENT '入职日期',
    CONSTRAINT fk_iam_user_profiles_user FOREIGN KEY (user_id) REFERENCES iam_users(id),
    CONSTRAINT fk_iam_user_profiles_department FOREIGN KEY (department_id) REFERENCES iam_departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 用户档案表';

-- IAM 用户扩展属性表
CREATE TABLE IF NOT EXISTS iam_user_attributes (
    user_id VARCHAR(64) NOT NULL COMMENT '用户 ID',
    attr_key VARCHAR(64) NOT NULL COMMENT '属性键',
    attr_value TEXT COMMENT '属性值（JSON 文本）',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (user_id, attr_key),
    CONSTRAINT fk_iam_user_attributes_user FOREIGN KEY (user_id) REFERENCES iam_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 用户自定义属性';

-- IAM 角色表
CREATE TABLE IF NOT EXISTS iam_roles (
    id VARCHAR(64) PRIMARY KEY COMMENT '角色 ID',
    name VARCHAR(64) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uq_iam_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 角色表';

-- IAM 权限表
CREATE TABLE IF NOT EXISTS iam_permissions (
    id VARCHAR(64) PRIMARY KEY COMMENT '权限 ID',
    app VARCHAR(128) NOT NULL COMMENT '应用名称',
    feature VARCHAR(128) NOT NULL COMMENT '功能点',
    resource VARCHAR(128) COMMENT '资源标识',
    description VARCHAR(255) COMMENT '权限描述',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE KEY uq_iam_permissions_scope (app, feature, resource)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 权限表';

-- IAM 用户-角色关联表
CREATE TABLE IF NOT EXISTS iam_user_roles (
    user_id VARCHAR(64) NOT NULL COMMENT '用户 ID',
    role_id VARCHAR(64) NOT NULL COMMENT '角色 ID',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_iam_user_roles_user FOREIGN KEY (user_id) REFERENCES iam_users(id),
    CONSTRAINT fk_iam_user_roles_role FOREIGN KEY (role_id) REFERENCES iam_roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 用户与角色关联';

-- IAM 角色-权限关联表
CREATE TABLE IF NOT EXISTS iam_role_permissions (
    role_id VARCHAR(64) NOT NULL COMMENT '角色 ID',
    permission_id VARCHAR(64) NOT NULL COMMENT '权限 ID',
    effect VARCHAR(16) NOT NULL DEFAULT 'allow' COMMENT '允许或拒绝',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_iam_role_permissions_role FOREIGN KEY (role_id) REFERENCES iam_roles(id),
    CONSTRAINT fk_iam_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES iam_permissions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IAM 角色权限关联';

-- 应用注册表
CREATE TABLE IF NOT EXISTS registry_apps (
    id VARCHAR(64) PRIMARY KEY COMMENT '应用 ID',
    name VARCHAR(128) NOT NULL COMMENT '应用名称',
    version VARCHAR(64) COMMENT '版本号',
    status VARCHAR(32) COMMENT '状态',
    entry_url VARCHAR(255) COMMENT '入口地址',
    description TEXT COMMENT '应用描述',
    features JSON COMMENT '功能清单',
    tags JSON COMMENT '标签',
    created_at DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用注册表';

-- 事件表
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(64) PRIMARY KEY COMMENT '事件 ID',
    topic VARCHAR(128) NOT NULL COMMENT '事件主题',
    payload JSON COMMENT '事件载荷',
    created_at DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS audits (
    id VARCHAR(64) PRIMARY KEY COMMENT '审计 ID',
    action VARCHAR(128) NOT NULL COMMENT '操作动作',
    actor VARCHAR(128) NOT NULL COMMENT '操作者',
    target VARCHAR(128) COMMENT '操作对象',
    detail JSON COMMENT '详细信息',
    created_at DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 初始化超级管理员账号与最高权限
INSERT IGNORE INTO iam_users (
    id,
    employee_id,
    username,
    password_hash,
    password_salt,
    display_name,
    email,
    phone,
    status,
    avatar_url,
    last_login_at,
    created_at,
    updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'ADMIN-0001',
    'admin',
    'E03gWb1ptAFhettnVQE7IOKl3ya03A+E0k4nswmdhVE=',
    'ASNFZ4mrze8QMlR2mLrc/g==',
    '系统管理员',
    'admin@inhouse.local',
    NULL,
    'active',
    NULL,
    NULL,
    NOW(),
    NOW()
);

INSERT IGNORE INTO iam_roles (
    id,
    name,
    description,
    created_at
) VALUES (
    '00000000-0000-0000-0000-000000000101',
    'admin',
    '系统最高管理员',
    NOW()
);

INSERT IGNORE INTO iam_permissions (
    id,
    app,
    feature,
    resource,
    description,
    created_at
) VALUES (
    '00000000-0000-0000-0000-000000000201',
    '*',
    '*',
    '*',
    '全量权限访问',
    NOW()
);

INSERT IGNORE INTO iam_user_roles (
    user_id,
    role_id,
    created_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000101',
    NOW()
);

INSERT IGNORE INTO iam_role_permissions (
    role_id,
    permission_id,
    effect,
    created_at
) VALUES (
    '00000000-0000-0000-0000-000000000101',
    '00000000-0000-0000-0000-000000000201',
    'allow',
    NOW()
);
