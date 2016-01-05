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

import net.morimekta.providence.serializer.PBinarySerializer;
import net.morimekta.providence.PMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Stein Eldar Johnsen
 * @since 19.09.15
 */
@Produces("application/vnd.thrift")
public class TCompactBinaryMessageBodyWriter<T extends PMessage<T>> extends TMessageBodyWriter<T> {
    public TCompactBinaryMessageBodyWriter() {
        this(new PBinarySerializer(true));
    }

    public TCompactBinaryMessageBodyWriter(PBinarySerializer serializer) {
        super(serializer);
    }

    @Override
    public void writeTo(T t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        super.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}
