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
import org.apache.thrift2.serializer.TCompactBinarySerializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 19.09.15
 */
@Consumes("application/vnd.thrift")
public class TCompactBinaryMessageBodyReader<T extends TMessage<T>> extends TMessageBodyReader<T> {
    public TCompactBinaryMessageBodyReader() {
        this(new TCompactBinarySerializer(false));
    }

    public TCompactBinaryMessageBodyReader(TCompactBinarySerializer serializer) {
        super(serializer);
    }

    @Override
    public T readFrom(Class<T> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders,
                      InputStream entityStream) throws IOException, WebApplicationException {
        return super.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}
