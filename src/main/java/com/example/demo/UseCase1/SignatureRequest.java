package com.example.demo.UseCase1;

import com.example.demo.AllUseCases.Request;
import com.example.demo.AllUseCases.User;
import com.example.demo.Documents.DocumentToSign;
import com.example.demo.JsonEntities.SignatureRequestResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.DemoApplication.convertJsonToEntity;


public class SignatureRequest {

    private static User user;


    public SignatureRequest(User user) {
        SignatureRequest.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        SignatureRequest.user = user;
    }


    public SignatureRequestResponse createSR(String filePath) throws IOException {

        //build a connection to the API call
        URL url = new URL("https://api.scribital.com/v1/signature-requests");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        DocumentToSign documentToSign = new DocumentToSign(filePath);
        user.setDocumentToSign(documentToSign);

        //Create jsonInputString for SR Body
        String jsonInputString2 = "{\"title\": \"Example Contract XI\"," +
                "\"message\": \"Please sign this document\"," +
                "\"content\":\"" + user.getDocumentToSign().getBase64Content() + "\"," +
                "\"signatures\":[{\"signer_email_address\" : \"max.zehnder@uzh.ch\"}]," +
                "\"callback_success_url\": \"https://invulnerable-vin-64865.herokuapp.com/download/SKRIBBLE_DOCUMENT_ID\"}";

        //process SR Request call and retrieve Response
        Request request = new Request("POST", jsonInputString2, connection, User.getToken());
        String response = request.processRequest(false);

        //convert Json Response to Entity and return (for polling)
        SignatureRequestResponse signatureRequestResponse = convertJsonToEntity(response);
        return signatureRequestResponse;
    }


}
