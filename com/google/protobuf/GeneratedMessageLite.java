// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.io.IOException;
import java.io.Serializable;

public abstract class GeneratedMessageLite extends AbstractMessageLite implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected GeneratedMessageLite() {
    }
    
    protected GeneratedMessageLite(final Builder builder) {
    }
    
    public Parser<? extends MessageLite> getParserForType() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }
    
    protected boolean parseUnknownField(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
        return input.skipField(tag);
    }
    
    protected void makeExtensionsImmutable() {
    }
    
    private static <MessageType extends MessageLite> boolean parseUnknownField(final FieldSet<ExtensionDescriptor> extensions, final MessageType defaultInstance, final CodedInputStream input, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
        final int wireType = WireFormat.getTagWireType(tag);
        final int fieldNumber = WireFormat.getTagFieldNumber(tag);
        final GeneratedExtension<MessageType, ?> extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, fieldNumber);
        boolean unknown = false;
        boolean packed = false;
        if (extension == null) {
            unknown = true;
        }
        else if (wireType == FieldSet.getWireFormatForFieldType(((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType(), false)) {
            packed = false;
        }
        else if (((GeneratedExtension<MessageLite, Object>)extension).descriptor.isRepeated && ((GeneratedExtension<MessageLite, Object>)extension).descriptor.type.isPackable() && wireType == FieldSet.getWireFormatForFieldType(((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType(), true)) {
            packed = true;
        }
        else {
            unknown = true;
        }
        if (unknown) {
            return input.skipField(tag);
        }
        if (packed) {
            final int length = input.readRawVarint32();
            final int limit = input.pushLimit(length);
            if (((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
                while (input.getBytesUntilLimit() > 0) {
                    final int rawValue = input.readEnum();
                    final Object value = ((GeneratedExtension<MessageLite, Object>)extension).descriptor.getEnumType().findValueByNumber(rawValue);
                    if (value == null) {
                        return true;
                    }
                    extensions.addRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value);
                }
            }
            else {
                while (input.getBytesUntilLimit() > 0) {
                    final Object value2 = FieldSet.readPrimitiveField(input, ((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType());
                    extensions.addRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value2);
                }
            }
            input.popLimit(limit);
        }
        else {
            Object value3 = null;
            switch (((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteJavaType()) {
                case MESSAGE: {
                    MessageLite.Builder subBuilder = null;
                    if (!((GeneratedExtension<MessageLite, Object>)extension).descriptor.isRepeated()) {
                        final MessageLite existingValue = (MessageLite)extensions.getField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
                        if (existingValue != null) {
                            subBuilder = existingValue.toBuilder();
                        }
                    }
                    if (subBuilder == null) {
                        subBuilder = ((GeneratedExtension<MessageLite, Object>)extension).messageDefaultInstance.newBuilderForType();
                    }
                    if (((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
                        input.readGroup(extension.getNumber(), subBuilder, extensionRegistry);
                    }
                    else {
                        input.readMessage(subBuilder, extensionRegistry);
                    }
                    value3 = subBuilder.build();
                    break;
                }
                case ENUM: {
                    final int rawValue2 = input.readEnum();
                    value3 = ((GeneratedExtension<MessageLite, Object>)extension).descriptor.getEnumType().findValueByNumber(rawValue2);
                    if (value3 == null) {
                        return true;
                    }
                    break;
                }
                default: {
                    value3 = FieldSet.readPrimitiveField(input, ((GeneratedExtension<MessageLite, Object>)extension).descriptor.getLiteType());
                    break;
                }
            }
            if (((GeneratedExtension<MessageLite, Object>)extension).descriptor.isRepeated()) {
                extensions.addRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value3);
            }
            else {
                extensions.setField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value3);
            }
        }
        return true;
    }
    
    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(final ContainingType containingTypeDefaultInstance, final Type defaultValue, final MessageLite messageDefaultInstance, final Internal.EnumLiteMap<?> enumTypeMap, final int number, final WireFormat.FieldType type) {
        return new GeneratedExtension<ContainingType, Type>((MessageLite)containingTypeDefaultInstance, (Object)defaultValue, messageDefaultInstance, new ExtensionDescriptor((Internal.EnumLiteMap)enumTypeMap, number, type, false, false));
    }
    
    public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(final ContainingType containingTypeDefaultInstance, final MessageLite messageDefaultInstance, final Internal.EnumLiteMap<?> enumTypeMap, final int number, final WireFormat.FieldType type, final boolean isPacked) {
        final Type emptyList = (Type)Collections.emptyList();
        return new GeneratedExtension<ContainingType, Type>((MessageLite)containingTypeDefaultInstance, (Object)emptyList, messageDefaultInstance, new ExtensionDescriptor((Internal.EnumLiteMap)enumTypeMap, number, type, true, isPacked));
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new SerializedForm(this);
    }
    
    public abstract static class Builder<MessageType extends GeneratedMessageLite, BuilderType extends Builder> extends AbstractMessageLite.Builder<BuilderType>
    {
        protected Builder() {
        }
        
        public BuilderType clear() {
            return (BuilderType)this;
        }
        
        @Override
        public BuilderType clone() {
            throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
        }
        
        public abstract BuilderType mergeFrom(final MessageType p0);
        
        public abstract MessageType getDefaultInstanceForType();
        
        protected boolean parseUnknownField(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            return input.skipField(tag);
        }
    }
    
    public abstract static class ExtendableMessage<MessageType extends ExtendableMessage<MessageType>> extends GeneratedMessageLite implements ExtendableMessageOrBuilder<MessageType>
    {
        private final FieldSet<ExtensionDescriptor> extensions;
        
        protected ExtendableMessage() {
            this.extensions = FieldSet.newFieldSet();
        }
        
        protected ExtendableMessage(final ExtendableBuilder<MessageType, ?> builder) {
            this.extensions = (FieldSet<ExtensionDescriptor>)((ExtendableBuilder<ExtendableMessage, ExtendableBuilder>)builder).buildExtensions();
        }
        
        private void verifyExtensionContainingType(final GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }
        
        public final <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
        }
        
        public final <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.getRepeatedFieldCount(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            final Object value = this.extensions.getField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
            if (value == null) {
                return (Type)((GeneratedExtension<MessageLite, Object>)extension).defaultValue;
            }
            return (Type)value;
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index) {
            this.verifyExtensionContainingType(extension);
            return (Type)this.extensions.getRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, index);
        }
        
        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }
        
        @Override
        protected boolean parseUnknownField(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            return parseUnknownField(this.extensions, this.getDefaultInstanceForType(), input, extensionRegistry, tag);
        }
        
        @Override
        protected void makeExtensionsImmutable() {
            this.extensions.makeImmutable();
        }
        
        protected ExtensionWriter newExtensionWriter() {
            return new ExtensionWriter(false);
        }
        
        protected ExtensionWriter newMessageSetExtensionWriter() {
            return new ExtensionWriter(true);
        }
        
        protected int extensionsSerializedSize() {
            return this.extensions.getSerializedSize();
        }
        
        protected int extensionsSerializedSizeAsMessageSet() {
            return this.extensions.getMessageSetSerializedSize();
        }
        
        protected class ExtensionWriter
        {
            private final Iterator<Map.Entry<ExtensionDescriptor, Object>> iter;
            private Map.Entry<ExtensionDescriptor, Object> next;
            private final boolean messageSetWireFormat;
            
            private ExtensionWriter(final boolean messageSetWireFormat) {
                this.iter = ExtendableMessage.this.extensions.iterator();
                if (this.iter.hasNext()) {
                    this.next = this.iter.next();
                }
                this.messageSetWireFormat = messageSetWireFormat;
            }
            
            public void writeUntil(final int end, final CodedOutputStream output) throws IOException {
                while (this.next != null && this.next.getKey().getNumber() < end) {
                    final ExtensionDescriptor extension = this.next.getKey();
                    if (this.messageSetWireFormat && extension.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !extension.isRepeated()) {
                        output.writeMessageSetExtension(extension.getNumber(), this.next.getValue());
                    }
                    else {
                        FieldSet.writeField(extension, this.next.getValue(), output);
                    }
                    if (this.iter.hasNext()) {
                        this.next = this.iter.next();
                    }
                    else {
                        this.next = null;
                    }
                }
            }
        }
    }
    
    public abstract static class ExtendableBuilder<MessageType extends ExtendableMessage<MessageType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends Builder<MessageType, BuilderType> implements ExtendableMessageOrBuilder<MessageType>
    {
        private FieldSet<ExtensionDescriptor> extensions;
        private boolean extensionsIsMutable;
        
        protected ExtendableBuilder() {
            this.extensions = FieldSet.emptySet();
        }
        
        @Override
        public BuilderType clear() {
            this.extensions.clear();
            this.extensionsIsMutable = false;
            return super.clear();
        }
        
        private void ensureExtensionsIsMutable() {
            if (!this.extensionsIsMutable) {
                this.extensions = this.extensions.clone();
                this.extensionsIsMutable = true;
            }
        }
        
        private FieldSet<ExtensionDescriptor> buildExtensions() {
            this.extensions.makeImmutable();
            this.extensionsIsMutable = false;
            return this.extensions;
        }
        
        private void verifyExtensionContainingType(final GeneratedExtension<MessageType, ?> extension) {
            if (extension.getContainingTypeDefaultInstance() != this.getDefaultInstanceForType()) {
                throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings.");
            }
        }
        
        public final <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
        }
        
        public final <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.getRepeatedFieldCount(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            final Object value = this.extensions.getField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
            if (value == null) {
                return (Type)((GeneratedExtension<MessageLite, Object>)extension).defaultValue;
            }
            return (Type)value;
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index) {
            this.verifyExtensionContainingType(extension);
            return (Type)this.extensions.getRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, index);
        }
        
        @Override
        public BuilderType clone() {
            throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
        }
        
        public final <Type> BuilderType setExtension(final GeneratedExtension<MessageType, Type> extension, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.setField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value);
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType setExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.setRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, index, value);
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType addExtension(final GeneratedExtension<MessageType, List<Type>> extension, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.addRepeatedField(((GeneratedExtension<MessageLite, Object>)extension).descriptor, value);
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType clearExtension(final GeneratedExtension<MessageType, ?> extension) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.clearField(((GeneratedExtension<MessageLite, Object>)extension).descriptor);
            return (BuilderType)this;
        }
        
        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }
        
        @Override
        protected boolean parseUnknownField(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            this.ensureExtensionsIsMutable();
            return parseUnknownField(this.extensions, this.getDefaultInstanceForType(), input, extensionRegistry, tag);
        }
        
        protected final void mergeExtensionFields(final MessageType other) {
            this.ensureExtensionsIsMutable();
            this.extensions.mergeFrom(((ExtendableMessage<ExtendableMessage>)other).extensions);
        }
    }
    
    private static final class ExtensionDescriptor implements FieldSet.FieldDescriptorLite<ExtensionDescriptor>
    {
        private final Internal.EnumLiteMap<?> enumTypeMap;
        private final int number;
        private final WireFormat.FieldType type;
        private final boolean isRepeated;
        private final boolean isPacked;
        
        private ExtensionDescriptor(final Internal.EnumLiteMap<?> enumTypeMap, final int number, final WireFormat.FieldType type, final boolean isRepeated, final boolean isPacked) {
            this.enumTypeMap = enumTypeMap;
            this.number = number;
            this.type = type;
            this.isRepeated = isRepeated;
            this.isPacked = isPacked;
        }
        
        public int getNumber() {
            return this.number;
        }
        
        public WireFormat.FieldType getLiteType() {
            return this.type;
        }
        
        public WireFormat.JavaType getLiteJavaType() {
            return this.type.getJavaType();
        }
        
        public boolean isRepeated() {
            return this.isRepeated;
        }
        
        public boolean isPacked() {
            return this.isPacked;
        }
        
        public Internal.EnumLiteMap<?> getEnumType() {
            return this.enumTypeMap;
        }
        
        public MessageLite.Builder internalMergeFrom(final MessageLite.Builder to, final MessageLite from) {
            return ((Builder)to).mergeFrom((GeneratedMessageLite)from);
        }
        
        public int compareTo(final ExtensionDescriptor other) {
            return this.number - other.number;
        }
    }
    
    public static final class GeneratedExtension<ContainingType extends MessageLite, Type>
    {
        private final ContainingType containingTypeDefaultInstance;
        private final Type defaultValue;
        private final MessageLite messageDefaultInstance;
        private final ExtensionDescriptor descriptor;
        
        private GeneratedExtension(final ContainingType containingTypeDefaultInstance, final Type defaultValue, final MessageLite messageDefaultInstance, final ExtensionDescriptor descriptor) {
            if (containingTypeDefaultInstance == null) {
                throw new IllegalArgumentException("Null containingTypeDefaultInstance");
            }
            if (descriptor.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance == null) {
                throw new IllegalArgumentException("Null messageDefaultInstance");
            }
            this.containingTypeDefaultInstance = containingTypeDefaultInstance;
            this.defaultValue = defaultValue;
            this.messageDefaultInstance = messageDefaultInstance;
            this.descriptor = descriptor;
        }
        
        public ContainingType getContainingTypeDefaultInstance() {
            return this.containingTypeDefaultInstance;
        }
        
        public int getNumber() {
            return this.descriptor.getNumber();
        }
        
        public MessageLite getMessageDefaultInstance() {
            return this.messageDefaultInstance;
        }
    }
    
    static final class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private String messageClassName;
        private byte[] asBytes;
        
        SerializedForm(final MessageLite regularForm) {
            this.messageClassName = regularForm.getClass().getName();
            this.asBytes = regularForm.toByteArray();
        }
        
        protected Object readResolve() throws ObjectStreamException {
            try {
                final Class messageClass = Class.forName(this.messageClassName);
                final Method newBuilder = messageClass.getMethod("newBuilder", (Class[])new Class[0]);
                final MessageLite.Builder builder = (MessageLite.Builder)newBuilder.invoke(null, new Object[0]);
                builder.mergeFrom(this.asBytes);
                return builder.buildPartial();
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to find proto buffer class", e);
            }
            catch (NoSuchMethodException e2) {
                throw new RuntimeException("Unable to find newBuilder method", e2);
            }
            catch (IllegalAccessException e3) {
                throw new RuntimeException("Unable to call newBuilder method", e3);
            }
            catch (InvocationTargetException e4) {
                throw new RuntimeException("Error calling newBuilder", e4.getCause());
            }
            catch (InvalidProtocolBufferException e5) {
                throw new RuntimeException("Unable to understand proto buffer", e5);
            }
        }
    }
    
    public interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage> extends MessageLiteOrBuilder
    {
         <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> p0);
        
         <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> p0);
        
         <Type> Type getExtension(final GeneratedExtension<MessageType, Type> p0);
        
         <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> p0, final int p1);
    }
}
