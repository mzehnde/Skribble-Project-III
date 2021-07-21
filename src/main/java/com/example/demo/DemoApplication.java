package com.example.demo;

import com.example.demo.Documents.DocumentToSign;
import com.example.demo.JsonEntities.SignatureRequestResponse;
import com.example.demo.UseCase2.Signer;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        doRequests(signerList);
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

    //iterate sigenr list and create sr post request
    private static void doRequests(ArrayList<Signer> signerList) throws IOException {
        ArrayList<SignatureRequestResponse> responseList = new ArrayList<>();
        for (int i = 0 ; i < signerList.size() ; i++){
            URL url = new URL("https://api.scribital.com/v1/signature-requests");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String jsonInputString = "{\"title\": \"Example Contract X\"," +
                    "\"message\": \"Please sign this document\"," +
                    "\"content\":\"" + signerList.get(i).getDocumentToSign().getBase64Content() + "\"," +
                    "\"signatures\":[{\"signer_email_address\" : \"" + signerList.get(i).getE_mail()+ "\"}]," +
                    "\"callback_success_url\": \"https://invulnerable-vin-64865.herokuapp.com/download/SKRIBBLE_DOCUMENT_ID\"}";

            Request request = new Request("POST", jsonInputString, connection, User.getToken());
            String response = request.processRequest(false);
            SignatureRequestResponse signatureRequestResponse = convertJsonToEntity(response);
            responseList.add(signatureRequestResponse);


        }
        writeIdtoFile(responseList);
        System.out.println(responseList.get(0).getId());
        System.out.println(responseList.get(1).getId());
    }


    //add value of list in list to array and write to file
    private static void writeIdtoFile(ArrayList<SignatureRequestResponse> responseList) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/maxzehnder/Desktop/Skribble/TestFiles/SignatureRequestIds");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(String.format("%s\t \t \t \t \t %s", "E-Mail", "Signature-Request-ID").getBytes());
        byteArrayOutputStream.write("\r\n".getBytes());
        byteArrayOutputStream.write("\r\n".getBytes());

        for (int i = 0 ; i < responseList.size(); i++){

            byteArrayOutputStream.write(String.format("%s\t \t \t %s", responseList.get(i).getSignatures().get(0).getSigner_email_address(), (responseList.get(i).getId())).getBytes());

            /*byteArrayOutputStream.write(responseList.get(i).getSignatures().get(0).getSigner_email_address().getBytes());
            byteArrayOutputStream.write("  ".getBytes());
            byteArrayOutputStream.write(responseList.get(i).getId().getBytes());*/
            if ( i != responseList.size()-1){
                byteArrayOutputStream.write("\r\n".getBytes());
            }
        }
        byte[] idToBytes = byteArrayOutputStream.toByteArray();
        fileOutputStream.write(idToBytes);
    }




    

//String.format("%s\t%s", query, value);





















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


