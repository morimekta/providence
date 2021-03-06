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

    /**
     * Add providence version to the <code>@Generated</code> annotation for each
     * generated class.
     */
    public boolean generated_annotation_version = true;

    /**
     * Generate public constructors for all structs and exceptions. Have no
     * effect on unions. Can be overridden per class with the
     * <code>java.public.constructor = ""</code> annotation.
     */
    public boolean public_constructors = false;

    /**
     * Set to true to generate types belonging in the 'net.morimekta.providence' and
     * 'net.morimekta.providence.model' packages. Per default this is skipped as the
     * models are located in the 'providence-core' and 'providence-reflect' packages
     * respectively, where they are mainly used.
     */
    public boolean generate_providence_core_types = false;
}
