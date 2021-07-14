package com.example.demo;

public class Email {

    public String getSigner_email_address() {
        return signer_email_address;
    }

    public void setSigner_email_address(String signer_email_address) {
        this.signer_email_address = signer_email_address;
    }

    public Email(String signer_email_address) {
        this.signer_email_address = signer_email_address;
    }

    public String signer_email_address;

}
