/*
 * Copyright 2016 Providence Authors
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
package net.morimekta.providence;

import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;
import net.morimekta.providence.serializer.SerializerException;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Interface for handling a call request from a synchronous client.
 */
@FunctionalInterface
public interface PServiceCallHandler {
    /**
     * Handle a service call.
     *
     * @param call The request call.
     * @param service The service to be handled.
     * @param <Request> Request type.
     * @param <Response> Response type.
     * @param <RequestField> Request type.
     * @param <ResponseField> Response type.
     * @return The response service call object, or null if none (e.g. oneway).
     * @throws IOException On read or write failure.
     * @throws SerializerException On serialization problems.
     */
    @Nullable
    <       Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(PServiceCall<Request, RequestField> call, PService service)
            throws IOException;
}
