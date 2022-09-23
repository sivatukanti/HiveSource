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
import com.google.protobuf.Internal;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class YarnServerResourceManagerRecoveryProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationStateDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_EpochProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_EpochProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnServerResourceManagerRecoveryProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnServerResourceManagerRecoveryProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n*yarn_server_resourcemanager_recovery.proto\u0012\u000bhadoop.yarn\u001a\u001fyarn_server_common_protos.proto\u001a\u0011yarn_protos.proto\u001a\u0019yarn_security_token.proto\"\u0092\u0002\n\u0019ApplicationStateDataProto\u0012\u0013\n\u000bsubmit_time\u0018\u0001 \u0001(\u0003\u0012V\n\u001eapplication_submission_context\u0018\u0002 \u0001(\u000b2..hadoop.yarn.ApplicationSubmissionContextProto\u0012\f\n\u0004user\u0018\u0003 \u0001(\t\u0012\u0012\n\nstart_time\u0018\u0004 \u0001(\u0003\u00127\n\u0011application_state\u0018\u0005 \u0001(\u000e2\u001c.hadoop.yarn.RMAppStateProto\u0012\u0018\n\u000bdiagnostics\u0018\u0006 \u0001(\t:\u0003N/A\u0012\u0013\n\u000bfini", "sh_time\u0018\u0007 \u0001(\u0003\"\u00f3\u0003\n ApplicationAttemptStateDataProto\u00129\n\tattemptId\u0018\u0001 \u0001(\u000b2&.hadoop.yarn.ApplicationAttemptIdProto\u00125\n\u0010master_container\u0018\u0002 \u0001(\u000b2\u001b.hadoop.yarn.ContainerProto\u0012\u001a\n\u0012app_attempt_tokens\u0018\u0003 \u0001(\f\u0012>\n\u0011app_attempt_state\u0018\u0004 \u0001(\u000e2#.hadoop.yarn.RMAppAttemptStateProto\u0012\u001a\n\u0012final_tracking_url\u0018\u0005 \u0001(\t\u0012\u0018\n\u000bdiagnostics\u0018\u0006 \u0001(\t:\u0003N/A\u0012\u0012\n\nstart_time\u0018\u0007 \u0001(\u0003\u0012J\n\u0018final_application_status\u0018\b \u0001(\u000e2(.hadoop.yarn.FinalApplicationStatu", "sProto\u0012'\n\u0018am_container_exit_status\u0018\t \u0001(\u0005:\u0005-1000\u0012\u0016\n\u000ememory_seconds\u0018\n \u0001(\u0003\u0012\u0015\n\rvcore_seconds\u0018\u000b \u0001(\u0003\u0012\u0013\n\u000bfinish_time\u0018\f \u0001(\u0003\"\u001b\n\nEpochProto\u0012\r\n\u0005epoch\u0018\u0001 \u0001(\u0003\"\u0091\u0001\n AMRMTokenSecretManagerStateProto\u00127\n\u0012current_master_key\u0018\u0001 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\u00124\n\u000fnext_master_key\u0018\u0002 \u0001(\u000b2\u001b.hadoop.yarn.MasterKeyProto\"\u0084\u0001\n$RMDelegationTokenIdentifierDataProto\u0012I\n\u0010token_identifier\u0018\u0001 \u0001(\u000b2/.hadoop.yarn.YARNDelegationTokenIdentif", "ierProto\u0012\u0011\n\trenewDate\u0018\u0002 \u0001(\u0003*\u0080\u0003\n\u0016RMAppAttemptStateProto\u0012\u0011\n\rRMATTEMPT_NEW\u0010\u0001\u0012\u0017\n\u0013RMATTEMPT_SUBMITTED\u0010\u0002\u0012\u0017\n\u0013RMATTEMPT_SCHEDULED\u0010\u0003\u0012\u0017\n\u0013RMATTEMPT_ALLOCATED\u0010\u0004\u0012\u0016\n\u0012RMATTEMPT_LAUNCHED\u0010\u0005\u0012\u0014\n\u0010RMATTEMPT_FAILED\u0010\u0006\u0012\u0015\n\u0011RMATTEMPT_RUNNING\u0010\u0007\u0012\u0017\n\u0013RMATTEMPT_FINISHING\u0010\b\u0012\u0016\n\u0012RMATTEMPT_FINISHED\u0010\t\u0012\u0014\n\u0010RMATTEMPT_KILLED\u0010\n\u0012\u001e\n\u001aRMATTEMPT_ALLOCATED_SAVING\u0010\u000b\u0012'\n#RMATTEMPT_LAUNCHED_UNMANAGED_SAVING\u0010\f\u0012\u0017\n\u0013RMATTEMPT_RECOVERED\u0010\r\u0012\u001a\n\u0016RMATTEMPT_", "FINAL_SAVING\u0010\u000e*\u00d7\u0001\n\u000fRMAppStateProto\u0012\r\n\tRMAPP_NEW\u0010\u0001\u0012\u0014\n\u0010RMAPP_NEW_SAVING\u0010\u0002\u0012\u0013\n\u000fRMAPP_SUBMITTED\u0010\u0003\u0012\u0012\n\u000eRMAPP_ACCEPTED\u0010\u0004\u0012\u0011\n\rRMAPP_RUNNING\u0010\u0005\u0012\u0016\n\u0012RMAPP_FINAL_SAVING\u0010\u0006\u0012\u0013\n\u000fRMAPP_FINISHING\u0010\u0007\u0012\u0012\n\u000eRMAPP_FINISHED\u0010\b\u0012\u0010\n\fRMAPP_FAILED\u0010\t\u0012\u0010\n\fRMAPP_KILLED\u0010\nBM\n\u001corg.apache.hadoop.yarn.protoB'YarnServerResourceManagerRecoveryProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnServerResourceManagerRecoveryProtos.descriptor = root;
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor = YarnServerResourceManagerRecoveryProtos.getDescriptor().getMessageTypes().get(0);
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor, new String[] { "SubmitTime", "ApplicationSubmissionContext", "User", "StartTime", "ApplicationState", "Diagnostics", "FinishTime" });
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor = YarnServerResourceManagerRecoveryProtos.getDescriptor().getMessageTypes().get(1);
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor, new String[] { "AttemptId", "MasterContainer", "AppAttemptTokens", "AppAttemptState", "FinalTrackingUrl", "Diagnostics", "StartTime", "FinalApplicationStatus", "AmContainerExitStatus", "MemorySeconds", "VcoreSeconds", "FinishTime" });
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_descriptor = YarnServerResourceManagerRecoveryProtos.getDescriptor().getMessageTypes().get(2);
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_descriptor, new String[] { "Epoch" });
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor = YarnServerResourceManagerRecoveryProtos.getDescriptor().getMessageTypes().get(3);
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor, new String[] { "CurrentMasterKey", "NextMasterKey" });
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor = YarnServerResourceManagerRecoveryProtos.getDescriptor().getMessageTypes().get(4);
                YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor, new String[] { "TokenIdentifier", "RenewDate" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnServerCommonProtos.getDescriptor(), YarnProtos.getDescriptor(), YarnSecurityTokenProtos.getDescriptor() }, assigner);
    }
    
    public enum RMAppAttemptStateProto implements ProtocolMessageEnum
    {
        RMATTEMPT_NEW(0, 1), 
        RMATTEMPT_SUBMITTED(1, 2), 
        RMATTEMPT_SCHEDULED(2, 3), 
        RMATTEMPT_ALLOCATED(3, 4), 
        RMATTEMPT_LAUNCHED(4, 5), 
        RMATTEMPT_FAILED(5, 6), 
        RMATTEMPT_RUNNING(6, 7), 
        RMATTEMPT_FINISHING(7, 8), 
        RMATTEMPT_FINISHED(8, 9), 
        RMATTEMPT_KILLED(9, 10), 
        RMATTEMPT_ALLOCATED_SAVING(10, 11), 
        RMATTEMPT_LAUNCHED_UNMANAGED_SAVING(11, 12), 
        RMATTEMPT_RECOVERED(12, 13), 
        RMATTEMPT_FINAL_SAVING(13, 14);
        
        public static final int RMATTEMPT_NEW_VALUE = 1;
        public static final int RMATTEMPT_SUBMITTED_VALUE = 2;
        public static final int RMATTEMPT_SCHEDULED_VALUE = 3;
        public static final int RMATTEMPT_ALLOCATED_VALUE = 4;
        public static final int RMATTEMPT_LAUNCHED_VALUE = 5;
        public static final int RMATTEMPT_FAILED_VALUE = 6;
        public static final int RMATTEMPT_RUNNING_VALUE = 7;
        public static final int RMATTEMPT_FINISHING_VALUE = 8;
        public static final int RMATTEMPT_FINISHED_VALUE = 9;
        public static final int RMATTEMPT_KILLED_VALUE = 10;
        public static final int RMATTEMPT_ALLOCATED_SAVING_VALUE = 11;
        public static final int RMATTEMPT_LAUNCHED_UNMANAGED_SAVING_VALUE = 12;
        public static final int RMATTEMPT_RECOVERED_VALUE = 13;
        public static final int RMATTEMPT_FINAL_SAVING_VALUE = 14;
        private static Internal.EnumLiteMap<RMAppAttemptStateProto> internalValueMap;
        private static final RMAppAttemptStateProto[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static RMAppAttemptStateProto valueOf(final int value) {
            switch (value) {
                case 1: {
                    return RMAppAttemptStateProto.RMATTEMPT_NEW;
                }
                case 2: {
                    return RMAppAttemptStateProto.RMATTEMPT_SUBMITTED;
                }
                case 3: {
                    return RMAppAttemptStateProto.RMATTEMPT_SCHEDULED;
                }
                case 4: {
                    return RMAppAttemptStateProto.RMATTEMPT_ALLOCATED;
                }
                case 5: {
                    return RMAppAttemptStateProto.RMATTEMPT_LAUNCHED;
                }
                case 6: {
                    return RMAppAttemptStateProto.RMATTEMPT_FAILED;
                }
                case 7: {
                    return RMAppAttemptStateProto.RMATTEMPT_RUNNING;
                }
                case 8: {
                    return RMAppAttemptStateProto.RMATTEMPT_FINISHING;
                }
                case 9: {
                    return RMAppAttemptStateProto.RMATTEMPT_FINISHED;
                }
                case 10: {
                    return RMAppAttemptStateProto.RMATTEMPT_KILLED;
                }
                case 11: {
                    return RMAppAttemptStateProto.RMATTEMPT_ALLOCATED_SAVING;
                }
                case 12: {
                    return RMAppAttemptStateProto.RMATTEMPT_LAUNCHED_UNMANAGED_SAVING;
                }
                case 13: {
                    return RMAppAttemptStateProto.RMATTEMPT_RECOVERED;
                }
                case 14: {
                    return RMAppAttemptStateProto.RMATTEMPT_FINAL_SAVING;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<RMAppAttemptStateProto> internalGetValueMap() {
            return RMAppAttemptStateProto.internalValueMap;
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
            return YarnServerResourceManagerRecoveryProtos.getDescriptor().getEnumTypes().get(0);
        }
        
        public static RMAppAttemptStateProto valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return RMAppAttemptStateProto.VALUES[desc.getIndex()];
        }
        
        private RMAppAttemptStateProto(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            RMAppAttemptStateProto.internalValueMap = new Internal.EnumLiteMap<RMAppAttemptStateProto>() {
                @Override
                public RMAppAttemptStateProto findValueByNumber(final int number) {
                    return RMAppAttemptStateProto.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public enum RMAppStateProto implements ProtocolMessageEnum
    {
        RMAPP_NEW(0, 1), 
        RMAPP_NEW_SAVING(1, 2), 
        RMAPP_SUBMITTED(2, 3), 
        RMAPP_ACCEPTED(3, 4), 
        RMAPP_RUNNING(4, 5), 
        RMAPP_FINAL_SAVING(5, 6), 
        RMAPP_FINISHING(6, 7), 
        RMAPP_FINISHED(7, 8), 
        RMAPP_FAILED(8, 9), 
        RMAPP_KILLED(9, 10);
        
        public static final int RMAPP_NEW_VALUE = 1;
        public static final int RMAPP_NEW_SAVING_VALUE = 2;
        public static final int RMAPP_SUBMITTED_VALUE = 3;
        public static final int RMAPP_ACCEPTED_VALUE = 4;
        public static final int RMAPP_RUNNING_VALUE = 5;
        public static final int RMAPP_FINAL_SAVING_VALUE = 6;
        public static final int RMAPP_FINISHING_VALUE = 7;
        public static final int RMAPP_FINISHED_VALUE = 8;
        public static final int RMAPP_FAILED_VALUE = 9;
        public static final int RMAPP_KILLED_VALUE = 10;
        private static Internal.EnumLiteMap<RMAppStateProto> internalValueMap;
        private static final RMAppStateProto[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static RMAppStateProto valueOf(final int value) {
            switch (value) {
                case 1: {
                    return RMAppStateProto.RMAPP_NEW;
                }
                case 2: {
                    return RMAppStateProto.RMAPP_NEW_SAVING;
                }
                case 3: {
                    return RMAppStateProto.RMAPP_SUBMITTED;
                }
                case 4: {
                    return RMAppStateProto.RMAPP_ACCEPTED;
                }
                case 5: {
                    return RMAppStateProto.RMAPP_RUNNING;
                }
                case 6: {
                    return RMAppStateProto.RMAPP_FINAL_SAVING;
                }
                case 7: {
                    return RMAppStateProto.RMAPP_FINISHING;
                }
                case 8: {
                    return RMAppStateProto.RMAPP_FINISHED;
                }
                case 9: {
                    return RMAppStateProto.RMAPP_FAILED;
                }
                case 10: {
                    return RMAppStateProto.RMAPP_KILLED;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<RMAppStateProto> internalGetValueMap() {
            return RMAppStateProto.internalValueMap;
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
            return YarnServerResourceManagerRecoveryProtos.getDescriptor().getEnumTypes().get(1);
        }
        
        public static RMAppStateProto valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return RMAppStateProto.VALUES[desc.getIndex()];
        }
        
        private RMAppStateProto(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            RMAppStateProto.internalValueMap = new Internal.EnumLiteMap<RMAppStateProto>() {
                @Override
                public RMAppStateProto findValueByNumber(final int number) {
                    return RMAppStateProto.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public static final class ApplicationStateDataProto extends GeneratedMessage implements ApplicationStateDataProtoOrBuilder
    {
        private static final ApplicationStateDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationStateDataProto> PARSER;
        private int bitField0_;
        public static final int SUBMIT_TIME_FIELD_NUMBER = 1;
        private long submitTime_;
        public static final int APPLICATION_SUBMISSION_CONTEXT_FIELD_NUMBER = 2;
        private YarnProtos.ApplicationSubmissionContextProto applicationSubmissionContext_;
        public static final int USER_FIELD_NUMBER = 3;
        private Object user_;
        public static final int START_TIME_FIELD_NUMBER = 4;
        private long startTime_;
        public static final int APPLICATION_STATE_FIELD_NUMBER = 5;
        private RMAppStateProto applicationState_;
        public static final int DIAGNOSTICS_FIELD_NUMBER = 6;
        private Object diagnostics_;
        public static final int FINISH_TIME_FIELD_NUMBER = 7;
        private long finishTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationStateDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationStateDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationStateDataProto getDefaultInstance() {
            return ApplicationStateDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationStateDataProto getDefaultInstanceForType() {
            return ApplicationStateDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationStateDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.submitTime_ = input.readInt64();
                            continue;
                        }
                        case 18: {
                            YarnProtos.ApplicationSubmissionContextProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.applicationSubmissionContext_.toBuilder();
                            }
                            this.applicationSubmissionContext_ = input.readMessage(YarnProtos.ApplicationSubmissionContextProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.applicationSubmissionContext_);
                                this.applicationSubmissionContext_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.user_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x8;
                            this.startTime_ = input.readInt64();
                            continue;
                        }
                        case 40: {
                            final int rawValue = input.readEnum();
                            final RMAppStateProto value = RMAppStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(5, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x10;
                            this.applicationState_ = value;
                            continue;
                        }
                        case 50: {
                            this.bitField0_ |= 0x20;
                            this.diagnostics_ = input.readBytes();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.finishTime_ = input.readInt64();
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
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationStateDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationStateDataProto> getParserForType() {
            return ApplicationStateDataProto.PARSER;
        }
        
        @Override
        public boolean hasSubmitTime() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getSubmitTime() {
            return this.submitTime_;
        }
        
        @Override
        public boolean hasApplicationSubmissionContext() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.ApplicationSubmissionContextProto getApplicationSubmissionContext() {
            return this.applicationSubmissionContext_;
        }
        
        @Override
        public YarnProtos.ApplicationSubmissionContextProtoOrBuilder getApplicationSubmissionContextOrBuilder() {
            return this.applicationSubmissionContext_;
        }
        
        @Override
        public boolean hasUser() {
            return (this.bitField0_ & 0x4) == 0x4;
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
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        @Override
        public boolean hasApplicationState() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public RMAppStateProto getApplicationState() {
            return this.applicationState_;
        }
        
        @Override
        public boolean hasDiagnostics() {
            return (this.bitField0_ & 0x20) == 0x20;
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
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        private void initFields() {
            this.submitTime_ = 0L;
            this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
            this.user_ = "";
            this.startTime_ = 0L;
            this.applicationState_ = RMAppStateProto.RMAPP_NEW;
            this.diagnostics_ = "N/A";
            this.finishTime_ = 0L;
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
                output.writeInt64(1, this.submitTime_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.applicationSubmissionContext_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeInt64(4, this.startTime_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeEnum(5, this.applicationState_.getNumber());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(6, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.finishTime_);
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
                size += CodedOutputStream.computeInt64Size(1, this.submitTime_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.applicationSubmissionContext_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getUserBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeInt64Size(4, this.startTime_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeEnumSize(5, this.applicationState_.getNumber());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(6, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.finishTime_);
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
            if (!(obj instanceof ApplicationStateDataProto)) {
                return super.equals(obj);
            }
            final ApplicationStateDataProto other = (ApplicationStateDataProto)obj;
            boolean result = true;
            result = (result && this.hasSubmitTime() == other.hasSubmitTime());
            if (this.hasSubmitTime()) {
                result = (result && this.getSubmitTime() == other.getSubmitTime());
            }
            result = (result && this.hasApplicationSubmissionContext() == other.hasApplicationSubmissionContext());
            if (this.hasApplicationSubmissionContext()) {
                result = (result && this.getApplicationSubmissionContext().equals(other.getApplicationSubmissionContext()));
            }
            result = (result && this.hasUser() == other.hasUser());
            if (this.hasUser()) {
                result = (result && this.getUser().equals(other.getUser()));
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
            }
            result = (result && this.hasApplicationState() == other.hasApplicationState());
            if (this.hasApplicationState()) {
                result = (result && this.getApplicationState() == other.getApplicationState());
            }
            result = (result && this.hasDiagnostics() == other.hasDiagnostics());
            if (this.hasDiagnostics()) {
                result = (result && this.getDiagnostics().equals(other.getDiagnostics()));
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
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
            if (this.hasSubmitTime()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getSubmitTime());
            }
            if (this.hasApplicationSubmissionContext()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getApplicationSubmissionContext().hashCode();
            }
            if (this.hasUser()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getUser().hashCode();
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            if (this.hasApplicationState()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getApplicationState());
            }
            if (this.hasDiagnostics()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getDiagnostics().hashCode();
            }
            if (this.hasFinishTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationStateDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationStateDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationStateDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationStateDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationStateDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationStateDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationStateDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationStateDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationStateDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationStateDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationStateDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStateDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationStateDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationStateDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationStateDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStateDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationStateDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationStateDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationStateDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationStateDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationStateDataProto prototype) {
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
            ApplicationStateDataProto.PARSER = new AbstractParser<ApplicationStateDataProto>() {
                @Override
                public ApplicationStateDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationStateDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationStateDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationStateDataProtoOrBuilder
        {
            private int bitField0_;
            private long submitTime_;
            private YarnProtos.ApplicationSubmissionContextProto applicationSubmissionContext_;
            private SingleFieldBuilder<YarnProtos.ApplicationSubmissionContextProto, YarnProtos.ApplicationSubmissionContextProto.Builder, YarnProtos.ApplicationSubmissionContextProtoOrBuilder> applicationSubmissionContextBuilder_;
            private Object user_;
            private long startTime_;
            private RMAppStateProto applicationState_;
            private Object diagnostics_;
            private long finishTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationStateDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
                this.user_ = "";
                this.applicationState_ = RMAppStateProto.RMAPP_NEW;
                this.diagnostics_ = "N/A";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
                this.user_ = "";
                this.applicationState_ = RMAppStateProto.RMAPP_NEW;
                this.diagnostics_ = "N/A";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationStateDataProto.alwaysUseFieldBuilders) {
                    this.getApplicationSubmissionContextFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.submitTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.applicationSubmissionContextBuilder_ == null) {
                    this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
                }
                else {
                    this.applicationSubmissionContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                this.user_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFF7;
                this.applicationState_ = RMAppStateProto.RMAPP_NEW;
                this.bitField0_ &= 0xFFFFFFEF;
                this.diagnostics_ = "N/A";
                this.bitField0_ &= 0xFFFFFFDF;
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationStateDataProto_descriptor;
            }
            
            @Override
            public ApplicationStateDataProto getDefaultInstanceForType() {
                return ApplicationStateDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationStateDataProto build() {
                final ApplicationStateDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationStateDataProto buildPartial() {
                final ApplicationStateDataProto result = new ApplicationStateDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.submitTime_ = this.submitTime_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.applicationSubmissionContextBuilder_ == null) {
                    result.applicationSubmissionContext_ = this.applicationSubmissionContext_;
                }
                else {
                    result.applicationSubmissionContext_ = this.applicationSubmissionContextBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.user_ = this.user_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.startTime_ = this.startTime_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.applicationState_ = this.applicationState_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.diagnostics_ = this.diagnostics_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.finishTime_ = this.finishTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationStateDataProto) {
                    return this.mergeFrom((ApplicationStateDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationStateDataProto other) {
                if (other == ApplicationStateDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasSubmitTime()) {
                    this.setSubmitTime(other.getSubmitTime());
                }
                if (other.hasApplicationSubmissionContext()) {
                    this.mergeApplicationSubmissionContext(other.getApplicationSubmissionContext());
                }
                if (other.hasUser()) {
                    this.bitField0_ |= 0x4;
                    this.user_ = other.user_;
                    this.onChanged();
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
                }
                if (other.hasApplicationState()) {
                    this.setApplicationState(other.getApplicationState());
                }
                if (other.hasDiagnostics()) {
                    this.bitField0_ |= 0x20;
                    this.diagnostics_ = other.diagnostics_;
                    this.onChanged();
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
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
                ApplicationStateDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationStateDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationStateDataProto)e.getUnfinishedMessage();
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
            public boolean hasSubmitTime() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getSubmitTime() {
                return this.submitTime_;
            }
            
            public Builder setSubmitTime(final long value) {
                this.bitField0_ |= 0x1;
                this.submitTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSubmitTime() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.submitTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasApplicationSubmissionContext() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.ApplicationSubmissionContextProto getApplicationSubmissionContext() {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    return this.applicationSubmissionContext_;
                }
                return this.applicationSubmissionContextBuilder_.getMessage();
            }
            
            public Builder setApplicationSubmissionContext(final YarnProtos.ApplicationSubmissionContextProto value) {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.applicationSubmissionContext_ = value;
                    this.onChanged();
                }
                else {
                    this.applicationSubmissionContextBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setApplicationSubmissionContext(final YarnProtos.ApplicationSubmissionContextProto.Builder builderForValue) {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    this.applicationSubmissionContext_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.applicationSubmissionContextBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeApplicationSubmissionContext(final YarnProtos.ApplicationSubmissionContextProto value) {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.applicationSubmissionContext_ != YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance()) {
                        this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.newBuilder(this.applicationSubmissionContext_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.applicationSubmissionContext_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.applicationSubmissionContextBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearApplicationSubmissionContext() {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    this.applicationSubmissionContext_ = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.applicationSubmissionContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnProtos.ApplicationSubmissionContextProto.Builder getApplicationSubmissionContextBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getApplicationSubmissionContextFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationSubmissionContextProtoOrBuilder getApplicationSubmissionContextOrBuilder() {
                if (this.applicationSubmissionContextBuilder_ != null) {
                    return this.applicationSubmissionContextBuilder_.getMessageOrBuilder();
                }
                return this.applicationSubmissionContext_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationSubmissionContextProto, YarnProtos.ApplicationSubmissionContextProto.Builder, YarnProtos.ApplicationSubmissionContextProtoOrBuilder> getApplicationSubmissionContextFieldBuilder() {
                if (this.applicationSubmissionContextBuilder_ == null) {
                    this.applicationSubmissionContextBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationSubmissionContextProto, YarnProtos.ApplicationSubmissionContextProto.Builder, YarnProtos.ApplicationSubmissionContextProtoOrBuilder>(this.applicationSubmissionContext_, this.getParentForChildren(), this.isClean());
                    this.applicationSubmissionContext_ = null;
                }
                return this.applicationSubmissionContextBuilder_;
            }
            
            @Override
            public boolean hasUser() {
                return (this.bitField0_ & 0x4) == 0x4;
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
                this.bitField0_ |= 0x4;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUser() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.user_ = ApplicationStateDataProto.getDefaultInstance().getUser();
                this.onChanged();
                return this;
            }
            
            public Builder setUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasStartTime() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public long getStartTime() {
                return this.startTime_;
            }
            
            public Builder setStartTime(final long value) {
                this.bitField0_ |= 0x8;
                this.startTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStartTime() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.startTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasApplicationState() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public RMAppStateProto getApplicationState() {
                return this.applicationState_;
            }
            
            public Builder setApplicationState(final RMAppStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.applicationState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearApplicationState() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.applicationState_ = RMAppStateProto.RMAPP_NEW;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnostics() {
                return (this.bitField0_ & 0x20) == 0x20;
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
                this.bitField0_ |= 0x20;
                this.diagnostics_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnostics() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.diagnostics_ = ApplicationStateDataProto.getDefaultInstance().getDiagnostics();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.diagnostics_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x40;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ApplicationAttemptStateDataProto extends GeneratedMessage implements ApplicationAttemptStateDataProtoOrBuilder
    {
        private static final ApplicationAttemptStateDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ApplicationAttemptStateDataProto> PARSER;
        private int bitField0_;
        public static final int ATTEMPTID_FIELD_NUMBER = 1;
        private YarnProtos.ApplicationAttemptIdProto attemptId_;
        public static final int MASTER_CONTAINER_FIELD_NUMBER = 2;
        private YarnProtos.ContainerProto masterContainer_;
        public static final int APP_ATTEMPT_TOKENS_FIELD_NUMBER = 3;
        private ByteString appAttemptTokens_;
        public static final int APP_ATTEMPT_STATE_FIELD_NUMBER = 4;
        private RMAppAttemptStateProto appAttemptState_;
        public static final int FINAL_TRACKING_URL_FIELD_NUMBER = 5;
        private Object finalTrackingUrl_;
        public static final int DIAGNOSTICS_FIELD_NUMBER = 6;
        private Object diagnostics_;
        public static final int START_TIME_FIELD_NUMBER = 7;
        private long startTime_;
        public static final int FINAL_APPLICATION_STATUS_FIELD_NUMBER = 8;
        private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
        public static final int AM_CONTAINER_EXIT_STATUS_FIELD_NUMBER = 9;
        private int amContainerExitStatus_;
        public static final int MEMORY_SECONDS_FIELD_NUMBER = 10;
        private long memorySeconds_;
        public static final int VCORE_SECONDS_FIELD_NUMBER = 11;
        private long vcoreSeconds_;
        public static final int FINISH_TIME_FIELD_NUMBER = 12;
        private long finishTime_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ApplicationAttemptStateDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ApplicationAttemptStateDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ApplicationAttemptStateDataProto getDefaultInstance() {
            return ApplicationAttemptStateDataProto.defaultInstance;
        }
        
        @Override
        public ApplicationAttemptStateDataProto getDefaultInstanceForType() {
            return ApplicationAttemptStateDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ApplicationAttemptStateDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.attemptId_.toBuilder();
                            }
                            this.attemptId_ = input.readMessage(YarnProtos.ApplicationAttemptIdProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.attemptId_);
                                this.attemptId_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            YarnProtos.ContainerProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder2 = this.masterContainer_.toBuilder();
                            }
                            this.masterContainer_ = input.readMessage(YarnProtos.ContainerProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.masterContainer_);
                                this.masterContainer_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.appAttemptTokens_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            final int rawValue = input.readEnum();
                            final RMAppAttemptStateProto value = RMAppAttemptStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(4, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x8;
                            this.appAttemptState_ = value;
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.finalTrackingUrl_ = input.readBytes();
                            continue;
                        }
                        case 50: {
                            this.bitField0_ |= 0x20;
                            this.diagnostics_ = input.readBytes();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.startTime_ = input.readInt64();
                            continue;
                        }
                        case 64: {
                            final int rawValue = input.readEnum();
                            final YarnProtos.FinalApplicationStatusProto value2 = YarnProtos.FinalApplicationStatusProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(8, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x80;
                            this.finalApplicationStatus_ = value2;
                            continue;
                        }
                        case 72: {
                            this.bitField0_ |= 0x100;
                            this.amContainerExitStatus_ = input.readInt32();
                            continue;
                        }
                        case 80: {
                            this.bitField0_ |= 0x200;
                            this.memorySeconds_ = input.readInt64();
                            continue;
                        }
                        case 88: {
                            this.bitField0_ |= 0x400;
                            this.vcoreSeconds_ = input.readInt64();
                            continue;
                        }
                        case 96: {
                            this.bitField0_ |= 0x800;
                            this.finishTime_ = input.readInt64();
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
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptStateDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<ApplicationAttemptStateDataProto> getParserForType() {
            return ApplicationAttemptStateDataProto.PARSER;
        }
        
        @Override
        public boolean hasAttemptId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProto getAttemptId() {
            return this.attemptId_;
        }
        
        @Override
        public YarnProtos.ApplicationAttemptIdProtoOrBuilder getAttemptIdOrBuilder() {
            return this.attemptId_;
        }
        
        @Override
        public boolean hasMasterContainer() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnProtos.ContainerProto getMasterContainer() {
            return this.masterContainer_;
        }
        
        @Override
        public YarnProtos.ContainerProtoOrBuilder getMasterContainerOrBuilder() {
            return this.masterContainer_;
        }
        
        @Override
        public boolean hasAppAttemptTokens() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public ByteString getAppAttemptTokens() {
            return this.appAttemptTokens_;
        }
        
        @Override
        public boolean hasAppAttemptState() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public RMAppAttemptStateProto getAppAttemptState() {
            return this.appAttemptState_;
        }
        
        @Override
        public boolean hasFinalTrackingUrl() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getFinalTrackingUrl() {
            final Object ref = this.finalTrackingUrl_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.finalTrackingUrl_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getFinalTrackingUrlBytes() {
            final Object ref = this.finalTrackingUrl_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.finalTrackingUrl_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasDiagnostics() {
            return (this.bitField0_ & 0x20) == 0x20;
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
        public boolean hasStartTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getStartTime() {
            return this.startTime_;
        }
        
        @Override
        public boolean hasFinalApplicationStatus() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
            return this.finalApplicationStatus_;
        }
        
        @Override
        public boolean hasAmContainerExitStatus() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public int getAmContainerExitStatus() {
            return this.amContainerExitStatus_;
        }
        
        @Override
        public boolean hasMemorySeconds() {
            return (this.bitField0_ & 0x200) == 0x200;
        }
        
        @Override
        public long getMemorySeconds() {
            return this.memorySeconds_;
        }
        
        @Override
        public boolean hasVcoreSeconds() {
            return (this.bitField0_ & 0x400) == 0x400;
        }
        
        @Override
        public long getVcoreSeconds() {
            return this.vcoreSeconds_;
        }
        
        @Override
        public boolean hasFinishTime() {
            return (this.bitField0_ & 0x800) == 0x800;
        }
        
        @Override
        public long getFinishTime() {
            return this.finishTime_;
        }
        
        private void initFields() {
            this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
            this.masterContainer_ = YarnProtos.ContainerProto.getDefaultInstance();
            this.appAttemptTokens_ = ByteString.EMPTY;
            this.appAttemptState_ = RMAppAttemptStateProto.RMATTEMPT_NEW;
            this.finalTrackingUrl_ = "";
            this.diagnostics_ = "N/A";
            this.startTime_ = 0L;
            this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
            this.amContainerExitStatus_ = -1000;
            this.memorySeconds_ = 0L;
            this.vcoreSeconds_ = 0L;
            this.finishTime_ = 0L;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (this.hasMasterContainer() && !this.getMasterContainer().isInitialized()) {
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
                output.writeMessage(1, this.attemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.masterContainer_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.appAttemptTokens_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeEnum(4, this.appAttemptState_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getFinalTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(6, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeInt64(7, this.startTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeEnum(8, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeInt32(9, this.amContainerExitStatus_);
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                output.writeInt64(10, this.memorySeconds_);
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                output.writeInt64(11, this.vcoreSeconds_);
            }
            if ((this.bitField0_ & 0x800) == 0x800) {
                output.writeInt64(12, this.finishTime_);
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
                size += CodedOutputStream.computeMessageSize(1, this.attemptId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.masterContainer_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.appAttemptTokens_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeEnumSize(4, this.appAttemptState_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getFinalTrackingUrlBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(6, this.getDiagnosticsBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeInt64Size(7, this.startTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeEnumSize(8, this.finalApplicationStatus_.getNumber());
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeInt32Size(9, this.amContainerExitStatus_);
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                size += CodedOutputStream.computeInt64Size(10, this.memorySeconds_);
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                size += CodedOutputStream.computeInt64Size(11, this.vcoreSeconds_);
            }
            if ((this.bitField0_ & 0x800) == 0x800) {
                size += CodedOutputStream.computeInt64Size(12, this.finishTime_);
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
            if (!(obj instanceof ApplicationAttemptStateDataProto)) {
                return super.equals(obj);
            }
            final ApplicationAttemptStateDataProto other = (ApplicationAttemptStateDataProto)obj;
            boolean result = true;
            result = (result && this.hasAttemptId() == other.hasAttemptId());
            if (this.hasAttemptId()) {
                result = (result && this.getAttemptId().equals(other.getAttemptId()));
            }
            result = (result && this.hasMasterContainer() == other.hasMasterContainer());
            if (this.hasMasterContainer()) {
                result = (result && this.getMasterContainer().equals(other.getMasterContainer()));
            }
            result = (result && this.hasAppAttemptTokens() == other.hasAppAttemptTokens());
            if (this.hasAppAttemptTokens()) {
                result = (result && this.getAppAttemptTokens().equals(other.getAppAttemptTokens()));
            }
            result = (result && this.hasAppAttemptState() == other.hasAppAttemptState());
            if (this.hasAppAttemptState()) {
                result = (result && this.getAppAttemptState() == other.getAppAttemptState());
            }
            result = (result && this.hasFinalTrackingUrl() == other.hasFinalTrackingUrl());
            if (this.hasFinalTrackingUrl()) {
                result = (result && this.getFinalTrackingUrl().equals(other.getFinalTrackingUrl()));
            }
            result = (result && this.hasDiagnostics() == other.hasDiagnostics());
            if (this.hasDiagnostics()) {
                result = (result && this.getDiagnostics().equals(other.getDiagnostics()));
            }
            result = (result && this.hasStartTime() == other.hasStartTime());
            if (this.hasStartTime()) {
                result = (result && this.getStartTime() == other.getStartTime());
            }
            result = (result && this.hasFinalApplicationStatus() == other.hasFinalApplicationStatus());
            if (this.hasFinalApplicationStatus()) {
                result = (result && this.getFinalApplicationStatus() == other.getFinalApplicationStatus());
            }
            result = (result && this.hasAmContainerExitStatus() == other.hasAmContainerExitStatus());
            if (this.hasAmContainerExitStatus()) {
                result = (result && this.getAmContainerExitStatus() == other.getAmContainerExitStatus());
            }
            result = (result && this.hasMemorySeconds() == other.hasMemorySeconds());
            if (this.hasMemorySeconds()) {
                result = (result && this.getMemorySeconds() == other.getMemorySeconds());
            }
            result = (result && this.hasVcoreSeconds() == other.hasVcoreSeconds());
            if (this.hasVcoreSeconds()) {
                result = (result && this.getVcoreSeconds() == other.getVcoreSeconds());
            }
            result = (result && this.hasFinishTime() == other.hasFinishTime());
            if (this.hasFinishTime()) {
                result = (result && this.getFinishTime() == other.getFinishTime());
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
            if (this.hasAttemptId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getAttemptId().hashCode();
            }
            if (this.hasMasterContainer()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getMasterContainer().hashCode();
            }
            if (this.hasAppAttemptTokens()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getAppAttemptTokens().hashCode();
            }
            if (this.hasAppAttemptState()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getAppAttemptState());
            }
            if (this.hasFinalTrackingUrl()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getFinalTrackingUrl().hashCode();
            }
            if (this.hasDiagnostics()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getDiagnostics().hashCode();
            }
            if (this.hasStartTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getStartTime());
            }
            if (this.hasFinalApplicationStatus()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFinalApplicationStatus());
            }
            if (this.hasAmContainerExitStatus()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + this.getAmContainerExitStatus();
            }
            if (this.hasMemorySeconds()) {
                hash = 37 * hash + 10;
                hash = 53 * hash + AbstractMessage.hashLong(this.getMemorySeconds());
            }
            if (this.hasVcoreSeconds()) {
                hash = 37 * hash + 11;
                hash = 53 * hash + AbstractMessage.hashLong(this.getVcoreSeconds());
            }
            if (this.hasFinishTime()) {
                hash = 37 * hash + 12;
                hash = 53 * hash + AbstractMessage.hashLong(this.getFinishTime());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(data);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final InputStream input) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptStateDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ApplicationAttemptStateDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final CodedInputStream input) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(input);
        }
        
        public static ApplicationAttemptStateDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ApplicationAttemptStateDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ApplicationAttemptStateDataProto prototype) {
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
            ApplicationAttemptStateDataProto.PARSER = new AbstractParser<ApplicationAttemptStateDataProto>() {
                @Override
                public ApplicationAttemptStateDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ApplicationAttemptStateDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ApplicationAttemptStateDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ApplicationAttemptStateDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnProtos.ApplicationAttemptIdProto attemptId_;
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> attemptIdBuilder_;
            private YarnProtos.ContainerProto masterContainer_;
            private SingleFieldBuilder<YarnProtos.ContainerProto, YarnProtos.ContainerProto.Builder, YarnProtos.ContainerProtoOrBuilder> masterContainerBuilder_;
            private ByteString appAttemptTokens_;
            private RMAppAttemptStateProto appAttemptState_;
            private Object finalTrackingUrl_;
            private Object diagnostics_;
            private long startTime_;
            private YarnProtos.FinalApplicationStatusProto finalApplicationStatus_;
            private int amContainerExitStatus_;
            private long memorySeconds_;
            private long vcoreSeconds_;
            private long finishTime_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ApplicationAttemptStateDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.masterContainer_ = YarnProtos.ContainerProto.getDefaultInstance();
                this.appAttemptTokens_ = ByteString.EMPTY;
                this.appAttemptState_ = RMAppAttemptStateProto.RMATTEMPT_NEW;
                this.finalTrackingUrl_ = "";
                this.diagnostics_ = "N/A";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.amContainerExitStatus_ = -1000;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                this.masterContainer_ = YarnProtos.ContainerProto.getDefaultInstance();
                this.appAttemptTokens_ = ByteString.EMPTY;
                this.appAttemptState_ = RMAppAttemptStateProto.RMATTEMPT_NEW;
                this.finalTrackingUrl_ = "";
                this.diagnostics_ = "N/A";
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.amContainerExitStatus_ = -1000;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ApplicationAttemptStateDataProto.alwaysUseFieldBuilders) {
                    this.getAttemptIdFieldBuilder();
                    this.getMasterContainerFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.attemptIdBuilder_ == null) {
                    this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                }
                else {
                    this.attemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.masterContainerBuilder_ == null) {
                    this.masterContainer_ = YarnProtos.ContainerProto.getDefaultInstance();
                }
                else {
                    this.masterContainerBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                this.appAttemptTokens_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                this.appAttemptState_ = RMAppAttemptStateProto.RMATTEMPT_NEW;
                this.bitField0_ &= 0xFFFFFFF7;
                this.finalTrackingUrl_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.diagnostics_ = "N/A";
                this.bitField0_ &= 0xFFFFFFDF;
                this.startTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.bitField0_ &= 0xFFFFFF7F;
                this.amContainerExitStatus_ = -1000;
                this.bitField0_ &= 0xFFFFFEFF;
                this.memorySeconds_ = 0L;
                this.bitField0_ &= 0xFFFFFDFF;
                this.vcoreSeconds_ = 0L;
                this.bitField0_ &= 0xFFFFFBFF;
                this.finishTime_ = 0L;
                this.bitField0_ &= 0xFFFFF7FF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_ApplicationAttemptStateDataProto_descriptor;
            }
            
            @Override
            public ApplicationAttemptStateDataProto getDefaultInstanceForType() {
                return ApplicationAttemptStateDataProto.getDefaultInstance();
            }
            
            @Override
            public ApplicationAttemptStateDataProto build() {
                final ApplicationAttemptStateDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ApplicationAttemptStateDataProto buildPartial() {
                final ApplicationAttemptStateDataProto result = new ApplicationAttemptStateDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.attemptIdBuilder_ == null) {
                    result.attemptId_ = this.attemptId_;
                }
                else {
                    result.attemptId_ = this.attemptIdBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.masterContainerBuilder_ == null) {
                    result.masterContainer_ = this.masterContainer_;
                }
                else {
                    result.masterContainer_ = this.masterContainerBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.appAttemptTokens_ = this.appAttemptTokens_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.appAttemptState_ = this.appAttemptState_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.finalTrackingUrl_ = this.finalTrackingUrl_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.diagnostics_ = this.diagnostics_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.startTime_ = this.startTime_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.finalApplicationStatus_ = this.finalApplicationStatus_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.amContainerExitStatus_ = this.amContainerExitStatus_;
                if ((from_bitField0_ & 0x200) == 0x200) {
                    to_bitField0_ |= 0x200;
                }
                result.memorySeconds_ = this.memorySeconds_;
                if ((from_bitField0_ & 0x400) == 0x400) {
                    to_bitField0_ |= 0x400;
                }
                result.vcoreSeconds_ = this.vcoreSeconds_;
                if ((from_bitField0_ & 0x800) == 0x800) {
                    to_bitField0_ |= 0x800;
                }
                result.finishTime_ = this.finishTime_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ApplicationAttemptStateDataProto) {
                    return this.mergeFrom((ApplicationAttemptStateDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ApplicationAttemptStateDataProto other) {
                if (other == ApplicationAttemptStateDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAttemptId()) {
                    this.mergeAttemptId(other.getAttemptId());
                }
                if (other.hasMasterContainer()) {
                    this.mergeMasterContainer(other.getMasterContainer());
                }
                if (other.hasAppAttemptTokens()) {
                    this.setAppAttemptTokens(other.getAppAttemptTokens());
                }
                if (other.hasAppAttemptState()) {
                    this.setAppAttemptState(other.getAppAttemptState());
                }
                if (other.hasFinalTrackingUrl()) {
                    this.bitField0_ |= 0x10;
                    this.finalTrackingUrl_ = other.finalTrackingUrl_;
                    this.onChanged();
                }
                if (other.hasDiagnostics()) {
                    this.bitField0_ |= 0x20;
                    this.diagnostics_ = other.diagnostics_;
                    this.onChanged();
                }
                if (other.hasStartTime()) {
                    this.setStartTime(other.getStartTime());
                }
                if (other.hasFinalApplicationStatus()) {
                    this.setFinalApplicationStatus(other.getFinalApplicationStatus());
                }
                if (other.hasAmContainerExitStatus()) {
                    this.setAmContainerExitStatus(other.getAmContainerExitStatus());
                }
                if (other.hasMemorySeconds()) {
                    this.setMemorySeconds(other.getMemorySeconds());
                }
                if (other.hasVcoreSeconds()) {
                    this.setVcoreSeconds(other.getVcoreSeconds());
                }
                if (other.hasFinishTime()) {
                    this.setFinishTime(other.getFinishTime());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return !this.hasMasterContainer() || this.getMasterContainer().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ApplicationAttemptStateDataProto parsedMessage = null;
                try {
                    parsedMessage = ApplicationAttemptStateDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ApplicationAttemptStateDataProto)e.getUnfinishedMessage();
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
            public boolean hasAttemptId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProto getAttemptId() {
                if (this.attemptIdBuilder_ == null) {
                    return this.attemptId_;
                }
                return this.attemptIdBuilder_.getMessage();
            }
            
            public Builder setAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.attemptIdBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.attemptId_ = value;
                    this.onChanged();
                }
                else {
                    this.attemptIdBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setAttemptId(final YarnProtos.ApplicationAttemptIdProto.Builder builderForValue) {
                if (this.attemptIdBuilder_ == null) {
                    this.attemptId_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.attemptIdBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeAttemptId(final YarnProtos.ApplicationAttemptIdProto value) {
                if (this.attemptIdBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.attemptId_ != YarnProtos.ApplicationAttemptIdProto.getDefaultInstance()) {
                        this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.newBuilder(this.attemptId_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.attemptId_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.attemptIdBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearAttemptId() {
                if (this.attemptIdBuilder_ == null) {
                    this.attemptId_ = YarnProtos.ApplicationAttemptIdProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.attemptIdBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnProtos.ApplicationAttemptIdProto.Builder getAttemptIdBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getAttemptIdFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ApplicationAttemptIdProtoOrBuilder getAttemptIdOrBuilder() {
                if (this.attemptIdBuilder_ != null) {
                    return this.attemptIdBuilder_.getMessageOrBuilder();
                }
                return this.attemptId_;
            }
            
            private SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder> getAttemptIdFieldBuilder() {
                if (this.attemptIdBuilder_ == null) {
                    this.attemptIdBuilder_ = new SingleFieldBuilder<YarnProtos.ApplicationAttemptIdProto, YarnProtos.ApplicationAttemptIdProto.Builder, YarnProtos.ApplicationAttemptIdProtoOrBuilder>(this.attemptId_, this.getParentForChildren(), this.isClean());
                    this.attemptId_ = null;
                }
                return this.attemptIdBuilder_;
            }
            
            @Override
            public boolean hasMasterContainer() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnProtos.ContainerProto getMasterContainer() {
                if (this.masterContainerBuilder_ == null) {
                    return this.masterContainer_;
                }
                return this.masterContainerBuilder_.getMessage();
            }
            
            public Builder setMasterContainer(final YarnProtos.ContainerProto value) {
                if (this.masterContainerBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.masterContainer_ = value;
                    this.onChanged();
                }
                else {
                    this.masterContainerBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setMasterContainer(final YarnProtos.ContainerProto.Builder builderForValue) {
                if (this.masterContainerBuilder_ == null) {
                    this.masterContainer_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.masterContainerBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeMasterContainer(final YarnProtos.ContainerProto value) {
                if (this.masterContainerBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.masterContainer_ != YarnProtos.ContainerProto.getDefaultInstance()) {
                        this.masterContainer_ = YarnProtos.ContainerProto.newBuilder(this.masterContainer_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.masterContainer_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.masterContainerBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearMasterContainer() {
                if (this.masterContainerBuilder_ == null) {
                    this.masterContainer_ = YarnProtos.ContainerProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.masterContainerBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnProtos.ContainerProto.Builder getMasterContainerBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getMasterContainerFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnProtos.ContainerProtoOrBuilder getMasterContainerOrBuilder() {
                if (this.masterContainerBuilder_ != null) {
                    return this.masterContainerBuilder_.getMessageOrBuilder();
                }
                return this.masterContainer_;
            }
            
            private SingleFieldBuilder<YarnProtos.ContainerProto, YarnProtos.ContainerProto.Builder, YarnProtos.ContainerProtoOrBuilder> getMasterContainerFieldBuilder() {
                if (this.masterContainerBuilder_ == null) {
                    this.masterContainerBuilder_ = new SingleFieldBuilder<YarnProtos.ContainerProto, YarnProtos.ContainerProto.Builder, YarnProtos.ContainerProtoOrBuilder>(this.masterContainer_, this.getParentForChildren(), this.isClean());
                    this.masterContainer_ = null;
                }
                return this.masterContainerBuilder_;
            }
            
            @Override
            public boolean hasAppAttemptTokens() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public ByteString getAppAttemptTokens() {
                return this.appAttemptTokens_;
            }
            
            public Builder setAppAttemptTokens(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.appAttemptTokens_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAppAttemptTokens() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.appAttemptTokens_ = ApplicationAttemptStateDataProto.getDefaultInstance().getAppAttemptTokens();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasAppAttemptState() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public RMAppAttemptStateProto getAppAttemptState() {
                return this.appAttemptState_;
            }
            
            public Builder setAppAttemptState(final RMAppAttemptStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.appAttemptState_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAppAttemptState() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.appAttemptState_ = RMAppAttemptStateProto.RMATTEMPT_NEW;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinalTrackingUrl() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getFinalTrackingUrl() {
                final Object ref = this.finalTrackingUrl_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.finalTrackingUrl_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getFinalTrackingUrlBytes() {
                final Object ref = this.finalTrackingUrl_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.finalTrackingUrl_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setFinalTrackingUrl(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.finalTrackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalTrackingUrl() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.finalTrackingUrl_ = ApplicationAttemptStateDataProto.getDefaultInstance().getFinalTrackingUrl();
                this.onChanged();
                return this;
            }
            
            public Builder setFinalTrackingUrlBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.finalTrackingUrl_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasDiagnostics() {
                return (this.bitField0_ & 0x20) == 0x20;
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
                this.bitField0_ |= 0x20;
                this.diagnostics_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDiagnostics() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.diagnostics_ = ApplicationAttemptStateDataProto.getDefaultInstance().getDiagnostics();
                this.onChanged();
                return this;
            }
            
            public Builder setDiagnosticsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.diagnostics_ = value;
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
            public boolean hasFinalApplicationStatus() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus() {
                return this.finalApplicationStatus_;
            }
            
            public Builder setFinalApplicationStatus(final YarnProtos.FinalApplicationStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x80;
                this.finalApplicationStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinalApplicationStatus() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.finalApplicationStatus_ = YarnProtos.FinalApplicationStatusProto.APP_UNDEFINED;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasAmContainerExitStatus() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public int getAmContainerExitStatus() {
                return this.amContainerExitStatus_;
            }
            
            public Builder setAmContainerExitStatus(final int value) {
                this.bitField0_ |= 0x100;
                this.amContainerExitStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAmContainerExitStatus() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.amContainerExitStatus_ = -1000;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasMemorySeconds() {
                return (this.bitField0_ & 0x200) == 0x200;
            }
            
            @Override
            public long getMemorySeconds() {
                return this.memorySeconds_;
            }
            
            public Builder setMemorySeconds(final long value) {
                this.bitField0_ |= 0x200;
                this.memorySeconds_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMemorySeconds() {
                this.bitField0_ &= 0xFFFFFDFF;
                this.memorySeconds_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasVcoreSeconds() {
                return (this.bitField0_ & 0x400) == 0x400;
            }
            
            @Override
            public long getVcoreSeconds() {
                return this.vcoreSeconds_;
            }
            
            public Builder setVcoreSeconds(final long value) {
                this.bitField0_ |= 0x400;
                this.vcoreSeconds_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearVcoreSeconds() {
                this.bitField0_ &= 0xFFFFFBFF;
                this.vcoreSeconds_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFinishTime() {
                return (this.bitField0_ & 0x800) == 0x800;
            }
            
            @Override
            public long getFinishTime() {
                return this.finishTime_;
            }
            
            public Builder setFinishTime(final long value) {
                this.bitField0_ |= 0x800;
                this.finishTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFinishTime() {
                this.bitField0_ &= 0xFFFFF7FF;
                this.finishTime_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class EpochProto extends GeneratedMessage implements EpochProtoOrBuilder
    {
        private static final EpochProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<EpochProto> PARSER;
        private int bitField0_;
        public static final int EPOCH_FIELD_NUMBER = 1;
        private long epoch_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private EpochProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private EpochProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static EpochProto getDefaultInstance() {
            return EpochProto.defaultInstance;
        }
        
        @Override
        public EpochProto getDefaultInstanceForType() {
            return EpochProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private EpochProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.epoch_ = input.readInt64();
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
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EpochProto.class, Builder.class);
        }
        
        @Override
        public Parser<EpochProto> getParserForType() {
            return EpochProto.PARSER;
        }
        
        @Override
        public boolean hasEpoch() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getEpoch() {
            return this.epoch_;
        }
        
        private void initFields() {
            this.epoch_ = 0L;
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
                output.writeInt64(1, this.epoch_);
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
                size += CodedOutputStream.computeInt64Size(1, this.epoch_);
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
            if (!(obj instanceof EpochProto)) {
                return super.equals(obj);
            }
            final EpochProto other = (EpochProto)obj;
            boolean result = true;
            result = (result && this.hasEpoch() == other.hasEpoch());
            if (this.hasEpoch()) {
                result = (result && this.getEpoch() == other.getEpoch());
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
            if (this.hasEpoch()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getEpoch());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static EpochProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return EpochProto.PARSER.parseFrom(data);
        }
        
        public static EpochProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EpochProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EpochProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return EpochProto.PARSER.parseFrom(data);
        }
        
        public static EpochProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EpochProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EpochProto parseFrom(final InputStream input) throws IOException {
            return EpochProto.PARSER.parseFrom(input);
        }
        
        public static EpochProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EpochProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static EpochProto parseDelimitedFrom(final InputStream input) throws IOException {
            return EpochProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static EpochProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EpochProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static EpochProto parseFrom(final CodedInputStream input) throws IOException {
            return EpochProto.PARSER.parseFrom(input);
        }
        
        public static EpochProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EpochProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final EpochProto prototype) {
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
            EpochProto.PARSER = new AbstractParser<EpochProto>() {
                @Override
                public EpochProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new EpochProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new EpochProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements EpochProtoOrBuilder
        {
            private int bitField0_;
            private long epoch_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EpochProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (EpochProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.epoch_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_EpochProto_descriptor;
            }
            
            @Override
            public EpochProto getDefaultInstanceForType() {
                return EpochProto.getDefaultInstance();
            }
            
            @Override
            public EpochProto build() {
                final EpochProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public EpochProto buildPartial() {
                final EpochProto result = new EpochProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.epoch_ = this.epoch_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof EpochProto) {
                    return this.mergeFrom((EpochProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final EpochProto other) {
                if (other == EpochProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasEpoch()) {
                    this.setEpoch(other.getEpoch());
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
                EpochProto parsedMessage = null;
                try {
                    parsedMessage = EpochProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (EpochProto)e.getUnfinishedMessage();
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
            public boolean hasEpoch() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getEpoch() {
                return this.epoch_;
            }
            
            public Builder setEpoch(final long value) {
                this.bitField0_ |= 0x1;
                this.epoch_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearEpoch() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.epoch_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class AMRMTokenSecretManagerStateProto extends GeneratedMessage implements AMRMTokenSecretManagerStateProtoOrBuilder
    {
        private static final AMRMTokenSecretManagerStateProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AMRMTokenSecretManagerStateProto> PARSER;
        private int bitField0_;
        public static final int CURRENT_MASTER_KEY_FIELD_NUMBER = 1;
        private YarnServerCommonProtos.MasterKeyProto currentMasterKey_;
        public static final int NEXT_MASTER_KEY_FIELD_NUMBER = 2;
        private YarnServerCommonProtos.MasterKeyProto nextMasterKey_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AMRMTokenSecretManagerStateProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AMRMTokenSecretManagerStateProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AMRMTokenSecretManagerStateProto getDefaultInstance() {
            return AMRMTokenSecretManagerStateProto.defaultInstance;
        }
        
        @Override
        public AMRMTokenSecretManagerStateProto getDefaultInstanceForType() {
            return AMRMTokenSecretManagerStateProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AMRMTokenSecretManagerStateProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                subBuilder = this.currentMasterKey_.toBuilder();
                            }
                            this.currentMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.currentMasterKey_);
                                this.currentMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 18: {
                            YarnServerCommonProtos.MasterKeyProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.nextMasterKey_.toBuilder();
                            }
                            this.nextMasterKey_ = input.readMessage(YarnServerCommonProtos.MasterKeyProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.nextMasterKey_);
                                this.nextMasterKey_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
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
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AMRMTokenSecretManagerStateProto.class, Builder.class);
        }
        
        @Override
        public Parser<AMRMTokenSecretManagerStateProto> getParserForType() {
            return AMRMTokenSecretManagerStateProto.PARSER;
        }
        
        @Override
        public boolean hasCurrentMasterKey() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getCurrentMasterKey() {
            return this.currentMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getCurrentMasterKeyOrBuilder() {
            return this.currentMasterKey_;
        }
        
        @Override
        public boolean hasNextMasterKey() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProto getNextMasterKey() {
            return this.nextMasterKey_;
        }
        
        @Override
        public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNextMasterKeyOrBuilder() {
            return this.nextMasterKey_;
        }
        
        private void initFields() {
            this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
            this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
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
                output.writeMessage(1, this.currentMasterKey_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(2, this.nextMasterKey_);
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
                size += CodedOutputStream.computeMessageSize(1, this.currentMasterKey_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(2, this.nextMasterKey_);
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
            if (!(obj instanceof AMRMTokenSecretManagerStateProto)) {
                return super.equals(obj);
            }
            final AMRMTokenSecretManagerStateProto other = (AMRMTokenSecretManagerStateProto)obj;
            boolean result = true;
            result = (result && this.hasCurrentMasterKey() == other.hasCurrentMasterKey());
            if (this.hasCurrentMasterKey()) {
                result = (result && this.getCurrentMasterKey().equals(other.getCurrentMasterKey()));
            }
            result = (result && this.hasNextMasterKey() == other.hasNextMasterKey());
            if (this.hasNextMasterKey()) {
                result = (result && this.getNextMasterKey().equals(other.getNextMasterKey()));
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
            if (this.hasCurrentMasterKey()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getCurrentMasterKey().hashCode();
            }
            if (this.hasNextMasterKey()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getNextMasterKey().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(data);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(data);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final InputStream input) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(input);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AMRMTokenSecretManagerStateProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AMRMTokenSecretManagerStateProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final CodedInputStream input) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(input);
        }
        
        public static AMRMTokenSecretManagerStateProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AMRMTokenSecretManagerStateProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AMRMTokenSecretManagerStateProto prototype) {
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
            AMRMTokenSecretManagerStateProto.PARSER = new AbstractParser<AMRMTokenSecretManagerStateProto>() {
                @Override
                public AMRMTokenSecretManagerStateProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AMRMTokenSecretManagerStateProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AMRMTokenSecretManagerStateProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AMRMTokenSecretManagerStateProtoOrBuilder
        {
            private int bitField0_;
            private YarnServerCommonProtos.MasterKeyProto currentMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> currentMasterKeyBuilder_;
            private YarnServerCommonProtos.MasterKeyProto nextMasterKey_;
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> nextMasterKeyBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AMRMTokenSecretManagerStateProto.class, Builder.class);
            }
            
            private Builder() {
                this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AMRMTokenSecretManagerStateProto.alwaysUseFieldBuilders) {
                    this.getCurrentMasterKeyFieldBuilder();
                    this.getNextMasterKeyFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.currentMasterKeyBuilder_ == null) {
                    this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.currentMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.nextMasterKeyBuilder_ == null) {
                    this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                }
                else {
                    this.nextMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_AMRMTokenSecretManagerStateProto_descriptor;
            }
            
            @Override
            public AMRMTokenSecretManagerStateProto getDefaultInstanceForType() {
                return AMRMTokenSecretManagerStateProto.getDefaultInstance();
            }
            
            @Override
            public AMRMTokenSecretManagerStateProto build() {
                final AMRMTokenSecretManagerStateProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AMRMTokenSecretManagerStateProto buildPartial() {
                final AMRMTokenSecretManagerStateProto result = new AMRMTokenSecretManagerStateProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.currentMasterKeyBuilder_ == null) {
                    result.currentMasterKey_ = this.currentMasterKey_;
                }
                else {
                    result.currentMasterKey_ = this.currentMasterKeyBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                if (this.nextMasterKeyBuilder_ == null) {
                    result.nextMasterKey_ = this.nextMasterKey_;
                }
                else {
                    result.nextMasterKey_ = this.nextMasterKeyBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AMRMTokenSecretManagerStateProto) {
                    return this.mergeFrom((AMRMTokenSecretManagerStateProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AMRMTokenSecretManagerStateProto other) {
                if (other == AMRMTokenSecretManagerStateProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCurrentMasterKey()) {
                    this.mergeCurrentMasterKey(other.getCurrentMasterKey());
                }
                if (other.hasNextMasterKey()) {
                    this.mergeNextMasterKey(other.getNextMasterKey());
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
                AMRMTokenSecretManagerStateProto parsedMessage = null;
                try {
                    parsedMessage = AMRMTokenSecretManagerStateProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AMRMTokenSecretManagerStateProto)e.getUnfinishedMessage();
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
            public boolean hasCurrentMasterKey() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getCurrentMasterKey() {
                if (this.currentMasterKeyBuilder_ == null) {
                    return this.currentMasterKey_;
                }
                return this.currentMasterKeyBuilder_.getMessage();
            }
            
            public Builder setCurrentMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.currentMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.currentMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.currentMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setCurrentMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.currentMasterKeyBuilder_ == null) {
                    this.currentMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.currentMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeCurrentMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.currentMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.currentMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.currentMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.currentMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.currentMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearCurrentMasterKey() {
                if (this.currentMasterKeyBuilder_ == null) {
                    this.currentMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.currentMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getCurrentMasterKeyBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getCurrentMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getCurrentMasterKeyOrBuilder() {
                if (this.currentMasterKeyBuilder_ != null) {
                    return this.currentMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.currentMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getCurrentMasterKeyFieldBuilder() {
                if (this.currentMasterKeyBuilder_ == null) {
                    this.currentMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.currentMasterKey_, this.getParentForChildren(), this.isClean());
                    this.currentMasterKey_ = null;
                }
                return this.currentMasterKeyBuilder_;
            }
            
            @Override
            public boolean hasNextMasterKey() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProto getNextMasterKey() {
                if (this.nextMasterKeyBuilder_ == null) {
                    return this.nextMasterKey_;
                }
                return this.nextMasterKeyBuilder_.getMessage();
            }
            
            public Builder setNextMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nextMasterKeyBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.nextMasterKey_ = value;
                    this.onChanged();
                }
                else {
                    this.nextMasterKeyBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder setNextMasterKey(final YarnServerCommonProtos.MasterKeyProto.Builder builderForValue) {
                if (this.nextMasterKeyBuilder_ == null) {
                    this.nextMasterKey_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.nextMasterKeyBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder mergeNextMasterKey(final YarnServerCommonProtos.MasterKeyProto value) {
                if (this.nextMasterKeyBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2 && this.nextMasterKey_ != YarnServerCommonProtos.MasterKeyProto.getDefaultInstance()) {
                        this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.newBuilder(this.nextMasterKey_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.nextMasterKey_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.nextMasterKeyBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x2;
                return this;
            }
            
            public Builder clearNextMasterKey() {
                if (this.nextMasterKeyBuilder_ == null) {
                    this.nextMasterKey_ = YarnServerCommonProtos.MasterKeyProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.nextMasterKeyBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            public YarnServerCommonProtos.MasterKeyProto.Builder getNextMasterKeyBuilder() {
                this.bitField0_ |= 0x2;
                this.onChanged();
                return this.getNextMasterKeyFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnServerCommonProtos.MasterKeyProtoOrBuilder getNextMasterKeyOrBuilder() {
                if (this.nextMasterKeyBuilder_ != null) {
                    return this.nextMasterKeyBuilder_.getMessageOrBuilder();
                }
                return this.nextMasterKey_;
            }
            
            private SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder> getNextMasterKeyFieldBuilder() {
                if (this.nextMasterKeyBuilder_ == null) {
                    this.nextMasterKeyBuilder_ = new SingleFieldBuilder<YarnServerCommonProtos.MasterKeyProto, YarnServerCommonProtos.MasterKeyProto.Builder, YarnServerCommonProtos.MasterKeyProtoOrBuilder>(this.nextMasterKey_, this.getParentForChildren(), this.isClean());
                    this.nextMasterKey_ = null;
                }
                return this.nextMasterKeyBuilder_;
            }
        }
    }
    
    public static final class RMDelegationTokenIdentifierDataProto extends GeneratedMessage implements RMDelegationTokenIdentifierDataProtoOrBuilder
    {
        private static final RMDelegationTokenIdentifierDataProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RMDelegationTokenIdentifierDataProto> PARSER;
        private int bitField0_;
        public static final int TOKEN_IDENTIFIER_FIELD_NUMBER = 1;
        private YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto tokenIdentifier_;
        public static final int RENEWDATE_FIELD_NUMBER = 2;
        private long renewDate_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RMDelegationTokenIdentifierDataProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RMDelegationTokenIdentifierDataProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RMDelegationTokenIdentifierDataProto getDefaultInstance() {
            return RMDelegationTokenIdentifierDataProto.defaultInstance;
        }
        
        @Override
        public RMDelegationTokenIdentifierDataProto getDefaultInstanceForType() {
            return RMDelegationTokenIdentifierDataProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RMDelegationTokenIdentifierDataProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.tokenIdentifier_.toBuilder();
                            }
                            this.tokenIdentifier_ = input.readMessage(YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.tokenIdentifier_);
                                this.tokenIdentifier_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x1;
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.renewDate_ = input.readInt64();
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
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RMDelegationTokenIdentifierDataProto.class, Builder.class);
        }
        
        @Override
        public Parser<RMDelegationTokenIdentifierDataProto> getParserForType() {
            return RMDelegationTokenIdentifierDataProto.PARSER;
        }
        
        @Override
        public boolean hasTokenIdentifier() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto getTokenIdentifier() {
            return this.tokenIdentifier_;
        }
        
        @Override
        public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder getTokenIdentifierOrBuilder() {
            return this.tokenIdentifier_;
        }
        
        @Override
        public boolean hasRenewDate() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public long getRenewDate() {
            return this.renewDate_;
        }
        
        private void initFields() {
            this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance();
            this.renewDate_ = 0L;
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
                output.writeMessage(1, this.tokenIdentifier_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt64(2, this.renewDate_);
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
                size += CodedOutputStream.computeMessageSize(1, this.tokenIdentifier_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt64Size(2, this.renewDate_);
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
            if (!(obj instanceof RMDelegationTokenIdentifierDataProto)) {
                return super.equals(obj);
            }
            final RMDelegationTokenIdentifierDataProto other = (RMDelegationTokenIdentifierDataProto)obj;
            boolean result = true;
            result = (result && this.hasTokenIdentifier() == other.hasTokenIdentifier());
            if (this.hasTokenIdentifier()) {
                result = (result && this.getTokenIdentifier().equals(other.getTokenIdentifier()));
            }
            result = (result && this.hasRenewDate() == other.hasRenewDate());
            if (this.hasRenewDate()) {
                result = (result && this.getRenewDate() == other.getRenewDate());
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
            if (this.hasTokenIdentifier()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getTokenIdentifier().hashCode();
            }
            if (this.hasRenewDate()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashLong(this.getRenewDate());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(data);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(data);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final InputStream input) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(input);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final CodedInputStream input) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(input);
        }
        
        public static RMDelegationTokenIdentifierDataProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RMDelegationTokenIdentifierDataProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RMDelegationTokenIdentifierDataProto prototype) {
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
            RMDelegationTokenIdentifierDataProto.PARSER = new AbstractParser<RMDelegationTokenIdentifierDataProto>() {
                @Override
                public RMDelegationTokenIdentifierDataProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RMDelegationTokenIdentifierDataProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RMDelegationTokenIdentifierDataProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RMDelegationTokenIdentifierDataProtoOrBuilder
        {
            private int bitField0_;
            private YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto tokenIdentifier_;
            private SingleFieldBuilder<YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder> tokenIdentifierBuilder_;
            private long renewDate_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RMDelegationTokenIdentifierDataProto.class, Builder.class);
            }
            
            private Builder() {
                this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RMDelegationTokenIdentifierDataProto.alwaysUseFieldBuilders) {
                    this.getTokenIdentifierFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.tokenIdentifierBuilder_ == null) {
                    this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance();
                }
                else {
                    this.tokenIdentifierBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                this.renewDate_ = 0L;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerRecoveryProtos.internal_static_hadoop_yarn_RMDelegationTokenIdentifierDataProto_descriptor;
            }
            
            @Override
            public RMDelegationTokenIdentifierDataProto getDefaultInstanceForType() {
                return RMDelegationTokenIdentifierDataProto.getDefaultInstance();
            }
            
            @Override
            public RMDelegationTokenIdentifierDataProto build() {
                final RMDelegationTokenIdentifierDataProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RMDelegationTokenIdentifierDataProto buildPartial() {
                final RMDelegationTokenIdentifierDataProto result = new RMDelegationTokenIdentifierDataProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.tokenIdentifierBuilder_ == null) {
                    result.tokenIdentifier_ = this.tokenIdentifier_;
                }
                else {
                    result.tokenIdentifier_ = this.tokenIdentifierBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.renewDate_ = this.renewDate_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RMDelegationTokenIdentifierDataProto) {
                    return this.mergeFrom((RMDelegationTokenIdentifierDataProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RMDelegationTokenIdentifierDataProto other) {
                if (other == RMDelegationTokenIdentifierDataProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasTokenIdentifier()) {
                    this.mergeTokenIdentifier(other.getTokenIdentifier());
                }
                if (other.hasRenewDate()) {
                    this.setRenewDate(other.getRenewDate());
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
                RMDelegationTokenIdentifierDataProto parsedMessage = null;
                try {
                    parsedMessage = RMDelegationTokenIdentifierDataProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RMDelegationTokenIdentifierDataProto)e.getUnfinishedMessage();
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
            public boolean hasTokenIdentifier() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto getTokenIdentifier() {
                if (this.tokenIdentifierBuilder_ == null) {
                    return this.tokenIdentifier_;
                }
                return this.tokenIdentifierBuilder_.getMessage();
            }
            
            public Builder setTokenIdentifier(final YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto value) {
                if (this.tokenIdentifierBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.tokenIdentifier_ = value;
                    this.onChanged();
                }
                else {
                    this.tokenIdentifierBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setTokenIdentifier(final YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder builderForValue) {
                if (this.tokenIdentifierBuilder_ == null) {
                    this.tokenIdentifier_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.tokenIdentifierBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeTokenIdentifier(final YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto value) {
                if (this.tokenIdentifierBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.tokenIdentifier_ != YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance()) {
                        this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.newBuilder(this.tokenIdentifier_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.tokenIdentifier_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.tokenIdentifierBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearTokenIdentifier() {
                if (this.tokenIdentifierBuilder_ == null) {
                    this.tokenIdentifier_ = YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.tokenIdentifierBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder getTokenIdentifierBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getTokenIdentifierFieldBuilder().getBuilder();
            }
            
            @Override
            public YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder getTokenIdentifierOrBuilder() {
                if (this.tokenIdentifierBuilder_ != null) {
                    return this.tokenIdentifierBuilder_.getMessageOrBuilder();
                }
                return this.tokenIdentifier_;
            }
            
            private SingleFieldBuilder<YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder> getTokenIdentifierFieldBuilder() {
                if (this.tokenIdentifierBuilder_ == null) {
                    this.tokenIdentifierBuilder_ = new SingleFieldBuilder<YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto.Builder, YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder>(this.tokenIdentifier_, this.getParentForChildren(), this.isClean());
                    this.tokenIdentifier_ = null;
                }
                return this.tokenIdentifierBuilder_;
            }
            
            @Override
            public boolean hasRenewDate() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public long getRenewDate() {
                return this.renewDate_;
            }
            
            public Builder setRenewDate(final long value) {
                this.bitField0_ |= 0x2;
                this.renewDate_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRenewDate() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.renewDate_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface RMDelegationTokenIdentifierDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasTokenIdentifier();
        
        YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProto getTokenIdentifier();
        
        YarnSecurityTokenProtos.YARNDelegationTokenIdentifierProtoOrBuilder getTokenIdentifierOrBuilder();
        
        boolean hasRenewDate();
        
        long getRenewDate();
    }
    
    public interface AMRMTokenSecretManagerStateProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasCurrentMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getCurrentMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getCurrentMasterKeyOrBuilder();
        
        boolean hasNextMasterKey();
        
        YarnServerCommonProtos.MasterKeyProto getNextMasterKey();
        
        YarnServerCommonProtos.MasterKeyProtoOrBuilder getNextMasterKeyOrBuilder();
    }
    
    public interface EpochProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasEpoch();
        
        long getEpoch();
    }
    
    public interface ApplicationAttemptStateDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasAttemptId();
        
        YarnProtos.ApplicationAttemptIdProto getAttemptId();
        
        YarnProtos.ApplicationAttemptIdProtoOrBuilder getAttemptIdOrBuilder();
        
        boolean hasMasterContainer();
        
        YarnProtos.ContainerProto getMasterContainer();
        
        YarnProtos.ContainerProtoOrBuilder getMasterContainerOrBuilder();
        
        boolean hasAppAttemptTokens();
        
        ByteString getAppAttemptTokens();
        
        boolean hasAppAttemptState();
        
        RMAppAttemptStateProto getAppAttemptState();
        
        boolean hasFinalTrackingUrl();
        
        String getFinalTrackingUrl();
        
        ByteString getFinalTrackingUrlBytes();
        
        boolean hasDiagnostics();
        
        String getDiagnostics();
        
        ByteString getDiagnosticsBytes();
        
        boolean hasStartTime();
        
        long getStartTime();
        
        boolean hasFinalApplicationStatus();
        
        YarnProtos.FinalApplicationStatusProto getFinalApplicationStatus();
        
        boolean hasAmContainerExitStatus();
        
        int getAmContainerExitStatus();
        
        boolean hasMemorySeconds();
        
        long getMemorySeconds();
        
        boolean hasVcoreSeconds();
        
        long getVcoreSeconds();
        
        boolean hasFinishTime();
        
        long getFinishTime();
    }
    
    public interface ApplicationStateDataProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasSubmitTime();
        
        long getSubmitTime();
        
        boolean hasApplicationSubmissionContext();
        
        YarnProtos.ApplicationSubmissionContextProto getApplicationSubmissionContext();
        
        YarnProtos.ApplicationSubmissionContextProtoOrBuilder getApplicationSubmissionContextOrBuilder();
        
        boolean hasUser();
        
        String getUser();
        
        ByteString getUserBytes();
        
        boolean hasStartTime();
        
        long getStartTime();
        
        boolean hasApplicationState();
        
        RMAppStateProto getApplicationState();
        
        boolean hasDiagnostics();
        
        String getDiagnostics();
        
        ByteString getDiagnosticsBytes();
        
        boolean hasFinishTime();
        
        long getFinishTime();
    }
}
