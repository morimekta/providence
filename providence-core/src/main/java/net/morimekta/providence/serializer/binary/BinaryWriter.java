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

import net.morimekta.providence.PMessage;
import net.morimekta.util.io.BigEndianBinaryWriter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for messages that can directly be written to binary.
 */
public interface BinaryWriter {
    /**
     * Write the current message to the binary writer.
     *
     * NOTE: This method is not intended to be used directly. Instead use
     * the {@link net.morimekta.providence.serializer.BinarySerializer#serialize(OutputStream, PMessage)}
     * call.
     *
     * @param writer The binary writer to write to.
     * @return The number of bytes written.
     * @throws IOException If it failed to write the message for any reason.
     */
    int writeBinary(BigEndianBinaryWriter writer) throws IOException;
}
