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

import net.morimekta.providence.PMessageBuilder;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Stein Eldar Johnsen
 * @since 07.09.15
 */
public class CException extends Exception implements CMessage<CException> {
    private final CExceptionDescriptor descriptor;
    private final Map<Integer, Object> values;

    private CException(Builder builder) {
        values = builder.getValueMap();
        descriptor = builder.descriptor;
    }

    @Override
    public Map<Integer,Object> values() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
               !(o == null ||
                 !(o instanceof CException)) && CStruct.equals(this, (CException) o);
    }

    @Override
    public int hashCode() {
        return CStruct.hashCode(this);
    }

    @Override
    public String toString() {
        return descriptor().getQualifiedName() + asString();
    }

    @Override
    @Nonnull
    public PMessageBuilder<CException,CField> mutate() {
        return new Builder(descriptor).merge(this);
    }

    @Nonnull
    @Override
    public CExceptionDescriptor descriptor() {
        return descriptor;
    }

    public static class Builder extends CMessageBuilder<Builder, CException> {
        private final CExceptionDescriptor descriptor;

        public Builder(CExceptionDescriptor type) {
            descriptor = type;
        }

        @Nonnull
        @Override
        public CExceptionDescriptor descriptor() {
            return descriptor;
        }

        @Nonnull
        @Override
        public CException build() {
            return new CException(this);
        }
    }
}
