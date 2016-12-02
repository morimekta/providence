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

/**
 * Enclosed call to a service method.
 */
public class PServiceCall<Message extends PMessage<Message, Field>, Field extends PField> {
    private final String           method;
    private final PServiceCallType type;
    private final int              sequence;
    private final Message          message;

    public PServiceCall(String method,
                        PServiceCallType type,
                        int sequence,
                        Message message) {
        this.method = method;
        this.type = type;
        this.sequence = sequence;
        this.message = message;
    }

    /**
     * The name of the method called.
     *
     * @return Name of method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * The type of service call.
     *
     * @return Type of call.
     */
    public PServiceCallType getType() {
        return type;
    }

    /**
     * The sequence number of the call. Can be used to
     * match responses with associated calls.
     *
     * @return Sequence Number.
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * The message sent or received. This should map to the request or response
     * message struct / union generated for the method being calles.
     *
     * @return The sent message.
     */
    public Message getMessage() {
        return message;
    }
}
