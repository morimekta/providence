package net.morimekta.providence.client;

import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.Serializer;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * HTTP client handler using the google HTTP client interface.
 *
 * TODO: Move to main providence as http utility module?
 */
public class HttpClientHandler implements PClientHandler {
    private final HttpRequestFactory factory;
    private final SerializerProvider serializerProvider;
    private final Serializer         requestSerializer;
    private final GenericUrl         endpoint;

    public HttpClientHandler(GenericUrl endpoint, HttpRequestFactory factory, SerializerProvider serializerProvider) {
        this.endpoint = endpoint;
        this.factory = factory;
        this.serializerProvider = serializerProvider;
        this.requestSerializer = serializerProvider.getDefault();

        if (requestSerializer == null) {
            throw new IllegalStateException("Serializer provider has no default serializer");
        }
    }

    @Override
    public <RQ extends PMessage<RQ>, RS extends PMessage<RS>> PServiceCall<RS> handleCall(PServiceCall<RQ> pServiceCall,
                                                                                          PService service)
            throws IOException, SerializerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestSerializer.serialize(baos, pServiceCall);

        ByteArrayContent content = new ByteArrayContent(requestSerializer.mimeType(), baos.toByteArray());

        HttpRequest request = factory.buildPostRequest(endpoint, content);
        HttpResponse response = request.execute();

        Serializer responseSerializer = requestSerializer;
        if (response.getContentType() != null) {
            responseSerializer = serializerProvider.getSerializer(response.getContentType());
            if (responseSerializer == null) {
                throw new IOException("Unknown mime type in response: " + response.getContentType());
            }
        }

        return responseSerializer.deserialize(response.getContent(), service);
    }
}
