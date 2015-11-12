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

import java.io.File;
import java.io.IOException;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.mio.utils.Sequence;
import org.apache.thrift.j2.serializer.TSerializer;

/**
 * An output stream that counts the number of bytes written.
 *
 * @author Stein Eldar Johnsen
 * @since 06.09.15
 */
public class TSequenceMessageWriter<M extends TMessage<M>> implements TMessageWriter<M> {
    private static final int DEFAULT_CUTOFF_SIZE = 1_000_000_000;  // <1 gigabyte.

    private final TSerializer mSerializer;
    private final int         mFileCutoffSize;

    private Sequence          mSequence;
    private File              mCurrent;
    private TMessageWriter<M> mWriter;
    private int               mCurrentSize;

    public TSequenceMessageWriter(Sequence sequence, TSerializer serializer) {
        this(sequence, serializer, DEFAULT_CUTOFF_SIZE);
    }

    public TSequenceMessageWriter(Sequence sequence, TSerializer serializer, int cutoffSize) {
        mSequence = sequence;
        mSerializer = serializer;
        mFileCutoffSize = cutoffSize;

        mCurrent = null;
        mCurrentSize = 0;
        mWriter = null;
    }

    @Override
    public int write(M message) throws IOException {
        synchronized (this) {
            // Close check.
            if (mSequence == null) {
                throw new IOException("Writer already closed");
            }

            try {
                if (mWriter == null || mCurrentSize > mFileCutoffSize) {
                    mCurrent = mSequence.next();
                    mWriter = new TRecordMessageWriter<>(mCurrent, mSerializer);
                    mCurrentSize = 0;
                }
                int written = mWriter.write(message);
                mCurrentSize += written;

                return written;
            } catch (IOException e) {
                // e.printStackTrace();

                // As the stream is not properly completed, close it so we can try
                // to start a new file. We cannot handle another entry to the
                // messageio file.
                try {
                    if (mWriter != null) {
                        mWriter.close();
                    }
                } catch (IOException e2) {
                    // e2.printStackTrace();
                } finally {
                    mWriter = null;
                }
                throw new IOException("Failed to doHandle output stream.", e);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            if (mWriter != null) {
                mWriter.flush();
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            mSequence = null;
            try {
                if (mWriter != null) {
                    mWriter.close();
                }
            } finally {
                mWriter = null;
            }
        }
    }
}
