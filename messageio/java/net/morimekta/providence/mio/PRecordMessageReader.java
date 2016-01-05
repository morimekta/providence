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

import net.morimekta.providence.Binary;
import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PStructDescriptor;
import net.morimekta.providence.serializer.PSerializer;
import net.morimekta.providence.serializer.PSerializeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Read messages from a file in the format:
 * <p/>
 * [file-magic-start]
 * ([message-magic-start][message...][message-magic-end][message sha-1 hash]) *
 */
public class PRecordMessageReader<T extends PMessage<T>>
        extends PMessageReader<T> {
    private final PSerializer mSerializer;
    private final PStructDescriptor<T, ?> mDescriptor;

    private File        mFile;
    private InputStream mInputStream;

    public PRecordMessageReader(File file, PSerializer serializer, PStructDescriptor<T, ?> descriptor) {
        mSerializer = serializer;
        mDescriptor = descriptor;
        mFile = file;
    }

    /**
     * Read the next available message from the files. If no file is available to read
     */
    public T read() throws IOException {
        try {
            synchronized (this) {
                if (mInputStream == null) {
                    if (mFile == null)
                        return null;
                    mInputStream = new FileInputStream(mFile);
                    if (!readMagic(PRecordMessageWriter.kMagicFileStart)) {
                        String file = mFile.getName();
                        close();
                        throw new IOException(String.format(
                                "%s is not a messageio formatted file.",
                                file));
                    }
                }
                // Verify message start magic.
                if (!readMagic(PRecordMessageWriter.kMagicMessageStart)) {
                    close();
                    return null;
                }
                DigestInputStream digestInputStream = new DigestInputStream(mInputStream,
                                                                            MessageDigest.getInstance("sha-1"));
                T message = mSerializer.deserialize(digestInputStream, mDescriptor);
                if (message == null) {
                    close();
                    throw new IOException("No message to read");
                }
                if (!readMagic(PRecordMessageWriter.kMagicMessageEnd)) {
                    close();
                    throw new IOException("Missing message end magic.");
                }
                byte[] digest = digestInputStream.getMessageDigest().digest();
                if (!readMagic(digest)) {
                    close();
                    throw new IOException(String.format(
                            "Message digest mismatch, message sha-1 \"%s\" not matching that on file.",
                            Binary.wrap(digest).toHexString()));
                }
                return message;
            }
        } catch (IOException e) {
            try { close(); } catch (IOException e2) {}
            throw new IOException("Unable to read messageio file.", e);
        } catch (PSerializeException tse) {
            try { close(); } catch (IOException e2) {}
            throw new IOException("Unable to deserialize message from file.", tse);
        } catch (NoSuchAlgorithmException e) {
            try { close(); } catch (IOException e2) {}
            throw new IOException("Unable to verify message consistency.", e);
        }
    }

    /**
     * Close the reading stream. Does not interfere with ongoing reads, but
     * will stop the read loop if ongoing.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        synchronized (this) {
            mFile = null;
            try {
                if (mInputStream != null) {
                    mInputStream.close();
                }
            } finally {
                mInputStream = null;
            }
        }
    }

    private boolean readMagic(byte[] magic) throws IOException {
        byte[] buffer = new byte[magic.length];
        int read = 0;
        while (read < buffer.length) {
            int tmp = mInputStream.read(buffer, read, buffer.length - read);
            if (tmp < 0)
                return false;
            read += tmp;
        }
        return Arrays.equals(buffer, magic);
    }

    /**
     * Checks if a given file has the 'messageio record file magic prefix'.
     *
     * @param file File to check.
     * @return
     * @throws FileNotFoundException
     */
    public static boolean hasFileMagic(File file) throws FileNotFoundException {
        if (file == null || !file.exists())
            return false;

        FileInputStream fis = new FileInputStream(file);
        try {
            byte[] buffer = new byte[PRecordMessageWriter.kMagicFileStart.length];
            int read = 0;
            while (read < buffer.length) {
                int tmp = fis.read(buffer, read, buffer.length - read);
                if (tmp < 0)
                    return false;
                read += tmp;
            }
            return Arrays.equals(buffer, PRecordMessageWriter.kMagicFileStart);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                // ignore.
            }
        }
    }
}
