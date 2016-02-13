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

package net.morimekta.providence.mio;

import net.morimekta.providence.PMessage;

import java.io.IOException;

/**
 * Message reader interface.
 */
public abstract class PMessageReader<T extends PMessage<T>> {
    /**
     * Read the next available message from the files. If no file is available to read
     */
    public abstract T read() throws IOException;

    /**
     * Close the reading stream. Does not interfere with ongoing reads, but
     * will stop the read loop if ongoing.
     */
    public abstract void close() throws IOException;

    /**
     * Read messages from the shard sequences and call the handler with them.
     *
     * @param handler The message handler.
     * @return The number of messages handled.
     */
    public final int each(PMessageWriter<T> handler) throws IOException {
        int handledMessages = 0;
        T message;
        while ((message = read()) != null) {
            handler.write(message);
            ++handledMessages;
        }
        return handledMessages;
    }
}
