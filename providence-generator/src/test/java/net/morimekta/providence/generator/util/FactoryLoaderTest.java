package net.morimekta.providence.generator.util;

import net.morimekta.providence.generator.Generator;
import net.morimekta.providence.generator.GeneratorFactory;
import net.morimekta.providence.generator.GeneratorOptions;
import net.morimekta.testing.ResourceUtils;

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FactoryLoaderTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testLoader() {
        GeneratorOptions options = new GeneratorOptions();
        options.generator_program_name = "loader-test";
        options.program_version = "1.0";
        ResourceUtils.copyResourceTo("/test.jar", tmp.getRoot());
        FactoryLoader loader = new FactoryLoader(tmp.getRoot());

        List<GeneratorFactory> factories = loader.getFactories();
        assertThat(factories, hasSize(1));

        GeneratorFactory factory = factories.get(0);

        assertThat(factory.generatorName(), is("js"));
        assertThat(factory.generatorDescription(), is("Generates JavaScript (es5.1 or es6)."));

        // Make sure we can instantiate the generator.
        Generator generator = factory.createGenerator(new FakeFileManager(tmp.getRoot()),
                                                      options,
                                                      ImmutableList.of());
        assertThat(generator, is(notNullValue()));
    }
}
