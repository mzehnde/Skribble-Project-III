package com.example.demo.UseCase2;

import com.example.demo.Documents.DocumentToSign;

public class Signer {

    private DocumentToSign documentToSign;
    private String e_mail;


    public Signer(DocumentToSign documentToSign, String e_mail) {
        this.e_mail = e_mail;
        this.documentToSign = documentToSign;
    }

    public DocumentToSign getDocumentToSign() {
        return documentToSign;
    }

    public void setDocumentToSign(DocumentToSign documentToSign) {
        this.documentToSign = documentToSign;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

}
