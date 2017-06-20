/*
 * Copyright 2015 Providence Authors
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

import javax.annotation.Nonnull;

/**
 * Base class for all messages.
 *
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
public interface PUnion<Union extends PUnion<Union, Field>, Field extends PField>
        extends PMessage<Union, Field> {
    /**
     * @return Returns true if the union has a field set. If true {@link #unionField()}
     *         will not throw an exception.
     */
    boolean unionFieldIsSet();

    /**
     * The user should be able to assume that this value never is null.
     * @return The field set on the union.
     * @throws IllegalStateException If no field is set.
     */
    @Nonnull
    Field unionField();
}
