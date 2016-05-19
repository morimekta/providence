package net.morimekta.providence.rpc.handler;

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

    public SetHeadersInitializer(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        HttpHeaders http = request.getHeaders();
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
                    http.set(entry.getKey(), value);
                    break;
            }
        }
    }
}
