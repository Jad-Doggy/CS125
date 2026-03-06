import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BackendServer
{
    private static SearchService searchService;

    public static void main(String[] args) throws Exception {

        // Load POIs (classpath resource first; falls back to filesystem inside ReadCSV)
        ReadCSV readCsv = ReadCSV.fromResource("pois_geocoded.csv");
        List<Poi> pois = readCsv.returnPOIs();
        searchService = new SearchService(pois);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // endpoints
        server.createContext("/api/search", BackendServer::handleSearch);
        server.createContext("/api/ping", BackendServer::handlePing);

        server.setExecutor(null);
        server.start();

        System.out.println("Backend running at http://localhost:8080");
    }

    private static void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void handlePing(HttpExchange exchange) throws IOException {
        addCors(exchange);

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String json = "{ \"status\": \"ok\" }";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static void handleSearch(HttpExchange exchange) throws IOException {
        addCors(exchange);

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        Map<String, String> q = parseQuery(exchange.getRequestURI());

        // tags=coffee,boba
        Set<String> tags = new LinkedHashSet<>();
        String tagsParam = q.getOrDefault("tags", "");
        if (!tagsParam.isBlank()) {
            for (String t : tagsParam.split(",")) {
                String s = t.trim();
                if (!s.isEmpty()) tags.add(s);
            }
        }

        // lat, lon optional
        GeoPoint userLoc = null;
        try {
            if (q.containsKey("lat") && q.containsKey("lon")) {
                double lat = Double.parseDouble(q.get("lat"));
                double lon = Double.parseDouble(q.get("lon"));
                userLoc = new GeoPoint(lat, lon);
            }
        } catch (Exception ignored) {}

        Double maxDist = null;
        try {
            if (q.containsKey("maxDist")) maxDist = Double.parseDouble(q.get("maxDist"));
        } catch (Exception ignored) {}

        Integer maxPrice = null;
        try {
            if (q.containsKey("maxPrice")) maxPrice = Integer.parseInt(q.get("maxPrice"));
        } catch (Exception ignored) {}

        boolean openNowOnly = "true".equalsIgnoreCase(q.getOrDefault("openNow", "false"));

        int k = 25;
        try {
            if (q.containsKey("k")) k = Integer.parseInt(q.get("k"));
        } catch (Exception ignored) {}

        SearchRequest req = new SearchRequest(tags, userLoc, maxDist, maxPrice, openNowOnly, null);
        List<SearchResult> results = searchService.searchTopK(req, k);

        String json = toJson(results);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    // helpers

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> out = new HashMap<>();
        String raw = uri.getRawQuery();
        if (raw == null || raw.isEmpty()) return out;

        for (String pair : raw.split("&")) {
            int idx = pair.indexOf('=');
            String k = (idx >= 0) ? pair.substring(0, idx) : pair;
            String v = (idx >= 0) ? pair.substring(idx + 1) : "";
            k = URLDecoder.decode(k, StandardCharsets.UTF_8);
            v = URLDecoder.decode(v, StandardCharsets.UTF_8);
            out.put(k, v);
        }
        return out;
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String toJson(List<SearchResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"results\":[");
        for (int i = 0; i < results.size(); i++) {
            SearchResult r = results.get(i);
            if (i > 0) sb.append(",");

            Poi p = r.poi;
            sb.append("{");
            sb.append("\"name\":\"").append(jsonEscape(p.name)).append("\",");

            if (Double.isNaN(r.distanceMiles)) sb.append("\"distanceMiles\":null,");
            else sb.append("\"distanceMiles\":").append(String.format(Locale.US, "%.4f", r.distanceMiles)).append(",");

            sb.append("\"tagMatches\":").append(r.tagMatches).append(",");
            sb.append("\"score\":").append(String.format(Locale.US, "%.4f", r.score)).append(",");

            sb.append("\"why\":[");
            for (int j = 0; j < r.why.size(); j++) {
                if (j > 0) sb.append(",");
                sb.append("\"").append(jsonEscape(r.why.get(j))).append("\"");
            }
            sb.append("]");

            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }
}