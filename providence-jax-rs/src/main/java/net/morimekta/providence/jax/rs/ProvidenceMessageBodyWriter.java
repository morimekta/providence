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
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.SerializerProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Base message body writer for providence objects.
 */
public abstract class ProvidenceMessageBodyWriter implements MessageBodyWriter<PMessage> {
    private final SerializerProvider provider;

    protected ProvidenceMessageBodyWriter(SerializerProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!PMessage.class.isAssignableFrom(type)) {
            return false;
        }
        String contentType = mediaType.getType() + "/" + mediaType.getSubtype();
        return provider.getSerializer(contentType) != null;
    }

    @Override
    public long getSize(PMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(PMessage entity,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        String contentType = mediaType.getType() + "/" + mediaType.getSubtype();

        try {
            provider.getSerializer(contentType)
                    .serialize(entityStream, entity);
        } catch (NullPointerException e) {
            throw new ProcessingException("Unknown media type: " + mediaType, e);
        } catch (SerializerException se) {
            throw new ProcessingException("Unable to serialize entity", se);
        }
    }
}
