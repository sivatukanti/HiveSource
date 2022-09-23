// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import java.util.Collection;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import com.google.protobuf.ByteString;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
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

public final class TraceAdminPB
{
    private static Descriptors.Descriptor internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_ListSpanReceiversRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_SpanReceiverListInfo_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_SpanReceiverListInfo_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_ListSpanReceiversResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_ConfigPair_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_ConfigPair_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_AddSpanReceiverRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_AddSpanReceiverResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RemoveSpanReceiverRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_RemoveSpanReceiverResponseProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private TraceAdminPB() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return TraceAdminPB.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0010TraceAdmin.proto\u0012\rhadoop.common\"\u001f\n\u001dListSpanReceiversRequestProto\"5\n\u0014SpanReceiverListInfo\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\u0012\u0011\n\tclassName\u0018\u0002 \u0002(\t\"[\n\u001eListSpanReceiversResponseProto\u00129\n\fdescriptions\u0018\u0001 \u0003(\u000b2#.hadoop.common.SpanReceiverListInfo\"(\n\nConfigPair\u0012\u000b\n\u0003key\u0018\u0001 \u0002(\t\u0012\r\n\u0005value\u0018\u0002 \u0002(\t\"[\n\u001bAddSpanReceiverRequestProto\u0012\u0011\n\tclassName\u0018\u0001 \u0002(\t\u0012)\n\u0006config\u0018\u0002 \u0003(\u000b2\u0019.hadoop.common.ConfigPair\"*\n\u001cAddSpanReceiverResponseProto\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\",\n\u001eRem", "oveSpanReceiverRequestProto\u0012\n\n\u0002id\u0018\u0001 \u0002(\u0003\"!\n\u001fRemoveSpanReceiverResponseProto2\u00e6\u0002\n\u0011TraceAdminService\u0012p\n\u0011listSpanReceivers\u0012,.hadoop.common.ListSpanReceiversRequestProto\u001a-.hadoop.common.ListSpanReceiversResponseProto\u0012j\n\u000faddSpanReceiver\u0012*.hadoop.common.AddSpanReceiverRequestProto\u001a+.hadoop.common.AddSpanReceiverResponseProto\u0012s\n\u0012removeSpanReceiver\u0012-.hadoop.common.RemoveSpanReceiverRequestProto\u001a..hadoop.com", "mon.RemoveSpanReceiverResponseProtoB/\n\u0019org.apache.hadoop.tracingB\fTraceAdminPB\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                TraceAdminPB.descriptor = root;
                TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(0);
                TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor, new String[0]);
                TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(1);
                TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_descriptor, new String[] { "Id", "ClassName" });
                TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(2);
                TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor, new String[] { "Descriptions" });
                TraceAdminPB.internal_static_hadoop_common_ConfigPair_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(3);
                TraceAdminPB.internal_static_hadoop_common_ConfigPair_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_ConfigPair_descriptor, new String[] { "Key", "Value" });
                TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(4);
                TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor, new String[] { "ClassName", "Config" });
                TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(5);
                TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor, new String[] { "Id" });
                TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(6);
                TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor, new String[] { "Id" });
                TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor = TraceAdminPB.getDescriptor().getMessageTypes().get(7);
                TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor, new String[0]);
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class ListSpanReceiversRequestProto extends GeneratedMessage implements ListSpanReceiversRequestProtoOrBuilder
    {
        private static final ListSpanReceiversRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ListSpanReceiversRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ListSpanReceiversRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ListSpanReceiversRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ListSpanReceiversRequestProto getDefaultInstance() {
            return ListSpanReceiversRequestProto.defaultInstance;
        }
        
        @Override
        public ListSpanReceiversRequestProto getDefaultInstanceForType() {
            return ListSpanReceiversRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ListSpanReceiversRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ListSpanReceiversRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<ListSpanReceiversRequestProto> getParserForType() {
            return ListSpanReceiversRequestProto.PARSER;
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
            if (!(obj instanceof ListSpanReceiversRequestProto)) {
                return super.equals(obj);
            }
            final ListSpanReceiversRequestProto other = (ListSpanReceiversRequestProto)obj;
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
        
        public static ListSpanReceiversRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(data);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(data);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final InputStream input) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(input);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ListSpanReceiversRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ListSpanReceiversRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(input);
        }
        
        public static ListSpanReceiversRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ListSpanReceiversRequestProto prototype) {
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
            ListSpanReceiversRequestProto.PARSER = new AbstractParser<ListSpanReceiversRequestProto>() {
                @Override
                public ListSpanReceiversRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ListSpanReceiversRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ListSpanReceiversRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ListSpanReceiversRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ListSpanReceiversRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ListSpanReceiversRequestProto.alwaysUseFieldBuilders) {}
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
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversRequestProto_descriptor;
            }
            
            @Override
            public ListSpanReceiversRequestProto getDefaultInstanceForType() {
                return ListSpanReceiversRequestProto.getDefaultInstance();
            }
            
            @Override
            public ListSpanReceiversRequestProto build() {
                final ListSpanReceiversRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ListSpanReceiversRequestProto buildPartial() {
                final ListSpanReceiversRequestProto result = new ListSpanReceiversRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ListSpanReceiversRequestProto) {
                    return this.mergeFrom((ListSpanReceiversRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ListSpanReceiversRequestProto other) {
                if (other == ListSpanReceiversRequestProto.getDefaultInstance()) {
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
                ListSpanReceiversRequestProto parsedMessage = null;
                try {
                    parsedMessage = ListSpanReceiversRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ListSpanReceiversRequestProto)e.getUnfinishedMessage();
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
    
    public static final class SpanReceiverListInfo extends GeneratedMessage implements SpanReceiverListInfoOrBuilder
    {
        private static final SpanReceiverListInfo defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<SpanReceiverListInfo> PARSER;
        private int bitField0_;
        public static final int ID_FIELD_NUMBER = 1;
        private long id_;
        public static final int CLASSNAME_FIELD_NUMBER = 2;
        private Object className_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private SpanReceiverListInfo(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private SpanReceiverListInfo(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static SpanReceiverListInfo getDefaultInstance() {
            return SpanReceiverListInfo.defaultInstance;
        }
        
        @Override
        public SpanReceiverListInfo getDefaultInstanceForType() {
            return SpanReceiverListInfo.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private SpanReceiverListInfo(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.id_ = input.readInt64();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.className_ = input.readBytes();
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
            return TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SpanReceiverListInfo.class, Builder.class);
        }
        
        @Override
        public Parser<SpanReceiverListInfo> getParserForType() {
            return SpanReceiverListInfo.PARSER;
        }
        
        @Override
        public boolean hasId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getId() {
            return this.id_;
        }
        
        @Override
        public boolean hasClassName() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getClassName() {
            final Object ref = this.className_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.className_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getClassNameBytes() {
            final Object ref = this.className_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.className_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.id_ = 0L;
            this.className_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasId()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasClassName()) {
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
                output.writeInt64(1, this.id_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getClassNameBytes());
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
                size += CodedOutputStream.computeInt64Size(1, this.id_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getClassNameBytes());
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
            if (!(obj instanceof SpanReceiverListInfo)) {
                return super.equals(obj);
            }
            final SpanReceiverListInfo other = (SpanReceiverListInfo)obj;
            boolean result = true;
            result = (result && this.hasId() == other.hasId());
            if (this.hasId()) {
                result = (result && this.getId() == other.getId());
            }
            result = (result && this.hasClassName() == other.hasClassName());
            if (this.hasClassName()) {
                result = (result && this.getClassName().equals(other.getClassName()));
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
            if (this.hasId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getId());
            }
            if (this.hasClassName()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getClassName().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static SpanReceiverListInfo parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return SpanReceiverListInfo.PARSER.parseFrom(data);
        }
        
        public static SpanReceiverListInfo parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SpanReceiverListInfo.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SpanReceiverListInfo parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return SpanReceiverListInfo.PARSER.parseFrom(data);
        }
        
        public static SpanReceiverListInfo parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SpanReceiverListInfo.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SpanReceiverListInfo parseFrom(final InputStream input) throws IOException {
            return SpanReceiverListInfo.PARSER.parseFrom(input);
        }
        
        public static SpanReceiverListInfo parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SpanReceiverListInfo.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static SpanReceiverListInfo parseDelimitedFrom(final InputStream input) throws IOException {
            return SpanReceiverListInfo.PARSER.parseDelimitedFrom(input);
        }
        
        public static SpanReceiverListInfo parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SpanReceiverListInfo.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static SpanReceiverListInfo parseFrom(final CodedInputStream input) throws IOException {
            return SpanReceiverListInfo.PARSER.parseFrom(input);
        }
        
        public static SpanReceiverListInfo parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SpanReceiverListInfo.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final SpanReceiverListInfo prototype) {
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
            SpanReceiverListInfo.PARSER = new AbstractParser<SpanReceiverListInfo>() {
                @Override
                public SpanReceiverListInfo parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new SpanReceiverListInfo(input, extensionRegistry);
                }
            };
            (defaultInstance = new SpanReceiverListInfo(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements SpanReceiverListInfoOrBuilder
        {
            private int bitField0_;
            private long id_;
            private Object className_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SpanReceiverListInfo.class, Builder.class);
            }
            
            private Builder() {
                this.className_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.className_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (SpanReceiverListInfo.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                this.className_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_SpanReceiverListInfo_descriptor;
            }
            
            @Override
            public SpanReceiverListInfo getDefaultInstanceForType() {
                return SpanReceiverListInfo.getDefaultInstance();
            }
            
            @Override
            public SpanReceiverListInfo build() {
                final SpanReceiverListInfo result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public SpanReceiverListInfo buildPartial() {
                final SpanReceiverListInfo result = new SpanReceiverListInfo((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.id_ = this.id_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.className_ = this.className_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof SpanReceiverListInfo) {
                    return this.mergeFrom((SpanReceiverListInfo)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final SpanReceiverListInfo other) {
                if (other == SpanReceiverListInfo.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    this.setId(other.getId());
                }
                if (other.hasClassName()) {
                    this.bitField0_ |= 0x2;
                    this.className_ = other.className_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasId() && this.hasClassName();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                SpanReceiverListInfo parsedMessage = null;
                try {
                    parsedMessage = SpanReceiverListInfo.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (SpanReceiverListInfo)e.getUnfinishedMessage();
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
            public boolean hasId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getId() {
                return this.id_;
            }
            
            public Builder setId(final long value) {
                this.bitField0_ |= 0x1;
                this.id_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.id_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasClassName() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getClassName() {
                final Object ref = this.className_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.className_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getClassNameBytes() {
                final Object ref = this.className_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.className_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setClassName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.className_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClassName() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.className_ = SpanReceiverListInfo.getDefaultInstance().getClassName();
                this.onChanged();
                return this;
            }
            
            public Builder setClassNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.className_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class ListSpanReceiversResponseProto extends GeneratedMessage implements ListSpanReceiversResponseProtoOrBuilder
    {
        private static final ListSpanReceiversResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ListSpanReceiversResponseProto> PARSER;
        public static final int DESCRIPTIONS_FIELD_NUMBER = 1;
        private List<SpanReceiverListInfo> descriptions_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ListSpanReceiversResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ListSpanReceiversResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ListSpanReceiversResponseProto getDefaultInstance() {
            return ListSpanReceiversResponseProto.defaultInstance;
        }
        
        @Override
        public ListSpanReceiversResponseProto getDefaultInstanceForType() {
            return ListSpanReceiversResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ListSpanReceiversResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.descriptions_ = new ArrayList<SpanReceiverListInfo>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.descriptions_.add(input.readMessage(SpanReceiverListInfo.PARSER, extensionRegistry));
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
                    this.descriptions_ = Collections.unmodifiableList((List<? extends SpanReceiverListInfo>)this.descriptions_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ListSpanReceiversResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<ListSpanReceiversResponseProto> getParserForType() {
            return ListSpanReceiversResponseProto.PARSER;
        }
        
        @Override
        public List<SpanReceiverListInfo> getDescriptionsList() {
            return this.descriptions_;
        }
        
        @Override
        public List<? extends SpanReceiverListInfoOrBuilder> getDescriptionsOrBuilderList() {
            return this.descriptions_;
        }
        
        @Override
        public int getDescriptionsCount() {
            return this.descriptions_.size();
        }
        
        @Override
        public SpanReceiverListInfo getDescriptions(final int index) {
            return this.descriptions_.get(index);
        }
        
        @Override
        public SpanReceiverListInfoOrBuilder getDescriptionsOrBuilder(final int index) {
            return this.descriptions_.get(index);
        }
        
        private void initFields() {
            this.descriptions_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getDescriptionsCount(); ++i) {
                if (!this.getDescriptions(i).isInitialized()) {
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
            for (int i = 0; i < this.descriptions_.size(); ++i) {
                output.writeMessage(1, this.descriptions_.get(i));
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
            for (int i = 0; i < this.descriptions_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.descriptions_.get(i));
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
            if (!(obj instanceof ListSpanReceiversResponseProto)) {
                return super.equals(obj);
            }
            final ListSpanReceiversResponseProto other = (ListSpanReceiversResponseProto)obj;
            boolean result = true;
            result = (result && this.getDescriptionsList().equals(other.getDescriptionsList()));
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
            if (this.getDescriptionsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getDescriptionsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(data);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(data);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final InputStream input) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(input);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ListSpanReceiversResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ListSpanReceiversResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(input);
        }
        
        public static ListSpanReceiversResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ListSpanReceiversResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ListSpanReceiversResponseProto prototype) {
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
            ListSpanReceiversResponseProto.PARSER = new AbstractParser<ListSpanReceiversResponseProto>() {
                @Override
                public ListSpanReceiversResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ListSpanReceiversResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ListSpanReceiversResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ListSpanReceiversResponseProtoOrBuilder
        {
            private int bitField0_;
            private List<SpanReceiverListInfo> descriptions_;
            private RepeatedFieldBuilder<SpanReceiverListInfo, SpanReceiverListInfo.Builder, SpanReceiverListInfoOrBuilder> descriptionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ListSpanReceiversResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.descriptions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.descriptions_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ListSpanReceiversResponseProto.alwaysUseFieldBuilders) {
                    this.getDescriptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.descriptionsBuilder_ == null) {
                    this.descriptions_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.descriptionsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_ListSpanReceiversResponseProto_descriptor;
            }
            
            @Override
            public ListSpanReceiversResponseProto getDefaultInstanceForType() {
                return ListSpanReceiversResponseProto.getDefaultInstance();
            }
            
            @Override
            public ListSpanReceiversResponseProto build() {
                final ListSpanReceiversResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ListSpanReceiversResponseProto buildPartial() {
                final ListSpanReceiversResponseProto result = new ListSpanReceiversResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.descriptionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.descriptions_ = Collections.unmodifiableList((List<? extends SpanReceiverListInfo>)this.descriptions_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.descriptions_ = this.descriptions_;
                }
                else {
                    result.descriptions_ = this.descriptionsBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ListSpanReceiversResponseProto) {
                    return this.mergeFrom((ListSpanReceiversResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ListSpanReceiversResponseProto other) {
                if (other == ListSpanReceiversResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (this.descriptionsBuilder_ == null) {
                    if (!other.descriptions_.isEmpty()) {
                        if (this.descriptions_.isEmpty()) {
                            this.descriptions_ = other.descriptions_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureDescriptionsIsMutable();
                            this.descriptions_.addAll(other.descriptions_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.descriptions_.isEmpty()) {
                    if (this.descriptionsBuilder_.isEmpty()) {
                        this.descriptionsBuilder_.dispose();
                        this.descriptionsBuilder_ = null;
                        this.descriptions_ = other.descriptions_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.descriptionsBuilder_ = (ListSpanReceiversResponseProto.alwaysUseFieldBuilders ? this.getDescriptionsFieldBuilder() : null);
                    }
                    else {
                        this.descriptionsBuilder_.addAllMessages(other.descriptions_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getDescriptionsCount(); ++i) {
                    if (!this.getDescriptions(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ListSpanReceiversResponseProto parsedMessage = null;
                try {
                    parsedMessage = ListSpanReceiversResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ListSpanReceiversResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureDescriptionsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.descriptions_ = new ArrayList<SpanReceiverListInfo>(this.descriptions_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<SpanReceiverListInfo> getDescriptionsList() {
                if (this.descriptionsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends SpanReceiverListInfo>)this.descriptions_);
                }
                return this.descriptionsBuilder_.getMessageList();
            }
            
            @Override
            public int getDescriptionsCount() {
                if (this.descriptionsBuilder_ == null) {
                    return this.descriptions_.size();
                }
                return this.descriptionsBuilder_.getCount();
            }
            
            @Override
            public SpanReceiverListInfo getDescriptions(final int index) {
                if (this.descriptionsBuilder_ == null) {
                    return this.descriptions_.get(index);
                }
                return this.descriptionsBuilder_.getMessage(index);
            }
            
            public Builder setDescriptions(final int index, final SpanReceiverListInfo value) {
                if (this.descriptionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setDescriptions(final int index, final SpanReceiverListInfo.Builder builderForValue) {
                if (this.descriptionsBuilder_ == null) {
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addDescriptions(final SpanReceiverListInfo value) {
                if (this.descriptionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.add(value);
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addDescriptions(final int index, final SpanReceiverListInfo value) {
                if (this.descriptionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addDescriptions(final SpanReceiverListInfo.Builder builderForValue) {
                if (this.descriptionsBuilder_ == null) {
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addDescriptions(final int index, final SpanReceiverListInfo.Builder builderForValue) {
                if (this.descriptionsBuilder_ == null) {
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllDescriptions(final Iterable<? extends SpanReceiverListInfo> values) {
                if (this.descriptionsBuilder_ == null) {
                    this.ensureDescriptionsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.descriptions_);
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearDescriptions() {
                if (this.descriptionsBuilder_ == null) {
                    this.descriptions_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeDescriptions(final int index) {
                if (this.descriptionsBuilder_ == null) {
                    this.ensureDescriptionsIsMutable();
                    this.descriptions_.remove(index);
                    this.onChanged();
                }
                else {
                    this.descriptionsBuilder_.remove(index);
                }
                return this;
            }
            
            public SpanReceiverListInfo.Builder getDescriptionsBuilder(final int index) {
                return this.getDescriptionsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public SpanReceiverListInfoOrBuilder getDescriptionsOrBuilder(final int index) {
                if (this.descriptionsBuilder_ == null) {
                    return this.descriptions_.get(index);
                }
                return this.descriptionsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends SpanReceiverListInfoOrBuilder> getDescriptionsOrBuilderList() {
                if (this.descriptionsBuilder_ != null) {
                    return this.descriptionsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends SpanReceiverListInfoOrBuilder>)this.descriptions_);
            }
            
            public SpanReceiverListInfo.Builder addDescriptionsBuilder() {
                return this.getDescriptionsFieldBuilder().addBuilder(SpanReceiverListInfo.getDefaultInstance());
            }
            
            public SpanReceiverListInfo.Builder addDescriptionsBuilder(final int index) {
                return this.getDescriptionsFieldBuilder().addBuilder(index, SpanReceiverListInfo.getDefaultInstance());
            }
            
            public List<SpanReceiverListInfo.Builder> getDescriptionsBuilderList() {
                return this.getDescriptionsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<SpanReceiverListInfo, SpanReceiverListInfo.Builder, SpanReceiverListInfoOrBuilder> getDescriptionsFieldBuilder() {
                if (this.descriptionsBuilder_ == null) {
                    this.descriptionsBuilder_ = new RepeatedFieldBuilder<SpanReceiverListInfo, SpanReceiverListInfo.Builder, SpanReceiverListInfoOrBuilder>(this.descriptions_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.descriptions_ = null;
                }
                return this.descriptionsBuilder_;
            }
        }
    }
    
    public static final class ConfigPair extends GeneratedMessage implements ConfigPairOrBuilder
    {
        private static final ConfigPair defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ConfigPair> PARSER;
        private int bitField0_;
        public static final int KEY_FIELD_NUMBER = 1;
        private Object key_;
        public static final int VALUE_FIELD_NUMBER = 2;
        private Object value_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ConfigPair(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ConfigPair(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ConfigPair getDefaultInstance() {
            return ConfigPair.defaultInstance;
        }
        
        @Override
        public ConfigPair getDefaultInstanceForType() {
            return ConfigPair.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ConfigPair(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.key_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.value_ = input.readBytes();
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
            return TraceAdminPB.internal_static_hadoop_common_ConfigPair_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_ConfigPair_fieldAccessorTable.ensureFieldAccessorsInitialized(ConfigPair.class, Builder.class);
        }
        
        @Override
        public Parser<ConfigPair> getParserForType() {
            return ConfigPair.PARSER;
        }
        
        @Override
        public boolean hasKey() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getKey() {
            final Object ref = this.key_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.key_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getKeyBytes() {
            final Object ref = this.key_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.key_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasValue() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getValue() {
            final Object ref = this.value_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.value_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getValueBytes() {
            final Object ref = this.value_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.value_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.key_ = "";
            this.value_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasKey()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasValue()) {
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
                output.writeBytes(1, this.getKeyBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getValueBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getKeyBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getValueBytes());
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
            if (!(obj instanceof ConfigPair)) {
                return super.equals(obj);
            }
            final ConfigPair other = (ConfigPair)obj;
            boolean result = true;
            result = (result && this.hasKey() == other.hasKey());
            if (this.hasKey()) {
                result = (result && this.getKey().equals(other.getKey()));
            }
            result = (result && this.hasValue() == other.hasValue());
            if (this.hasValue()) {
                result = (result && this.getValue().equals(other.getValue()));
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
            if (this.hasKey()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getKey().hashCode();
            }
            if (this.hasValue()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getValue().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ConfigPair parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ConfigPair.PARSER.parseFrom(data);
        }
        
        public static ConfigPair parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ConfigPair.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ConfigPair parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ConfigPair.PARSER.parseFrom(data);
        }
        
        public static ConfigPair parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ConfigPair.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ConfigPair parseFrom(final InputStream input) throws IOException {
            return ConfigPair.PARSER.parseFrom(input);
        }
        
        public static ConfigPair parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ConfigPair.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ConfigPair parseDelimitedFrom(final InputStream input) throws IOException {
            return ConfigPair.PARSER.parseDelimitedFrom(input);
        }
        
        public static ConfigPair parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ConfigPair.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ConfigPair parseFrom(final CodedInputStream input) throws IOException {
            return ConfigPair.PARSER.parseFrom(input);
        }
        
        public static ConfigPair parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ConfigPair.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ConfigPair prototype) {
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
            ConfigPair.PARSER = new AbstractParser<ConfigPair>() {
                @Override
                public ConfigPair parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ConfigPair(input, extensionRegistry);
                }
            };
            (defaultInstance = new ConfigPair(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ConfigPairOrBuilder
        {
            private int bitField0_;
            private Object key_;
            private Object value_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_ConfigPair_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_ConfigPair_fieldAccessorTable.ensureFieldAccessorsInitialized(ConfigPair.class, Builder.class);
            }
            
            private Builder() {
                this.key_ = "";
                this.value_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.key_ = "";
                this.value_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ConfigPair.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.key_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.value_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_ConfigPair_descriptor;
            }
            
            @Override
            public ConfigPair getDefaultInstanceForType() {
                return ConfigPair.getDefaultInstance();
            }
            
            @Override
            public ConfigPair build() {
                final ConfigPair result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ConfigPair buildPartial() {
                final ConfigPair result = new ConfigPair((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.key_ = this.key_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.value_ = this.value_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ConfigPair) {
                    return this.mergeFrom((ConfigPair)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ConfigPair other) {
                if (other == ConfigPair.getDefaultInstance()) {
                    return this;
                }
                if (other.hasKey()) {
                    this.bitField0_ |= 0x1;
                    this.key_ = other.key_;
                    this.onChanged();
                }
                if (other.hasValue()) {
                    this.bitField0_ |= 0x2;
                    this.value_ = other.value_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasKey() && this.hasValue();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ConfigPair parsedMessage = null;
                try {
                    parsedMessage = ConfigPair.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ConfigPair)e.getUnfinishedMessage();
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
            public boolean hasKey() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getKey() {
                final Object ref = this.key_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.key_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getKeyBytes() {
                final Object ref = this.key_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.key_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setKey(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.key_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearKey() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.key_ = ConfigPair.getDefaultInstance().getKey();
                this.onChanged();
                return this;
            }
            
            public Builder setKeyBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.key_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasValue() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getValue() {
                final Object ref = this.value_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.value_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getValueBytes() {
                final Object ref = this.value_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.value_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setValue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.value_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearValue() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.value_ = ConfigPair.getDefaultInstance().getValue();
                this.onChanged();
                return this;
            }
            
            public Builder setValueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.value_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class AddSpanReceiverRequestProto extends GeneratedMessage implements AddSpanReceiverRequestProtoOrBuilder
    {
        private static final AddSpanReceiverRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AddSpanReceiverRequestProto> PARSER;
        private int bitField0_;
        public static final int CLASSNAME_FIELD_NUMBER = 1;
        private Object className_;
        public static final int CONFIG_FIELD_NUMBER = 2;
        private List<ConfigPair> config_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AddSpanReceiverRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AddSpanReceiverRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AddSpanReceiverRequestProto getDefaultInstance() {
            return AddSpanReceiverRequestProto.defaultInstance;
        }
        
        @Override
        public AddSpanReceiverRequestProto getDefaultInstanceForType() {
            return AddSpanReceiverRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AddSpanReceiverRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.bitField0_ |= 0x1;
                            this.className_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.config_ = new ArrayList<ConfigPair>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.config_.add(input.readMessage(ConfigPair.PARSER, extensionRegistry));
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
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.config_ = Collections.unmodifiableList((List<? extends ConfigPair>)this.config_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddSpanReceiverRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<AddSpanReceiverRequestProto> getParserForType() {
            return AddSpanReceiverRequestProto.PARSER;
        }
        
        @Override
        public boolean hasClassName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getClassName() {
            final Object ref = this.className_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.className_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getClassNameBytes() {
            final Object ref = this.className_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.className_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public List<ConfigPair> getConfigList() {
            return this.config_;
        }
        
        @Override
        public List<? extends ConfigPairOrBuilder> getConfigOrBuilderList() {
            return this.config_;
        }
        
        @Override
        public int getConfigCount() {
            return this.config_.size();
        }
        
        @Override
        public ConfigPair getConfig(final int index) {
            return this.config_.get(index);
        }
        
        @Override
        public ConfigPairOrBuilder getConfigOrBuilder(final int index) {
            return this.config_.get(index);
        }
        
        private void initFields() {
            this.className_ = "";
            this.config_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasClassName()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            for (int i = 0; i < this.getConfigCount(); ++i) {
                if (!this.getConfig(i).isInitialized()) {
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
                output.writeBytes(1, this.getClassNameBytes());
            }
            for (int i = 0; i < this.config_.size(); ++i) {
                output.writeMessage(2, this.config_.get(i));
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
                size += CodedOutputStream.computeBytesSize(1, this.getClassNameBytes());
            }
            for (int i = 0; i < this.config_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.config_.get(i));
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
            if (!(obj instanceof AddSpanReceiverRequestProto)) {
                return super.equals(obj);
            }
            final AddSpanReceiverRequestProto other = (AddSpanReceiverRequestProto)obj;
            boolean result = true;
            result = (result && this.hasClassName() == other.hasClassName());
            if (this.hasClassName()) {
                result = (result && this.getClassName().equals(other.getClassName()));
            }
            result = (result && this.getConfigList().equals(other.getConfigList()));
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
            if (this.hasClassName()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getClassName().hashCode();
            }
            if (this.getConfigCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getConfigList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(data);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(data);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final InputStream input) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(input);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AddSpanReceiverRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AddSpanReceiverRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(input);
        }
        
        public static AddSpanReceiverRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AddSpanReceiverRequestProto prototype) {
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
            AddSpanReceiverRequestProto.PARSER = new AbstractParser<AddSpanReceiverRequestProto>() {
                @Override
                public AddSpanReceiverRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AddSpanReceiverRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AddSpanReceiverRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AddSpanReceiverRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object className_;
            private List<ConfigPair> config_;
            private RepeatedFieldBuilder<ConfigPair, ConfigPair.Builder, ConfigPairOrBuilder> configBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddSpanReceiverRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.className_ = "";
                this.config_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.className_ = "";
                this.config_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AddSpanReceiverRequestProto.alwaysUseFieldBuilders) {
                    this.getConfigFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.className_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.configBuilder_ == null) {
                    this.config_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.configBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverRequestProto_descriptor;
            }
            
            @Override
            public AddSpanReceiverRequestProto getDefaultInstanceForType() {
                return AddSpanReceiverRequestProto.getDefaultInstance();
            }
            
            @Override
            public AddSpanReceiverRequestProto build() {
                final AddSpanReceiverRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AddSpanReceiverRequestProto buildPartial() {
                final AddSpanReceiverRequestProto result = new AddSpanReceiverRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.className_ = this.className_;
                if (this.configBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.config_ = Collections.unmodifiableList((List<? extends ConfigPair>)this.config_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.config_ = this.config_;
                }
                else {
                    result.config_ = this.configBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AddSpanReceiverRequestProto) {
                    return this.mergeFrom((AddSpanReceiverRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AddSpanReceiverRequestProto other) {
                if (other == AddSpanReceiverRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasClassName()) {
                    this.bitField0_ |= 0x1;
                    this.className_ = other.className_;
                    this.onChanged();
                }
                if (this.configBuilder_ == null) {
                    if (!other.config_.isEmpty()) {
                        if (this.config_.isEmpty()) {
                            this.config_ = other.config_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureConfigIsMutable();
                            this.config_.addAll(other.config_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.config_.isEmpty()) {
                    if (this.configBuilder_.isEmpty()) {
                        this.configBuilder_.dispose();
                        this.configBuilder_ = null;
                        this.config_ = other.config_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.configBuilder_ = (AddSpanReceiverRequestProto.alwaysUseFieldBuilders ? this.getConfigFieldBuilder() : null);
                    }
                    else {
                        this.configBuilder_.addAllMessages(other.config_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                if (!this.hasClassName()) {
                    return false;
                }
                for (int i = 0; i < this.getConfigCount(); ++i) {
                    if (!this.getConfig(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                AddSpanReceiverRequestProto parsedMessage = null;
                try {
                    parsedMessage = AddSpanReceiverRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AddSpanReceiverRequestProto)e.getUnfinishedMessage();
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
            public boolean hasClassName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getClassName() {
                final Object ref = this.className_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.className_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getClassNameBytes() {
                final Object ref = this.className_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.className_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setClassName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.className_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClassName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.className_ = AddSpanReceiverRequestProto.getDefaultInstance().getClassName();
                this.onChanged();
                return this;
            }
            
            public Builder setClassNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.className_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureConfigIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.config_ = new ArrayList<ConfigPair>(this.config_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            @Override
            public List<ConfigPair> getConfigList() {
                if (this.configBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends ConfigPair>)this.config_);
                }
                return this.configBuilder_.getMessageList();
            }
            
            @Override
            public int getConfigCount() {
                if (this.configBuilder_ == null) {
                    return this.config_.size();
                }
                return this.configBuilder_.getCount();
            }
            
            @Override
            public ConfigPair getConfig(final int index) {
                if (this.configBuilder_ == null) {
                    return this.config_.get(index);
                }
                return this.configBuilder_.getMessage(index);
            }
            
            public Builder setConfig(final int index, final ConfigPair value) {
                if (this.configBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureConfigIsMutable();
                    this.config_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.configBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setConfig(final int index, final ConfigPair.Builder builderForValue) {
                if (this.configBuilder_ == null) {
                    this.ensureConfigIsMutable();
                    this.config_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.configBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addConfig(final ConfigPair value) {
                if (this.configBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureConfigIsMutable();
                    this.config_.add(value);
                    this.onChanged();
                }
                else {
                    this.configBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addConfig(final int index, final ConfigPair value) {
                if (this.configBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureConfigIsMutable();
                    this.config_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.configBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addConfig(final ConfigPair.Builder builderForValue) {
                if (this.configBuilder_ == null) {
                    this.ensureConfigIsMutable();
                    this.config_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.configBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addConfig(final int index, final ConfigPair.Builder builderForValue) {
                if (this.configBuilder_ == null) {
                    this.ensureConfigIsMutable();
                    this.config_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.configBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllConfig(final Iterable<? extends ConfigPair> values) {
                if (this.configBuilder_ == null) {
                    this.ensureConfigIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.config_);
                    this.onChanged();
                }
                else {
                    this.configBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearConfig() {
                if (this.configBuilder_ == null) {
                    this.config_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.configBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeConfig(final int index) {
                if (this.configBuilder_ == null) {
                    this.ensureConfigIsMutable();
                    this.config_.remove(index);
                    this.onChanged();
                }
                else {
                    this.configBuilder_.remove(index);
                }
                return this;
            }
            
            public ConfigPair.Builder getConfigBuilder(final int index) {
                return this.getConfigFieldBuilder().getBuilder(index);
            }
            
            @Override
            public ConfigPairOrBuilder getConfigOrBuilder(final int index) {
                if (this.configBuilder_ == null) {
                    return this.config_.get(index);
                }
                return this.configBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends ConfigPairOrBuilder> getConfigOrBuilderList() {
                if (this.configBuilder_ != null) {
                    return this.configBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends ConfigPairOrBuilder>)this.config_);
            }
            
            public ConfigPair.Builder addConfigBuilder() {
                return this.getConfigFieldBuilder().addBuilder(ConfigPair.getDefaultInstance());
            }
            
            public ConfigPair.Builder addConfigBuilder(final int index) {
                return this.getConfigFieldBuilder().addBuilder(index, ConfigPair.getDefaultInstance());
            }
            
            public List<ConfigPair.Builder> getConfigBuilderList() {
                return this.getConfigFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<ConfigPair, ConfigPair.Builder, ConfigPairOrBuilder> getConfigFieldBuilder() {
                if (this.configBuilder_ == null) {
                    this.configBuilder_ = new RepeatedFieldBuilder<ConfigPair, ConfigPair.Builder, ConfigPairOrBuilder>(this.config_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.config_ = null;
                }
                return this.configBuilder_;
            }
        }
    }
    
    public static final class AddSpanReceiverResponseProto extends GeneratedMessage implements AddSpanReceiverResponseProtoOrBuilder
    {
        private static final AddSpanReceiverResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AddSpanReceiverResponseProto> PARSER;
        private int bitField0_;
        public static final int ID_FIELD_NUMBER = 1;
        private long id_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AddSpanReceiverResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AddSpanReceiverResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AddSpanReceiverResponseProto getDefaultInstance() {
            return AddSpanReceiverResponseProto.defaultInstance;
        }
        
        @Override
        public AddSpanReceiverResponseProto getDefaultInstanceForType() {
            return AddSpanReceiverResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AddSpanReceiverResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.id_ = input.readInt64();
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
            return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddSpanReceiverResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<AddSpanReceiverResponseProto> getParserForType() {
            return AddSpanReceiverResponseProto.PARSER;
        }
        
        @Override
        public boolean hasId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getId() {
            return this.id_;
        }
        
        private void initFields() {
            this.id_ = 0L;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasId()) {
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
                output.writeInt64(1, this.id_);
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
                size += CodedOutputStream.computeInt64Size(1, this.id_);
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
            if (!(obj instanceof AddSpanReceiverResponseProto)) {
                return super.equals(obj);
            }
            final AddSpanReceiverResponseProto other = (AddSpanReceiverResponseProto)obj;
            boolean result = true;
            result = (result && this.hasId() == other.hasId());
            if (this.hasId()) {
                result = (result && this.getId() == other.getId());
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
            if (this.hasId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getId());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(data);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(data);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final InputStream input) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(input);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AddSpanReceiverResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AddSpanReceiverResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(input);
        }
        
        public static AddSpanReceiverResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddSpanReceiverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AddSpanReceiverResponseProto prototype) {
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
            AddSpanReceiverResponseProto.PARSER = new AbstractParser<AddSpanReceiverResponseProto>() {
                @Override
                public AddSpanReceiverResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AddSpanReceiverResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AddSpanReceiverResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AddSpanReceiverResponseProtoOrBuilder
        {
            private int bitField0_;
            private long id_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddSpanReceiverResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AddSpanReceiverResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_AddSpanReceiverResponseProto_descriptor;
            }
            
            @Override
            public AddSpanReceiverResponseProto getDefaultInstanceForType() {
                return AddSpanReceiverResponseProto.getDefaultInstance();
            }
            
            @Override
            public AddSpanReceiverResponseProto build() {
                final AddSpanReceiverResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AddSpanReceiverResponseProto buildPartial() {
                final AddSpanReceiverResponseProto result = new AddSpanReceiverResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.id_ = this.id_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AddSpanReceiverResponseProto) {
                    return this.mergeFrom((AddSpanReceiverResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AddSpanReceiverResponseProto other) {
                if (other == AddSpanReceiverResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    this.setId(other.getId());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasId();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                AddSpanReceiverResponseProto parsedMessage = null;
                try {
                    parsedMessage = AddSpanReceiverResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AddSpanReceiverResponseProto)e.getUnfinishedMessage();
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
            public boolean hasId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getId() {
                return this.id_;
            }
            
            public Builder setId(final long value) {
                this.bitField0_ |= 0x1;
                this.id_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.id_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RemoveSpanReceiverRequestProto extends GeneratedMessage implements RemoveSpanReceiverRequestProtoOrBuilder
    {
        private static final RemoveSpanReceiverRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RemoveSpanReceiverRequestProto> PARSER;
        private int bitField0_;
        public static final int ID_FIELD_NUMBER = 1;
        private long id_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RemoveSpanReceiverRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RemoveSpanReceiverRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RemoveSpanReceiverRequestProto getDefaultInstance() {
            return RemoveSpanReceiverRequestProto.defaultInstance;
        }
        
        @Override
        public RemoveSpanReceiverRequestProto getDefaultInstanceForType() {
            return RemoveSpanReceiverRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RemoveSpanReceiverRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.id_ = input.readInt64();
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
            return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveSpanReceiverRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RemoveSpanReceiverRequestProto> getParserForType() {
            return RemoveSpanReceiverRequestProto.PARSER;
        }
        
        @Override
        public boolean hasId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getId() {
            return this.id_;
        }
        
        private void initFields() {
            this.id_ = 0L;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasId()) {
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
                output.writeInt64(1, this.id_);
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
                size += CodedOutputStream.computeInt64Size(1, this.id_);
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
            if (!(obj instanceof RemoveSpanReceiverRequestProto)) {
                return super.equals(obj);
            }
            final RemoveSpanReceiverRequestProto other = (RemoveSpanReceiverRequestProto)obj;
            boolean result = true;
            result = (result && this.hasId() == other.hasId());
            if (this.hasId()) {
                result = (result && this.getId() == other.getId());
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
            if (this.hasId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getId());
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(data);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(data);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final InputStream input) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(input);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RemoveSpanReceiverRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RemoveSpanReceiverRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(input);
        }
        
        public static RemoveSpanReceiverRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RemoveSpanReceiverRequestProto prototype) {
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
            RemoveSpanReceiverRequestProto.PARSER = new AbstractParser<RemoveSpanReceiverRequestProto>() {
                @Override
                public RemoveSpanReceiverRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RemoveSpanReceiverRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RemoveSpanReceiverRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RemoveSpanReceiverRequestProtoOrBuilder
        {
            private int bitField0_;
            private long id_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveSpanReceiverRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RemoveSpanReceiverRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.id_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverRequestProto_descriptor;
            }
            
            @Override
            public RemoveSpanReceiverRequestProto getDefaultInstanceForType() {
                return RemoveSpanReceiverRequestProto.getDefaultInstance();
            }
            
            @Override
            public RemoveSpanReceiverRequestProto build() {
                final RemoveSpanReceiverRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RemoveSpanReceiverRequestProto buildPartial() {
                final RemoveSpanReceiverRequestProto result = new RemoveSpanReceiverRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.id_ = this.id_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RemoveSpanReceiverRequestProto) {
                    return this.mergeFrom((RemoveSpanReceiverRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RemoveSpanReceiverRequestProto other) {
                if (other == RemoveSpanReceiverRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasId()) {
                    this.setId(other.getId());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasId();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                RemoveSpanReceiverRequestProto parsedMessage = null;
                try {
                    parsedMessage = RemoveSpanReceiverRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RemoveSpanReceiverRequestProto)e.getUnfinishedMessage();
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
            public boolean hasId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getId() {
                return this.id_;
            }
            
            public Builder setId(final long value) {
                this.bitField0_ |= 0x1;
                this.id_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.id_ = 0L;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RemoveSpanReceiverResponseProto extends GeneratedMessage implements RemoveSpanReceiverResponseProtoOrBuilder
    {
        private static final RemoveSpanReceiverResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RemoveSpanReceiverResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RemoveSpanReceiverResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RemoveSpanReceiverResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RemoveSpanReceiverResponseProto getDefaultInstance() {
            return RemoveSpanReceiverResponseProto.defaultInstance;
        }
        
        @Override
        public RemoveSpanReceiverResponseProto getDefaultInstanceForType() {
            return RemoveSpanReceiverResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RemoveSpanReceiverResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveSpanReceiverResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RemoveSpanReceiverResponseProto> getParserForType() {
            return RemoveSpanReceiverResponseProto.PARSER;
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
            if (!(obj instanceof RemoveSpanReceiverResponseProto)) {
                return super.equals(obj);
            }
            final RemoveSpanReceiverResponseProto other = (RemoveSpanReceiverResponseProto)obj;
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
        
        public static RemoveSpanReceiverResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(data);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(data);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final InputStream input) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(input);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RemoveSpanReceiverResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RemoveSpanReceiverResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(input);
        }
        
        public static RemoveSpanReceiverResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveSpanReceiverResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RemoveSpanReceiverResponseProto prototype) {
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
            RemoveSpanReceiverResponseProto.PARSER = new AbstractParser<RemoveSpanReceiverResponseProto>() {
                @Override
                public RemoveSpanReceiverResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RemoveSpanReceiverResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RemoveSpanReceiverResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RemoveSpanReceiverResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveSpanReceiverResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RemoveSpanReceiverResponseProto.alwaysUseFieldBuilders) {}
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
                return TraceAdminPB.internal_static_hadoop_common_RemoveSpanReceiverResponseProto_descriptor;
            }
            
            @Override
            public RemoveSpanReceiverResponseProto getDefaultInstanceForType() {
                return RemoveSpanReceiverResponseProto.getDefaultInstance();
            }
            
            @Override
            public RemoveSpanReceiverResponseProto build() {
                final RemoveSpanReceiverResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RemoveSpanReceiverResponseProto buildPartial() {
                final RemoveSpanReceiverResponseProto result = new RemoveSpanReceiverResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RemoveSpanReceiverResponseProto) {
                    return this.mergeFrom((RemoveSpanReceiverResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RemoveSpanReceiverResponseProto other) {
                if (other == RemoveSpanReceiverResponseProto.getDefaultInstance()) {
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
                RemoveSpanReceiverResponseProto parsedMessage = null;
                try {
                    parsedMessage = RemoveSpanReceiverResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RemoveSpanReceiverResponseProto)e.getUnfinishedMessage();
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
    
    public abstract static class TraceAdminService implements Service
    {
        protected TraceAdminService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new TraceAdminService() {
                @Override
                public void listSpanReceivers(final RpcController controller, final ListSpanReceiversRequestProto request, final RpcCallback<ListSpanReceiversResponseProto> done) {
                    impl.listSpanReceivers(controller, request, done);
                }
                
                @Override
                public void addSpanReceiver(final RpcController controller, final AddSpanReceiverRequestProto request, final RpcCallback<AddSpanReceiverResponseProto> done) {
                    impl.addSpanReceiver(controller, request, done);
                }
                
                @Override
                public void removeSpanReceiver(final RpcController controller, final RemoveSpanReceiverRequestProto request, final RpcCallback<RemoveSpanReceiverResponseProto> done) {
                    impl.removeSpanReceiver(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return TraceAdminService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != TraceAdminService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.listSpanReceivers(controller, (ListSpanReceiversRequestProto)request);
                        }
                        case 1: {
                            return impl.addSpanReceiver(controller, (AddSpanReceiverRequestProto)request);
                        }
                        case 2: {
                            return impl.removeSpanReceiver(controller, (RemoveSpanReceiverRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != TraceAdminService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return ListSpanReceiversRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return AddSpanReceiverRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return RemoveSpanReceiverRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != TraceAdminService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return ListSpanReceiversResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return AddSpanReceiverResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return RemoveSpanReceiverResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void listSpanReceivers(final RpcController p0, final ListSpanReceiversRequestProto p1, final RpcCallback<ListSpanReceiversResponseProto> p2);
        
        public abstract void addSpanReceiver(final RpcController p0, final AddSpanReceiverRequestProto p1, final RpcCallback<AddSpanReceiverResponseProto> p2);
        
        public abstract void removeSpanReceiver(final RpcController p0, final RemoveSpanReceiverRequestProto p1, final RpcCallback<RemoveSpanReceiverResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return TraceAdminPB.getDescriptor().getServices().get(0);
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
                    this.listSpanReceivers(controller, (ListSpanReceiversRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.addSpanReceiver(controller, (AddSpanReceiverRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.removeSpanReceiver(controller, (RemoveSpanReceiverRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return ListSpanReceiversRequestProto.getDefaultInstance();
                }
                case 1: {
                    return AddSpanReceiverRequestProto.getDefaultInstance();
                }
                case 2: {
                    return RemoveSpanReceiverRequestProto.getDefaultInstance();
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
                    return ListSpanReceiversResponseProto.getDefaultInstance();
                }
                case 1: {
                    return AddSpanReceiverResponseProto.getDefaultInstance();
                }
                case 2: {
                    return RemoveSpanReceiverResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends TraceAdminService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void listSpanReceivers(final RpcController controller, final ListSpanReceiversRequestProto request, final RpcCallback<ListSpanReceiversResponseProto> done) {
                this.channel.callMethod(TraceAdminService.getDescriptor().getMethods().get(0), controller, request, ListSpanReceiversResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, ListSpanReceiversResponseProto.class, ListSpanReceiversResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void addSpanReceiver(final RpcController controller, final AddSpanReceiverRequestProto request, final RpcCallback<AddSpanReceiverResponseProto> done) {
                this.channel.callMethod(TraceAdminService.getDescriptor().getMethods().get(1), controller, request, AddSpanReceiverResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, AddSpanReceiverResponseProto.class, AddSpanReceiverResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void removeSpanReceiver(final RpcController controller, final RemoveSpanReceiverRequestProto request, final RpcCallback<RemoveSpanReceiverResponseProto> done) {
                this.channel.callMethod(TraceAdminService.getDescriptor().getMethods().get(2), controller, request, RemoveSpanReceiverResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, RemoveSpanReceiverResponseProto.class, RemoveSpanReceiverResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public ListSpanReceiversResponseProto listSpanReceivers(final RpcController controller, final ListSpanReceiversRequestProto request) throws ServiceException {
                return (ListSpanReceiversResponseProto)this.channel.callBlockingMethod(TraceAdminService.getDescriptor().getMethods().get(0), controller, request, ListSpanReceiversResponseProto.getDefaultInstance());
            }
            
            @Override
            public AddSpanReceiverResponseProto addSpanReceiver(final RpcController controller, final AddSpanReceiverRequestProto request) throws ServiceException {
                return (AddSpanReceiverResponseProto)this.channel.callBlockingMethod(TraceAdminService.getDescriptor().getMethods().get(1), controller, request, AddSpanReceiverResponseProto.getDefaultInstance());
            }
            
            @Override
            public RemoveSpanReceiverResponseProto removeSpanReceiver(final RpcController controller, final RemoveSpanReceiverRequestProto request) throws ServiceException {
                return (RemoveSpanReceiverResponseProto)this.channel.callBlockingMethod(TraceAdminService.getDescriptor().getMethods().get(2), controller, request, RemoveSpanReceiverResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            ListSpanReceiversResponseProto listSpanReceivers(final RpcController p0, final ListSpanReceiversRequestProto p1) throws ServiceException;
            
            AddSpanReceiverResponseProto addSpanReceiver(final RpcController p0, final AddSpanReceiverRequestProto p1) throws ServiceException;
            
            RemoveSpanReceiverResponseProto removeSpanReceiver(final RpcController p0, final RemoveSpanReceiverRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void listSpanReceivers(final RpcController p0, final ListSpanReceiversRequestProto p1, final RpcCallback<ListSpanReceiversResponseProto> p2);
            
            void addSpanReceiver(final RpcController p0, final AddSpanReceiverRequestProto p1, final RpcCallback<AddSpanReceiverResponseProto> p2);
            
            void removeSpanReceiver(final RpcController p0, final RemoveSpanReceiverRequestProto p1, final RpcCallback<RemoveSpanReceiverResponseProto> p2);
        }
    }
    
    public interface ListSpanReceiversRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface SpanReceiverListInfoOrBuilder extends MessageOrBuilder
    {
        boolean hasId();
        
        long getId();
        
        boolean hasClassName();
        
        String getClassName();
        
        ByteString getClassNameBytes();
    }
    
    public interface ListSpanReceiversResponseProtoOrBuilder extends MessageOrBuilder
    {
        List<SpanReceiverListInfo> getDescriptionsList();
        
        SpanReceiverListInfo getDescriptions(final int p0);
        
        int getDescriptionsCount();
        
        List<? extends SpanReceiverListInfoOrBuilder> getDescriptionsOrBuilderList();
        
        SpanReceiverListInfoOrBuilder getDescriptionsOrBuilder(final int p0);
    }
    
    public interface ConfigPairOrBuilder extends MessageOrBuilder
    {
        boolean hasKey();
        
        String getKey();
        
        ByteString getKeyBytes();
        
        boolean hasValue();
        
        String getValue();
        
        ByteString getValueBytes();
    }
    
    public interface AddSpanReceiverRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasClassName();
        
        String getClassName();
        
        ByteString getClassNameBytes();
        
        List<ConfigPair> getConfigList();
        
        ConfigPair getConfig(final int p0);
        
        int getConfigCount();
        
        List<? extends ConfigPairOrBuilder> getConfigOrBuilderList();
        
        ConfigPairOrBuilder getConfigOrBuilder(final int p0);
    }
    
    public interface AddSpanReceiverResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasId();
        
        long getId();
    }
    
    public interface RemoveSpanReceiverRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasId();
        
        long getId();
    }
    
    public interface RemoveSpanReceiverResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
}
