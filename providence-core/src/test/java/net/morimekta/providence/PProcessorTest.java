package net.morimekta.providence;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.test.providence.core.calculator.Calculator;
import net.morimekta.test.providence.core.calculator.Operation;

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class PProcessorTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testHandleCall() throws IOException {
        PProcessor processor = new TestProcessor();
        PServiceCall call = new PServiceCall<>(
                "calculate",
                PServiceCallType.CALL,
                42,
                Operation.builder().build());
        PServiceCall reply = processor.handleCall(call);

        assertThat(reply, is(notNullValue()));
        assertThat(reply.getMessage(), is(sameInstance(call.getMessage())));
        assertThat(reply.toString(),
                   is("PServiceCall{method=calculate, type=REPLY, seq=42, message=calculator.Operation{}}"));

        assertThat(call.hashCode(), is(call.hashCode()));
        assertThat(reply.hashCode(), is(not(call.hashCode())));
    }

    private class TestProcessor implements PProcessor {
        @Nonnull
        @Override
        public PService getDescriptor() {
            return Calculator.kDescriptor;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <Request extends PMessage<Request, RequestField>, Response extends PMessage<Response, ResponseField>, RequestField extends PField, ResponseField extends PField> PServiceCall<Response, ResponseField> handleCall(
                PServiceCall<Request, RequestField> call,
                PService service) throws IOException {
            assertThat(service, is(sameInstance(getDescriptor())));

            return new PServiceCall(
                    call.getMethod(),
                    PServiceCallType.REPLY,
                    call.getSequence(),
                    call.getMessage());
        }
    }
}
