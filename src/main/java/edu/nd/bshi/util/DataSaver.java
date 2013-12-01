package edu.nd.bshi.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class DataSaver {
    private String data_path = "";
    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;

    public DataSaver(String path) {
        this.data_path = path;
        try {
            fileWriter = new FileWriter(this.data_path);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
            fileWriter = null;
        }
    }

    public boolean write(String str) {
        try {
            bufferedWriter.write(str);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean write(LinkedHashSet<LinkedList<Integer>> path, int startId, int stopId) {
        try {
            while (path.iterator().hasNext()) {
                bufferedWriter.write(startId + "," + stopId + "," + path.iterator().next().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
