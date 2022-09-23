// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.proto;

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

public final class RefreshUserMappingsProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private RefreshUserMappingsProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return RefreshUserMappingsProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n!RefreshUserMappingsProtocol.proto\u0012\rhadoop.common\")\n'RefreshUserToGroupsMappingsRequestProto\"*\n(RefreshUserToGroupsMappingsResponseProto\"1\n/RefreshSuperUserGroupsConfigurationRequestProto\"2\n0RefreshSuperUserGroupsConfigurationResponseProto2\u00de\u0002\n\"RefreshUserMappingsProtocolService\u0012\u008e\u0001\n\u001brefreshUserToGroupsMappings\u00126.hadoop.common.RefreshUserToGroupsMappingsRequestProto\u001a7.hadoop.common.RefreshUserToGro", "upsMappingsResponseProto\u0012¦\u0001\n#refreshSuperUserGroupsConfiguration\u0012>.hadoop.common.RefreshSuperUserGroupsConfigurationRequestProto\u001a?.hadoop.common.RefreshSuperUserGroupsConfigurationResponseProtoBK\n org.apache.hadoop.security.protoB!RefreshUserMappingsProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                RefreshUserMappingsProtocolProtos.descriptor = root;
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor = RefreshUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(0);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor, new String[0]);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor = RefreshUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(1);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor, new String[0]);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor = RefreshUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(2);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor, new String[0]);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor = RefreshUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(3);
                RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class RefreshUserToGroupsMappingsRequestProto extends GeneratedMessage implements RefreshUserToGroupsMappingsRequestProtoOrBuilder
    {
        private static final RefreshUserToGroupsMappingsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshUserToGroupsMappingsRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshUserToGroupsMappingsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshUserToGroupsMappingsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshUserToGroupsMappingsRequestProto getDefaultInstance() {
            return RefreshUserToGroupsMappingsRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshUserToGroupsMappingsRequestProto getDefaultInstanceForType() {
            return RefreshUserToGroupsMappingsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshUserToGroupsMappingsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshUserToGroupsMappingsRequestProto> getParserForType() {
            return RefreshUserToGroupsMappingsRequestProto.PARSER;
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
            if (!(obj instanceof RefreshUserToGroupsMappingsRequestProto)) {
                return super.equals(obj);
            }
            final RefreshUserToGroupsMappingsRequestProto other = (RefreshUserToGroupsMappingsRequestProto)obj;
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
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshUserToGroupsMappingsRequestProto prototype) {
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
            RefreshUserToGroupsMappingsRequestProto.PARSER = new AbstractParser<RefreshUserToGroupsMappingsRequestProto>() {
                @Override
                public RefreshUserToGroupsMappingsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshUserToGroupsMappingsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshUserToGroupsMappingsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshUserToGroupsMappingsRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshUserToGroupsMappingsRequestProto.alwaysUseFieldBuilders) {}
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
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsRequestProto_descriptor;
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto getDefaultInstanceForType() {
                return RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto build() {
                final RefreshUserToGroupsMappingsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto buildPartial() {
                final RefreshUserToGroupsMappingsRequestProto result = new RefreshUserToGroupsMappingsRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshUserToGroupsMappingsRequestProto) {
                    return this.mergeFrom((RefreshUserToGroupsMappingsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshUserToGroupsMappingsRequestProto other) {
                if (other == RefreshUserToGroupsMappingsRequestProto.getDefaultInstance()) {
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
                RefreshUserToGroupsMappingsRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshUserToGroupsMappingsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshUserToGroupsMappingsRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshUserToGroupsMappingsResponseProto extends GeneratedMessage implements RefreshUserToGroupsMappingsResponseProtoOrBuilder
    {
        private static final RefreshUserToGroupsMappingsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshUserToGroupsMappingsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshUserToGroupsMappingsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshUserToGroupsMappingsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshUserToGroupsMappingsResponseProto getDefaultInstance() {
            return RefreshUserToGroupsMappingsResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshUserToGroupsMappingsResponseProto getDefaultInstanceForType() {
            return RefreshUserToGroupsMappingsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshUserToGroupsMappingsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshUserToGroupsMappingsResponseProto> getParserForType() {
            return RefreshUserToGroupsMappingsResponseProto.PARSER;
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
            if (!(obj instanceof RefreshUserToGroupsMappingsResponseProto)) {
                return super.equals(obj);
            }
            final RefreshUserToGroupsMappingsResponseProto other = (RefreshUserToGroupsMappingsResponseProto)obj;
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
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshUserToGroupsMappingsResponseProto prototype) {
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
            RefreshUserToGroupsMappingsResponseProto.PARSER = new AbstractParser<RefreshUserToGroupsMappingsResponseProto>() {
                @Override
                public RefreshUserToGroupsMappingsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshUserToGroupsMappingsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshUserToGroupsMappingsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshUserToGroupsMappingsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshUserToGroupsMappingsResponseProto.alwaysUseFieldBuilders) {}
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
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshUserToGroupsMappingsResponseProto_descriptor;
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto getDefaultInstanceForType() {
                return RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto build() {
                final RefreshUserToGroupsMappingsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto buildPartial() {
                final RefreshUserToGroupsMappingsResponseProto result = new RefreshUserToGroupsMappingsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshUserToGroupsMappingsResponseProto) {
                    return this.mergeFrom((RefreshUserToGroupsMappingsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshUserToGroupsMappingsResponseProto other) {
                if (other == RefreshUserToGroupsMappingsResponseProto.getDefaultInstance()) {
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
                RefreshUserToGroupsMappingsResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshUserToGroupsMappingsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshUserToGroupsMappingsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshSuperUserGroupsConfigurationRequestProto extends GeneratedMessage implements RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder
    {
        private static final RefreshSuperUserGroupsConfigurationRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshSuperUserGroupsConfigurationRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstance() {
            return RefreshSuperUserGroupsConfigurationRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstanceForType() {
            return RefreshSuperUserGroupsConfigurationRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshSuperUserGroupsConfigurationRequestProto> getParserForType() {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER;
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
            if (!(obj instanceof RefreshSuperUserGroupsConfigurationRequestProto)) {
                return super.equals(obj);
            }
            final RefreshSuperUserGroupsConfigurationRequestProto other = (RefreshSuperUserGroupsConfigurationRequestProto)obj;
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
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshSuperUserGroupsConfigurationRequestProto prototype) {
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
            RefreshSuperUserGroupsConfigurationRequestProto.PARSER = new AbstractParser<RefreshSuperUserGroupsConfigurationRequestProto>() {
                @Override
                public RefreshSuperUserGroupsConfigurationRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshSuperUserGroupsConfigurationRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshSuperUserGroupsConfigurationRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshSuperUserGroupsConfigurationRequestProto.alwaysUseFieldBuilders) {}
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
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstanceForType() {
                return RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto build() {
                final RefreshSuperUserGroupsConfigurationRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto buildPartial() {
                final RefreshSuperUserGroupsConfigurationRequestProto result = new RefreshSuperUserGroupsConfigurationRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshSuperUserGroupsConfigurationRequestProto) {
                    return this.mergeFrom((RefreshSuperUserGroupsConfigurationRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshSuperUserGroupsConfigurationRequestProto other) {
                if (other == RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance()) {
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
                RefreshSuperUserGroupsConfigurationRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshSuperUserGroupsConfigurationRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshSuperUserGroupsConfigurationResponseProto extends GeneratedMessage implements RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder
    {
        private static final RefreshSuperUserGroupsConfigurationResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshSuperUserGroupsConfigurationResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstance() {
            return RefreshSuperUserGroupsConfigurationResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstanceForType() {
            return RefreshSuperUserGroupsConfigurationResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshSuperUserGroupsConfigurationResponseProto> getParserForType() {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER;
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
            if (!(obj instanceof RefreshSuperUserGroupsConfigurationResponseProto)) {
                return super.equals(obj);
            }
            final RefreshSuperUserGroupsConfigurationResponseProto other = (RefreshSuperUserGroupsConfigurationResponseProto)obj;
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
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshSuperUserGroupsConfigurationResponseProto prototype) {
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
            RefreshSuperUserGroupsConfigurationResponseProto.PARSER = new AbstractParser<RefreshSuperUserGroupsConfigurationResponseProto>() {
                @Override
                public RefreshSuperUserGroupsConfigurationResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshSuperUserGroupsConfigurationResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshSuperUserGroupsConfigurationResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshSuperUserGroupsConfigurationResponseProto.alwaysUseFieldBuilders) {}
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
                return RefreshUserMappingsProtocolProtos.internal_static_hadoop_common_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstanceForType() {
                return RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto build() {
                final RefreshSuperUserGroupsConfigurationResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto buildPartial() {
                final RefreshSuperUserGroupsConfigurationResponseProto result = new RefreshSuperUserGroupsConfigurationResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshSuperUserGroupsConfigurationResponseProto) {
                    return this.mergeFrom((RefreshSuperUserGroupsConfigurationResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshSuperUserGroupsConfigurationResponseProto other) {
                if (other == RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance()) {
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
                RefreshSuperUserGroupsConfigurationResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshSuperUserGroupsConfigurationResponseProto)e.getUnfinishedMessage();
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
    
    public abstract static class RefreshUserMappingsProtocolService implements Service
    {
        protected RefreshUserMappingsProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new RefreshUserMappingsProtocolService() {
                @Override
                public void refreshUserToGroupsMappings(final RpcController controller, final RefreshUserToGroupsMappingsRequestProto request, final RpcCallback<RefreshUserToGroupsMappingsResponseProto> done) {
                    impl.refreshUserToGroupsMappings(controller, request, done);
                }
                
                @Override
                public void refreshSuperUserGroupsConfiguration(final RpcController controller, final RefreshSuperUserGroupsConfigurationRequestProto request, final RpcCallback<RefreshSuperUserGroupsConfigurationResponseProto> done) {
                    impl.refreshSuperUserGroupsConfiguration(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return RefreshUserMappingsProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != RefreshUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.refreshUserToGroupsMappings(controller, (RefreshUserToGroupsMappingsRequestProto)request);
                        }
                        case 1: {
                            return impl.refreshSuperUserGroupsConfiguration(controller, (RefreshSuperUserGroupsConfigurationRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void refreshUserToGroupsMappings(final RpcController p0, final RefreshUserToGroupsMappingsRequestProto p1, final RpcCallback<RefreshUserToGroupsMappingsResponseProto> p2);
        
        public abstract void refreshSuperUserGroupsConfiguration(final RpcController p0, final RefreshSuperUserGroupsConfigurationRequestProto p1, final RpcCallback<RefreshSuperUserGroupsConfigurationResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return RefreshUserMappingsProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.refreshUserToGroupsMappings(controller, (RefreshUserToGroupsMappingsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.refreshSuperUserGroupsConfiguration(controller, (RefreshSuperUserGroupsConfigurationRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
                }
                case 1: {
                    return RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
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
                    return RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
                }
                case 1: {
                    return RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends RefreshUserMappingsProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void refreshUserToGroupsMappings(final RpcController controller, final RefreshUserToGroupsMappingsRequestProto request, final RpcCallback<RefreshUserToGroupsMappingsResponseProto> done) {
                this.channel.callMethod(RefreshUserMappingsProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshUserToGroupsMappingsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, RefreshUserToGroupsMappingsResponseProto.class, RefreshUserToGroupsMappingsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshSuperUserGroupsConfiguration(final RpcController controller, final RefreshSuperUserGroupsConfigurationRequestProto request, final RpcCallback<RefreshSuperUserGroupsConfigurationResponseProto> done) {
                this.channel.callMethod(RefreshUserMappingsProtocolService.getDescriptor().getMethods().get(1), controller, request, RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, RefreshSuperUserGroupsConfigurationResponseProto.class, RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController controller, final RefreshUserToGroupsMappingsRequestProto request) throws ServiceException {
                return (RefreshUserToGroupsMappingsResponseProto)this.channel.callBlockingMethod(RefreshUserMappingsProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshUserToGroupsMappingsResponseProto.getDefaultInstance());
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController controller, final RefreshSuperUserGroupsConfigurationRequestProto request) throws ServiceException {
                return (RefreshSuperUserGroupsConfigurationResponseProto)this.channel.callBlockingMethod(RefreshUserMappingsProtocolService.getDescriptor().getMethods().get(1), controller, request, RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController p0, final RefreshUserToGroupsMappingsRequestProto p1) throws ServiceException;
            
            RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController p0, final RefreshSuperUserGroupsConfigurationRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void refreshUserToGroupsMappings(final RpcController p0, final RefreshUserToGroupsMappingsRequestProto p1, final RpcCallback<RefreshUserToGroupsMappingsResponseProto> p2);
            
            void refreshSuperUserGroupsConfiguration(final RpcController p0, final RefreshSuperUserGroupsConfigurationRequestProto p1, final RpcCallback<RefreshSuperUserGroupsConfigurationResponseProto> p2);
        }
    }
    
    public interface RefreshUserToGroupsMappingsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshUserToGroupsMappingsRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
}
