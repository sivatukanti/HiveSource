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

public final class RefreshAuthorizationPolicyProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshServiceAclRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshServiceAclResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private RefreshAuthorizationPolicyProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return RefreshAuthorizationPolicyProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n(RefreshAuthorizationPolicyProtocol.proto\u0012\rhadoop.common\"\u001f\n\u001dRefreshServiceAclRequestProto\" \n\u001eRefreshServiceAclResponseProto2\u009d\u0001\n)RefreshAuthorizationPolicyProtocolService\u0012p\n\u0011refreshServiceAcl\u0012,.hadoop.common.RefreshServiceAclRequestProto\u001a-.hadoop.common.RefreshServiceAclResponseProtoBR\n org.apache.hadoop.security.protoB(RefreshAuthorizationPolicyProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                RefreshAuthorizationPolicyProtocolProtos.descriptor = root;
                RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor = RefreshAuthorizationPolicyProtocolProtos.getDescriptor().getMessageTypes().get(0);
                RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor, new String[0]);
                RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor = RefreshAuthorizationPolicyProtocolProtos.getDescriptor().getMessageTypes().get(1);
                RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class RefreshServiceAclRequestProto extends GeneratedMessage implements RefreshServiceAclRequestProtoOrBuilder
    {
        private static final RefreshServiceAclRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshServiceAclRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshServiceAclRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshServiceAclRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshServiceAclRequestProto getDefaultInstance() {
            return RefreshServiceAclRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshServiceAclRequestProto getDefaultInstanceForType() {
            return RefreshServiceAclRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshServiceAclRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshServiceAclRequestProto> getParserForType() {
            return RefreshServiceAclRequestProto.PARSER;
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
            if (!(obj instanceof RefreshServiceAclRequestProto)) {
                return super.equals(obj);
            }
            final RefreshServiceAclRequestProto other = (RefreshServiceAclRequestProto)obj;
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
        
        public static RefreshServiceAclRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshServiceAclRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshServiceAclRequestProto prototype) {
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
            RefreshServiceAclRequestProto.PARSER = new AbstractParser<RefreshServiceAclRequestProto>() {
                @Override
                public RefreshServiceAclRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshServiceAclRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshServiceAclRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshServiceAclRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshServiceAclRequestProto.alwaysUseFieldBuilders) {}
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
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclRequestProto_descriptor;
            }
            
            @Override
            public RefreshServiceAclRequestProto getDefaultInstanceForType() {
                return RefreshServiceAclRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshServiceAclRequestProto build() {
                final RefreshServiceAclRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshServiceAclRequestProto buildPartial() {
                final RefreshServiceAclRequestProto result = new RefreshServiceAclRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshServiceAclRequestProto) {
                    return this.mergeFrom((RefreshServiceAclRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshServiceAclRequestProto other) {
                if (other == RefreshServiceAclRequestProto.getDefaultInstance()) {
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
                RefreshServiceAclRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshServiceAclRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshServiceAclRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshServiceAclResponseProto extends GeneratedMessage implements RefreshServiceAclResponseProtoOrBuilder
    {
        private static final RefreshServiceAclResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshServiceAclResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshServiceAclResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshServiceAclResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshServiceAclResponseProto getDefaultInstance() {
            return RefreshServiceAclResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshServiceAclResponseProto getDefaultInstanceForType() {
            return RefreshServiceAclResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshServiceAclResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshServiceAclResponseProto> getParserForType() {
            return RefreshServiceAclResponseProto.PARSER;
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
            if (!(obj instanceof RefreshServiceAclResponseProto)) {
                return super.equals(obj);
            }
            final RefreshServiceAclResponseProto other = (RefreshServiceAclResponseProto)obj;
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
        
        public static RefreshServiceAclResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshServiceAclResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshServiceAclResponseProto prototype) {
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
            RefreshServiceAclResponseProto.PARSER = new AbstractParser<RefreshServiceAclResponseProto>() {
                @Override
                public RefreshServiceAclResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshServiceAclResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshServiceAclResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshServiceAclResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshServiceAclResponseProto.alwaysUseFieldBuilders) {}
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
                return RefreshAuthorizationPolicyProtocolProtos.internal_static_hadoop_common_RefreshServiceAclResponseProto_descriptor;
            }
            
            @Override
            public RefreshServiceAclResponseProto getDefaultInstanceForType() {
                return RefreshServiceAclResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshServiceAclResponseProto build() {
                final RefreshServiceAclResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshServiceAclResponseProto buildPartial() {
                final RefreshServiceAclResponseProto result = new RefreshServiceAclResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshServiceAclResponseProto) {
                    return this.mergeFrom((RefreshServiceAclResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshServiceAclResponseProto other) {
                if (other == RefreshServiceAclResponseProto.getDefaultInstance()) {
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
                RefreshServiceAclResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshServiceAclResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshServiceAclResponseProto)e.getUnfinishedMessage();
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
    
    public abstract static class RefreshAuthorizationPolicyProtocolService implements Service
    {
        protected RefreshAuthorizationPolicyProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new RefreshAuthorizationPolicyProtocolService() {
                @Override
                public void refreshServiceAcl(final RpcController controller, final RefreshServiceAclRequestProto request, final RpcCallback<RefreshServiceAclResponseProto> done) {
                    impl.refreshServiceAcl(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return RefreshAuthorizationPolicyProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != RefreshAuthorizationPolicyProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.refreshServiceAcl(controller, (RefreshServiceAclRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshAuthorizationPolicyProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshServiceAclRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshAuthorizationPolicyProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshServiceAclResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void refreshServiceAcl(final RpcController p0, final RefreshServiceAclRequestProto p1, final RpcCallback<RefreshServiceAclResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return RefreshAuthorizationPolicyProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.refreshServiceAcl(controller, (RefreshServiceAclRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return RefreshServiceAclRequestProto.getDefaultInstance();
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
                    return RefreshServiceAclResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends RefreshAuthorizationPolicyProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void refreshServiceAcl(final RpcController controller, final RefreshServiceAclRequestProto request, final RpcCallback<RefreshServiceAclResponseProto> done) {
                this.channel.callMethod(RefreshAuthorizationPolicyProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshServiceAclResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, RefreshServiceAclResponseProto.class, RefreshServiceAclResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public RefreshServiceAclResponseProto refreshServiceAcl(final RpcController controller, final RefreshServiceAclRequestProto request) throws ServiceException {
                return (RefreshServiceAclResponseProto)this.channel.callBlockingMethod(RefreshAuthorizationPolicyProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshServiceAclResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            RefreshServiceAclResponseProto refreshServiceAcl(final RpcController p0, final RefreshServiceAclRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void refreshServiceAcl(final RpcController p0, final RefreshServiceAclRequestProto p1, final RpcCallback<RefreshServiceAclResponseProto> p2);
        }
    }
    
    public interface RefreshServiceAclResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshServiceAclRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
}
