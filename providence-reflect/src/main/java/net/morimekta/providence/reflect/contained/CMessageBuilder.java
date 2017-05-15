package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PList;
import net.morimekta.providence.descriptor.PMap;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.descriptor.PSet;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Base message builder class for contained messages.
 */
public abstract class CMessageBuilder<Builder extends CMessageBuilder<Builder, Message>,
                                      Message extends PMessage<Message, CField>>
        extends PMessageBuilder<Message, CField> {
    private final Map<Integer, Object> values;
    private final Set<Integer>         modified;

    public CMessageBuilder() {
        this.values = new TreeMap<>();
        this.modified = new TreeSet<>();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Builder merge(Message from) {
        for (PField field : descriptor().getFields()) {
            int key = field.getKey();
            if (from.has(key)) {
                switch (field.getType()) {
                    case MESSAGE:
                        if (values.containsKey(key)) {
                            mutator(key).merge((PMessage) from.get(key));
                        } else {
                            set(key, from.get(key));
                        }
                        break;
                    case SET:
                        if (values.containsKey(key)) {
                            ((PSet.Builder<Object>) values.get(key)).addAll((Collection<Object>) from.get(key));
                        } else {
                            set(key, from.get(key));
                        }
                        break;
                    case MAP:
                        if (values.containsKey(key)) {
                            ((PMap.Builder<Object, Object>) values.get(key)).putAll((Map<Object, Object>) from.get(key));
                        } else {
                            set(key, from.get(key));
                        }
                        break;
                    default:
                        set(key, from.get(key));
                        break;
                }
                modified.add(key);
            }
        }

        return (Builder) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public PMessageBuilder mutator(int key) {
        CField field = descriptor().getField(key);
        if (field == null) {
            throw new IllegalArgumentException("No such field ID " + key);
        } else if (field.getType() != PType.MESSAGE) {
            throw new IllegalArgumentException("Not a message field ID " + key + ": " + field.getName());
        }

        Object current = values.get(key);
        if (current == null) {
            current = ((PMessageDescriptor) field.getDescriptor()).builder();
            values.put(key, current);
        } else if (current instanceof PMessage) {
            current = ((PMessage) current).mutate();
            values.put(key, current);
        } else if (!(current instanceof PMessageBuilder)) {
            // This should in theory not be possible. This is just a safe-guard.
            throw new IllegalArgumentException("Invalid value in map on message type: " + current.getClass().getSimpleName());
        }
        modified.add(key);

        return (PMessageBuilder) current;
    }

    @Override
    public boolean valid() {
        for (PField field : descriptor().getFields()) {
            if (field.getRequirement() == PRequirement.REQUIRED) {
                if (!values.containsKey(field.getKey())) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void validate() {
        LinkedList<String> missing = new LinkedList<>();
        for (PField field : descriptor().getFields()) {
            if (field.getRequirement() == PRequirement.REQUIRED) {
                if (!values.containsKey(field.getKey())) {
                    missing.add(field.getName());
                }
            }
        }

        if (missing.size() > 0) {
            throw new IllegalStateException(
                    "Missing required fields " +
                    String.join(",", missing) +
                    " in message " + descriptor().getQualifiedName());
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Builder set(int key, Object value) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return (Builder) this; // soft ignoring unsupported fields.
        }
        if (value == null) {
            values.remove(key);
        } else {
            switch (field.getType()) {
                case LIST: {
                    PList.Builder builder = ((PList) field.getDescriptor()).builder();
                    builder.addAll((Collection<Object>) value);
                    values.put(key, builder);
                    break;
                }
                case SET: {
                    PSet.Builder builder = ((PSet) field.getDescriptor()).builder();
                    builder.addAll((Collection<Object>) value);
                    values.put(key, builder);
                    break;
                }
                case MAP: {
                    PMap.Builder builder = ((PMap) field.getDescriptor()).builder();
                    builder.putAll((Map<Object, Object>) value);
                    values.put(key, builder);
                    break;
                }
                default:
                    values.put(key, value);
                    break;
            }
        }

        modified.add(key);
        return (Builder) this;
    }

    @Override
    public boolean isSet(int key) {
        return values.containsKey(key);
    }

    @Override
    public boolean isModified(int key) {
        return modified.contains(key);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Builder addTo(int key, Object value) {
        PField field = descriptor().getField(key);
        if (field == null) {
            return (Builder) this; // soft ignoring unsupported fields.
        }
        if (value == null) {
            throw new IllegalArgumentException("Adding null value");
        }
        if (field.getType() == PType.LIST) {
            @SuppressWarnings("unchecked")
            PList.Builder<Object> list = (PList.Builder<Object>) values.get(field.getKey());
            if (list == null) {
                list = ((PList) field.getDescriptor()).builder();
                values.put(field.getKey(), list);
            }
            list.add(value);
        } else if (field.getType() == PType.SET) {
            @SuppressWarnings("unchecked")
            PSet.Builder<Object> set = (PSet.Builder<Object>) values.get(field.getKey());
            if (set == null) {
                set = ((PSet) field.getDescriptor()).builder();
                values.put(field.getKey(), set);
            }
            set.add(value);
        } else {
            throw new IllegalArgumentException("Key " + key + " is not a collection: " + field.getType());
        }
        modified.add(key);
        return (Builder) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public Builder clear(int key) {
        values.remove(key);
        modified.add(key);
        return (Builder) this;
    }

    @SuppressWarnings("unchecked")
    Map<Integer, Object> getValueMap() {
        ImmutableMap.Builder<Integer, Object> out = ImmutableMap.builder();
        for (CField field : descriptor().getFields()) {
            int key = field.getKey();
            if (values.containsKey(key)) {
                switch (field.getType()) {
                    case SET:
                        out.put(key, ((PSet.Builder<Object>) values.get(key)).build());
                        break;
                    case LIST:
                        out.put(key, ((PList.Builder<Object>) values.get(key)).build());
                        break;
                    case MAP:
                        out.put(key, ((PMap.Builder<Object, Object>) values.get(key)).build());
                        break;
                    case MESSAGE:
                        Object current = values.get(key);
                        if (current instanceof PMessageBuilder) {
                            out.put(key, ((PMessageBuilder) current).build());
                        } else {
                            out.put(key, current);
                        }
                        break;
                    default:
                        out.put(key, values.get(key));
                        break;
                }
            } else if (field.getRequirement() != PRequirement.OPTIONAL) {
                if (field.hasDefaultValue()) {
                    out.put(key, field.getDefaultValue());
                } else if (field.getDescriptor().getDefaultValue() != null) {
                    out.put(key, field.getDescriptor().getDefaultValue());
                }
            }
        }
        return out.build();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                          .add("values", values)
                          .add("modified", modified)
                          .toString();
    }
}
