package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//import static com.example.demo.DemoApplication.processRequest;

public class User {

    private String username;
    private String api_key;
    private static StringBuilder Token;
    private DocumentToSign DocumentToSign;

    public DocumentToSign getDocument() {
        return DocumentToSign;
    }

    public void setDocument(DocumentToSign base64DocumentToSign) {
        this.DocumentToSign = base64DocumentToSign;
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



    public void setUsername(String username) {
        this.username = username;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public static void setToken(StringBuilder token) {
        Token = token;
    }

    public User(String username, String api_key){
        this.username = username;
        this.api_key = api_key;
    }




    public void loginUser() throws IOException {
        // build a connection to the API call
        URL url1 = new URL("https://api.scribital.com/v1/access/login");
        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();

        //Create JSonInput for Login Request
        //String jsonInputString1 = "{\"username\": \"api_demo_maxag_dd58_0\", \"api-key\":\"8cecd429-3749-4e2a-9bf4-7d520e3196b0\"}";
        String jsonInputString1 = "{\"username\": \""+this.getUsername()+"\", \"api-key\":\""+this.getApi_key()+"\"}";
        System.out.println(jsonInputString1);

        Request request = new Request("POST", jsonInputString1, connection1, null);
        //Process the request and get response of request
        String response1 = request.processRequest(connection1, jsonInputString1, "POST", null, false);

        //save response as our token
        StringBuilder responseData = new StringBuilder();
        responseData.append(response1);
        Token = responseData;

        //Print and disconnectx

        connection1.disconnect();
    }

}
