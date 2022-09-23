// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.ByteString;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
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

public final class ZKFCProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_CedeActiveRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CedeActiveRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_CedeActiveResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CedeActiveResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GracefulFailoverRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GracefulFailoverResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private ZKFCProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ZKFCProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0012ZKFCProtocol.proto\u0012\rhadoop.common\".\n\u0016CedeActiveRequestProto\u0012\u0014\n\fmillisToCede\u0018\u0001 \u0002(\r\"\u0019\n\u0017CedeActiveResponseProto\"\u001e\n\u001cGracefulFailoverRequestProto\"\u001f\n\u001dGracefulFailoverResponseProto2\u00e1\u0001\n\u0013ZKFCProtocolService\u0012[\n\ncedeActive\u0012%.hadoop.common.CedeActiveRequestProto\u001a&.hadoop.common.CedeActiveResponseProto\u0012m\n\u0010gracefulFailover\u0012+.hadoop.common.GracefulFailoverRequestProto\u001a,.hadoop.common.GracefulFailoverResponsePr", "otoB6\n\u001aorg.apache.hadoop.ha.protoB\u0012ZKFCProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ZKFCProtocolProtos.descriptor = root;
                ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_descriptor = ZKFCProtocolProtos.getDescriptor().getMessageTypes().get(0);
                ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_descriptor, new String[] { "MillisToCede" });
                ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_descriptor = ZKFCProtocolProtos.getDescriptor().getMessageTypes().get(1);
                ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_descriptor, new String[0]);
                ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor = ZKFCProtocolProtos.getDescriptor().getMessageTypes().get(2);
                ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor, new String[0]);
                ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor = ZKFCProtocolProtos.getDescriptor().getMessageTypes().get(3);
                ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class CedeActiveRequestProto extends GeneratedMessage implements CedeActiveRequestProtoOrBuilder
    {
        private static final CedeActiveRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CedeActiveRequestProto> PARSER;
        private int bitField0_;
        public static final int MILLISTOCEDE_FIELD_NUMBER = 1;
        private int millisToCede_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CedeActiveRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CedeActiveRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CedeActiveRequestProto getDefaultInstance() {
            return CedeActiveRequestProto.defaultInstance;
        }
        
        @Override
        public CedeActiveRequestProto getDefaultInstanceForType() {
            return CedeActiveRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CedeActiveRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 8: {
                            this.bitField0_ |= 0x1;
                            this.millisToCede_ = input.readUInt32();
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
            return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CedeActiveRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<CedeActiveRequestProto> getParserForType() {
            return CedeActiveRequestProto.PARSER;
        }
        
        @Override
        public boolean hasMillisToCede() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getMillisToCede() {
            return this.millisToCede_;
        }
        
        private void initFields() {
            this.millisToCede_ = 0;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasMillisToCede()) {
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
                output.writeUInt32(1, this.millisToCede_);
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
                size += CodedOutputStream.computeUInt32Size(1, this.millisToCede_);
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
            if (!(obj instanceof CedeActiveRequestProto)) {
                return super.equals(obj);
            }
            final CedeActiveRequestProto other = (CedeActiveRequestProto)obj;
            boolean result = true;
            result = (result && this.hasMillisToCede() == other.hasMillisToCede());
            if (this.hasMillisToCede()) {
                result = (result && this.getMillisToCede() == other.getMillisToCede());
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
            if (this.hasMillisToCede()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getMillisToCede();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static CedeActiveRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CedeActiveRequestProto.PARSER.parseFrom(data);
        }
        
        public static CedeActiveRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CedeActiveRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CedeActiveRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CedeActiveRequestProto.PARSER.parseFrom(data);
        }
        
        public static CedeActiveRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CedeActiveRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CedeActiveRequestProto parseFrom(final InputStream input) throws IOException {
            return CedeActiveRequestProto.PARSER.parseFrom(input);
        }
        
        public static CedeActiveRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CedeActiveRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CedeActiveRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CedeActiveRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CedeActiveRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return CedeActiveRequestProto.PARSER.parseFrom(input);
        }
        
        public static CedeActiveRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CedeActiveRequestProto prototype) {
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
            CedeActiveRequestProto.PARSER = new AbstractParser<CedeActiveRequestProto>() {
                @Override
                public CedeActiveRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CedeActiveRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CedeActiveRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CedeActiveRequestProtoOrBuilder
        {
            private int bitField0_;
            private int millisToCede_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CedeActiveRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CedeActiveRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.millisToCede_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveRequestProto_descriptor;
            }
            
            @Override
            public CedeActiveRequestProto getDefaultInstanceForType() {
                return CedeActiveRequestProto.getDefaultInstance();
            }
            
            @Override
            public CedeActiveRequestProto build() {
                final CedeActiveRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CedeActiveRequestProto buildPartial() {
                final CedeActiveRequestProto result = new CedeActiveRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.millisToCede_ = this.millisToCede_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CedeActiveRequestProto) {
                    return this.mergeFrom((CedeActiveRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CedeActiveRequestProto other) {
                if (other == CedeActiveRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMillisToCede()) {
                    this.setMillisToCede(other.getMillisToCede());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasMillisToCede();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                CedeActiveRequestProto parsedMessage = null;
                try {
                    parsedMessage = CedeActiveRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CedeActiveRequestProto)e.getUnfinishedMessage();
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
            public boolean hasMillisToCede() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getMillisToCede() {
                return this.millisToCede_;
            }
            
            public Builder setMillisToCede(final int value) {
                this.bitField0_ |= 0x1;
                this.millisToCede_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMillisToCede() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.millisToCede_ = 0;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class CedeActiveResponseProto extends GeneratedMessage implements CedeActiveResponseProtoOrBuilder
    {
        private static final CedeActiveResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CedeActiveResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CedeActiveResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CedeActiveResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CedeActiveResponseProto getDefaultInstance() {
            return CedeActiveResponseProto.defaultInstance;
        }
        
        @Override
        public CedeActiveResponseProto getDefaultInstanceForType() {
            return CedeActiveResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CedeActiveResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
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
            return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CedeActiveResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<CedeActiveResponseProto> getParserForType() {
            return CedeActiveResponseProto.PARSER;
        }
        
        private void initFields() {
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
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
            if (!(obj instanceof CedeActiveResponseProto)) {
                return super.equals(obj);
            }
            final CedeActiveResponseProto other = (CedeActiveResponseProto)obj;
            boolean result = true;
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
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static CedeActiveResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CedeActiveResponseProto.PARSER.parseFrom(data);
        }
        
        public static CedeActiveResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CedeActiveResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CedeActiveResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CedeActiveResponseProto.PARSER.parseFrom(data);
        }
        
        public static CedeActiveResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CedeActiveResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CedeActiveResponseProto parseFrom(final InputStream input) throws IOException {
            return CedeActiveResponseProto.PARSER.parseFrom(input);
        }
        
        public static CedeActiveResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CedeActiveResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CedeActiveResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CedeActiveResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CedeActiveResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return CedeActiveResponseProto.PARSER.parseFrom(input);
        }
        
        public static CedeActiveResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CedeActiveResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CedeActiveResponseProto prototype) {
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
            CedeActiveResponseProto.PARSER = new AbstractParser<CedeActiveResponseProto>() {
                @Override
                public CedeActiveResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CedeActiveResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CedeActiveResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CedeActiveResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CedeActiveResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CedeActiveResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_CedeActiveResponseProto_descriptor;
            }
            
            @Override
            public CedeActiveResponseProto getDefaultInstanceForType() {
                return CedeActiveResponseProto.getDefaultInstance();
            }
            
            @Override
            public CedeActiveResponseProto build() {
                final CedeActiveResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CedeActiveResponseProto buildPartial() {
                final CedeActiveResponseProto result = new CedeActiveResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CedeActiveResponseProto) {
                    return this.mergeFrom((CedeActiveResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CedeActiveResponseProto other) {
                if (other == CedeActiveResponseProto.getDefaultInstance()) {
                    return this;
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                CedeActiveResponseProto parsedMessage = null;
                try {
                    parsedMessage = CedeActiveResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CedeActiveResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
        }
    }
    
    public static final class GracefulFailoverRequestProto extends GeneratedMessage implements GracefulFailoverRequestProtoOrBuilder
    {
        private static final GracefulFailoverRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GracefulFailoverRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GracefulFailoverRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GracefulFailoverRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GracefulFailoverRequestProto getDefaultInstance() {
            return GracefulFailoverRequestProto.defaultInstance;
        }
        
        @Override
        public GracefulFailoverRequestProto getDefaultInstanceForType() {
            return GracefulFailoverRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GracefulFailoverRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
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
            return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GracefulFailoverRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GracefulFailoverRequestProto> getParserForType() {
            return GracefulFailoverRequestProto.PARSER;
        }
        
        private void initFields() {
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
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
            if (!(obj instanceof GracefulFailoverRequestProto)) {
                return super.equals(obj);
            }
            final GracefulFailoverRequestProto other = (GracefulFailoverRequestProto)obj;
            boolean result = true;
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
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GracefulFailoverRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(data);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(data);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final InputStream input) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(input);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GracefulFailoverRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GracefulFailoverRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(input);
        }
        
        public static GracefulFailoverRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GracefulFailoverRequestProto prototype) {
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
            GracefulFailoverRequestProto.PARSER = new AbstractParser<GracefulFailoverRequestProto>() {
                @Override
                public GracefulFailoverRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GracefulFailoverRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GracefulFailoverRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GracefulFailoverRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GracefulFailoverRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GracefulFailoverRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverRequestProto_descriptor;
            }
            
            @Override
            public GracefulFailoverRequestProto getDefaultInstanceForType() {
                return GracefulFailoverRequestProto.getDefaultInstance();
            }
            
            @Override
            public GracefulFailoverRequestProto build() {
                final GracefulFailoverRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GracefulFailoverRequestProto buildPartial() {
                final GracefulFailoverRequestProto result = new GracefulFailoverRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GracefulFailoverRequestProto) {
                    return this.mergeFrom((GracefulFailoverRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GracefulFailoverRequestProto other) {
                if (other == GracefulFailoverRequestProto.getDefaultInstance()) {
                    return this;
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GracefulFailoverRequestProto parsedMessage = null;
                try {
                    parsedMessage = GracefulFailoverRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GracefulFailoverRequestProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
        }
    }
    
    public static final class GracefulFailoverResponseProto extends GeneratedMessage implements GracefulFailoverResponseProtoOrBuilder
    {
        private static final GracefulFailoverResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GracefulFailoverResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GracefulFailoverResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GracefulFailoverResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GracefulFailoverResponseProto getDefaultInstance() {
            return GracefulFailoverResponseProto.defaultInstance;
        }
        
        @Override
        public GracefulFailoverResponseProto getDefaultInstanceForType() {
            return GracefulFailoverResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GracefulFailoverResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.initFields();
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
            return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GracefulFailoverResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GracefulFailoverResponseProto> getParserForType() {
            return GracefulFailoverResponseProto.PARSER;
        }
        
        private void initFields() {
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
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
            if (!(obj instanceof GracefulFailoverResponseProto)) {
                return super.equals(obj);
            }
            final GracefulFailoverResponseProto other = (GracefulFailoverResponseProto)obj;
            boolean result = true;
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
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GracefulFailoverResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(data);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(data);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final InputStream input) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(input);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GracefulFailoverResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GracefulFailoverResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(input);
        }
        
        public static GracefulFailoverResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GracefulFailoverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GracefulFailoverResponseProto prototype) {
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
            GracefulFailoverResponseProto.PARSER = new AbstractParser<GracefulFailoverResponseProto>() {
                @Override
                public GracefulFailoverResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GracefulFailoverResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GracefulFailoverResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GracefulFailoverResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GracefulFailoverResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GracefulFailoverResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ZKFCProtocolProtos.internal_static_hadoop_common_GracefulFailoverResponseProto_descriptor;
            }
            
            @Override
            public GracefulFailoverResponseProto getDefaultInstanceForType() {
                return GracefulFailoverResponseProto.getDefaultInstance();
            }
            
            @Override
            public GracefulFailoverResponseProto build() {
                final GracefulFailoverResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GracefulFailoverResponseProto buildPartial() {
                final GracefulFailoverResponseProto result = new GracefulFailoverResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GracefulFailoverResponseProto) {
                    return this.mergeFrom((GracefulFailoverResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GracefulFailoverResponseProto other) {
                if (other == GracefulFailoverResponseProto.getDefaultInstance()) {
                    return this;
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GracefulFailoverResponseProto parsedMessage = null;
                try {
                    parsedMessage = GracefulFailoverResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GracefulFailoverResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
        }
    }
    
    public abstract static class ZKFCProtocolService implements Service
    {
        protected ZKFCProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ZKFCProtocolService() {
                @Override
                public void cedeActive(final RpcController controller, final CedeActiveRequestProto request, final RpcCallback<CedeActiveResponseProto> done) {
                    impl.cedeActive(controller, request, done);
                }
                
                @Override
                public void gracefulFailover(final RpcController controller, final GracefulFailoverRequestProto request, final RpcCallback<GracefulFailoverResponseProto> done) {
                    impl.gracefulFailover(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ZKFCProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ZKFCProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.cedeActive(controller, (CedeActiveRequestProto)request);
                        }
                        case 1: {
                            return impl.gracefulFailover(controller, (GracefulFailoverRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ZKFCProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return CedeActiveRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return GracefulFailoverRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ZKFCProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return CedeActiveResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return GracefulFailoverResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void cedeActive(final RpcController p0, final CedeActiveRequestProto p1, final RpcCallback<CedeActiveResponseProto> p2);
        
        public abstract void gracefulFailover(final RpcController p0, final GracefulFailoverRequestProto p1, final RpcCallback<GracefulFailoverResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ZKFCProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.cedeActive(controller, (CedeActiveRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.gracefulFailover(controller, (GracefulFailoverRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return CedeActiveRequestProto.getDefaultInstance();
                }
                case 1: {
                    return GracefulFailoverRequestProto.getDefaultInstance();
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
                    return CedeActiveResponseProto.getDefaultInstance();
                }
                case 1: {
                    return GracefulFailoverResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ZKFCProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void cedeActive(final RpcController controller, final CedeActiveRequestProto request, final RpcCallback<CedeActiveResponseProto> done) {
                this.channel.callMethod(ZKFCProtocolService.getDescriptor().getMethods().get(0), controller, request, CedeActiveResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, CedeActiveResponseProto.class, CedeActiveResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void gracefulFailover(final RpcController controller, final GracefulFailoverRequestProto request, final RpcCallback<GracefulFailoverResponseProto> done) {
                this.channel.callMethod(ZKFCProtocolService.getDescriptor().getMethods().get(1), controller, request, GracefulFailoverResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GracefulFailoverResponseProto.class, GracefulFailoverResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public CedeActiveResponseProto cedeActive(final RpcController controller, final CedeActiveRequestProto request) throws ServiceException {
                return (CedeActiveResponseProto)this.channel.callBlockingMethod(ZKFCProtocolService.getDescriptor().getMethods().get(0), controller, request, CedeActiveResponseProto.getDefaultInstance());
            }
            
            @Override
            public GracefulFailoverResponseProto gracefulFailover(final RpcController controller, final GracefulFailoverRequestProto request) throws ServiceException {
                return (GracefulFailoverResponseProto)this.channel.callBlockingMethod(ZKFCProtocolService.getDescriptor().getMethods().get(1), controller, request, GracefulFailoverResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            CedeActiveResponseProto cedeActive(final RpcController p0, final CedeActiveRequestProto p1) throws ServiceException;
            
            GracefulFailoverResponseProto gracefulFailover(final RpcController p0, final GracefulFailoverRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void cedeActive(final RpcController p0, final CedeActiveRequestProto p1, final RpcCallback<CedeActiveResponseProto> p2);
            
            void gracefulFailover(final RpcController p0, final GracefulFailoverRequestProto p1, final RpcCallback<GracefulFailoverResponseProto> p2);
        }
    }
    
    public interface CedeActiveRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasMillisToCede();
        
        int getMillisToCede();
    }
    
    public interface CedeActiveResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface GracefulFailoverRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface GracefulFailoverResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
}
