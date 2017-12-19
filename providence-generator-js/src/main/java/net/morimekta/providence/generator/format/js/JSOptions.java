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
package net.morimekta.providence.generator.format.js;

import net.morimekta.providence.generator.GeneratorException;

/**
 * Options class for js generator.
 */
public class JSOptions {
    public boolean type_script = false;
    public boolean closure     = false;
    public boolean node_js     = false;
    public boolean es51        = false;
    public boolean pvd         = false;

    public boolean useMaps() {
        return !es51;
    }

    public void validate() {
        if ((node_js && closure) ||
            (node_js && type_script) ||
            (closure && type_script)) {
            throw new GeneratorException("More than one of node.js, closure and type_script options used!");
        }
    }
}
