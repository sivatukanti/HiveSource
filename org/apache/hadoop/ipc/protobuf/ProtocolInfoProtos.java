// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protobuf;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
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

public final class ProtocolInfoProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetProtocolVersionsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_ProtocolVersionProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_ProtocolVersionProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetProtocolVersionsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetProtocolSignatureRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetProtocolSignatureResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_ProtocolSignatureProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_ProtocolSignatureProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private ProtocolInfoProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ProtocolInfoProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0012ProtocolInfo.proto\u0012\rhadoop.common\"3\n\u001fGetProtocolVersionsRequestProto\u0012\u0010\n\bprotocol\u0018\u0001 \u0002(\t\"9\n\u0014ProtocolVersionProto\u0012\u000f\n\u0007rpcKind\u0018\u0001 \u0002(\t\u0012\u0010\n\bversions\u0018\u0002 \u0003(\u0004\"a\n GetProtocolVersionsResponseProto\u0012=\n\u0010protocolVersions\u0018\u0001 \u0003(\u000b2#.hadoop.common.ProtocolVersionProto\"E\n GetProtocolSignatureRequestProto\u0012\u0010\n\bprotocol\u0018\u0001 \u0002(\t\u0012\u000f\n\u0007rpcKind\u0018\u0002 \u0002(\t\"e\n!GetProtocolSignatureResponseProto\u0012@\n\u0011protocolSignature\u0018\u0001 \u0003(\u000b2%.hadoop.common.Pr", "otocolSignatureProto\":\n\u0016ProtocolSignatureProto\u0012\u000f\n\u0007version\u0018\u0001 \u0002(\u0004\u0012\u000f\n\u0007methods\u0018\u0002 \u0003(\r2\u0088\u0002\n\u0013ProtocolInfoService\u0012v\n\u0013getProtocolVersions\u0012..hadoop.common.GetProtocolVersionsRequestProto\u001a/.hadoop.common.GetProtocolVersionsResponseProto\u0012y\n\u0014getProtocolSignature\u0012/.hadoop.common.GetProtocolSignatureRequestProto\u001a0.hadoop.common.GetProtocolSignatureResponseProtoB:\n\u001eorg.apache.hadoop.ipc.protobufB\u0012ProtocolInfoProto", "s\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ProtocolInfoProtos.descriptor = root;
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(0);
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor, new String[] { "Protocol" });
                ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(1);
                ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_descriptor, new String[] { "RpcKind", "Versions" });
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(2);
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor, new String[] { "ProtocolVersions" });
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(3);
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor, new String[] { "Protocol", "RpcKind" });
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(4);
                ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor, new String[] { "ProtocolSignature" });
                ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_descriptor = ProtocolInfoProtos.getDescriptor().getMessageTypes().get(5);
                ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_descriptor, new String[] { "Version", "Methods" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class GetProtocolVersionsRequestProto extends GeneratedMessage implements GetProtocolVersionsRequestProtoOrBuilder
    {
        private static final GetProtocolVersionsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetProtocolVersionsRequestProto> PARSER;
        private int bitField0_;
        public static final int PROTOCOL_FIELD_NUMBER = 1;
        private Object protocol_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetProtocolVersionsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetProtocolVersionsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetProtocolVersionsRequestProto getDefaultInstance() {
            return GetProtocolVersionsRequestProto.defaultInstance;
        }
        
        @Override
        public GetProtocolVersionsRequestProto getDefaultInstanceForType() {
            return GetProtocolVersionsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetProtocolVersionsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.protocol_ = input.readBytes();
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
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolVersionsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetProtocolVersionsRequestProto> getParserForType() {
            return GetProtocolVersionsRequestProto.PARSER;
        }
        
        @Override
        public boolean hasProtocol() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getProtocol() {
            final Object ref = this.protocol_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.protocol_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getProtocolBytes() {
            final Object ref = this.protocol_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.protocol_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.protocol_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasProtocol()) {
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
                output.writeBytes(1, this.getProtocolBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getProtocolBytes());
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
            if (!(obj instanceof GetProtocolVersionsRequestProto)) {
                return super.equals(obj);
            }
            final GetProtocolVersionsRequestProto other = (GetProtocolVersionsRequestProto)obj;
            boolean result = true;
            result = (result && this.hasProtocol() == other.hasProtocol());
            if (this.hasProtocol()) {
                result = (result && this.getProtocol().equals(other.getProtocol()));
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
            if (this.hasProtocol()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getProtocol().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final InputStream input) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetProtocolVersionsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetProtocolVersionsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolVersionsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetProtocolVersionsRequestProto prototype) {
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
            GetProtocolVersionsRequestProto.PARSER = new AbstractParser<GetProtocolVersionsRequestProto>() {
                @Override
                public GetProtocolVersionsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetProtocolVersionsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetProtocolVersionsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetProtocolVersionsRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object protocol_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolVersionsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.protocol_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.protocol_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetProtocolVersionsRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.protocol_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsRequestProto_descriptor;
            }
            
            @Override
            public GetProtocolVersionsRequestProto getDefaultInstanceForType() {
                return GetProtocolVersionsRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetProtocolVersionsRequestProto build() {
                final GetProtocolVersionsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetProtocolVersionsRequestProto buildPartial() {
                final GetProtocolVersionsRequestProto result = new GetProtocolVersionsRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.protocol_ = this.protocol_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetProtocolVersionsRequestProto) {
                    return this.mergeFrom((GetProtocolVersionsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetProtocolVersionsRequestProto other) {
                if (other == GetProtocolVersionsRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasProtocol()) {
                    this.bitField0_ |= 0x1;
                    this.protocol_ = other.protocol_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasProtocol();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetProtocolVersionsRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetProtocolVersionsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetProtocolVersionsRequestProto)e.getUnfinishedMessage();
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
            public boolean hasProtocol() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getProtocol() {
                final Object ref = this.protocol_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.protocol_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getProtocolBytes() {
                final Object ref = this.protocol_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.protocol_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setProtocol(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearProtocol() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.protocol_ = GetProtocolVersionsRequestProto.getDefaultInstance().getProtocol();
                this.onChanged();
                return this;
            }
            
            public Builder setProtocolBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ProtocolVersionProto extends GeneratedMessage implements ProtocolVersionProtoOrBuilder
    {
        private static final ProtocolVersionProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ProtocolVersionProto> PARSER;
        private int bitField0_;
        public static final int RPCKIND_FIELD_NUMBER = 1;
        private Object rpcKind_;
        public static final int VERSIONS_FIELD_NUMBER = 2;
        private List<Long> versions_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ProtocolVersionProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ProtocolVersionProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ProtocolVersionProto getDefaultInstance() {
            return ProtocolVersionProto.defaultInstance;
        }
        
        @Override
        public ProtocolVersionProto getDefaultInstanceForType() {
            return ProtocolVersionProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ProtocolVersionProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
            int mutable_bitField0_ = 0;
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
                            this.rpcKind_ = input.readBytes();
                            continue;
                        }
                        case 16: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.versions_ = new ArrayList<Long>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.versions_.add(input.readUInt64());
                            continue;
                        }
                        case 18: {
                            final int length = input.readRawVarint32();
                            final int limit = input.pushLimit(length);
                            if ((mutable_bitField0_ & 0x2) != 0x2 && input.getBytesUntilLimit() > 0) {
                                this.versions_ = new ArrayList<Long>();
                                mutable_bitField0_ |= 0x2;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.versions_.add(input.readUInt64());
                            }
                            input.popLimit(limit);
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
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.versions_ = Collections.unmodifiableList((List<? extends Long>)this.versions_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ProtocolVersionProto.class, Builder.class);
        }
        
        @Override
        public Parser<ProtocolVersionProto> getParserForType() {
            return ProtocolVersionProto.PARSER;
        }
        
        @Override
        public boolean hasRpcKind() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getRpcKind() {
            final Object ref = this.rpcKind_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.rpcKind_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRpcKindBytes() {
            final Object ref = this.rpcKind_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.rpcKind_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public List<Long> getVersionsList() {
            return this.versions_;
        }
        
        @Override
        public int getVersionsCount() {
            return this.versions_.size();
        }
        
        @Override
        public long getVersions(final int index) {
            return this.versions_.get(index);
        }
        
        private void initFields() {
            this.rpcKind_ = "";
            this.versions_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasRpcKind()) {
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
                output.writeBytes(1, this.getRpcKindBytes());
            }
            for (int i = 0; i < this.versions_.size(); ++i) {
                output.writeUInt64(2, this.versions_.get(i));
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
                size += CodedOutputStream.computeBytesSize(1, this.getRpcKindBytes());
            }
            int dataSize = 0;
            for (int i = 0; i < this.versions_.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt64SizeNoTag(this.versions_.get(i));
            }
            size += dataSize;
            size += 1 * this.getVersionsList().size();
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
            if (!(obj instanceof ProtocolVersionProto)) {
                return super.equals(obj);
            }
            final ProtocolVersionProto other = (ProtocolVersionProto)obj;
            boolean result = true;
            result = (result && this.hasRpcKind() == other.hasRpcKind());
            if (this.hasRpcKind()) {
                result = (result && this.getRpcKind().equals(other.getRpcKind()));
            }
            result = (result && this.getVersionsList().equals(other.getVersionsList()));
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
            if (this.hasRpcKind()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getRpcKind().hashCode();
            }
            if (this.getVersionsCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getVersionsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ProtocolVersionProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ProtocolVersionProto.PARSER.parseFrom(data);
        }
        
        public static ProtocolVersionProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ProtocolVersionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ProtocolVersionProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ProtocolVersionProto.PARSER.parseFrom(data);
        }
        
        public static ProtocolVersionProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ProtocolVersionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ProtocolVersionProto parseFrom(final InputStream input) throws IOException {
            return ProtocolVersionProto.PARSER.parseFrom(input);
        }
        
        public static ProtocolVersionProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolVersionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ProtocolVersionProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ProtocolVersionProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ProtocolVersionProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolVersionProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ProtocolVersionProto parseFrom(final CodedInputStream input) throws IOException {
            return ProtocolVersionProto.PARSER.parseFrom(input);
        }
        
        public static ProtocolVersionProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolVersionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ProtocolVersionProto prototype) {
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
            ProtocolVersionProto.PARSER = new AbstractParser<ProtocolVersionProto>() {
                @Override
                public ProtocolVersionProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ProtocolVersionProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ProtocolVersionProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ProtocolVersionProtoOrBuilder
        {
            private int bitField0_;
            private Object rpcKind_;
            private List<Long> versions_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ProtocolVersionProto.class, Builder.class);
            }
            
            private Builder() {
                this.rpcKind_ = "";
                this.versions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.rpcKind_ = "";
                this.versions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ProtocolVersionProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.rpcKind_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.versions_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolVersionProto_descriptor;
            }
            
            @Override
            public ProtocolVersionProto getDefaultInstanceForType() {
                return ProtocolVersionProto.getDefaultInstance();
            }
            
            @Override
            public ProtocolVersionProto build() {
                final ProtocolVersionProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ProtocolVersionProto buildPartial() {
                final ProtocolVersionProto result = new ProtocolVersionProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.rpcKind_ = this.rpcKind_;
                if ((this.bitField0_ & 0x2) == 0x2) {
                    this.versions_ = Collections.unmodifiableList((List<? extends Long>)this.versions_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.versions_ = this.versions_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ProtocolVersionProto) {
                    return this.mergeFrom((ProtocolVersionProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ProtocolVersionProto other) {
                if (other == ProtocolVersionProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasRpcKind()) {
                    this.bitField0_ |= 0x1;
                    this.rpcKind_ = other.rpcKind_;
                    this.onChanged();
                }
                if (!other.versions_.isEmpty()) {
                    if (this.versions_.isEmpty()) {
                        this.versions_ = other.versions_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    else {
                        this.ensureVersionsIsMutable();
                        this.versions_.addAll(other.versions_);
                    }
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasRpcKind();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ProtocolVersionProto parsedMessage = null;
                try {
                    parsedMessage = ProtocolVersionProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ProtocolVersionProto)e.getUnfinishedMessage();
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
            public boolean hasRpcKind() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getRpcKind() {
                final Object ref = this.rpcKind_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.rpcKind_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRpcKindBytes() {
                final Object ref = this.rpcKind_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.rpcKind_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRpcKind(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.rpcKind_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcKind() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.rpcKind_ = ProtocolVersionProto.getDefaultInstance().getRpcKind();
                this.onChanged();
                return this;
            }
            
            public Builder setRpcKindBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.rpcKind_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureVersionsIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.versions_ = new ArrayList<Long>(this.versions_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            @Override
            public List<Long> getVersionsList() {
                return Collections.unmodifiableList((List<? extends Long>)this.versions_);
            }
            
            @Override
            public int getVersionsCount() {
                return this.versions_.size();
            }
            
            @Override
            public long getVersions(final int index) {
                return this.versions_.get(index);
            }
            
            public Builder setVersions(final int index, final long value) {
                this.ensureVersionsIsMutable();
                this.versions_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addVersions(final long value) {
                this.ensureVersionsIsMutable();
                this.versions_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllVersions(final Iterable<? extends Long> values) {
                this.ensureVersionsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.versions_);
                this.onChanged();
                return this;
            }
            
            public Builder clearVersions() {
                this.versions_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GetProtocolVersionsResponseProto extends GeneratedMessage implements GetProtocolVersionsResponseProtoOrBuilder
    {
        private static final GetProtocolVersionsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetProtocolVersionsResponseProto> PARSER;
        public static final int PROTOCOLVERSIONS_FIELD_NUMBER = 1;
        private List<ProtocolVersionProto> protocolVersions_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetProtocolVersionsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetProtocolVersionsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetProtocolVersionsResponseProto getDefaultInstance() {
            return GetProtocolVersionsResponseProto.defaultInstance;
        }
        
        @Override
        public GetProtocolVersionsResponseProto getDefaultInstanceForType() {
            return GetProtocolVersionsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetProtocolVersionsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
            int mutable_bitField0_ = 0;
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
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.protocolVersions_ = new ArrayList<ProtocolVersionProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.protocolVersions_.add(input.readMessage(ProtocolVersionProto.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.protocolVersions_ = Collections.unmodifiableList((List<? extends ProtocolVersionProto>)this.protocolVersions_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolVersionsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetProtocolVersionsResponseProto> getParserForType() {
            return GetProtocolVersionsResponseProto.PARSER;
        }
        
        @Override
        public List<ProtocolVersionProto> getProtocolVersionsList() {
            return this.protocolVersions_;
        }
        
        @Override
        public List<? extends ProtocolVersionProtoOrBuilder> getProtocolVersionsOrBuilderList() {
            return this.protocolVersions_;
        }
        
        @Override
        public int getProtocolVersionsCount() {
            return this.protocolVersions_.size();
        }
        
        @Override
        public ProtocolVersionProto getProtocolVersions(final int index) {
            return this.protocolVersions_.get(index);
        }
        
        @Override
        public ProtocolVersionProtoOrBuilder getProtocolVersionsOrBuilder(final int index) {
            return this.protocolVersions_.get(index);
        }
        
        private void initFields() {
            this.protocolVersions_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getProtocolVersionsCount(); ++i) {
                if (!this.getProtocolVersions(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            for (int i = 0; i < this.protocolVersions_.size(); ++i) {
                output.writeMessage(1, this.protocolVersions_.get(i));
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
            for (int i = 0; i < this.protocolVersions_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.protocolVersions_.get(i));
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
            if (!(obj instanceof GetProtocolVersionsResponseProto)) {
                return super.equals(obj);
            }
            final GetProtocolVersionsResponseProto other = (GetProtocolVersionsResponseProto)obj;
            boolean result = true;
            result = (result && this.getProtocolVersionsList().equals(other.getProtocolVersionsList()));
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
            if (this.getProtocolVersionsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getProtocolVersionsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final InputStream input) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetProtocolVersionsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetProtocolVersionsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolVersionsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolVersionsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetProtocolVersionsResponseProto prototype) {
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
            GetProtocolVersionsResponseProto.PARSER = new AbstractParser<GetProtocolVersionsResponseProto>() {
                @Override
                public GetProtocolVersionsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetProtocolVersionsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetProtocolVersionsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetProtocolVersionsResponseProtoOrBuilder
        {
            private int bitField0_;
            private List<ProtocolVersionProto> protocolVersions_;
            private RepeatedFieldBuilder<ProtocolVersionProto, ProtocolVersionProto.Builder, ProtocolVersionProtoOrBuilder> protocolVersionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolVersionsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.protocolVersions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.protocolVersions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetProtocolVersionsResponseProto.alwaysUseFieldBuilders) {
                    this.getProtocolVersionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.protocolVersionsBuilder_ == null) {
                    this.protocolVersions_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.protocolVersionsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolVersionsResponseProto_descriptor;
            }
            
            @Override
            public GetProtocolVersionsResponseProto getDefaultInstanceForType() {
                return GetProtocolVersionsResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetProtocolVersionsResponseProto build() {
                final GetProtocolVersionsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetProtocolVersionsResponseProto buildPartial() {
                final GetProtocolVersionsResponseProto result = new GetProtocolVersionsResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.protocolVersionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.protocolVersions_ = Collections.unmodifiableList((List<? extends ProtocolVersionProto>)this.protocolVersions_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.protocolVersions_ = this.protocolVersions_;
                }
                else {
                    result.protocolVersions_ = this.protocolVersionsBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetProtocolVersionsResponseProto) {
                    return this.mergeFrom((GetProtocolVersionsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetProtocolVersionsResponseProto other) {
                if (other == GetProtocolVersionsResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (this.protocolVersionsBuilder_ == null) {
                    if (!other.protocolVersions_.isEmpty()) {
                        if (this.protocolVersions_.isEmpty()) {
                            this.protocolVersions_ = other.protocolVersions_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureProtocolVersionsIsMutable();
                            this.protocolVersions_.addAll(other.protocolVersions_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.protocolVersions_.isEmpty()) {
                    if (this.protocolVersionsBuilder_.isEmpty()) {
                        this.protocolVersionsBuilder_.dispose();
                        this.protocolVersionsBuilder_ = null;
                        this.protocolVersions_ = other.protocolVersions_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.protocolVersionsBuilder_ = (GetProtocolVersionsResponseProto.alwaysUseFieldBuilders ? this.getProtocolVersionsFieldBuilder() : null);
                    }
                    else {
                        this.protocolVersionsBuilder_.addAllMessages(other.protocolVersions_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getProtocolVersionsCount(); ++i) {
                    if (!this.getProtocolVersions(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetProtocolVersionsResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetProtocolVersionsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetProtocolVersionsResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureProtocolVersionsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.protocolVersions_ = new ArrayList<ProtocolVersionProto>(this.protocolVersions_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<ProtocolVersionProto> getProtocolVersionsList() {
                if (this.protocolVersionsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends ProtocolVersionProto>)this.protocolVersions_);
                }
                return this.protocolVersionsBuilder_.getMessageList();
            }
            
            @Override
            public int getProtocolVersionsCount() {
                if (this.protocolVersionsBuilder_ == null) {
                    return this.protocolVersions_.size();
                }
                return this.protocolVersionsBuilder_.getCount();
            }
            
            @Override
            public ProtocolVersionProto getProtocolVersions(final int index) {
                if (this.protocolVersionsBuilder_ == null) {
                    return this.protocolVersions_.get(index);
                }
                return this.protocolVersionsBuilder_.getMessage(index);
            }
            
            public Builder setProtocolVersions(final int index, final ProtocolVersionProto value) {
                if (this.protocolVersionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setProtocolVersions(final int index, final ProtocolVersionProto.Builder builderForValue) {
                if (this.protocolVersionsBuilder_ == null) {
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addProtocolVersions(final ProtocolVersionProto value) {
                if (this.protocolVersionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.add(value);
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addProtocolVersions(final int index, final ProtocolVersionProto value) {
                if (this.protocolVersionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addProtocolVersions(final ProtocolVersionProto.Builder builderForValue) {
                if (this.protocolVersionsBuilder_ == null) {
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addProtocolVersions(final int index, final ProtocolVersionProto.Builder builderForValue) {
                if (this.protocolVersionsBuilder_ == null) {
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllProtocolVersions(final Iterable<? extends ProtocolVersionProto> values) {
                if (this.protocolVersionsBuilder_ == null) {
                    this.ensureProtocolVersionsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.protocolVersions_);
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearProtocolVersions() {
                if (this.protocolVersionsBuilder_ == null) {
                    this.protocolVersions_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeProtocolVersions(final int index) {
                if (this.protocolVersionsBuilder_ == null) {
                    this.ensureProtocolVersionsIsMutable();
                    this.protocolVersions_.remove(index);
                    this.onChanged();
                }
                else {
                    this.protocolVersionsBuilder_.remove(index);
                }
                return this;
            }
            
            public ProtocolVersionProto.Builder getProtocolVersionsBuilder(final int index) {
                return this.getProtocolVersionsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public ProtocolVersionProtoOrBuilder getProtocolVersionsOrBuilder(final int index) {
                if (this.protocolVersionsBuilder_ == null) {
                    return this.protocolVersions_.get(index);
                }
                return this.protocolVersionsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends ProtocolVersionProtoOrBuilder> getProtocolVersionsOrBuilderList() {
                if (this.protocolVersionsBuilder_ != null) {
                    return this.protocolVersionsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends ProtocolVersionProtoOrBuilder>)this.protocolVersions_);
            }
            
            public ProtocolVersionProto.Builder addProtocolVersionsBuilder() {
                return this.getProtocolVersionsFieldBuilder().addBuilder(ProtocolVersionProto.getDefaultInstance());
            }
            
            public ProtocolVersionProto.Builder addProtocolVersionsBuilder(final int index) {
                return this.getProtocolVersionsFieldBuilder().addBuilder(index, ProtocolVersionProto.getDefaultInstance());
            }
            
            public List<ProtocolVersionProto.Builder> getProtocolVersionsBuilderList() {
                return this.getProtocolVersionsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<ProtocolVersionProto, ProtocolVersionProto.Builder, ProtocolVersionProtoOrBuilder> getProtocolVersionsFieldBuilder() {
                if (this.protocolVersionsBuilder_ == null) {
                    this.protocolVersionsBuilder_ = new RepeatedFieldBuilder<ProtocolVersionProto, ProtocolVersionProto.Builder, ProtocolVersionProtoOrBuilder>(this.protocolVersions_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.protocolVersions_ = null;
                }
                return this.protocolVersionsBuilder_;
            }
        }
    }
    
    public static final class GetProtocolSignatureRequestProto extends GeneratedMessage implements GetProtocolSignatureRequestProtoOrBuilder
    {
        private static final GetProtocolSignatureRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetProtocolSignatureRequestProto> PARSER;
        private int bitField0_;
        public static final int PROTOCOL_FIELD_NUMBER = 1;
        private Object protocol_;
        public static final int RPCKIND_FIELD_NUMBER = 2;
        private Object rpcKind_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetProtocolSignatureRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetProtocolSignatureRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetProtocolSignatureRequestProto getDefaultInstance() {
            return GetProtocolSignatureRequestProto.defaultInstance;
        }
        
        @Override
        public GetProtocolSignatureRequestProto getDefaultInstanceForType() {
            return GetProtocolSignatureRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetProtocolSignatureRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.protocol_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.rpcKind_ = input.readBytes();
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
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolSignatureRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetProtocolSignatureRequestProto> getParserForType() {
            return GetProtocolSignatureRequestProto.PARSER;
        }
        
        @Override
        public boolean hasProtocol() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getProtocol() {
            final Object ref = this.protocol_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.protocol_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getProtocolBytes() {
            final Object ref = this.protocol_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.protocol_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRpcKind() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getRpcKind() {
            final Object ref = this.rpcKind_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.rpcKind_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRpcKindBytes() {
            final Object ref = this.rpcKind_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.rpcKind_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.protocol_ = "";
            this.rpcKind_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasProtocol()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasRpcKind()) {
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
                output.writeBytes(1, this.getProtocolBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getRpcKindBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getProtocolBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getRpcKindBytes());
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
            if (!(obj instanceof GetProtocolSignatureRequestProto)) {
                return super.equals(obj);
            }
            final GetProtocolSignatureRequestProto other = (GetProtocolSignatureRequestProto)obj;
            boolean result = true;
            result = (result && this.hasProtocol() == other.hasProtocol());
            if (this.hasProtocol()) {
                result = (result && this.getProtocol().equals(other.getProtocol()));
            }
            result = (result && this.hasRpcKind() == other.hasRpcKind());
            if (this.hasRpcKind()) {
                result = (result && this.getRpcKind().equals(other.getRpcKind()));
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
            if (this.hasProtocol()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getProtocol().hashCode();
            }
            if (this.hasRpcKind()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getRpcKind().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final InputStream input) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetProtocolSignatureRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetProtocolSignatureRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolSignatureRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetProtocolSignatureRequestProto prototype) {
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
            GetProtocolSignatureRequestProto.PARSER = new AbstractParser<GetProtocolSignatureRequestProto>() {
                @Override
                public GetProtocolSignatureRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetProtocolSignatureRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetProtocolSignatureRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetProtocolSignatureRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object protocol_;
            private Object rpcKind_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolSignatureRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.protocol_ = "";
                this.rpcKind_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.protocol_ = "";
                this.rpcKind_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetProtocolSignatureRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.protocol_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.rpcKind_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureRequestProto_descriptor;
            }
            
            @Override
            public GetProtocolSignatureRequestProto getDefaultInstanceForType() {
                return GetProtocolSignatureRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetProtocolSignatureRequestProto build() {
                final GetProtocolSignatureRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetProtocolSignatureRequestProto buildPartial() {
                final GetProtocolSignatureRequestProto result = new GetProtocolSignatureRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.protocol_ = this.protocol_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.rpcKind_ = this.rpcKind_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetProtocolSignatureRequestProto) {
                    return this.mergeFrom((GetProtocolSignatureRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetProtocolSignatureRequestProto other) {
                if (other == GetProtocolSignatureRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasProtocol()) {
                    this.bitField0_ |= 0x1;
                    this.protocol_ = other.protocol_;
                    this.onChanged();
                }
                if (other.hasRpcKind()) {
                    this.bitField0_ |= 0x2;
                    this.rpcKind_ = other.rpcKind_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasProtocol() && this.hasRpcKind();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetProtocolSignatureRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetProtocolSignatureRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetProtocolSignatureRequestProto)e.getUnfinishedMessage();
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
            public boolean hasProtocol() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getProtocol() {
                final Object ref = this.protocol_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.protocol_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getProtocolBytes() {
                final Object ref = this.protocol_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.protocol_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setProtocol(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearProtocol() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.protocol_ = GetProtocolSignatureRequestProto.getDefaultInstance().getProtocol();
                this.onChanged();
                return this;
            }
            
            public Builder setProtocolBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRpcKind() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getRpcKind() {
                final Object ref = this.rpcKind_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.rpcKind_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRpcKindBytes() {
                final Object ref = this.rpcKind_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.rpcKind_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRpcKind(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.rpcKind_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcKind() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.rpcKind_ = GetProtocolSignatureRequestProto.getDefaultInstance().getRpcKind();
                this.onChanged();
                return this;
            }
            
            public Builder setRpcKindBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.rpcKind_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GetProtocolSignatureResponseProto extends GeneratedMessage implements GetProtocolSignatureResponseProtoOrBuilder
    {
        private static final GetProtocolSignatureResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetProtocolSignatureResponseProto> PARSER;
        public static final int PROTOCOLSIGNATURE_FIELD_NUMBER = 1;
        private List<ProtocolSignatureProto> protocolSignature_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetProtocolSignatureResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetProtocolSignatureResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetProtocolSignatureResponseProto getDefaultInstance() {
            return GetProtocolSignatureResponseProto.defaultInstance;
        }
        
        @Override
        public GetProtocolSignatureResponseProto getDefaultInstanceForType() {
            return GetProtocolSignatureResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetProtocolSignatureResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
            int mutable_bitField0_ = 0;
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
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.protocolSignature_ = new ArrayList<ProtocolSignatureProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.protocolSignature_.add(input.readMessage(ProtocolSignatureProto.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.protocolSignature_ = Collections.unmodifiableList((List<? extends ProtocolSignatureProto>)this.protocolSignature_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolSignatureResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetProtocolSignatureResponseProto> getParserForType() {
            return GetProtocolSignatureResponseProto.PARSER;
        }
        
        @Override
        public List<ProtocolSignatureProto> getProtocolSignatureList() {
            return this.protocolSignature_;
        }
        
        @Override
        public List<? extends ProtocolSignatureProtoOrBuilder> getProtocolSignatureOrBuilderList() {
            return this.protocolSignature_;
        }
        
        @Override
        public int getProtocolSignatureCount() {
            return this.protocolSignature_.size();
        }
        
        @Override
        public ProtocolSignatureProto getProtocolSignature(final int index) {
            return this.protocolSignature_.get(index);
        }
        
        @Override
        public ProtocolSignatureProtoOrBuilder getProtocolSignatureOrBuilder(final int index) {
            return this.protocolSignature_.get(index);
        }
        
        private void initFields() {
            this.protocolSignature_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getProtocolSignatureCount(); ++i) {
                if (!this.getProtocolSignature(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            for (int i = 0; i < this.protocolSignature_.size(); ++i) {
                output.writeMessage(1, this.protocolSignature_.get(i));
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
            for (int i = 0; i < this.protocolSignature_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.protocolSignature_.get(i));
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
            if (!(obj instanceof GetProtocolSignatureResponseProto)) {
                return super.equals(obj);
            }
            final GetProtocolSignatureResponseProto other = (GetProtocolSignatureResponseProto)obj;
            boolean result = true;
            result = (result && this.getProtocolSignatureList().equals(other.getProtocolSignatureList()));
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
            if (this.getProtocolSignatureCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getProtocolSignatureList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final InputStream input) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetProtocolSignatureResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetProtocolSignatureResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetProtocolSignatureResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetProtocolSignatureResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetProtocolSignatureResponseProto prototype) {
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
            GetProtocolSignatureResponseProto.PARSER = new AbstractParser<GetProtocolSignatureResponseProto>() {
                @Override
                public GetProtocolSignatureResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetProtocolSignatureResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetProtocolSignatureResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetProtocolSignatureResponseProtoOrBuilder
        {
            private int bitField0_;
            private List<ProtocolSignatureProto> protocolSignature_;
            private RepeatedFieldBuilder<ProtocolSignatureProto, ProtocolSignatureProto.Builder, ProtocolSignatureProtoOrBuilder> protocolSignatureBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetProtocolSignatureResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.protocolSignature_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.protocolSignature_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetProtocolSignatureResponseProto.alwaysUseFieldBuilders) {
                    this.getProtocolSignatureFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.protocolSignatureBuilder_ == null) {
                    this.protocolSignature_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.protocolSignatureBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_GetProtocolSignatureResponseProto_descriptor;
            }
            
            @Override
            public GetProtocolSignatureResponseProto getDefaultInstanceForType() {
                return GetProtocolSignatureResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetProtocolSignatureResponseProto build() {
                final GetProtocolSignatureResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetProtocolSignatureResponseProto buildPartial() {
                final GetProtocolSignatureResponseProto result = new GetProtocolSignatureResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.protocolSignatureBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.protocolSignature_ = Collections.unmodifiableList((List<? extends ProtocolSignatureProto>)this.protocolSignature_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.protocolSignature_ = this.protocolSignature_;
                }
                else {
                    result.protocolSignature_ = this.protocolSignatureBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetProtocolSignatureResponseProto) {
                    return this.mergeFrom((GetProtocolSignatureResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetProtocolSignatureResponseProto other) {
                if (other == GetProtocolSignatureResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (this.protocolSignatureBuilder_ == null) {
                    if (!other.protocolSignature_.isEmpty()) {
                        if (this.protocolSignature_.isEmpty()) {
                            this.protocolSignature_ = other.protocolSignature_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureProtocolSignatureIsMutable();
                            this.protocolSignature_.addAll(other.protocolSignature_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.protocolSignature_.isEmpty()) {
                    if (this.protocolSignatureBuilder_.isEmpty()) {
                        this.protocolSignatureBuilder_.dispose();
                        this.protocolSignatureBuilder_ = null;
                        this.protocolSignature_ = other.protocolSignature_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.protocolSignatureBuilder_ = (GetProtocolSignatureResponseProto.alwaysUseFieldBuilders ? this.getProtocolSignatureFieldBuilder() : null);
                    }
                    else {
                        this.protocolSignatureBuilder_.addAllMessages(other.protocolSignature_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getProtocolSignatureCount(); ++i) {
                    if (!this.getProtocolSignature(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetProtocolSignatureResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetProtocolSignatureResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetProtocolSignatureResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureProtocolSignatureIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.protocolSignature_ = new ArrayList<ProtocolSignatureProto>(this.protocolSignature_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<ProtocolSignatureProto> getProtocolSignatureList() {
                if (this.protocolSignatureBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends ProtocolSignatureProto>)this.protocolSignature_);
                }
                return this.protocolSignatureBuilder_.getMessageList();
            }
            
            @Override
            public int getProtocolSignatureCount() {
                if (this.protocolSignatureBuilder_ == null) {
                    return this.protocolSignature_.size();
                }
                return this.protocolSignatureBuilder_.getCount();
            }
            
            @Override
            public ProtocolSignatureProto getProtocolSignature(final int index) {
                if (this.protocolSignatureBuilder_ == null) {
                    return this.protocolSignature_.get(index);
                }
                return this.protocolSignatureBuilder_.getMessage(index);
            }
            
            public Builder setProtocolSignature(final int index, final ProtocolSignatureProto value) {
                if (this.protocolSignatureBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setProtocolSignature(final int index, final ProtocolSignatureProto.Builder builderForValue) {
                if (this.protocolSignatureBuilder_ == null) {
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addProtocolSignature(final ProtocolSignatureProto value) {
                if (this.protocolSignatureBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.add(value);
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addProtocolSignature(final int index, final ProtocolSignatureProto value) {
                if (this.protocolSignatureBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addProtocolSignature(final ProtocolSignatureProto.Builder builderForValue) {
                if (this.protocolSignatureBuilder_ == null) {
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addProtocolSignature(final int index, final ProtocolSignatureProto.Builder builderForValue) {
                if (this.protocolSignatureBuilder_ == null) {
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllProtocolSignature(final Iterable<? extends ProtocolSignatureProto> values) {
                if (this.protocolSignatureBuilder_ == null) {
                    this.ensureProtocolSignatureIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.protocolSignature_);
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearProtocolSignature() {
                if (this.protocolSignatureBuilder_ == null) {
                    this.protocolSignature_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeProtocolSignature(final int index) {
                if (this.protocolSignatureBuilder_ == null) {
                    this.ensureProtocolSignatureIsMutable();
                    this.protocolSignature_.remove(index);
                    this.onChanged();
                }
                else {
                    this.protocolSignatureBuilder_.remove(index);
                }
                return this;
            }
            
            public ProtocolSignatureProto.Builder getProtocolSignatureBuilder(final int index) {
                return this.getProtocolSignatureFieldBuilder().getBuilder(index);
            }
            
            @Override
            public ProtocolSignatureProtoOrBuilder getProtocolSignatureOrBuilder(final int index) {
                if (this.protocolSignatureBuilder_ == null) {
                    return this.protocolSignature_.get(index);
                }
                return this.protocolSignatureBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends ProtocolSignatureProtoOrBuilder> getProtocolSignatureOrBuilderList() {
                if (this.protocolSignatureBuilder_ != null) {
                    return this.protocolSignatureBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends ProtocolSignatureProtoOrBuilder>)this.protocolSignature_);
            }
            
            public ProtocolSignatureProto.Builder addProtocolSignatureBuilder() {
                return this.getProtocolSignatureFieldBuilder().addBuilder(ProtocolSignatureProto.getDefaultInstance());
            }
            
            public ProtocolSignatureProto.Builder addProtocolSignatureBuilder(final int index) {
                return this.getProtocolSignatureFieldBuilder().addBuilder(index, ProtocolSignatureProto.getDefaultInstance());
            }
            
            public List<ProtocolSignatureProto.Builder> getProtocolSignatureBuilderList() {
                return this.getProtocolSignatureFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<ProtocolSignatureProto, ProtocolSignatureProto.Builder, ProtocolSignatureProtoOrBuilder> getProtocolSignatureFieldBuilder() {
                if (this.protocolSignatureBuilder_ == null) {
                    this.protocolSignatureBuilder_ = new RepeatedFieldBuilder<ProtocolSignatureProto, ProtocolSignatureProto.Builder, ProtocolSignatureProtoOrBuilder>(this.protocolSignature_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.protocolSignature_ = null;
                }
                return this.protocolSignatureBuilder_;
            }
        }
    }
    
    public static final class ProtocolSignatureProto extends GeneratedMessage implements ProtocolSignatureProtoOrBuilder
    {
        private static final ProtocolSignatureProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ProtocolSignatureProto> PARSER;
        private int bitField0_;
        public static final int VERSION_FIELD_NUMBER = 1;
        private long version_;
        public static final int METHODS_FIELD_NUMBER = 2;
        private List<Integer> methods_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ProtocolSignatureProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ProtocolSignatureProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ProtocolSignatureProto getDefaultInstance() {
            return ProtocolSignatureProto.defaultInstance;
        }
        
        @Override
        public ProtocolSignatureProto getDefaultInstanceForType() {
            return ProtocolSignatureProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ProtocolSignatureProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
            int mutable_bitField0_ = 0;
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
                        case 8: {
                            this.bitField0_ |= 0x1;
                            this.version_ = input.readUInt64();
                            continue;
                        }
                        case 16: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.methods_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.methods_.add(input.readUInt32());
                            continue;
                        }
                        case 18: {
                            final int length = input.readRawVarint32();
                            final int limit = input.pushLimit(length);
                            if ((mutable_bitField0_ & 0x2) != 0x2 && input.getBytesUntilLimit() > 0) {
                                this.methods_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x2;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.methods_.add(input.readUInt32());
                            }
                            input.popLimit(limit);
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
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.methods_ = Collections.unmodifiableList((List<? extends Integer>)this.methods_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ProtocolSignatureProto.class, Builder.class);
        }
        
        @Override
        public Parser<ProtocolSignatureProto> getParserForType() {
            return ProtocolSignatureProto.PARSER;
        }
        
        @Override
        public boolean hasVersion() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getVersion() {
            return this.version_;
        }
        
        @Override
        public List<Integer> getMethodsList() {
            return this.methods_;
        }
        
        @Override
        public int getMethodsCount() {
            return this.methods_.size();
        }
        
        @Override
        public int getMethods(final int index) {
            return this.methods_.get(index);
        }
        
        private void initFields() {
            this.version_ = 0L;
            this.methods_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasVersion()) {
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
                output.writeUInt64(1, this.version_);
            }
            for (int i = 0; i < this.methods_.size(); ++i) {
                output.writeUInt32(2, this.methods_.get(i));
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
                size += CodedOutputStream.computeUInt64Size(1, this.version_);
            }
            int dataSize = 0;
            for (int i = 0; i < this.methods_.size(); ++i) {
                dataSize += CodedOutputStream.computeUInt32SizeNoTag(this.methods_.get(i));
            }
            size += dataSize;
            size += 1 * this.getMethodsList().size();
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
            if (!(obj instanceof ProtocolSignatureProto)) {
                return super.equals(obj);
            }
            final ProtocolSignatureProto other = (ProtocolSignatureProto)obj;
            boolean result = true;
            result = (result && this.hasVersion() == other.hasVersion());
            if (this.hasVersion()) {
                result = (result && this.getVersion() == other.getVersion());
            }
            result = (result && this.getMethodsList().equals(other.getMethodsList()));
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
            if (this.hasVersion()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getVersion());
            }
            if (this.getMethodsCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getMethodsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ProtocolSignatureProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ProtocolSignatureProto.PARSER.parseFrom(data);
        }
        
        public static ProtocolSignatureProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ProtocolSignatureProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ProtocolSignatureProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ProtocolSignatureProto.PARSER.parseFrom(data);
        }
        
        public static ProtocolSignatureProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ProtocolSignatureProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ProtocolSignatureProto parseFrom(final InputStream input) throws IOException {
            return ProtocolSignatureProto.PARSER.parseFrom(input);
        }
        
        public static ProtocolSignatureProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolSignatureProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ProtocolSignatureProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ProtocolSignatureProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ProtocolSignatureProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolSignatureProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ProtocolSignatureProto parseFrom(final CodedInputStream input) throws IOException {
            return ProtocolSignatureProto.PARSER.parseFrom(input);
        }
        
        public static ProtocolSignatureProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ProtocolSignatureProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ProtocolSignatureProto prototype) {
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
            ProtocolSignatureProto.PARSER = new AbstractParser<ProtocolSignatureProto>() {
                @Override
                public ProtocolSignatureProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ProtocolSignatureProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ProtocolSignatureProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ProtocolSignatureProtoOrBuilder
        {
            private int bitField0_;
            private long version_;
            private List<Integer> methods_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ProtocolSignatureProto.class, Builder.class);
            }
            
            private Builder() {
                this.methods_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.methods_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ProtocolSignatureProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.version_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                this.methods_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ProtocolInfoProtos.internal_static_hadoop_common_ProtocolSignatureProto_descriptor;
            }
            
            @Override
            public ProtocolSignatureProto getDefaultInstanceForType() {
                return ProtocolSignatureProto.getDefaultInstance();
            }
            
            @Override
            public ProtocolSignatureProto build() {
                final ProtocolSignatureProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ProtocolSignatureProto buildPartial() {
                final ProtocolSignatureProto result = new ProtocolSignatureProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.version_ = this.version_;
                if ((this.bitField0_ & 0x2) == 0x2) {
                    this.methods_ = Collections.unmodifiableList((List<? extends Integer>)this.methods_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.methods_ = this.methods_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ProtocolSignatureProto) {
                    return this.mergeFrom((ProtocolSignatureProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ProtocolSignatureProto other) {
                if (other == ProtocolSignatureProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasVersion()) {
                    this.setVersion(other.getVersion());
                }
                if (!other.methods_.isEmpty()) {
                    if (this.methods_.isEmpty()) {
                        this.methods_ = other.methods_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    else {
                        this.ensureMethodsIsMutable();
                        this.methods_.addAll(other.methods_);
                    }
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasVersion();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ProtocolSignatureProto parsedMessage = null;
                try {
                    parsedMessage = ProtocolSignatureProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ProtocolSignatureProto)e.getUnfinishedMessage();
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
            public boolean hasVersion() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getVersion() {
                return this.version_;
            }
            
            public Builder setVersion(final long value) {
                this.bitField0_ |= 0x1;
                this.version_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearVersion() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.version_ = 0L;
                this.onChanged();
                return this;
            }
            
            private void ensureMethodsIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.methods_ = new ArrayList<Integer>(this.methods_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            @Override
            public List<Integer> getMethodsList() {
                return Collections.unmodifiableList((List<? extends Integer>)this.methods_);
            }
            
            @Override
            public int getMethodsCount() {
                return this.methods_.size();
            }
            
            @Override
            public int getMethods(final int index) {
                return this.methods_.get(index);
            }
            
            public Builder setMethods(final int index, final int value) {
                this.ensureMethodsIsMutable();
                this.methods_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addMethods(final int value) {
                this.ensureMethodsIsMutable();
                this.methods_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllMethods(final Iterable<? extends Integer> values) {
                this.ensureMethodsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.methods_);
                this.onChanged();
                return this;
            }
            
            public Builder clearMethods() {
                this.methods_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
                return this;
            }
        }
    }
    
    public abstract static class ProtocolInfoService implements Service
    {
        protected ProtocolInfoService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ProtocolInfoService() {
                @Override
                public void getProtocolVersions(final RpcController controller, final GetProtocolVersionsRequestProto request, final RpcCallback<GetProtocolVersionsResponseProto> done) {
                    impl.getProtocolVersions(controller, request, done);
                }
                
                @Override
                public void getProtocolSignature(final RpcController controller, final GetProtocolSignatureRequestProto request, final RpcCallback<GetProtocolSignatureResponseProto> done) {
                    impl.getProtocolSignature(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ProtocolInfoService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ProtocolInfoService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.getProtocolVersions(controller, (GetProtocolVersionsRequestProto)request);
                        }
                        case 1: {
                            return impl.getProtocolSignature(controller, (GetProtocolSignatureRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ProtocolInfoService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GetProtocolVersionsRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return GetProtocolSignatureRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ProtocolInfoService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GetProtocolVersionsResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return GetProtocolSignatureResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void getProtocolVersions(final RpcController p0, final GetProtocolVersionsRequestProto p1, final RpcCallback<GetProtocolVersionsResponseProto> p2);
        
        public abstract void getProtocolSignature(final RpcController p0, final GetProtocolSignatureRequestProto p1, final RpcCallback<GetProtocolSignatureResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ProtocolInfoProtos.getDescriptor().getServices().get(0);
        }
        
        @Override
        public final Descriptors.ServiceDescriptor getDescriptorForType() {
            return getDescriptor();
        }
        
        @Override
        public final void callMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request, final RpcCallback<Message> done) {
            if (method.getService() != getDescriptor()) {
                throw new IllegalArgumentException("Service.callMethod() given method descriptor for wrong service type.");
            }
            switch (method.getIndex()) {
                case 0: {
                    this.getProtocolVersions(controller, (GetProtocolVersionsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.getProtocolSignature(controller, (GetProtocolSignatureRequestProto)request, RpcUtil.specializeCallback(done));
                }
                default: {
                    throw new AssertionError((Object)"Can't get here.");
                }
            }
        }
        
        @Override
        public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
            if (method.getService() != getDescriptor()) {
                throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
            }
            switch (method.getIndex()) {
                case 0: {
                    return GetProtocolVersionsRequestProto.getDefaultInstance();
                }
                case 1: {
                    return GetProtocolSignatureRequestProto.getDefaultInstance();
                }
                default: {
                    throw new AssertionError((Object)"Can't get here.");
                }
            }
        }
        
        @Override
        public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
            if (method.getService() != getDescriptor()) {
                throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
            }
            switch (method.getIndex()) {
                case 0: {
                    return GetProtocolVersionsResponseProto.getDefaultInstance();
                }
                case 1: {
                    return GetProtocolSignatureResponseProto.getDefaultInstance();
                }
                default: {
                    throw new AssertionError((Object)"Can't get here.");
                }
            }
        }
        
        public static Stub newStub(final RpcChannel channel) {
            return new Stub(channel);
        }
        
        public static BlockingInterface newBlockingStub(final BlockingRpcChannel channel) {
            return new BlockingStub(channel);
        }
        
        public static final class Stub extends ProtocolInfoService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void getProtocolVersions(final RpcController controller, final GetProtocolVersionsRequestProto request, final RpcCallback<GetProtocolVersionsResponseProto> done) {
                this.channel.callMethod(ProtocolInfoService.getDescriptor().getMethods().get(0), controller, request, GetProtocolVersionsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GetProtocolVersionsResponseProto.class, GetProtocolVersionsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getProtocolSignature(final RpcController controller, final GetProtocolSignatureRequestProto request, final RpcCallback<GetProtocolSignatureResponseProto> done) {
                this.channel.callMethod(ProtocolInfoService.getDescriptor().getMethods().get(1), controller, request, GetProtocolSignatureResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GetProtocolSignatureResponseProto.class, GetProtocolSignatureResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public GetProtocolVersionsResponseProto getProtocolVersions(final RpcController controller, final GetProtocolVersionsRequestProto request) throws ServiceException {
                return (GetProtocolVersionsResponseProto)this.channel.callBlockingMethod(ProtocolInfoService.getDescriptor().getMethods().get(0), controller, request, GetProtocolVersionsResponseProto.getDefaultInstance());
            }
            
            @Override
            public GetProtocolSignatureResponseProto getProtocolSignature(final RpcController controller, final GetProtocolSignatureRequestProto request) throws ServiceException {
                return (GetProtocolSignatureResponseProto)this.channel.callBlockingMethod(ProtocolInfoService.getDescriptor().getMethods().get(1), controller, request, GetProtocolSignatureResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            GetProtocolVersionsResponseProto getProtocolVersions(final RpcController p0, final GetProtocolVersionsRequestProto p1) throws ServiceException;
            
            GetProtocolSignatureResponseProto getProtocolSignature(final RpcController p0, final GetProtocolSignatureRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void getProtocolVersions(final RpcController p0, final GetProtocolVersionsRequestProto p1, final RpcCallback<GetProtocolVersionsResponseProto> p2);
            
            void getProtocolSignature(final RpcController p0, final GetProtocolSignatureRequestProto p1, final RpcCallback<GetProtocolSignatureResponseProto> p2);
        }
    }
    
    public interface GetProtocolVersionsRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasProtocol();
        
        String getProtocol();
        
        ByteString getProtocolBytes();
    }
    
    public interface ProtocolVersionProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasRpcKind();
        
        String getRpcKind();
        
        ByteString getRpcKindBytes();
        
        List<Long> getVersionsList();
        
        int getVersionsCount();
        
        long getVersions(final int p0);
    }
    
    public interface GetProtocolVersionsResponseProtoOrBuilder extends MessageOrBuilder
    {
        List<ProtocolVersionProto> getProtocolVersionsList();
        
        ProtocolVersionProto getProtocolVersions(final int p0);
        
        int getProtocolVersionsCount();
        
        List<? extends ProtocolVersionProtoOrBuilder> getProtocolVersionsOrBuilderList();
        
        ProtocolVersionProtoOrBuilder getProtocolVersionsOrBuilder(final int p0);
    }
    
    public interface GetProtocolSignatureRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasProtocol();
        
        String getProtocol();
        
        ByteString getProtocolBytes();
        
        boolean hasRpcKind();
        
        String getRpcKind();
        
        ByteString getRpcKindBytes();
    }
    
    public interface ProtocolSignatureProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasVersion();
        
        long getVersion();
        
        List<Integer> getMethodsList();
        
        int getMethodsCount();
        
        int getMethods(final int p0);
    }
    
    public interface GetProtocolSignatureResponseProtoOrBuilder extends MessageOrBuilder
    {
        List<ProtocolSignatureProto> getProtocolSignatureList();
        
        ProtocolSignatureProto getProtocolSignature(final int p0);
        
        int getProtocolSignatureCount();
        
        List<? extends ProtocolSignatureProtoOrBuilder> getProtocolSignatureOrBuilderList();
        
        ProtocolSignatureProtoOrBuilder getProtocolSignatureOrBuilder(final int p0);
    }
}
