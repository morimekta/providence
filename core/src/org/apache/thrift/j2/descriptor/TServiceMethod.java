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

import org.apache.thrift.j2.util.TTypeUtils;
import org.apache.thrift.j2.TMessage;

/**
 * @author Stein Eldar Johnsen
 * @since 13.09.15
 */
public class TServiceMethod<R,
                            P extends TMessage<P>,
                            E extends TMessage<E>> {
    private final String                       mComment;
    private final boolean                      mOneway;
    private final String                       mName;
    private final TDescriptorProvider<R>       mReturnTypeProvider;
    private final TStructDescriptorProvider<P> mParamsTypeProvider;
    private final TUnionDescriptorProvider<E>  mExceptionTypeProvider;

    public TServiceMethod(String comment,
                          boolean oneway,
                          String name,
                          TDescriptorProvider<R> returnTypeProvider,
                          TStructDescriptorProvider<P> paramsTypeProvider,
                          TUnionDescriptorProvider<E> exceptionTypeProvider) {
        mComment = comment;
        mOneway = oneway;
        mName = name;
        mReturnTypeProvider = returnTypeProvider;
        mParamsTypeProvider = paramsTypeProvider;
        mExceptionTypeProvider = exceptionTypeProvider;
    }

    public String getComment() {
        return mComment;
    }

    public boolean isOneway() {
        return mOneway;
    }

    public String getName() {
        return mName;
    }

    public TDescriptor<R> getReturnType() {
        if (mReturnTypeProvider != null) {
            return mReturnTypeProvider.descriptor();
        }
        return null;
    }

    public TStructDescriptor<P> getParamsDescriptor() {
        if (mParamsTypeProvider != null) {
            return mParamsTypeProvider.descriptor();
        }
        return null;
    }

    public TUnionDescriptor<E> getExceptionDescriptor() {
        if (mExceptionTypeProvider != null) {
            return mExceptionTypeProvider.descriptor();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName())
               .append('{');
        TDescriptor ret = getReturnType();
        TStructDescriptor<P> params = getParamsDescriptor();
        TUnionDescriptor<E> exception = getExceptionDescriptor();
        if (ret == null) {
            if (mOneway) {
                builder.append("oneway ");
            }
            builder.append("void");
        } else {
            builder.append(ret.getName());
        }
        builder.append(' ')
               .append(mName)
               .append('(');
        if (params != null) {
            boolean first = false;
            for (TField<?> field : params.getFields()) {
                if (!first)
                    first = true;
                else
                    builder.append(',');
                builder.append(field.getDescriptor().getName())
                       .append(' ')
                       .append(field.getName());
            }
        }
        builder.append(')');
        if (exception != null) {
            boolean first = true;
            for (TField<?> field : exception.getFields()) {
                if (first) {
                    first = false;
                    builder.append(" throws ");
                } else
                    builder.append(',');
                builder.append(field.getDescriptor().getName());
            }
        }
        builder.append('}');
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TServiceMethod)) {
            return false;
        }
        TServiceMethod<?,?,?> other = (TServiceMethod<?,?,?>) o;
        return mOneway == other.mOneway &&
               mName.equals(other.mName) &&
               TTypeUtils.equalsQualifiedName(getReturnType(), other.getReturnType()) &&
               TTypeUtils.equalsQualifiedName(getParamsDescriptor(), other.getParamsDescriptor()) &&
               TTypeUtils.equalsQualifiedName(getExceptionDescriptor(), other.getExceptionDescriptor());
    }

    @Override
    public int hashCode() {
        return TServiceMethod.class.hashCode() +
               TTypeUtils.hashCode(mOneway) +
               TTypeUtils.hashCode(mName) +
               getParamsDescriptor().getQualifiedName(null).hashCode() +
               getExceptionDescriptor().getQualifiedName(null).hashCode();
    }
}
