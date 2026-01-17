package com.inhouse.iam;

/**
 * 注册请求体。
 */
public class RegisterRequest {
    // 账号（姓名）
    private String account;
    // 邮箱（必填）
    private String email;
    // 密码
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
