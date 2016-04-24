package net.morimekta.providence.rpc.handler;

import com.google.api.client.http.*;
import net.morimekta.providence.PClientHandler;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.*;
import net.morimekta.providence.thrift.TCompactProtocolSerializer;
import net.morimekta.providence.thrift.TJsonProtocolSerializer;
import net.morimekta.providence.thrift.TTupleProtocolSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * HTTP client handler using the google HTTP client interface.
 *
 * TODO: Move to main providence as http utility module?
 */
public class HttpClientHandler implements PClientHandler {
    private final HttpRequestFactory factory;
    private final Serializer requestSerializer;
    private final GenericUrl endpoint;

    public HttpClientHandler(GenericUrl endpoint,
                             HttpRequestFactory factory,
                             Serializer requestSerializer) {
        this.endpoint = endpoint;
        this.factory = factory;
        this.requestSerializer = requestSerializer;
    }

    @Override
    public <RQ extends PMessage<RQ>, RS extends PMessage<RS>> PServiceCall<RS>
    handleCall(PServiceCall<RQ> pServiceCall, PService service) throws IOException, SerializerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestSerializer.serialize(baos, pServiceCall);

        ByteArrayContent content = new ByteArrayContent(requestSerializer.mimeType(), baos.toByteArray());

        HttpRequest request = factory.buildPostRequest(endpoint, content);
        HttpResponse response = request.execute();

        Serializer responseSerializer = requestSerializer;
        if (response.getContentType() != null) {
            switch (response.getContentType()) {
                case BinarySerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof BinarySerializer)) {
                        responseSerializer = new BinarySerializer();
                    }
                    break;
                case FastBinarySerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof FastBinarySerializer)) {
                        responseSerializer = new FastBinarySerializer();
                    }
                    break;
                // "simple" JSON is handles as the providence JSON format, not thrift's.
                case "application/json":
                case JsonSerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof JsonSerializer)) {
                        responseSerializer = new JsonSerializer();
                    }
                    break;
                case TCompactProtocolSerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof TCompactProtocolSerializer)) {
                        responseSerializer = new TCompactProtocolSerializer();
                    }
                    break;
                case TJsonProtocolSerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof TJsonProtocolSerializer)) {
                        responseSerializer = new TJsonProtocolSerializer();
                    }
                    break;
                case TTupleProtocolSerializer.MIME_TYPE:
                    if (!(responseSerializer instanceof TTupleProtocolSerializer)) {
                        responseSerializer = new TTupleProtocolSerializer();
                    }
                    break;
                default:
                    throw new IOException("Unknown mime type in response: " + response.getContentType());
            }
        }

        return responseSerializer.deserialize(response.getContent(), service);
    }
}
