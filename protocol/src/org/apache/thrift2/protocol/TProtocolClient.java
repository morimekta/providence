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

package org.apache.thrift2.protocol;

import java.io.IOException;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift2.TClient;
import org.apache.thrift2.TException;
import org.apache.thrift2.TMessage;
import org.apache.thrift2.descriptor.TField;
import org.apache.thrift2.descriptor.TServiceMethod;
import org.apache.thrift2.serializer.TSerializeException;

/**
 * A simple HTTP client class that assumes a base path for all requests, amends
 * the method name to path, posts content (possibly Base64 encoded is non-ASCII
 * compatible format), and returns content received in response. 2xx is counted
 * as success. 4xx and 5xx as potential exceptions.
 * <p/>
 * E.g.: BASE = 'http://example.com/api'
 * <p/>
 * The call 'Value calculate(Operation o) throws CalculateException' will call:
 * <pre>
 *     POST http://example.com/api/calculate
 *
 *     {Operation serialized}
 * </pre>
 * Successful response will be
 * <pre>
 *     StatusCode: 200
 *
 *     {Value serialized}
 * </pre>
 * And failure response could be:
 * <pre>
 *     StatusCode: 501
 *
 *     {CalculateException serialized}
 * </pre>
 * HTTP 1.1 sessions etc will be handled by the provided HttpTransport
 * instance.
 *
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 14.09.15
 */
public class TProtocolClient
        implements TClient {
    private final TProtocolFactory  mProtocolFactory;
    private final TTransportFactory mTransportFactory;

    private int mNextSequenceId;

    public TProtocolClient(String url) {
        this(new TBinaryProtocol.Factory(), new THttpClient.Factory(url));
    }

    public TProtocolClient(TProtocolFactory protocolFactory, TTransportFactory transportFactory) {
        mProtocolFactory = protocolFactory;
        mTransportFactory = transportFactory;

        mNextSequenceId = 1;
    }

    private synchronized int newSequenceId() {
        return mNextSequenceId++;
    }

    @Override
    public <R, P extends TMessage<P>, E extends TMessage<E>>
    R call(TServiceMethod<R, P, E> method,
           TMessage<P> request) throws TException, IOException {
        TProtocol protocol = mProtocolFactory.getProtocol(mTransportFactory.getTransport(null));
        TProtocolSerializer serializer = new TProtocolSerializer(null);

        int seq = newSequenceId();

        org.apache.thrift.protocol.TMessage msg = new org.apache.thrift.protocol.TMessage(
                method.getName(),
                method.isOneway() ? TMessageType.ONEWAY : TMessageType.CALL,
                seq);
        R result = null;
        try {
            protocol.writeMessageBegin(msg);
            serializer.write(request, protocol);
            protocol.writeMessageEnd();
            protocol.getTransport().flush();

            if (method.isOneway()) {
                return null;
            }

            org.apache.thrift.protocol.TMessage reply = protocol.readMessageBegin();
            if (reply.type == TMessageType.REPLY) {
                result = serializer.read(protocol, method.getReturnType());
                protocol.readMessageEnd();
            } else if (reply.type == TMessageType.EXCEPTION) {
                TMessage<?> ex = serializer.read(protocol, method.getExceptionDescriptor());
                protocol.readMessageEnd();
                for (TField<?> field : ex.descriptor().getFields()) {
                    if (ex.has(field.getKey())) {
                        throw (TException) ex.get(field.getKey());
                    }
                }
            }
        } catch (TSerializeException se) {
            throw new IOException("Protocol serialization failed", se);
        } catch (org.apache.thrift.TException te) {
            throw new IOException("Protocol transport failed", te);
        }

        return result;
    }
}
