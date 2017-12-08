package net.morimekta.providence.generator.format.js.utils;

import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PContainer;
import net.morimekta.providence.descriptor.PDeclaredDescriptor;
import net.morimekta.providence.descriptor.PDescriptor;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.generator.format.js.JSOptions;
import net.morimekta.providence.reflect.contained.CField;

import javax.annotation.Nonnull;

import static net.morimekta.providence.generator.format.js.utils.JSUtils.alwaysPresent;
import static net.morimekta.providence.generator.format.js.utils.JSUtils.getClassName;

/**
 * Utilities for closure.
 */
public class ClosureUtils {
    public static String getFieldType(@Nonnull CField field, JSOptions options) {
        return getTypeString(field.getDescriptor(), options) + getFieldOptionality(field);
    }

    public static String getTypeString(@Nonnull PDescriptor descriptor, JSOptions options) {
        switch (descriptor.getType()) {
            case VOID:
            case BOOL:
                return "boolean";
            case BYTE:
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                return "number";
            case BINARY:
            case STRING:
                return "string";
            case ENUM:
            case MESSAGE:
                return descriptor.getProgramName() + "." + getClassName((PDeclaredDescriptor) descriptor);
            case LIST:
            case SET:
                PContainer container = (PContainer) descriptor;
                return "Array<" + getTypeString(container.itemDescriptor(), options) + ">";
            case MAP:
                if (!options.useMaps()) {
                    // closure objects don't have inner typing.
                    return "Object";
                }
                PMap map = (PMap) descriptor;
                String keyDesc = getTypeString(map.keyDescriptor(), options);
                if (map.keyDescriptor().getType() == PType.MESSAGE) {
                    // TODO: Make better workaround!
                    // Messages use the compact JSON string version for the key.
                    // es51 does not support objects as keys, as all object instances
                    // ar non-equal.
                    keyDesc = "string";
                }
                return "Map<" + keyDesc + "," + getTypeString(map.itemDescriptor(), options) + ">";
            default:
                throw new IllegalArgumentException("Unhandled type: " + descriptor.getType());
        }
    }

    private static String getFieldOptionality(@Nonnull CField field) {
        if (!alwaysPresent(field)) {
            return "?";
        }
        return "";
    }
}
