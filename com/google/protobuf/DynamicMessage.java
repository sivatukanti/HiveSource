// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;

public final class DynamicMessage extends AbstractMessage
{
    private final Descriptors.Descriptor type;
    private final FieldSet<Descriptors.FieldDescriptor> fields;
    private final UnknownFieldSet unknownFields;
    private int memoizedSize;
    
    private DynamicMessage(final Descriptors.Descriptor type, final FieldSet<Descriptors.FieldDescriptor> fields, final UnknownFieldSet unknownFields) {
        this.memoizedSize = -1;
        this.type = type;
        this.fields = fields;
        this.unknownFields = unknownFields;
    }
    
    public static DynamicMessage getDefaultInstance(final Descriptors.Descriptor type) {
        return new DynamicMessage(type, FieldSet.emptySet(), UnknownFieldSet.getDefaultInstance());
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final CodedInputStream input) throws IOException {
        return newBuilder(type).mergeFrom(input).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final CodedInputStream input, final ExtensionRegistry extensionRegistry) throws IOException {
        return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final ByteString data) throws InvalidProtocolBufferException {
        return newBuilder(type).mergeFrom(data).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final ByteString data, final ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
        return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final byte[] data) throws InvalidProtocolBufferException {
        return newBuilder(type).mergeFrom(data).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final byte[] data, final ExtensionRegistry extensionRegistry) throws InvalidProtocolBufferException {
        return newBuilder(type).mergeFrom(data, extensionRegistry).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final InputStream input) throws IOException {
        return newBuilder(type).mergeFrom(input).buildParsed();
    }
    
    public static DynamicMessage parseFrom(final Descriptors.Descriptor type, final InputStream input, final ExtensionRegistry extensionRegistry) throws IOException {
        return newBuilder(type).mergeFrom(input, extensionRegistry).buildParsed();
    }
    
    public static Builder newBuilder(final Descriptors.Descriptor type) {
        return new Builder(type);
    }
    
    public static Builder newBuilder(final Message prototype) {
        return new Builder(prototype.getDescriptorForType()).mergeFrom(prototype);
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
        return this.type;
    }
    
    public DynamicMessage getDefaultInstanceForType() {
        return getDefaultInstance(this.type);
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        return this.fields.getAllFields();
    }
    
    public boolean hasField(final Descriptors.FieldDescriptor field) {
        this.verifyContainingType(field);
        return this.fields.hasField(field);
    }
    
    public Object getField(final Descriptors.FieldDescriptor field) {
        this.verifyContainingType(field);
        Object result = this.fields.getField(field);
        if (result == null) {
            if (field.isRepeated()) {
                result = Collections.emptyList();
            }
            else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                result = getDefaultInstance(field.getMessageType());
            }
            else {
                result = field.getDefaultValue();
            }
        }
        return result;
    }
    
    public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
        this.verifyContainingType(field);
        return this.fields.getRepeatedFieldCount(field);
    }
    
    public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
        this.verifyContainingType(field);
        return this.fields.getRepeatedField(field, index);
    }
    
    public UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }
    
    private static boolean isInitialized(final Descriptors.Descriptor type, final FieldSet<Descriptors.FieldDescriptor> fields) {
        for (final Descriptors.FieldDescriptor field : type.getFields()) {
            if (field.isRequired() && !fields.hasField(field)) {
                return false;
            }
        }
        return fields.isInitialized();
    }
    
    @Override
    public boolean isInitialized() {
        return isInitialized(this.type, this.fields);
    }
    
    @Override
    public void writeTo(final CodedOutputStream output) throws IOException {
        if (this.type.getOptions().getMessageSetWireFormat()) {
            this.fields.writeMessageSetTo(output);
            this.unknownFields.writeAsMessageSetTo(output);
        }
        else {
            this.fields.writeTo(output);
            this.unknownFields.writeTo(output);
        }
    }
    
    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        if (this.type.getOptions().getMessageSetWireFormat()) {
            size = this.fields.getMessageSetSerializedSize();
            size += this.unknownFields.getSerializedSizeAsMessageSet();
        }
        else {
            size = this.fields.getSerializedSize();
            size += this.unknownFields.getSerializedSize();
        }
        return this.memoizedSize = size;
    }
    
    public Builder newBuilderForType() {
        return new Builder(this.type);
    }
    
    public Builder toBuilder() {
        return this.newBuilderForType().mergeFrom(this);
    }
    
    public Parser<DynamicMessage> getParserForType() {
        return new AbstractParser<DynamicMessage>() {
            public DynamicMessage parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                final Builder builder = DynamicMessage.newBuilder(DynamicMessage.this.type);
                try {
                    builder.mergeFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(builder.buildPartial());
                }
                catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(builder.buildPartial());
                }
                return builder.buildPartial();
            }
        };
    }
    
    private void verifyContainingType(final Descriptors.FieldDescriptor field) {
        if (field.getContainingType() != this.type) {
            throw new IllegalArgumentException("FieldDescriptor does not match message type.");
        }
    }
    
    public static final class Builder extends AbstractMessage.Builder<Builder>
    {
        private final Descriptors.Descriptor type;
        private FieldSet<Descriptors.FieldDescriptor> fields;
        private UnknownFieldSet unknownFields;
        
        private Builder(final Descriptors.Descriptor type) {
            this.type = type;
            this.fields = FieldSet.newFieldSet();
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        @Override
        public Builder clear() {
            if (this.fields.isImmutable()) {
                this.fields = FieldSet.newFieldSet();
            }
            else {
                this.fields.clear();
            }
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
            return this;
        }
        
        @Override
        public Builder mergeFrom(final Message other) {
            if (!(other instanceof DynamicMessage)) {
                return super.mergeFrom(other);
            }
            final DynamicMessage otherDynamicMessage = (DynamicMessage)other;
            if (otherDynamicMessage.type != this.type) {
                throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
            }
            this.ensureIsMutable();
            this.fields.mergeFrom(otherDynamicMessage.fields);
            this.mergeUnknownFields(otherDynamicMessage.unknownFields);
            return this;
        }
        
        public DynamicMessage build() {
            if (!this.isInitialized()) {
                throw AbstractMessage.Builder.newUninitializedMessageException(new DynamicMessage(this.type, this.fields, this.unknownFields, null));
            }
            return this.buildPartial();
        }
        
        private DynamicMessage buildParsed() throws InvalidProtocolBufferException {
            if (!this.isInitialized()) {
                throw AbstractMessage.Builder.newUninitializedMessageException(new DynamicMessage(this.type, this.fields, this.unknownFields, null)).asInvalidProtocolBufferException();
            }
            return this.buildPartial();
        }
        
        public DynamicMessage buildPartial() {
            this.fields.makeImmutable();
            final DynamicMessage result = new DynamicMessage(this.type, this.fields, this.unknownFields, null);
            return result;
        }
        
        @Override
        public Builder clone() {
            final Builder result = new Builder(this.type);
            result.fields.mergeFrom(this.fields);
            result.mergeUnknownFields(this.unknownFields);
            return result;
        }
        
        public boolean isInitialized() {
            return isInitialized(this.type, this.fields);
        }
        
        public Descriptors.Descriptor getDescriptorForType() {
            return this.type;
        }
        
        public DynamicMessage getDefaultInstanceForType() {
            return DynamicMessage.getDefaultInstance(this.type);
        }
        
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            return this.fields.getAllFields();
        }
        
        public Builder newBuilderForField(final Descriptors.FieldDescriptor field) {
            this.verifyContainingType(field);
            if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                throw new IllegalArgumentException("newBuilderForField is only valid for fields with message type.");
            }
            return new Builder(field.getMessageType());
        }
        
        public boolean hasField(final Descriptors.FieldDescriptor field) {
            this.verifyContainingType(field);
            return this.fields.hasField(field);
        }
        
        public Object getField(final Descriptors.FieldDescriptor field) {
            this.verifyContainingType(field);
            Object result = this.fields.getField(field);
            if (result == null) {
                if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    result = DynamicMessage.getDefaultInstance(field.getMessageType());
                }
                else {
                    result = field.getDefaultValue();
                }
            }
            return result;
        }
        
        public Builder setField(final Descriptors.FieldDescriptor field, final Object value) {
            this.verifyContainingType(field);
            this.ensureIsMutable();
            this.fields.setField(field, value);
            return this;
        }
        
        public Builder clearField(final Descriptors.FieldDescriptor field) {
            this.verifyContainingType(field);
            this.ensureIsMutable();
            this.fields.clearField(field);
            return this;
        }
        
        public int getRepeatedFieldCount(final Descriptors.FieldDescriptor field) {
            this.verifyContainingType(field);
            return this.fields.getRepeatedFieldCount(field);
        }
        
        public Object getRepeatedField(final Descriptors.FieldDescriptor field, final int index) {
            this.verifyContainingType(field);
            return this.fields.getRepeatedField(field, index);
        }
        
        public Builder setRepeatedField(final Descriptors.FieldDescriptor field, final int index, final Object value) {
            this.verifyContainingType(field);
            this.ensureIsMutable();
            this.fields.setRepeatedField(field, index, value);
            return this;
        }
        
        public Builder addRepeatedField(final Descriptors.FieldDescriptor field, final Object value) {
            this.verifyContainingType(field);
            this.ensureIsMutable();
            this.fields.addRepeatedField(field, value);
            return this;
        }
        
        public UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        public Builder setUnknownFields(final UnknownFieldSet unknownFields) {
            this.unknownFields = unknownFields;
            return this;
        }
        
        @Override
        public Builder mergeUnknownFields(final UnknownFieldSet unknownFields) {
            this.unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
            return this;
        }
        
        private void verifyContainingType(final Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.type) {
                throw new IllegalArgumentException("FieldDescriptor does not match message type.");
            }
        }
        
        private void ensureIsMutable() {
            if (this.fields.isImmutable()) {
                this.fields = this.fields.clone();
            }
        }
        
        @Override
        public Message.Builder getFieldBuilder(final Descriptors.FieldDescriptor field) {
            throw new UnsupportedOperationException("getFieldBuilder() called on a dynamic message type.");
        }
    }
}
