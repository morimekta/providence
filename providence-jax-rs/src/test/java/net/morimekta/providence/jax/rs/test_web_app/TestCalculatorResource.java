package net.morimekta.providence.jax.rs.test_web_app;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.serializer.BinarySerializer;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.calculator.CalculateException;
import net.morimekta.test.calculator.Calculator;
import net.morimekta.test.calculator.Operation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Test service implementation for dropwizard testing.
 */
@Path("/calculator")
@Consumes({JsonSerializer.MIME_TYPE, BinarySerializer.MIME_TYPE, BinarySerializer.ALT_MIME_TYPE})
@Produces({JsonSerializer.MIME_TYPE, BinarySerializer.MIME_TYPE, BinarySerializer.ALT_MIME_TYPE})
public class TestCalculatorResource {
    Calculator.Iface impl;

    public TestCalculatorResource(Calculator.Iface impl) {
        this.impl = impl;
    }

    @GET
    public Response root() {
        return Response.noContent().build();
    }

    @POST
    @Path("/calculate")
    public Response postCalculate(Operation pOp) {
        try {
            return Response.ok()
                           .entity(impl.calculate(pOp))
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(new PApplicationException(
                                   e.getMessage(),
                                   PApplicationExceptionType.INTERNAL_ERROR))
                           .build();
        } catch (CalculateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e)
                           .build();
        }
    }

    @GET
    @Path("/iamalive")
    public Response getIAmAlive() {
        try {
            impl.iamalive();
            // Do nothing.
            return Response.noContent()
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(new PApplicationException(
                                   e.getMessage(),
                                   PApplicationExceptionType.INTERNAL_ERROR))
                           .build();
        }
    }
}
