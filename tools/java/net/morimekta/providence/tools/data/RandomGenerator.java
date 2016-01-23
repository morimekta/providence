package net.morimekta.providence.tools.data;

import net.morimekta.util.Binary;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.Random;

/**
 */
public class RandomGenerator {
    private final Random           random;
    private final RealDistribution normal;

    public RandomGenerator(Random random) {
        this.random = random;
        this.normal = new CauchyDistribution(0, 1 << 20);
    }

    /**
     * Generate a random string containing UTC characters.
     * @param size Number of characters in the string.
     * @return The random string.
     */
    public String nextString(final int size) {
        char[] out = new char[size];
        for (int i = 0; i < size; ++i) {
            int c = random.nextInt(128);
            if (c == '\n' ||
                c == '\t' ||
                c == '\f' ||
                c == '\r' ||
                c == '\b' ||
                (c >= 32 && c < 127)) {
                out[i] = (char) c;
            } else {
                c = random.nextInt(2048);
                if (c >= 160) {
                    out[i] = (char) c;
                } else {
                    c = random.nextInt(1 << 16);
                    if(c < 32 || (127 <= c && c < 160) || (8192 <= c && c < 8448) ||
                            !Character.isValidCodePoint(c)) {
                        out[i] = '?';
                    } else {
                        out[i] = (char) c;
                    }
                }
            }
        }
        return String.valueOf(out);
    }

    /**
     * Generate a random name-like string.
     * @param size Number of characters in the string.
     * @return The random string.
     */
    public String nextName(final int size) {
        char[] out = new char[size];
        for (int i = 0; i < size; ++i) {
            if (i == 0) {
                out[i] = (char) ('A' + random.nextInt('Z' - 'A'));
            } else {
                int c = random.nextInt(128);
                if ((c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= '0' && c <= '9') ||
                    (c == '_' || c == '-' || c == '.')) {
                    out[i] = (char) c;
                } else {
                    out[i] = (char) ('a' + random.nextInt('z' - 'a'));
                }
            }
        }
        return String.valueOf(out);
    }

    public Binary nextBinary(final int size) {
        byte[] out = new byte[size];
        random.nextBytes(out);
        return Binary.wrap(out);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public byte nextByte() {
        byte[] out = new byte[1];
        random.nextBytes(out);
        return out[0];
    }

    public short nextShort() {
        return (short) random.nextInt();
    }

    public int nextInt() {
        long v = 0;
        for (int i = 0; i < 100; ++i) {
            v += random.nextInt();
        }
        return (int) (v / 100);
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public int nextInt(int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public long nextLong() {
        long out = random.nextLong();
        return out ^ (long) random.nextInt();
    }

    public long nextLong(long bound) {
        return nextLong() % bound;
    }

    public long nextLong(long lowerBound, long upperBound) {
        return nextLong(upperBound - lowerBound) + lowerBound;
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public double nextDistributedDouble() {
        return normal.inverseCumulativeProbability(random.nextDouble());
    }

    public boolean byChance(double probability) {
        return probability > random.nextDouble();
    }
}
