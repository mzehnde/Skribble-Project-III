package com.example.demo;

import com.example.demo.Documents.DocumentToSign;
import com.example.demo.Request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import static com.example.demo.DemoApplication.processRequest;

public class User {

    private String username;
    private String api_key;
    private static StringBuilder Token;
    private com.example.demo.Documents.DocumentToSign DocumentToSign;


    public DocumentToSign getDocumentToSign() {
        return DocumentToSign;
    }

    public String getUsername() {
        return username;
    }

    public String getApi_key() {
        return api_key;
    }

    public static StringBuilder getToken() {
        return Token;
    }


    public void setDocumentToSign(DocumentToSign base64DocumentToSign) {
        this.DocumentToSign = base64DocumentToSign;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public static void setToken(StringBuilder token) {
        Token = token;
    }


    public User(String username, String api_key) {
        this.username = username;
        this.api_key = api_key;
    }


    public void loginUser() throws IOException {

        // build a connection to the API call
        URL url = new URL("https://api.scribital.com/v1/access/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //Create JSonInput for Login Request
        String jsonInputString1 = "{\"username\": \"" + this.getUsername() + "\", \"api-key\":\"" + this.getApi_key() + "\"}";

        //Process the request and get response of request
        Request request = new Request("POST", jsonInputString1, connection, null);
        String token = request.processRequest(false);

        //save response as our token
        saveToken(token);

        //disconnect
        connection.disconnect();
    }


    public void saveToken(String token) {
        StringBuilder responseData = new StringBuilder();
        responseData.append(token);
        Token = responseData;
    }

}
