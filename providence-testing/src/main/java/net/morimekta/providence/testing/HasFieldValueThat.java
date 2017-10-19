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
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Stein Eldar Johnsen
 * @since 21.01.16.
 */
public class HasFieldValueThat<Message extends PMessage<Message, Field>, Field extends PField, MT>
        extends BaseMatcher<Message> {
    private final String[]    path;
    private final Matcher<MT> valueMatcher;

    public HasFieldValueThat(String path, Matcher<MT> valueMatcher) {
        this.path = path.split("[.]");
        this.valueMatcher = valueMatcher;

        assertTrue("Field path has content", this.path.length > 0);
    }

    public HasFieldValueThat(Field field, Matcher<MT> valueMatcher) {
        this.path = new String[]{field.getName()};
        this.valueMatcher = valueMatcher;
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
            if (!actual.has(field.getId())) {
                return false;
            }
            o = actual.get(field.getId());
        }
        return valueMatcher.matches(o);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has field \'" + Strings.join(".", (Object[]) path) + "\' that ");
        valueMatcher.describeTo(description);
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

            List<String> stack = new ArrayList<>();
            String path = "";
            for (int i = 0; i < this.path.length; ++i) {
                String fieldName = this.path[i];
                stack.add(fieldName);

                path = Strings.join(".", stack);

                if (!(o instanceof PMessage)) {
                    mismatchDescription.appendText("field " + path + " is not a message");
                    return;
                }
                PMessage actual = (PMessage) o;

                PField field = actual.descriptor().findFieldByName(fieldName);
                if (field == null) {
                    mismatchDescription.appendText("field path " + path + " is not valid");
                    return;
                }
                if (!actual.has(field.getId())) {
                    mismatchDescription.appendText("field " + path + " is missing");
                    return;
                }

                o = actual.get(field.getId());
            }

            mismatchDescription.appendText("field " + path + " ");
            valueMatcher.describeMismatch(o, mismatchDescription);
        }
    }
}
