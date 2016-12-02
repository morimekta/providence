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
package net.morimekta.providence.server;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PService;

import java.io.IOException;

/**
 * Wrapping processor in a callback style
 */
public class WrappedProcessor implements PProcessor {
    @FunctionalInterface
    public interface ProcessorWrapper {
        PServiceCall handleWrappedCall(PServiceCall call, PProcessor processor) throws IOException;
    }

    private final PProcessor       processor;
    private final ProcessorWrapper processorWrapper;

    public WrappedProcessor(PProcessor processor, ProcessorWrapper processorWrapper) {
        this.processor = processor;
        this.processorWrapper = processorWrapper;
    }

    @Override
    public PService getDescriptor() {
        return processor.getDescriptor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Request extends PMessage<Request, RequestField>,
            Response extends PMessage<Response, ResponseField>,
            RequestField extends PField,
            ResponseField extends PField>
    PServiceCall<Response, ResponseField> handleCall(
            PServiceCall<Request, RequestField> call,
            PService service) throws IOException {
        return processorWrapper.handleWrappedCall(call, processor);
    }
}
