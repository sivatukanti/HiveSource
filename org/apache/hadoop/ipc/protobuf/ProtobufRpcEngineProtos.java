// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protobuf;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.AbstractMessage;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ByteString;
import java.io.IOException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class ProtobufRpcEngineProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_RequestHeaderProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RequestHeaderProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private ProtobufRpcEngineProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ProtobufRpcEngineProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0017ProtobufRpcEngine.proto\u0012\rhadoop.common\"k\n\u0012RequestHeaderProto\u0012\u0012\n\nmethodName\u0018\u0001 \u0002(\t\u0012\"\n\u001adeclaringClassProtocolName\u0018\u0002 \u0002(\t\u0012\u001d\n\u0015clientProtocolVersion\u0018\u0003 \u0002(\u0004B<\n\u001eorg.apache.hadoop.ipc.protobufB\u0017ProtobufRpcEngineProtos \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ProtobufRpcEngineProtos.descriptor = root;
                ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_descriptor = ProtobufRpcEngineProtos.getDescriptor().getMessageTypes().get(0);
                ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_descriptor, new String[] { "MethodName", "DeclaringClassProtocolName", "ClientProtocolVersion" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class RequestHeaderProto extends GeneratedMessage implements RequestHeaderProtoOrBuilder
    {
        private static final RequestHeaderProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RequestHeaderProto> PARSER;
        private int bitField0_;
        public static final int METHODNAME_FIELD_NUMBER = 1;
        private Object methodName_;
        public static final int DECLARINGCLASSPROTOCOLNAME_FIELD_NUMBER = 2;
        private Object declaringClassProtocolName_;
        public static final int CLIENTPROTOCOLVERSION_FIELD_NUMBER = 3;
        private long clientProtocolVersion_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RequestHeaderProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RequestHeaderProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RequestHeaderProto getDefaultInstance() {
            return RequestHeaderProto.defaultInstance;
        }
        
        @Override
        public RequestHeaderProto getDefaultInstanceForType() {
            return RequestHeaderProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RequestHeaderProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
            final int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.methodName_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.declaringClassProtocolName_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.clientProtocolVersion_ = input.readUInt64();
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RequestHeaderProto.class, Builder.class);
        }
        
        @Override
        public Parser<RequestHeaderProto> getParserForType() {
            return RequestHeaderProto.PARSER;
        }
        
        @Override
        public boolean hasMethodName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getMethodName() {
            final Object ref = this.methodName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.methodName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getMethodNameBytes() {
            final Object ref = this.methodName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.methodName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasDeclaringClassProtocolName() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getDeclaringClassProtocolName() {
            final Object ref = this.declaringClassProtocolName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.declaringClassProtocolName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDeclaringClassProtocolNameBytes() {
            final Object ref = this.declaringClassProtocolName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.declaringClassProtocolName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasClientProtocolVersion() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public long getClientProtocolVersion() {
            return this.clientProtocolVersion_;
        }
        
        private void initFields() {
            this.methodName_ = "";
            this.declaringClassProtocolName_ = "";
            this.clientProtocolVersion_ = 0L;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasMethodName()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasDeclaringClassProtocolName()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasClientProtocolVersion()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getMethodNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getDeclaringClassProtocolNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeUInt64(3, this.clientProtocolVersion_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getMethodNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getDeclaringClassProtocolNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeUInt64Size(3, this.clientProtocolVersion_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof RequestHeaderProto)) {
                return super.equals(obj);
            }
            final RequestHeaderProto other = (RequestHeaderProto)obj;
            boolean result = true;
            result = (result && this.hasMethodName() == other.hasMethodName());
            if (this.hasMethodName()) {
                result = (result && this.getMethodName().equals(other.getMethodName()));
            }
            result = (result && this.hasDeclaringClassProtocolName() == other.hasDeclaringClassProtocolName());
            if (this.hasDeclaringClassProtocolName()) {
                result = (result && this.getDeclaringClassProtocolName().equals(other.getDeclaringClassProtocolName()));
            }
            result = (result && this.hasClientProtocolVersion() == other.hasClientProtocolVersion());
            if (this.hasClientProtocolVersion()) {
                result = (result && this.getClientProtocolVersion() == other.getClientProtocolVersion());
            }
            result = (result && this.getUnknownFields().equals(other.getUnknownFields()));
            return result;
        }
        
        @Override
        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            }
            int hash = 41;
            hash = 19 * hash + this.getDescriptorForType().hashCode();
            if (this.hasMethodName()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getMethodName().hashCode();
            }
            if (this.hasDeclaringClassProtocolName()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getDeclaringClassProtocolName().hashCode();
            }
            if (this.hasClientProtocolVersion()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + AbstractMessage.hashLong(this.getClientProtocolVersion());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RequestHeaderProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RequestHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RequestHeaderProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RequestHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RequestHeaderProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RequestHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RequestHeaderProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RequestHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RequestHeaderProto parseFrom(final InputStream input) throws IOException {
            return RequestHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RequestHeaderProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RequestHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RequestHeaderProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RequestHeaderProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RequestHeaderProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RequestHeaderProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RequestHeaderProto parseFrom(final CodedInputStream input) throws IOException {
            return RequestHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RequestHeaderProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RequestHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RequestHeaderProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        @Override
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            RequestHeaderProto.PARSER = new AbstractParser<RequestHeaderProto>() {
                @Override
                public RequestHeaderProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RequestHeaderProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RequestHeaderProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RequestHeaderProtoOrBuilder
        {
            private int bitField0_;
            private Object methodName_;
            private Object declaringClassProtocolName_;
            private long clientProtocolVersion_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RequestHeaderProto.class, Builder.class);
            }
            
            private Builder() {
                this.methodName_ = "";
                this.declaringClassProtocolName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.methodName_ = "";
                this.declaringClassProtocolName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RequestHeaderProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.methodName_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.declaringClassProtocolName_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.clientProtocolVersion_ = 0L;
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtobufRpcEngineProtos.internal_static_hadoop_common_RequestHeaderProto_descriptor;
            }
            
            @Override
            public RequestHeaderProto getDefaultInstanceForType() {
                return RequestHeaderProto.getDefaultInstance();
            }
            
            @Override
            public RequestHeaderProto build() {
                final RequestHeaderProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RequestHeaderProto buildPartial() {
                final RequestHeaderProto result = new RequestHeaderProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.methodName_ = this.methodName_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.declaringClassProtocolName_ = this.declaringClassProtocolName_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.clientProtocolVersion_ = this.clientProtocolVersion_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RequestHeaderProto) {
                    return this.mergeFrom((RequestHeaderProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RequestHeaderProto other) {
                if (other == RequestHeaderProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMethodName()) {
                    this.bitField0_ |= 0x1;
                    this.methodName_ = other.methodName_;
                    this.onChanged();
                }
                if (other.hasDeclaringClassProtocolName()) {
                    this.bitField0_ |= 0x2;
                    this.declaringClassProtocolName_ = other.declaringClassProtocolName_;
                    this.onChanged();
                }
                if (other.hasClientProtocolVersion()) {
                    this.setClientProtocolVersion(other.getClientProtocolVersion());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasMethodName() && this.hasDeclaringClassProtocolName() && this.hasClientProtocolVersion();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RequestHeaderProto parsedMessage = null;
                try {
                    parsedMessage = RequestHeaderProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RequestHeaderProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            @Override
            public boolean hasMethodName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getMethodName() {
                final Object ref = this.methodName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.methodName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getMethodNameBytes() {
                final Object ref = this.methodName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.methodName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setMethodName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.methodName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMethodName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.methodName_ = RequestHeaderProto.getDefaultInstance().getMethodName();
                this.onChanged();
                return this;
            }
            
            public Builder setMethodNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.methodName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDeclaringClassProtocolName() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getDeclaringClassProtocolName() {
                final Object ref = this.declaringClassProtocolName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.declaringClassProtocolName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDeclaringClassProtocolNameBytes() {
                final Object ref = this.declaringClassProtocolName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.declaringClassProtocolName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDeclaringClassProtocolName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.declaringClassProtocolName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDeclaringClassProtocolName() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.declaringClassProtocolName_ = RequestHeaderProto.getDefaultInstance().getDeclaringClassProtocolName();
                this.onChanged();
                return this;
            }
            
            public Builder setDeclaringClassProtocolNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.declaringClassProtocolName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasClientProtocolVersion() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public long getClientProtocolVersion() {
                return this.clientProtocolVersion_;
            }
            
            public Builder setClientProtocolVersion(final long value) {
                this.bitField0_ |= 0x4;
                this.clientProtocolVersion_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClientProtocolVersion() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.clientProtocolVersion_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface RequestHeaderProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasMethodName();
        
        String getMethodName();
        
        ByteString getMethodNameBytes();
        
        boolean hasDeclaringClassProtocolName();
        
        String getDeclaringClassProtocolName();
        
        ByteString getDeclaringClassProtocolNameBytes();
        
        boolean hasClientProtocolVersion();
        
        long getClientProtocolVersion();
    }
}
