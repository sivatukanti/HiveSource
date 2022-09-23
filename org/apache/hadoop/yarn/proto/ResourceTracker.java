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

public final class ResourceTracker
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ResourceTracker() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ResourceTracker.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n\u0015ResourceTracker.proto\u0012\u000bhadoop.yarn\u001a'yarn_server_common_service_protos.proto2\u00ee\u0001\n\u0016ResourceTrackerService\u0012r\n\u0013registerNodeManager\u0012,.hadoop.yarn.RegisterNodeManagerRequestProto\u001a-.hadoop.yarn.RegisterNodeManagerResponseProto\u0012`\n\rnodeHeartbeat\u0012&.hadoop.yarn.NodeHeartbeatRequestProto\u001a'.hadoop.yarn.NodeHeartbeatResponseProtoB5\n\u001corg.apache.hadoop.yarn.protoB\u000fResourceTracker\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ResourceTracker.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnServerCommonServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ResourceTrackerService implements Service
    {
        protected ResourceTrackerService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ResourceTrackerService() {
                @Override
                public void registerNodeManager(final RpcController controller, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto request, final RpcCallback<YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto> done) {
                    impl.registerNodeManager(controller, request, done);
                }
                
                @Override
                public void nodeHeartbeat(final RpcController controller, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto request, final RpcCallback<YarnServerCommonServiceProtos.NodeHeartbeatResponseProto> done) {
                    impl.nodeHeartbeat(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ResourceTrackerService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ResourceTrackerService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.registerNodeManager(controller, (YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto)request);
                        }
                        case 1: {
                            return impl.nodeHeartbeat(controller, (YarnServerCommonServiceProtos.NodeHeartbeatRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ResourceTrackerService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ResourceTrackerService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void registerNodeManager(final RpcController p0, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto p1, final RpcCallback<YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto> p2);
        
        public abstract void nodeHeartbeat(final RpcController p0, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto p1, final RpcCallback<YarnServerCommonServiceProtos.NodeHeartbeatResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ResourceTracker.getDescriptor().getServices().get(0);
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
                    this.registerNodeManager(controller, (YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.nodeHeartbeat(controller, (YarnServerCommonServiceProtos.NodeHeartbeatRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServerCommonServiceProtos.NodeHeartbeatRequestProto.getDefaultInstance();
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
                    return YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ResourceTrackerService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void registerNodeManager(final RpcController controller, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto request, final RpcCallback<YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto> done) {
                this.channel.callMethod(ResourceTrackerService.getDescriptor().getMethods().get(0), controller, request, YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.class, YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void nodeHeartbeat(final RpcController controller, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto request, final RpcCallback<YarnServerCommonServiceProtos.NodeHeartbeatResponseProto> done) {
                this.channel.callMethod(ResourceTrackerService.getDescriptor().getMethods().get(1), controller, request, YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.class, YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto registerNodeManager(final RpcController controller, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto request) throws ServiceException {
                return (YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto)this.channel.callBlockingMethod(ResourceTrackerService.getDescriptor().getMethods().get(0), controller, request, YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServerCommonServiceProtos.NodeHeartbeatResponseProto nodeHeartbeat(final RpcController controller, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto request) throws ServiceException {
                return (YarnServerCommonServiceProtos.NodeHeartbeatResponseProto)this.channel.callBlockingMethod(ResourceTrackerService.getDescriptor().getMethods().get(1), controller, request, YarnServerCommonServiceProtos.NodeHeartbeatResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto registerNodeManager(final RpcController p0, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto p1) throws ServiceException;
            
            YarnServerCommonServiceProtos.NodeHeartbeatResponseProto nodeHeartbeat(final RpcController p0, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void registerNodeManager(final RpcController p0, final YarnServerCommonServiceProtos.RegisterNodeManagerRequestProto p1, final RpcCallback<YarnServerCommonServiceProtos.RegisterNodeManagerResponseProto> p2);
            
            void nodeHeartbeat(final RpcController p0, final YarnServerCommonServiceProtos.NodeHeartbeatRequestProto p1, final RpcCallback<YarnServerCommonServiceProtos.NodeHeartbeatResponseProto> p2);
        }
    }
}
