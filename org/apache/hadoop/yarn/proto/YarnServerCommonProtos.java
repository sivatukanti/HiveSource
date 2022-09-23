// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.AbstractMessageLite;
import java.util.Collection;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.ByteString;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
import java.util.Collections;
import java.io.IOException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import java.util.ArrayList;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.CodedInputStream;
import java.util.List;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class YarnServerCommonProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NodeStatusProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NodeStatusProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_MasterKeyProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_MasterKeyProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NodeHealthStatusProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_VersionProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_VersionProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnServerCommonProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnServerCommonProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u001fyarn_server_common_protos.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\"\u0090\u0002\n\u000fNodeStatusProto\u0012)\n\u0007node_id\u0018\u0001 \u0001(\u000b2\u0018.hadoop.yarn.NodeIdProto\u0012\u0013\n\u000bresponse_id\u0018\u0002 \u0001(\u0005\u0012=\n\u0012containersStatuses\u0018\u0003 \u0003(\u000b2!.hadoop.yarn.ContainerStatusProto\u0012<\n\u0010nodeHealthStatus\u0018\u0004 \u0001(\u000b2\".hadoop.yarn.NodeHealthStatusProto\u0012@\n\u0017keep_alive_applications\u0018\u0005 \u0003(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\"/\n\u000eMasterKeyProto\u0012\u000e\n\u0006key_id\u0018\u0001 \u0001(\u0005\u0012\r\n\u0005bytes\u0018\u0002 \u0001(\f\"h\n\u0015NodeHea", "lthStatusProto\u0012\u0017\n\u000fis_node_healthy\u0018\u0001 \u0001(\b\u0012\u0015\n\rhealth_report\u0018\u0002 \u0001(\t\u0012\u001f\n\u0017last_health_report_time\u0018\u0003 \u0001(\u0003\"<\n\fVersionProto\u0012\u0015\n\rmajor_version\u0018\u0001 \u0001(\u0005\u0012\u0015\n\rminor_version\u0018\u0002 \u0001(\u0005*7\n\u000fNodeActionProto\u0012\n\n\u0006NORMAL\u0010\u0000\u0012\n\n\u0006RESYNC\u0010\u0001\u0012\f\n\bSHUTDOWN\u0010\u0002B<\n\u001corg.apache.hadoop.yarn.protoB\u0016YarnServerCommonProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnServerCommonProtos.descriptor = root;
                YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_descriptor = YarnServerCommonProtos.getDescriptor().getMessageTypes().get(0);
                YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_descriptor, new String[] { "NodeId", "ResponseId", "ContainersStatuses", "NodeHealthStatus", "KeepAliveApplications" });
                YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_descriptor = YarnServerCommonProtos.getDescriptor().getMessageTypes().get(1);
                YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_descriptor, new String[] { "KeyId", "Bytes" });
                YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor = YarnServerCommonProtos.getDescriptor().getMessageTypes().get(2);
                YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor, new String[] { "IsNodeHealthy", "HealthReport", "LastHealthReportTime" });
                YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_descriptor = YarnServerCommonProtos.getDescriptor().getMessageTypes().get(3);
                YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_descriptor, new String[] { "MajorVersion", "MinorVersion" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor() }, assigner);
    }
    
    public enum NodeActionProto implements ProtocolMessageEnum
    {
        NORMAL(0, 0), 
        RESYNC(1, 1), 
        SHUTDOWN(2, 2);
        
        public static final int NORMAL_VALUE = 0;
        public static final int RESYNC_VALUE = 1;
        public static final int SHUTDOWN_VALUE = 2;
        private static Internal.EnumLiteMap<NodeActionProto> internalValueMap;
        private static final NodeActionProto[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static NodeActionProto valueOf(final int value) {
            switch (value) {
                case 0: {
                    return NodeActionProto.NORMAL;
                }
                case 1: {
                    return NodeActionProto.RESYNC;
                }
                case 2: {
                    return NodeActionProto.SHUTDOWN;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<NodeActionProto> internalGetValueMap() {
            return NodeActionProto.internalValueMap;
        }
        
        @Override
        public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(this.index);
        }
        
        @Override
        public final Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
        }
        
        public static final Descriptors.EnumDescriptor getDescriptor() {
            return YarnServerCommonProtos.getDescriptor().getEnumTypes().get(0);
        }
        
        public static NodeActionProto valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return NodeActionProto.VALUES[desc.getIndex()];
        }
        
        private NodeActionProto(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            NodeActionProto.internalValueMap = new Internal.EnumLiteMap<NodeActionProto>() {
                @Override
                public NodeActionProto findValueByNumber(final int number) {
                    return NodeActionProto.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public static final class NodeStatusProto extends GeneratedMessage implements NodeStatusProtoOrBuilder
    {
        private static final NodeStatusProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NodeStatusProto> PARSER;
        private int bitField0_;
        public static final int NODE_ID_FIELD_NUMBER = 1;
        private YarnProtos.NodeIdProto nodeId_;
        public static final int RESPONSE_ID_FIELD_NUMBER = 2;
        private int responseId_;
        public static final int CONTAINERSSTATUSES_FIELD_NUMBER = 3;
        private List<YarnProtos.ContainerStatusProto> containersStatuses_;
        public static final int NODEHEALTHSTATUS_FIELD_NUMBER = 4;
        private NodeHealthStatusProto nodeHealthStatus_;
        public static final int KEEP_ALIVE_APPLICATIONS_FIELD_NUMBER = 5;
        private List<YarnProtos.ApplicationIdProto> keepAliveApplications_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NodeStatusProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NodeStatusProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NodeStatusProto getDefaultInstance() {
            return NodeStatusProto.defaultInstance;
        }
        
        @Override
        public NodeStatusProto getDefaultInstanceForType() {
            return NodeStatusProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NodeStatusProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.NodeIdProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.nodeId_.toBuilder();
                            }
                            this.nodeId_ = input.readMessage(YarnProtos.NodeIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.nodeId_);
                                this.nodeId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.responseId_ = input.readInt32();
                            continue;
                        }
                        case 26: {
                            if ((mutable_bitField0_ & 0x4) != 0x4) {
                                this.containersStatuses_ = new ArrayList<YarnProtos.ContainerStatusProto>();
                                mutable_bitField0_ |= 0x4;
                            }
                            this.containersStatuses_.add(input.readMessage(YarnProtos.ContainerStatusProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 34: {
                            NodeHealthStatusProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder2 = this.nodeHealthStatus_.toBuilder();
                            }
                            this.nodeHealthStatus_ = input.readMessage(NodeHealthStatusProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.nodeHealthStatus_);
                                this.nodeHealthStatus_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 42: {
                            if ((mutable_bitField0_ & 0x10) != 0x10) {
                                this.keepAliveApplications_ = new ArrayList<YarnProtos.ApplicationIdProto>();
                                mutable_bitField0_ |= 0x10;
                            }
                            this.keepAliveApplications_.add(input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x4) == 0x4) {
                    this.containersStatuses_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerStatusProto>)this.containersStatuses_);
                }
                if ((mutable_bitField0_ & 0x10) == 0x10) {
                    this.keepAliveApplications_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.keepAliveApplications_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeStatusProto.class, Builder.class);
        }
        
        @Override
        public Parser<NodeStatusProto> getParserForType() {
            return NodeStatusProto.PARSER;
        }
        
        @Override
        public boolean hasNodeId() {
            return (this.bitField0_ & 0x1) == 0x1;
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
        public boolean hasResponseId() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public int getResponseId() {
            return this.responseId_;
        }
        
        @Override
        public List<YarnProtos.ContainerStatusProto> getContainersStatusesList() {
            return this.containersStatuses_;
        }
        
        @Override
        public List<? extends YarnProtos.ContainerStatusProtoOrBuilder> getContainersStatusesOrBuilderList() {
            return this.containersStatuses_;
        }
        
        @Override
        public int getContainersStatusesCount() {
            return this.containersStatuses_.size();
        }
        
        @Override
        public YarnProtos.ContainerStatusProto getContainersStatuses(final int index) {
            return this.containersStatuses_.get(index);
        }
        
        @Override
        public YarnProtos.ContainerStatusProtoOrBuilder getContainersStatusesOrBuilder(final int index) {
            return this.containersStatuses_.get(index);
        }
        
        @Override
        public boolean hasNodeHealthStatus() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public NodeHealthStatusProto getNodeHealthStatus() {
            return this.nodeHealthStatus_;
        }
        
        @Override
        public NodeHealthStatusProtoOrBuilder getNodeHealthStatusOrBuilder() {
            return this.nodeHealthStatus_;
        }
        
        @Override
        public List<YarnProtos.ApplicationIdProto> getKeepAliveApplicationsList() {
            return this.keepAliveApplications_;
        }
        
        @Override
        public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getKeepAliveApplicationsOrBuilderList() {
            return this.keepAliveApplications_;
        }
        
        @Override
        public int getKeepAliveApplicationsCount() {
            return this.keepAliveApplications_.size();
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getKeepAliveApplications(final int index) {
            return this.keepAliveApplications_.get(index);
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getKeepAliveApplicationsOrBuilder(final int index) {
            return this.keepAliveApplications_.get(index);
        }
        
        private void initFields() {
            this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
            this.responseId_ = 0;
            this.containersStatuses_ = Collections.emptyList();
            this.nodeHealthStatus_ = NodeHealthStatusProto.getDefaultInstance();
            this.keepAliveApplications_ = Collections.emptyList();
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
                output.writeMessage(1, this.nodeId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt32(2, this.responseId_);
            }
            for (int i = 0; i < this.containersStatuses_.size(); ++i) {
                output.writeMessage(3, this.containersStatuses_.get(i));
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(4, this.nodeHealthStatus_);
            }
            for (int i = 0; i < this.keepAliveApplications_.size(); ++i) {
                output.writeMessage(5, this.keepAliveApplications_.get(i));
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
                size += CodedOutputStream.computeMessageSize(1, this.nodeId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt32Size(2, this.responseId_);
            }
            for (int i = 0; i < this.containersStatuses_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(3, this.containersStatuses_.get(i));
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(4, this.nodeHealthStatus_);
            }
            for (int i = 0; i < this.keepAliveApplications_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(5, this.keepAliveApplications_.get(i));
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
            if (!(obj instanceof NodeStatusProto)) {
                return super.equals(obj);
            }
            final NodeStatusProto other = (NodeStatusProto)obj;
            boolean result = true;
            result = (result && this.hasNodeId() == other.hasNodeId());
            if (this.hasNodeId()) {
                result = (result && this.getNodeId().equals(other.getNodeId()));
            }
            result = (result && this.hasResponseId() == other.hasResponseId());
            if (this.hasResponseId()) {
                result = (result && this.getResponseId() == other.getResponseId());
            }
            result = (result && this.getContainersStatusesList().equals(other.getContainersStatusesList()));
            result = (result && this.hasNodeHealthStatus() == other.hasNodeHealthStatus());
            if (this.hasNodeHealthStatus()) {
                result = (result && this.getNodeHealthStatus().equals(other.getNodeHealthStatus()));
            }
            result = (result && this.getKeepAliveApplicationsList().equals(other.getKeepAliveApplicationsList()));
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
            if (this.hasNodeId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeId().hashCode();
            }
            if (this.hasResponseId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getResponseId();
            }
            if (this.getContainersStatusesCount() > 0) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getContainersStatusesList().hashCode();
            }
            if (this.hasNodeHealthStatus()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getNodeHealthStatus().hashCode();
            }
            if (this.getKeepAliveApplicationsCount() > 0) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getKeepAliveApplicationsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NodeStatusProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NodeStatusProto.PARSER.parseFrom(data);
        }
        
        public static NodeStatusProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeStatusProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NodeStatusProto.PARSER.parseFrom(data);
        }
        
        public static NodeStatusProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeStatusProto parseFrom(final InputStream input) throws IOException {
            return NodeStatusProto.PARSER.parseFrom(input);
        }
        
        public static NodeStatusProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NodeStatusProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NodeStatusProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NodeStatusProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeStatusProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NodeStatusProto parseFrom(final CodedInputStream input) throws IOException {
            return NodeStatusProto.PARSER.parseFrom(input);
        }
        
        public static NodeStatusProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NodeStatusProto prototype) {
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
            NodeStatusProto.PARSER = new AbstractParser<NodeStatusProto>() {
                @Override
                public NodeStatusProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NodeStatusProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NodeStatusProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NodeStatusProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.NodeIdProto nodeId_;
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> nodeIdBuilder_;
            private int responseId_;
            private List<YarnProtos.ContainerStatusProto> containersStatuses_;
            private RepeatedFieldBuilder<YarnProtos.ContainerStatusProto, YarnProtos.ContainerStatusProto.Builder, YarnProtos.ContainerStatusProtoOrBuilder> containersStatusesBuilder_;
            private NodeHealthStatusProto nodeHealthStatus_;
            private SingleFieldBuilder<NodeHealthStatusProto, NodeHealthStatusProto.Builder, NodeHealthStatusProtoOrBuilder> nodeHealthStatusBuilder_;
            private List<YarnProtos.ApplicationIdProto> keepAliveApplications_;
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> keepAliveApplicationsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeStatusProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.containersStatuses_ = Collections.emptyList();
                this.nodeHealthStatus_ = NodeHealthStatusProto.getDefaultInstance();
                this.keepAliveApplications_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.containersStatuses_ = Collections.emptyList();
                this.nodeHealthStatus_ = NodeHealthStatusProto.getDefaultInstance();
                this.keepAliveApplications_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NodeStatusProto.alwaysUseFieldBuilders) {
                    this.getNodeIdFieldBuilder();
                    this.getContainersStatusesFieldBuilder();
                    this.getNodeHealthStatusFieldBuilder();
                    this.getKeepAliveApplicationsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.nodeIdBuilder_ == null) {
                    this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                }
                else {
                    this.nodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.responseId_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.containersStatusesBuilder_ == null) {
                    this.containersStatuses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                }
                else {
                    this.containersStatusesBuilder_.clear();
                }
                if (this.nodeHealthStatusBuilder_ == null) {
                    this.nodeHealthStatus_ = NodeHealthStatusProto.getDefaultInstance();
                }
                else {
                    this.nodeHealthStatusBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.keepAliveApplications_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                }
                else {
                    this.keepAliveApplicationsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeStatusProto_descriptor;
            }
            
            @Override
            public NodeStatusProto getDefaultInstanceForType() {
                return NodeStatusProto.getDefaultInstance();
            }
            
            @Override
            public NodeStatusProto build() {
                final NodeStatusProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NodeStatusProto buildPartial() {
                final NodeStatusProto result = new NodeStatusProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.nodeIdBuilder_ == null) {
                    result.nodeId_ = this.nodeId_;
                }
                else {
                    result.nodeId_ = this.nodeIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.responseId_ = this.responseId_;
                if (this.containersStatusesBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4) {
                        this.containersStatuses_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerStatusProto>)this.containersStatuses_);
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    result.containersStatuses_ = this.containersStatuses_;
                }
                else {
                    result.containersStatuses_ = this.containersStatusesBuilder_.build();
                }
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x4;
                }
                if (this.nodeHealthStatusBuilder_ == null) {
                    result.nodeHealthStatus_ = this.nodeHealthStatus_;
                }
                else {
                    result.nodeHealthStatus_ = this.nodeHealthStatusBuilder_.build();
                }
                if (this.keepAliveApplicationsBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) == 0x10) {
                        this.keepAliveApplications_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.keepAliveApplications_);
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    result.keepAliveApplications_ = this.keepAliveApplications_;
                }
                else {
                    result.keepAliveApplications_ = this.keepAliveApplicationsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NodeStatusProto) {
                    return this.mergeFrom((NodeStatusProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NodeStatusProto other) {
                if (other == NodeStatusProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasNodeId()) {
                    this.mergeNodeId(other.getNodeId());
                }
                if (other.hasResponseId()) {
                    this.setResponseId(other.getResponseId());
                }
                if (this.containersStatusesBuilder_ == null) {
                    if (!other.containersStatuses_.isEmpty()) {
                        if (this.containersStatuses_.isEmpty()) {
                            this.containersStatuses_ = other.containersStatuses_;
                            this.bitField0_ &= 0xFFFFFFFB;
                        }
                        else {
                            this.ensureContainersStatusesIsMutable();
                            this.containersStatuses_.addAll(other.containersStatuses_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.containersStatuses_.isEmpty()) {
                    if (this.containersStatusesBuilder_.isEmpty()) {
                        this.containersStatusesBuilder_.dispose();
                        this.containersStatusesBuilder_ = null;
                        this.containersStatuses_ = other.containersStatuses_;
                        this.bitField0_ &= 0xFFFFFFFB;
                        this.containersStatusesBuilder_ = (NodeStatusProto.alwaysUseFieldBuilders ? this.getContainersStatusesFieldBuilder() : null);
                    }
                    else {
                        this.containersStatusesBuilder_.addAllMessages(other.containersStatuses_);
                    }
                }
                if (other.hasNodeHealthStatus()) {
                    this.mergeNodeHealthStatus(other.getNodeHealthStatus());
                }
                if (this.keepAliveApplicationsBuilder_ == null) {
                    if (!other.keepAliveApplications_.isEmpty()) {
                        if (this.keepAliveApplications_.isEmpty()) {
                            this.keepAliveApplications_ = other.keepAliveApplications_;
                            this.bitField0_ &= 0xFFFFFFEF;
                        }
                        else {
                            this.ensureKeepAliveApplicationsIsMutable();
                            this.keepAliveApplications_.addAll(other.keepAliveApplications_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.keepAliveApplications_.isEmpty()) {
                    if (this.keepAliveApplicationsBuilder_.isEmpty()) {
                        this.keepAliveApplicationsBuilder_.dispose();
                        this.keepAliveApplicationsBuilder_ = null;
                        this.keepAliveApplications_ = other.keepAliveApplications_;
                        this.bitField0_ &= 0xFFFFFFEF;
                        this.keepAliveApplicationsBuilder_ = (NodeStatusProto.alwaysUseFieldBuilders ? this.getKeepAliveApplicationsFieldBuilder() : null);
                    }
                    else {
                        this.keepAliveApplicationsBuilder_.addAllMessages(other.keepAliveApplications_);
                    }
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
                NodeStatusProto parsedMessage = null;
                try {
                    parsedMessage = NodeStatusProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NodeStatusProto)e.getUnfinishedMessage();
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
            public boolean hasNodeId() {
                return (this.bitField0_ & 0x1) == 0x1;
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
                this.bitField0_ |= 0x1;
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
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeNodeId(final YarnProtos.NodeIdProto value) {
                if (this.nodeIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.nodeId_ != YarnProtos.NodeIdProto.getDefaultInstance()) {
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
                this.bitField0_ |= 0x1;
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
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.NodeIdProto.Builder getNodeIdBuilder() {
                this.bitField0_ |= 0x1;
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
            public boolean hasResponseId() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public int getResponseId() {
                return this.responseId_;
            }
            
            public Builder setResponseId(final int value) {
                this.bitField0_ |= 0x2;
                this.responseId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearResponseId() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.responseId_ = 0;
                this.onChanged();
                return this;
            }
            
            private void ensureContainersStatusesIsMutable() {
                if ((this.bitField0_ & 0x4) != 0x4) {
                    this.containersStatuses_ = new ArrayList<YarnProtos.ContainerStatusProto>(this.containersStatuses_);
                    this.bitField0_ |= 0x4;
                }
            }
            
            @Override
            public List<YarnProtos.ContainerStatusProto> getContainersStatusesList() {
                if (this.containersStatusesBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ContainerStatusProto>)this.containersStatuses_);
                }
                return this.containersStatusesBuilder_.getMessageList();
            }
            
            @Override
            public int getContainersStatusesCount() {
                if (this.containersStatusesBuilder_ == null) {
                    return this.containersStatuses_.size();
                }
                return this.containersStatusesBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ContainerStatusProto getContainersStatuses(final int index) {
                if (this.containersStatusesBuilder_ == null) {
                    return this.containersStatuses_.get(index);
                }
                return this.containersStatusesBuilder_.getMessage(index);
            }
            
            public Builder setContainersStatuses(final int index, final YarnProtos.ContainerStatusProto value) {
                if (this.containersStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setContainersStatuses(final int index, final YarnProtos.ContainerStatusProto.Builder builderForValue) {
                if (this.containersStatusesBuilder_ == null) {
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersStatuses(final YarnProtos.ContainerStatusProto value) {
                if (this.containersStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.add(value);
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addContainersStatuses(final int index, final YarnProtos.ContainerStatusProto value) {
                if (this.containersStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addContainersStatuses(final YarnProtos.ContainerStatusProto.Builder builderForValue) {
                if (this.containersStatusesBuilder_ == null) {
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersStatuses(final int index, final YarnProtos.ContainerStatusProto.Builder builderForValue) {
                if (this.containersStatusesBuilder_ == null) {
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllContainersStatuses(final Iterable<? extends YarnProtos.ContainerStatusProto> values) {
                if (this.containersStatusesBuilder_ == null) {
                    this.ensureContainersStatusesIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.containersStatuses_);
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearContainersStatuses() {
                if (this.containersStatusesBuilder_ == null) {
                    this.containersStatuses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeContainersStatuses(final int index) {
                if (this.containersStatusesBuilder_ == null) {
                    this.ensureContainersStatusesIsMutable();
                    this.containersStatuses_.remove(index);
                    this.onChanged();
                }
                else {
                    this.containersStatusesBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ContainerStatusProto.Builder getContainersStatusesBuilder(final int index) {
                return this.getContainersStatusesFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ContainerStatusProtoOrBuilder getContainersStatusesOrBuilder(final int index) {
                if (this.containersStatusesBuilder_ == null) {
                    return this.containersStatuses_.get(index);
                }
                return this.containersStatusesBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ContainerStatusProtoOrBuilder> getContainersStatusesOrBuilderList() {
                if (this.containersStatusesBuilder_ != null) {
                    return this.containersStatusesBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ContainerStatusProtoOrBuilder>)this.containersStatuses_);
            }
            
            public YarnProtos.ContainerStatusProto.Builder addContainersStatusesBuilder() {
                return this.getContainersStatusesFieldBuilder().addBuilder(YarnProtos.ContainerStatusProto.getDefaultInstance());
            }
            
            public YarnProtos.ContainerStatusProto.Builder addContainersStatusesBuilder(final int index) {
                return this.getContainersStatusesFieldBuilder().addBuilder(index, YarnProtos.ContainerStatusProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ContainerStatusProto.Builder> getContainersStatusesBuilderList() {
                return this.getContainersStatusesFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ContainerStatusProto, YarnProtos.ContainerStatusProto.Builder, YarnProtos.ContainerStatusProtoOrBuilder> getContainersStatusesFieldBuilder() {
                if (this.containersStatusesBuilder_ == null) {
                    this.containersStatusesBuilder_ = new RepeatedFieldBuilder<YarnProtos.ContainerStatusProto, YarnProtos.ContainerStatusProto.Builder, YarnProtos.ContainerStatusProtoOrBuilder>(this.containersStatuses_, (this.bitField0_ & 0x4) == 0x4, this.getParentForChildren(), this.isClean());
                    this.containersStatuses_ = null;
                }
                return this.containersStatusesBuilder_;
            }
            
            @Override
            public boolean hasNodeHealthStatus() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public NodeHealthStatusProto getNodeHealthStatus() {
                if (this.nodeHealthStatusBuilder_ == null) {
                    return this.nodeHealthStatus_;
                }
                return this.nodeHealthStatusBuilder_.getMessage();
            }
            
            public Builder setNodeHealthStatus(final NodeHealthStatusProto value) {
                if (this.nodeHealthStatusBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nodeHealthStatus_ = value;
                    this.onChanged();
                }
                else {
                    this.nodeHealthStatusBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder setNodeHealthStatus(final NodeHealthStatusProto.Builder builderForValue) {
                if (this.nodeHealthStatusBuilder_ == null) {
                    this.nodeHealthStatus_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nodeHealthStatusBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergeNodeHealthStatus(final NodeHealthStatusProto value) {
                if (this.nodeHealthStatusBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.nodeHealthStatus_ != NodeHealthStatusProto.getDefaultInstance()) {
                        this.nodeHealthStatus_ = NodeHealthStatusProto.newBuilder(this.nodeHealthStatus_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nodeHealthStatus_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nodeHealthStatusBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder clearNodeHealthStatus() {
                if (this.nodeHealthStatusBuilder_ == null) {
                    this.nodeHealthStatus_ = NodeHealthStatusProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nodeHealthStatusBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public NodeHealthStatusProto.Builder getNodeHealthStatusBuilder() {
                this.bitField0_ |= 0x8;
                this.onChanged();
                return this.getNodeHealthStatusFieldBuilder().getBuilder();
            }
            
            @Override
            public NodeHealthStatusProtoOrBuilder getNodeHealthStatusOrBuilder() {
                if (this.nodeHealthStatusBuilder_ != null) {
                    return this.nodeHealthStatusBuilder_.getMessageOrBuilder();
                }
                return this.nodeHealthStatus_;
            }
            
            private SingleFieldBuilder<NodeHealthStatusProto, NodeHealthStatusProto.Builder, NodeHealthStatusProtoOrBuilder> getNodeHealthStatusFieldBuilder() {
                if (this.nodeHealthStatusBuilder_ == null) {
                    this.nodeHealthStatusBuilder_ = new SingleFieldBuilder<NodeHealthStatusProto, NodeHealthStatusProto.Builder, NodeHealthStatusProtoOrBuilder>(this.nodeHealthStatus_, this.getParentForChildren(), this.isClean());
                    this.nodeHealthStatus_ = null;
                }
                return this.nodeHealthStatusBuilder_;
            }
            
            private void ensureKeepAliveApplicationsIsMutable() {
                if ((this.bitField0_ & 0x10) != 0x10) {
                    this.keepAliveApplications_ = new ArrayList<YarnProtos.ApplicationIdProto>(this.keepAliveApplications_);
                    this.bitField0_ |= 0x10;
                }
            }
            
            @Override
            public List<YarnProtos.ApplicationIdProto> getKeepAliveApplicationsList() {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.keepAliveApplications_);
                }
                return this.keepAliveApplicationsBuilder_.getMessageList();
            }
            
            @Override
            public int getKeepAliveApplicationsCount() {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    return this.keepAliveApplications_.size();
                }
                return this.keepAliveApplicationsBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getKeepAliveApplications(final int index) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    return this.keepAliveApplications_.get(index);
                }
                return this.keepAliveApplicationsBuilder_.getMessage(index);
            }
            
            public Builder setKeepAliveApplications(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setKeepAliveApplications(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addKeepAliveApplications(final YarnProtos.ApplicationIdProto value) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.add(value);
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addKeepAliveApplications(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addKeepAliveApplications(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addKeepAliveApplications(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllKeepAliveApplications(final Iterable<? extends YarnProtos.ApplicationIdProto> values) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.ensureKeepAliveApplicationsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.keepAliveApplications_);
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearKeepAliveApplications() {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.keepAliveApplications_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeKeepAliveApplications(final int index) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.ensureKeepAliveApplicationsIsMutable();
                    this.keepAliveApplications_.remove(index);
                    this.onChanged();
                }
                else {
                    this.keepAliveApplicationsBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getKeepAliveApplicationsBuilder(final int index) {
                return this.getKeepAliveApplicationsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getKeepAliveApplicationsOrBuilder(final int index) {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    return this.keepAliveApplications_.get(index);
                }
                return this.keepAliveApplicationsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getKeepAliveApplicationsOrBuilderList() {
                if (this.keepAliveApplicationsBuilder_ != null) {
                    return this.keepAliveApplicationsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProtoOrBuilder>)this.keepAliveApplications_);
            }
            
            public YarnProtos.ApplicationIdProto.Builder addKeepAliveApplicationsBuilder() {
                return this.getKeepAliveApplicationsFieldBuilder().addBuilder(YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public YarnProtos.ApplicationIdProto.Builder addKeepAliveApplicationsBuilder(final int index) {
                return this.getKeepAliveApplicationsFieldBuilder().addBuilder(index, YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ApplicationIdProto.Builder> getKeepAliveApplicationsBuilderList() {
                return this.getKeepAliveApplicationsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getKeepAliveApplicationsFieldBuilder() {
                if (this.keepAliveApplicationsBuilder_ == null) {
                    this.keepAliveApplicationsBuilder_ = new RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.keepAliveApplications_, (this.bitField0_ & 0x10) == 0x10, this.getParentForChildren(), this.isClean());
                    this.keepAliveApplications_ = null;
                }
                return this.keepAliveApplicationsBuilder_;
            }
        }
    }
    
    public static final class MasterKeyProto extends GeneratedMessage implements MasterKeyProtoOrBuilder
    {
        private static final MasterKeyProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MasterKeyProto> PARSER;
        private int bitField0_;
        public static final int KEY_ID_FIELD_NUMBER = 1;
        private int keyId_;
        public static final int BYTES_FIELD_NUMBER = 2;
        private ByteString bytes_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private MasterKeyProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MasterKeyProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MasterKeyProto getDefaultInstance() {
            return MasterKeyProto.defaultInstance;
        }
        
        @Override
        public MasterKeyProto getDefaultInstanceForType() {
            return MasterKeyProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MasterKeyProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.keyId_ = input.readInt32();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.bytes_ = input.readBytes();
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
            return YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MasterKeyProto.class, Builder.class);
        }
        
        @Override
        public Parser<MasterKeyProto> getParserForType() {
            return MasterKeyProto.PARSER;
        }
        
        @Override
        public boolean hasKeyId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getKeyId() {
            return this.keyId_;
        }
        
        @Override
        public boolean hasBytes() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public ByteString getBytes() {
            return this.bytes_;
        }
        
        private void initFields() {
            this.keyId_ = 0;
            this.bytes_ = ByteString.EMPTY;
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
                output.writeInt32(1, this.keyId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.bytes_);
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
                size += CodedOutputStream.computeInt32Size(1, this.keyId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.bytes_);
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
            if (!(obj instanceof MasterKeyProto)) {
                return super.equals(obj);
            }
            final MasterKeyProto other = (MasterKeyProto)obj;
            boolean result = true;
            result = (result && this.hasKeyId() == other.hasKeyId());
            if (this.hasKeyId()) {
                result = (result && this.getKeyId() == other.getKeyId());
            }
            result = (result && this.hasBytes() == other.hasBytes());
            if (this.hasBytes()) {
                result = (result && this.getBytes().equals(other.getBytes()));
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
            if (this.hasKeyId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getKeyId();
            }
            if (this.hasBytes()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getBytes().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static MasterKeyProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MasterKeyProto.PARSER.parseFrom(data);
        }
        
        public static MasterKeyProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MasterKeyProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MasterKeyProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MasterKeyProto.PARSER.parseFrom(data);
        }
        
        public static MasterKeyProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MasterKeyProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MasterKeyProto parseFrom(final InputStream input) throws IOException {
            return MasterKeyProto.PARSER.parseFrom(input);
        }
        
        public static MasterKeyProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MasterKeyProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MasterKeyProto parseDelimitedFrom(final InputStream input) throws IOException {
            return MasterKeyProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static MasterKeyProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MasterKeyProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MasterKeyProto parseFrom(final CodedInputStream input) throws IOException {
            return MasterKeyProto.PARSER.parseFrom(input);
        }
        
        public static MasterKeyProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MasterKeyProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MasterKeyProto prototype) {
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
            MasterKeyProto.PARSER = new AbstractParser<MasterKeyProto>() {
                @Override
                public MasterKeyProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MasterKeyProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new MasterKeyProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements MasterKeyProtoOrBuilder
        {
            private int bitField0_;
            private int keyId_;
            private ByteString bytes_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MasterKeyProto.class, Builder.class);
            }
            
            private Builder() {
                this.bytes_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.bytes_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (MasterKeyProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.keyId_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.bytes_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_MasterKeyProto_descriptor;
            }
            
            @Override
            public MasterKeyProto getDefaultInstanceForType() {
                return MasterKeyProto.getDefaultInstance();
            }
            
            @Override
            public MasterKeyProto build() {
                final MasterKeyProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public MasterKeyProto buildPartial() {
                final MasterKeyProto result = new MasterKeyProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.keyId_ = this.keyId_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.bytes_ = this.bytes_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MasterKeyProto) {
                    return this.mergeFrom((MasterKeyProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MasterKeyProto other) {
                if (other == MasterKeyProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasKeyId()) {
                    this.setKeyId(other.getKeyId());
                }
                if (other.hasBytes()) {
                    this.setBytes(other.getBytes());
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
                MasterKeyProto parsedMessage = null;
                try {
                    parsedMessage = MasterKeyProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MasterKeyProto)e.getUnfinishedMessage();
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
            public boolean hasKeyId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getKeyId() {
                return this.keyId_;
            }
            
            public Builder setKeyId(final int value) {
                this.bitField0_ |= 0x1;
                this.keyId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearKeyId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.keyId_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasBytes() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public ByteString getBytes() {
                return this.bytes_;
            }
            
            public Builder setBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.bytes_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearBytes() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.bytes_ = MasterKeyProto.getDefaultInstance().getBytes();
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class NodeHealthStatusProto extends GeneratedMessage implements NodeHealthStatusProtoOrBuilder
    {
        private static final NodeHealthStatusProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NodeHealthStatusProto> PARSER;
        private int bitField0_;
        public static final int IS_NODE_HEALTHY_FIELD_NUMBER = 1;
        private boolean isNodeHealthy_;
        public static final int HEALTH_REPORT_FIELD_NUMBER = 2;
        private Object healthReport_;
        public static final int LAST_HEALTH_REPORT_TIME_FIELD_NUMBER = 3;
        private long lastHealthReportTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NodeHealthStatusProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NodeHealthStatusProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NodeHealthStatusProto getDefaultInstance() {
            return NodeHealthStatusProto.defaultInstance;
        }
        
        @Override
        public NodeHealthStatusProto getDefaultInstanceForType() {
            return NodeHealthStatusProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NodeHealthStatusProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.isNodeHealthy_ = input.readBool();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.healthReport_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.lastHealthReportTime_ = input.readInt64();
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
            return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHealthStatusProto.class, Builder.class);
        }
        
        @Override
        public Parser<NodeHealthStatusProto> getParserForType() {
            return NodeHealthStatusProto.PARSER;
        }
        
        @Override
        public boolean hasIsNodeHealthy() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public boolean getIsNodeHealthy() {
            return this.isNodeHealthy_;
        }
        
        @Override
        public boolean hasHealthReport() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getHealthReport() {
            final Object ref = this.healthReport_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.healthReport_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getHealthReportBytes() {
            final Object ref = this.healthReport_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.healthReport_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasLastHealthReportTime() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public long getLastHealthReportTime() {
            return this.lastHealthReportTime_;
        }
        
        private void initFields() {
            this.isNodeHealthy_ = false;
            this.healthReport_ = "";
            this.lastHealthReportTime_ = 0L;
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
                output.writeBool(1, this.isNodeHealthy_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getHealthReportBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeInt64(3, this.lastHealthReportTime_);
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
                size += CodedOutputStream.computeBoolSize(1, this.isNodeHealthy_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getHealthReportBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeInt64Size(3, this.lastHealthReportTime_);
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
            if (!(obj instanceof NodeHealthStatusProto)) {
                return super.equals(obj);
            }
            final NodeHealthStatusProto other = (NodeHealthStatusProto)obj;
            boolean result = true;
            result = (result && this.hasIsNodeHealthy() == other.hasIsNodeHealthy());
            if (this.hasIsNodeHealthy()) {
                result = (result && this.getIsNodeHealthy() == other.getIsNodeHealthy());
            }
            result = (result && this.hasHealthReport() == other.hasHealthReport());
            if (this.hasHealthReport()) {
                result = (result && this.getHealthReport().equals(other.getHealthReport()));
            }
            result = (result && this.hasLastHealthReportTime() == other.hasLastHealthReportTime());
            if (this.hasLastHealthReportTime()) {
                result = (result && this.getLastHealthReportTime() == other.getLastHealthReportTime());
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
            if (this.hasIsNodeHealthy()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashBoolean(this.getIsNodeHealthy());
            }
            if (this.hasHealthReport()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getHealthReport().hashCode();
            }
            if (this.hasLastHealthReportTime()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + AbstractMessage.hashLong(this.getLastHealthReportTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NodeHealthStatusProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NodeHealthStatusProto.PARSER.parseFrom(data);
        }
        
        public static NodeHealthStatusProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHealthStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHealthStatusProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NodeHealthStatusProto.PARSER.parseFrom(data);
        }
        
        public static NodeHealthStatusProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHealthStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHealthStatusProto parseFrom(final InputStream input) throws IOException {
            return NodeHealthStatusProto.PARSER.parseFrom(input);
        }
        
        public static NodeHealthStatusProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHealthStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NodeHealthStatusProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NodeHealthStatusProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NodeHealthStatusProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHealthStatusProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NodeHealthStatusProto parseFrom(final CodedInputStream input) throws IOException {
            return NodeHealthStatusProto.PARSER.parseFrom(input);
        }
        
        public static NodeHealthStatusProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHealthStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NodeHealthStatusProto prototype) {
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
            NodeHealthStatusProto.PARSER = new AbstractParser<NodeHealthStatusProto>() {
                @Override
                public NodeHealthStatusProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NodeHealthStatusProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NodeHealthStatusProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NodeHealthStatusProtoOrBuilder
        {
            private int bitField0_;
            private boolean isNodeHealthy_;
            private Object healthReport_;
            private long lastHealthReportTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHealthStatusProto.class, Builder.class);
            }
            
            private Builder() {
                this.healthReport_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.healthReport_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NodeHealthStatusProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.isNodeHealthy_ = false;
                this.bitField0_ &= 0xFFFFFFFE;
                this.healthReport_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.lastHealthReportTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_NodeHealthStatusProto_descriptor;
            }
            
            @Override
            public NodeHealthStatusProto getDefaultInstanceForType() {
                return NodeHealthStatusProto.getDefaultInstance();
            }
            
            @Override
            public NodeHealthStatusProto build() {
                final NodeHealthStatusProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NodeHealthStatusProto buildPartial() {
                final NodeHealthStatusProto result = new NodeHealthStatusProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.isNodeHealthy_ = this.isNodeHealthy_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.healthReport_ = this.healthReport_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.lastHealthReportTime_ = this.lastHealthReportTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NodeHealthStatusProto) {
                    return this.mergeFrom((NodeHealthStatusProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NodeHealthStatusProto other) {
                if (other == NodeHealthStatusProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasIsNodeHealthy()) {
                    this.setIsNodeHealthy(other.getIsNodeHealthy());
                }
                if (other.hasHealthReport()) {
                    this.bitField0_ |= 0x2;
                    this.healthReport_ = other.healthReport_;
                    this.onChanged();
                }
                if (other.hasLastHealthReportTime()) {
                    this.setLastHealthReportTime(other.getLastHealthReportTime());
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
                NodeHealthStatusProto parsedMessage = null;
                try {
                    parsedMessage = NodeHealthStatusProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NodeHealthStatusProto)e.getUnfinishedMessage();
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
            public boolean hasIsNodeHealthy() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public boolean getIsNodeHealthy() {
                return this.isNodeHealthy_;
            }
            
            public Builder setIsNodeHealthy(final boolean value) {
                this.bitField0_ |= 0x1;
                this.isNodeHealthy_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearIsNodeHealthy() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.isNodeHealthy_ = false;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasHealthReport() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getHealthReport() {
                final Object ref = this.healthReport_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.healthReport_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getHealthReportBytes() {
                final Object ref = this.healthReport_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.healthReport_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setHealthReport(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.healthReport_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearHealthReport() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.healthReport_ = NodeHealthStatusProto.getDefaultInstance().getHealthReport();
                this.onChanged();
                return this;
            }
            
            public Builder setHealthReportBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.healthReport_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasLastHealthReportTime() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public long getLastHealthReportTime() {
                return this.lastHealthReportTime_;
            }
            
            public Builder setLastHealthReportTime(final long value) {
                this.bitField0_ |= 0x4;
                this.lastHealthReportTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearLastHealthReportTime() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.lastHealthReportTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class VersionProto extends GeneratedMessage implements VersionProtoOrBuilder
    {
        private static final VersionProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<VersionProto> PARSER;
        private int bitField0_;
        public static final int MAJOR_VERSION_FIELD_NUMBER = 1;
        private int majorVersion_;
        public static final int MINOR_VERSION_FIELD_NUMBER = 2;
        private int minorVersion_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private VersionProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private VersionProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static VersionProto getDefaultInstance() {
            return VersionProto.defaultInstance;
        }
        
        @Override
        public VersionProto getDefaultInstanceForType() {
            return VersionProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private VersionProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.majorVersion_ = input.readInt32();
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.minorVersion_ = input.readInt32();
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
            return YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(VersionProto.class, Builder.class);
        }
        
        @Override
        public Parser<VersionProto> getParserForType() {
            return VersionProto.PARSER;
        }
        
        @Override
        public boolean hasMajorVersion() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getMajorVersion() {
            return this.majorVersion_;
        }
        
        @Override
        public boolean hasMinorVersion() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public int getMinorVersion() {
            return this.minorVersion_;
        }
        
        private void initFields() {
            this.majorVersion_ = 0;
            this.minorVersion_ = 0;
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
                output.writeInt32(1, this.majorVersion_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt32(2, this.minorVersion_);
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
                size += CodedOutputStream.computeInt32Size(1, this.majorVersion_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt32Size(2, this.minorVersion_);
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
            if (!(obj instanceof VersionProto)) {
                return super.equals(obj);
            }
            final VersionProto other = (VersionProto)obj;
            boolean result = true;
            result = (result && this.hasMajorVersion() == other.hasMajorVersion());
            if (this.hasMajorVersion()) {
                result = (result && this.getMajorVersion() == other.getMajorVersion());
            }
            result = (result && this.hasMinorVersion() == other.hasMinorVersion());
            if (this.hasMinorVersion()) {
                result = (result && this.getMinorVersion() == other.getMinorVersion());
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
            if (this.hasMajorVersion()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getMajorVersion();
            }
            if (this.hasMinorVersion()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getMinorVersion();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static VersionProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return VersionProto.PARSER.parseFrom(data);
        }
        
        public static VersionProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return VersionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static VersionProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return VersionProto.PARSER.parseFrom(data);
        }
        
        public static VersionProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return VersionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static VersionProto parseFrom(final InputStream input) throws IOException {
            return VersionProto.PARSER.parseFrom(input);
        }
        
        public static VersionProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return VersionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static VersionProto parseDelimitedFrom(final InputStream input) throws IOException {
            return VersionProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static VersionProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return VersionProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static VersionProto parseFrom(final CodedInputStream input) throws IOException {
            return VersionProto.PARSER.parseFrom(input);
        }
        
        public static VersionProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return VersionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final VersionProto prototype) {
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
            VersionProto.PARSER = new AbstractParser<VersionProto>() {
                @Override
                public VersionProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new VersionProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new VersionProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements VersionProtoOrBuilder
        {
            private int bitField0_;
            private int majorVersion_;
            private int minorVersion_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(VersionProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (VersionProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.majorVersion_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.minorVersion_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonProtos.internal_static_hadoop_yarn_VersionProto_descriptor;
            }
            
            @Override
            public VersionProto getDefaultInstanceForType() {
                return VersionProto.getDefaultInstance();
            }
            
            @Override
            public VersionProto build() {
                final VersionProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public VersionProto buildPartial() {
                final VersionProto result = new VersionProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.majorVersion_ = this.majorVersion_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.minorVersion_ = this.minorVersion_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof VersionProto) {
                    return this.mergeFrom((VersionProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final VersionProto other) {
                if (other == VersionProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMajorVersion()) {
                    this.setMajorVersion(other.getMajorVersion());
                }
                if (other.hasMinorVersion()) {
                    this.setMinorVersion(other.getMinorVersion());
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
                VersionProto parsedMessage = null;
                try {
                    parsedMessage = VersionProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (VersionProto)e.getUnfinishedMessage();
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
            public boolean hasMajorVersion() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getMajorVersion() {
                return this.majorVersion_;
            }
            
            public Builder setMajorVersion(final int value) {
                this.bitField0_ |= 0x1;
                this.majorVersion_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMajorVersion() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.majorVersion_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMinorVersion() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public int getMinorVersion() {
                return this.minorVersion_;
            }
            
            public Builder setMinorVersion(final int value) {
                this.bitField0_ |= 0x2;
                this.minorVersion_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMinorVersion() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.minorVersion_ = 0;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface VersionProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasMajorVersion();
        
        int getMajorVersion();
        
        boolean hasMinorVersion();
        
        int getMinorVersion();
    }
    
    public interface NodeHealthStatusProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasIsNodeHealthy();
        
        boolean getIsNodeHealthy();
        
        boolean hasHealthReport();
        
        String getHealthReport();
        
        ByteString getHealthReportBytes();
        
        boolean hasLastHealthReportTime();
        
        long getLastHealthReportTime();
    }
    
    public interface MasterKeyProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasKeyId();
        
        int getKeyId();
        
        boolean hasBytes();
        
        ByteString getBytes();
    }
    
    public interface NodeStatusProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasNodeId();
        
        YarnProtos.NodeIdProto getNodeId();
        
        YarnProtos.NodeIdProtoOrBuilder getNodeIdOrBuilder();
        
        boolean hasResponseId();
        
        int getResponseId();
        
        List<YarnProtos.ContainerStatusProto> getContainersStatusesList();
        
        YarnProtos.ContainerStatusProto getContainersStatuses(final int p0);
        
        int getContainersStatusesCount();
        
        List<? extends YarnProtos.ContainerStatusProtoOrBuilder> getContainersStatusesOrBuilderList();
        
        YarnProtos.ContainerStatusProtoOrBuilder getContainersStatusesOrBuilder(final int p0);
        
        boolean hasNodeHealthStatus();
        
        NodeHealthStatusProto getNodeHealthStatus();
        
        NodeHealthStatusProtoOrBuilder getNodeHealthStatusOrBuilder();
        
        List<YarnProtos.ApplicationIdProto> getKeepAliveApplicationsList();
        
        YarnProtos.ApplicationIdProto getKeepAliveApplications(final int p0);
        
        int getKeepAliveApplicationsCount();
        
        List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getKeepAliveApplicationsOrBuilderList();
        
        YarnProtos.ApplicationIdProtoOrBuilder getKeepAliveApplicationsOrBuilder(final int p0);
    }
}
