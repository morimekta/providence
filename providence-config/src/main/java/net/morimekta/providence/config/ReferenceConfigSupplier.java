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
package net.morimekta.providence.config;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PType;
import net.morimekta.providence.config.impl.UpdatingConfigSupplier;
import net.morimekta.providence.descriptor.PField;

import javax.annotation.Nonnull;
import java.time.Clock;

/**
 * A supplier to get a config (aka message) from a resource location. This is
 * a fixed static supplier, so listening to changes will never do anything.
 *
 * <pre>
 *     ConfigSupplier&lt;Service, Service._Field&gt; supplier =
 *             new ResourceConfigSupplier&lt;&gt;(referencePath, Service.kDescriptor);
 * </pre>
 */
public class ReferenceConfigSupplier<
        RefMessage extends PMessage<RefMessage, RefField>, RefField extends PField,
        ParentMessage extends PMessage<ParentMessage, ParentField>, ParentField extends PField>
        extends UpdatingConfigSupplier<RefMessage, RefField> {
    private final String referencePath;
    private final ConfigListener<ParentMessage, ParentField> listener;
    private final ConfigSupplier<ParentMessage, ParentField> parent;

    /**
     * Create a config that wraps a providence message instance, and fetches a message from
     * within that parent config. It is not allowed to have it return a null, meaning for
     * the reference config to be valid, the reference must exist.
     *
     * @param referencePath The resource name to load.
     * @param parent The message type descriptor.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public ReferenceConfigSupplier(@Nonnull String referencePath,
                                   @Nonnull ConfigSupplier<ParentMessage, ParentField> parent)
            throws ProvidenceConfigException {
        this(referencePath, parent, Clock.systemUTC());
    }

    /**
     * Create a config that wraps a providence message instance, and fetches a message from
     * within that parent config. It is not allowed to have it return a null, meaning for
     * the reference config to be valid, the reference must exist.
     *
     * @param referencePath The resource name to load.
     * @param parent The message type descriptor.
     * @param clock The clock to use for timing.
     * @throws ProvidenceConfigException If message overriding failed
     */
    public ReferenceConfigSupplier(String referencePath, ConfigSupplier<ParentMessage, ParentField> parent, Clock clock)
            throws ProvidenceConfigException {
        super(clock);
        listener = c -> {
            try {
                set(getReference(c));
            } catch (ProvidenceConfigException e) {
                throw new UncheckedProvidenceConfigException(e);
            }
        };
        parent.addListener(listener);
        this.parent = parent;
        this.referencePath = referencePath;
        set(getReference(parent.get()));
    }

    @Override
    public String toString() {
        return "ReferenceConfig{" + referencePath + ", parent=" + parent.getName() + "}";
    }

    @Override
    public String getName() {
        return "ReferenceConfig{" + referencePath + "}";
    }

    @SuppressWarnings("unchecked")
    private <
            RefMessage extends PMessage<RefMessage, RefField>, RefField extends PField,
            ParentMessage extends PMessage<ParentMessage, ParentField>, ParentField extends PField>
    RefMessage getReference(ParentMessage parent) throws ProvidenceConfigException {
        PMessage current = parent;
        String[] fieldNames = referencePath.split("[.]");
        for (String name : fieldNames) {
            PField field = current.descriptor().findFieldByName(name);
            if (field == null) {
                throw new ProvidenceConfigException("No such field " + name + " in " + current.descriptor().getQualifiedName() + " from " + referencePath);
            }
            if (field.getType() != PType.MESSAGE) {
                throw new ProvidenceConfigException("Field " + name + " in " + current.descriptor().getQualifiedName() + " is not a message, from " + referencePath);
            }
            current = (PMessage) current.get(field);
        }
        return (RefMessage) current;
    }
}
