const apiBase = 'http://localhost:8081';

const loginForm = document.getElementById('login-form');
const loginMessage = document.getElementById('login-message');

const showMessage = (text, type = 'info') => {
  loginMessage.textContent = text;
  loginMessage.className = `form-message ${type}`;
};

loginForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);
  const account = formData.get('account').trim();
  const password = formData.get('password').trim();

  showMessage('正在验证账号…', 'info');

  try {
    const response = await fetch(`${apiBase}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ account, password }),
    });

    if (!response.ok) {
      throw new Error('账号或密码不正确，请重试。');
    }

    const data = await response.json();
    localStorage.setItem('inhouse_token', data.accessToken);
    showMessage('登录成功，正在进入系统…', 'success');
    setTimeout(() => {
      window.location.href = 'system.html';
    }, 600);
  } catch (error) {
    showMessage(error.message, 'error');
  }
});
