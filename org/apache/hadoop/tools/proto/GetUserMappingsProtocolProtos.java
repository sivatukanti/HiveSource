// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import com.google.protobuf.UnmodifiableLazyStringList;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
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

public final class GetUserMappingsProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetGroupsForUserRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetGroupsForUserResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private GetUserMappingsProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return GetUserMappingsProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u001dGetUserMappingsProtocol.proto\u0012\rhadoop.common\",\n\u001cGetGroupsForUserRequestProto\u0012\f\n\u0004user\u0018\u0001 \u0002(\t\"/\n\u001dGetGroupsForUserResponseProto\u0012\u000e\n\u0006groups\u0018\u0001 \u0003(\t2\u008f\u0001\n\u001eGetUserMappingsProtocolService\u0012m\n\u0010getGroupsForUser\u0012+.hadoop.common.GetGroupsForUserRequestProto\u001a,.hadoop.common.GetGroupsForUserResponseProtoBD\n\u001dorg.apache.hadoop.tools.protoB\u001dGetUserMappingsProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                GetUserMappingsProtocolProtos.descriptor = root;
                GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor = GetUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(0);
                GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor, new String[] { "User" });
                GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor = GetUserMappingsProtocolProtos.getDescriptor().getMessageTypes().get(1);
                GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor, new String[] { "Groups" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class GetGroupsForUserRequestProto extends GeneratedMessage implements GetGroupsForUserRequestProtoOrBuilder
    {
        private static final GetGroupsForUserRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetGroupsForUserRequestProto> PARSER;
        private int bitField0_;
        public static final int USER_FIELD_NUMBER = 1;
        private Object user_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetGroupsForUserRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetGroupsForUserRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetGroupsForUserRequestProto getDefaultInstance() {
            return GetGroupsForUserRequestProto.defaultInstance;
        }
        
        @Override
        public GetGroupsForUserRequestProto getDefaultInstanceForType() {
            return GetGroupsForUserRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetGroupsForUserRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.user_ = input.readBytes();
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
            return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetGroupsForUserRequestProto> getParserForType() {
            return GetGroupsForUserRequestProto.PARSER;
        }
        
        @Override
        public boolean hasUser() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getUser() {
            final Object ref = this.user_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.user_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getUserBytes() {
            final Object ref = this.user_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.user_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.user_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasUser()) {
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
                output.writeBytes(1, this.getUserBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getUserBytes());
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
            if (!(obj instanceof GetGroupsForUserRequestProto)) {
                return super.equals(obj);
            }
            final GetGroupsForUserRequestProto other = (GetGroupsForUserRequestProto)obj;
            boolean result = true;
            result = (result && this.hasUser() == other.hasUser());
            if (this.hasUser()) {
                result = (result && this.getUser().equals(other.getUser()));
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
            if (this.hasUser()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getUser().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final InputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetGroupsForUserRequestProto prototype) {
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
            GetGroupsForUserRequestProto.PARSER = new AbstractParser<GetGroupsForUserRequestProto>() {
                @Override
                public GetGroupsForUserRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetGroupsForUserRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetGroupsForUserRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetGroupsForUserRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object user_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.user_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.user_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetGroupsForUserRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.user_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserRequestProto_descriptor;
            }
            
            @Override
            public GetGroupsForUserRequestProto getDefaultInstanceForType() {
                return GetGroupsForUserRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetGroupsForUserRequestProto build() {
                final GetGroupsForUserRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetGroupsForUserRequestProto buildPartial() {
                final GetGroupsForUserRequestProto result = new GetGroupsForUserRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.user_ = this.user_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetGroupsForUserRequestProto) {
                    return this.mergeFrom((GetGroupsForUserRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetGroupsForUserRequestProto other) {
                if (other == GetGroupsForUserRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasUser()) {
                    this.bitField0_ |= 0x1;
                    this.user_ = other.user_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasUser();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetGroupsForUserRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetGroupsForUserRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetGroupsForUserRequestProto)e.getUnfinishedMessage();
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
            public boolean hasUser() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getUser() {
                final Object ref = this.user_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.user_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getUserBytes() {
                final Object ref = this.user_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.user_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setUser(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUser() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.user_ = GetGroupsForUserRequestProto.getDefaultInstance().getUser();
                this.onChanged();
                return this;
            }
            
            public Builder setUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.user_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GetGroupsForUserResponseProto extends GeneratedMessage implements GetGroupsForUserResponseProtoOrBuilder
    {
        private static final GetGroupsForUserResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetGroupsForUserResponseProto> PARSER;
        public static final int GROUPS_FIELD_NUMBER = 1;
        private LazyStringList groups_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetGroupsForUserResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetGroupsForUserResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetGroupsForUserResponseProto getDefaultInstance() {
            return GetGroupsForUserResponseProto.defaultInstance;
        }
        
        @Override
        public GetGroupsForUserResponseProto getDefaultInstanceForType() {
            return GetGroupsForUserResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetGroupsForUserResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.groups_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.groups_.add(input.readBytes());
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
                    this.groups_ = new UnmodifiableLazyStringList(this.groups_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetGroupsForUserResponseProto> getParserForType() {
            return GetGroupsForUserResponseProto.PARSER;
        }
        
        @Override
        public List<String> getGroupsList() {
            return this.groups_;
        }
        
        @Override
        public int getGroupsCount() {
            return this.groups_.size();
        }
        
        @Override
        public String getGroups(final int index) {
            return this.groups_.get(index);
        }
        
        @Override
        public ByteString getGroupsBytes(final int index) {
            return this.groups_.getByteString(index);
        }
        
        private void initFields() {
            this.groups_ = LazyStringArrayList.EMPTY;
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
            for (int i = 0; i < this.groups_.size(); ++i) {
                output.writeBytes(1, this.groups_.getByteString(i));
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
            int dataSize = 0;
            for (int i = 0; i < this.groups_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.groups_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getGroupsList().size();
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
            if (!(obj instanceof GetGroupsForUserResponseProto)) {
                return super.equals(obj);
            }
            final GetGroupsForUserResponseProto other = (GetGroupsForUserResponseProto)obj;
            boolean result = true;
            result = (result && this.getGroupsList().equals(other.getGroupsList()));
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
            if (this.getGroupsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getGroupsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final InputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetGroupsForUserResponseProto prototype) {
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
            GetGroupsForUserResponseProto.PARSER = new AbstractParser<GetGroupsForUserResponseProto>() {
                @Override
                public GetGroupsForUserResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetGroupsForUserResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetGroupsForUserResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetGroupsForUserResponseProtoOrBuilder
        {
            private int bitField0_;
            private LazyStringList groups_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.groups_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.groups_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetGroupsForUserResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.groups_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return GetUserMappingsProtocolProtos.internal_static_hadoop_common_GetGroupsForUserResponseProto_descriptor;
            }
            
            @Override
            public GetGroupsForUserResponseProto getDefaultInstanceForType() {
                return GetGroupsForUserResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetGroupsForUserResponseProto build() {
                final GetGroupsForUserResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetGroupsForUserResponseProto buildPartial() {
                final GetGroupsForUserResponseProto result = new GetGroupsForUserResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    this.groups_ = new UnmodifiableLazyStringList(this.groups_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.groups_ = this.groups_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetGroupsForUserResponseProto) {
                    return this.mergeFrom((GetGroupsForUserResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetGroupsForUserResponseProto other) {
                if (other == GetGroupsForUserResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (!other.groups_.isEmpty()) {
                    if (this.groups_.isEmpty()) {
                        this.groups_ = other.groups_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    else {
                        this.ensureGroupsIsMutable();
                        this.groups_.addAll(other.groups_);
                    }
                    this.onChanged();
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
                GetGroupsForUserResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetGroupsForUserResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetGroupsForUserResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureGroupsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.groups_ = new LazyStringArrayList(this.groups_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<String> getGroupsList() {
                return Collections.unmodifiableList((List<? extends String>)this.groups_);
            }
            
            @Override
            public int getGroupsCount() {
                return this.groups_.size();
            }
            
            @Override
            public String getGroups(final int index) {
                return this.groups_.get(index);
            }
            
            @Override
            public ByteString getGroupsBytes(final int index) {
                return this.groups_.getByteString(index);
            }
            
            public Builder setGroups(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addGroups(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllGroups(final Iterable<String> values) {
                this.ensureGroupsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.groups_);
                this.onChanged();
                return this;
            }
            
            public Builder clearGroups() {
                this.groups_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }
            
            public Builder addGroupsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.add(value);
                this.onChanged();
                return this;
            }
        }
    }
    
    public abstract static class GetUserMappingsProtocolService implements Service
    {
        protected GetUserMappingsProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new GetUserMappingsProtocolService() {
                @Override
                public void getGroupsForUser(final RpcController controller, final GetGroupsForUserRequestProto request, final RpcCallback<GetGroupsForUserResponseProto> done) {
                    impl.getGroupsForUser(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return GetUserMappingsProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != GetUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.getGroupsForUser(controller, (GetGroupsForUserRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != GetUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GetGroupsForUserRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != GetUserMappingsProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GetGroupsForUserResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void getGroupsForUser(final RpcController p0, final GetGroupsForUserRequestProto p1, final RpcCallback<GetGroupsForUserResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return GetUserMappingsProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.getGroupsForUser(controller, (GetGroupsForUserRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return GetGroupsForUserRequestProto.getDefaultInstance();
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
                    return GetGroupsForUserResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends GetUserMappingsProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void getGroupsForUser(final RpcController controller, final GetGroupsForUserRequestProto request, final RpcCallback<GetGroupsForUserResponseProto> done) {
                this.channel.callMethod(GetUserMappingsProtocolService.getDescriptor().getMethods().get(0), controller, request, GetGroupsForUserResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GetGroupsForUserResponseProto.class, GetGroupsForUserResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public GetGroupsForUserResponseProto getGroupsForUser(final RpcController controller, final GetGroupsForUserRequestProto request) throws ServiceException {
                return (GetGroupsForUserResponseProto)this.channel.callBlockingMethod(GetUserMappingsProtocolService.getDescriptor().getMethods().get(0), controller, request, GetGroupsForUserResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            GetGroupsForUserResponseProto getGroupsForUser(final RpcController p0, final GetGroupsForUserRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void getGroupsForUser(final RpcController p0, final GetGroupsForUserRequestProto p1, final RpcCallback<GetGroupsForUserResponseProto> p2);
        }
    }
    
    public interface GetGroupsForUserResponseProtoOrBuilder extends MessageOrBuilder
    {
        List<String> getGroupsList();
        
        int getGroupsCount();
        
        String getGroups(final int p0);
        
        ByteString getGroupsBytes(final int p0);
    }
    
    public interface GetGroupsForUserRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasUser();
        
        String getUser();
        
        ByteString getUserBytes();
    }
}
