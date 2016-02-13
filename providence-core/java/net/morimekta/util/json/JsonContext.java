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

package net.morimekta.util.json;

/**
 * @author Stein Eldar Johnsen
 * @since 16.01.16.
 */
class JsonContext {
    protected enum Mode {
        VALUE,
        LIST,
        MAP,
    }

    protected enum Expect {
        KEY,
        VALUE,
    }

    public final Mode   mode;
    public       Expect expect;
    public       int    num;

    public JsonContext(Mode mode) {
        this.mode = mode;
        if (mode == Mode.MAP) {
            expect = Expect.KEY;
        } else {
            expect = Expect.VALUE;
        }
        num = 0;
    }

    public boolean key() {
        return mode == Mode.MAP && expect == Expect.KEY;
    }

    public boolean value() {
        return mode == Mode.VALUE ? num == 0 : !key();
    }

    public boolean map() {
        return mode == Mode.MAP;
    }

    public boolean list() {
        return mode == Mode.LIST;
    }
}
