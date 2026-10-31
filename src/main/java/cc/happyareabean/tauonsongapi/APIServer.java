package cc.happyareabean.tauonsongapi;

import cc.happyareabean.tauonsongapi.json.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.CorsPluginConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class APIServer {
    
    private static final List<String> VALID_GET = Arrays.asList(
        "song", "title", "artist", "album", "album-art", "status", "full"
    );

    private static final List<String> VALID_DATA_TYPE = Arrays.asList(
            "json", "plain"
    );
    
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String hostName;
    private static String APP_URL;
    private static String STATIC_FILES_PATH;

    public static void main(String[] args) {
        hostName = System.getenv("TAUON_API_URL");
        APP_URL = System.getenv("APP_URL");
        STATIC_FILES_PATH = System.getenv("STATIC_FILES_PATH");

        var portEnv = System.getenv("APP_PORT");
        int APP_PORT = portEnv == null ? 7000 : Integer.parseInt(portEnv);

        var apiServer = new APIServer();

        Javalin app = Javalin.create(config -> {

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });

            config.routes.get("/", ctx -> ctx.status(404));

            config.routes.get("/get/{type}", apiServer::handleDownload);
            config.routes.get("/get/album-art/{id}", apiServer::handleAlbumArt);
            config.routes.get("/get/{type}/{dataType}", apiServer::handleDownload);

            config.routes.get("/static/{fileName}", apiServer::handleStaticFileProxy);
        }).start(APP_PORT);
    }

    private void handleStaticFileProxy(Context ctx) throws IOException {
        String htmlDir = STATIC_FILES_PATH == null ? "./html" : STATIC_FILES_PATH;
        Path overlayDir = Path.of(htmlDir).toAbsolutePath().normalize();
        Path file = overlayDir.resolve(ctx.pathParam("fileName")).normalize();

        if (!file.startsWith(overlayDir) || !Files.isRegularFile(file)) {
            ctx.status(404);
            return;
        }

        String contentType = Files.probeContentType(file);
        ctx.contentType(contentType != null ? contentType : "application/octet-stream");
        ctx.result(Files.readAllBytes(file));
    }

    private void handleAlbumArt(Context ctx) {
        String id = ctx.pathParam("id");

        try {
            String albumArtUrl = "http://" + hostName + "/api1/pic/medium/" + id;

            var art = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(albumArtUrl)).GET().build(), HttpResponse.BodyHandlers.ofByteArray());

            ctx.contentType(ContentType.IMAGE_PNG).result(art.body());
        } catch (Exception e) {
            ctx.status(404).result("error: " + e.getMessage());
        }
    }
    
    private void handleDownload(Context ctx) {
        String type = ctx.pathParam("type");
        String format = ctx.queryString() == null ? null : ctx.queryParam("format");

        if (hostName == null) {
            ctx.status(400).json(Map.of("error", true, "message", "hostname is null."));
            return;
        }
        
        if (!VALID_GET.contains(type)) {
            ctx.status(400).json(Map.of("error", true, "message", "Invalid type."));
            return;
        }

        if (format != null) {
            if (!VALID_DATA_TYPE.contains(format)) {
                ctx.status(400).json(Map.of("error", true, "message", "Invalid type."));
                return;
            }
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://" + hostName + "/api1/status"))
                .GET()
                .build();

        try {
            switch (type) {
                case "status", "full" -> {
                    Status root = fetchStatus(req);
                    String status = root.getStatus() == null ? "stopped" : root.getStatus();
                    String artist = root.getArtist();
                    String title = root.getTitle();
                    String album = root.getAlbum();

                    String plain = songText(root);

                    if (format != null) {
                        int id = root.getTrack().getId();
                        String autoAppHostName = ctx.req().getScheme() + "://" + ctx.req().getServerName() + ":" + ctx.req().getServerPort();
                        String albumArtUrl = (APP_URL == null ? autoAppHostName : APP_URL) + "/get/album-art/" + id;

                        switch (format) {
                            case "json" -> {
                                ctx.status(200).json(new GSASongData(status, artist, title, album, albumArtUrl, null));
                            }
                            case "plain" -> {
                                if (plain == null)
                                    throw new RuntimeException("error when parsing artist and song title");

                                ctx.status(404).result("error: format plain is not allowed in this endpoint.");
                            }
                        }
                        return;
                    }

                    if (status.equalsIgnoreCase("stopped") || status.equalsIgnoreCase("paused")) {
                        ctx.status(200).result("No song is playing as the player is stopped.");
                        return;
                    }

                    if (artist == null || title == null || artist.isEmpty() || title.isEmpty()) {
                        ctx.status(200).result("No song is playing.");
                        return;
                    }

                    if (plain == null)
                        throw new RuntimeException("error when parsing artist and song title");

                    ctx.result(plain);
                }
                case "song" -> {
                    Status root = fetchStatus(req);
                    String song = songText(root);

                    if (song == null)
                        throw new RuntimeException("Unable to parse song title");

                    switch (format) {
                        case "json" -> {
                            ctx.status(200).json(GSASongData.builder().song(song).build());
                        }
                        case "plain" -> {
                            ctx.result(song);
                        }
                        case null, default -> ctx.result(song);
                    }
                }
                case "title" -> {
                    Status root = fetchStatus(req);
                    String title = root.getTitle();

                    switch (format) {
                        case "json" -> {
                            ctx.status(200).json(GSASongData.builder().title(title).build());
                        }
                        case "plain" -> {
                            ctx.result(title);
                        }
                        case null, default -> ctx.result(title);
                    }
                }
                case "artist" -> {
                    Status root = fetchStatus(req);
                    String artist = root.getArtist();

                    switch (format) {
                        case "json" -> {
                            ctx.status(200).json(GSASongData.builder().artist(artist).build());
                        }
                        case "plain" -> {
                            ctx.result(artist);
                        }
                        case null, default -> ctx.result(artist);
                    }
                }
                case "album" -> {
                    Status root = fetchStatus(req);
                    String album = root.getAlbum();

                    switch (format) {
                        case "json" -> {
                            ctx.status(200).json(GSASongData.builder().album(album).build());
                        }
                        case "plain" -> {
                            ctx.result(album);
                        }
                        case null, default -> ctx.result(album);
                    }
                }
                case "album-art" -> {
                    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() != 200) {
                        ctx.status(resp.statusCode()).result("upstream error");
                        return;
                    }

                    Status root = objectMapper.readValue(resp.body(), Status.class);
                    int id = root.getTrack().getId();

                    String autoAppHostName = ctx.req().getScheme() + "://" + ctx.req().getServerName() + ":" + ctx.req().getServerPort();
                    String albumArtUrl = (APP_URL == null ? autoAppHostName : APP_URL) + "/get/album-art/" + id;

                    switch (format) {
                        case "json" -> {
                            ctx.status(200).json(GSASongData.builder().albumArt(albumArtUrl).build());
                        }
                        case "plain" -> {
                            ctx.result(albumArtUrl);
                        }
                        case null, default -> ctx.redirect("/get/album-art/" + id);
                    }
                }
            }
        } catch (Exception e) {
            ctx.status(404).json(Map.of("error", true, "message", "Internal server error: " + e.getClass().getName() + " - " + e.getMessage()));
        }
    }

    private Status fetchStatus(HttpRequest req)
            throws IOException, InterruptedException {
        HttpResponse<String> resp =
                httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("Invalid response code: " + resp.statusCode());
        }

        Status status = objectMapper.readValue(resp.body(), Status.class);
        if (status == null) {
            throw new RuntimeException("No or invalid response received");
        }

        return status;
    }

    private String formatArtist(String artist) {
        return artist == null ? "" : artist.replace("; ", ", ");
    }

    private String songText(Status root) {
        String artist = formatArtist(root.getArtist());
        String title = root.getTitle();

        if (artist.isBlank() || title == null || title.isBlank()) {
            return null;
        }
        return artist + " - " + title;
    }
}
