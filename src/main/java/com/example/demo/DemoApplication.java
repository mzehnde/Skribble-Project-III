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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;







//create a request /download (heroku url --> deploy this to heroku) that will be sent as callbackURL,
//request / download: has to cal skribble api to download doc to desktop (use download() function)
@SpringBootApplication
@RestController
public class DemoApplication {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static StringBuilder Token;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);
        //LOGIN POST REQUEST
        loginUser();
        System.out.println("You have been successfully logged in");

        //CREATE SR POST REQUEST
        SignatureRequestResponse signatureRequestResponse = createSR();
        System.out.println("Please wait until your document is signed");

        //check if signed and download doc after signing
        //startPolling(signatureRequestResponse);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		//System.out.println("hello");
        logger.info("skr");
		return String.format("Hello %s!", name);
	}

	@GetMapping("/download/{documentId}")
    public void hello2(@PathVariable String documentId) throws IOException {

        System.out.println("hello");
        downloadPDF(documentId);
    }



    public static void loginUser() throws IOException {
        // build a connection to the API call
        URL url1 = new URL("https://api.scribital.com/v1/access/login");
        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();

        //Create JSonInput for Login Request
        String jsonInputString1 = "{\"username\": \"api_demo_maxag_dd58_0\", \"api-key\":\"8cecd429-3749-4e2a-9bf4-7d520e3196b0\"}";

        //Process the request and get response of request
        String response1 = processRequest(connection1, jsonInputString1, "POST", null);

        //save response as our token
        StringBuilder responseData = new StringBuilder();
        responseData.append(response1);
        Token = responseData;

        //Print and disconnectx

        connection1.disconnect();
    }

    public  static SignatureRequestResponse createSR() throws IOException {
        //build a connection to the API call
        URL url2 = new URL("https://api.scribital.com/v1/signature-requests");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();

        //Handle Input (Filepath of file to be signed)
        Scanner filePathInput = new Scanner(System.in);
        System.out.println("Enter the path of your File to be signed:");
        String filePath = filePathInput.nextLine();

        /*Scanner signerInput = new Scanner(System.in);
        System.out.println("Enter the Email of your signer:");
        String signerEmail = signerInput.nextLine();
        Email signer_email = new Email(signerEmail);*/



        //convert file to base64 for SR Body
        File file = new File(filePath);
        byte [] bytes = Files.readAllBytes(file.toPath());
        String content = Base64.getEncoder().encodeToString(bytes);

        //Create jsonInputString for SR Body
        String jsonInputString2 = "{\"title\": \"Example Contract XX\"," +
                "\"message\": \"Please sign this document\"," +
                "\"content\":\"" + content + "\"," +
                "\"signatures\":[{\"signer_email_address\" : \"max.zehnder@uzh.ch\"}]," +
                "\"callback_success_url\": \"https://invulnerable-vin-64865.herokuapp.com/hello\"}";
//https://invulnerable-vin-64865.herokuapp.com/hello"
        //process SR Request call and retrieve Response
        String response2 = processRequest(connection2, jsonInputString2, "POST", Token);

        //convert Json Response to Entity and return (for polling)
        SignatureRequestResponse signatureRequestResponse = convertJsonToEntity(response2);
        return signatureRequestResponse;
    }

    public static void startPolling(SignatureRequestResponse signatureRequestResponse){
        //Start polling SR Get, every 10 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //open connection
                URL url3 = null;
                try {
                    url3 = new URL("https://api.scribital.com/v1/signature-requests/"+signatureRequestResponse.getId()+"");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection connection3 = null;
                try {
                    connection3 = (HttpURLConnection) url3.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //retrieve SR response
                String data = null;
                try {
                    data = processRequest(connection3, null, "GET", Token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //convert Response from json to entity to check status (signed or declined)
                SignatureRequestResponse signatureRequestResponse2 = convertJsonToEntity(data);

                //check if doc was signed
                if(signatureRequestResponse2.getStatus_overall().equals("SIGNED")){
                    //process get requests to download pdf
                    try {
                        downloadPDF(signatureRequestResponse2.getDocument_id());
                        System.out.println("Your Document was signed and downloaded");
                        System.out.println(signatureRequestResponse2.getDocument_id());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Stop polling
                    timer.cancel();
                }

                if(signatureRequestResponse2.getStatus_overall().equals("DECLINED")){
                    System.out.println("Your Signature Request was declined");

                    //Stop polling
                    timer.cancel();
                }
                //disconnect
                connection3.disconnect();
            }
        }, 0, 10000);//wait 0 ms before doing the action and do it evry 1000ms (1second)
    }





    public static void downloadPDF(String signatureRequestResponseId) throws IOException {

        //GET REQUEST TO RETRIEVE PDF
        URL url4 = new URL("https://api.scribital.com/v1/documents/"+signatureRequestResponseId+"/content");
        HttpURLConnection connection4 = (HttpURLConnection) url4.openConnection();

        setRequestProperties(connection4, "GET", Token);

        try {
            InputStream inputStream = connection4.getInputStream();
            FileOutputStream outputStream = new FileOutputStream("/Users/maxzehnder/Desktop/Skribble/Signed3.pdf");

            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e){
            System.out.println("Couldn't access Input Stream");
        }

    }


    //Function to process request
    public static String processRequest(HttpURLConnection connection, String jsonInputString, String requestType, StringBuilder token) throws IOException {

        setRequestProperties(connection, requestType, token);

        if (requestType == "POST"){
            try (OutputStream os = connection.getOutputStream()) {
                setOutputStream(os, jsonInputString);
            }
        }

        return getResponseData(connection);
    }



    //Helper Functions / setting Streams & Requests up
    public static HttpURLConnection setRequestProperties(HttpURLConnection connection, String requestType, StringBuilder token) throws IOException {
        // set token
        if (token != null) {
            connection.setRequestProperty("Authorization", "Bearer " + Token.toString());
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



    //Function to convert Json
    public static SignatureRequestResponse convertJsonToEntity(String json){
        Gson gson = new Gson(); //
        return gson.fromJson(json, SignatureRequestResponse.class);
    }
}


