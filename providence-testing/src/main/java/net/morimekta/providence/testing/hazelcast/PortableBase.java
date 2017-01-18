package net.morimekta.providence.testing.hazelcast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TBD
 */
public class PortableBase {

    protected final Map<String, Integer> integerMap;
    protected final Map<String, Boolean> booleanMap;
    protected final Map<String, Short> shortMap;
    protected final Map<String, Byte> byteMap;
    protected final Map<String, Double> doubleMap;
    protected final Map<String, Long> longMap;
    protected final Map<String, String> stringMap;
    protected final Map<String, Integer> charMap;
    protected final Map<String, Float> floatMap;
    protected final Map<String, com.hazelcast.nio.serialization.Portable> portableMap;
    protected final Map<String, int[]> integerArrayMap;
    protected final Map<String, boolean[]> booleanArrayMap;
    protected final Map<String, short[]> shortArrayMap;
    protected final Map<String, byte[]> byteArrayMap;
    protected final Map<String, double[]> doubleArrayMap;
    protected final Map<String, long[]> longArrayMap;
    protected final Map<String, String[]> stringArrayMap;
    protected final Map<String, int[]> charArrayMap;
    protected final Map<String, float[]> floatArrayMap;
    protected final Map<String, com.hazelcast.nio.serialization.Portable[]> portableArrayMap;
    protected final Set<String> fieldNames;

    public PortableBase() {
        this.integerMap = new HashMap<>();
        this.booleanMap = new HashMap<>();
        this.shortMap = new HashMap<>();
        this.byteMap = new HashMap<>();
        this.doubleMap = new HashMap<>();
        this.longMap = new HashMap<>();
        this.stringMap = new HashMap<>();
        this.charMap = new HashMap<>();
        this.floatMap = new HashMap<>();
        this.portableMap = new HashMap<>();
        this.integerArrayMap = new HashMap<>();
        this.booleanArrayMap = new HashMap<>();
        this.shortArrayMap = new HashMap<>();
        this.byteArrayMap = new HashMap<>();
        this.doubleArrayMap = new HashMap<>();
        this.longArrayMap = new HashMap<>();
        this.stringArrayMap = new HashMap<>();
        this.charArrayMap = new HashMap<>();
        this.floatArrayMap = new HashMap<>();
        this.portableArrayMap = new HashMap<>();
        this.fieldNames = new HashSet<>();
    }

    public PortableBase(PortableBase obj2copy) {
        this.integerMap = obj2copy.integerMap;
        this.booleanMap = obj2copy.booleanMap;
        this.shortMap = obj2copy.shortMap;
        this.byteMap = obj2copy.byteMap;
        this.doubleMap = obj2copy.doubleMap;
        this.longMap = obj2copy.longMap;
        this.stringMap = obj2copy.stringMap;
        this.charMap = obj2copy.charMap;
        this.floatMap = obj2copy.floatMap;
        this.portableMap = obj2copy.portableMap;
        this.integerArrayMap = obj2copy.integerArrayMap;
        this.booleanArrayMap = obj2copy.booleanArrayMap;
        this.shortArrayMap = obj2copy.shortArrayMap;
        this.byteArrayMap = obj2copy.byteArrayMap;
        this.doubleArrayMap = obj2copy.doubleArrayMap;
        this.longArrayMap = obj2copy.longArrayMap;
        this.stringArrayMap = obj2copy.stringArrayMap;
        this.charArrayMap = obj2copy.charArrayMap;
        this.floatArrayMap = obj2copy.floatArrayMap;
        this.portableArrayMap = obj2copy.portableArrayMap;
        this.fieldNames = obj2copy.fieldNames;
    }

}
