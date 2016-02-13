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

package net.morimekta.providence;

/**
 * @author Stein Eldar Johnsen
 * @since 13.09.15
 */
public abstract class PException extends Exception {
    private static final long serialVersionUID = 1442914318188313667L;

    protected PException(String message) {
        super(message);
    }

    public String origGetMessage() {
        return super.getMessage();
    }

    public String origGetLocalizedMessage() {
        return super.getLocalizedMessage();
    }
}
