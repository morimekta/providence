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

import net.morimekta.providence.mio.MessageReader;
import net.morimekta.providence.mio.MessageWriter;

import java.io.IOException;

/**
 * Stream processor interface for providence services.
 */
@FunctionalInterface
public interface ProcessorHandler {
    /**
     * Process message read from reader, and write response to writer.
     *
     * @param reader The message reader for the request.
     * @param writer The message writer for the response.
     * @throws IOException In failure to handle input or output.
     */
    void process(MessageReader reader, MessageWriter writer) throws IOException;
}
