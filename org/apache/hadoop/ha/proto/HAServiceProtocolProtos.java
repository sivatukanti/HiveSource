// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.ByteString;
import com.google.protobuf.AbstractMessage;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
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

public final class HAServiceProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_HAStateChangeRequestInfoProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_MonitorHealthRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_MonitorHealthRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_MonitorHealthResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_MonitorHealthResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_TransitionToActiveRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_TransitionToActiveResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_TransitionToStandbyRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_TransitionToStandbyResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetServiceStatusRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GetServiceStatusResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private HAServiceProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return HAServiceProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0017HAServiceProtocol.proto\u0012\rhadoop.common\"R\n\u001dHAStateChangeRequestInfoProto\u00121\n\treqSource\u0018\u0001 \u0002(\u000e2\u001e.hadoop.common.HARequestSource\"\u001b\n\u0019MonitorHealthRequestProto\"\u001c\n\u001aMonitorHealthResponseProto\"_\n\u001eTransitionToActiveRequestProto\u0012=\n\u0007reqInfo\u0018\u0001 \u0002(\u000b2,.hadoop.common.HAStateChangeRequestInfoProto\"!\n\u001fTransitionToActiveResponseProto\"`\n\u001fTransitionToStandbyRequestProto\u0012=\n\u0007reqInfo\u0018\u0001 \u0002(\u000b2,.hadoop.common.HAStateChangeReq", "uestInfoProto\"\"\n TransitionToStandbyResponseProto\"\u001e\n\u001cGetServiceStatusRequestProto\"\u0087\u0001\n\u001dGetServiceStatusResponseProto\u00121\n\u0005state\u0018\u0001 \u0002(\u000e2\".hadoop.common.HAServiceStateProto\u0012\u001b\n\u0013readyToBecomeActive\u0018\u0002 \u0001(\b\u0012\u0016\n\u000enotReadyReason\u0018\u0003 \u0001(\t*@\n\u0013HAServiceStateProto\u0012\u0010\n\fINITIALIZING\u0010\u0000\u0012\n\n\u0006ACTIVE\u0010\u0001\u0012\u000b\n\u0007STANDBY\u0010\u0002*W\n\u000fHARequestSource\u0012\u0013\n\u000fREQUEST_BY_USER\u0010\u0000\u0012\u001a\n\u0016REQUEST_BY_USER_FORCED\u0010\u0001\u0012\u0013\n\u000fREQUEST_BY_ZKFC\u0010\u00022\u00dc\u0003\n\u0018HAServiceProtocolServ", "ice\u0012d\n\rmonitorHealth\u0012(.hadoop.common.MonitorHealthRequestProto\u001a).hadoop.common.MonitorHealthResponseProto\u0012s\n\u0012transitionToActive\u0012-.hadoop.common.TransitionToActiveRequestProto\u001a..hadoop.common.TransitionToActiveResponseProto\u0012v\n\u0013transitionToStandby\u0012..hadoop.common.TransitionToStandbyRequestProto\u001a/.hadoop.common.TransitionToStandbyResponseProto\u0012m\n\u0010getServiceStatus\u0012+.hadoop.common.GetServiceStatusReque", "stProto\u001a,.hadoop.common.GetServiceStatusResponseProtoB;\n\u001aorg.apache.hadoop.ha.protoB\u0017HAServiceProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                HAServiceProtocolProtos.descriptor = root;
                HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(0);
                HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor, new String[] { "ReqSource" });
                HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(1);
                HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_descriptor, new String[0]);
                HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(2);
                HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_descriptor, new String[0]);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(3);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor, new String[] { "ReqInfo" });
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(4);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor, new String[0]);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(5);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor, new String[] { "ReqInfo" });
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(6);
                HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor, new String[0]);
                HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(7);
                HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor, new String[0]);
                HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor = HAServiceProtocolProtos.getDescriptor().getMessageTypes().get(8);
                HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor, new String[] { "State", "ReadyToBecomeActive", "NotReadyReason" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public enum HAServiceStateProto implements ProtocolMessageEnum
    {
        INITIALIZING(0, 0), 
        ACTIVE(1, 1), 
        STANDBY(2, 2);
        
        public static final int INITIALIZING_VALUE = 0;
        public static final int ACTIVE_VALUE = 1;
        public static final int STANDBY_VALUE = 2;
        private static Internal.EnumLiteMap<HAServiceStateProto> internalValueMap;
        private static final HAServiceStateProto[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static HAServiceStateProto valueOf(final int value) {
            switch (value) {
                case 0: {
                    return HAServiceStateProto.INITIALIZING;
                }
                case 1: {
                    return HAServiceStateProto.ACTIVE;
                }
                case 2: {
                    return HAServiceStateProto.STANDBY;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<HAServiceStateProto> internalGetValueMap() {
            return HAServiceStateProto.internalValueMap;
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
            return HAServiceProtocolProtos.getDescriptor().getEnumTypes().get(0);
        }
        
        public static HAServiceStateProto valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return HAServiceStateProto.VALUES[desc.getIndex()];
        }
        
        private HAServiceStateProto(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            HAServiceStateProto.internalValueMap = new Internal.EnumLiteMap<HAServiceStateProto>() {
                @Override
                public HAServiceStateProto findValueByNumber(final int number) {
                    return HAServiceStateProto.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public enum HARequestSource implements ProtocolMessageEnum
    {
        REQUEST_BY_USER(0, 0), 
        REQUEST_BY_USER_FORCED(1, 1), 
        REQUEST_BY_ZKFC(2, 2);
        
        public static final int REQUEST_BY_USER_VALUE = 0;
        public static final int REQUEST_BY_USER_FORCED_VALUE = 1;
        public static final int REQUEST_BY_ZKFC_VALUE = 2;
        private static Internal.EnumLiteMap<HARequestSource> internalValueMap;
        private static final HARequestSource[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static HARequestSource valueOf(final int value) {
            switch (value) {
                case 0: {
                    return HARequestSource.REQUEST_BY_USER;
                }
                case 1: {
                    return HARequestSource.REQUEST_BY_USER_FORCED;
                }
                case 2: {
                    return HARequestSource.REQUEST_BY_ZKFC;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<HARequestSource> internalGetValueMap() {
            return HARequestSource.internalValueMap;
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
            return HAServiceProtocolProtos.getDescriptor().getEnumTypes().get(1);
        }
        
        public static HARequestSource valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return HARequestSource.VALUES[desc.getIndex()];
        }
        
        private HARequestSource(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            HARequestSource.internalValueMap = new Internal.EnumLiteMap<HARequestSource>() {
                @Override
                public HARequestSource findValueByNumber(final int number) {
                    return HARequestSource.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public static final class HAStateChangeRequestInfoProto extends GeneratedMessage implements HAStateChangeRequestInfoProtoOrBuilder
    {
        private static final HAStateChangeRequestInfoProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<HAStateChangeRequestInfoProto> PARSER;
        private int bitField0_;
        public static final int REQSOURCE_FIELD_NUMBER = 1;
        private HARequestSource reqSource_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private HAStateChangeRequestInfoProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private HAStateChangeRequestInfoProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static HAStateChangeRequestInfoProto getDefaultInstance() {
            return HAStateChangeRequestInfoProto.defaultInstance;
        }
        
        @Override
        public HAStateChangeRequestInfoProto getDefaultInstanceForType() {
            return HAStateChangeRequestInfoProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private HAStateChangeRequestInfoProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            final int rawValue = input.readEnum();
                            final HARequestSource value = HARequestSource.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x1;
                            this.reqSource_ = value;
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(HAStateChangeRequestInfoProto.class, Builder.class);
        }
        
        @Override
        public Parser<HAStateChangeRequestInfoProto> getParserForType() {
            return HAStateChangeRequestInfoProto.PARSER;
        }
        
        @Override
        public boolean hasReqSource() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public HARequestSource getReqSource() {
            return this.reqSource_;
        }
        
        private void initFields() {
            this.reqSource_ = HARequestSource.REQUEST_BY_USER;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasReqSource()) {
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
                output.writeEnum(1, this.reqSource_.getNumber());
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
                size += CodedOutputStream.computeEnumSize(1, this.reqSource_.getNumber());
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
            if (!(obj instanceof HAStateChangeRequestInfoProto)) {
                return super.equals(obj);
            }
            final HAStateChangeRequestInfoProto other = (HAStateChangeRequestInfoProto)obj;
            boolean result = true;
            result = (result && this.hasReqSource() == other.hasReqSource());
            if (this.hasReqSource()) {
                result = (result && this.getReqSource() == other.getReqSource());
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
            if (this.hasReqSource()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getReqSource());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(data);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(data);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final InputStream input) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(input);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static HAStateChangeRequestInfoProto parseDelimitedFrom(final InputStream input) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static HAStateChangeRequestInfoProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final CodedInputStream input) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(input);
        }
        
        public static HAStateChangeRequestInfoProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return HAStateChangeRequestInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final HAStateChangeRequestInfoProto prototype) {
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
            HAStateChangeRequestInfoProto.PARSER = new AbstractParser<HAStateChangeRequestInfoProto>() {
                @Override
                public HAStateChangeRequestInfoProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new HAStateChangeRequestInfoProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new HAStateChangeRequestInfoProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements HAStateChangeRequestInfoProtoOrBuilder
        {
            private int bitField0_;
            private HARequestSource reqSource_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(HAStateChangeRequestInfoProto.class, Builder.class);
            }
            
            private Builder() {
                this.reqSource_ = HARequestSource.REQUEST_BY_USER;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.reqSource_ = HARequestSource.REQUEST_BY_USER;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (HAStateChangeRequestInfoProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.reqSource_ = HARequestSource.REQUEST_BY_USER;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_HAStateChangeRequestInfoProto_descriptor;
            }
            
            @Override
            public HAStateChangeRequestInfoProto getDefaultInstanceForType() {
                return HAStateChangeRequestInfoProto.getDefaultInstance();
            }
            
            @Override
            public HAStateChangeRequestInfoProto build() {
                final HAStateChangeRequestInfoProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public HAStateChangeRequestInfoProto buildPartial() {
                final HAStateChangeRequestInfoProto result = new HAStateChangeRequestInfoProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.reqSource_ = this.reqSource_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof HAStateChangeRequestInfoProto) {
                    return this.mergeFrom((HAStateChangeRequestInfoProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final HAStateChangeRequestInfoProto other) {
                if (other == HAStateChangeRequestInfoProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasReqSource()) {
                    this.setReqSource(other.getReqSource());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasReqSource();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                HAStateChangeRequestInfoProto parsedMessage = null;
                try {
                    parsedMessage = HAStateChangeRequestInfoProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (HAStateChangeRequestInfoProto)e.getUnfinishedMessage();
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
            public boolean hasReqSource() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public HARequestSource getReqSource() {
                return this.reqSource_;
            }
            
            public Builder setReqSource(final HARequestSource value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.reqSource_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearReqSource() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.reqSource_ = HARequestSource.REQUEST_BY_USER;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class MonitorHealthRequestProto extends GeneratedMessage implements MonitorHealthRequestProtoOrBuilder
    {
        private static final MonitorHealthRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MonitorHealthRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private MonitorHealthRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MonitorHealthRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MonitorHealthRequestProto getDefaultInstance() {
            return MonitorHealthRequestProto.defaultInstance;
        }
        
        @Override
        public MonitorHealthRequestProto getDefaultInstanceForType() {
            return MonitorHealthRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MonitorHealthRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MonitorHealthRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<MonitorHealthRequestProto> getParserForType() {
            return MonitorHealthRequestProto.PARSER;
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
            if (!(obj instanceof MonitorHealthRequestProto)) {
                return super.equals(obj);
            }
            final MonitorHealthRequestProto other = (MonitorHealthRequestProto)obj;
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
        
        public static MonitorHealthRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MonitorHealthRequestProto.PARSER.parseFrom(data);
        }
        
        public static MonitorHealthRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MonitorHealthRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MonitorHealthRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MonitorHealthRequestProto.PARSER.parseFrom(data);
        }
        
        public static MonitorHealthRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MonitorHealthRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MonitorHealthRequestProto parseFrom(final InputStream input) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseFrom(input);
        }
        
        public static MonitorHealthRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MonitorHealthRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static MonitorHealthRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MonitorHealthRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseFrom(input);
        }
        
        public static MonitorHealthRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MonitorHealthRequestProto prototype) {
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
            MonitorHealthRequestProto.PARSER = new AbstractParser<MonitorHealthRequestProto>() {
                @Override
                public MonitorHealthRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MonitorHealthRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new MonitorHealthRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements MonitorHealthRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MonitorHealthRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (MonitorHealthRequestProto.alwaysUseFieldBuilders) {}
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthRequestProto_descriptor;
            }
            
            @Override
            public MonitorHealthRequestProto getDefaultInstanceForType() {
                return MonitorHealthRequestProto.getDefaultInstance();
            }
            
            @Override
            public MonitorHealthRequestProto build() {
                final MonitorHealthRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public MonitorHealthRequestProto buildPartial() {
                final MonitorHealthRequestProto result = new MonitorHealthRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MonitorHealthRequestProto) {
                    return this.mergeFrom((MonitorHealthRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MonitorHealthRequestProto other) {
                if (other == MonitorHealthRequestProto.getDefaultInstance()) {
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
                MonitorHealthRequestProto parsedMessage = null;
                try {
                    parsedMessage = MonitorHealthRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MonitorHealthRequestProto)e.getUnfinishedMessage();
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
    
    public static final class MonitorHealthResponseProto extends GeneratedMessage implements MonitorHealthResponseProtoOrBuilder
    {
        private static final MonitorHealthResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MonitorHealthResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private MonitorHealthResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MonitorHealthResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MonitorHealthResponseProto getDefaultInstance() {
            return MonitorHealthResponseProto.defaultInstance;
        }
        
        @Override
        public MonitorHealthResponseProto getDefaultInstanceForType() {
            return MonitorHealthResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MonitorHealthResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MonitorHealthResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<MonitorHealthResponseProto> getParserForType() {
            return MonitorHealthResponseProto.PARSER;
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
            if (!(obj instanceof MonitorHealthResponseProto)) {
                return super.equals(obj);
            }
            final MonitorHealthResponseProto other = (MonitorHealthResponseProto)obj;
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
        
        public static MonitorHealthResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MonitorHealthResponseProto.PARSER.parseFrom(data);
        }
        
        public static MonitorHealthResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MonitorHealthResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MonitorHealthResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MonitorHealthResponseProto.PARSER.parseFrom(data);
        }
        
        public static MonitorHealthResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MonitorHealthResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MonitorHealthResponseProto parseFrom(final InputStream input) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseFrom(input);
        }
        
        public static MonitorHealthResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MonitorHealthResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static MonitorHealthResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MonitorHealthResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseFrom(input);
        }
        
        public static MonitorHealthResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MonitorHealthResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MonitorHealthResponseProto prototype) {
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
            MonitorHealthResponseProto.PARSER = new AbstractParser<MonitorHealthResponseProto>() {
                @Override
                public MonitorHealthResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MonitorHealthResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new MonitorHealthResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements MonitorHealthResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MonitorHealthResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (MonitorHealthResponseProto.alwaysUseFieldBuilders) {}
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_MonitorHealthResponseProto_descriptor;
            }
            
            @Override
            public MonitorHealthResponseProto getDefaultInstanceForType() {
                return MonitorHealthResponseProto.getDefaultInstance();
            }
            
            @Override
            public MonitorHealthResponseProto build() {
                final MonitorHealthResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public MonitorHealthResponseProto buildPartial() {
                final MonitorHealthResponseProto result = new MonitorHealthResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MonitorHealthResponseProto) {
                    return this.mergeFrom((MonitorHealthResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MonitorHealthResponseProto other) {
                if (other == MonitorHealthResponseProto.getDefaultInstance()) {
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
                MonitorHealthResponseProto parsedMessage = null;
                try {
                    parsedMessage = MonitorHealthResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MonitorHealthResponseProto)e.getUnfinishedMessage();
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
    
    public static final class TransitionToActiveRequestProto extends GeneratedMessage implements TransitionToActiveRequestProtoOrBuilder
    {
        private static final TransitionToActiveRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<TransitionToActiveRequestProto> PARSER;
        private int bitField0_;
        public static final int REQINFO_FIELD_NUMBER = 1;
        private HAStateChangeRequestInfoProto reqInfo_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private TransitionToActiveRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private TransitionToActiveRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static TransitionToActiveRequestProto getDefaultInstance() {
            return TransitionToActiveRequestProto.defaultInstance;
        }
        
        @Override
        public TransitionToActiveRequestProto getDefaultInstanceForType() {
            return TransitionToActiveRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private TransitionToActiveRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            HAStateChangeRequestInfoProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.reqInfo_.toBuilder();
                            }
                            this.reqInfo_ = input.readMessage(HAStateChangeRequestInfoProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.reqInfo_);
                                this.reqInfo_ = subBuilder.buildPartial();
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToActiveRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<TransitionToActiveRequestProto> getParserForType() {
            return TransitionToActiveRequestProto.PARSER;
        }
        
        @Override
        public boolean hasReqInfo() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public HAStateChangeRequestInfoProto getReqInfo() {
            return this.reqInfo_;
        }
        
        @Override
        public HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder() {
            return this.reqInfo_;
        }
        
        private void initFields() {
            this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasReqInfo()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.getReqInfo().isInitialized()) {
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
                output.writeMessage(1, this.reqInfo_);
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
                size += CodedOutputStream.computeMessageSize(1, this.reqInfo_);
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
            if (!(obj instanceof TransitionToActiveRequestProto)) {
                return super.equals(obj);
            }
            final TransitionToActiveRequestProto other = (TransitionToActiveRequestProto)obj;
            boolean result = true;
            result = (result && this.hasReqInfo() == other.hasReqInfo());
            if (this.hasReqInfo()) {
                result = (result && this.getReqInfo().equals(other.getReqInfo()));
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
            if (this.hasReqInfo()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getReqInfo().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static TransitionToActiveRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final InputStream input) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static TransitionToActiveRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static TransitionToActiveRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToActiveRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final TransitionToActiveRequestProto prototype) {
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
            TransitionToActiveRequestProto.PARSER = new AbstractParser<TransitionToActiveRequestProto>() {
                @Override
                public TransitionToActiveRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new TransitionToActiveRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new TransitionToActiveRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements TransitionToActiveRequestProtoOrBuilder
        {
            private int bitField0_;
            private HAStateChangeRequestInfoProto reqInfo_;
            private SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder> reqInfoBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToActiveRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (TransitionToActiveRequestProto.alwaysUseFieldBuilders) {
                    this.getReqInfoFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                }
                else {
                    this.reqInfoBuilder_.clear();
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveRequestProto_descriptor;
            }
            
            @Override
            public TransitionToActiveRequestProto getDefaultInstanceForType() {
                return TransitionToActiveRequestProto.getDefaultInstance();
            }
            
            @Override
            public TransitionToActiveRequestProto build() {
                final TransitionToActiveRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public TransitionToActiveRequestProto buildPartial() {
                final TransitionToActiveRequestProto result = new TransitionToActiveRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.reqInfoBuilder_ == null) {
                    result.reqInfo_ = this.reqInfo_;
                }
                else {
                    result.reqInfo_ = this.reqInfoBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof TransitionToActiveRequestProto) {
                    return this.mergeFrom((TransitionToActiveRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final TransitionToActiveRequestProto other) {
                if (other == TransitionToActiveRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasReqInfo()) {
                    this.mergeReqInfo(other.getReqInfo());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasReqInfo() && this.getReqInfo().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                TransitionToActiveRequestProto parsedMessage = null;
                try {
                    parsedMessage = TransitionToActiveRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (TransitionToActiveRequestProto)e.getUnfinishedMessage();
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
            public boolean hasReqInfo() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public HAStateChangeRequestInfoProto getReqInfo() {
                if (this.reqInfoBuilder_ == null) {
                    return this.reqInfo_;
                }
                return this.reqInfoBuilder_.getMessage();
            }
            
            public Builder setReqInfo(final HAStateChangeRequestInfoProto value) {
                if (this.reqInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.reqInfo_ = value;
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setReqInfo(final HAStateChangeRequestInfoProto.Builder builderForValue) {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeReqInfo(final HAStateChangeRequestInfoProto value) {
                if (this.reqInfoBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.reqInfo_ != HAStateChangeRequestInfoProto.getDefaultInstance()) {
                        this.reqInfo_ = HAStateChangeRequestInfoProto.newBuilder(this.reqInfo_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.reqInfo_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearReqInfo() {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public HAStateChangeRequestInfoProto.Builder getReqInfoBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getReqInfoFieldBuilder().getBuilder();
            }
            
            @Override
            public HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder() {
                if (this.reqInfoBuilder_ != null) {
                    return this.reqInfoBuilder_.getMessageOrBuilder();
                }
                return this.reqInfo_;
            }
            
            private SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder> getReqInfoFieldBuilder() {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfoBuilder_ = new SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder>(this.reqInfo_, this.getParentForChildren(), this.isClean());
                    this.reqInfo_ = null;
                }
                return this.reqInfoBuilder_;
            }
        }
    }
    
    public static final class TransitionToActiveResponseProto extends GeneratedMessage implements TransitionToActiveResponseProtoOrBuilder
    {
        private static final TransitionToActiveResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<TransitionToActiveResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private TransitionToActiveResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private TransitionToActiveResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static TransitionToActiveResponseProto getDefaultInstance() {
            return TransitionToActiveResponseProto.defaultInstance;
        }
        
        @Override
        public TransitionToActiveResponseProto getDefaultInstanceForType() {
            return TransitionToActiveResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private TransitionToActiveResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToActiveResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<TransitionToActiveResponseProto> getParserForType() {
            return TransitionToActiveResponseProto.PARSER;
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
            if (!(obj instanceof TransitionToActiveResponseProto)) {
                return super.equals(obj);
            }
            final TransitionToActiveResponseProto other = (TransitionToActiveResponseProto)obj;
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
        
        public static TransitionToActiveResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final InputStream input) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static TransitionToActiveResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static TransitionToActiveResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToActiveResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToActiveResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final TransitionToActiveResponseProto prototype) {
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
            TransitionToActiveResponseProto.PARSER = new AbstractParser<TransitionToActiveResponseProto>() {
                @Override
                public TransitionToActiveResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new TransitionToActiveResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new TransitionToActiveResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements TransitionToActiveResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToActiveResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (TransitionToActiveResponseProto.alwaysUseFieldBuilders) {}
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToActiveResponseProto_descriptor;
            }
            
            @Override
            public TransitionToActiveResponseProto getDefaultInstanceForType() {
                return TransitionToActiveResponseProto.getDefaultInstance();
            }
            
            @Override
            public TransitionToActiveResponseProto build() {
                final TransitionToActiveResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public TransitionToActiveResponseProto buildPartial() {
                final TransitionToActiveResponseProto result = new TransitionToActiveResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof TransitionToActiveResponseProto) {
                    return this.mergeFrom((TransitionToActiveResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final TransitionToActiveResponseProto other) {
                if (other == TransitionToActiveResponseProto.getDefaultInstance()) {
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
                TransitionToActiveResponseProto parsedMessage = null;
                try {
                    parsedMessage = TransitionToActiveResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (TransitionToActiveResponseProto)e.getUnfinishedMessage();
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
    
    public static final class TransitionToStandbyRequestProto extends GeneratedMessage implements TransitionToStandbyRequestProtoOrBuilder
    {
        private static final TransitionToStandbyRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<TransitionToStandbyRequestProto> PARSER;
        private int bitField0_;
        public static final int REQINFO_FIELD_NUMBER = 1;
        private HAStateChangeRequestInfoProto reqInfo_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private TransitionToStandbyRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private TransitionToStandbyRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static TransitionToStandbyRequestProto getDefaultInstance() {
            return TransitionToStandbyRequestProto.defaultInstance;
        }
        
        @Override
        public TransitionToStandbyRequestProto getDefaultInstanceForType() {
            return TransitionToStandbyRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private TransitionToStandbyRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            HAStateChangeRequestInfoProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x1) == 0x1) {
                                subBuilder = this.reqInfo_.toBuilder();
                            }
                            this.reqInfo_ = input.readMessage(HAStateChangeRequestInfoProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.reqInfo_);
                                this.reqInfo_ = subBuilder.buildPartial();
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToStandbyRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<TransitionToStandbyRequestProto> getParserForType() {
            return TransitionToStandbyRequestProto.PARSER;
        }
        
        @Override
        public boolean hasReqInfo() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public HAStateChangeRequestInfoProto getReqInfo() {
            return this.reqInfo_;
        }
        
        @Override
        public HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder() {
            return this.reqInfo_;
        }
        
        private void initFields() {
            this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasReqInfo()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.getReqInfo().isInitialized()) {
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
                output.writeMessage(1, this.reqInfo_);
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
                size += CodedOutputStream.computeMessageSize(1, this.reqInfo_);
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
            if (!(obj instanceof TransitionToStandbyRequestProto)) {
                return super.equals(obj);
            }
            final TransitionToStandbyRequestProto other = (TransitionToStandbyRequestProto)obj;
            boolean result = true;
            result = (result && this.hasReqInfo() == other.hasReqInfo());
            if (this.hasReqInfo()) {
                result = (result && this.getReqInfo().equals(other.getReqInfo()));
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
            if (this.hasReqInfo()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getReqInfo().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final InputStream input) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static TransitionToStandbyRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static TransitionToStandbyRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToStandbyRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final TransitionToStandbyRequestProto prototype) {
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
            TransitionToStandbyRequestProto.PARSER = new AbstractParser<TransitionToStandbyRequestProto>() {
                @Override
                public TransitionToStandbyRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new TransitionToStandbyRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new TransitionToStandbyRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements TransitionToStandbyRequestProtoOrBuilder
        {
            private int bitField0_;
            private HAStateChangeRequestInfoProto reqInfo_;
            private SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder> reqInfoBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToStandbyRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (TransitionToStandbyRequestProto.alwaysUseFieldBuilders) {
                    this.getReqInfoFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                }
                else {
                    this.reqInfoBuilder_.clear();
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyRequestProto_descriptor;
            }
            
            @Override
            public TransitionToStandbyRequestProto getDefaultInstanceForType() {
                return TransitionToStandbyRequestProto.getDefaultInstance();
            }
            
            @Override
            public TransitionToStandbyRequestProto build() {
                final TransitionToStandbyRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public TransitionToStandbyRequestProto buildPartial() {
                final TransitionToStandbyRequestProto result = new TransitionToStandbyRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                if (this.reqInfoBuilder_ == null) {
                    result.reqInfo_ = this.reqInfo_;
                }
                else {
                    result.reqInfo_ = this.reqInfoBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof TransitionToStandbyRequestProto) {
                    return this.mergeFrom((TransitionToStandbyRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final TransitionToStandbyRequestProto other) {
                if (other == TransitionToStandbyRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasReqInfo()) {
                    this.mergeReqInfo(other.getReqInfo());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasReqInfo() && this.getReqInfo().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                TransitionToStandbyRequestProto parsedMessage = null;
                try {
                    parsedMessage = TransitionToStandbyRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (TransitionToStandbyRequestProto)e.getUnfinishedMessage();
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
            public boolean hasReqInfo() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public HAStateChangeRequestInfoProto getReqInfo() {
                if (this.reqInfoBuilder_ == null) {
                    return this.reqInfo_;
                }
                return this.reqInfoBuilder_.getMessage();
            }
            
            public Builder setReqInfo(final HAStateChangeRequestInfoProto value) {
                if (this.reqInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.reqInfo_ = value;
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder setReqInfo(final HAStateChangeRequestInfoProto.Builder builderForValue) {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder mergeReqInfo(final HAStateChangeRequestInfoProto value) {
                if (this.reqInfoBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1 && this.reqInfo_ != HAStateChangeRequestInfoProto.getDefaultInstance()) {
                        this.reqInfo_ = HAStateChangeRequestInfoProto.newBuilder(this.reqInfo_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.reqInfo_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x1;
                return this;
            }
            
            public Builder clearReqInfo() {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfo_ = HAStateChangeRequestInfoProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.reqInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            public HAStateChangeRequestInfoProto.Builder getReqInfoBuilder() {
                this.bitField0_ |= 0x1;
                this.onChanged();
                return this.getReqInfoFieldBuilder().getBuilder();
            }
            
            @Override
            public HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder() {
                if (this.reqInfoBuilder_ != null) {
                    return this.reqInfoBuilder_.getMessageOrBuilder();
                }
                return this.reqInfo_;
            }
            
            private SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder> getReqInfoFieldBuilder() {
                if (this.reqInfoBuilder_ == null) {
                    this.reqInfoBuilder_ = new SingleFieldBuilder<HAStateChangeRequestInfoProto, HAStateChangeRequestInfoProto.Builder, HAStateChangeRequestInfoProtoOrBuilder>(this.reqInfo_, this.getParentForChildren(), this.isClean());
                    this.reqInfo_ = null;
                }
                return this.reqInfoBuilder_;
            }
        }
    }
    
    public static final class TransitionToStandbyResponseProto extends GeneratedMessage implements TransitionToStandbyResponseProtoOrBuilder
    {
        private static final TransitionToStandbyResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<TransitionToStandbyResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private TransitionToStandbyResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private TransitionToStandbyResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static TransitionToStandbyResponseProto getDefaultInstance() {
            return TransitionToStandbyResponseProto.defaultInstance;
        }
        
        @Override
        public TransitionToStandbyResponseProto getDefaultInstanceForType() {
            return TransitionToStandbyResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private TransitionToStandbyResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToStandbyResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<TransitionToStandbyResponseProto> getParserForType() {
            return TransitionToStandbyResponseProto.PARSER;
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
            if (!(obj instanceof TransitionToStandbyResponseProto)) {
                return super.equals(obj);
            }
            final TransitionToStandbyResponseProto other = (TransitionToStandbyResponseProto)obj;
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
        
        public static TransitionToStandbyResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(data);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final InputStream input) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static TransitionToStandbyResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static TransitionToStandbyResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(input);
        }
        
        public static TransitionToStandbyResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return TransitionToStandbyResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final TransitionToStandbyResponseProto prototype) {
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
            TransitionToStandbyResponseProto.PARSER = new AbstractParser<TransitionToStandbyResponseProto>() {
                @Override
                public TransitionToStandbyResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new TransitionToStandbyResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new TransitionToStandbyResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements TransitionToStandbyResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(TransitionToStandbyResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (TransitionToStandbyResponseProto.alwaysUseFieldBuilders) {}
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_TransitionToStandbyResponseProto_descriptor;
            }
            
            @Override
            public TransitionToStandbyResponseProto getDefaultInstanceForType() {
                return TransitionToStandbyResponseProto.getDefaultInstance();
            }
            
            @Override
            public TransitionToStandbyResponseProto build() {
                final TransitionToStandbyResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public TransitionToStandbyResponseProto buildPartial() {
                final TransitionToStandbyResponseProto result = new TransitionToStandbyResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof TransitionToStandbyResponseProto) {
                    return this.mergeFrom((TransitionToStandbyResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final TransitionToStandbyResponseProto other) {
                if (other == TransitionToStandbyResponseProto.getDefaultInstance()) {
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
                TransitionToStandbyResponseProto parsedMessage = null;
                try {
                    parsedMessage = TransitionToStandbyResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (TransitionToStandbyResponseProto)e.getUnfinishedMessage();
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
    
    public static final class GetServiceStatusRequestProto extends GeneratedMessage implements GetServiceStatusRequestProtoOrBuilder
    {
        private static final GetServiceStatusRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetServiceStatusRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetServiceStatusRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetServiceStatusRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetServiceStatusRequestProto getDefaultInstance() {
            return GetServiceStatusRequestProto.defaultInstance;
        }
        
        @Override
        public GetServiceStatusRequestProto getDefaultInstanceForType() {
            return GetServiceStatusRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetServiceStatusRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetServiceStatusRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetServiceStatusRequestProto> getParserForType() {
            return GetServiceStatusRequestProto.PARSER;
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
            if (!(obj instanceof GetServiceStatusRequestProto)) {
                return super.equals(obj);
            }
            final GetServiceStatusRequestProto other = (GetServiceStatusRequestProto)obj;
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
        
        public static GetServiceStatusRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final InputStream input) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetServiceStatusRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetServiceStatusRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetServiceStatusRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetServiceStatusRequestProto prototype) {
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
            GetServiceStatusRequestProto.PARSER = new AbstractParser<GetServiceStatusRequestProto>() {
                @Override
                public GetServiceStatusRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetServiceStatusRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetServiceStatusRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetServiceStatusRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetServiceStatusRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetServiceStatusRequestProto.alwaysUseFieldBuilders) {}
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
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusRequestProto_descriptor;
            }
            
            @Override
            public GetServiceStatusRequestProto getDefaultInstanceForType() {
                return GetServiceStatusRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetServiceStatusRequestProto build() {
                final GetServiceStatusRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetServiceStatusRequestProto buildPartial() {
                final GetServiceStatusRequestProto result = new GetServiceStatusRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetServiceStatusRequestProto) {
                    return this.mergeFrom((GetServiceStatusRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetServiceStatusRequestProto other) {
                if (other == GetServiceStatusRequestProto.getDefaultInstance()) {
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
                GetServiceStatusRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetServiceStatusRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetServiceStatusRequestProto)e.getUnfinishedMessage();
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
    
    public static final class GetServiceStatusResponseProto extends GeneratedMessage implements GetServiceStatusResponseProtoOrBuilder
    {
        private static final GetServiceStatusResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetServiceStatusResponseProto> PARSER;
        private int bitField0_;
        public static final int STATE_FIELD_NUMBER = 1;
        private HAServiceStateProto state_;
        public static final int READYTOBECOMEACTIVE_FIELD_NUMBER = 2;
        private boolean readyToBecomeActive_;
        public static final int NOTREADYREASON_FIELD_NUMBER = 3;
        private Object notReadyReason_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetServiceStatusResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetServiceStatusResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetServiceStatusResponseProto getDefaultInstance() {
            return GetServiceStatusResponseProto.defaultInstance;
        }
        
        @Override
        public GetServiceStatusResponseProto getDefaultInstanceForType() {
            return GetServiceStatusResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetServiceStatusResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            final int rawValue = input.readEnum();
                            final HAServiceStateProto value = HAServiceStateProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x1;
                            this.state_ = value;
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.readyToBecomeActive_ = input.readBool();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.notReadyReason_ = input.readBytes();
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
            return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetServiceStatusResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetServiceStatusResponseProto> getParserForType() {
            return GetServiceStatusResponseProto.PARSER;
        }
        
        @Override
        public boolean hasState() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public HAServiceStateProto getState() {
            return this.state_;
        }
        
        @Override
        public boolean hasReadyToBecomeActive() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public boolean getReadyToBecomeActive() {
            return this.readyToBecomeActive_;
        }
        
        @Override
        public boolean hasNotReadyReason() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getNotReadyReason() {
            final Object ref = this.notReadyReason_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.notReadyReason_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getNotReadyReasonBytes() {
            final Object ref = this.notReadyReason_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.notReadyReason_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.state_ = HAServiceStateProto.INITIALIZING;
            this.readyToBecomeActive_ = false;
            this.notReadyReason_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasState()) {
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
                output.writeEnum(1, this.state_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBool(2, this.readyToBecomeActive_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getNotReadyReasonBytes());
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
                size += CodedOutputStream.computeEnumSize(1, this.state_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBoolSize(2, this.readyToBecomeActive_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getNotReadyReasonBytes());
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
            if (!(obj instanceof GetServiceStatusResponseProto)) {
                return super.equals(obj);
            }
            final GetServiceStatusResponseProto other = (GetServiceStatusResponseProto)obj;
            boolean result = true;
            result = (result && this.hasState() == other.hasState());
            if (this.hasState()) {
                result = (result && this.getState() == other.getState());
            }
            result = (result && this.hasReadyToBecomeActive() == other.hasReadyToBecomeActive());
            if (this.hasReadyToBecomeActive()) {
                result = (result && this.getReadyToBecomeActive() == other.getReadyToBecomeActive());
            }
            result = (result && this.hasNotReadyReason() == other.hasNotReadyReason());
            if (this.hasNotReadyReason()) {
                result = (result && this.getNotReadyReason().equals(other.getNotReadyReason()));
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
            if (this.hasState()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getState());
            }
            if (this.hasReadyToBecomeActive()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashBoolean(this.getReadyToBecomeActive());
            }
            if (this.hasNotReadyReason()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getNotReadyReason().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetServiceStatusResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final InputStream input) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetServiceStatusResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetServiceStatusResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetServiceStatusResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetServiceStatusResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetServiceStatusResponseProto prototype) {
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
            GetServiceStatusResponseProto.PARSER = new AbstractParser<GetServiceStatusResponseProto>() {
                @Override
                public GetServiceStatusResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetServiceStatusResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetServiceStatusResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetServiceStatusResponseProtoOrBuilder
        {
            private int bitField0_;
            private HAServiceStateProto state_;
            private boolean readyToBecomeActive_;
            private Object notReadyReason_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetServiceStatusResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.state_ = HAServiceStateProto.INITIALIZING;
                this.notReadyReason_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.state_ = HAServiceStateProto.INITIALIZING;
                this.notReadyReason_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetServiceStatusResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.state_ = HAServiceStateProto.INITIALIZING;
                this.bitField0_ &= 0xFFFFFFFE;
                this.readyToBecomeActive_ = false;
                this.bitField0_ &= 0xFFFFFFFD;
                this.notReadyReason_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return HAServiceProtocolProtos.internal_static_hadoop_common_GetServiceStatusResponseProto_descriptor;
            }
            
            @Override
            public GetServiceStatusResponseProto getDefaultInstanceForType() {
                return GetServiceStatusResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetServiceStatusResponseProto build() {
                final GetServiceStatusResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetServiceStatusResponseProto buildPartial() {
                final GetServiceStatusResponseProto result = new GetServiceStatusResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.state_ = this.state_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.readyToBecomeActive_ = this.readyToBecomeActive_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.notReadyReason_ = this.notReadyReason_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetServiceStatusResponseProto) {
                    return this.mergeFrom((GetServiceStatusResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetServiceStatusResponseProto other) {
                if (other == GetServiceStatusResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasState()) {
                    this.setState(other.getState());
                }
                if (other.hasReadyToBecomeActive()) {
                    this.setReadyToBecomeActive(other.getReadyToBecomeActive());
                }
                if (other.hasNotReadyReason()) {
                    this.bitField0_ |= 0x4;
                    this.notReadyReason_ = other.notReadyReason_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasState();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetServiceStatusResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetServiceStatusResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetServiceStatusResponseProto)e.getUnfinishedMessage();
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
            public boolean hasState() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public HAServiceStateProto getState() {
                return this.state_;
            }
            
            public Builder setState(final HAServiceStateProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.state_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearState() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.state_ = HAServiceStateProto.INITIALIZING;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasReadyToBecomeActive() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public boolean getReadyToBecomeActive() {
                return this.readyToBecomeActive_;
            }
            
            public Builder setReadyToBecomeActive(final boolean value) {
                this.bitField0_ |= 0x2;
                this.readyToBecomeActive_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearReadyToBecomeActive() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.readyToBecomeActive_ = false;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasNotReadyReason() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getNotReadyReason() {
                final Object ref = this.notReadyReason_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.notReadyReason_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getNotReadyReasonBytes() {
                final Object ref = this.notReadyReason_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.notReadyReason_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setNotReadyReason(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.notReadyReason_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNotReadyReason() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.notReadyReason_ = GetServiceStatusResponseProto.getDefaultInstance().getNotReadyReason();
                this.onChanged();
                return this;
            }
            
            public Builder setNotReadyReasonBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.notReadyReason_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public abstract static class HAServiceProtocolService implements Service
    {
        protected HAServiceProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new HAServiceProtocolService() {
                @Override
                public void monitorHealth(final RpcController controller, final MonitorHealthRequestProto request, final RpcCallback<MonitorHealthResponseProto> done) {
                    impl.monitorHealth(controller, request, done);
                }
                
                @Override
                public void transitionToActive(final RpcController controller, final TransitionToActiveRequestProto request, final RpcCallback<TransitionToActiveResponseProto> done) {
                    impl.transitionToActive(controller, request, done);
                }
                
                @Override
                public void transitionToStandby(final RpcController controller, final TransitionToStandbyRequestProto request, final RpcCallback<TransitionToStandbyResponseProto> done) {
                    impl.transitionToStandby(controller, request, done);
                }
                
                @Override
                public void getServiceStatus(final RpcController controller, final GetServiceStatusRequestProto request, final RpcCallback<GetServiceStatusResponseProto> done) {
                    impl.getServiceStatus(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return HAServiceProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != HAServiceProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.monitorHealth(controller, (MonitorHealthRequestProto)request);
                        }
                        case 1: {
                            return impl.transitionToActive(controller, (TransitionToActiveRequestProto)request);
                        }
                        case 2: {
                            return impl.transitionToStandby(controller, (TransitionToStandbyRequestProto)request);
                        }
                        case 3: {
                            return impl.getServiceStatus(controller, (GetServiceStatusRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != HAServiceProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return MonitorHealthRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return TransitionToActiveRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return TransitionToStandbyRequestProto.getDefaultInstance();
                        }
                        case 3: {
                            return GetServiceStatusRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != HAServiceProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return MonitorHealthResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return TransitionToActiveResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return TransitionToStandbyResponseProto.getDefaultInstance();
                        }
                        case 3: {
                            return GetServiceStatusResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void monitorHealth(final RpcController p0, final MonitorHealthRequestProto p1, final RpcCallback<MonitorHealthResponseProto> p2);
        
        public abstract void transitionToActive(final RpcController p0, final TransitionToActiveRequestProto p1, final RpcCallback<TransitionToActiveResponseProto> p2);
        
        public abstract void transitionToStandby(final RpcController p0, final TransitionToStandbyRequestProto p1, final RpcCallback<TransitionToStandbyResponseProto> p2);
        
        public abstract void getServiceStatus(final RpcController p0, final GetServiceStatusRequestProto p1, final RpcCallback<GetServiceStatusResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return HAServiceProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.monitorHealth(controller, (MonitorHealthRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.transitionToActive(controller, (TransitionToActiveRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.transitionToStandby(controller, (TransitionToStandbyRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 3: {
                    this.getServiceStatus(controller, (GetServiceStatusRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return MonitorHealthRequestProto.getDefaultInstance();
                }
                case 1: {
                    return TransitionToActiveRequestProto.getDefaultInstance();
                }
                case 2: {
                    return TransitionToStandbyRequestProto.getDefaultInstance();
                }
                case 3: {
                    return GetServiceStatusRequestProto.getDefaultInstance();
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
                    return MonitorHealthResponseProto.getDefaultInstance();
                }
                case 1: {
                    return TransitionToActiveResponseProto.getDefaultInstance();
                }
                case 2: {
                    return TransitionToStandbyResponseProto.getDefaultInstance();
                }
                case 3: {
                    return GetServiceStatusResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends HAServiceProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void monitorHealth(final RpcController controller, final MonitorHealthRequestProto request, final RpcCallback<MonitorHealthResponseProto> done) {
                this.channel.callMethod(HAServiceProtocolService.getDescriptor().getMethods().get(0), controller, request, MonitorHealthResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, MonitorHealthResponseProto.class, MonitorHealthResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void transitionToActive(final RpcController controller, final TransitionToActiveRequestProto request, final RpcCallback<TransitionToActiveResponseProto> done) {
                this.channel.callMethod(HAServiceProtocolService.getDescriptor().getMethods().get(1), controller, request, TransitionToActiveResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, TransitionToActiveResponseProto.class, TransitionToActiveResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void transitionToStandby(final RpcController controller, final TransitionToStandbyRequestProto request, final RpcCallback<TransitionToStandbyResponseProto> done) {
                this.channel.callMethod(HAServiceProtocolService.getDescriptor().getMethods().get(2), controller, request, TransitionToStandbyResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, TransitionToStandbyResponseProto.class, TransitionToStandbyResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getServiceStatus(final RpcController controller, final GetServiceStatusRequestProto request, final RpcCallback<GetServiceStatusResponseProto> done) {
                this.channel.callMethod(HAServiceProtocolService.getDescriptor().getMethods().get(3), controller, request, GetServiceStatusResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GetServiceStatusResponseProto.class, GetServiceStatusResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public MonitorHealthResponseProto monitorHealth(final RpcController controller, final MonitorHealthRequestProto request) throws ServiceException {
                return (MonitorHealthResponseProto)this.channel.callBlockingMethod(HAServiceProtocolService.getDescriptor().getMethods().get(0), controller, request, MonitorHealthResponseProto.getDefaultInstance());
            }
            
            @Override
            public TransitionToActiveResponseProto transitionToActive(final RpcController controller, final TransitionToActiveRequestProto request) throws ServiceException {
                return (TransitionToActiveResponseProto)this.channel.callBlockingMethod(HAServiceProtocolService.getDescriptor().getMethods().get(1), controller, request, TransitionToActiveResponseProto.getDefaultInstance());
            }
            
            @Override
            public TransitionToStandbyResponseProto transitionToStandby(final RpcController controller, final TransitionToStandbyRequestProto request) throws ServiceException {
                return (TransitionToStandbyResponseProto)this.channel.callBlockingMethod(HAServiceProtocolService.getDescriptor().getMethods().get(2), controller, request, TransitionToStandbyResponseProto.getDefaultInstance());
            }
            
            @Override
            public GetServiceStatusResponseProto getServiceStatus(final RpcController controller, final GetServiceStatusRequestProto request) throws ServiceException {
                return (GetServiceStatusResponseProto)this.channel.callBlockingMethod(HAServiceProtocolService.getDescriptor().getMethods().get(3), controller, request, GetServiceStatusResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            MonitorHealthResponseProto monitorHealth(final RpcController p0, final MonitorHealthRequestProto p1) throws ServiceException;
            
            TransitionToActiveResponseProto transitionToActive(final RpcController p0, final TransitionToActiveRequestProto p1) throws ServiceException;
            
            TransitionToStandbyResponseProto transitionToStandby(final RpcController p0, final TransitionToStandbyRequestProto p1) throws ServiceException;
            
            GetServiceStatusResponseProto getServiceStatus(final RpcController p0, final GetServiceStatusRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void monitorHealth(final RpcController p0, final MonitorHealthRequestProto p1, final RpcCallback<MonitorHealthResponseProto> p2);
            
            void transitionToActive(final RpcController p0, final TransitionToActiveRequestProto p1, final RpcCallback<TransitionToActiveResponseProto> p2);
            
            void transitionToStandby(final RpcController p0, final TransitionToStandbyRequestProto p1, final RpcCallback<TransitionToStandbyResponseProto> p2);
            
            void getServiceStatus(final RpcController p0, final GetServiceStatusRequestProto p1, final RpcCallback<GetServiceStatusResponseProto> p2);
        }
    }
    
    public interface MonitorHealthRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface MonitorHealthResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface HAStateChangeRequestInfoProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasReqSource();
        
        HARequestSource getReqSource();
    }
    
    public interface TransitionToActiveRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasReqInfo();
        
        HAStateChangeRequestInfoProto getReqInfo();
        
        HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder();
    }
    
    public interface TransitionToActiveResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface TransitionToStandbyRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasReqInfo();
        
        HAStateChangeRequestInfoProto getReqInfo();
        
        HAStateChangeRequestInfoProtoOrBuilder getReqInfoOrBuilder();
    }
    
    public interface TransitionToStandbyResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface GetServiceStatusRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface GetServiceStatusResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasState();
        
        HAServiceStateProto getState();
        
        boolean hasReadyToBecomeActive();
        
        boolean getReadyToBecomeActive();
        
        boolean hasNotReadyReason();
        
        String getNotReadyReason();
        
        ByteString getNotReadyReasonBytes();
    }
}
