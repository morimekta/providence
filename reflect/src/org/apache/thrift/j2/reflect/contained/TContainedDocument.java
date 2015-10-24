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

package org.apache.thrift.j2.reflect.contained;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.thrift.j2.descriptor.TDeclaredDescriptor;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TServiceDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 25.08.15
 */
@SuppressWarnings("unused")
public class TContainedDocument {
    private final String                       mComment;
    private final String                       mPackageName;
    private final List<String>                 mIncludes;
    private final Map<String, String>          mNamespaces;
    private final Map<String, String>          mTypedefs;
    private final List<TDeclaredDescriptor<?>> mDeclaredTypes;
    private final List<TServiceDescriptor>     mServices;
    private final List<TField<?>>              mConstants;

    public TContainedDocument(String comment,
                              String packageName,
                              Map<String, String> namespaces,
                              List<String> includes,
                              Map<String, String> typedefs,
                              List<TDeclaredDescriptor<?>> declaredTypes,
                              List<TServiceDescriptor> services,
                              List<TField<?>> constants) {
        mComment = comment;
        mPackageName = packageName;
        mNamespaces = Collections.unmodifiableMap(namespaces);
        mIncludes = Collections.unmodifiableList(includes);
        mTypedefs = Collections.unmodifiableMap(typedefs);
        mDeclaredTypes = Collections.unmodifiableList(declaredTypes);
        mServices = Collections.unmodifiableList(services);
        mConstants = Collections.unmodifiableList(constants);
    }

    public String getComment() {
        return mComment;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public List<String> getIncludes() {
        return mIncludes;
    }

    public Map<String, String> getNamespaces() {
        return mNamespaces;
    }

    public Map<String, String> getTypedefs() {
        return mTypedefs;
    }

    public List<TDeclaredDescriptor<?>> getDeclaredTypes() {
        return mDeclaredTypes;
    }

    public List<TServiceDescriptor> getServices() {
        return mServices;
    }

    public List<TField<?>> getConstants() {
        return mConstants;
    }

    // --- Extra methods.

    public String getNamespaceForLanguage(String language) {
        return mNamespaces.get(language);
    }

}
