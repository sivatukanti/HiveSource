// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.SingleFieldBuilder;
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

public final class YarnSecurityTestClientAMTokenProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnSecurityTestClientAMTokenProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnSecurityTestClientAMTokenProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0018test_client_tokens.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\"\u008a\u0001\n%ClientToAMTokenIdentifierForTestProto\u0012<\n\fappAttemptId\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012\u0012\n\nclientName\u0018\u0002 \u0001(\t\u0012\u000f\n\u0007message\u0018\u0003 \u0001(\t\"\u00d4\u0001\n'RMDelegationTokenIdentifierForTestProto\u0012\r\n\u0005owner\u0018\u0001 \u0001(\t\u0012\u000f\n\u0007renewer\u0018\u0002 \u0001(\t\u0012\u0010\n\brealUser\u0018\u0003 \u0001(\t\u0012\u0011\n\tissueDate\u0018\u0004 \u0001(\u0003\u0012\u000f\n\u0007maxDate\u0018\u0005 \u0001(\u0003\u0012\u0016\n\u000esequenceNumber\u0018\u0006 \u0001(\u0005\u0012\u0017\n\u000bmasterKeyId\u0018\u0007 \u0001(\u0005:\u0002-1\u0012\u0011\n\trenewDate\u0018\b \u0001(\u0003\u0012\u000f\n", "\u0007message\u0018\t \u0001(\tBI\n\u001corg.apache.hadoop.yarn.protoB#YarnSecurityTestClientAMTokenProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnSecurityTestClientAMTokenProtos.descriptor = root;
                YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor = YarnSecurityTestClientAMTokenProtos.getDescriptor().getMessageTypes().get(0);
                YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor, new String[] { "AppAttemptId", "ClientName", "Message" });
                YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor = YarnSecurityTestClientAMTokenProtos.getDescriptor().getMessageTypes().get(1);
                YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor, new String[] { "Owner", "Renewer", "RealUser", "IssueDate", "MaxDate", "SequenceNumber", "MasterKeyId", "RenewDate", "Message" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor() }, assigner);
    }
    
    public static final class ClientToAMTokenIdentifierForTestProto extends GeneratedMessage implements ClientToAMTokenIdentifierForTestProtoOrBuilder
    {
        private static final ClientToAMTokenIdentifierForTestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ClientToAMTokenIdentifierForTestProto> PARSER;
        private int bitField0_;
        public static final int APPATTEMPTID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
        public static final int CLIENTNAME_FIELD_NUMBER = 2;
        private Object clientName_;
        public static final int MESSAGE_FIELD_NUMBER = 3;
        private Object message_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ClientToAMTokenIdentifierForTestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ClientToAMTokenIdentifierForTestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ClientToAMTokenIdentifierForTestProto getDefaultInstance() {
            return ClientToAMTokenIdentifierForTestProto.defaultInstance;
        }
        
        @Override
        public ClientToAMTokenIdentifierForTestProto getDefaultInstanceForType() {
            return ClientToAMTokenIdentifierForTestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ClientToAMTokenIdentifierForTestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.ApplicationAttemptIdProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.appAttemptId_.toBuilder();
                            }
                            this.appAttemptId_ = input.readMessage(YarnProtos.ApplicationAttemptIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.appAttemptId_);
                                this.appAttemptId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.clientName_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.message_ = input.readBytes();
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
            return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientToAMTokenIdentifierForTestProto.class, Builder.class);
        }
        
        @Override
        public Parser<ClientToAMTokenIdentifierForTestProto> getParserForType() {
            return ClientToAMTokenIdentifierForTestProto.PARSER;
        }
        
        @Override
        public boolean hasAppAttemptId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProto getAppAttemptId() {
            return this.appAttemptId_;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder() {
            return this.appAttemptId_;
        }
        
        @Override
        public boolean hasClientName() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getClientName() {
            final Object ref = this.clientName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.clientName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getClientNameBytes() {
            final Object ref = this.clientName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.clientName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasMessage() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getMessage() {
            final Object ref = this.message_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.message_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getMessageBytes() {
            final Object ref = this.message_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.message_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.clientName_ = "";
            this.message_ = "";
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
                output.writeMessage(1, this.appAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getClientNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getMessageBytes());
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
                size += CodedOutputStream.computeMessageSize(1, this.appAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getClientNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getMessageBytes());
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
            if (!(obj instanceof ClientToAMTokenIdentifierForTestProto)) {
                return super.equals(obj);
            }
            final ClientToAMTokenIdentifierForTestProto other = (ClientToAMTokenIdentifierForTestProto)obj;
            boolean result = true;
            result = (result && this.hasAppAttemptId() == other.hasAppAttemptId());
            if (this.hasAppAttemptId()) {
                result = (result && this.getAppAttemptId().equals(other.getAppAttemptId()));
            }
            result = (result && this.hasClientName() == other.hasClientName());
            if (this.hasClientName()) {
                result = (result && this.getClientName().equals(other.getClientName()));
            }
            result = (result && this.hasMessage() == other.hasMessage());
            if (this.hasMessage()) {
                result = (result && this.getMessage().equals(other.getMessage()));
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
            if (this.hasAppAttemptId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getAppAttemptId().hashCode();
            }
            if (this.hasClientName()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getClientName().hashCode();
            }
            if (this.hasMessage()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getMessage().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(data);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(data);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final InputStream input) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(input);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final CodedInputStream input) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(input);
        }
        
        public static ClientToAMTokenIdentifierForTestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierForTestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ClientToAMTokenIdentifierForTestProto prototype) {
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
            ClientToAMTokenIdentifierForTestProto.PARSER = new AbstractParser<ClientToAMTokenIdentifierForTestProto>() {
                @Override
                public ClientToAMTokenIdentifierForTestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ClientToAMTokenIdentifierForTestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ClientToAMTokenIdentifierForTestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ClientToAMTokenIdentifierForTestProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> appAttemptIdBuilder_;
            private Object clientName_;
            private Object message_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientToAMTokenIdentifierForTestProto.class, Builder.class);
            }
            
            private Builder() {
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.clientName_ = "";
                this.message_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.clientName_ = "";
                this.message_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ClientToAMTokenIdentifierForTestProto.alwaysUseFieldBuilders) {
                    this.getAppAttemptIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.appAttemptIdBuilder_ == null) {
                    this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                }
                else {
                    this.appAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.clientName_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.message_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierForTestProto_descriptor;
            }
            
            @Override
            public ClientToAMTokenIdentifierForTestProto getDefaultInstanceForType() {
                return ClientToAMTokenIdentifierForTestProto.getDefaultInstance();
            }
            
            @Override
            public ClientToAMTokenIdentifierForTestProto build() {
                final ClientToAMTokenIdentifierForTestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ClientToAMTokenIdentifierForTestProto buildPartial() {
                final ClientToAMTokenIdentifierForTestProto result = new ClientToAMTokenIdentifierForTestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.appAttemptIdBuilder_ == null) {
                    result.appAttemptId_ = this.appAttemptId_;
                }
                else {
                    result.appAttemptId_ = this.appAttemptIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.clientName_ = this.clientName_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.message_ = this.message_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ClientToAMTokenIdentifierForTestProto) {
                    return this.mergeFrom((ClientToAMTokenIdentifierForTestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ClientToAMTokenIdentifierForTestProto other) {
                if (other == ClientToAMTokenIdentifierForTestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAppAttemptId()) {
                    this.mergeAppAttemptId(other.getAppAttemptId());
                }
                if (other.hasClientName()) {
                    this.bitField0_ |= 0x2;
                    this.clientName_ = other.clientName_;
                    this.onChanged();
                }
                if (other.hasMessage()) {
                    this.bitField0_ |= 0x4;
                    this.message_ = other.message_;
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
                ClientToAMTokenIdentifierForTestProto parsedMessage = null;
                try {
                    parsedMessage = ClientToAMTokenIdentifierForTestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ClientToAMTokenIdentifierForTestProto)e.getUnfinishedMessage();
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
            public boolean hasAppAttemptId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProto getAppAttemptId() {
                if (this.appAttemptIdBuilder_ == null) {
                    return this.appAttemptId_;
                }
                return this.appAttemptIdBuilder_.getMessage();
            }
            
            public Builder setAppAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.appAttemptIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.appAttemptId_ = value;
                    this.onChanged();
                }
                else {
                    this.appAttemptIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setAppAttemptId(final YarnProtos.ApplicationAttemptIdProto.Builder builderForValue) {
                if (this.appAttemptIdBuilder_ == null) {
                    this.appAttemptId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.appAttemptIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeAppAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.appAttemptIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.appAttemptId_ != YarnProtos.ApplicationAttemptIdProto.getDefaultInstance()) {
                        this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.newBuilder(this.appAttemptId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.appAttemptId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.appAttemptIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearAppAttemptId() {
                if (this.appAttemptIdBuilder_ == null) {
                    this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.appAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationAttemptIdProto.Builder getAppAttemptIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getAppAttemptIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder() {
                if (this.appAttemptIdBuilder_ != null) {
                    return this.appAttemptIdBuilder_.getMessageOrBuilder();
                }
                return this.appAttemptId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> getAppAttemptIdFieldBuilder() {
                if (this.appAttemptIdBuilder_ == null) {
                    this.appAttemptIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder>(this.appAttemptId_, this.getParentForChildren(), this.isClean());
                    this.appAttemptId_ = null;
                }
                return this.appAttemptIdBuilder_;
            }
            
            @Override
            public boolean hasClientName() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getClientName() {
                final Object ref = this.clientName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.clientName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getClientNameBytes() {
                final Object ref = this.clientName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.clientName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setClientName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.clientName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClientName() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.clientName_ = ClientToAMTokenIdentifierForTestProto.getDefaultInstance().getClientName();
                this.onChanged();
                return this;
            }
            
            public Builder setClientNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.clientName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMessage() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getMessage() {
                final Object ref = this.message_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.message_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getMessageBytes() {
                final Object ref = this.message_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.message_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setMessage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.message_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMessage() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.message_ = ClientToAMTokenIdentifierForTestProto.getDefaultInstance().getMessage();
                this.onChanged();
                return this;
            }
            
            public Builder setMessageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.message_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RMDelegationTokenIdentifierForTestProto extends GeneratedMessage implements RMDelegationTokenIdentifierForTestProtoOrBuilder
    {
        private static final RMDelegationTokenIdentifierForTestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RMDelegationTokenIdentifierForTestProto> PARSER;
        private int bitField0_;
        public static final int OWNER_FIELD_NUMBER = 1;
        private Object owner_;
        public static final int RENEWER_FIELD_NUMBER = 2;
        private Object renewer_;
        public static final int REALUSER_FIELD_NUMBER = 3;
        private Object realUser_;
        public static final int ISSUEDATE_FIELD_NUMBER = 4;
        private long issueDate_;
        public static final int MAXDATE_FIELD_NUMBER = 5;
        private long maxDate_;
        public static final int SEQUENCENUMBER_FIELD_NUMBER = 6;
        private int sequenceNumber_;
        public static final int MASTERKEYID_FIELD_NUMBER = 7;
        private int masterKeyId_;
        public static final int RENEWDATE_FIELD_NUMBER = 8;
        private long renewDate_;
        public static final int MESSAGE_FIELD_NUMBER = 9;
        private Object message_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RMDelegationTokenIdentifierForTestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RMDelegationTokenIdentifierForTestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RMDelegationTokenIdentifierForTestProto getDefaultInstance() {
            return RMDelegationTokenIdentifierForTestProto.defaultInstance;
        }
        
        @Override
        public RMDelegationTokenIdentifierForTestProto getDefaultInstanceForType() {
            return RMDelegationTokenIdentifierForTestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RMDelegationTokenIdentifierForTestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.owner_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.renewer_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.realUser_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x8;
                            this.issueDate_ = input.readInt64();
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x10;
                            this.maxDate_ = input.readInt64();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.sequenceNumber_ = input.readInt32();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.masterKeyId_ = input.readInt32();
                            continue;
                        }
                        case 64: {
                            this.bitField0_ |= 0x80;
                            this.renewDate_ = input.readInt64();
                            continue;
                        }
                        case 74: {
                            this.bitField0_ |= 0x100;
                            this.message_ = input.readBytes();
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
            return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RMDelegationTokenIdentifierForTestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RMDelegationTokenIdentifierForTestProto> getParserForType() {
            return RMDelegationTokenIdentifierForTestProto.PARSER;
        }
        
        @Override
        public boolean hasOwner() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getOwner() {
            final Object ref = this.owner_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.owner_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getOwnerBytes() {
            final Object ref = this.owner_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.owner_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRenewer() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getRenewer() {
            final Object ref = this.renewer_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.renewer_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRenewerBytes() {
            final Object ref = this.renewer_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.renewer_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRealUser() {
            return (this.bitField0_ & 0x4) == 0x4;
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
        
        @Override
        public boolean hasIssueDate() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public long getIssueDate() {
            return this.issueDate_;
        }
        
        @Override
        public boolean hasMaxDate() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public long getMaxDate() {
            return this.maxDate_;
        }
        
        @Override
        public boolean hasSequenceNumber() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public int getSequenceNumber() {
            return this.sequenceNumber_;
        }
        
        @Override
        public boolean hasMasterKeyId() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public int getMasterKeyId() {
            return this.masterKeyId_;
        }
        
        @Override
        public boolean hasRenewDate() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public long getRenewDate() {
            return this.renewDate_;
        }
        
        @Override
        public boolean hasMessage() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public String getMessage() {
            final Object ref = this.message_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.message_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getMessageBytes() {
            final Object ref = this.message_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.message_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.owner_ = "";
            this.renewer_ = "";
            this.realUser_ = "";
            this.issueDate_ = 0L;
            this.maxDate_ = 0L;
            this.sequenceNumber_ = 0;
            this.masterKeyId_ = -1;
            this.renewDate_ = 0L;
            this.message_ = "";
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
                output.writeBytes(1, this.getOwnerBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getRenewerBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getRealUserBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeInt64(4, this.issueDate_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeInt64(5, this.maxDate_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt32(6, this.sequenceNumber_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt32(7, this.masterKeyId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeInt64(8, this.renewDate_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeBytes(9, this.getMessageBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getOwnerBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getRenewerBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getRealUserBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeInt64Size(4, this.issueDate_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeInt64Size(5, this.maxDate_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt32Size(6, this.sequenceNumber_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt32Size(7, this.masterKeyId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeInt64Size(8, this.renewDate_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeBytesSize(9, this.getMessageBytes());
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
            if (!(obj instanceof RMDelegationTokenIdentifierForTestProto)) {
                return super.equals(obj);
            }
            final RMDelegationTokenIdentifierForTestProto other = (RMDelegationTokenIdentifierForTestProto)obj;
            boolean result = true;
            result = (result && this.hasOwner() == other.hasOwner());
            if (this.hasOwner()) {
                result = (result && this.getOwner().equals(other.getOwner()));
            }
            result = (result && this.hasRenewer() == other.hasRenewer());
            if (this.hasRenewer()) {
                result = (result && this.getRenewer().equals(other.getRenewer()));
            }
            result = (result && this.hasRealUser() == other.hasRealUser());
            if (this.hasRealUser()) {
                result = (result && this.getRealUser().equals(other.getRealUser()));
            }
            result = (result && this.hasIssueDate() == other.hasIssueDate());
            if (this.hasIssueDate()) {
                result = (result && this.getIssueDate() == other.getIssueDate());
            }
            result = (result && this.hasMaxDate() == other.hasMaxDate());
            if (this.hasMaxDate()) {
                result = (result && this.getMaxDate() == other.getMaxDate());
            }
            result = (result && this.hasSequenceNumber() == other.hasSequenceNumber());
            if (this.hasSequenceNumber()) {
                result = (result && this.getSequenceNumber() == other.getSequenceNumber());
            }
            result = (result && this.hasMasterKeyId() == other.hasMasterKeyId());
            if (this.hasMasterKeyId()) {
                result = (result && this.getMasterKeyId() == other.getMasterKeyId());
            }
            result = (result && this.hasRenewDate() == other.hasRenewDate());
            if (this.hasRenewDate()) {
                result = (result && this.getRenewDate() == other.getRenewDate());
            }
            result = (result && this.hasMessage() == other.hasMessage());
            if (this.hasMessage()) {
                result = (result && this.getMessage().equals(other.getMessage()));
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
            if (this.hasOwner()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getOwner().hashCode();
            }
            if (this.hasRenewer()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getRenewer().hashCode();
            }
            if (this.hasRealUser()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getRealUser().hashCode();
            }
            if (this.hasIssueDate()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashLong(this.getIssueDate());
            }
            if (this.hasMaxDate()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashLong(this.getMaxDate());
            }
            if (this.hasSequenceNumber()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getSequenceNumber();
            }
            if (this.hasMasterKeyId()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getMasterKeyId();
            }
            if (this.hasRenewDate()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + AbstractMessage.hashLong(this.getRenewDate());
            }
            if (this.hasMessage()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + this.getMessage().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(data);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(data);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final InputStream input) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(input);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final CodedInputStream input) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(input);
        }
        
        public static RMDelegationTokenIdentifierForTestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierForTestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RMDelegationTokenIdentifierForTestProto prototype) {
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
            RMDelegationTokenIdentifierForTestProto.PARSER = new AbstractParser<RMDelegationTokenIdentifierForTestProto>() {
                @Override
                public RMDelegationTokenIdentifierForTestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RMDelegationTokenIdentifierForTestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RMDelegationTokenIdentifierForTestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RMDelegationTokenIdentifierForTestProtoOrBuilder
        {
            private int bitField0_;
            private Object owner_;
            private Object renewer_;
            private Object realUser_;
            private long issueDate_;
            private long maxDate_;
            private int sequenceNumber_;
            private int masterKeyId_;
            private long renewDate_;
            private Object message_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RMDelegationTokenIdentifierForTestProto.class, Builder.class);
            }
            
            private Builder() {
                this.owner_ = "";
                this.renewer_ = "";
                this.realUser_ = "";
                this.masterKeyId_ = -1;
                this.message_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.owner_ = "";
                this.renewer_ = "";
                this.realUser_ = "";
                this.masterKeyId_ = -1;
                this.message_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RMDelegationTokenIdentifierForTestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.owner_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.renewer_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.realUser_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.issueDate_ = 0L;
                this.bitField0_ &= 0xFFFFFFF7;
                this.maxDate_ = 0L;
                this.bitField0_ &= 0xFFFFFFEF;
                this.sequenceNumber_ = 0;
                this.bitField0_ &= 0xFFFFFFDF;
                this.masterKeyId_ = -1;
                this.bitField0_ &= 0xFFFFFFBF;
                this.renewDate_ = 0L;
                this.bitField0_ &= 0xFFFFFF7F;
                this.message_ = "";
                this.bitField0_ &= 0xFFFFFEFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTestClientAMTokenProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierForTestProto_descriptor;
            }
            
            @Override
            public RMDelegationTokenIdentifierForTestProto getDefaultInstanceForType() {
                return RMDelegationTokenIdentifierForTestProto.getDefaultInstance();
            }
            
            @Override
            public RMDelegationTokenIdentifierForTestProto build() {
                final RMDelegationTokenIdentifierForTestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RMDelegationTokenIdentifierForTestProto buildPartial() {
                final RMDelegationTokenIdentifierForTestProto result = new RMDelegationTokenIdentifierForTestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.owner_ = this.owner_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.renewer_ = this.renewer_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.realUser_ = this.realUser_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.issueDate_ = this.issueDate_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.maxDate_ = this.maxDate_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.sequenceNumber_ = this.sequenceNumber_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.masterKeyId_ = this.masterKeyId_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.renewDate_ = this.renewDate_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.message_ = this.message_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RMDelegationTokenIdentifierForTestProto) {
                    return this.mergeFrom((RMDelegationTokenIdentifierForTestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RMDelegationTokenIdentifierForTestProto other) {
                if (other == RMDelegationTokenIdentifierForTestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasOwner()) {
                    this.bitField0_ |= 0x1;
                    this.owner_ = other.owner_;
                    this.onChanged();
                }
                if (other.hasRenewer()) {
                    this.bitField0_ |= 0x2;
                    this.renewer_ = other.renewer_;
                    this.onChanged();
                }
                if (other.hasRealUser()) {
                    this.bitField0_ |= 0x4;
                    this.realUser_ = other.realUser_;
                    this.onChanged();
                }
                if (other.hasIssueDate()) {
                    this.setIssueDate(other.getIssueDate());
                }
                if (other.hasMaxDate()) {
                    this.setMaxDate(other.getMaxDate());
                }
                if (other.hasSequenceNumber()) {
                    this.setSequenceNumber(other.getSequenceNumber());
                }
                if (other.hasMasterKeyId()) {
                    this.setMasterKeyId(other.getMasterKeyId());
                }
                if (other.hasRenewDate()) {
                    this.setRenewDate(other.getRenewDate());
                }
                if (other.hasMessage()) {
                    this.bitField0_ |= 0x100;
                    this.message_ = other.message_;
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
                RMDelegationTokenIdentifierForTestProto parsedMessage = null;
                try {
                    parsedMessage = RMDelegationTokenIdentifierForTestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RMDelegationTokenIdentifierForTestProto)e.getUnfinishedMessage();
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
            public boolean hasOwner() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getOwner() {
                final Object ref = this.owner_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.owner_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getOwnerBytes() {
                final Object ref = this.owner_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.owner_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setOwner(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.owner_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearOwner() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.owner_ = RMDelegationTokenIdentifierForTestProto.getDefaultInstance().getOwner();
                this.onChanged();
                return this;
            }
            
            public Builder setOwnerBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.owner_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRenewer() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getRenewer() {
                final Object ref = this.renewer_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.renewer_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRenewerBytes() {
                final Object ref = this.renewer_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.renewer_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRenewer(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.renewer_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRenewer() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.renewer_ = RMDelegationTokenIdentifierForTestProto.getDefaultInstance().getRenewer();
                this.onChanged();
                return this;
            }
            
            public Builder setRenewerBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.renewer_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRealUser() {
                return (this.bitField0_ & 0x4) == 0x4;
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
                this.bitField0_ |= 0x4;
                this.realUser_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRealUser() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.realUser_ = RMDelegationTokenIdentifierForTestProto.getDefaultInstance().getRealUser();
                this.onChanged();
                return this;
            }
            
            public Builder setRealUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.realUser_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasIssueDate() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public long getIssueDate() {
                return this.issueDate_;
            }
            
            public Builder setIssueDate(final long value) {
                this.bitField0_ |= 0x8;
                this.issueDate_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearIssueDate() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.issueDate_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMaxDate() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public long getMaxDate() {
                return this.maxDate_;
            }
            
            public Builder setMaxDate(final long value) {
                this.bitField0_ |= 0x10;
                this.maxDate_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMaxDate() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.maxDate_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSequenceNumber() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public int getSequenceNumber() {
                return this.sequenceNumber_;
            }
            
            public Builder setSequenceNumber(final int value) {
                this.bitField0_ |= 0x20;
                this.sequenceNumber_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSequenceNumber() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.sequenceNumber_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMasterKeyId() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public int getMasterKeyId() {
                return this.masterKeyId_;
            }
            
            public Builder setMasterKeyId(final int value) {
                this.bitField0_ |= 0x40;
                this.masterKeyId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMasterKeyId() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.masterKeyId_ = -1;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRenewDate() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public long getRenewDate() {
                return this.renewDate_;
            }
            
            public Builder setRenewDate(final long value) {
                this.bitField0_ |= 0x80;
                this.renewDate_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRenewDate() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.renewDate_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMessage() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public String getMessage() {
                final Object ref = this.message_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.message_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getMessageBytes() {
                final Object ref = this.message_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.message_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setMessage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.message_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMessage() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.message_ = RMDelegationTokenIdentifierForTestProto.getDefaultInstance().getMessage();
                this.onChanged();
                return this;
            }
            
            public Builder setMessageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.message_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface RMDelegationTokenIdentifierForTestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasOwner();
        
        String getOwner();
        
        ByteString getOwnerBytes();
        
        boolean hasRenewer();
        
        String getRenewer();
        
        ByteString getRenewerBytes();
        
        boolean hasRealUser();
        
        String getRealUser();
        
        ByteString getRealUserBytes();
        
        boolean hasIssueDate();
        
        long getIssueDate();
        
        boolean hasMaxDate();
        
        long getMaxDate();
        
        boolean hasSequenceNumber();
        
        int getSequenceNumber();
        
        boolean hasMasterKeyId();
        
        int getMasterKeyId();
        
        boolean hasRenewDate();
        
        long getRenewDate();
        
        boolean hasMessage();
        
        String getMessage();
        
        ByteString getMessageBytes();
    }
    
    public interface ClientToAMTokenIdentifierForTestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder();
        
        boolean hasClientName();
        
        String getClientName();
        
        ByteString getClientNameBytes();
        
        boolean hasMessage();
        
        String getMessage();
        
        ByteString getMessageBytes();
    }
}
