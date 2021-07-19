package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class Request {


    private String requestType;
    private String JsonInputString;
    private static StringBuilder Token;
    private HttpURLConnection connection;

    public Request(String requestType, String jsonInputString, HttpURLConnection connection, StringBuilder Token) {
        this.requestType = requestType;
        this.JsonInputString = jsonInputString;
        this.connection = connection;
        Token = Token;

    }


    public String getRequestType() {
        return requestType;
    }

    public String getJsonInputString() {
        return JsonInputString;
    }

    public static StringBuilder getToken() {
        return Token;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setJsonInputString(String jsonInputString) {
        JsonInputString = jsonInputString;
    }

    public static void setToken(StringBuilder token) {
        Token = token;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }




    public String processRequest(HttpURLConnection connection, String jsonInputString, String requestType, StringBuilder token, boolean pdf) throws IOException {

        HttpURLConnection connection2 = setRequestProperties(connection, requestType, token);
        this.setConnection(connection2);

        if (requestType == "POST") {
            try (OutputStream os = connection.getOutputStream()) {
                setOutputStream(os, jsonInputString);
            }
        }

        if (pdf){
            return null;
        }

        else {
            return getResponseData(this.getConnection());
        }
    }


    //Helper Functions / setting Streams & Requests up
    public static HttpURLConnection setRequestProperties(HttpURLConnection connection, String requestType, StringBuilder token) throws IOException {
        // set token
        if (token != null) {
            //connection.setRequestProperty("Authorization", "Bearer " + Token.toString());
            connection.setRequestProperty("Authorization", "Bearer " + User.getToken().toString());
        }

        // set the type of request (POST, GET, PUT, DELETE)
        connection.setRequestMethod(requestType);

        // type of content, in this case it is a JSON file
        connection.setRequestProperty("Content-Type", "application/json; utf-8");

        connection.setDoOutput(true);

        // set the Timeout, will disconnect if the connection did not work, avoid infinite waiting
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);
        return connection;
    }

    public static void setOutputStream(OutputStream os, String jsonInputString) throws IOException {
        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        os.close();

    }

    public static String getResponseData(HttpURLConnection connection) throws IOException {
        String responseData = "";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response1 = new StringBuilder();
            String responseLine1 = null;
            while ((responseLine1 = br.readLine()) != null) {
                response1.append(responseLine1.trim());
                responseData += responseLine1;
            }
        }
        return responseData;
    }




}
