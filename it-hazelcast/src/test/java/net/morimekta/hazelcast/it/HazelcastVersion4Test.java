package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v4.AllFields;
import net.morimekta.test.hazelcast.v4.OptionalFields;
import net.morimekta.test.hazelcast.v4.OptionalListFields;
import net.morimekta.test.hazelcast.v4.OptionalMapFields;
import net.morimekta.test.hazelcast.v4.OptionalMapListFields;
import net.morimekta.test.hazelcast.v4.OptionalMapSetFields;
import net.morimekta.test.hazelcast.v4.OptionalSetFields;
import net.morimekta.test.hazelcast.v4.UnionFields;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.Collectors;

import static net.morimekta.providence.testing.ProvidenceMatchers.equalToMessage;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * TBD
 */
public class HazelcastVersion4Test extends GenericMethods {

    static HazelcastInstance instance1;
    static HazelcastInstance instance2;

    @BeforeClass
    public static void setupClass() {
        instance1 = Hazelcast.newHazelcastInstance(getV4Config());
        instance2 = Hazelcast.newHazelcastInstance(getV4Config());
    }

    @AfterClass
    public static void shutDownClass() {
        Hazelcast.shutdownAll();
    }

    @Test
    public void testVersion4OptionalFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalFields._Builder> readMap = instance2.getMap(mapName);

        OptionalFields expected = generator.nextOptionalFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertThat(expected, is(actual));

        OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(
                                                                                   actual.getAnotherStringValue() + "asdf")
                                                                           .setDoubleValue(
                                                                                   actual.getDoubleValue() - 0.12345)
                                                                           .setBooleanValue(!actual.isBooleanValue())
                                                                           .setIntegerValue(
                                                                                   actual.getIntegerValue() - 12345)
                                                                           .setLongValue(actual.getLongValue() - 123456)
                                                                           .setAnotherShortValue((short) (
                                                                                   actual.getAnotherShortValue() - 1234))
                                                                           .build();

        readMap.put(key, newExpected.mutate());

        OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion4OptionalFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalFields._Builder> readMap = instance2.getMap(mapName);

        OptionalFields expected = generator.nextOptionalFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalFields actual = readMap.get(key)
                                                                       .build();

        assertThat(expected, is(actual));

        OptionalFields newExpected = actual.mutate()
                                                                           .setAnotherStringValue(
                                                                                   actual.getAnotherStringValue() + "asdf")
                                                                           .setDoubleValue(
                                                                                   actual.getDoubleValue() - 0.12345)
                                                                           .setBooleanValue(!actual.isBooleanValue())
                                                                           .setIntegerValue(
                                                                                   actual.getIntegerValue() - 12345)
                                                                           .setLongValue(actual.getLongValue() - 123456)
                                                                           .setAnotherShortValue((short) (
                                                                                   actual.getAnotherShortValue() - 1234))
                                                                           .build();

        readMap.put(key, newExpected.mutate());

        OptionalFields newActual = writeMap.get(key)
                                                                           .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion4OptionalListFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        OptionalListFields expected = generator.nextOptionalListFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(actual));

        OptionalListFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(actual.getAnotherStringValues().stream().filter(t -> t.contains("ab"))
                                                    .collect(Collectors.toList()))
                      .setDoubleValue(actual.getDoubleValue().stream().filter(t -> t < 0.5)
                                            .collect(Collectors.toList()))
                      .setBooleanValues(actual.getBooleanValues().stream().map(t -> !t).limit(25)
                                              .collect(Collectors.toList()))
                      .setAnotherIntegerValue(actual.getAnotherIntegerValue().stream().limit(25).collect(Collectors.toList()))
                      .setLongValue(actual.getLongValue().stream().limit(23).collect(Collectors.toList()))
                      .setShortValues(actual.getShortValues().stream().limit(12).collect(Collectors.toList()))
                      .build();

        readMap.put(key, newExpected.mutate());

        OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion4OptionalListFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalListFields._Builder> readMap = instance2.getMap(mapName);

        OptionalListFields expected = generator.nextOptionalListFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalListFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected.toString(), is(actual.toString()));
        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected.hashCode(), is(actual.hashCode()));
        assertThat(expected, is(actual));

        OptionalListFields._Builder newBuilder = actual.mutate();
        if( actual.hasAnotherStringValues() ) {
            newBuilder.setAnotherStringValues(actual.getAnotherStringValues()
                                                     .stream()
                                                     .filter(t -> t.contains("ab"))
                                                     .collect(Collectors.toList()));
        }
        if( actual.hasDoubleValue() ) {
            newBuilder.setDoubleValue(actual.getDoubleValue()
                                             .stream()
                                             .filter(t -> t < 0.5)
                                             .collect(Collectors.toList()));
        }
        if( actual.hasBooleanValues() ) {
            newBuilder.setBooleanValues(actual.getBooleanValues()
                                               .stream()
                                               .map(t -> !t)
                                               .limit(actual.numBooleanValues() - rand.nextInt(actual.numBooleanValues()))
                                               .collect(Collectors.toList()));
        }
        if( actual.hasAnotherIntegerValue() ) {
            newBuilder.setAnotherIntegerValue(actual.getAnotherIntegerValue()
                                              .stream()
                                              .limit(actual.numAnotherIntegerValue() - rand.nextInt(actual.numAnotherIntegerValue()))
                                              .collect(Collectors.toList()));
        }
        if( actual.hasLongValue() ) {
            newBuilder.setLongValue(actual.getLongValue()
                                           .stream()
                                           .limit(actual.numLongValue() - rand.nextInt(actual.numLongValue()))
                                           .collect(Collectors.toList()));
        }
        if( actual.hasShortValues() ) {
            newBuilder.setShortValues(actual.getShortValues()
                                             .stream()
                                             .limit(actual.numShortValues() - rand.nextInt(actual.numShortValues()))
                                             .collect(Collectors.toList()));
        }

        OptionalListFields newExpected = newBuilder.build();

        readMap.put(key, newExpected.mutate());

        OptionalListFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion4OptionalSetFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        OptionalSetFields expected = generator.nextOptionalSetFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(actual));

        OptionalSetFields newExpected =
                actual.mutate()
                      .setAnotherStringValues(actual.getAnotherStringValues().stream().filter(t -> t.contains("ab"))
                                                    .collect(Collectors.toList()))
                      .setDoubleValue(actual.getDoubleValue().stream().filter(t -> t < 0.5)
                                            .collect(Collectors.toList()))
                      .setBooleanValues(actual.getBooleanValues().stream().map(t -> !t).limit(25)
                                              .collect(Collectors.toList()))
                      .setAnotherIntegerValue(actual.getAnotherIntegerValue().stream().limit(25).collect(Collectors.toList()))
                      .setLongValue(actual.getLongValue().stream().limit(23).collect(Collectors.toList()))
                      .setShortValues(actual.getShortValues().stream().limit(12).collect(Collectors.toList()))
                      .build();

        readMap.put(key, newExpected.mutate());

        OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVersion4OptionalSetFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalSetFields._Builder> readMap = instance2.getMap(mapName);

        OptionalSetFields expected = generator.nextOptionalSetFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalSetFields actual = readMap.get(key)
                                                                           .build();

        assertThat(expected, is(equalToMessage(actual)));
        assertThat(expected.hashCode(), is(actual.hashCode()));
        assertThat(expected, is(actual));

        OptionalSetFields._Builder newBuilder = actual.mutate();
        if( actual.hasAnotherStringValues() ) {
            newBuilder.setAnotherStringValues(actual.getAnotherStringValues()
                                                    .stream()
                                                    .filter(t -> t.contains("ab"))
                                                    .collect(Collectors.toList()));
        }
        if( actual.hasDoubleValue() ) {
            newBuilder.setDoubleValue(actual.getDoubleValue()
                                            .stream()
                                            .filter(t -> t < 0.5)
                                            .collect(Collectors.toList()));
        }
        if( actual.hasBooleanValues() ) {
            newBuilder.setBooleanValues(actual.getBooleanValues()
                                              .stream()
                                              .map(t -> !t)
                                              .limit(actual.numBooleanValues() - rand.nextInt(actual.numBooleanValues()))
                                              .collect(Collectors.toList()));
        }
        if( actual.hasAnotherIntegerValue() ) {
            newBuilder.setAnotherIntegerValue(actual.getAnotherIntegerValue()
                                                    .stream()
                                                    .limit(actual.numAnotherIntegerValue() - rand.nextInt(actual.numAnotherIntegerValue()))
                                                    .collect(Collectors.toList()));
        }
        if( actual.hasLongValue() ) {
            newBuilder.setLongValue(actual.getLongValue()
                                          .stream()
                                          .limit(actual.numLongValue() - rand.nextInt(actual.numLongValue()))
                                          .collect(Collectors.toList()));
        }
        if( actual.hasShortValues() ) {
            newBuilder.setShortValues(actual.getShortValues()
                                            .stream()
                                            .limit(actual.numShortValues() - rand.nextInt(actual.numShortValues()))
                                            .collect(Collectors.toList()));
        }

        OptionalSetFields newExpected = newBuilder.build();

        readMap.put(key, newExpected.mutate());

        OptionalSetFields newActual = writeMap.get(key)
                                                                               .build();

        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapFields expected = generator.nextOptionalMapFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapFields actual = readMap.get(key)
                                          .build();

        assertThat(expected, is(actual));

        OptionalMapFields newExpected = actual.mutate()
                                              .setAnotherIntegerValue(generator.item.nextIntegerMap())
                                              .setDoubleValue(generator.item.nextDoubleMap())
                                              .setLongValue(generator.item.nextLongMap())
                                              .setShortValue(generator.item.nextShortMap())
                                              .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapFields newActual = writeMap.get(key)
                                              .build();

        for (OptionalMapFields._Field field : OptionalMapFields._Field.values()) {
            assertThat(actual.has(field), is(true));
            assertThat(expected.has(field), is(true));
            assertThat(newActual.has(field), is(true));
            assertThat(newExpected.has(field), is(true));
        }
        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapFields expected = generator.nextOptionalMapFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapFields actual = readMap.get(key)
                                          .build();

        assertThat(expected, is(equalToMessage(actual)));

        OptionalMapFields newExpected = actual.mutate()
                                              .setAnotherIntegerValue(generator.item.nextIntegerMap())
                                              .setDoubleValue(generator.item.nextDoubleMap())
                                              .setLongValue(generator.item.nextLongMap())
                                              .setShortValue(generator.item.nextShortMap())
                                              .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapFields newActual = writeMap.get(key)
                                              .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapListFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapListFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapListFields expected = generator.nextOptionalMapListFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapListFields actual = readMap.get(key)
                                              .build();

        assertThat(expected, is(actual));

        OptionalMapListFields newExpected = actual.mutate()
                                                  .setAnotherIntegerValueList(generator.item.nextIntegerListMap())
                                                  .setDoubleValueList(generator.item.nextDoubleListMap())
                                                  .setLongValueList(generator.item.nextLongListMap())
                                                  .setShortValueList(generator.item.nextShortListMap())
                                                  .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapListFields newActual = writeMap.get(key)
                                                  .build();

        for (OptionalMapListFields._Field field : OptionalMapListFields._Field.values()) {
            assertThat(actual.has(field), is(true));
            assertThat(expected.has(field), is(true));
            assertThat(newActual.has(field), is(true));
            assertThat(newExpected.has(field), is(true));
        }
        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapListFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapListFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapListFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapListFields expected = generator.nextOptionalMapListFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapListFields actual = readMap.get(key)
                                              .build();

        assertThat(expected, is(equalToMessage(actual)));

        OptionalMapListFields newExpected = actual.mutate()
                                                  .setAnotherIntegerValueList(generator.item.nextIntegerListMap())
                                                  .setDoubleValueList(generator.item.nextDoubleListMap())
                                                  .setLongValueList(generator.item.nextLongListMap())
                                                  .setShortValueList(generator.item.nextShortListMap())
                                                  .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapListFields newActual = writeMap.get(key)
                                                  .build();

        assertThat(newExpected.toString(), is(newActual.toString()));
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapSetFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapSetFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapSetFields expected = generator.nextOptionalMapSetFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapSetFields actual = readMap.get(key)
                                             .build();

        assertThat(expected, is(actual));

        OptionalMapSetFields newExpected = actual.mutate()
                                                 .setAnotherIntegerValueSet(generator.item.nextIntegerSetMap())
                                                 .setDoubleValueSet(generator.item.nextDoubleSetMap())
                                                 .setLongValueSet(generator.item.nextLongSetMap())
                                                 .setShortValueSet(generator.item.nextShortSetMap())
                                                 .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapSetFields newActual = writeMap.get(key)
                                                 .build();

        for (OptionalMapSetFields._Field field : OptionalMapSetFields._Field.values()) {
            assertThat(actual.has(field), is(true));
            assertThat(expected.has(field), is(true));
            assertThat(newActual.has(field), is(true));
            assertThat(newExpected.has(field), is(true));
        }
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4OptionalMapSetFieldsRand() throws InterruptedException {
        String mapName = nextString();

        IMap<String, OptionalMapSetFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, OptionalMapSetFields._Builder> readMap = instance2.getMap(mapName);

        OptionalMapSetFields expected = generator.nextOptionalMapSetFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        OptionalMapSetFields actual = readMap.get(key)
                                             .build();

        assertThat(expected, is(equalToMessage(actual)));

        OptionalMapSetFields newExpected = actual.mutate()
                                                 .setAnotherIntegerValueSet(generator.item.nextIntegerSetMap())
                                                 .setDoubleValueSet(generator.item.nextDoubleSetMap())
                                                 .setLongValueSet(generator.item.nextLongSetMap())
                                                 .setShortValueSet(generator.item.nextShortSetMap())
                                                 .build();

        readMap.put(key, newExpected.mutate());

        OptionalMapSetFields newActual = writeMap.get(key)
                                                 .build();

        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4UnionFieldsAll() throws InterruptedException {
        String mapName = nextString();

        IMap<String, UnionFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, UnionFields._Builder> readMap = instance2.getMap(mapName);

        UnionFields expected = generator.nextUnionFieldsV4(true);

        String key = nextString();
        writeMap.put(key, expected.mutate());

        UnionFields actual = readMap.get(key)
                                    .build();

        assertThat(expected, is(actual));

        UnionFields newExpected = actual.mutate()
                                        .setAnotherIntegerValue(generator.item.nextInt())
                                        .setDoubleValue(generator.item.nextDouble())
                                        .setLongValue(generator.item.nextLong())
                                        .setShortValue(generator.item.nextShort())
                                        .setAllFields(AllFields.withByteValue(generator.item.nextByte()))
                                        .build();

        readMap.put(key, newExpected.mutate());

        UnionFields newActual = writeMap.get(key)
                                        .build();

        for (UnionFields._Field field : UnionFields._Field.values()) {
            assertThat(actual.has(field), is(true));
            assertThat(expected.has(field), is(true));
            assertThat(newActual.has(field), is(true));
            assertThat(newExpected.has(field), is(true));
        }
        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }

    @Test
    public void testVerison4UnionFieldsRand() throws InterruptedException {
        String mapName = nextString();
        IMap<String, UnionFields._Builder> writeMap = instance1.getMap(mapName);
        IMap<String, UnionFields._Builder> readMap = instance2.getMap(mapName);

        UnionFields expected = generator.nextUnionFieldsV4();

        String key = nextString();
        writeMap.put(key, expected.mutate());

        UnionFields actual = readMap.get(key)
                                    .build();

        assertThat(expected, is(equalToMessage(actual)));

        UnionFields newExpected = actual.mutate()
                                        .setAnotherIntegerValue(generator.item.nextInt())
                                        .setDoubleValue(generator.item.nextDouble())
                                        .setLongValue(generator.item.nextLong())
                                        .setShortValue(generator.item.nextShort())
                                        .setAllFields(AllFields.withByteValue(generator.item.nextByte()))
                                        .build();

        readMap.put(key, newExpected.mutate());

        UnionFields newActual = writeMap.get(key)
                                        .build();

        assertThat(newExpected, is(equalToMessage(newActual)));
        assertThat(newExpected.hashCode(), is(newActual.hashCode()));
        assertThat(newExpected, is(newActual));
    }


}
