/*
 * Copyright 2017 Providence Authors
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
package net.morimekta.providence.serializer.json;

/**
 * Marker class and implementation check for if a struct can use the JSON
 * jsonCompact format, using an array of the fields in numeric order.
 * <p>
 * So the message:
 * <pre>{@code
 * {
 *     "first_field": "The first",
 *     "second_field": 12345
 * }
 * }</pre>
 * Becomes:
 * <pre>{@code
 * ["The first", 12345]
 * }</pre>
 *
 * @see JsonCompactible
 */
public interface JsonCompactibleDescriptor {
    /**
     * @return If the message may be compactible.
     */
    default boolean isJsonCompactible() {
        return true;
    }
}
