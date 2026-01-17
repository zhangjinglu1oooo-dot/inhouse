# Inhouse 前端门户

该前端以 **Vue 3（CDN 方式）** 搭建，包含两套入口页面：

- `frontend/user-portal/`：员工门户
- `frontend/admin-portal/`：管理后台

## 启动方式

任意静态文件服务器即可，例如：

```bash
cd frontend/user-portal
python -m http.server 5173
```

打开 `http://localhost:5173` 查看员工门户。

若需要查看管理后台，可在另一个终端运行：

```bash
cd frontend/admin-portal
python -m http.server 5174
```

打开 `http://localhost:5174`。
