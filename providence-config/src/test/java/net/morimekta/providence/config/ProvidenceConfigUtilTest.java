package net.morimekta.providence.config;

import net.morimekta.providence.model.Declaration;
import net.morimekta.providence.model.Requirement;
import net.morimekta.providence.model.ThriftField;

import org.junit.Test;

import static net.morimekta.providence.config.ProvidenceConfigUtil.getInMessage;
import static org.junit.Assert.assertEquals;

/**
 * Testing for the providence config utils.
 */
public class ProvidenceConfigUtilTest {
    @Test
    public void testGetInMessage() {
        Declaration declaration = Declaration.withDeclConst(
                ThriftField.builder()
                           .setName("Name")
                           .setKey(44)
                           .setType("i32")
                           .build());

        // Return the field value.
        assertEquals(44, getInMessage(declaration, "decl_const.key"));
        assertEquals("Name", getInMessage(declaration, "decl_const.name"));
        // Return the field value even when default is set.
        assertEquals(44, getInMessage(declaration, "decl_const.key", 66));
        // If value is not set, and no default specified return the thrift field default.
        assertEquals(Requirement.DEFAULT, getInMessage(declaration, "decl_const.requirement"));
        // Specified default overrides the "default" default value.
        assertEquals(Requirement.REQUIRED, getInMessage(declaration, "decl_const.requirement", Requirement.REQUIRED));
        // Return null when there are no default in thrift, and none specified.
        assertEquals(null, getInMessage(declaration, "decl_const.default_value"));
    }
}
