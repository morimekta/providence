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
package net.morimekta.providence.generator.format.java.messages.extras;

import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.rw.BinaryFormatUtils;
import net.morimekta.providence.serializer.rw.BinaryType;
import net.morimekta.providence.serializer.rw.BinaryWriter;
import net.morimekta.util.Binary;
import net.morimekta.util.io.BigEndianBinaryWriter;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;
import static net.morimekta.providence.serializer.rw.BinaryType.forType;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BinaryWriterFormatter implements MessageMemberFormatter {
    private final AtomicInteger nextId = new AtomicInteger(1);

    private final IndentedPrintWriter writer;
    private final JHelper helper;

    public BinaryWriterFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        return ImmutableList.of(BinaryWriter.class.getName());
    }

    private void appendWriteFieldValue(String member, PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case VOID:
                break;
            case BOOL:
                writer.formatln("length += writer.writeUInt8(%s ? (byte) 1 : (byte) 0);", member);
                break;
            case BYTE:
                writer.formatln("length += writer.writeByte(%s);", member);
                break;
            case I16:
                writer.formatln("length += writer.writeShort(%s);", member);
                break;
            case I32:
                writer.formatln("length += writer.writeInt(%s);", member);
                break;
            case I64:
                writer.formatln("length += writer.writeLong(%s);", member);
                break;
            case DOUBLE:
                writer.formatln("length += writer.writeDouble(%s);", member);
                break;
            case BINARY:
                writer.formatln("length += writer.writeUInt32(%s.length());", member);
                writer.formatln("length += writer.writeBinary(%s);", member);
                break;
            case STRING: {
                String tmpName = "tmp_" + nextId.getAndIncrement();

                writer.formatln("%s %s = %s.wrap(%s.getBytes(%s.UTF_8));",
                                Binary.class.getName(),
                                tmpName,
                                Binary.class.getName(),
                                member,
                                StandardCharsets.class.getName());
                writer.formatln("length += writer.writeUInt32(%s.length());", tmpName);
                writer.formatln("length += writer.writeBinary(%s);", tmpName);
                break;
            }
            case ENUM:
                writer.formatln("length += writer.writeInt(%s.getValue());", member);
                break;
            case MAP: {
                PMap<?, ?> pMap = (PMap<?, ?>) descriptor;
                String entryName = "entry_" + nextId.getAndIncrement();

                writer.formatln("length += writer.writeByte((byte) %d);", forType(pMap.keyDescriptor().getType()))
                      .formatln("length += writer.writeByte((byte) %d);", forType(pMap.itemDescriptor().getType()))
                      .formatln("length += writer.writeUInt32(%s.size());", member)
                      .formatln("for (%s.Entry<%s,%s> %s : %s.entrySet()) {",
                                Map.class.getName(),
                                helper.getFieldType(pMap.keyDescriptor()),
                                helper.getFieldType(pMap.itemDescriptor()),
                                entryName, member)
                      .begin();

                appendWriteFieldValue(entryName + ".getKey()", pMap.keyDescriptor());
                appendWriteFieldValue(entryName + ".getValue()", pMap.itemDescriptor());

                writer.end()
                      .appendln('}');
                break;
            }
            case LIST:
            case SET: {
                PContainer<?> pContainer = (PContainer<?>) descriptor;
                String entryName = "entry_" + nextId.getAndIncrement();

                writer.formatln("length += writer.writeByte((byte) %d);", forType(pContainer.itemDescriptor().getType()))
                      .formatln("length += writer.writeUInt32(%s.size());", member)
                      .formatln("for (%s %s : %s) {",
                                helper.getFieldType(pContainer.itemDescriptor()),
                                entryName,
                                member)
                      .begin();

                appendWriteFieldValue(entryName, pContainer.itemDescriptor());

                writer.end()
                      .appendln('}');
                break;
            }
            case MESSAGE: {
                // Since the referred class may not implement the BinaryWriter interface (yet)
                writer.formatln("length += %s.writeMessage(writer, %s);",
                                BinaryFormatUtils.class.getName(),
                                member);
                break;
            }
            default:
                throw new GeneratorException("Unsupported binary writer type: " + descriptor.getQualifiedName());
        }

    }

    @Override
    public void appendMethods(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public int writeBinary(%s writer) throws %s {",
                        BigEndianBinaryWriter.class.getName(),
                        IOException.class.getName())
              .begin()
              .appendln("int length = 0;")
              .newline();

        if (message.isUnion()) {
            writer.formatln("if (%s != null) {", UNION_FIELD)
                  .begin()
                  .formatln("switch (%s) {", UNION_FIELD)
                  .begin();
            for (JField field : message.numericalOrderFields()) {
                writer.formatln("case %s: {", field.fieldEnum())
                      .begin()
                      .formatln("length += writer.writeByte((byte) %d);", forType(field.type()))
                      .formatln("length += writer.writeShort((short) %d);", field.id());

                appendWriteFieldValue(field.member(),
                                      field.field()
                                           .getDescriptor());

                writer.appendln("break;")
                      .end()
                      .appendln('}');
            }
            writer.appendln("default: break;")
                  .end()
                  .appendln('}')
                  .end()
                  .appendln('}');
        } else {
            for (JField field : message.numericalOrderFields()) {
                if (!field.alwaysPresent()) {
                    writer.formatln("if (%s()) {", field.presence())
                          .begin();
                }

                writer.formatln("length += writer.writeByte((byte) %d);", forType(field.type()))
                      .formatln("length += writer.writeShort((short) %d);", field.id());

                appendWriteFieldValue(field.member(),
                                      field.field()
                                           .getDescriptor());

                if (!field.alwaysPresent()) {
                    writer.end()
                          .appendln('}');
                }

                writer.newline();
            }
        }

        writer.formatln("length += writer.writeByte((byte) %d);", BinaryType.STOP);

        writer.appendln("return length;")
              .end()
              .appendln('}')
              .newline();
    }
}
