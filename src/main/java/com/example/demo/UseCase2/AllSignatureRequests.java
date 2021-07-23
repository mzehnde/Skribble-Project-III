package com.example.demo.UseCase2;

import com.example.demo.AllUseCases.Request;
import com.example.demo.AllUseCases.User;
import com.example.demo.JsonEntities.SignatureRequestResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.demo.DemoApplication.convertJsonToEntity;

public class AllSignatureRequests {

    private ArrayList<SignatureRequestResponse> responseList;
    private ArrayList<Signer> signerList;


    public AllSignatureRequests(ArrayList<Signer> signerList) {
        this.signerList = signerList;
    }

    public ArrayList<Signer> getSignerList() {
        return signerList;
    }

    public void setSignerList(ArrayList<Signer> signerList) {
        this.signerList = signerList;
    }

    public ArrayList<SignatureRequestResponse> getResponseList() {
        return responseList;
    }

    public void setResponseList(ArrayList<SignatureRequestResponse> responseList) {
        this.responseList = responseList;
    }

    public void doRequests() throws IOException {
        ArrayList<SignatureRequestResponse> responseList = new ArrayList<>();
        for (Signer signer : signerList) {
            URL url = new URL("https://api.scribital.com/v1/signature-requests");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String jsonInputString = "{\"title\": \"Example Contract X\"," +
                    "\"message\": \"Please sign this document\"," +
                    "\"content\":\"" + signer.getDocumentToSign().getBase64Content() + "\"," +
                    "\"signatures\":[{\"signer_email_address\" : \"" + signer.getE_mail() + "\"}]," +
                    "\"callback_success_url\": \"https://invulnerable-vin-64865.herokuapp.com/download/SKRIBBLE_DOCUMENT_ID\"}";

            Request request = new Request("POST", jsonInputString, connection, User.getToken());
            String response = request.processRequest(false);
            SignatureRequestResponse signatureRequestResponse = convertJsonToEntity(response);
            responseList.add(signatureRequestResponse);
            setResponseList(responseList);
        }
    }
}
