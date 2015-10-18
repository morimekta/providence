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

package org.apache.thrift.j2.client;

import org.apache.thrift.j2.TClient;
import org.apache.thrift.j2.TException;
import org.apache.thrift.j2.TMessage;
import org.apache.thrift.j2.descriptor.TServiceMethod;

import java.io.IOException;

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
public class TSimpleHttpClient
        implements TClient {
    public TSimpleHttpClient() {
    }

    @Override
    public <R, P extends TMessage<P>, E extends TMessage<E>>
    R call(TServiceMethod<R, P, E> method,
           TMessage<P> request) throws TException, IOException {
        return null;
    }
}
