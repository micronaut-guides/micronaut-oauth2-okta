package example.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeniedControllerTest {

    private static EmbeddedServer server;
    private static HttpClient client;

    private static boolean shouldIgnore() {
        return System.getenv("OAUTH_CLIENT_SECRET") == null ||
                System.getenv("OAUTH_CLIENT_ID") == null ||
        System.getenv("OKTA_DOMAIN") == null ||
                System.getenv("OKTA_AUTHSERVERID") == null;
    }
    @BeforeClass
    public static void setupServer() {
        if (!shouldIgnore()) {
            server = ApplicationContext.run(EmbeddedServer.class);
            client = server
                    .getApplicationContext()
                    .createBean(HttpClient.class, server.getURL());
        }
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    public void testDenied() {
        if (shouldIgnore()) {
            assertTrue(true);
        } else {
            HttpRequest request = HttpRequest.GET("/denied");
            String body = client.toBlocking().retrieve(request);
            assertNotNull(body);
            assertTrue(body.contains("Denied"));
            assertTrue(body.contains("Sorry, you're not authorized to view this page"));
        }
    }
}
