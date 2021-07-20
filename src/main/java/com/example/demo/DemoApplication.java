package com.example.demo;

import com.example.demo.JsonEntities.SignatureRequestResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Scanner;


@SpringBootApplication
@RestController
public class DemoApplication {

    private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);


    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class, args);

        //Login user with username and API-key
        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        user.loginUser();
        System.out.println("You have been successfully logged in");


        //handle path of file to be signed
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
        System.out.println("Your Document was signed and downloaded");

    }


    //Function to convert Json
    public static SignatureRequestResponse convertJsonToEntity(String json) {
        Gson gson = new Gson(); //
        return gson.fromJson(json, SignatureRequestResponse.class);
    }
}


