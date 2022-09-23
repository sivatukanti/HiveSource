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

public final class ContainerManagementProtocol
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ContainerManagementProtocol() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ContainerManagementProtocol.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\"containermanagement_protocol.proto\u0012\u000bhadoop.yarn\u001a\u0019yarn_service_protos.proto2\u00e8\u0002\n\"ContainerManagementProtocolService\u0012f\n\u000fstartContainers\u0012(.hadoop.yarn.StartContainersRequestProto\u001a).hadoop.yarn.StartContainersResponseProto\u0012c\n\u000estopContainers\u0012'.hadoop.yarn.StopContainersRequestProto\u001a(.hadoop.yarn.StopContainersResponseProto\u0012u\n\u0014getContainerStatuses\u0012-.hadoop.yarn.GetContainerStatusesRequestProto\u001a..hadoop", ".yarn.GetContainerStatusesResponseProtoBA\n\u001corg.apache.hadoop.yarn.protoB\u001bContainerManagementProtocol\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ContainerManagementProtocol.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ContainerManagementProtocolService implements Service
    {
        protected ContainerManagementProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ContainerManagementProtocolService() {
                @Override
                public void startContainers(final RpcController controller, final YarnServiceProtos.StartContainersRequestProto request, final RpcCallback<YarnServiceProtos.StartContainersResponseProto> done) {
                    impl.startContainers(controller, request, done);
                }
                
                @Override
                public void stopContainers(final RpcController controller, final YarnServiceProtos.StopContainersRequestProto request, final RpcCallback<YarnServiceProtos.StopContainersResponseProto> done) {
                    impl.stopContainers(controller, request, done);
                }
                
                @Override
                public void getContainerStatuses(final RpcController controller, final YarnServiceProtos.GetContainerStatusesRequestProto request, final RpcCallback<YarnServiceProtos.GetContainerStatusesResponseProto> done) {
                    impl.getContainerStatuses(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ContainerManagementProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ContainerManagementProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.startContainers(controller, (YarnServiceProtos.StartContainersRequestProto)request);
                        }
                        case 1: {
                            return impl.stopContainers(controller, (YarnServiceProtos.StopContainersRequestProto)request);
                        }
                        case 2: {
                            return impl.getContainerStatuses(controller, (YarnServiceProtos.GetContainerStatusesRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ContainerManagementProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.StartContainersRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.StopContainersRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.GetContainerStatusesRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ContainerManagementProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.StartContainersResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.StopContainersResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void startContainers(final RpcController p0, final YarnServiceProtos.StartContainersRequestProto p1, final RpcCallback<YarnServiceProtos.StartContainersResponseProto> p2);
        
        public abstract void stopContainers(final RpcController p0, final YarnServiceProtos.StopContainersRequestProto p1, final RpcCallback<YarnServiceProtos.StopContainersResponseProto> p2);
        
        public abstract void getContainerStatuses(final RpcController p0, final YarnServiceProtos.GetContainerStatusesRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerStatusesResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ContainerManagementProtocol.getDescriptor().getServices().get(0);
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
                    this.startContainers(controller, (YarnServiceProtos.StartContainersRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.stopContainers(controller, (YarnServiceProtos.StopContainersRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.getContainerStatuses(controller, (YarnServiceProtos.GetContainerStatusesRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServiceProtos.StartContainersRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.StopContainersRequestProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.GetContainerStatusesRequestProto.getDefaultInstance();
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
                    return YarnServiceProtos.StartContainersResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.StopContainersResponseProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ContainerManagementProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void startContainers(final RpcController controller, final YarnServiceProtos.StartContainersRequestProto request, final RpcCallback<YarnServiceProtos.StartContainersResponseProto> done) {
                this.channel.callMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.StartContainersResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.StartContainersResponseProto.class, YarnServiceProtos.StartContainersResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void stopContainers(final RpcController controller, final YarnServiceProtos.StopContainersRequestProto request, final RpcCallback<YarnServiceProtos.StopContainersResponseProto> done) {
                this.channel.callMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.StopContainersResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.StopContainersResponseProto.class, YarnServiceProtos.StopContainersResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getContainerStatuses(final RpcController controller, final YarnServiceProtos.GetContainerStatusesRequestProto request, final RpcCallback<YarnServiceProtos.GetContainerStatusesResponseProto> done) {
                this.channel.callMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetContainerStatusesResponseProto.class, YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServiceProtos.StartContainersResponseProto startContainers(final RpcController controller, final YarnServiceProtos.StartContainersRequestProto request) throws ServiceException {
                return (YarnServiceProtos.StartContainersResponseProto)this.channel.callBlockingMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.StartContainersResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.StopContainersResponseProto stopContainers(final RpcController controller, final YarnServiceProtos.StopContainersRequestProto request) throws ServiceException {
                return (YarnServiceProtos.StopContainersResponseProto)this.channel.callBlockingMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.StopContainersResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetContainerStatusesResponseProto getContainerStatuses(final RpcController controller, final YarnServiceProtos.GetContainerStatusesRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetContainerStatusesResponseProto)this.channel.callBlockingMethod(ContainerManagementProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.GetContainerStatusesResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServiceProtos.StartContainersResponseProto startContainers(final RpcController p0, final YarnServiceProtos.StartContainersRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.StopContainersResponseProto stopContainers(final RpcController p0, final YarnServiceProtos.StopContainersRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetContainerStatusesResponseProto getContainerStatuses(final RpcController p0, final YarnServiceProtos.GetContainerStatusesRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void startContainers(final RpcController p0, final YarnServiceProtos.StartContainersRequestProto p1, final RpcCallback<YarnServiceProtos.StartContainersResponseProto> p2);
            
            void stopContainers(final RpcController p0, final YarnServiceProtos.StopContainersRequestProto p1, final RpcCallback<YarnServiceProtos.StopContainersResponseProto> p2);
            
            void getContainerStatuses(final RpcController p0, final YarnServiceProtos.GetContainerStatusesRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerStatusesResponseProto> p2);
        }
    }
}
