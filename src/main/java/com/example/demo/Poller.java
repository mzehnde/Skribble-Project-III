package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.demo.DemoApplication.convertJsonToEntity;

public class Poller {
    public SignatureRequestResponse getSignatureRequestResponse() {
        return signatureRequestResponse;
    }

    public void setSignatureRequestResponse(SignatureRequestResponse signatureRequestResponse) {
        this.signatureRequestResponse = signatureRequestResponse;
    }

    public Poller(SignatureRequestResponse signatureRequestResponse) {
        this.signatureRequestResponse = signatureRequestResponse;
    }

    private SignatureRequestResponse signatureRequestResponse;


    public static void startPolling(SignatureRequestResponse signatureRequestResponse) {
        //Start polling SR Get, every 10 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //open connection
                URL url3 = null;
                try {
                    url3 = new URL("https://api.scribital.com/v1/signature-requests/" + signatureRequestResponse.getId() + "");
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
                    Request request = new Request("GET", null, connection3, User.getToken());
                    data = request.processRequest(connection3, null, "GET", User.getToken(), false);
                    //data = processRequest(connection3, null, "GET", Token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //convert Response from json to entity to check status (signed or declined)
                SignatureRequestResponse signatureRequestResponse2 = convertJsonToEntity(data);

                //check if doc was signed
                if (signatureRequestResponse2.getStatus_overall().equals("SIGNED")) {
                    //process get requests to download pdf
                    try {
                        DocumentSigned documentSigned = new DocumentSigned(signatureRequestResponse2.getDocument_id());
                        documentSigned.downloadPDF(signatureRequestResponse2.getDocument_id());
                        System.out.println("Your Document was signed and downloaded");
                        System.out.println(signatureRequestResponse2.getDocument_id());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Stop polling
                    timer.cancel();
                }

                if (signatureRequestResponse2.getStatus_overall().equals("DECLINED")) {
                    System.out.println("Your Signature Request was declined");

                    //Stop polling
                    timer.cancel();
                }
                //disconnect
                connection3.disconnect();
            }
        }, 0, 10000);//wait 0 ms before doing the action and do it evry 1000ms (1second)
    }



}
