// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.Internal;
import com.google.protobuf.AbstractMessage;
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

public final class ApplicationHistoryServerProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationHistoryDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationStartDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationFinishDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ContainerHistoryDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ContainerStartDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ContainerStartDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ContainerFinishDataProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private ApplicationHistoryServerProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ApplicationHistoryServerProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n'server/application_history_server.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\"\u0093\u0003\n\u001bApplicationHistoryDataProto\u00127\n\u000eapplication_id\u0018\u0001 \u0001(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\u0012\u0018\n\u0010application_name\u0018\u0002 \u0001(\t\u0012\u0018\n\u0010application_type\u0018\u0003 \u0001(\t\u0012\f\n\u0004user\u0018\u0004 \u0001(\t\u0012\r\n\u0005queue\u0018\u0005 \u0001(\t\u0012\u0013\n\u000bsubmit_time\u0018\u0006 \u0001(\u0003\u0012\u0012\n\nstart_time\u0018\u0007 \u0001(\u0003\u0012\u0013\n\u000bfinish_time\u0018\b \u0001(\u0003\u0012\u0018\n\u0010diagnostics_info\u0018\t \u0001(\t\u0012J\n\u0018final_application_status\u0018\n \u0001(\u000e2(.hadoop.yarn.FinalApplicationStat", "usProto\u0012F\n\u0016yarn_application_state\u0018\u000b \u0001(\u000e2&.hadoop.yarn.YarnApplicationStateProto\"\u00ce\u0001\n\u0019ApplicationStartDataProto\u00127\n\u000eapplication_id\u0018\u0001 \u0001(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\u0012\u0018\n\u0010application_name\u0018\u0002 \u0001(\t\u0012\u0018\n\u0010application_type\u0018\u0003 \u0001(\t\u0012\f\n\u0004user\u0018\u0004 \u0001(\t\u0012\r\n\u0005queue\u0018\u0005 \u0001(\t\u0012\u0013\n\u000bsubmit_time\u0018\u0006 \u0001(\u0003\u0012\u0012\n\nstart_time\u0018\u0007 \u0001(\u0003\"\u0098\u0002\n\u001aApplicationFinishDataProto\u00127\n\u000eapplication_id\u0018\u0001 \u0001(\u000b2\u001f.hadoop.yarn.ApplicationIdProto\u0012\u0013\n\u000bfinish_time\u0018\u0002 \u0001(\u0003\u0012\u0018\n", "\u0010diagnostics_info\u0018\u0003 \u0001(\t\u0012J\n\u0018final_application_status\u0018\u0004 \u0001(\u000e2(.hadoop.yarn.FinalApplicationStatusProto\u0012F\n\u0016yarn_application_state\u0018\u0005 \u0001(\u000e2&.hadoop.yarn.YarnApplicationStateProto\"\u009b\u0003\n\"ApplicationAttemptHistoryDataProto\u0012F\n\u0016application_attempt_id\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012\f\n\u0004host\u0018\u0002 \u0001(\t\u0012\u0010\n\brpc_port\u0018\u0003 \u0001(\u0005\u0012\u0014\n\ftracking_url\u0018\u0004 \u0001(\t\u0012\u0018\n\u0010diagnostics_info\u0018\u0005 \u0001(\t\u0012J\n\u0018final_application_status\u0018\u0006 \u0001(\u000e2(.h", "adoop.yarn.FinalApplicationStatusProto\u0012:\n\u0013master_container_id\u0018\u0007 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u0012U\n\u001eyarn_application_attempt_state\u0018\b \u0001(\u000e2-.hadoop.yarn.YarnApplicationAttemptStateProto\"\u00c6\u0001\n ApplicationAttemptStartDataProto\u0012F\n\u0016application_attempt_id\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012\f\n\u0004host\u0018\u0002 \u0001(\t\u0012\u0010\n\brpc_port\u0018\u0003 \u0001(\u0005\u0012:\n\u0013master_container_id\u0018\u0004 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\"¾\u0002\n!Applicati", "onAttemptFinishDataProto\u0012F\n\u0016application_attempt_id\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u0012\u0014\n\ftracking_url\u0018\u0002 \u0001(\t\u0012\u0018\n\u0010diagnostics_info\u0018\u0003 \u0001(\t\u0012J\n\u0018final_application_status\u0018\u0004 \u0001(\u000e2(.hadoop.yarn.FinalApplicationStatusProto\u0012U\n\u001eyarn_application_attempt_state\u0018\u0005 \u0001(\u000e2-.hadoop.yarn.YarnApplicationAttemptStateProto\"\u0087\u0003\n\u0019ContainerHistoryDataProto\u00123\n\fcontainer_id\u0018\u0001 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u00126\n\u0012alloca", "ted_resource\u0018\u0002 \u0001(\u000b2\u001a.hadoop.yarn.ResourceProto\u00122\n\u0010assigned_node_id\u0018\u0003 \u0001(\u000b2\u0018.hadoop.yarn.NodeIdProto\u0012,\n\bpriority\u0018\u0004 \u0001(\u000b2\u001a.hadoop.yarn.PriorityProto\u0012\u0012\n\nstart_time\u0018\u0005 \u0001(\u0003\u0012\u0013\n\u000bfinish_time\u0018\u0006 \u0001(\u0003\u0012\u0018\n\u0010diagnostics_info\u0018\u0007 \u0001(\t\u0012\u001d\n\u0015container_exit_status\u0018\b \u0001(\u0005\u00129\n\u000fcontainer_state\u0018\t \u0001(\u000e2 .hadoop.yarn.ContainerStateProto\"\u00fc\u0001\n\u0017ContainerStartDataProto\u00123\n\fcontainer_id\u0018\u0001 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u00126\n\u0012allocated_reso", "urce\u0018\u0002 \u0001(\u000b2\u001a.hadoop.yarn.ResourceProto\u00122\n\u0010assigned_node_id\u0018\u0003 \u0001(\u000b2\u0018.hadoop.yarn.NodeIdProto\u0012,\n\bpriority\u0018\u0004 \u0001(\u000b2\u001a.hadoop.yarn.PriorityProto\u0012\u0012\n\nstart_time\u0018\u0005 \u0001(\u0003\"\u00d8\u0001\n\u0018ContainerFinishDataProto\u00123\n\fcontainer_id\u0018\u0001 \u0001(\u000b2\u001d.hadoop.yarn.ContainerIdProto\u0012\u0013\n\u000bfinish_time\u0018\u0002 \u0001(\u0003\u0012\u0018\n\u0010diagnostics_info\u0018\u0003 \u0001(\t\u0012\u001d\n\u0015container_exit_status\u0018\u0004 \u0001(\u0005\u00129\n\u000fcontainer_state\u0018\u0005 \u0001(\u000e2 .hadoop.yarn.ContainerStateProtoBD\n\u001corg.apache.hadoop.yar", "n.protoB\u001eApplicationHistoryServerProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ApplicationHistoryServerProtos.descriptor = root;
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(0);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor, new String[] { "ApplicationId", "ApplicationName", "ApplicationType", "User", "Queue", "SubmitTime", "StartTime", "FinishTime", "DiagnosticsInfo", "FinalApplicationStatus", "YarnApplicationState" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(1);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor, new String[] { "ApplicationId", "ApplicationName", "ApplicationType", "User", "Queue", "SubmitTime", "StartTime" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(2);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor, new String[] { "ApplicationId", "FinishTime", "DiagnosticsInfo", "FinalApplicationStatus", "YarnApplicationState" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(3);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor, new String[] { "ApplicationAttemptId", "Host", "RpcPort", "TrackingUrl", "DiagnosticsInfo", "FinalApplicationStatus", "MasterContainerId", "YarnApplicationAttemptState" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(4);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor, new String[] { "ApplicationAttemptId", "Host", "RpcPort", "MasterContainerId" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(5);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor, new String[] { "ApplicationAttemptId", "TrackingUrl", "DiagnosticsInfo", "FinalApplicationStatus", "YarnApplicationAttemptState" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(6);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor, new String[] { "ContainerId", "AllocatedResource", "AssignedNodeId", "Priority", "StartTime", "FinishTime", "DiagnosticsInfo", "ContainerExitStatus", "ContainerState" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(7);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_descriptor, new String[] { "ContainerId", "AllocatedResource", "AssignedNodeId", "Priority", "StartTime" });
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor = ApplicationHistoryServerProtos.getDescriptor().getMessageTypes().get(8);
                ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor, new String[] { "ContainerId", "FinishTime", "DiagnosticsInfo", "ContainerExitStatus", "ContainerState" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor() }, assigner);
    }
    
    public static final class ApplicationHistoryDataProto extends GeneratedMessage implements ApplicationHistoryDataProtoOrBuilder
    {
        private static final ApplicationHistoryDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationHistoryDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationIdProto applicationId_;
        public static final int APPLICATION_NAME_FIELD_NUMBER = 2;
        private Object applicationName_;
        public static final int APPLICATION_TYPE_FIELD_NUMBER = 3;
        private Object applicationType_;
        public static final int USER_FIELD_NUMBER = 4;
        private Object user_;
        public static final int QUEUE_FIELD_NUMBER = 5;
        private Object queue_;
        public static final int SUBMIT_TIME_FIELD_NUMBER = 6;
        private long submitTime_;
        public static final int START_TIME_FIELD_NUMBER = 7;
        private long startTime_;
        public static final int FINISH_TIME_FIELD_NUMBER = 8;
        private long finishTime_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 9;
        private Object diagnosticsInfo_;
        public static final int FINAL_APPLICATION_STATUS_FIELD_NUMBER = 10;
        private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
        public static final int YARN_APPLICATION_STATE_FIELD_NUMBER = 11;
        private YarnProtos.YarnApplicationStateProto yarnApplicationState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationHistoryDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationHistoryDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationHistoryDataProto getDefaultInstance() {
            return ApplicationHistoryDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationHistoryDataProto getDefaultInstanceForType() {
            return ApplicationHistoryDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationHistoryDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationId_.toBuilder();
                            }
                            this.applicationId_ = input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationId_);
                                this.applicationId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.applicationName_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.applicationType_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.user_ = input.readBytes();
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.queue_ = input.readBytes();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.submitTime_ = input.readInt64();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.startTime_ = input.readInt64();
                            continue;
                        }
                        case 64: {
                            this.bitField0_ |= 0x80;
                            this.finishTime_ = input.readInt64();
                            continue;
                        }
                        case 74: {
                            this.bitField0_ |= 0x100;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 80: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.FinalApplicationStatusProto value = YarnProtos.FinalApplicationStatusProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(10, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x200;
                            this.finalApplicationStatus_ = value;
                            continue;
                        }
                        case 88: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.YarnApplicationStateProto value2 = YarnProtos.YarnApplicationStateProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(11, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x400;
                            this.yarnApplicationState_ = value2;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationHistoryDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationHistoryDataProto> getParserForType() {
            return ApplicationHistoryDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getApplicationId() {
            return this.applicationId_;
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
            return this.applicationId_;
        }
        
        @Override
        public boolean hasApplicationName() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getApplicationName() {
            final Object ref = this.applicationName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.applicationName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getApplicationNameBytes() {
            final Object ref = this.applicationName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.applicationName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasApplicationType() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getApplicationType() {
            final Object ref = this.applicationType_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.applicationType_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getApplicationTypeBytes() {
            final Object ref = this.applicationType_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.applicationType_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasUser() {
            return (this.bitField0_ & 0x8) == 0x8;
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
        
        @Override
        public boolean hasQueue() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getQueue() {
            final Object ref = this.queue_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.queue_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getQueueBytes() {
            final Object ref = this.queue_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.queue_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasSubmitTime() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public long getSubmitTime() {
            return this.submitTime_;
        }
        
        @Override
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        @Override
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasFinalApplicationStatus() {
            return (this.bitField0_ & 0x200) == 0x200;
        }
        
        @Override
        public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
            return this.finalApplicationStatus_;
        }
        
        @Override
        public boolean hasYarnApplicationState() {
            return (this.bitField0_ & 0x400) == 0x400;
        }
        
        @Override
        public YarnProtos.YarnApplicationStateProto getYarnApplicationState() {
            return this.yarnApplicationState_;
        }
        
        private void initFields() {
            this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
            this.applicationName_ = "";
            this.applicationType_ = "";
            this.user_ = "";
            this.queue_ = "";
            this.submitTime_ = 0L;
            this.startTime_ = 0L;
            this.finishTime_ = 0L;
            this.diagnosticsInfo_ = "";
            this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
            this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
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
                output.writeMessage(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getApplicationNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getApplicationTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getQueueBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt64(6, this.submitTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.startTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeInt64(8, this.finishTime_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeBytes(9, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                output.writeEnum(10, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                output.writeEnum(11, this.yarnApplicationState_.getNumber());
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getApplicationNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getApplicationTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getQueueBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt64Size(6, this.submitTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.startTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeInt64Size(8, this.finishTime_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeBytesSize(9, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                size += CodedOutputStream.computeEnumSize(10, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                size += CodedOutputStream.computeEnumSize(11, this.yarnApplicationState_.getNumber());
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
            if (!(obj instanceof ApplicationHistoryDataProto)) {
                return super.equals(obj);
            }
            final ApplicationHistoryDataProto other = (ApplicationHistoryDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationId() == other.hasApplicationId());
            if (this.hasApplicationId()) {
                result = (result && this.getApplicationId().equals(other.getApplicationId()));
            }
            result = (result && this.hasApplicationName() == other.hasApplicationName());
            if (this.hasApplicationName()) {
                result = (result && this.getApplicationName().equals(other.getApplicationName()));
            }
            result = (result && this.hasApplicationType() == other.hasApplicationType());
            if (this.hasApplicationType()) {
                result = (result && this.getApplicationType().equals(other.getApplicationType()));
            }
            result = (result && this.hasUser() == other.hasUser());
            if (this.hasUser()) {
                result = (result && this.getUser().equals(other.getUser()));
            }
            result = (result && this.hasQueue() == other.hasQueue());
            if (this.hasQueue()) {
                result = (result && this.getQueue().equals(other.getQueue()));
            }
            result = (result && this.hasSubmitTime() == other.hasSubmitTime());
            if (this.hasSubmitTime()) {
                result = (result && this.getSubmitTime() == other.getSubmitTime());
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasFinalApplicationStatus() == other.hasFinalApplicationStatus());
            if (this.hasFinalApplicationStatus()) {
                result = (result && this.getFinalApplicationStatus() == other.getFinalApplicationStatus());
            }
            result = (result && this.hasYarnApplicationState() == other.hasYarnApplicationState());
            if (this.hasYarnApplicationState()) {
                result = (result && this.getYarnApplicationState() == other.getYarnApplicationState());
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
            if (this.hasApplicationId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationId().hashCode();
            }
            if (this.hasApplicationName()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getApplicationName().hashCode();
            }
            if (this.hasApplicationType()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getApplicationType().hashCode();
            }
            if (this.hasUser()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getUser().hashCode();
            }
            if (this.hasQueue()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getQueue().hashCode();
            }
            if (this.hasSubmitTime()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + AbstractMessage.hashLong(this.getSubmitTime());
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            if (this.hasFinishTime()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasFinalApplicationStatus()) {
                hash = 37 * hash + 10;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFinalApplicationStatus());
            }
            if (this.hasYarnApplicationState()) {
                hash = 37 * hash + 11;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getYarnApplicationState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationHistoryDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationHistoryDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationHistoryDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationHistoryDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationHistoryDataProto prototype) {
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
            ApplicationHistoryDataProto.PARSER = new AbstractParser<ApplicationHistoryDataProto>() {
                @Override
                public ApplicationHistoryDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationHistoryDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationHistoryDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationHistoryDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationIdProto applicationId_;
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> applicationIdBuilder_;
            private Object applicationName_;
            private Object applicationType_;
            private Object user_;
            private Object queue_;
            private long submitTime_;
            private long startTime_;
            private long finishTime_;
            private Object diagnosticsInfo_;
            private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
            private YarnProtos.YarnApplicationStateProto yarnApplicationState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationHistoryDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.applicationName_ = "";
                this.applicationType_ = "";
                this.user_ = "";
                this.queue_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.applicationName_ = "";
                this.applicationType_ = "";
                this.user_ = "";
                this.queue_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationHistoryDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.applicationName_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.applicationType_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.user_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                this.queue_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.submitTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFDF;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFFF7F;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFEFF;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.bitField0_ &= 0xFFFFFDFF;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.bitField0_ &= 0xFFFFFBFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationHistoryDataProto_descriptor;
            }
            
            @Override
            public ApplicationHistoryDataProto getDefaultInstanceForType() {
                return ApplicationHistoryDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationHistoryDataProto build() {
                final ApplicationHistoryDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationHistoryDataProto buildPartial() {
                final ApplicationHistoryDataProto result = new ApplicationHistoryDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationIdBuilder_ == null) {
                    result.applicationId_ = this.applicationId_;
                }
                else {
                    result.applicationId_ = this.applicationIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.applicationName_ = this.applicationName_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.applicationType_ = this.applicationType_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.user_ = this.user_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.queue_ = this.queue_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.submitTime_ = this.submitTime_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.startTime_ = this.startTime_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.finishTime_ = this.finishTime_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x200) == 0x200) {
                    to_bitField0_ |= 0x200;
                }
                result.finalApplicationStatus_ = this.finalApplicationStatus_;
                if ((from_bitField0_ & 0x400) == 0x400) {
                    to_bitField0_ |= 0x400;
                }
                result.yarnApplicationState_ = this.yarnApplicationState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationHistoryDataProto) {
                    return this.mergeFrom((ApplicationHistoryDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationHistoryDataProto other) {
                if (other == ApplicationHistoryDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationId()) {
                    this.mergeApplicationId(other.getApplicationId());
                }
                if (other.hasApplicationName()) {
                    this.bitField0_ |= 0x2;
                    this.applicationName_ = other.applicationName_;
                    this.onChanged();
                }
                if (other.hasApplicationType()) {
                    this.bitField0_ |= 0x4;
                    this.applicationType_ = other.applicationType_;
                    this.onChanged();
                }
                if (other.hasUser()) {
                    this.bitField0_ |= 0x8;
                    this.user_ = other.user_;
                    this.onChanged();
                }
                if (other.hasQueue()) {
                    this.bitField0_ |= 0x10;
                    this.queue_ = other.queue_;
                    this.onChanged();
                }
                if (other.hasSubmitTime()) {
                    this.setSubmitTime(other.getSubmitTime());
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x100;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasFinalApplicationStatus()) {
                    this.setFinalApplicationStatus(other.getFinalApplicationStatus());
                }
                if (other.hasYarnApplicationState()) {
                    this.setYarnApplicationState(other.getYarnApplicationState());
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
                ApplicationHistoryDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationHistoryDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationHistoryDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    return this.applicationId_;
                }
                return this.applicationIdBuilder_.getMessage();
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationId_ != YarnProtos.ApplicationIdProto.getDefaultInstance()) {
                        this.applicationId_ = YarnProtos.ApplicationIdProto.newBuilder(this.applicationId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getApplicationIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
                if (this.applicationIdBuilder_ != null) {
                    return this.applicationIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getApplicationIdFieldBuilder() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.applicationId_, this.getParentForChildren(), this.isClean());
                    this.applicationId_ = null;
                }
                return this.applicationIdBuilder_;
            }
            
            @Override
            public boolean hasApplicationName() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getApplicationName() {
                final Object ref = this.applicationName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.applicationName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getApplicationNameBytes() {
                final Object ref = this.applicationName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.applicationName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setApplicationName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.applicationName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearApplicationName() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.applicationName_ = ApplicationHistoryDataProto.getDefaultInstance().getApplicationName();
                this.onChanged();
                return this;
            }
            
            public Builder setApplicationNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.applicationName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasApplicationType() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getApplicationType() {
                final Object ref = this.applicationType_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.applicationType_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getApplicationTypeBytes() {
                final Object ref = this.applicationType_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.applicationType_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setApplicationType(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.applicationType_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearApplicationType() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.applicationType_ = ApplicationHistoryDataProto.getDefaultInstance().getApplicationType();
                this.onChanged();
                return this;
            }
            
            public Builder setApplicationTypeBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.applicationType_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasUser() {
                return (this.bitField0_ & 0x8) == 0x8;
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
                this.bitField0_ |= 0x8;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUser() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.user_ = ApplicationHistoryDataProto.getDefaultInstance().getUser();
                this.onChanged();
                return this;
            }
            
            public Builder setUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasQueue() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getQueue() {
                final Object ref = this.queue_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.queue_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getQueueBytes() {
                final Object ref = this.queue_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.queue_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setQueue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.queue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearQueue() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.queue_ = ApplicationHistoryDataProto.getDefaultInstance().getQueue();
                this.onChanged();
                return this;
            }
            
            public Builder setQueueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.queue_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSubmitTime() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public long getSubmitTime() {
                return this.submitTime_;
            }
            
            public Builder setSubmitTime(final long value) {
                this.bitField0_ |= 0x20;
                this.submitTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSubmitTime() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.submitTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasStartTime() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getStartTime() {
                return this.startTime_;
            }
            
            public Builder setStartTime(final long value) {
                this.bitField0_ |= 0x40;
                this.startTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStartTime() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.startTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x80;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.diagnosticsInfo_ = ApplicationHistoryDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinalApplicationStatus() {
                return (this.bitField0_ & 0x200) == 0x200;
            }
            
            @Override
            public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
                return this.finalApplicationStatus_;
            }
            
            public Builder setFinalApplicationStatus(final YarnProtos.FinalApplicationStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x200;
                this.finalApplicationStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalApplicationStatus() {
                this.bitField0_ &= 0xFFFFFDFF;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasYarnApplicationState() {
                return (this.bitField0_ & 0x400) == 0x400;
            }
            
            @Override
            public YarnProtos.YarnApplicationStateProto getYarnApplicationState() {
                return this.yarnApplicationState_;
            }
            
            public Builder setYarnApplicationState(final YarnProtos.YarnApplicationStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x400;
                this.yarnApplicationState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearYarnApplicationState() {
                this.bitField0_ &= 0xFFFFFBFF;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ApplicationStartDataProto extends GeneratedMessage implements ApplicationStartDataProtoOrBuilder
    {
        private static final ApplicationStartDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationStartDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationIdProto applicationId_;
        public static final int APPLICATION_NAME_FIELD_NUMBER = 2;
        private Object applicationName_;
        public static final int APPLICATION_TYPE_FIELD_NUMBER = 3;
        private Object applicationType_;
        public static final int USER_FIELD_NUMBER = 4;
        private Object user_;
        public static final int QUEUE_FIELD_NUMBER = 5;
        private Object queue_;
        public static final int SUBMIT_TIME_FIELD_NUMBER = 6;
        private long submitTime_;
        public static final int START_TIME_FIELD_NUMBER = 7;
        private long startTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationStartDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationStartDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationStartDataProto getDefaultInstance() {
            return ApplicationStartDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationStartDataProto getDefaultInstanceForType() {
            return ApplicationStartDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationStartDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationId_.toBuilder();
                            }
                            this.applicationId_ = input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationId_);
                                this.applicationId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.applicationName_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.applicationType_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.user_ = input.readBytes();
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.queue_ = input.readBytes();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.submitTime_ = input.readInt64();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.startTime_ = input.readInt64();
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationStartDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationStartDataProto> getParserForType() {
            return ApplicationStartDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getApplicationId() {
            return this.applicationId_;
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
            return this.applicationId_;
        }
        
        @Override
        public boolean hasApplicationName() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getApplicationName() {
            final Object ref = this.applicationName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.applicationName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getApplicationNameBytes() {
            final Object ref = this.applicationName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.applicationName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasApplicationType() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getApplicationType() {
            final Object ref = this.applicationType_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.applicationType_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getApplicationTypeBytes() {
            final Object ref = this.applicationType_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.applicationType_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasUser() {
            return (this.bitField0_ & 0x8) == 0x8;
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
        
        @Override
        public boolean hasQueue() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getQueue() {
            final Object ref = this.queue_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.queue_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getQueueBytes() {
            final Object ref = this.queue_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.queue_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasSubmitTime() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public long getSubmitTime() {
            return this.submitTime_;
        }
        
        @Override
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        private void initFields() {
            this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
            this.applicationName_ = "";
            this.applicationType_ = "";
            this.user_ = "";
            this.queue_ = "";
            this.submitTime_ = 0L;
            this.startTime_ = 0L;
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
                output.writeMessage(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getApplicationNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getApplicationTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getQueueBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt64(6, this.submitTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.startTime_);
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getApplicationNameBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getApplicationTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getQueueBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt64Size(6, this.submitTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.startTime_);
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
            if (!(obj instanceof ApplicationStartDataProto)) {
                return super.equals(obj);
            }
            final ApplicationStartDataProto other = (ApplicationStartDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationId() == other.hasApplicationId());
            if (this.hasApplicationId()) {
                result = (result && this.getApplicationId().equals(other.getApplicationId()));
            }
            result = (result && this.hasApplicationName() == other.hasApplicationName());
            if (this.hasApplicationName()) {
                result = (result && this.getApplicationName().equals(other.getApplicationName()));
            }
            result = (result && this.hasApplicationType() == other.hasApplicationType());
            if (this.hasApplicationType()) {
                result = (result && this.getApplicationType().equals(other.getApplicationType()));
            }
            result = (result && this.hasUser() == other.hasUser());
            if (this.hasUser()) {
                result = (result && this.getUser().equals(other.getUser()));
            }
            result = (result && this.hasQueue() == other.hasQueue());
            if (this.hasQueue()) {
                result = (result && this.getQueue().equals(other.getQueue()));
            }
            result = (result && this.hasSubmitTime() == other.hasSubmitTime());
            if (this.hasSubmitTime()) {
                result = (result && this.getSubmitTime() == other.getSubmitTime());
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
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
            if (this.hasApplicationId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationId().hashCode();
            }
            if (this.hasApplicationName()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getApplicationName().hashCode();
            }
            if (this.hasApplicationType()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getApplicationType().hashCode();
            }
            if (this.hasUser()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getUser().hashCode();
            }
            if (this.hasQueue()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getQueue().hashCode();
            }
            if (this.hasSubmitTime()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + AbstractMessage.hashLong(this.getSubmitTime());
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationStartDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationStartDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationStartDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationStartDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationStartDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationStartDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationStartDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationStartDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationStartDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStartDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationStartDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationStartDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationStartDataProto prototype) {
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
            ApplicationStartDataProto.PARSER = new AbstractParser<ApplicationStartDataProto>() {
                @Override
                public ApplicationStartDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationStartDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationStartDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationStartDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationIdProto applicationId_;
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> applicationIdBuilder_;
            private Object applicationName_;
            private Object applicationType_;
            private Object user_;
            private Object queue_;
            private long submitTime_;
            private long startTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationStartDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.applicationName_ = "";
                this.applicationType_ = "";
                this.user_ = "";
                this.queue_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.applicationName_ = "";
                this.applicationType_ = "";
                this.user_ = "";
                this.queue_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationStartDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.applicationName_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.applicationType_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.user_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                this.queue_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.submitTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFDF;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationStartDataProto_descriptor;
            }
            
            @Override
            public ApplicationStartDataProto getDefaultInstanceForType() {
                return ApplicationStartDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationStartDataProto build() {
                final ApplicationStartDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationStartDataProto buildPartial() {
                final ApplicationStartDataProto result = new ApplicationStartDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationIdBuilder_ == null) {
                    result.applicationId_ = this.applicationId_;
                }
                else {
                    result.applicationId_ = this.applicationIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.applicationName_ = this.applicationName_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.applicationType_ = this.applicationType_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.user_ = this.user_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.queue_ = this.queue_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.submitTime_ = this.submitTime_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.startTime_ = this.startTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationStartDataProto) {
                    return this.mergeFrom((ApplicationStartDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationStartDataProto other) {
                if (other == ApplicationStartDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationId()) {
                    this.mergeApplicationId(other.getApplicationId());
                }
                if (other.hasApplicationName()) {
                    this.bitField0_ |= 0x2;
                    this.applicationName_ = other.applicationName_;
                    this.onChanged();
                }
                if (other.hasApplicationType()) {
                    this.bitField0_ |= 0x4;
                    this.applicationType_ = other.applicationType_;
                    this.onChanged();
                }
                if (other.hasUser()) {
                    this.bitField0_ |= 0x8;
                    this.user_ = other.user_;
                    this.onChanged();
                }
                if (other.hasQueue()) {
                    this.bitField0_ |= 0x10;
                    this.queue_ = other.queue_;
                    this.onChanged();
                }
                if (other.hasSubmitTime()) {
                    this.setSubmitTime(other.getSubmitTime());
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
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
                ApplicationStartDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationStartDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationStartDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    return this.applicationId_;
                }
                return this.applicationIdBuilder_.getMessage();
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationId_ != YarnProtos.ApplicationIdProto.getDefaultInstance()) {
                        this.applicationId_ = YarnProtos.ApplicationIdProto.newBuilder(this.applicationId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getApplicationIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
                if (this.applicationIdBuilder_ != null) {
                    return this.applicationIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getApplicationIdFieldBuilder() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.applicationId_, this.getParentForChildren(), this.isClean());
                    this.applicationId_ = null;
                }
                return this.applicationIdBuilder_;
            }
            
            @Override
            public boolean hasApplicationName() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getApplicationName() {
                final Object ref = this.applicationName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.applicationName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getApplicationNameBytes() {
                final Object ref = this.applicationName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.applicationName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setApplicationName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.applicationName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearApplicationName() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.applicationName_ = ApplicationStartDataProto.getDefaultInstance().getApplicationName();
                this.onChanged();
                return this;
            }
            
            public Builder setApplicationNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.applicationName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasApplicationType() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getApplicationType() {
                final Object ref = this.applicationType_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.applicationType_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getApplicationTypeBytes() {
                final Object ref = this.applicationType_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.applicationType_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setApplicationType(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.applicationType_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearApplicationType() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.applicationType_ = ApplicationStartDataProto.getDefaultInstance().getApplicationType();
                this.onChanged();
                return this;
            }
            
            public Builder setApplicationTypeBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.applicationType_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasUser() {
                return (this.bitField0_ & 0x8) == 0x8;
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
                this.bitField0_ |= 0x8;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUser() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.user_ = ApplicationStartDataProto.getDefaultInstance().getUser();
                this.onChanged();
                return this;
            }
            
            public Builder setUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasQueue() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getQueue() {
                final Object ref = this.queue_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.queue_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getQueueBytes() {
                final Object ref = this.queue_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.queue_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setQueue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.queue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearQueue() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.queue_ = ApplicationStartDataProto.getDefaultInstance().getQueue();
                this.onChanged();
                return this;
            }
            
            public Builder setQueueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.queue_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSubmitTime() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public long getSubmitTime() {
                return this.submitTime_;
            }
            
            public Builder setSubmitTime(final long value) {
                this.bitField0_ |= 0x20;
                this.submitTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSubmitTime() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.submitTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasStartTime() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getStartTime() {
                return this.startTime_;
            }
            
            public Builder setStartTime(final long value) {
                this.bitField0_ |= 0x40;
                this.startTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStartTime() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.startTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ApplicationFinishDataProto extends GeneratedMessage implements ApplicationFinishDataProtoOrBuilder
    {
        private static final ApplicationFinishDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationFinishDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationIdProto applicationId_;
        public static final int FINISH_TIME_FIELD_NUMBER = 2;
        private long finishTime_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 3;
        private Object diagnosticsInfo_;
        public static final int FINAL_APPLICATION_STATUS_FIELD_NUMBER = 4;
        private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
        public static final int YARN_APPLICATION_STATE_FIELD_NUMBER = 5;
        private YarnProtos.YarnApplicationStateProto yarnApplicationState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationFinishDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationFinishDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationFinishDataProto getDefaultInstance() {
            return ApplicationFinishDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationFinishDataProto getDefaultInstanceForType() {
            return ApplicationFinishDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationFinishDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationId_.toBuilder();
                            }
                            this.applicationId_ = input.readMessage(YarnProtos.ApplicationIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationId_);
                                this.applicationId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.finishTime_ = input.readInt64();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.FinalApplicationStatusProto value = YarnProtos.FinalApplicationStatusProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(4, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x8;
                            this.finalApplicationStatus_ = value;
                            continue;
                        }
                        case 40: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.YarnApplicationStateProto value2 = YarnProtos.YarnApplicationStateProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(5, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x10;
                            this.yarnApplicationState_ = value2;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationFinishDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationFinishDataProto> getParserForType() {
            return ApplicationFinishDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationIdProto getApplicationId() {
            return this.applicationId_;
        }
        
        @Override
        public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
            return this.applicationId_;
        }
        
        @Override
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasFinalApplicationStatus() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
            return this.finalApplicationStatus_;
        }
        
        @Override
        public boolean hasYarnApplicationState() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public YarnProtos.YarnApplicationStateProto getYarnApplicationState() {
            return this.yarnApplicationState_;
        }
        
        private void initFields() {
            this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
            this.finishTime_ = 0L;
            this.diagnosticsInfo_ = "";
            this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
            this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
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
                output.writeMessage(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt64(2, this.finishTime_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeEnum(4, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeEnum(5, this.yarnApplicationState_.getNumber());
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt64Size(2, this.finishTime_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeEnumSize(4, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeEnumSize(5, this.yarnApplicationState_.getNumber());
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
            if (!(obj instanceof ApplicationFinishDataProto)) {
                return super.equals(obj);
            }
            final ApplicationFinishDataProto other = (ApplicationFinishDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationId() == other.hasApplicationId());
            if (this.hasApplicationId()) {
                result = (result && this.getApplicationId().equals(other.getApplicationId()));
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasFinalApplicationStatus() == other.hasFinalApplicationStatus());
            if (this.hasFinalApplicationStatus()) {
                result = (result && this.getFinalApplicationStatus() == other.getFinalApplicationStatus());
            }
            result = (result && this.hasYarnApplicationState() == other.hasYarnApplicationState());
            if (this.hasYarnApplicationState()) {
                result = (result && this.getYarnApplicationState() == other.getYarnApplicationState());
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
            if (this.hasApplicationId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationId().hashCode();
            }
            if (this.hasFinishTime()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasFinalApplicationStatus()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFinalApplicationStatus());
            }
            if (this.hasYarnApplicationState()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getYarnApplicationState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationFinishDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationFinishDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationFinishDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationFinishDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationFinishDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationFinishDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationFinishDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationFinishDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationFinishDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationFinishDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationFinishDataProto prototype) {
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
            ApplicationFinishDataProto.PARSER = new AbstractParser<ApplicationFinishDataProto>() {
                @Override
                public ApplicationFinishDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationFinishDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationFinishDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationFinishDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationIdProto applicationId_;
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> applicationIdBuilder_;
            private long finishTime_;
            private Object diagnosticsInfo_;
            private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
            private YarnProtos.YarnApplicationStateProto yarnApplicationState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationFinishDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationFinishDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFD;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.bitField0_ &= 0xFFFFFFF7;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.bitField0_ &= 0xFFFFFFEF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationFinishDataProto_descriptor;
            }
            
            @Override
            public ApplicationFinishDataProto getDefaultInstanceForType() {
                return ApplicationFinishDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationFinishDataProto build() {
                final ApplicationFinishDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationFinishDataProto buildPartial() {
                final ApplicationFinishDataProto result = new ApplicationFinishDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationIdBuilder_ == null) {
                    result.applicationId_ = this.applicationId_;
                }
                else {
                    result.applicationId_ = this.applicationIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.finishTime_ = this.finishTime_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.finalApplicationStatus_ = this.finalApplicationStatus_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.yarnApplicationState_ = this.yarnApplicationState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationFinishDataProto) {
                    return this.mergeFrom((ApplicationFinishDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationFinishDataProto other) {
                if (other == ApplicationFinishDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationId()) {
                    this.mergeApplicationId(other.getApplicationId());
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x4;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasFinalApplicationStatus()) {
                    this.setFinalApplicationStatus(other.getFinalApplicationStatus());
                }
                if (other.hasYarnApplicationState()) {
                    this.setYarnApplicationState(other.getYarnApplicationState());
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
                ApplicationFinishDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationFinishDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationFinishDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationIdProto getApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    return this.applicationId_;
                }
                return this.applicationIdBuilder_.getMessage();
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationId(final YarnProtos.ApplicationIdProto.Builder builderForValue) {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationId(final YarnProtos.ApplicationIdProto value) {
                if (this.applicationIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationId_ != YarnProtos.ApplicationIdProto.getDefaultInstance()) {
                        this.applicationId_ = YarnProtos.ApplicationIdProto.newBuilder(this.applicationId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationId() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationId_ = YarnProtos.ApplicationIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationIdProto.Builder getApplicationIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder() {
                if (this.applicationIdBuilder_ != null) {
                    return this.applicationIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder> getApplicationIdFieldBuilder() {
                if (this.applicationIdBuilder_ == null) {
                    this.applicationIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationIdProto, YarnProtos.ApplicationIdProto.Builder, YarnProtos.ApplicationIdProtoOrBuilder>(this.applicationId_, this.getParentForChildren(), this.isClean());
                    this.applicationId_ = null;
                }
                return this.applicationIdBuilder_;
            }
            
            @Override
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x2;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.diagnosticsInfo_ = ApplicationFinishDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinalApplicationStatus() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
                return this.finalApplicationStatus_;
            }
            
            public Builder setFinalApplicationStatus(final YarnProtos.FinalApplicationStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.finalApplicationStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalApplicationStatus() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasYarnApplicationState() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public YarnProtos.YarnApplicationStateProto getYarnApplicationState() {
                return this.yarnApplicationState_;
            }
            
            public Builder setYarnApplicationState(final YarnProtos.YarnApplicationStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.yarnApplicationState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearYarnApplicationState() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.yarnApplicationState_ = YarnProtos.YarnApplicationStateProto.NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ApplicationAttemptHistoryDataProto extends GeneratedMessage implements ApplicationAttemptHistoryDataProtoOrBuilder
    {
        private static final ApplicationAttemptHistoryDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationAttemptHistoryDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ATTEMPT_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
        public static final int HOST_FIELD_NUMBER = 2;
        private Object host_;
        public static final int RPC_PORT_FIELD_NUMBER = 3;
        private int rpcPort_;
        public static final int TRACKING_URL_FIELD_NUMBER = 4;
        private Object trackingUrl_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 5;
        private Object diagnosticsInfo_;
        public static final int FINAL_APPLICATION_STATUS_FIELD_NUMBER = 6;
        private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
        public static final int MASTER_CONTAINER_ID_FIELD_NUMBER = 7;
        private YarnProtos.ContainerIdProto masterContainerId_;
        public static final int YARN_APPLICATION_ATTEMPT_STATE_FIELD_NUMBER = 8;
        private YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationAttemptHistoryDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationAttemptHistoryDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationAttemptHistoryDataProto getDefaultInstance() {
            return ApplicationAttemptHistoryDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationAttemptHistoryDataProto getDefaultInstanceForType() {
            return ApplicationAttemptHistoryDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationAttemptHistoryDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationAttemptId_.toBuilder();
                            }
                            this.applicationAttemptId_ = input.readMessage(YarnProtos.ApplicationAttemptIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationAttemptId_);
                                this.applicationAttemptId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.host_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.rpcPort_ = input.readInt32();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.trackingUrl_ = input.readBytes();
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 48: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.FinalApplicationStatusProto value = YarnProtos.FinalApplicationStatusProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(6, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x20;
                            this.finalApplicationStatus_ = value;
                            continue;
                        }
                        case 58: {
                            YarnProtos.ContainerIdProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x40) == 0x40) {
                                subBuilder2 = this.masterContainerId_.toBuilder();
                            }
                            this.masterContainerId_ = input.readMessage(YarnProtos.ContainerIdProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.masterContainerId_);
                                this.masterContainerId_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x40;
                            continue;
                        }
                        case 64: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.YarnApplicationAttemptStateProto value2 = YarnProtos.YarnApplicationAttemptStateProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(8, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x80;
                            this.yarnApplicationAttemptState_ = value2;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptHistoryDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationAttemptHistoryDataProto> getParserForType() {
            return ApplicationAttemptHistoryDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationAttemptId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public boolean hasHost() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getHost() {
            final Object ref = this.host_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.host_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getHostBytes() {
            final Object ref = this.host_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.host_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRpcPort() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public int getRpcPort() {
            return this.rpcPort_;
        }
        
        @Override
        public boolean hasTrackingUrl() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public String getTrackingUrl() {
            final Object ref = this.trackingUrl_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.trackingUrl_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getTrackingUrlBytes() {
            final Object ref = this.trackingUrl_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.trackingUrl_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasFinalApplicationStatus() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
            return this.finalApplicationStatus_;
        }
        
        @Override
        public boolean hasMasterContainerId() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public YarnProtos.ContainerIdProto getMasterContainerId() {
            return this.masterContainerId_;
        }
        
        @Override
        public YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder() {
            return this.masterContainerId_;
        }
        
        @Override
        public boolean hasYarnApplicationAttemptState() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState() {
            return this.yarnApplicationAttemptState_;
        }
        
        private void initFields() {
            this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.host_ = "";
            this.rpcPort_ = 0;
            this.trackingUrl_ = "";
            this.diagnosticsInfo_ = "";
            this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
            this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
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
                output.writeMessage(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getHostBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeInt32(3, this.rpcPort_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.getTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeEnum(6, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeMessage(7, this.masterContainerId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeEnum(8, this.yarnApplicationAttemptState_.getNumber());
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getHostBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeInt32Size(3, this.rpcPort_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.getTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeEnumSize(6, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeMessageSize(7, this.masterContainerId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeEnumSize(8, this.yarnApplicationAttemptState_.getNumber());
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
            if (!(obj instanceof ApplicationAttemptHistoryDataProto)) {
                return super.equals(obj);
            }
            final ApplicationAttemptHistoryDataProto other = (ApplicationAttemptHistoryDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationAttemptId() == other.hasApplicationAttemptId());
            if (this.hasApplicationAttemptId()) {
                result = (result && this.getApplicationAttemptId().equals(other.getApplicationAttemptId()));
            }
            result = (result && this.hasHost() == other.hasHost());
            if (this.hasHost()) {
                result = (result && this.getHost().equals(other.getHost()));
            }
            result = (result && this.hasRpcPort() == other.hasRpcPort());
            if (this.hasRpcPort()) {
                result = (result && this.getRpcPort() == other.getRpcPort());
            }
            result = (result && this.hasTrackingUrl() == other.hasTrackingUrl());
            if (this.hasTrackingUrl()) {
                result = (result && this.getTrackingUrl().equals(other.getTrackingUrl()));
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasFinalApplicationStatus() == other.hasFinalApplicationStatus());
            if (this.hasFinalApplicationStatus()) {
                result = (result && this.getFinalApplicationStatus() == other.getFinalApplicationStatus());
            }
            result = (result && this.hasMasterContainerId() == other.hasMasterContainerId());
            if (this.hasMasterContainerId()) {
                result = (result && this.getMasterContainerId().equals(other.getMasterContainerId()));
            }
            result = (result && this.hasYarnApplicationAttemptState() == other.hasYarnApplicationAttemptState());
            if (this.hasYarnApplicationAttemptState()) {
                result = (result && this.getYarnApplicationAttemptState() == other.getYarnApplicationAttemptState());
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
            if (this.hasApplicationAttemptId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationAttemptId().hashCode();
            }
            if (this.hasHost()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getHost().hashCode();
            }
            if (this.hasRpcPort()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getRpcPort();
            }
            if (this.hasTrackingUrl()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getTrackingUrl().hashCode();
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasFinalApplicationStatus()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFinalApplicationStatus());
            }
            if (this.hasMasterContainerId()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getMasterContainerId().hashCode();
            }
            if (this.hasYarnApplicationAttemptState()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getYarnApplicationAttemptState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptHistoryDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationAttemptHistoryDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptHistoryDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationAttemptHistoryDataProto prototype) {
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
            ApplicationAttemptHistoryDataProto.PARSER = new AbstractParser<ApplicationAttemptHistoryDataProto>() {
                @Override
                public ApplicationAttemptHistoryDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationAttemptHistoryDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationAttemptHistoryDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationAttemptHistoryDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> applicationAttemptIdBuilder_;
            private Object host_;
            private int rpcPort_;
            private Object trackingUrl_;
            private Object diagnosticsInfo_;
            private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
            private YarnProtos.ContainerIdProto masterContainerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> masterContainerIdBuilder_;
            private YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptHistoryDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.host_ = "";
                this.trackingUrl_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.host_ = "";
                this.trackingUrl_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationAttemptHistoryDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationAttemptIdFieldBuilder();
                    this.getMasterContainerIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.host_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.rpcPort_ = 0;
                this.bitField0_ &= 0xFFFFFFFB;
                this.trackingUrl_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.bitField0_ &= 0xFFFFFFDF;
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                }
                else {
                    this.masterContainerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.bitField0_ &= 0xFFFFFF7F;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptHistoryDataProto_descriptor;
            }
            
            @Override
            public ApplicationAttemptHistoryDataProto getDefaultInstanceForType() {
                return ApplicationAttemptHistoryDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationAttemptHistoryDataProto build() {
                final ApplicationAttemptHistoryDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationAttemptHistoryDataProto buildPartial() {
                final ApplicationAttemptHistoryDataProto result = new ApplicationAttemptHistoryDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationAttemptIdBuilder_ == null) {
                    result.applicationAttemptId_ = this.applicationAttemptId_;
                }
                else {
                    result.applicationAttemptId_ = this.applicationAttemptIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.host_ = this.host_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.rpcPort_ = this.rpcPort_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.trackingUrl_ = this.trackingUrl_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.finalApplicationStatus_ = this.finalApplicationStatus_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                if (this.masterContainerIdBuilder_ == null) {
                    result.masterContainerId_ = this.masterContainerId_;
                }
                else {
                    result.masterContainerId_ = this.masterContainerIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.yarnApplicationAttemptState_ = this.yarnApplicationAttemptState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationAttemptHistoryDataProto) {
                    return this.mergeFrom((ApplicationAttemptHistoryDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationAttemptHistoryDataProto other) {
                if (other == ApplicationAttemptHistoryDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationAttemptId()) {
                    this.mergeApplicationAttemptId(other.getApplicationAttemptId());
                }
                if (other.hasHost()) {
                    this.bitField0_ |= 0x2;
                    this.host_ = other.host_;
                    this.onChanged();
                }
                if (other.hasRpcPort()) {
                    this.setRpcPort(other.getRpcPort());
                }
                if (other.hasTrackingUrl()) {
                    this.bitField0_ |= 0x8;
                    this.trackingUrl_ = other.trackingUrl_;
                    this.onChanged();
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x10;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasFinalApplicationStatus()) {
                    this.setFinalApplicationStatus(other.getFinalApplicationStatus());
                }
                if (other.hasMasterContainerId()) {
                    this.mergeMasterContainerId(other.getMasterContainerId());
                }
                if (other.hasYarnApplicationAttemptState()) {
                    this.setYarnApplicationAttemptState(other.getYarnApplicationAttemptState());
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
                ApplicationAttemptHistoryDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationAttemptHistoryDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationAttemptHistoryDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationAttemptId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    return this.applicationAttemptId_;
                }
                return this.applicationAttemptIdBuilder_.getMessage();
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationAttemptId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto.Builder builderForValue) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationAttemptId_ != YarnProtos.ApplicationAttemptIdProto.getDefaultInstance()) {
                        this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.newBuilder(this.applicationAttemptId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationAttemptId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationAttemptIdProto.Builder getApplicationAttemptIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationAttemptIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
                if (this.applicationAttemptIdBuilder_ != null) {
                    return this.applicationAttemptIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationAttemptId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> getApplicationAttemptIdFieldBuilder() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder>(this.applicationAttemptId_, this.getParentForChildren(), this.isClean());
                    this.applicationAttemptId_ = null;
                }
                return this.applicationAttemptIdBuilder_;
            }
            
            @Override
            public boolean hasHost() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getHost() {
                final Object ref = this.host_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.host_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getHostBytes() {
                final Object ref = this.host_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.host_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setHost(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.host_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearHost() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.host_ = ApplicationAttemptHistoryDataProto.getDefaultInstance().getHost();
                this.onChanged();
                return this;
            }
            
            public Builder setHostBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.host_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRpcPort() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public int getRpcPort() {
                return this.rpcPort_;
            }
            
            public Builder setRpcPort(final int value) {
                this.bitField0_ |= 0x4;
                this.rpcPort_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcPort() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.rpcPort_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasTrackingUrl() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public String getTrackingUrl() {
                final Object ref = this.trackingUrl_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.trackingUrl_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getTrackingUrlBytes() {
                final Object ref = this.trackingUrl_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.trackingUrl_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setTrackingUrl(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.trackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearTrackingUrl() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.trackingUrl_ = ApplicationAttemptHistoryDataProto.getDefaultInstance().getTrackingUrl();
                this.onChanged();
                return this;
            }
            
            public Builder setTrackingUrlBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.trackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.diagnosticsInfo_ = ApplicationAttemptHistoryDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinalApplicationStatus() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
                return this.finalApplicationStatus_;
            }
            
            public Builder setFinalApplicationStatus(final YarnProtos.FinalApplicationStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.finalApplicationStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalApplicationStatus() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMasterContainerId() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public YarnProtos.ContainerIdProto getMasterContainerId() {
                if (this.masterContainerIdBuilder_ == null) {
                    return this.masterContainerId_;
                }
                return this.masterContainerIdBuilder_.getMessage();
            }
            
            public Builder setMasterContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.masterContainerIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.masterContainerId_ = value;
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder setMasterContainerId(final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder mergeMasterContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.masterContainerIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x40) == 0x40 && this.masterContainerId_ != YarnProtos.ContainerIdProto.getDefaultInstance()) {
                        this.masterContainerId_ = YarnProtos.ContainerIdProto.newBuilder(this.masterContainerId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.masterContainerId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder clearMasterContainerId() {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            public YarnProtos.ContainerIdProto.Builder getMasterContainerIdBuilder() {
                this.bitField0_ |= 0x40;
                this.onChanged();
                return this.getMasterContainerIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder() {
                if (this.masterContainerIdBuilder_ != null) {
                    return this.masterContainerIdBuilder_.getMessageOrBuilder();
                }
                return this.masterContainerId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> getMasterContainerIdFieldBuilder() {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerIdBuilder_ = new SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder>(this.masterContainerId_, this.getParentForChildren(), this.isClean());
                    this.masterContainerId_ = null;
                }
                return this.masterContainerIdBuilder_;
            }
            
            @Override
            public boolean hasYarnApplicationAttemptState() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState() {
                return this.yarnApplicationAttemptState_;
            }
            
            public Builder setYarnApplicationAttemptState(final YarnProtos.YarnApplicationAttemptStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x80;
                this.yarnApplicationAttemptState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearYarnApplicationAttemptState() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ApplicationAttemptStartDataProto extends GeneratedMessage implements ApplicationAttemptStartDataProtoOrBuilder
    {
        private static final ApplicationAttemptStartDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationAttemptStartDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ATTEMPT_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
        public static final int HOST_FIELD_NUMBER = 2;
        private Object host_;
        public static final int RPC_PORT_FIELD_NUMBER = 3;
        private int rpcPort_;
        public static final int MASTER_CONTAINER_ID_FIELD_NUMBER = 4;
        private YarnProtos.ContainerIdProto masterContainerId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationAttemptStartDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationAttemptStartDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationAttemptStartDataProto getDefaultInstance() {
            return ApplicationAttemptStartDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationAttemptStartDataProto getDefaultInstanceForType() {
            return ApplicationAttemptStartDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationAttemptStartDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationAttemptId_.toBuilder();
                            }
                            this.applicationAttemptId_ = input.readMessage(YarnProtos.ApplicationAttemptIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationAttemptId_);
                                this.applicationAttemptId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.host_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.rpcPort_ = input.readInt32();
                            continue;
                        }
                        case 34: {
                            YarnProtos.ContainerIdProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder2 = this.masterContainerId_.toBuilder();
                            }
                            this.masterContainerId_ = input.readMessage(YarnProtos.ContainerIdProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.masterContainerId_);
                                this.masterContainerId_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptStartDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationAttemptStartDataProto> getParserForType() {
            return ApplicationAttemptStartDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationAttemptId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public boolean hasHost() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getHost() {
            final Object ref = this.host_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.host_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getHostBytes() {
            final Object ref = this.host_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.host_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRpcPort() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public int getRpcPort() {
            return this.rpcPort_;
        }
        
        @Override
        public boolean hasMasterContainerId() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public YarnProtos.ContainerIdProto getMasterContainerId() {
            return this.masterContainerId_;
        }
        
        @Override
        public YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder() {
            return this.masterContainerId_;
        }
        
        private void initFields() {
            this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.host_ = "";
            this.rpcPort_ = 0;
            this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
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
                output.writeMessage(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getHostBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeInt32(3, this.rpcPort_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.masterContainerId_);
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getHostBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeInt32Size(3, this.rpcPort_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.masterContainerId_);
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
            if (!(obj instanceof ApplicationAttemptStartDataProto)) {
                return super.equals(obj);
            }
            final ApplicationAttemptStartDataProto other = (ApplicationAttemptStartDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationAttemptId() == other.hasApplicationAttemptId());
            if (this.hasApplicationAttemptId()) {
                result = (result && this.getApplicationAttemptId().equals(other.getApplicationAttemptId()));
            }
            result = (result && this.hasHost() == other.hasHost());
            if (this.hasHost()) {
                result = (result && this.getHost().equals(other.getHost()));
            }
            result = (result && this.hasRpcPort() == other.hasRpcPort());
            if (this.hasRpcPort()) {
                result = (result && this.getRpcPort() == other.getRpcPort());
            }
            result = (result && this.hasMasterContainerId() == other.hasMasterContainerId());
            if (this.hasMasterContainerId()) {
                result = (result && this.getMasterContainerId().equals(other.getMasterContainerId()));
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
            if (this.hasApplicationAttemptId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationAttemptId().hashCode();
            }
            if (this.hasHost()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getHost().hashCode();
            }
            if (this.hasRpcPort()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getRpcPort();
            }
            if (this.hasMasterContainerId()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getMasterContainerId().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptStartDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationAttemptStartDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptStartDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationAttemptStartDataProto prototype) {
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
            ApplicationAttemptStartDataProto.PARSER = new AbstractParser<ApplicationAttemptStartDataProto>() {
                @Override
                public ApplicationAttemptStartDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationAttemptStartDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationAttemptStartDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationAttemptStartDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> applicationAttemptIdBuilder_;
            private Object host_;
            private int rpcPort_;
            private YarnProtos.ContainerIdProto masterContainerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> masterContainerIdBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptStartDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.host_ = "";
                this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.host_ = "";
                this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationAttemptStartDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationAttemptIdFieldBuilder();
                    this.getMasterContainerIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.host_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.rpcPort_ = 0;
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                }
                else {
                    this.masterContainerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptStartDataProto_descriptor;
            }
            
            @Override
            public ApplicationAttemptStartDataProto getDefaultInstanceForType() {
                return ApplicationAttemptStartDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationAttemptStartDataProto build() {
                final ApplicationAttemptStartDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationAttemptStartDataProto buildPartial() {
                final ApplicationAttemptStartDataProto result = new ApplicationAttemptStartDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationAttemptIdBuilder_ == null) {
                    result.applicationAttemptId_ = this.applicationAttemptId_;
                }
                else {
                    result.applicationAttemptId_ = this.applicationAttemptIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.host_ = this.host_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.rpcPort_ = this.rpcPort_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                if (this.masterContainerIdBuilder_ == null) {
                    result.masterContainerId_ = this.masterContainerId_;
                }
                else {
                    result.masterContainerId_ = this.masterContainerIdBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationAttemptStartDataProto) {
                    return this.mergeFrom((ApplicationAttemptStartDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationAttemptStartDataProto other) {
                if (other == ApplicationAttemptStartDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationAttemptId()) {
                    this.mergeApplicationAttemptId(other.getApplicationAttemptId());
                }
                if (other.hasHost()) {
                    this.bitField0_ |= 0x2;
                    this.host_ = other.host_;
                    this.onChanged();
                }
                if (other.hasRpcPort()) {
                    this.setRpcPort(other.getRpcPort());
                }
                if (other.hasMasterContainerId()) {
                    this.mergeMasterContainerId(other.getMasterContainerId());
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
                ApplicationAttemptStartDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationAttemptStartDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationAttemptStartDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationAttemptId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    return this.applicationAttemptId_;
                }
                return this.applicationAttemptIdBuilder_.getMessage();
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationAttemptId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto.Builder builderForValue) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationAttemptId_ != YarnProtos.ApplicationAttemptIdProto.getDefaultInstance()) {
                        this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.newBuilder(this.applicationAttemptId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationAttemptId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationAttemptIdProto.Builder getApplicationAttemptIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationAttemptIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
                if (this.applicationAttemptIdBuilder_ != null) {
                    return this.applicationAttemptIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationAttemptId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> getApplicationAttemptIdFieldBuilder() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder>(this.applicationAttemptId_, this.getParentForChildren(), this.isClean());
                    this.applicationAttemptId_ = null;
                }
                return this.applicationAttemptIdBuilder_;
            }
            
            @Override
            public boolean hasHost() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getHost() {
                final Object ref = this.host_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.host_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getHostBytes() {
                final Object ref = this.host_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.host_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setHost(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.host_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearHost() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.host_ = ApplicationAttemptStartDataProto.getDefaultInstance().getHost();
                this.onChanged();
                return this;
            }
            
            public Builder setHostBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.host_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRpcPort() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public int getRpcPort() {
                return this.rpcPort_;
            }
            
            public Builder setRpcPort(final int value) {
                this.bitField0_ |= 0x4;
                this.rpcPort_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcPort() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.rpcPort_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMasterContainerId() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public YarnProtos.ContainerIdProto getMasterContainerId() {
                if (this.masterContainerIdBuilder_ == null) {
                    return this.masterContainerId_;
                }
                return this.masterContainerIdBuilder_.getMessage();
            }
            
            public Builder setMasterContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.masterContainerIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.masterContainerId_ = value;
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder setMasterContainerId(final YarnProtos.ContainerIdProto.Builder builderForValue) {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergeMasterContainerId(final YarnProtos.ContainerIdProto value) {
                if (this.masterContainerIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.masterContainerId_ != YarnProtos.ContainerIdProto.getDefaultInstance()) {
                        this.masterContainerId_ = YarnProtos.ContainerIdProto.newBuilder(this.masterContainerId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.masterContainerId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder clearMasterContainerId() {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.masterContainerIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public YarnProtos.ContainerIdProto.Builder getMasterContainerIdBuilder() {
                this.bitField0_ |= 0x8;
                this.onChanged();
                return this.getMasterContainerIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder() {
                if (this.masterContainerIdBuilder_ != null) {
                    return this.masterContainerIdBuilder_.getMessageOrBuilder();
                }
                return this.masterContainerId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> getMasterContainerIdFieldBuilder() {
                if (this.masterContainerIdBuilder_ == null) {
                    this.masterContainerIdBuilder_ = new SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder>(this.masterContainerId_, this.getParentForChildren(), this.isClean());
                    this.masterContainerId_ = null;
                }
                return this.masterContainerIdBuilder_;
            }
        }
    }
    
    public static final class ApplicationAttemptFinishDataProto extends GeneratedMessage implements ApplicationAttemptFinishDataProtoOrBuilder
    {
        private static final ApplicationAttemptFinishDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationAttemptFinishDataProto> PARSER;
        private int bitField0_;
        public static final int APPLICATION_ATTEMPT_ID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
        public static final int TRACKING_URL_FIELD_NUMBER = 2;
        private Object trackingUrl_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 3;
        private Object diagnosticsInfo_;
        public static final int FINAL_APPLICATION_STATUS_FIELD_NUMBER = 4;
        private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
        public static final int YARN_APPLICATION_ATTEMPT_STATE_FIELD_NUMBER = 5;
        private YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationAttemptFinishDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationAttemptFinishDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationAttemptFinishDataProto getDefaultInstance() {
            return ApplicationAttemptFinishDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationAttemptFinishDataProto getDefaultInstanceForType() {
            return ApplicationAttemptFinishDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationAttemptFinishDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.applicationAttemptId_.toBuilder();
                            }
                            this.applicationAttemptId_ = input.readMessage(YarnProtos.ApplicationAttemptIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationAttemptId_);
                                this.applicationAttemptId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.trackingUrl_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.FinalApplicationStatusProto value = YarnProtos.FinalApplicationStatusProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(4, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x8;
                            this.finalApplicationStatus_ = value;
                            continue;
                        }
                        case 40: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.YarnApplicationAttemptStateProto value2 = YarnProtos.YarnApplicationAttemptStateProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(5, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x10;
                            this.yarnApplicationAttemptState_ = value2;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptFinishDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationAttemptFinishDataProto> getParserForType() {
            return ApplicationAttemptFinishDataProto.PARSER;
        }
        
        @Override
        public boolean hasApplicationAttemptId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
            return this.applicationAttemptId_;
        }
        
        @Override
        public boolean hasTrackingUrl() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getTrackingUrl() {
            final Object ref = this.trackingUrl_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.trackingUrl_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getTrackingUrlBytes() {
            final Object ref = this.trackingUrl_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.trackingUrl_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasFinalApplicationStatus() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
            return this.finalApplicationStatus_;
        }
        
        @Override
        public boolean hasYarnApplicationAttemptState() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState() {
            return this.yarnApplicationAttemptState_;
        }
        
        private void initFields() {
            this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.trackingUrl_ = "";
            this.diagnosticsInfo_ = "";
            this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
            this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
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
                output.writeMessage(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeEnum(4, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeEnum(5, this.yarnApplicationAttemptState_.getNumber());
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
                size += CodedOutputStream.computeMessageSize(1, this.applicationAttemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeEnumSize(4, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeEnumSize(5, this.yarnApplicationAttemptState_.getNumber());
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
            if (!(obj instanceof ApplicationAttemptFinishDataProto)) {
                return super.equals(obj);
            }
            final ApplicationAttemptFinishDataProto other = (ApplicationAttemptFinishDataProto)obj;
            boolean result = true;
            result = (result && this.hasApplicationAttemptId() == other.hasApplicationAttemptId());
            if (this.hasApplicationAttemptId()) {
                result = (result && this.getApplicationAttemptId().equals(other.getApplicationAttemptId()));
            }
            result = (result && this.hasTrackingUrl() == other.hasTrackingUrl());
            if (this.hasTrackingUrl()) {
                result = (result && this.getTrackingUrl().equals(other.getTrackingUrl()));
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasFinalApplicationStatus() == other.hasFinalApplicationStatus());
            if (this.hasFinalApplicationStatus()) {
                result = (result && this.getFinalApplicationStatus() == other.getFinalApplicationStatus());
            }
            result = (result && this.hasYarnApplicationAttemptState() == other.hasYarnApplicationAttemptState());
            if (this.hasYarnApplicationAttemptState()) {
                result = (result && this.getYarnApplicationAttemptState() == other.getYarnApplicationAttemptState());
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
            if (this.hasApplicationAttemptId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getApplicationAttemptId().hashCode();
            }
            if (this.hasTrackingUrl()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getTrackingUrl().hashCode();
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasFinalApplicationStatus()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFinalApplicationStatus());
            }
            if (this.hasYarnApplicationAttemptState()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getYarnApplicationAttemptState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptFinishDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationAttemptFinishDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptFinishDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationAttemptFinishDataProto prototype) {
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
            ApplicationAttemptFinishDataProto.PARSER = new AbstractParser<ApplicationAttemptFinishDataProto>() {
                @Override
                public ApplicationAttemptFinishDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationAttemptFinishDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationAttemptFinishDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationAttemptFinishDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto applicationAttemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> applicationAttemptIdBuilder_;
            private Object trackingUrl_;
            private Object diagnosticsInfo_;
            private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
            private YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptFinishDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.trackingUrl_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.trackingUrl_ = "";
                this.diagnosticsInfo_ = "";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationAttemptFinishDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationAttemptIdFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.trackingUrl_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.bitField0_ &= 0xFFFFFFF7;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.bitField0_ &= 0xFFFFFFEF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ApplicationAttemptFinishDataProto_descriptor;
            }
            
            @Override
            public ApplicationAttemptFinishDataProto getDefaultInstanceForType() {
                return ApplicationAttemptFinishDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationAttemptFinishDataProto build() {
                final ApplicationAttemptFinishDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationAttemptFinishDataProto buildPartial() {
                final ApplicationAttemptFinishDataProto result = new ApplicationAttemptFinishDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.applicationAttemptIdBuilder_ == null) {
                    result.applicationAttemptId_ = this.applicationAttemptId_;
                }
                else {
                    result.applicationAttemptId_ = this.applicationAttemptIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.trackingUrl_ = this.trackingUrl_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.finalApplicationStatus_ = this.finalApplicationStatus_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.yarnApplicationAttemptState_ = this.yarnApplicationAttemptState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationAttemptFinishDataProto) {
                    return this.mergeFrom((ApplicationAttemptFinishDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationAttemptFinishDataProto other) {
                if (other == ApplicationAttemptFinishDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasApplicationAttemptId()) {
                    this.mergeApplicationAttemptId(other.getApplicationAttemptId());
                }
                if (other.hasTrackingUrl()) {
                    this.bitField0_ |= 0x2;
                    this.trackingUrl_ = other.trackingUrl_;
                    this.onChanged();
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x4;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasFinalApplicationStatus()) {
                    this.setFinalApplicationStatus(other.getFinalApplicationStatus());
                }
                if (other.hasYarnApplicationAttemptState()) {
                    this.setYarnApplicationAttemptState(other.getYarnApplicationAttemptState());
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
                ApplicationAttemptFinishDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationAttemptFinishDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationAttemptFinishDataProto)e.getUnfinishedMessage();
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
            public boolean hasApplicationAttemptId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    return this.applicationAttemptId_;
                }
                return this.applicationAttemptIdBuilder_.getMessage();
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationAttemptId_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto.Builder builderForValue) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeApplicationAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.applicationAttemptIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.applicationAttemptId_ != YarnProtos.ApplicationAttemptIdProto.getDefaultInstance()) {
                        this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.newBuilder(this.applicationAttemptId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationAttemptId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearApplicationAttemptId() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationAttemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationAttemptIdProto.Builder getApplicationAttemptIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getApplicationAttemptIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder() {
                if (this.applicationAttemptIdBuilder_ != null) {
                    return this.applicationAttemptIdBuilder_.getMessageOrBuilder();
                }
                return this.applicationAttemptId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> getApplicationAttemptIdFieldBuilder() {
                if (this.applicationAttemptIdBuilder_ == null) {
                    this.applicationAttemptIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder>(this.applicationAttemptId_, this.getParentForChildren(), this.isClean());
                    this.applicationAttemptId_ = null;
                }
                return this.applicationAttemptIdBuilder_;
            }
            
            @Override
            public boolean hasTrackingUrl() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getTrackingUrl() {
                final Object ref = this.trackingUrl_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.trackingUrl_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getTrackingUrlBytes() {
                final Object ref = this.trackingUrl_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.trackingUrl_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setTrackingUrl(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.trackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearTrackingUrl() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.trackingUrl_ = ApplicationAttemptFinishDataProto.getDefaultInstance().getTrackingUrl();
                this.onChanged();
                return this;
            }
            
            public Builder setTrackingUrlBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.trackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.diagnosticsInfo_ = ApplicationAttemptFinishDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinalApplicationStatus() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
                return this.finalApplicationStatus_;
            }
            
            public Builder setFinalApplicationStatus(final YarnProtos.FinalApplicationStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.finalApplicationStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalApplicationStatus() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasYarnApplicationAttemptState() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState() {
                return this.yarnApplicationAttemptState_;
            }
            
            public Builder setYarnApplicationAttemptState(final YarnProtos.YarnApplicationAttemptStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.yarnApplicationAttemptState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearYarnApplicationAttemptState() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.yarnApplicationAttemptState_ = YarnProtos.YarnApplicationAttemptStateProto.APP_ATTEMPT_NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ContainerHistoryDataProto extends GeneratedMessage implements ContainerHistoryDataProtoOrBuilder
    {
        private static final ContainerHistoryDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ContainerHistoryDataProto> PARSER;
        private int bitField0_;
        public static final int CONTAINER_ID_FIELD_NUMBER = 1;
        private YarnProtos.ContainerIdProto containerId_;
        public static final int ALLOCATED_RESOURCE_FIELD_NUMBER = 2;
        private YarnProtos.ResourceProto allocatedResource_;
        public static final int ASSIGNED_NODE_ID_FIELD_NUMBER = 3;
        private YarnProtos.NodeIdProto assignedNodeId_;
        public static final int PRIORITY_FIELD_NUMBER = 4;
        private YarnProtos.PriorityProto priority_;
        public static final int START_TIME_FIELD_NUMBER = 5;
        private long startTime_;
        public static final int FINISH_TIME_FIELD_NUMBER = 6;
        private long finishTime_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 7;
        private Object diagnosticsInfo_;
        public static final int CONTAINER_EXIT_STATUS_FIELD_NUMBER = 8;
        private int containerExitStatus_;
        public static final int CONTAINER_STATE_FIELD_NUMBER = 9;
        private YarnProtos.ContainerStateProto containerState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ContainerHistoryDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ContainerHistoryDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ContainerHistoryDataProto getDefaultInstance() {
            return ContainerHistoryDataProto.defaultInstance;
        }
        
        @Override
        public ContainerHistoryDataProto getDefaultInstanceForType() {
            return ContainerHistoryDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ContainerHistoryDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.ResourceProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder2 = this.allocatedResource_.toBuilder();
                            }
                            this.allocatedResource_ = input.readMessage(YarnProtos.ResourceProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.allocatedResource_);
                                this.allocatedResource_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            YarnProtos.NodeIdProto.Builder subBuilder3 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder3 = this.assignedNodeId_.toBuilder();
                            }
                            this.assignedNodeId_ = input.readMessage(YarnProtos.NodeIdProto.PARSER, extensionRegistry);
                            if (subBuilder3 != null) {
                                subBuilder3.mergeFrom(this.assignedNodeId_);
                                this.assignedNodeId_ = subBuilder3.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 34: {
                            YarnProtos.PriorityProto.Builder subBuilder4 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder4 = this.priority_.toBuilder();
                            }
                            this.priority_ = input.readMessage(YarnProtos.PriorityProto.PARSER, extensionRegistry);
                            if (subBuilder4 != null) {
                                subBuilder4.mergeFrom(this.priority_);
                                this.priority_ = subBuilder4.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x10;
                            this.startTime_ = input.readInt64();
                            continue;
                        }
                        case 48: {
                            this.bitField0_ |= 0x20;
                            this.finishTime_ = input.readInt64();
                            continue;
                        }
                        case 58: {
                            this.bitField0_ |= 0x40;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 64: {
                            this.bitField0_ |= 0x80;
                            this.containerExitStatus_ = input.readInt32();
                            continue;
                        }
                        case 72: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.ContainerStateProto value = YarnProtos.ContainerStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(9, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x100;
                            this.containerState_ = value;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerHistoryDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ContainerHistoryDataProto> getParserForType() {
            return ContainerHistoryDataProto.PARSER;
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
        public boolean hasAllocatedResource() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.ResourceProto getAllocatedResource() {
            return this.allocatedResource_;
        }
        
        @Override
        public YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder() {
            return this.allocatedResource_;
        }
        
        @Override
        public boolean hasAssignedNodeId() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public YarnProtos.NodeIdProto getAssignedNodeId() {
            return this.assignedNodeId_;
        }
        
        @Override
        public YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder() {
            return this.assignedNodeId_;
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
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        @Override
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasContainerExitStatus() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public int getContainerExitStatus() {
            return this.containerExitStatus_;
        }
        
        @Override
        public boolean hasContainerState() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public YarnProtos.ContainerStateProto getContainerState() {
            return this.containerState_;
        }
        
        private void initFields() {
            this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
            this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
            this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
            this.startTime_ = 0L;
            this.finishTime_ = 0L;
            this.diagnosticsInfo_ = "";
            this.containerExitStatus_ = 0;
            this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
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
                output.writeMessage(2, this.allocatedResource_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.assignedNodeId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeInt64(5, this.startTime_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeInt64(6, this.finishTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeBytes(7, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeInt32(8, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeEnum(9, this.containerState_.getNumber());
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
                size += CodedOutputStream.computeMessageSize(2, this.allocatedResource_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.assignedNodeId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeInt64Size(5, this.startTime_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeInt64Size(6, this.finishTime_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeBytesSize(7, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeInt32Size(8, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeEnumSize(9, this.containerState_.getNumber());
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
            if (!(obj instanceof ContainerHistoryDataProto)) {
                return super.equals(obj);
            }
            final ContainerHistoryDataProto other = (ContainerHistoryDataProto)obj;
            boolean result = true;
            result = (result && this.hasContainerId() == other.hasContainerId());
            if (this.hasContainerId()) {
                result = (result && this.getContainerId().equals(other.getContainerId()));
            }
            result = (result && this.hasAllocatedResource() == other.hasAllocatedResource());
            if (this.hasAllocatedResource()) {
                result = (result && this.getAllocatedResource().equals(other.getAllocatedResource()));
            }
            result = (result && this.hasAssignedNodeId() == other.hasAssignedNodeId());
            if (this.hasAssignedNodeId()) {
                result = (result && this.getAssignedNodeId().equals(other.getAssignedNodeId()));
            }
            result = (result && this.hasPriority() == other.hasPriority());
            if (this.hasPriority()) {
                result = (result && this.getPriority().equals(other.getPriority()));
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasContainerExitStatus() == other.hasContainerExitStatus());
            if (this.hasContainerExitStatus()) {
                result = (result && this.getContainerExitStatus() == other.getContainerExitStatus());
            }
            result = (result && this.hasContainerState() == other.hasContainerState());
            if (this.hasContainerState()) {
                result = (result && this.getContainerState() == other.getContainerState());
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
            if (this.hasAllocatedResource()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getAllocatedResource().hashCode();
            }
            if (this.hasAssignedNodeId()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getAssignedNodeId().hashCode();
            }
            if (this.hasPriority()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getPriority().hashCode();
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            if (this.hasFinishTime()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasContainerExitStatus()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + this.getContainerExitStatus();
            }
            if (this.hasContainerState()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getContainerState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ContainerHistoryDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ContainerHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerHistoryDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerHistoryDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ContainerHistoryDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerHistoryDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerHistoryDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerHistoryDataProto parseFrom(final InputStream input) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerHistoryDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ContainerHistoryDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ContainerHistoryDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ContainerHistoryDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerHistoryDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerHistoryDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ContainerHistoryDataProto prototype) {
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
            ContainerHistoryDataProto.PARSER = new AbstractParser<ContainerHistoryDataProto>() {
                @Override
                public ContainerHistoryDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ContainerHistoryDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ContainerHistoryDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ContainerHistoryDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ContainerIdProto containerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containerIdBuilder_;
            private YarnProtos.ResourceProto allocatedResource_;
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> allocatedResourceBuilder_;
            private YarnProtos.NodeIdProto assignedNodeId_;
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> assignedNodeIdBuilder_;
            private YarnProtos.PriorityProto priority_;
            private SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder> priorityBuilder_;
            private long startTime_;
            private long finishTime_;
            private Object diagnosticsInfo_;
            private int containerExitStatus_;
            private YarnProtos.ContainerStateProto containerState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerHistoryDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ContainerHistoryDataProto.alwaysUseFieldBuilders) {
                    this.getContainerIdFieldBuilder();
                    this.getAllocatedResourceFieldBuilder();
                    this.getAssignedNodeIdFieldBuilder();
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
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                }
                else {
                    this.allocatedResourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                }
                else {
                    this.assignedNodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.priorityBuilder_ == null) {
                    this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                }
                else {
                    this.priorityBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFEF;
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFDF;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFFBF;
                this.containerExitStatus_ = 0;
                this.bitField0_ &= 0xFFFFFF7F;
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.bitField0_ &= 0xFFFFFEFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerHistoryDataProto_descriptor;
            }
            
            @Override
            public ContainerHistoryDataProto getDefaultInstanceForType() {
                return ContainerHistoryDataProto.getDefaultInstance();
            }
            
            @Override
            public ContainerHistoryDataProto build() {
                final ContainerHistoryDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ContainerHistoryDataProto buildPartial() {
                final ContainerHistoryDataProto result = new ContainerHistoryDataProto((GeneratedMessage.Builder)this);
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
                if (this.allocatedResourceBuilder_ == null) {
                    result.allocatedResource_ = this.allocatedResource_;
                }
                else {
                    result.allocatedResource_ = this.allocatedResourceBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.assignedNodeIdBuilder_ == null) {
                    result.assignedNodeId_ = this.assignedNodeId_;
                }
                else {
                    result.assignedNodeId_ = this.assignedNodeIdBuilder_.build();
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
                result.startTime_ = this.startTime_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.finishTime_ = this.finishTime_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.containerExitStatus_ = this.containerExitStatus_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.containerState_ = this.containerState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ContainerHistoryDataProto) {
                    return this.mergeFrom((ContainerHistoryDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ContainerHistoryDataProto other) {
                if (other == ContainerHistoryDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerId()) {
                    this.mergeContainerId(other.getContainerId());
                }
                if (other.hasAllocatedResource()) {
                    this.mergeAllocatedResource(other.getAllocatedResource());
                }
                if (other.hasAssignedNodeId()) {
                    this.mergeAssignedNodeId(other.getAssignedNodeId());
                }
                if (other.hasPriority()) {
                    this.mergePriority(other.getPriority());
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x40;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasContainerExitStatus()) {
                    this.setContainerExitStatus(other.getContainerExitStatus());
                }
                if (other.hasContainerState()) {
                    this.setContainerState(other.getContainerState());
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
                ContainerHistoryDataProto parsedMessage = null;
                try {
                    parsedMessage = ContainerHistoryDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ContainerHistoryDataProto)e.getUnfinishedMessage();
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
            public boolean hasAllocatedResource() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.ResourceProto getAllocatedResource() {
                if (this.allocatedResourceBuilder_ == null) {
                    return this.allocatedResource_;
                }
                return this.allocatedResourceBuilder_.getMessage();
            }
            
            public Builder setAllocatedResource(final YarnProtos.ResourceProto value) {
                if (this.allocatedResourceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.allocatedResource_ = value;
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setAllocatedResource(final YarnProtos.ResourceProto.Builder builderForValue) {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeAllocatedResource(final YarnProtos.ResourceProto value) {
                if (this.allocatedResourceBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.allocatedResource_ != YarnProtos.ResourceProto.getDefaultInstance()) {
                        this.allocatedResource_ = YarnProtos.ResourceProto.newBuilder(this.allocatedResource_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.allocatedResource_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearAllocatedResource() {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnProtos.ResourceProto.Builder getAllocatedResourceBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getAllocatedResourceFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder() {
                if (this.allocatedResourceBuilder_ != null) {
                    return this.allocatedResourceBuilder_.getMessageOrBuilder();
                }
                return this.allocatedResource_;
            }
            
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> getAllocatedResourceFieldBuilder() {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResourceBuilder_ = new SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder>(this.allocatedResource_, this.getParentForChildren(), this.isClean());
                    this.allocatedResource_ = null;
                }
                return this.allocatedResourceBuilder_;
            }
            
            @Override
            public boolean hasAssignedNodeId() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public YarnProtos.NodeIdProto getAssignedNodeId() {
                if (this.assignedNodeIdBuilder_ == null) {
                    return this.assignedNodeId_;
                }
                return this.assignedNodeIdBuilder_.getMessage();
            }
            
            public Builder setAssignedNodeId(final YarnProtos.NodeIdProto value) {
                if (this.assignedNodeIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.assignedNodeId_ = value;
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setAssignedNodeId(final YarnProtos.NodeIdProto.Builder builderForValue) {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeAssignedNodeId(final YarnProtos.NodeIdProto value) {
                if (this.assignedNodeIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.assignedNodeId_ != YarnProtos.NodeIdProto.getDefaultInstance()) {
                        this.assignedNodeId_ = YarnProtos.NodeIdProto.newBuilder(this.assignedNodeId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.assignedNodeId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearAssignedNodeId() {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnProtos.NodeIdProto.Builder getAssignedNodeIdBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getAssignedNodeIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder() {
                if (this.assignedNodeIdBuilder_ != null) {
                    return this.assignedNodeIdBuilder_.getMessageOrBuilder();
                }
                return this.assignedNodeId_;
            }
            
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> getAssignedNodeIdFieldBuilder() {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeIdBuilder_ = new SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder>(this.assignedNodeId_, this.getParentForChildren(), this.isClean());
                    this.assignedNodeId_ = null;
                }
                return this.assignedNodeIdBuilder_;
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
            public boolean hasStartTime() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public long getStartTime() {
                return this.startTime_;
            }
            
            public Builder setStartTime(final long value) {
                this.bitField0_ |= 0x10;
                this.startTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStartTime() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.startTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x20;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.diagnosticsInfo_ = ContainerHistoryDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerExitStatus() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public int getContainerExitStatus() {
                return this.containerExitStatus_;
            }
            
            public Builder setContainerExitStatus(final int value) {
                this.bitField0_ |= 0x80;
                this.containerExitStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerExitStatus() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.containerExitStatus_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerState() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public YarnProtos.ContainerStateProto getContainerState() {
                return this.containerState_;
            }
            
            public Builder setContainerState(final YarnProtos.ContainerStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.containerState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerState() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ContainerStartDataProto extends GeneratedMessage implements ContainerStartDataProtoOrBuilder
    {
        private static final ContainerStartDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ContainerStartDataProto> PARSER;
        private int bitField0_;
        public static final int CONTAINER_ID_FIELD_NUMBER = 1;
        private YarnProtos.ContainerIdProto containerId_;
        public static final int ALLOCATED_RESOURCE_FIELD_NUMBER = 2;
        private YarnProtos.ResourceProto allocatedResource_;
        public static final int ASSIGNED_NODE_ID_FIELD_NUMBER = 3;
        private YarnProtos.NodeIdProto assignedNodeId_;
        public static final int PRIORITY_FIELD_NUMBER = 4;
        private YarnProtos.PriorityProto priority_;
        public static final int START_TIME_FIELD_NUMBER = 5;
        private long startTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ContainerStartDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ContainerStartDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ContainerStartDataProto getDefaultInstance() {
            return ContainerStartDataProto.defaultInstance;
        }
        
        @Override
        public ContainerStartDataProto getDefaultInstanceForType() {
            return ContainerStartDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ContainerStartDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnProtos.ResourceProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder2 = this.allocatedResource_.toBuilder();
                            }
                            this.allocatedResource_ = input.readMessage(YarnProtos.ResourceProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.allocatedResource_);
                                this.allocatedResource_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            YarnProtos.NodeIdProto.Builder subBuilder3 = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder3 = this.assignedNodeId_.toBuilder();
                            }
                            this.assignedNodeId_ = input.readMessage(YarnProtos.NodeIdProto.PARSER, extensionRegistry);
                            if (subBuilder3 != null) {
                                subBuilder3.mergeFrom(this.assignedNodeId_);
                                this.assignedNodeId_ = subBuilder3.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 34: {
                            YarnProtos.PriorityProto.Builder subBuilder4 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder4 = this.priority_.toBuilder();
                            }
                            this.priority_ = input.readMessage(YarnProtos.PriorityProto.PARSER, extensionRegistry);
                            if (subBuilder4 != null) {
                                subBuilder4.mergeFrom(this.priority_);
                                this.priority_ = subBuilder4.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x10;
                            this.startTime_ = input.readInt64();
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerStartDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ContainerStartDataProto> getParserForType() {
            return ContainerStartDataProto.PARSER;
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
        public boolean hasAllocatedResource() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.ResourceProto getAllocatedResource() {
            return this.allocatedResource_;
        }
        
        @Override
        public YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder() {
            return this.allocatedResource_;
        }
        
        @Override
        public boolean hasAssignedNodeId() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public YarnProtos.NodeIdProto getAssignedNodeId() {
            return this.assignedNodeId_;
        }
        
        @Override
        public YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder() {
            return this.assignedNodeId_;
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
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        private void initFields() {
            this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
            this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
            this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
            this.startTime_ = 0L;
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
                output.writeMessage(2, this.allocatedResource_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.assignedNodeId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeInt64(5, this.startTime_);
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
                size += CodedOutputStream.computeMessageSize(2, this.allocatedResource_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.assignedNodeId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.priority_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeInt64Size(5, this.startTime_);
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
            if (!(obj instanceof ContainerStartDataProto)) {
                return super.equals(obj);
            }
            final ContainerStartDataProto other = (ContainerStartDataProto)obj;
            boolean result = true;
            result = (result && this.hasContainerId() == other.hasContainerId());
            if (this.hasContainerId()) {
                result = (result && this.getContainerId().equals(other.getContainerId()));
            }
            result = (result && this.hasAllocatedResource() == other.hasAllocatedResource());
            if (this.hasAllocatedResource()) {
                result = (result && this.getAllocatedResource().equals(other.getAllocatedResource()));
            }
            result = (result && this.hasAssignedNodeId() == other.hasAssignedNodeId());
            if (this.hasAssignedNodeId()) {
                result = (result && this.getAssignedNodeId().equals(other.getAssignedNodeId()));
            }
            result = (result && this.hasPriority() == other.hasPriority());
            if (this.hasPriority()) {
                result = (result && this.getPriority().equals(other.getPriority()));
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
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
            if (this.hasAllocatedResource()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getAllocatedResource().hashCode();
            }
            if (this.hasAssignedNodeId()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getAssignedNodeId().hashCode();
            }
            if (this.hasPriority()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getPriority().hashCode();
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ContainerStartDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ContainerStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerStartDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerStartDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ContainerStartDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerStartDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerStartDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerStartDataProto parseFrom(final InputStream input) throws IOException {
            return ContainerStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerStartDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ContainerStartDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ContainerStartDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ContainerStartDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerStartDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ContainerStartDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ContainerStartDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerStartDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerStartDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ContainerStartDataProto prototype) {
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
            ContainerStartDataProto.PARSER = new AbstractParser<ContainerStartDataProto>() {
                @Override
                public ContainerStartDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ContainerStartDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ContainerStartDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ContainerStartDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ContainerIdProto containerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containerIdBuilder_;
            private YarnProtos.ResourceProto allocatedResource_;
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> allocatedResourceBuilder_;
            private YarnProtos.NodeIdProto assignedNodeId_;
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> assignedNodeIdBuilder_;
            private YarnProtos.PriorityProto priority_;
            private SingleFieldBuilder<YarnProtos.PriorityProto, YarnProtos.PriorityProto.Builder, YarnProtos.PriorityProtoOrBuilder> priorityBuilder_;
            private long startTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerStartDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ContainerStartDataProto.alwaysUseFieldBuilders) {
                    this.getContainerIdFieldBuilder();
                    this.getAllocatedResourceFieldBuilder();
                    this.getAssignedNodeIdFieldBuilder();
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
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                }
                else {
                    this.allocatedResourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                }
                else {
                    this.assignedNodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.priorityBuilder_ == null) {
                    this.priority_ = YarnProtos.PriorityProto.getDefaultInstance();
                }
                else {
                    this.priorityBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFEF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerStartDataProto_descriptor;
            }
            
            @Override
            public ContainerStartDataProto getDefaultInstanceForType() {
                return ContainerStartDataProto.getDefaultInstance();
            }
            
            @Override
            public ContainerStartDataProto build() {
                final ContainerStartDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ContainerStartDataProto buildPartial() {
                final ContainerStartDataProto result = new ContainerStartDataProto((GeneratedMessage.Builder)this);
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
                if (this.allocatedResourceBuilder_ == null) {
                    result.allocatedResource_ = this.allocatedResource_;
                }
                else {
                    result.allocatedResource_ = this.allocatedResourceBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.assignedNodeIdBuilder_ == null) {
                    result.assignedNodeId_ = this.assignedNodeId_;
                }
                else {
                    result.assignedNodeId_ = this.assignedNodeIdBuilder_.build();
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
                result.startTime_ = this.startTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ContainerStartDataProto) {
                    return this.mergeFrom((ContainerStartDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ContainerStartDataProto other) {
                if (other == ContainerStartDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerId()) {
                    this.mergeContainerId(other.getContainerId());
                }
                if (other.hasAllocatedResource()) {
                    this.mergeAllocatedResource(other.getAllocatedResource());
                }
                if (other.hasAssignedNodeId()) {
                    this.mergeAssignedNodeId(other.getAssignedNodeId());
                }
                if (other.hasPriority()) {
                    this.mergePriority(other.getPriority());
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
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
                ContainerStartDataProto parsedMessage = null;
                try {
                    parsedMessage = ContainerStartDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ContainerStartDataProto)e.getUnfinishedMessage();
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
            public boolean hasAllocatedResource() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.ResourceProto getAllocatedResource() {
                if (this.allocatedResourceBuilder_ == null) {
                    return this.allocatedResource_;
                }
                return this.allocatedResourceBuilder_.getMessage();
            }
            
            public Builder setAllocatedResource(final YarnProtos.ResourceProto value) {
                if (this.allocatedResourceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.allocatedResource_ = value;
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setAllocatedResource(final YarnProtos.ResourceProto.Builder builderForValue) {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeAllocatedResource(final YarnProtos.ResourceProto value) {
                if (this.allocatedResourceBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.allocatedResource_ != YarnProtos.ResourceProto.getDefaultInstance()) {
                        this.allocatedResource_ = YarnProtos.ResourceProto.newBuilder(this.allocatedResource_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.allocatedResource_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearAllocatedResource() {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResource_ = YarnProtos.ResourceProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.allocatedResourceBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnProtos.ResourceProto.Builder getAllocatedResourceBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getAllocatedResourceFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder() {
                if (this.allocatedResourceBuilder_ != null) {
                    return this.allocatedResourceBuilder_.getMessageOrBuilder();
                }
                return this.allocatedResource_;
            }
            
            private SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder> getAllocatedResourceFieldBuilder() {
                if (this.allocatedResourceBuilder_ == null) {
                    this.allocatedResourceBuilder_ = new SingleFieldBuilder<YarnProtos.ResourceProto, YarnProtos.ResourceProto.Builder, YarnProtos.ResourceProtoOrBuilder>(this.allocatedResource_, this.getParentForChildren(), this.isClean());
                    this.allocatedResource_ = null;
                }
                return this.allocatedResourceBuilder_;
            }
            
            @Override
            public boolean hasAssignedNodeId() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public YarnProtos.NodeIdProto getAssignedNodeId() {
                if (this.assignedNodeIdBuilder_ == null) {
                    return this.assignedNodeId_;
                }
                return this.assignedNodeIdBuilder_.getMessage();
            }
            
            public Builder setAssignedNodeId(final YarnProtos.NodeIdProto value) {
                if (this.assignedNodeIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.assignedNodeId_ = value;
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setAssignedNodeId(final YarnProtos.NodeIdProto.Builder builderForValue) {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeAssignedNodeId(final YarnProtos.NodeIdProto value) {
                if (this.assignedNodeIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.assignedNodeId_ != YarnProtos.NodeIdProto.getDefaultInstance()) {
                        this.assignedNodeId_ = YarnProtos.NodeIdProto.newBuilder(this.assignedNodeId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.assignedNodeId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearAssignedNodeId() {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeId_ = YarnProtos.NodeIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.assignedNodeIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public YarnProtos.NodeIdProto.Builder getAssignedNodeIdBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getAssignedNodeIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder() {
                if (this.assignedNodeIdBuilder_ != null) {
                    return this.assignedNodeIdBuilder_.getMessageOrBuilder();
                }
                return this.assignedNodeId_;
            }
            
            private SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder> getAssignedNodeIdFieldBuilder() {
                if (this.assignedNodeIdBuilder_ == null) {
                    this.assignedNodeIdBuilder_ = new SingleFieldBuilder<YarnProtos.NodeIdProto, YarnProtos.NodeIdProto.Builder, YarnProtos.NodeIdProtoOrBuilder>(this.assignedNodeId_, this.getParentForChildren(), this.isClean());
                    this.assignedNodeId_ = null;
                }
                return this.assignedNodeIdBuilder_;
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
            public boolean hasStartTime() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public long getStartTime() {
                return this.startTime_;
            }
            
            public Builder setStartTime(final long value) {
                this.bitField0_ |= 0x10;
                this.startTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStartTime() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.startTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ContainerFinishDataProto extends GeneratedMessage implements ContainerFinishDataProtoOrBuilder
    {
        private static final ContainerFinishDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ContainerFinishDataProto> PARSER;
        private int bitField0_;
        public static final int CONTAINER_ID_FIELD_NUMBER = 1;
        private YarnProtos.ContainerIdProto containerId_;
        public static final int FINISH_TIME_FIELD_NUMBER = 2;
        private long finishTime_;
        public static final int DIAGNOSTICS_INFO_FIELD_NUMBER = 3;
        private Object diagnosticsInfo_;
        public static final int CONTAINER_EXIT_STATUS_FIELD_NUMBER = 4;
        private int containerExitStatus_;
        public static final int CONTAINER_STATE_FIELD_NUMBER = 5;
        private YarnProtos.ContainerStateProto containerState_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ContainerFinishDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ContainerFinishDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ContainerFinishDataProto getDefaultInstance() {
            return ContainerFinishDataProto.defaultInstance;
        }
        
        @Override
        public ContainerFinishDataProto getDefaultInstanceForType() {
            return ContainerFinishDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ContainerFinishDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.bitField0_ |= 0x2;
                            this.finishTime_ = input.readInt64();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.diagnosticsInfo_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x8;
                            this.containerExitStatus_ = input.readInt32();
                            continue;
                        }
                        case 40: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.ContainerStateProto value = YarnProtos.ContainerStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(5, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x10;
                            this.containerState_ = value;
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
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerFinishDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ContainerFinishDataProto> getParserForType() {
            return ContainerFinishDataProto.PARSER;
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
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        @Override
        public boolean hasDiagnosticsInfo() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getDiagnosticsInfo() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.diagnosticsInfo_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getDiagnosticsInfoBytes() {
            final Object ref = this.diagnosticsInfo_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.diagnosticsInfo_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasContainerExitStatus() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public int getContainerExitStatus() {
            return this.containerExitStatus_;
        }
        
        @Override
        public boolean hasContainerState() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public YarnProtos.ContainerStateProto getContainerState() {
            return this.containerState_;
        }
        
        private void initFields() {
            this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
            this.finishTime_ = 0L;
            this.diagnosticsInfo_ = "";
            this.containerExitStatus_ = 0;
            this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
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
                output.writeInt64(2, this.finishTime_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeInt32(4, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeEnum(5, this.containerState_.getNumber());
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
                size += CodedOutputStream.computeInt64Size(2, this.finishTime_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getDiagnosticsInfoBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeInt32Size(4, this.containerExitStatus_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeEnumSize(5, this.containerState_.getNumber());
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
            if (!(obj instanceof ContainerFinishDataProto)) {
                return super.equals(obj);
            }
            final ContainerFinishDataProto other = (ContainerFinishDataProto)obj;
            boolean result = true;
            result = (result && this.hasContainerId() == other.hasContainerId());
            if (this.hasContainerId()) {
                result = (result && this.getContainerId().equals(other.getContainerId()));
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
            }
            result = (result && this.hasDiagnosticsInfo() == other.hasDiagnosticsInfo());
            if (this.hasDiagnosticsInfo()) {
                result = (result && this.getDiagnosticsInfo().equals(other.getDiagnosticsInfo()));
            }
            result = (result && this.hasContainerExitStatus() == other.hasContainerExitStatus());
            if (this.hasContainerExitStatus()) {
                result = (result && this.getContainerExitStatus() == other.getContainerExitStatus());
            }
            result = (result && this.hasContainerState() == other.hasContainerState());
            if (this.hasContainerState()) {
                result = (result && this.getContainerState() == other.getContainerState());
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
            if (this.hasFinishTime()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            if (this.hasDiagnosticsInfo()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getDiagnosticsInfo().hashCode();
            }
            if (this.hasContainerExitStatus()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getContainerExitStatus();
            }
            if (this.hasContainerState()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getContainerState());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ContainerFinishDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ContainerFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerFinishDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerFinishDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ContainerFinishDataProto.PARSER.parseFrom(data);
        }
        
        public static ContainerFinishDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ContainerFinishDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ContainerFinishDataProto parseFrom(final InputStream input) throws IOException {
            return ContainerFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerFinishDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ContainerFinishDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ContainerFinishDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ContainerFinishDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerFinishDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ContainerFinishDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ContainerFinishDataProto.PARSER.parseFrom(input);
        }
        
        public static ContainerFinishDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ContainerFinishDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ContainerFinishDataProto prototype) {
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
            ContainerFinishDataProto.PARSER = new AbstractParser<ContainerFinishDataProto>() {
                @Override
                public ContainerFinishDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ContainerFinishDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ContainerFinishDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ContainerFinishDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ContainerIdProto containerId_;
            private SingleFieldBuilder<YarnProtos.ContainerIdProto, YarnProtos.ContainerIdProto.Builder, YarnProtos.ContainerIdProtoOrBuilder> containerIdBuilder_;
            private long finishTime_;
            private Object diagnosticsInfo_;
            private int containerExitStatus_;
            private YarnProtos.ContainerStateProto containerState_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ContainerFinishDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.containerId_ = YarnProtos.ContainerIdProto.getDefaultInstance();
                this.diagnosticsInfo_ = "";
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ContainerFinishDataProto.alwaysUseFieldBuilders) {
                    this.getContainerIdFieldBuilder();
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
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFD;
                this.diagnosticsInfo_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.containerExitStatus_ = 0;
                this.bitField0_ &= 0xFFFFFFF7;
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.bitField0_ &= 0xFFFFFFEF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return ApplicationHistoryServerProtos.internal_static_hadoop_yarn_ContainerFinishDataProto_descriptor;
            }
            
            @Override
            public ContainerFinishDataProto getDefaultInstanceForType() {
                return ContainerFinishDataProto.getDefaultInstance();
            }
            
            @Override
            public ContainerFinishDataProto build() {
                final ContainerFinishDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ContainerFinishDataProto buildPartial() {
                final ContainerFinishDataProto result = new ContainerFinishDataProto((GeneratedMessage.Builder)this);
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
                result.finishTime_ = this.finishTime_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.diagnosticsInfo_ = this.diagnosticsInfo_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.containerExitStatus_ = this.containerExitStatus_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.containerState_ = this.containerState_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ContainerFinishDataProto) {
                    return this.mergeFrom((ContainerFinishDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ContainerFinishDataProto other) {
                if (other == ContainerFinishDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContainerId()) {
                    this.mergeContainerId(other.getContainerId());
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
                }
                if (other.hasDiagnosticsInfo()) {
                    this.bitField0_ |= 0x4;
                    this.diagnosticsInfo_ = other.diagnosticsInfo_;
                    this.onChanged();
                }
                if (other.hasContainerExitStatus()) {
                    this.setContainerExitStatus(other.getContainerExitStatus());
                }
                if (other.hasContainerState()) {
                    this.setContainerState(other.getContainerState());
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
                ContainerFinishDataProto parsedMessage = null;
                try {
                    parsedMessage = ContainerFinishDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ContainerFinishDataProto)e.getUnfinishedMessage();
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
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x2;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnosticsInfo() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getDiagnosticsInfo() {
                final Object ref = this.diagnosticsInfo_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.diagnosticsInfo_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getDiagnosticsInfoBytes() {
                final Object ref = this.diagnosticsInfo_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.diagnosticsInfo_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDiagnosticsInfo(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnosticsInfo() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.diagnosticsInfo_ = ContainerFinishDataProto.getDefaultInstance().getDiagnosticsInfo();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsInfoBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.diagnosticsInfo_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerExitStatus() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public int getContainerExitStatus() {
                return this.containerExitStatus_;
            }
            
            public Builder setContainerExitStatus(final int value) {
                this.bitField0_ |= 0x8;
                this.containerExitStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerExitStatus() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.containerExitStatus_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasContainerState() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public YarnProtos.ContainerStateProto getContainerState() {
                return this.containerState_;
            }
            
            public Builder setContainerState(final YarnProtos.ContainerStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.containerState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContainerState() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.containerState_ = YarnProtos.ContainerStateProto.C_NEW;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface ContainerFinishDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerId();
        
        YarnProtos.ContainerIdProto getContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder();
        
        boolean hasFinishTime();
        
        long getFinishTime();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasContainerExitStatus();
        
        int getContainerExitStatus();
        
        boolean hasContainerState();
        
        YarnProtos.ContainerStateProto getContainerState();
    }
    
    public interface ContainerStartDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerId();
        
        YarnProtos.ContainerIdProto getContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder();
        
        boolean hasAllocatedResource();
        
        YarnProtos.ResourceProto getAllocatedResource();
        
        YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder();
        
        boolean hasAssignedNodeId();
        
        YarnProtos.NodeIdProto getAssignedNodeId();
        
        YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder();
        
        boolean hasPriority();
        
        YarnProtos.PriorityProto getPriority();
        
        YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder();
        
        boolean hasStartTime();
        
        long getStartTime();
    }
    
    public interface ContainerHistoryDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContainerId();
        
        YarnProtos.ContainerIdProto getContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getContainerIdOrBuilder();
        
        boolean hasAllocatedResource();
        
        YarnProtos.ResourceProto getAllocatedResource();
        
        YarnProtos.ResourceProtoOrBuilder getAllocatedResourceOrBuilder();
        
        boolean hasAssignedNodeId();
        
        YarnProtos.NodeIdProto getAssignedNodeId();
        
        YarnProtos.NodeIdProtoOrBuilder getAssignedNodeIdOrBuilder();
        
        boolean hasPriority();
        
        YarnProtos.PriorityProto getPriority();
        
        YarnProtos.PriorityProtoOrBuilder getPriorityOrBuilder();
        
        boolean hasStartTime();
        
        long getStartTime();
        
        boolean hasFinishTime();
        
        long getFinishTime();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasContainerExitStatus();
        
        int getContainerExitStatus();
        
        boolean hasContainerState();
        
        YarnProtos.ContainerStateProto getContainerState();
    }
    
    public interface ApplicationAttemptFinishDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder();
        
        boolean hasTrackingUrl();
        
        String getTrackingUrl();
        
        ByteString getTrackingUrlBytes();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasFinalApplicationStatus();
        
        YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus();
        
        boolean hasYarnApplicationAttemptState();
        
        YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState();
    }
    
    public interface ApplicationAttemptStartDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder();
        
        boolean hasHost();
        
        String getHost();
        
        ByteString getHostBytes();
        
        boolean hasRpcPort();
        
        int getRpcPort();
        
        boolean hasMasterContainerId();
        
        YarnProtos.ContainerIdProto getMasterContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder();
    }
    
    public interface ApplicationAttemptHistoryDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getApplicationAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getApplicationAttemptIdOrBuilder();
        
        boolean hasHost();
        
        String getHost();
        
        ByteString getHostBytes();
        
        boolean hasRpcPort();
        
        int getRpcPort();
        
        boolean hasTrackingUrl();
        
        String getTrackingUrl();
        
        ByteString getTrackingUrlBytes();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasFinalApplicationStatus();
        
        YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus();
        
        boolean hasMasterContainerId();
        
        YarnProtos.ContainerIdProto getMasterContainerId();
        
        YarnProtos.ContainerIdProtoOrBuilder getMasterContainerIdOrBuilder();
        
        boolean hasYarnApplicationAttemptState();
        
        YarnProtos.YarnApplicationAttemptStateProto getYarnApplicationAttemptState();
    }
    
    public interface ApplicationFinishDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationId();
        
        YarnProtos.ApplicationIdProto getApplicationId();
        
        YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder();
        
        boolean hasFinishTime();
        
        long getFinishTime();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasFinalApplicationStatus();
        
        YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus();
        
        boolean hasYarnApplicationState();
        
        YarnProtos.YarnApplicationStateProto getYarnApplicationState();
    }
    
    public interface ApplicationStartDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationId();
        
        YarnProtos.ApplicationIdProto getApplicationId();
        
        YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder();
        
        boolean hasApplicationName();
        
        String getApplicationName();
        
        ByteString getApplicationNameBytes();
        
        boolean hasApplicationType();
        
        String getApplicationType();
        
        ByteString getApplicationTypeBytes();
        
        boolean hasUser();
        
        String getUser();
        
        ByteString getUserBytes();
        
        boolean hasQueue();
        
        String getQueue();
        
        ByteString getQueueBytes();
        
        boolean hasSubmitTime();
        
        long getSubmitTime();
        
        boolean hasStartTime();
        
        long getStartTime();
    }
    
    public interface ApplicationHistoryDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasApplicationId();
        
        YarnProtos.ApplicationIdProto getApplicationId();
        
        YarnProtos.ApplicationIdProtoOrBuilder getApplicationIdOrBuilder();
        
        boolean hasApplicationName();
        
        String getApplicationName();
        
        ByteString getApplicationNameBytes();
        
        boolean hasApplicationType();
        
        String getApplicationType();
        
        ByteString getApplicationTypeBytes();
        
        boolean hasUser();
        
        String getUser();
        
        ByteString getUserBytes();
        
        boolean hasQueue();
        
        String getQueue();
        
        ByteString getQueueBytes();
        
        boolean hasSubmitTime();
        
        long getSubmitTime();
        
        boolean hasStartTime();
        
        long getStartTime();
        
        boolean hasFinishTime();
        
        long getFinishTime();
        
        boolean hasDiagnosticsInfo();
        
        String getDiagnosticsInfo();
        
        ByteString getDiagnosticsInfoBytes();
        
        boolean hasFinalApplicationStatus();
        
        YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus();
        
        boolean hasYarnApplicationState();
        
        YarnProtos.YarnApplicationStateProto getYarnApplicationState();
    }
}
