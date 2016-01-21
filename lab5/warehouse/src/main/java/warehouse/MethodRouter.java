package warehouse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

/**
 * Created by Vi
 */
public class MethodRouter implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {

        String method = he.getRequestMethod();
        URI uri =  he.getRequestURI();

        System.out.print(method + ": ");
        System.out.println(uri);

        try {
            switch (he.getRequestMethod()) {
                case "GET":     get(he);    break;
                case "POST":    post(he);   break;
                case "PATCH":   patch(he);  break;
                case "PUT":     put(he);    break;
                case "DELETE":  delete(he); break;
                default:
                    he.sendResponseHeaders(404, -1);
                    break;
            }
        } catch(Exception ex) {
            he.sendResponseHeaders(500, 0);
            ex.printStackTrace(new PrintStream(he.getResponseBody()));
            ex.printStackTrace(System.out);
        }
        he.close();
    }

    void get(HttpExchange he) throws Exception {

    }

    void post(HttpExchange he) throws Exception {

    }

    void put(HttpExchange he) throws Exception {

    }

    void patch(HttpExchange he) throws Exception {

    }

    void delete(HttpExchange he) throws Exception {

    }
}
