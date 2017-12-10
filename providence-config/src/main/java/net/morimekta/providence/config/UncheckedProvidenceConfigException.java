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
package net.morimekta.providence.config;

/**
 * Unchecked config exception wrapping the providence config exception.
 * Handy for using config in streams etc.
 */
public class UncheckedProvidenceConfigException extends RuntimeException {
    public UncheckedProvidenceConfigException(ProvidenceConfigException cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public UncheckedProvidenceConfigException initCause(Throwable cause) {
        if (!(cause instanceof ProvidenceConfigException)) {
            throw new IllegalArgumentException("Exception " + cause.getClass().getName() + " is not a config exception");
        }
        super.initCause(cause);
        return this;
    }

    public ProvidenceConfigException getCause() {
        return (ProvidenceConfigException) super.getCause();
    }
}
