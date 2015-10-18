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

package org.apache.thrift.j2.descriptor;

import org.apache.thrift.j2.TBuilderFactory;

/**
 * @author Stein Eldar Johnsen <steineldar@zedge.net>
 * @since 25.08.15
 */
public abstract class TDeclaredDescriptor<T>
        implements TDescriptor<T> {
    private final String   mComment;
    private final String   mPackageName;
    private final String   mName;

    protected TDeclaredDescriptor(String comment,
                                  String packageName,
                                  String name) {
        mComment = comment;
        mPackageName = packageName;
        mName = name;
    }

    /**
     * The type comment is the last block of comment written before
     * the type declaration. Comments on the same line, after the
     * declaration is ignored.
     *
     * @return The comment string containing all formatting (not
     *         including the comment delimiter and the leading space.
     */
    public final String getComment() {
        return mComment;
    }

    @Override
    public final String getPackageName() {
        return mPackageName;
    }

    @Override
    public final String getName() {
        return mName;
    }

    @Override
    public final String getQualifiedName(String packageName) {
        if (!mPackageName.equals(packageName)) {
            return getPackageName() + "." + getName();
        }
        return getName();
    }

    @Override
    public String toString() {
        return getQualifiedName(null);
    }

    /**
     * Get the builder for the given declared type.
     *
     * @return The type specific builder provider.
     */
    public abstract TBuilderFactory<T> factory();
}
