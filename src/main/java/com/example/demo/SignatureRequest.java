package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.DemoApplication.convertJsonToEntity;
//import static com.example.demo.DemoApplication.processRequest;

public class SignatureRequest {

    private static User user;


    public SignatureRequest(User user) {
        this.user = user;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public static SignatureRequestResponse createSR(String filePath) throws IOException {

        /*User user = new User("api_demo_maxag_dd58_0", "8cecd429-3749-4e2a-9bf4-7d520e3196b0");
        user.loginUser();
        Token = User.getToken();
        System.out.println("You have been successfully logged in");
*/
        //build a connection to the API call
        URL url2 = new URL("https://api.scribital.com/v1/signature-requests");
        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();

        DocumentToSign documentToSign = new DocumentToSign(filePath);
        user.setDocument(documentToSign);


        //Create jsonInputString for SR Body
        String jsonInputString2 = "{\"title\": \"Example Contract XI\"," +
                "\"message\": \"Please sign this document\"," +
                "\"content\":\"" + user.getDocument().getBase64Content() + "\"," +
                "\"signatures\":[{\"signer_email_address\" : \"max.zehnder@uzh.ch\"}]," +
                "\"callback_success_url\": \"https://invulnerable-vin-64865.herokuapp.com/download/SKRIBBLE_DOCUMENT_ID\"}";
//https://invulnerable-vin-64865.herokuapp.com/hello"
        //process SR Request call and retrieve Response
        Request request = new Request("POST", jsonInputString2, connection2, user.getToken());
        String response2 = request.processRequest(connection2, jsonInputString2, "POST", user.getToken(), false);
        //String response2 = processRequest(connection2, jsonInputString2, "POST", user.getToken());

        //convert Json Response to Entity and return (for polling)
        SignatureRequestResponse signatureRequestResponse = convertJsonToEntity(response2);
        //System.out.println(signatureRequestResponse.getDocument_id());
        return signatureRequestResponse;
    }


}
