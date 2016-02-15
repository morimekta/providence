package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.PBuilderFactory;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PPrimitive;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.reflect.contained.CField;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Stein Eldar Johnsen
 * @since 25.10.15.
 */
public class JAnnotationTest {
    private Map<String, Boolean> deprecated;

    @Before
    public void setUp() {
        deprecated = new TreeMap<>();
        deprecated.put("@deprecated", true);
        deprecated.put("@Deprecated", true);
        deprecated.put("Something\n" + "@deprecated", true);
        deprecated.put("@deprecated something", true);
        deprecated.put("@deprecated since 20150225, use HTTP request header instead", true);
        deprecated.put("@deprecated\n" + "Something", true);
        deprecated.put("Something", false);
        deprecated.put("Deprecated", false);
        deprecated.put("deprecated", false);
        deprecated.put("@Something", false);
    }

    @Test
    public void testHasDeprecatedAnnotation() {
        for (Map.Entry<String, Boolean> entry : deprecated.entrySet()) {
            PDeclaredDescriptor descriptor = new PDeclaredDescriptor(entry.getKey(), "tmp", "name") {
                @Override
                public PBuilderFactory factory() {
                    return null;
                }

                @Override
                public PType getType() {
                    return null;
                }
            };

            CField<?> field = new CField<>(entry.getKey(),
                                           1,
                                           PRequirement.DEFAULT,
                                           "name",
                                           PPrimitive.BINARY.provider(),
                                           null);

            assertEquals(entry.getValue(), JAnnotation.isDeprecated(descriptor));
            assertEquals(entry.getValue(), JAnnotation.isDeprecated(field));
        }
    }
}
