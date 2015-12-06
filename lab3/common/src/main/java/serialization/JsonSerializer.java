package serialization;


import common.Employee;
import common.Employees;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonSerializer implements Serializer{
    @Override
    public void serialize(Employees employees, OutputStream os) {
        JSONArray jsonArray = new JSONArray();

        for (Employee emp: employees) {
            jsonArray.put(emp.toJSON());
        }

        System.out.println("[INFO]--------------------------------------------\n" +
                "[INFO] JSON string to be sent: " + jsonArray.toString());

        try {
//            OutputStreamWriter out = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            PrintWriter pw = new PrintWriter(os);
            pw.write(jsonArray.toString());
            pw.flush();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Employees deserialize(InputStream is) {

        Employees employees = null;
        try {

//            ObjectInputStream ois = new ObjectInputStream(is);
//            JSONArray jsonArray = (JSONArray) ois.readObject();

//            char[] bytes = new char[1024];
//            is.read(bytes);
//
//            String jsonString = bytes.toString();

//            BufferedReader bfrd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            BufferedReader bfrd = new BufferedReader(new InputStreamReader(new DataInputStream(is)));

            JSONArray jsonArray = new JSONArray(bfrd.readLine());

            System.out.println("[INFO]--------------------------------------------\n" +
                    "[INFO] Received JSON string: " + jsonArray);

            employees = new Employees();

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject o = (JSONObject) jsonArray.get(i);
                employees.add(Employee.fromJSON((JSONObject) o.getJSONObject("Employee")));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }
}
