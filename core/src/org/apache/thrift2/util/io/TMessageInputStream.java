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

package org.apache.thrift2.util.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.thrift2.TMessage;
import org.apache.thrift2.descriptor.TStructDescriptor;
import org.apache.thrift2.serializer.TSerializeException;
import org.apache.thrift2.serializer.TSerializer;

/**
 * An output stream that counts the number of bytes written.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 06.09.15
 */
public class TMessageInputStream
        extends InputStream {
    private final InputStream mIn;
    private final TSerializer mSerializer;

    public TMessageInputStream(InputStream in, TSerializer serializer) {
        mIn = in;
        mSerializer = serializer;
    }

    public <T extends TMessage<T>> T read(TStructDescriptor<T> type) throws IOException {
        try {
            return mSerializer.deserialize(mIn, type);
        } catch (TSerializeException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read() throws IOException {
        return mIn.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return mIn.read(bytes);
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        return mIn.read(bytes, off, len);
    }

    @Override
    public void close() throws IOException {
        mIn.close();
    }
}
