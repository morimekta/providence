package net.morimekta.providence.it.serialization;

import net.morimekta.providence.util.PrettyPrinter;
import net.morimekta.test.number.Imaginary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing that the maven plugin can compile targets containing references to
 * thrift files in dependencies.
 */
public class InclusionTest {
    @Test
    public void testInclusion() {
        // HasNumber is declared in the test
        HasNumber has = HasNumber.builder()
                                 .setI(new Imaginary(0.2, 0.1))
                                 .build();

        assertEquals("i = {\n" +
                     "  v = 0.2\n" +
                     "  i = 0.1\n" +
                     "}",
                     PrettyPrinter.debugString(has));
    }
}
