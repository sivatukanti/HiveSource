// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protobuf;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.SingleFieldBuilder;
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

public final class IpcConnectionContextProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_UserInformationProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_UserInformationProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_IpcConnectionContextProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_IpcConnectionContextProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private IpcConnectionContextProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return IpcConnectionContextProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u001aIpcConnectionContext.proto\u0012\rhadoop.common\"?\n\u0014UserInformationProto\u0012\u0015\n\reffectiveUser\u0018\u0001 \u0001(\t\u0012\u0010\n\brealUser\u0018\u0002 \u0001(\t\"d\n\u0019IpcConnectionContextProto\u00125\n\buserInfo\u0018\u0002 \u0001(\u000b2#.hadoop.common.UserInformationProto\u0012\u0010\n\bprotocol\u0018\u0003 \u0001(\tB?\n\u001eorg.apache.hadoop.ipc.protobufB\u001aIpcConnectionContextProtos \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                IpcConnectionContextProtos.descriptor = root;
                IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_descriptor = IpcConnectionContextProtos.getDescriptor().getMessageTypes().get(0);
                IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_descriptor, new String[] { "EffectiveUser", "RealUser" });
                IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_descriptor = IpcConnectionContextProtos.getDescriptor().getMessageTypes().get(1);
                IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_descriptor, new String[] { "UserInfo", "Protocol" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class UserInformationProto extends GeneratedMessage implements UserInformationProtoOrBuilder
    {
        private static final UserInformationProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<UserInformationProto> PARSER;
        private int bitField0_;
        public static final int EFFECTIVEUSER_FIELD_NUMBER = 1;
        private Object effectiveUser_;
        public static final int REALUSER_FIELD_NUMBER = 2;
        private Object realUser_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private UserInformationProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private UserInformationProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static UserInformationProto getDefaultInstance() {
            return UserInformationProto.defaultInstance;
        }
        
        @Override
        public UserInformationProto getDefaultInstanceForType() {
            return UserInformationProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private UserInformationProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.effectiveUser_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.realUser_ = input.readBytes();
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
            return IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UserInformationProto.class, Builder.class);
        }
        
        @Override
        public Parser<UserInformationProto> getParserForType() {
            return UserInformationProto.PARSER;
        }
        
        @Override
        public boolean hasEffectiveUser() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getEffectiveUser() {
            final Object ref = this.effectiveUser_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.effectiveUser_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getEffectiveUserBytes() {
            final Object ref = this.effectiveUser_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.effectiveUser_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRealUser() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getRealUser() {
            final Object ref = this.realUser_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.realUser_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRealUserBytes() {
            final Object ref = this.realUser_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.realUser_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.effectiveUser_ = "";
            this.realUser_ = "";
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
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getEffectiveUserBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getRealUserBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getEffectiveUserBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getRealUserBytes());
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
            if (!(obj instanceof UserInformationProto)) {
                return super.equals(obj);
            }
            final UserInformationProto other = (UserInformationProto)obj;
            boolean result = true;
            result = (result && this.hasEffectiveUser() == other.hasEffectiveUser());
            if (this.hasEffectiveUser()) {
                result = (result && this.getEffectiveUser().equals(other.getEffectiveUser()));
            }
            result = (result && this.hasRealUser() == other.hasRealUser());
            if (this.hasRealUser()) {
                result = (result && this.getRealUser().equals(other.getRealUser()));
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
            if (this.hasEffectiveUser()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getEffectiveUser().hashCode();
            }
            if (this.hasRealUser()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getRealUser().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static UserInformationProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return UserInformationProto.PARSER.parseFrom(data);
        }
        
        public static UserInformationProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UserInformationProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UserInformationProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return UserInformationProto.PARSER.parseFrom(data);
        }
        
        public static UserInformationProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UserInformationProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UserInformationProto parseFrom(final InputStream input) throws IOException {
            return UserInformationProto.PARSER.parseFrom(input);
        }
        
        public static UserInformationProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UserInformationProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static UserInformationProto parseDelimitedFrom(final InputStream input) throws IOException {
            return UserInformationProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static UserInformationProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UserInformationProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static UserInformationProto parseFrom(final CodedInputStream input) throws IOException {
            return UserInformationProto.PARSER.parseFrom(input);
        }
        
        public static UserInformationProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UserInformationProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final UserInformationProto prototype) {
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
            UserInformationProto.PARSER = new AbstractParser<UserInformationProto>() {
                @Override
                public UserInformationProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new UserInformationProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new UserInformationProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements UserInformationProtoOrBuilder
        {
            private int bitField0_;
            private Object effectiveUser_;
            private Object realUser_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UserInformationProto.class, Builder.class);
            }
            
            private Builder() {
                this.effectiveUser_ = "";
                this.realUser_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.effectiveUser_ = "";
                this.realUser_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (UserInformationProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.effectiveUser_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.realUser_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_UserInformationProto_descriptor;
            }
            
            @Override
            public UserInformationProto getDefaultInstanceForType() {
                return UserInformationProto.getDefaultInstance();
            }
            
            @Override
            public UserInformationProto build() {
                final UserInformationProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public UserInformationProto buildPartial() {
                final UserInformationProto result = new UserInformationProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.effectiveUser_ = this.effectiveUser_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.realUser_ = this.realUser_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof UserInformationProto) {
                    return this.mergeFrom((UserInformationProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final UserInformationProto other) {
                if (other == UserInformationProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasEffectiveUser()) {
                    this.bitField0_ |= 0x1;
                    this.effectiveUser_ = other.effectiveUser_;
                    this.onChanged();
                }
                if (other.hasRealUser()) {
                    this.bitField0_ |= 0x2;
                    this.realUser_ = other.realUser_;
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
                UserInformationProto parsedMessage = null;
                try {
                    parsedMessage = UserInformationProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (UserInformationProto)e.getUnfinishedMessage();
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
            public boolean hasEffectiveUser() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getEffectiveUser() {
                final Object ref = this.effectiveUser_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.effectiveUser_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getEffectiveUserBytes() {
                final Object ref = this.effectiveUser_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.effectiveUser_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setEffectiveUser(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.effectiveUser_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearEffectiveUser() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.effectiveUser_ = UserInformationProto.getDefaultInstance().getEffectiveUser();
                this.onChanged();
                return this;
            }
            
            public Builder setEffectiveUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.effectiveUser_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRealUser() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getRealUser() {
                final Object ref = this.realUser_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.realUser_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRealUserBytes() {
                final Object ref = this.realUser_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.realUser_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRealUser(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.realUser_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRealUser() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.realUser_ = UserInformationProto.getDefaultInstance().getRealUser();
                this.onChanged();
                return this;
            }
            
            public Builder setRealUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.realUser_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class IpcConnectionContextProto extends GeneratedMessage implements IpcConnectionContextProtoOrBuilder
    {
        private static final IpcConnectionContextProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<IpcConnectionContextProto> PARSER;
        private int bitField0_;
        public static final int USERINFO_FIELD_NUMBER = 2;
        private UserInformationProto userInfo_;
        public static final int PROTOCOL_FIELD_NUMBER = 3;
        private Object protocol_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private IpcConnectionContextProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private IpcConnectionContextProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static IpcConnectionContextProto getDefaultInstance() {
            return IpcConnectionContextProto.defaultInstance;
        }
        
        @Override
        public IpcConnectionContextProto getDefaultInstanceForType() {
            return IpcConnectionContextProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private IpcConnectionContextProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 18: {
                            UserInformationProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.userInfo_.toBuilder();
                            }
                            this.userInfo_ = input.readMessage(UserInformationProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.userInfo_);
                                this.userInfo_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x2;
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
            return IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_fieldAccessorTable.ensureFieldAccessorsInitialized(IpcConnectionContextProto.class, Builder.class);
        }
        
        @Override
        public Parser<IpcConnectionContextProto> getParserForType() {
            return IpcConnectionContextProto.PARSER;
        }
        
        @Override
        public boolean hasUserInfo() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public UserInformationProto getUserInfo() {
            return this.userInfo_;
        }
        
        @Override
        public UserInformationProtoOrBuilder getUserInfoOrBuilder() {
            return this.userInfo_;
        }
        
        @Override
        public boolean hasProtocol() {
            return (this.bitField0_ & 0x2) == 0x2;
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
            this.userInfo_ = UserInformationProto.getDefaultInstance();
            this.protocol_ = "";
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
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeMessage(2, this.userInfo_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(3, this.getProtocolBytes());
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
                size += CodedOutputStream.computeMessageSize(2, this.userInfo_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(3, this.getProtocolBytes());
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
            if (!(obj instanceof IpcConnectionContextProto)) {
                return super.equals(obj);
            }
            final IpcConnectionContextProto other = (IpcConnectionContextProto)obj;
            boolean result = true;
            result = (result && this.hasUserInfo() == other.hasUserInfo());
            if (this.hasUserInfo()) {
                result = (result && this.getUserInfo().equals(other.getUserInfo()));
            }
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
            if (this.hasUserInfo()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getUserInfo().hashCode();
            }
            if (this.hasProtocol()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getProtocol().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static IpcConnectionContextProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return IpcConnectionContextProto.PARSER.parseFrom(data);
        }
        
        public static IpcConnectionContextProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return IpcConnectionContextProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static IpcConnectionContextProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return IpcConnectionContextProto.PARSER.parseFrom(data);
        }
        
        public static IpcConnectionContextProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return IpcConnectionContextProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static IpcConnectionContextProto parseFrom(final InputStream input) throws IOException {
            return IpcConnectionContextProto.PARSER.parseFrom(input);
        }
        
        public static IpcConnectionContextProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return IpcConnectionContextProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static IpcConnectionContextProto parseDelimitedFrom(final InputStream input) throws IOException {
            return IpcConnectionContextProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static IpcConnectionContextProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return IpcConnectionContextProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static IpcConnectionContextProto parseFrom(final CodedInputStream input) throws IOException {
            return IpcConnectionContextProto.PARSER.parseFrom(input);
        }
        
        public static IpcConnectionContextProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return IpcConnectionContextProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final IpcConnectionContextProto prototype) {
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
            IpcConnectionContextProto.PARSER = new AbstractParser<IpcConnectionContextProto>() {
                @Override
                public IpcConnectionContextProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new IpcConnectionContextProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new IpcConnectionContextProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements IpcConnectionContextProtoOrBuilder
        {
            private int bitField0_;
            private UserInformationProto userInfo_;
            private SingleFieldBuilder<UserInformationProto, UserInformationProto.Builder, UserInformationProtoOrBuilder> userInfoBuilder_;
            private Object protocol_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_fieldAccessorTable.ensureFieldAccessorsInitialized(IpcConnectionContextProto.class, Builder.class);
            }
            
            private Builder() {
                this.userInfo_ = UserInformationProto.getDefaultInstance();
                this.protocol_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.userInfo_ = UserInformationProto.getDefaultInstance();
                this.protocol_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (IpcConnectionContextProto.alwaysUseFieldBuilders) {
                    this.getUserInfoFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.userInfoBuilder_ == null) {
                    this.userInfo_ = UserInformationProto.getDefaultInstance();
                }
                else {
                    this.userInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.protocol_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return IpcConnectionContextProtos.internal_static_hadoop_common_IpcConnectionContextProto_descriptor;
            }
            
            @Override
            public IpcConnectionContextProto getDefaultInstanceForType() {
                return IpcConnectionContextProto.getDefaultInstance();
            }
            
            @Override
            public IpcConnectionContextProto build() {
                final IpcConnectionContextProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public IpcConnectionContextProto buildPartial() {
                final IpcConnectionContextProto result = new IpcConnectionContextProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.userInfoBuilder_ == null) {
                    result.userInfo_ = this.userInfo_;
                }
                else {
                    result.userInfo_ = this.userInfoBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.protocol_ = this.protocol_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof IpcConnectionContextProto) {
                    return this.mergeFrom((IpcConnectionContextProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final IpcConnectionContextProto other) {
                if (other == IpcConnectionContextProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasUserInfo()) {
                    this.mergeUserInfo(other.getUserInfo());
                }
                if (other.hasProtocol()) {
                    this.bitField0_ |= 0x2;
                    this.protocol_ = other.protocol_;
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
                IpcConnectionContextProto parsedMessage = null;
                try {
                    parsedMessage = IpcConnectionContextProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (IpcConnectionContextProto)e.getUnfinishedMessage();
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
            public boolean hasUserInfo() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public UserInformationProto getUserInfo() {
                if (this.userInfoBuilder_ == null) {
                    return this.userInfo_;
                }
                return this.userInfoBuilder_.getMessage();
            }
            
            public Builder setUserInfo(final UserInformationProto value) {
                if (this.userInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.userInfo_ = value;
                    this.onChanged();
                }
                else {
                    this.userInfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setUserInfo(final UserInformationProto.Builder builderForValue) {
                if (this.userInfoBuilder_ == null) {
                    this.userInfo_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.userInfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeUserInfo(final UserInformationProto value) {
                if (this.userInfoBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.userInfo_ != UserInformationProto.getDefaultInstance()) {
                        this.userInfo_ = UserInformationProto.newBuilder(this.userInfo_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.userInfo_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.userInfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearUserInfo() {
                if (this.userInfoBuilder_ == null) {
                    this.userInfo_ = UserInformationProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.userInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public UserInformationProto.Builder getUserInfoBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getUserInfoFieldBuilder().getBuilder();
            }
            
            @Override
            public UserInformationProtoOrBuilder getUserInfoOrBuilder() {
                if (this.userInfoBuilder_ != null) {
                    return this.userInfoBuilder_.getMessageOrBuilder();
                }
                return this.userInfo_;
            }
            
            private SingleFieldBuilder<UserInformationProto, UserInformationProto.Builder, UserInformationProtoOrBuilder> getUserInfoFieldBuilder() {
                if (this.userInfoBuilder_ == null) {
                    this.userInfoBuilder_ = new SingleFieldBuilder<UserInformationProto, UserInformationProto.Builder, UserInformationProtoOrBuilder>(this.userInfo_, this.getParentForChildren(), this.isClean());
                    this.userInfo_ = null;
                }
                return this.userInfoBuilder_;
            }
            
            @Override
            public boolean hasProtocol() {
                return (this.bitField0_ & 0x2) == 0x2;
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
                this.bitField0_ |= 0x2;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearProtocol() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.protocol_ = IpcConnectionContextProto.getDefaultInstance().getProtocol();
                this.onChanged();
                return this;
            }
            
            public Builder setProtocolBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.protocol_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface UserInformationProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasEffectiveUser();
        
        String getEffectiveUser();
        
        ByteString getEffectiveUserBytes();
        
        boolean hasRealUser();
        
        String getRealUser();
        
        ByteString getRealUserBytes();
    }
    
    public interface IpcConnectionContextProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasUserInfo();
        
        UserInformationProto getUserInfo();
        
        UserInformationProtoOrBuilder getUserInfoOrBuilder();
        
        boolean hasProtocol();
        
        String getProtocol();
        
        ByteString getProtocolBytes();
    }
}
