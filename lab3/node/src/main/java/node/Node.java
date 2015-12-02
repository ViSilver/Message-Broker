package node;

import discovery.DiscoveryListener;
import org.json.JSONArray;
import org.json.JSONObject;
import transport.TransportListener;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * if gradle plugin installed
 * the app can be started in terminal with:
 * gradle run -Pargs="config/nodeN.json"
 * where N is the int id of the node
 */
public class Node {
    public static void main(String[] args) {
        int mavenServerPort = 5555;
        ArrayList<Integer> neighbourPorts = new ArrayList<>();
        String empLocation = "";

        if (args.length > 0) {
            String strFile = "";
            try{
                strFile = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
                JSONObject json = new JSONObject(strFile);

                json = (JSONObject) json.get("node");

                mavenServerPort = json.getInt("maven port");
                empLocation = json.get("employee Location").toString();
                JSONArray arr = (JSONArray) json.get("neighbours");

                for (int i = 0; i < arr.length(); i++) {
                    neighbourPorts.add(arr.getInt(i));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        ArrayList<InetSocketAddress> neighbourLocations = new ArrayList<>();

        neighbourPorts.stream()
                .forEach(p -> neighbourLocations.add(new InetSocketAddress("127.0.0.1", p)));

        InetSocketAddress serverLocation = new InetSocketAddress("127.0.0.1", mavenServerPort);
        System.out.println("[INFO] -----------------------------------------\n" +
                "[INFO] Server is running... on " + mavenServerPort);

        new DiscoveryListener(serverLocation)
                .start();

        TransportListener transportListener =
                new TransportListener(new InetSocketAddress("127.0.0.1", mavenServerPort), empLocation, neighbourLocations);
        transportListener.start();

        try {
            Thread.sleep(SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
