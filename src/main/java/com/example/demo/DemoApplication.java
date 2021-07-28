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
import java.io.IOException;
import java.util.Scanner;



public class DemoApplication {

    //@Todo: write arguments in args
    //@Todo: format of classes (static beginning, constructor beginning) -->DONE
    //@Todo: dont do set(sth) when class isn't used later instead return it
    //@Todo: open connection stuff in request class if possible
    //@Todo: dont make token as a static field but return it after login for use in later requests --> DONE




    public static void main(/*@org.jetbrains.annotations.NotNull*/ String[] args) throws IOException {
        //doUseCase1();
        doUseCase2(new String[]{args[0]});
    }



    public static void doUseCase2(String args[]) throws IOException {

        //1. Login User
        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        StringBuilder token = user.loginUser();
        System.out.println("You have been successfully logged in");

        //2. read the csv file and create a list with signer documents and E-Mails
        CSVFile csvFile = new CSVFile(args[0]);
        csvFile.readCSVFile();

        //3. populate signer-List with signers of csv file as Signer Entities
        AllSigners allSigners = new AllSigners();
        allSigners.populateSignerList(csvFile);

        //4. Process the requests and get response-list of all the requests
        AllSignatureRequests allSignatureRequests = new AllSignatureRequests(allSigners.getSignerList(), token);
        allSignatureRequests.doRequests();

        //5. Write SR Id's to file with corresponding E-Mail
        SignatureRequestIdFile signatureRequestIdFile = new SignatureRequestIdFile(allSignatureRequests.getResponseList(), "/Users/maxzehnder/Desktop/Skribble/TestFiles/SignatureRequestIds");
        signatureRequestIdFile.writeIdToFile();

        System.out.println(allSignatureRequests.getResponseList().get(0).getId());
        System.out.println(allSignatureRequests.getResponseList().get(1).getId());


    }


    public static void doUseCase1() throws IOException {
        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        StringBuilder token = user.loginUser();
        System.out.println("You have been successfully logged in");


        //handle path of file to be signed
        Scanner filePathInput = new Scanner(System.in);
        System.out.println("Enter the path of your File to be signed:");
        String filePath = filePathInput.nextLine();


        //CREATE SR POST REQUEST
        SignatureRequest signatureRequest = new SignatureRequest(user, token);
        SignatureRequestResponse signatureRequestResponse = signatureRequest.createSR(filePath);
        System.out.println("Please wait until your document is signed");


        //check if signed and download doc after signing
        Poller poller = new Poller(signatureRequestResponse, token);
        poller.startPolling(signatureRequestResponse);
    }


    //Function to convert Json
    public static SignatureRequestResponse convertJsonToEntity(String json) {
        Gson gson = new Gson(); //
        return gson.fromJson(json, SignatureRequestResponse.class);
    }
}


