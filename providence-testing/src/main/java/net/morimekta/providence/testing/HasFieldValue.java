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
import net.morimekta.util.Strings;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class HasFieldValue<Message extends PMessage<Message, Field>, Field extends PField>
        extends BaseMatcher<Message> {
    private final String[] path;

    public HasFieldValue(String path) {
        this.path = path.split("[.]");

        assertTrue("Field path has content", this.path.length > 0);
    }

    public HasFieldValue(Field field) {
        this.path = new String[]{field.getName()};
    }

    @Override
    public boolean matches(Object o) {
        if (o == null) {
            return false;
        }
        for (int i = 0; i < path.length; ++i) {
            if (!(o instanceof PMessage)) {
                return false;
            }
            PMessage actual = (PMessage) o;

            PField field = actual.descriptor().findFieldByName(path[i]);
            if (field == null) {
                return false;
            }
            if (!actual.has(field.getKey())) {
                return false;
            }
            o = actual.get(field.getKey());
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has field \'" + Strings.join(".", (Object[]) path) + "\'");
    }

    @Override
    public void describeMismatch(Object o, Description mismatchDescription) {
        if (o == null) {
            mismatchDescription.appendText("got null message");
        } else {
            if (!(o instanceof PMessage)) {
                mismatchDescription.appendText("instance is not a message");
                return;
            }

            List<String> stack = new LinkedList<>();
            for (int i = 0; i < path.length; ++i) {
                stack.add(path[i]);
                String path = Strings.join(".", stack);

                if (!(o instanceof PMessage)) {
                    mismatchDescription.appendText("field \'" + path + "\' is not a message");
                    return;
                }
                PMessage actual = (PMessage) o;

                PField field = actual.descriptor().findFieldByName(this.path[i]);
                if (field == null) {
                    mismatchDescription.appendText("field path \'" + path + "\' is not valid");
                    return;
                }
                if (!actual.has(field.getKey())) {
                    mismatchDescription.appendText("field \'" + path + "\' is missing");
                    return;
                }

                o = actual.get(field.getKey());
            }
        }
    }
}
