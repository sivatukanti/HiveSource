// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessage extends AbstractMessageLite implements Message
{
    private int memoizedSize;
    
    public AbstractMessage() {
        this.memoizedSize = -1;
    }
    
    public boolean isInitialized() {
        for (final Descriptors.FieldDescriptor field : this.getDescriptorForType().getFields()) {
            if (field.isRequired() && !this.hasField(field)) {
                return false;
            }
        }
        for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : this.getAllFields().entrySet()) {
            final Descriptors.FieldDescriptor field2 = entry.getKey();
            if (field2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                if (field2.isRepeated()) {
                    for (final Message element : entry.getValue()) {
                        if (!element.isInitialized()) {
                            return false;
                        }
                    }
                }
                else {
                    if (!entry.getValue().isInitialized()) {
                        return false;
                    }
                    continue;
                }
            }
        }
        return true;
    }
    
    public List<String> findInitializationErrors() {
        return findMissingFields(this);
    }
    
    public String getInitializationErrorString() {
        return delimitWithCommas(this.findInitializationErrors());
    }
    
    private static String delimitWithCommas(final List<String> parts) {
        final StringBuilder result = new StringBuilder();
        for (final String part : parts) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(part);
        }
        return result.toString();
    }
    
    @Override
    public final String toString() {
        return TextFormat.printToString(this);
    }
    
    public void writeTo(final CodedOutputStream output) throws IOException {
        final boolean isMessageSet = this.getDescriptorForType().getOptions().getMessageSetWireFormat();
        for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : this.getAllFields().entrySet()) {
            final Descriptors.FieldDescriptor field = entry.getKey();
            final Object value = entry.getValue();
            if (isMessageSet && field.isExtension() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && !field.isRepeated()) {
                output.writeMessageSetExtension(field.getNumber(), (MessageLite)value);
            }
            else {
                FieldSet.writeField(field, value, output);
            }
        }
        final UnknownFieldSet unknownFields = this.getUnknownFields();
        if (isMessageSet) {
            unknownFields.writeAsMessageSetTo(output);
        }
        else {
            unknownFields.writeTo(output);
        }
    }
    
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        final boolean isMessageSet = this.getDescriptorForType().getOptions().getMessageSetWireFormat();
        for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : this.getAllFields().entrySet()) {
            final Descriptors.FieldDescriptor field = entry.getKey();
            final Object value = entry.getValue();
            if (isMessageSet && field.isExtension() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && !field.isRepeated()) {
                size += CodedOutputStream.computeMessageSetExtensionSize(field.getNumber(), (MessageLite)value);
            }
            else {
                size += FieldSet.computeFieldSize(field, value);
            }
        }
        final UnknownFieldSet unknownFields = this.getUnknownFields();
        if (isMessageSet) {
            size += unknownFields.getSerializedSizeAsMessageSet();
        }
        else {
            size += unknownFields.getSerializedSize();
        }
        return this.memoizedSize = size;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Message)) {
            return false;
        }
        final Message otherMessage = (Message)other;
        return this.getDescriptorForType() == otherMessage.getDescriptorForType() && this.getAllFields().equals(otherMessage.getAllFields()) && this.getUnknownFields().equals(otherMessage.getUnknownFields());
    }
    
    @Override
    public int hashCode() {
        int hash = 41;
        hash = 19 * hash + this.getDescriptorForType().hashCode();
        hash = this.hashFields(hash, this.getAllFields());
        hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }
    
    protected int hashFields(int hash, final Map<Descriptors.FieldDescriptor, Object> map) {
        for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : map.entrySet()) {
            final Descriptors.FieldDescriptor field = entry.getKey();
            final Object value = entry.getValue();
            hash = 37 * hash + field.getNumber();
            if (field.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
                hash = 53 * hash + value.hashCode();
            }
            else if (field.isRepeated()) {
                final List<? extends Internal.EnumLite> list = (List<? extends Internal.EnumLite>)value;
                hash = 53 * hash + hashEnumList(list);
            }
            else {
                hash = 53 * hash + hashEnum((Internal.EnumLite)value);
            }
        }
        return hash;
    }
    
    protected static int hashLong(final long n) {
        return (int)(n ^ n >>> 32);
    }
    
    protected static int hashBoolean(final boolean b) {
        return b ? 1231 : 1237;
    }
    
    @Override
    UninitializedMessageException newUninitializedMessageException() {
        return Builder.newUninitializedMessageException(this);
    }
    
    protected static int hashEnum(final Internal.EnumLite e) {
        return e.getNumber();
    }
    
    protected static int hashEnumList(final List<? extends Internal.EnumLite> list) {
        int hash = 1;
        for (final Internal.EnumLite e : list) {
            hash = 31 * hash + hashEnum(e);
        }
        return hash;
    }
    
    public abstract static class Builder<BuilderType extends Builder> extends AbstractMessageLite.Builder<BuilderType> implements Message.Builder
    {
        @Override
        public abstract BuilderType clone();
        
        public BuilderType clear() {
            for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : this.getAllFields().entrySet()) {
                this.clearField(entry.getKey());
            }
            return (BuilderType)this;
        }
        
        public List<String> findInitializationErrors() {
            return findMissingFields(this);
        }
        
        public String getInitializationErrorString() {
            return delimitWithCommas(this.findInitializationErrors());
        }
        
        public BuilderType mergeFrom(final Message other) {
            if (other.getDescriptorForType() != this.getDescriptorForType()) {
                throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
            }
            for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : other.getAllFields().entrySet()) {
                final Descriptors.FieldDescriptor field = entry.getKey();
                if (field.isRepeated()) {
                    for (final Object element : entry.getValue()) {
                        this.addRepeatedField(field, element);
                    }
                }
                else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    final Message existingValue = (Message)this.getField(field);
                    if (existingValue == existingValue.getDefaultInstanceForType()) {
                        this.setField(field, entry.getValue());
                    }
                    else {
                        this.setField(field, existingValue.newBuilderForType().mergeFrom(existingValue).mergeFrom(entry.getValue()).build());
                    }
                }
                else {
                    this.setField(field, entry.getValue());
                }
            }
            this.mergeUnknownFields(other.getUnknownFields());
            return (BuilderType)this;
        }
        
        @Override
        public BuilderType mergeFrom(final CodedInputStream input) throws IOException {
            return this.mergeFrom(input, ExtensionRegistry.getEmptyRegistry());
        }
        
        @Override
        public BuilderType mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder(this.getUnknownFields());
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (mergeFieldFrom(input, unknownFields, extensionRegistry, this.getDescriptorForType(), this, null, tag));
            this.setUnknownFields(unknownFields.build());
            return (BuilderType)this;
        }
        
        private static void addRepeatedField(final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final Descriptors.FieldDescriptor field, final Object value) {
            if (builder != null) {
                builder.addRepeatedField(field, value);
            }
            else {
                extensions.addRepeatedField(field, value);
            }
        }
        
        private static void setField(final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final Descriptors.FieldDescriptor field, final Object value) {
            if (builder != null) {
                builder.setField(field, value);
            }
            else {
                extensions.setField(field, value);
            }
        }
        
        private static boolean hasOriginalMessage(final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final Descriptors.FieldDescriptor field) {
            if (builder != null) {
                return builder.hasField(field);
            }
            return extensions.hasField(field);
        }
        
        private static Message getOriginalMessage(final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final Descriptors.FieldDescriptor field) {
            if (builder != null) {
                return (Message)builder.getField(field);
            }
            return (Message)extensions.getField(field);
        }
        
        private static void mergeOriginalMessage(final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final Descriptors.FieldDescriptor field, final Message.Builder subBuilder) {
            final Message originalMessage = getOriginalMessage(builder, extensions, field);
            if (originalMessage != null) {
                subBuilder.mergeFrom(originalMessage);
            }
        }
        
        static boolean mergeFieldFrom(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final Descriptors.Descriptor type, final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions, final int tag) throws IOException {
            if (type.getOptions().getMessageSetWireFormat() && tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
                mergeMessageSetExtensionFromCodedStream(input, unknownFields, extensionRegistry, type, builder, extensions);
                return true;
            }
            final int wireType = WireFormat.getTagWireType(tag);
            final int fieldNumber = WireFormat.getTagFieldNumber(tag);
            Message defaultInstance = null;
            Descriptors.FieldDescriptor field;
            if (type.isExtensionNumber(fieldNumber)) {
                if (extensionRegistry instanceof ExtensionRegistry) {
                    final ExtensionRegistry.ExtensionInfo extension = ((ExtensionRegistry)extensionRegistry).findExtensionByNumber(type, fieldNumber);
                    if (extension == null) {
                        field = null;
                    }
                    else {
                        field = extension.descriptor;
                        defaultInstance = extension.defaultInstance;
                        if (defaultInstance == null && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                            throw new IllegalStateException("Message-typed extension lacked default instance: " + field.getFullName());
                        }
                    }
                }
                else {
                    field = null;
                }
            }
            else if (builder != null) {
                field = type.findFieldByNumber(fieldNumber);
            }
            else {
                field = null;
            }
            boolean unknown = false;
            boolean packed = false;
            if (field == null) {
                unknown = true;
            }
            else if (wireType == FieldSet.getWireFormatForFieldType(field.getLiteType(), false)) {
                packed = false;
            }
            else if (field.isPackable() && wireType == FieldSet.getWireFormatForFieldType(field.getLiteType(), true)) {
                packed = true;
            }
            else {
                unknown = true;
            }
            if (unknown) {
                return unknownFields.mergeFieldFrom(tag, input);
            }
            if (packed) {
                final int length = input.readRawVarint32();
                final int limit = input.pushLimit(length);
                if (field.getLiteType() == WireFormat.FieldType.ENUM) {
                    while (input.getBytesUntilLimit() > 0) {
                        final int rawValue = input.readEnum();
                        final Object value = field.getEnumType().findValueByNumber(rawValue);
                        if (value == null) {
                            return true;
                        }
                        addRepeatedField(builder, extensions, field, value);
                    }
                }
                else {
                    while (input.getBytesUntilLimit() > 0) {
                        final Object value2 = FieldSet.readPrimitiveField(input, field.getLiteType());
                        addRepeatedField(builder, extensions, field, value2);
                    }
                }
                input.popLimit(limit);
            }
            else {
                Object value3 = null;
                switch (field.getType()) {
                    case GROUP: {
                        Message.Builder subBuilder;
                        if (defaultInstance != null) {
                            subBuilder = defaultInstance.newBuilderForType();
                        }
                        else {
                            subBuilder = builder.newBuilderForField(field);
                        }
                        if (!field.isRepeated()) {
                            mergeOriginalMessage(builder, extensions, field, subBuilder);
                        }
                        input.readGroup(field.getNumber(), subBuilder, extensionRegistry);
                        value3 = subBuilder.buildPartial();
                        break;
                    }
                    case MESSAGE: {
                        Message.Builder subBuilder;
                        if (defaultInstance != null) {
                            subBuilder = defaultInstance.newBuilderForType();
                        }
                        else {
                            subBuilder = builder.newBuilderForField(field);
                        }
                        if (!field.isRepeated()) {
                            mergeOriginalMessage(builder, extensions, field, subBuilder);
                        }
                        input.readMessage(subBuilder, extensionRegistry);
                        value3 = subBuilder.buildPartial();
                        break;
                    }
                    case ENUM: {
                        final int rawValue2 = input.readEnum();
                        value3 = field.getEnumType().findValueByNumber(rawValue2);
                        if (value3 == null) {
                            unknownFields.mergeVarintField(fieldNumber, rawValue2);
                            return true;
                        }
                        break;
                    }
                    default: {
                        value3 = FieldSet.readPrimitiveField(input, field.getLiteType());
                        break;
                    }
                }
                if (field.isRepeated()) {
                    addRepeatedField(builder, extensions, field, value3);
                }
                else {
                    setField(builder, extensions, field, value3);
                }
            }
            return true;
        }
        
        private static void mergeMessageSetExtensionFromCodedStream(final CodedInputStream input, final UnknownFieldSet.Builder unknownFields, final ExtensionRegistryLite extensionRegistry, final Descriptors.Descriptor type, final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            int typeId = 0;
            ByteString rawBytes = null;
            ExtensionRegistry.ExtensionInfo extension = null;
            while (true) {
                final int tag = input.readTag();
                if (tag == 0) {
                    break;
                }
                if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
                    typeId = input.readUInt32();
                    if (typeId == 0 || !(extensionRegistry instanceof ExtensionRegistry)) {
                        continue;
                    }
                    extension = ((ExtensionRegistry)extensionRegistry).findExtensionByNumber(type, typeId);
                }
                else if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
                    if (typeId != 0 && extension != null && ExtensionRegistryLite.isEagerlyParseMessageSets()) {
                        eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, builder, extensions);
                        rawBytes = null;
                    }
                    else {
                        rawBytes = input.readBytes();
                    }
                }
                else {
                    if (!input.skipField(tag)) {
                        break;
                    }
                    continue;
                }
            }
            input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
            if (rawBytes != null && typeId != 0) {
                if (extension != null) {
                    mergeMessageSetExtensionFromBytes(rawBytes, extension, extensionRegistry, builder, extensions);
                }
                else if (rawBytes != null) {
                    unknownFields.mergeField(typeId, UnknownFieldSet.Field.newBuilder().addLengthDelimited(rawBytes).build());
                }
            }
        }
        
        private static void eagerlyMergeMessageSetExtension(final CodedInputStream input, final ExtensionRegistry.ExtensionInfo extension, final ExtensionRegistryLite extensionRegistry, final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            final Descriptors.FieldDescriptor field = extension.descriptor;
            Message value = null;
            if (hasOriginalMessage(builder, extensions, field)) {
                final Message originalMessage = getOriginalMessage(builder, extensions, field);
                final Message.Builder subBuilder = originalMessage.toBuilder();
                input.readMessage(subBuilder, extensionRegistry);
                value = subBuilder.buildPartial();
            }
            else {
                value = input.readMessage(extension.defaultInstance.getParserForType(), extensionRegistry);
            }
            if (builder != null) {
                builder.setField(field, value);
            }
            else {
                extensions.setField(field, value);
            }
        }
        
        private static void mergeMessageSetExtensionFromBytes(final ByteString rawBytes, final ExtensionRegistry.ExtensionInfo extension, final ExtensionRegistryLite extensionRegistry, final Message.Builder builder, final FieldSet<Descriptors.FieldDescriptor> extensions) throws IOException {
            final Descriptors.FieldDescriptor field = extension.descriptor;
            final boolean hasOriginalValue = hasOriginalMessage(builder, extensions, field);
            if (hasOriginalValue || ExtensionRegistryLite.isEagerlyParseMessageSets()) {
                Message value = null;
                if (hasOriginalValue) {
                    final Message originalMessage = getOriginalMessage(builder, extensions, field);
                    final Message.Builder subBuilder = originalMessage.toBuilder();
                    subBuilder.mergeFrom(rawBytes, extensionRegistry);
                    value = subBuilder.buildPartial();
                }
                else {
                    value = (Message)extension.defaultInstance.getParserForType().parsePartialFrom(rawBytes, extensionRegistry);
                }
                setField(builder, extensions, field, value);
            }
            else {
                final LazyField lazyField = new LazyField(extension.defaultInstance, extensionRegistry, rawBytes);
                if (builder != null) {
                    if (builder instanceof GeneratedMessage.ExtendableBuilder) {
                        builder.setField(field, lazyField);
                    }
                    else {
                        builder.setField(field, lazyField.getValue());
                    }
                }
                else {
                    extensions.setField(field, lazyField);
                }
            }
        }
        
        public BuilderType mergeUnknownFields(final UnknownFieldSet unknownFields) {
            this.setUnknownFields(UnknownFieldSet.newBuilder(this.getUnknownFields()).mergeFrom(unknownFields).build());
            return (BuilderType)this;
        }
        
        public Message.Builder getFieldBuilder(final Descriptors.FieldDescriptor field) {
            throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
        }
        
        protected static UninitializedMessageException newUninitializedMessageException(final Message message) {
            return new UninitializedMessageException(findMissingFields(message));
        }
        
        private static List<String> findMissingFields(final MessageOrBuilder message) {
            final List<String> results = new ArrayList<String>();
            findMissingFields(message, "", results);
            return results;
        }
        
        private static void findMissingFields(final MessageOrBuilder message, final String prefix, final List<String> results) {
            for (final Descriptors.FieldDescriptor field : message.getDescriptorForType().getFields()) {
                if (field.isRequired() && !message.hasField(field)) {
                    results.add(prefix + field.getName());
                }
            }
            for (final Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
                final Descriptors.FieldDescriptor field2 = entry.getKey();
                final Object value = entry.getValue();
                if (field2.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    if (field2.isRepeated()) {
                        int i = 0;
                        for (final Object element : (List)value) {
                            findMissingFields((MessageOrBuilder)element, subMessagePrefix(prefix, field2, i++), results);
                        }
                    }
                    else {
                        if (!message.hasField(field2)) {
                            continue;
                        }
                        findMissingFields((MessageOrBuilder)value, subMessagePrefix(prefix, field2, -1), results);
                    }
                }
            }
        }
        
        private static String subMessagePrefix(final String prefix, final Descriptors.FieldDescriptor field, final int index) {
            final StringBuilder result = new StringBuilder(prefix);
            if (field.isExtension()) {
                result.append('(').append(field.getFullName()).append(')');
            }
            else {
                result.append(field.getName());
            }
            if (index != -1) {
                result.append('[').append(index).append(']');
            }
            result.append('.');
            return result.toString();
        }
        
        @Override
        public BuilderType mergeFrom(final ByteString data) throws InvalidProtocolBufferException {
            return super.mergeFrom(data);
        }
        
        @Override
        public BuilderType mergeFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return super.mergeFrom(data, extensionRegistry);
        }
        
        @Override
        public BuilderType mergeFrom(final byte[] data) throws InvalidProtocolBufferException {
            return super.mergeFrom(data);
        }
        
        @Override
        public BuilderType mergeFrom(final byte[] data, final int off, final int len) throws InvalidProtocolBufferException {
            return super.mergeFrom(data, off, len);
        }
        
        @Override
        public BuilderType mergeFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return super.mergeFrom(data, extensionRegistry);
        }
        
        @Override
        public BuilderType mergeFrom(final byte[] data, final int off, final int len, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return super.mergeFrom(data, off, len, extensionRegistry);
        }
        
        @Override
        public BuilderType mergeFrom(final InputStream input) throws IOException {
            return super.mergeFrom(input);
        }
        
        @Override
        public BuilderType mergeFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return super.mergeFrom(input, extensionRegistry);
        }
        
        @Override
        public boolean mergeDelimitedFrom(final InputStream input) throws IOException {
            return super.mergeDelimitedFrom(input);
        }
        
        @Override
        public boolean mergeDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return super.mergeDelimitedFrom(input, extensionRegistry);
        }
    }
}
