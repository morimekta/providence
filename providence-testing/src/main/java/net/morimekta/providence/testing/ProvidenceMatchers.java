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
package net.morimekta.providence.testing;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.descriptor.PField;

import org.hamcrest.Matcher;

/**
 * Assert shorthands for providence messages.
 */
public class ProvidenceMatchers {
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    EqualToMessage<Message, Field> equalToMessage(Message expected) {
        return new EqualToMessage<>(expected);
    }

    public static <Message extends PMessage<Message, Field>, Field extends PField>
    HasFieldValue<Message, Field> hasFieldValue(String path) {
        return new HasFieldValue<>(path);
    }
    public static <Message extends PMessage<Message, Field>, Field extends PField>
    HasFieldValue<Message, Field> hasFieldValue(Field field) {
        return new HasFieldValue<>(field);
    }
    public static <Message extends PMessage<Message, Field>, Field extends PField, MT>
    HasFieldValueThat<Message, Field, MT> hasFieldValueThat(String path, Matcher<MT> matcher) {
        return new HasFieldValueThat<>(path, matcher);
    }
    public static <Message extends PMessage<Message, Field>, Field extends PField, MT>
    HasFieldValueThat<Message, Field, MT> hasFieldValueThat(Field field, Matcher<MT> matcher) {
        return new HasFieldValueThat<>(field, matcher);
    }
}
