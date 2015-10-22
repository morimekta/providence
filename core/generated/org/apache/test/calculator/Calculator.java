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

package org.apache.test.calculator;

import org.apache.thrift.j2.TClient;
import org.apache.thrift.j2.TException;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.TMessageBuilder;
import org.apache.thrift.j2.TMessageBuilderFactory;
import org.apache.thrift.j2.TService;
import org.apache.thrift.j2.TType;
import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TServiceDescriptor;
import org.apache.thrift.j2.descriptor.TServiceMethod;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.descriptor.TStructDescriptorProvider;
import org.apache.thrift.j2.descriptor.TUnionDescriptor;
import org.apache.thrift.j2.descriptor.TUnionDescriptorProvider;
import org.apache.thrift.j2.descriptor.TValueProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public abstract class Calculator {
    public static final List<Operator> kComplexOperands = createKComplexOperands();

    protected Calculator() {
    }

    public static TServiceDescriptor DESCRIPTOR = createDescriptor();

    private static TServiceDescriptor createDescriptor() {
        List<TServiceMethod<?, ?, ?>> methods = new LinkedList<>();
        methods.add(new TServiceMethod<>(null, false, "calculate",
                                         Operand.provider(),
                                         Calculate_Params.provider(),
                                         Calculate_Exception.provider()));
        methods.add(new TServiceMethod<>(null, true, "iamalive",
                                         null, null, null));
        return new TServiceDescriptor(null, "calculator", "Calculator", methods);
    }

    public abstract Operand calculate(Operation op) throws IOException, CalculateException;

    public abstract void iamalive() throws IOException;

    public class Client
            extends Calculator {
        private final TClient mClient;

        public Client(TClient handler) {
            mClient = handler;
        }

        @Override
        public Operand calculate(Operation op) throws IOException, CalculateException {
            TServiceMethod<Operand, Calculate_Params, Calculate_Exception> method =
                    DESCRIPTOR.getMethodByName("calculate");
            Calculate_Params request = Calculate_Params.builder()
                                                       .setOp(op)
                                                       .build();
            try {
                return mClient.call(method, request);
            } catch (Calculate_Exception e) {
                if (e.hasCe()) {
                    throw e.getCe();
                }
                throw new IOException("Unknown exception field", e);
            } catch (TException e) {
                throw new IOException("Unknown serialized exception", e);
            }
        }

        @Override
        public void iamalive() throws IOException {
            TServiceMethod<Void,Calculate_Params,Calculate_Exception> method =
                    DESCRIPTOR.getMethodByName("calculate");
            try {
                mClient.call(method, null);
            } catch (TException e) {
                throw new IOException("Unknown serialized exception", e);
            }
        }
    }

    public class Service
            implements TService {
        private final Calculator mService;

        public Service(Calculator service) {
            mService = service;
        }

        @Override
        public TServiceDescriptor getServiceType() {
            return DESCRIPTOR;
        }

        @Override
        public <R, P extends TMessage<P>, E extends TMessage<E>>
        R call(TServiceMethod<R, P, E> method,
               TMessage<P> request) throws TException, IOException {
            switch (method.getName()) {
                case "calculate":
                    try {
                        if (!Calculate_Params.class.isAssignableFrom(request.getClass())) {
                            throw new IOException("Invalid request format");
                        }
                        // instance <- (upcast) <- (downcast) <- source.
                        Calculate_Params r = (Calculate_Params) (TMessage<?>) request;
                        return (R) mService.calculate(r.getOp());
                    } catch (CalculateException ce) {
                        throw Calculate_Exception.builder().setCe(ce).build();
                    }
                case "iamalive":
                    mService.iamalive();
                    return null;
                default:
                    throw new IOException("No such method " + method);
            }
        }
    }

    private static class Calculate_Params
            implements TMessage<Calculate_Params> {
        // Struct containing the request parameters.
        // 1: required Operation op;
        public final Operation mOp;

        private Calculate_Params(Calculate_Params.Builder builder) {
            mOp = builder.mOp;
        }

        public boolean hasOp() {
            return mOp != null;
        }

        public Operation getOp() {
            return mOp;
        }

        @Override
        public boolean has(int key) {
            switch (key) {
                case 1: return hasOp();
            }
            return false;
        }

        @Override
        public int num(int key) {
            switch (key) {
                case 1: return hasOp() ? 1 : 0;
            }
            return 0;
        }

        @Override
        public Object get(int key) {
            switch (key) {
                case 1: return getOp();
            }
            return null;
        }

        @Override
        public boolean isCompact() {
            return false;
        }

        @Override
        public Calculate_Params.Builder mutate() {
            return new Calculate_Params.Builder(this);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        private final static class Factory
                extends TMessageBuilderFactory<Calculate_Params> {
            @Override
            public Calculate_Params.Builder builder() {
                return new Calculate_Params.Builder();
            }
        }

        public static TStructDescriptorProvider<Calculate_Params> provider() {
            return new TStructDescriptorProvider<Calculate_Params>() {
                @Override
                public TStructDescriptor<Calculate_Params> descriptor() {
                    return DESCRIPTOR;
                }
            };
        }

        @Override
        public TStructDescriptor<Calculate_Params> getDescriptor() {
            return DESCRIPTOR;
        }

        public static final TStructDescriptor<Calculate_Params> DESCRIPTOR = createDescriptor();

        public enum Field implements TField {
            OPERATION(1, false, "operation", Operation.provider(), null),
            ;

            private final int mKey;
            private final boolean mRequired;
            private final String mName;
            private final TDescriptorProvider<?> mTypeProvider;
            private final TValueProvider<?> mDefaultValue;

            Field(int key, boolean required, String name, TDescriptorProvider<?> typeProvider, TValueProvider<?> defaultValue) {
                mKey = key;
                mRequired = required;
                mName = name;
                mTypeProvider = typeProvider;
                mDefaultValue = defaultValue;
            }

            @Override
            public String getComment() { return null; }

            @Override
            public int getKey() { return mKey; }

            @Override
            public boolean getRequired() { return mRequired; }

            @Override
            public TType getType() { return mTypeProvider.descriptor().getType(); }

            @Override
            public TDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

            @Override
            public String getName() { return mName; }

            @Override
            public boolean hasDefaultValue() { return mDefaultValue != null; }

            @Override
            public Object getDefaultValue() {
                return hasDefaultValue() ? mDefaultValue.get() : null;
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append(Operand.class.getSimpleName())
                       .append('{')
                       .append(mKey)
                       .append(": ");
                if (mRequired) {
                    builder.append("required ");
                }
                builder.append(getDescriptor().getQualifiedName(null))
                       .append(' ')
                       .append(mName)
                       .append('}');
                return builder.toString();
            }

            public static Field forKey(int key) {
                for (Field field : values()) {
                    if (field.mKey == key) return field;
                }
                return null;
            }

            public static Field forName(String name) {
                for (Field field : values()) {
                    if (field.mName.equals(name)) return field;
                }
                return null;
            }
        }

        private static TStructDescriptor<Calculate_Params> createDescriptor() {
            return new TStructDescriptor<>(null,
                                           "calculator",
                                           "Calculator_calculate_request",
                                           Calculate_Params.Field.values(),
                                           new Factory(),
                                           false);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder
                extends TMessageBuilder<Calculate_Params> {
            private Operation mOp;

            public Builder() {
            }

            public Builder(Calculate_Params baseStruct) {
                mOp = baseStruct.mOp;
            }

            public Builder setOp(Operation value) {
                mOp = value;
                return this;
            }

            @Override
            public Calculate_Params build() {
                return new Calculate_Params(this);
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public Builder set(int key, Object value) {
                switch (key) {
                    // For enums, the object is the int value of the enum.
                    case 1:
                        setOp((Operation) value);
                        break;
                }
                return this;
            }
        }
    }

    private static class Calculate_Exception
            extends TException
            implements TMessage<Calculate_Exception> {
        private final static long serialVersionUID = 1l;
        // Union containing the response or thrown exception.
        // 1: CalculateException ce;
        private final CalculateException mCe;

        private Calculate_Exception(Calculate_Exception.Builder builder) {
            super(builder.createMessage());
            mCe = builder.mCe;
        }

        public boolean hasCe() {
            return mCe != null;
        }

        public CalculateException getCe() {
            return mCe;
        }

        @Override
        public boolean has(int key) {
            switch (key) {
                case 1:
                    return hasCe();
            }
            return false;
        }

        @Override
        public int num(int key) {
            switch (key) {
                case 1:
                    return hasCe() ? 1 : 0;
            }
            return 0;
        }

        @Override
        public Object get(int key) {
            switch (key) {
                case 1:
                    return getCe();
            }
            return null;
        }

        @Override
        public boolean isCompact() {
            return false;
        }

        @Override
        public Calculate_Exception.Builder mutate() {
            return new Calculate_Exception.Builder(this);
        }

        @Override
        public boolean isValid() {
            return (mCe != null ? 1 : 0) == 1;
        }

        public enum Field implements TField {
            MESSAGE(1, false, "message", TPrimitive.STRING.provider(), null),
            ;

            private final int mKey;
            private final boolean mRequired;
            private final String mName;
            private final TDescriptorProvider<?> mTypeProvider;
            private final TValueProvider<?> mDefaultValue;

            Field(int key, boolean required, String name, TDescriptorProvider<?> typeProvider, TValueProvider<?> defaultValue) {
                mKey = key;
                mRequired = required;
                mName = name;
                mTypeProvider = typeProvider;
                mDefaultValue = defaultValue;
            }

            @Override
            public String getComment() { return null; }

            @Override
            public int getKey() { return mKey; }

            @Override
            public boolean getRequired() { return mRequired; }

            @Override
            public TType getType() { return mTypeProvider.descriptor().getType(); }

            @Override
            public TDescriptor<?> getDescriptor() { return mTypeProvider.descriptor(); }

            @Override
            public String getName() { return mName; }

            @Override
            public boolean hasDefaultValue() { return mDefaultValue != null; }

            @Override
            public Object getDefaultValue() {
                return hasDefaultValue() ? mDefaultValue.get() : null;
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder();
                builder.append(Operand.class.getSimpleName())
                       .append('{')
                       .append(mKey)
                       .append(": ");
                if (mRequired) {
                    builder.append("required ");
                }
                builder.append(getDescriptor().getQualifiedName(null))
                       .append(' ')
                       .append(mName)
                       .append('}');
                return builder.toString();
            }

            public static Field forKey(int key) {
                for (Field field : values()) {
                    if (field.mKey == key) return field;
                }
                return null;
            }

            public static Field forName(String name) {
                for (Field field : values()) {
                    if (field.mName.equals(name)) return field;
                }
                return null;
            }
        }

        private final static class _Factory
                extends TMessageBuilderFactory<Calculate_Exception> {
            @Override
            public Calculate_Exception.Builder builder() {
                return new Calculate_Exception.Builder();
            }
        }

        public static TUnionDescriptorProvider<Calculate_Exception> provider() {
            return new TUnionDescriptorProvider<Calculate_Exception>() {
                @Override
                public TUnionDescriptor<Calculate_Exception> descriptor() {
                    return DESCRIPTOR;
                }
            };
        }

        @Override
        public TStructDescriptor<Calculate_Exception> getDescriptor() {
            return DESCRIPTOR;
        }

        public static final TUnionDescriptor<Calculate_Exception> DESCRIPTOR = createDescriptor();

        private static TUnionDescriptor<Calculate_Exception> createDescriptor() {
            return new TUnionDescriptor<>(null,
                                          "calculator",
                                          "Calculator_calculate_response",
                                          Calculate_Exception.Field.values(),
                                          new _Factory());
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder
                extends TMessageBuilder<Calculate_Exception> {
            private CalculateException mCe;

            public Builder() {
            }

            public Builder(Calculate_Exception baseStruct) {
                mCe = baseStruct.mCe;
            }

            public Builder setCe(CalculateException value) {
                mCe = value;
                return this;
            }

            @Override
            public Calculate_Exception build() {
                return new Calculate_Exception(this);
            }

            @Override
            public boolean isValid() {
                return ((mCe != null ? 1 : 0) == 1);
            }

            @Override
            public Builder set(int key, Object value) {
                switch (key) {
                    case 1:
                        setCe((CalculateException) value);
                        break;
                }
                return this;
            }

            private String createMessage() {
                StringBuilder builder = new StringBuilder();
                builder.append('{');
                boolean first = true;
                if (mCe != null) {
                    if (first)
                        first = false;
                    else
                        builder.append(',');
                    builder.append("ce:");
                    builder.append(mCe.toString());
                }
                builder.append('}');
                return builder.toString();
            }
        }
    }

    private static List<Operator> createKComplexOperands() {
        LinkedList<Operator> constant = new LinkedList<>();
        constant.add(Operator.MULTIPLY);
        constant.add(Operator.DIVIDE);
        return Collections.unmodifiableList(constant);
    }
}
