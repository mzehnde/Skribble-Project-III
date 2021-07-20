package com.example.demo.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class DocumentToSign {
    private String filePath;
    private String base64Content;


    public String getFilePath() {
        return filePath;
    }

    public String getBase64Content() {
        return base64Content;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    public DocumentToSign(String filePath) throws IOException {
        this.filePath = filePath;

        File file = new File(filePath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        this.base64Content = Base64.getEncoder().encodeToString(bytes);
    }


}
