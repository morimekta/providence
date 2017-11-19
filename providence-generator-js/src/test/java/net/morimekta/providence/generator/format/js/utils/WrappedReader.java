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
package net.morimekta.providence.generator.format.js.utils;

import javax.annotation.Nonnull;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

public class WrappedReader extends Reader {
    private boolean headCompleted = false;
    private boolean contentCompleted = false;

    private final CharArrayReader header;
    private final Reader content;
    private final CharArrayReader footer;

    public WrappedReader(String header, Reader content, String footer) {
        this.header = new CharArrayReader(header.toCharArray());
        this.content = content;
        this.footer = new CharArrayReader(footer.toCharArray());
    }

    @Override
    public int read(@Nonnull char[] chars, int off, int len) throws IOException {
        if (!headCompleted) {
            int tmp = header.read(chars, off, len);
            if (tmp > 0) {
                return tmp;
            }
            headCompleted = true;
        }
        if (!contentCompleted) {
            int tmp = content.read(chars, off, len);
            if (tmp > 0) {
                return tmp;
            }
            contentCompleted = true;
        }

        return footer.read(chars, off, len);
    }

    @Override
    public void close() throws IOException {
        headCompleted = true;
        contentCompleted = true;
        header.close();
        footer.close();
        content.close();
    }
}
