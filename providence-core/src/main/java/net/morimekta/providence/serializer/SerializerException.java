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

package net.morimekta.providence.serializer;

import net.morimekta.providence.PServiceCallType;
import net.morimekta.util.Stringable;

import com.google.common.base.MoreObjects;

/**
 * @author Stein Eldar Johnsen
 * @since 19.09.15
 */
public class SerializerException extends Exception implements Stringable {
    private final static long serialVersionUID = 1442914425369642982L;

    private String           methodName;
    private PServiceCallType callType;
    private int              sequenceNo;
    private ApplicationExceptionType exceptionType;

    public SerializerException(String format, Object... args) {
        super(args.length == 0 ? format : String.format(format, args));
        exceptionType = ApplicationExceptionType.PROTOCOL_ERROR;
    }

    public SerializerException(Throwable cause, String format, Object... args) {
        super(args.length == 0 ? format : String.format(format, args), cause);
        exceptionType = ApplicationExceptionType.PROTOCOL_ERROR;
    }

    public String getMethodName() {
        return methodName == null ? "" : methodName;
    }

    public PServiceCallType getCallType() {
        return callType;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public ApplicationExceptionType getExceptionType() {
        return exceptionType;
    }

    public SerializerException setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public SerializerException setCallType(PServiceCallType callType) {
        this.callType = callType;
        return this;
    }

    public SerializerException setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
        return this;
    }

    public SerializerException setExceptionType(ApplicationExceptionType type) {
        this.exceptionType = type;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                          .omitNullValues()
                          .addValue(getMessage())
                          .add("method", methodName)
                          .add("type", callType)
                          .add("seq", sequenceNo)
                          .toString();
    }

    @Override
    public String asString() {
        if (methodName != null) {
            return "Error in " + methodName + ": " + getMessage();
        } else {
            return "Error: " + getMessage();
        }
    }
}
