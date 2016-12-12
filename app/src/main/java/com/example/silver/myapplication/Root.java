package com.example.silver.myapplication;

//重服务器端返回的数据格式就是Root,Root下又包含LoginReturn这样的格式
public class Root {
    private LoginReturn loginReturn;

    public void setLoginReturn(LoginReturn loginReturn) {
        this.loginReturn = loginReturn;
    }

    public LoginReturn getLoginReturn() {
        return this.loginReturn;
    }

}