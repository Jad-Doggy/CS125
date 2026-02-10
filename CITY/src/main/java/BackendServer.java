import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BackendServer
{

    private static SearchService searchService;

    public static void main(String[] args) throws Exception {

        // Load POIs
        ReadCSV readCsv = new ReadCSV("pois.csv");
        List<Poi> pois = readCsv.returnPOIs();
        searchService = new SearchService(pois);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // ---- ping endpoint ----
        server.createContext("/api/ping", BackendServer::handlePing);


        server.setExecutor(null);
        server.start();

        System.out.println("Backend running at http://localhost:8080");

    }


    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "http://localhost:5173"
        );
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }

    private static void handlePing(HttpExchange exchange) throws IOException {

        addCors(exchange);

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String json = "{ \"status\": \"ok\" }";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

}

