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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by SteinEldar on 24.12.2015.
 */
public class BinaryTest {
    private static final byte[] a1 = new byte[]{'a', 'b', 'c'};
    private static final byte[] a2 = new byte[]{'a', 'b', 'c'};
    private static final byte[] b1 = new byte[]{'a', 'b', 'd'};
    private static final byte[] b2 = new byte[]{'a', 'b', 'd'};
    private static final byte[] c1 = new byte[]{'a', 'b', 'c', 'd'};
    private static final byte[] c2 = new byte[]{'a', 'b', 'c', 'd'};

    @Test
    public void testHashCode() {
        Assert.assertEquals(Binary.wrap(a1)
                                  .hashCode(),
                            Binary.wrap(a2)
                                  .hashCode());
        Assert.assertEquals(Binary.wrap(b1)
                                  .hashCode(),
                            Binary.wrap(b2)
                                  .hashCode());
        Assert.assertEquals(Binary.wrap(c1)
                                  .hashCode(),
                            Binary.wrap(c2)
                                  .hashCode());

        assertNotEquals(Binary.wrap(b1)
                              .hashCode(),
                        Binary.wrap(a2)
                              .hashCode());
        assertNotEquals(Binary.wrap(c1)
                              .hashCode(),
                        Binary.wrap(b2)
                              .hashCode());
        assertNotEquals(Binary.wrap(a1)
                              .hashCode(),
                        Binary.wrap(c2)
                              .hashCode());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(Binary.wrap(a1), Binary.wrap(a2));
        Assert.assertEquals(Binary.wrap(b1), Binary.wrap(b2));
        Assert.assertEquals(Binary.wrap(c1), Binary.wrap(c2));

        assertNotEquals(Binary.wrap(b1), Binary.wrap(a2));
        assertNotEquals(Binary.wrap(c1), Binary.wrap(b2));
        assertNotEquals(Binary.wrap(a1), Binary.wrap(c2));
    }

    @Test
    public void testCompareTo() {
        TreeSet<Binary> set = new TreeSet<>();
        set.add(Binary.wrap(a1));
        set.add(Binary.wrap(a2));
        set.add(Binary.wrap(b1));
        set.add(Binary.wrap(b2));
        set.add(Binary.wrap(c1));
        set.add(Binary.wrap(c2));

        assertEquals(3, set.size());
        ArrayList<Binary> list = new ArrayList<>(set);
        assertEquals(list.get(0), Binary.wrap(a1));
        assertEquals(list.get(1), Binary.wrap(c1));
        assertEquals(list.get(2), Binary.wrap(b1));
    }

    @Test
    public void testBase64() throws IOException {
        String a = Base64.encodeBytes(a1);

        assertEquals(a,
                     Binary.wrap(a1)
                           .toBase64());
        assertEquals(Binary.wrap(a2), Binary.fromBase64(a));
    }
}
