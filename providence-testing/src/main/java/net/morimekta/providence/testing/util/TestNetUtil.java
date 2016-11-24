package net.morimekta.providence.testing.util;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import static org.junit.Assert.fail;

/**
 * Networking utility for testing.
 */
public class TestNetUtil {
    public static int getExposedPort(Server server) {
        for (Connector connector : server.getConnectors()) {
            if (connector instanceof ServerConnector) {
                return  ((ServerConnector) connector).getLocalPort();
            }
        }
        fail("Unable to determine port of server");
        return -1;
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
