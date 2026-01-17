// 前端主入口脚本，负责调用后端 API 并渲染结果
const apiBaseInput = document.getElementById('apiBase');

// 获取当前 API 基础地址
const getApiBase = () => apiBaseInput.value.trim().replace(/\/$/, '');

// 统一的请求方法，自动处理 JSON
async function request(path, options = {}) {
  const url = `${getApiBase()}${path}`;
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
    },
    ...options,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`${response.status} ${errorText}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

// 将 JSON 数据格式化展示
function renderJson(target, data) {
  target.textContent = JSON.stringify(data, null, 2);
}

// 表单提交辅助函数
function bindForm(formId, handler) {
  const form = document.getElementById(formId);
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(form);
    const payload = Object.fromEntries(formData.entries());
    await handler(payload);
    form.reset();
  });
}

bindForm('userForm', async (payload) => {
  const user = await request('/iam/users', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  renderJson(document.getElementById('userList'), user);
});

document.getElementById('loadUsers').addEventListener('click', async () => {
  const users = await request('/iam/users');
  renderJson(document.getElementById('userList'), users);
});

bindForm('loginForm', async (payload) => {
  const token = await request('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  renderJson(document.getElementById('loginResult'), token);
});

bindForm('policyForm', async (payload) => {
  const policy = await request('/permissions/policies', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  renderJson(document.getElementById('policyList'), policy);
});

document.getElementById('loadPolicies').addEventListener('click', async () => {
  const policies = await request('/permissions/policies');
  renderJson(document.getElementById('policyList'), policies);
});

bindForm('appForm', async (payload) => {
  const app = await request('/registry/apps', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
  renderJson(document.getElementById('appList'), app);
});

document.getElementById('loadApps').addEventListener('click', async () => {
  const apps = await request('/registry/apps');
  renderJson(document.getElementById('appList'), apps);
});

bindForm('eventForm', async (payload) => {
  let parsedPayload = {};
  if (payload.payload) {
    // 解析用户输入的 JSON 负载
    parsedPayload = JSON.parse(payload.payload);
  }
  const event = await request('/events', {
    method: 'POST',
    body: JSON.stringify({
      topic: payload.topic,
      payload: parsedPayload,
    }),
  });
  renderJson(document.getElementById('eventList'), event);
});

document.getElementById('loadEvents').addEventListener('click', async () => {
  const events = await request('/events');
  renderJson(document.getElementById('eventList'), events);
});

bindForm('auditForm', async (payload) => {
  let detail = {};
  if (payload.detail) {
    // 解析审计详情 JSON
    detail = JSON.parse(payload.detail);
  }
  const audit = await request('/observability/audits', {
    method: 'POST',
    body: JSON.stringify({
      action: payload.action,
      actor: payload.actor,
      target: payload.target,
      detail,
    }),
  });
  renderJson(document.getElementById('auditList'), audit);
});

document.getElementById('loadAudits').addEventListener('click', async () => {
  const audits = await request('/observability/audits');
  renderJson(document.getElementById('auditList'), audits);
});

// 统一捕获错误并提示
window.addEventListener('unhandledrejection', (event) => {
  alert(`请求失败：${event.reason.message}`);
});
