// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protobuf;

import com.google.protobuf.MessageOrBuilder;
import java.util.Collection;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
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

public final class RpcHeaderProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_RPCTraceInfoProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RPCTraceInfoProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RPCCallerContextProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RPCCallerContextProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RpcRequestHeaderProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RpcRequestHeaderProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RpcResponseHeaderProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RpcResponseHeaderProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RpcSaslProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RpcSaslProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RpcSaslProto_SaslAuth_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private RpcHeaderProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return RpcHeaderProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u000fRpcHeader.proto\u0012\rhadoop.common\"6\n\u0011RPCTraceInfoProto\u0012\u000f\n\u0007traceId\u0018\u0001 \u0001(\u0003\u0012\u0010\n\bparentId\u0018\u0002 \u0001(\u0003\";\n\u0015RPCCallerContextProto\u0012\u000f\n\u0007context\u0018\u0001 \u0002(\t\u0012\u0011\n\tsignature\u0018\u0002 \u0001(\f\"\u0094\u0003\n\u0015RpcRequestHeaderProto\u0012,\n\u0007rpcKind\u0018\u0001 \u0001(\u000e2\u001b.hadoop.common.RpcKindProto\u0012B\n\u0005rpcOp\u0018\u0002 \u0001(\u000e23.hadoop.common.RpcRequestHeaderProto.OperationProto\u0012\u000e\n\u0006callId\u0018\u0003 \u0002(\u0011\u0012\u0010\n\bclientId\u0018\u0004 \u0002(\f\u0012\u0016\n\nretryCount\u0018\u0005 \u0001(\u0011:\u0002-1\u00123\n\ttraceInfo\u0018\u0006 \u0001(\u000b2 .hadoop.common.RPCTraceInfoProto", "\u0012;\n\rcallerContext\u0018\u0007 \u0001(\u000b2$.hadoop.common.RPCCallerContextProto\"]\n\u000eOperationProto\u0012\u0014\n\u0010RPC_FINAL_PACKET\u0010\u0000\u0012\u001b\n\u0017RPC_CONTINUATION_PACKET\u0010\u0001\u0012\u0018\n\u0014RPC_CLOSE_CONNECTION\u0010\u0002\"\u00ca\u0005\n\u0016RpcResponseHeaderProto\u0012\u000e\n\u0006callId\u0018\u0001 \u0002(\r\u0012D\n\u0006status\u0018\u0002 \u0002(\u000e24.hadoop.common.RpcResponseHeaderProto.RpcStatusProto\u0012\u001b\n\u0013serverIpcVersionNum\u0018\u0003 \u0001(\r\u0012\u001a\n\u0012exceptionClassName\u0018\u0004 \u0001(\t\u0012\u0010\n\berrorMsg\u0018\u0005 \u0001(\t\u0012L\n\u000berrorDetail\u0018\u0006 \u0001(\u000e27.hadoop.common.RpcResponseHeaderP", "roto.RpcErrorCodeProto\u0012\u0010\n\bclientId\u0018\u0007 \u0001(\f\u0012\u0016\n\nretryCount\u0018\b \u0001(\u0011:\u0002-1\"3\n\u000eRpcStatusProto\u0012\u000b\n\u0007SUCCESS\u0010\u0000\u0012\t\n\u0005ERROR\u0010\u0001\u0012\t\n\u0005FATAL\u0010\u0002\"\u00e1\u0002\n\u0011RpcErrorCodeProto\u0012\u0015\n\u0011ERROR_APPLICATION\u0010\u0001\u0012\u0018\n\u0014ERROR_NO_SUCH_METHOD\u0010\u0002\u0012\u001a\n\u0016ERROR_NO_SUCH_PROTOCOL\u0010\u0003\u0012\u0014\n\u0010ERROR_RPC_SERVER\u0010\u0004\u0012\u001e\n\u001aERROR_SERIALIZING_RESPONSE\u0010\u0005\u0012\u001e\n\u001aERROR_RPC_VERSION_MISMATCH\u0010\u0006\u0012\u0011\n\rFATAL_UNKNOWN\u0010\n\u0012#\n\u001fFATAL_UNSUPPORTED_SERIALIZATION\u0010\u000b\u0012\u001c\n\u0018FATAL_INVALID_RPC_HEADER\u0010\f\u0012\u001f\n\u001bFATAL_DE", "SERIALIZING_REQUEST\u0010\r\u0012\u001a\n\u0016FATAL_VERSION_MISMATCH\u0010\u000e\u0012\u0016\n\u0012FATAL_UNAUTHORIZED\u0010\u000f\"\u00dd\u0002\n\fRpcSaslProto\u0012\u000f\n\u0007version\u0018\u0001 \u0001(\r\u00124\n\u0005state\u0018\u0002 \u0002(\u000e2%.hadoop.common.RpcSaslProto.SaslState\u0012\r\n\u0005token\u0018\u0003 \u0001(\f\u00123\n\u0005auths\u0018\u0004 \u0003(\u000b2$.hadoop.common.RpcSaslProto.SaslAuth\u001ad\n\bSaslAuth\u0012\u000e\n\u0006method\u0018\u0001 \u0002(\t\u0012\u0011\n\tmechanism\u0018\u0002 \u0002(\t\u0012\u0010\n\bprotocol\u0018\u0003 \u0001(\t\u0012\u0010\n\bserverId\u0018\u0004 \u0001(\t\u0012\u0011\n\tchallenge\u0018\u0005 \u0001(\f\"\\\n\tSaslState\u0012\u000b\n\u0007SUCCESS\u0010\u0000\u0012\r\n\tNEGOTIATE\u0010\u0001\u0012\f\n\bINITIATE\u0010\u0002\u0012\r\n\tCHALLENGE\u0010", "\u0003\u0012\f\n\bRESPONSE\u0010\u0004\u0012\b\n\u0004WRAP\u0010\u0005*J\n\fRpcKindProto\u0012\u000f\n\u000bRPC_BUILTIN\u0010\u0000\u0012\u0010\n\fRPC_WRITABLE\u0010\u0001\u0012\u0017\n\u0013RPC_PROTOCOL_BUFFER\u0010\u0002B4\n\u001eorg.apache.hadoop.ipc.protobufB\u000fRpcHeaderProtos \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                RpcHeaderProtos.descriptor = root;
                RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_descriptor = RpcHeaderProtos.getDescriptor().getMessageTypes().get(0);
                RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_descriptor, new String[] { "TraceId", "ParentId" });
                RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_descriptor = RpcHeaderProtos.getDescriptor().getMessageTypes().get(1);
                RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_descriptor, new String[] { "Context", "Signature" });
                RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_descriptor = RpcHeaderProtos.getDescriptor().getMessageTypes().get(2);
                RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_descriptor, new String[] { "RpcKind", "RpcOp", "CallId", "ClientId", "RetryCount", "TraceInfo", "CallerContext" });
                RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_descriptor = RpcHeaderProtos.getDescriptor().getMessageTypes().get(3);
                RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_descriptor, new String[] { "CallId", "Status", "ServerIpcVersionNum", "ExceptionClassName", "ErrorMsg", "ErrorDetail", "ClientId", "RetryCount" });
                RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor = RpcHeaderProtos.getDescriptor().getMessageTypes().get(4);
                RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor, new String[] { "Version", "State", "Token", "Auths" });
                RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor = RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor.getNestedTypes().get(0);
                RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor, new String[] { "Method", "Mechanism", "Protocol", "ServerId", "Challenge" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public enum RpcKindProto implements ProtocolMessageEnum
    {
        RPC_BUILTIN(0, 0), 
        RPC_WRITABLE(1, 1), 
        RPC_PROTOCOL_BUFFER(2, 2);
        
        public static final int RPC_BUILTIN_VALUE = 0;
        public static final int RPC_WRITABLE_VALUE = 1;
        public static final int RPC_PROTOCOL_BUFFER_VALUE = 2;
        private static Internal.EnumLiteMap<RpcKindProto> internalValueMap;
        private static final RpcKindProto[] VALUES;
        private final int index;
        private final int value;
        
        @Override
        public final int getNumber() {
            return this.value;
        }
        
        public static RpcKindProto valueOf(final int value) {
            switch (value) {
                case 0: {
                    return RpcKindProto.RPC_BUILTIN;
                }
                case 1: {
                    return RpcKindProto.RPC_WRITABLE;
                }
                case 2: {
                    return RpcKindProto.RPC_PROTOCOL_BUFFER;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static Internal.EnumLiteMap<RpcKindProto> internalGetValueMap() {
            return RpcKindProto.internalValueMap;
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
            return RpcHeaderProtos.getDescriptor().getEnumTypes().get(0);
        }
        
        public static RpcKindProto valueOf(final Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return RpcKindProto.VALUES[desc.getIndex()];
        }
        
        private RpcKindProto(final int index, final int value) {
            this.index = index;
            this.value = value;
        }
        
        static {
            RpcKindProto.internalValueMap = new Internal.EnumLiteMap<RpcKindProto>() {
                @Override
                public RpcKindProto findValueByNumber(final int number) {
                    return RpcKindProto.valueOf(number);
                }
            };
            VALUES = values();
        }
    }
    
    public static final class RPCTraceInfoProto extends GeneratedMessage implements RPCTraceInfoProtoOrBuilder
    {
        private static final RPCTraceInfoProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RPCTraceInfoProto> PARSER;
        private int bitField0_;
        public static final int TRACEID_FIELD_NUMBER = 1;
        private long traceId_;
        public static final int PARENTID_FIELD_NUMBER = 2;
        private long parentId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RPCTraceInfoProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RPCTraceInfoProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RPCTraceInfoProto getDefaultInstance() {
            return RPCTraceInfoProto.defaultInstance;
        }
        
        @Override
        public RPCTraceInfoProto getDefaultInstanceForType() {
            return RPCTraceInfoProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RPCTraceInfoProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.traceId_ = input.readInt64();
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.parentId_ = input.readInt64();
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
            return RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RPCTraceInfoProto.class, Builder.class);
        }
        
        @Override
        public Parser<RPCTraceInfoProto> getParserForType() {
            return RPCTraceInfoProto.PARSER;
        }
        
        @Override
        public boolean hasTraceId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getTraceId() {
            return this.traceId_;
        }
        
        @Override
        public boolean hasParentId() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public long getParentId() {
            return this.parentId_;
        }
        
        private void initFields() {
            this.traceId_ = 0L;
            this.parentId_ = 0L;
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
                output.writeInt64(1, this.traceId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt64(2, this.parentId_);
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
                size += CodedOutputStream.computeInt64Size(1, this.traceId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt64Size(2, this.parentId_);
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
            if (!(obj instanceof RPCTraceInfoProto)) {
                return super.equals(obj);
            }
            final RPCTraceInfoProto other = (RPCTraceInfoProto)obj;
            boolean result = true;
            result = (result && this.hasTraceId() == other.hasTraceId());
            if (this.hasTraceId()) {
                result = (result && this.getTraceId() == other.getTraceId());
            }
            result = (result && this.hasParentId() == other.hasParentId());
            if (this.hasParentId()) {
                result = (result && this.getParentId() == other.getParentId());
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
            if (this.hasTraceId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getTraceId());
            }
            if (this.hasParentId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashLong(this.getParentId());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RPCTraceInfoProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RPCTraceInfoProto.PARSER.parseFrom(data);
        }
        
        public static RPCTraceInfoProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RPCTraceInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RPCTraceInfoProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RPCTraceInfoProto.PARSER.parseFrom(data);
        }
        
        public static RPCTraceInfoProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RPCTraceInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RPCTraceInfoProto parseFrom(final InputStream input) throws IOException {
            return RPCTraceInfoProto.PARSER.parseFrom(input);
        }
        
        public static RPCTraceInfoProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCTraceInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RPCTraceInfoProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RPCTraceInfoProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RPCTraceInfoProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCTraceInfoProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RPCTraceInfoProto parseFrom(final CodedInputStream input) throws IOException {
            return RPCTraceInfoProto.PARSER.parseFrom(input);
        }
        
        public static RPCTraceInfoProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCTraceInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RPCTraceInfoProto prototype) {
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
            RPCTraceInfoProto.PARSER = new AbstractParser<RPCTraceInfoProto>() {
                @Override
                public RPCTraceInfoProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RPCTraceInfoProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RPCTraceInfoProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RPCTraceInfoProtoOrBuilder
        {
            private int bitField0_;
            private long traceId_;
            private long parentId_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RPCTraceInfoProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RPCTraceInfoProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.traceId_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                this.parentId_ = 0L;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCTraceInfoProto_descriptor;
            }
            
            @Override
            public RPCTraceInfoProto getDefaultInstanceForType() {
                return RPCTraceInfoProto.getDefaultInstance();
            }
            
            @Override
            public RPCTraceInfoProto build() {
                final RPCTraceInfoProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RPCTraceInfoProto buildPartial() {
                final RPCTraceInfoProto result = new RPCTraceInfoProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.traceId_ = this.traceId_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.parentId_ = this.parentId_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RPCTraceInfoProto) {
                    return this.mergeFrom((RPCTraceInfoProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RPCTraceInfoProto other) {
                if (other == RPCTraceInfoProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasTraceId()) {
                    this.setTraceId(other.getTraceId());
                }
                if (other.hasParentId()) {
                    this.setParentId(other.getParentId());
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
                RPCTraceInfoProto parsedMessage = null;
                try {
                    parsedMessage = RPCTraceInfoProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RPCTraceInfoProto)e.getUnfinishedMessage();
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
            public boolean hasTraceId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getTraceId() {
                return this.traceId_;
            }
            
            public Builder setTraceId(final long value) {
                this.bitField0_ |= 0x1;
                this.traceId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearTraceId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.traceId_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasParentId() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public long getParentId() {
                return this.parentId_;
            }
            
            public Builder setParentId(final long value) {
                this.bitField0_ |= 0x2;
                this.parentId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearParentId() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.parentId_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RPCCallerContextProto extends GeneratedMessage implements RPCCallerContextProtoOrBuilder
    {
        private static final RPCCallerContextProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RPCCallerContextProto> PARSER;
        private int bitField0_;
        public static final int CONTEXT_FIELD_NUMBER = 1;
        private Object context_;
        public static final int SIGNATURE_FIELD_NUMBER = 2;
        private ByteString signature_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RPCCallerContextProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RPCCallerContextProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RPCCallerContextProto getDefaultInstance() {
            return RPCCallerContextProto.defaultInstance;
        }
        
        @Override
        public RPCCallerContextProto getDefaultInstanceForType() {
            return RPCCallerContextProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RPCCallerContextProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.context_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.signature_ = input.readBytes();
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
            return RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RPCCallerContextProto.class, Builder.class);
        }
        
        @Override
        public Parser<RPCCallerContextProto> getParserForType() {
            return RPCCallerContextProto.PARSER;
        }
        
        @Override
        public boolean hasContext() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getContext() {
            final Object ref = this.context_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.context_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getContextBytes() {
            final Object ref = this.context_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.context_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasSignature() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public ByteString getSignature() {
            return this.signature_;
        }
        
        private void initFields() {
            this.context_ = "";
            this.signature_ = ByteString.EMPTY;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasContext()) {
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
                output.writeBytes(1, this.getContextBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.signature_);
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
                size += CodedOutputStream.computeBytesSize(1, this.getContextBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.signature_);
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
            if (!(obj instanceof RPCCallerContextProto)) {
                return super.equals(obj);
            }
            final RPCCallerContextProto other = (RPCCallerContextProto)obj;
            boolean result = true;
            result = (result && this.hasContext() == other.hasContext());
            if (this.hasContext()) {
                result = (result && this.getContext().equals(other.getContext()));
            }
            result = (result && this.hasSignature() == other.hasSignature());
            if (this.hasSignature()) {
                result = (result && this.getSignature().equals(other.getSignature()));
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
            if (this.hasContext()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getContext().hashCode();
            }
            if (this.hasSignature()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getSignature().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RPCCallerContextProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RPCCallerContextProto.PARSER.parseFrom(data);
        }
        
        public static RPCCallerContextProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RPCCallerContextProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RPCCallerContextProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RPCCallerContextProto.PARSER.parseFrom(data);
        }
        
        public static RPCCallerContextProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RPCCallerContextProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RPCCallerContextProto parseFrom(final InputStream input) throws IOException {
            return RPCCallerContextProto.PARSER.parseFrom(input);
        }
        
        public static RPCCallerContextProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCCallerContextProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RPCCallerContextProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RPCCallerContextProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RPCCallerContextProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCCallerContextProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RPCCallerContextProto parseFrom(final CodedInputStream input) throws IOException {
            return RPCCallerContextProto.PARSER.parseFrom(input);
        }
        
        public static RPCCallerContextProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RPCCallerContextProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RPCCallerContextProto prototype) {
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
            RPCCallerContextProto.PARSER = new AbstractParser<RPCCallerContextProto>() {
                @Override
                public RPCCallerContextProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RPCCallerContextProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RPCCallerContextProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RPCCallerContextProtoOrBuilder
        {
            private int bitField0_;
            private Object context_;
            private ByteString signature_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RPCCallerContextProto.class, Builder.class);
            }
            
            private Builder() {
                this.context_ = "";
                this.signature_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.context_ = "";
                this.signature_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RPCCallerContextProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.context_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.signature_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return RpcHeaderProtos.internal_static_hadoop_common_RPCCallerContextProto_descriptor;
            }
            
            @Override
            public RPCCallerContextProto getDefaultInstanceForType() {
                return RPCCallerContextProto.getDefaultInstance();
            }
            
            @Override
            public RPCCallerContextProto build() {
                final RPCCallerContextProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RPCCallerContextProto buildPartial() {
                final RPCCallerContextProto result = new RPCCallerContextProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.context_ = this.context_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.signature_ = this.signature_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RPCCallerContextProto) {
                    return this.mergeFrom((RPCCallerContextProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RPCCallerContextProto other) {
                if (other == RPCCallerContextProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasContext()) {
                    this.bitField0_ |= 0x1;
                    this.context_ = other.context_;
                    this.onChanged();
                }
                if (other.hasSignature()) {
                    this.setSignature(other.getSignature());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasContext();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RPCCallerContextProto parsedMessage = null;
                try {
                    parsedMessage = RPCCallerContextProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RPCCallerContextProto)e.getUnfinishedMessage();
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
            public boolean hasContext() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getContext() {
                final Object ref = this.context_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.context_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getContextBytes() {
                final Object ref = this.context_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.context_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setContext(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.context_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearContext() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.context_ = RPCCallerContextProto.getDefaultInstance().getContext();
                this.onChanged();
                return this;
            }
            
            public Builder setContextBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.context_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSignature() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public ByteString getSignature() {
                return this.signature_;
            }
            
            public Builder setSignature(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.signature_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSignature() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.signature_ = RPCCallerContextProto.getDefaultInstance().getSignature();
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RpcRequestHeaderProto extends GeneratedMessage implements RpcRequestHeaderProtoOrBuilder
    {
        private static final RpcRequestHeaderProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RpcRequestHeaderProto> PARSER;
        private int bitField0_;
        public static final int RPCKIND_FIELD_NUMBER = 1;
        private RpcKindProto rpcKind_;
        public static final int RPCOP_FIELD_NUMBER = 2;
        private OperationProto rpcOp_;
        public static final int CALLID_FIELD_NUMBER = 3;
        private int callId_;
        public static final int CLIENTID_FIELD_NUMBER = 4;
        private ByteString clientId_;
        public static final int RETRYCOUNT_FIELD_NUMBER = 5;
        private int retryCount_;
        public static final int TRACEINFO_FIELD_NUMBER = 6;
        private RPCTraceInfoProto traceInfo_;
        public static final int CALLERCONTEXT_FIELD_NUMBER = 7;
        private RPCCallerContextProto callerContext_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RpcRequestHeaderProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RpcRequestHeaderProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RpcRequestHeaderProto getDefaultInstance() {
            return RpcRequestHeaderProto.defaultInstance;
        }
        
        @Override
        public RpcRequestHeaderProto getDefaultInstanceForType() {
            return RpcRequestHeaderProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RpcRequestHeaderProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            final RpcKindProto value = RpcKindProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x1;
                            this.rpcKind_ = value;
                            continue;
                        }
                        case 16: {
                            final int rawValue = input.readEnum();
                            final OperationProto value2 = OperationProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(2, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x2;
                            this.rpcOp_ = value2;
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.callId_ = input.readSInt32();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.clientId_ = input.readBytes();
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x10;
                            this.retryCount_ = input.readSInt32();
                            continue;
                        }
                        case 50: {
                            RPCTraceInfoProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x20) == 0x20) {
                                subBuilder = this.traceInfo_.toBuilder();
                            }
                            this.traceInfo_ = input.readMessage(RPCTraceInfoProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.traceInfo_);
                                this.traceInfo_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x20;
                            continue;
                        }
                        case 58: {
                            RPCCallerContextProto.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x40) == 0x40) {
                                subBuilder2 = this.callerContext_.toBuilder();
                            }
                            this.callerContext_ = input.readMessage(RPCCallerContextProto.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.callerContext_);
                                this.callerContext_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x40;
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
            return RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcRequestHeaderProto.class, Builder.class);
        }
        
        @Override
        public Parser<RpcRequestHeaderProto> getParserForType() {
            return RpcRequestHeaderProto.PARSER;
        }
        
        @Override
        public boolean hasRpcKind() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public RpcKindProto getRpcKind() {
            return this.rpcKind_;
        }
        
        @Override
        public boolean hasRpcOp() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public OperationProto getRpcOp() {
            return this.rpcOp_;
        }
        
        @Override
        public boolean hasCallId() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public int getCallId() {
            return this.callId_;
        }
        
        @Override
        public boolean hasClientId() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public ByteString getClientId() {
            return this.clientId_;
        }
        
        @Override
        public boolean hasRetryCount() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public int getRetryCount() {
            return this.retryCount_;
        }
        
        @Override
        public boolean hasTraceInfo() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public RPCTraceInfoProto getTraceInfo() {
            return this.traceInfo_;
        }
        
        @Override
        public RPCTraceInfoProtoOrBuilder getTraceInfoOrBuilder() {
            return this.traceInfo_;
        }
        
        @Override
        public boolean hasCallerContext() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public RPCCallerContextProto getCallerContext() {
            return this.callerContext_;
        }
        
        @Override
        public RPCCallerContextProtoOrBuilder getCallerContextOrBuilder() {
            return this.callerContext_;
        }
        
        private void initFields() {
            this.rpcKind_ = RpcKindProto.RPC_BUILTIN;
            this.rpcOp_ = OperationProto.RPC_FINAL_PACKET;
            this.callId_ = 0;
            this.clientId_ = ByteString.EMPTY;
            this.retryCount_ = -1;
            this.traceInfo_ = RPCTraceInfoProto.getDefaultInstance();
            this.callerContext_ = RPCCallerContextProto.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasCallId()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasClientId()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (this.hasCallerContext() && !this.getCallerContext().isInitialized()) {
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
                output.writeEnum(1, this.rpcKind_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeEnum(2, this.rpcOp_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeSInt32(3, this.callId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.clientId_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeSInt32(5, this.retryCount_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeMessage(6, this.traceInfo_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeMessage(7, this.callerContext_);
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
                size += CodedOutputStream.computeEnumSize(1, this.rpcKind_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeEnumSize(2, this.rpcOp_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeSInt32Size(3, this.callId_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.clientId_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeSInt32Size(5, this.retryCount_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeMessageSize(6, this.traceInfo_);
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeMessageSize(7, this.callerContext_);
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
            if (!(obj instanceof RpcRequestHeaderProto)) {
                return super.equals(obj);
            }
            final RpcRequestHeaderProto other = (RpcRequestHeaderProto)obj;
            boolean result = true;
            result = (result && this.hasRpcKind() == other.hasRpcKind());
            if (this.hasRpcKind()) {
                result = (result && this.getRpcKind() == other.getRpcKind());
            }
            result = (result && this.hasRpcOp() == other.hasRpcOp());
            if (this.hasRpcOp()) {
                result = (result && this.getRpcOp() == other.getRpcOp());
            }
            result = (result && this.hasCallId() == other.hasCallId());
            if (this.hasCallId()) {
                result = (result && this.getCallId() == other.getCallId());
            }
            result = (result && this.hasClientId() == other.hasClientId());
            if (this.hasClientId()) {
                result = (result && this.getClientId().equals(other.getClientId()));
            }
            result = (result && this.hasRetryCount() == other.hasRetryCount());
            if (this.hasRetryCount()) {
                result = (result && this.getRetryCount() == other.getRetryCount());
            }
            result = (result && this.hasTraceInfo() == other.hasTraceInfo());
            if (this.hasTraceInfo()) {
                result = (result && this.getTraceInfo().equals(other.getTraceInfo()));
            }
            result = (result && this.hasCallerContext() == other.hasCallerContext());
            if (this.hasCallerContext()) {
                result = (result && this.getCallerContext().equals(other.getCallerContext()));
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
            if (this.hasRpcKind()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getRpcKind());
            }
            if (this.hasRpcOp()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getRpcOp());
            }
            if (this.hasCallId()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getCallId();
            }
            if (this.hasClientId()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getClientId().hashCode();
            }
            if (this.hasRetryCount()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getRetryCount();
            }
            if (this.hasTraceInfo()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getTraceInfo().hashCode();
            }
            if (this.hasCallerContext()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getCallerContext().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RpcRequestHeaderProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RpcRequestHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RpcRequestHeaderProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcRequestHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcRequestHeaderProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RpcRequestHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RpcRequestHeaderProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcRequestHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcRequestHeaderProto parseFrom(final InputStream input) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RpcRequestHeaderProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RpcRequestHeaderProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RpcRequestHeaderProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RpcRequestHeaderProto parseFrom(final CodedInputStream input) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RpcRequestHeaderProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcRequestHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RpcRequestHeaderProto prototype) {
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
            RpcRequestHeaderProto.PARSER = new AbstractParser<RpcRequestHeaderProto>() {
                @Override
                public RpcRequestHeaderProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RpcRequestHeaderProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RpcRequestHeaderProto(true)).initFields();
        }
        
        public enum OperationProto implements ProtocolMessageEnum
        {
            RPC_FINAL_PACKET(0, 0), 
            RPC_CONTINUATION_PACKET(1, 1), 
            RPC_CLOSE_CONNECTION(2, 2);
            
            public static final int RPC_FINAL_PACKET_VALUE = 0;
            public static final int RPC_CONTINUATION_PACKET_VALUE = 1;
            public static final int RPC_CLOSE_CONNECTION_VALUE = 2;
            private static Internal.EnumLiteMap<OperationProto> internalValueMap;
            private static final OperationProto[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static OperationProto valueOf(final int value) {
                switch (value) {
                    case 0: {
                        return OperationProto.RPC_FINAL_PACKET;
                    }
                    case 1: {
                        return OperationProto.RPC_CONTINUATION_PACKET;
                    }
                    case 2: {
                        return OperationProto.RPC_CLOSE_CONNECTION;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<OperationProto> internalGetValueMap() {
                return OperationProto.internalValueMap;
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
                return RpcRequestHeaderProto.getDescriptor().getEnumTypes().get(0);
            }
            
            public static OperationProto valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return OperationProto.VALUES[desc.getIndex()];
            }
            
            private OperationProto(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                OperationProto.internalValueMap = new Internal.EnumLiteMap<OperationProto>() {
                    @Override
                    public OperationProto findValueByNumber(final int number) {
                        return OperationProto.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RpcRequestHeaderProtoOrBuilder
        {
            private int bitField0_;
            private RpcKindProto rpcKind_;
            private OperationProto rpcOp_;
            private int callId_;
            private ByteString clientId_;
            private int retryCount_;
            private RPCTraceInfoProto traceInfo_;
            private SingleFieldBuilder<RPCTraceInfoProto, RPCTraceInfoProto.Builder, RPCTraceInfoProtoOrBuilder> traceInfoBuilder_;
            private RPCCallerContextProto callerContext_;
            private SingleFieldBuilder<RPCCallerContextProto, RPCCallerContextProto.Builder, RPCCallerContextProtoOrBuilder> callerContextBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcRequestHeaderProto.class, Builder.class);
            }
            
            private Builder() {
                this.rpcKind_ = RpcKindProto.RPC_BUILTIN;
                this.rpcOp_ = OperationProto.RPC_FINAL_PACKET;
                this.clientId_ = ByteString.EMPTY;
                this.retryCount_ = -1;
                this.traceInfo_ = RPCTraceInfoProto.getDefaultInstance();
                this.callerContext_ = RPCCallerContextProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.rpcKind_ = RpcKindProto.RPC_BUILTIN;
                this.rpcOp_ = OperationProto.RPC_FINAL_PACKET;
                this.clientId_ = ByteString.EMPTY;
                this.retryCount_ = -1;
                this.traceInfo_ = RPCTraceInfoProto.getDefaultInstance();
                this.callerContext_ = RPCCallerContextProto.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RpcRequestHeaderProto.alwaysUseFieldBuilders) {
                    this.getTraceInfoFieldBuilder();
                    this.getCallerContextFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.rpcKind_ = RpcKindProto.RPC_BUILTIN;
                this.bitField0_ &= 0xFFFFFFFE;
                this.rpcOp_ = OperationProto.RPC_FINAL_PACKET;
                this.bitField0_ &= 0xFFFFFFFD;
                this.callId_ = 0;
                this.bitField0_ &= 0xFFFFFFFB;
                this.clientId_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFF7;
                this.retryCount_ = -1;
                this.bitField0_ &= 0xFFFFFFEF;
                if (this.traceInfoBuilder_ == null) {
                    this.traceInfo_ = RPCTraceInfoProto.getDefaultInstance();
                }
                else {
                    this.traceInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFDF;
                if (this.callerContextBuilder_ == null) {
                    this.callerContext_ = RPCCallerContextProto.getDefaultInstance();
                }
                else {
                    this.callerContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcRequestHeaderProto_descriptor;
            }
            
            @Override
            public RpcRequestHeaderProto getDefaultInstanceForType() {
                return RpcRequestHeaderProto.getDefaultInstance();
            }
            
            @Override
            public RpcRequestHeaderProto build() {
                final RpcRequestHeaderProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RpcRequestHeaderProto buildPartial() {
                final RpcRequestHeaderProto result = new RpcRequestHeaderProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.rpcKind_ = this.rpcKind_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.rpcOp_ = this.rpcOp_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.callId_ = this.callId_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.clientId_ = this.clientId_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.retryCount_ = this.retryCount_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                if (this.traceInfoBuilder_ == null) {
                    result.traceInfo_ = this.traceInfo_;
                }
                else {
                    result.traceInfo_ = this.traceInfoBuilder_.build();
                }
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                if (this.callerContextBuilder_ == null) {
                    result.callerContext_ = this.callerContext_;
                }
                else {
                    result.callerContext_ = this.callerContextBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RpcRequestHeaderProto) {
                    return this.mergeFrom((RpcRequestHeaderProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RpcRequestHeaderProto other) {
                if (other == RpcRequestHeaderProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasRpcKind()) {
                    this.setRpcKind(other.getRpcKind());
                }
                if (other.hasRpcOp()) {
                    this.setRpcOp(other.getRpcOp());
                }
                if (other.hasCallId()) {
                    this.setCallId(other.getCallId());
                }
                if (other.hasClientId()) {
                    this.setClientId(other.getClientId());
                }
                if (other.hasRetryCount()) {
                    this.setRetryCount(other.getRetryCount());
                }
                if (other.hasTraceInfo()) {
                    this.mergeTraceInfo(other.getTraceInfo());
                }
                if (other.hasCallerContext()) {
                    this.mergeCallerContext(other.getCallerContext());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasCallId() && this.hasClientId() && (!this.hasCallerContext() || this.getCallerContext().isInitialized());
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RpcRequestHeaderProto parsedMessage = null;
                try {
                    parsedMessage = RpcRequestHeaderProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RpcRequestHeaderProto)e.getUnfinishedMessage();
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
            public boolean hasRpcKind() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public RpcKindProto getRpcKind() {
                return this.rpcKind_;
            }
            
            public Builder setRpcKind(final RpcKindProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.rpcKind_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcKind() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.rpcKind_ = RpcKindProto.RPC_BUILTIN;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRpcOp() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public OperationProto getRpcOp() {
                return this.rpcOp_;
            }
            
            public Builder setRpcOp(final OperationProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.rpcOp_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRpcOp() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.rpcOp_ = OperationProto.RPC_FINAL_PACKET;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasCallId() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public int getCallId() {
                return this.callId_;
            }
            
            public Builder setCallId(final int value) {
                this.bitField0_ |= 0x4;
                this.callId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCallId() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.callId_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasClientId() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public ByteString getClientId() {
                return this.clientId_;
            }
            
            public Builder setClientId(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.clientId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClientId() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.clientId_ = RpcRequestHeaderProto.getDefaultInstance().getClientId();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRetryCount() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public int getRetryCount() {
                return this.retryCount_;
            }
            
            public Builder setRetryCount(final int value) {
                this.bitField0_ |= 0x10;
                this.retryCount_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRetryCount() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.retryCount_ = -1;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasTraceInfo() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public RPCTraceInfoProto getTraceInfo() {
                if (this.traceInfoBuilder_ == null) {
                    return this.traceInfo_;
                }
                return this.traceInfoBuilder_.getMessage();
            }
            
            public Builder setTraceInfo(final RPCTraceInfoProto value) {
                if (this.traceInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.traceInfo_ = value;
                    this.onChanged();
                }
                else {
                    this.traceInfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x20;
                return this;
            }
            
            public Builder setTraceInfo(final RPCTraceInfoProto.Builder builderForValue) {
                if (this.traceInfoBuilder_ == null) {
                    this.traceInfo_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.traceInfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x20;
                return this;
            }
            
            public Builder mergeTraceInfo(final RPCTraceInfoProto value) {
                if (this.traceInfoBuilder_ == null) {
                    if ((this.bitField0_ & 0x20) == 0x20 && this.traceInfo_ != RPCTraceInfoProto.getDefaultInstance()) {
                        this.traceInfo_ = RPCTraceInfoProto.newBuilder(this.traceInfo_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.traceInfo_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.traceInfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x20;
                return this;
            }
            
            public Builder clearTraceInfo() {
                if (this.traceInfoBuilder_ == null) {
                    this.traceInfo_ = RPCTraceInfoProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.traceInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFDF;
                return this;
            }
            
            public RPCTraceInfoProto.Builder getTraceInfoBuilder() {
                this.bitField0_ |= 0x20;
                this.onChanged();
                return this.getTraceInfoFieldBuilder().getBuilder();
            }
            
            @Override
            public RPCTraceInfoProtoOrBuilder getTraceInfoOrBuilder() {
                if (this.traceInfoBuilder_ != null) {
                    return this.traceInfoBuilder_.getMessageOrBuilder();
                }
                return this.traceInfo_;
            }
            
            private SingleFieldBuilder<RPCTraceInfoProto, RPCTraceInfoProto.Builder, RPCTraceInfoProtoOrBuilder> getTraceInfoFieldBuilder() {
                if (this.traceInfoBuilder_ == null) {
                    this.traceInfoBuilder_ = new SingleFieldBuilder<RPCTraceInfoProto, RPCTraceInfoProto.Builder, RPCTraceInfoProtoOrBuilder>(this.traceInfo_, this.getParentForChildren(), this.isClean());
                    this.traceInfo_ = null;
                }
                return this.traceInfoBuilder_;
            }
            
            @Override
            public boolean hasCallerContext() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public RPCCallerContextProto getCallerContext() {
                if (this.callerContextBuilder_ == null) {
                    return this.callerContext_;
                }
                return this.callerContextBuilder_.getMessage();
            }
            
            public Builder setCallerContext(final RPCCallerContextProto value) {
                if (this.callerContextBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.callerContext_ = value;
                    this.onChanged();
                }
                else {
                    this.callerContextBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder setCallerContext(final RPCCallerContextProto.Builder builderForValue) {
                if (this.callerContextBuilder_ == null) {
                    this.callerContext_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.callerContextBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder mergeCallerContext(final RPCCallerContextProto value) {
                if (this.callerContextBuilder_ == null) {
                    if ((this.bitField0_ & 0x40) == 0x40 && this.callerContext_ != RPCCallerContextProto.getDefaultInstance()) {
                        this.callerContext_ = RPCCallerContextProto.newBuilder(this.callerContext_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.callerContext_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.callerContextBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder clearCallerContext() {
                if (this.callerContextBuilder_ == null) {
                    this.callerContext_ = RPCCallerContextProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.callerContextBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            public RPCCallerContextProto.Builder getCallerContextBuilder() {
                this.bitField0_ |= 0x40;
                this.onChanged();
                return this.getCallerContextFieldBuilder().getBuilder();
            }
            
            @Override
            public RPCCallerContextProtoOrBuilder getCallerContextOrBuilder() {
                if (this.callerContextBuilder_ != null) {
                    return this.callerContextBuilder_.getMessageOrBuilder();
                }
                return this.callerContext_;
            }
            
            private SingleFieldBuilder<RPCCallerContextProto, RPCCallerContextProto.Builder, RPCCallerContextProtoOrBuilder> getCallerContextFieldBuilder() {
                if (this.callerContextBuilder_ == null) {
                    this.callerContextBuilder_ = new SingleFieldBuilder<RPCCallerContextProto, RPCCallerContextProto.Builder, RPCCallerContextProtoOrBuilder>(this.callerContext_, this.getParentForChildren(), this.isClean());
                    this.callerContext_ = null;
                }
                return this.callerContextBuilder_;
            }
        }
    }
    
    public static final class RpcResponseHeaderProto extends GeneratedMessage implements RpcResponseHeaderProtoOrBuilder
    {
        private static final RpcResponseHeaderProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RpcResponseHeaderProto> PARSER;
        private int bitField0_;
        public static final int CALLID_FIELD_NUMBER = 1;
        private int callId_;
        public static final int STATUS_FIELD_NUMBER = 2;
        private RpcStatusProto status_;
        public static final int SERVERIPCVERSIONNUM_FIELD_NUMBER = 3;
        private int serverIpcVersionNum_;
        public static final int EXCEPTIONCLASSNAME_FIELD_NUMBER = 4;
        private Object exceptionClassName_;
        public static final int ERRORMSG_FIELD_NUMBER = 5;
        private Object errorMsg_;
        public static final int ERRORDETAIL_FIELD_NUMBER = 6;
        private RpcErrorCodeProto errorDetail_;
        public static final int CLIENTID_FIELD_NUMBER = 7;
        private ByteString clientId_;
        public static final int RETRYCOUNT_FIELD_NUMBER = 8;
        private int retryCount_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RpcResponseHeaderProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RpcResponseHeaderProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RpcResponseHeaderProto getDefaultInstance() {
            return RpcResponseHeaderProto.defaultInstance;
        }
        
        @Override
        public RpcResponseHeaderProto getDefaultInstanceForType() {
            return RpcResponseHeaderProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RpcResponseHeaderProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.callId_ = input.readUInt32();
                            continue;
                        }
                        case 16: {
                            final int rawValue = input.readEnum();
                            final RpcStatusProto value = RpcStatusProto.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(2, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x2;
                            this.status_ = value;
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.serverIpcVersionNum_ = input.readUInt32();
                            continue;
                        }
                        case 34: {
                            this.bitField0_ |= 0x8;
                            this.exceptionClassName_ = input.readBytes();
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.errorMsg_ = input.readBytes();
                            continue;
                        }
                        case 48: {
                            final int rawValue = input.readEnum();
                            final RpcErrorCodeProto value2 = RpcErrorCodeProto.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(6, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x20;
                            this.errorDetail_ = value2;
                            continue;
                        }
                        case 58: {
                            this.bitField0_ |= 0x40;
                            this.clientId_ = input.readBytes();
                            continue;
                        }
                        case 64: {
                            this.bitField0_ |= 0x80;
                            this.retryCount_ = input.readSInt32();
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
            return RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcResponseHeaderProto.class, Builder.class);
        }
        
        @Override
        public Parser<RpcResponseHeaderProto> getParserForType() {
            return RpcResponseHeaderProto.PARSER;
        }
        
        @Override
        public boolean hasCallId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getCallId() {
            return this.callId_;
        }
        
        @Override
        public boolean hasStatus() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public RpcStatusProto getStatus() {
            return this.status_;
        }
        
        @Override
        public boolean hasServerIpcVersionNum() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public int getServerIpcVersionNum() {
            return this.serverIpcVersionNum_;
        }
        
        @Override
        public boolean hasExceptionClassName() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public String getExceptionClassName() {
            final Object ref = this.exceptionClassName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.exceptionClassName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getExceptionClassNameBytes() {
            final Object ref = this.exceptionClassName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.exceptionClassName_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasErrorMsg() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        @Override
        public String getErrorMsg() {
            final Object ref = this.errorMsg_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.errorMsg_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getErrorMsgBytes() {
            final Object ref = this.errorMsg_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.errorMsg_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasErrorDetail() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public RpcErrorCodeProto getErrorDetail() {
            return this.errorDetail_;
        }
        
        @Override
        public boolean hasClientId() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public ByteString getClientId() {
            return this.clientId_;
        }
        
        @Override
        public boolean hasRetryCount() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public int getRetryCount() {
            return this.retryCount_;
        }
        
        private void initFields() {
            this.callId_ = 0;
            this.status_ = RpcStatusProto.SUCCESS;
            this.serverIpcVersionNum_ = 0;
            this.exceptionClassName_ = "";
            this.errorMsg_ = "";
            this.errorDetail_ = RpcErrorCodeProto.ERROR_APPLICATION;
            this.clientId_ = ByteString.EMPTY;
            this.retryCount_ = -1;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasCallId()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasStatus()) {
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
                output.writeUInt32(1, this.callId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeEnum(2, this.status_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeUInt32(3, this.serverIpcVersionNum_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBytes(4, this.getExceptionClassNameBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getErrorMsgBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeEnum(6, this.errorDetail_.getNumber());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeBytes(7, this.clientId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeSInt32(8, this.retryCount_);
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
                size += CodedOutputStream.computeUInt32Size(1, this.callId_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeEnumSize(2, this.status_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeUInt32Size(3, this.serverIpcVersionNum_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBytesSize(4, this.getExceptionClassNameBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getErrorMsgBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeEnumSize(6, this.errorDetail_.getNumber());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeBytesSize(7, this.clientId_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeSInt32Size(8, this.retryCount_);
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
            if (!(obj instanceof RpcResponseHeaderProto)) {
                return super.equals(obj);
            }
            final RpcResponseHeaderProto other = (RpcResponseHeaderProto)obj;
            boolean result = true;
            result = (result && this.hasCallId() == other.hasCallId());
            if (this.hasCallId()) {
                result = (result && this.getCallId() == other.getCallId());
            }
            result = (result && this.hasStatus() == other.hasStatus());
            if (this.hasStatus()) {
                result = (result && this.getStatus() == other.getStatus());
            }
            result = (result && this.hasServerIpcVersionNum() == other.hasServerIpcVersionNum());
            if (this.hasServerIpcVersionNum()) {
                result = (result && this.getServerIpcVersionNum() == other.getServerIpcVersionNum());
            }
            result = (result && this.hasExceptionClassName() == other.hasExceptionClassName());
            if (this.hasExceptionClassName()) {
                result = (result && this.getExceptionClassName().equals(other.getExceptionClassName()));
            }
            result = (result && this.hasErrorMsg() == other.hasErrorMsg());
            if (this.hasErrorMsg()) {
                result = (result && this.getErrorMsg().equals(other.getErrorMsg()));
            }
            result = (result && this.hasErrorDetail() == other.hasErrorDetail());
            if (this.hasErrorDetail()) {
                result = (result && this.getErrorDetail() == other.getErrorDetail());
            }
            result = (result && this.hasClientId() == other.hasClientId());
            if (this.hasClientId()) {
                result = (result && this.getClientId().equals(other.getClientId()));
            }
            result = (result && this.hasRetryCount() == other.hasRetryCount());
            if (this.hasRetryCount()) {
                result = (result && this.getRetryCount() == other.getRetryCount());
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
            if (this.hasCallId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getCallId();
            }
            if (this.hasStatus()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getStatus());
            }
            if (this.hasServerIpcVersionNum()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getServerIpcVersionNum();
            }
            if (this.hasExceptionClassName()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getExceptionClassName().hashCode();
            }
            if (this.hasErrorMsg()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getErrorMsg().hashCode();
            }
            if (this.hasErrorDetail()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getErrorDetail());
            }
            if (this.hasClientId()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + this.getClientId().hashCode();
            }
            if (this.hasRetryCount()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + this.getRetryCount();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RpcResponseHeaderProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RpcResponseHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RpcResponseHeaderProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcResponseHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcResponseHeaderProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RpcResponseHeaderProto.PARSER.parseFrom(data);
        }
        
        public static RpcResponseHeaderProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcResponseHeaderProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcResponseHeaderProto parseFrom(final InputStream input) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RpcResponseHeaderProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RpcResponseHeaderProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RpcResponseHeaderProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RpcResponseHeaderProto parseFrom(final CodedInputStream input) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseFrom(input);
        }
        
        public static RpcResponseHeaderProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcResponseHeaderProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RpcResponseHeaderProto prototype) {
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
            RpcResponseHeaderProto.PARSER = new AbstractParser<RpcResponseHeaderProto>() {
                @Override
                public RpcResponseHeaderProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RpcResponseHeaderProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RpcResponseHeaderProto(true)).initFields();
        }
        
        public enum RpcStatusProto implements ProtocolMessageEnum
        {
            SUCCESS(0, 0), 
            ERROR(1, 1), 
            FATAL(2, 2);
            
            public static final int SUCCESS_VALUE = 0;
            public static final int ERROR_VALUE = 1;
            public static final int FATAL_VALUE = 2;
            private static Internal.EnumLiteMap<RpcStatusProto> internalValueMap;
            private static final RpcStatusProto[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static RpcStatusProto valueOf(final int value) {
                switch (value) {
                    case 0: {
                        return RpcStatusProto.SUCCESS;
                    }
                    case 1: {
                        return RpcStatusProto.ERROR;
                    }
                    case 2: {
                        return RpcStatusProto.FATAL;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<RpcStatusProto> internalGetValueMap() {
                return RpcStatusProto.internalValueMap;
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
                return RpcResponseHeaderProto.getDescriptor().getEnumTypes().get(0);
            }
            
            public static RpcStatusProto valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return RpcStatusProto.VALUES[desc.getIndex()];
            }
            
            private RpcStatusProto(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                RpcStatusProto.internalValueMap = new Internal.EnumLiteMap<RpcStatusProto>() {
                    @Override
                    public RpcStatusProto findValueByNumber(final int number) {
                        return RpcStatusProto.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public enum RpcErrorCodeProto implements ProtocolMessageEnum
        {
            ERROR_APPLICATION(0, 1), 
            ERROR_NO_SUCH_METHOD(1, 2), 
            ERROR_NO_SUCH_PROTOCOL(2, 3), 
            ERROR_RPC_SERVER(3, 4), 
            ERROR_SERIALIZING_RESPONSE(4, 5), 
            ERROR_RPC_VERSION_MISMATCH(5, 6), 
            FATAL_UNKNOWN(6, 10), 
            FATAL_UNSUPPORTED_SERIALIZATION(7, 11), 
            FATAL_INVALID_RPC_HEADER(8, 12), 
            FATAL_DESERIALIZING_REQUEST(9, 13), 
            FATAL_VERSION_MISMATCH(10, 14), 
            FATAL_UNAUTHORIZED(11, 15);
            
            public static final int ERROR_APPLICATION_VALUE = 1;
            public static final int ERROR_NO_SUCH_METHOD_VALUE = 2;
            public static final int ERROR_NO_SUCH_PROTOCOL_VALUE = 3;
            public static final int ERROR_RPC_SERVER_VALUE = 4;
            public static final int ERROR_SERIALIZING_RESPONSE_VALUE = 5;
            public static final int ERROR_RPC_VERSION_MISMATCH_VALUE = 6;
            public static final int FATAL_UNKNOWN_VALUE = 10;
            public static final int FATAL_UNSUPPORTED_SERIALIZATION_VALUE = 11;
            public static final int FATAL_INVALID_RPC_HEADER_VALUE = 12;
            public static final int FATAL_DESERIALIZING_REQUEST_VALUE = 13;
            public static final int FATAL_VERSION_MISMATCH_VALUE = 14;
            public static final int FATAL_UNAUTHORIZED_VALUE = 15;
            private static Internal.EnumLiteMap<RpcErrorCodeProto> internalValueMap;
            private static final RpcErrorCodeProto[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static RpcErrorCodeProto valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return RpcErrorCodeProto.ERROR_APPLICATION;
                    }
                    case 2: {
                        return RpcErrorCodeProto.ERROR_NO_SUCH_METHOD;
                    }
                    case 3: {
                        return RpcErrorCodeProto.ERROR_NO_SUCH_PROTOCOL;
                    }
                    case 4: {
                        return RpcErrorCodeProto.ERROR_RPC_SERVER;
                    }
                    case 5: {
                        return RpcErrorCodeProto.ERROR_SERIALIZING_RESPONSE;
                    }
                    case 6: {
                        return RpcErrorCodeProto.ERROR_RPC_VERSION_MISMATCH;
                    }
                    case 10: {
                        return RpcErrorCodeProto.FATAL_UNKNOWN;
                    }
                    case 11: {
                        return RpcErrorCodeProto.FATAL_UNSUPPORTED_SERIALIZATION;
                    }
                    case 12: {
                        return RpcErrorCodeProto.FATAL_INVALID_RPC_HEADER;
                    }
                    case 13: {
                        return RpcErrorCodeProto.FATAL_DESERIALIZING_REQUEST;
                    }
                    case 14: {
                        return RpcErrorCodeProto.FATAL_VERSION_MISMATCH;
                    }
                    case 15: {
                        return RpcErrorCodeProto.FATAL_UNAUTHORIZED;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<RpcErrorCodeProto> internalGetValueMap() {
                return RpcErrorCodeProto.internalValueMap;
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
                return RpcResponseHeaderProto.getDescriptor().getEnumTypes().get(1);
            }
            
            public static RpcErrorCodeProto valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return RpcErrorCodeProto.VALUES[desc.getIndex()];
            }
            
            private RpcErrorCodeProto(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                RpcErrorCodeProto.internalValueMap = new Internal.EnumLiteMap<RpcErrorCodeProto>() {
                    @Override
                    public RpcErrorCodeProto findValueByNumber(final int number) {
                        return RpcErrorCodeProto.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RpcResponseHeaderProtoOrBuilder
        {
            private int bitField0_;
            private int callId_;
            private RpcStatusProto status_;
            private int serverIpcVersionNum_;
            private Object exceptionClassName_;
            private Object errorMsg_;
            private RpcErrorCodeProto errorDetail_;
            private ByteString clientId_;
            private int retryCount_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcResponseHeaderProto.class, Builder.class);
            }
            
            private Builder() {
                this.status_ = RpcStatusProto.SUCCESS;
                this.exceptionClassName_ = "";
                this.errorMsg_ = "";
                this.errorDetail_ = RpcErrorCodeProto.ERROR_APPLICATION;
                this.clientId_ = ByteString.EMPTY;
                this.retryCount_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.status_ = RpcStatusProto.SUCCESS;
                this.exceptionClassName_ = "";
                this.errorMsg_ = "";
                this.errorDetail_ = RpcErrorCodeProto.ERROR_APPLICATION;
                this.clientId_ = ByteString.EMPTY;
                this.retryCount_ = -1;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RpcResponseHeaderProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.callId_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.status_ = RpcStatusProto.SUCCESS;
                this.bitField0_ &= 0xFFFFFFFD;
                this.serverIpcVersionNum_ = 0;
                this.bitField0_ &= 0xFFFFFFFB;
                this.exceptionClassName_ = "";
                this.bitField0_ &= 0xFFFFFFF7;
                this.errorMsg_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.errorDetail_ = RpcErrorCodeProto.ERROR_APPLICATION;
                this.bitField0_ &= 0xFFFFFFDF;
                this.clientId_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFBF;
                this.retryCount_ = -1;
                this.bitField0_ &= 0xFFFFFF7F;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcResponseHeaderProto_descriptor;
            }
            
            @Override
            public RpcResponseHeaderProto getDefaultInstanceForType() {
                return RpcResponseHeaderProto.getDefaultInstance();
            }
            
            @Override
            public RpcResponseHeaderProto build() {
                final RpcResponseHeaderProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RpcResponseHeaderProto buildPartial() {
                final RpcResponseHeaderProto result = new RpcResponseHeaderProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.callId_ = this.callId_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.status_ = this.status_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.serverIpcVersionNum_ = this.serverIpcVersionNum_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.exceptionClassName_ = this.exceptionClassName_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.errorMsg_ = this.errorMsg_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.errorDetail_ = this.errorDetail_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.clientId_ = this.clientId_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.retryCount_ = this.retryCount_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RpcResponseHeaderProto) {
                    return this.mergeFrom((RpcResponseHeaderProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RpcResponseHeaderProto other) {
                if (other == RpcResponseHeaderProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCallId()) {
                    this.setCallId(other.getCallId());
                }
                if (other.hasStatus()) {
                    this.setStatus(other.getStatus());
                }
                if (other.hasServerIpcVersionNum()) {
                    this.setServerIpcVersionNum(other.getServerIpcVersionNum());
                }
                if (other.hasExceptionClassName()) {
                    this.bitField0_ |= 0x8;
                    this.exceptionClassName_ = other.exceptionClassName_;
                    this.onChanged();
                }
                if (other.hasErrorMsg()) {
                    this.bitField0_ |= 0x10;
                    this.errorMsg_ = other.errorMsg_;
                    this.onChanged();
                }
                if (other.hasErrorDetail()) {
                    this.setErrorDetail(other.getErrorDetail());
                }
                if (other.hasClientId()) {
                    this.setClientId(other.getClientId());
                }
                if (other.hasRetryCount()) {
                    this.setRetryCount(other.getRetryCount());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasCallId() && this.hasStatus();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RpcResponseHeaderProto parsedMessage = null;
                try {
                    parsedMessage = RpcResponseHeaderProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RpcResponseHeaderProto)e.getUnfinishedMessage();
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
            public boolean hasCallId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getCallId() {
                return this.callId_;
            }
            
            public Builder setCallId(final int value) {
                this.bitField0_ |= 0x1;
                this.callId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCallId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.callId_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasStatus() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public RpcStatusProto getStatus() {
                return this.status_;
            }
            
            public Builder setStatus(final RpcStatusProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.status_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStatus() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.status_ = RpcStatusProto.SUCCESS;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasServerIpcVersionNum() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public int getServerIpcVersionNum() {
                return this.serverIpcVersionNum_;
            }
            
            public Builder setServerIpcVersionNum(final int value) {
                this.bitField0_ |= 0x4;
                this.serverIpcVersionNum_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearServerIpcVersionNum() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.serverIpcVersionNum_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasExceptionClassName() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public String getExceptionClassName() {
                final Object ref = this.exceptionClassName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.exceptionClassName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getExceptionClassNameBytes() {
                final Object ref = this.exceptionClassName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.exceptionClassName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setExceptionClassName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.exceptionClassName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearExceptionClassName() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.exceptionClassName_ = RpcResponseHeaderProto.getDefaultInstance().getExceptionClassName();
                this.onChanged();
                return this;
            }
            
            public Builder setExceptionClassNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.exceptionClassName_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasErrorMsg() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public String getErrorMsg() {
                final Object ref = this.errorMsg_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.errorMsg_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getErrorMsgBytes() {
                final Object ref = this.errorMsg_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.errorMsg_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setErrorMsg(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.errorMsg_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearErrorMsg() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.errorMsg_ = RpcResponseHeaderProto.getDefaultInstance().getErrorMsg();
                this.onChanged();
                return this;
            }
            
            public Builder setErrorMsgBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.errorMsg_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasErrorDetail() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public RpcErrorCodeProto getErrorDetail() {
                return this.errorDetail_;
            }
            
            public Builder setErrorDetail(final RpcErrorCodeProto value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.errorDetail_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearErrorDetail() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.errorDetail_ = RpcErrorCodeProto.ERROR_APPLICATION;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasClientId() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public ByteString getClientId() {
                return this.clientId_;
            }
            
            public Builder setClientId(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.clientId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClientId() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.clientId_ = RpcResponseHeaderProto.getDefaultInstance().getClientId();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRetryCount() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public int getRetryCount() {
                return this.retryCount_;
            }
            
            public Builder setRetryCount(final int value) {
                this.bitField0_ |= 0x80;
                this.retryCount_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRetryCount() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.retryCount_ = -1;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RpcSaslProto extends GeneratedMessage implements RpcSaslProtoOrBuilder
    {
        private static final RpcSaslProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RpcSaslProto> PARSER;
        private int bitField0_;
        public static final int VERSION_FIELD_NUMBER = 1;
        private int version_;
        public static final int STATE_FIELD_NUMBER = 2;
        private SaslState state_;
        public static final int TOKEN_FIELD_NUMBER = 3;
        private ByteString token_;
        public static final int AUTHS_FIELD_NUMBER = 4;
        private List<SaslAuth> auths_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RpcSaslProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RpcSaslProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RpcSaslProto getDefaultInstance() {
            return RpcSaslProto.defaultInstance;
        }
        
        @Override
        public RpcSaslProto getDefaultInstanceForType() {
            return RpcSaslProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RpcSaslProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.version_ = input.readUInt32();
                            continue;
                        }
                        case 16: {
                            final int rawValue = input.readEnum();
                            final SaslState value = SaslState.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(2, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x2;
                            this.state_ = value;
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.token_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            if ((mutable_bitField0_ & 0x8) != 0x8) {
                                this.auths_ = new ArrayList<SaslAuth>();
                                mutable_bitField0_ |= 0x8;
                            }
                            this.auths_.add(input.readMessage(SaslAuth.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x8) == 0x8) {
                    this.auths_ = Collections.unmodifiableList((List<? extends SaslAuth>)this.auths_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcSaslProto.class, Builder.class);
        }
        
        @Override
        public Parser<RpcSaslProto> getParserForType() {
            return RpcSaslProto.PARSER;
        }
        
        @Override
        public boolean hasVersion() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getVersion() {
            return this.version_;
        }
        
        @Override
        public boolean hasState() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public SaslState getState() {
            return this.state_;
        }
        
        @Override
        public boolean hasToken() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public ByteString getToken() {
            return this.token_;
        }
        
        @Override
        public List<SaslAuth> getAuthsList() {
            return this.auths_;
        }
        
        @Override
        public List<? extends SaslAuthOrBuilder> getAuthsOrBuilderList() {
            return this.auths_;
        }
        
        @Override
        public int getAuthsCount() {
            return this.auths_.size();
        }
        
        @Override
        public SaslAuth getAuths(final int index) {
            return this.auths_.get(index);
        }
        
        @Override
        public SaslAuthOrBuilder getAuthsOrBuilder(final int index) {
            return this.auths_.get(index);
        }
        
        private void initFields() {
            this.version_ = 0;
            this.state_ = SaslState.SUCCESS;
            this.token_ = ByteString.EMPTY;
            this.auths_ = Collections.emptyList();
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
            for (int i = 0; i < this.getAuthsCount(); ++i) {
                if (!this.getAuths(i).isInitialized()) {
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
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeUInt32(1, this.version_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeEnum(2, this.state_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.token_);
            }
            for (int i = 0; i < this.auths_.size(); ++i) {
                output.writeMessage(4, this.auths_.get(i));
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
                size += CodedOutputStream.computeUInt32Size(1, this.version_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeEnumSize(2, this.state_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.token_);
            }
            for (int i = 0; i < this.auths_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(4, this.auths_.get(i));
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
            if (!(obj instanceof RpcSaslProto)) {
                return super.equals(obj);
            }
            final RpcSaslProto other = (RpcSaslProto)obj;
            boolean result = true;
            result = (result && this.hasVersion() == other.hasVersion());
            if (this.hasVersion()) {
                result = (result && this.getVersion() == other.getVersion());
            }
            result = (result && this.hasState() == other.hasState());
            if (this.hasState()) {
                result = (result && this.getState() == other.getState());
            }
            result = (result && this.hasToken() == other.hasToken());
            if (this.hasToken()) {
                result = (result && this.getToken().equals(other.getToken()));
            }
            result = (result && this.getAuthsList().equals(other.getAuthsList()));
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
            if (this.hasVersion()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getVersion();
            }
            if (this.hasState()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getState());
            }
            if (this.hasToken()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getToken().hashCode();
            }
            if (this.getAuthsCount() > 0) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getAuthsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RpcSaslProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RpcSaslProto.PARSER.parseFrom(data);
        }
        
        public static RpcSaslProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcSaslProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcSaslProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RpcSaslProto.PARSER.parseFrom(data);
        }
        
        public static RpcSaslProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RpcSaslProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RpcSaslProto parseFrom(final InputStream input) throws IOException {
            return RpcSaslProto.PARSER.parseFrom(input);
        }
        
        public static RpcSaslProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcSaslProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RpcSaslProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RpcSaslProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RpcSaslProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcSaslProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RpcSaslProto parseFrom(final CodedInputStream input) throws IOException {
            return RpcSaslProto.PARSER.parseFrom(input);
        }
        
        public static RpcSaslProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RpcSaslProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RpcSaslProto prototype) {
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
            RpcSaslProto.PARSER = new AbstractParser<RpcSaslProto>() {
                @Override
                public RpcSaslProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RpcSaslProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RpcSaslProto(true)).initFields();
        }
        
        public enum SaslState implements ProtocolMessageEnum
        {
            SUCCESS(0, 0), 
            NEGOTIATE(1, 1), 
            INITIATE(2, 2), 
            CHALLENGE(3, 3), 
            RESPONSE(4, 4), 
            WRAP(5, 5);
            
            public static final int SUCCESS_VALUE = 0;
            public static final int NEGOTIATE_VALUE = 1;
            public static final int INITIATE_VALUE = 2;
            public static final int CHALLENGE_VALUE = 3;
            public static final int RESPONSE_VALUE = 4;
            public static final int WRAP_VALUE = 5;
            private static Internal.EnumLiteMap<SaslState> internalValueMap;
            private static final SaslState[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static SaslState valueOf(final int value) {
                switch (value) {
                    case 0: {
                        return SaslState.SUCCESS;
                    }
                    case 1: {
                        return SaslState.NEGOTIATE;
                    }
                    case 2: {
                        return SaslState.INITIATE;
                    }
                    case 3: {
                        return SaslState.CHALLENGE;
                    }
                    case 4: {
                        return SaslState.RESPONSE;
                    }
                    case 5: {
                        return SaslState.WRAP;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<SaslState> internalGetValueMap() {
                return SaslState.internalValueMap;
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
                return RpcSaslProto.getDescriptor().getEnumTypes().get(0);
            }
            
            public static SaslState valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return SaslState.VALUES[desc.getIndex()];
            }
            
            private SaslState(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                SaslState.internalValueMap = new Internal.EnumLiteMap<SaslState>() {
                    @Override
                    public SaslState findValueByNumber(final int number) {
                        return SaslState.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class SaslAuth extends GeneratedMessage implements SaslAuthOrBuilder
        {
            private static final SaslAuth defaultInstance;
            private final UnknownFieldSet unknownFields;
            public static Parser<SaslAuth> PARSER;
            private int bitField0_;
            public static final int METHOD_FIELD_NUMBER = 1;
            private Object method_;
            public static final int MECHANISM_FIELD_NUMBER = 2;
            private Object mechanism_;
            public static final int PROTOCOL_FIELD_NUMBER = 3;
            private Object protocol_;
            public static final int SERVERID_FIELD_NUMBER = 4;
            private Object serverId_;
            public static final int CHALLENGE_FIELD_NUMBER = 5;
            private ByteString challenge_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            private static final long serialVersionUID = 0L;
            private int memoizedHashCode;
            
            private SaslAuth(final GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.memoizedHashCode = 0;
                this.unknownFields = builder.getUnknownFields();
            }
            
            private SaslAuth(final boolean noInit) {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.memoizedHashCode = 0;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }
            
            public static SaslAuth getDefaultInstance() {
                return SaslAuth.defaultInstance;
            }
            
            @Override
            public SaslAuth getDefaultInstanceForType() {
                return SaslAuth.defaultInstance;
            }
            
            @Override
            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }
            
            private SaslAuth(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.method_ = input.readBytes();
                                continue;
                            }
                            case 18: {
                                this.bitField0_ |= 0x2;
                                this.mechanism_ = input.readBytes();
                                continue;
                            }
                            case 26: {
                                this.bitField0_ |= 0x4;
                                this.protocol_ = input.readBytes();
                                continue;
                            }
                            case 34: {
                                this.bitField0_ |= 0x8;
                                this.serverId_ = input.readBytes();
                                continue;
                            }
                            case 42: {
                                this.bitField0_ |= 0x10;
                                this.challenge_ = input.readBytes();
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
                return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_fieldAccessorTable.ensureFieldAccessorsInitialized(SaslAuth.class, Builder.class);
            }
            
            @Override
            public Parser<SaslAuth> getParserForType() {
                return SaslAuth.PARSER;
            }
            
            @Override
            public boolean hasMethod() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getMethod() {
                final Object ref = this.method_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.method_ = s;
                }
                return s;
            }
            
            @Override
            public ByteString getMethodBytes() {
                final Object ref = this.method_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.method_ = b);
                }
                return (ByteString)ref;
            }
            
            @Override
            public boolean hasMechanism() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getMechanism() {
                final Object ref = this.mechanism_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.mechanism_ = s;
                }
                return s;
            }
            
            @Override
            public ByteString getMechanismBytes() {
                final Object ref = this.mechanism_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.mechanism_ = b);
                }
                return (ByteString)ref;
            }
            
            @Override
            public boolean hasProtocol() {
                return (this.bitField0_ & 0x4) == 0x4;
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
            
            @Override
            public boolean hasServerId() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public String getServerId() {
                final Object ref = this.serverId_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.serverId_ = s;
                }
                return s;
            }
            
            @Override
            public ByteString getServerIdBytes() {
                final Object ref = this.serverId_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.serverId_ = b);
                }
                return (ByteString)ref;
            }
            
            @Override
            public boolean hasChallenge() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            @Override
            public ByteString getChallenge() {
                return this.challenge_;
            }
            
            private void initFields() {
                this.method_ = "";
                this.mechanism_ = "";
                this.protocol_ = "";
                this.serverId_ = "";
                this.challenge_ = ByteString.EMPTY;
            }
            
            @Override
            public final boolean isInitialized() {
                final byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                if (!this.hasMethod()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!this.hasMechanism()) {
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
                    output.writeBytes(1, this.getMethodBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    output.writeBytes(2, this.getMechanismBytes());
                }
                if ((this.bitField0_ & 0x4) == 0x4) {
                    output.writeBytes(3, this.getProtocolBytes());
                }
                if ((this.bitField0_ & 0x8) == 0x8) {
                    output.writeBytes(4, this.getServerIdBytes());
                }
                if ((this.bitField0_ & 0x10) == 0x10) {
                    output.writeBytes(5, this.challenge_);
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
                    size += CodedOutputStream.computeBytesSize(1, this.getMethodBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    size += CodedOutputStream.computeBytesSize(2, this.getMechanismBytes());
                }
                if ((this.bitField0_ & 0x4) == 0x4) {
                    size += CodedOutputStream.computeBytesSize(3, this.getProtocolBytes());
                }
                if ((this.bitField0_ & 0x8) == 0x8) {
                    size += CodedOutputStream.computeBytesSize(4, this.getServerIdBytes());
                }
                if ((this.bitField0_ & 0x10) == 0x10) {
                    size += CodedOutputStream.computeBytesSize(5, this.challenge_);
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
                if (!(obj instanceof SaslAuth)) {
                    return super.equals(obj);
                }
                final SaslAuth other = (SaslAuth)obj;
                boolean result = true;
                result = (result && this.hasMethod() == other.hasMethod());
                if (this.hasMethod()) {
                    result = (result && this.getMethod().equals(other.getMethod()));
                }
                result = (result && this.hasMechanism() == other.hasMechanism());
                if (this.hasMechanism()) {
                    result = (result && this.getMechanism().equals(other.getMechanism()));
                }
                result = (result && this.hasProtocol() == other.hasProtocol());
                if (this.hasProtocol()) {
                    result = (result && this.getProtocol().equals(other.getProtocol()));
                }
                result = (result && this.hasServerId() == other.hasServerId());
                if (this.hasServerId()) {
                    result = (result && this.getServerId().equals(other.getServerId()));
                }
                result = (result && this.hasChallenge() == other.hasChallenge());
                if (this.hasChallenge()) {
                    result = (result && this.getChallenge().equals(other.getChallenge()));
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
                if (this.hasMethod()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + this.getMethod().hashCode();
                }
                if (this.hasMechanism()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + this.getMechanism().hashCode();
                }
                if (this.hasProtocol()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + this.getProtocol().hashCode();
                }
                if (this.hasServerId()) {
                    hash = 37 * hash + 4;
                    hash = 53 * hash + this.getServerId().hashCode();
                }
                if (this.hasChallenge()) {
                    hash = 37 * hash + 5;
                    hash = 53 * hash + this.getChallenge().hashCode();
                }
                hash = 29 * hash + this.getUnknownFields().hashCode();
                return this.memoizedHashCode = hash;
            }
            
            public static SaslAuth parseFrom(final ByteString data) throws InvalidProtocolBufferException {
                return SaslAuth.PARSER.parseFrom(data);
            }
            
            public static SaslAuth parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return SaslAuth.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static SaslAuth parseFrom(final byte[] data) throws InvalidProtocolBufferException {
                return SaslAuth.PARSER.parseFrom(data);
            }
            
            public static SaslAuth parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return SaslAuth.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static SaslAuth parseFrom(final InputStream input) throws IOException {
                return SaslAuth.PARSER.parseFrom(input);
            }
            
            public static SaslAuth parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return SaslAuth.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static SaslAuth parseDelimitedFrom(final InputStream input) throws IOException {
                return SaslAuth.PARSER.parseDelimitedFrom(input);
            }
            
            public static SaslAuth parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return SaslAuth.PARSER.parseDelimitedFrom(input, extensionRegistry);
            }
            
            public static SaslAuth parseFrom(final CodedInputStream input) throws IOException {
                return SaslAuth.PARSER.parseFrom(input);
            }
            
            public static SaslAuth parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return SaslAuth.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static Builder newBuilder() {
                return create();
            }
            
            @Override
            public Builder newBuilderForType() {
                return newBuilder();
            }
            
            public static Builder newBuilder(final SaslAuth prototype) {
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
                SaslAuth.PARSER = new AbstractParser<SaslAuth>() {
                    @Override
                    public SaslAuth parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new SaslAuth(input, extensionRegistry);
                    }
                };
                (defaultInstance = new SaslAuth(true)).initFields();
            }
            
            public static final class Builder extends GeneratedMessage.Builder<Builder> implements SaslAuthOrBuilder
            {
                private int bitField0_;
                private Object method_;
                private Object mechanism_;
                private Object protocol_;
                private Object serverId_;
                private ByteString challenge_;
                
                public static final Descriptors.Descriptor getDescriptor() {
                    return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor;
                }
                
                @Override
                protected FieldAccessorTable internalGetFieldAccessorTable() {
                    return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_fieldAccessorTable.ensureFieldAccessorsInitialized(SaslAuth.class, Builder.class);
                }
                
                private Builder() {
                    this.method_ = "";
                    this.mechanism_ = "";
                    this.protocol_ = "";
                    this.serverId_ = "";
                    this.challenge_ = ByteString.EMPTY;
                    this.maybeForceBuilderInitialization();
                }
                
                private Builder(final BuilderParent parent) {
                    super(parent);
                    this.method_ = "";
                    this.mechanism_ = "";
                    this.protocol_ = "";
                    this.serverId_ = "";
                    this.challenge_ = ByteString.EMPTY;
                    this.maybeForceBuilderInitialization();
                }
                
                private void maybeForceBuilderInitialization() {
                    if (SaslAuth.alwaysUseFieldBuilders) {}
                }
                
                private static Builder create() {
                    return new Builder();
                }
                
                @Override
                public Builder clear() {
                    super.clear();
                    this.method_ = "";
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.mechanism_ = "";
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.protocol_ = "";
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.serverId_ = "";
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.challenge_ = ByteString.EMPTY;
                    this.bitField0_ &= 0xFFFFFFEF;
                    return this;
                }
                
                @Override
                public Builder clone() {
                    return create().mergeFrom(this.buildPartial());
                }
                
                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_SaslAuth_descriptor;
                }
                
                @Override
                public SaslAuth getDefaultInstanceForType() {
                    return SaslAuth.getDefaultInstance();
                }
                
                @Override
                public SaslAuth build() {
                    final SaslAuth result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw AbstractMessage.Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }
                
                @Override
                public SaslAuth buildPartial() {
                    final SaslAuth result = new SaslAuth((GeneratedMessage.Builder)this);
                    final int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 0x1) {
                        to_bitField0_ |= 0x1;
                    }
                    result.method_ = this.method_;
                    if ((from_bitField0_ & 0x2) == 0x2) {
                        to_bitField0_ |= 0x2;
                    }
                    result.mechanism_ = this.mechanism_;
                    if ((from_bitField0_ & 0x4) == 0x4) {
                        to_bitField0_ |= 0x4;
                    }
                    result.protocol_ = this.protocol_;
                    if ((from_bitField0_ & 0x8) == 0x8) {
                        to_bitField0_ |= 0x8;
                    }
                    result.serverId_ = this.serverId_;
                    if ((from_bitField0_ & 0x10) == 0x10) {
                        to_bitField0_ |= 0x10;
                    }
                    result.challenge_ = this.challenge_;
                    result.bitField0_ = to_bitField0_;
                    this.onBuilt();
                    return result;
                }
                
                @Override
                public Builder mergeFrom(final Message other) {
                    if (other instanceof SaslAuth) {
                        return this.mergeFrom((SaslAuth)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }
                
                public Builder mergeFrom(final SaslAuth other) {
                    if (other == SaslAuth.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasMethod()) {
                        this.bitField0_ |= 0x1;
                        this.method_ = other.method_;
                        this.onChanged();
                    }
                    if (other.hasMechanism()) {
                        this.bitField0_ |= 0x2;
                        this.mechanism_ = other.mechanism_;
                        this.onChanged();
                    }
                    if (other.hasProtocol()) {
                        this.bitField0_ |= 0x4;
                        this.protocol_ = other.protocol_;
                        this.onChanged();
                    }
                    if (other.hasServerId()) {
                        this.bitField0_ |= 0x8;
                        this.serverId_ = other.serverId_;
                        this.onChanged();
                    }
                    if (other.hasChallenge()) {
                        this.setChallenge(other.getChallenge());
                    }
                    this.mergeUnknownFields(other.getUnknownFields());
                    return this;
                }
                
                @Override
                public final boolean isInitialized() {
                    return this.hasMethod() && this.hasMechanism();
                }
                
                @Override
                public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                    SaslAuth parsedMessage = null;
                    try {
                        parsedMessage = SaslAuth.PARSER.parsePartialFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        parsedMessage = (SaslAuth)e.getUnfinishedMessage();
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
                public boolean hasMethod() {
                    return (this.bitField0_ & 0x1) == 0x1;
                }
                
                @Override
                public String getMethod() {
                    final Object ref = this.method_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.method_ = s);
                    }
                    return (String)ref;
                }
                
                @Override
                public ByteString getMethodBytes() {
                    final Object ref = this.method_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.method_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setMethod(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x1;
                    this.method_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearMethod() {
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.method_ = SaslAuth.getDefaultInstance().getMethod();
                    this.onChanged();
                    return this;
                }
                
                public Builder setMethodBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x1;
                    this.method_ = value;
                    this.onChanged();
                    return this;
                }
                
                @Override
                public boolean hasMechanism() {
                    return (this.bitField0_ & 0x2) == 0x2;
                }
                
                @Override
                public String getMechanism() {
                    final Object ref = this.mechanism_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.mechanism_ = s);
                    }
                    return (String)ref;
                }
                
                @Override
                public ByteString getMechanismBytes() {
                    final Object ref = this.mechanism_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.mechanism_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setMechanism(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x2;
                    this.mechanism_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearMechanism() {
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.mechanism_ = SaslAuth.getDefaultInstance().getMechanism();
                    this.onChanged();
                    return this;
                }
                
                public Builder setMechanismBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x2;
                    this.mechanism_ = value;
                    this.onChanged();
                    return this;
                }
                
                @Override
                public boolean hasProtocol() {
                    return (this.bitField0_ & 0x4) == 0x4;
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
                    this.bitField0_ |= 0x4;
                    this.protocol_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearProtocol() {
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.protocol_ = SaslAuth.getDefaultInstance().getProtocol();
                    this.onChanged();
                    return this;
                }
                
                public Builder setProtocolBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x4;
                    this.protocol_ = value;
                    this.onChanged();
                    return this;
                }
                
                @Override
                public boolean hasServerId() {
                    return (this.bitField0_ & 0x8) == 0x8;
                }
                
                @Override
                public String getServerId() {
                    final Object ref = this.serverId_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.serverId_ = s);
                    }
                    return (String)ref;
                }
                
                @Override
                public ByteString getServerIdBytes() {
                    final Object ref = this.serverId_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.serverId_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setServerId(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x8;
                    this.serverId_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearServerId() {
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.serverId_ = SaslAuth.getDefaultInstance().getServerId();
                    this.onChanged();
                    return this;
                }
                
                public Builder setServerIdBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x8;
                    this.serverId_ = value;
                    this.onChanged();
                    return this;
                }
                
                @Override
                public boolean hasChallenge() {
                    return (this.bitField0_ & 0x10) == 0x10;
                }
                
                @Override
                public ByteString getChallenge() {
                    return this.challenge_;
                }
                
                public Builder setChallenge(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x10;
                    this.challenge_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearChallenge() {
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.challenge_ = SaslAuth.getDefaultInstance().getChallenge();
                    this.onChanged();
                    return this;
                }
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RpcSaslProtoOrBuilder
        {
            private int bitField0_;
            private int version_;
            private SaslState state_;
            private ByteString token_;
            private List<SaslAuth> auths_;
            private RepeatedFieldBuilder<SaslAuth, SaslAuth.Builder, SaslAuthOrBuilder> authsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RpcSaslProto.class, Builder.class);
            }
            
            private Builder() {
                this.state_ = SaslState.SUCCESS;
                this.token_ = ByteString.EMPTY;
                this.auths_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.state_ = SaslState.SUCCESS;
                this.token_ = ByteString.EMPTY;
                this.auths_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RpcSaslProto.alwaysUseFieldBuilders) {
                    this.getAuthsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.version_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.state_ = SaslState.SUCCESS;
                this.bitField0_ &= 0xFFFFFFFD;
                this.token_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.authsBuilder_ == null) {
                    this.auths_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFF7;
                }
                else {
                    this.authsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return RpcHeaderProtos.internal_static_hadoop_common_RpcSaslProto_descriptor;
            }
            
            @Override
            public RpcSaslProto getDefaultInstanceForType() {
                return RpcSaslProto.getDefaultInstance();
            }
            
            @Override
            public RpcSaslProto build() {
                final RpcSaslProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RpcSaslProto buildPartial() {
                final RpcSaslProto result = new RpcSaslProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.version_ = this.version_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.state_ = this.state_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.token_ = this.token_;
                if (this.authsBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8) {
                        this.auths_ = Collections.unmodifiableList((List<? extends SaslAuth>)this.auths_);
                        this.bitField0_ &= 0xFFFFFFF7;
                    }
                    result.auths_ = this.auths_;
                }
                else {
                    result.auths_ = this.authsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RpcSaslProto) {
                    return this.mergeFrom((RpcSaslProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RpcSaslProto other) {
                if (other == RpcSaslProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasVersion()) {
                    this.setVersion(other.getVersion());
                }
                if (other.hasState()) {
                    this.setState(other.getState());
                }
                if (other.hasToken()) {
                    this.setToken(other.getToken());
                }
                if (this.authsBuilder_ == null) {
                    if (!other.auths_.isEmpty()) {
                        if (this.auths_.isEmpty()) {
                            this.auths_ = other.auths_;
                            this.bitField0_ &= 0xFFFFFFF7;
                        }
                        else {
                            this.ensureAuthsIsMutable();
                            this.auths_.addAll(other.auths_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.auths_.isEmpty()) {
                    if (this.authsBuilder_.isEmpty()) {
                        this.authsBuilder_.dispose();
                        this.authsBuilder_ = null;
                        this.auths_ = other.auths_;
                        this.bitField0_ &= 0xFFFFFFF7;
                        this.authsBuilder_ = (RpcSaslProto.alwaysUseFieldBuilders ? this.getAuthsFieldBuilder() : null);
                    }
                    else {
                        this.authsBuilder_.addAllMessages(other.auths_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                if (!this.hasState()) {
                    return false;
                }
                for (int i = 0; i < this.getAuthsCount(); ++i) {
                    if (!this.getAuths(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RpcSaslProto parsedMessage = null;
                try {
                    parsedMessage = RpcSaslProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RpcSaslProto)e.getUnfinishedMessage();
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
            public boolean hasVersion() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getVersion() {
                return this.version_;
            }
            
            public Builder setVersion(final int value) {
                this.bitField0_ |= 0x1;
                this.version_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearVersion() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.version_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasState() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public SaslState getState() {
                return this.state_;
            }
            
            public Builder setState(final SaslState value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.state_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearState() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.state_ = SaslState.SUCCESS;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasToken() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public ByteString getToken() {
                return this.token_;
            }
            
            public Builder setToken(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.token_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearToken() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.token_ = RpcSaslProto.getDefaultInstance().getToken();
                this.onChanged();
                return this;
            }
            
            private void ensureAuthsIsMutable() {
                if ((this.bitField0_ & 0x8) != 0x8) {
                    this.auths_ = new ArrayList<SaslAuth>(this.auths_);
                    this.bitField0_ |= 0x8;
                }
            }
            
            @Override
            public List<SaslAuth> getAuthsList() {
                if (this.authsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends SaslAuth>)this.auths_);
                }
                return this.authsBuilder_.getMessageList();
            }
            
            @Override
            public int getAuthsCount() {
                if (this.authsBuilder_ == null) {
                    return this.auths_.size();
                }
                return this.authsBuilder_.getCount();
            }
            
            @Override
            public SaslAuth getAuths(final int index) {
                if (this.authsBuilder_ == null) {
                    return this.auths_.get(index);
                }
                return this.authsBuilder_.getMessage(index);
            }
            
            public Builder setAuths(final int index, final SaslAuth value) {
                if (this.authsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureAuthsIsMutable();
                    this.auths_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setAuths(final int index, final SaslAuth.Builder builderForValue) {
                if (this.authsBuilder_ == null) {
                    this.ensureAuthsIsMutable();
                    this.auths_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAuths(final SaslAuth value) {
                if (this.authsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureAuthsIsMutable();
                    this.auths_.add(value);
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addAuths(final int index, final SaslAuth value) {
                if (this.authsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureAuthsIsMutable();
                    this.auths_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addAuths(final SaslAuth.Builder builderForValue) {
                if (this.authsBuilder_ == null) {
                    this.ensureAuthsIsMutable();
                    this.auths_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addAuths(final int index, final SaslAuth.Builder builderForValue) {
                if (this.authsBuilder_ == null) {
                    this.ensureAuthsIsMutable();
                    this.auths_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllAuths(final Iterable<? extends SaslAuth> values) {
                if (this.authsBuilder_ == null) {
                    this.ensureAuthsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.auths_);
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearAuths() {
                if (this.authsBuilder_ == null) {
                    this.auths_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeAuths(final int index) {
                if (this.authsBuilder_ == null) {
                    this.ensureAuthsIsMutable();
                    this.auths_.remove(index);
                    this.onChanged();
                }
                else {
                    this.authsBuilder_.remove(index);
                }
                return this;
            }
            
            public SaslAuth.Builder getAuthsBuilder(final int index) {
                return this.getAuthsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public SaslAuthOrBuilder getAuthsOrBuilder(final int index) {
                if (this.authsBuilder_ == null) {
                    return this.auths_.get(index);
                }
                return this.authsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends SaslAuthOrBuilder> getAuthsOrBuilderList() {
                if (this.authsBuilder_ != null) {
                    return this.authsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends SaslAuthOrBuilder>)this.auths_);
            }
            
            public SaslAuth.Builder addAuthsBuilder() {
                return this.getAuthsFieldBuilder().addBuilder(SaslAuth.getDefaultInstance());
            }
            
            public SaslAuth.Builder addAuthsBuilder(final int index) {
                return this.getAuthsFieldBuilder().addBuilder(index, SaslAuth.getDefaultInstance());
            }
            
            public List<SaslAuth.Builder> getAuthsBuilderList() {
                return this.getAuthsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<SaslAuth, SaslAuth.Builder, SaslAuthOrBuilder> getAuthsFieldBuilder() {
                if (this.authsBuilder_ == null) {
                    this.authsBuilder_ = new RepeatedFieldBuilder<SaslAuth, SaslAuth.Builder, SaslAuthOrBuilder>(this.auths_, (this.bitField0_ & 0x8) == 0x8, this.getParentForChildren(), this.isClean());
                    this.auths_ = null;
                }
                return this.authsBuilder_;
            }
        }
        
        public interface SaslAuthOrBuilder extends MessageOrBuilder
        {
            boolean hasMethod();
            
            String getMethod();
            
            ByteString getMethodBytes();
            
            boolean hasMechanism();
            
            String getMechanism();
            
            ByteString getMechanismBytes();
            
            boolean hasProtocol();
            
            String getProtocol();
            
            ByteString getProtocolBytes();
            
            boolean hasServerId();
            
            String getServerId();
            
            ByteString getServerIdBytes();
            
            boolean hasChallenge();
            
            ByteString getChallenge();
        }
    }
    
    public interface RpcSaslProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasVersion();
        
        int getVersion();
        
        boolean hasState();
        
        RpcSaslProto.SaslState getState();
        
        boolean hasToken();
        
        ByteString getToken();
        
        List<RpcSaslProto.SaslAuth> getAuthsList();
        
        RpcSaslProto.SaslAuth getAuths(final int p0);
        
        int getAuthsCount();
        
        List<? extends RpcSaslProto.SaslAuthOrBuilder> getAuthsOrBuilderList();
        
        RpcSaslProto.SaslAuthOrBuilder getAuthsOrBuilder(final int p0);
    }
    
    public interface RpcResponseHeaderProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasCallId();
        
        int getCallId();
        
        boolean hasStatus();
        
        RpcResponseHeaderProto.RpcStatusProto getStatus();
        
        boolean hasServerIpcVersionNum();
        
        int getServerIpcVersionNum();
        
        boolean hasExceptionClassName();
        
        String getExceptionClassName();
        
        ByteString getExceptionClassNameBytes();
        
        boolean hasErrorMsg();
        
        String getErrorMsg();
        
        ByteString getErrorMsgBytes();
        
        boolean hasErrorDetail();
        
        RpcResponseHeaderProto.RpcErrorCodeProto getErrorDetail();
        
        boolean hasClientId();
        
        ByteString getClientId();
        
        boolean hasRetryCount();
        
        int getRetryCount();
    }
    
    public interface RPCTraceInfoProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasTraceId();
        
        long getTraceId();
        
        boolean hasParentId();
        
        long getParentId();
    }
    
    public interface RPCCallerContextProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasContext();
        
        String getContext();
        
        ByteString getContextBytes();
        
        boolean hasSignature();
        
        ByteString getSignature();
    }
    
    public interface RpcRequestHeaderProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasRpcKind();
        
        RpcKindProto getRpcKind();
        
        boolean hasRpcOp();
        
        RpcRequestHeaderProto.OperationProto getRpcOp();
        
        boolean hasCallId();
        
        int getCallId();
        
        boolean hasClientId();
        
        ByteString getClientId();
        
        boolean hasRetryCount();
        
        int getRetryCount();
        
        boolean hasTraceInfo();
        
        RPCTraceInfoProto getTraceInfo();
        
        RPCTraceInfoProtoOrBuilder getTraceInfoOrBuilder();
        
        boolean hasCallerContext();
        
        RPCCallerContextProto getCallerContext();
        
        RPCCallerContextProtoOrBuilder getCallerContextOrBuilder();
    }
}
