/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.morimekta.util;

import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A slice of a byte array.
 */
public class Slice implements Comparable<Slice> {
    protected final byte[] fb;
    protected final int    off;
    protected final int    len;

    public Slice(byte[] fb, int off, int len) {
        this.fb = fb;
        this.off = off;
        this.len = len;
    }

    public final int length() {
        return len;
    }

    /**
     * Get the whole slice as a string.
     *
     * @return Slice decoded as UTF_8 string.
     */
    public final String asString() {
        return new String(fb, off, len, UTF_8);
    }

    public final Slice substring(int start, int end) {
        int l = end < 0 ? (len - start) + end : end - start;
        if (l < 0 || l > (len - start)) {
            throw new IllegalArgumentException();
        }
        return new Slice(fb, off + start, l);
    }

    public final char charAt(int i) {
        if (i < 0 || len <= i) {
            throw new IllegalArgumentException();
        }
        return (char) fb[off + i];
    }

    /**
     * Get the whole slice as a simple integer.
     *
     * @return Integer long value.
     */
    public final long parseInteger() {
        int pos = off, radix = 10;
        if (len > 2 && charAt(0) == '0' && charAt(1) == 'x') {
            pos += 2;
            radix = 16;
        } else if (len > 1 && charAt(0) == '0') {
            pos += 1;
            radix = 8;
        }
        long res = 0;
        boolean neg = false;
        if (fb[off] == '-') {
            neg = true;
            pos++;
        }
        for (; pos < off + len; ++pos) {
            res *= radix;
            res += valueOfHex(fb[pos]);
        }
        return neg ? -res : res;
    }

    /**
     * Get the whole slice as a real number.
     *
     * @return Real double value.
     */
    public final double parseDouble() {
        return Double.parseDouble(asString());
    }

    public boolean strEquals(byte[] a) {
        return strEquals(a, 0, a.length);
    }

    public boolean strEquals(byte[] a, int aOff, int aLen) {
        if (aLen != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (a[aOff + i] != fb[off + i]) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(byte... a) {
        for (int i = 0; i < len; ++i) {
            for (byte b : a) {
                if (b == fb[off + i]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(byte[] a) {
        pos:
        for (int i = off; i <= (off + len - a.length); ++i) {
            for (int j = 0; j < a.length; ++j) {
                if (a[j] != fb[off + i + j]) {
                    continue pos;
                }
                return true;
            }
        }
        return false;
    }

    public boolean contains(byte a) {
        for (int i = off; i < (off + len); ++i) {
            if (fb[i] == a) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("slice(off:%d,len:%d,total:%d)", off, len, fb.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(off, len);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Slice)) {
            return false;
        }
        Slice other = (Slice) o;
        return other.fb == fb &&
               other.off == off &&
               other.len == len;
    }

    @Override
    public int compareTo(Slice o) {
        if (o.off != off) {
            return Integer.compare(off, o.off);
        }
        return Integer.compare(o.len, len);
    }

    private static int valueOfHex(byte b) {
        if (b <= '9') {
            return (b - '0');
        }
        if (b >= 'a') {
            return (b - 'a') + 10;
        }
        return (b - 'A') + 10;
    }
}
