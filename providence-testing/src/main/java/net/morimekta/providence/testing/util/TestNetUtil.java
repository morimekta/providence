package net.morimekta.providence.testing.util;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.fail;

/**
 * Networking utility for testing.
 */
public class TestNetUtil {
    public static int findFreePort() {
        int port = -1;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (IOException e) {
            fail("Unable to locate free port.");
        }
        return port;
    }

    public static HttpRequestFactory factory() {
        return transport().createRequestFactory();
    }

    public static HttpRequestFactory factory(HttpRequestInitializer initializer) {
        return transport().createRequestFactory(initializer);
    }

    public static HttpTransport transport() {
        return new ApacheHttpTransport();
    }
}
