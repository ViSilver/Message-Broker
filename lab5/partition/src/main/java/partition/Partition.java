package partition;


import common.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Vi
 */
public class Partition {

    private String dataFilePath;
    private String hostName;
    private String warehouseHostName;
    private String mimeType;
    private int pollPeriod;
    private Map<Integer, Employee> employees;
    private Serializer serializer;

    public static void main(String[] args) throws Exception {
        new Partition(args).run();
    }

    public Partition(String[] args) throws Exception {
        this.hostName = args[0];
        this.dataFilePath = args[1];
        this.warehouseHostName = args[2];
        this.pollPeriod = 10000;
        this.employees = new HashMap<Integer, Employee>();

        if (args[3].equals("json")) {
            this.mimeType = "application/json";
            this.serializer = new JSONSerializer();
        } else if (args[3].equals("xml")) {
            this.mimeType = "application/xml";
            this.serializer = new XMLSerializer();
        } else {
            throw new Exception("Wrong format.");
        }
    }

    public void run() throws Exception {
        loadData();
        pollUpdates();
    }

    protected void loadData() throws Exception {
        InputStream fileIn = new FileInputStream(new File(dataFilePath));

        Employees list = new JSONSerializer().deserialize(fileIn, Employees.class);

        URL url = new URL("http://" + warehouseHostName + "/employee/");

        Iterator<Employee> it = list.getEmployees().listIterator();
        while (it.hasNext()) {
            Employee e = it.next();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", mimeType);
            conn.setRequestProperty("Accept", mimeType);
            conn.setRequestProperty("From", hostName);
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            serializer.serialize(e, out);

            InputStream in = conn.getInputStream();
            e = serializer.deserialize(in, Employee.class);
            employees.put(e.getID(), e);

            conn.disconnect();
        }
    }

    protected void pollUpdates() throws Exception {
        URL url = new URL("http://" + warehouseHostName + "/update/employees/");

        while (true) {
            Date t = new Date();
            Thread.sleep(pollPeriod);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", mimeType);
            conn.setRequestProperty("From", hostName);
            conn.setIfModifiedSince(t.getTime());

            if (conn.getResponseCode() == 304) {
                conn.disconnect();
                continue;
            }

            InputStream in = conn.getInputStream();
            Employees list = serializer.deserialize(in, Employees.class);

            list.getEmployees().forEach((e) -> {
                employees.put(e.getID(), e);
            });

            System.out.printf("Updated %d records.\n", list.getEmployees().size());

            conn.disconnect();
        }
    }
}
