// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.RepeatedFieldBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import com.google.protobuf.UnmodifiableLazyStringList;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
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

public final class YarnServerResourceManagerServiceProtos
{
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshQueuesRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshQueuesResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshNodesRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshNodesResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_GetGroupsForUserRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_GetGroupsForUserResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_hadoop_yarn_ActiveRMInfoProto_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private YarnServerResourceManagerServiceProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return YarnServerResourceManagerServiceProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n7server/yarn_server_resourcemanager_service_protos.proto\u0012\u000bhadoop.yarn\u001a\u0011yarn_protos.proto\"\u001b\n\u0019RefreshQueuesRequestProto\"\u001c\n\u001aRefreshQueuesResponseProto\"\u001a\n\u0018RefreshNodesRequestProto\"\u001b\n\u0019RefreshNodesResponseProto\"1\n/RefreshSuperUserGroupsConfigurationRequestProto\"2\n0RefreshSuperUserGroupsConfigurationResponseProto\")\n'RefreshUserToGroupsMappingsRequestProto\"*\n(RefreshUserToGroupsMappingsResponseProto\"\u001e\n\u001cR", "efreshAdminAclsRequestProto\"\u001f\n\u001dRefreshAdminAclsResponseProto\" \n\u001eRefreshServiceAclsRequestProto\"!\n\u001fRefreshServiceAclsResponseProto\",\n\u001cGetGroupsForUserRequestProto\u0012\f\n\u0004user\u0018\u0001 \u0002(\t\"/\n\u001dGetGroupsForUserResponseProto\u0012\u000e\n\u0006groups\u0018\u0001 \u0003(\t\"^\n\u001eUpdateNodeResourceRequestProto\u0012<\n\u0011node_resource_map\u0018\u0001 \u0003(\u000b2!.hadoop.yarn.NodeResourceMapProto\"!\n\u001fUpdateNodeResourceResponseProto\"8\n\"AddToClusterNodeLabelsRequestProto\u0012\u0012\n\nnod", "eLabels\u0018\u0001 \u0003(\t\"%\n#AddToClusterNodeLabelsResponseProto\"=\n'RemoveFromClusterNodeLabelsRequestProto\u0012\u0012\n\nnodeLabels\u0018\u0001 \u0003(\t\"*\n(RemoveFromClusterNodeLabelsResponseProto\"Y\n\u001fReplaceLabelsOnNodeRequestProto\u00126\n\fnodeToLabels\u0018\u0001 \u0003(\u000b2 .hadoop.yarn.NodeIdToLabelsProto\"\"\n ReplaceLabelsOnNodeResponseProto\"4\n\u0011ActiveRMInfoProto\u0012\u0011\n\tclusterId\u0018\u0001 \u0001(\t\u0012\f\n\u0004rmId\u0018\u0002 \u0001(\tBL\n\u001corg.apache.hadoop.yarn.protoB&YarnServerResourceManagerS", "erviceProtos\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                YarnServerResourceManagerServiceProtos.descriptor = root;
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(0);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(1);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(2);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(3);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(4);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(5);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(6);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(7);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(8);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(9);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(10);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(11);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(12);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor, new String[] { "User" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(13);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor, new String[] { "Groups" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(14);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor, new String[] { "NodeResourceMap" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(15);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(16);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor, new String[] { "NodeLabels" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(17);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(18);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor, new String[] { "NodeLabels" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(19);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(20);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor, new String[] { "NodeToLabels" });
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(21);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor, new String[0]);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor = YarnServerResourceManagerServiceProtos.getDescriptor().getMessageTypes().get(22);
                YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor, new String[] { "ClusterId", "RmId" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnProtos.getDescriptor() }, assigner);
    }
    
    public static final class RefreshQueuesRequestProto extends GeneratedMessage implements RefreshQueuesRequestProtoOrBuilder
    {
        private static final RefreshQueuesRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshQueuesRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshQueuesRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshQueuesRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshQueuesRequestProto getDefaultInstance() {
            return RefreshQueuesRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshQueuesRequestProto getDefaultInstanceForType() {
            return RefreshQueuesRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshQueuesRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshQueuesRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshQueuesRequestProto> getParserForType() {
            return RefreshQueuesRequestProto.PARSER;
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
            if (!(obj instanceof RefreshQueuesRequestProto)) {
                return super.equals(obj);
            }
            final RefreshQueuesRequestProto other = (RefreshQueuesRequestProto)obj;
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
        
        public static RefreshQueuesRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshQueuesRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshQueuesRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshQueuesRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshQueuesRequestProto prototype) {
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
            RefreshQueuesRequestProto.PARSER = new AbstractParser<RefreshQueuesRequestProto>() {
                @Override
                public RefreshQueuesRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshQueuesRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshQueuesRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshQueuesRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshQueuesRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshQueuesRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesRequestProto_descriptor;
            }
            
            @Override
            public RefreshQueuesRequestProto getDefaultInstanceForType() {
                return RefreshQueuesRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshQueuesRequestProto build() {
                final RefreshQueuesRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshQueuesRequestProto buildPartial() {
                final RefreshQueuesRequestProto result = new RefreshQueuesRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshQueuesRequestProto) {
                    return this.mergeFrom((RefreshQueuesRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshQueuesRequestProto other) {
                if (other == RefreshQueuesRequestProto.getDefaultInstance()) {
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
                RefreshQueuesRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshQueuesRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshQueuesRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshQueuesResponseProto extends GeneratedMessage implements RefreshQueuesResponseProtoOrBuilder
    {
        private static final RefreshQueuesResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshQueuesResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshQueuesResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshQueuesResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshQueuesResponseProto getDefaultInstance() {
            return RefreshQueuesResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshQueuesResponseProto getDefaultInstanceForType() {
            return RefreshQueuesResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshQueuesResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshQueuesResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshQueuesResponseProto> getParserForType() {
            return RefreshQueuesResponseProto.PARSER;
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
            if (!(obj instanceof RefreshQueuesResponseProto)) {
                return super.equals(obj);
            }
            final RefreshQueuesResponseProto other = (RefreshQueuesResponseProto)obj;
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
        
        public static RefreshQueuesResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshQueuesResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshQueuesResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshQueuesResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshQueuesResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshQueuesResponseProto prototype) {
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
            RefreshQueuesResponseProto.PARSER = new AbstractParser<RefreshQueuesResponseProto>() {
                @Override
                public RefreshQueuesResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshQueuesResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshQueuesResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshQueuesResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshQueuesResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshQueuesResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshQueuesResponseProto_descriptor;
            }
            
            @Override
            public RefreshQueuesResponseProto getDefaultInstanceForType() {
                return RefreshQueuesResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshQueuesResponseProto build() {
                final RefreshQueuesResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshQueuesResponseProto buildPartial() {
                final RefreshQueuesResponseProto result = new RefreshQueuesResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshQueuesResponseProto) {
                    return this.mergeFrom((RefreshQueuesResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshQueuesResponseProto other) {
                if (other == RefreshQueuesResponseProto.getDefaultInstance()) {
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
                RefreshQueuesResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshQueuesResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshQueuesResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshNodesRequestProto extends GeneratedMessage implements RefreshNodesRequestProtoOrBuilder
    {
        private static final RefreshNodesRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshNodesRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshNodesRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshNodesRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshNodesRequestProto getDefaultInstance() {
            return RefreshNodesRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshNodesRequestProto getDefaultInstanceForType() {
            return RefreshNodesRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshNodesRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshNodesRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshNodesRequestProto> getParserForType() {
            return RefreshNodesRequestProto.PARSER;
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
            if (!(obj instanceof RefreshNodesRequestProto)) {
                return super.equals(obj);
            }
            final RefreshNodesRequestProto other = (RefreshNodesRequestProto)obj;
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
        
        public static RefreshNodesRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshNodesRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshNodesRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshNodesRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshNodesRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshNodesRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshNodesRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshNodesRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshNodesRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshNodesRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshNodesRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshNodesRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshNodesRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshNodesRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshNodesRequestProto prototype) {
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
            RefreshNodesRequestProto.PARSER = new AbstractParser<RefreshNodesRequestProto>() {
                @Override
                public RefreshNodesRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshNodesRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshNodesRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshNodesRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshNodesRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshNodesRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesRequestProto_descriptor;
            }
            
            @Override
            public RefreshNodesRequestProto getDefaultInstanceForType() {
                return RefreshNodesRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshNodesRequestProto build() {
                final RefreshNodesRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshNodesRequestProto buildPartial() {
                final RefreshNodesRequestProto result = new RefreshNodesRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshNodesRequestProto) {
                    return this.mergeFrom((RefreshNodesRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshNodesRequestProto other) {
                if (other == RefreshNodesRequestProto.getDefaultInstance()) {
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
                RefreshNodesRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshNodesRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshNodesRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshNodesResponseProto extends GeneratedMessage implements RefreshNodesResponseProtoOrBuilder
    {
        private static final RefreshNodesResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshNodesResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshNodesResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshNodesResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshNodesResponseProto getDefaultInstance() {
            return RefreshNodesResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshNodesResponseProto getDefaultInstanceForType() {
            return RefreshNodesResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshNodesResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshNodesResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshNodesResponseProto> getParserForType() {
            return RefreshNodesResponseProto.PARSER;
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
            if (!(obj instanceof RefreshNodesResponseProto)) {
                return super.equals(obj);
            }
            final RefreshNodesResponseProto other = (RefreshNodesResponseProto)obj;
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
        
        public static RefreshNodesResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshNodesResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshNodesResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshNodesResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshNodesResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshNodesResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshNodesResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshNodesResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshNodesResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshNodesResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshNodesResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshNodesResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshNodesResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshNodesResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshNodesResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshNodesResponseProto prototype) {
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
            RefreshNodesResponseProto.PARSER = new AbstractParser<RefreshNodesResponseProto>() {
                @Override
                public RefreshNodesResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshNodesResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshNodesResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshNodesResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshNodesResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshNodesResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshNodesResponseProto_descriptor;
            }
            
            @Override
            public RefreshNodesResponseProto getDefaultInstanceForType() {
                return RefreshNodesResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshNodesResponseProto build() {
                final RefreshNodesResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshNodesResponseProto buildPartial() {
                final RefreshNodesResponseProto result = new RefreshNodesResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshNodesResponseProto) {
                    return this.mergeFrom((RefreshNodesResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshNodesResponseProto other) {
                if (other == RefreshNodesResponseProto.getDefaultInstance()) {
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
                RefreshNodesResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshNodesResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshNodesResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshSuperUserGroupsConfigurationRequestProto extends GeneratedMessage implements RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder
    {
        private static final RefreshSuperUserGroupsConfigurationRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshSuperUserGroupsConfigurationRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstance() {
            return RefreshSuperUserGroupsConfigurationRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstanceForType() {
            return RefreshSuperUserGroupsConfigurationRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshSuperUserGroupsConfigurationRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshSuperUserGroupsConfigurationRequestProto> getParserForType() {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER;
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
            if (!(obj instanceof RefreshSuperUserGroupsConfigurationRequestProto)) {
                return super.equals(obj);
            }
            final RefreshSuperUserGroupsConfigurationRequestProto other = (RefreshSuperUserGroupsConfigurationRequestProto)obj;
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
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshSuperUserGroupsConfigurationRequestProto prototype) {
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
            RefreshSuperUserGroupsConfigurationRequestProto.PARSER = new AbstractParser<RefreshSuperUserGroupsConfigurationRequestProto>() {
                @Override
                public RefreshSuperUserGroupsConfigurationRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshSuperUserGroupsConfigurationRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshSuperUserGroupsConfigurationRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshSuperUserGroupsConfigurationRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationRequestProto_descriptor;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto getDefaultInstanceForType() {
                return RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto build() {
                final RefreshSuperUserGroupsConfigurationRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationRequestProto buildPartial() {
                final RefreshSuperUserGroupsConfigurationRequestProto result = new RefreshSuperUserGroupsConfigurationRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshSuperUserGroupsConfigurationRequestProto) {
                    return this.mergeFrom((RefreshSuperUserGroupsConfigurationRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshSuperUserGroupsConfigurationRequestProto other) {
                if (other == RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance()) {
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
                RefreshSuperUserGroupsConfigurationRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshSuperUserGroupsConfigurationRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshSuperUserGroupsConfigurationRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshSuperUserGroupsConfigurationResponseProto extends GeneratedMessage implements RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder
    {
        private static final RefreshSuperUserGroupsConfigurationResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshSuperUserGroupsConfigurationResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstance() {
            return RefreshSuperUserGroupsConfigurationResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstanceForType() {
            return RefreshSuperUserGroupsConfigurationResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshSuperUserGroupsConfigurationResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshSuperUserGroupsConfigurationResponseProto> getParserForType() {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER;
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
            if (!(obj instanceof RefreshSuperUserGroupsConfigurationResponseProto)) {
                return super.equals(obj);
            }
            final RefreshSuperUserGroupsConfigurationResponseProto other = (RefreshSuperUserGroupsConfigurationResponseProto)obj;
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
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshSuperUserGroupsConfigurationResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshSuperUserGroupsConfigurationResponseProto prototype) {
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
            RefreshSuperUserGroupsConfigurationResponseProto.PARSER = new AbstractParser<RefreshSuperUserGroupsConfigurationResponseProto>() {
                @Override
                public RefreshSuperUserGroupsConfigurationResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshSuperUserGroupsConfigurationResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshSuperUserGroupsConfigurationResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshSuperUserGroupsConfigurationResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshSuperUserGroupsConfigurationResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshSuperUserGroupsConfigurationResponseProto_descriptor;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto getDefaultInstanceForType() {
                return RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto build() {
                final RefreshSuperUserGroupsConfigurationResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshSuperUserGroupsConfigurationResponseProto buildPartial() {
                final RefreshSuperUserGroupsConfigurationResponseProto result = new RefreshSuperUserGroupsConfigurationResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshSuperUserGroupsConfigurationResponseProto) {
                    return this.mergeFrom((RefreshSuperUserGroupsConfigurationResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshSuperUserGroupsConfigurationResponseProto other) {
                if (other == RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance()) {
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
                RefreshSuperUserGroupsConfigurationResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshSuperUserGroupsConfigurationResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshSuperUserGroupsConfigurationResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshUserToGroupsMappingsRequestProto extends GeneratedMessage implements RefreshUserToGroupsMappingsRequestProtoOrBuilder
    {
        private static final RefreshUserToGroupsMappingsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshUserToGroupsMappingsRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshUserToGroupsMappingsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshUserToGroupsMappingsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshUserToGroupsMappingsRequestProto getDefaultInstance() {
            return RefreshUserToGroupsMappingsRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshUserToGroupsMappingsRequestProto getDefaultInstanceForType() {
            return RefreshUserToGroupsMappingsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshUserToGroupsMappingsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshUserToGroupsMappingsRequestProto> getParserForType() {
            return RefreshUserToGroupsMappingsRequestProto.PARSER;
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
            if (!(obj instanceof RefreshUserToGroupsMappingsRequestProto)) {
                return super.equals(obj);
            }
            final RefreshUserToGroupsMappingsRequestProto other = (RefreshUserToGroupsMappingsRequestProto)obj;
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
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshUserToGroupsMappingsRequestProto prototype) {
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
            RefreshUserToGroupsMappingsRequestProto.PARSER = new AbstractParser<RefreshUserToGroupsMappingsRequestProto>() {
                @Override
                public RefreshUserToGroupsMappingsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshUserToGroupsMappingsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshUserToGroupsMappingsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshUserToGroupsMappingsRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshUserToGroupsMappingsRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsRequestProto_descriptor;
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto getDefaultInstanceForType() {
                return RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto build() {
                final RefreshUserToGroupsMappingsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshUserToGroupsMappingsRequestProto buildPartial() {
                final RefreshUserToGroupsMappingsRequestProto result = new RefreshUserToGroupsMappingsRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshUserToGroupsMappingsRequestProto) {
                    return this.mergeFrom((RefreshUserToGroupsMappingsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshUserToGroupsMappingsRequestProto other) {
                if (other == RefreshUserToGroupsMappingsRequestProto.getDefaultInstance()) {
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
                RefreshUserToGroupsMappingsRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshUserToGroupsMappingsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshUserToGroupsMappingsRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshUserToGroupsMappingsResponseProto extends GeneratedMessage implements RefreshUserToGroupsMappingsResponseProtoOrBuilder
    {
        private static final RefreshUserToGroupsMappingsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshUserToGroupsMappingsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshUserToGroupsMappingsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshUserToGroupsMappingsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshUserToGroupsMappingsResponseProto getDefaultInstance() {
            return RefreshUserToGroupsMappingsResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshUserToGroupsMappingsResponseProto getDefaultInstanceForType() {
            return RefreshUserToGroupsMappingsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshUserToGroupsMappingsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshUserToGroupsMappingsResponseProto> getParserForType() {
            return RefreshUserToGroupsMappingsResponseProto.PARSER;
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
            if (!(obj instanceof RefreshUserToGroupsMappingsResponseProto)) {
                return super.equals(obj);
            }
            final RefreshUserToGroupsMappingsResponseProto other = (RefreshUserToGroupsMappingsResponseProto)obj;
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
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshUserToGroupsMappingsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshUserToGroupsMappingsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshUserToGroupsMappingsResponseProto prototype) {
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
            RefreshUserToGroupsMappingsResponseProto.PARSER = new AbstractParser<RefreshUserToGroupsMappingsResponseProto>() {
                @Override
                public RefreshUserToGroupsMappingsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshUserToGroupsMappingsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshUserToGroupsMappingsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshUserToGroupsMappingsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshUserToGroupsMappingsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshUserToGroupsMappingsResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshUserToGroupsMappingsResponseProto_descriptor;
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto getDefaultInstanceForType() {
                return RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto build() {
                final RefreshUserToGroupsMappingsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshUserToGroupsMappingsResponseProto buildPartial() {
                final RefreshUserToGroupsMappingsResponseProto result = new RefreshUserToGroupsMappingsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshUserToGroupsMappingsResponseProto) {
                    return this.mergeFrom((RefreshUserToGroupsMappingsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshUserToGroupsMappingsResponseProto other) {
                if (other == RefreshUserToGroupsMappingsResponseProto.getDefaultInstance()) {
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
                RefreshUserToGroupsMappingsResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshUserToGroupsMappingsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshUserToGroupsMappingsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshAdminAclsRequestProto extends GeneratedMessage implements RefreshAdminAclsRequestProtoOrBuilder
    {
        private static final RefreshAdminAclsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshAdminAclsRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshAdminAclsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshAdminAclsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshAdminAclsRequestProto getDefaultInstance() {
            return RefreshAdminAclsRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshAdminAclsRequestProto getDefaultInstanceForType() {
            return RefreshAdminAclsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshAdminAclsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshAdminAclsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshAdminAclsRequestProto> getParserForType() {
            return RefreshAdminAclsRequestProto.PARSER;
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
            if (!(obj instanceof RefreshAdminAclsRequestProto)) {
                return super.equals(obj);
            }
            final RefreshAdminAclsRequestProto other = (RefreshAdminAclsRequestProto)obj;
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
        
        public static RefreshAdminAclsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshAdminAclsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshAdminAclsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshAdminAclsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshAdminAclsRequestProto prototype) {
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
            RefreshAdminAclsRequestProto.PARSER = new AbstractParser<RefreshAdminAclsRequestProto>() {
                @Override
                public RefreshAdminAclsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshAdminAclsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshAdminAclsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshAdminAclsRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshAdminAclsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshAdminAclsRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsRequestProto_descriptor;
            }
            
            @Override
            public RefreshAdminAclsRequestProto getDefaultInstanceForType() {
                return RefreshAdminAclsRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshAdminAclsRequestProto build() {
                final RefreshAdminAclsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshAdminAclsRequestProto buildPartial() {
                final RefreshAdminAclsRequestProto result = new RefreshAdminAclsRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshAdminAclsRequestProto) {
                    return this.mergeFrom((RefreshAdminAclsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshAdminAclsRequestProto other) {
                if (other == RefreshAdminAclsRequestProto.getDefaultInstance()) {
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
                RefreshAdminAclsRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshAdminAclsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshAdminAclsRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshAdminAclsResponseProto extends GeneratedMessage implements RefreshAdminAclsResponseProtoOrBuilder
    {
        private static final RefreshAdminAclsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshAdminAclsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshAdminAclsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshAdminAclsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshAdminAclsResponseProto getDefaultInstance() {
            return RefreshAdminAclsResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshAdminAclsResponseProto getDefaultInstanceForType() {
            return RefreshAdminAclsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshAdminAclsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshAdminAclsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshAdminAclsResponseProto> getParserForType() {
            return RefreshAdminAclsResponseProto.PARSER;
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
            if (!(obj instanceof RefreshAdminAclsResponseProto)) {
                return super.equals(obj);
            }
            final RefreshAdminAclsResponseProto other = (RefreshAdminAclsResponseProto)obj;
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
        
        public static RefreshAdminAclsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshAdminAclsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshAdminAclsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshAdminAclsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshAdminAclsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshAdminAclsResponseProto prototype) {
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
            RefreshAdminAclsResponseProto.PARSER = new AbstractParser<RefreshAdminAclsResponseProto>() {
                @Override
                public RefreshAdminAclsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshAdminAclsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshAdminAclsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshAdminAclsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshAdminAclsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshAdminAclsResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshAdminAclsResponseProto_descriptor;
            }
            
            @Override
            public RefreshAdminAclsResponseProto getDefaultInstanceForType() {
                return RefreshAdminAclsResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshAdminAclsResponseProto build() {
                final RefreshAdminAclsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshAdminAclsResponseProto buildPartial() {
                final RefreshAdminAclsResponseProto result = new RefreshAdminAclsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshAdminAclsResponseProto) {
                    return this.mergeFrom((RefreshAdminAclsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshAdminAclsResponseProto other) {
                if (other == RefreshAdminAclsResponseProto.getDefaultInstance()) {
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
                RefreshAdminAclsResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshAdminAclsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshAdminAclsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshServiceAclsRequestProto extends GeneratedMessage implements RefreshServiceAclsRequestProtoOrBuilder
    {
        private static final RefreshServiceAclsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshServiceAclsRequestProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshServiceAclsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshServiceAclsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshServiceAclsRequestProto getDefaultInstance() {
            return RefreshServiceAclsRequestProto.defaultInstance;
        }
        
        @Override
        public RefreshServiceAclsRequestProto getDefaultInstanceForType() {
            return RefreshServiceAclsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshServiceAclsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshServiceAclsRequestProto> getParserForType() {
            return RefreshServiceAclsRequestProto.PARSER;
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
            if (!(obj instanceof RefreshServiceAclsRequestProto)) {
                return super.equals(obj);
            }
            final RefreshServiceAclsRequestProto other = (RefreshServiceAclsRequestProto)obj;
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
        
        public static RefreshServiceAclsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final InputStream input) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshServiceAclsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshServiceAclsRequestProto prototype) {
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
            RefreshServiceAclsRequestProto.PARSER = new AbstractParser<RefreshServiceAclsRequestProto>() {
                @Override
                public RefreshServiceAclsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshServiceAclsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshServiceAclsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshServiceAclsRequestProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshServiceAclsRequestProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsRequestProto_descriptor;
            }
            
            @Override
            public RefreshServiceAclsRequestProto getDefaultInstanceForType() {
                return RefreshServiceAclsRequestProto.getDefaultInstance();
            }
            
            @Override
            public RefreshServiceAclsRequestProto build() {
                final RefreshServiceAclsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshServiceAclsRequestProto buildPartial() {
                final RefreshServiceAclsRequestProto result = new RefreshServiceAclsRequestProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshServiceAclsRequestProto) {
                    return this.mergeFrom((RefreshServiceAclsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshServiceAclsRequestProto other) {
                if (other == RefreshServiceAclsRequestProto.getDefaultInstance()) {
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
                RefreshServiceAclsRequestProto parsedMessage = null;
                try {
                    parsedMessage = RefreshServiceAclsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshServiceAclsRequestProto)e.getUnfinishedMessage();
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
    
    public static final class RefreshServiceAclsResponseProto extends GeneratedMessage implements RefreshServiceAclsResponseProtoOrBuilder
    {
        private static final RefreshServiceAclsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RefreshServiceAclsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RefreshServiceAclsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RefreshServiceAclsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RefreshServiceAclsResponseProto getDefaultInstance() {
            return RefreshServiceAclsResponseProto.defaultInstance;
        }
        
        @Override
        public RefreshServiceAclsResponseProto getDefaultInstanceForType() {
            return RefreshServiceAclsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RefreshServiceAclsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RefreshServiceAclsResponseProto> getParserForType() {
            return RefreshServiceAclsResponseProto.PARSER;
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
            if (!(obj instanceof RefreshServiceAclsResponseProto)) {
                return super.equals(obj);
            }
            final RefreshServiceAclsResponseProto other = (RefreshServiceAclsResponseProto)obj;
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
        
        public static RefreshServiceAclsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final InputStream input) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RefreshServiceAclsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RefreshServiceAclsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RefreshServiceAclsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RefreshServiceAclsResponseProto prototype) {
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
            RefreshServiceAclsResponseProto.PARSER = new AbstractParser<RefreshServiceAclsResponseProto>() {
                @Override
                public RefreshServiceAclsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RefreshServiceAclsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RefreshServiceAclsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RefreshServiceAclsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RefreshServiceAclsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RefreshServiceAclsResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RefreshServiceAclsResponseProto_descriptor;
            }
            
            @Override
            public RefreshServiceAclsResponseProto getDefaultInstanceForType() {
                return RefreshServiceAclsResponseProto.getDefaultInstance();
            }
            
            @Override
            public RefreshServiceAclsResponseProto build() {
                final RefreshServiceAclsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RefreshServiceAclsResponseProto buildPartial() {
                final RefreshServiceAclsResponseProto result = new RefreshServiceAclsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RefreshServiceAclsResponseProto) {
                    return this.mergeFrom((RefreshServiceAclsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RefreshServiceAclsResponseProto other) {
                if (other == RefreshServiceAclsResponseProto.getDefaultInstance()) {
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
                RefreshServiceAclsResponseProto parsedMessage = null;
                try {
                    parsedMessage = RefreshServiceAclsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RefreshServiceAclsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class GetGroupsForUserRequestProto extends GeneratedMessage implements GetGroupsForUserRequestProtoOrBuilder
    {
        private static final GetGroupsForUserRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetGroupsForUserRequestProto> PARSER;
        private int bitField0_;
        public static final int USER_FIELD_NUMBER = 1;
        private Object user_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetGroupsForUserRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetGroupsForUserRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetGroupsForUserRequestProto getDefaultInstance() {
            return GetGroupsForUserRequestProto.defaultInstance;
        }
        
        @Override
        public GetGroupsForUserRequestProto getDefaultInstanceForType() {
            return GetGroupsForUserRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetGroupsForUserRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.user_ = input.readBytes();
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetGroupsForUserRequestProto> getParserForType() {
            return GetGroupsForUserRequestProto.PARSER;
        }
        
        @Override
        public boolean hasUser() {
            return (this.bitField0_ & 0x1) == 0x1;
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
        
        private void initFields() {
            this.user_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (!this.hasUser()) {
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
                output.writeBytes(1, this.getUserBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getUserBytes());
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
            if (!(obj instanceof GetGroupsForUserRequestProto)) {
                return super.equals(obj);
            }
            final GetGroupsForUserRequestProto other = (GetGroupsForUserRequestProto)obj;
            boolean result = true;
            result = (result && this.hasUser() == other.hasUser());
            if (this.hasUser()) {
                result = (result && this.getUser().equals(other.getUser()));
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
            if (this.hasUser()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getUser().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final InputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetGroupsForUserRequestProto prototype) {
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
            GetGroupsForUserRequestProto.PARSER = new AbstractParser<GetGroupsForUserRequestProto>() {
                @Override
                public GetGroupsForUserRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetGroupsForUserRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetGroupsForUserRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetGroupsForUserRequestProtoOrBuilder
        {
            private int bitField0_;
            private Object user_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.user_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.user_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetGroupsForUserRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.user_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserRequestProto_descriptor;
            }
            
            @Override
            public GetGroupsForUserRequestProto getDefaultInstanceForType() {
                return GetGroupsForUserRequestProto.getDefaultInstance();
            }
            
            @Override
            public GetGroupsForUserRequestProto build() {
                final GetGroupsForUserRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetGroupsForUserRequestProto buildPartial() {
                final GetGroupsForUserRequestProto result = new GetGroupsForUserRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.user_ = this.user_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetGroupsForUserRequestProto) {
                    return this.mergeFrom((GetGroupsForUserRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetGroupsForUserRequestProto other) {
                if (other == GetGroupsForUserRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasUser()) {
                    this.bitField0_ |= 0x1;
                    this.user_ = other.user_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return this.hasUser();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                GetGroupsForUserRequestProto parsedMessage = null;
                try {
                    parsedMessage = GetGroupsForUserRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetGroupsForUserRequestProto)e.getUnfinishedMessage();
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
            public boolean hasUser() {
                return (this.bitField0_ & 0x1) == 0x1;
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
                this.bitField0_ |= 0x1;
                this.user_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearUser() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.user_ = GetGroupsForUserRequestProto.getDefaultInstance().getUser();
                this.onChanged();
                return this;
            }
            
            public Builder setUserBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.user_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class GetGroupsForUserResponseProto extends GeneratedMessage implements GetGroupsForUserResponseProtoOrBuilder
    {
        private static final GetGroupsForUserResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<GetGroupsForUserResponseProto> PARSER;
        public static final int GROUPS_FIELD_NUMBER = 1;
        private LazyStringList groups_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private GetGroupsForUserResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private GetGroupsForUserResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static GetGroupsForUserResponseProto getDefaultInstance() {
            return GetGroupsForUserResponseProto.defaultInstance;
        }
        
        @Override
        public GetGroupsForUserResponseProto getDefaultInstanceForType() {
            return GetGroupsForUserResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private GetGroupsForUserResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.groups_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.groups_.add(input.readBytes());
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
                    this.groups_ = new UnmodifiableLazyStringList(this.groups_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<GetGroupsForUserResponseProto> getParserForType() {
            return GetGroupsForUserResponseProto.PARSER;
        }
        
        @Override
        public List<String> getGroupsList() {
            return this.groups_;
        }
        
        @Override
        public int getGroupsCount() {
            return this.groups_.size();
        }
        
        @Override
        public String getGroups(final int index) {
            return this.groups_.get(index);
        }
        
        @Override
        public ByteString getGroupsBytes(final int index) {
            return this.groups_.getByteString(index);
        }
        
        private void initFields() {
            this.groups_ = LazyStringArrayList.EMPTY;
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
            for (int i = 0; i < this.groups_.size(); ++i) {
                output.writeBytes(1, this.groups_.getByteString(i));
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
            int dataSize = 0;
            for (int i = 0; i < this.groups_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.groups_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getGroupsList().size();
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
            if (!(obj instanceof GetGroupsForUserResponseProto)) {
                return super.equals(obj);
            }
            final GetGroupsForUserResponseProto other = (GetGroupsForUserResponseProto)obj;
            boolean result = true;
            result = (result && this.getGroupsList().equals(other.getGroupsList()));
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
            if (this.getGroupsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getGroupsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final InputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input);
        }
        
        public static GetGroupsForUserResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return GetGroupsForUserResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final GetGroupsForUserResponseProto prototype) {
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
            GetGroupsForUserResponseProto.PARSER = new AbstractParser<GetGroupsForUserResponseProto>() {
                @Override
                public GetGroupsForUserResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new GetGroupsForUserResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new GetGroupsForUserResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements GetGroupsForUserResponseProtoOrBuilder
        {
            private int bitField0_;
            private LazyStringList groups_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(GetGroupsForUserResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.groups_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.groups_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GetGroupsForUserResponseProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.groups_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_GetGroupsForUserResponseProto_descriptor;
            }
            
            @Override
            public GetGroupsForUserResponseProto getDefaultInstanceForType() {
                return GetGroupsForUserResponseProto.getDefaultInstance();
            }
            
            @Override
            public GetGroupsForUserResponseProto build() {
                final GetGroupsForUserResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public GetGroupsForUserResponseProto buildPartial() {
                final GetGroupsForUserResponseProto result = new GetGroupsForUserResponseProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    this.groups_ = new UnmodifiableLazyStringList(this.groups_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.groups_ = this.groups_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof GetGroupsForUserResponseProto) {
                    return this.mergeFrom((GetGroupsForUserResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final GetGroupsForUserResponseProto other) {
                if (other == GetGroupsForUserResponseProto.getDefaultInstance()) {
                    return this;
                }
                if (!other.groups_.isEmpty()) {
                    if (this.groups_.isEmpty()) {
                        this.groups_ = other.groups_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    else {
                        this.ensureGroupsIsMutable();
                        this.groups_.addAll(other.groups_);
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
                GetGroupsForUserResponseProto parsedMessage = null;
                try {
                    parsedMessage = GetGroupsForUserResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (GetGroupsForUserResponseProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureGroupsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.groups_ = new LazyStringArrayList(this.groups_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<String> getGroupsList() {
                return Collections.unmodifiableList((List<? extends String>)this.groups_);
            }
            
            @Override
            public int getGroupsCount() {
                return this.groups_.size();
            }
            
            @Override
            public String getGroups(final int index) {
                return this.groups_.get(index);
            }
            
            @Override
            public ByteString getGroupsBytes(final int index) {
                return this.groups_.getByteString(index);
            }
            
            public Builder setGroups(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addGroups(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllGroups(final Iterable<String> values) {
                this.ensureGroupsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.groups_);
                this.onChanged();
                return this;
            }
            
            public Builder clearGroups() {
                this.groups_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }
            
            public Builder addGroupsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureGroupsIsMutable();
                this.groups_.add(value);
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class UpdateNodeResourceRequestProto extends GeneratedMessage implements UpdateNodeResourceRequestProtoOrBuilder
    {
        private static final UpdateNodeResourceRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<UpdateNodeResourceRequestProto> PARSER;
        public static final int NODE_RESOURCE_MAP_FIELD_NUMBER = 1;
        private List<YarnProtos.NodeResourceMapProto> nodeResourceMap_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private UpdateNodeResourceRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private UpdateNodeResourceRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static UpdateNodeResourceRequestProto getDefaultInstance() {
            return UpdateNodeResourceRequestProto.defaultInstance;
        }
        
        @Override
        public UpdateNodeResourceRequestProto getDefaultInstanceForType() {
            return UpdateNodeResourceRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private UpdateNodeResourceRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.nodeResourceMap_ = new ArrayList<YarnProtos.NodeResourceMapProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.nodeResourceMap_.add(input.readMessage(YarnProtos.NodeResourceMapProto.PARSER, extensionRegistry));
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
                    this.nodeResourceMap_ = Collections.unmodifiableList((List<? extends YarnProtos.NodeResourceMapProto>)this.nodeResourceMap_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateNodeResourceRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<UpdateNodeResourceRequestProto> getParserForType() {
            return UpdateNodeResourceRequestProto.PARSER;
        }
        
        @Override
        public List<YarnProtos.NodeResourceMapProto> getNodeResourceMapList() {
            return this.nodeResourceMap_;
        }
        
        @Override
        public List<? extends YarnProtos.NodeResourceMapProtoOrBuilder> getNodeResourceMapOrBuilderList() {
            return this.nodeResourceMap_;
        }
        
        @Override
        public int getNodeResourceMapCount() {
            return this.nodeResourceMap_.size();
        }
        
        @Override
        public YarnProtos.NodeResourceMapProto getNodeResourceMap(final int index) {
            return this.nodeResourceMap_.get(index);
        }
        
        @Override
        public YarnProtos.NodeResourceMapProtoOrBuilder getNodeResourceMapOrBuilder(final int index) {
            return this.nodeResourceMap_.get(index);
        }
        
        private void initFields() {
            this.nodeResourceMap_ = Collections.emptyList();
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
            for (int i = 0; i < this.nodeResourceMap_.size(); ++i) {
                output.writeMessage(1, this.nodeResourceMap_.get(i));
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
            for (int i = 0; i < this.nodeResourceMap_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.nodeResourceMap_.get(i));
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
            if (!(obj instanceof UpdateNodeResourceRequestProto)) {
                return super.equals(obj);
            }
            final UpdateNodeResourceRequestProto other = (UpdateNodeResourceRequestProto)obj;
            boolean result = true;
            result = (result && this.getNodeResourceMapList().equals(other.getNodeResourceMapList()));
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
            if (this.getNodeResourceMapCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeResourceMapList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(data);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(data);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final InputStream input) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(input);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static UpdateNodeResourceRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static UpdateNodeResourceRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(input);
        }
        
        public static UpdateNodeResourceRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final UpdateNodeResourceRequestProto prototype) {
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
            UpdateNodeResourceRequestProto.PARSER = new AbstractParser<UpdateNodeResourceRequestProto>() {
                @Override
                public UpdateNodeResourceRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new UpdateNodeResourceRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new UpdateNodeResourceRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements UpdateNodeResourceRequestProtoOrBuilder
        {
            private int bitField0_;
            private List<YarnProtos.NodeResourceMapProto> nodeResourceMap_;
            private RepeatedFieldBuilder<YarnProtos.NodeResourceMapProto, YarnProtos.NodeResourceMapProto.Builder, YarnProtos.NodeResourceMapProtoOrBuilder> nodeResourceMapBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateNodeResourceRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeResourceMap_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeResourceMap_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (UpdateNodeResourceRequestProto.alwaysUseFieldBuilders) {
                    this.getNodeResourceMapFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.nodeResourceMapBuilder_ == null) {
                    this.nodeResourceMap_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.nodeResourceMapBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceRequestProto_descriptor;
            }
            
            @Override
            public UpdateNodeResourceRequestProto getDefaultInstanceForType() {
                return UpdateNodeResourceRequestProto.getDefaultInstance();
            }
            
            @Override
            public UpdateNodeResourceRequestProto build() {
                final UpdateNodeResourceRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public UpdateNodeResourceRequestProto buildPartial() {
                final UpdateNodeResourceRequestProto result = new UpdateNodeResourceRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.nodeResourceMapBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.nodeResourceMap_ = Collections.unmodifiableList((List<? extends YarnProtos.NodeResourceMapProto>)this.nodeResourceMap_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.nodeResourceMap_ = this.nodeResourceMap_;
                }
                else {
                    result.nodeResourceMap_ = this.nodeResourceMapBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof UpdateNodeResourceRequestProto) {
                    return this.mergeFrom((UpdateNodeResourceRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final UpdateNodeResourceRequestProto other) {
                if (other == UpdateNodeResourceRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (this.nodeResourceMapBuilder_ == null) {
                    if (!other.nodeResourceMap_.isEmpty()) {
                        if (this.nodeResourceMap_.isEmpty()) {
                            this.nodeResourceMap_ = other.nodeResourceMap_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureNodeResourceMapIsMutable();
                            this.nodeResourceMap_.addAll(other.nodeResourceMap_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.nodeResourceMap_.isEmpty()) {
                    if (this.nodeResourceMapBuilder_.isEmpty()) {
                        this.nodeResourceMapBuilder_.dispose();
                        this.nodeResourceMapBuilder_ = null;
                        this.nodeResourceMap_ = other.nodeResourceMap_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.nodeResourceMapBuilder_ = (UpdateNodeResourceRequestProto.alwaysUseFieldBuilders ? this.getNodeResourceMapFieldBuilder() : null);
                    }
                    else {
                        this.nodeResourceMapBuilder_.addAllMessages(other.nodeResourceMap_);
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
                UpdateNodeResourceRequestProto parsedMessage = null;
                try {
                    parsedMessage = UpdateNodeResourceRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (UpdateNodeResourceRequestProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureNodeResourceMapIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.nodeResourceMap_ = new ArrayList<YarnProtos.NodeResourceMapProto>(this.nodeResourceMap_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<YarnProtos.NodeResourceMapProto> getNodeResourceMapList() {
                if (this.nodeResourceMapBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.NodeResourceMapProto>)this.nodeResourceMap_);
                }
                return this.nodeResourceMapBuilder_.getMessageList();
            }
            
            @Override
            public int getNodeResourceMapCount() {
                if (this.nodeResourceMapBuilder_ == null) {
                    return this.nodeResourceMap_.size();
                }
                return this.nodeResourceMapBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.NodeResourceMapProto getNodeResourceMap(final int index) {
                if (this.nodeResourceMapBuilder_ == null) {
                    return this.nodeResourceMap_.get(index);
                }
                return this.nodeResourceMapBuilder_.getMessage(index);
            }
            
            public Builder setNodeResourceMap(final int index, final YarnProtos.NodeResourceMapProto value) {
                if (this.nodeResourceMapBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setNodeResourceMap(final int index, final YarnProtos.NodeResourceMapProto.Builder builderForValue) {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addNodeResourceMap(final YarnProtos.NodeResourceMapProto value) {
                if (this.nodeResourceMapBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.add(value);
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addNodeResourceMap(final int index, final YarnProtos.NodeResourceMapProto value) {
                if (this.nodeResourceMapBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addNodeResourceMap(final YarnProtos.NodeResourceMapProto.Builder builderForValue) {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addNodeResourceMap(final int index, final YarnProtos.NodeResourceMapProto.Builder builderForValue) {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllNodeResourceMap(final Iterable<? extends YarnProtos.NodeResourceMapProto> values) {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.ensureNodeResourceMapIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.nodeResourceMap_);
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearNodeResourceMap() {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.nodeResourceMap_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeNodeResourceMap(final int index) {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.ensureNodeResourceMapIsMutable();
                    this.nodeResourceMap_.remove(index);
                    this.onChanged();
                }
                else {
                    this.nodeResourceMapBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.NodeResourceMapProto.Builder getNodeResourceMapBuilder(final int index) {
                return this.getNodeResourceMapFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.NodeResourceMapProtoOrBuilder getNodeResourceMapOrBuilder(final int index) {
                if (this.nodeResourceMapBuilder_ == null) {
                    return this.nodeResourceMap_.get(index);
                }
                return this.nodeResourceMapBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.NodeResourceMapProtoOrBuilder> getNodeResourceMapOrBuilderList() {
                if (this.nodeResourceMapBuilder_ != null) {
                    return this.nodeResourceMapBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.NodeResourceMapProtoOrBuilder>)this.nodeResourceMap_);
            }
            
            public YarnProtos.NodeResourceMapProto.Builder addNodeResourceMapBuilder() {
                return this.getNodeResourceMapFieldBuilder().addBuilder(YarnProtos.NodeResourceMapProto.getDefaultInstance());
            }
            
            public YarnProtos.NodeResourceMapProto.Builder addNodeResourceMapBuilder(final int index) {
                return this.getNodeResourceMapFieldBuilder().addBuilder(index, YarnProtos.NodeResourceMapProto.getDefaultInstance());
            }
            
            public List<YarnProtos.NodeResourceMapProto.Builder> getNodeResourceMapBuilderList() {
                return this.getNodeResourceMapFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.NodeResourceMapProto, YarnProtos.NodeResourceMapProto.Builder, YarnProtos.NodeResourceMapProtoOrBuilder> getNodeResourceMapFieldBuilder() {
                if (this.nodeResourceMapBuilder_ == null) {
                    this.nodeResourceMapBuilder_ = new RepeatedFieldBuilder<YarnProtos.NodeResourceMapProto, YarnProtos.NodeResourceMapProto.Builder, YarnProtos.NodeResourceMapProtoOrBuilder>(this.nodeResourceMap_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.nodeResourceMap_ = null;
                }
                return this.nodeResourceMapBuilder_;
            }
        }
    }
    
    public static final class UpdateNodeResourceResponseProto extends GeneratedMessage implements UpdateNodeResourceResponseProtoOrBuilder
    {
        private static final UpdateNodeResourceResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<UpdateNodeResourceResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private UpdateNodeResourceResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private UpdateNodeResourceResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static UpdateNodeResourceResponseProto getDefaultInstance() {
            return UpdateNodeResourceResponseProto.defaultInstance;
        }
        
        @Override
        public UpdateNodeResourceResponseProto getDefaultInstanceForType() {
            return UpdateNodeResourceResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private UpdateNodeResourceResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateNodeResourceResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<UpdateNodeResourceResponseProto> getParserForType() {
            return UpdateNodeResourceResponseProto.PARSER;
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
            if (!(obj instanceof UpdateNodeResourceResponseProto)) {
                return super.equals(obj);
            }
            final UpdateNodeResourceResponseProto other = (UpdateNodeResourceResponseProto)obj;
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
        
        public static UpdateNodeResourceResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(data);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(data);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final InputStream input) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(input);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static UpdateNodeResourceResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static UpdateNodeResourceResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(input);
        }
        
        public static UpdateNodeResourceResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UpdateNodeResourceResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final UpdateNodeResourceResponseProto prototype) {
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
            UpdateNodeResourceResponseProto.PARSER = new AbstractParser<UpdateNodeResourceResponseProto>() {
                @Override
                public UpdateNodeResourceResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new UpdateNodeResourceResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new UpdateNodeResourceResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements UpdateNodeResourceResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(UpdateNodeResourceResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (UpdateNodeResourceResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_UpdateNodeResourceResponseProto_descriptor;
            }
            
            @Override
            public UpdateNodeResourceResponseProto getDefaultInstanceForType() {
                return UpdateNodeResourceResponseProto.getDefaultInstance();
            }
            
            @Override
            public UpdateNodeResourceResponseProto build() {
                final UpdateNodeResourceResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public UpdateNodeResourceResponseProto buildPartial() {
                final UpdateNodeResourceResponseProto result = new UpdateNodeResourceResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof UpdateNodeResourceResponseProto) {
                    return this.mergeFrom((UpdateNodeResourceResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final UpdateNodeResourceResponseProto other) {
                if (other == UpdateNodeResourceResponseProto.getDefaultInstance()) {
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
                UpdateNodeResourceResponseProto parsedMessage = null;
                try {
                    parsedMessage = UpdateNodeResourceResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (UpdateNodeResourceResponseProto)e.getUnfinishedMessage();
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
    
    public static final class AddToClusterNodeLabelsRequestProto extends GeneratedMessage implements AddToClusterNodeLabelsRequestProtoOrBuilder
    {
        private static final AddToClusterNodeLabelsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AddToClusterNodeLabelsRequestProto> PARSER;
        public static final int NODELABELS_FIELD_NUMBER = 1;
        private LazyStringList nodeLabels_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AddToClusterNodeLabelsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AddToClusterNodeLabelsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AddToClusterNodeLabelsRequestProto getDefaultInstance() {
            return AddToClusterNodeLabelsRequestProto.defaultInstance;
        }
        
        @Override
        public AddToClusterNodeLabelsRequestProto getDefaultInstanceForType() {
            return AddToClusterNodeLabelsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AddToClusterNodeLabelsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.nodeLabels_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.nodeLabels_.add(input.readBytes());
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
                    this.nodeLabels_ = new UnmodifiableLazyStringList(this.nodeLabels_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddToClusterNodeLabelsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<AddToClusterNodeLabelsRequestProto> getParserForType() {
            return AddToClusterNodeLabelsRequestProto.PARSER;
        }
        
        @Override
        public List<String> getNodeLabelsList() {
            return this.nodeLabels_;
        }
        
        @Override
        public int getNodeLabelsCount() {
            return this.nodeLabels_.size();
        }
        
        @Override
        public String getNodeLabels(final int index) {
            return this.nodeLabels_.get(index);
        }
        
        @Override
        public ByteString getNodeLabelsBytes(final int index) {
            return this.nodeLabels_.getByteString(index);
        }
        
        private void initFields() {
            this.nodeLabels_ = LazyStringArrayList.EMPTY;
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
            for (int i = 0; i < this.nodeLabels_.size(); ++i) {
                output.writeBytes(1, this.nodeLabels_.getByteString(i));
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
            int dataSize = 0;
            for (int i = 0; i < this.nodeLabels_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.nodeLabels_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getNodeLabelsList().size();
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
            if (!(obj instanceof AddToClusterNodeLabelsRequestProto)) {
                return super.equals(obj);
            }
            final AddToClusterNodeLabelsRequestProto other = (AddToClusterNodeLabelsRequestProto)obj;
            boolean result = true;
            result = (result && this.getNodeLabelsList().equals(other.getNodeLabelsList()));
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
            if (this.getNodeLabelsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeLabelsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(data);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(data);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final InputStream input) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(input);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(input);
        }
        
        public static AddToClusterNodeLabelsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AddToClusterNodeLabelsRequestProto prototype) {
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
            AddToClusterNodeLabelsRequestProto.PARSER = new AbstractParser<AddToClusterNodeLabelsRequestProto>() {
                @Override
                public AddToClusterNodeLabelsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AddToClusterNodeLabelsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AddToClusterNodeLabelsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AddToClusterNodeLabelsRequestProtoOrBuilder
        {
            private int bitField0_;
            private LazyStringList nodeLabels_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddToClusterNodeLabelsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AddToClusterNodeLabelsRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsRequestProto_descriptor;
            }
            
            @Override
            public AddToClusterNodeLabelsRequestProto getDefaultInstanceForType() {
                return AddToClusterNodeLabelsRequestProto.getDefaultInstance();
            }
            
            @Override
            public AddToClusterNodeLabelsRequestProto build() {
                final AddToClusterNodeLabelsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AddToClusterNodeLabelsRequestProto buildPartial() {
                final AddToClusterNodeLabelsRequestProto result = new AddToClusterNodeLabelsRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    this.nodeLabels_ = new UnmodifiableLazyStringList(this.nodeLabels_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.nodeLabels_ = this.nodeLabels_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AddToClusterNodeLabelsRequestProto) {
                    return this.mergeFrom((AddToClusterNodeLabelsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AddToClusterNodeLabelsRequestProto other) {
                if (other == AddToClusterNodeLabelsRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (!other.nodeLabels_.isEmpty()) {
                    if (this.nodeLabels_.isEmpty()) {
                        this.nodeLabels_ = other.nodeLabels_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    else {
                        this.ensureNodeLabelsIsMutable();
                        this.nodeLabels_.addAll(other.nodeLabels_);
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
                AddToClusterNodeLabelsRequestProto parsedMessage = null;
                try {
                    parsedMessage = AddToClusterNodeLabelsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AddToClusterNodeLabelsRequestProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureNodeLabelsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.nodeLabels_ = new LazyStringArrayList(this.nodeLabels_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<String> getNodeLabelsList() {
                return Collections.unmodifiableList((List<? extends String>)this.nodeLabels_);
            }
            
            @Override
            public int getNodeLabelsCount() {
                return this.nodeLabels_.size();
            }
            
            @Override
            public String getNodeLabels(final int index) {
                return this.nodeLabels_.get(index);
            }
            
            @Override
            public ByteString getNodeLabelsBytes(final int index) {
                return this.nodeLabels_.getByteString(index);
            }
            
            public Builder setNodeLabels(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addNodeLabels(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllNodeLabels(final Iterable<String> values) {
                this.ensureNodeLabelsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.nodeLabels_);
                this.onChanged();
                return this;
            }
            
            public Builder clearNodeLabels() {
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }
            
            public Builder addNodeLabelsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.add(value);
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class AddToClusterNodeLabelsResponseProto extends GeneratedMessage implements AddToClusterNodeLabelsResponseProtoOrBuilder
    {
        private static final AddToClusterNodeLabelsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<AddToClusterNodeLabelsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private AddToClusterNodeLabelsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private AddToClusterNodeLabelsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static AddToClusterNodeLabelsResponseProto getDefaultInstance() {
            return AddToClusterNodeLabelsResponseProto.defaultInstance;
        }
        
        @Override
        public AddToClusterNodeLabelsResponseProto getDefaultInstanceForType() {
            return AddToClusterNodeLabelsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private AddToClusterNodeLabelsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddToClusterNodeLabelsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<AddToClusterNodeLabelsResponseProto> getParserForType() {
            return AddToClusterNodeLabelsResponseProto.PARSER;
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
            if (!(obj instanceof AddToClusterNodeLabelsResponseProto)) {
                return super.equals(obj);
            }
            final AddToClusterNodeLabelsResponseProto other = (AddToClusterNodeLabelsResponseProto)obj;
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
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(data);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(data);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final InputStream input) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(input);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(input);
        }
        
        public static AddToClusterNodeLabelsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return AddToClusterNodeLabelsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final AddToClusterNodeLabelsResponseProto prototype) {
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
            AddToClusterNodeLabelsResponseProto.PARSER = new AbstractParser<AddToClusterNodeLabelsResponseProto>() {
                @Override
                public AddToClusterNodeLabelsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new AddToClusterNodeLabelsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new AddToClusterNodeLabelsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements AddToClusterNodeLabelsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(AddToClusterNodeLabelsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (AddToClusterNodeLabelsResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_AddToClusterNodeLabelsResponseProto_descriptor;
            }
            
            @Override
            public AddToClusterNodeLabelsResponseProto getDefaultInstanceForType() {
                return AddToClusterNodeLabelsResponseProto.getDefaultInstance();
            }
            
            @Override
            public AddToClusterNodeLabelsResponseProto build() {
                final AddToClusterNodeLabelsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public AddToClusterNodeLabelsResponseProto buildPartial() {
                final AddToClusterNodeLabelsResponseProto result = new AddToClusterNodeLabelsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof AddToClusterNodeLabelsResponseProto) {
                    return this.mergeFrom((AddToClusterNodeLabelsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final AddToClusterNodeLabelsResponseProto other) {
                if (other == AddToClusterNodeLabelsResponseProto.getDefaultInstance()) {
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
                AddToClusterNodeLabelsResponseProto parsedMessage = null;
                try {
                    parsedMessage = AddToClusterNodeLabelsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (AddToClusterNodeLabelsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class RemoveFromClusterNodeLabelsRequestProto extends GeneratedMessage implements RemoveFromClusterNodeLabelsRequestProtoOrBuilder
    {
        private static final RemoveFromClusterNodeLabelsRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RemoveFromClusterNodeLabelsRequestProto> PARSER;
        public static final int NODELABELS_FIELD_NUMBER = 1;
        private LazyStringList nodeLabels_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RemoveFromClusterNodeLabelsRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RemoveFromClusterNodeLabelsRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto getDefaultInstance() {
            return RemoveFromClusterNodeLabelsRequestProto.defaultInstance;
        }
        
        @Override
        public RemoveFromClusterNodeLabelsRequestProto getDefaultInstanceForType() {
            return RemoveFromClusterNodeLabelsRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RemoveFromClusterNodeLabelsRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.nodeLabels_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.nodeLabels_.add(input.readBytes());
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
                    this.nodeLabels_ = new UnmodifiableLazyStringList(this.nodeLabels_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveFromClusterNodeLabelsRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<RemoveFromClusterNodeLabelsRequestProto> getParserForType() {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER;
        }
        
        @Override
        public List<String> getNodeLabelsList() {
            return this.nodeLabels_;
        }
        
        @Override
        public int getNodeLabelsCount() {
            return this.nodeLabels_.size();
        }
        
        @Override
        public String getNodeLabels(final int index) {
            return this.nodeLabels_.get(index);
        }
        
        @Override
        public ByteString getNodeLabelsBytes(final int index) {
            return this.nodeLabels_.getByteString(index);
        }
        
        private void initFields() {
            this.nodeLabels_ = LazyStringArrayList.EMPTY;
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
            for (int i = 0; i < this.nodeLabels_.size(); ++i) {
                output.writeBytes(1, this.nodeLabels_.getByteString(i));
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
            int dataSize = 0;
            for (int i = 0; i < this.nodeLabels_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.nodeLabels_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getNodeLabelsList().size();
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
            if (!(obj instanceof RemoveFromClusterNodeLabelsRequestProto)) {
                return super.equals(obj);
            }
            final RemoveFromClusterNodeLabelsRequestProto other = (RemoveFromClusterNodeLabelsRequestProto)obj;
            boolean result = true;
            result = (result && this.getNodeLabelsList().equals(other.getNodeLabelsList()));
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
            if (this.getNodeLabelsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeLabelsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(data);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final InputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RemoveFromClusterNodeLabelsRequestProto prototype) {
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
            RemoveFromClusterNodeLabelsRequestProto.PARSER = new AbstractParser<RemoveFromClusterNodeLabelsRequestProto>() {
                @Override
                public RemoveFromClusterNodeLabelsRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RemoveFromClusterNodeLabelsRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RemoveFromClusterNodeLabelsRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RemoveFromClusterNodeLabelsRequestProtoOrBuilder
        {
            private int bitField0_;
            private LazyStringList nodeLabels_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveFromClusterNodeLabelsRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RemoveFromClusterNodeLabelsRequestProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsRequestProto_descriptor;
            }
            
            @Override
            public RemoveFromClusterNodeLabelsRequestProto getDefaultInstanceForType() {
                return RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance();
            }
            
            @Override
            public RemoveFromClusterNodeLabelsRequestProto build() {
                final RemoveFromClusterNodeLabelsRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RemoveFromClusterNodeLabelsRequestProto buildPartial() {
                final RemoveFromClusterNodeLabelsRequestProto result = new RemoveFromClusterNodeLabelsRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    this.nodeLabels_ = new UnmodifiableLazyStringList(this.nodeLabels_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.nodeLabels_ = this.nodeLabels_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RemoveFromClusterNodeLabelsRequestProto) {
                    return this.mergeFrom((RemoveFromClusterNodeLabelsRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RemoveFromClusterNodeLabelsRequestProto other) {
                if (other == RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (!other.nodeLabels_.isEmpty()) {
                    if (this.nodeLabels_.isEmpty()) {
                        this.nodeLabels_ = other.nodeLabels_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    else {
                        this.ensureNodeLabelsIsMutable();
                        this.nodeLabels_.addAll(other.nodeLabels_);
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
                RemoveFromClusterNodeLabelsRequestProto parsedMessage = null;
                try {
                    parsedMessage = RemoveFromClusterNodeLabelsRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RemoveFromClusterNodeLabelsRequestProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureNodeLabelsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.nodeLabels_ = new LazyStringArrayList(this.nodeLabels_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<String> getNodeLabelsList() {
                return Collections.unmodifiableList((List<? extends String>)this.nodeLabels_);
            }
            
            @Override
            public int getNodeLabelsCount() {
                return this.nodeLabels_.size();
            }
            
            @Override
            public String getNodeLabels(final int index) {
                return this.nodeLabels_.get(index);
            }
            
            @Override
            public ByteString getNodeLabelsBytes(final int index) {
                return this.nodeLabels_.getByteString(index);
            }
            
            public Builder setNodeLabels(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addNodeLabels(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllNodeLabels(final Iterable<String> values) {
                this.ensureNodeLabelsIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.nodeLabels_);
                this.onChanged();
                return this;
            }
            
            public Builder clearNodeLabels() {
                this.nodeLabels_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }
            
            public Builder addNodeLabelsBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureNodeLabelsIsMutable();
                this.nodeLabels_.add(value);
                this.onChanged();
                return this;
            }
        }
    }
    
    public static final class RemoveFromClusterNodeLabelsResponseProto extends GeneratedMessage implements RemoveFromClusterNodeLabelsResponseProtoOrBuilder
    {
        private static final RemoveFromClusterNodeLabelsResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<RemoveFromClusterNodeLabelsResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private RemoveFromClusterNodeLabelsResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private RemoveFromClusterNodeLabelsResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto getDefaultInstance() {
            return RemoveFromClusterNodeLabelsResponseProto.defaultInstance;
        }
        
        @Override
        public RemoveFromClusterNodeLabelsResponseProto getDefaultInstanceForType() {
            return RemoveFromClusterNodeLabelsResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private RemoveFromClusterNodeLabelsResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveFromClusterNodeLabelsResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<RemoveFromClusterNodeLabelsResponseProto> getParserForType() {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER;
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
            if (!(obj instanceof RemoveFromClusterNodeLabelsResponseProto)) {
                return super.equals(obj);
            }
            final RemoveFromClusterNodeLabelsResponseProto other = (RemoveFromClusterNodeLabelsResponseProto)obj;
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
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(data);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final InputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(input);
        }
        
        public static RemoveFromClusterNodeLabelsResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return RemoveFromClusterNodeLabelsResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final RemoveFromClusterNodeLabelsResponseProto prototype) {
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
            RemoveFromClusterNodeLabelsResponseProto.PARSER = new AbstractParser<RemoveFromClusterNodeLabelsResponseProto>() {
                @Override
                public RemoveFromClusterNodeLabelsResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new RemoveFromClusterNodeLabelsResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new RemoveFromClusterNodeLabelsResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements RemoveFromClusterNodeLabelsResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(RemoveFromClusterNodeLabelsResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (RemoveFromClusterNodeLabelsResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_RemoveFromClusterNodeLabelsResponseProto_descriptor;
            }
            
            @Override
            public RemoveFromClusterNodeLabelsResponseProto getDefaultInstanceForType() {
                return RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance();
            }
            
            @Override
            public RemoveFromClusterNodeLabelsResponseProto build() {
                final RemoveFromClusterNodeLabelsResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public RemoveFromClusterNodeLabelsResponseProto buildPartial() {
                final RemoveFromClusterNodeLabelsResponseProto result = new RemoveFromClusterNodeLabelsResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof RemoveFromClusterNodeLabelsResponseProto) {
                    return this.mergeFrom((RemoveFromClusterNodeLabelsResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final RemoveFromClusterNodeLabelsResponseProto other) {
                if (other == RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance()) {
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
                RemoveFromClusterNodeLabelsResponseProto parsedMessage = null;
                try {
                    parsedMessage = RemoveFromClusterNodeLabelsResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (RemoveFromClusterNodeLabelsResponseProto)e.getUnfinishedMessage();
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
    
    public static final class ReplaceLabelsOnNodeRequestProto extends GeneratedMessage implements ReplaceLabelsOnNodeRequestProtoOrBuilder
    {
        private static final ReplaceLabelsOnNodeRequestProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ReplaceLabelsOnNodeRequestProto> PARSER;
        public static final int NODETOLABELS_FIELD_NUMBER = 1;
        private List<YarnProtos.NodeIdToLabelsProto> nodeToLabels_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ReplaceLabelsOnNodeRequestProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ReplaceLabelsOnNodeRequestProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ReplaceLabelsOnNodeRequestProto getDefaultInstance() {
            return ReplaceLabelsOnNodeRequestProto.defaultInstance;
        }
        
        @Override
        public ReplaceLabelsOnNodeRequestProto getDefaultInstanceForType() {
            return ReplaceLabelsOnNodeRequestProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ReplaceLabelsOnNodeRequestProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                                this.nodeToLabels_ = new ArrayList<YarnProtos.NodeIdToLabelsProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.nodeToLabels_.add(input.readMessage(YarnProtos.NodeIdToLabelsProto.PARSER, extensionRegistry));
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
                    this.nodeToLabels_ = Collections.unmodifiableList((List<? extends YarnProtos.NodeIdToLabelsProto>)this.nodeToLabels_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ReplaceLabelsOnNodeRequestProto.class, Builder.class);
        }
        
        @Override
        public Parser<ReplaceLabelsOnNodeRequestProto> getParserForType() {
            return ReplaceLabelsOnNodeRequestProto.PARSER;
        }
        
        @Override
        public List<YarnProtos.NodeIdToLabelsProto> getNodeToLabelsList() {
            return this.nodeToLabels_;
        }
        
        @Override
        public List<? extends YarnProtos.NodeIdToLabelsProtoOrBuilder> getNodeToLabelsOrBuilderList() {
            return this.nodeToLabels_;
        }
        
        @Override
        public int getNodeToLabelsCount() {
            return this.nodeToLabels_.size();
        }
        
        @Override
        public YarnProtos.NodeIdToLabelsProto getNodeToLabels(final int index) {
            return this.nodeToLabels_.get(index);
        }
        
        @Override
        public YarnProtos.NodeIdToLabelsProtoOrBuilder getNodeToLabelsOrBuilder(final int index) {
            return this.nodeToLabels_.get(index);
        }
        
        private void initFields() {
            this.nodeToLabels_ = Collections.emptyList();
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
            for (int i = 0; i < this.nodeToLabels_.size(); ++i) {
                output.writeMessage(1, this.nodeToLabels_.get(i));
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
            for (int i = 0; i < this.nodeToLabels_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.nodeToLabels_.get(i));
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
            if (!(obj instanceof ReplaceLabelsOnNodeRequestProto)) {
                return super.equals(obj);
            }
            final ReplaceLabelsOnNodeRequestProto other = (ReplaceLabelsOnNodeRequestProto)obj;
            boolean result = true;
            result = (result && this.getNodeToLabelsList().equals(other.getNodeToLabelsList()));
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
            if (this.getNodeToLabelsCount() > 0) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getNodeToLabelsList().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(data);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(data);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final InputStream input) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(input);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final CodedInputStream input) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(input);
        }
        
        public static ReplaceLabelsOnNodeRequestProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeRequestProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ReplaceLabelsOnNodeRequestProto prototype) {
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
            ReplaceLabelsOnNodeRequestProto.PARSER = new AbstractParser<ReplaceLabelsOnNodeRequestProto>() {
                @Override
                public ReplaceLabelsOnNodeRequestProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ReplaceLabelsOnNodeRequestProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ReplaceLabelsOnNodeRequestProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ReplaceLabelsOnNodeRequestProtoOrBuilder
        {
            private int bitField0_;
            private List<YarnProtos.NodeIdToLabelsProto> nodeToLabels_;
            private RepeatedFieldBuilder<YarnProtos.NodeIdToLabelsProto, YarnProtos.NodeIdToLabelsProto.Builder, YarnProtos.NodeIdToLabelsProtoOrBuilder> nodeToLabelsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ReplaceLabelsOnNodeRequestProto.class, Builder.class);
            }
            
            private Builder() {
                this.nodeToLabels_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.nodeToLabels_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ReplaceLabelsOnNodeRequestProto.alwaysUseFieldBuilders) {
                    this.getNodeToLabelsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.nodeToLabelsBuilder_ == null) {
                    this.nodeToLabels_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.nodeToLabelsBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeRequestProto_descriptor;
            }
            
            @Override
            public ReplaceLabelsOnNodeRequestProto getDefaultInstanceForType() {
                return ReplaceLabelsOnNodeRequestProto.getDefaultInstance();
            }
            
            @Override
            public ReplaceLabelsOnNodeRequestProto build() {
                final ReplaceLabelsOnNodeRequestProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ReplaceLabelsOnNodeRequestProto buildPartial() {
                final ReplaceLabelsOnNodeRequestProto result = new ReplaceLabelsOnNodeRequestProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.nodeToLabelsBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.nodeToLabels_ = Collections.unmodifiableList((List<? extends YarnProtos.NodeIdToLabelsProto>)this.nodeToLabels_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.nodeToLabels_ = this.nodeToLabels_;
                }
                else {
                    result.nodeToLabels_ = this.nodeToLabelsBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ReplaceLabelsOnNodeRequestProto) {
                    return this.mergeFrom((ReplaceLabelsOnNodeRequestProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ReplaceLabelsOnNodeRequestProto other) {
                if (other == ReplaceLabelsOnNodeRequestProto.getDefaultInstance()) {
                    return this;
                }
                if (this.nodeToLabelsBuilder_ == null) {
                    if (!other.nodeToLabels_.isEmpty()) {
                        if (this.nodeToLabels_.isEmpty()) {
                            this.nodeToLabels_ = other.nodeToLabels_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureNodeToLabelsIsMutable();
                            this.nodeToLabels_.addAll(other.nodeToLabels_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.nodeToLabels_.isEmpty()) {
                    if (this.nodeToLabelsBuilder_.isEmpty()) {
                        this.nodeToLabelsBuilder_.dispose();
                        this.nodeToLabelsBuilder_ = null;
                        this.nodeToLabels_ = other.nodeToLabels_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.nodeToLabelsBuilder_ = (ReplaceLabelsOnNodeRequestProto.alwaysUseFieldBuilders ? this.getNodeToLabelsFieldBuilder() : null);
                    }
                    else {
                        this.nodeToLabelsBuilder_.addAllMessages(other.nodeToLabels_);
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
                ReplaceLabelsOnNodeRequestProto parsedMessage = null;
                try {
                    parsedMessage = ReplaceLabelsOnNodeRequestProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ReplaceLabelsOnNodeRequestProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureNodeToLabelsIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.nodeToLabels_ = new ArrayList<YarnProtos.NodeIdToLabelsProto>(this.nodeToLabels_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            @Override
            public List<YarnProtos.NodeIdToLabelsProto> getNodeToLabelsList() {
                if (this.nodeToLabelsBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends YarnProtos.NodeIdToLabelsProto>)this.nodeToLabels_);
                }
                return this.nodeToLabelsBuilder_.getMessageList();
            }
            
            @Override
            public int getNodeToLabelsCount() {
                if (this.nodeToLabelsBuilder_ == null) {
                    return this.nodeToLabels_.size();
                }
                return this.nodeToLabelsBuilder_.getCount();
            }
            
            @Override
            public YarnProtos.NodeIdToLabelsProto getNodeToLabels(final int index) {
                if (this.nodeToLabelsBuilder_ == null) {
                    return this.nodeToLabels_.get(index);
                }
                return this.nodeToLabelsBuilder_.getMessage(index);
            }
            
            public Builder setNodeToLabels(final int index, final YarnProtos.NodeIdToLabelsProto value) {
                if (this.nodeToLabelsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setNodeToLabels(final int index, final YarnProtos.NodeIdToLabelsProto.Builder builderForValue) {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addNodeToLabels(final YarnProtos.NodeIdToLabelsProto value) {
                if (this.nodeToLabelsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.add(value);
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addNodeToLabels(final int index, final YarnProtos.NodeIdToLabelsProto value) {
                if (this.nodeToLabelsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addNodeToLabels(final YarnProtos.NodeIdToLabelsProto.Builder builderForValue) {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addNodeToLabels(final int index, final YarnProtos.NodeIdToLabelsProto.Builder builderForValue) {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllNodeToLabels(final Iterable<? extends YarnProtos.NodeIdToLabelsProto> values) {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.ensureNodeToLabelsIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.nodeToLabels_);
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearNodeToLabels() {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.nodeToLabels_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeNodeToLabels(final int index) {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.ensureNodeToLabelsIsMutable();
                    this.nodeToLabels_.remove(index);
                    this.onChanged();
                }
                else {
                    this.nodeToLabelsBuilder_.remove(index);
                }
                return this;
            }
            
            public YarnProtos.NodeIdToLabelsProto.Builder getNodeToLabelsBuilder(final int index) {
                return this.getNodeToLabelsFieldBuilder().getBuilder(index);
            }
            
            @Override
            public YarnProtos.NodeIdToLabelsProtoOrBuilder getNodeToLabelsOrBuilder(final int index) {
                if (this.nodeToLabelsBuilder_ == null) {
                    return this.nodeToLabels_.get(index);
                }
                return this.nodeToLabelsBuilder_.getMessageOrBuilder(index);
            }
            
            @Override
            public List<? extends YarnProtos.NodeIdToLabelsProtoOrBuilder> getNodeToLabelsOrBuilderList() {
                if (this.nodeToLabelsBuilder_ != null) {
                    return this.nodeToLabelsBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends YarnProtos.NodeIdToLabelsProtoOrBuilder>)this.nodeToLabels_);
            }
            
            public YarnProtos.NodeIdToLabelsProto.Builder addNodeToLabelsBuilder() {
                return this.getNodeToLabelsFieldBuilder().addBuilder(YarnProtos.NodeIdToLabelsProto.getDefaultInstance());
            }
            
            public YarnProtos.NodeIdToLabelsProto.Builder addNodeToLabelsBuilder(final int index) {
                return this.getNodeToLabelsFieldBuilder().addBuilder(index, YarnProtos.NodeIdToLabelsProto.getDefaultInstance());
            }
            
            public List<YarnProtos.NodeIdToLabelsProto.Builder> getNodeToLabelsBuilderList() {
                return this.getNodeToLabelsFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<YarnProtos.NodeIdToLabelsProto, YarnProtos.NodeIdToLabelsProto.Builder, YarnProtos.NodeIdToLabelsProtoOrBuilder> getNodeToLabelsFieldBuilder() {
                if (this.nodeToLabelsBuilder_ == null) {
                    this.nodeToLabelsBuilder_ = new RepeatedFieldBuilder<YarnProtos.NodeIdToLabelsProto, YarnProtos.NodeIdToLabelsProto.Builder, YarnProtos.NodeIdToLabelsProtoOrBuilder>(this.nodeToLabels_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.nodeToLabels_ = null;
                }
                return this.nodeToLabelsBuilder_;
            }
        }
    }
    
    public static final class ReplaceLabelsOnNodeResponseProto extends GeneratedMessage implements ReplaceLabelsOnNodeResponseProtoOrBuilder
    {
        private static final ReplaceLabelsOnNodeResponseProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ReplaceLabelsOnNodeResponseProto> PARSER;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ReplaceLabelsOnNodeResponseProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ReplaceLabelsOnNodeResponseProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ReplaceLabelsOnNodeResponseProto getDefaultInstance() {
            return ReplaceLabelsOnNodeResponseProto.defaultInstance;
        }
        
        @Override
        public ReplaceLabelsOnNodeResponseProto getDefaultInstanceForType() {
            return ReplaceLabelsOnNodeResponseProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ReplaceLabelsOnNodeResponseProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ReplaceLabelsOnNodeResponseProto.class, Builder.class);
        }
        
        @Override
        public Parser<ReplaceLabelsOnNodeResponseProto> getParserForType() {
            return ReplaceLabelsOnNodeResponseProto.PARSER;
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
            if (!(obj instanceof ReplaceLabelsOnNodeResponseProto)) {
                return super.equals(obj);
            }
            final ReplaceLabelsOnNodeResponseProto other = (ReplaceLabelsOnNodeResponseProto)obj;
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
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(data);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(data);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final InputStream input) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(input);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final CodedInputStream input) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(input);
        }
        
        public static ReplaceLabelsOnNodeResponseProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ReplaceLabelsOnNodeResponseProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ReplaceLabelsOnNodeResponseProto prototype) {
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
            ReplaceLabelsOnNodeResponseProto.PARSER = new AbstractParser<ReplaceLabelsOnNodeResponseProto>() {
                @Override
                public ReplaceLabelsOnNodeResponseProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ReplaceLabelsOnNodeResponseProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ReplaceLabelsOnNodeResponseProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ReplaceLabelsOnNodeResponseProtoOrBuilder
        {
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ReplaceLabelsOnNodeResponseProto.class, Builder.class);
            }
            
            private Builder() {
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ReplaceLabelsOnNodeResponseProto.alwaysUseFieldBuilders) {}
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
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ReplaceLabelsOnNodeResponseProto_descriptor;
            }
            
            @Override
            public ReplaceLabelsOnNodeResponseProto getDefaultInstanceForType() {
                return ReplaceLabelsOnNodeResponseProto.getDefaultInstance();
            }
            
            @Override
            public ReplaceLabelsOnNodeResponseProto build() {
                final ReplaceLabelsOnNodeResponseProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ReplaceLabelsOnNodeResponseProto buildPartial() {
                final ReplaceLabelsOnNodeResponseProto result = new ReplaceLabelsOnNodeResponseProto((GeneratedMessage.Builder)this);
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ReplaceLabelsOnNodeResponseProto) {
                    return this.mergeFrom((ReplaceLabelsOnNodeResponseProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ReplaceLabelsOnNodeResponseProto other) {
                if (other == ReplaceLabelsOnNodeResponseProto.getDefaultInstance()) {
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
                ReplaceLabelsOnNodeResponseProto parsedMessage = null;
                try {
                    parsedMessage = ReplaceLabelsOnNodeResponseProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ReplaceLabelsOnNodeResponseProto)e.getUnfinishedMessage();
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
    
    public static final class ActiveRMInfoProto extends GeneratedMessage implements ActiveRMInfoProtoOrBuilder
    {
        private static final ActiveRMInfoProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ActiveRMInfoProto> PARSER;
        private int bitField0_;
        public static final int CLUSTERID_FIELD_NUMBER = 1;
        private Object clusterId_;
        public static final int RMID_FIELD_NUMBER = 2;
        private Object rmId_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        private int memoizedHashCode;
        
        private ActiveRMInfoProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ActiveRMInfoProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.memoizedHashCode = 0;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ActiveRMInfoProto getDefaultInstance() {
            return ActiveRMInfoProto.defaultInstance;
        }
        
        @Override
        public ActiveRMInfoProto getDefaultInstanceForType() {
            return ActiveRMInfoProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ActiveRMInfoProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                            this.clusterId_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.rmId_ = input.readBytes();
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
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ActiveRMInfoProto.class, Builder.class);
        }
        
        @Override
        public Parser<ActiveRMInfoProto> getParserForType() {
            return ActiveRMInfoProto.PARSER;
        }
        
        @Override
        public boolean hasClusterId() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        @Override
        public String getClusterId() {
            final Object ref = this.clusterId_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.clusterId_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getClusterIdBytes() {
            final Object ref = this.clusterId_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.clusterId_ = b);
            }
            return (ByteString)ref;
        }
        
        @Override
        public boolean hasRmId() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        @Override
        public String getRmId() {
            final Object ref = this.rmId_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.rmId_ = s;
            }
            return s;
        }
        
        @Override
        public ByteString getRmIdBytes() {
            final Object ref = this.rmId_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.rmId_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.clusterId_ = "";
            this.rmId_ = "";
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
                output.writeBytes(1, this.getClusterIdBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getRmIdBytes());
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
                size += CodedOutputStream.computeBytesSize(1, this.getClusterIdBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getRmIdBytes());
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
            if (!(obj instanceof ActiveRMInfoProto)) {
                return super.equals(obj);
            }
            final ActiveRMInfoProto other = (ActiveRMInfoProto)obj;
            boolean result = true;
            result = (result && this.hasClusterId() == other.hasClusterId());
            if (this.hasClusterId()) {
                result = (result && this.getClusterId().equals(other.getClusterId()));
            }
            result = (result && this.hasRmId() == other.hasRmId());
            if (this.hasRmId()) {
                result = (result && this.getRmId().equals(other.getRmId()));
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
            if (this.hasClusterId()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getClusterId().hashCode();
            }
            if (this.hasRmId()) {
                hash = 37 * hash + 2;
                hash = 53 * hash + this.getRmId().hashCode();
            }
            hash = 29 * hash + this.getUnknownFields().hashCode();
            return this.memoizedHashCode = hash;
        }
        
        public static ActiveRMInfoProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ActiveRMInfoProto.PARSER.parseFrom(data);
        }
        
        public static ActiveRMInfoProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ActiveRMInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ActiveRMInfoProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ActiveRMInfoProto.PARSER.parseFrom(data);
        }
        
        public static ActiveRMInfoProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ActiveRMInfoProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ActiveRMInfoProto parseFrom(final InputStream input) throws IOException {
            return ActiveRMInfoProto.PARSER.parseFrom(input);
        }
        
        public static ActiveRMInfoProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ActiveRMInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ActiveRMInfoProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ActiveRMInfoProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ActiveRMInfoProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ActiveRMInfoProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ActiveRMInfoProto parseFrom(final CodedInputStream input) throws IOException {
            return ActiveRMInfoProto.PARSER.parseFrom(input);
        }
        
        public static ActiveRMInfoProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ActiveRMInfoProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ActiveRMInfoProto prototype) {
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
            ActiveRMInfoProto.PARSER = new AbstractParser<ActiveRMInfoProto>() {
                @Override
                public ActiveRMInfoProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ActiveRMInfoProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ActiveRMInfoProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ActiveRMInfoProtoOrBuilder
        {
            private int bitField0_;
            private Object clusterId_;
            private Object rmId_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ActiveRMInfoProto.class, Builder.class);
            }
            
            private Builder() {
                this.clusterId_ = "";
                this.rmId_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.clusterId_ = "";
                this.rmId_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (ActiveRMInfoProto.alwaysUseFieldBuilders) {}
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.clusterId_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.rmId_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return YarnServerResourceManagerServiceProtos.internal_static_hadoop_yarn_ActiveRMInfoProto_descriptor;
            }
            
            @Override
            public ActiveRMInfoProto getDefaultInstanceForType() {
                return ActiveRMInfoProto.getDefaultInstance();
            }
            
            @Override
            public ActiveRMInfoProto build() {
                final ActiveRMInfoProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            @Override
            public ActiveRMInfoProto buildPartial() {
                final ActiveRMInfoProto result = new ActiveRMInfoProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.clusterId_ = this.clusterId_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.rmId_ = this.rmId_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ActiveRMInfoProto) {
                    return this.mergeFrom((ActiveRMInfoProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ActiveRMInfoProto other) {
                if (other == ActiveRMInfoProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasClusterId()) {
                    this.bitField0_ |= 0x1;
                    this.clusterId_ = other.clusterId_;
                    this.onChanged();
                }
                if (other.hasRmId()) {
                    this.bitField0_ |= 0x2;
                    this.rmId_ = other.rmId_;
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
                ActiveRMInfoProto parsedMessage = null;
                try {
                    parsedMessage = ActiveRMInfoProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ActiveRMInfoProto)e.getUnfinishedMessage();
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
            public boolean hasClusterId() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            @Override
            public String getClusterId() {
                final Object ref = this.clusterId_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.clusterId_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getClusterIdBytes() {
                final Object ref = this.clusterId_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.clusterId_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setClusterId(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.clusterId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearClusterId() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.clusterId_ = ActiveRMInfoProto.getDefaultInstance().getClusterId();
                this.onChanged();
                return this;
            }
            
            public Builder setClusterIdBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.clusterId_ = value;
                this.onChanged();
                return this;
            }
            
            @Override
            public boolean hasRmId() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            @Override
            public String getRmId() {
                final Object ref = this.rmId_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.rmId_ = s);
                }
                return (String)ref;
            }
            
            @Override
            public ByteString getRmIdBytes() {
                final Object ref = this.rmId_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.rmId_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setRmId(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.rmId_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearRmId() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.rmId_ = ActiveRMInfoProto.getDefaultInstance().getRmId();
                this.onChanged();
                return this;
            }
            
            public Builder setRmIdBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.rmId_ = value;
                this.onChanged();
                return this;
            }
        }
    }
    
    public interface ActiveRMInfoProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasClusterId();
        
        String getClusterId();
        
        ByteString getClusterIdBytes();
        
        boolean hasRmId();
        
        String getRmId();
        
        ByteString getRmIdBytes();
    }
    
    public interface ReplaceLabelsOnNodeResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface ReplaceLabelsOnNodeRequestProtoOrBuilder extends MessageOrBuilder
    {
        List<YarnProtos.NodeIdToLabelsProto> getNodeToLabelsList();
        
        YarnProtos.NodeIdToLabelsProto getNodeToLabels(final int p0);
        
        int getNodeToLabelsCount();
        
        List<? extends YarnProtos.NodeIdToLabelsProtoOrBuilder> getNodeToLabelsOrBuilderList();
        
        YarnProtos.NodeIdToLabelsProtoOrBuilder getNodeToLabelsOrBuilder(final int p0);
    }
    
    public interface RemoveFromClusterNodeLabelsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RemoveFromClusterNodeLabelsRequestProtoOrBuilder extends MessageOrBuilder
    {
        List<String> getNodeLabelsList();
        
        int getNodeLabelsCount();
        
        String getNodeLabels(final int p0);
        
        ByteString getNodeLabelsBytes(final int p0);
    }
    
    public interface AddToClusterNodeLabelsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface AddToClusterNodeLabelsRequestProtoOrBuilder extends MessageOrBuilder
    {
        List<String> getNodeLabelsList();
        
        int getNodeLabelsCount();
        
        String getNodeLabels(final int p0);
        
        ByteString getNodeLabelsBytes(final int p0);
    }
    
    public interface UpdateNodeResourceResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface UpdateNodeResourceRequestProtoOrBuilder extends MessageOrBuilder
    {
        List<YarnProtos.NodeResourceMapProto> getNodeResourceMapList();
        
        YarnProtos.NodeResourceMapProto getNodeResourceMap(final int p0);
        
        int getNodeResourceMapCount();
        
        List<? extends YarnProtos.NodeResourceMapProtoOrBuilder> getNodeResourceMapOrBuilderList();
        
        YarnProtos.NodeResourceMapProtoOrBuilder getNodeResourceMapOrBuilder(final int p0);
    }
    
    public interface GetGroupsForUserResponseProtoOrBuilder extends MessageOrBuilder
    {
        List<String> getGroupsList();
        
        int getGroupsCount();
        
        String getGroups(final int p0);
        
        ByteString getGroupsBytes(final int p0);
    }
    
    public interface GetGroupsForUserRequestProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasUser();
        
        String getUser();
        
        ByteString getUserBytes();
    }
    
    public interface RefreshServiceAclsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshServiceAclsRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshAdminAclsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshAdminAclsRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshUserToGroupsMappingsResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshUserToGroupsMappingsRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshSuperUserGroupsConfigurationResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshSuperUserGroupsConfigurationRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshNodesResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshNodesRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshQueuesResponseProtoOrBuilder extends MessageOrBuilder
    {
    }
    
    public interface RefreshQueuesRequestProtoOrBuilder extends MessageOrBuilder
    {
    }
}
