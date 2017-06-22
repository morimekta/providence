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
package net.morimekta.providence.thrift;

import org.apache.thrift.protocol.TJSONProtocol;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TJsonProtocolSerializer extends TProtocolSerializer {
    public static final String MEDIA_TYPE = "application/vnd.apache.thrift.json";

    public TJsonProtocolSerializer() {
        this(DEFAULT_STRICT);
    }

    public TJsonProtocolSerializer(boolean readStrict) {
        super(readStrict, new TJSONProtocol.Factory(),
              false, MEDIA_TYPE);
    }
}
