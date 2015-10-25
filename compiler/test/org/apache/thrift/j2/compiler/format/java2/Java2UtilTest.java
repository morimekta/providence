package org.apache.thrift.j2.compiler.format.java2;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 25.10.15.
 */
public class Java2UtilTest {
    @Test
    public void testHasDeprecatedAnnotation() {
        assertTrue(Java2Utils.hasDeprecatedAnnotation("@deprecated"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("@Deprecated"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("Something\n@deprecated"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("@deprecated something"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("@deprecated since 20150225, use HTTP request header instead"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("@deprecated\nSomething"));
        assertTrue(Java2Utils.hasDeprecatedAnnotation("Something\n@deprecated\n"));
        assertFalse(Java2Utils.hasDeprecatedAnnotation(null));
        assertFalse(Java2Utils.hasDeprecatedAnnotation("Something"));
        assertFalse(Java2Utils.hasDeprecatedAnnotation("Deprecated"));
        assertFalse(Java2Utils.hasDeprecatedAnnotation("deprecated"));
    }
}
