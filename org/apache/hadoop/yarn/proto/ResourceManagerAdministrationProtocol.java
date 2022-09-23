// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.proto;

import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcUtil;
import com.google.protobuf.ServiceException;
import com.google.protobuf.Message;
import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Descriptors;

public final class ResourceManagerAdministrationProtocol
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ResourceManagerAdministrationProtocol() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ResourceManagerAdministrationProtocol.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n4server/resourcemanager_administration_protocol.proto\u0012\u000bhadoop.yarn\u001a7server/yarn_server_resourcemanager_service_protos.proto2\u00d8\n\n,ResourceManagerAdministrationProtocolService\u0012`\n\rrefreshQueues\u0012&.hadoop.yarn.RefreshQueuesRequestProto\u001a'.hadoop.yarn.RefreshQueuesResponseProto\u0012]\n\frefreshNodes\u0012%.hadoop.yarn.RefreshNodesRequestProto\u001a&.hadoop.yarn.RefreshNodesResponseProto\u0012¢\u0001\n#refreshSuperUserGroupsConfigu", "ration\u0012<.hadoop.yarn.RefreshSuperUserGroupsConfigurationRequestProto\u001a=.hadoop.yarn.RefreshSuperUserGroupsConfigurationResponseProto\u0012\u008a\u0001\n\u001brefreshUserToGroupsMappings\u00124.hadoop.yarn.RefreshUserToGroupsMappingsRequestProto\u001a5.hadoop.yarn.RefreshUserToGroupsMappingsResponseProto\u0012i\n\u0010refreshAdminAcls\u0012).hadoop.yarn.RefreshAdminAclsRequestProto\u001a*.hadoop.yarn.RefreshAdminAclsResponseProto\u0012o\n\u0012refreshServiceAcl", "s\u0012+.hadoop.yarn.RefreshServiceAclsRequestProto\u001a,.hadoop.yarn.RefreshServiceAclsResponseProto\u0012i\n\u0010getGroupsForUser\u0012).hadoop.yarn.GetGroupsForUserRequestProto\u001a*.hadoop.yarn.GetGroupsForUserResponseProto\u0012o\n\u0012updateNodeResource\u0012+.hadoop.yarn.UpdateNodeResourceRequestProto\u001a,.hadoop.yarn.UpdateNodeResourceResponseProto\u0012{\n\u0016addToClusterNodeLabels\u0012/.hadoop.yarn.AddToClusterNodeLabelsRequestProto\u001a0.hadoop.yar", "n.AddToClusterNodeLabelsResponseProto\u0012\u008a\u0001\n\u001bremoveFromClusterNodeLabels\u00124.hadoop.yarn.RemoveFromClusterNodeLabelsRequestProto\u001a5.hadoop.yarn.RemoveFromClusterNodeLabelsResponseProto\u0012s\n\u0014replaceLabelsOnNodes\u0012,.hadoop.yarn.ReplaceLabelsOnNodeRequestProto\u001a-.hadoop.yarn.ReplaceLabelsOnNodeResponseProtoBK\n\u001corg.apache.hadoop.yarn.protoB%ResourceManagerAdministrationProtocol\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ResourceManagerAdministrationProtocol.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnServerResourceManagerServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ResourceManagerAdministrationProtocolService implements Service
    {
        protected ResourceManagerAdministrationProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ResourceManagerAdministrationProtocolService() {
                @Override
                public void refreshQueues(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto> done) {
                    impl.refreshQueues(controller, request, done);
                }
                
                @Override
                public void refreshNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto> done) {
                    impl.refreshNodes(controller, request, done);
                }
                
                @Override
                public void refreshSuperUserGroupsConfiguration(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto> done) {
                    impl.refreshSuperUserGroupsConfiguration(controller, request, done);
                }
                
                @Override
                public void refreshUserToGroupsMappings(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto> done) {
                    impl.refreshUserToGroupsMappings(controller, request, done);
                }
                
                @Override
                public void refreshAdminAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto> done) {
                    impl.refreshAdminAcls(controller, request, done);
                }
                
                @Override
                public void refreshServiceAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto> done) {
                    impl.refreshServiceAcls(controller, request, done);
                }
                
                @Override
                public void getGroupsForUser(final RpcController controller, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto> done) {
                    impl.getGroupsForUser(controller, request, done);
                }
                
                @Override
                public void updateNodeResource(final RpcController controller, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto> done) {
                    impl.updateNodeResource(controller, request, done);
                }
                
                @Override
                public void addToClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto> done) {
                    impl.addToClusterNodeLabels(controller, request, done);
                }
                
                @Override
                public void removeFromClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto> done) {
                    impl.removeFromClusterNodeLabels(controller, request, done);
                }
                
                @Override
                public void replaceLabelsOnNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto> done) {
                    impl.replaceLabelsOnNodes(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ResourceManagerAdministrationProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ResourceManagerAdministrationProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.refreshQueues(controller, (YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto)request);
                        }
                        case 1: {
                            return impl.refreshNodes(controller, (YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto)request);
                        }
                        case 2: {
                            return impl.refreshSuperUserGroupsConfiguration(controller, (YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto)request);
                        }
                        case 3: {
                            return impl.refreshUserToGroupsMappings(controller, (YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto)request);
                        }
                        case 4: {
                            return impl.refreshAdminAcls(controller, (YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto)request);
                        }
                        case 5: {
                            return impl.refreshServiceAcls(controller, (YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto)request);
                        }
                        case 6: {
                            return impl.getGroupsForUser(controller, (YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto)request);
                        }
                        case 7: {
                            return impl.updateNodeResource(controller, (YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto)request);
                        }
                        case 8: {
                            return impl.addToClusterNodeLabels(controller, (YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto)request);
                        }
                        case 9: {
                            return impl.removeFromClusterNodeLabels(controller, (YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto)request);
                        }
                        case 10: {
                            return impl.replaceLabelsOnNodes(controller, (YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ResourceManagerAdministrationProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto.getDefaultInstance();
                        }
                        case 6: {
                            return YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto.getDefaultInstance();
                        }
                        case 7: {
                            return YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.getDefaultInstance();
                        }
                        case 8: {
                            return YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.getDefaultInstance();
                        }
                        case 9: {
                            return YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance();
                        }
                        case 10: {
                            return YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ResourceManagerAdministrationProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.getDefaultInstance();
                        }
                        case 6: {
                            return YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.getDefaultInstance();
                        }
                        case 7: {
                            return YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance();
                        }
                        case 8: {
                            return YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance();
                        }
                        case 9: {
                            return YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance();
                        }
                        case 10: {
                            return YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void refreshQueues(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto> p2);
        
        public abstract void refreshNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto> p2);
        
        public abstract void refreshSuperUserGroupsConfiguration(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto> p2);
        
        public abstract void refreshUserToGroupsMappings(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto> p2);
        
        public abstract void refreshAdminAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto> p2);
        
        public abstract void refreshServiceAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto> p2);
        
        public abstract void getGroupsForUser(final RpcController p0, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto> p2);
        
        public abstract void updateNodeResource(final RpcController p0, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto> p2);
        
        public abstract void addToClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto> p2);
        
        public abstract void removeFromClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto> p2);
        
        public abstract void replaceLabelsOnNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ResourceManagerAdministrationProtocol.getDescriptor().getServices().get(0);
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
                    this.refreshQueues(controller, (YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.refreshNodes(controller, (YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.refreshSuperUserGroupsConfiguration(controller, (YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 3: {
                    this.refreshUserToGroupsMappings(controller, (YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 4: {
                    this.refreshAdminAcls(controller, (YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 5: {
                    this.refreshServiceAcls(controller, (YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 6: {
                    this.getGroupsForUser(controller, (YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 7: {
                    this.updateNodeResource(controller, (YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 8: {
                    this.addToClusterNodeLabels(controller, (YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 9: {
                    this.removeFromClusterNodeLabels(controller, (YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 10: {
                    this.replaceLabelsOnNodes(controller, (YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto.getDefaultInstance();
                }
                case 6: {
                    return YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto.getDefaultInstance();
                }
                case 7: {
                    return YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.getDefaultInstance();
                }
                case 8: {
                    return YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.getDefaultInstance();
                }
                case 9: {
                    return YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.getDefaultInstance();
                }
                case 10: {
                    return YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.getDefaultInstance();
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
                    return YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.getDefaultInstance();
                }
                case 6: {
                    return YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.getDefaultInstance();
                }
                case 7: {
                    return YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance();
                }
                case 8: {
                    return YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance();
                }
                case 9: {
                    return YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance();
                }
                case 10: {
                    return YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ResourceManagerAdministrationProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void refreshQueues(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshSuperUserGroupsConfiguration(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshUserToGroupsMappings(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshAdminAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void refreshServiceAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.class, YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getGroupsForUser(final RpcController controller, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(6), controller, request, YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.class, YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void updateNodeResource(final RpcController controller, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(7), controller, request, YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.class, YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void addToClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(8), controller, request, YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.class, YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void removeFromClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(9), controller, request, YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.class, YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void replaceLabelsOnNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto request, final RpcCallback<YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto> done) {
                this.channel.callMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(10), controller, request, YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.class, YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto refreshQueues(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto refreshNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto refreshAdminAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto refreshServiceAcls(final RpcController controller, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto getGroupsForUser(final RpcController controller, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(6), controller, request, YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto updateNodeResource(final RpcController controller, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(7), controller, request, YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto addToClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(8), controller, request, YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto removeFromClusterNodeLabels(final RpcController controller, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(9), controller, request, YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto replaceLabelsOnNodes(final RpcController controller, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto request) throws ServiceException {
                return (YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto)this.channel.callBlockingMethod(ResourceManagerAdministrationProtocolService.getDescriptor().getMethods().get(10), controller, request, YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto refreshQueues(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto refreshNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto refreshSuperUserGroupsConfiguration(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto refreshUserToGroupsMappings(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto refreshAdminAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto refreshServiceAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto getGroupsForUser(final RpcController p0, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto updateNodeResource(final RpcController p0, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto addToClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto removeFromClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto p1) throws ServiceException;
            
            YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto replaceLabelsOnNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void refreshQueues(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshQueuesRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshQueuesResponseProto> p2);
            
            void refreshNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshNodesRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshNodesResponseProto> p2);
            
            void refreshSuperUserGroupsConfiguration(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshSuperUserGroupsConfigurationResponseProto> p2);
            
            void refreshUserToGroupsMappings(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshUserToGroupsMappingsResponseProto> p2);
            
            void refreshAdminAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshAdminAclsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshAdminAclsResponseProto> p2);
            
            void refreshServiceAcls(final RpcController p0, final YarnServerResourceManagerServiceProtos.RefreshServiceAclsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RefreshServiceAclsResponseProto> p2);
            
            void getGroupsForUser(final RpcController p0, final YarnServerResourceManagerServiceProtos.GetGroupsForUserRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.GetGroupsForUserResponseProto> p2);
            
            void updateNodeResource(final RpcController p0, final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto> p2);
            
            void addToClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto> p2);
            
            void removeFromClusterNodeLabels(final RpcController p0, final YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsResponseProto> p2);
            
            void replaceLabelsOnNodes(final RpcController p0, final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto p1, final RpcCallback<YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeResponseProto> p2);
        }
    }
}
