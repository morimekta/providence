package net.morimekta.providence.jax.rs.test_web_app;

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.serializer.JsonSerializer;
import net.morimekta.test.providence.jax.rs.calculator.CalculateException;
import net.morimekta.test.providence.jax.rs.calculator.Calculator;
import net.morimekta.test.providence.jax.rs.calculator.Operation;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Test service implementation for jersey test app.
 */
@Path("/calculator")
public class TestCalculatorResource {
    private final Calculator.Iface impl;

    public TestCalculatorResource() {
        this.impl = new TestCalculator();
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
