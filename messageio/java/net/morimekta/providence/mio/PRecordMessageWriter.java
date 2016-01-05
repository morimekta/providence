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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.serializer.PSerializeException;

/**
 * Write messages to a file in the format:
 * <p/>
 * [file-magic-start]
 * ([message-magic-start][message...][message-magic-end][message sha-1 hash]) *
 */
public class PRecordMessageWriter<M extends PMessage<M>>
        implements PMessageWriter<M> {
    protected static final byte[] kMagicFileStart    = new byte[] {
            (byte) 0x74,
            (byte) 0x21,
            (byte) 0xF7,
            (byte) 0x12
    };
    protected static final byte[] kMagicMessageStart = new byte[] {
            (byte) 0x9A,
            (byte) 0x33,
            (byte) 0xA1,
            (byte) 0x70
    };
    protected static final byte[] kMagicMessageEnd   = new byte[] {
            (byte) 0x02,
            (byte) 0x9A,
            (byte) 0x51,
            (byte) 0x5D
    };

    private final PSerializer mSerializer;

    private File             mFile;
    private FileOutputStream mOutputStream;

    public PRecordMessageWriter(File file, PSerializer serializer) {
        mSerializer = serializer;

        mFile = file;
        mOutputStream = null;
    }

    @Override
    public int write(M message) throws IOException {
        synchronized (this) {
            // Close check.
            if (mFile == null) {
                throw new IOException("Writer is closed.");
            }
            try {
                int written = 0;
                if (mOutputStream == null) {
                    mOutputStream = new FileOutputStream(mFile);
                    mOutputStream.write(kMagicFileStart);
                    written += kMagicFileStart.length;
                }
                mOutputStream.write(kMagicMessageStart);
                written += kMagicMessageStart.length;
                DigestOutputStream digestOutputStream = new DigestOutputStream(
                        mOutputStream, MessageDigest.getInstance("sha-1"));
                written += mSerializer.serialize(digestOutputStream, message);
                digestOutputStream.flush();

                mOutputStream.write(kMagicMessageEnd);
                written += kMagicMessageEnd.length;

                byte[] digest = digestOutputStream.getMessageDigest().digest();
                mOutputStream.write(digest);
                written += digest.length;

                mOutputStream.flush();

                return written;
            } catch (IOException | PSerializeException | NoSuchAlgorithmException e) {
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
                throw new IOException("Failed to write to output stream.", e);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            if (mOutputStream != null) {
                mOutputStream.flush();
            }
        }
    }

    @Override
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
