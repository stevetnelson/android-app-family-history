package com.example.client;

import Request.LoginRequest;

public class Login {
    private String username = null;
    private String password = null;
    private String webRequest = null;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void checkLogin() {
        LoginRequest loginRequest = new LoginRequest(username, password);
        webRequest = Serializer.serialize(loginRequest);
    }

    public String getWebRequest() {
        return webRequest;
    }
}
