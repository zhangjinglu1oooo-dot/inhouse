# Inhouse 前端门户

该前端以 **Vue 3（CDN 方式）** 搭建，包含两套入口页面。当前为**静态演示页面**（示例数据写在前端脚本内，未对接后端接口与数据库，也未实现导航路由）。

- `frontend/user-portal/`：员工门户
- `frontend/admin-portal/`：管理后台

## 启动方式

任意静态文件服务器即可，例如：

```bash
cd frontend/user-portal
python -m http.server 5173
```

打开 `http://localhost:5173` 查看员工门户。

> 如果在仓库根目录启动静态服务器，会看到目录索引页（`Index of /`）。请确保静态服务器的根目录指向具体前端目录。

若需要查看管理后台，可在另一个终端运行：

```bash
cd frontend/admin-portal
python -m http.server 5174
```

打开 `http://localhost:5174`。
