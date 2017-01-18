/*
 * Copyright 2015-2016 Providence Authors
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
package net.morimekta.providence.generator.format.java;

/**
 * Options class for java 2 generator.
 */
public class JavaOptions {
    /**
     * Add android parcelable support.
     */
    public boolean android = false;

    /**
     * Add jackson serializers and deserializers.
     */
    public boolean jackson = false;

    /**
     * Add precompiled serialization for the binary protocol.
     */
    public boolean rw_binary = true;

    /**
     * Add hazelcast_portable serializer and deserializers for portable interface.
     */
    public boolean hazelcast_portable = false;
}
