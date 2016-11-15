package net.morimekta.providence.server;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.SerializerException;

import java.io.IOException;

/**
 * Wrapping processor in a callback style
 */
public class WrappedProcessor implements PProcessor {
    public interface ProcessorWrapper {
        PServiceCall handleWrappedCall(PServiceCall call, PProcessor processor) throws IOException;
    }

    private final PProcessor       processor;
    private final ProcessorWrapper processorWrapper;

    public WrappedProcessor(PProcessor processor, ProcessorWrapper processorWrapper) {
        this.processor = processor;
        this.processorWrapper = processorWrapper;
    }

    @Override
    public PService getDescriptor() {
        return processor.getDescriptor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(
            PServiceCall<Request, RequestField> call,
            PService service) throws IOException {
        return processorWrapper.handleWrappedCall(call, processor);
    }
}
