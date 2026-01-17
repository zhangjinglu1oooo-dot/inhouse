const apiBase = 'http://localhost:8081';

const registerForm = document.getElementById('register-form');
const registerMessage = document.getElementById('register-message');

const showRegisterMessage = (text, type = 'info') => {
  registerMessage.textContent = text;
  registerMessage.className = `form-message ${type}`;
};

registerForm.addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(registerForm);
  const account = formData.get('account').trim();
  const email = formData.get('email').trim();
  const password = formData.get('password').trim();
  const confirmPassword = formData.get('confirmPassword').trim();

  if (!email) {
    showRegisterMessage('邮箱为必填信息，请填写。', 'error');
    return;
  }

  if (password !== confirmPassword) {
    showRegisterMessage('两次输入的密码不一致，请检查。', 'error');
    return;
  }

  showRegisterMessage('正在创建账号…', 'info');

  try {
    const response = await fetch(`${apiBase}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        account,
        email,
        password,
      }),
    });

    if (!response.ok) {
      const message = await response.text();
      throw new Error(message || '注册失败，请稍后重试。');
    }

    showRegisterMessage('注册成功！请返回登录。', 'success');
    registerForm.reset();
  } catch (error) {
    showRegisterMessage(error.message, 'error');
  }
});
