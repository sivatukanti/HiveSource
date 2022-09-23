// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.proto;

import com.google.protobuf.MessageOrBuilder;
import java.util.Collection;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ByteString;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class SecurityProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_TokenProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_TokenProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_CredentialsKVProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CredentialsKVProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_CredentialsProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CredentialsProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetDelegationTokenRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetDelegationTokenResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RenewDelegationTokenRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RenewDelegationTokenResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CancelDelegationTokenRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_CancelDelegationTokenResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private SecurityProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return SecurityProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u000eSecurity.proto\u0012\rhadoop.common\"Q\n\nTokenProto\u0012\u0012\n\nidentifier\u0018\u0001 \u0002(\f\u0012\u0010\n\bpassword\u0018\u0002 \u0002(\f\u0012\f\n\u0004kind\u0018\u0003 \u0002(\t\u0012\u000f\n\u0007service\u0018\u0004 \u0002(\t\"]\n\u0012CredentialsKVProto\u0012\r\n\u0005alias\u0018\u0001 \u0002(\t\u0012(\n\u0005token\u0018\u0002 \u0001(\u000b2\u0019.hadoop.common.TokenProto\u0012\u000e\n\u0006secret\u0018\u0003 \u0001(\f\"y\n\u0010CredentialsProto\u00121\n\u0006tokens\u0018\u0001 \u0003(\u000b2!.hadoop.common.CredentialsKVProto\u00122\n\u0007secrets\u0018\u0002 \u0003(\u000b2!.hadoop.common.CredentialsKVProto\"1\n\u001eGetDelegationTokenRequestProto\u0012\u000f\n\u0007renewer\u0018\u0001 \u0002(\t\"K\n\u001fGetDelegation", "TokenResponseProto\u0012(\n\u0005token\u0018\u0001 \u0001(\u000b2\u0019.hadoop.common.TokenProto\"L\n RenewDelegationTokenRequestProto\u0012(\n\u0005token\u0018\u0001 \u0002(\u000b2\u0019.hadoop.common.TokenProto\":\n!RenewDelegationTokenResponseProto\u0012\u0015\n\rnewExpiryTime\u0018\u0001 \u0002(\u0004\"M\n!CancelDelegationTokenRequestProto\u0012(\n\u0005token\u0018\u0001 \u0002(\u000b2\u0019.hadoop.common.TokenProto\"$\n\"CancelDelegationTokenResponseProtoB8\n org.apache.hadoop.security.protoB\u000eSecurityProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                SecurityProtos.descriptor = root;
                SecurityProtos.internal_static_hadoop_common_TokenProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(0);
                SecurityProtos.internal_static_hadoop_common_TokenProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_TokenProto_descriptor, new String[] { "Identifier", "Password", "Kind", "Service" });
                SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(1);
                SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_descriptor, new String[] { "Alias", "Token", "Secret" });
                SecurityProtos.internal_static_hadoop_common_CredentialsProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(2);
                SecurityProtos.internal_static_hadoop_common_CredentialsProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_CredentialsProto_descriptor, new String[] { "Tokens", "Secrets" });
                SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(3);
                SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor, new String[] { "Renewer" });
                SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(4);
                SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor, new String[] { "Token" });
                SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(5);
                SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor, new String[] { "Token" });
                SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(6);
                SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor, new String[] { "NewExpiryTime" });
                SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(7);
                SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor, new String[] { "Token" });
                SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor = SecurityProtos.getDescriptor().getMessageTypes().get(8);
                SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class TokenProto extends GeneratedMessage implements TokenProtoOrBuilder
    {
        private static final TokenProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<TokenProto> PARSER;
        private int bitField0_;
        public static final int IDENTIFIER_FIELD_NUMBER = 1;
        private ByteString identifier_;
        public static final int PASSWORD_FIELD_NUMBER = 2;
        private ByteString password_;
        public static final int KIND_FIELD_NUMBER = 3;
        private Object kind_;
        public static final int SERVICE_FIELD_NUMBER = 4;
        private Object service_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private TokenProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private TokenProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static TokenProto getDefaultInstance() {
            return TokenProto.defaultInstance;
        }
        
        @Override
        public TokenProto getDefaultInstanceForType() {
            return TokenProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private TokenProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.identifier_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.password_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.kind_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.service_ = input.readBytes();
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
            return SecurityProtos.internal_static_hadoop_common_TokenProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_TokenProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TokenProto.class, Builder.class);
        }
        
        @Override
        public Parser<TokenProto> getParserForType() {
            return TokenProto.PARSER;
        }
        
        @Override
        public boolean hasIdentifier() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public ByteString getIdentifier() {
            return this.identifier_;
        }
        
        @Override
        public boolean hasPassword() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public ByteString getPassword() {
            return this.password_;
        }
        
        @Override
        public boolean hasKind() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getKind() {
            final Object ref = this.kind_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.kind_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getKindBytes() {
            final Object ref = this.kind_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.kind_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasService() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public String getService() {
            final Object ref = this.service_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.service_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getServiceBytes() {
            final Object ref = this.service_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.service_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.identifier_ = ByteString.EMPTY;
            this.password_ = ByteString.EMPTY;
            this.kind_ = "";
            this.service_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasIdentifier()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasPassword()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasKind()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasService()) {
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
                output.writeBytes(1, this.identifier_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.password_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getKindBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.getServiceBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.identifier_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.password_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getKindBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.getServiceBytes());
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
            if (!(obj instanceof TokenProto)) {
                return super.equals(obj);
            }
            final TokenProto other = (TokenProto)obj;
            boolean result = true;
            result = (result && this.hasIdentifier() == other.hasIdentifier());
            if (this.hasIdentifier()) {
                result = (result && this.getIdentifier().equals(other.getIdentifier()));
            }
            result = (result && this.hasPassword() == other.hasPassword());
            if (this.hasPassword()) {
                result = (result && this.getPassword().equals(other.getPassword()));
            }
            result = (result && this.hasKind() == other.hasKind());
            if (this.hasKind()) {
                result = (result && this.getKind().equals(other.getKind()));
            }
            result = (result && this.hasService() == other.hasService());
            if (this.hasService()) {
                result = (result && this.getService().equals(other.getService()));
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
            if (this.hasIdentifier()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getIdentifier().hashCode();
            }
            if (this.hasPassword()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getPassword().hashCode();
            }
            if (this.hasKind()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getKind().hashCode();
            }
            if (this.hasService()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getService().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static TokenProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return TokenProto.PARSER.parseFrom(data);
        }
        
        public static TokenProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TokenProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TokenProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return TokenProto.PARSER.parseFrom(data);
        }
        
        public static TokenProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TokenProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TokenProto parseFrom(final InputStream input) throws IOException {
            return TokenProto.PARSER.parseFrom(input);
        }
        
        public static TokenProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TokenProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static TokenProto parseDelimitedFrom(final InputStream input) throws IOException {
            return TokenProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static TokenProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TokenProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static TokenProto parseFrom(final CodedInputStream input) throws IOException {
            return TokenProto.PARSER.parseFrom(input);
        }
        
        public static TokenProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TokenProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final TokenProto prototype) {
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
            TokenProto.PARSER = new AbstractParser<TokenProto>() {
                @Override
                public TokenProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new TokenProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new TokenProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements TokenProtoOrBuilder
        {
            private int bitField0_;
            private ByteString identifier_;
            private ByteString password_;
            private Object kind_;
            private Object service_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_TokenProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_TokenProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TokenProto.class, Builder.class);
            }
            
            private Builder() {
                this.identifier_ = ByteString.EMPTY;
                this.password_ = ByteString.EMPTY;
                this.kind_ = "";
                this.service_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.identifier_ = ByteString.EMPTY;
                this.password_ = ByteString.EMPTY;
                this.kind_ = "";
                this.service_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (TokenProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.identifier_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.password_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                this.kind_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.service_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_TokenProto_descriptor;
            }
            
            @Override
            public TokenProto getDefaultInstanceForType() {
                return TokenProto.getDefaultInstance();
            }
            
            @Override
            public TokenProto build() {
                final TokenProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public TokenProto buildPartial() {
                final TokenProto result = new TokenProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.identifier_ = this.identifier_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.password_ = this.password_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.kind_ = this.kind_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.service_ = this.service_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof TokenProto) {
                    return this.mergeFrom((TokenProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final TokenProto other) {
                if (other == TokenProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasIdentifier()) {
                    this.setIdentifier(other.getIdentifier());
                }
                if (other.hasPassword()) {
                    this.setPassword(other.getPassword());
                }
                if (other.hasKind()) {
                    this.bitField0_ |= 0x4;
                    this.kind_ = other.kind_;
                    this.onChanged();
                }
                if (other.hasService()) {
                    this.bitField0_ |= 0x8;
                    this.service_ = other.service_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasIdentifier() && this.hasPassword() && this.hasKind() && this.hasService();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                TokenProto parsedMessage = null;
                try {
                    parsedMessage = TokenProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (TokenProto)e.getUnfinishedMessage();
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
            public boolean hasIdentifier() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public ByteString getIdentifier() {
                return this.identifier_;
            }
            
            public Builder setIdentifier(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.identifier_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearIdentifier() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.identifier_ = TokenProto.getDefaultInstance().getIdentifier();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasPassword() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public ByteString getPassword() {
                return this.password_;
            }
            
            public Builder setPassword(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.password_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPassword() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.password_ = TokenProto.getDefaultInstance().getPassword();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasKind() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getKind() {
                final Object ref = this.kind_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.kind_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getKindBytes() {
                final Object ref = this.kind_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.kind_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setKind(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.kind_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearKind() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.kind_ = TokenProto.getDefaultInstance().getKind();
                this.onChanged();
                return this;
            }
            
            public Builder setKindBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.kind_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasService() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public String getService() {
                final Object ref = this.service_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.service_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getServiceBytes() {
                final Object ref = this.service_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.service_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setService(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.service_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearService() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.service_ = TokenProto.getDefaultInstance().getService();
                this.onChanged();
                return this;
            }
            
            public Builder setServiceBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.service_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class CredentialsKVProto extends GeneratedMessage implements CredentialsKVProtoOrBuilder
    {
        private static final CredentialsKVProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CredentialsKVProto> PARSER;
        private int bitField0_;
        public static final int ALIAS_FIELD_NUMBER = 1;
        private Object alias_;
        public static final int TOKEN_FIELD_NUMBER = 2;
        private TokenProto token_;
        public static final int SECRET_FIELD_NUMBER = 3;
        private ByteString secret_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CredentialsKVProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CredentialsKVProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CredentialsKVProto getDefaultInstance() {
            return CredentialsKVProto.defaultInstance;
        }
        
        @Override
        public CredentialsKVProto getDefaultInstanceForType() {
            return CredentialsKVProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CredentialsKVProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.alias_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            TokenProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.token_.toBuilder();
                            }
                            this.token_ = input.readMessage(TokenProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.token_);
                                this.token_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.secret_ = input.readBytes();
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
            return SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CredentialsKVProto.class, Builder.class);
        }
        
        @Override
        public Parser<CredentialsKVProto> getParserForType() {
            return CredentialsKVProto.PARSER;
        }
        
        @Override
        public boolean hasAlias() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getAlias() {
            final Object ref = this.alias_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.alias_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getAliasBytes() {
            final Object ref = this.alias_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.alias_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasToken() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public TokenProto getToken() {
            return this.token_;
        }
        
        @Override
        public TokenProtoOrBuilder getTokenOrBuilder() {
            return this.token_;
        }
        
        @Override
        public boolean hasSecret() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public ByteString getSecret() {
            return this.secret_;
        }
        
        private void initFields() {
            this.alias_ = "";
            this.token_ = TokenProto.getDefaultInstance();
            this.secret_ = ByteString.EMPTY;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasAlias()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (this.hasToken() && !this.getToken().isInitialized()) {
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
                output.writeBytes(1, this.getAliasBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.token_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.secret_);
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
                size += CodedOutputStream.computeBytesSize(1, this.getAliasBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.token_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.secret_);
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
            if (!(obj instanceof CredentialsKVProto)) {
                return super.equals(obj);
            }
            final CredentialsKVProto other = (CredentialsKVProto)obj;
            boolean result = true;
            result = (result && this.hasAlias() == other.hasAlias());
            if (this.hasAlias()) {
                result = (result && this.getAlias().equals(other.getAlias()));
            }
            result = (result && this.hasToken() == other.hasToken());
            if (this.hasToken()) {
                result = (result && this.getToken().equals(other.getToken()));
            }
            result = (result && this.hasSecret() == other.hasSecret());
            if (this.hasSecret()) {
                result = (result && this.getSecret().equals(other.getSecret()));
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
            if (this.hasAlias()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getAlias().hashCode();
            }
            if (this.hasToken()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getToken().hashCode();
            }
            if (this.hasSecret()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getSecret().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static CredentialsKVProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CredentialsKVProto.PARSER.parseFrom(data);
        }
        
        public static CredentialsKVProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CredentialsKVProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CredentialsKVProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CredentialsKVProto.PARSER.parseFrom(data);
        }
        
        public static CredentialsKVProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CredentialsKVProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CredentialsKVProto parseFrom(final InputStream input) throws IOException {
            return CredentialsKVProto.PARSER.parseFrom(input);
        }
        
        public static CredentialsKVProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsKVProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CredentialsKVProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CredentialsKVProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CredentialsKVProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsKVProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CredentialsKVProto parseFrom(final CodedInputStream input) throws IOException {
            return CredentialsKVProto.PARSER.parseFrom(input);
        }
        
        public static CredentialsKVProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsKVProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CredentialsKVProto prototype) {
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
            CredentialsKVProto.PARSER = new AbstractParser<CredentialsKVProto>() {
                @Override
                public CredentialsKVProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CredentialsKVProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CredentialsKVProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CredentialsKVProtoOrBuilder
        {
            private int bitField0_;
            private Object alias_;
            private TokenProto token_;
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> tokenBuilder_;
            private ByteString secret_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CredentialsKVProto.class, Builder.class);
            }
            
            private Builder() {
                this.alias_ = "";
                this.token_ = TokenProto.getDefaultInstance();
                this.secret_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.alias_ = "";
                this.token_ = TokenProto.getDefaultInstance();
                this.secret_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CredentialsKVProto.alwaysUseFieldBuilders) {
                    this.getTokenFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.alias_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                this.secret_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsKVProto_descriptor;
            }
            
            @Override
            public CredentialsKVProto getDefaultInstanceForType() {
                return CredentialsKVProto.getDefaultInstance();
            }
            
            @Override
            public CredentialsKVProto build() {
                final CredentialsKVProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CredentialsKVProto buildPartial() {
                final CredentialsKVProto result = new CredentialsKVProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.alias_ = this.alias_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.tokenBuilder_ == null) {
                    result.token_ = this.token_;
                }
                else {
                    result.token_ = this.tokenBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.secret_ = this.secret_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CredentialsKVProto) {
                    return this.mergeFrom((CredentialsKVProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CredentialsKVProto other) {
                if (other == CredentialsKVProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAlias()) {
                    this.bitField0_ |= 0x1;
                    this.alias_ = other.alias_;
                    this.onChanged();
                }
                if (other.hasToken()) {
                    this.mergeToken(other.getToken());
                }
                if (other.hasSecret()) {
                    this.setSecret(other.getSecret());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasAlias() && (!this.hasToken() || this.getToken().isInitialized());
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                CredentialsKVProto parsedMessage = null;
                try {
                    parsedMessage = CredentialsKVProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CredentialsKVProto)e.getUnfinishedMessage();
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
            public boolean hasAlias() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getAlias() {
                final Object ref = this.alias_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.alias_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getAliasBytes() {
                final Object ref = this.alias_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.alias_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setAlias(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.alias_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAlias() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.alias_ = CredentialsKVProto.getDefaultInstance().getAlias();
                this.onChanged();
                return this;
            }
            
            public Builder setAliasBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.alias_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasToken() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public TokenProto getToken() {
                if (this.tokenBuilder_ == null) {
                    return this.token_;
                }
                return this.tokenBuilder_.getMessage();
            }
            
            public Builder setToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.token_ = value;
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setToken(final TokenProto.Builder builderForValue) {
                if (this.tokenBuilder_ == null) {
                    this.token_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.token_ != TokenProto.getDefaultInstance()) {
                        this.token_ = TokenProto.newBuilder(this.token_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.token_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearToken() {
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public TokenProto.Builder getTokenBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getTokenFieldBuilder().getBuilder();
            }
            
            @Override
            public TokenProtoOrBuilder getTokenOrBuilder() {
                if (this.tokenBuilder_ != null) {
                    return this.tokenBuilder_.getMessageOrBuilder();
                }
                return this.token_;
            }
            
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> getTokenFieldBuilder() {
                if (this.tokenBuilder_ == null) {
                    this.tokenBuilder_ = new SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder>(this.token_, this.getParentForChildren(), this.isClean());
                    this.token_ = null;
                }
                return this.tokenBuilder_;
            }
            
            @Override
            public boolean hasSecret() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public ByteString getSecret() {
                return this.secret_;
            }
            
            public Builder setSecret(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.secret_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSecret() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.secret_ = CredentialsKVProto.getDefaultInstance().getSecret();
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class CredentialsProto extends GeneratedMessage implements CredentialsProtoOrBuilder
    {
        private static final CredentialsProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CredentialsProto> PARSER;
        public static final int TOKENS_FIELD_NUMBER = 1;
        private List<CredentialsKVProto> tokens_;
        public static final int SECRETS_FIELD_NUMBER = 2;
        private List<CredentialsKVProto> secrets_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CredentialsProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CredentialsProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CredentialsProto getDefaultInstance() {
            return CredentialsProto.defaultInstance;
        }
        
        @Override
        public CredentialsProto getDefaultInstanceForType() {
            return CredentialsProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CredentialsProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.tokens_ = new ArrayList<CredentialsKVProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.tokens_.add(input.readMessage(CredentialsKVProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.secrets_ = new ArrayList<CredentialsKVProto>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.secrets_.add(input.readMessage(CredentialsKVProto.PARSER, extensionRegistry));
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
                    this.tokens_ = Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.tokens_);
                }
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.secrets_ = Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.secrets_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return SecurityProtos.internal_static_hadoop_common_CredentialsProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_CredentialsProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CredentialsProto.class, Builder.class);
        }
        
        @Override
        public Parser<CredentialsProto> getParserForType() {
            return CredentialsProto.PARSER;
        }
        
        @Override
        public List<CredentialsKVProto> getTokensList() {
            return this.tokens_;
        }
        
        @Override
        public List<? extends CredentialsKVProtoOrBuilder> getTokensOrBuilderList() {
            return this.tokens_;
        }
        
        @Override
        public int getTokensCount() {
            return this.tokens_.size();
        }
        
        @Override
        public CredentialsKVProto getTokens(final int index) {
            return this.tokens_.get(index);
        }
        
        @Override
        public CredentialsKVProtoOrBuilder getTokensOrBuilder(final int index) {
            return this.tokens_.get(index);
        }
        
        @Override
        public List<CredentialsKVProto> getSecretsList() {
            return this.secrets_;
        }
        
        @Override
        public List<? extends CredentialsKVProtoOrBuilder> getSecretsOrBuilderList() {
            return this.secrets_;
        }
        
        @Override
        public int getSecretsCount() {
            return this.secrets_.size();
        }
        
        @Override
        public CredentialsKVProto getSecrets(final int index) {
            return this.secrets_.get(index);
        }
        
        @Override
        public CredentialsKVProtoOrBuilder getSecretsOrBuilder(final int index) {
            return this.secrets_.get(index);
        }
        
        private void initFields() {
            this.tokens_ = Collections.emptyList();
            this.secrets_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getTokensCount(); ++i) {
                if (!this.getTokens(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getSecretsCount(); ++i) {
                if (!this.getSecrets(i).isInitialized()) {
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
            for (int i = 0; i < this.tokens_.size(); ++i) {
                output.writeMessage(1, this.tokens_.get(i));
            }
            for (int i = 0; i < this.secrets_.size(); ++i) {
                output.writeMessage(2, this.secrets_.get(i));
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
            for (int i = 0; i < this.tokens_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.tokens_.get(i));
            }
            for (int i = 0; i < this.secrets_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.secrets_.get(i));
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
            if (!(obj instanceof CredentialsProto)) {
                return super.equals(obj);
            }
            final CredentialsProto other = (CredentialsProto)obj;
            boolean result = true;
            result = (result && this.getTokensList().equals(other.getTokensList()));
            result = (result && this.getSecretsList().equals(other.getSecretsList()));
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
            if (this.getTokensCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getTokensList().hashCode();
            }
            if (this.getSecretsCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getSecretsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static CredentialsProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CredentialsProto.PARSER.parseFrom(data);
        }
        
        public static CredentialsProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CredentialsProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CredentialsProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CredentialsProto.PARSER.parseFrom(data);
        }
        
        public static CredentialsProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CredentialsProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CredentialsProto parseFrom(final InputStream input) throws IOException {
            return CredentialsProto.PARSER.parseFrom(input);
        }
        
        public static CredentialsProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CredentialsProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CredentialsProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CredentialsProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CredentialsProto parseFrom(final CodedInputStream input) throws IOException {
            return CredentialsProto.PARSER.parseFrom(input);
        }
        
        public static CredentialsProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CredentialsProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CredentialsProto prototype) {
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
            CredentialsProto.PARSER = new AbstractParser<CredentialsProto>() {
                @Override
                public CredentialsProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CredentialsProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CredentialsProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CredentialsProtoOrBuilder
        {
            private int bitField0_;
            private List<CredentialsKVProto> tokens_;
            private RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder> tokensBuilder_;
            private List<CredentialsKVProto> secrets_;
            private RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder> secretsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CredentialsProto.class, Builder.class);
            }
            
            private Builder() {
                this.tokens_ = Collections.emptyList();
                this.secrets_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.tokens_ = Collections.emptyList();
                this.secrets_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CredentialsProto.alwaysUseFieldBuilders) {
                    this.getTokensFieldBuilder();
                    this.getSecretsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.tokensBuilder_ == null) {
                    this.tokens_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.tokensBuilder_.clear();
                }
                if (this.secretsBuilder_ == null) {
                    this.secrets_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.secretsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_CredentialsProto_descriptor;
            }
            
            @Override
            public CredentialsProto getDefaultInstanceForType() {
                return CredentialsProto.getDefaultInstance();
            }
            
            @Override
            public CredentialsProto build() {
                final CredentialsProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CredentialsProto buildPartial() {
                final CredentialsProto result = new CredentialsProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.tokensBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.tokens_ = Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.tokens_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.tokens_ = this.tokens_;
                }
                else {
                    result.tokens_ = this.tokensBuilder_.build();
                }
                if (this.secretsBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.secrets_ = Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.secrets_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.secrets_ = this.secrets_;
                }
                else {
                    result.secrets_ = this.secretsBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CredentialsProto) {
                    return this.mergeFrom((CredentialsProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CredentialsProto other) {
                if (other == CredentialsProto.getDefaultInstance()) {
                    return this;
                }
                if (this.tokensBuilder_ == null) {
                    if (!other.tokens_.isEmpty()) {
                        if (this.tokens_.isEmpty()) {
                            this.tokens_ = other.tokens_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureTokensIsMutable();
                            this.tokens_.addAll(other.tokens_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.tokens_.isEmpty()) {
                    if (this.tokensBuilder_.isEmpty()) {
                        this.tokensBuilder_.dispose();
                        this.tokensBuilder_ = null;
                        this.tokens_ = other.tokens_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.tokensBuilder_ = (CredentialsProto.alwaysUseFieldBuilders ? this.getTokensFieldBuilder() : null);
                    }
                    else {
                        this.tokensBuilder_.addAllMessages(other.tokens_);
                    }
                }
                if (this.secretsBuilder_ == null) {
                    if (!other.secrets_.isEmpty()) {
                        if (this.secrets_.isEmpty()) {
                            this.secrets_ = other.secrets_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureSecretsIsMutable();
                            this.secrets_.addAll(other.secrets_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.secrets_.isEmpty()) {
                    if (this.secretsBuilder_.isEmpty()) {
                        this.secretsBuilder_.dispose();
                        this.secretsBuilder_ = null;
                        this.secrets_ = other.secrets_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.secretsBuilder_ = (CredentialsProto.alwaysUseFieldBuilders ? this.getSecretsFieldBuilder() : null);
                    }
                    else {
                        this.secretsBuilder_.addAllMessages(other.secrets_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getTokensCount(); ++i) {
                    if (!this.getTokens(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getSecretsCount(); ++i) {
                    if (!this.getSecrets(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                CredentialsProto parsedMessage = null;
                try {
                    parsedMessage = CredentialsProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CredentialsProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureTokensIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.tokens_ = new ArrayList<CredentialsKVProto>(this.tokens_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<CredentialsKVProto> getTokensList() {
                if (this.tokensBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.tokens_);
                }
                return this.tokensBuilder_.getMessageList();
            }
            
            @Override
            public int getTokensCount() {
                if (this.tokensBuilder_ == null) {
                    return this.tokens_.size();
                }
                return this.tokensBuilder_.getCount();
            }
            
            @Override
            public CredentialsKVProto getTokens(final int index) {
                if (this.tokensBuilder_ == null) {
                    return this.tokens_.get(index);
                }
                return this.tokensBuilder_.getMessage(index);
            }
            
            public Builder setTokens(final int index, final CredentialsKVProto value) {
                if (this.tokensBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureTokensIsMutable();
                    this.tokens_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setTokens(final int index, final CredentialsKVProto.Builder builderForValue) {
                if (this.tokensBuilder_ == null) {
                    this.ensureTokensIsMutable();
                    this.tokens_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addTokens(final CredentialsKVProto value) {
                if (this.tokensBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureTokensIsMutable();
                    this.tokens_.add(value);
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addTokens(final int index, final CredentialsKVProto value) {
                if (this.tokensBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureTokensIsMutable();
                    this.tokens_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addTokens(final CredentialsKVProto.Builder builderForValue) {
                if (this.tokensBuilder_ == null) {
                    this.ensureTokensIsMutable();
                    this.tokens_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addTokens(final int index, final CredentialsKVProto.Builder builderForValue) {
                if (this.tokensBuilder_ == null) {
                    this.ensureTokensIsMutable();
                    this.tokens_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllTokens(final Iterable<? extends CredentialsKVProto> values) {
                if (this.tokensBuilder_ == null) {
                    this.ensureTokensIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.tokens_);
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearTokens() {
                if (this.tokensBuilder_ == null) {
                    this.tokens_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeTokens(final int index) {
                if (this.tokensBuilder_ == null) {
                    this.ensureTokensIsMutable();
                    this.tokens_.remove(index);
                    this.onChanged();
                }
                else {
                    this.tokensBuilder_.remove(index);
                }
                return this;
            }
            
            public CredentialsKVProto.Builder getTokensBuilder(final int index) {
                return this.getTokensFieldBuilder().getBuilder(index);
            }
            
            @Override
            public CredentialsKVProtoOrBuilder getTokensOrBuilder(final int index) {
                if (this.tokensBuilder_ == null) {
                    return this.tokens_.get(index);
                }
                return this.tokensBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends CredentialsKVProtoOrBuilder> getTokensOrBuilderList() {
                if (this.tokensBuilder_ != null) {
                    return this.tokensBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends CredentialsKVProtoOrBuilder>)this.tokens_);
            }
            
            public CredentialsKVProto.Builder addTokensBuilder() {
                return this.getTokensFieldBuilder().addBuilder(CredentialsKVProto.getDefaultInstance());
            }
            
            public CredentialsKVProto.Builder addTokensBuilder(final int index) {
                return this.getTokensFieldBuilder().addBuilder(index, CredentialsKVProto.getDefaultInstance());
            }
            
            public List<CredentialsKVProto.Builder> getTokensBuilderList() {
                return this.getTokensFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder> getTokensFieldBuilder() {
                if (this.tokensBuilder_ == null) {
                    this.tokensBuilder_ = new RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder>(this.tokens_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.tokens_ = null;
                }
                return this.tokensBuilder_;
            }
            
            private void ensureSecretsIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.secrets_ = new ArrayList<CredentialsKVProto>(this.secrets_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            @Override
            public List<CredentialsKVProto> getSecretsList() {
                if (this.secretsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends CredentialsKVProto>)this.secrets_);
                }
                return this.secretsBuilder_.getMessageList();
            }
            
            @Override
            public int getSecretsCount() {
                if (this.secretsBuilder_ == null) {
                    return this.secrets_.size();
                }
                return this.secretsBuilder_.getCount();
            }
            
            @Override
            public CredentialsKVProto getSecrets(final int index) {
                if (this.secretsBuilder_ == null) {
                    return this.secrets_.get(index);
                }
                return this.secretsBuilder_.getMessage(index);
            }
            
            public Builder setSecrets(final int index, final CredentialsKVProto value) {
                if (this.secretsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSecretsIsMutable();
                    this.secrets_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setSecrets(final int index, final CredentialsKVProto.Builder builderForValue) {
                if (this.secretsBuilder_ == null) {
                    this.ensureSecretsIsMutable();
                    this.secrets_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addSecrets(final CredentialsKVProto value) {
                if (this.secretsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSecretsIsMutable();
                    this.secrets_.add(value);
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addSecrets(final int index, final CredentialsKVProto value) {
                if (this.secretsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSecretsIsMutable();
                    this.secrets_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addSecrets(final CredentialsKVProto.Builder builderForValue) {
                if (this.secretsBuilder_ == null) {
                    this.ensureSecretsIsMutable();
                    this.secrets_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addSecrets(final int index, final CredentialsKVProto.Builder builderForValue) {
                if (this.secretsBuilder_ == null) {
                    this.ensureSecretsIsMutable();
                    this.secrets_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllSecrets(final Iterable<? extends CredentialsKVProto> values) {
                if (this.secretsBuilder_ == null) {
                    this.ensureSecretsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.secrets_);
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearSecrets() {
                if (this.secretsBuilder_ == null) {
                    this.secrets_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeSecrets(final int index) {
                if (this.secretsBuilder_ == null) {
                    this.ensureSecretsIsMutable();
                    this.secrets_.remove(index);
                    this.onChanged();
                }
                else {
                    this.secretsBuilder_.remove(index);
                }
                return this;
            }
            
            public CredentialsKVProto.Builder getSecretsBuilder(final int index) {
                return this.getSecretsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public CredentialsKVProtoOrBuilder getSecretsOrBuilder(final int index) {
                if (this.secretsBuilder_ == null) {
                    return this.secrets_.get(index);
                }
                return this.secretsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends CredentialsKVProtoOrBuilder> getSecretsOrBuilderList() {
                if (this.secretsBuilder_ != null) {
                    return this.secretsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends CredentialsKVProtoOrBuilder>)this.secrets_);
            }
            
            public CredentialsKVProto.Builder addSecretsBuilder() {
                return this.getSecretsFieldBuilder().addBuilder(CredentialsKVProto.getDefaultInstance());
            }
            
            public CredentialsKVProto.Builder addSecretsBuilder(final int index) {
                return this.getSecretsFieldBuilder().addBuilder(index, CredentialsKVProto.getDefaultInstance());
            }
            
            public List<CredentialsKVProto.Builder> getSecretsBuilderList() {
                return this.getSecretsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder> getSecretsFieldBuilder() {
                if (this.secretsBuilder_ == null) {
                    this.secretsBuilder_ = new RepeatedFieldBuilder<CredentialsKVProto, CredentialsKVProto.Builder, CredentialsKVProtoOrBuilder>(this.secrets_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.secrets_ = null;
                }
                return this.secretsBuilder_;
            }
        }
    }
    
    public static final class GetDelegationTokenRequestProto extends GeneratedMessage implements GetDelegationTokenRequestProtoOrBuilder
    {
        private static final GetDelegationTokenRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetDelegationTokenRequestProto> PARSER;
        private int bitField0_;
        public static final int RENEWER_FIELD_NUMBER = 1;
        private Object renewer_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetDelegationTokenRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetDelegationTokenRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetDelegationTokenRequestProto getDefaultInstance() {
            return GetDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public GetDelegationTokenRequestProto getDefaultInstanceForType() {
            return GetDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetDelegationTokenRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.renewer_ = input.readBytes();
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
            return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetDelegationTokenRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetDelegationTokenRequestProto> getParserForType() {
            return GetDelegationTokenRequestProto.PARSER;
        }
        
        @Override
        public boolean hasRenewer() {
            return (this.bitField0_ & 0x1) == 0x1;
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
        
        private void initFields() {
            this.renewer_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasRenewer()) {
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
                output.writeBytes(1, this.getRenewerBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getRenewerBytes());
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
            if (!(obj instanceof GetDelegationTokenRequestProto)) {
                return super.equals(obj);
            }
            final GetDelegationTokenRequestProto other = (GetDelegationTokenRequestProto)obj;
            boolean result = true;
            result = (result && this.hasRenewer() == other.hasRenewer());
            if (this.hasRenewer()) {
                result = (result && this.getRenewer().equals(other.getRenewer()));
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
            if (this.hasRenewer()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getRenewer().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final InputStream input) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetDelegationTokenRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetDelegationTokenRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetDelegationTokenRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetDelegationTokenRequestProto prototype) {
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
            GetDelegationTokenRequestProto.PARSER = new AbstractParser<GetDelegationTokenRequestProto>() {
                @Override
                public GetDelegationTokenRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetDelegationTokenRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetDelegationTokenRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetDelegationTokenRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object renewer_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetDelegationTokenRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.renewer_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.renewer_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetDelegationTokenRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.renewer_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            public GetDelegationTokenRequestProto getDefaultInstanceForType() {
                return GetDelegationTokenRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetDelegationTokenRequestProto build() {
                final GetDelegationTokenRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetDelegationTokenRequestProto buildPartial() {
                final GetDelegationTokenRequestProto result = new GetDelegationTokenRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.renewer_ = this.renewer_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetDelegationTokenRequestProto) {
                    return this.mergeFrom((GetDelegationTokenRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetDelegationTokenRequestProto other) {
                if (other == GetDelegationTokenRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasRenewer()) {
                    this.bitField0_ |= 0x1;
                    this.renewer_ = other.renewer_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasRenewer();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetDelegationTokenRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetDelegationTokenRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetDelegationTokenRequestProto)e.getUnfinishedMessage();
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
            public boolean hasRenewer() {
                return (this.bitField0_ & 0x1) == 0x1;
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
                this.bitField0_ |= 0x1;
                this.renewer_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRenewer() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.renewer_ = GetDelegationTokenRequestProto.getDefaultInstance().getRenewer();
                this.onChanged();
                return this;
            }
            
            public Builder setRenewerBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.renewer_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GetDelegationTokenResponseProto extends GeneratedMessage implements GetDelegationTokenResponseProtoOrBuilder
    {
        private static final GetDelegationTokenResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetDelegationTokenResponseProto> PARSER;
        private int bitField0_;
        public static final int TOKEN_FIELD_NUMBER = 1;
        private TokenProto token_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetDelegationTokenResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetDelegationTokenResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetDelegationTokenResponseProto getDefaultInstance() {
            return GetDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public GetDelegationTokenResponseProto getDefaultInstanceForType() {
            return GetDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetDelegationTokenResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            TokenProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.token_.toBuilder();
                            }
                            this.token_ = input.readMessage(TokenProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.token_);
                                this.token_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
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
            return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetDelegationTokenResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetDelegationTokenResponseProto> getParserForType() {
            return GetDelegationTokenResponseProto.PARSER;
        }
        
        @Override
        public boolean hasToken() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public TokenProto getToken() {
            return this.token_;
        }
        
        @Override
        public TokenProtoOrBuilder getTokenOrBuilder() {
            return this.token_;
        }
        
        private void initFields() {
            this.token_ = TokenProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (this.hasToken() && !this.getToken().isInitialized()) {
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
                output.writeMessage(1, this.token_);
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
                size += CodedOutputStream.computeMessageSize(1, this.token_);
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
            if (!(obj instanceof GetDelegationTokenResponseProto)) {
                return super.equals(obj);
            }
            final GetDelegationTokenResponseProto other = (GetDelegationTokenResponseProto)obj;
            boolean result = true;
            result = (result && this.hasToken() == other.hasToken());
            if (this.hasToken()) {
                result = (result && this.getToken().equals(other.getToken()));
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
            if (this.hasToken()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getToken().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final InputStream input) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetDelegationTokenResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetDelegationTokenResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetDelegationTokenResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetDelegationTokenResponseProto prototype) {
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
            GetDelegationTokenResponseProto.PARSER = new AbstractParser<GetDelegationTokenResponseProto>() {
                @Override
                public GetDelegationTokenResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetDelegationTokenResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetDelegationTokenResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetDelegationTokenResponseProtoOrBuilder
        {
            private int bitField0_;
            private TokenProto token_;
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> tokenBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetDelegationTokenResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetDelegationTokenResponseProto.alwaysUseFieldBuilders) {
                    this.getTokenFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_GetDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            public GetDelegationTokenResponseProto getDefaultInstanceForType() {
                return GetDelegationTokenResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetDelegationTokenResponseProto build() {
                final GetDelegationTokenResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetDelegationTokenResponseProto buildPartial() {
                final GetDelegationTokenResponseProto result = new GetDelegationTokenResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.tokenBuilder_ == null) {
                    result.token_ = this.token_;
                }
                else {
                    result.token_ = this.tokenBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetDelegationTokenResponseProto) {
                    return this.mergeFrom((GetDelegationTokenResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetDelegationTokenResponseProto other) {
                if (other == GetDelegationTokenResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasToken()) {
                    this.mergeToken(other.getToken());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return !this.hasToken() || this.getToken().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetDelegationTokenResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetDelegationTokenResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetDelegationTokenResponseProto)e.getUnfinishedMessage();
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
            public boolean hasToken() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public TokenProto getToken() {
                if (this.tokenBuilder_ == null) {
                    return this.token_;
                }
                return this.tokenBuilder_.getMessage();
            }
            
            public Builder setToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.token_ = value;
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setToken(final TokenProto.Builder builderForValue) {
                if (this.tokenBuilder_ == null) {
                    this.token_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.token_ != TokenProto.getDefaultInstance()) {
                        this.token_ = TokenProto.newBuilder(this.token_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.token_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearToken() {
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public TokenProto.Builder getTokenBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getTokenFieldBuilder().getBuilder();
            }
            
            @Override
            public TokenProtoOrBuilder getTokenOrBuilder() {
                if (this.tokenBuilder_ != null) {
                    return this.tokenBuilder_.getMessageOrBuilder();
                }
                return this.token_;
            }
            
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> getTokenFieldBuilder() {
                if (this.tokenBuilder_ == null) {
                    this.tokenBuilder_ = new SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder>(this.token_, this.getParentForChildren(), this.isClean());
                    this.token_ = null;
                }
                return this.tokenBuilder_;
            }
        }
    }
    
    public static final class RenewDelegationTokenRequestProto extends GeneratedMessage implements RenewDelegationTokenRequestProtoOrBuilder
    {
        private static final RenewDelegationTokenRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RenewDelegationTokenRequestProto> PARSER;
        private int bitField0_;
        public static final int TOKEN_FIELD_NUMBER = 1;
        private TokenProto token_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RenewDelegationTokenRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RenewDelegationTokenRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RenewDelegationTokenRequestProto getDefaultInstance() {
            return RenewDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public RenewDelegationTokenRequestProto getDefaultInstanceForType() {
            return RenewDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RenewDelegationTokenRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            TokenProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.token_.toBuilder();
                            }
                            this.token_ = input.readMessage(TokenProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.token_);
                                this.token_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
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
            return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RenewDelegationTokenRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RenewDelegationTokenRequestProto> getParserForType() {
            return RenewDelegationTokenRequestProto.PARSER;
        }
        
        @Override
        public boolean hasToken() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public TokenProto getToken() {
            return this.token_;
        }
        
        @Override
        public TokenProtoOrBuilder getTokenOrBuilder() {
            return this.token_;
        }
        
        private void initFields() {
            this.token_ = TokenProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasToken()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.getToken().isInitialized()) {
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
                output.writeMessage(1, this.token_);
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
                size += CodedOutputStream.computeMessageSize(1, this.token_);
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
            if (!(obj instanceof RenewDelegationTokenRequestProto)) {
                return super.equals(obj);
            }
            final RenewDelegationTokenRequestProto other = (RenewDelegationTokenRequestProto)obj;
            boolean result = true;
            result = (result && this.hasToken() == other.hasToken());
            if (this.hasToken()) {
                result = (result && this.getToken().equals(other.getToken()));
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
            if (this.hasToken()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getToken().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final InputStream input) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RenewDelegationTokenRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RenewDelegationTokenRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static RenewDelegationTokenRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RenewDelegationTokenRequestProto prototype) {
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
            RenewDelegationTokenRequestProto.PARSER = new AbstractParser<RenewDelegationTokenRequestProto>() {
                @Override
                public RenewDelegationTokenRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RenewDelegationTokenRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RenewDelegationTokenRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RenewDelegationTokenRequestProtoOrBuilder
        {
            private int bitField0_;
            private TokenProto token_;
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> tokenBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RenewDelegationTokenRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RenewDelegationTokenRequestProto.alwaysUseFieldBuilders) {
                    this.getTokenFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            public RenewDelegationTokenRequestProto getDefaultInstanceForType() {
                return RenewDelegationTokenRequestProto.getDefaultInstance();
            }
            
            @Override
            public RenewDelegationTokenRequestProto build() {
                final RenewDelegationTokenRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RenewDelegationTokenRequestProto buildPartial() {
                final RenewDelegationTokenRequestProto result = new RenewDelegationTokenRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.tokenBuilder_ == null) {
                    result.token_ = this.token_;
                }
                else {
                    result.token_ = this.tokenBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RenewDelegationTokenRequestProto) {
                    return this.mergeFrom((RenewDelegationTokenRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RenewDelegationTokenRequestProto other) {
                if (other == RenewDelegationTokenRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasToken()) {
                    this.mergeToken(other.getToken());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasToken() && this.getToken().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RenewDelegationTokenRequestProto parsedMessage = null;
                try {
                    parsedMessage = RenewDelegationTokenRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RenewDelegationTokenRequestProto)e.getUnfinishedMessage();
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
            public boolean hasToken() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public TokenProto getToken() {
                if (this.tokenBuilder_ == null) {
                    return this.token_;
                }
                return this.tokenBuilder_.getMessage();
            }
            
            public Builder setToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.token_ = value;
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setToken(final TokenProto.Builder builderForValue) {
                if (this.tokenBuilder_ == null) {
                    this.token_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.token_ != TokenProto.getDefaultInstance()) {
                        this.token_ = TokenProto.newBuilder(this.token_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.token_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearToken() {
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public TokenProto.Builder getTokenBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getTokenFieldBuilder().getBuilder();
            }
            
            @Override
            public TokenProtoOrBuilder getTokenOrBuilder() {
                if (this.tokenBuilder_ != null) {
                    return this.tokenBuilder_.getMessageOrBuilder();
                }
                return this.token_;
            }
            
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> getTokenFieldBuilder() {
                if (this.tokenBuilder_ == null) {
                    this.tokenBuilder_ = new SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder>(this.token_, this.getParentForChildren(), this.isClean());
                    this.token_ = null;
                }
                return this.tokenBuilder_;
            }
        }
    }
    
    public static final class RenewDelegationTokenResponseProto extends GeneratedMessage implements RenewDelegationTokenResponseProtoOrBuilder
    {
        private static final RenewDelegationTokenResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RenewDelegationTokenResponseProto> PARSER;
        private int bitField0_;
        public static final int NEWEXPIRYTIME_FIELD_NUMBER = 1;
        private long newExpiryTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RenewDelegationTokenResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RenewDelegationTokenResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RenewDelegationTokenResponseProto getDefaultInstance() {
            return RenewDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public RenewDelegationTokenResponseProto getDefaultInstanceForType() {
            return RenewDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RenewDelegationTokenResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.newExpiryTime_ = input.readUInt64();
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
            return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RenewDelegationTokenResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RenewDelegationTokenResponseProto> getParserForType() {
            return RenewDelegationTokenResponseProto.PARSER;
        }
        
        @Override
        public boolean hasNewExpiryTime() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getNewExpiryTime() {
            return this.newExpiryTime_;
        }
        
        private void initFields() {
            this.newExpiryTime_ = 0L;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasNewExpiryTime()) {
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
                output.writeUInt64(1, this.newExpiryTime_);
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
                size += CodedOutputStream.computeUInt64Size(1, this.newExpiryTime_);
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
            if (!(obj instanceof RenewDelegationTokenResponseProto)) {
                return super.equals(obj);
            }
            final RenewDelegationTokenResponseProto other = (RenewDelegationTokenResponseProto)obj;
            boolean result = true;
            result = (result && this.hasNewExpiryTime() == other.hasNewExpiryTime());
            if (this.hasNewExpiryTime()) {
                result = (result && this.getNewExpiryTime() == other.getNewExpiryTime());
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
            if (this.hasNewExpiryTime()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getNewExpiryTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final InputStream input) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RenewDelegationTokenResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RenewDelegationTokenResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static RenewDelegationTokenResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RenewDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RenewDelegationTokenResponseProto prototype) {
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
            RenewDelegationTokenResponseProto.PARSER = new AbstractParser<RenewDelegationTokenResponseProto>() {
                @Override
                public RenewDelegationTokenResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RenewDelegationTokenResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RenewDelegationTokenResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RenewDelegationTokenResponseProtoOrBuilder
        {
            private int bitField0_;
            private long newExpiryTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RenewDelegationTokenResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RenewDelegationTokenResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.newExpiryTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_RenewDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            public RenewDelegationTokenResponseProto getDefaultInstanceForType() {
                return RenewDelegationTokenResponseProto.getDefaultInstance();
            }
            
            @Override
            public RenewDelegationTokenResponseProto build() {
                final RenewDelegationTokenResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RenewDelegationTokenResponseProto buildPartial() {
                final RenewDelegationTokenResponseProto result = new RenewDelegationTokenResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.newExpiryTime_ = this.newExpiryTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RenewDelegationTokenResponseProto) {
                    return this.mergeFrom((RenewDelegationTokenResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RenewDelegationTokenResponseProto other) {
                if (other == RenewDelegationTokenResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasNewExpiryTime()) {
                    this.setNewExpiryTime(other.getNewExpiryTime());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasNewExpiryTime();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RenewDelegationTokenResponseProto parsedMessage = null;
                try {
                    parsedMessage = RenewDelegationTokenResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RenewDelegationTokenResponseProto)e.getUnfinishedMessage();
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
            public boolean hasNewExpiryTime() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getNewExpiryTime() {
                return this.newExpiryTime_;
            }
            
            public Builder setNewExpiryTime(final long value) {
                this.bitField0_ |= 0x1;
                this.newExpiryTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNewExpiryTime() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.newExpiryTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class CancelDelegationTokenRequestProto extends GeneratedMessage implements CancelDelegationTokenRequestProtoOrBuilder
    {
        private static final CancelDelegationTokenRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CancelDelegationTokenRequestProto> PARSER;
        private int bitField0_;
        public static final int TOKEN_FIELD_NUMBER = 1;
        private TokenProto token_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CancelDelegationTokenRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CancelDelegationTokenRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CancelDelegationTokenRequestProto getDefaultInstance() {
            return CancelDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public CancelDelegationTokenRequestProto getDefaultInstanceForType() {
            return CancelDelegationTokenRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CancelDelegationTokenRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            TokenProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.token_.toBuilder();
                            }
                            this.token_ = input.readMessage(TokenProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.token_);
                                this.token_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
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
            return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CancelDelegationTokenRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<CancelDelegationTokenRequestProto> getParserForType() {
            return CancelDelegationTokenRequestProto.PARSER;
        }
        
        @Override
        public boolean hasToken() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public TokenProto getToken() {
            return this.token_;
        }
        
        @Override
        public TokenProtoOrBuilder getTokenOrBuilder() {
            return this.token_;
        }
        
        private void initFields() {
            this.token_ = TokenProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasToken()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.getToken().isInitialized()) {
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
                output.writeMessage(1, this.token_);
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
                size += CodedOutputStream.computeMessageSize(1, this.token_);
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
            if (!(obj instanceof CancelDelegationTokenRequestProto)) {
                return super.equals(obj);
            }
            final CancelDelegationTokenRequestProto other = (CancelDelegationTokenRequestProto)obj;
            boolean result = true;
            result = (result && this.hasToken() == other.hasToken());
            if (this.hasToken()) {
                result = (result && this.getToken().equals(other.getToken()));
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
            if (this.hasToken()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getToken().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(data);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final InputStream input) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CancelDelegationTokenRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CancelDelegationTokenRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(input);
        }
        
        public static CancelDelegationTokenRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CancelDelegationTokenRequestProto prototype) {
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
            CancelDelegationTokenRequestProto.PARSER = new AbstractParser<CancelDelegationTokenRequestProto>() {
                @Override
                public CancelDelegationTokenRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CancelDelegationTokenRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CancelDelegationTokenRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CancelDelegationTokenRequestProtoOrBuilder
        {
            private int bitField0_;
            private TokenProto token_;
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> tokenBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CancelDelegationTokenRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.token_ = TokenProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CancelDelegationTokenRequestProto.alwaysUseFieldBuilders) {
                    this.getTokenFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenRequestProto_descriptor;
            }
            
            @Override
            public CancelDelegationTokenRequestProto getDefaultInstanceForType() {
                return CancelDelegationTokenRequestProto.getDefaultInstance();
            }
            
            @Override
            public CancelDelegationTokenRequestProto build() {
                final CancelDelegationTokenRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CancelDelegationTokenRequestProto buildPartial() {
                final CancelDelegationTokenRequestProto result = new CancelDelegationTokenRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.tokenBuilder_ == null) {
                    result.token_ = this.token_;
                }
                else {
                    result.token_ = this.tokenBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CancelDelegationTokenRequestProto) {
                    return this.mergeFrom((CancelDelegationTokenRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CancelDelegationTokenRequestProto other) {
                if (other == CancelDelegationTokenRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasToken()) {
                    this.mergeToken(other.getToken());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasToken() && this.getToken().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                CancelDelegationTokenRequestProto parsedMessage = null;
                try {
                    parsedMessage = CancelDelegationTokenRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CancelDelegationTokenRequestProto)e.getUnfinishedMessage();
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
            public boolean hasToken() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public TokenProto getToken() {
                if (this.tokenBuilder_ == null) {
                    return this.token_;
                }
                return this.tokenBuilder_.getMessage();
            }
            
            public Builder setToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.token_ = value;
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setToken(final TokenProto.Builder builderForValue) {
                if (this.tokenBuilder_ == null) {
                    this.token_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeToken(final TokenProto value) {
                if (this.tokenBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.token_ != TokenProto.getDefaultInstance()) {
                        this.token_ = TokenProto.newBuilder(this.token_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.token_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearToken() {
                if (this.tokenBuilder_ == null) {
                    this.token_ = TokenProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.tokenBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public TokenProto.Builder getTokenBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getTokenFieldBuilder().getBuilder();
            }
            
            @Override
            public TokenProtoOrBuilder getTokenOrBuilder() {
                if (this.tokenBuilder_ != null) {
                    return this.tokenBuilder_.getMessageOrBuilder();
                }
                return this.token_;
            }
            
            private SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder> getTokenFieldBuilder() {
                if (this.tokenBuilder_ == null) {
                    this.tokenBuilder_ = new SingleFieldBuilder<TokenProto, TokenProto.Builder, TokenProtoOrBuilder>(this.token_, this.getParentForChildren(), this.isClean());
                    this.token_ = null;
                }
                return this.tokenBuilder_;
            }
        }
    }
    
    public static final class CancelDelegationTokenResponseProto extends GeneratedMessage implements CancelDelegationTokenResponseProtoOrBuilder
    {
        private static final CancelDelegationTokenResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<CancelDelegationTokenResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private CancelDelegationTokenResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private CancelDelegationTokenResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static CancelDelegationTokenResponseProto getDefaultInstance() {
            return CancelDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public CancelDelegationTokenResponseProto getDefaultInstanceForType() {
            return CancelDelegationTokenResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private CancelDelegationTokenResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CancelDelegationTokenResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<CancelDelegationTokenResponseProto> getParserForType() {
            return CancelDelegationTokenResponseProto.PARSER;
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
            if (!(obj instanceof CancelDelegationTokenResponseProto)) {
                return super.equals(obj);
            }
            final CancelDelegationTokenResponseProto other = (CancelDelegationTokenResponseProto)obj;
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
        
        public static CancelDelegationTokenResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(data);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final InputStream input) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static CancelDelegationTokenResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static CancelDelegationTokenResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(input);
        }
        
        public static CancelDelegationTokenResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return CancelDelegationTokenResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final CancelDelegationTokenResponseProto prototype) {
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
            CancelDelegationTokenResponseProto.PARSER = new AbstractParser<CancelDelegationTokenResponseProto>() {
                @Override
                public CancelDelegationTokenResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new CancelDelegationTokenResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new CancelDelegationTokenResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements CancelDelegationTokenResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(CancelDelegationTokenResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (CancelDelegationTokenResponseProto.alwaysUseFieldBuilders) {}
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
                return SecurityProtos.internal_static_hadoop_common_CancelDelegationTokenResponseProto_descriptor;
            }
            
            @Override
            public CancelDelegationTokenResponseProto getDefaultInstanceForType() {
                return CancelDelegationTokenResponseProto.getDefaultInstance();
            }
            
            @Override
            public CancelDelegationTokenResponseProto build() {
                final CancelDelegationTokenResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public CancelDelegationTokenResponseProto buildPartial() {
                final CancelDelegationTokenResponseProto result = new CancelDelegationTokenResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof CancelDelegationTokenResponseProto) {
                    return this.mergeFrom((CancelDelegationTokenResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final CancelDelegationTokenResponseProto other) {
                if (other == CancelDelegationTokenResponseProto.getDefaultInstance()) {
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
                CancelDelegationTokenResponseProto parsedMessage = null;
                try {
                    parsedMessage = CancelDelegationTokenResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (CancelDelegationTokenResponseProto)e.getUnfinishedMessage();
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
    
    public interface CancelDelegationTokenResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface TokenProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasIdentifier();
        
        ByteString getIdentifier();
        
        boolean hasPassword();
        
        ByteString getPassword();
        
        boolean hasKind();
        
        String getKind();
        
        ByteString getKindBytes();
        
        boolean hasService();
        
        String getService();
        
        ByteString getServiceBytes();
    }
    
    public interface CancelDelegationTokenRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasToken();
        
        TokenProto getToken();
        
        TokenProtoOrBuilder getTokenOrBuilder();
    }
    
    public interface RenewDelegationTokenResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasNewExpiryTime();
        
        long getNewExpiryTime();
    }
    
    public interface RenewDelegationTokenRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasToken();
        
        TokenProto getToken();
        
        TokenProtoOrBuilder getTokenOrBuilder();
    }
    
    public interface GetDelegationTokenResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasToken();
        
        TokenProto getToken();
        
        TokenProtoOrBuilder getTokenOrBuilder();
    }
    
    public interface GetDelegationTokenRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasRenewer();
        
        String getRenewer();
        
        ByteString getRenewerBytes();
    }
    
    public interface CredentialsKVProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAlias();
        
        String getAlias();
        
        ByteString getAliasBytes();
        
        boolean hasToken();
        
        TokenProto getToken();
        
        TokenProtoOrBuilder getTokenOrBuilder();
        
        boolean hasSecret();
        
        ByteString getSecret();
    }
    
    public interface CredentialsProtoOrBuilder extends MessageOrBuilder
    {
        List<CredentialsKVProto> getTokensList();
        
        CredentialsKVProto getTokens(final int p0);
        
        int getTokensCount();
        
        List<? extends CredentialsKVProtoOrBuilder> getTokensOrBuilderList();
        
        CredentialsKVProtoOrBuilder getTokensOrBuilder(final int p0);
        
        List<CredentialsKVProto> getSecretsList();
        
        CredentialsKVProto getSecrets(final int p0);
        
        int getSecretsCount();
        
        List<? extends CredentialsKVProtoOrBuilder> getSecretsOrBuilderList();
        
        CredentialsKVProtoOrBuilder getSecretsOrBuilder(final int p0);
    }
}
