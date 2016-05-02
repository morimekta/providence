package net.morimekta.providence.thrift.util;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.fail;

/**
 * Created by morimekta on 4/29/16.
 */
public class TestUtil {
    public static int findFreePort() {
        int port = -1;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (IOException e) {
            fail("Unable to locate free port.");
        }
        return port;
    }


}
