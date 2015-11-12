package client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class JSONReaderExample {

    public static void main(String[] args) {
        String strFile = "";
        String location = "config/example.json";

        try{
            strFile = new Scanner(new File(location)).useDelimiter("\\Z").next();
            JSONObject json = new JSONObject(strFile);

            System.out.println(json.get("menu").toString());

            JSONArray arr = ((JSONArray) ((JSONObject) ((JSONObject) json.get("menu")).get("popup")).get(("menuitem")));
            System.out.println(arr.length());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
