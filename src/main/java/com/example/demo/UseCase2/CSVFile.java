package com.example.demo.UseCase2;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFile {

    private String filePath;
    private List<String> CSVFileList;

    public CSVFile(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getCSVFileList() {
        return CSVFileList;
    }

    public void setCSVFileList(List<String> CSVFile) {
        this.CSVFileList = CSVFile;
    }


    public void readCSVFile() throws FileNotFoundException {
        List<List<String>> records = new ArrayList<>();
        List<String> csvFileList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(this.getFilePath()))) {

            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }

            for (List<String> record : records) {
                csvFileList.add(record.get(0));
            }

            setCSVFileList(csvFileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
