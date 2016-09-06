package net.morimekta.providence.generator.format.java.tiny;

import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.util.FileManager;
import net.morimekta.providence.reflect.TypeLoader;
import net.morimekta.providence.reflect.parser.ParseException;
import net.morimekta.providence.reflect.parser.ThriftDocumentParser;
import net.morimekta.providence.reflect.util.DocumentRegistry;
import net.morimekta.util.io.IOUtils;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by morimekta on 6/12/16.
 */
public class TinyGeneratorTest {
    @Rule
    public  TemporaryFolder      tmp;
    private FileManager          fileManager;
    private File                 out;
    private DocumentRegistry     documentRegistry;
    private TypeLoader           typeLoader;
    private ThriftDocumentParser parser;
    private File                 inc;
    private File                 file;

    @Before
    public void setUp() throws IOException {
        tmp = new TemporaryFolder();
        tmp.create();

        file = tmp.newFile("test.thrift");

        try (FileOutputStream fos = new FileOutputStream(file, false);
             InputStream in = getClass().getResourceAsStream("/net/morimekta/providence/generator/format/java/test.thrift")) {
            IOUtils.copy(in, fos);
        }

        out = tmp.newFolder("out");
        inc = tmp.newFolder("includes");

        fileManager = new FileManager(out);
        parser = new ThriftDocumentParser();
        typeLoader = new TypeLoader(ImmutableList.of(inc), parser);
        documentRegistry = new DocumentRegistry();
    }

    @Test
    public void testGenerate_simple() throws GeneratorException, IOException, ParseException {
        TinyOptions options = new TinyOptions();

        TinyGenerator generator = new TinyGenerator(fileManager, documentRegistry, options);
        generator.generate(typeLoader.load(file));

        File test = new File(out, "net/morimekta/test/java/Test.java");
        assertTrue(test.exists());
    }

}
