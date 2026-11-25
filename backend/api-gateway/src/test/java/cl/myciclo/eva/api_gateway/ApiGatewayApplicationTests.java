package cl.myciclo.eva.api_gateway;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    private static final HttpServer USUARIOS = startBackend("usuarios", 201);
    private static final HttpServer SEGUIMIENTO = startBackend("seguimiento", 200);

    private final HttpClient client = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void configureBackendUrls(DynamicPropertyRegistry registry) {
        registry.add("USUARIOS_SERVICE_URL", () -> backendUrl(USUARIOS));
        registry.add("SEGUIMIENTO_SERVICE_URL", () -> backendUrl(SEGUIMIENTO));
    }

    @AfterAll
    static void stopBackends() {
        USUARIOS.stop(0);
        SEGUIMIENTO.stop(0);
    }

    @Test
    void forwardsRegistrationWithItsBodyAndReturnsTheServiceStatus() throws Exception {
        String path = "/api/v1/usuarios/registro";
        String body = "{\"nombre\":\"EVA\"}";
        HttpRequest request = request(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.body()).isEqualTo("usuarios\nPOST\n" + path + "\n" + body);
    }

    @Test
    void forwardsTrackingRequestsWithTheirQueryParameters() throws Exception {
        String path = "/api/v1/seguimiento/bitacoras?pagina=1";

        HttpResponse<String> response = client.send(
                request(path).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("seguimiento\nGET\n" + path + "\n");
    }

    @Test
    void returnsNotFoundForAnUnconfiguredRoute() throws Exception {
        HttpResponse<Void> response = client.send(
                request("/api/v1/desconocido").GET().build(), HttpResponse.BodyHandlers.discarding());

        assertThat(response.statusCode()).isEqualTo(404);
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(10));
    }

    private static String backendUrl(HttpServer server) {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    private static HttpServer startBackend(String name, int status) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                try (exchange) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    String result = String.join("\n", name, exchange.getRequestMethod(),
                            exchange.getRequestURI().toString(), body);
                    byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(status, bytes.length);
                    exchange.getResponseBody().write(bytes);
                }
            });
            server.start();
            return server;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
