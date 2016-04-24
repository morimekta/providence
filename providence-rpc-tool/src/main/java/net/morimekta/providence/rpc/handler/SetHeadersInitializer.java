package net.morimekta.providence.rpc.handler;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by morimekta on 4/24/16.
 */
public class SetHeadersInitializer implements HttpRequestInitializer {
    Map<String, String> headers;

    public SetHeadersInitializer(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            switch (entry.getKey().toLowerCase()) {
                case "authorization":
                    request.getHeaders().setAuthorization(entry.getValue());
                    break;
                default:
                    request.getHeaders().set(entry.getKey(), entry.getValue());
                    break;
            }
        }
    }
}
