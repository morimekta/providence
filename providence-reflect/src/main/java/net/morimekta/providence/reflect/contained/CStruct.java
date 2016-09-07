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

package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;
import net.morimekta.providence.descriptor.PStructDescriptor;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stein Eldar Johnsen
 * @since 26.08.15
 */
public class CStruct extends CMessage<CStruct,CField> {
    CStructDescriptor descriptor;

    private CStruct(Builder builder) {
        super(builder.getValueMap());
        descriptor = builder.descriptor;
    }

    @Override
    public PMessageBuilder<CStruct,CField> mutate() {
        return new Builder(descriptor);
    }

    @Override
    public CStructDescriptor descriptor() {
        return descriptor;
    }

    public static class Builder extends PMessageBuilder<CStruct,CField> {
        private final CStructDescriptor    descriptor;
        private final Map<Integer, Object> values;

        public Builder(CStructDescriptor descriptor) {
            this.descriptor = descriptor;
            this.values = new TreeMap<>();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Builder merge(CStruct from) {
            for (PField field : descriptor.getFields()) {
                int key = field.getKey();
                if (from.has(key)) {
                    switch (field.getType()) {
                        case MESSAGE:
                            ((PMessageBuilder) mutator(key)).merge((PMessage) from.get(key));
                            break;
                        case SET:
                            if (values.containsKey(key)) {
                                ((PSet.Builder<Object>) values.get(key)).addAll((Collection<Object>) from.get(key));
                            } else {
                                set(key, from.get(key));
                            }
                            break;
                        case MAP:
                            if (values.containsKey(key)) {
                                ((PMap.Builder<Object, Object>) values.get(key)).putAll((Map<Object, Object>) from.get(key));
                            } else {
                                set(key, from.get(key));
                            }
                            break;
                        default:
                            set(key, from.get(key));
                            break;
                    }
                }
            }
            
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public PMessageBuilder mutator(int key) {
            CField field = descriptor.getField(key);
            if (field == null) {
                throw new IllegalArgumentException("No such field ID " + key);
            } else if (field.getType() != PType.MESSAGE) {
                throw new IllegalArgumentException("Not a message field ID " + key + ": " + field.getName());
            }

            Object current = values.get(key);
            if (current == null) {
                current = ((PStructDescriptor) field.getDescriptor()).builder();
                values.put(key, current);
            } else if (current instanceof PMessage) {
                current = ((PMessage) current).mutate();
                values.put(key, current);
            } else if (!(current instanceof PMessageBuilder)) {
                // This should in theory not be possible. This is just a safe-guard.
                throw new IllegalArgumentException("Invalid value in map on message type: " + current.getClass().getSimpleName());
            }

            return (PMessageBuilder) current;
        }

        @Override
        public PStructDescriptor<CStruct, CField> descriptor() {
            return descriptor;
        }

        @Override
        public CStruct build() {
            return new CStruct(this);
        }

        @Override
        public boolean isValid() {
            for (PField field : descriptor.getFields()) {
                if (field.getRequirement() == PRequirement.REQUIRED) {
                    if (!values.containsKey(field.getKey())) {
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        public void validate() {
            LinkedList<String> missing = new LinkedList<>();
            for (PField field : descriptor.getFields()) {
                if (field.getRequirement() == PRequirement.REQUIRED) {
                    if (!values.containsKey(field.getKey())) {
                        missing.add(field.getName());
                    }
                }
            }

            if (missing.size() > 0) {
                throw new IllegalStateException(
                        "Missing required fields " +
                        String.join(",", missing) +
                        " in message " + descriptor().getQualifiedName(null));
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public Builder set(int key, Object value) {
            PField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value == null) {
                values.remove(key);
            } else {
                switch (field.getType()) {
                    case LIST: {
                        PList.Builder builder = ((PList) field.getDescriptor()).builder();
                        builder.addAll((Collection<Object>) value);
                        values.put(key, builder);
                        break;
                    }
                    case SET: {
                        PSet.Builder builder = ((PSet) field.getDescriptor()).builder();
                        builder.addAll((Collection<Object>) value);
                        values.put(key, builder);
                        break;
                    }
                    case MAP: {
                        PMap.Builder builder = ((PMap) field.getDescriptor()).builder();
                        builder.putAll((Map<Object, Object>) value);
                        values.put(key, builder);
                        break;
                    }
                    default:
                        values.put(key, value);
                        break;
                }
            }

            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Builder addTo(int key, Object value) {
            PField field = descriptor.getField(key);
            if (field == null) {
                return this; // soft ignoring unsupported fields.
            }
            if (value != null) {
                if (field.getType() == PType.LIST) {
                    @SuppressWarnings("unchecked")
                    PList.Builder<Object> list = (PList.Builder<Object>) values.get(field.getKey());
                    if (list == null) {
                        list = ((PList) field.getDescriptor()).builder();
                        values.put(field.getKey(), list);
                    }
                    list.add(value);
                } else if (field.getType() == PType.SET) {
                    @SuppressWarnings("unchecked")
                    PSet.Builder<Object> set = (PSet.Builder<Object>) values.get(field.getKey());
                    if (set == null) {
                        set = ((PSet) field.getDescriptor()).builder();
                        values.put(field.getKey(), set);
                    }
                    set.add(value);
                } else {
                    throw new IllegalArgumentException("Key " + key + " is not a collection: " + field.getType());
                }
            }
            return this;
        }

        @Override
        public Builder clear(int key) {
            values.remove(key);
            return this;
        }

        @SuppressWarnings("unchecked")
        private Map<Integer, Object> getValueMap() {
            ImmutableMap.Builder<Integer, Object> out = ImmutableMap.builder();
            for (CField field : descriptor.getFields()) {
                int key = field.getKey();
                if (values.containsKey(key)) {
                    switch (field.getType()) {
                        case SET:
                            out.put(key, ((PSet.Builder<Object>) values.get(key)).build());
                            break;
                        case LIST:
                            out.put(key, ((PList.Builder<Object>) values.get(key)).build());
                            break;
                        case MAP:
                            out.put(key, ((PMap.Builder<Object, Object>) values.get(key)).build());
                            break;
                        case MESSAGE:
                            Object current = values.get(key);
                            if (current instanceof PMessageBuilder) {
                                out.put(key, ((PMessageBuilder) current).build());
                            } else {
                                out.put(key, current);
                            }
                            break;
                        default:
                            out.put(key, values.get(key));
                            break;
                    }
                }
            }
            return out.build();
        }
    }
}
