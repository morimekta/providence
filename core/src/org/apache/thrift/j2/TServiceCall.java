package org.apache.thrift.j2;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.j2.descriptor.TDescriptor;
import org.apache.thrift.j2.descriptor.TDescriptorProvider;
import org.apache.thrift.j2.descriptor.TField;
import org.apache.thrift.j2.descriptor.TFieldInfo;
import org.apache.thrift.j2.descriptor.TPrimitive;
import org.apache.thrift.j2.descriptor.TServiceMethod;
import org.apache.thrift.j2.descriptor.TStructDescriptor;

/**
 * @author Stein Eldar Johnsen
 * @since 24.10.15.
 */
public class TServiceCall<MSG> implements TMessage<TServiceCall<MSG>> {
    private final String           mName;
    private final Byte             mType;
    private final Integer          mSeqNo;
    private final MSG              mMessage;
    private final TDescriptor<MSG> mMessageDescriptor;

    private TServiceCall(_Builder<MSG> builder) {
        mName = builder.mName;
        mType = builder.mType;
        mSeqNo = builder.mSeqNo;
        mMessage = builder.mMessage;
        mMessageDescriptor = builder.mMessageDescriptor;
    }

    public TDescriptor<MSG> getMessageDescriptor() {
        return mMessageDescriptor;
    }

    public boolean hasName() {
        return mName != null;
    }

    public String getName() {
        return mName;
    }

    public boolean hasType() {
        return mType != null;
    }

    public byte getType() {
        return hasType() ? mType : 0;
    }

    public boolean hasSeqNo() {
        return mSeqNo != null;
    }

    public int getSeqNo() {
        return hasSeqNo() ? mSeqNo : 0;
    }

    public boolean hasMessage() {
        return mMessage != null;
    }

    public MSG getMessage() {
        return mMessage;
    }

    @Override
    public boolean has(int key) {
        switch (key) {
            case 1: return hasName();
            case 2: return hasType();
            case 3: return hasSeqNo();
            case 4: return hasMessage();
        }
        return false;
    }

    @Override
    public int num(int key) {
        switch (key) {
            case 1: return hasName() ? 1 : 0;
            case 2: return hasType() ? 1 : 0;
            case 3: return hasSeqNo() ? 1 : 0;
            case 4: return hasMessage() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public Object get(int key) {
        switch (key) {
            case 1: return getName();
            case 2: return getType();
            case 3: return getSeqNo();
            case 4: return getMessage();
        }
        return null;
    }

    @Override
    public TMessageBuilder<TServiceCall<MSG>> mutate() {
        return new _Builder<>(this);
    }

    @Override
    public boolean isValid() {
        return mName != null &&
               mType != null &&
               mSeqNo != null &&
               mMessage != null;
    }

    @Override
    public boolean isCompact() {
        return isValid();
    }

    @Override
    public TStructDescriptor<TServiceCall<MSG>> descriptor() {
        return null;
    }

    public static class _Builder<MSG> extends TMessageBuilder<TServiceCall<MSG>> {
        private final TDescriptor<MSG> mMessageDescriptor;

        private String  mName;
        private Byte    mType;
        private Integer mSeqNo;
        private MSG     mMessage;

        public _Builder(TDescriptor<MSG> descriptor) {
            mMessageDescriptor = descriptor;
        }

        public _Builder(TServiceCall<MSG> base) {
            mName = base.mName;
            mType = base.mType;
            mSeqNo = base.mSeqNo;
            mMessage = base.mMessage;
            mMessageDescriptor = base.mMessageDescriptor;
        }

        public TDescriptor<MSG> getMessageDescriptor() {
            return mMessageDescriptor;
        }

        public _Builder<MSG> setName(String value) {
            mName = value;
            return this;
        }

        public _Builder<MSG> clearName() {
            mName = null;
            return this;
        }

        public _Builder<MSG> setType(byte value) {
            mType = value;
            return this;
        }

        public _Builder<MSG> clearType() {
            mType = null;
            return this;
        }

        public _Builder<MSG> setSeqNo(int value) {
            mSeqNo = value;
            return this;
        }

        public _Builder<MSG> clearSeqNo() {
            mSeqNo = null;
            return this;
        }

        public _Builder<MSG> setMessage(MSG value) {
            mMessage = value;
            return this;
        }

        public _Builder<MSG> clearMessage() {
            mMessage = null;
            return this;
        }

        @Override
        public boolean isValid() {
            return mName != null &&
                   mType != null &&
                   mSeqNo != null &&
                   mMessage != null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public TMessageBuilder<TServiceCall<MSG>> set(int key, Object value) {
            switch (key) {
                case 1:
                    setName((String) value);
                    break;
                case 2:
                    setType((Byte) value);
                    break;
                case 3:
                    setSeqNo((Integer) value);
                    break;
                case 4:
                    setMessage((MSG) value);
                    break;
            }
            return this;
        }

        @Override
        public TServiceCall<MSG> build() {
            return new TServiceCall<>(this);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMessage<T>>
    TStructDescriptor<TServiceCall<T>> callProvider(TService service,
                                                    TServiceMethod method) {
        return new TStructDescriptor<>(method.getComment(),
                                       service.getServiceType().getPackageName(),
                                       service.getServiceType().getName() + '.' + method.getName(),
                                       makeFieldSpec(method.getParamsDescriptor()),
                                       builderFactory(method.getParamsDescriptor()),
                                       true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMessage<T>>
    TStructDescriptor<TServiceCall<T>> responseProvider(TService service,
                                                        TServiceMethod method) {
        return new TStructDescriptor<>(method.getComment(),
                                       service.getServiceType().getPackageName(),
                                       service.getServiceType().getName() + '.' + method.getName(),
                                       makeFieldSpec(method.getReturnType()),
                                       builderFactory(method.getReturnType()),
                                       true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TMessage<T>>
    TStructDescriptor<TServiceCall<T>> throwsProvider(TService service,
                                                      TServiceMethod method) {
        return new TStructDescriptor<>(method.getComment(),
                                       service.getServiceType().getPackageName(),
                                       service.getServiceType().getName() + '.' + method.getName(),
                                       makeFieldSpec(method.getExceptionDescriptor()),
                                       builderFactory(method.getExceptionDescriptor()),
                                       true);
    }

    private static <T extends TMessage<T>>
    List<TField<?>> makeFieldSpec(TDescriptor<T> message) {
        List<TField<?>> fields = new LinkedList<>();
        fields.add(new TFieldInfo<>(null, 1, true, "name", TPrimitive.STRING.provider(), null));
        fields.add(new TFieldInfo<>(null, 2, true, "type", TPrimitive.BYTE.provider(), null));
        fields.add(new TFieldInfo<>(null, 3, true, "seq_id", TPrimitive.I32.provider(), null));
        fields.add(new TFieldInfo<>(null, 4, true, "msg", messageProvider(message), null));
        return fields;
    }

    private static <T>
    TDescriptorProvider<T> messageProvider(final TDescriptor<T> descriptor) {
        return new TDescriptorProvider<T>() {
            @Override
            public TDescriptor<T> descriptor() {
                return descriptor;
            }
        };
    }

    private static <T extends TMessage<T>>
    TMessageBuilderFactory<TServiceCall<T>> builderFactory(final TDescriptor<T> descriptor) {
        return new TMessageBuilderFactory<TServiceCall<T>>() {
            @Override
            public TMessageBuilder<TServiceCall<T>> builder() {
                return new _Builder<>(descriptor);
            }
        };
    }
}
