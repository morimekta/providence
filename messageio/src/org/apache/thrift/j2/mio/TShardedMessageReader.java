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
import java.util.LinkedList;
import java.util.Queue;

import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TStructDescriptor;
import org.apache.thrift.j2.mio.utils.Sequence;
import org.apache.thrift.j2.mio.utils.ShardUtil;
import org.apache.thrift.j2.serializer.TSerializer;

/**
 * Read messages (in global order) from a set of files in the format:
 *
 * {name}-{shard}-{seq}
 *
 * @author Stein Eldar Johnsen
 * @since 06.09.15
 */
public class TShardedMessageReader<T extends TMessage<T>> extends TMessageReader<T> {
    private final TSerializer          mSerializer;
    private final TStructDescriptor<T> mDescriptor;
    private final Queue<Sequence>      mQueue;

    private Sequence          mCurrentSequence;
    private File              mCurrent;
    private TMessageReader<T> mReader;

    public TShardedMessageReader(String pattern, TSerializer serializer, TStructDescriptor<T> descriptor) {
        mSerializer = serializer;
        mDescriptor = descriptor;
        mQueue = new LinkedList<>();
        for (String prefix : ShardUtil.prefixes(pattern)) {
            mQueue.add(new Sequence(prefix));
        }
    }

    /**
     * Read the next available message from the files. If no file is available to read
     */
    public T read() throws IOException {
        synchronized (this) {
            while (true) {
                if (mReader == null) {
                    while (mCurrentSequence != null && !mCurrentSequence.hasNext()) {
                        mCurrentSequence = mQueue.poll();
                    }
                    if (mCurrentSequence == null || !mCurrentSequence.hasNext()) {
                        return null;
                    }
                    mCurrent = mCurrentSequence.next();
                    mReader = new TRecordMessageReader<>(mCurrent, mSerializer, mDescriptor);
                }
                T message = mReader.read();
                if (message != null) {
                    return message;
                } else {
                    mReader.close();
                    mReader = null;
                }
            }
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
            try {
                mQueue.clear();
                if (mReader != null) {
                    mReader.close();
                }
            } finally {
                mCurrentSequence = null;
                mCurrent = null;
                mReader = null;
            }
        }
    }
}
