// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.ArrayList;
import com.google.protobuf.AbstractMessageLite;
import java.util.Collections;
import java.util.Collection;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.Message;
import java.io.InputStream;
import java.io.ObjectStreamException;
import com.google.protobuf.CodedOutputStream;
import java.util.List;
import com.google.protobuf.ByteString;
import com.google.protobuf.UnmodifiableLazyStringList;
import java.io.IOException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors;

public final class GenericRefreshProtocolProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_common_GenericRefreshRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GenericRefreshRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GenericRefreshResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GenericRefreshResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_common_GenericRefreshResponseCollectionProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private GenericRefreshProtocolProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return GenericRefreshProtocolProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u001cGenericRefreshProtocol.proto\u0012\rhadoop.common\">\n\u001aGenericRefreshRequestProto\u0012\u0012\n\nidentifier\u0018\u0001 \u0001(\t\u0012\f\n\u0004args\u0018\u0002 \u0003(\t\"Z\n\u001bGenericRefreshResponseProto\u0012\u0012\n\nexitStatus\u0018\u0001 \u0001(\u0005\u0012\u0013\n\u000buserMessage\u0018\u0002 \u0001(\t\u0012\u0012\n\nsenderName\u0018\u0003 \u0001(\t\"f\n%GenericRefreshResponseCollectionProto\u0012=\n\tresponses\u0018\u0001 \u0003(\u000b2*.hadoop.common.GenericRefreshResponseProto2\u008b\u0001\n\u001dGenericRefreshProtocolService\u0012j\n\u0007refresh\u0012).hadoop.common.GenericRefreshRequestProto\u001a4.hado", "op.common.GenericRefreshResponseCollectionProtoBA\n\u001borg.apache.hadoop.ipc.protoB\u001cGenericRefreshProtocolProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                GenericRefreshProtocolProtos.descriptor = root;
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_descriptor = GenericRefreshProtocolProtos.getDescriptor().getMessageTypes().get(0);
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_descriptor, new String[] { "Identifier", "Args" });
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_descriptor = GenericRefreshProtocolProtos.getDescriptor().getMessageTypes().get(1);
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_descriptor, new String[] { "ExitStatus", "UserMessage", "SenderName" });
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor = GenericRefreshProtocolProtos.getDescriptor().getMessageTypes().get(2);
                GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor, new String[] { "Responses" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class GenericRefreshRequestProto extends GeneratedMessage implements GenericRefreshRequestProtoOrBuilder
    {
        private static final GenericRefreshRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GenericRefreshRequestProto> PARSER;
        private int bitField0_;
        public static final int IDENTIFIER_FIELD_NUMBER = 1;
        private Object identifier_;
        public static final int ARGS_FIELD_NUMBER = 2;
        private LazyStringList args_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GenericRefreshRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GenericRefreshRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GenericRefreshRequestProto getDefaultInstance() {
            return GenericRefreshRequestProto.defaultInstance;
        }
        
        @Override
        public GenericRefreshRequestProto getDefaultInstanceForType() {
            return GenericRefreshRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GenericRefreshRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.identifier_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.args_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.args_.add(input.readBytes());
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
                    this.args_ = new UnmodifiableLazyStringList(this.args_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GenericRefreshRequestProto> getParserForType() {
            return GenericRefreshRequestProto.PARSER;
        }
        
        @Override
        public boolean hasIdentifier() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getIdentifier() {
            final Object ref = this.identifier_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.identifier_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getIdentifierBytes() {
            final Object ref = this.identifier_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.identifier_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public List<String> getArgsList() {
            return this.args_;
        }
        
        @Override
        public int getArgsCount() {
            return this.args_.size();
        }
        
        @Override
        public String getArgs(final int index) {
            return this.args_.get(index);
        }
        
        @Override
        public ByteString getArgsBytes(final int index) {
            return this.args_.getByteString(index);
        }
        
        private void initFields() {
            this.identifier_ = "";
            this.args_ = LazyStringArrayList.EMPTY;
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
                output.writeBytes(1, this.getIdentifierBytes());
            }
            for (int i = 0; i < this.args_.size(); ++i) {
                output.writeBytes(2, this.args_.getByteString(i));
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
                size += CodedOutputStream.computeBytesSize(1, this.getIdentifierBytes());
            }
            int dataSize = 0;
            for (int i = 0; i < this.args_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.args_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getArgsList().size();
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
            if (!(obj instanceof GenericRefreshRequestProto)) {
                return super.equals(obj);
            }
            final GenericRefreshRequestProto other = (GenericRefreshRequestProto)obj;
            boolean result = true;
            result = (result && this.hasIdentifier() == other.hasIdentifier());
            if (this.hasIdentifier()) {
                result = (result && this.getIdentifier().equals(other.getIdentifier()));
            }
            result = (result && this.getArgsList().equals(other.getArgsList()));
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
            if (this.getArgsCount() > 0) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getArgsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GenericRefreshRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GenericRefreshRequestProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GenericRefreshRequestProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshRequestProto parseFrom(final InputStream input) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GenericRefreshRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GenericRefreshRequestProto prototype) {
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
            GenericRefreshRequestProto.PARSER = new AbstractParser<GenericRefreshRequestProto>() {
                @Override
                public GenericRefreshRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GenericRefreshRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GenericRefreshRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GenericRefreshRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object identifier_;
            private LazyStringList args_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.identifier_ = "";
                this.args_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.identifier_ = "";
                this.args_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GenericRefreshRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.identifier_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.args_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshRequestProto_descriptor;
            }
            
            @Override
            public GenericRefreshRequestProto getDefaultInstanceForType() {
                return GenericRefreshRequestProto.getDefaultInstance();
            }
            
            @Override
            public GenericRefreshRequestProto build() {
                final GenericRefreshRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GenericRefreshRequestProto buildPartial() {
                final GenericRefreshRequestProto result = new GenericRefreshRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.identifier_ = this.identifier_;
                if ((this.bitField0_ & 0x2) == 0x2) {
                    this.args_ = new UnmodifiableLazyStringList(this.args_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.args_ = this.args_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GenericRefreshRequestProto) {
                    return this.mergeFrom((GenericRefreshRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GenericRefreshRequestProto other) {
                if (other == GenericRefreshRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasIdentifier()) {
                    this.bitField0_ |= 0x1;
                    this.identifier_ = other.identifier_;
                    this.onChanged();
                }
                if (!other.args_.isEmpty()) {
                    if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                    }
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
                GenericRefreshRequestProto parsedMessage = null;
                try {
                    parsedMessage = GenericRefreshRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GenericRefreshRequestProto)e.getUnfinishedMessage();
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
            public String getIdentifier() {
                final Object ref = this.identifier_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.identifier_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getIdentifierBytes() {
                final Object ref = this.identifier_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.identifier_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setIdentifier(final String value) {
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
                this.identifier_ = GenericRefreshRequestProto.getDefaultInstance().getIdentifier();
                this.onChanged();
                return this;
            }
            
            public Builder setIdentifierBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.identifier_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureArgsIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.args_ = new LazyStringArrayList(this.args_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            @Override
            public List<String> getArgsList() {
                return Collections.unmodifiableList((List<? extends String>)this.args_);
            }
            
            @Override
            public int getArgsCount() {
                return this.args_.size();
            }
            
            @Override
            public String getArgs(final int index) {
                return this.args_.get(index);
            }
            
            @Override
            public ByteString getArgsBytes(final int index) {
                return this.args_.getByteString(index);
            }
            
            public Builder setArgs(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureArgsIsMutable();
                this.args_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addArgs(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureArgsIsMutable();
                this.args_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllArgs(final Iterable<String> values) {
                this.ensureArgsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.args_);
                this.onChanged();
                return this;
            }
            
            public Builder clearArgs() {
                this.args_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
                return this;
            }
            
            public Builder addArgsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureArgsIsMutable();
                this.args_.add(value);
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GenericRefreshResponseProto extends GeneratedMessage implements GenericRefreshResponseProtoOrBuilder
    {
        private static final GenericRefreshResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GenericRefreshResponseProto> PARSER;
        private int bitField0_;
        public static final int EXITSTATUS_FIELD_NUMBER = 1;
        private int exitStatus_;
        public static final int USERMESSAGE_FIELD_NUMBER = 2;
        private Object userMessage_;
        public static final int SENDERNAME_FIELD_NUMBER = 3;
        private Object senderName_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GenericRefreshResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GenericRefreshResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GenericRefreshResponseProto getDefaultInstance() {
            return GenericRefreshResponseProto.defaultInstance;
        }
        
        @Override
        public GenericRefreshResponseProto getDefaultInstanceForType() {
            return GenericRefreshResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GenericRefreshResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.exitStatus_ = input.readInt32();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.userMessage_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.senderName_ = input.readBytes();
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
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GenericRefreshResponseProto> getParserForType() {
            return GenericRefreshResponseProto.PARSER;
        }
        
        @Override
        public boolean hasExitStatus() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getExitStatus() {
            return this.exitStatus_;
        }
        
        @Override
        public boolean hasUserMessage() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getUserMessage() {
            final Object ref = this.userMessage_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.userMessage_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getUserMessageBytes() {
            final Object ref = this.userMessage_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.userMessage_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasSenderName() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public String getSenderName() {
            final Object ref = this.senderName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.senderName_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getSenderNameBytes() {
            final Object ref = this.senderName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.senderName_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.exitStatus_ = 0;
            this.userMessage_ = "";
            this.senderName_ = "";
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
                output.writeInt32(1, this.exitStatus_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getUserMessageBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getSenderNameBytes());
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
                size += CodedOutputStream.computeInt32Size(1, this.exitStatus_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getUserMessageBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getSenderNameBytes());
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
            if (!(obj instanceof GenericRefreshResponseProto)) {
                return super.equals(obj);
            }
            final GenericRefreshResponseProto other = (GenericRefreshResponseProto)obj;
            boolean result = true;
            result = (result && this.hasExitStatus() == other.hasExitStatus());
            if (this.hasExitStatus()) {
                result = (result && this.getExitStatus() == other.getExitStatus());
            }
            result = (result && this.hasUserMessage() == other.hasUserMessage());
            if (this.hasUserMessage()) {
                result = (result && this.getUserMessage().equals(other.getUserMessage()));
            }
            result = (result && this.hasSenderName() == other.hasSenderName());
            if (this.hasSenderName()) {
                result = (result && this.getSenderName().equals(other.getSenderName()));
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
            if (this.hasExitStatus()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getExitStatus();
            }
            if (this.hasUserMessage()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getUserMessage().hashCode();
            }
            if (this.hasSenderName()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + this.getSenderName().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GenericRefreshResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GenericRefreshResponseProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GenericRefreshResponseProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshResponseProto parseFrom(final InputStream input) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GenericRefreshResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GenericRefreshResponseProto prototype) {
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
            GenericRefreshResponseProto.PARSER = new AbstractParser<GenericRefreshResponseProto>() {
                @Override
                public GenericRefreshResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GenericRefreshResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GenericRefreshResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GenericRefreshResponseProtoOrBuilder
        {
            private int bitField0_;
            private int exitStatus_;
            private Object userMessage_;
            private Object senderName_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.userMessage_ = "";
                this.senderName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.userMessage_ = "";
                this.senderName_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GenericRefreshResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.exitStatus_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                this.userMessage_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.senderName_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseProto_descriptor;
            }
            
            @Override
            public GenericRefreshResponseProto getDefaultInstanceForType() {
                return GenericRefreshResponseProto.getDefaultInstance();
            }
            
            @Override
            public GenericRefreshResponseProto build() {
                final GenericRefreshResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GenericRefreshResponseProto buildPartial() {
                final GenericRefreshResponseProto result = new GenericRefreshResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.exitStatus_ = this.exitStatus_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.userMessage_ = this.userMessage_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.senderName_ = this.senderName_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GenericRefreshResponseProto) {
                    return this.mergeFrom((GenericRefreshResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GenericRefreshResponseProto other) {
                if (other == GenericRefreshResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasExitStatus()) {
                    this.setExitStatus(other.getExitStatus());
                }
                if (other.hasUserMessage()) {
                    this.bitField0_ |= 0x2;
                    this.userMessage_ = other.userMessage_;
                    this.onChanged();
                }
                if (other.hasSenderName()) {
                    this.bitField0_ |= 0x4;
                    this.senderName_ = other.senderName_;
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
                GenericRefreshResponseProto parsedMessage = null;
                try {
                    parsedMessage = GenericRefreshResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GenericRefreshResponseProto)e.getUnfinishedMessage();
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
            public boolean hasExitStatus() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getExitStatus() {
                return this.exitStatus_;
            }
            
            public Builder setExitStatus(final int value) {
                this.bitField0_ |= 0x1;
                this.exitStatus_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearExitStatus() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.exitStatus_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasUserMessage() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getUserMessage() {
                final Object ref = this.userMessage_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.userMessage_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getUserMessageBytes() {
                final Object ref = this.userMessage_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.userMessage_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setUserMessage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.userMessage_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUserMessage() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.userMessage_ = GenericRefreshResponseProto.getDefaultInstance().getUserMessage();
                this.onChanged();
                return this;
            }
            
            public Builder setUserMessageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.userMessage_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSenderName() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public String getSenderName() {
                final Object ref = this.senderName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.senderName_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getSenderNameBytes() {
                final Object ref = this.senderName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.senderName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setSenderName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.senderName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSenderName() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.senderName_ = GenericRefreshResponseProto.getDefaultInstance().getSenderName();
                this.onChanged();
                return this;
            }
            
            public Builder setSenderNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.senderName_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GenericRefreshResponseCollectionProto extends GeneratedMessage implements GenericRefreshResponseCollectionProtoOrBuilder
    {
        private static final GenericRefreshResponseCollectionProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GenericRefreshResponseCollectionProto> PARSER;
        public static final int RESPONSES_FIELD_NUMBER = 1;
        private List<GenericRefreshResponseProto> responses_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GenericRefreshResponseCollectionProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GenericRefreshResponseCollectionProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GenericRefreshResponseCollectionProto getDefaultInstance() {
            return GenericRefreshResponseCollectionProto.defaultInstance;
        }
        
        @Override
        public GenericRefreshResponseCollectionProto getDefaultInstanceForType() {
            return GenericRefreshResponseCollectionProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GenericRefreshResponseCollectionProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.responses_ = new ArrayList<GenericRefreshResponseProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.responses_.add(input.readMessage(GenericRefreshResponseProto.PARSER, extensionRegistry));
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
                    this.responses_ = Collections.unmodifiableList((List<? extends GenericRefreshResponseProto>)this.responses_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshResponseCollectionProto.class, Builder.class);
        }
        
        @Override
        public Parser<GenericRefreshResponseCollectionProto> getParserForType() {
            return GenericRefreshResponseCollectionProto.PARSER;
        }
        
        @Override
        public List<GenericRefreshResponseProto> getResponsesList() {
            return this.responses_;
        }
        
        @Override
        public List<? extends GenericRefreshResponseProtoOrBuilder> getResponsesOrBuilderList() {
            return this.responses_;
        }
        
        @Override
        public int getResponsesCount() {
            return this.responses_.size();
        }
        
        @Override
        public GenericRefreshResponseProto getResponses(final int index) {
            return this.responses_.get(index);
        }
        
        @Override
        public GenericRefreshResponseProtoOrBuilder getResponsesOrBuilder(final int index) {
            return this.responses_.get(index);
        }
        
        private void initFields() {
            this.responses_ = Collections.emptyList();
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
            for (int i = 0; i < this.responses_.size(); ++i) {
                output.writeMessage(1, this.responses_.get(i));
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
            for (int i = 0; i < this.responses_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.responses_.get(i));
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
            if (!(obj instanceof GenericRefreshResponseCollectionProto)) {
                return super.equals(obj);
            }
            final GenericRefreshResponseCollectionProto other = (GenericRefreshResponseCollectionProto)obj;
            boolean result = true;
            result = (result && this.getResponsesList().equals(other.getResponsesList()));
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
            if (this.getResponsesCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getResponsesList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(data);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final InputStream input) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshResponseCollectionProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GenericRefreshResponseCollectionProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final CodedInputStream input) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(input);
        }
        
        public static GenericRefreshResponseCollectionProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GenericRefreshResponseCollectionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GenericRefreshResponseCollectionProto prototype) {
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
            GenericRefreshResponseCollectionProto.PARSER = new AbstractParser<GenericRefreshResponseCollectionProto>() {
                @Override
                public GenericRefreshResponseCollectionProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GenericRefreshResponseCollectionProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GenericRefreshResponseCollectionProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GenericRefreshResponseCollectionProtoOrBuilder
        {
            private int bitField0_;
            private List<GenericRefreshResponseProto> responses_;
            private RepeatedFieldBuilder<GenericRefreshResponseProto, GenericRefreshResponseProto.Builder, GenericRefreshResponseProtoOrBuilder> responsesBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GenericRefreshResponseCollectionProto.class, Builder.class);
            }
            
            private Builder() {
                this.responses_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.responses_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GenericRefreshResponseCollectionProto.alwaysUseFieldBuilders) {
                    this.getResponsesFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.responsesBuilder_ == null) {
                    this.responses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.responsesBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return GenericRefreshProtocolProtos.internal_static_hadoop_common_GenericRefreshResponseCollectionProto_descriptor;
            }
            
            @Override
            public GenericRefreshResponseCollectionProto getDefaultInstanceForType() {
                return GenericRefreshResponseCollectionProto.getDefaultInstance();
            }
            
            @Override
            public GenericRefreshResponseCollectionProto build() {
                final GenericRefreshResponseCollectionProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GenericRefreshResponseCollectionProto buildPartial() {
                final GenericRefreshResponseCollectionProto result = new GenericRefreshResponseCollectionProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.responsesBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.responses_ = Collections.unmodifiableList((List<? extends GenericRefreshResponseProto>)this.responses_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.responses_ = this.responses_;
                }
                else {
                    result.responses_ = this.responsesBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GenericRefreshResponseCollectionProto) {
                    return this.mergeFrom((GenericRefreshResponseCollectionProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GenericRefreshResponseCollectionProto other) {
                if (other == GenericRefreshResponseCollectionProto.getDefaultInstance()) {
                    return this;
                }
                if (this.responsesBuilder_ == null) {
                    if (!other.responses_.isEmpty()) {
                        if (this.responses_.isEmpty()) {
                            this.responses_ = other.responses_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureResponsesIsMutable();
                            this.responses_.addAll(other.responses_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.responses_.isEmpty()) {
                    if (this.responsesBuilder_.isEmpty()) {
                        this.responsesBuilder_.dispose();
                        this.responsesBuilder_ = null;
                        this.responses_ = other.responses_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.responsesBuilder_ = (GenericRefreshResponseCollectionProto.alwaysUseFieldBuilders ? this.getResponsesFieldBuilder() : null);
                    }
                    else {
                        this.responsesBuilder_.addAllMessages(other.responses_);
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
                GenericRefreshResponseCollectionProto parsedMessage = null;
                try {
                    parsedMessage = GenericRefreshResponseCollectionProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GenericRefreshResponseCollectionProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureResponsesIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.responses_ = new ArrayList<GenericRefreshResponseProto>(this.responses_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<GenericRefreshResponseProto> getResponsesList() {
                if (this.responsesBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends GenericRefreshResponseProto>)this.responses_);
                }
                return this.responsesBuilder_.getMessageList();
            }
            
            @Override
            public int getResponsesCount() {
                if (this.responsesBuilder_ == null) {
                    return this.responses_.size();
                }
                return this.responsesBuilder_.getCount();
            }
            
            @Override
            public GenericRefreshResponseProto getResponses(final int index) {
                if (this.responsesBuilder_ == null) {
                    return this.responses_.get(index);
                }
                return this.responsesBuilder_.getMessage(index);
            }
            
            public Builder setResponses(final int index, final GenericRefreshResponseProto value) {
                if (this.responsesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureResponsesIsMutable();
                    this.responses_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setResponses(final int index, final GenericRefreshResponseProto.Builder builderForValue) {
                if (this.responsesBuilder_ == null) {
                    this.ensureResponsesIsMutable();
                    this.responses_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addResponses(final GenericRefreshResponseProto value) {
                if (this.responsesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureResponsesIsMutable();
                    this.responses_.add(value);
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addResponses(final int index, final GenericRefreshResponseProto value) {
                if (this.responsesBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureResponsesIsMutable();
                    this.responses_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addResponses(final GenericRefreshResponseProto.Builder builderForValue) {
                if (this.responsesBuilder_ == null) {
                    this.ensureResponsesIsMutable();
                    this.responses_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addResponses(final int index, final GenericRefreshResponseProto.Builder builderForValue) {
                if (this.responsesBuilder_ == null) {
                    this.ensureResponsesIsMutable();
                    this.responses_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllResponses(final Iterable<? extends GenericRefreshResponseProto> values) {
                if (this.responsesBuilder_ == null) {
                    this.ensureResponsesIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.responses_);
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearResponses() {
                if (this.responsesBuilder_ == null) {
                    this.responses_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeResponses(final int index) {
                if (this.responsesBuilder_ == null) {
                    this.ensureResponsesIsMutable();
                    this.responses_.remove(index);
                    this.onChanged();
                }
                else {
                    this.responsesBuilder_.remove(index);
                }
                return this;
            }
            
            public GenericRefreshResponseProto.Builder getResponsesBuilder(final int index) {
                return this.getResponsesFieldBuilder().getBuilder(index);
            }
            
            @Override
            public GenericRefreshResponseProtoOrBuilder getResponsesOrBuilder(final int index) {
                if (this.responsesBuilder_ == null) {
                    return this.responses_.get(index);
                }
                return this.responsesBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends GenericRefreshResponseProtoOrBuilder> getResponsesOrBuilderList() {
                if (this.responsesBuilder_ != null) {
                    return this.responsesBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends GenericRefreshResponseProtoOrBuilder>)this.responses_);
            }
            
            public GenericRefreshResponseProto.Builder addResponsesBuilder() {
                return this.getResponsesFieldBuilder().addBuilder(GenericRefreshResponseProto.getDefaultInstance());
            }
            
            public GenericRefreshResponseProto.Builder addResponsesBuilder(final int index) {
                return this.getResponsesFieldBuilder().addBuilder(index, GenericRefreshResponseProto.getDefaultInstance());
            }
            
            public List<GenericRefreshResponseProto.Builder> getResponsesBuilderList() {
                return this.getResponsesFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<GenericRefreshResponseProto, GenericRefreshResponseProto.Builder, GenericRefreshResponseProtoOrBuilder> getResponsesFieldBuilder() {
                if (this.responsesBuilder_ == null) {
                    this.responsesBuilder_ = new RepeatedFieldBuilder<GenericRefreshResponseProto, GenericRefreshResponseProto.Builder, GenericRefreshResponseProtoOrBuilder>(this.responses_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.responses_ = null;
                }
                return this.responsesBuilder_;
            }
        }
    }
    
    public abstract static class GenericRefreshProtocolService implements Service
    {
        protected GenericRefreshProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new GenericRefreshProtocolService() {
                @Override
                public void refresh(final RpcController controller, final GenericRefreshRequestProto request, final RpcCallback<GenericRefreshResponseCollectionProto> done) {
                    impl.refresh(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return GenericRefreshProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != GenericRefreshProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.refresh(controller, (GenericRefreshRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != GenericRefreshProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GenericRefreshRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != GenericRefreshProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return GenericRefreshResponseCollectionProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void refresh(final RpcController p0, final GenericRefreshRequestProto p1, final RpcCallback<GenericRefreshResponseCollectionProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return GenericRefreshProtocolProtos.getDescriptor().getServices().get(0);
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
                    this.refresh(controller, (GenericRefreshRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return GenericRefreshRequestProto.getDefaultInstance();
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
                    return GenericRefreshResponseCollectionProto.getDefaultInstance();
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
        
        public static final class Stub extends GenericRefreshProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void refresh(final RpcController controller, final GenericRefreshRequestProto request, final RpcCallback<GenericRefreshResponseCollectionProto> done) {
                this.channel.callMethod(GenericRefreshProtocolService.getDescriptor().getMethods().get(0), controller, request, GenericRefreshResponseCollectionProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, GenericRefreshResponseCollectionProto.class, GenericRefreshResponseCollectionProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public GenericRefreshResponseCollectionProto refresh(final RpcController controller, final GenericRefreshRequestProto request) throws ServiceException {
                return (GenericRefreshResponseCollectionProto)this.channel.callBlockingMethod(GenericRefreshProtocolService.getDescriptor().getMethods().get(0), controller, request, GenericRefreshResponseCollectionProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            GenericRefreshResponseCollectionProto refresh(final RpcController p0, final GenericRefreshRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void refresh(final RpcController p0, final GenericRefreshRequestProto p1, final RpcCallback<GenericRefreshResponseCollectionProto> p2);
        }
    }
    
    public interface GenericRefreshResponseProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasExitStatus();
        
        int getExitStatus();
        
        boolean hasUserMessage();
        
        String getUserMessage();
        
        ByteString getUserMessageBytes();
        
        boolean hasSenderName();
        
        String getSenderName();
        
        ByteString getSenderNameBytes();
    }
    
    public interface GenericRefreshResponseCollectionProtoOrBuilder extends MessageOrBuilder
    {
        List<GenericRefreshResponseProto> getResponsesList();
        
        GenericRefreshResponseProto getResponses(final int p0);
        
        int getResponsesCount();
        
        List<? extends GenericRefreshResponseProtoOrBuilder> getResponsesOrBuilderList();
        
        GenericRefreshResponseProtoOrBuilder getResponsesOrBuilder(final int p0);
    }
    
    public interface GenericRefreshRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasIdentifier();
        
        String getIdentifier();
        
        ByteString getIdentifierBytes();
        
        List<String> getArgsList();
        
        int getArgsCount();
        
        String getArgs(final int p0);
        
        ByteString getArgsBytes(final int p0);
    }
}
