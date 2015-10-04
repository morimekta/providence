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

package org.apache.thrift2.jax.rs;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.descriptor.TStructDescriptorProvider;
import org.apache.thrift2.serializer.TSerializeException;
import org.apache.thrift2.serializer.TSerializer;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 19.09.15
 */
public abstract class TMessageBodyReader<T extends TMessage<T>> implements MessageBodyReader<T> {
    private final TSerializer mSerializer;

    public TMessageBodyReader(TSerializer serializer) {
        mSerializer = serializer;
    }

    @SuppressWarnings("unchecked")
    private TStructDescriptor<T> getDescriptor(Class<?> type) {
        try {
            if (!TMessage.class.isAssignableFrom(type))
                return null;
            Method method = type.getMethod("provider");
            TStructDescriptorProvider<T> provider = (TStructDescriptorProvider<T>) method.invoke(null);
            return provider.descriptor();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (type != null) {
            TStructDescriptor<?> descriptor = getDescriptor(type);
            return descriptor != null;
        }
        return false;
    }

    @Override
    public T readFrom(Class<T> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        // We need to get the "DESCRIPTOR" static field form the type class.
        try {
            TStructDescriptor<T> descriptor = getDescriptor(type);
            return mSerializer.deserialize(entityStream, descriptor);
        } catch (TSerializeException e) {
            throw new ProcessingException("", e);
        }
    }
}
