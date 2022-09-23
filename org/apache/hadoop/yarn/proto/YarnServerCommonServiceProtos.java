// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Internal;
import com.google.protobuf.AbstractMessageLite;
import java.util.Collection;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ByteString;
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
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class YarnServerCommonServiceProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NodeHeartbeatRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NodeHeartbeatResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_SystemCredentialsForAppsProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_NMContainerStatusProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_NMContainerStatusProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnServerCommonServiceProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnServerCommonServiceProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n'yarn_server_common_service_protos.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\u001a\u001fyarn_server_common_protos.proto\" \u0002\n\u001fRegisterNodeManagerRequestProto\u0012)\n\u0007node_id\u0018\u0001 \u0001(\u000b2\u0018.hadoop.yarn.NodeIdProto\u0012\u0011\n\thttp_port\u0018\u0003 \u0001(\u0005\u0012,\n\bresource\u0018\u0004 \u0001(\u000b2\u001a.hadoop.yarn.ResourceProto\u0012\u0012\n\nnm_version\u0018\u0005 \u0001(\t\u0012?\n\u0012container_statuses\u0018\u0006 \u0003(\u000b2#.hadoop.yarn.NMContainerStatusProto\u0012<\n\u0013runningApplications\u0018\u0007 \u0003(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\"\u0097\u0002", "\n RegisterNodeManagerResponseProto\u0012?\n\u001acontainer_token_master_key\u0018\u0001 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u00128\n\u0013nm_token_master_key\u0018\u0002 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u00120\n\nnodeAction\u0018\u0003 \u0001(\u000e2\u001c.hadoop.yarn.NodeActionProto\u0012\u0015\n\rrm_identifier\u0018\u0004 \u0001(\u0003\u0012\u001b\n\u0013diagnostics_message\u0018\u0005 \u0001(\t\u0012\u0012\n\nrm_version\u0018\u0006 \u0001(\t\"\u00df\u0001\n\u0019NodeHeartbeatRequestProto\u00121\n\u000bnode_status\u0018\u0001 \u0001(\u000b2\u001c.hadoop.yarn.NodeStatusProto\u0012J\n%last_known_container_token_master_ke", "y\u0018\u0002 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u0012C\n\u001elast_known_nm_token_master_key\u0018\u0003 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\"´\u0004\n\u001aNodeHeartbeatResponseProto\u0012\u0013\n\u000bresponse_id\u0018\u0001 \u0001(\u0005\u0012?\n\u001acontainer_token_master_key\u0018\u0002 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u00128\n\u0013nm_token_master_key\u0018\u0003 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u00120\n\nnodeAction\u0018\u0004 \u0001(\u000e2\u001c.hadoop.yarn.NodeActionProto\u0012<\n\u0015containers_to_cleanup\u0018\u0005 \u0003(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u0012@\n\u0017appl", "ications_to_cleanup\u0018\u0006 \u0003(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\u0012\u001d\n\u0015nextHeartBeatInterval\u0018\u0007 \u0001(\u0003\u0012\u001b\n\u0013diagnostics_message\u0018\b \u0001(\t\u0012G\n containers_to_be_removed_from_nm\u0018\t \u0003(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u0012O\n\u001bsystem_credentials_for_apps\u0018\n \u0003(\u000b2*.hadoop.yarn.SystemCredentialsForAppsProto\"j\n\u001dSystemCredentialsForAppsProto\u0012.\n\u0005appId\u0018\u0001 \u0001(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\u0012\u0019\n\u0011credentialsForApp\u0018\u0002 \u0001(\f\"´\u0002\n\u0016NMContainerStat", "usProto\u00123\n\fcontainer_id\u0018\u0001 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u00129\n\u000fcontainer_state\u0018\u0002 \u0001(\u000e2 .hadoop.yarn.ContainerStateProto\u0012,\n\bresource\u0018\u0003 \u0001(\u000b2\u001a.hadoop.yarn.ResourceProto\u0012,\n\bpriority\u0018\u0004 \u0001(\u000b2\u001a.hadoop.yarn.PriorityProto\u0012\u0018\n\u000bdiagnostics\u0018\u0005 \u0001(\t:\u0003N/A\u0012\u001d\n\u0015container_exit_status\u0018\u0006 \u0001(\u0005\u0012\u0015\n\rcreation_time\u0018\u0007 \u0001(\u0003BC\n\u001corg.apache.hadoop.yarn.protoB\u001dYarnServerCommonServiceProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnServerCommonServiceProtos.descriptor = root;
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(0);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor, new String[] { "NodeId", "HttpPort", "Resource", "NmVersion", "ContainerStatuses", "RunningApplications" });
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(1);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor, new String[] { "ContainerTokenMasterKey", "NmTokenMasterKey", "NodeAction", "RmIdentifier", "DiagnosticsMessage", "RmVersion" });
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(2);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor, new String[] { "NodeStatus", "LastKnownContainerTokenMasterKey", "LastKnownNmTokenMasterKey" });
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(3);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor, new String[] { "ResponseId", "ContainerTokenMasterKey", "NmTokenMasterKey", "NodeAction", "ContainersToCleanup", "ApplicationsToCleanup", "NextHeartBeatInterval", "DiagnosticsMessage", "ContainersToBeRemovedFromNm", "SystemCredentialsForApps" });
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(4);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor, new String[] { "AppId", "CredentialsForApp" });
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_descriptor = YarnServerCommonServiceProtos.getDescriptor().getMessageTypes().get(5);
                YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_descriptor, new String[] { "ContainerId", "ContainerState", "Resource", "Priority", "Diagnostics", "ContainerExitStatus", "CreationTime" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor(), YarnServerCommonProtos.getDescriptor() }, assigner);
    }
    
    public static final class RegisterNodeManagerRequestProto extends GeneratedMessage implements RegisterNodeManagerRequestProtoOrBuilder
    {
        private static final RegisterNodeManagerRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RegisterNodeManagerRequestProto> PARSER;
        private int bitField0_;
        public static final int NODE_ID_FIELD_NUMBER = 1;
        private YarnProtos.NodeIdProto nodeId_;
        public static final int HTTP_PORT_FIELD_NUMBER = 3;
        private int httpPort_;
        public static final int RESOURCE_FIELD_NUMBER = 4;
        private YarnProtos.ResourceProto resource_;
        public static final int NM_VERSION_FIELD_NUMBER = 5;
        private Object nmVersion_;
        public static final int CONTAINER_STATUSES_FIELD_NUMBER = 6;
        private List<NMContainerStatusProto> containerStatuses_;
        public static final int RUNNINGAPPLICATIONS_FIELD_NUMBER = 7;
        private List<YarnProtos.ApplicationIdProto> runningApplications_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RegisterNodeManagerRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RegisterNodeManagerRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RegisterNodeManagerRequestProto getDefaultInstance() {
            return RegisterNodeManagerRequestProto.defaultInstance;
        }
        
        @Override
        public RegisterNodeManagerRequestProto getDefaultInstanceForType() {
            return RegisterNodeManagerRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RegisterNodeManagerRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 24: {
                            this.bitField0_ |= 0x2;
                            this.httpPort_ = input.readInt32();
                            continue;
                        }
                        case 34: {
                            YarnProtos.ResourceProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder2 = this.resource_.toBuilder();
                            }
                            this.resource_ = input.readMessage(YarnProtos.ResourceProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.resource_);
                                this.resource_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x8;
                            this.nmVersion_ = input.readBytes();
                            continue;
                        }
                        case 50: {
                            if ((mutable_bitField0_ & 0x10) != 0x10) {
                                this.containerStatuses_ = new ArrayList<NMContainerStatusProto>();
                                mutable_bitField0_ |= 0x10;
                            }
                            this.containerStatuses_.add(input.readMessage(NMContainerStatusProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 58: {
                            if ((mutable_bitField0_ & 0x20) != 0x20) {
                                this.runningApplications_ = new ArrayList<YarnProtos.ApplicationIdProto>();
                                mutable_bitField0_ |= 0x20;
                            }
                            this.runningApplications_.add(input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x10) == 0x10) {
                    this.containerStatuses_ = Collections.unmodifiableList((List<? extends NMContainerStatusProto>)this.containerStatuses_);
                }
                if ((mutable_bitField0_ & 0x20) == 0x20) {
                    this.runningApplications_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.runningApplications_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RegisterNodeManagerRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RegisterNodeManagerRequestProto> getParserForType() {
            return RegisterNodeManagerRequestProto.PARSER;
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
        public boolean hasHttpPort() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public int getHttpPort() {
            return this.httpPort_;
        }
        
        @Override
        public boolean hasResource() {
            return (this.bitField0_ & 0x4) == 0x4;
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
        public boolean hasNmVersion() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public String getNmVersion() {
            final Object ref = this.nmVersion_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.nmVersion_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getNmVersionBytes() {
            final Object ref = this.nmVersion_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.nmVersion_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public List<NMContainerStatusProto> getContainerStatusesList() {
            return this.containerStatuses_;
        }
        
        @Override
        public List<? extends NMContainerStatusProtoOrBuilder> getContainerStatusesOrBuilderList() {
            return this.containerStatuses_;
        }
        
        @Override
        public int getContainerStatusesCount() {
            return this.containerStatuses_.size();
        }
        
        @Override
        public NMContainerStatusProto getContainerStatuses(final int index) {
            return this.containerStatuses_.get(index);
        }
        
        @Override
        public NMContainerStatusProtoOrBuilder getContainerStatusesOrBuilder(final int index) {
            return this.containerStatuses_.get(index);
        }
        
        @Override
        public List<YarnProtos.ApplicationIdProto> getRunningApplicationsList() {
            return this.runningApplications_;
        }
        
        @Override
        public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getRunningApplicationsOrBuilderList() {
            return this.runningApplications_;
        }
        
        @Override
        public int getRunningApplicationsCount() {
            return this.runningApplications_.size();
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getRunningApplications(final int index) {
            return this.runningApplications_.get(index);
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getRunningApplicationsOrBuilder(final int index) {
            return this.runningApplications_.get(index);
        }
        
        private void initFields() {
            this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
            this.httpPort_ = 0;
            this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
            this.nmVersion_ = "";
            this.containerStatuses_ = Collections.emptyList();
            this.runningApplications_ = Collections.emptyList();
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
                output.writeInt32(3, this.httpPort_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(4, this.resource_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(5, this.getNmVersionBytes());
            }
            for (int i = 0; i < this.containerStatuses_.size(); ++i) {
                output.writeMessage(6, this.containerStatuses_.get(i));
            }
            for (int i = 0; i < this.runningApplications_.size(); ++i) {
                output.writeMessage(7, this.runningApplications_.get(i));
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
                size += CodedOutputStream.computeInt32Size(3, this.httpPort_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(4, this.resource_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(5, this.getNmVersionBytes());
            }
            for (int i = 0; i < this.containerStatuses_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(6, this.containerStatuses_.get(i));
            }
            for (int i = 0; i < this.runningApplications_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(7, this.runningApplications_.get(i));
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
            if (!(obj instanceof RegisterNodeManagerRequestProto)) {
                return super.equals(obj);
            }
            final RegisterNodeManagerRequestProto other = (RegisterNodeManagerRequestProto)obj;
            boolean result = true;
            result = (result && this.hasNodeId() == other.hasNodeId());
            if (this.hasNodeId()) {
                result = (result && this.getNodeId().equals(other.getNodeId()));
            }
            result = (result && this.hasHttpPort() == other.hasHttpPort());
            if (this.hasHttpPort()) {
                result = (result && this.getHttpPort() == other.getHttpPort());
            }
            result = (result && this.hasResource() == other.hasResource());
            if (this.hasResource()) {
                result = (result && this.getResource().equals(other.getResource()));
            }
            result = (result && this.hasNmVersion() == other.hasNmVersion());
            if (this.hasNmVersion()) {
                result = (result && this.getNmVersion().equals(other.getNmVersion()));
            }
            result = (result && this.getContainerStatusesList().equals(other.getContainerStatusesList()));
            result = (result && this.getRunningApplicationsList().equals(other.getRunningApplicationsList()));
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
            if (this.hasHttpPort()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getHttpPort();
            }
            if (this.hasResource()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getResource().hashCode();
            }
            if (this.hasNmVersion()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getNmVersion().hashCode();
            }
            if (this.getContainerStatusesCount() > 0) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getContainerStatusesList().hashCode();
            }
            if (this.getRunningApplicationsCount() > 0) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getRunningApplicationsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(data);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(data);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final InputStream input) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(input);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RegisterNodeManagerRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RegisterNodeManagerRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(input);
        }
        
        public static RegisterNodeManagerRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RegisterNodeManagerRequestProto prototype) {
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
            RegisterNodeManagerRequestProto.PARSER = new AbstractParser<RegisterNodeManagerRequestProto>() {
                @Override
                public RegisterNodeManagerRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RegisterNodeManagerRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RegisterNodeManagerRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RegisterNodeManagerRequestProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.NodeIdProto nodeId_;
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> nodeIdBuilder_;
            private int httpPort_;
            private YarnProtos.ResourceProto resource_;
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> resourceBuilder_;
            private Object nmVersion_;
            private List<NMContainerStatusProto> containerStatuses_;
            private RepeatedFieldBuilder<NMContainerStatusProto, NMContainerStatusProto.Builder, NMContainerStatusProtoOrBuilder> containerStatusesBuilder_;
            private List<YarnProtos.ApplicationIdProto> runningApplications_;
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> runningApplicationsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RegisterNodeManagerRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.nmVersion_ = "";
                this.containerStatuses_ = Collections.emptyList();
                this.runningApplications_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.nmVersion_ = "";
                this.containerStatuses_ = Collections.emptyList();
                this.runningApplications_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RegisterNodeManagerRequestProto.alwaysUseFieldBuilders) {
                    this.getNodeIdFieldBuilder();
                    this.getResourceFieldBuilder();
                    this.getContainerStatusesFieldBuilder();
                    this.getRunningApplicationsFieldBuilder();
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
                this.httpPort_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.resourceBuilder_ == null) {
                    this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                }
                else {
                    this.resourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                this.nmVersion_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                if (this.containerStatusesBuilder_ == null) {
                    this.containerStatuses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                }
                else {
                    this.containerStatusesBuilder_.clear();
                }
                if (this.runningApplicationsBuilder_ == null) {
                    this.runningApplications_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                }
                else {
                    this.runningApplicationsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerRequestProto_descriptor;
            }
            
            @Override
            public RegisterNodeManagerRequestProto getDefaultInstanceForType() {
                return RegisterNodeManagerRequestProto.getDefaultInstance();
            }
            
            @Override
            public RegisterNodeManagerRequestProto build() {
                final RegisterNodeManagerRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RegisterNodeManagerRequestProto buildPartial() {
                final RegisterNodeManagerRequestProto result = new RegisterNodeManagerRequestProto((GeneratedMessage.Builder)this);
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
                result.httpPort_ = this.httpPort_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.resourceBuilder_ == null) {
                    result.resource_ = this.resource_;
                }
                else {
                    result.resource_ = this.resourceBuilder_.build();
                }
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.nmVersion_ = this.nmVersion_;
                if (this.containerStatusesBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) == 0x10) {
                        this.containerStatuses_ = Collections.unmodifiableList((List<? extends NMContainerStatusProto>)this.containerStatuses_);
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    result.containerStatuses_ = this.containerStatuses_;
                }
                else {
                    result.containerStatuses_ = this.containerStatusesBuilder_.build();
                }
                if (this.runningApplicationsBuilder_ == null) {
                    if ((this.bitField0_ & 0x20) == 0x20) {
                        this.runningApplications_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.runningApplications_);
                        this.bitField0_ &= 0xFFFFFFDF;
                    }
                    result.runningApplications_ = this.runningApplications_;
                }
                else {
                    result.runningApplications_ = this.runningApplicationsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RegisterNodeManagerRequestProto) {
                    return this.mergeFrom((RegisterNodeManagerRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RegisterNodeManagerRequestProto other) {
                if (other == RegisterNodeManagerRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasNodeId()) {
                    this.mergeNodeId(other.getNodeId());
                }
                if (other.hasHttpPort()) {
                    this.setHttpPort(other.getHttpPort());
                }
                if (other.hasResource()) {
                    this.mergeResource(other.getResource());
                }
                if (other.hasNmVersion()) {
                    this.bitField0_ |= 0x8;
                    this.nmVersion_ = other.nmVersion_;
                    this.onChanged();
                }
                if (this.containerStatusesBuilder_ == null) {
                    if (!other.containerStatuses_.isEmpty()) {
                        if (this.containerStatuses_.isEmpty()) {
                            this.containerStatuses_ = other.containerStatuses_;
                            this.bitField0_ &= 0xFFFFFFEF;
                        }
                        else {
                            this.ensureContainerStatusesIsMutable();
                            this.containerStatuses_.addAll(other.containerStatuses_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.containerStatuses_.isEmpty()) {
                    if (this.containerStatusesBuilder_.isEmpty()) {
                        this.containerStatusesBuilder_.dispose();
                        this.containerStatusesBuilder_ = null;
                        this.containerStatuses_ = other.containerStatuses_;
                        this.bitField0_ &= 0xFFFFFFEF;
                        this.containerStatusesBuilder_ = (RegisterNodeManagerRequestProto.alwaysUseFieldBuilders ? this.getContainerStatusesFieldBuilder() : null);
                    }
                    else {
                        this.containerStatusesBuilder_.addAllMessages(other.containerStatuses_);
                    }
                }
                if (this.runningApplicationsBuilder_ == null) {
                    if (!other.runningApplications_.isEmpty()) {
                        if (this.runningApplications_.isEmpty()) {
                            this.runningApplications_ = other.runningApplications_;
                            this.bitField0_ &= 0xFFFFFFDF;
                        }
                        else {
                            this.ensureRunningApplicationsIsMutable();
                            this.runningApplications_.addAll(other.runningApplications_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.runningApplications_.isEmpty()) {
                    if (this.runningApplicationsBuilder_.isEmpty()) {
                        this.runningApplicationsBuilder_.dispose();
                        this.runningApplicationsBuilder_ = null;
                        this.runningApplications_ = other.runningApplications_;
                        this.bitField0_ &= 0xFFFFFFDF;
                        this.runningApplicationsBuilder_ = (RegisterNodeManagerRequestProto.alwaysUseFieldBuilders ? this.getRunningApplicationsFieldBuilder() : null);
                    }
                    else {
                        this.runningApplicationsBuilder_.addAllMessages(other.runningApplications_);
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
                RegisterNodeManagerRequestProto parsedMessage = null;
                try {
                    parsedMessage = RegisterNodeManagerRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RegisterNodeManagerRequestProto)e.getUnfinishedMessage();
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
            public boolean hasHttpPort() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public int getHttpPort() {
                return this.httpPort_;
            }
            
            public Builder setHttpPort(final int value) {
                this.bitField0_ |= 0x2;
                this.httpPort_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearHttpPort() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.httpPort_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasResource() {
                return (this.bitField0_ & 0x4) == 0x4;
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
                this.bitField0_ |= 0x4;
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
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeResource(final YarnProtos.ResourceProto value) {
                if (this.resourceBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.resource_ != YarnProtos.ResourceProto.getDefaultInstance()) {
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
                this.bitField0_ |= 0x4;
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
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnProtos.ResourceProto.Builder getResourceBuilder() {
                this.bitField0_ |= 0x4;
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
            public boolean hasNmVersion() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public String getNmVersion() {
                final Object ref = this.nmVersion_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.nmVersion_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getNmVersionBytes() {
                final Object ref = this.nmVersion_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.nmVersion_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setNmVersion(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.nmVersion_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNmVersion() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.nmVersion_ = RegisterNodeManagerRequestProto.getDefaultInstance().getNmVersion();
                this.onChanged();
                return this;
            }
            
            public Builder setNmVersionBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.nmVersion_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureContainerStatusesIsMutable() {
                if ((this.bitField0_ & 0x10) != 0x10) {
                    this.containerStatuses_ = new ArrayList<NMContainerStatusProto>(this.containerStatuses_);
                    this.bitField0_ |= 0x10;
                }
            }
            
            @Override
            public List<NMContainerStatusProto> getContainerStatusesList() {
                if (this.containerStatusesBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends NMContainerStatusProto>)this.containerStatuses_);
                }
                return this.containerStatusesBuilder_.getMessageList();
            }
            
            @Override
            public int getContainerStatusesCount() {
                if (this.containerStatusesBuilder_ == null) {
                    return this.containerStatuses_.size();
                }
                return this.containerStatusesBuilder_.getCount();
            }
            
            @Override
            public NMContainerStatusProto getContainerStatuses(final int index) {
                if (this.containerStatusesBuilder_ == null) {
                    return this.containerStatuses_.get(index);
                }
                return this.containerStatusesBuilder_.getMessage(index);
            }
            
            public Builder setContainerStatuses(final int index, final NMContainerStatusProto value) {
                if (this.containerStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setContainerStatuses(final int index, final NMContainerStatusProto.Builder builderForValue) {
                if (this.containerStatusesBuilder_ == null) {
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainerStatuses(final NMContainerStatusProto value) {
                if (this.containerStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.add(value);
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addContainerStatuses(final int index, final NMContainerStatusProto value) {
                if (this.containerStatusesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addContainerStatuses(final NMContainerStatusProto.Builder builderForValue) {
                if (this.containerStatusesBuilder_ == null) {
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainerStatuses(final int index, final NMContainerStatusProto.Builder builderForValue) {
                if (this.containerStatusesBuilder_ == null) {
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllContainerStatuses(final Iterable<? extends NMContainerStatusProto> values) {
                if (this.containerStatusesBuilder_ == null) {
                    this.ensureContainerStatusesIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.containerStatuses_);
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearContainerStatuses() {
                if (this.containerStatusesBuilder_ == null) {
                    this.containerStatuses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeContainerStatuses(final int index) {
                if (this.containerStatusesBuilder_ == null) {
                    this.ensureContainerStatusesIsMutable();
                    this.containerStatuses_.remove(index);
                    this.onChanged();
                }
                else {
                    this.containerStatusesBuilder_.remove(index);
                }
                return this;
            }
            
            public NMContainerStatusProto.Builder getContainerStatusesBuilder(final int index) {
                return this.getContainerStatusesFieldBuilder().getBuilder(index);
            }
            
            @Override
            public NMContainerStatusProtoOrBuilder getContainerStatusesOrBuilder(final int index) {
                if (this.containerStatusesBuilder_ == null) {
                    return this.containerStatuses_.get(index);
                }
                return this.containerStatusesBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends NMContainerStatusProtoOrBuilder> getContainerStatusesOrBuilderList() {
                if (this.containerStatusesBuilder_ != null) {
                    return this.containerStatusesBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends NMContainerStatusProtoOrBuilder>)this.containerStatuses_);
            }
            
            public NMContainerStatusProto.Builder addContainerStatusesBuilder() {
                return this.getContainerStatusesFieldBuilder().addBuilder(NMContainerStatusProto.getDefaultInstance());
            }
            
            public NMContainerStatusProto.Builder addContainerStatusesBuilder(final int index) {
                return this.getContainerStatusesFieldBuilder().addBuilder(index, NMContainerStatusProto.getDefaultInstance());
            }
            
            public List<NMContainerStatusProto.Builder> getContainerStatusesBuilderList() {
                return this.getContainerStatusesFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<NMContainerStatusProto, NMContainerStatusProto.Builder, NMContainerStatusProtoOrBuilder> getContainerStatusesFieldBuilder() {
                if (this.containerStatusesBuilder_ == null) {
                    this.containerStatusesBuilder_ = new RepeatedFieldBuilder<NMContainerStatusProto, NMContainerStatusProto.Builder, NMContainerStatusProtoOrBuilder>(this.containerStatuses_, (this.bitField0_ & 0x10) == 0x10, this.getParentForChildren(), this.isClean());
                    this.containerStatuses_ = null;
                }
                return this.containerStatusesBuilder_;
            }
            
            private void ensureRunningApplicationsIsMutable() {
                if ((this.bitField0_ & 0x20) != 0x20) {
                    this.runningApplications_ = new ArrayList<YarnProtos.ApplicationIdProto>(this.runningApplications_);
                    this.bitField0_ |= 0x20;
                }
            }
            
            @Override
            public List<YarnProtos.ApplicationIdProto> getRunningApplicationsList() {
                if (this.runningApplicationsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.runningApplications_);
                }
                return this.runningApplicationsBuilder_.getMessageList();
            }
            
            @Override
            public int getRunningApplicationsCount() {
                if (this.runningApplicationsBuilder_ == null) {
                    return this.runningApplications_.size();
                }
                return this.runningApplicationsBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getRunningApplications(final int index) {
                if (this.runningApplicationsBuilder_ == null) {
                    return this.runningApplications_.get(index);
                }
                return this.runningApplicationsBuilder_.getMessage(index);
            }
            
            public Builder setRunningApplications(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.runningApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setRunningApplications(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.runningApplicationsBuilder_ == null) {
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addRunningApplications(final YarnProtos.ApplicationIdProto value) {
                if (this.runningApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.add(value);
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addRunningApplications(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.runningApplicationsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addRunningApplications(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.runningApplicationsBuilder_ == null) {
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addRunningApplications(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.runningApplicationsBuilder_ == null) {
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllRunningApplications(final Iterable<? extends YarnProtos.ApplicationIdProto> values) {
                if (this.runningApplicationsBuilder_ == null) {
                    this.ensureRunningApplicationsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.runningApplications_);
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearRunningApplications() {
                if (this.runningApplicationsBuilder_ == null) {
                    this.runningApplications_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeRunningApplications(final int index) {
                if (this.runningApplicationsBuilder_ == null) {
                    this.ensureRunningApplicationsIsMutable();
                    this.runningApplications_.remove(index);
                    this.onChanged();
                }
                else {
                    this.runningApplicationsBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getRunningApplicationsBuilder(final int index) {
                return this.getRunningApplicationsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getRunningApplicationsOrBuilder(final int index) {
                if (this.runningApplicationsBuilder_ == null) {
                    return this.runningApplications_.get(index);
                }
                return this.runningApplicationsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getRunningApplicationsOrBuilderList() {
                if (this.runningApplicationsBuilder_ != null) {
                    return this.runningApplicationsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProtoOrBuilder>)this.runningApplications_);
            }
            
            public YarnProtos.ApplicationIdProto.Builder addRunningApplicationsBuilder() {
                return this.getRunningApplicationsFieldBuilder().addBuilder(YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public YarnProtos.ApplicationIdProto.Builder addRunningApplicationsBuilder(final int index) {
                return this.getRunningApplicationsFieldBuilder().addBuilder(index, YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ApplicationIdProto.Builder> getRunningApplicationsBuilderList() {
                return this.getRunningApplicationsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getRunningApplicationsFieldBuilder() {
                if (this.runningApplicationsBuilder_ == null) {
                    this.runningApplicationsBuilder_ = new RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.runningApplications_, (this.bitField0_ & 0x20) == 0x20, this.getParentForChildren(), this.isClean());
                    this.runningApplications_ = null;
                }
                return this.runningApplicationsBuilder_;
            }
        }
    }
    
    public static final class RegisterNodeManagerResponseProto extends GeneratedMessage implements RegisterNodeManagerResponseProtoOrBuilder
    {
        private static final RegisterNodeManagerResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RegisterNodeManagerResponseProto> PARSER;
        private int bitField0_;
        public static final int CONTAINER_TOKEN_MASTER_KEY_FIELD_NUMBER = 1;
        private YarnServerCommonProtos.MasterKeyProto containerTokenMasterKey_;
        public static final int NM_TOKEN_MASTER_KEY_FIELD_NUMBER = 2;
        private YarnServerCommonProtos.MasterKeyProto nmTokenMasterKey_;
        public static final int NODEACTION_FIELD_NUMBER = 3;
        private YarnServerCommonProtos.NodeActionProto nodeAction_;
        public static final int RM_IDENTIFIER_FIELD_NUMBER = 4;
        private long rmIdentifier_;
        public static final int DIAGNOSTICS_MESSAGE_FIELD_NUMBER = 5;
        private Object diagnosticsMessage_;
        public static final int RM_VERSION_FIELD_NUMBER = 6;
        private Object rmVersion_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RegisterNodeManagerResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RegisterNodeManagerResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RegisterNodeManagerResponseProto getDefaultInstance() {
            return RegisterNodeManagerResponseProto.defaultInstance;
        }
        
        @Override
        public RegisterNodeManagerResponseProto getDefaultInstanceForType() {
            return RegisterNodeManagerResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RegisterNodeManagerResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.containerTokenMasterKey_.toBuilder();
                            }
                            this.containerTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.containerTokenMasterKey_);
                                this.containerTokenMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.nmTokenMasterKey_.toBuilder();
                            }
                            this.nmTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.nmTokenMasterKey_);
                                this.nmTokenMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 24: {
                            final int rawValue = input.readEnum();
                            final YarnServerCommonProtos.NodeActionProto value = YarnServerCommonProtos.NodeActionProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(3, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x4;
                            this.nodeAction_ = value;
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x8;
                            this.rmIdentifier_ = input.readInt64();
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.diagnosticsMessage_ = input.readBytes();
                            continue;
                        }
                        case 50: {
                            this.bitField0_ |= 0x20;
                            this.rmVersion_ = input.readBytes();
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
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RegisterNodeManagerResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RegisterNodeManagerResponseProto> getParserForType() {
            return RegisterNodeManagerResponseProto.PARSER;
        }
        
        @Override
        public boolean hasContainerTokenMasterKey() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey() {
            return this.containerTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder() {
            return this.containerTokenMasterKey_;
        }
        
        @Override
        public boolean hasNmTokenMasterKey() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey() {
            return this.nmTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder() {
            return this.nmTokenMasterKey_;
        }
        
        @Override
        public boolean hasNodeAction() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public YarnServerCommonProtos.NodeActionProto getNodeAction() {
            return this.nodeAction_;
        }
        
        @Override
        public boolean hasRmIdentifier() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public long getRmIdentifier() {
            return this.rmIdentifier_;
        }
        
        @Override
        public boolean hasDiagnosticsMessage() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getDiagnosticsMessage() {
            final Object ref = this.diagnosticsMessage_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsMessage_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsMessageBytes() {
            final Object ref = this.diagnosticsMessage_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsMessage_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRmVersion() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public String getRmVersion() {
            final Object ref = this.rmVersion_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.rmVersion_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRmVersionBytes() {
            final Object ref = this.rmVersion_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.rmVersion_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
            this.rmIdentifier_ = 0L;
            this.diagnosticsMessage_ = "";
            this.rmVersion_ = "";
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
                output.writeMessage(1, this.containerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.nmTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeEnum(3, this.nodeAction_.getNumber());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeInt64(4, this.rmIdentifier_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getDiagnosticsMessageBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(6, this.getRmVersionBytes());
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
                size += CodedOutputStream.computeMessageSize(1, this.containerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.nmTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeEnumSize(3, this.nodeAction_.getNumber());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeInt64Size(4, this.rmIdentifier_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getDiagnosticsMessageBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(6, this.getRmVersionBytes());
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
            if (!(obj instanceof RegisterNodeManagerResponseProto)) {
                return super.equals(obj);
            }
            final RegisterNodeManagerResponseProto other = (RegisterNodeManagerResponseProto)obj;
            boolean result = true;
            result = (result && this.hasContainerTokenMasterKey() == other.hasContainerTokenMasterKey());
            if (this.hasContainerTokenMasterKey()) {
                result = (result && this.getContainerTokenMasterKey().equals(other.getContainerTokenMasterKey()));
            }
            result = (result && this.hasNmTokenMasterKey() == other.hasNmTokenMasterKey());
            if (this.hasNmTokenMasterKey()) {
                result = (result && this.getNmTokenMasterKey().equals(other.getNmTokenMasterKey()));
            }
            result = (result && this.hasNodeAction() == other.hasNodeAction());
            if (this.hasNodeAction()) {
                result = (result && this.getNodeAction() == other.getNodeAction());
            }
            result = (result && this.hasRmIdentifier() == other.hasRmIdentifier());
            if (this.hasRmIdentifier()) {
                result = (result && this.getRmIdentifier() == other.getRmIdentifier());
            }
            result = (result && this.hasDiagnosticsMessage() == other.hasDiagnosticsMessage());
            if (this.hasDiagnosticsMessage()) {
                result = (result && this.getDiagnosticsMessage().equals(other.getDiagnosticsMessage()));
            }
            result = (result && this.hasRmVersion() == other.hasRmVersion());
            if (this.hasRmVersion()) {
                result = (result && this.getRmVersion().equals(other.getRmVersion()));
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
            if (this.hasContainerTokenMasterKey()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getContainerTokenMasterKey().hashCode();
            }
            if (this.hasNmTokenMasterKey()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getNmTokenMasterKey().hashCode();
            }
            if (this.hasNodeAction()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getNodeAction());
            }
            if (this.hasRmIdentifier()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashLong(this.getRmIdentifier());
            }
            if (this.hasDiagnosticsMessage()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getDiagnosticsMessage().hashCode();
            }
            if (this.hasRmVersion()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getRmVersion().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(data);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(data);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final InputStream input) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(input);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RegisterNodeManagerResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RegisterNodeManagerResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(input);
        }
        
        public static RegisterNodeManagerResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RegisterNodeManagerResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RegisterNodeManagerResponseProto prototype) {
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
            RegisterNodeManagerResponseProto.PARSER = new AbstractParser<RegisterNodeManagerResponseProto>() {
                @Override
                public RegisterNodeManagerResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RegisterNodeManagerResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RegisterNodeManagerResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RegisterNodeManagerResponseProtoOrBuilder
        {
            private int bitField0_;
            private YarnServerCommonProtos.MasterKeyProto containerTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> containerTokenMasterKeyBuilder_;
            private YarnServerCommonProtos.MasterKeyProto nmTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> nmTokenMasterKeyBuilder_;
            private YarnServerCommonProtos.NodeActionProto nodeAction_;
            private long rmIdentifier_;
            private Object diagnosticsMessage_;
            private Object rmVersion_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RegisterNodeManagerResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.diagnosticsMessage_ = "";
                this.rmVersion_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.diagnosticsMessage_ = "";
                this.rmVersion_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RegisterNodeManagerResponseProto.alwaysUseFieldBuilders) {
                    this.getContainerTokenMasterKeyFieldBuilder();
                    this.getNmTokenMasterKeyFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.bitField0_ &= 0xFFFFFFFB;
                this.rmIdentifier_ = 0L;
                this.bitField0_ &= 0xFFFFFFF7;
                this.diagnosticsMessage_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.rmVersion_ = "";
                this.bitField0_ &= 0xFFFFFFDF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_RegisterNodeManagerResponseProto_descriptor;
            }
            
            @Override
            public RegisterNodeManagerResponseProto getDefaultInstanceForType() {
                return RegisterNodeManagerResponseProto.getDefaultInstance();
            }
            
            @Override
            public RegisterNodeManagerResponseProto build() {
                final RegisterNodeManagerResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RegisterNodeManagerResponseProto buildPartial() {
                final RegisterNodeManagerResponseProto result = new RegisterNodeManagerResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    result.containerTokenMasterKey_ = this.containerTokenMasterKey_;
                }
                else {
                    result.containerTokenMasterKey_ = this.containerTokenMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    result.nmTokenMasterKey_ = this.nmTokenMasterKey_;
                }
                else {
                    result.nmTokenMasterKey_ = this.nmTokenMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.nodeAction_ = this.nodeAction_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.rmIdentifier_ = this.rmIdentifier_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.diagnosticsMessage_ = this.diagnosticsMessage_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.rmVersion_ = this.rmVersion_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RegisterNodeManagerResponseProto) {
                    return this.mergeFrom((RegisterNodeManagerResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RegisterNodeManagerResponseProto other) {
                if (other == RegisterNodeManagerResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerTokenMasterKey()) {
                    this.mergeContainerTokenMasterKey(other.getContainerTokenMasterKey());
                }
                if (other.hasNmTokenMasterKey()) {
                    this.mergeNmTokenMasterKey(other.getNmTokenMasterKey());
                }
                if (other.hasNodeAction()) {
                    this.setNodeAction(other.getNodeAction());
                }
                if (other.hasRmIdentifier()) {
                    this.setRmIdentifier(other.getRmIdentifier());
                }
                if (other.hasDiagnosticsMessage()) {
                    this.bitField0_ |= 0x10;
                    this.diagnosticsMessage_ = other.diagnosticsMessage_;
                    this.onChanged();
                }
                if (other.hasRmVersion()) {
                    this.bitField0_ |= 0x20;
                    this.rmVersion_ = other.rmVersion_;
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
                RegisterNodeManagerResponseProto parsedMessage = null;
                try {
                    parsedMessage = RegisterNodeManagerResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RegisterNodeManagerResponseProto)e.getUnfinishedMessage();
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
            public boolean hasContainerTokenMasterKey() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    return this.containerTokenMasterKey_;
                }
                return this.containerTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.containerTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.containerTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.containerTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.containerTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearContainerTokenMasterKey() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getContainerTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getContainerTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder() {
                if (this.containerTokenMasterKeyBuilder_ != null) {
                    return this.containerTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.containerTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getContainerTokenMasterKeyFieldBuilder() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.containerTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.containerTokenMasterKey_ = null;
                }
                return this.containerTokenMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasNmTokenMasterKey() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    return this.nmTokenMasterKey_;
                }
                return this.nmTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nmTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.nmTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.nmTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nmTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearNmTokenMasterKey() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getNmTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getNmTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder() {
                if (this.nmTokenMasterKeyBuilder_ != null) {
                    return this.nmTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.nmTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getNmTokenMasterKeyFieldBuilder() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.nmTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.nmTokenMasterKey_ = null;
                }
                return this.nmTokenMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasNodeAction() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public YarnServerCommonProtos.NodeActionProto getNodeAction() {
                return this.nodeAction_;
            }
            
            public Builder setNodeAction(final YarnServerCommonProtos.NodeActionProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.nodeAction_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNodeAction() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRmIdentifier() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public long getRmIdentifier() {
                return this.rmIdentifier_;
            }
            
            public Builder setRmIdentifier(final long value) {
                this.bitField0_ |= 0x8;
                this.rmIdentifier_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRmIdentifier() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.rmIdentifier_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsMessage() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getDiagnosticsMessage() {
                final Object ref = this.diagnosticsMessage_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsMessage_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsMessageBytes() {
                final Object ref = this.diagnosticsMessage_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsMessage_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsMessage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnosticsMessage_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsMessage() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.diagnosticsMessage_ = RegisterNodeManagerResponseProto.getDefaultInstance().getDiagnosticsMessage();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsMessageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnosticsMessage_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRmVersion() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public String getRmVersion() {
                final Object ref = this.rmVersion_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.rmVersion_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRmVersionBytes() {
                final Object ref = this.rmVersion_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.rmVersion_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRmVersion(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.rmVersion_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRmVersion() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.rmVersion_ = RegisterNodeManagerResponseProto.getDefaultInstance().getRmVersion();
                this.onChanged();
                return this;
            }
            
            public Builder setRmVersionBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.rmVersion_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class NodeHeartbeatRequestProto extends GeneratedMessage implements NodeHeartbeatRequestProtoOrBuilder
    {
        private static final NodeHeartbeatRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NodeHeartbeatRequestProto> PARSER;
        private int bitField0_;
        public static final int NODE_STATUS_FIELD_NUMBER = 1;
        private YarnServerCommonProtos.NodeStatusProto nodeStatus_;
        public static final int LAST_KNOWN_CONTAINER_TOKEN_MASTER_KEY_FIELD_NUMBER = 2;
        private YarnServerCommonProtos.MasterKeyProto lastKnownContainerTokenMasterKey_;
        public static final int LAST_KNOWN_NM_TOKEN_MASTER_KEY_FIELD_NUMBER = 3;
        private YarnServerCommonProtos.MasterKeyProto lastKnownNmTokenMasterKey_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NodeHeartbeatRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NodeHeartbeatRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NodeHeartbeatRequestProto getDefaultInstance() {
            return NodeHeartbeatRequestProto.defaultInstance;
        }
        
        @Override
        public NodeHeartbeatRequestProto getDefaultInstanceForType() {
            return NodeHeartbeatRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NodeHeartbeatRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnServerCommonProtos.NodeStatusProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.nodeStatus_.toBuilder();
                            }
                            this.nodeStatus_ = input.readMessage(YarnServerCommonProtos.NodeStatusProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.nodeStatus_);
                                this.nodeStatus_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder2 = this.lastKnownContainerTokenMasterKey_.toBuilder();
                            }
                            this.lastKnownContainerTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.lastKnownContainerTokenMasterKey_);
                                this.lastKnownContainerTokenMasterKey_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder2 = this.lastKnownNmTokenMasterKey_.toBuilder();
                            }
                            this.lastKnownNmTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.lastKnownNmTokenMasterKey_);
                                this.lastKnownNmTokenMasterKey_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
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
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHeartbeatRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<NodeHeartbeatRequestProto> getParserForType() {
            return NodeHeartbeatRequestProto.PARSER;
        }
        
        @Override
        public boolean hasNodeStatus() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnServerCommonProtos.NodeStatusProto getNodeStatus() {
            return this.nodeStatus_;
        }
        
        @Override
        public YarnServerCommonProtos.NodeStatusProtoOrBuilder getNodeStatusOrBuilder() {
            return this.nodeStatus_;
        }
        
        @Override
        public boolean hasLastKnownContainerTokenMasterKey() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getLastKnownContainerTokenMasterKey() {
            return this.lastKnownContainerTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownContainerTokenMasterKeyOrBuilder() {
            return this.lastKnownContainerTokenMasterKey_;
        }
        
        @Override
        public boolean hasLastKnownNmTokenMasterKey() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getLastKnownNmTokenMasterKey() {
            return this.lastKnownNmTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownNmTokenMasterKeyOrBuilder() {
            return this.lastKnownNmTokenMasterKey_;
        }
        
        private void initFields() {
            this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
            this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
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
                output.writeMessage(1, this.nodeStatus_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.lastKnownContainerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.lastKnownNmTokenMasterKey_);
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
                size += CodedOutputStream.computeMessageSize(1, this.nodeStatus_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.lastKnownContainerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.lastKnownNmTokenMasterKey_);
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
            if (!(obj instanceof NodeHeartbeatRequestProto)) {
                return super.equals(obj);
            }
            final NodeHeartbeatRequestProto other = (NodeHeartbeatRequestProto)obj;
            boolean result = true;
            result = (result && this.hasNodeStatus() == other.hasNodeStatus());
            if (this.hasNodeStatus()) {
                result = (result && this.getNodeStatus().equals(other.getNodeStatus()));
            }
            result = (result && this.hasLastKnownContainerTokenMasterKey() == other.hasLastKnownContainerTokenMasterKey());
            if (this.hasLastKnownContainerTokenMasterKey()) {
                result = (result && this.getLastKnownContainerTokenMasterKey().equals(other.getLastKnownContainerTokenMasterKey()));
            }
            result = (result && this.hasLastKnownNmTokenMasterKey() == other.hasLastKnownNmTokenMasterKey());
            if (this.hasLastKnownNmTokenMasterKey()) {
                result = (result && this.getLastKnownNmTokenMasterKey().equals(other.getLastKnownNmTokenMasterKey()));
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
            if (this.hasNodeStatus()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeStatus().hashCode();
            }
            if (this.hasLastKnownContainerTokenMasterKey()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getLastKnownContainerTokenMasterKey().hashCode();
            }
            if (this.hasLastKnownNmTokenMasterKey()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getLastKnownNmTokenMasterKey().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(data);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(data);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final InputStream input) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(input);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NodeHeartbeatRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NodeHeartbeatRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(input);
        }
        
        public static NodeHeartbeatRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NodeHeartbeatRequestProto prototype) {
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
            NodeHeartbeatRequestProto.PARSER = new AbstractParser<NodeHeartbeatRequestProto>() {
                @Override
                public NodeHeartbeatRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NodeHeartbeatRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NodeHeartbeatRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NodeHeartbeatRequestProtoOrBuilder
        {
            private int bitField0_;
            private YarnServerCommonProtos.NodeStatusProto nodeStatus_;
            private SingleFieldBuilder<YarnServerCommonProtos.NodeStatusProto, YarnServerCommonProtos.NodeStatusProto.Builder, YarnServerCommonProtos.NodeStatusProtoOrBuilder> nodeStatusBuilder_;
            private YarnServerCommonProtos.MasterKeyProto lastKnownContainerTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> lastKnownContainerTokenMasterKeyBuilder_;
            private YarnServerCommonProtos.MasterKeyProto lastKnownNmTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> lastKnownNmTokenMasterKeyBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHeartbeatRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
                this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
                this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NodeHeartbeatRequestProto.alwaysUseFieldBuilders) {
                    this.getNodeStatusFieldBuilder();
                    this.getLastKnownContainerTokenMasterKeyFieldBuilder();
                    this.getLastKnownNmTokenMasterKeyFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.nodeStatusBuilder_ == null) {
                    this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
                }
                else {
                    this.nodeStatusBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.lastKnownContainerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.lastKnownNmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatRequestProto_descriptor;
            }
            
            @Override
            public NodeHeartbeatRequestProto getDefaultInstanceForType() {
                return NodeHeartbeatRequestProto.getDefaultInstance();
            }
            
            @Override
            public NodeHeartbeatRequestProto build() {
                final NodeHeartbeatRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NodeHeartbeatRequestProto buildPartial() {
                final NodeHeartbeatRequestProto result = new NodeHeartbeatRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.nodeStatusBuilder_ == null) {
                    result.nodeStatus_ = this.nodeStatus_;
                }
                else {
                    result.nodeStatus_ = this.nodeStatusBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    result.lastKnownContainerTokenMasterKey_ = this.lastKnownContainerTokenMasterKey_;
                }
                else {
                    result.lastKnownContainerTokenMasterKey_ = this.lastKnownContainerTokenMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    result.lastKnownNmTokenMasterKey_ = this.lastKnownNmTokenMasterKey_;
                }
                else {
                    result.lastKnownNmTokenMasterKey_ = this.lastKnownNmTokenMasterKeyBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NodeHeartbeatRequestProto) {
                    return this.mergeFrom((NodeHeartbeatRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NodeHeartbeatRequestProto other) {
                if (other == NodeHeartbeatRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasNodeStatus()) {
                    this.mergeNodeStatus(other.getNodeStatus());
                }
                if (other.hasLastKnownContainerTokenMasterKey()) {
                    this.mergeLastKnownContainerTokenMasterKey(other.getLastKnownContainerTokenMasterKey());
                }
                if (other.hasLastKnownNmTokenMasterKey()) {
                    this.mergeLastKnownNmTokenMasterKey(other.getLastKnownNmTokenMasterKey());
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
                NodeHeartbeatRequestProto parsedMessage = null;
                try {
                    parsedMessage = NodeHeartbeatRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NodeHeartbeatRequestProto)e.getUnfinishedMessage();
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
            public boolean hasNodeStatus() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnServerCommonProtos.NodeStatusProto getNodeStatus() {
                if (this.nodeStatusBuilder_ == null) {
                    return this.nodeStatus_;
                }
                return this.nodeStatusBuilder_.getMessage();
            }
            
            public Builder setNodeStatus(final YarnServerCommonProtos.NodeStatusProto value) {
                if (this.nodeStatusBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nodeStatus_ = value;
                    this.onChanged();
                }
                else {
                    this.nodeStatusBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setNodeStatus(final YarnServerCommonProtos.NodeStatusProto.Builder builderForValue) {
                if (this.nodeStatusBuilder_ == null) {
                    this.nodeStatus_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nodeStatusBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeNodeStatus(final YarnServerCommonProtos.NodeStatusProto value) {
                if (this.nodeStatusBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.nodeStatus_ != YarnServerCommonProtos.NodeStatusProto.getDefaultInstance()) {
                        this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.newBuilder(this.nodeStatus_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nodeStatus_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nodeStatusBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearNodeStatus() {
                if (this.nodeStatusBuilder_ == null) {
                    this.nodeStatus_ = YarnServerCommonProtos.NodeStatusProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nodeStatusBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnServerCommonProtos.NodeStatusProto.Builder getNodeStatusBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getNodeStatusFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.NodeStatusProtoOrBuilder getNodeStatusOrBuilder() {
                if (this.nodeStatusBuilder_ != null) {
                    return this.nodeStatusBuilder_.getMessageOrBuilder();
                }
                return this.nodeStatus_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.NodeStatusProto, YarnServerCommonProtos.NodeStatusProto.Builder, YarnServerCommonProtos.NodeStatusProtoOrBuilder> getNodeStatusFieldBuilder() {
                if (this.nodeStatusBuilder_ == null) {
                    this.nodeStatusBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.NodeStatusProto, YarnServerCommonProtos.NodeStatusProto.Builder, YarnServerCommonProtos.NodeStatusProtoOrBuilder>(this.nodeStatus_, this.getParentForChildren(), this.isClean());
                    this.nodeStatus_ = null;
                }
                return this.nodeStatusBuilder_;
            }
            
            @Override
            public boolean hasLastKnownContainerTokenMasterKey() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getLastKnownContainerTokenMasterKey() {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    return this.lastKnownContainerTokenMasterKey_;
                }
                return this.lastKnownContainerTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setLastKnownContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.lastKnownContainerTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.lastKnownContainerTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setLastKnownContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    this.lastKnownContainerTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.lastKnownContainerTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeLastKnownContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.lastKnownContainerTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.lastKnownContainerTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.lastKnownContainerTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.lastKnownContainerTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearLastKnownContainerTokenMasterKey() {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    this.lastKnownContainerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.lastKnownContainerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getLastKnownContainerTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getLastKnownContainerTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownContainerTokenMasterKeyOrBuilder() {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ != null) {
                    return this.lastKnownContainerTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.lastKnownContainerTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getLastKnownContainerTokenMasterKeyFieldBuilder() {
                if (this.lastKnownContainerTokenMasterKeyBuilder_ == null) {
                    this.lastKnownContainerTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.lastKnownContainerTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.lastKnownContainerTokenMasterKey_ = null;
                }
                return this.lastKnownContainerTokenMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasLastKnownNmTokenMasterKey() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getLastKnownNmTokenMasterKey() {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    return this.lastKnownNmTokenMasterKey_;
                }
                return this.lastKnownNmTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setLastKnownNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.lastKnownNmTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.lastKnownNmTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setLastKnownNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    this.lastKnownNmTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.lastKnownNmTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeLastKnownNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.lastKnownNmTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.lastKnownNmTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.lastKnownNmTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.lastKnownNmTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearLastKnownNmTokenMasterKey() {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    this.lastKnownNmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.lastKnownNmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getLastKnownNmTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getLastKnownNmTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownNmTokenMasterKeyOrBuilder() {
                if (this.lastKnownNmTokenMasterKeyBuilder_ != null) {
                    return this.lastKnownNmTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.lastKnownNmTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getLastKnownNmTokenMasterKeyFieldBuilder() {
                if (this.lastKnownNmTokenMasterKeyBuilder_ == null) {
                    this.lastKnownNmTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.lastKnownNmTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.lastKnownNmTokenMasterKey_ = null;
                }
                return this.lastKnownNmTokenMasterKeyBuilder_;
            }
        }
    }
    
    public static final class NodeHeartbeatResponseProto extends GeneratedMessage implements NodeHeartbeatResponseProtoOrBuilder
    {
        private static final NodeHeartbeatResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NodeHeartbeatResponseProto> PARSER;
        private int bitField0_;
        public static final int RESPONSE_ID_FIELD_NUMBER = 1;
        private int responseId_;
        public static final int CONTAINER_TOKEN_MASTER_KEY_FIELD_NUMBER = 2;
        private YarnServerCommonProtos.MasterKeyProto containerTokenMasterKey_;
        public static final int NM_TOKEN_MASTER_KEY_FIELD_NUMBER = 3;
        private YarnServerCommonProtos.MasterKeyProto nmTokenMasterKey_;
        public static final int NODEACTION_FIELD_NUMBER = 4;
        private YarnServerCommonProtos.NodeActionProto nodeAction_;
        public static final int CONTAINERS_TO_CLEANUP_FIELD_NUMBER = 5;
        private List<YarnProtos.ContainerIdProto> containersToCleanup_;
        public static final int APPLICATIONS_TO_CLEANUP_FIELD_NUMBER = 6;
        private List<YarnProtos.ApplicationIdProto> applicationsToCleanup_;
        public static final int NEXTHEARTBEATINTERVAL_FIELD_NUMBER = 7;
        private long nextHeartBeatInterval_;
        public static final int DIAGNOSTICS_MESSAGE_FIELD_NUMBER = 8;
        private Object diagnosticsMessage_;
        public static final int CONTAINERS_TO_BE_REMOVED_FROM_NM_FIELD_NUMBER = 9;
        private List<YarnProtos.ContainerIdProto> containersToBeRemovedFromNm_;
        public static final int SYSTEM_CREDENTIALS_FOR_APPS_FIELD_NUMBER = 10;
        private List<SystemCredentialsForAppsProto> systemCredentialsForApps_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NodeHeartbeatResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NodeHeartbeatResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NodeHeartbeatResponseProto getDefaultInstance() {
            return NodeHeartbeatResponseProto.defaultInstance;
        }
        
        @Override
        public NodeHeartbeatResponseProto getDefaultInstanceForType() {
            return NodeHeartbeatResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NodeHeartbeatResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 8: {
                            this.bitField0_ |= 0x1;
                            this.responseId_ = input.readInt32();
                            continue;
                        }
                        case 18: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.containerTokenMasterKey_.toBuilder();
                            }
                            this.containerTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.containerTokenMasterKey_);
                                this.containerTokenMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder = this.nmTokenMasterKey_.toBuilder();
                            }
                            this.nmTokenMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.nmTokenMasterKey_);
                                this.nmTokenMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 32: {
                            final int rawValue = input.readEnum();
                            final YarnServerCommonProtos.NodeActionProto value = YarnServerCommonProtos.NodeActionProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(4, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x8;
                            this.nodeAction_ = value;
                            continue;
                        }
                        case 42: {
                            if ((mutable_bitField0_ & 0x10) != 0x10) {
                                this.containersToCleanup_ = new ArrayList<YarnProtos.ContainerIdProto>();
                                mutable_bitField0_ |= 0x10;
                            }
                            this.containersToCleanup_.add(input.readMessage(YarnProtos.ContainerIdProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 50: {
                            if ((mutable_bitField0_ & 0x20) != 0x20) {
                                this.applicationsToCleanup_ = new ArrayList<YarnProtos.ApplicationIdProto>();
                                mutable_bitField0_ |= 0x20;
                            }
                            this.applicationsToCleanup_.add(input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x10;
                            this.nextHeartBeatInterval_ = input.readInt64();
                            continue;
                        }
                        case 66: {
                            this.bitField0_ |= 0x20;
                            this.diagnosticsMessage_ = input.readBytes();
                            continue;
                        }
                        case 74: {
                            if ((mutable_bitField0_ & 0x100) != 0x100) {
                                this.containersToBeRemovedFromNm_ = new ArrayList<YarnProtos.ContainerIdProto>();
                                mutable_bitField0_ |= 0x100;
                            }
                            this.containersToBeRemovedFromNm_.add(input.readMessage(YarnProtos.ContainerIdProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 82: {
                            if ((mutable_bitField0_ & 0x200) != 0x200) {
                                this.systemCredentialsForApps_ = new ArrayList<SystemCredentialsForAppsProto>();
                                mutable_bitField0_ |= 0x200;
                            }
                            this.systemCredentialsForApps_.add(input.readMessage(SystemCredentialsForAppsProto.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x10) == 0x10) {
                    this.containersToCleanup_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToCleanup_);
                }
                if ((mutable_bitField0_ & 0x20) == 0x20) {
                    this.applicationsToCleanup_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.applicationsToCleanup_);
                }
                if ((mutable_bitField0_ & 0x100) == 0x100) {
                    this.containersToBeRemovedFromNm_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToBeRemovedFromNm_);
                }
                if ((mutable_bitField0_ & 0x200) == 0x200) {
                    this.systemCredentialsForApps_ = Collections.unmodifiableList((List<? extends SystemCredentialsForAppsProto>)this.systemCredentialsForApps_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHeartbeatResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<NodeHeartbeatResponseProto> getParserForType() {
            return NodeHeartbeatResponseProto.PARSER;
        }
        
        @Override
        public boolean hasResponseId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getResponseId() {
            return this.responseId_;
        }
        
        @Override
        public boolean hasContainerTokenMasterKey() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey() {
            return this.containerTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder() {
            return this.containerTokenMasterKey_;
        }
        
        @Override
        public boolean hasNmTokenMasterKey() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey() {
            return this.nmTokenMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder() {
            return this.nmTokenMasterKey_;
        }
        
        @Override
        public boolean hasNodeAction() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public YarnServerCommonProtos.NodeActionProto getNodeAction() {
            return this.nodeAction_;
        }
        
        @Override
        public List<YarnProtos.ContainerIdProto> getContainersToCleanupList() {
            return this.containersToCleanup_;
        }
        
        @Override
        public List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToCleanupOrBuilderList() {
            return this.containersToCleanup_;
        }
        
        @Override
        public int getContainersToCleanupCount() {
            return this.containersToCleanup_.size();
        }
        
        @Override
        public YarnProtos.ContainerIdProto getContainersToCleanup(final int index) {
            return this.containersToCleanup_.get(index);
        }
        
        @Override
        public YarnProtos.ContainerIdProtoOrBuilder getContainersToCleanupOrBuilder(final int index) {
            return this.containersToCleanup_.get(index);
        }
        
        @Override
        public List<YarnProtos.ApplicationIdProto> getApplicationsToCleanupList() {
            return this.applicationsToCleanup_;
        }
        
        @Override
        public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getApplicationsToCleanupOrBuilderList() {
            return this.applicationsToCleanup_;
        }
        
        @Override
        public int getApplicationsToCleanupCount() {
            return this.applicationsToCleanup_.size();
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getApplicationsToCleanup(final int index) {
            return this.applicationsToCleanup_.get(index);
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getApplicationsToCleanupOrBuilder(final int index) {
            return this.applicationsToCleanup_.get(index);
        }
        
        @Override
        public boolean hasNextHeartBeatInterval() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public long getNextHeartBeatInterval() {
            return this.nextHeartBeatInterval_;
        }
        
        @Override
        public boolean hasDiagnosticsMessage() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public String getDiagnosticsMessage() {
            final Object ref = this.diagnosticsMessage_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsMessage_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsMessageBytes() {
            final Object ref = this.diagnosticsMessage_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsMessage_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public List<YarnProtos.ContainerIdProto> getContainersToBeRemovedFromNmList() {
            return this.containersToBeRemovedFromNm_;
        }
        
        @Override
        public List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToBeRemovedFromNmOrBuilderList() {
            return this.containersToBeRemovedFromNm_;
        }
        
        @Override
        public int getContainersToBeRemovedFromNmCount() {
            return this.containersToBeRemovedFromNm_.size();
        }
        
        @Override
        public YarnProtos.ContainerIdProto getContainersToBeRemovedFromNm(final int index) {
            return this.containersToBeRemovedFromNm_.get(index);
        }
        
        @Override
        public YarnProtos.ContainerIdProtoOrBuilder getContainersToBeRemovedFromNmOrBuilder(final int index) {
            return this.containersToBeRemovedFromNm_.get(index);
        }
        
        @Override
        public List<SystemCredentialsForAppsProto> getSystemCredentialsForAppsList() {
            return this.systemCredentialsForApps_;
        }
        
        @Override
        public List<? extends SystemCredentialsForAppsProtoOrBuilder> getSystemCredentialsForAppsOrBuilderList() {
            return this.systemCredentialsForApps_;
        }
        
        @Override
        public int getSystemCredentialsForAppsCount() {
            return this.systemCredentialsForApps_.size();
        }
        
        @Override
        public SystemCredentialsForAppsProto getSystemCredentialsForApps(final int index) {
            return this.systemCredentialsForApps_.get(index);
        }
        
        @Override
        public SystemCredentialsForAppsProtoOrBuilder getSystemCredentialsForAppsOrBuilder(final int index) {
            return this.systemCredentialsForApps_.get(index);
        }
        
        private void initFields() {
            this.responseId_ = 0;
            this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
            this.containersToCleanup_ = Collections.emptyList();
            this.applicationsToCleanup_ = Collections.emptyList();
            this.nextHeartBeatInterval_ = 0L;
            this.diagnosticsMessage_ = "";
            this.containersToBeRemovedFromNm_ = Collections.emptyList();
            this.systemCredentialsForApps_ = Collections.emptyList();
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
                output.writeInt32(1, this.responseId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.containerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.nmTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeEnum(4, this.nodeAction_.getNumber());
            }
            for (int i = 0; i < this.containersToCleanup_.size(); ++i) {
                output.writeMessage(5, this.containersToCleanup_.get(i));
            }
            for (int i = 0; i < this.applicationsToCleanup_.size(); ++i) {
                output.writeMessage(6, this.applicationsToCleanup_.get(i));
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeInt64(7, this.nextHeartBeatInterval_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(8, this.getDiagnosticsMessageBytes());
            }
            for (int i = 0; i < this.containersToBeRemovedFromNm_.size(); ++i) {
                output.writeMessage(9, this.containersToBeRemovedFromNm_.get(i));
            }
            for (int i = 0; i < this.systemCredentialsForApps_.size(); ++i) {
                output.writeMessage(10, this.systemCredentialsForApps_.get(i));
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
                size += CodedOutputStream.computeInt32Size(1, this.responseId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.containerTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.nmTokenMasterKey_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeEnumSize(4, this.nodeAction_.getNumber());
            }
            for (int i = 0; i < this.containersToCleanup_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(5, this.containersToCleanup_.get(i));
            }
            for (int i = 0; i < this.applicationsToCleanup_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(6, this.applicationsToCleanup_.get(i));
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeInt64Size(7, this.nextHeartBeatInterval_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(8, this.getDiagnosticsMessageBytes());
            }
            for (int i = 0; i < this.containersToBeRemovedFromNm_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(9, this.containersToBeRemovedFromNm_.get(i));
            }
            for (int i = 0; i < this.systemCredentialsForApps_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(10, this.systemCredentialsForApps_.get(i));
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
            if (!(obj instanceof NodeHeartbeatResponseProto)) {
                return super.equals(obj);
            }
            final NodeHeartbeatResponseProto other = (NodeHeartbeatResponseProto)obj;
            boolean result = true;
            result = (result && this.hasResponseId() == other.hasResponseId());
            if (this.hasResponseId()) {
                result = (result && this.getResponseId() == other.getResponseId());
            }
            result = (result && this.hasContainerTokenMasterKey() == other.hasContainerTokenMasterKey());
            if (this.hasContainerTokenMasterKey()) {
                result = (result && this.getContainerTokenMasterKey().equals(other.getContainerTokenMasterKey()));
            }
            result = (result && this.hasNmTokenMasterKey() == other.hasNmTokenMasterKey());
            if (this.hasNmTokenMasterKey()) {
                result = (result && this.getNmTokenMasterKey().equals(other.getNmTokenMasterKey()));
            }
            result = (result && this.hasNodeAction() == other.hasNodeAction());
            if (this.hasNodeAction()) {
                result = (result && this.getNodeAction() == other.getNodeAction());
            }
            result = (result && this.getContainersToCleanupList().equals(other.getContainersToCleanupList()));
            result = (result && this.getApplicationsToCleanupList().equals(other.getApplicationsToCleanupList()));
            result = (result && this.hasNextHeartBeatInterval() == other.hasNextHeartBeatInterval());
            if (this.hasNextHeartBeatInterval()) {
                result = (result && this.getNextHeartBeatInterval() == other.getNextHeartBeatInterval());
            }
            result = (result && this.hasDiagnosticsMessage() == other.hasDiagnosticsMessage());
            if (this.hasDiagnosticsMessage()) {
                result = (result && this.getDiagnosticsMessage().equals(other.getDiagnosticsMessage()));
            }
            result = (result && this.getContainersToBeRemovedFromNmList().equals(other.getContainersToBeRemovedFromNmList()));
            result = (result && this.getSystemCredentialsForAppsList().equals(other.getSystemCredentialsForAppsList()));
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
            if (this.hasResponseId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getResponseId();
            }
            if (this.hasContainerTokenMasterKey()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getContainerTokenMasterKey().hashCode();
            }
            if (this.hasNmTokenMasterKey()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getNmTokenMasterKey().hashCode();
            }
            if (this.hasNodeAction()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getNodeAction());
            }
            if (this.getContainersToCleanupCount() > 0) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getContainersToCleanupList().hashCode();
            }
            if (this.getApplicationsToCleanupCount() > 0) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getApplicationsToCleanupList().hashCode();
            }
            if (this.hasNextHeartBeatInterval()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getNextHeartBeatInterval());
            }
            if (this.hasDiagnosticsMessage()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + this.getDiagnosticsMessage().hashCode();
            }
            if (this.getContainersToBeRemovedFromNmCount() > 0) {
                hash = 37 * hash + 9;
                hash = 53 * hash + this.getContainersToBeRemovedFromNmList().hashCode();
            }
            if (this.getSystemCredentialsForAppsCount() > 0) {
                hash = 37 * hash + 10;
                hash = 53 * hash + this.getSystemCredentialsForAppsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(data);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(data);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final InputStream input) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(input);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NodeHeartbeatResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NodeHeartbeatResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(input);
        }
        
        public static NodeHeartbeatResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NodeHeartbeatResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NodeHeartbeatResponseProto prototype) {
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
            NodeHeartbeatResponseProto.PARSER = new AbstractParser<NodeHeartbeatResponseProto>() {
                @Override
                public NodeHeartbeatResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NodeHeartbeatResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NodeHeartbeatResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NodeHeartbeatResponseProtoOrBuilder
        {
            private int bitField0_;
            private int responseId_;
            private YarnServerCommonProtos.MasterKeyProto containerTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> containerTokenMasterKeyBuilder_;
            private YarnServerCommonProtos.MasterKeyProto nmTokenMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> nmTokenMasterKeyBuilder_;
            private YarnServerCommonProtos.NodeActionProto nodeAction_;
            private List<YarnProtos.ContainerIdProto> containersToCleanup_;
            private RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containersToCleanupBuilder_;
            private List<YarnProtos.ApplicationIdProto> applicationsToCleanup_;
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> applicationsToCleanupBuilder_;
            private long nextHeartBeatInterval_;
            private Object diagnosticsMessage_;
            private List<YarnProtos.ContainerIdProto> containersToBeRemovedFromNm_;
            private RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containersToBeRemovedFromNmBuilder_;
            private List<SystemCredentialsForAppsProto> systemCredentialsForApps_;
            private RepeatedFieldBuilder<SystemCredentialsForAppsProto, SystemCredentialsForAppsProto.Builder, SystemCredentialsForAppsProtoOrBuilder> systemCredentialsForAppsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NodeHeartbeatResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.containersToCleanup_ = Collections.emptyList();
                this.applicationsToCleanup_ = Collections.emptyList();
                this.diagnosticsMessage_ = "";
                this.containersToBeRemovedFromNm_ = Collections.emptyList();
                this.systemCredentialsForApps_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.containersToCleanup_ = Collections.emptyList();
                this.applicationsToCleanup_ = Collections.emptyList();
                this.diagnosticsMessage_ = "";
                this.containersToBeRemovedFromNm_ = Collections.emptyList();
                this.systemCredentialsForApps_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NodeHeartbeatResponseProto.alwaysUseFieldBuilders) {
                    this.getContainerTokenMasterKeyFieldBuilder();
                    this.getNmTokenMasterKeyFieldBuilder();
                    this.getContainersToCleanupFieldBuilder();
                    this.getApplicationsToCleanupFieldBuilder();
                    this.getContainersToBeRemovedFromNmFieldBuilder();
                    this.getSystemCredentialsForAppsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.responseId_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.bitField0_ &= 0xFFFFFFF7;
                if (this.containersToCleanupBuilder_ == null) {
                    this.containersToCleanup_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                }
                else {
                    this.containersToCleanupBuilder_.clear();
                }
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.applicationsToCleanup_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                }
                else {
                    this.applicationsToCleanupBuilder_.clear();
                }
                this.nextHeartBeatInterval_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                this.diagnosticsMessage_ = "";
                this.bitField0_ &= 0xFFFFFF7F;
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.containersToBeRemovedFromNm_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFEFF;
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.clear();
                }
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.systemCredentialsForApps_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFDFF;
                }
                else {
                    this.systemCredentialsForAppsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NodeHeartbeatResponseProto_descriptor;
            }
            
            @Override
            public NodeHeartbeatResponseProto getDefaultInstanceForType() {
                return NodeHeartbeatResponseProto.getDefaultInstance();
            }
            
            @Override
            public NodeHeartbeatResponseProto build() {
                final NodeHeartbeatResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NodeHeartbeatResponseProto buildPartial() {
                final NodeHeartbeatResponseProto result = new NodeHeartbeatResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.responseId_ = this.responseId_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    result.containerTokenMasterKey_ = this.containerTokenMasterKey_;
                }
                else {
                    result.containerTokenMasterKey_ = this.containerTokenMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    result.nmTokenMasterKey_ = this.nmTokenMasterKey_;
                }
                else {
                    result.nmTokenMasterKey_ = this.nmTokenMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.nodeAction_ = this.nodeAction_;
                if (this.containersToCleanupBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) == 0x10) {
                        this.containersToCleanup_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToCleanup_);
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    result.containersToCleanup_ = this.containersToCleanup_;
                }
                else {
                    result.containersToCleanup_ = this.containersToCleanupBuilder_.build();
                }
                if (this.applicationsToCleanupBuilder_ == null) {
                    if ((this.bitField0_ & 0x20) == 0x20) {
                        this.applicationsToCleanup_ = Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.applicationsToCleanup_);
                        this.bitField0_ &= 0xFFFFFFDF;
                    }
                    result.applicationsToCleanup_ = this.applicationsToCleanup_;
                }
                else {
                    result.applicationsToCleanup_ = this.applicationsToCleanupBuilder_.build();
                }
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x10;
                }
                result.nextHeartBeatInterval_ = this.nextHeartBeatInterval_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x20;
                }
                result.diagnosticsMessage_ = this.diagnosticsMessage_;
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    if ((this.bitField0_ & 0x100) == 0x100) {
                        this.containersToBeRemovedFromNm_ = Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToBeRemovedFromNm_);
                        this.bitField0_ &= 0xFFFFFEFF;
                    }
                    result.containersToBeRemovedFromNm_ = this.containersToBeRemovedFromNm_;
                }
                else {
                    result.containersToBeRemovedFromNm_ = this.containersToBeRemovedFromNmBuilder_.build();
                }
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    if ((this.bitField0_ & 0x200) == 0x200) {
                        this.systemCredentialsForApps_ = Collections.unmodifiableList((List<? extends SystemCredentialsForAppsProto>)this.systemCredentialsForApps_);
                        this.bitField0_ &= 0xFFFFFDFF;
                    }
                    result.systemCredentialsForApps_ = this.systemCredentialsForApps_;
                }
                else {
                    result.systemCredentialsForApps_ = this.systemCredentialsForAppsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NodeHeartbeatResponseProto) {
                    return this.mergeFrom((NodeHeartbeatResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NodeHeartbeatResponseProto other) {
                if (other == NodeHeartbeatResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasResponseId()) {
                    this.setResponseId(other.getResponseId());
                }
                if (other.hasContainerTokenMasterKey()) {
                    this.mergeContainerTokenMasterKey(other.getContainerTokenMasterKey());
                }
                if (other.hasNmTokenMasterKey()) {
                    this.mergeNmTokenMasterKey(other.getNmTokenMasterKey());
                }
                if (other.hasNodeAction()) {
                    this.setNodeAction(other.getNodeAction());
                }
                if (this.containersToCleanupBuilder_ == null) {
                    if (!other.containersToCleanup_.isEmpty()) {
                        if (this.containersToCleanup_.isEmpty()) {
                            this.containersToCleanup_ = other.containersToCleanup_;
                            this.bitField0_ &= 0xFFFFFFEF;
                        }
                        else {
                            this.ensureContainersToCleanupIsMutable();
                            this.containersToCleanup_.addAll(other.containersToCleanup_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.containersToCleanup_.isEmpty()) {
                    if (this.containersToCleanupBuilder_.isEmpty()) {
                        this.containersToCleanupBuilder_.dispose();
                        this.containersToCleanupBuilder_ = null;
                        this.containersToCleanup_ = other.containersToCleanup_;
                        this.bitField0_ &= 0xFFFFFFEF;
                        this.containersToCleanupBuilder_ = (NodeHeartbeatResponseProto.alwaysUseFieldBuilders ? this.getContainersToCleanupFieldBuilder() : null);
                    }
                    else {
                        this.containersToCleanupBuilder_.addAllMessages(other.containersToCleanup_);
                    }
                }
                if (this.applicationsToCleanupBuilder_ == null) {
                    if (!other.applicationsToCleanup_.isEmpty()) {
                        if (this.applicationsToCleanup_.isEmpty()) {
                            this.applicationsToCleanup_ = other.applicationsToCleanup_;
                            this.bitField0_ &= 0xFFFFFFDF;
                        }
                        else {
                            this.ensureApplicationsToCleanupIsMutable();
                            this.applicationsToCleanup_.addAll(other.applicationsToCleanup_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.applicationsToCleanup_.isEmpty()) {
                    if (this.applicationsToCleanupBuilder_.isEmpty()) {
                        this.applicationsToCleanupBuilder_.dispose();
                        this.applicationsToCleanupBuilder_ = null;
                        this.applicationsToCleanup_ = other.applicationsToCleanup_;
                        this.bitField0_ &= 0xFFFFFFDF;
                        this.applicationsToCleanupBuilder_ = (NodeHeartbeatResponseProto.alwaysUseFieldBuilders ? this.getApplicationsToCleanupFieldBuilder() : null);
                    }
                    else {
                        this.applicationsToCleanupBuilder_.addAllMessages(other.applicationsToCleanup_);
                    }
                }
                if (other.hasNextHeartBeatInterval()) {
                    this.setNextHeartBeatInterval(other.getNextHeartBeatInterval());
                }
                if (other.hasDiagnosticsMessage()) {
                    this.bitField0_ |= 0x80;
                    this.diagnosticsMessage_ = other.diagnosticsMessage_;
                    this.onChanged();
                }
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    if (!other.containersToBeRemovedFromNm_.isEmpty()) {
                        if (this.containersToBeRemovedFromNm_.isEmpty()) {
                            this.containersToBeRemovedFromNm_ = other.containersToBeRemovedFromNm_;
                            this.bitField0_ &= 0xFFFFFEFF;
                        }
                        else {
                            this.ensureContainersToBeRemovedFromNmIsMutable();
                            this.containersToBeRemovedFromNm_.addAll(other.containersToBeRemovedFromNm_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.containersToBeRemovedFromNm_.isEmpty()) {
                    if (this.containersToBeRemovedFromNmBuilder_.isEmpty()) {
                        this.containersToBeRemovedFromNmBuilder_.dispose();
                        this.containersToBeRemovedFromNmBuilder_ = null;
                        this.containersToBeRemovedFromNm_ = other.containersToBeRemovedFromNm_;
                        this.bitField0_ &= 0xFFFFFEFF;
                        this.containersToBeRemovedFromNmBuilder_ = (NodeHeartbeatResponseProto.alwaysUseFieldBuilders ? this.getContainersToBeRemovedFromNmFieldBuilder() : null);
                    }
                    else {
                        this.containersToBeRemovedFromNmBuilder_.addAllMessages(other.containersToBeRemovedFromNm_);
                    }
                }
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    if (!other.systemCredentialsForApps_.isEmpty()) {
                        if (this.systemCredentialsForApps_.isEmpty()) {
                            this.systemCredentialsForApps_ = other.systemCredentialsForApps_;
                            this.bitField0_ &= 0xFFFFFDFF;
                        }
                        else {
                            this.ensureSystemCredentialsForAppsIsMutable();
                            this.systemCredentialsForApps_.addAll(other.systemCredentialsForApps_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.systemCredentialsForApps_.isEmpty()) {
                    if (this.systemCredentialsForAppsBuilder_.isEmpty()) {
                        this.systemCredentialsForAppsBuilder_.dispose();
                        this.systemCredentialsForAppsBuilder_ = null;
                        this.systemCredentialsForApps_ = other.systemCredentialsForApps_;
                        this.bitField0_ &= 0xFFFFFDFF;
                        this.systemCredentialsForAppsBuilder_ = (NodeHeartbeatResponseProto.alwaysUseFieldBuilders ? this.getSystemCredentialsForAppsFieldBuilder() : null);
                    }
                    else {
                        this.systemCredentialsForAppsBuilder_.addAllMessages(other.systemCredentialsForApps_);
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
                NodeHeartbeatResponseProto parsedMessage = null;
                try {
                    parsedMessage = NodeHeartbeatResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NodeHeartbeatResponseProto)e.getUnfinishedMessage();
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
            public boolean hasResponseId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getResponseId() {
                return this.responseId_;
            }
            
            public Builder setResponseId(final int value) {
                this.bitField0_ |= 0x1;
                this.responseId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearResponseId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.responseId_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerTokenMasterKey() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    return this.containerTokenMasterKey_;
                }
                return this.containerTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.containerTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeContainerTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.containerTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.containerTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.containerTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearContainerTokenMasterKey() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.containerTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getContainerTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getContainerTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder() {
                if (this.containerTokenMasterKeyBuilder_ != null) {
                    return this.containerTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.containerTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getContainerTokenMasterKeyFieldBuilder() {
                if (this.containerTokenMasterKeyBuilder_ == null) {
                    this.containerTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.containerTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.containerTokenMasterKey_ = null;
                }
                return this.containerTokenMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasNmTokenMasterKey() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    return this.nmTokenMasterKey_;
                }
                return this.nmTokenMasterKeyBuilder_.getMessage();
            }
            
            public Builder setNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nmTokenMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeNmTokenMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.nmTokenMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.nmTokenMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nmTokenMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearNmTokenMasterKey() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nmTokenMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getNmTokenMasterKeyBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getNmTokenMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder() {
                if (this.nmTokenMasterKeyBuilder_ != null) {
                    return this.nmTokenMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.nmTokenMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getNmTokenMasterKeyFieldBuilder() {
                if (this.nmTokenMasterKeyBuilder_ == null) {
                    this.nmTokenMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.nmTokenMasterKey_, this.getParentForChildren(), this.isClean());
                    this.nmTokenMasterKey_ = null;
                }
                return this.nmTokenMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasNodeAction() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public YarnServerCommonProtos.NodeActionProto getNodeAction() {
                return this.nodeAction_;
            }
            
            public Builder setNodeAction(final YarnServerCommonProtos.NodeActionProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.nodeAction_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNodeAction() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.nodeAction_ = YarnServerCommonProtos.NodeActionProto.NORMAL;
                this.onChanged();
                return this;
            }
            
            private void ensureContainersToCleanupIsMutable() {
                if ((this.bitField0_ & 0x10) != 0x10) {
                    this.containersToCleanup_ = new ArrayList<YarnProtos.ContainerIdProto>(this.containersToCleanup_);
                    this.bitField0_ |= 0x10;
                }
            }
            
            @Override
            public List<YarnProtos.ContainerIdProto> getContainersToCleanupList() {
                if (this.containersToCleanupBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToCleanup_);
                }
                return this.containersToCleanupBuilder_.getMessageList();
            }
            
            @Override
            public int getContainersToCleanupCount() {
                if (this.containersToCleanupBuilder_ == null) {
                    return this.containersToCleanup_.size();
                }
                return this.containersToCleanupBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ContainerIdProto getContainersToCleanup(final int index) {
                if (this.containersToCleanupBuilder_ == null) {
                    return this.containersToCleanup_.get(index);
                }
                return this.containersToCleanupBuilder_.getMessage(index);
            }
            
            public Builder setContainersToCleanup(final int index, final YarnProtos.ContainerIdProto value) {
                if (this.containersToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setContainersToCleanup(final int index, final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToCleanupBuilder_ == null) {
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersToCleanup(final YarnProtos.ContainerIdProto value) {
                if (this.containersToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.add(value);
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addContainersToCleanup(final int index, final YarnProtos.ContainerIdProto value) {
                if (this.containersToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addContainersToCleanup(final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToCleanupBuilder_ == null) {
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersToCleanup(final int index, final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToCleanupBuilder_ == null) {
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllContainersToCleanup(final Iterable<? extends YarnProtos.ContainerIdProto> values) {
                if (this.containersToCleanupBuilder_ == null) {
                    this.ensureContainersToCleanupIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.containersToCleanup_);
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearContainersToCleanup() {
                if (this.containersToCleanupBuilder_ == null) {
                    this.containersToCleanup_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeContainersToCleanup(final int index) {
                if (this.containersToCleanupBuilder_ == null) {
                    this.ensureContainersToCleanupIsMutable();
                    this.containersToCleanup_.remove(index);
                    this.onChanged();
                }
                else {
                    this.containersToCleanupBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ContainerIdProto.Builder getContainersToCleanupBuilder(final int index) {
                return this.getContainersToCleanupFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ContainerIdProtoOrBuilder getContainersToCleanupOrBuilder(final int index) {
                if (this.containersToCleanupBuilder_ == null) {
                    return this.containersToCleanup_.get(index);
                }
                return this.containersToCleanupBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToCleanupOrBuilderList() {
                if (this.containersToCleanupBuilder_ != null) {
                    return this.containersToCleanupBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProtoOrBuilder>)this.containersToCleanup_);
            }
            
            public YarnProtos.ContainerIdProto.Builder addContainersToCleanupBuilder() {
                return this.getContainersToCleanupFieldBuilder().addBuilder(YarnProtos.ContainerIdProto.getDefaultInstance());
            }
            
            public YarnProtos.ContainerIdProto.Builder addContainersToCleanupBuilder(final int index) {
                return this.getContainersToCleanupFieldBuilder().addBuilder(index, YarnProtos.ContainerIdProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ContainerIdProto.Builder> getContainersToCleanupBuilderList() {
                return this.getContainersToCleanupFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> getContainersToCleanupFieldBuilder() {
                if (this.containersToCleanupBuilder_ == null) {
                    this.containersToCleanupBuilder_ = new RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder>(this.containersToCleanup_, (this.bitField0_ & 0x10) == 0x10, this.getParentForChildren(), this.isClean());
                    this.containersToCleanup_ = null;
                }
                return this.containersToCleanupBuilder_;
            }
            
            private void ensureApplicationsToCleanupIsMutable() {
                if ((this.bitField0_ & 0x20) != 0x20) {
                    this.applicationsToCleanup_ = new ArrayList<YarnProtos.ApplicationIdProto>(this.applicationsToCleanup_);
                    this.bitField0_ |= 0x20;
                }
            }
            
            @Override
            public List<YarnProtos.ApplicationIdProto> getApplicationsToCleanupList() {
                if (this.applicationsToCleanupBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProto>)this.applicationsToCleanup_);
                }
                return this.applicationsToCleanupBuilder_.getMessageList();
            }
            
            @Override
            public int getApplicationsToCleanupCount() {
                if (this.applicationsToCleanupBuilder_ == null) {
                    return this.applicationsToCleanup_.size();
                }
                return this.applicationsToCleanupBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getApplicationsToCleanup(final int index) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    return this.applicationsToCleanup_.get(index);
                }
                return this.applicationsToCleanupBuilder_.getMessage(index);
            }
            
            public Builder setApplicationsToCleanup(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setApplicationsToCleanup(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addApplicationsToCleanup(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.add(value);
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addApplicationsToCleanup(final int index, final YarnProtos.ApplicationIdProto value) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addApplicationsToCleanup(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addApplicationsToCleanup(final int index, final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllApplicationsToCleanup(final Iterable<? extends YarnProtos.ApplicationIdProto> values) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.ensureApplicationsToCleanupIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.applicationsToCleanup_);
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearApplicationsToCleanup() {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.applicationsToCleanup_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeApplicationsToCleanup(final int index) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.ensureApplicationsToCleanupIsMutable();
                    this.applicationsToCleanup_.remove(index);
                    this.onChanged();
                }
                else {
                    this.applicationsToCleanupBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getApplicationsToCleanupBuilder(final int index) {
                return this.getApplicationsToCleanupFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getApplicationsToCleanupOrBuilder(final int index) {
                if (this.applicationsToCleanupBuilder_ == null) {
                    return this.applicationsToCleanup_.get(index);
                }
                return this.applicationsToCleanupBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getApplicationsToCleanupOrBuilderList() {
                if (this.applicationsToCleanupBuilder_ != null) {
                    return this.applicationsToCleanupBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ApplicationIdProtoOrBuilder>)this.applicationsToCleanup_);
            }
            
            public YarnProtos.ApplicationIdProto.Builder addApplicationsToCleanupBuilder() {
                return this.getApplicationsToCleanupFieldBuilder().addBuilder(YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public YarnProtos.ApplicationIdProto.Builder addApplicationsToCleanupBuilder(final int index) {
                return this.getApplicationsToCleanupFieldBuilder().addBuilder(index, YarnProtos.ApplicationIdProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ApplicationIdProto.Builder> getApplicationsToCleanupBuilderList() {
                return this.getApplicationsToCleanupFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getApplicationsToCleanupFieldBuilder() {
                if (this.applicationsToCleanupBuilder_ == null) {
                    this.applicationsToCleanupBuilder_ = new RepeatedFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.applicationsToCleanup_, (this.bitField0_ & 0x20) == 0x20, this.getParentForChildren(), this.isClean());
                    this.applicationsToCleanup_ = null;
                }
                return this.applicationsToCleanupBuilder_;
            }
            
            @Override
            public boolean hasNextHeartBeatInterval() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getNextHeartBeatInterval() {
                return this.nextHeartBeatInterval_;
            }
            
            public Builder setNextHeartBeatInterval(final long value) {
                this.bitField0_ |= 0x40;
                this.nextHeartBeatInterval_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNextHeartBeatInterval() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.nextHeartBeatInterval_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsMessage() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public String getDiagnosticsMessage() {
                final Object ref = this.diagnosticsMessage_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsMessage_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsMessageBytes() {
                final Object ref = this.diagnosticsMessage_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsMessage_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsMessage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x80;
                this.diagnosticsMessage_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsMessage() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.diagnosticsMessage_ = NodeHeartbeatResponseProto.getDefaultInstance().getDiagnosticsMessage();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsMessageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x80;
                this.diagnosticsMessage_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureContainersToBeRemovedFromNmIsMutable() {
                if ((this.bitField0_ & 0x100) != 0x100) {
                    this.containersToBeRemovedFromNm_ = new ArrayList<YarnProtos.ContainerIdProto>(this.containersToBeRemovedFromNm_);
                    this.bitField0_ |= 0x100;
                }
            }
            
            @Override
            public List<YarnProtos.ContainerIdProto> getContainersToBeRemovedFromNmList() {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProto>)this.containersToBeRemovedFromNm_);
                }
                return this.containersToBeRemovedFromNmBuilder_.getMessageList();
            }
            
            @Override
            public int getContainersToBeRemovedFromNmCount() {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    return this.containersToBeRemovedFromNm_.size();
                }
                return this.containersToBeRemovedFromNmBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.ContainerIdProto getContainersToBeRemovedFromNm(final int index) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    return this.containersToBeRemovedFromNm_.get(index);
                }
                return this.containersToBeRemovedFromNmBuilder_.getMessage(index);
            }
            
            public Builder setContainersToBeRemovedFromNm(final int index, final YarnProtos.ContainerIdProto value) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setContainersToBeRemovedFromNm(final int index, final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersToBeRemovedFromNm(final YarnProtos.ContainerIdProto value) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.add(value);
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addContainersToBeRemovedFromNm(final int index, final YarnProtos.ContainerIdProto value) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addContainersToBeRemovedFromNm(final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addContainersToBeRemovedFromNm(final int index, final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllContainersToBeRemovedFromNm(final Iterable<? extends YarnProtos.ContainerIdProto> values) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.containersToBeRemovedFromNm_);
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearContainersToBeRemovedFromNm() {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.containersToBeRemovedFromNm_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFEFF;
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeContainersToBeRemovedFromNm(final int index) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.ensureContainersToBeRemovedFromNmIsMutable();
                    this.containersToBeRemovedFromNm_.remove(index);
                    this.onChanged();
                }
                else {
                    this.containersToBeRemovedFromNmBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.ContainerIdProto.Builder getContainersToBeRemovedFromNmBuilder(final int index) {
                return this.getContainersToBeRemovedFromNmFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.ContainerIdProtoOrBuilder getContainersToBeRemovedFromNmOrBuilder(final int index) {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    return this.containersToBeRemovedFromNm_.get(index);
                }
                return this.containersToBeRemovedFromNmBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToBeRemovedFromNmOrBuilderList() {
                if (this.containersToBeRemovedFromNmBuilder_ != null) {
                    return this.containersToBeRemovedFromNmBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.ContainerIdProtoOrBuilder>)this.containersToBeRemovedFromNm_);
            }
            
            public YarnProtos.ContainerIdProto.Builder addContainersToBeRemovedFromNmBuilder() {
                return this.getContainersToBeRemovedFromNmFieldBuilder().addBuilder(YarnProtos.ContainerIdProto.getDefaultInstance());
            }
            
            public YarnProtos.ContainerIdProto.Builder addContainersToBeRemovedFromNmBuilder(final int index) {
                return this.getContainersToBeRemovedFromNmFieldBuilder().addBuilder(index, YarnProtos.ContainerIdProto.getDefaultInstance());
            }
            
            public List<YarnProtos.ContainerIdProto.Builder> getContainersToBeRemovedFromNmBuilderList() {
                return this.getContainersToBeRemovedFromNmFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> getContainersToBeRemovedFromNmFieldBuilder() {
                if (this.containersToBeRemovedFromNmBuilder_ == null) {
                    this.containersToBeRemovedFromNmBuilder_ = new RepeatedFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder>(this.containersToBeRemovedFromNm_, (this.bitField0_ & 0x100) == 0x100, this.getParentForChildren(), this.isClean());
                    this.containersToBeRemovedFromNm_ = null;
                }
                return this.containersToBeRemovedFromNmBuilder_;
            }
            
            private void ensureSystemCredentialsForAppsIsMutable() {
                if ((this.bitField0_ & 0x200) != 0x200) {
                    this.systemCredentialsForApps_ = new ArrayList<SystemCredentialsForAppsProto>(this.systemCredentialsForApps_);
                    this.bitField0_ |= 0x200;
                }
            }
            
            @Override
            public List<SystemCredentialsForAppsProto> getSystemCredentialsForAppsList() {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends SystemCredentialsForAppsProto>)this.systemCredentialsForApps_);
                }
                return this.systemCredentialsForAppsBuilder_.getMessageList();
            }
            
            @Override
            public int getSystemCredentialsForAppsCount() {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    return this.systemCredentialsForApps_.size();
                }
                return this.systemCredentialsForAppsBuilder_.getCount();
            }
            
            @Override
            public SystemCredentialsForAppsProto getSystemCredentialsForApps(final int index) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    return this.systemCredentialsForApps_.get(index);
                }
                return this.systemCredentialsForAppsBuilder_.getMessage(index);
            }
            
            public Builder setSystemCredentialsForApps(final int index, final SystemCredentialsForAppsProto value) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setSystemCredentialsForApps(final int index, final SystemCredentialsForAppsProto.Builder builderForValue) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addSystemCredentialsForApps(final SystemCredentialsForAppsProto value) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.add(value);
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addSystemCredentialsForApps(final int index, final SystemCredentialsForAppsProto value) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addSystemCredentialsForApps(final SystemCredentialsForAppsProto.Builder builderForValue) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addSystemCredentialsForApps(final int index, final SystemCredentialsForAppsProto.Builder builderForValue) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllSystemCredentialsForApps(final Iterable<? extends SystemCredentialsForAppsProto> values) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.ensureSystemCredentialsForAppsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.systemCredentialsForApps_);
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearSystemCredentialsForApps() {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.systemCredentialsForApps_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFDFF;
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeSystemCredentialsForApps(final int index) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.ensureSystemCredentialsForAppsIsMutable();
                    this.systemCredentialsForApps_.remove(index);
                    this.onChanged();
                }
                else {
                    this.systemCredentialsForAppsBuilder_.remove(index);
                }
                return this;
            }
            
            public SystemCredentialsForAppsProto.Builder getSystemCredentialsForAppsBuilder(final int index) {
                return this.getSystemCredentialsForAppsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public SystemCredentialsForAppsProtoOrBuilder getSystemCredentialsForAppsOrBuilder(final int index) {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    return this.systemCredentialsForApps_.get(index);
                }
                return this.systemCredentialsForAppsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends SystemCredentialsForAppsProtoOrBuilder> getSystemCredentialsForAppsOrBuilderList() {
                if (this.systemCredentialsForAppsBuilder_ != null) {
                    return this.systemCredentialsForAppsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends SystemCredentialsForAppsProtoOrBuilder>)this.systemCredentialsForApps_);
            }
            
            public SystemCredentialsForAppsProto.Builder addSystemCredentialsForAppsBuilder() {
                return this.getSystemCredentialsForAppsFieldBuilder().addBuilder(SystemCredentialsForAppsProto.getDefaultInstance());
            }
            
            public SystemCredentialsForAppsProto.Builder addSystemCredentialsForAppsBuilder(final int index) {
                return this.getSystemCredentialsForAppsFieldBuilder().addBuilder(index, SystemCredentialsForAppsProto.getDefaultInstance());
            }
            
            public List<SystemCredentialsForAppsProto.Builder> getSystemCredentialsForAppsBuilderList() {
                return this.getSystemCredentialsForAppsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<SystemCredentialsForAppsProto, SystemCredentialsForAppsProto.Builder, SystemCredentialsForAppsProtoOrBuilder> getSystemCredentialsForAppsFieldBuilder() {
                if (this.systemCredentialsForAppsBuilder_ == null) {
                    this.systemCredentialsForAppsBuilder_ = new RepeatedFieldBuilder<SystemCredentialsForAppsProto, SystemCredentialsForAppsProto.Builder, SystemCredentialsForAppsProtoOrBuilder>(this.systemCredentialsForApps_, (this.bitField0_ & 0x200) == 0x200, this.getParentForChildren(), this.isClean());
                    this.systemCredentialsForApps_ = null;
                }
                return this.systemCredentialsForAppsBuilder_;
            }
        }
    }
    
    public static final class SystemCredentialsForAppsProto extends GeneratedMessage implements SystemCredentialsForAppsProtoOrBuilder
    {
        private static final SystemCredentialsForAppsProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<SystemCredentialsForAppsProto> PARSER;
        private int bitField0_;
        public static final int APPID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationIdProto appId_;
        public static final int CREDENTIALSFORAPP_FIELD_NUMBER = 2;
        private ByteString credentialsForApp_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private SystemCredentialsForAppsProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private SystemCredentialsForAppsProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static SystemCredentialsForAppsProto getDefaultInstance() {
            return SystemCredentialsForAppsProto.defaultInstance;
        }
        
        @Override
        public SystemCredentialsForAppsProto getDefaultInstanceForType() {
            return SystemCredentialsForAppsProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private SystemCredentialsForAppsProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.ApplicationIdProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.appId_.toBuilder();
                            }
                            this.appId_ = input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.appId_);
                                this.appId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.credentialsForApp_ = input.readBytes();
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
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_fieldAccessorTable.ensureFieldAccessorsInitialized(SystemCredentialsForAppsProto.class, Builder.class);
        }
        
        @Override
        public Parser<SystemCredentialsForAppsProto> getParserForType() {
            return SystemCredentialsForAppsProto.PARSER;
        }
        
        @Override
        public boolean hasAppId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getAppId() {
            return this.appId_;
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getAppIdOrBuilder() {
            return this.appId_;
        }
        
        @Override
        public boolean hasCredentialsForApp() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public ByteString getCredentialsForApp() {
            return this.credentialsForApp_;
        }
        
        private void initFields() {
            this.appId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
            this.credentialsForApp_ = ByteString.EMPTY;
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
                output.writeMessage(1, this.appId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.credentialsForApp_);
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
                size += CodedOutputStream.computeMessageSize(1, this.appId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.credentialsForApp_);
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
            if (!(obj instanceof SystemCredentialsForAppsProto)) {
                return super.equals(obj);
            }
            final SystemCredentialsForAppsProto other = (SystemCredentialsForAppsProto)obj;
            boolean result = true;
            result = (result && this.hasAppId() == other.hasAppId());
            if (this.hasAppId()) {
                result = (result && this.getAppId().equals(other.getAppId()));
            }
            result = (result && this.hasCredentialsForApp() == other.hasCredentialsForApp());
            if (this.hasCredentialsForApp()) {
                result = (result && this.getCredentialsForApp().equals(other.getCredentialsForApp()));
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
            if (this.hasAppId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getAppId().hashCode();
            }
            if (this.hasCredentialsForApp()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getCredentialsForApp().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(data);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(data);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final InputStream input) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(input);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static SystemCredentialsForAppsProto parseDelimitedFrom(final InputStream input) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static SystemCredentialsForAppsProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final CodedInputStream input) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(input);
        }
        
        public static SystemCredentialsForAppsProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SystemCredentialsForAppsProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final SystemCredentialsForAppsProto prototype) {
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
            SystemCredentialsForAppsProto.PARSER = new AbstractParser<SystemCredentialsForAppsProto>() {
                @Override
                public SystemCredentialsForAppsProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new SystemCredentialsForAppsProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new SystemCredentialsForAppsProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements SystemCredentialsForAppsProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationIdProto appId_;
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> appIdBuilder_;
            private ByteString credentialsForApp_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_fieldAccessorTable.ensureFieldAccessorsInitialized(SystemCredentialsForAppsProto.class, Builder.class);
            }
            
            private Builder() {
                this.appId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.credentialsForApp_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.appId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.credentialsForApp_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (SystemCredentialsForAppsProto.alwaysUseFieldBuilders) {
                    this.getAppIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.appIdBuilder_ == null) {
                    this.appId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                }
                else {
                    this.appIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.credentialsForApp_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_SystemCredentialsForAppsProto_descriptor;
            }
            
            @Override
            public SystemCredentialsForAppsProto getDefaultInstanceForType() {
                return SystemCredentialsForAppsProto.getDefaultInstance();
            }
            
            @Override
            public SystemCredentialsForAppsProto build() {
                final SystemCredentialsForAppsProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public SystemCredentialsForAppsProto buildPartial() {
                final SystemCredentialsForAppsProto result = new SystemCredentialsForAppsProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.appIdBuilder_ == null) {
                    result.appId_ = this.appId_;
                }
                else {
                    result.appId_ = this.appIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.credentialsForApp_ = this.credentialsForApp_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof SystemCredentialsForAppsProto) {
                    return this.mergeFrom((SystemCredentialsForAppsProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final SystemCredentialsForAppsProto other) {
                if (other == SystemCredentialsForAppsProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAppId()) {
                    this.mergeAppId(other.getAppId());
                }
                if (other.hasCredentialsForApp()) {
                    this.setCredentialsForApp(other.getCredentialsForApp());
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
                SystemCredentialsForAppsProto parsedMessage = null;
                try {
                    parsedMessage = SystemCredentialsForAppsProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (SystemCredentialsForAppsProto)e.getUnfinishedMessage();
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
            public boolean hasAppId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getAppId() {
                if (this.appIdBuilder_ == null) {
                    return this.appId_;
                }
                return this.appIdBuilder_.getMessage();
            }
            
            public Builder setAppId(final YarnProtos.ApplicationIdProto value) {
                if (this.appIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.appId_ = value;
                    this.onChanged();
                }
                else {
                    this.appIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setAppId(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.appIdBuilder_ == null) {
                    this.appId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.appIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeAppId(final YarnProtos.ApplicationIdProto value) {
                if (this.appIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.appId_ != YarnProtos.ApplicationIdProto.getDefaultInstance()) {
                        this.appId_ = YarnProtos.ApplicationIdProto.newBuilder(this.appId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.appId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.appIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearAppId() {
                if (this.appIdBuilder_ == null) {
                    this.appId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.appIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getAppIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getAppIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getAppIdOrBuilder() {
                if (this.appIdBuilder_ != null) {
                    return this.appIdBuilder_.getMessageOrBuilder();
                }
                return this.appId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getAppIdFieldBuilder() {
                if (this.appIdBuilder_ == null) {
                    this.appIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.appId_, this.getParentForChildren(), this.isClean());
                    this.appId_ = null;
                }
                return this.appIdBuilder_;
            }
            
            @Override
            public boolean hasCredentialsForApp() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public ByteString getCredentialsForApp() {
                return this.credentialsForApp_;
            }
            
            public Builder setCredentialsForApp(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.credentialsForApp_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCredentialsForApp() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.credentialsForApp_ = SystemCredentialsForAppsProto.getDefaultInstance().getCredentialsForApp();
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class NMContainerStatusProto extends GeneratedMessage implements NMContainerStatusProtoOrBuilder
    {
        private static final NMContainerStatusProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<NMContainerStatusProto> PARSER;
        private int bitField0_;
        public static final int CONTAINER_ID_FIELD_NUMBER = 1;
        private YarnProtos.ContainerIdProto containerId_;
        public static final int CONTAINER_STATE_FIELD_NUMBER = 2;
        private YarnProtos.ContainerStateProto containerState_;
        public static final int RESOURCE_FIELD_NUMBER = 3;
        private YarnProtos.ResourceProto resource_;
        public static final int PRIORITY_FIELD_NUMBER = 4;
        private YarnProtos.PriorityProto priority_;
        public static final int DIAGNOSTICS_FIELD_NUMBER = 5;
        private Object diagnostics_;
        public static final int CONTAINER_EXIT_STATUS_FIELD_NUMBER = 6;
        private int containerExitStatus_;
        public static final int CREATION_TIME_FIELD_NUMBER = 7;
        private long creationTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private NMContainerStatusProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private NMContainerStatusProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static NMContainerStatusProto getDefaultInstance() {
            return NMContainerStatusProto.defaultInstance;
        }
        
        @Override
        public NMContainerStatusProto getDefaultInstanceForType() {
            return NMContainerStatusProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private NMContainerStatusProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        case 16: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.ContainerStateProto value = YarnProtos.ContainerStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(2, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x2;
                            this.containerState_ = value;
                            continue;
                        }
                        case 26: {
                            YarnProtos.ResourceProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder2 = this.resource_.toBuilder();
                            }
                            this.resource_ = input.readMessage(YarnProtos.ResourceProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.resource_);
                                this.resource_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 34: {
                            YarnProtos.PriorityProto.Builder subBuilder3 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder3 = this.priority_.toBuilder();
                            }
                            this.priority_ = input.readMessage(YarnProtos.PriorityProto.PARSER, extensionRegistry);
                            if (subBuilder3 != null) {
                                subBuilder3.mergeFrom(this.priority_);
                                this.priority_ = subBuilder3.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.diagnostics_ = input.readBytes();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.containerExitStatus_ = input.readInt32();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.creationTime_ = input.readInt64();
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
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NMContainerStatusProto.class, Builder.class);
        }
        
        @Override
        public Parser<NMContainerStatusProto> getParserForType() {
            return NMContainerStatusProto.PARSER;
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
        public boolean hasContainerState() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.ContainerStateProto getContainerState() {
            return this.containerState_;
        }
        
        @Override
        public boolean hasResource() {
            return (this.bitField0_ & 0x4) == 0x4;
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
        public boolean hasPriority() {
            return (this.bitField0_ & 0x8) == 0x8;
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
        public boolean hasDiagnostics() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getDiagnostics() {
            final Object ref = this.diagnostics_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnostics_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsBytes() {
            final Object ref = this.diagnostics_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnostics_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasContainerExitStatus() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public int getContainerExitStatus() {
            return this.containerExitStatus_;
        }
        
        @Override
        public boolean hasCreationTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getCreationTime() {
            return this.creationTime_;
        }
        
        private void initFields() {
            this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
            this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
            this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
            this.diagnostics_ = "N/A";
            this.containerExitStatus_ = 0;
            this.creationTime_ = 0L;
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
                output.writeEnum(2, this.containerState_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.resource_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt32(6, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.creationTime_);
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
                size += CodedOutputStream.computeEnumSize(2, this.containerState_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.resource_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt32Size(6, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.creationTime_);
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
            if (!(obj instanceof NMContainerStatusProto)) {
                return super.equals(obj);
            }
            final NMContainerStatusProto other = (NMContainerStatusProto)obj;
            boolean result = true;
            result = (result && this.hasContainerId() == other.hasContainerId());
            if (this.hasContainerId()) {
                result = (result && this.getContainerId().equals(other.getContainerId()));
            }
            result = (result && this.hasContainerState() == other.hasContainerState());
            if (this.hasContainerState()) {
                result = (result && this.getContainerState() == other.getContainerState());
            }
            result = (result && this.hasResource() == other.hasResource());
            if (this.hasResource()) {
                result = (result && this.getResource().equals(other.getResource()));
            }
            result = (result && this.hasPriority() == other.hasPriority());
            if (this.hasPriority()) {
                result = (result && this.getPriority().equals(other.getPriority()));
            }
            result = (result && this.hasDiagnostics() == other.hasDiagnostics());
            if (this.hasDiagnostics()) {
                result = (result && this.getDiagnostics().equals(other.getDiagnostics()));
            }
            result = (result && this.hasContainerExitStatus() == other.hasContainerExitStatus());
            if (this.hasContainerExitStatus()) {
                result = (result && this.getContainerExitStatus() == other.getContainerExitStatus());
            }
            result = (result && this.hasCreationTime() == other.hasCreationTime());
            if (this.hasCreationTime()) {
                result = (result && this.getCreationTime() == other.getCreationTime());
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
            if (this.hasContainerState()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getContainerState());
            }
            if (this.hasResource()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getResource().hashCode();
            }
            if (this.hasPriority()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getPriority().hashCode();
            }
            if (this.hasDiagnostics()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getDiagnostics().hashCode();
            }
            if (this.hasContainerExitStatus()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getContainerExitStatus();
            }
            if (this.hasCreationTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getCreationTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static NMContainerStatusProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return NMContainerStatusProto.PARSER.parseFrom(data);
        }
        
        public static NMContainerStatusProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NMContainerStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NMContainerStatusProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return NMContainerStatusProto.PARSER.parseFrom(data);
        }
        
        public static NMContainerStatusProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return NMContainerStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static NMContainerStatusProto parseFrom(final InputStream input) throws IOException {
            return NMContainerStatusProto.PARSER.parseFrom(input);
        }
        
        public static NMContainerStatusProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMContainerStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static NMContainerStatusProto parseDelimitedFrom(final InputStream input) throws IOException {
            return NMContainerStatusProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static NMContainerStatusProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMContainerStatusProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static NMContainerStatusProto parseFrom(final CodedInputStream input) throws IOException {
            return NMContainerStatusProto.PARSER.parseFrom(input);
        }
        
        public static NMContainerStatusProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return NMContainerStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final NMContainerStatusProto prototype) {
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
            NMContainerStatusProto.PARSER = new AbstractParser<NMContainerStatusProto>() {
                @Override
                public NMContainerStatusProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new NMContainerStatusProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new NMContainerStatusProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements NMContainerStatusProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ContainerIdProto containerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containerIdBuilder_;
            private YarnProtos.ContainerStateProto containerState_;
            private YarnProtos.ResourceProto resource_;
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> resourceBuilder_;
            private YarnProtos.PriorityProto priority_;
            private SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder> priorityBuilder_;
            private Object diagnostics_;
            private int containerExitStatus_;
            private long creationTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(NMContainerStatusProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.diagnostics_ = "N/A";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.diagnostics_ = "N/A";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (NMContainerStatusProto.alwaysUseFieldBuilders) {
                    this.getContainerIdFieldBuilder();
                    this.getResourceFieldBuilder();
                    this.getPriorityFieldBuilder();
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
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.resourceBuilder_ == null) {
                    this.resource_ = YarnProtos.ResourceProto.getDefaultInstance();
                }
                else {
                    this.resourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.priorityBuilder_ == null) {
                    this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                }
                else {
                    this.priorityBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.diagnostics_ = "N/A";
                this.bitField0_ &= 0xFFFFFFEF;
                this.containerExitStatus_ = 0;
                this.bitField0_ &= 0xFFFFFFDF;
                this.creationTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerCommonServiceProtos.internal_static_hadoop_yarn_NMContainerStatusProto_descriptor;
            }
            
            @Override
            public NMContainerStatusProto getDefaultInstanceForType() {
                return NMContainerStatusProto.getDefaultInstance();
            }
            
            @Override
            public NMContainerStatusProto build() {
                final NMContainerStatusProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public NMContainerStatusProto buildPartial() {
                final NMContainerStatusProto result = new NMContainerStatusProto((GeneratedMessage.Builder)this);
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
                result.containerState_ = this.containerState_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.resourceBuilder_ == null) {
                    result.resource_ = this.resource_;
                }
                else {
                    result.resource_ = this.resourceBuilder_.build();
                }
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                if (this.priorityBuilder_ == null) {
                    result.priority_ = this.priority_;
                }
                else {
                    result.priority_ = this.priorityBuilder_.build();
                }
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.diagnostics_ = this.diagnostics_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.containerExitStatus_ = this.containerExitStatus_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.creationTime_ = this.creationTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof NMContainerStatusProto) {
                    return this.mergeFrom((NMContainerStatusProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final NMContainerStatusProto other) {
                if (other == NMContainerStatusProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerId()) {
                    this.mergeContainerId(other.getContainerId());
                }
                if (other.hasContainerState()) {
                    this.setContainerState(other.getContainerState());
                }
                if (other.hasResource()) {
                    this.mergeResource(other.getResource());
                }
                if (other.hasPriority()) {
                    this.mergePriority(other.getPriority());
                }
                if (other.hasDiagnostics()) {
                    this.bitField0_ |= 0x10;
                    this.diagnostics_ = other.diagnostics_;
                    this.onChanged();
                }
                if (other.hasContainerExitStatus()) {
                    this.setContainerExitStatus(other.getContainerExitStatus());
                }
                if (other.hasCreationTime()) {
                    this.setCreationTime(other.getCreationTime());
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
                NMContainerStatusProto parsedMessage = null;
                try {
                    parsedMessage = NMContainerStatusProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (NMContainerStatusProto)e.getUnfinishedMessage();
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
            public boolean hasContainerState() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.ContainerStateProto getContainerState() {
                return this.containerState_;
            }
            
            public Builder setContainerState(final YarnProtos.ContainerStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.containerState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerState() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasResource() {
                return (this.bitField0_ & 0x4) == 0x4;
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
                this.bitField0_ |= 0x4;
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
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeResource(final YarnProtos.ResourceProto value) {
                if (this.resourceBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.resource_ != YarnProtos.ResourceProto.getDefaultInstance()) {
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
                this.bitField0_ |= 0x4;
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
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnProtos.ResourceProto.Builder getResourceBuilder() {
                this.bitField0_ |= 0x4;
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
            public boolean hasPriority() {
                return (this.bitField0_ & 0x8) == 0x8;
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
                this.bitField0_ |= 0x8;
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
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergePriority(final YarnProtos.PriorityProto value) {
                if (this.priorityBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.priority_ != YarnProtos.PriorityProto.getDefaultInstance()) {
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
                this.bitField0_ |= 0x8;
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
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public YarnProtos.PriorityProto.Builder getPriorityBuilder() {
                this.bitField0_ |= 0x8;
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
            public boolean hasDiagnostics() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getDiagnostics() {
                final Object ref = this.diagnostics_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnostics_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsBytes() {
                final Object ref = this.diagnostics_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnostics_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnostics(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnostics_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnostics() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.diagnostics_ = NMContainerStatusProto.getDefaultInstance().getDiagnostics();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnostics_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerExitStatus() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public int getContainerExitStatus() {
                return this.containerExitStatus_;
            }
            
            public Builder setContainerExitStatus(final int value) {
                this.bitField0_ |= 0x20;
                this.containerExitStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerExitStatus() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.containerExitStatus_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasCreationTime() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getCreationTime() {
                return this.creationTime_;
            }
            
            public Builder setCreationTime(final long value) {
                this.bitField0_ |= 0x40;
                this.creationTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCreationTime() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.creationTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface NMContainerStatusProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerId();
        
        YarnProtos.ContainerIdProto getContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder();
        
        boolean hasContainerState();
        
        YarnProtos.ContainerStateProto getContainerState();
        
        boolean hasResource();
        
        YarnProtos.ResourceProto getResource();
        
        YarnProtos.ResourceProtoOrBuilder getResourceOrBuilder();
        
        boolean hasPriority();
        
        YarnProtos.PriorityProto getPriority();
        
        YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder();
        
        boolean hasDiagnostics();
        
        String getDiagnostics();
        
        ByteString getDiagnosticsBytes();
        
        boolean hasContainerExitStatus();
        
        int getContainerExitStatus();
        
        boolean hasCreationTime();
        
        long getCreationTime();
    }
    
    public interface SystemCredentialsForAppsProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAppId();
        
        YarnProtos.ApplicationIdProto getAppId();
        
        YarnProtos.ApplicationIdProtoOrBuilder getAppIdOrBuilder();
        
        boolean hasCredentialsForApp();
        
        ByteString getCredentialsForApp();
    }
    
    public interface NodeHeartbeatResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasResponseId();
        
        int getResponseId();
        
        boolean hasContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder();
        
        boolean hasNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder();
        
        boolean hasNodeAction();
        
        YarnServerCommonProtos.NodeActionProto getNodeAction();
        
        List<YarnProtos.ContainerIdProto> getContainersToCleanupList();
        
        YarnProtos.ContainerIdProto getContainersToCleanup(final int p0);
        
        int getContainersToCleanupCount();
        
        List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToCleanupOrBuilderList();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainersToCleanupOrBuilder(final int p0);
        
        List<YarnProtos.ApplicationIdProto> getApplicationsToCleanupList();
        
        YarnProtos.ApplicationIdProto getApplicationsToCleanup(final int p0);
        
        int getApplicationsToCleanupCount();
        
        List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getApplicationsToCleanupOrBuilderList();
        
        YarnProtos.ApplicationIdProtoOrBuilder getApplicationsToCleanupOrBuilder(final int p0);
        
        boolean hasNextHeartBeatInterval();
        
        long getNextHeartBeatInterval();
        
        boolean hasDiagnosticsMessage();
        
        String getDiagnosticsMessage();
        
        ByteString getDiagnosticsMessageBytes();
        
        List<YarnProtos.ContainerIdProto> getContainersToBeRemovedFromNmList();
        
        YarnProtos.ContainerIdProto getContainersToBeRemovedFromNm(final int p0);
        
        int getContainersToBeRemovedFromNmCount();
        
        List<? extends YarnProtos.ContainerIdProtoOrBuilder> getContainersToBeRemovedFromNmOrBuilderList();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainersToBeRemovedFromNmOrBuilder(final int p0);
        
        List<SystemCredentialsForAppsProto> getSystemCredentialsForAppsList();
        
        SystemCredentialsForAppsProto getSystemCredentialsForApps(final int p0);
        
        int getSystemCredentialsForAppsCount();
        
        List<? extends SystemCredentialsForAppsProtoOrBuilder> getSystemCredentialsForAppsOrBuilderList();
        
        SystemCredentialsForAppsProtoOrBuilder getSystemCredentialsForAppsOrBuilder(final int p0);
    }
    
    public interface NodeHeartbeatRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasNodeStatus();
        
        YarnServerCommonProtos.NodeStatusProto getNodeStatus();
        
        YarnServerCommonProtos.NodeStatusProtoOrBuilder getNodeStatusOrBuilder();
        
        boolean hasLastKnownContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getLastKnownContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownContainerTokenMasterKeyOrBuilder();
        
        boolean hasLastKnownNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getLastKnownNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getLastKnownNmTokenMasterKeyOrBuilder();
    }
    
    public interface RegisterNodeManagerResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getContainerTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getContainerTokenMasterKeyOrBuilder();
        
        boolean hasNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getNmTokenMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getNmTokenMasterKeyOrBuilder();
        
        boolean hasNodeAction();
        
        YarnServerCommonProtos.NodeActionProto getNodeAction();
        
        boolean hasRmIdentifier();
        
        long getRmIdentifier();
        
        boolean hasDiagnosticsMessage();
        
        String getDiagnosticsMessage();
        
        ByteString getDiagnosticsMessageBytes();
        
        boolean hasRmVersion();
        
        String getRmVersion();
        
        ByteString getRmVersionBytes();
    }
    
    public interface RegisterNodeManagerRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasNodeId();
        
        YarnProtos.NodeIdProto getNodeId();
        
        YarnProtos.NodeIdProtoOrBuilder getNodeIdOrBuilder();
        
        boolean hasHttpPort();
        
        int getHttpPort();
        
        boolean hasResource();
        
        YarnProtos.ResourceProto getResource();
        
        YarnProtos.ResourceProtoOrBuilder getResourceOrBuilder();
        
        boolean hasNmVersion();
        
        String getNmVersion();
        
        ByteString getNmVersionBytes();
        
        List<NMContainerStatusProto> getContainerStatusesList();
        
        NMContainerStatusProto getContainerStatuses(final int p0);
        
        int getContainerStatusesCount();
        
        List<? extends NMContainerStatusProtoOrBuilder> getContainerStatusesOrBuilderList();
        
        NMContainerStatusProtoOrBuilder getContainerStatusesOrBuilder(final int p0);
        
        List<YarnProtos.ApplicationIdProto> getRunningApplicationsList();
        
        YarnProtos.ApplicationIdProto getRunningApplications(final int p0);
        
        int getRunningApplicationsCount();
        
        List<? extends YarnProtos.ApplicationIdProtoOrBuilder> getRunningApplicationsOrBuilderList();
        
        YarnProtos.ApplicationIdProtoOrBuilder getRunningApplicationsOrBuilder(final int p0);
    }
}
