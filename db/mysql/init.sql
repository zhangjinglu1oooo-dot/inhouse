-- Inhouse 系统初始化数据库与表结构
CREATE DATABASE IF NOT EXISTS inhouse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inhouse;

-- IAM 用户表（企业员工信息）
CREATE TABLE IF NOT EXISTS iam_users (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    password_salt VARCHAR(128) NOT NULL,
    display_name VARCHAR(128),
    email VARCHAR(128),
    phone VARCHAR(32),
    department VARCHAR(128),
    title VARCHAR(128),
    manager_id VARCHAR(64),
    location VARCHAR(128),
    status VARCHAR(32) DEFAULT 'active',
    hire_date DATE,
    last_login_at DATETIME,
    avatar_url VARCHAR(255),
    roles JSON,
    attributes JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- IAM 角色表
CREATE TABLE IF NOT EXISTS iam_roles (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    permissions JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权限策略表
CREATE TABLE IF NOT EXISTS permission_policies (
    id VARCHAR(64) PRIMARY KEY,
    app VARCHAR(128) NOT NULL,
    feature VARCHAR(128) NOT NULL,
    resource VARCHAR(128),
    effect VARCHAR(16) NOT NULL,
    conditions JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 应用注册表
CREATE TABLE IF NOT EXISTS registry_apps (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    version VARCHAR(64),
    status VARCHAR(32),
    entry_url VARCHAR(255),
    description TEXT,
    features JSON,
    tags JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 事件表
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(64) PRIMARY KEY,
    topic VARCHAR(128) NOT NULL,
    payload JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 审计日志表
CREATE TABLE IF NOT EXISTS audits (
    id VARCHAR(64) PRIMARY KEY,
    action VARCHAR(128) NOT NULL,
    actor VARCHAR(128) NOT NULL,
    target VARCHAR(128),
    detail JSON,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
