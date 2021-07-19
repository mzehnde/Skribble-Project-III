package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocumentSigned {
    public String getSignedDocumentId() {
        return signedDocumentId;
    }

    public void setSignedDocumentId(String signedDocumentId) {
        this.signedDocumentId = signedDocumentId;
    }

    public DocumentSigned(String signedDocumentId) {
        this.signedDocumentId = signedDocumentId;
    }

    private String signedDocumentId;


    public void downloadPDF(String signatureRequestResponseId) throws IOException {

        //GET REQUEST TO RETRIEVE PDF
        URL url4 = new URL("https://api.scribital.com/v1/documents/" + signatureRequestResponseId + "/content");
        HttpURLConnection connection4 = (HttpURLConnection) url4.openConnection();

        Request request = new Request("GET", null, connection4, User.getToken());
        request.processRequest(connection4, null, "GET", User.getToken(), true);

        //setRequestProperties(connection4, "GET", Token);

        try {
            InputStream inputStream = request.getConnection().getInputStream();
            //InputStream inputStream = connection4.getInputStream();

            FileOutputStream outputStream = new FileOutputStream("/Users/maxzehnder/Desktop/Skribble/TestFiles/signed.pdf");


            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }


        }
        catch (Exception e) {
            System.out.println("Couldn't access Input Stream");
        }

    }

}
