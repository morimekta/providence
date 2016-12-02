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
package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.descriptor.PDescriptorProvider;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PValueProvider;

import java.util.Map;

/**
 * First stage before we have a totally separate CConst from the CField contained type class.
 */
public class CConst extends CField {
    public CConst(String comment,
                  String name,
                  PDescriptorProvider typeProvider,
                  PValueProvider defaultValue,
                  Map<String, String> annotations) {
        super(comment, -1, PRequirement.REQUIRED, name, typeProvider, defaultValue, annotations);
    }
}
