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

public final class YarnSecurityTokenProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NMTokenIdentifierProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_AMRMTokenIdentifierProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ContainerTokenIdentifierProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnSecurityTokenProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnSecurityTokenProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n server/yarn_security_token.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\"©\u0001\n\u0016NMTokenIdentifierProto\u0012<\n\fappAttemptId\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012(\n\u0006nodeId\u0018\u0002 \u0001(\u000b2\u0018.hadoop.yarn.NodeIdProto\u0012\u0014\n\fappSubmitter\u0018\u0003 \u0001(\t\u0012\u0011\n\u0005keyId\u0018\u0004 \u0001(\u0005:\u0002-1\"k\n\u0018AMRMTokenIdentifierProto\u0012<\n\fappAttemptId\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012\u0011\n\u0005keyId\u0018\u0002 \u0001(\u0005:\u0002-1\"\u00ff\u0002\n\u001dContainerTokenIdentifierProto\u00122\n\u000bcontainerId\u0018\u0001 \u0001", "(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u0012\u0012\n\nnmHostAddr\u0018\u0002 \u0001(\t\u0012\u0014\n\fappSubmitter\u0018\u0003 \u0001(\t\u0012,\n\bresource\u0018\u0004 \u0001(\u000b2\u001a.hadoop.yarn.ResourceProto\u0012\u0017\n\u000fexpiryTimeStamp\u0018\u0005 \u0001(\u0003\u0012\u0017\n\u000bmasterKeyId\u0018\u0006 \u0001(\u0005:\u0002-1\u0012\u0014\n\frmIdentifier\u0018\u0007 \u0001(\u0003\u0012,\n\bpriority\u0018\b \u0001(\u000b2\u001a.hadoop.yarn.PriorityProto\u0012\u0014\n\fcreationTime\u0018\t \u0001(\u0003\u0012F\n\u0015logAggregationContext\u0018\n \u0001(\u000b2'.hadoop.yarn.LogAggregationContextProto\"r\n\u001eClientToAMTokenIdentifierProto\u0012<\n\fappAttemptId\u0018\u0001 \u0001(\u000b2&.hadoop.y", "arn.ApplicationAttemptIdProto\u0012\u0012\n\nclientName\u0018\u0002 \u0001(\t\"§\u0001\n\"YARNDelegationTokenIdentifierProto\u0012\r\n\u0005owner\u0018\u0001 \u0001(\t\u0012\u000f\n\u0007renewer\u0018\u0002 \u0001(\t\u0012\u0010\n\brealUser\u0018\u0003 \u0001(\t\u0012\u0011\n\tissueDate\u0018\u0004 \u0001(\u0003\u0012\u000f\n\u0007maxDate\u0018\u0005 \u0001(\u0003\u0012\u0016\n\u000esequenceNumber\u0018\u0006 \u0001(\u0005\u0012\u0013\n\u000bmasterKeyId\u0018\u0007 \u0001(\u0005B=\n\u001corg.apache.hadoop.yarn.protoB\u0017YarnSecurityTokenProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnSecurityTokenProtos.descriptor = root;
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor = YarnSecurityTokenProtos.getDescriptor().getMessageTypes().get(0);
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor, new String[] { "AppAttemptId", "NodeId", "AppSubmitter", "KeyId" });
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor = YarnSecurityTokenProtos.getDescriptor().getMessageTypes().get(1);
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor, new String[] { "AppAttemptId", "KeyId" });
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor = YarnSecurityTokenProtos.getDescriptor().getMessageTypes().get(2);
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor, new String[] { "ContainerId", "NmHostAddr", "AppSubmitter", "Resource", "ExpiryTimeStamp", "MasterKeyId", "RmIdentifier", "Priority", "CreationTime", "LogAggregationContext" });
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor = YarnSecurityTokenProtos.getDescriptor().getMessageTypes().get(3);
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor, new String[] { "AppAttemptId", "ClientName" });
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor = YarnSecurityTokenProtos.getDescriptor().getMessageTypes().get(4);
                YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor, new String[] { "Owner", "Renewer", "RealUser", "IssueDate", "MaxDate", "SequenceNumber", "MasterKeyId" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor() }, assigner);
    }
    
    public static final class NMTokenIdentifierProto extends GeneratedMessage implements NMTokenIdentifierProtoOrBuilder
    {
        private static final NMTokenIdentifierProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NMTokenIdentifierProto> PARSER;
        private int bitField0_;
        public static final int APPATTEMPTID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
        public static final int NODEID_FIELD_NUMBER = 2;
        private YarnProtos.NodeIdProto nodeId_;
        public static final int APPSUBMITTER_FIELD_NUMBER = 3;
        private Object appSubmitter_;
        public static final int KEYID_FIELD_NUMBER = 4;
        private int keyId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NMTokenIdentifierProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NMTokenIdentifierProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NMTokenIdentifierProto getDefaultInstance() {
            return NMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public NMTokenIdentifierProto getDefaultInstanceForType() {
            return NMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NMTokenIdentifierProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.NodeIdProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder2 = this.nodeId_.toBuilder();
                            }
                            this.nodeId_ = input.readMessage(YarnProtos.NodeIdProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.nodeId_);
                                this.nodeId_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.appSubmitter_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x8;
                            this.keyId_ = input.readInt32();
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
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NMTokenIdentifierProto.class, Builder.class);
        }
        
        @Override
        public Parser<NMTokenIdentifierProto> getParserForType() {
            return NMTokenIdentifierProto.PARSER;
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
        public boolean hasNodeId() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.NodeIdProto getNodeId() {
            return this.nodeId_;
        }
        
        @Override
        public YarnProtos.NodeIdProtoOrBuilder getNodeIdOrBuilder() {
            return this.nodeId_;
        }
        
        @Override
        public boolean hasAppSubmitter() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getAppSubmitter() {
            final Object ref = this.appSubmitter_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.appSubmitter_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getAppSubmitterBytes() {
            final Object ref = this.appSubmitter_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.appSubmitter_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasKeyId() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public int getKeyId() {
            return this.keyId_;
        }
        
        private void initFields() {
            this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
            this.appSubmitter_ = "";
            this.keyId_ = -1;
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
                output.writeMessage(2, this.nodeId_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getAppSubmitterBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeInt32(4, this.keyId_);
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
                size += CodedOutputStream.computeMessageSize(2, this.nodeId_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getAppSubmitterBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeInt32Size(4, this.keyId_);
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
            if (!(obj instanceof NMTokenIdentifierProto)) {
                return super.equals(obj);
            }
            final NMTokenIdentifierProto other = (NMTokenIdentifierProto)obj;
            boolean result = true;
            result = (result && this.hasAppAttemptId() == other.hasAppAttemptId());
            if (this.hasAppAttemptId()) {
                result = (result && this.getAppAttemptId().equals(other.getAppAttemptId()));
            }
            result = (result && this.hasNodeId() == other.hasNodeId());
            if (this.hasNodeId()) {
                result = (result && this.getNodeId().equals(other.getNodeId()));
            }
            result = (result && this.hasAppSubmitter() == other.hasAppSubmitter());
            if (this.hasAppSubmitter()) {
                result = (result && this.getAppSubmitter().equals(other.getAppSubmitter()));
            }
            result = (result && this.hasKeyId() == other.hasKeyId());
            if (this.hasKeyId()) {
                result = (result && this.getKeyId() == other.getKeyId());
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
            if (this.hasNodeId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getNodeId().hashCode();
            }
            if (this.hasAppSubmitter()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getAppSubmitter().hashCode();
            }
            if (this.hasKeyId()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getKeyId();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NMTokenIdentifierProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static NMTokenIdentifierProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NMTokenIdentifierProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static NMTokenIdentifierProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NMTokenIdentifierProto parseFrom(final InputStream input) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static NMTokenIdentifierProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NMTokenIdentifierProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NMTokenIdentifierProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NMTokenIdentifierProto parseFrom(final CodedInputStream input) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static NMTokenIdentifierProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NMTokenIdentifierProto prototype) {
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
            NMTokenIdentifierProto.PARSER = new AbstractParser<NMTokenIdentifierProto>() {
                @Override
                public NMTokenIdentifierProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NMTokenIdentifierProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NMTokenIdentifierProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NMTokenIdentifierProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> appAttemptIdBuilder_;
            private YarnProtos.NodeIdProto nodeId_;
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> nodeIdBuilder_;
            private Object appSubmitter_;
            private int keyId_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NMTokenIdentifierProto.class, Builder.class);
            }
            
            private Builder() {
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.appSubmitter_ = "";
                this.keyId_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.appSubmitter_ = "";
                this.keyId_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NMTokenIdentifierProto.alwaysUseFieldBuilders) {
                    this.getAppAttemptIdFieldBuilder();
                    this.getNodeIdFieldBuilder();
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
                if (this.nodeIdBuilder_ == null) {
                    this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                }
                else {
                    this.nodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                this.appSubmitter_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.keyId_ = -1;
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_NMTokenIdentifierProto_descriptor;
            }
            
            @Override
            public NMTokenIdentifierProto getDefaultInstanceForType() {
                return NMTokenIdentifierProto.getDefaultInstance();
            }
            
            @Override
            public NMTokenIdentifierProto build() {
                final NMTokenIdentifierProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NMTokenIdentifierProto buildPartial() {
                final NMTokenIdentifierProto result = new NMTokenIdentifierProto((GeneratedMessage.Builder)this);
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
                if (this.nodeIdBuilder_ == null) {
                    result.nodeId_ = this.nodeId_;
                }
                else {
                    result.nodeId_ = this.nodeIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.appSubmitter_ = this.appSubmitter_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.keyId_ = this.keyId_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NMTokenIdentifierProto) {
                    return this.mergeFrom((NMTokenIdentifierProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NMTokenIdentifierProto other) {
                if (other == NMTokenIdentifierProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAppAttemptId()) {
                    this.mergeAppAttemptId(other.getAppAttemptId());
                }
                if (other.hasNodeId()) {
                    this.mergeNodeId(other.getNodeId());
                }
                if (other.hasAppSubmitter()) {
                    this.bitField0_ |= 0x4;
                    this.appSubmitter_ = other.appSubmitter_;
                    this.onChanged();
                }
                if (other.hasKeyId()) {
                    this.setKeyId(other.getKeyId());
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
                NMTokenIdentifierProto parsedMessage = null;
                try {
                    parsedMessage = NMTokenIdentifierProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NMTokenIdentifierProto)e.getUnfinishedMessage();
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
            public boolean hasNodeId() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.NodeIdProto getNodeId() {
                if (this.nodeIdBuilder_ == null) {
                    return this.nodeId_;
                }
                return this.nodeIdBuilder_.getMessage();
            }
            
            public Builder setNodeId(final YarnProtos.NodeIdProto value) {
                if (this.nodeIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nodeId_ = value;
                    this.onChanged();
                }
                else {
                    this.nodeIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setNodeId(final YarnProtos.NodeIdProto.Builder builderForValue) {
                if (this.nodeIdBuilder_ == null) {
                    this.nodeId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nodeIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeNodeId(final YarnProtos.NodeIdProto value) {
                if (this.nodeIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.nodeId_ != YarnProtos.NodeIdProto.getDefaultInstance()) {
                        this.nodeId_ = YarnProtos.NodeIdProto.newBuilder(this.nodeId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nodeId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nodeIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearNodeId() {
                if (this.nodeIdBuilder_ == null) {
                    this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnProtos.NodeIdProto.Builder getNodeIdBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getNodeIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.NodeIdProtoOrBuilder getNodeIdOrBuilder() {
                if (this.nodeIdBuilder_ != null) {
                    return this.nodeIdBuilder_.getMessageOrBuilder();
                }
                return this.nodeId_;
            }
            
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> getNodeIdFieldBuilder() {
                if (this.nodeIdBuilder_ == null) {
                    this.nodeIdBuilder_ = new SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder>(this.nodeId_, this.getParentForChildren(), this.isClean());
                    this.nodeId_ = null;
                }
                return this.nodeIdBuilder_;
            }
            
            @Override
            public boolean hasAppSubmitter() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getAppSubmitter() {
                final Object ref = this.appSubmitter_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.appSubmitter_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getAppSubmitterBytes() {
                final Object ref = this.appSubmitter_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.appSubmitter_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setAppSubmitter(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.appSubmitter_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAppSubmitter() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.appSubmitter_ = NMTokenIdentifierProto.getDefaultInstance().getAppSubmitter();
                this.onChanged();
                return this;
            }
            
            public Builder setAppSubmitterBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.appSubmitter_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasKeyId() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public int getKeyId() {
                return this.keyId_;
            }
            
            public Builder setKeyId(final int value) {
                this.bitField0_ |= 0x8;
                this.keyId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearKeyId() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.keyId_ = -1;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class AMRMTokenIdentifierProto extends GeneratedMessage implements AMRMTokenIdentifierProtoOrBuilder
    {
        private static final AMRMTokenIdentifierProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AMRMTokenIdentifierProto> PARSER;
        private int bitField0_;
        public static final int APPATTEMPTID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
        public static final int KEYID_FIELD_NUMBER = 2;
        private int keyId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AMRMTokenIdentifierProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AMRMTokenIdentifierProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AMRMTokenIdentifierProto getDefaultInstance() {
            return AMRMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public AMRMTokenIdentifierProto getDefaultInstanceForType() {
            return AMRMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AMRMTokenIdentifierProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.keyId_ = input.readInt32();
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
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AMRMTokenIdentifierProto.class, Builder.class);
        }
        
        @Override
        public Parser<AMRMTokenIdentifierProto> getParserForType() {
            return AMRMTokenIdentifierProto.PARSER;
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
        public boolean hasKeyId() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public int getKeyId() {
            return this.keyId_;
        }
        
        private void initFields() {
            this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.keyId_ = -1;
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
                output.writeInt32(2, this.keyId_);
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
                size += CodedOutputStream.computeInt32Size(2, this.keyId_);
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
            if (!(obj instanceof AMRMTokenIdentifierProto)) {
                return super.equals(obj);
            }
            final AMRMTokenIdentifierProto other = (AMRMTokenIdentifierProto)obj;
            boolean result = true;
            result = (result && this.hasAppAttemptId() == other.hasAppAttemptId());
            if (this.hasAppAttemptId()) {
                result = (result && this.getAppAttemptId().equals(other.getAppAttemptId()));
            }
            result = (result && this.hasKeyId() == other.hasKeyId());
            if (this.hasKeyId()) {
                result = (result && this.getKeyId() == other.getKeyId());
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
            if (this.hasKeyId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getKeyId();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final InputStream input) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AMRMTokenIdentifierProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AMRMTokenIdentifierProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final CodedInputStream input) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static AMRMTokenIdentifierProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AMRMTokenIdentifierProto prototype) {
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
            AMRMTokenIdentifierProto.PARSER = new AbstractParser<AMRMTokenIdentifierProto>() {
                @Override
                public AMRMTokenIdentifierProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AMRMTokenIdentifierProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AMRMTokenIdentifierProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AMRMTokenIdentifierProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> appAttemptIdBuilder_;
            private int keyId_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AMRMTokenIdentifierProto.class, Builder.class);
            }
            
            private Builder() {
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.keyId_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.keyId_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AMRMTokenIdentifierProto.alwaysUseFieldBuilders) {
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
                this.keyId_ = -1;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_AMRMTokenIdentifierProto_descriptor;
            }
            
            @Override
            public AMRMTokenIdentifierProto getDefaultInstanceForType() {
                return AMRMTokenIdentifierProto.getDefaultInstance();
            }
            
            @Override
            public AMRMTokenIdentifierProto build() {
                final AMRMTokenIdentifierProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AMRMTokenIdentifierProto buildPartial() {
                final AMRMTokenIdentifierProto result = new AMRMTokenIdentifierProto((GeneratedMessage.Builder)this);
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
                result.keyId_ = this.keyId_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AMRMTokenIdentifierProto) {
                    return this.mergeFrom((AMRMTokenIdentifierProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AMRMTokenIdentifierProto other) {
                if (other == AMRMTokenIdentifierProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAppAttemptId()) {
                    this.mergeAppAttemptId(other.getAppAttemptId());
                }
                if (other.hasKeyId()) {
                    this.setKeyId(other.getKeyId());
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
                AMRMTokenIdentifierProto parsedMessage = null;
                try {
                    parsedMessage = AMRMTokenIdentifierProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AMRMTokenIdentifierProto)e.getUnfinishedMessage();
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
            public boolean hasKeyId() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public int getKeyId() {
                return this.keyId_;
            }
            
            public Builder setKeyId(final int value) {
                this.bitField0_ |= 0x2;
                this.keyId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearKeyId() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.keyId_ = -1;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ContainerTokenIdentifierProto extends GeneratedMessage implements ContainerTokenIdentifierProtoOrBuilder
    {
        private static final ContainerTokenIdentifierProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ContainerTokenIdentifierProto> PARSER;
        private int bitField0_;
        public static final int CONTAINERID_FIELD_NUMBER = 1;
        private YarnProtos.ContainerIdProto containerId_;
        public static final int NMHOSTADDR_FIELD_NUMBER = 2;
        private Object nmHostAddr_;
        public static final int APPSUBMITTER_FIELD_NUMBER = 3;
        private Object appSubmitter_;
        public static final int RESOURCE_FIELD_NUMBER = 4;
        private YarnProtos.ResourceProto resource_;
        public static final int EXPIRYTIMESTAMP_FIELD_NUMBER = 5;
        private long expiryTimeStamp_;
        public static final int MASTERKEYID_FIELD_NUMBER = 6;
        private int masterKeyId_;
        public static final int RMIDENTIFIER_FIELD_NUMBER = 7;
        private long rmIdentifier_;
        public static final int PRIORITY_FIELD_NUMBER = 8;
        private YarnProtos.PriorityProto priority_;
        public static final int CREATIONTIME_FIELD_NUMBER = 9;
        private long creationTime_;
        public static final int LOGAGGREGATIONCONTEXT_FIELD_NUMBER = 10;
        private YarnProtos.LogAggregationContextProto logAggregationContext_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ContainerTokenIdentifierProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ContainerTokenIdentifierProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ContainerTokenIdentifierProto getDefaultInstance() {
            return ContainerTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public ContainerTokenIdentifierProto getDefaultInstanceForType() {
            return ContainerTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ContainerTokenIdentifierProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.ContainerIdProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.containerId_.toBuilder();
                            }
                            this.containerId_ = input.readMessage(YarnProtos.ContainerIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.containerId_);
                                this.containerId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.nmHostAddr_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.appSubmitter_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            YarnProtos.ResourceProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder2 = this.resource_.toBuilder();
                            }
                            this.resource_ = input.readMessage(YarnProtos.ResourceProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.resource_);
                                this.resource_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x10;
                            this.expiryTimeStamp_ = input.readInt64();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.masterKeyId_ = input.readInt32();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.rmIdentifier_ = input.readInt64();
                            continue;
                        }
                        case 66: {
                            YarnProtos.PriorityProto.Builder subBuilder3 = null;
                            if ((this.bitField0_ & 0x80) == 0x80) {
                                subBuilder3 = this.priority_.toBuilder();
                            }
                            this.priority_ = input.readMessage(YarnProtos.PriorityProto.PARSER, extensionRegistry);
                            if (subBuilder3 != null) {
                                subBuilder3.mergeFrom(this.priority_);
                                this.priority_ = subBuilder3.buildPartial();
                            }
                            this.bitField0_ |= 0x80;
                            continue;
                        }
                        case 72: {
                            this.bitField0_ |= 0x100;
                            this.creationTime_ = input.readInt64();
                            continue;
                        }
                        case 82: {
                            YarnProtos.LogAggregationContextProto.Builder subBuilder4 = null;
                            if ((this.bitField0_ & 0x200) == 0x200) {
                                subBuilder4 = this.logAggregationContext_.toBuilder();
                            }
                            this.logAggregationContext_ = input.readMessage(YarnProtos.LogAggregationContextProto.PARSER, extensionRegistry);
                            if (subBuilder4 != null) {
                                subBuilder4.mergeFrom(this.logAggregationContext_);
                                this.logAggregationContext_ = subBuilder4.buildPartial();
                            }
                            this.bitField0_ |= 0x200;
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
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerTokenIdentifierProto.class, Builder.class);
        }
        
        @Override
        public Parser<ContainerTokenIdentifierProto> getParserForType() {
            return ContainerTokenIdentifierProto.PARSER;
        }
        
        @Override
        public boolean hasContainerId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ContainerIdProto getContainerId() {
            return this.containerId_;
        }
        
        @Override
        public YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder() {
            return this.containerId_;
        }
        
        @Override
        public boolean hasNmHostAddr() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getNmHostAddr() {
            final Object ref = this.nmHostAddr_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.nmHostAddr_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getNmHostAddrBytes() {
            final Object ref = this.nmHostAddr_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.nmHostAddr_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasAppSubmitter() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getAppSubmitter() {
            final Object ref = this.appSubmitter_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.appSubmitter_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getAppSubmitterBytes() {
            final Object ref = this.appSubmitter_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.appSubmitter_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasResource() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public YarnProtos.ResourceProto getResource() {
            return this.resource_;
        }
        
        @Override
        public YarnProtos.ResourceProtoOrBuilder getResourceOrBuilder() {
            return this.resource_;
        }
        
        @Override
        public boolean hasExpiryTimeStamp() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public long getExpiryTimeStamp() {
            return this.expiryTimeStamp_;
        }
        
        @Override
        public boolean hasMasterKeyId() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public int getMasterKeyId() {
            return this.masterKeyId_;
        }
        
        @Override
        public boolean hasRmIdentifier() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getRmIdentifier() {
            return this.rmIdentifier_;
        }
        
        @Override
        public boolean hasPriority() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public YarnProtos.PriorityProto getPriority() {
            return this.priority_;
        }
        
        @Override
        public YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder() {
            return this.priority_;
        }
        
        @Override
        public boolean hasCreationTime() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public long getCreationTime() {
            return this.creationTime_;
        }
        
        @Override
        public boolean hasLogAggregationContext() {
            return (this.bitField0_ & 0x200) == 0x200;
        }
        
        @Override
        public YarnProtos.LogAggregationContextProto getLogAggregationContext() {
            return this.logAggregationContext_;
        }
        
        @Override
        public YarnProtos.LogAggregationContextProtoOrBuilder getLogAggregationContextOrBuilder() {
            return this.logAggregationContext_;
        }
        
        private void initFields() {
            this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.nmHostAddr_ = "";
            this.appSubmitter_ = "";
            this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
            this.expiryTimeStamp_ = 0L;
            this.masterKeyId_ = -1;
            this.rmIdentifier_ = 0L;
            this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
            this.creationTime_ = 0L;
            this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.getDefaultInstance();
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
                output.writeMessage(1, this.containerId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getNmHostAddrBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getAppSubmitterBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.resource_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeInt64(5, this.expiryTimeStamp_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt32(6, this.masterKeyId_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.rmIdentifier_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeMessage(8, this.priority_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeInt64(9, this.creationTime_);
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                output.writeMessage(10, this.logAggregationContext_);
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
                size += CodedOutputStream.computeMessageSize(1, this.containerId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getNmHostAddrBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getAppSubmitterBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.resource_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeInt64Size(5, this.expiryTimeStamp_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt32Size(6, this.masterKeyId_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.rmIdentifier_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeMessageSize(8, this.priority_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeInt64Size(9, this.creationTime_);
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                size += CodedOutputStream.computeMessageSize(10, this.logAggregationContext_);
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
            if (!(obj instanceof ContainerTokenIdentifierProto)) {
                return super.equals(obj);
            }
            final ContainerTokenIdentifierProto other = (ContainerTokenIdentifierProto)obj;
            boolean result = true;
            result = (result && this.hasContainerId() == other.hasContainerId());
            if (this.hasContainerId()) {
                result = (result && this.getContainerId().equals(other.getContainerId()));
            }
            result = (result && this.hasNmHostAddr() == other.hasNmHostAddr());
            if (this.hasNmHostAddr()) {
                result = (result && this.getNmHostAddr().equals(other.getNmHostAddr()));
            }
            result = (result && this.hasAppSubmitter() == other.hasAppSubmitter());
            if (this.hasAppSubmitter()) {
                result = (result && this.getAppSubmitter().equals(other.getAppSubmitter()));
            }
            result = (result && this.hasResource() == other.hasResource());
            if (this.hasResource()) {
                result = (result && this.getResource().equals(other.getResource()));
            }
            result = (result && this.hasExpiryTimeStamp() == other.hasExpiryTimeStamp());
            if (this.hasExpiryTimeStamp()) {
                result = (result && this.getExpiryTimeStamp() == other.getExpiryTimeStamp());
            }
            result = (result && this.hasMasterKeyId() == other.hasMasterKeyId());
            if (this.hasMasterKeyId()) {
                result = (result && this.getMasterKeyId() == other.getMasterKeyId());
            }
            result = (result && this.hasRmIdentifier() == other.hasRmIdentifier());
            if (this.hasRmIdentifier()) {
                result = (result && this.getRmIdentifier() == other.getRmIdentifier());
            }
            result = (result && this.hasPriority() == other.hasPriority());
            if (this.hasPriority()) {
                result = (result && this.getPriority().equals(other.getPriority()));
            }
            result = (result && this.hasCreationTime() == other.hasCreationTime());
            if (this.hasCreationTime()) {
                result = (result && this.getCreationTime() == other.getCreationTime());
            }
            result = (result && this.hasLogAggregationContext() == other.hasLogAggregationContext());
            if (this.hasLogAggregationContext()) {
                result = (result && this.getLogAggregationContext().equals(other.getLogAggregationContext()));
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
            if (this.hasContainerId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getContainerId().hashCode();
            }
            if (this.hasNmHostAddr()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getNmHostAddr().hashCode();
            }
            if (this.hasAppSubmitter()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getAppSubmitter().hashCode();
            }
            if (this.hasResource()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getResource().hashCode();
            }
            if (this.hasExpiryTimeStamp()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashLong(this.getExpiryTimeStamp());
            }
            if (this.hasMasterKeyId()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getMasterKeyId();
            }
            if (this.hasRmIdentifier()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getRmIdentifier());
            }
            if (this.hasPriority()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + this.getPriority().hashCode();
            }
            if (this.hasCreationTime()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + AbstractMessage.hashLong(this.getCreationTime());
            }
            if (this.hasLogAggregationContext()) {
                hash = 37 * hash + 10;
                hash = 53 * hash + this.getLogAggregationContext().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final InputStream input) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ContainerTokenIdentifierProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ContainerTokenIdentifierProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final CodedInputStream input) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static ContainerTokenIdentifierProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ContainerTokenIdentifierProto prototype) {
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
            ContainerTokenIdentifierProto.PARSER = new AbstractParser<ContainerTokenIdentifierProto>() {
                @Override
                public ContainerTokenIdentifierProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ContainerTokenIdentifierProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ContainerTokenIdentifierProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ContainerTokenIdentifierProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ContainerIdProto containerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containerIdBuilder_;
            private Object nmHostAddr_;
            private Object appSubmitter_;
            private YarnProtos.ResourceProto resource_;
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> resourceBuilder_;
            private long expiryTimeStamp_;
            private int masterKeyId_;
            private long rmIdentifier_;
            private YarnProtos.PriorityProto priority_;
            private SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder> priorityBuilder_;
            private long creationTime_;
            private YarnProtos.LogAggregationContextProto logAggregationContext_;
            private SingleFieldBuilder<YarnProtos.LogAggregationContextProto, YarnProtos.LogAggregationContextProto.Builder, YarnProtos.LogAggregationContextProtoOrBuilder> logAggregationContextBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerTokenIdentifierProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.nmHostAddr_ = "";
                this.appSubmitter_ = "";
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.masterKeyId_ = -1;
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.nmHostAddr_ = "";
                this.appSubmitter_ = "";
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.masterKeyId_ = -1;
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ContainerTokenIdentifierProto.alwaysUseFieldBuilders) {
                    this.getContainerIdFieldBuilder();
                    this.getResourceFieldBuilder();
                    this.getPriorityFieldBuilder();
                    this.getLogAggregationContextFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.containerIdBuilder_ == null) {
                    this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                }
                else {
                    this.containerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.nmHostAddr_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.appSubmitter_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.resourceBuilder_ == null) {
                    this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                }
                else {
                    this.resourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.expiryTimeStamp_ = 0L;
                this.bitField0_ &= 0xFFFFFFEF;
                this.masterKeyId_ = -1;
                this.bitField0_ &= 0xFFFFFFDF;
                this.rmIdentifier_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                if (this.priorityBuilder_ == null) {
                    this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                }
                else {
                    this.priorityBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFF7F;
                this.creationTime_ = 0L;
                this.bitField0_ &= 0xFFFFFEFF;
                if (this.logAggregationContextBuilder_ == null) {
                    this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.getDefaultInstance();
                }
                else {
                    this.logAggregationContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFDFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ContainerTokenIdentifierProto_descriptor;
            }
            
            @Override
            public ContainerTokenIdentifierProto getDefaultInstanceForType() {
                return ContainerTokenIdentifierProto.getDefaultInstance();
            }
            
            @Override
            public ContainerTokenIdentifierProto build() {
                final ContainerTokenIdentifierProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ContainerTokenIdentifierProto buildPartial() {
                final ContainerTokenIdentifierProto result = new ContainerTokenIdentifierProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.containerIdBuilder_ == null) {
                    result.containerId_ = this.containerId_;
                }
                else {
                    result.containerId_ = this.containerIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.nmHostAddr_ = this.nmHostAddr_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.appSubmitter_ = this.appSubmitter_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                if (this.resourceBuilder_ == null) {
                    result.resource_ = this.resource_;
                }
                else {
                    result.resource_ = this.resourceBuilder_.build();
                }
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.expiryTimeStamp_ = this.expiryTimeStamp_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.masterKeyId_ = this.masterKeyId_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.rmIdentifier_ = this.rmIdentifier_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                if (this.priorityBuilder_ == null) {
                    result.priority_ = this.priority_;
                }
                else {
                    result.priority_ = this.priorityBuilder_.build();
                }
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.creationTime_ = this.creationTime_;
                if ((from_bitField0_ & 0x200) == 0x200) {
                    to_bitField0_ |= 0x200;
                }
                if (this.logAggregationContextBuilder_ == null) {
                    result.logAggregationContext_ = this.logAggregationContext_;
                }
                else {
                    result.logAggregationContext_ = this.logAggregationContextBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ContainerTokenIdentifierProto) {
                    return this.mergeFrom((ContainerTokenIdentifierProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ContainerTokenIdentifierProto other) {
                if (other == ContainerTokenIdentifierProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerId()) {
                    this.mergeContainerId(other.getContainerId());
                }
                if (other.hasNmHostAddr()) {
                    this.bitField0_ |= 0x2;
                    this.nmHostAddr_ = other.nmHostAddr_;
                    this.onChanged();
                }
                if (other.hasAppSubmitter()) {
                    this.bitField0_ |= 0x4;
                    this.appSubmitter_ = other.appSubmitter_;
                    this.onChanged();
                }
                if (other.hasResource()) {
                    this.mergeResource(other.getResource());
                }
                if (other.hasExpiryTimeStamp()) {
                    this.setExpiryTimeStamp(other.getExpiryTimeStamp());
                }
                if (other.hasMasterKeyId()) {
                    this.setMasterKeyId(other.getMasterKeyId());
                }
                if (other.hasRmIdentifier()) {
                    this.setRmIdentifier(other.getRmIdentifier());
                }
                if (other.hasPriority()) {
                    this.mergePriority(other.getPriority());
                }
                if (other.hasCreationTime()) {
                    this.setCreationTime(other.getCreationTime());
                }
                if (other.hasLogAggregationContext()) {
                    this.mergeLogAggregationContext(other.getLogAggregationContext());
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
                ContainerTokenIdentifierProto parsedMessage = null;
                try {
                    parsedMessage = ContainerTokenIdentifierProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ContainerTokenIdentifierProto)e.getUnfinishedMessage();
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
            public boolean hasContainerId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ContainerIdProto getContainerId() {
                if (this.containerIdBuilder_ == null) {
                    return this.containerId_;
                }
                return this.containerIdBuilder_.getMessage();
            }
            
            public Builder setContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.containerIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.containerId_ = value;
                    this.onChanged();
                }
                else {
                    this.containerIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setContainerId(final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containerIdBuilder_ == null) {
                    this.containerId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.containerIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.containerIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.containerId_ != YarnProtos.ContainerIdProto.getDefaultInstance()) {
                        this.containerId_ = YarnProtos.ContainerIdProto.newBuilder(this.containerId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.containerId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.containerIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearContainerId() {
                if (this.containerIdBuilder_ == null) {
                    this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.containerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ContainerIdProto.Builder getContainerIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getContainerIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder() {
                if (this.containerIdBuilder_ != null) {
                    return this.containerIdBuilder_.getMessageOrBuilder();
                }
                return this.containerId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> getContainerIdFieldBuilder() {
                if (this.containerIdBuilder_ == null) {
                    this.containerIdBuilder_ = new SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder>(this.containerId_, this.getParentForChildren(), this.isClean());
                    this.containerId_ = null;
                }
                return this.containerIdBuilder_;
            }
            
            @Override
            public boolean hasNmHostAddr() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getNmHostAddr() {
                final Object ref = this.nmHostAddr_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.nmHostAddr_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getNmHostAddrBytes() {
                final Object ref = this.nmHostAddr_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.nmHostAddr_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setNmHostAddr(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.nmHostAddr_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNmHostAddr() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.nmHostAddr_ = ContainerTokenIdentifierProto.getDefaultInstance().getNmHostAddr();
                this.onChanged();
                return this;
            }
            
            public Builder setNmHostAddrBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.nmHostAddr_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasAppSubmitter() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getAppSubmitter() {
                final Object ref = this.appSubmitter_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.appSubmitter_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getAppSubmitterBytes() {
                final Object ref = this.appSubmitter_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.appSubmitter_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setAppSubmitter(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.appSubmitter_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAppSubmitter() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.appSubmitter_ = ContainerTokenIdentifierProto.getDefaultInstance().getAppSubmitter();
                this.onChanged();
                return this;
            }
            
            public Builder setAppSubmitterBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.appSubmitter_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasResource() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public YarnProtos.ResourceProto getResource() {
                if (this.resourceBuilder_ == null) {
                    return this.resource_;
                }
                return this.resourceBuilder_.getMessage();
            }
            
            public Builder setResource(final YarnProtos.ResourceProto value) {
                if (this.resourceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.resource_ = value;
                    this.onChanged();
                }
                else {
                    this.resourceBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder setResource(final YarnProtos.ResourceProto.Builder builderForValue) {
                if (this.resourceBuilder_ == null) {
                    this.resource_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.resourceBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergeResource(final YarnProtos.ResourceProto value) {
                if (this.resourceBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.resource_ != YarnProtos.ResourceProto.getDefaultInstance()) {
                        this.resource_ = YarnProtos.ResourceProto.newBuilder(this.resource_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.resource_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.resourceBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder clearResource() {
                if (this.resourceBuilder_ == null) {
                    this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.resourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public YarnProtos.ResourceProto.Builder getResourceBuilder() {
                this.bitField0_ |= 0x8;
                this.onChanged();
                return this.getResourceFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ResourceProtoOrBuilder getResourceOrBuilder() {
                if (this.resourceBuilder_ != null) {
                    return this.resourceBuilder_.getMessageOrBuilder();
                }
                return this.resource_;
            }
            
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> getResourceFieldBuilder() {
                if (this.resourceBuilder_ == null) {
                    this.resourceBuilder_ = new SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder>(this.resource_, this.getParentForChildren(), this.isClean());
                    this.resource_ = null;
                }
                return this.resourceBuilder_;
            }
            
            @Override
            public boolean hasExpiryTimeStamp() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public long getExpiryTimeStamp() {
                return this.expiryTimeStamp_;
            }
            
            public Builder setExpiryTimeStamp(final long value) {
                this.bitField0_ |= 0x10;
                this.expiryTimeStamp_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearExpiryTimeStamp() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.expiryTimeStamp_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMasterKeyId() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public int getMasterKeyId() {
                return this.masterKeyId_;
            }
            
            public Builder setMasterKeyId(final int value) {
                this.bitField0_ |= 0x20;
                this.masterKeyId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMasterKeyId() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.masterKeyId_ = -1;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRmIdentifier() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getRmIdentifier() {
                return this.rmIdentifier_;
            }
            
            public Builder setRmIdentifier(final long value) {
                this.bitField0_ |= 0x40;
                this.rmIdentifier_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRmIdentifier() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.rmIdentifier_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasPriority() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public YarnProtos.PriorityProto getPriority() {
                if (this.priorityBuilder_ == null) {
                    return this.priority_;
                }
                return this.priorityBuilder_.getMessage();
            }
            
            public Builder setPriority(final YarnProtos.PriorityProto value) {
                if (this.priorityBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.priority_ = value;
                    this.onChanged();
                }
                else {
                    this.priorityBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder setPriority(final YarnProtos.PriorityProto.Builder builderForValue) {
                if (this.priorityBuilder_ == null) {
                    this.priority_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.priorityBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder mergePriority(final YarnProtos.PriorityProto value) {
                if (this.priorityBuilder_ == null) {
                    if ((this.bitField0_ & 0x80) == 0x80 && this.priority_ != YarnProtos.PriorityProto.getDefaultInstance()) {
                        this.priority_ = YarnProtos.PriorityProto.newBuilder(this.priority_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.priority_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.priorityBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder clearPriority() {
                if (this.priorityBuilder_ == null) {
                    this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.priorityBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFF7F;
                return this;
            }
            
            public YarnProtos.PriorityProto.Builder getPriorityBuilder() {
                this.bitField0_ |= 0x80;
                this.onChanged();
                return this.getPriorityFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder() {
                if (this.priorityBuilder_ != null) {
                    return this.priorityBuilder_.getMessageOrBuilder();
                }
                return this.priority_;
            }
            
            private SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder> getPriorityFieldBuilder() {
                if (this.priorityBuilder_ == null) {
                    this.priorityBuilder_ = new SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder>(this.priority_, this.getParentForChildren(), this.isClean());
                    this.priority_ = null;
                }
                return this.priorityBuilder_;
            }
            
            @Override
            public boolean hasCreationTime() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public long getCreationTime() {
                return this.creationTime_;
            }
            
            public Builder setCreationTime(final long value) {
                this.bitField0_ |= 0x100;
                this.creationTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCreationTime() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.creationTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasLogAggregationContext() {
                return (this.bitField0_ & 0x200) == 0x200;
            }
            
            @Override
            public YarnProtos.LogAggregationContextProto getLogAggregationContext() {
                if (this.logAggregationContextBuilder_ == null) {
                    return this.logAggregationContext_;
                }
                return this.logAggregationContextBuilder_.getMessage();
            }
            
            public Builder setLogAggregationContext(final YarnProtos.LogAggregationContextProto value) {
                if (this.logAggregationContextBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.logAggregationContext_ = value;
                    this.onChanged();
                }
                else {
                    this.logAggregationContextBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder setLogAggregationContext(final YarnProtos.LogAggregationContextProto.Builder builderForValue) {
                if (this.logAggregationContextBuilder_ == null) {
                    this.logAggregationContext_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.logAggregationContextBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder mergeLogAggregationContext(final YarnProtos.LogAggregationContextProto value) {
                if (this.logAggregationContextBuilder_ == null) {
                    if ((this.bitField0_ & 0x200) == 0x200 && this.logAggregationContext_ != YarnProtos.LogAggregationContextProto.getDefaultInstance()) {
                        this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.newBuilder(this.logAggregationContext_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.logAggregationContext_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.logAggregationContextBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder clearLogAggregationContext() {
                if (this.logAggregationContextBuilder_ == null) {
                    this.logAggregationContext_ = YarnProtos.LogAggregationContextProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.logAggregationContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFDFF;
                return this;
            }
            
            public YarnProtos.LogAggregationContextProto.Builder getLogAggregationContextBuilder() {
                this.bitField0_ |= 0x200;
                this.onChanged();
                return this.getLogAggregationContextFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.LogAggregationContextProtoOrBuilder getLogAggregationContextOrBuilder() {
                if (this.logAggregationContextBuilder_ != null) {
                    return this.logAggregationContextBuilder_.getMessageOrBuilder();
                }
                return this.logAggregationContext_;
            }
            
            private SingleFieldBuilder<YarnProtos.LogAggregationContextProto, YarnProtos.LogAggregationContextProto.Builder, YarnProtos.LogAggregationContextProtoOrBuilder> getLogAggregationContextFieldBuilder() {
                if (this.logAggregationContextBuilder_ == null) {
                    this.logAggregationContextBuilder_ = new SingleFieldBuilder<YarnProtos.LogAggregationContextProto, YarnProtos.LogAggregationContextProto.Builder, YarnProtos.LogAggregationContextProtoOrBuilder>(this.logAggregationContext_, this.getParentForChildren(), this.isClean());
                    this.logAggregationContext_ = null;
                }
                return this.logAggregationContextBuilder_;
            }
        }
    }
    
    public static final class ClientToAMTokenIdentifierProto extends GeneratedMessage implements ClientToAMTokenIdentifierProtoOrBuilder
    {
        private static final ClientToAMTokenIdentifierProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ClientToAMTokenIdentifierProto> PARSER;
        private int bitField0_;
        public static final int APPATTEMPTID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
        public static final int CLIENTNAME_FIELD_NUMBER = 2;
        private Object clientName_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ClientToAMTokenIdentifierProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ClientToAMTokenIdentifierProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ClientToAMTokenIdentifierProto getDefaultInstance() {
            return ClientToAMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public ClientToAMTokenIdentifierProto getDefaultInstanceForType() {
            return ClientToAMTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ClientToAMTokenIdentifierProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientToAMTokenIdentifierProto.class, Builder.class);
        }
        
        @Override
        public Parser<ClientToAMTokenIdentifierProto> getParserForType() {
            return ClientToAMTokenIdentifierProto.PARSER;
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
        
        private void initFields() {
            this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.clientName_ = "";
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
            if (!(obj instanceof ClientToAMTokenIdentifierProto)) {
                return super.equals(obj);
            }
            final ClientToAMTokenIdentifierProto other = (ClientToAMTokenIdentifierProto)obj;
            boolean result = true;
            result = (result && this.hasAppAttemptId() == other.hasAppAttemptId());
            if (this.hasAppAttemptId()) {
                result = (result && this.getAppAttemptId().equals(other.getAppAttemptId()));
            }
            result = (result && this.hasClientName() == other.hasClientName());
            if (this.hasClientName()) {
                result = (result && this.getClientName().equals(other.getClientName()));
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
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final InputStream input) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ClientToAMTokenIdentifierProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final CodedInputStream input) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static ClientToAMTokenIdentifierProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ClientToAMTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ClientToAMTokenIdentifierProto prototype) {
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
            ClientToAMTokenIdentifierProto.PARSER = new AbstractParser<ClientToAMTokenIdentifierProto>() {
                @Override
                public ClientToAMTokenIdentifierProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ClientToAMTokenIdentifierProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ClientToAMTokenIdentifierProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ClientToAMTokenIdentifierProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto appAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> appAttemptIdBuilder_;
            private Object clientName_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ClientToAMTokenIdentifierProto.class, Builder.class);
            }
            
            private Builder() {
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.clientName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.appAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.clientName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ClientToAMTokenIdentifierProto.alwaysUseFieldBuilders) {
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
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_ClientToAMTokenIdentifierProto_descriptor;
            }
            
            @Override
            public ClientToAMTokenIdentifierProto getDefaultInstanceForType() {
                return ClientToAMTokenIdentifierProto.getDefaultInstance();
            }
            
            @Override
            public ClientToAMTokenIdentifierProto build() {
                final ClientToAMTokenIdentifierProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ClientToAMTokenIdentifierProto buildPartial() {
                final ClientToAMTokenIdentifierProto result = new ClientToAMTokenIdentifierProto((GeneratedMessage.Builder)this);
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
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ClientToAMTokenIdentifierProto) {
                    return this.mergeFrom((ClientToAMTokenIdentifierProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ClientToAMTokenIdentifierProto other) {
                if (other == ClientToAMTokenIdentifierProto.getDefaultInstance()) {
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
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ClientToAMTokenIdentifierProto parsedMessage = null;
                try {
                    parsedMessage = ClientToAMTokenIdentifierProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ClientToAMTokenIdentifierProto)e.getUnfinishedMessage();
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
                this.clientName_ = ClientToAMTokenIdentifierProto.getDefaultInstance().getClientName();
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
        }
    }
    
    public static final class YARNDelegationTokenIdentifierProto extends GeneratedMessage implements YARNDelegationTokenIdentifierProtoOrBuilder
    {
        private static final YARNDelegationTokenIdentifierProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<YARNDelegationTokenIdentifierProto> PARSER;
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
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private YARNDelegationTokenIdentifierProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private YARNDelegationTokenIdentifierProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static YARNDelegationTokenIdentifierProto getDefaultInstance() {
            return YARNDelegationTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public YARNDelegationTokenIdentifierProto getDefaultInstanceForType() {
            return YARNDelegationTokenIdentifierProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private YARNDelegationTokenIdentifierProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(YARNDelegationTokenIdentifierProto.class, Builder.class);
        }
        
        @Override
        public Parser<YARNDelegationTokenIdentifierProto> getParserForType() {
            return YARNDelegationTokenIdentifierProto.PARSER;
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
        
        private void initFields() {
            this.owner_ = "";
            this.renewer_ = "";
            this.realUser_ = "";
            this.issueDate_ = 0L;
            this.maxDate_ = 0L;
            this.sequenceNumber_ = 0;
            this.masterKeyId_ = 0;
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
            if (!(obj instanceof YARNDelegationTokenIdentifierProto)) {
                return super.equals(obj);
            }
            final YARNDelegationTokenIdentifierProto other = (YARNDelegationTokenIdentifierProto)obj;
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
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(data);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final InputStream input) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static YARNDelegationTokenIdentifierProto parseDelimitedFrom(final InputStream input) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static YARNDelegationTokenIdentifierProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final CodedInputStream input) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(input);
        }
        
        public static YARNDelegationTokenIdentifierProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return YARNDelegationTokenIdentifierProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final YARNDelegationTokenIdentifierProto prototype) {
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
            YARNDelegationTokenIdentifierProto.PARSER = new AbstractParser<YARNDelegationTokenIdentifierProto>() {
                @Override
                public YARNDelegationTokenIdentifierProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new YARNDelegationTokenIdentifierProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new YARNDelegationTokenIdentifierProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements YARNDelegationTokenIdentifierProtoOrBuilder
        {
            private int bitField0_;
            private Object owner_;
            private Object renewer_;
            private Object realUser_;
            private long issueDate_;
            private long maxDate_;
            private int sequenceNumber_;
            private int masterKeyId_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_fieldAccessorTable.ensureFieldAccessorsInitialized(YARNDelegationTokenIdentifierProto.class, Builder.class);
            }
            
            private Builder() {
                this.owner_ = "";
                this.renewer_ = "";
                this.realUser_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.owner_ = "";
                this.renewer_ = "";
                this.realUser_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (YARNDelegationTokenIdentifierProto.alwaysUseFieldBuilders) {}
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
                this.masterKeyId_ = 0;
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnSecurityTokenProtos.internal_static_hadoop_yarn_YARNDelegationTokenIdentifierProto_descriptor;
            }
            
            @Override
            public YARNDelegationTokenIdentifierProto getDefaultInstanceForType() {
                return YARNDelegationTokenIdentifierProto.getDefaultInstance();
            }
            
            @Override
            public YARNDelegationTokenIdentifierProto build() {
                final YARNDelegationTokenIdentifierProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public YARNDelegationTokenIdentifierProto buildPartial() {
                final YARNDelegationTokenIdentifierProto result = new YARNDelegationTokenIdentifierProto((GeneratedMessage.Builder)this);
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
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof YARNDelegationTokenIdentifierProto) {
                    return this.mergeFrom((YARNDelegationTokenIdentifierProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final YARNDelegationTokenIdentifierProto other) {
                if (other == YARNDelegationTokenIdentifierProto.getDefaultInstance()) {
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
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                YARNDelegationTokenIdentifierProto parsedMessage = null;
                try {
                    parsedMessage = YARNDelegationTokenIdentifierProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (YARNDelegationTokenIdentifierProto)e.getUnfinishedMessage();
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
                this.owner_ = YARNDelegationTokenIdentifierProto.getDefaultInstance().getOwner();
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
                this.renewer_ = YARNDelegationTokenIdentifierProto.getDefaultInstance().getRenewer();
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
                this.realUser_ = YARNDelegationTokenIdentifierProto.getDefaultInstance().getRealUser();
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
                this.masterKeyId_ = 0;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface YARNDelegationTokenIdentifierProtoOrBuilder extends MessageOrBuilder
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
    }
    
    public interface ClientToAMTokenIdentifierProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder();
        
        boolean hasClientName();
        
        String getClientName();
        
        ByteString getClientNameBytes();
    }
    
    public interface ContainerTokenIdentifierProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerId();
        
        YarnProtos.ContainerIdProto getContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder();
        
        boolean hasNmHostAddr();
        
        String getNmHostAddr();
        
        ByteString getNmHostAddrBytes();
        
        boolean hasAppSubmitter();
        
        String getAppSubmitter();
        
        ByteString getAppSubmitterBytes();
        
        boolean hasResource();
        
        YarnProtos.ResourceProto getResource();
        
        YarnProtos.ResourceProtoOrBuilder getResourceOrBuilder();
        
        boolean hasExpiryTimeStamp();
        
        long getExpiryTimeStamp();
        
        boolean hasMasterKeyId();
        
        int getMasterKeyId();
        
        boolean hasRmIdentifier();
        
        long getRmIdentifier();
        
        boolean hasPriority();
        
        YarnProtos.PriorityProto getPriority();
        
        YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder();
        
        boolean hasCreationTime();
        
        long getCreationTime();
        
        boolean hasLogAggregationContext();
        
        YarnProtos.LogAggregationContextProto getLogAggregationContext();
        
        YarnProtos.LogAggregationContextProtoOrBuilder getLogAggregationContextOrBuilder();
    }
    
    public interface AMRMTokenIdentifierProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder();
        
        boolean hasKeyId();
        
        int getKeyId();
    }
    
    public interface NMTokenIdentifierProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getAppAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getAppAttemptIdOrBuilder();
        
        boolean hasNodeId();
        
        YarnProtos.NodeIdProto getNodeId();
        
        YarnProtos.NodeIdProtoOrBuilder getNodeIdOrBuilder();
        
        boolean hasAppSubmitter();
        
        String getAppSubmitter();
        
        ByteString getAppSubmitterBytes();
        
        boolean hasKeyId();
        
        int getKeyId();
    }
}
