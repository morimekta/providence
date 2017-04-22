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

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.GeneratorException;
import net.morimekta.providence.generator.format.java.shared.MessageMemberFormatter;
import net.morimekta.providence.generator.format.java.utils.JField;
import net.morimekta.providence.generator.format.java.utils.JHelper;
import net.morimekta.providence.generator.format.java.utils.JMessage;
import net.morimekta.providence.serializer.SerializerException;
import net.morimekta.providence.serializer.rw.BinaryFormatUtils;
import net.morimekta.providence.serializer.rw.BinaryReader;
import net.morimekta.providence.serializer.rw.BinaryType;
import net.morimekta.util.io.BigEndianBinaryReader;
import net.morimekta.util.io.IndentedPrintWriter;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.morimekta.providence.generator.format.java.messages.CoreOverridesFormatter.UNION_FIELD;
import static net.morimekta.providence.serializer.rw.BinaryType.forType;

/**
 * @author Stein Eldar Johnsen
 * @since 08.01.16.
 */
public class BinaryReaderBuilderFormatter implements MessageMemberFormatter {
    private final AtomicInteger nextId = new AtomicInteger(1);

    private final IndentedPrintWriter writer;
    private final JHelper helper;

    public BinaryReaderBuilderFormatter(IndentedPrintWriter writer, JHelper helper) {
        this.writer = writer;
        this.helper = helper;
    }

    @Override
    public Collection<String> getExtraImplements(JMessage<?> message) throws GeneratorException {
        return ImmutableList.of(BinaryReader.class.getName());
    }

    private void appendReadFieldValue(String member, JMessage message, JField field, PDescriptor descriptor) {
        switch (descriptor.getType()) {
            case VOID:
                break;
            case BOOL:
                writer.formatln("%s = reader.expectUInt8() == 1;", member);
                break;
            case BYTE:
                writer.formatln("%s = reader.expectByte();", member);
                break;
            case I16:
                writer.formatln("%s = reader.expectShort();", member);
                break;
            case I32:
                writer.formatln("%s = reader.expectInt();", member);
                break;
            case I64:
                writer.formatln("%s = reader.expectLong();", member);
                break;
            case DOUBLE:
                writer.formatln("%s = reader.expectDouble();", member);
                break;
            case BINARY: {
                String tmp = "len_" + nextId.getAndIncrement();
                writer.formatln("int %s = reader.expectUInt32();", tmp);
                writer.formatln("%s = reader.expectBinary(%s);", member, tmp);
                break;
            }
            case STRING: {
                String tmp = "len_" + nextId.getAndIncrement();
                writer.formatln("int %s = reader.expectUInt32();", tmp);
                writer.formatln("%s = new String(reader.expectBytes(%s), %s.UTF_8);",
                                member, tmp, StandardCharsets.class.getName());
                break;
            }
            case ENUM:
                writer.formatln("%s = %s.forValue(reader.expectInt());",
                                member, helper.getFieldType(descriptor));
                break;
            case MAP: {
                PMap<?, ?> pMap = (PMap<?, ?>) descriptor;
                if (field == null) {
                    throw new GeneratorException("Impossible!");
                }
                if (pMap.keyDescriptor() instanceof PContainer ||
                    pMap.itemDescriptor() instanceof PContainer) {
                    // If the container contains a container this code will
                    // break. Using the reader library in that case.
                    writer.formatln("%s.putAll((%s) %s.readFieldValue(reader, new %s(field, type), _Field.%s.getDescriptor(), strict));",
                                    member,
                                    field.fieldType(),
                                    BinaryFormatUtils.class.getName(),
                                    BinaryFormatUtils.FieldInfo.class.getName().replaceAll("\\$", "."),
                                    field.fieldEnum(),
                                    helper.getFieldType(descriptor));
                    break;
                }
                String len = "len_" + nextId.getAndIncrement();
                String keyType = "t_" + nextId.getAndIncrement();
                String valueType = "t_" + nextId.getAndIncrement();
                writer.formatln("byte %s = reader.expectByte();", keyType)
                      .formatln("byte %s = reader.expectByte();", valueType)
                      .formatln("if (%s == %d && %s == %d) {",
                                keyType, forType(pMap.keyDescriptor().getType()),
                                valueType, forType(pMap.itemDescriptor().getType()))
                      .begin()
                      .formatln("final int %s = reader.expectUInt32();", len);

                String i = "i_" + nextId.getAndIncrement();

                writer.formatln("for (int %s = 0; %s < %s; ++%s) {",
                                i, i, len, i)
                      .begin();

                String key = "key_" + nextId.getAndIncrement();
                String value = "val_" + nextId.getAndIncrement();

                String keyMember = String.format("%s %s",
                                                 helper.getFieldType(pMap.keyDescriptor()),
                                                 key);
                String valueMember = String.format("%s %s",
                                                 helper.getFieldType(pMap.itemDescriptor()),
                                                 value);

                appendReadFieldValue(keyMember, null, null, pMap.keyDescriptor());
                appendReadFieldValue(valueMember, null, null, pMap.itemDescriptor());

                writer.formatln("%s.put(%s, %s);", member, key, value);

                writer.end()  // for len
                      .appendln('}');

                writer.end()  // if keyType && valueType
                      .appendln("} else {")
                      .formatln("    throw new %s(", SerializerException.class.getName())
                      .formatln("            \"Wrong key type \" + %s.asString(%s) +",
                                BinaryType.class.getName(),
                                keyType)
                      .formatln("            \" or value type \" + %s.asString(%s) +",
                                BinaryType.class.getName(),
                                valueType)
                      .formatln("            \" for %s.%s, should be %s and %s\");",
                                message.descriptor().getQualifiedName(),
                                field.name(),
                                BinaryType.asString(forType(pMap.keyDescriptor().getType())),
                                BinaryType.asString(forType(pMap.itemDescriptor().getType())))
                      .appendln('}');
                break;
            }
            case LIST:
            case SET: {
                if (field == null) {
                    throw new GeneratorException("Impossible!");
                }
                PContainer<?> pCont = (PContainer<?>) descriptor;
                if (pCont.itemDescriptor() instanceof PContainer) {
                    // If the container contains a container this code will
                    // break. Using the reader library in that case.
                    writer.formatln("%s.addAll((%s) %s.readFieldValue(reader, new %s(field, type), _Field.%s.getDescriptor(), strict));",
                                    member,
                                    field.fieldType(),
                                    BinaryFormatUtils.class.getName(),
                                    BinaryFormatUtils.FieldInfo.class.getName().replaceAll("\\$", "."),
                                    field.fieldEnum(),
                                    helper.getFieldType(descriptor));
                    break;
                }

                String len = "len_" + nextId.getAndIncrement();
                String itemType = "t_" + nextId.getAndIncrement();
                writer.formatln("byte %s = reader.expectByte();", itemType)
                      .formatln("if (%s == %d) {",
                                itemType, forType(pCont.itemDescriptor().getType()))
                      .begin()
                      .formatln("final int %s = reader.expectUInt32();", len);

                String i = "i_" + nextId.getAndIncrement();

                writer.formatln("for (int %s = 0; %s < %s; ++%s) {",
                                i, i, len, i)
                      .begin();

                String item = "key_" + nextId.getAndIncrement();

                String itemMember = String.format("%s %s",
                                                 helper.getFieldType(pCont.itemDescriptor()),
                                                 item);

                appendReadFieldValue(itemMember, null, null, pCont.itemDescriptor());

                writer.formatln("%s.add(%s);", member, item);

                writer.end()  // for len
                      .appendln('}');

                writer.end()  // if itemType
                      .appendln("} else {")
                      .formatln("    throw new %s(\"Wrong item type \" + %s.asString(%s) + \" for %s.%s, should be %s\");",
                                SerializerException.class.getName(),
                                BinaryType.class.getName(),
                                itemType,
                                message.descriptor().getQualifiedName(),
                                field.name(),
                                BinaryType.asString(forType(pCont.itemDescriptor().getType())))
                      .appendln('}');
                break;
            }
            case MESSAGE: {
                // Since the referred class may not implement the BinaryWriter interface (yet)
                writer.formatln("%s = %s.readMessage(reader, %s.kDescriptor, strict);",
                                member,
                                BinaryFormatUtils.class.getName(),
                                helper.getFieldType(descriptor));
                break;
            }
            default:
                throw new GeneratorException("Unsupported binary writer type: " + descriptor.getQualifiedName());
        }

    }

    @Override
    public void appendMethods(JMessage<?> message) {
        writer.appendln("@Override")
              .formatln("public void readBinary(%s reader, boolean strict) throws %s {",
                        BigEndianBinaryReader.class.getName(),
                        IOException.class.getName())
              .begin();

        writer.appendln("byte type = reader.expectByte();")
              .formatln("while (type != %d) {", BinaryType.STOP)
              .begin();

        writer.appendln("int field = reader.expectShort();")
              .appendln("switch (field) {")
              .begin();

        for (JField field : message.numericalOrderFields()) {
            writer.formatln("case %d: {", field.id())
                  .begin();

            writer.formatln("if (type == %d) {", forType(field.type()))
                  .begin();

            appendReadFieldValue(field.member(),
                                 message,
                                 field,
                                 field.field()
                                      .getDescriptor());

            if (message.isUnion()) {
                writer.formatln("%s = _Field.%s;", UNION_FIELD, field.fieldEnum());
            } else {
                writer.formatln("optionals.set(%d);", field.index());
            }
            writer.end()  // if type
                  .appendln("} else {")
                  .formatln("    throw new %s(\"Wrong type \" + %s.asString(type) + \" for %s.%s, should be %s\");",
                            SerializerException.class.getName(),
                            BinaryType.class.getName(),
                            message.descriptor().getQualifiedName(),
                            field.name(),
                            BinaryType.asString(forType(message.descriptor().getType())))
                  .appendln('}');

            writer.appendln("break;")
                  .end()  // case
                  .appendln('}');
        }

        writer.appendln("default: {")
              .appendln("    if (strict) {")
              .formatln("        throw new %s(\"No field with id \" + field + \" exists in %s\");",
                        SerializerException.class.getName(),
                        message.descriptor().getQualifiedName())
              .appendln("    } else {")
              .formatln("        %s.readFieldValue(reader, new %s(field, type), null, false);",
                        BinaryFormatUtils.class.getName(),
                        BinaryFormatUtils.FieldInfo.class.getName().replaceAll("\\$", "."))
              .appendln("    }")
              .appendln("    break;")
              .appendln('}');

        writer.end()  // switch
              .appendln('}');

        writer.appendln("type = reader.expectByte();")
              .end()  // while
              .appendln("}");

        writer.end()  // readBinary
              .appendln('}')
              .newline();
    }
}
