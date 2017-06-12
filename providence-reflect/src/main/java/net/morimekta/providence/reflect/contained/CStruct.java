/*
 * Copyright 2016 Providence Authors
 *
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
package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.serializer.PrettySerializer;
import net.morimekta.providence.serializer.json.JsonCompactible;
import net.morimekta.providence.serializer.json.JsonCompactibleDescriptor;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A contained message of variant struct.
 */
public class CStruct implements CMessage<CStruct>, JsonCompactible {
    private static final PrettySerializer PRETTY_SERIALIZER = new PrettySerializer().compact();

    Map<Integer,Object> values;
    CStructDescriptor   descriptor;

    private CStruct(Builder builder) {
        descriptor = builder.descriptor;
        values     = builder.getValueMap();
    }

    public Map<Integer,Object> values() {
        return values;
    }

    @Override
    public boolean jsonCompact() {
        PMessageDescriptor<CStruct, CField> descriptor = descriptor();
        if (!((JsonCompactibleDescriptor) descriptor).isJsonCompactible()) {
            return false;
        }
        boolean missing = false;
        for (CField field : descriptor.getFields()) {
            if (has(field.getId())) {
                if (missing) {
                    return false;
                }
            } else {
                missing = true;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
               !(o == null ||
                 !(o instanceof CStruct)) && equals(this, (CStruct) o);
    }

    @Override
    public int hashCode() {
        return hashCode(this);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName() + asString();
    }

    @Nonnull
    @Override
    public PMessageBuilder<CStruct,CField> mutate() {
        return new Builder(descriptor).merge(this);
    }

    @Nonnull
    @Override
    public CStructDescriptor descriptor() {
        return descriptor;
    }

    public static class Builder extends CMessageBuilder<Builder,CStruct> {
        private final CStructDescriptor descriptor;

        public Builder(CStructDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        @Nonnull
        @Override
        public CStructDescriptor descriptor() {
            return descriptor;
        }

        @Nonnull
        @Override
        public CStruct build() {
            return new CStruct(this);
        }
    }

    protected static <M extends PMessage<M, CField>> boolean equals(M a, M b) {
        PMessageDescriptor<?, ?> type = b.descriptor();
        if (!a.descriptor()
                 .getQualifiedName()
                 .equals(type.getQualifiedName()) ||
            !a.descriptor()
                 .getVariant()
                 .equals(type.getVariant())) {
            return false;
        }

        for (CField field : a.descriptor().getFields()) {
            int id = field.getId();
            if (a.has(id) != b.has(id)) {
                return false;
            }
            if (!Objects.equals(a.get(id), b.get(id))) {
                return false;
            }
        }
        return true;
    }

    protected static <M extends CMessage<M>> int hashCode(M self) {
        int hash = self.descriptor().hashCode();
        for (CField field : self.descriptor().getFields()) {
            hash *= 29251;
            hash ^= Objects.hash(field, self.get(field));
        }
        return hash;
    }

    /**
     * Prints a jsonCompact string representation of the message.
     *
     * @param message The message to stringify.
     * @return The resulting string.
     */
    protected static <Message extends PMessage<Message, CField>>
    String asString(Message message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PRETTY_SERIALIZER.serialize(baos, message);
        return new String(baos.toByteArray(), UTF_8);
    }

    /**
     * Compare two values to each other.
     *
     * @param o1 The first value.
     * @param o2 The second value.
     * @param <T> The object type.
     * @return The compare value (-1, 0 or 1).
     */
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> int compare(T o1, T o2) {
        if (o1 == null || o2 == null) {
            return Boolean.compare(o1 != null, o2 != null);
        } else if (o1 instanceof PMessage && o2 instanceof PMessage) {
            return compareMessages((PMessage) o1, (PMessage) o2);
        }
        return o1.compareTo(o2);
    }

    @SuppressWarnings("unchecked")
    static <T extends PMessage<T, F>, F extends PField> int compareMessages(T m1, T m2) {
        int c = m1.descriptor()
                  .getQualifiedName()
                  .compareTo(m2.descriptor()
                               .getQualifiedName());
        if (c != 0) {
            return c;
        }
        for (PField field : m1.descriptor()
                              .getFields()) {
            c = Boolean.compare(m1.has(field.getId()), m2.has(field.getId()));
            if (c != 0) {
                return c;
            }
            if (m1.has(field.getId())) {
                c = compare((Comparable) m1.get(field.getId()), (Comparable) m2.get(field.getId()));
                if (c != 0) {
                    return c;
                }
            }
        }
        return 0;
    }
}
