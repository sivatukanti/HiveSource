// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import java.util.ArrayList;
import java.util.List;
import parquet.Preconditions;

public class Types
{
    private static final int NOT_SET = 0;
    
    public static MessageTypeBuilder buildMessage() {
        return new MessageTypeBuilder();
    }
    
    public static GroupBuilder<GroupType> buildGroup(final Type.Repetition repetition) {
        return new GroupBuilder((Class)GroupType.class).repetition(repetition);
    }
    
    public static GroupBuilder<GroupType> requiredGroup() {
        return new GroupBuilder((Class)GroupType.class).repetition(Type.Repetition.REQUIRED);
    }
    
    public static GroupBuilder<GroupType> optionalGroup() {
        return new GroupBuilder((Class)GroupType.class).repetition(Type.Repetition.OPTIONAL);
    }
    
    public static GroupBuilder<GroupType> repeatedGroup() {
        return new GroupBuilder((Class)GroupType.class).repetition(Type.Repetition.REPEATED);
    }
    
    public static PrimitiveBuilder<PrimitiveType> primitive(final PrimitiveType.PrimitiveTypeName type, final Type.Repetition repetition) {
        return new PrimitiveBuilder((Class)PrimitiveType.class, type).repetition(repetition);
    }
    
    public static PrimitiveBuilder<PrimitiveType> required(final PrimitiveType.PrimitiveTypeName type) {
        return new PrimitiveBuilder((Class)PrimitiveType.class, type).repetition(Type.Repetition.REQUIRED);
    }
    
    public static PrimitiveBuilder<PrimitiveType> optional(final PrimitiveType.PrimitiveTypeName type) {
        return new PrimitiveBuilder((Class)PrimitiveType.class, type).repetition(Type.Repetition.OPTIONAL);
    }
    
    public static PrimitiveBuilder<PrimitiveType> repeated(final PrimitiveType.PrimitiveTypeName type) {
        return new PrimitiveBuilder((Class)PrimitiveType.class, type).repetition(Type.Repetition.REPEATED);
    }
    
    public abstract static class Builder<T extends Builder, P>
    {
        protected final P parent;
        protected final Class<? extends P> returnClass;
        protected Type.Repetition repetition;
        protected OriginalType originalType;
        protected Type.ID id;
        private boolean repetitionAlreadySet;
        
        protected Builder(final P parent) {
            this.repetition = null;
            this.originalType = null;
            this.id = null;
            this.repetitionAlreadySet = false;
            Preconditions.checkNotNull(parent, "Parent cannot be null");
            this.parent = parent;
            this.returnClass = null;
        }
        
        protected Builder(final Class<P> returnClass) {
            this.repetition = null;
            this.originalType = null;
            this.id = null;
            this.repetitionAlreadySet = false;
            Preconditions.checkArgument(Type.class.isAssignableFrom(returnClass), "The requested return class must extend Type");
            this.returnClass = (Class<? extends P>)returnClass;
            this.parent = null;
        }
        
        protected abstract T self();
        
        protected final T repetition(final Type.Repetition repetition) {
            Preconditions.checkArgument(!this.repetitionAlreadySet, "Repetition has already been set");
            Preconditions.checkNotNull(repetition, "Repetition cannot be null");
            this.repetition = repetition;
            this.repetitionAlreadySet = true;
            return this.self();
        }
        
        public T as(final OriginalType type) {
            this.originalType = type;
            return this.self();
        }
        
        public T id(final int id) {
            this.id = new Type.ID(id);
            return this.self();
        }
        
        protected abstract Type build(final String p0);
        
        public P named(final String name) {
            Preconditions.checkNotNull(name, "Name is required");
            Preconditions.checkNotNull(this.repetition, "Repetition is required");
            final Type type = this.build(name);
            if (this.parent != null) {
                if (GroupBuilder.class.isAssignableFrom(this.parent.getClass())) {
                    GroupBuilder.class.cast(this.parent).addField(type);
                }
                return this.parent;
            }
            return (P)this.returnClass.cast(type);
        }
    }
    
    public static class PrimitiveBuilder<P> extends Builder<PrimitiveBuilder<P>, P>
    {
        private static final long MAX_PRECISION_INT32;
        private static final long MAX_PRECISION_INT64;
        private final PrimitiveType.PrimitiveTypeName primitiveType;
        private int length;
        private int precision;
        private int scale;
        
        private PrimitiveBuilder(final P parent, final PrimitiveType.PrimitiveTypeName type) {
            super(parent);
            this.length = 0;
            this.precision = 0;
            this.scale = 0;
            this.primitiveType = type;
        }
        
        private PrimitiveBuilder(final Class<P> returnType, final PrimitiveType.PrimitiveTypeName type) {
            super(returnType);
            this.length = 0;
            this.precision = 0;
            this.scale = 0;
            this.primitiveType = type;
        }
        
        @Override
        protected PrimitiveBuilder<P> self() {
            return this;
        }
        
        public PrimitiveBuilder<P> length(final int length) {
            this.length = length;
            return this;
        }
        
        public PrimitiveBuilder<P> precision(final int precision) {
            this.precision = precision;
            return this;
        }
        
        public PrimitiveBuilder<P> scale(final int scale) {
            this.scale = scale;
            return this;
        }
        
        @Override
        protected PrimitiveType build(final String name) {
            if (PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY == this.primitiveType) {
                Preconditions.checkArgument(this.length > 0, "Invalid FIXED_LEN_BYTE_ARRAY length: " + this.length);
            }
            final DecimalMetadata meta = this.decimalMetadata();
            if (this.originalType != null) {
                switch (this.originalType) {
                    case UTF8:
                    case JSON:
                    case BSON: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.BINARY, this.originalType.toString() + " can only annotate binary fields");
                        break;
                    }
                    case DECIMAL: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.INT32 || this.primitiveType == PrimitiveType.PrimitiveTypeName.INT64 || this.primitiveType == PrimitiveType.PrimitiveTypeName.BINARY || this.primitiveType == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY, "DECIMAL can only annotate INT32, INT64, BINARY, and FIXED");
                        if (this.primitiveType == PrimitiveType.PrimitiveTypeName.INT32) {
                            Preconditions.checkState(meta.getPrecision() <= PrimitiveBuilder.MAX_PRECISION_INT32, "INT32 cannot store " + meta.getPrecision() + " digits " + "(max " + PrimitiveBuilder.MAX_PRECISION_INT32 + ")");
                            break;
                        }
                        if (this.primitiveType == PrimitiveType.PrimitiveTypeName.INT64) {
                            Preconditions.checkState(meta.getPrecision() <= PrimitiveBuilder.MAX_PRECISION_INT64, "INT64 cannot store " + meta.getPrecision() + " digits " + "(max " + PrimitiveBuilder.MAX_PRECISION_INT64 + ")");
                            break;
                        }
                        if (this.primitiveType == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY) {
                            Preconditions.checkState(meta.getPrecision() <= maxPrecision(this.length), "FIXED(" + this.length + ") cannot store " + meta.getPrecision() + " digits (max " + maxPrecision(this.length) + ")");
                            break;
                        }
                        break;
                    }
                    case DATE:
                    case TIME_MILLIS:
                    case UINT_8:
                    case UINT_16:
                    case UINT_32:
                    case INT_8:
                    case INT_16:
                    case INT_32: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.INT32, this.originalType.toString() + " can only annotate INT32");
                        break;
                    }
                    case TIMESTAMP_MILLIS:
                    case UINT_64:
                    case INT_64: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.INT64, this.originalType.toString() + " can only annotate INT64");
                        break;
                    }
                    case INTERVAL: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY && this.length == 12, "INTERVAL can only annotate FIXED_LEN_BYTE_ARRAY(12)");
                        break;
                    }
                    case ENUM: {
                        Preconditions.checkState(this.primitiveType == PrimitiveType.PrimitiveTypeName.BINARY, "ENUM can only annotate binary fields");
                        break;
                    }
                    default: {
                        throw new IllegalStateException(this.originalType + " can not be applied to a primitive type");
                    }
                }
            }
            return new PrimitiveType(this.repetition, this.primitiveType, this.length, name, this.originalType, meta, this.id);
        }
        
        private static long maxPrecision(final int numBytes) {
            return Math.round(Math.floor(Math.log10(Math.pow(2.0, 8 * numBytes - 1) - 1.0)));
        }
        
        protected DecimalMetadata decimalMetadata() {
            DecimalMetadata meta = null;
            if (OriginalType.DECIMAL == this.originalType) {
                Preconditions.checkArgument(this.precision > 0, "Invalid DECIMAL precision: " + this.precision);
                Preconditions.checkArgument(this.scale >= 0, "Invalid DECIMAL scale: " + this.scale);
                Preconditions.checkArgument(this.scale <= this.precision, "Invalid DECIMAL scale: cannot be greater than precision");
                meta = new DecimalMetadata(this.precision, this.scale);
            }
            return meta;
        }
        
        static {
            MAX_PRECISION_INT32 = maxPrecision(4);
            MAX_PRECISION_INT64 = maxPrecision(8);
        }
    }
    
    public static class GroupBuilder<P> extends Builder<GroupBuilder<P>, P>
    {
        protected final List<Type> fields;
        
        private GroupBuilder(final P parent) {
            super(parent);
            this.fields = new ArrayList<Type>();
        }
        
        private GroupBuilder(final Class<P> returnType) {
            super(returnType);
            this.fields = new ArrayList<Type>();
        }
        
        @Override
        protected GroupBuilder<P> self() {
            return this;
        }
        
        public PrimitiveBuilder<GroupBuilder<P>> primitive(final PrimitiveType.PrimitiveTypeName type, final Type.Repetition repetition) {
            return new PrimitiveBuilder((Object)this, type).repetition(repetition);
        }
        
        public PrimitiveBuilder<GroupBuilder<P>> required(final PrimitiveType.PrimitiveTypeName type) {
            return new PrimitiveBuilder((Object)this, type).repetition(Type.Repetition.REQUIRED);
        }
        
        public PrimitiveBuilder<GroupBuilder<P>> optional(final PrimitiveType.PrimitiveTypeName type) {
            return new PrimitiveBuilder((Object)this, type).repetition(Type.Repetition.OPTIONAL);
        }
        
        public PrimitiveBuilder<GroupBuilder<P>> repeated(final PrimitiveType.PrimitiveTypeName type) {
            return new PrimitiveBuilder((Object)this, type).repetition(Type.Repetition.REPEATED);
        }
        
        public GroupBuilder<GroupBuilder<P>> group(final Type.Repetition repetition) {
            return (GroupBuilder<GroupBuilder<P>>)new GroupBuilder(this).repetition(repetition);
        }
        
        public GroupBuilder<GroupBuilder<P>> requiredGroup() {
            return (GroupBuilder<GroupBuilder<P>>)new GroupBuilder(this).repetition(Type.Repetition.REQUIRED);
        }
        
        public GroupBuilder<GroupBuilder<P>> optionalGroup() {
            return (GroupBuilder<GroupBuilder<P>>)new GroupBuilder(this).repetition(Type.Repetition.OPTIONAL);
        }
        
        public GroupBuilder<GroupBuilder<P>> repeatedGroup() {
            return (GroupBuilder<GroupBuilder<P>>)new GroupBuilder(this).repetition(Type.Repetition.REPEATED);
        }
        
        public GroupBuilder<P> addField(final Type type) {
            this.fields.add(type);
            return this;
        }
        
        public GroupBuilder<P> addFields(final Type... types) {
            for (final Type type : types) {
                this.fields.add(type);
            }
            return this;
        }
        
        @Override
        protected GroupType build(final String name) {
            Preconditions.checkState(!this.fields.isEmpty(), "Cannot build an empty group");
            return new GroupType(this.repetition, name, this.originalType, this.fields, this.id);
        }
    }
    
    public static class MessageTypeBuilder extends GroupBuilder<MessageType>
    {
        private MessageTypeBuilder() {
            super((Class)MessageType.class);
            this.repetition(Type.Repetition.REQUIRED);
        }
        
        @Override
        public MessageType named(final String name) {
            Preconditions.checkNotNull(name, "Name is required");
            return new MessageType(name, this.fields);
        }
    }
}
