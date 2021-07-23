package com.example.demo.Documents;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;

public class DocumentToSign {
    private String filePath;
    private String base64Content;


    public DocumentToSign(String filePath) throws IOException {
        this.filePath = filePath;

        File file = new File(filePath);
        byte[] bytes = Files.readAllBytes(file.toPath());
        this.base64Content = Base64.getEncoder().encodeToString(bytes);
    }

    public static DocumentToSign convertFileToBase64(String filename, String files) throws IOException {
        File file = new File(files);
        for (File file1 : Objects.requireNonNull(file.listFiles())) {
            if (file1.getName().equals(filename)) {
                return new DocumentToSign(file1.toString());
            }
        }
        return null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }


}
