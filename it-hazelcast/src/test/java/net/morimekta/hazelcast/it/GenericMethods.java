package net.morimekta.hazelcast.it;

import net.morimekta.providence.PEnumValue;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.testing.generator.GeneratorWatcher;
import net.morimekta.providence.testing.generator.SimpleGeneratorWatcher;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.codearte.jfairy.Fairy;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by scrier on 2017-01-05.
 */
public class GenericMethods {

    static { // fix logging.
        final String logging = "hazelcast.logging.type";
        System.setProperty(logging, "slf4j");
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public SimpleGeneratorWatcher generator = GeneratorWatcher.create();

    public Random rand;
    public Fairy  fairy;

    @Before
    public void setUp() {
        generator// .dumpOnFailure()
                 .getBaseContext()
                 .setDefaultFillRate(0.6667);
        rand = generator.getBaseContext().getRandom();
        fairy = generator.getBaseContext().getFairy();
    }

    @AfterClass
    public static void shutDownClass() {
        Hazelcast.shutdownAll();
    }

    protected static Config getV1Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v1.Hazelcastv1_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(1);
        return config;
    }

    protected static Config getV2Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v2.Hazelcastv2_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(2);
        return config;
    }

    protected static Config getV3Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v3.Hazelcastv3_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(3);
        return config;
    }

    protected static Config getV4Config() {
        Config config = new Config();
        net.morimekta.test.hazelcast.v4.Hazelcastv4_Factory.populateConfig(config);
        config.getSerializationConfig()
              .setPortableVersion(4);
        return config;
    }

    protected String nextString() {
        return fairy.textProducer()
                    .randomString(rand.nextInt(Byte.MAX_VALUE) + 1);
    }

    protected <M extends PMessage<M,F>, F extends PField>
    void assertMapIntegrity(HazelcastInstance instance1,
                            HazelcastInstance instance2,
                            PMessageDescriptor<M, F> descriptor) {
        String mapName = nextString();

        IMap<String, PMessageBuilder<M,F>> writeMap = instance1.getMap(mapName);
        IMap<String, PMessageBuilder<M,F>> readMap = instance2.getMap(mapName);

        M expected = generator.generate(descriptor);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        PMessageBuilder<M,F> builder = readMap.get(key);
        assertThat(builder, is(notNullValue()));

        M actual = builder.build();

        assertThat(actual, is(equalToMessage(expected)));

        M newExpected = generator.generate(descriptor);

        readMap.put(key, newExpected.mutate());

        PMessageBuilder<M,F> newBuilder = readMap.get(key);
        assertThat(newBuilder, is(notNullValue()));
        M newActual = newBuilder.build();

        assertThat(newActual, is(equalToMessage(newExpected)));
    }

    protected <
            M1 extends PMessage<M1,F1>,
            F1 extends PField,
            M2 extends PMessage<M2,F2>,
            F2 extends PField>
    void assertMapUpgradeIntegrity(HazelcastInstance instance1,
                                   HazelcastInstance instance2,
                                   PMessageDescriptor<M1, F1> sourceDescriptor,
                                   PMessageDescriptor<M2, F2> targetDescriptor) {

        String mapName = nextString();
        String key = nextString();

        IMap<String, PMessageBuilder<M1,F1>> sourceMap = instance1.getMap(mapName);
        IMap<String, PMessageBuilder<M2,F2>> targetMap = instance2.getMap(mapName);

        M1 expected = generator.generate(sourceDescriptor);
        sourceMap.put(key, expected.mutate());

        PMessageBuilder<M2,F2> actualBuilder = targetMap.get(key);
        assertThat(actualBuilder, is(notNullValue()));
        M2 actual = actualBuilder.build();
        assertFieldIntegrity(actual, expected);

        // And return
        M2 newExpected = generator.generate(targetDescriptor);
        targetMap.put(key, newExpected.mutate());

        PMessageBuilder<M1,F1> newActualBuilder = sourceMap.get(key);
        assertThat(newActualBuilder, is(notNullValue()));
        M1 newActual = newActualBuilder.build();
        assertFieldIntegrity(newActual, newExpected);

    }

    private void assertFieldIntegrity(PMessage<?,?> actual,
                                      PMessage<?,?> expected) {
        for (PField field : actual.descriptor().getFields()) {
            PField expectedField = expected.descriptor().findFieldById(field.getId());
            if (expectedField != null) {
                // Ignore differences in fields only declared in one of the versions.
                // They will be missing in one of the messages anyway.

                assertThat(expectedField.getType(), is(field.getType()));
                assertThat(expectedField.getName(), is(field.getName()));

                assertValueIntegrity(expectedField,
                                     actual.get(field.getId()),
                                     expected.get(expectedField.getId()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assertValueIntegrity(PField expectedField, Object actualValue, Object expectedValue) {

        if (actualValue == null || expectedValue == null) {

            assertThat(expectedField.toString(), actualValue, is(expectedValue));

        } else if (actualValue instanceof PMessage && expectedValue instanceof PMessage) {

            assertFieldIntegrity((PMessage<?, ?>) actualValue, (PMessage<?, ?>) expectedValue);

        } else if (actualValue instanceof PEnumValue && expectedValue instanceof PEnumValue) {

            assertThat(expectedField.toString(), ((PEnumValue) actualValue).getId(), is(((PEnumValue) expectedValue).getId()));

        } else if (actualValue instanceof List && expectedValue instanceof List) {

            List<Object> actualList = (List) actualValue;
            List<Object> expectedList = (List) expectedValue;

            assertThat(expectedField.toString(), actualList, hasSize(expectedList.size()));

            for (int i = 0; i < actualList.size(); ++i) {
                assertValueIntegrity(expectedField, actualList.get(i), expectedList.get(i));
            }

        } else if (actualValue instanceof Set && expectedValue instanceof Set) {

            TreeSet<Object> actualSet = new TreeSet<>((Set<Object>) actualValue);
            TreeSet<Object> expectedSet = new TreeSet<>((Set<Object>) expectedValue);

            assertThat(expectedField.toString(), actualSet, hasSize(expectedSet.size()));

            while (actualSet.size() > 0) {
                assertValueIntegrity(expectedField, actualSet.pollFirst(), expectedSet.pollFirst());
            }

        } else if (actualValue instanceof Map && expectedValue instanceof Map) {

            Map actualMap = (Map) actualValue;
            Map expectedMap = (Map) expectedValue;

            assertValueIntegrity(expectedField, actualMap.keySet(), expectedMap.keySet());

            for (Object key : actualMap.keySet()) {
                if (key instanceof PEnumValue) {

                    // Search for the enum with the same ID in the expected map keys.
                    Object epxectedKey = expectedMap.keySet()
                                                    .stream()
                                                    .filter(k -> ((PEnumValue) k).getId() == ((PEnumValue) key).getId())
                                                    .findFirst()
                                                    .orElse(null);

                    if (epxectedKey == null) {
                        throw new AssertionError("No expected map key matching " + key);
                    }

                    assertValueIntegrity(expectedField, actualMap.get(key), expectedMap.get(epxectedKey));

                } else if (key instanceof PMessage) {

                    // The messages should sort identically.
                    TreeSet<PMessage> actualKeys = new TreeSet<>(actualMap.keySet());
                    TreeSet<PMessage> expectedKeys = new TreeSet<>(expectedMap.keySet());

                    while (expectedKeys.size() > 0) {

                        PMessage actual = actualKeys.pollFirst();
                        PMessage expected = expectedKeys.pollFirst();

                        assertFieldIntegrity(actual, expected);

                        assertValueIntegrity(expectedField, actualMap.get(actual), expectedMap.get(expected));

                    }

                } else {

                    assertValueIntegrity(expectedField, actualMap.get(key), expectedMap.get(key));

                }
            }

        } else {

            assertThat(expectedField.toString(), actualValue, is(expectedValue));

        }
    }
}
