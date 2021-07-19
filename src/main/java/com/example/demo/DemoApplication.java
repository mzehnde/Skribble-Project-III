package com.example.demo;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


//create a request /download (heroku url --> deploy this to heroku) that will be sent as callbackURL,
//request / download: has to cal skribble api to download doc to desktop (use download() function)
@SpringBootApplication
@RestController
public class DemoApplication {

    private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    private static StringBuilder Token;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class, args);

        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        user.loginUser();
        Token = User.getToken();
        System.out.println("You have been successfully logged in");

        Scanner filePathInput = new Scanner(System.in);
        System.out.println("Enter the path of your File to be signed:");
        String filePath = filePathInput.nextLine();


        //CREATE SR POST REQUEST
        SignatureRequest signatureRequest = new SignatureRequest(user);
        SignatureRequestResponse signatureRequestResponse = signatureRequest.createSR(filePath);
        System.out.println("Please wait until your document is signed");


        //check if signed and download doc after signing
        Poller poller = new Poller(signatureRequestResponse);
        poller.startPolling(signatureRequestResponse);
        //startPolling(signatureRequestResponse);
    }

















    //Function to convert Json
    public static SignatureRequestResponse convertJsonToEntity(String json) {
        Gson gson = new Gson(); //
        return gson.fromJson(json, SignatureRequestResponse.class);
    }
}


