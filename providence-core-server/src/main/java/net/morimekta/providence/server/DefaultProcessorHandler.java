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

import net.morimekta.providence.PApplicationException;
import net.morimekta.providence.PApplicationExceptionType;
import net.morimekta.providence.PProcessor;
import net.morimekta.providence.PServiceCall;
import net.morimekta.providence.PServiceCallHandler;
import net.morimekta.providence.PServiceCallType;
import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;
import net.morimekta.providence.serializer.SerializerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Service processor wraps the service' Processor implementation of the
 * {@link PServiceCallHandler} so that it can read the service call message
 * parsed into a {@link PServiceCall}, and write the response with proper
 * exception handling.
 */
public class DefaultProcessorHandler implements ProcessorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessorHandler.class);

    private final PProcessor processor;

    /**
     * Wrap a service with a service call handler to work as the server side
     * of a providence service.
     *
     * @param processor The service call handler.
     */
    public DefaultProcessorHandler(@Nonnull PProcessor processor) {
        this.processor = processor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void process(MessageReader reader, MessageWriter writer) throws IOException {
        PServiceCall call, reply;
        try {
            call = reader.read(processor.getDescriptor());
        } catch (SerializerException e) {
            if (e.getMethodName() != null) {
                LOGGER.error("Error when reading service call " + processor.getDescriptor().getName() + "." + e.getMethodName() + "()", e);
            } else {
                LOGGER.error("Error when reading service call " + processor.getDescriptor().getName(), e);
            }
            try {
                PApplicationException oe = new PApplicationException(e.getMessage(), e.getExceptionType());
                reply = new PServiceCall<>(e.getMethodName(), PServiceCallType.EXCEPTION, e.getSequenceNo(), oe);
                writer.write(reply);
                return;
            } catch (Exception e2) {
                e.addSuppressed(e2);
                throw e;
            }
        }

        try {
            reply = processor.handleCall(call);
        } catch (Exception e) {
            LOGGER.error("Error when handling service call " + processor.getDescriptor().getName() + "." + call.getMethod() + "()", e);
            try {
                PApplicationException oe = new PApplicationException(e.getMessage(), PApplicationExceptionType.INTERNAL_ERROR);
                reply = new PServiceCall<>(call.getMethod(), PServiceCallType.EXCEPTION, call.getSequence(), oe);
                writer.write(reply);
                return;
            } catch (Exception e2) {
                e.addSuppressed(e2);
                throw e;
            }
        }

        if (reply != null) {
            try {
                writer.write(reply);
            } catch (SerializerException e) {
                LOGGER.error("Error when replying to service call " + processor.getDescriptor().getName() + "." + call.getMethod() + "()", e);
                try {
                    PApplicationException oe = new PApplicationException(e.getMessage(), e.getExceptionType());
                    reply = new PServiceCall<>(call.getMethod(), PServiceCallType.EXCEPTION, call.getSequence(), oe);
                    writer.write(reply);
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                    throw e;
                }
            }
        }

    }
}
