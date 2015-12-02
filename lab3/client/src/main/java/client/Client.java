package client;

import discovery.DiscoveryClient;
import common.Employee;
import common.Location;
import transport.TransportClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Can be started in terminal with:
 * gradle run
 */
public class Client {
    public static void main(String[] args) {
        System.out.println("[INFO] -----------------------------------------\n" +
                "[INFO] Client is running...");

        try {
            Location location = new DiscoveryClient(
                    new InetSocketAddress("127.0.0.1", 33333))
                    .retrieveLocation();
            System.out.println("[INFO] -----------------------------------------\n" +
                    "[INFO] Discovered server: " + location);

            if (location != null) {
                showFiltered(
                        new TransportClient()
                                .getEmployeesFrom(location, "client"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showFiltered(ArrayList<Employee> list) {
        System.out.println("[Result] -----------------------------------------\n" +
                        "Discovered employees: " +
                        list.stream()
                                .filter(e -> e.getSalary() > 500.0)
                                .sorted(Comparator.comparing(Employee::getLastName))
                                .collect(Collectors.groupingBy(Employee::getDepartment))
                                .toString()
        );
    }
}