package net.morimekta.providence.config;

import net.morimekta.providence.PEnumValue;
import net.morimekta.util.Binary;
import net.morimekta.util.Strings;

import java.io.File;
import java.util.Objects;

import static net.morimekta.config.util.ConfigUtil.asString;

/**
 * A parameter parsed from
 */
public class ProvidenceConfigParam {
    public final String name;
    public final Object value;
    public final File   file;

    ProvidenceConfigParam(String name, Object value, File file) {
        this.name = name;
        this.value = value;
        this.file = file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ProvidenceConfigParam.class, name, value, file);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }
        ProvidenceConfigParam p = (ProvidenceConfigParam) o;

        return Objects.equals(name, p.name) && Objects.equals(value, p.value) && Objects.equals(file, p.file);
    }

    @Override
    public String toString() {
        if (value == null) {
            return String.format("%s = null (%s)", name, file.getName());
        } else if (value instanceof Binary) {
            return String.format("%s = b64(%s) (%s)", name, ((Binary) value).toBase64(), file.getName());
        } else if (value instanceof PEnumValue) {
            return String.format("%s = %s.%s (%s)",
                                 name,
                                 ((PEnumValue) value).descriptor()
                                                     .getQualifiedName(),
                                 asString(value),
                                 file.getName());
        } else if (value instanceof CharSequence) {
            return String.format("%s = \"%s\" (%s)", name, Strings.escape((CharSequence) value), file.getName());
        } else {
            return String.format("%s = %s (%s)", name, asString(value), file.getName());
        }
    }
}
