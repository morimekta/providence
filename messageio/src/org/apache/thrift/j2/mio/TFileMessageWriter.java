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

package org.apache.thrift.j2.mio;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.serializer.TSerializeException;
import org.apache.thrift.j2.serializer.TSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An output stream that counts the number of bytes written.
 *
 * @author Stein Eldar Johnsen
 * @since 06.09.15
 */
public class TFileMessageWriter<M extends TMessage<M>> implements TMessageWriter<M> {
    private final TSerializer mSerializer;

    private File mFile;
    private FileOutputStream mOutputStream;

    public TFileMessageWriter(File file, TSerializer serializer) {
        mSerializer = serializer;

        mFile = file;
        mOutputStream = null;
    }

    @Override
    public void write(M message) throws IOException {
        synchronized (this) {
            // Close check.
            if (mFile == null) return;
            try {
                if (mOutputStream == null) {
                    mOutputStream = new FileOutputStream(mFile);
                }
                mSerializer.serialize(mOutputStream, message);
            } catch (IOException | TSerializeException e) {
                // e.printStackTrace();

                // As the stream is not properly completed, close it so we can try
                // to start a new file. We cannot doHandle another entry to the
                // messageio file.
                try {
                    if (mOutputStream != null) {
                        mOutputStream.close();
                    }
                } catch (IOException e2) {
                    // e2.printStackTrace();
                } finally {
                    mOutputStream = null;
                    mFile = null;
                }
                throw new IOException("Failed to doHandle output stream.", e);
            }
        }
    }

    /**
     * Close the doHandle stream. It cannot be written to again after this call.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        synchronized (this) {
            mFile = null;
            try {
                if (mOutputStream != null) {
                    mOutputStream.close();
                }
            } finally {
                mOutputStream = null;
            }
        }
    }
}
