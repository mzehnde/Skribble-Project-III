package com.example.demo;

import com.example.demo.Documents.DocumentSigned;
import com.example.demo.JsonEntities.SignatureRequestResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.demo.DemoApplication.convertJsonToEntity;

public class Poller {

    private SignatureRequestResponse signatureRequestResponse;


    public SignatureRequestResponse getSignatureRequestResponse() {
        return signatureRequestResponse;
    }


    public void setSignatureRequestResponse(SignatureRequestResponse signatureRequestResponse) {
        this.signatureRequestResponse = signatureRequestResponse;
    }


    public Poller(SignatureRequestResponse signatureRequestResponse) {
        this.signatureRequestResponse = signatureRequestResponse;
    }


    public void startPolling(SignatureRequestResponse signatureRequestResponse) {
        //Start polling SR Get, every 10 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //open connection
                HttpURLConnection connection = openConnection();


                SignatureRequestResponse signatureRequestResponse = getSignatureRequestResponse(connection);

                if (isSigned(signatureRequestResponse)) {
                    try {

                        DocumentSigned documentSigned = new DocumentSigned(signatureRequestResponse.getDocument_id());
                        documentSigned.downloadPDF();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                    //Stop polling
                    timer.cancel();
                }

                //disconnect
                connection.disconnect();
            }
        }, 0, 10000);//wait 0 ms before doing the action and do it evry 1000ms (1second)
    }


    public HttpURLConnection openConnection() {
        URL url = null;
        try {
            url = new URL("https://api.scribital.com/v1/signature-requests/" + signatureRequestResponse.getId() + "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connection;
    }


    public SignatureRequestResponse getSignatureRequestResponse(HttpURLConnection connection) {
        String data = null;
        try {
            Request request = new Request("GET", null, connection, User.getToken());
            data = request.processRequest(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //convert Response from json to entity to check status (signed or declined)
        return convertJsonToEntity(data);
    }


    public boolean isSigned(SignatureRequestResponse signatureRequestResponse) {
        return signatureRequestResponse.getStatus_overall().equals("SIGNED");
    }
}
