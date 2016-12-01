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

package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PStructDescriptor;

/**
 * See the struct descriptor of {@link PStructDescriptor}. It is avoided in this case in
 * order to be able to have subclasses of PStructDescriptor and PUnionDescriptor to
 * implement an interface that alreadt have the {@link #getFields()} methods with the
 * {@link CField} contained field implementation.
 */
public interface CMessageDescriptor
        extends CAnnotatedDescriptor, PDescriptor {
    // From PMessageDescriptor
    CField[] getFields();

    CField getField(String name);

    CField getField(int key);
}
