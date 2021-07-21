package com.example.demo.Documents;

import com.example.demo.Request;
import com.example.demo.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocumentSigned {
    private String signedDocumentId;


    public DocumentSigned(String signedDocumentId) {
        this.signedDocumentId = signedDocumentId;
    }


    public String getSignedDocumentId() {
        return signedDocumentId;
    }

    public void setSignedDocumentId(String signedDocumentId) {
        this.signedDocumentId = signedDocumentId;
    }


    public void downloadPDF() throws IOException {

        //GET REQUEST TO RETRIEVE PDF
        URL url = new URL("https://api.scribital.com/v1/documents/" + signedDocumentId + "/content");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Request request = new Request("GET", null, connection, User.getToken());
        request.processRequest(true);

        try {
            InputStream inputStream = request.getConnection().getInputStream();

            FileOutputStream outputStream = new FileOutputStream("/Users/maxzehnder/Desktop/Skribble/TestFiles/signed.pdf");


            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }


        } catch (Exception e) {
            System.out.println("Couldn't access Input Stream");
        }

    }

}