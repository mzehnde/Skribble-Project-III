package com.example.demo;

import com.example.demo.AllUseCases.User;
import com.example.demo.JsonEntities.SignatureRequestResponse;
import com.example.demo.UseCase1.Poller;
import com.example.demo.UseCase1.SignatureRequest;
import com.example.demo.UseCase2.AllSignatureRequests;
import com.example.demo.UseCase2.AllSigners;
import com.example.demo.UseCase2.CSVFile;
import com.example.demo.UseCase2.SignatureRequestIdFile;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Scanner;


@SpringBootApplication
@RestController
public class DemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);


    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class, args);
        //doUseCase1();
        doUseCase2();


    }


    //Function to convert Json
    public static SignatureRequestResponse convertJsonToEntity(String json) {
        Gson gson = new Gson(); //
        return gson.fromJson(json, SignatureRequestResponse.class);
    }


    public static void doUseCase2() throws IOException {

        //1. Login User
        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        user.loginUser();
        System.out.println("You have been successfully logged in");

        //2. read the csv file
        CSVFile csvFile = new CSVFile("/Users/maxzehnder/Desktop/Skribble/TestFiles/TestCSVFile.csv");
        csvFile.readCSVFile();

        //3. populate signerlist with signers of csv file
        AllSigners allSigners = new AllSigners();
        allSigners.populateSignerList(csvFile);

        //4. process the requests and get responselist
        AllSignatureRequests allSignatureRequests = new AllSignatureRequests(allSigners.getSignerList());
        allSignatureRequests.doRequests();

        //5. write SR Id's to file
        SignatureRequestIdFile signatureRequestIdFile = new SignatureRequestIdFile(allSignatureRequests.getResponseList(), "/Users/maxzehnder/Desktop/Skribble/TestFiles/SignatureRequestIds");
        signatureRequestIdFile.writeIdToFile();

        System.out.println(allSignatureRequests.getResponseList().get(0).getId());
        System.out.println(allSignatureRequests.getResponseList().get(1).getId());


    }


    public static void doUseCase1() throws IOException {
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
    }
}


