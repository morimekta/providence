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

package net.morimekta.providence.generator.format.java;

import net.morimekta.providence.generator.format.java.enums.CommonMemberFormatter;
import net.morimekta.providence.generator.format.java.enums.CoreMemberFormatter;
import net.morimekta.providence.generator.format.java.enums.extras.JacksonEnumFormatter;
import net.morimekta.providence.generator.format.java.shared.BaseEnumFormatter;
import net.morimekta.providence.generator.format.java.shared.EnumMemberFormatter;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author Stein Eldar Johnsen
 * @since 20.09.15
 */
public class JavaEnumFormatter extends BaseEnumFormatter {
    JavaEnumFormatter(IndentedPrintWriter writer,
                      JavaOptions options) {
        super(writer, getFormatters(writer, options));
    }

    private static List<EnumMemberFormatter> getFormatters(IndentedPrintWriter writer, JavaOptions options) {
        ImmutableList.Builder<EnumMemberFormatter> builder = ImmutableList.builder();

        builder.add(new CommonMemberFormatter(writer))
               .add(new CoreMemberFormatter(writer));

        if (options.jackson) {
            builder.add(new JacksonEnumFormatter(writer));
        }

        return builder.build();
    }
}
