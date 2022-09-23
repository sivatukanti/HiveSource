// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.Internal;
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

public final class FSProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_fs_FsPermissionProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_fs_FsPermissionProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_fs_FileStatusProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_fs_FileStatusProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_fs_LocalFileSystemPathHandleProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private FSProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return FSProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u000eFSProtos.proto\u0012\thadoop.fs\"!\n\u0011FsPermissionProto\u0012\f\n\u0004perm\u0018\u0001 \u0002(\r\"\u00de\u0003\n\u000fFileStatusProto\u00125\n\bfileType\u0018\u0001 \u0002(\u000e2#.hadoop.fs.FileStatusProto.FileType\u0012\f\n\u0004path\u0018\u0002 \u0002(\t\u0012\u000e\n\u0006length\u0018\u0003 \u0001(\u0004\u00120\n\npermission\u0018\u0004 \u0001(\u000b2\u001c.hadoop.fs.FsPermissionProto\u0012\r\n\u0005owner\u0018\u0005 \u0001(\t\u0012\r\n\u0005group\u0018\u0006 \u0001(\t\u0012\u0019\n\u0011modification_time\u0018\u0007 \u0001(\u0004\u0012\u0013\n\u000baccess_time\u0018\b \u0001(\u0004\u0012\u000f\n\u0007symlink\u0018\t \u0001(\t\u0012\u0019\n\u0011block_replication\u0018\n \u0001(\r\u0012\u0012\n\nblock_size\u0018\u000b \u0001(\u0004\u0012\u0017\n\u000fencryption_data\u0018\u000f \u0001(\f\u0012\u000f\n\u0007ec_data\u0018\u0011 \u0001(", "\f\u0012\u0010\n\u0005flags\u0018\u0012 \u0001(\r:\u00010\"3\n\bFileType\u0012\n\n\u0006FT_DIR\u0010\u0001\u0012\u000b\n\u0007FT_FILE\u0010\u0002\u0012\u000e\n\nFT_SYMLINK\u0010\u0003\"E\n\u0005Flags\u0012\u000b\n\u0007HAS_ACL\u0010\u0001\u0012\r\n\tHAS_CRYPT\u0010\u0002\u0012\n\n\u0006HAS_EC\u0010\u0004\u0012\u0014\n\u0010SNAPSHOT_ENABLED\u0010\b\"=\n\u001eLocalFileSystemPathHandleProto\u0012\r\n\u0005mtime\u0018\u0001 \u0001(\u0004\u0012\f\n\u0004path\u0018\u0002 \u0001(\tB&\n\u0014org.apache.hadoop.fsB\bFSProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                FSProtos.descriptor = root;
                FSProtos.internal_static_hadoop_fs_FsPermissionProto_descriptor = FSProtos.getDescriptor().getMessageTypes().get(0);
                FSProtos.internal_static_hadoop_fs_FsPermissionProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(FSProtos.internal_static_hadoop_fs_FsPermissionProto_descriptor, new String[] { "Perm" });
                FSProtos.internal_static_hadoop_fs_FileStatusProto_descriptor = FSProtos.getDescriptor().getMessageTypes().get(1);
                FSProtos.internal_static_hadoop_fs_FileStatusProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(FSProtos.internal_static_hadoop_fs_FileStatusProto_descriptor, new String[] { "FileType", "Path", "Length", "Permission", "Owner", "Group", "ModificationTime", "AccessTime", "Symlink", "BlockReplication", "BlockSize", "EncryptionData", "EcData", "Flags" });
                FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor = FSProtos.getDescriptor().getMessageTypes().get(2);
                FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor, new String[] { "Mtime", "Path" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class FsPermissionProto extends GeneratedMessage implements FsPermissionProtoOrBuilder
    {
        private static final FsPermissionProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FsPermissionProto> PARSER;
        private int bitField0_;
        public static final int PERM_FIELD_NUMBER = 1;
        private int perm_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private FsPermissionProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FsPermissionProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FsPermissionProto getDefaultInstance() {
            return FsPermissionProto.defaultInstance;
        }
        
        @Override
        public FsPermissionProto getDefaultInstanceForType() {
            return FsPermissionProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FsPermissionProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.perm_ = input.readUInt32();
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
            return FSProtos.internal_static_hadoop_fs_FsPermissionProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return FSProtos.internal_static_hadoop_fs_FsPermissionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FsPermissionProto.class, Builder.class);
        }
        
        @Override
        public Parser<FsPermissionProto> getParserForType() {
            return FsPermissionProto.PARSER;
        }
        
        @Override
        public boolean hasPerm() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public int getPerm() {
            return this.perm_;
        }
        
        private void initFields() {
            this.perm_ = 0;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasPerm()) {
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
                output.writeUInt32(1, this.perm_);
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
                size += CodedOutputStream.computeUInt32Size(1, this.perm_);
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
            if (!(obj instanceof FsPermissionProto)) {
                return super.equals(obj);
            }
            final FsPermissionProto other = (FsPermissionProto)obj;
            boolean result = true;
            result = (result && this.hasPerm() == other.hasPerm());
            if (this.hasPerm()) {
                result = (result && this.getPerm() == other.getPerm());
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
            if (this.hasPerm()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getPerm();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static FsPermissionProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FsPermissionProto.PARSER.parseFrom(data);
        }
        
        public static FsPermissionProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FsPermissionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FsPermissionProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FsPermissionProto.PARSER.parseFrom(data);
        }
        
        public static FsPermissionProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FsPermissionProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FsPermissionProto parseFrom(final InputStream input) throws IOException {
            return FsPermissionProto.PARSER.parseFrom(input);
        }
        
        public static FsPermissionProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FsPermissionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FsPermissionProto parseDelimitedFrom(final InputStream input) throws IOException {
            return FsPermissionProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static FsPermissionProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FsPermissionProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FsPermissionProto parseFrom(final CodedInputStream input) throws IOException {
            return FsPermissionProto.PARSER.parseFrom(input);
        }
        
        public static FsPermissionProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FsPermissionProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FsPermissionProto prototype) {
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
            FsPermissionProto.PARSER = new AbstractParser<FsPermissionProto>() {
                @Override
                public FsPermissionProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FsPermissionProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new FsPermissionProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FsPermissionProtoOrBuilder
        {
            private int bitField0_;
            private int perm_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return FSProtos.internal_static_hadoop_fs_FsPermissionProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return FSProtos.internal_static_hadoop_fs_FsPermissionProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FsPermissionProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (FsPermissionProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.perm_ = 0;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return FSProtos.internal_static_hadoop_fs_FsPermissionProto_descriptor;
            }
            
            @Override
            public FsPermissionProto getDefaultInstanceForType() {
                return FsPermissionProto.getDefaultInstance();
            }
            
            @Override
            public FsPermissionProto build() {
                final FsPermissionProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public FsPermissionProto buildPartial() {
                final FsPermissionProto result = new FsPermissionProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.perm_ = this.perm_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FsPermissionProto) {
                    return this.mergeFrom((FsPermissionProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FsPermissionProto other) {
                if (other == FsPermissionProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasPerm()) {
                    this.setPerm(other.getPerm());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasPerm();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FsPermissionProto parsedMessage = null;
                try {
                    parsedMessage = FsPermissionProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FsPermissionProto)e.getUnfinishedMessage();
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
            public boolean hasPerm() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public int getPerm() {
                return this.perm_;
            }
            
            public Builder setPerm(final int value) {
                this.bitField0_ |= 0x1;
                this.perm_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPerm() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.perm_ = 0;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class FileStatusProto extends GeneratedMessage implements FileStatusProtoOrBuilder
    {
        private static final FileStatusProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FileStatusProto> PARSER;
        private int bitField0_;
        public static final int FILETYPE_FIELD_NUMBER = 1;
        private FileType fileType_;
        public static final int PATH_FIELD_NUMBER = 2;
        private Object path_;
        public static final int LENGTH_FIELD_NUMBER = 3;
        private long length_;
        public static final int PERMISSION_FIELD_NUMBER = 4;
        private FsPermissionProto permission_;
        public static final int OWNER_FIELD_NUMBER = 5;
        private Object owner_;
        public static final int GROUP_FIELD_NUMBER = 6;
        private Object group_;
        public static final int MODIFICATION_TIME_FIELD_NUMBER = 7;
        private long modificationTime_;
        public static final int ACCESS_TIME_FIELD_NUMBER = 8;
        private long accessTime_;
        public static final int SYMLINK_FIELD_NUMBER = 9;
        private Object symlink_;
        public static final int BLOCK_REPLICATION_FIELD_NUMBER = 10;
        private int blockReplication_;
        public static final int BLOCK_SIZE_FIELD_NUMBER = 11;
        private long blockSize_;
        public static final int ENCRYPTION_DATA_FIELD_NUMBER = 15;
        private ByteString encryptionData_;
        public static final int EC_DATA_FIELD_NUMBER = 17;
        private ByteString ecData_;
        public static final int FLAGS_FIELD_NUMBER = 18;
        private int flags_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private FileStatusProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FileStatusProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FileStatusProto getDefaultInstance() {
            return FileStatusProto.defaultInstance;
        }
        
        @Override
        public FileStatusProto getDefaultInstanceForType() {
            return FileStatusProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FileStatusProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            final FileType value = FileType.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x1;
                            this.fileType_ = value;
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.path_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x4;
                            this.length_ = input.readUInt64();
                            continue;
                        }
                        case 34: {
                            FsPermissionProto.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder = this.permission_.toBuilder();
                            }
                            this.permission_ = input.readMessage(FsPermissionProto.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.permission_);
                                this.permission_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 42: {
                            this.bitField0_ |= 0x10;
                            this.owner_ = input.readBytes();
                            continue;
                        }
                        case 50: {
                            this.bitField0_ |= 0x20;
                            this.group_ = input.readBytes();
                            continue;
                        }
                        case 56: {
                            this.bitField0_ |= 0x40;
                            this.modificationTime_ = input.readUInt64();
                            continue;
                        }
                        case 64: {
                            this.bitField0_ |= 0x80;
                            this.accessTime_ = input.readUInt64();
                            continue;
                        }
                        case 74: {
                            this.bitField0_ |= 0x100;
                            this.symlink_ = input.readBytes();
                            continue;
                        }
                        case 80: {
                            this.bitField0_ |= 0x200;
                            this.blockReplication_ = input.readUInt32();
                            continue;
                        }
                        case 88: {
                            this.bitField0_ |= 0x400;
                            this.blockSize_ = input.readUInt64();
                            continue;
                        }
                        case 122: {
                            this.bitField0_ |= 0x800;
                            this.encryptionData_ = input.readBytes();
                            continue;
                        }
                        case 138: {
                            this.bitField0_ |= 0x1000;
                            this.ecData_ = input.readBytes();
                            continue;
                        }
                        case 144: {
                            this.bitField0_ |= 0x2000;
                            this.flags_ = input.readUInt32();
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
            return FSProtos.internal_static_hadoop_fs_FileStatusProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return FSProtos.internal_static_hadoop_fs_FileStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileStatusProto.class, Builder.class);
        }
        
        @Override
        public Parser<FileStatusProto> getParserForType() {
            return FileStatusProto.PARSER;
        }
        
        @Override
        public boolean hasFileType() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public FileType getFileType() {
            return this.fileType_;
        }
        
        @Override
        public boolean hasPath() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getPath() {
            final Object ref = this.path_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.path_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getPathBytes() {
            final Object ref = this.path_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.path_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasLength() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        @Override
        public long getLength() {
            return this.length_;
        }
        
        @Override
        public boolean hasPermission() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        @Override
        public FsPermissionProto getPermission() {
            return this.permission_;
        }
        
        @Override
        public FsPermissionProtoOrBuilder getPermissionOrBuilder() {
            return this.permission_;
        }
        
        @Override
        public boolean hasOwner() {
            return (this.bitField0_ & 0x10) == 0x10;
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
        public boolean hasGroup() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        @Override
        public String getGroup() {
            final Object ref = this.group_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.group_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getGroupBytes() {
            final Object ref = this.group_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.group_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasModificationTime() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        @Override
        public long getModificationTime() {
            return this.modificationTime_;
        }
        
        @Override
        public boolean hasAccessTime() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime_;
        }
        
        @Override
        public boolean hasSymlink() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        @Override
        public String getSymlink() {
            final Object ref = this.symlink_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.symlink_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getSymlinkBytes() {
            final Object ref = this.symlink_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.symlink_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasBlockReplication() {
            return (this.bitField0_ & 0x200) == 0x200;
        }
        
        @Override
        public int getBlockReplication() {
            return this.blockReplication_;
        }
        
        @Override
        public boolean hasBlockSize() {
            return (this.bitField0_ & 0x400) == 0x400;
        }
        
        @Override
        public long getBlockSize() {
            return this.blockSize_;
        }
        
        @Override
        public boolean hasEncryptionData() {
            return (this.bitField0_ & 0x800) == 0x800;
        }
        
        @Override
        public ByteString getEncryptionData() {
            return this.encryptionData_;
        }
        
        @Override
        public boolean hasEcData() {
            return (this.bitField0_ & 0x1000) == 0x1000;
        }
        
        @Override
        public ByteString getEcData() {
            return this.ecData_;
        }
        
        @Override
        public boolean hasFlags() {
            return (this.bitField0_ & 0x2000) == 0x2000;
        }
        
        @Override
        public int getFlags() {
            return this.flags_;
        }
        
        private void initFields() {
            this.fileType_ = FileType.FT_DIR;
            this.path_ = "";
            this.length_ = 0L;
            this.permission_ = FsPermissionProto.getDefaultInstance();
            this.owner_ = "";
            this.group_ = "";
            this.modificationTime_ = 0L;
            this.accessTime_ = 0L;
            this.symlink_ = "";
            this.blockReplication_ = 0;
            this.blockSize_ = 0L;
            this.encryptionData_ = ByteString.EMPTY;
            this.ecData_ = ByteString.EMPTY;
            this.flags_ = 0;
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasFileType()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (!this.hasPath()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            if (this.hasPermission() && !this.getPermission().isInitialized()) {
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
                output.writeEnum(1, this.fileType_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getPathBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeUInt64(3, this.length_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.permission_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(5, this.getOwnerBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(6, this.getGroupBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeUInt64(7, this.modificationTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeUInt64(8, this.accessTime_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeBytes(9, this.getSymlinkBytes());
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                output.writeUInt32(10, this.blockReplication_);
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                output.writeUInt64(11, this.blockSize_);
            }
            if ((this.bitField0_ & 0x800) == 0x800) {
                output.writeBytes(15, this.encryptionData_);
            }
            if ((this.bitField0_ & 0x1000) == 0x1000) {
                output.writeBytes(17, this.ecData_);
            }
            if ((this.bitField0_ & 0x2000) == 0x2000) {
                output.writeUInt32(18, this.flags_);
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
                size += CodedOutputStream.computeEnumSize(1, this.fileType_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getPathBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeUInt64Size(3, this.length_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.permission_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(5, this.getOwnerBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(6, this.getGroupBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeUInt64Size(7, this.modificationTime_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeUInt64Size(8, this.accessTime_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeBytesSize(9, this.getSymlinkBytes());
            }
            if ((this.bitField0_ & 0x200) == 0x200) {
                size += CodedOutputStream.computeUInt32Size(10, this.blockReplication_);
            }
            if ((this.bitField0_ & 0x400) == 0x400) {
                size += CodedOutputStream.computeUInt64Size(11, this.blockSize_);
            }
            if ((this.bitField0_ & 0x800) == 0x800) {
                size += CodedOutputStream.computeBytesSize(15, this.encryptionData_);
            }
            if ((this.bitField0_ & 0x1000) == 0x1000) {
                size += CodedOutputStream.computeBytesSize(17, this.ecData_);
            }
            if ((this.bitField0_ & 0x2000) == 0x2000) {
                size += CodedOutputStream.computeUInt32Size(18, this.flags_);
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
            if (!(obj instanceof FileStatusProto)) {
                return super.equals(obj);
            }
            final FileStatusProto other = (FileStatusProto)obj;
            boolean result = true;
            result = (result && this.hasFileType() == other.hasFileType());
            if (this.hasFileType()) {
                result = (result && this.getFileType() == other.getFileType());
            }
            result = (result && this.hasPath() == other.hasPath());
            if (this.hasPath()) {
                result = (result && this.getPath().equals(other.getPath()));
            }
            result = (result && this.hasLength() == other.hasLength());
            if (this.hasLength()) {
                result = (result && this.getLength() == other.getLength());
            }
            result = (result && this.hasPermission() == other.hasPermission());
            if (this.hasPermission()) {
                result = (result && this.getPermission().equals(other.getPermission()));
            }
            result = (result && this.hasOwner() == other.hasOwner());
            if (this.hasOwner()) {
                result = (result && this.getOwner().equals(other.getOwner()));
            }
            result = (result && this.hasGroup() == other.hasGroup());
            if (this.hasGroup()) {
                result = (result && this.getGroup().equals(other.getGroup()));
            }
            result = (result && this.hasModificationTime() == other.hasModificationTime());
            if (this.hasModificationTime()) {
                result = (result && this.getModificationTime() == other.getModificationTime());
            }
            result = (result && this.hasAccessTime() == other.hasAccessTime());
            if (this.hasAccessTime()) {
                result = (result && this.getAccessTime() == other.getAccessTime());
            }
            result = (result && this.hasSymlink() == other.hasSymlink());
            if (this.hasSymlink()) {
                result = (result && this.getSymlink().equals(other.getSymlink()));
            }
            result = (result && this.hasBlockReplication() == other.hasBlockReplication());
            if (this.hasBlockReplication()) {
                result = (result && this.getBlockReplication() == other.getBlockReplication());
            }
            result = (result && this.hasBlockSize() == other.hasBlockSize());
            if (this.hasBlockSize()) {
                result = (result && this.getBlockSize() == other.getBlockSize());
            }
            result = (result && this.hasEncryptionData() == other.hasEncryptionData());
            if (this.hasEncryptionData()) {
                result = (result && this.getEncryptionData().equals(other.getEncryptionData()));
            }
            result = (result && this.hasEcData() == other.hasEcData());
            if (this.hasEcData()) {
                result = (result && this.getEcData().equals(other.getEcData()));
            }
            result = (result && this.hasFlags() == other.hasFlags());
            if (this.hasFlags()) {
                result = (result && this.getFlags() == other.getFlags());
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
            if (this.hasFileType()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashEnum(this.getFileType());
            }
            if (this.hasPath()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getPath().hashCode();
            }
            if (this.hasLength()) {
                hash = 37 * hash + 3;
                hash = 53 * hash + AbstractMessage.hashLong(this.getLength());
            }
            if (this.hasPermission()) {
                hash = 37 * hash + 4;
                hash = 53 * hash + this.getPermission().hashCode();
            }
            if (this.hasOwner()) {
                hash = 37 * hash + 5;
                hash = 53 * hash + this.getOwner().hashCode();
            }
            if (this.hasGroup()) {
                hash = 37 * hash + 6;
                hash = 53 * hash + this.getGroup().hashCode();
            }
            if (this.hasModificationTime()) {
                hash = 37 * hash + 7;
                hash = 53 * hash + AbstractMessage.hashLong(this.getModificationTime());
            }
            if (this.hasAccessTime()) {
                hash = 37 * hash + 8;
                hash = 53 * hash + AbstractMessage.hashLong(this.getAccessTime());
            }
            if (this.hasSymlink()) {
                hash = 37 * hash + 9;
                hash = 53 * hash + this.getSymlink().hashCode();
            }
            if (this.hasBlockReplication()) {
                hash = 37 * hash + 10;
                hash = 53 * hash + this.getBlockReplication();
            }
            if (this.hasBlockSize()) {
                hash = 37 * hash + 11;
                hash = 53 * hash + AbstractMessage.hashLong(this.getBlockSize());
            }
            if (this.hasEncryptionData()) {
                hash = 37 * hash + 15;
                hash = 53 * hash + this.getEncryptionData().hashCode();
            }
            if (this.hasEcData()) {
                hash = 37 * hash + 17;
                hash = 53 * hash + this.getEcData().hashCode();
            }
            if (this.hasFlags()) {
                hash = 37 * hash + 18;
                hash = 53 * hash + this.getFlags();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static FileStatusProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FileStatusProto.PARSER.parseFrom(data);
        }
        
        public static FileStatusProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileStatusProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FileStatusProto.PARSER.parseFrom(data);
        }
        
        public static FileStatusProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileStatusProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileStatusProto parseFrom(final InputStream input) throws IOException {
            return FileStatusProto.PARSER.parseFrom(input);
        }
        
        public static FileStatusProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FileStatusProto parseDelimitedFrom(final InputStream input) throws IOException {
            return FileStatusProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static FileStatusProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileStatusProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FileStatusProto parseFrom(final CodedInputStream input) throws IOException {
            return FileStatusProto.PARSER.parseFrom(input);
        }
        
        public static FileStatusProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileStatusProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FileStatusProto prototype) {
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
            FileStatusProto.PARSER = new AbstractParser<FileStatusProto>() {
                @Override
                public FileStatusProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FileStatusProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new FileStatusProto(true)).initFields();
        }
        
        public enum FileType implements ProtocolMessageEnum
        {
            FT_DIR(0, 1), 
            FT_FILE(1, 2), 
            FT_SYMLINK(2, 3);
            
            public static final int FT_DIR_VALUE = 1;
            public static final int FT_FILE_VALUE = 2;
            public static final int FT_SYMLINK_VALUE = 3;
            private static Internal.EnumLiteMap<FileType> internalValueMap;
            private static final FileType[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static FileType valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return FileType.FT_DIR;
                    }
                    case 2: {
                        return FileType.FT_FILE;
                    }
                    case 3: {
                        return FileType.FT_SYMLINK;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<FileType> internalGetValueMap() {
                return FileType.internalValueMap;
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
                return FileStatusProto.getDescriptor().getEnumTypes().get(0);
            }
            
            public static FileType valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return FileType.VALUES[desc.getIndex()];
            }
            
            private FileType(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                FileType.internalValueMap = new Internal.EnumLiteMap<FileType>() {
                    @Override
                    public FileType findValueByNumber(final int number) {
                        return FileType.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public enum Flags implements ProtocolMessageEnum
        {
            HAS_ACL(0, 1), 
            HAS_CRYPT(1, 2), 
            HAS_EC(2, 4), 
            SNAPSHOT_ENABLED(3, 8);
            
            public static final int HAS_ACL_VALUE = 1;
            public static final int HAS_CRYPT_VALUE = 2;
            public static final int HAS_EC_VALUE = 4;
            public static final int SNAPSHOT_ENABLED_VALUE = 8;
            private static Internal.EnumLiteMap<Flags> internalValueMap;
            private static final Flags[] VALUES;
            private final int index;
            private final int value;
            
            @Override
            public final int getNumber() {
                return this.value;
            }
            
            public static Flags valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return Flags.HAS_ACL;
                    }
                    case 2: {
                        return Flags.HAS_CRYPT;
                    }
                    case 4: {
                        return Flags.HAS_EC;
                    }
                    case 8: {
                        return Flags.SNAPSHOT_ENABLED;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<Flags> internalGetValueMap() {
                return Flags.internalValueMap;
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
                return FileStatusProto.getDescriptor().getEnumTypes().get(1);
            }
            
            public static Flags valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return Flags.VALUES[desc.getIndex()];
            }
            
            private Flags(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                Flags.internalValueMap = new Internal.EnumLiteMap<Flags>() {
                    @Override
                    public Flags findValueByNumber(final int number) {
                        return Flags.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FileStatusProtoOrBuilder
        {
            private int bitField0_;
            private FileType fileType_;
            private Object path_;
            private long length_;
            private FsPermissionProto permission_;
            private SingleFieldBuilder<FsPermissionProto, FsPermissionProto.Builder, FsPermissionProtoOrBuilder> permissionBuilder_;
            private Object owner_;
            private Object group_;
            private long modificationTime_;
            private long accessTime_;
            private Object symlink_;
            private int blockReplication_;
            private long blockSize_;
            private ByteString encryptionData_;
            private ByteString ecData_;
            private int flags_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return FSProtos.internal_static_hadoop_fs_FileStatusProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return FSProtos.internal_static_hadoop_fs_FileStatusProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileStatusProto.class, Builder.class);
            }
            
            private Builder() {
                this.fileType_ = FileType.FT_DIR;
                this.path_ = "";
                this.permission_ = FsPermissionProto.getDefaultInstance();
                this.owner_ = "";
                this.group_ = "";
                this.symlink_ = "";
                this.encryptionData_ = ByteString.EMPTY;
                this.ecData_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.fileType_ = FileType.FT_DIR;
                this.path_ = "";
                this.permission_ = FsPermissionProto.getDefaultInstance();
                this.owner_ = "";
                this.group_ = "";
                this.symlink_ = "";
                this.encryptionData_ = ByteString.EMPTY;
                this.ecData_ = ByteString.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (FileStatusProto.alwaysUseFieldBuilders) {
                    this.getPermissionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.fileType_ = FileType.FT_DIR;
                this.bitField0_ &= 0xFFFFFFFE;
                this.path_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.length_ = 0L;
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.permissionBuilder_ == null) {
                    this.permission_ = FsPermissionProto.getDefaultInstance();
                }
                else {
                    this.permissionBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                this.owner_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.group_ = "";
                this.bitField0_ &= 0xFFFFFFDF;
                this.modificationTime_ = 0L;
                this.bitField0_ &= 0xFFFFFFBF;
                this.accessTime_ = 0L;
                this.bitField0_ &= 0xFFFFFF7F;
                this.symlink_ = "";
                this.bitField0_ &= 0xFFFFFEFF;
                this.blockReplication_ = 0;
                this.bitField0_ &= 0xFFFFFDFF;
                this.blockSize_ = 0L;
                this.bitField0_ &= 0xFFFFFBFF;
                this.encryptionData_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFF7FF;
                this.ecData_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFEFFF;
                this.flags_ = 0;
                this.bitField0_ &= 0xFFFFDFFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return FSProtos.internal_static_hadoop_fs_FileStatusProto_descriptor;
            }
            
            @Override
            public FileStatusProto getDefaultInstanceForType() {
                return FileStatusProto.getDefaultInstance();
            }
            
            @Override
            public FileStatusProto build() {
                final FileStatusProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public FileStatusProto buildPartial() {
                final FileStatusProto result = new FileStatusProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.fileType_ = this.fileType_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.path_ = this.path_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.length_ = this.length_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                if (this.permissionBuilder_ == null) {
                    result.permission_ = this.permission_;
                }
                else {
                    result.permission_ = this.permissionBuilder_.build();
                }
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.owner_ = this.owner_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.group_ = this.group_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.modificationTime_ = this.modificationTime_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.accessTime_ = this.accessTime_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.symlink_ = this.symlink_;
                if ((from_bitField0_ & 0x200) == 0x200) {
                    to_bitField0_ |= 0x200;
                }
                result.blockReplication_ = this.blockReplication_;
                if ((from_bitField0_ & 0x400) == 0x400) {
                    to_bitField0_ |= 0x400;
                }
                result.blockSize_ = this.blockSize_;
                if ((from_bitField0_ & 0x800) == 0x800) {
                    to_bitField0_ |= 0x800;
                }
                result.encryptionData_ = this.encryptionData_;
                if ((from_bitField0_ & 0x1000) == 0x1000) {
                    to_bitField0_ |= 0x1000;
                }
                result.ecData_ = this.ecData_;
                if ((from_bitField0_ & 0x2000) == 0x2000) {
                    to_bitField0_ |= 0x2000;
                }
                result.flags_ = this.flags_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FileStatusProto) {
                    return this.mergeFrom((FileStatusProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FileStatusProto other) {
                if (other == FileStatusProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasFileType()) {
                    this.setFileType(other.getFileType());
                }
                if (other.hasPath()) {
                    this.bitField0_ |= 0x2;
                    this.path_ = other.path_;
                    this.onChanged();
                }
                if (other.hasLength()) {
                    this.setLength(other.getLength());
                }
                if (other.hasPermission()) {
                    this.mergePermission(other.getPermission());
                }
                if (other.hasOwner()) {
                    this.bitField0_ |= 0x10;
                    this.owner_ = other.owner_;
                    this.onChanged();
                }
                if (other.hasGroup()) {
                    this.bitField0_ |= 0x20;
                    this.group_ = other.group_;
                    this.onChanged();
                }
                if (other.hasModificationTime()) {
                    this.setModificationTime(other.getModificationTime());
                }
                if (other.hasAccessTime()) {
                    this.setAccessTime(other.getAccessTime());
                }
                if (other.hasSymlink()) {
                    this.bitField0_ |= 0x100;
                    this.symlink_ = other.symlink_;
                    this.onChanged();
                }
                if (other.hasBlockReplication()) {
                    this.setBlockReplication(other.getBlockReplication());
                }
                if (other.hasBlockSize()) {
                    this.setBlockSize(other.getBlockSize());
                }
                if (other.hasEncryptionData()) {
                    this.setEncryptionData(other.getEncryptionData());
                }
                if (other.hasEcData()) {
                    this.setEcData(other.getEcData());
                }
                if (other.hasFlags()) {
                    this.setFlags(other.getFlags());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasFileType() && this.hasPath() && (!this.hasPermission() || this.getPermission().isInitialized());
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FileStatusProto parsedMessage = null;
                try {
                    parsedMessage = FileStatusProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FileStatusProto)e.getUnfinishedMessage();
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
            public boolean hasFileType() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public FileType getFileType() {
                return this.fileType_;
            }
            
            public Builder setFileType(final FileType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.fileType_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFileType() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.fileType_ = FileType.FT_DIR;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasPath() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getPath() {
                final Object ref = this.path_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.path_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getPathBytes() {
                final Object ref = this.path_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.path_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setPath(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.path_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPath() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.path_ = FileStatusProto.getDefaultInstance().getPath();
                this.onChanged();
                return this;
            }
            
            public Builder setPathBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.path_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasLength() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            @Override
            public long getLength() {
                return this.length_;
            }
            
            public Builder setLength(final long value) {
                this.bitField0_ |= 0x4;
                this.length_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearLength() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.length_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasPermission() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            @Override
            public FsPermissionProto getPermission() {
                if (this.permissionBuilder_ == null) {
                    return this.permission_;
                }
                return this.permissionBuilder_.getMessage();
            }
            
            public Builder setPermission(final FsPermissionProto value) {
                if (this.permissionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.permission_ = value;
                    this.onChanged();
                }
                else {
                    this.permissionBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder setPermission(final FsPermissionProto.Builder builderForValue) {
                if (this.permissionBuilder_ == null) {
                    this.permission_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.permissionBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergePermission(final FsPermissionProto value) {
                if (this.permissionBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.permission_ != FsPermissionProto.getDefaultInstance()) {
                        this.permission_ = FsPermissionProto.newBuilder(this.permission_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.permission_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.permissionBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder clearPermission() {
                if (this.permissionBuilder_ == null) {
                    this.permission_ = FsPermissionProto.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.permissionBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public FsPermissionProto.Builder getPermissionBuilder() {
                this.bitField0_ |= 0x8;
                this.onChanged();
                return this.getPermissionFieldBuilder().getBuilder();
            }
            
            @Override
            public FsPermissionProtoOrBuilder getPermissionOrBuilder() {
                if (this.permissionBuilder_ != null) {
                    return this.permissionBuilder_.getMessageOrBuilder();
                }
                return this.permission_;
            }
            
            private SingleFieldBuilder<FsPermissionProto, FsPermissionProto.Builder, FsPermissionProtoOrBuilder> getPermissionFieldBuilder() {
                if (this.permissionBuilder_ == null) {
                    this.permissionBuilder_ = new SingleFieldBuilder<FsPermissionProto, FsPermissionProto.Builder, FsPermissionProtoOrBuilder>(this.permission_, this.getParentForChildren(), this.isClean());
                    this.permission_ = null;
                }
                return this.permissionBuilder_;
            }
            
            @Override
            public boolean hasOwner() {
                return (this.bitField0_ & 0x10) == 0x10;
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
                this.bitField0_ |= 0x10;
                this.owner_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearOwner() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.owner_ = FileStatusProto.getDefaultInstance().getOwner();
                this.onChanged();
                return this;
            }
            
            public Builder setOwnerBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.owner_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasGroup() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            @Override
            public String getGroup() {
                final Object ref = this.group_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.group_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getGroupBytes() {
                final Object ref = this.group_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.group_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setGroup(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.group_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearGroup() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.group_ = FileStatusProto.getDefaultInstance().getGroup();
                this.onChanged();
                return this;
            }
            
            public Builder setGroupBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.group_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasModificationTime() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            @Override
            public long getModificationTime() {
                return this.modificationTime_;
            }
            
            public Builder setModificationTime(final long value) {
                this.bitField0_ |= 0x40;
                this.modificationTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearModificationTime() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.modificationTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasAccessTime() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            @Override
            public long getAccessTime() {
                return this.accessTime_;
            }
            
            public Builder setAccessTime(final long value) {
                this.bitField0_ |= 0x80;
                this.accessTime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAccessTime() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.accessTime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasSymlink() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            @Override
            public String getSymlink() {
                final Object ref = this.symlink_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.symlink_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getSymlinkBytes() {
                final Object ref = this.symlink_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.symlink_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setSymlink(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.symlink_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearSymlink() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.symlink_ = FileStatusProto.getDefaultInstance().getSymlink();
                this.onChanged();
                return this;
            }
            
            public Builder setSymlinkBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x100;
                this.symlink_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasBlockReplication() {
                return (this.bitField0_ & 0x200) == 0x200;
            }
            
            @Override
            public int getBlockReplication() {
                return this.blockReplication_;
            }
            
            public Builder setBlockReplication(final int value) {
                this.bitField0_ |= 0x200;
                this.blockReplication_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearBlockReplication() {
                this.bitField0_ &= 0xFFFFFDFF;
                this.blockReplication_ = 0;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasBlockSize() {
                return (this.bitField0_ & 0x400) == 0x400;
            }
            
            @Override
            public long getBlockSize() {
                return this.blockSize_;
            }
            
            public Builder setBlockSize(final long value) {
                this.bitField0_ |= 0x400;
                this.blockSize_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearBlockSize() {
                this.bitField0_ &= 0xFFFFFBFF;
                this.blockSize_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasEncryptionData() {
                return (this.bitField0_ & 0x800) == 0x800;
            }
            
            @Override
            public ByteString getEncryptionData() {
                return this.encryptionData_;
            }
            
            public Builder setEncryptionData(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x800;
                this.encryptionData_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearEncryptionData() {
                this.bitField0_ &= 0xFFFFF7FF;
                this.encryptionData_ = FileStatusProto.getDefaultInstance().getEncryptionData();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasEcData() {
                return (this.bitField0_ & 0x1000) == 0x1000;
            }
            
            @Override
            public ByteString getEcData() {
                return this.ecData_;
            }
            
            public Builder setEcData(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1000;
                this.ecData_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearEcData() {
                this.bitField0_ &= 0xFFFFEFFF;
                this.ecData_ = FileStatusProto.getDefaultInstance().getEcData();
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasFlags() {
                return (this.bitField0_ & 0x2000) == 0x2000;
            }
            
            @Override
            public int getFlags() {
                return this.flags_;
            }
            
            public Builder setFlags(final int value) {
                this.bitField0_ |= 0x2000;
                this.flags_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearFlags() {
                this.bitField0_ &= 0xFFFFDFFF;
                this.flags_ = 0;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class LocalFileSystemPathHandleProto extends GeneratedMessage implements LocalFileSystemPathHandleProtoOrBuilder
    {
        private static final LocalFileSystemPathHandleProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<LocalFileSystemPathHandleProto> PARSER;
        private int bitField0_;
        public static final int MTIME_FIELD_NUMBER = 1;
        private long mtime_;
        public static final int PATH_FIELD_NUMBER = 2;
        private Object path_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private LocalFileSystemPathHandleProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private LocalFileSystemPathHandleProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static LocalFileSystemPathHandleProto getDefaultInstance() {
            return LocalFileSystemPathHandleProto.defaultInstance;
        }
        
        @Override
        public LocalFileSystemPathHandleProto getDefaultInstanceForType() {
            return LocalFileSystemPathHandleProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private LocalFileSystemPathHandleProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.mtime_ = input.readUInt64();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.path_ = input.readBytes();
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
            return FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_fieldAccessorTable.ensureFieldAccessorsInitialized(LocalFileSystemPathHandleProto.class, Builder.class);
        }
        
        @Override
        public Parser<LocalFileSystemPathHandleProto> getParserForType() {
            return LocalFileSystemPathHandleProto.PARSER;
        }
        
        @Override
        public boolean hasMtime() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public long getMtime() {
            return this.mtime_;
        }
        
        @Override
        public boolean hasPath() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getPath() {
            final Object ref = this.path_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.path_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getPathBytes() {
            final Object ref = this.path_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.path_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.mtime_ = 0L;
            this.path_ = "";
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
                output.writeUInt64(1, this.mtime_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getPathBytes());
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
                size += CodedOutputStream.computeUInt64Size(1, this.mtime_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getPathBytes());
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
            if (!(obj instanceof LocalFileSystemPathHandleProto)) {
                return super.equals(obj);
            }
            final LocalFileSystemPathHandleProto other = (LocalFileSystemPathHandleProto)obj;
            boolean result = true;
            result = (result && this.hasMtime() == other.hasMtime());
            if (this.hasMtime()) {
                result = (result && this.getMtime() == other.getMtime());
            }
            result = (result && this.hasPath() == other.hasPath());
            if (this.hasPath()) {
                result = (result && this.getPath().equals(other.getPath()));
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
            if (this.hasMtime()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + AbstractMessage.hashLong(this.getMtime());
            }
            if (this.hasPath()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getPath().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(data);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(data);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final InputStream input) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(input);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static LocalFileSystemPathHandleProto parseDelimitedFrom(final InputStream input) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static LocalFileSystemPathHandleProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final CodedInputStream input) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(input);
        }
        
        public static LocalFileSystemPathHandleProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return LocalFileSystemPathHandleProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final LocalFileSystemPathHandleProto prototype) {
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
            LocalFileSystemPathHandleProto.PARSER = new AbstractParser<LocalFileSystemPathHandleProto>() {
                @Override
                public LocalFileSystemPathHandleProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new LocalFileSystemPathHandleProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new LocalFileSystemPathHandleProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements LocalFileSystemPathHandleProtoOrBuilder
        {
            private int bitField0_;
            private long mtime_;
            private Object path_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_fieldAccessorTable.ensureFieldAccessorsInitialized(LocalFileSystemPathHandleProto.class, Builder.class);
            }
            
            private Builder() {
                this.path_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.path_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (LocalFileSystemPathHandleProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.mtime_ = 0L;
                this.bitField0_ &= 0xFFFFFFFE;
                this.path_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return FSProtos.internal_static_hadoop_fs_LocalFileSystemPathHandleProto_descriptor;
            }
            
            @Override
            public LocalFileSystemPathHandleProto getDefaultInstanceForType() {
                return LocalFileSystemPathHandleProto.getDefaultInstance();
            }
            
            @Override
            public LocalFileSystemPathHandleProto build() {
                final LocalFileSystemPathHandleProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public LocalFileSystemPathHandleProto buildPartial() {
                final LocalFileSystemPathHandleProto result = new LocalFileSystemPathHandleProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.mtime_ = this.mtime_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.path_ = this.path_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof LocalFileSystemPathHandleProto) {
                    return this.mergeFrom((LocalFileSystemPathHandleProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final LocalFileSystemPathHandleProto other) {
                if (other == LocalFileSystemPathHandleProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMtime()) {
                    this.setMtime(other.getMtime());
                }
                if (other.hasPath()) {
                    this.bitField0_ |= 0x2;
                    this.path_ = other.path_;
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
                LocalFileSystemPathHandleProto parsedMessage = null;
                try {
                    parsedMessage = LocalFileSystemPathHandleProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (LocalFileSystemPathHandleProto)e.getUnfinishedMessage();
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
            public boolean hasMtime() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public long getMtime() {
                return this.mtime_;
            }
            
            public Builder setMtime(final long value) {
                this.bitField0_ |= 0x1;
                this.mtime_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMtime() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.mtime_ = 0L;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasPath() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getPath() {
                final Object ref = this.path_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.path_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getPathBytes() {
                final Object ref = this.path_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.path_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setPath(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.path_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPath() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.path_ = LocalFileSystemPathHandleProto.getDefaultInstance().getPath();
                this.onChanged();
                return this;
            }
            
            public Builder setPathBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.path_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface LocalFileSystemPathHandleProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasMtime();
        
        long getMtime();
        
        boolean hasPath();
        
        String getPath();
        
        ByteString getPathBytes();
    }
    
    public interface FsPermissionProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasPerm();
        
        int getPerm();
    }
    
    public interface FileStatusProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasFileType();
        
        FileStatusProto.FileType getFileType();
        
        boolean hasPath();
        
        String getPath();
        
        ByteString getPathBytes();
        
        boolean hasLength();
        
        long getLength();
        
        boolean hasPermission();
        
        FsPermissionProto getPermission();
        
        FsPermissionProtoOrBuilder getPermissionOrBuilder();
        
        boolean hasOwner();
        
        String getOwner();
        
        ByteString getOwnerBytes();
        
        boolean hasGroup();
        
        String getGroup();
        
        ByteString getGroupBytes();
        
        boolean hasModificationTime();
        
        long getModificationTime();
        
        boolean hasAccessTime();
        
        long getAccessTime();
        
        boolean hasSymlink();
        
        String getSymlink();
        
        ByteString getSymlinkBytes();
        
        boolean hasBlockReplication();
        
        int getBlockReplication();
        
        boolean hasBlockSize();
        
        long getBlockSize();
        
        boolean hasEncryptionData();
        
        ByteString getEncryptionData();
        
        boolean hasEcData();
        
        ByteString getEcData();
        
        boolean hasFlags();
        
        int getFlags();
    }
}
