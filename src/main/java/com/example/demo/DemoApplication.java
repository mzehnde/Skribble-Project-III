package com.example.demo;

import com.example.demo.Documents.DocumentToSign;
import com.example.demo.JsonEntities.SignatureRequestResponse;
import com.example.demo.UseCase2.Signer;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@SpringBootApplication
@RestController
public class DemoApplication {

    private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);


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


    //UseCase2:
    //Anwendung mit 2 Argumenten:
    //  -->Verzeichnis mit den Quelldateien
    //  -->CSV mit zwei Spalten:
    //      -->Filename
    //      -->e-mail
    //@Todo: 1. login User with username,api-Key DONE
    //@Todo: 2. read CSV File and print to console (for understanding purpose) DONE
    //@Todo: 3. create new List of all Signers and base64 content of file DONE
    //@Todo: 4. create SR for file corresponding to email --> 4.1 iterate through signer list and create SR's
    //@Todo: 5. Get SR ID from Response of SR POST and write to file
    public static void doUseCase2() throws IOException {

        //1. Login User
        User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        user.loginUser();
        System.out.println("You have been successfully logged in");

        //2. read the csv file
        List<List<String>> records = new ArrayList<List<String>>();
        List<String> csvFile = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader("/Users/maxzehnder/Desktop/Skribble/TestFiles/TestCSVFile.csv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }

            for (int i = 0; i < records.size(); i++) {
                csvFile.add(records.get(i).get(0));
            }
        }


        ArrayList<Signer> signerList = new ArrayList<>();
        for (int i = 0; i < csvFile.size(); i++) {
            String[] parts = csvFile.get(i).split(";");
            DocumentToSign documentToSign = convertFileNameToBase64(parts[0], "/Users/maxzehnder/Desktop/Skribble/TestFiles");
            if (documentToSign != null){
                signerList.add(new Signer(documentToSign, parts[1]));
            }
            //signerList.add(new Signer(parts[0], parts[1]));
        }
        System.out.print(signerList.get(0).getE_mail());

    }

    //@Todo: find corresponding file in files, convert it to base 64
    private static DocumentToSign convertFileNameToBase64(String filename, String files) throws IOException {
        File file = new File(files);
        for (File file1 : file.listFiles()){
            if (file1.getName().equals(filename)){
                DocumentToSign documentToSign = new DocumentToSign(file1.toString());
                return documentToSign;
            }

        }
        return null;
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


