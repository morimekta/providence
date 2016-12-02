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

package net.morimekta.providence.jax.rs;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Base message body reader for providence objects.
 */
public abstract class ProvidenceMessageBodyReader implements MessageBodyReader<PMessage> {
    private final SerializerProvider provider;

    protected ProvidenceMessageBodyReader(SerializerProvider provider) {
        this.provider = provider;
    }

    private PStructDescriptor getDescriptor(Class<?> type) {
        try {
            if (!PMessage.class.isAssignableFrom(type)) {
                return null;
            }
            Field descField = type.getDeclaredField("kDescriptor");
            Object desc = descField.get(null);
            if (desc instanceof PStructDescriptor) {
                return (PStructDescriptor) desc;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type != null && getDescriptor(type) != null;
    }

    @Override
    public PMessage readFrom(Class<PMessage> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        String contentType = mediaType.toString();

        try {
            PStructDescriptor descriptor = getDescriptor(type);
            return provider.getSerializer(contentType)
                           .deserialize(entityStream, descriptor);
        } catch (NullPointerException e) {
            throw new ProcessingException("Unknown media type: " + mediaType, e);
        } catch (SerializerException e) {
            throw new ProcessingException("Unable to deserialize entity", e);
        }
    }
}
