/*
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

package net.morimekta.providence.descriptor;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilderFactory;
import net.morimekta.providence.PMessageVariant;

/**
 * The definition of a thrift exception.
 */
public abstract class PExceptionDescriptor<T extends PMessage<T>, F extends PField> extends PStructDescriptor<T, F> {
    public PExceptionDescriptor(String packageName,
                                String name,
                                PMessageBuilderFactory<T> provider,
                                boolean simple) {
        super(packageName, name, provider, simple, false);
    }

    @Override
    public PMessageVariant getVariant() {
        return PMessageVariant.EXCEPTION;
    }
}
