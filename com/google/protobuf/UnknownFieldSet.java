// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.Collections;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public final class UnknownFieldSet implements MessageLite
{
    private static final UnknownFieldSet defaultInstance;
    private Map<Integer, Field> fields;
    private static final Parser PARSER;
    
    private UnknownFieldSet() {
    }
    
    public static Builder newBuilder() {
        return create();
    }
    
    public static Builder newBuilder(final UnknownFieldSet copyFrom) {
        return newBuilder().mergeFrom(copyFrom);
    }
    
    public static UnknownFieldSet getDefaultInstance() {
        return UnknownFieldSet.defaultInstance;
    }
    
    public UnknownFieldSet getDefaultInstanceForType() {
        return UnknownFieldSet.defaultInstance;
    }
    
    private UnknownFieldSet(final Map<Integer, Field> fields) {
        this.fields = fields;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof UnknownFieldSet && this.fields.equals(((UnknownFieldSet)other).fields));
    }
    
    @Override
    public int hashCode() {
        return this.fields.hashCode();
    }
    
    public Map<Integer, Field> asMap() {
        return this.fields;
    }
    
    public boolean hasField(final int number) {
        return this.fields.containsKey(number);
    }
    
    public Field getField(final int number) {
        final Field result = this.fields.get(number);
        return (result == null) ? Field.getDefaultInstance() : result;
    }
    
    public void writeTo(final CodedOutputStream output) throws IOException {
        for (final Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            entry.getValue().writeTo(entry.getKey(), output);
        }
    }
    
    @Override
    public String toString() {
        return TextFormat.printToString(this);
    }
    
    public ByteString toByteString() {
        try {
            final ByteString.CodedBuilder out = ByteString.newCodedBuilder(this.getSerializedSize());
            this.writeTo(out.getCodedOutput());
            return out.build();
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", e);
        }
    }
    
    public byte[] toByteArray() {
        try {
            final byte[] result = new byte[this.getSerializedSize()];
            final CodedOutputStream output = CodedOutputStream.newInstance(result);
            this.writeTo(output);
            output.checkNoSpaceLeft();
            return result;
        }
        catch (IOException e) {
            throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", e);
        }
    }
    
    public void writeTo(final OutputStream output) throws IOException {
        final CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        this.writeTo(codedOutput);
        codedOutput.flush();
    }
    
    public void writeDelimitedTo(final OutputStream output) throws IOException {
        final CodedOutputStream codedOutput = CodedOutputStream.newInstance(output);
        codedOutput.writeRawVarint32(this.getSerializedSize());
        this.writeTo(codedOutput);
        codedOutput.flush();
    }
    
    public int getSerializedSize() {
        int result = 0;
        for (final Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSize(entry.getKey());
        }
        return result;
    }
    
    public void writeAsMessageSetTo(final CodedOutputStream output) throws IOException {
        for (final Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            entry.getValue().writeAsMessageSetExtensionTo(entry.getKey(), output);
        }
    }
    
    public int getSerializedSizeAsMessageSet() {
        int result = 0;
        for (final Map.Entry<Integer, Field> entry : this.fields.entrySet()) {
            result += entry.getValue().getSerializedSizeAsMessageSetExtension(entry.getKey());
        }
        return result;
    }
    
    public boolean isInitialized() {
        return true;
    }
    
    public static UnknownFieldSet parseFrom(final CodedInputStream input) throws IOException {
        return newBuilder().mergeFrom(input).build();
    }
    
    public static UnknownFieldSet parseFrom(final ByteString data) throws InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).build();
    }
    
    public static UnknownFieldSet parseFrom(final byte[] data) throws InvalidProtocolBufferException {
        return newBuilder().mergeFrom(data).build();
    }
    
    public static UnknownFieldSet parseFrom(final InputStream input) throws IOException {
        return newBuilder().mergeFrom(input).build();
    }
    
    public Builder newBuilderForType() {
        return newBuilder();
    }
    
    public Builder toBuilder() {
        return newBuilder().mergeFrom(this);
    }
    
    public final Parser getParserForType() {
        return UnknownFieldSet.PARSER;
    }
    
    static {
        defaultInstance = new UnknownFieldSet(Collections.emptyMap());
        PARSER = new Parser();
    }
    
    public static final class Builder implements MessageLite.Builder
    {
        private Map<Integer, Field> fields;
        private int lastFieldNumber;
        private Field.Builder lastField;
        
        private Builder() {
        }
        
        private static Builder create() {
            final Builder builder = new Builder();
            builder.reinitialize();
            return builder;
        }
        
        private Field.Builder getFieldBuilder(final int number) {
            if (this.lastField != null) {
                if (number == this.lastFieldNumber) {
                    return this.lastField;
                }
                this.addField(this.lastFieldNumber, this.lastField.build());
            }
            if (number == 0) {
                return null;
            }
            final Field existing = this.fields.get(number);
            this.lastFieldNumber = number;
            this.lastField = Field.newBuilder();
            if (existing != null) {
                this.lastField.mergeFrom(existing);
            }
            return this.lastField;
        }
        
        public UnknownFieldSet build() {
            this.getFieldBuilder(0);
            UnknownFieldSet result;
            if (this.fields.isEmpty()) {
                result = UnknownFieldSet.getDefaultInstance();
            }
            else {
                result = new UnknownFieldSet(Collections.unmodifiableMap((Map<?, ?>)this.fields), null);
            }
            this.fields = null;
            return result;
        }
        
        public UnknownFieldSet buildPartial() {
            return this.build();
        }
        
        public Builder clone() {
            this.getFieldBuilder(0);
            return UnknownFieldSet.newBuilder().mergeFrom(new UnknownFieldSet(this.fields, null));
        }
        
        public UnknownFieldSet getDefaultInstanceForType() {
            return UnknownFieldSet.getDefaultInstance();
        }
        
        private void reinitialize() {
            this.fields = Collections.emptyMap();
            this.lastFieldNumber = 0;
            this.lastField = null;
        }
        
        public Builder clear() {
            this.reinitialize();
            return this;
        }
        
        public Builder mergeFrom(final UnknownFieldSet other) {
            if (other != UnknownFieldSet.getDefaultInstance()) {
                for (final Map.Entry<Integer, Field> entry : other.fields.entrySet()) {
                    this.mergeField(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }
        
        public Builder mergeField(final int number, final Field field) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            if (this.hasField(number)) {
                this.getFieldBuilder(number).mergeFrom(field);
            }
            else {
                this.addField(number, field);
            }
            return this;
        }
        
        public Builder mergeVarintField(final int number, final int value) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            this.getFieldBuilder(number).addVarint(value);
            return this;
        }
        
        public boolean hasField(final int number) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            return number == this.lastFieldNumber || this.fields.containsKey(number);
        }
        
        public Builder addField(final int number, final Field field) {
            if (number == 0) {
                throw new IllegalArgumentException("Zero is not a valid field number.");
            }
            if (this.lastField != null && this.lastFieldNumber == number) {
                this.lastField = null;
                this.lastFieldNumber = 0;
            }
            if (this.fields.isEmpty()) {
                this.fields = new TreeMap<Integer, Field>();
            }
            this.fields.put(number, field);
            return this;
        }
        
        public Map<Integer, Field> asMap() {
            this.getFieldBuilder(0);
            return Collections.unmodifiableMap((Map<? extends Integer, ? extends Field>)this.fields);
        }
        
        public Builder mergeFrom(final CodedInputStream input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
            } while (tag != 0 && this.mergeFieldFrom(tag, input));
            return this;
        }
        
        public boolean mergeFieldFrom(final int tag, final CodedInputStream input) throws IOException {
            final int number = WireFormat.getTagFieldNumber(tag);
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.getFieldBuilder(number).addVarint(input.readInt64());
                    return true;
                }
                case 1: {
                    this.getFieldBuilder(number).addFixed64(input.readFixed64());
                    return true;
                }
                case 2: {
                    this.getFieldBuilder(number).addLengthDelimited(input.readBytes());
                    return true;
                }
                case 3: {
                    final Builder subBuilder = UnknownFieldSet.newBuilder();
                    input.readGroup(number, subBuilder, ExtensionRegistry.getEmptyRegistry());
                    this.getFieldBuilder(number).addGroup(subBuilder.build());
                    return true;
                }
                case 4: {
                    return false;
                }
                case 5: {
                    this.getFieldBuilder(number).addFixed32(input.readFixed32());
                    return true;
                }
                default: {
                    throw InvalidProtocolBufferException.invalidWireType();
                }
            }
        }
        
        public Builder mergeFrom(final ByteString data) throws InvalidProtocolBufferException {
            try {
                final CodedInputStream input = data.newCodedInput();
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e2) {
                throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", e2);
            }
        }
        
        public Builder mergeFrom(final byte[] data) throws InvalidProtocolBufferException {
            try {
                final CodedInputStream input = CodedInputStream.newInstance(data);
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }
        
        public Builder mergeFrom(final InputStream input) throws IOException {
            final CodedInputStream codedInput = CodedInputStream.newInstance(input);
            this.mergeFrom(codedInput);
            codedInput.checkLastTagWas(0);
            return this;
        }
        
        public boolean mergeDelimitedFrom(final InputStream input) throws IOException {
            final int firstByte = input.read();
            if (firstByte == -1) {
                return false;
            }
            final int size = CodedInputStream.readRawVarint32(firstByte, input);
            final InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
            this.mergeFrom(limitedInput);
            return true;
        }
        
        public boolean mergeDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeDelimitedFrom(input);
        }
        
        public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeFrom(input);
        }
        
        public Builder mergeFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data);
        }
        
        public Builder mergeFrom(final byte[] data, final int off, final int len) throws InvalidProtocolBufferException {
            try {
                final CodedInputStream input = CodedInputStream.newInstance(data, off, len);
                this.mergeFrom(input);
                input.checkLastTagWas(0);
                return this;
            }
            catch (InvalidProtocolBufferException e) {
                throw e;
            }
            catch (IOException e2) {
                throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", e2);
            }
        }
        
        public Builder mergeFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data);
        }
        
        public Builder mergeFrom(final byte[] data, final int off, final int len, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return this.mergeFrom(data, off, len);
        }
        
        public Builder mergeFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return this.mergeFrom(input);
        }
        
        public boolean isInitialized() {
            return true;
        }
    }
    
    public static final class Field
    {
        private static final Field fieldDefaultInstance;
        private List<Long> varint;
        private List<Integer> fixed32;
        private List<Long> fixed64;
        private List<ByteString> lengthDelimited;
        private List<UnknownFieldSet> group;
        
        private Field() {
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public static Builder newBuilder(final Field copyFrom) {
            return newBuilder().mergeFrom(copyFrom);
        }
        
        public static Field getDefaultInstance() {
            return Field.fieldDefaultInstance;
        }
        
        public List<Long> getVarintList() {
            return this.varint;
        }
        
        public List<Integer> getFixed32List() {
            return this.fixed32;
        }
        
        public List<Long> getFixed64List() {
            return this.fixed64;
        }
        
        public List<ByteString> getLengthDelimitedList() {
            return this.lengthDelimited;
        }
        
        public List<UnknownFieldSet> getGroupList() {
            return this.group;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof Field && Arrays.equals(this.getIdentityArray(), ((Field)other).getIdentityArray()));
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.getIdentityArray());
        }
        
        private Object[] getIdentityArray() {
            return new Object[] { this.varint, this.fixed32, this.fixed64, this.lengthDelimited, this.group };
        }
        
        public void writeTo(final int fieldNumber, final CodedOutputStream output) throws IOException {
            for (final long value : this.varint) {
                output.writeUInt64(fieldNumber, value);
            }
            for (final int value2 : this.fixed32) {
                output.writeFixed32(fieldNumber, value2);
            }
            for (final long value : this.fixed64) {
                output.writeFixed64(fieldNumber, value);
            }
            for (final ByteString value3 : this.lengthDelimited) {
                output.writeBytes(fieldNumber, value3);
            }
            for (final UnknownFieldSet value4 : this.group) {
                output.writeGroup(fieldNumber, value4);
            }
        }
        
        public int getSerializedSize(final int fieldNumber) {
            int result = 0;
            for (final long value : this.varint) {
                result += CodedOutputStream.computeUInt64Size(fieldNumber, value);
            }
            for (final int value2 : this.fixed32) {
                result += CodedOutputStream.computeFixed32Size(fieldNumber, value2);
            }
            for (final long value : this.fixed64) {
                result += CodedOutputStream.computeFixed64Size(fieldNumber, value);
            }
            for (final ByteString value3 : this.lengthDelimited) {
                result += CodedOutputStream.computeBytesSize(fieldNumber, value3);
            }
            for (final UnknownFieldSet value4 : this.group) {
                result += CodedOutputStream.computeGroupSize(fieldNumber, value4);
            }
            return result;
        }
        
        public void writeAsMessageSetExtensionTo(final int fieldNumber, final CodedOutputStream output) throws IOException {
            for (final ByteString value : this.lengthDelimited) {
                output.writeRawMessageSetExtension(fieldNumber, value);
            }
        }
        
        public int getSerializedSizeAsMessageSetExtension(final int fieldNumber) {
            int result = 0;
            for (final ByteString value : this.lengthDelimited) {
                result += CodedOutputStream.computeRawMessageSetExtensionSize(fieldNumber, value);
            }
            return result;
        }
        
        static {
            fieldDefaultInstance = newBuilder().build();
        }
        
        public static final class Builder
        {
            private Field result;
            
            private Builder() {
            }
            
            private static Builder create() {
                final Builder builder = new Builder();
                builder.result = new Field();
                return builder;
            }
            
            public Field build() {
                if (this.result.varint == null) {
                    this.result.varint = (List<Long>)Collections.emptyList();
                }
                else {
                    this.result.varint = (List<Long>)Collections.unmodifiableList((List<?>)this.result.varint);
                }
                if (this.result.fixed32 == null) {
                    this.result.fixed32 = (List<Integer>)Collections.emptyList();
                }
                else {
                    this.result.fixed32 = (List<Integer>)Collections.unmodifiableList((List<?>)this.result.fixed32);
                }
                if (this.result.fixed64 == null) {
                    this.result.fixed64 = (List<Long>)Collections.emptyList();
                }
                else {
                    this.result.fixed64 = (List<Long>)Collections.unmodifiableList((List<?>)this.result.fixed64);
                }
                if (this.result.lengthDelimited == null) {
                    this.result.lengthDelimited = (List<ByteString>)Collections.emptyList();
                }
                else {
                    this.result.lengthDelimited = (List<ByteString>)Collections.unmodifiableList((List<?>)this.result.lengthDelimited);
                }
                if (this.result.group == null) {
                    this.result.group = (List<UnknownFieldSet>)Collections.emptyList();
                }
                else {
                    this.result.group = (List<UnknownFieldSet>)Collections.unmodifiableList((List<?>)this.result.group);
                }
                final Field returnMe = this.result;
                this.result = null;
                return returnMe;
            }
            
            public Builder clear() {
                this.result = new Field();
                return this;
            }
            
            public Builder mergeFrom(final Field other) {
                if (!other.varint.isEmpty()) {
                    if (this.result.varint == null) {
                        this.result.varint = (List<Long>)new ArrayList();
                    }
                    this.result.varint.addAll(other.varint);
                }
                if (!other.fixed32.isEmpty()) {
                    if (this.result.fixed32 == null) {
                        this.result.fixed32 = (List<Integer>)new ArrayList();
                    }
                    this.result.fixed32.addAll(other.fixed32);
                }
                if (!other.fixed64.isEmpty()) {
                    if (this.result.fixed64 == null) {
                        this.result.fixed64 = (List<Long>)new ArrayList();
                    }
                    this.result.fixed64.addAll(other.fixed64);
                }
                if (!other.lengthDelimited.isEmpty()) {
                    if (this.result.lengthDelimited == null) {
                        this.result.lengthDelimited = (List<ByteString>)new ArrayList();
                    }
                    this.result.lengthDelimited.addAll(other.lengthDelimited);
                }
                if (!other.group.isEmpty()) {
                    if (this.result.group == null) {
                        this.result.group = (List<UnknownFieldSet>)new ArrayList();
                    }
                    this.result.group.addAll(other.group);
                }
                return this;
            }
            
            public Builder addVarint(final long value) {
                if (this.result.varint == null) {
                    this.result.varint = (List<Long>)new ArrayList();
                }
                this.result.varint.add(value);
                return this;
            }
            
            public Builder addFixed32(final int value) {
                if (this.result.fixed32 == null) {
                    this.result.fixed32 = (List<Integer>)new ArrayList();
                }
                this.result.fixed32.add(value);
                return this;
            }
            
            public Builder addFixed64(final long value) {
                if (this.result.fixed64 == null) {
                    this.result.fixed64 = (List<Long>)new ArrayList();
                }
                this.result.fixed64.add(value);
                return this;
            }
            
            public Builder addLengthDelimited(final ByteString value) {
                if (this.result.lengthDelimited == null) {
                    this.result.lengthDelimited = (List<ByteString>)new ArrayList();
                }
                this.result.lengthDelimited.add(value);
                return this;
            }
            
            public Builder addGroup(final UnknownFieldSet value) {
                if (this.result.group == null) {
                    this.result.group = (List<UnknownFieldSet>)new ArrayList();
                }
                this.result.group.add(value);
                return this;
            }
        }
    }
    
    public static final class Parser extends AbstractParser<UnknownFieldSet>
    {
        public UnknownFieldSet parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            final Builder builder = UnknownFieldSet.newBuilder();
            try {
                builder.mergeFrom(input);
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(builder.buildPartial());
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(builder.buildPartial());
            }
            return builder.buildPartial();
        }
    }
}
