package net.morimekta.providence.gentests;

import net.morimekta.providence.model.ProgramType;
import net.morimekta.providence.util.ProvidenceHelper;
import net.morimekta.test.providence.testing.includes.Program;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing that the maven plugin can compile targets containing references to
 * thrift files in dependencies.
 */
public class InclusionTest {
    @Test
    public void testInclusion() {
        // Program is declared in the test
        Program prog = Program.builder()
                              .setProgram(ProgramType.builder()
                                                     .setProgramName("program")
                                                     .build())
                              .build();

        assertEquals("{\n" +
                     "  program = {\n" +
                     "    program_name = \"program\"\n" +
                     "  }\n" +
                     "}",
                     ProvidenceHelper.debugString(prog));
    }
}
