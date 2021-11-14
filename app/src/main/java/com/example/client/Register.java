package com.example.client;

import Request.RegisterRequest;

public class Register {
    private String username = null;
    private String password = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String gender = null;
    private String webRequest = null;

    public Register(String username, String password, String firstName, String lastName,
                    String email, String gender) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }

    public void checkRegister() {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email, firstName,
                lastName, gender);
        webRequest = Serializer.serialize(registerRequest);
    }

    public String getWebRequest() {
        return webRequest;
    }
}
