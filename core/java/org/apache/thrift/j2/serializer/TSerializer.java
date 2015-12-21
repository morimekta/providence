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

package org.apache.thrift.j2.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TDescriptor;

/**
 * Thrift serializers are stateless injectable implementation classes that
 * transforms messages to binary stream (serializes), or binary stream to
 * messages (deserializes). Since the serializer is state-less it should also be
 * inherently thread safe (including not needing any synchronized methods.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public abstract class TSerializer {
    public abstract int serialize(OutputStream output, TMessage<?> message)
            throws IOException, TSerializeException;

    public abstract <T> int serialize(OutputStream output, TDescriptor<T> descriptor, T value)
            throws IOException, TSerializeException;

    public abstract <T> T deserialize(InputStream input,
                                      TDescriptor<T> descriptor)
            throws IOException, TSerializeException;

    @SuppressWarnings("unchecked")
    protected <T> T cast(Object o) {
        return (T) o;
    }
}
