package net.morimekta.providence.tools.rpc.utils;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import java.io.IOException;
import java.util.Map;

/**
 * Request initializer that sets a set of fixed headers.
 */
public class SetHeadersInitializer implements HttpRequestInitializer {
    private final Map<String, String> headers;
    private final int                 connect_timeout;
    private final int                 read_timeout;

    public SetHeadersInitializer(Map<String, String> headers, int connect_timeout, int read_timeout) {
        this.headers = headers;
        this.connect_timeout = connect_timeout;
        this.read_timeout = read_timeout;
    }

    @Override
    public void initialize(HttpRequest request) {
        request.setConnectTimeout(connect_timeout);
        request.setReadTimeout(read_timeout);

        // With the interceptor will overwrite headers set by the Http client.
        request.setInterceptor(rq -> {
            HttpHeaders http = rq.getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                switch (entry.getKey().toLowerCase()) {
                    case "accept":
                        http.setAccept(value);
                        break;
                    case "accept-encoding":
                        http.setAcceptEncoding(value);
                        break;
                    case "authorization":
                        http.setAuthorization(value);
                        break;
                    case "content-encoding":
                        http.setContentEncoding(value);
                        break;
                    case "content-type":
                        http.setContentType(value);
                        break;
                    case "user-agent":
                        http.setUserAgent(value);
                        break;
                    default:
                        try {
                            http.set(entry.getKey(), value);
                        } catch (Exception e) {
                            // Turn the exception into one that we track and
                            // print proper error message. This is most likely
                            // because the header has to be handled with a
                            // special method.
                            throw new IOException("Unable to set header " + entry.getKey() + ": " + e.getMessage(), e);
                        }
                        break;
                }
            }
        });
    }
}
