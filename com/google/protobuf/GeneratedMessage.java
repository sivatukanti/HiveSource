// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.ArrayList;
import java.io.ObjectStreamException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.io.Serializable;

public abstract class GeneratedMessage extends AbstractMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static boolean alwaysUseFieldBuilders;
    
    protected GeneratedMessage() {
    }
    
    protected GeneratedMessage(final Builder<?> builder) {
    }
    
    public Parser<? extends Message> getParserForType() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }
    
    static void enableAlwaysUseFieldBuildersForTesting() {
        GeneratedMessage.alwaysUseFieldBuilders = true;
    }
    
    protected abstract FieldAccessorTable internalGetFieldAccessorTable();
    
    public Descriptors.Descriptor getDescriptorForType() {
        return this.internalGetFieldAccessorTable().descriptor;
    }
    
    private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
        final TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
        final Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
        for (final Descriptors.FieldDescriptor field : descriptor.getFields()) {
            if (field.isRepeated()) {
                final List<?> value = (List<?>)this.getField(field);
                if (value.isEmpty()) {
                    continue;
                }
                result.put(field, value);
            }
            else {
                if (!this.hasField(field)) {
                    continue;
                }
                result.put(field, this.getField(field));
            }
        }
        return result;
    }
    
    @Override
    public boolean isInitialized() {
        for (final Descriptors.FieldDescriptor field : this.getDescriptorForType().getFields()) {
            if (field.isRequired() && !this.hasField(field)) {
                return false;
            }
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                continue;
            }
            if (field.isRepeated()) {
                final List<Message> messageList = (List<Message>)this.getField(field);
                for (final Message element : messageList) {
                    if (!element.isInitialized()) {
                        return false;
                    }
                }
            }
            else {
                if (this.hasField(field) && !((Message)this.getField(field)).isInitialized()) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return Collections.unmodifiableMap((Map<? extends Descriptors.FieldDescriptor, ?>)this.getAllFieldsMutable());
    }
    
    public boolean hasField(final Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).has(this);
    }
    
    public Object getField(final Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).get(this);
    }
    
    public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
        return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
    }
    
    public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
        return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
    }
    
    public UnknownFieldSet getUnknownFields() {
        throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
    }
    
    protected boolean parseUnknownField(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
        return unknownFields.mergeFieldFrom(tag, input);
    }
    
    protected void makeExtensionsImmutable() {
    }
    
    protected abstract Message.Builder newBuilderForType(final BuilderParent p0);
    
    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final int descriptorIndex, final Class singularType, final Message defaultInstance) {
        return new GeneratedExtension<ContainingType, Type>((ExtensionDescriptorRetriever)new ExtensionDescriptorRetriever() {
            public Descriptors.FieldDescriptor getDescriptor() {
                return scope.getDescriptorForType().getExtensions().get(descriptorIndex);
            }
        }, singularType, defaultInstance);
    }
    
    public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(final Class singularType, final Message defaultInstance) {
        return new GeneratedExtension<ContainingType, Type>((ExtensionDescriptorRetriever)null, singularType, defaultInstance);
    }
    
    private static Method getMethodOrDie(final Class clazz, final String name, final Class... params) {
        try {
            return clazz.getMethod(name, (Class[])params);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Generated message class \"" + clazz.getName() + "\" missing method \"" + name + "\".", e);
        }
    }
    
    private static Object invokeOrDie(final Method method, final Object object, final Object... params) {
        try {
            return method.invoke(object, params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        }
        catch (InvocationTargetException e2) {
            final Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
        }
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new GeneratedMessageLite.SerializedForm(this);
    }
    
    static {
        GeneratedMessage.alwaysUseFieldBuilders = false;
    }
    
    public abstract static class Builder<BuilderType extends Builder> extends AbstractMessage.Builder<BuilderType>
    {
        private BuilderParent builderParent;
        private BuilderParentImpl meAsParent;
        private boolean isClean;
        private UnknownFieldSet unknownFields;
        
        protected Builder() {
            this(null);
        }
        
        protected Builder(final BuilderParent builderParent) {
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
            this.builderParent = builderParent;
        }
        
        void dispose() {
            this.builderParent = null;
        }
        
        protected void onBuilt() {
            if (this.builderParent != null) {
                this.markClean();
            }
        }
        
        protected void markClean() {
            this.isClean = true;
        }
        
        protected boolean isClean() {
            return this.isClean;
        }
        
        @Override
        public BuilderType clone() {
            throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
        }
        
        @Override
        public BuilderType clear() {
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
            this.onChanged();
            return (BuilderType)this;
        }
        
        protected abstract FieldAccessorTable internalGetFieldAccessorTable();
        
        public Descriptors.Descriptor getDescriptorForType() {
            return this.internalGetFieldAccessorTable().descriptor;
        }
        
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            return Collections.unmodifiableMap((Map<? extends Descriptors.FieldDescriptor, ?>)this.getAllFieldsMutable());
        }
        
        private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
            final TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
            final Descriptors.Descriptor descriptor = this.internalGetFieldAccessorTable().descriptor;
            for (final Descriptors.FieldDescriptor field : descriptor.getFields()) {
                if (field.isRepeated()) {
                    final List value = (List)this.getField(field);
                    if (value.isEmpty()) {
                        continue;
                    }
                    result.put(field, value);
                }
                else {
                    if (!this.hasField(field)) {
                        continue;
                    }
                    result.put(field, this.getField(field));
                }
            }
            return result;
        }
        
        public Message.Builder newBuilderForField(final Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).newBuilder();
        }
        
        @Override
        public Message.Builder getFieldBuilder(final Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).getBuilder(this);
        }
        
        public boolean hasField(final Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).has(this);
        }
        
        public Object getField(final Descriptors.FieldDescriptor field) {
            final Object object = this.internalGetFieldAccessorTable().getField(field).get(this);
            if (field.isRepeated()) {
                return Collections.unmodifiableList((List<?>)object);
            }
            return object;
        }
        
        public BuilderType setField(final Descriptors.FieldDescriptor field, final Object value) {
            this.internalGetFieldAccessorTable().getField(field).set(this, value);
            return (BuilderType)this;
        }
        
        public BuilderType clearField(final Descriptors.FieldDescriptor field) {
            this.internalGetFieldAccessorTable().getField(field).clear(this);
            return (BuilderType)this;
        }
        
        public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
            return this.internalGetFieldAccessorTable().getField(field).getRepeatedCount(this);
        }
        
        public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
            return this.internalGetFieldAccessorTable().getField(field).getRepeated(this, index);
        }
        
        public BuilderType setRepeatedField(final Descriptors.FieldDescriptor field, final int index, final Object value) {
            this.internalGetFieldAccessorTable().getField(field).setRepeated(this, index, value);
            return (BuilderType)this;
        }
        
        public BuilderType addRepeatedField(final Descriptors.FieldDescriptor field, final Object value) {
            this.internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
            return (BuilderType)this;
        }
        
        public final BuilderType setUnknownFields(final UnknownFieldSet unknownFields) {
            this.unknownFields = unknownFields;
            this.onChanged();
            return (BuilderType)this;
        }
        
        @Override
        public final BuilderType mergeUnknownFields(final UnknownFieldSet unknownFields) {
            this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
            this.onChanged();
            return (BuilderType)this;
        }
        
        public boolean isInitialized() {
            for (final Descriptors.FieldDescriptor field : this.getDescriptorForType().getFields()) {
                if (field.isRequired() && !this.hasField(field)) {
                    return false;
                }
                if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    continue;
                }
                if (field.isRepeated()) {
                    final List<Message> messageList = (List<Message>)this.getField(field);
                    for (final Message element : messageList) {
                        if (!element.isInitialized()) {
                            return false;
                        }
                    }
                }
                else {
                    if (this.hasField(field) && !((Message)this.getField(field)).isInitialized()) {
                        return false;
                    }
                    continue;
                }
            }
            return true;
        }
        
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        protected boolean parseUnknownField(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            return unknownFields.mergeFieldFrom(tag, input);
        }
        
        protected BuilderParent getParentForChildren() {
            if (this.meAsParent == null) {
                this.meAsParent = new BuilderParentImpl();
            }
            return this.meAsParent;
        }
        
        protected final void onChanged() {
            if (this.isClean && this.builderParent != null) {
                this.builderParent.markDirty();
                this.isClean = false;
            }
        }
        
        private class BuilderParentImpl implements BuilderParent
        {
            public void markDirty() {
                Builder.this.onChanged();
            }
        }
    }
    
    public abstract static class ExtendableMessage<MessageType extends ExtendableMessage> extends GeneratedMessage implements ExtendableMessageOrBuilder<MessageType>
    {
        private final FieldSet<Descriptors.FieldDescriptor> extensions;
        
        protected ExtendableMessage() {
            this.extensions = FieldSet.newFieldSet();
        }
        
        protected ExtendableMessage(final ExtendableBuilder<MessageType, ?> builder) {
            super(builder);
            this.extensions = (FieldSet<Descriptors.FieldDescriptor>)((ExtendableBuilder<ExtendableMessage, ExtendableBuilder>)builder).buildExtensions();
        }
        
        private void verifyExtensionContainingType(final GeneratedExtension<MessageType, ?> extension) {
            if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
            }
        }
        
        public final <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(extension.getDescriptor());
        }
        
        public final <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> extension) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions.getRepeatedFieldCount(descriptor);
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            final Object value = this.extensions.getField(descriptor);
            if (value != null) {
                return (Type)((GeneratedExtension<Message, Object>)extension).fromReflectionType(value);
            }
            if (descriptor.isRepeated()) {
                return (Type)Collections.emptyList();
            }
            if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return (Type)extension.getMessageDefaultInstance();
            }
            return (Type)((GeneratedExtension<Message, Object>)extension).fromReflectionType(descriptor.getDefaultValue());
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return (Type)((GeneratedExtension<Message, Object>)extension).singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }
        
        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }
        
        @Override
        public boolean isInitialized() {
            return super.isInitialized() && this.extensionsAreInitialized();
        }
        
        @Override
        protected boolean parseUnknownField(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            return AbstractMessage.Builder.mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), null, this.extensions, tag);
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
        
        protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
            return this.extensions.getAllFields();
        }
        
        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            final Map<Descriptors.FieldDescriptor, Object> result = GeneratedMessage.this.getAllFieldsMutable();
            result.putAll(this.getExtensionFields());
            return Collections.unmodifiableMap((Map<? extends Descriptors.FieldDescriptor, ?>)result);
        }
        
        @Override
        public boolean hasField(final Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.hasField(field);
            }
            return super.hasField(field);
        }
        
        @Override
        public Object getField(final Descriptors.FieldDescriptor field) {
            if (!field.isExtension()) {
                return super.getField(field);
            }
            this.verifyContainingType(field);
            final Object value = this.extensions.getField(field);
            if (value != null) {
                return value;
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return DynamicMessage.getDefaultInstance(field.getMessageType());
            }
            return field.getDefaultValue();
        }
        
        @Override
        public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedFieldCount(field);
            }
            return super.getRepeatedFieldCount(field);
        }
        
        @Override
        public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedField(field, index);
            }
            return super.getRepeatedField(field, index);
        }
        
        private void verifyContainingType(final Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }
        
        protected class ExtensionWriter
        {
            private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter;
            private Map.Entry<Descriptors.FieldDescriptor, Object> next;
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
                    final Descriptors.FieldDescriptor descriptor = this.next.getKey();
                    if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && !descriptor.isRepeated()) {
                        if (this.next instanceof LazyField.LazyEntry) {
                            output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next).getField().toByteString());
                        }
                        else {
                            output.writeMessageSetExtension(descriptor.getNumber(), this.next.getValue());
                        }
                    }
                    else {
                        FieldSet.writeField(descriptor, this.next.getValue(), output);
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
    
    public abstract static class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder> extends Builder<BuilderType> implements ExtendableMessageOrBuilder<MessageType>
    {
        private FieldSet<Descriptors.FieldDescriptor> extensions;
        
        protected ExtendableBuilder() {
            this.extensions = FieldSet.emptySet();
        }
        
        protected ExtendableBuilder(final BuilderParent parent) {
            super(parent);
            this.extensions = FieldSet.emptySet();
        }
        
        @Override
        public BuilderType clear() {
            this.extensions = FieldSet.emptySet();
            return super.clear();
        }
        
        @Override
        public BuilderType clone() {
            throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
        }
        
        private void ensureExtensionsIsMutable() {
            if (this.extensions.isImmutable()) {
                this.extensions = this.extensions.clone();
            }
        }
        
        private void verifyExtensionContainingType(final GeneratedExtension<MessageType, ?> extension) {
            if (extension.getDescriptor().getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("Extension is for type \"" + extension.getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + this.getDescriptorForType().getFullName() + "\".");
            }
        }
        
        public final <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            return this.extensions.hasField(extension.getDescriptor());
        }
        
        public final <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> extension) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return this.extensions.getRepeatedFieldCount(descriptor);
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, Type> extension) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            final Object value = this.extensions.getField(descriptor);
            if (value != null) {
                return (Type)((GeneratedExtension<Message, Object>)extension).fromReflectionType(value);
            }
            if (descriptor.isRepeated()) {
                return (Type)Collections.emptyList();
            }
            if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return (Type)extension.getMessageDefaultInstance();
            }
            return (Type)((GeneratedExtension<Message, Object>)extension).fromReflectionType(descriptor.getDefaultValue());
        }
        
        public final <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index) {
            this.verifyExtensionContainingType(extension);
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            return (Type)((GeneratedExtension<Message, Object>)extension).singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
        }
        
        public final <Type> BuilderType setExtension(final GeneratedExtension<MessageType, Type> extension, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setField(descriptor, ((GeneratedExtension<Message, Object>)extension).toReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType setExtension(final GeneratedExtension<MessageType, List<Type>> extension, final int index, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.setRepeatedField(descriptor, index, ((GeneratedExtension<Message, Object>)extension).singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType addExtension(final GeneratedExtension<MessageType, List<Type>> extension, final Type value) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            final Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
            this.extensions.addRepeatedField(descriptor, ((GeneratedExtension<Message, Object>)extension).singularToReflectionType(value));
            this.onChanged();
            return (BuilderType)this;
        }
        
        public final <Type> BuilderType clearExtension(final GeneratedExtension<MessageType, ?> extension) {
            this.verifyExtensionContainingType(extension);
            this.ensureExtensionsIsMutable();
            this.extensions.clearField(extension.getDescriptor());
            this.onChanged();
            return (BuilderType)this;
        }
        
        protected boolean extensionsAreInitialized() {
            return this.extensions.isInitialized();
        }
        
        private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
            this.extensions.makeImmutable();
            return this.extensions;
        }
        
        @Override
        public boolean isInitialized() {
            return super.isInitialized() && this.extensionsAreInitialized();
        }
        
        @Override
        protected boolean parseUnknownField(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final int tag) throws IOException {
            return AbstractMessage.Builder.mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), this, null, tag);
        }
        
        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            final Map<Descriptors.FieldDescriptor, Object> result = (Map<Descriptors.FieldDescriptor, Object>)((Builder<Builder>)this).getAllFieldsMutable();
            result.putAll(this.extensions.getAllFields());
            return Collections.unmodifiableMap((Map<? extends Descriptors.FieldDescriptor, ?>)result);
        }
        
        @Override
        public Object getField(final Descriptors.FieldDescriptor field) {
            if (!field.isExtension()) {
                return super.getField(field);
            }
            this.verifyContainingType(field);
            final Object value = this.extensions.getField(field);
            if (value != null) {
                return value;
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                return DynamicMessage.getDefaultInstance(field.getMessageType());
            }
            return field.getDefaultValue();
        }
        
        @Override
        public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedFieldCount(field);
            }
            return super.getRepeatedFieldCount(field);
        }
        
        @Override
        public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.getRepeatedField(field, index);
            }
            return super.getRepeatedField(field, index);
        }
        
        @Override
        public boolean hasField(final Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                return this.extensions.hasField(field);
            }
            return super.hasField(field);
        }
        
        @Override
        public BuilderType setField(final Descriptors.FieldDescriptor field, final Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.setField(field, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return super.setField(field, value);
        }
        
        @Override
        public BuilderType clearField(final Descriptors.FieldDescriptor field) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.clearField(field);
                this.onChanged();
                return (BuilderType)this;
            }
            return super.clearField(field);
        }
        
        @Override
        public BuilderType setRepeatedField(final Descriptors.FieldDescriptor field, final int index, final Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.setRepeatedField(field, index, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return super.setRepeatedField(field, index, value);
        }
        
        @Override
        public BuilderType addRepeatedField(final Descriptors.FieldDescriptor field, final Object value) {
            if (field.isExtension()) {
                this.verifyContainingType(field);
                this.ensureExtensionsIsMutable();
                this.extensions.addRepeatedField(field, value);
                this.onChanged();
                return (BuilderType)this;
            }
            return super.addRepeatedField(field, value);
        }
        
        protected final void mergeExtensionFields(final ExtendableMessage other) {
            this.ensureExtensionsIsMutable();
            this.extensions.mergeFrom(other.extensions);
            this.onChanged();
        }
        
        private void verifyContainingType(final Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }
    }
    
    public static final class GeneratedExtension<ContainingType extends Message, Type>
    {
        private ExtensionDescriptorRetriever descriptorRetriever;
        private final Class singularType;
        private final Message messageDefaultInstance;
        private final Method enumValueOf;
        private final Method enumGetValueDescriptor;
        
        private GeneratedExtension(final ExtensionDescriptorRetriever descriptorRetriever, final Class singularType, final Message messageDefaultInstance) {
            if (Message.class.isAssignableFrom(singularType) && !singularType.isInstance(messageDefaultInstance)) {
                throw new IllegalArgumentException("Bad messageDefaultInstance for " + singularType.getName());
            }
            this.descriptorRetriever = descriptorRetriever;
            this.singularType = singularType;
            this.messageDefaultInstance = messageDefaultInstance;
            if (ProtocolMessageEnum.class.isAssignableFrom(singularType)) {
                this.enumValueOf = getMethodOrDie(singularType, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
                this.enumGetValueDescriptor = getMethodOrDie(singularType, "getValueDescriptor", new Class[0]);
            }
            else {
                this.enumValueOf = null;
                this.enumGetValueDescriptor = null;
            }
        }
        
        public void internalInit(final Descriptors.FieldDescriptor descriptor) {
            if (this.descriptorRetriever != null) {
                throw new IllegalStateException("Already initialized.");
            }
            this.descriptorRetriever = new ExtensionDescriptorRetriever() {
                public Descriptors.FieldDescriptor getDescriptor() {
                    return descriptor;
                }
            };
        }
        
        public Descriptors.FieldDescriptor getDescriptor() {
            if (this.descriptorRetriever == null) {
                throw new IllegalStateException("getDescriptor() called before internalInit()");
            }
            return this.descriptorRetriever.getDescriptor();
        }
        
        public Message getMessageDefaultInstance() {
            return this.messageDefaultInstance;
        }
        
        private Object fromReflectionType(final Object value) {
            final Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            if (!descriptor.isRepeated()) {
                return this.singularFromReflectionType(value);
            }
            if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                final List result = new ArrayList();
                for (final Object element : (List)value) {
                    result.add(this.singularFromReflectionType(element));
                }
                return result;
            }
            return value;
        }
        
        private Object singularFromReflectionType(final Object value) {
            final Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            switch (descriptor.getJavaType()) {
                case MESSAGE: {
                    if (this.singularType.isInstance(value)) {
                        return value;
                    }
                    return this.messageDefaultInstance.newBuilderForType().mergeFrom((Message)value).build();
                }
                case ENUM: {
                    return invokeOrDie(this.enumValueOf, null, new Object[] { (Descriptors.EnumValueDescriptor)value });
                }
                default: {
                    return value;
                }
            }
        }
        
        private Object toReflectionType(final Object value) {
            final Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            if (!descriptor.isRepeated()) {
                return this.singularToReflectionType(value);
            }
            if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                final List result = new ArrayList();
                for (final Object element : (List)value) {
                    result.add(this.singularToReflectionType(element));
                }
                return result;
            }
            return value;
        }
        
        private Object singularToReflectionType(final Object value) {
            final Descriptors.FieldDescriptor descriptor = this.getDescriptor();
            switch (descriptor.getJavaType()) {
                case ENUM: {
                    return invokeOrDie(this.enumGetValueDescriptor, value, new Object[0]);
                }
                default: {
                    return value;
                }
            }
        }
    }
    
    public static final class FieldAccessorTable
    {
        private final Descriptors.Descriptor descriptor;
        private final FieldAccessor[] fields;
        private String[] camelCaseNames;
        private volatile boolean initialized;
        
        public FieldAccessorTable(final Descriptors.Descriptor descriptor, final String[] camelCaseNames, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
            this(descriptor, camelCaseNames);
            this.ensureFieldAccessorsInitialized(messageClass, builderClass);
        }
        
        public FieldAccessorTable(final Descriptors.Descriptor descriptor, final String[] camelCaseNames) {
            this.descriptor = descriptor;
            this.camelCaseNames = camelCaseNames;
            this.fields = new FieldAccessor[descriptor.getFields().size()];
            this.initialized = false;
        }
        
        public FieldAccessorTable ensureFieldAccessorsInitialized(final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
            if (this.initialized) {
                return this;
            }
            synchronized (this) {
                if (this.initialized) {
                    return this;
                }
                for (int i = 0; i < this.fields.length; ++i) {
                    final Descriptors.FieldDescriptor field = this.descriptor.getFields().get(i);
                    if (field.isRepeated()) {
                        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                            this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        }
                        else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                            this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        }
                        else {
                            this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                        }
                    }
                    else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        this.fields[i] = new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                    }
                    else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                        this.fields[i] = new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                    }
                    else {
                        this.fields[i] = new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
                    }
                }
                this.initialized = true;
                this.camelCaseNames = null;
                return this;
            }
        }
        
        private FieldAccessor getField(final Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.descriptor) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
            if (field.isExtension()) {
                throw new IllegalArgumentException("This type does not have extensions.");
            }
            return this.fields[field.getIndex()];
        }
        
        private static class SingularFieldAccessor implements FieldAccessor
        {
            protected final Class<?> type;
            protected final Method getMethod;
            protected final Method getMethodBuilder;
            protected final Method setMethod;
            protected final Method hasMethod;
            protected final Method hasMethodBuilder;
            protected final Method clearMethod;
            
            SingularFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                this.getMethod = getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
                this.getMethodBuilder = getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
                this.type = this.getMethod.getReturnType();
                this.setMethod = getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { this.type });
                this.hasMethod = getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]);
                this.hasMethodBuilder = getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]);
                this.clearMethod = getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
            }
            
            public Object get(final GeneratedMessage message) {
                return invokeOrDie(this.getMethod, message, new Object[0]);
            }
            
            public Object get(final Builder builder) {
                return invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
            }
            
            public void set(final Builder builder, final Object value) {
                invokeOrDie(this.setMethod, builder, new Object[] { value });
            }
            
            public Object getRepeated(final GeneratedMessage message, final int index) {
                throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
            }
            
            public Object getRepeated(final Builder builder, final int index) {
                throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
            }
            
            public void setRepeated(final Builder builder, final int index, final Object value) {
                throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
            }
            
            public void addRepeated(final Builder builder, final Object value) {
                throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
            }
            
            public boolean has(final GeneratedMessage message) {
                return (boolean)invokeOrDie(this.hasMethod, message, new Object[0]);
            }
            
            public boolean has(final Builder builder) {
                return (boolean)invokeOrDie(this.hasMethodBuilder, builder, new Object[0]);
            }
            
            public int getRepeatedCount(final GeneratedMessage message) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }
            
            public int getRepeatedCount(final Builder builder) {
                throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
            }
            
            public void clear(final Builder builder) {
                invokeOrDie(this.clearMethod, builder, new Object[0]);
            }
            
            public Message.Builder newBuilder() {
                throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
            }
            
            public Message.Builder getBuilder(final Builder builder) {
                throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            }
        }
        
        private static class RepeatedFieldAccessor implements FieldAccessor
        {
            protected final Class type;
            protected final Method getMethod;
            protected final Method getMethodBuilder;
            protected final Method getRepeatedMethod;
            protected final Method getRepeatedMethodBuilder;
            protected final Method setRepeatedMethod;
            protected final Method addRepeatedMethod;
            protected final Method getCountMethod;
            protected final Method getCountMethodBuilder;
            protected final Method clearMethod;
            
            RepeatedFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                this.getMethod = getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
                this.getMethodBuilder = getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
                this.getRepeatedMethod = getMethodOrDie(messageClass, "get" + camelCaseName, new Class[] { Integer.TYPE });
                this.getRepeatedMethodBuilder = getMethodOrDie(builderClass, "get" + camelCaseName, new Class[] { Integer.TYPE });
                this.type = this.getRepeatedMethod.getReturnType();
                this.setRepeatedMethod = getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { Integer.TYPE, this.type });
                this.addRepeatedMethod = getMethodOrDie(builderClass, "add" + camelCaseName, new Class[] { this.type });
                this.getCountMethod = getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
                this.getCountMethodBuilder = getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
                this.clearMethod = getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
            }
            
            public Object get(final GeneratedMessage message) {
                return invokeOrDie(this.getMethod, message, new Object[0]);
            }
            
            public Object get(final Builder builder) {
                return invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
            }
            
            public void set(final Builder builder, final Object value) {
                this.clear(builder);
                for (final Object element : (List)value) {
                    this.addRepeated(builder, element);
                }
            }
            
            public Object getRepeated(final GeneratedMessage message, final int index) {
                return invokeOrDie(this.getRepeatedMethod, message, new Object[] { index });
            }
            
            public Object getRepeated(final Builder builder, final int index) {
                return invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[] { index });
            }
            
            public void setRepeated(final Builder builder, final int index, final Object value) {
                invokeOrDie(this.setRepeatedMethod, builder, new Object[] { index, value });
            }
            
            public void addRepeated(final Builder builder, final Object value) {
                invokeOrDie(this.addRepeatedMethod, builder, new Object[] { value });
            }
            
            public boolean has(final GeneratedMessage message) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }
            
            public boolean has(final Builder builder) {
                throw new UnsupportedOperationException("hasField() called on a repeated field.");
            }
            
            public int getRepeatedCount(final GeneratedMessage message) {
                return (int)invokeOrDie(this.getCountMethod, message, new Object[0]);
            }
            
            public int getRepeatedCount(final Builder builder) {
                return (int)invokeOrDie(this.getCountMethodBuilder, builder, new Object[0]);
            }
            
            public void clear(final Builder builder) {
                invokeOrDie(this.clearMethod, builder, new Object[0]);
            }
            
            public Message.Builder newBuilder() {
                throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
            }
            
            public Message.Builder getBuilder(final Builder builder) {
                throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
            }
        }
        
        private static final class SingularEnumFieldAccessor extends SingularFieldAccessor
        {
            private Method valueOfMethod;
            private Method getValueDescriptorMethod;
            
            SingularEnumFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.valueOfMethod = getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
                this.getValueDescriptorMethod = getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
            }
            
            @Override
            public Object get(final GeneratedMessage message) {
                return invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
            }
            
            @Override
            public Object get(final Builder builder) {
                return invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
            }
            
            @Override
            public void set(final Builder builder, final Object value) {
                super.set(builder, invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
            }
        }
        
        private static final class RepeatedEnumFieldAccessor extends RepeatedFieldAccessor
        {
            private final Method valueOfMethod;
            private final Method getValueDescriptorMethod;
            
            RepeatedEnumFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.valueOfMethod = getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
                this.getValueDescriptorMethod = getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
            }
            
            @Override
            public Object get(final GeneratedMessage message) {
                final List newList = new ArrayList();
                for (final Object element : (List)super.get(message)) {
                    newList.add(invokeOrDie(this.getValueDescriptorMethod, element, new Object[0]));
                }
                return Collections.unmodifiableList((List<?>)newList);
            }
            
            @Override
            public Object get(final Builder builder) {
                final List newList = new ArrayList();
                for (final Object element : (List)super.get(builder)) {
                    newList.add(invokeOrDie(this.getValueDescriptorMethod, element, new Object[0]));
                }
                return Collections.unmodifiableList((List<?>)newList);
            }
            
            @Override
            public Object getRepeated(final GeneratedMessage message, final int index) {
                return invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index), new Object[0]);
            }
            
            @Override
            public Object getRepeated(final Builder builder, final int index) {
                return invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index), new Object[0]);
            }
            
            @Override
            public void setRepeated(final Builder builder, final int index, final Object value) {
                super.setRepeated(builder, index, invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
            }
            
            @Override
            public void addRepeated(final Builder builder, final Object value) {
                super.addRepeated(builder, invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
            }
        }
        
        private static final class SingularMessageFieldAccessor extends SingularFieldAccessor
        {
            private final Method newBuilderMethod;
            private final Method getBuilderMethodBuilder;
            
            SingularMessageFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.newBuilderMethod = getMethodOrDie(this.type, "newBuilder", new Class[0]);
                this.getBuilderMethodBuilder = getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
            }
            
            private Object coerceType(final Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).buildPartial();
            }
            
            @Override
            public void set(final Builder builder, final Object value) {
                super.set(builder, this.coerceType(value));
            }
            
            @Override
            public Message.Builder newBuilder() {
                return (Message.Builder)invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }
            
            @Override
            public Message.Builder getBuilder(final Builder builder) {
                return (Message.Builder)invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
            }
        }
        
        private static final class RepeatedMessageFieldAccessor extends RepeatedFieldAccessor
        {
            private final Method newBuilderMethod;
            
            RepeatedMessageFieldAccessor(final Descriptors.FieldDescriptor descriptor, final String camelCaseName, final Class<? extends GeneratedMessage> messageClass, final Class<? extends Builder> builderClass) {
                super(descriptor, camelCaseName, messageClass, builderClass);
                this.newBuilderMethod = getMethodOrDie(this.type, "newBuilder", new Class[0]);
            }
            
            private Object coerceType(final Object value) {
                if (this.type.isInstance(value)) {
                    return value;
                }
                return ((Message.Builder)invokeOrDie(this.newBuilderMethod, null, new Object[0])).mergeFrom((Message)value).build();
            }
            
            @Override
            public void setRepeated(final Builder builder, final int index, final Object value) {
                super.setRepeated(builder, index, this.coerceType(value));
            }
            
            @Override
            public void addRepeated(final Builder builder, final Object value) {
                super.addRepeated(builder, this.coerceType(value));
            }
            
            @Override
            public Message.Builder newBuilder() {
                return (Message.Builder)invokeOrDie(this.newBuilderMethod, null, new Object[0]);
            }
        }
        
        private interface FieldAccessor
        {
            Object get(final GeneratedMessage p0);
            
            Object get(final Builder p0);
            
            void set(final Builder p0, final Object p1);
            
            Object getRepeated(final GeneratedMessage p0, final int p1);
            
            Object getRepeated(final Builder p0, final int p1);
            
            void setRepeated(final Builder p0, final int p1, final Object p2);
            
            void addRepeated(final Builder p0, final Object p1);
            
            boolean has(final GeneratedMessage p0);
            
            boolean has(final Builder p0);
            
            int getRepeatedCount(final GeneratedMessage p0);
            
            int getRepeatedCount(final Builder p0);
            
            void clear(final Builder p0);
            
            Message.Builder newBuilder();
            
            Message.Builder getBuilder(final Builder p0);
        }
    }
    
    protected interface BuilderParent
    {
        void markDirty();
    }
    
    private interface ExtensionDescriptorRetriever
    {
        Descriptors.FieldDescriptor getDescriptor();
    }
    
    public interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage> extends MessageOrBuilder
    {
         <Type> boolean hasExtension(final GeneratedExtension<MessageType, Type> p0);
        
         <Type> int getExtensionCount(final GeneratedExtension<MessageType, List<Type>> p0);
        
         <Type> Type getExtension(final GeneratedExtension<MessageType, Type> p0);
        
         <Type> Type getExtension(final GeneratedExtension<MessageType, List<Type>> p0, final int p1);
    }
}
