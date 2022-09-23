// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.proto;

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

public final class RefreshCallQueueProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshCallQueueRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RefreshCallQueueResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private RefreshCallQueueProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return RefreshCallQueueProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u001eRefreshCallQueueProtocol.proto\u0012\rhadoop.common\"\u001e\n\u001cRefreshCallQueueRequestProto\"\u001f\n\u001dRefreshCallQueueResponseProto2\u0090\u0001\n\u001fRefreshCallQueueProtocolService\u0012m\n\u0010refreshCallQueue\u0012+.hadoop.common.RefreshCallQueueRequestProto\u001a,.hadoop.common.RefreshCallQueueResponseProtoBC\n\u001borg.apache.hadoop.ipc.protoB\u001eRefreshCallQueueProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                RefreshCallQueueProtocolProtos.descriptor = root;
                RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor = RefreshCallQueueProtocolProtos.getDescriptor().getMessageTypes().get(0);
                RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor, new String[0]);
                RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor = RefreshCallQueueProtocolProtos.getDescriptor().getMessageTypes().get(1);
                RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class RefreshCallQueueRequestProto extends GeneratedMessage implements RefreshCallQueueRequestProtoOrBuilder
    {
        private static final RefreshCallQueueRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshCallQueueRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshCallQueueRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshCallQueueRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshCallQueueRequestProto getDefaultInstance() {
            return RefreshCallQueueRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshCallQueueRequestProto getDefaultInstanceForType() {
            return RefreshCallQueueRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshCallQueueRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshCallQueueRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshCallQueueRequestProto> getParserForType() {
            return RefreshCallQueueRequestProto.PARSER;
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
            if (!(obj instanceof RefreshCallQueueRequestProto)) {
                return super.equals(obj);
            }
            final RefreshCallQueueRequestProto other = (RefreshCallQueueRequestProto)obj;
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
        
        public static RefreshCallQueueRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshCallQueueRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshCallQueueRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshCallQueueRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshCallQueueRequestProto prototype) {
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
            RefreshCallQueueRequestProto.PARSER = new AbstractParser<RefreshCallQueueRequestProto>() {
                @Override
                public RefreshCallQueueRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshCallQueueRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshCallQueueRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshCallQueueRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshCallQueueRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshCallQueueRequestProto.alwaysUseFieldBuilders) {}
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
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueRequestProto_descriptor;
            }
            
            @Override
            public RefreshCallQueueRequestProto getDefaultInstanceForType() {
                return RefreshCallQueueRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshCallQueueRequestProto build() {
                final RefreshCallQueueRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshCallQueueRequestProto buildPartial() {
                final RefreshCallQueueRequestProto result = new RefreshCallQueueRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshCallQueueRequestProto) {
                    return this.mergeFrom((RefreshCallQueueRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshCallQueueRequestProto other) {
                if (other == RefreshCallQueueRequestProto.getDefaultInstance()) {
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
                RefreshCallQueueRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshCallQueueRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshCallQueueRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshCallQueueResponseProto extends GeneratedMessage implements RefreshCallQueueResponseProtoOrBuilder
    {
        private static final RefreshCallQueueResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshCallQueueResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshCallQueueResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshCallQueueResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshCallQueueResponseProto getDefaultInstance() {
            return RefreshCallQueueResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshCallQueueResponseProto getDefaultInstanceForType() {
            return RefreshCallQueueResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshCallQueueResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshCallQueueResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshCallQueueResponseProto> getParserForType() {
            return RefreshCallQueueResponseProto.PARSER;
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
            if (!(obj instanceof RefreshCallQueueResponseProto)) {
                return super.equals(obj);
            }
            final RefreshCallQueueResponseProto other = (RefreshCallQueueResponseProto)obj;
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
        
        public static RefreshCallQueueResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshCallQueueResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshCallQueueResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshCallQueueResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshCallQueueResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshCallQueueResponseProto prototype) {
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
            RefreshCallQueueResponseProto.PARSER = new AbstractParser<RefreshCallQueueResponseProto>() {
                @Override
                public RefreshCallQueueResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshCallQueueResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshCallQueueResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshCallQueueResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshCallQueueResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshCallQueueResponseProto.alwaysUseFieldBuilders) {}
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
                return RefreshCallQueueProtocolProtos.internal_static_hadoop_common_RefreshCallQueueResponseProto_descriptor;
            }
            
            @Override
            public RefreshCallQueueResponseProto getDefaultInstanceForType() {
                return RefreshCallQueueResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshCallQueueResponseProto build() {
                final RefreshCallQueueResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshCallQueueResponseProto buildPartial() {
                final RefreshCallQueueResponseProto result = new RefreshCallQueueResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshCallQueueResponseProto) {
                    return this.mergeFrom((RefreshCallQueueResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshCallQueueResponseProto other) {
                if (other == RefreshCallQueueResponseProto.getDefaultInstance()) {
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
                RefreshCallQueueResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshCallQueueResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshCallQueueResponseProto)e.getUnfinishedMessage();
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
    
    public abstract static class RefreshCallQueueProtocolService implements Service
    {
        protected RefreshCallQueueProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new RefreshCallQueueProtocolService() {
                @Override
                public void refreshCallQueue(final RpcController controller, final RefreshCallQueueRequestProto request, final RpcCallback<RefreshCallQueueResponseProto> done) {
                    impl.refreshCallQueue(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return RefreshCallQueueProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != RefreshCallQueueProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.refreshCallQueue(controller, (RefreshCallQueueRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshCallQueueProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshCallQueueRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != RefreshCallQueueProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return RefreshCallQueueResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void refreshCallQueue(final RpcController p0, final RefreshCallQueueRequestProto p1, final RpcCallback<RefreshCallQueueResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return RefreshCallQueueProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.refreshCallQueue(controller, (RefreshCallQueueRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return RefreshCallQueueRequestProto.getDefaultInstance();
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
                    return RefreshCallQueueResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends RefreshCallQueueProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void refreshCallQueue(final RpcController controller, final RefreshCallQueueRequestProto request, final RpcCallback<RefreshCallQueueResponseProto> done) {
                this.channel.callMethod(RefreshCallQueueProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshCallQueueResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, RefreshCallQueueResponseProto.class, RefreshCallQueueResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public RefreshCallQueueResponseProto refreshCallQueue(final RpcController controller, final RefreshCallQueueRequestProto request) throws ServiceException {
                return (RefreshCallQueueResponseProto)this.channel.callBlockingMethod(RefreshCallQueueProtocolService.getDescriptor().getMethods().get(0), controller, request, RefreshCallQueueResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            RefreshCallQueueResponseProto refreshCallQueue(final RpcController p0, final RefreshCallQueueRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void refreshCallQueue(final RpcController p0, final RefreshCallQueueRequestProto p1, final RpcCallback<RefreshCallQueueResponseProto> p2);
        }
    }
    
    public interface RefreshCallQueueResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshCallQueueRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
}
