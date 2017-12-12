/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.serializer.binary;

import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.util.io.BigEndianBinaryReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for message builders that can read binary directly from
 * an big endian binary reader.
 */
public interface BinaryReader {
    /**
     * Read the binary content into the current builder.
     *
     * NOTE: This method is not intended to be used directly. Instead use
     * the {@link net.morimekta.providence.serializer.BinarySerializer#deserialize(InputStream, PMessageDescriptor)}
     * call.
     *
     * @param reader The reader to read from.
     * @param strict If content should be handled strictly. True means to fail
     *               on everything that Apache thrift failed read() on.
     * @throws IOException When unable to read message for any reason.
     */
    void readBinary(BigEndianBinaryReader reader, boolean strict) throws IOException;
}
