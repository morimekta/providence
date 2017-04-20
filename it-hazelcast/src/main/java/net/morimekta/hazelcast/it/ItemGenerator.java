package net.morimekta.hazelcast.it;

import net.morimekta.test.hazelcast.v1.CompactFields;
import net.morimekta.test.hazelcast.v1.Value;
import net.morimekta.util.Binary;

import com.google.common.primitives.Bytes;
import org.jfairy.Fairy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ItemGenerator {
    private final Random random;
    private final Fairy  fairy;

    public ItemGenerator() {
        random = new Random();
        fairy = Fairy.create();
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public List<Boolean> nextBooleans() {
        return Arrays.asList(Stream.generate(() -> nextBoolean())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Boolean) t)
                     .collect(Collectors.toList());
    }

    public byte nextByte() {
        return (byte) random.nextInt(Byte.MAX_VALUE);
    }

    public short nextShort() {
        return (short) random.nextInt(Short.MAX_VALUE);
    }

    public List<Short> nextShorts() {
        return Arrays.asList(Stream.generate(() -> nextShort())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Short) t)
                     .collect(Collectors.toList());
    }

    public int nextInt() {
        return random.nextInt();
    }

    public List<Integer> nextInts() {
        return Arrays.asList(Stream.generate(() -> nextInt())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Integer) t)
                     .collect(Collectors.toList());
    }

    public long nextLong() {
        return random.nextLong();
    }

    public List<Long> nextLongs() {
        return Arrays.asList(Stream.generate(() -> nextLong())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Long) t)
                     .collect(Collectors.toList());
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public List<Double> nextDoubles() {
        return Arrays.asList(Stream.generate(() -> nextDouble())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Double) t)
                     .collect(Collectors.toList());
    }

    public String nextString() {
        return fairy.textProducer()
                    .randomString(random.nextInt(Byte.MAX_VALUE));
    }

    public List<String> nextStrings() {
        return Arrays.asList(Stream.generate(() -> nextString())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (String) t)
                     .collect(Collectors.toList());
    }

    public Binary nextBinary() {
        return Binary.copy(nextBytes());
    }

    public List<Binary> nextBinaries() {
        return Arrays.asList(Stream.generate(() -> nextBinary())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Binary) t)
                     .collect(Collectors.toList());
    }

    public byte[] nextBytes() {
        byte[] result = new byte[random.nextInt(Byte.MAX_VALUE)];
        random.nextBytes(result);
        return result;
    }

    public Value nextV1Value() {
        Value[] values = Value.values();
        return values[random.nextInt(values.length)];
    }

    public List<Value> nextV1Values() {
        return Arrays.asList(Stream.generate(() -> nextV1Value())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (Value) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v2.Value nextV2Value() {
        net.morimekta.test.hazelcast.v2.Value[] values = net.morimekta.test.hazelcast.v2.Value.values();
        return values[random.nextInt(values.length)];
    }

    public List<net.morimekta.test.hazelcast.v2.Value> nextV2Values() {
        return Arrays.asList(Stream.generate(() -> nextV2Value())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v2.Value) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v3.Value nextV3Value() {
        net.morimekta.test.hazelcast.v3.Value[] values = net.morimekta.test.hazelcast.v3.Value.values();
        return values[random.nextInt(values.length)];
    }

    public List<net.morimekta.test.hazelcast.v3.Value> nextV3Values() {
        return Arrays.asList(Stream.generate(() -> nextV3Value())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v3.Value) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v4.Value nextV4Value() {
        net.morimekta.test.hazelcast.v4.Value[] values = net.morimekta.test.hazelcast.v4.Value.values();
        return values[random.nextInt(values.length)];
    }

    public List<net.morimekta.test.hazelcast.v4.Value> nextV4Values() {
        return Arrays.asList(Stream.generate(() -> nextV4Value())
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v4.Value) t)
                     .collect(Collectors.toList());
    }

    public CompactFields nextV1CompactField(int flags, int index1, int index2, int index3) {
        CompactFields._Builder builder = CompactFields.builder();
        if (0 < (index1 & flags)) {
            builder.setName(nextString());
        }
        if (0 < (index2 & flags)) {
            builder.setId(nextInt());
        }
        if (0 < (index3 & flags)) {
            builder.setLabel(nextString());
        }
        return builder.build();
    }

    public List<CompactFields> nextV1CompactFields(int flags, int index1, int index2, int index3) {
        return Arrays.asList(Stream.generate(() -> nextV1CompactField(flags, index1, index2, index3))
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (CompactFields) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v2.CompactFields nextV2CompactField(int flags,
                                                                            int index1,
                                                                            int index2,
                                                                            int index3) {
        net.morimekta.test.hazelcast.v2.CompactFields._Builder builder = net.morimekta.test.hazelcast.v2.CompactFields.builder();
        if (0 < (index1 & flags)) {
            builder.setName(nextString());
        }
        if (0 < (index2 & flags)) {
            builder.setId(nextInt());
        }
        if (0 < (index3 & flags)) {
            builder.setLabel(nextString());
        }
        return builder.build();
    }

    public List<net.morimekta.test.hazelcast.v2.CompactFields> nextV2CompactFields(int flags,
                                                                                   int index1,
                                                                                   int index2,
                                                                                   int index3) {
        return Arrays.asList(Stream.generate(() -> nextV2CompactField(flags, index1, index2, index3))
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v2.CompactFields) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v3.CompactFields nextV3CompactField(int flags,
                                                                            int index1,
                                                                            int index2,
                                                                            int index3) {
        net.morimekta.test.hazelcast.v3.CompactFields._Builder builder = net.morimekta.test.hazelcast.v3.CompactFields.builder();
        if (0 < (index1 & flags)) {
            builder.setName(nextString());
        }
        if (0 < (index2 & flags)) {
            builder.setId(nextInt());
        }
        if (0 < (index3 & flags)) {
            builder.setLabel(nextString());
        }
        return builder.build();
    }

    public List<net.morimekta.test.hazelcast.v3.CompactFields> nextV3CompactFields(int flags,
                                                                                   int index1,
                                                                                   int index2,
                                                                                   int index3) {
        return Arrays.asList(Stream.generate(() -> nextV3CompactField(flags, index1, index2, index3))
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v3.CompactFields) t)
                     .collect(Collectors.toList());
    }

    public net.morimekta.test.hazelcast.v4.CompactFields nextV4CompactField(int flags,
                                                                            int index1,
                                                                            int index2,
                                                                            int index3) {
        net.morimekta.test.hazelcast.v4.CompactFields._Builder builder = net.morimekta.test.hazelcast.v4.CompactFields.builder();
        if (0 < (index1 & flags)) {
            builder.setName(nextString());
        }
        if (0 < (index2 & flags)) {
            builder.setId(nextInt());
        }
        if (0 < (index3 & flags)) {
            builder.setLabel(nextString());
        }
        return builder.build();
    }

    public List<net.morimekta.test.hazelcast.v4.CompactFields> nextV4CompactFields(int flags,
                                                                                   int index1,
                                                                                   int index2,
                                                                                   int index3) {
        return Arrays.asList(Stream.generate(() -> nextV4CompactField(flags, index1, index2, index3))
                                   .limit(random.nextInt(Byte.MAX_VALUE) + 1)
                                   .toArray())
                     .stream()
                     .map(t -> (net.morimekta.test.hazelcast.v4.CompactFields) t)
                     .collect(Collectors.toList());
    }

    public Map<Boolean, Boolean> nextBooleanMap() {
        return nextMap(() -> nextBooleans(), () -> nextBooleans(), 20);
    }

    public Map<Byte, Byte> nextByteMap() {
        return nextMap(() -> Bytes.asList(nextBytes()), () -> Bytes.asList(nextBytes()), 20);
    }

    public Map<Short, Short> nextShortMap() {
        return nextMap(() -> nextShorts(), () -> nextShorts(), 20);
    }

    public Map<Integer, Integer> nextIntegerMap() {
        return nextMap(() -> nextInts(), () -> nextInts(), 20);
    }

    public Map<Long, Long> nextLongMap() {
        return nextMap(() -> nextLongs(), () -> nextLongs(), 20);
    }

    public Map<Double, Double> nextDoubleMap() {
        return nextMap(() -> nextDoubles(), () -> nextDoubles(), 20);
    }

    public Map<String, String> nextStringMap() {
        return nextMap(() -> nextStrings(), () -> nextStrings(), 20);
    }

    public Map<Binary, Binary> nextBinaryMap() {
        return nextMap(() -> nextBinaries(), () -> nextBinaries(), 10);
    }

    public Map<Value, Value> nextV1ValueMap() {
        return nextMap(() -> nextV1Values(), () -> nextV1Values(), 10);
    }

    public Map<net.morimekta.test.hazelcast.v2.Value, net.morimekta.test.hazelcast.v2.Value> nextV2ValueMap() {
        return nextMap(() -> nextV2Values(), () -> nextV2Values(), 10);
    }

    public Map<net.morimekta.test.hazelcast.v3.Value, net.morimekta.test.hazelcast.v3.Value> nextV3ValueMap() {
        return nextMap(() -> nextV3Values(), () -> nextV3Values(), 10);
    }

    public Map<net.morimekta.test.hazelcast.v4.Value, net.morimekta.test.hazelcast.v4.Value> nextV4ValueMap() {
        return nextMap(() -> nextV4Values(), () -> nextV4Values(), 10);
    }

    public Map<CompactFields, CompactFields> nextV1CompactMap(int flags,
                                                              int index1,
                                                              int index2,
                                                              int index3,
                                                              int index4,
                                                              int index5,
                                                              int index6) {
        return nextMap(() -> nextV1CompactFields(flags, index1, index2, index3),
                       () -> nextV1CompactFields(flags, index4, index5, index6),
                       10);
    }

    public Map<net.morimekta.test.hazelcast.v2.CompactFields, net.morimekta.test.hazelcast.v2.CompactFields> nextV2CompactMap(
            int flags,
            int index1,
            int index2,
            int index3,
            int index4,
            int index5,
            int index6) {
        return nextMap(() -> nextV2CompactFields(flags, index1, index2, index3),
                       () -> nextV2CompactFields(flags, index4, index5, index6),
                       10);
    }

    public Map<net.morimekta.test.hazelcast.v3.CompactFields, net.morimekta.test.hazelcast.v3.CompactFields> nextV3CompactMap(
            int flags,
            int index1,
            int index2,
            int index3,
            int index4,
            int index5,
            int index6) {
        return nextMap(() -> nextV3CompactFields(flags, index1, index2, index3),
                       () -> nextV3CompactFields(flags, index4, index5, index6),
                       10);
    }

    public Map<net.morimekta.test.hazelcast.v4.CompactFields, net.morimekta.test.hazelcast.v4.CompactFields> nextV4CompactMap(
            int flags,
            int index1,
            int index2,
            int index3,
            int index4,
            int index5,
            int index6) {
        return nextMap(() -> nextV4CompactFields(flags, index1, index2, index3),
                       () -> nextV4CompactFields(flags, index4, index5, index6),
                       10);
    }

    public Map<Integer, List<Boolean>> nextBooleanListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextBooleans())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Byte>> nextByteListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> Bytes.asList(nextBytes()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Short>> nextShortListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextShorts())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Integer>> nextIntegerListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextInts())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Long>> nextLongListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextLongs())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Double>> nextDoubleListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextDoubles())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<String>> nextStringListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> nextStrings())
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, List<Binary>> nextBinaryListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextBinaries())
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<Value>> nextV1ValueListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV1Values())
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v2.Value>> nextV2ValueListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV2Values())
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v3.Value>> nextV3ValueListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV3Values())
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v4.Value>> nextV4ValueListMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV4Values())
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<CompactFields>> nextV1CompactListMap(int flags, int index1, int index2, int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV1CompactFields(flags, index1, index2, index3))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v2.CompactFields>> nextV2CompactListMap(int flags,
                                                                                                  int index1,
                                                                                                  int index2,
                                                                                                  int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV2CompactFields(flags, index1, index2, index3))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v3.CompactFields>> nextV3CompactListMap(int flags,
                                                                                                  int index1,
                                                                                                  int index2,
                                                                                                  int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV3CompactFields(flags, index1, index2, index3))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, List<net.morimekta.test.hazelcast.v4.CompactFields>> nextV4CompactListMap(int flags,
                                                                                                  int index1,
                                                                                                  int index2,
                                                                                                  int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> nextV4CompactFields(flags, index1, index2, index3))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<Boolean>> nextBooleanSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Boolean>(nextBooleans()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Byte>> nextByteSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Byte>(Bytes.asList(nextBytes())))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Short>> nextShortSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Short>(nextShorts()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Integer>> nextIntegerSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Integer>(nextInts()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Long>> nextLongSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Long>(nextLongs()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Double>> nextDoubleSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<Double>(nextDoubles()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<String>> nextStringSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 20)
                                      .mapToObj(i -> new HashSet<String>(nextStrings()))
                                      .collect(Collectors.toList()),
                       20);
    }

    public Map<Integer, Set<Binary>> nextBinarySetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<Binary>(nextBinaries()))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<Value>> nextV1ValueSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<Value>(nextV1Values()))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v2.Value>> nextV2ValueSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v2.Value>(nextV2Values()))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v3.Value>> nextV3ValueSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v3.Value>(nextV3Values()))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v4.Value>> nextV4ValueSetMap() {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v4.Value>(nextV4Values()))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<CompactFields>> nextV1CompactSetMap(int flags, int index1, int index2, int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<CompactFields>(nextV1CompactFields(flags,
                                                                                                    index1,
                                                                                                    index2,
                                                                                                    index3)))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v2.CompactFields>> nextV2CompactSetMap(int flags,
                                                                                                int index1,
                                                                                                int index2,
                                                                                                int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v2.CompactFields>(
                                              nextV2CompactFields(flags, index1, index2, index3)))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v3.CompactFields>> nextV3CompactSetMap(int flags,
                                                                                                int index1,
                                                                                                int index2,
                                                                                                int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v3.CompactFields>(
                                              nextV3CompactFields(flags, index1, index2, index3)))
                                      .collect(Collectors.toList()),
                       10);
    }

    public Map<Integer, Set<net.morimekta.test.hazelcast.v4.CompactFields>> nextV4CompactSetMap(int flags,
                                                                                                int index1,
                                                                                                int index2,
                                                                                                int index3) {
        return nextMap(() -> nextInts(),
                       () -> IntStream.range(0, 10)
                                      .mapToObj(i -> new HashSet<net.morimekta.test.hazelcast.v4.CompactFields>(
                                              nextV4CompactFields(flags, index1, index2, index3)))
                                      .collect(Collectors.toList()),
                       10);
    }

    public <K, V> Map<K, V> nextMap(Supplier<List<K>> keyInput, Supplier<List<V>> valueInput, int maxSize) {
        Map<K, V> result = new HashMap<K, V>();
        List<K> keys = keyInput.get()
                               .stream()
                               .distinct()
                               .collect(Collectors.toList());
        List<V> values = valueInput.get();
        int size = keys.size() > values.size() ? values.size() : keys.size();
        size = ((maxSize < size && maxSize != 0) ? maxSize : size);
        for (int i = 0; i < size; i++) {
            result.put(keys.get(i), values.get(i));
        }
        return result;
    }

    public <V> V randFromArray(V[] array) {
        return array[random.nextInt(array.length)];
    }

}