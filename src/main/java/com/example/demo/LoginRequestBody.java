package com.example.demo;

public class LoginRequestBody {

    private String username;
    private String api_key;


    public LoginRequestBody(String username, String api_key){
        this.username = username;
        this.api_key = api_key;
    }


    public String getUsername() {
        return username;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

}
