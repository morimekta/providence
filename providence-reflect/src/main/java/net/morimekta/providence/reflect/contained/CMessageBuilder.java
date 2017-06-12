package net.morimekta.providence.reflect.contained;

import net.morimekta.providence.PMessage;
import net.morimekta.providence.PMessageBuilder;
import net.morimekta.providence.PType;
import net.morimekta.providence.descriptor.PField;
import net.morimekta.providence.descriptor.PMessageDescriptor;
import net.morimekta.providence.descriptor.PRequirement;
import net.morimekta.providence.reflect.util.ThriftAnnotation;
import net.morimekta.providence.reflect.util.ThriftCollection;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
            int key = field.getId();
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
                            ((Set<Object>) values.get(key)).addAll((Collection<Object>) from.get(key));
                        } else {
                            set(key, from.get(key));
                        }
                        break;
                    case MAP:
                        if (values.containsKey(key)) {
                            ((Map<Object, Object>) values.get(key)).putAll((Map<Object, Object>) from.get(key));
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
        CField field = descriptor().findFieldById(key);
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
                if (!values.containsKey(field.getId())) {
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
                if (!values.containsKey(field.getId())) {
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
        CField field = descriptor().findFieldById(key);
        if (field == null) {
            return (Builder) this; // soft ignoring unsupported fields.
        }
        if (value == null) {
            values.remove(key);
        } else {
            switch (field.getType()) {
                case LIST: {
                    values.put(key, ImmutableList.copyOf((Collection<Object>) value));
                    break;
                }
                case SET: {
                    values.put(key, new LinkedHashSet<>((Set) value));
                    break;
                }
                case MAP: {
                    values.put(key, new LinkedHashMap<>((Map) value));
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
        CField field = descriptor().findFieldById(key);
        if (field == null) {
            return (Builder) this; // soft ignoring unsupported fields.
        }
        if (value == null) {
            throw new IllegalArgumentException("Adding null value");
        }
        if (field.getType() == PType.LIST) {
            List<Object> list = (List<Object>) values.get(field.getId());
            if (list == null) {
                list = new LinkedList<>();
                values.put(field.getId(), list);
            }
            list.add(value);
        } else if (field.getType() == PType.SET) {
            ThriftCollection ctype = ThriftCollection.forName(field.getAnnotationValue(ThriftAnnotation.CONTAINER));

            Set<Object> set = (Set<Object>) values.get(field.getId());
            if (set == null) {
                set = new LinkedHashSet<>();
                values.put(field.getId(), set);
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
            int key = field.getId();
            if (values.containsKey(key)) {
                switch (field.getType()) {
                    case LIST:
                        out.put(key, ImmutableList.copyOf((List<Object>) values.get(key)));
                        break;
                    case SET: {
                        ThriftCollection ctype = ThriftCollection.forName(field.getAnnotationValue(ThriftAnnotation.CONTAINER));
                        switch (ctype) {
                            case SORTED:
                                out.put(key, ImmutableSortedSet.copyOf((Set) values.get(key)));
                                break;
                            default:
                                out.put(key, ImmutableSet.copyOf((Set) values.get(key)));
                                break;
                        }
                        break;
                    }
                    case MAP: {
                        ThriftCollection ctype = ThriftCollection.forName(field.getAnnotationValue(ThriftAnnotation.CONTAINER));
                        switch (ctype) {
                            case SORTED:
                                out.put(key, ImmutableSortedMap.copyOf((Map) values.get(key)));
                                break;
                            default:
                                out.put(key, ImmutableMap.copyOf((Map) values.get(key)));
                                break;
                        }
                        break;
                    }
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
