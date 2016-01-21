package warehouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Vi
 */
public class DataWarehouse {
    public static void main(String[] args) throws IOException, InterruptedException {
        new DataWarehouse(args).run();
    }

    public DataWarehouse(String[] args) {

    }

    public void run() throws IOException, InterruptedException {
        InetAddress host = InetAddress.getByName("0.0.0.0");
        int port = 3000;

        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 1000);

        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/employee/", new MethodRouter() {
            @Override
            void get(HttpExchange he) throws Exception {
                new EmployeesController(he).show();
            }

            @Override
            void post(HttpExchange he) throws Exception {
                new EmployeesController(he).create();
            }

            @Override
            void patch(HttpExchange he) throws Exception {
                new EmployeesController(he).update();
            }
        });

        server.createContext("/employees/", new MethodRouter() {
            @Override
            void get(HttpExchange he) throws Exception {
                new EmployeesController(he).index();
            }
        });

        server.createContext("/update/employees/", new MethodRouter() {
            @Override
            void get(HttpExchange he) throws Exception {
                new EmployeesController(he).getUpdates();
            }
        });

        server.start();
        System.out.println("Server started.");
    }
}
