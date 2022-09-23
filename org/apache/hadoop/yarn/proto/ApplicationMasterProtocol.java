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

public final class ApplicationMasterProtocol
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ApplicationMasterProtocol() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ApplicationMasterProtocol.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n applicationmaster_protocol.proto\u0012\u000bhadoop.yarn\u001a\u0019yarn_service_protos.proto2\u00fc\u0002\n ApplicationMasterProtocolService\u0012\u0084\u0001\n\u0019registerApplicationMaster\u00122.hadoop.yarn.RegisterApplicationMasterRequestProto\u001a3.hadoop.yarn.RegisterApplicationMasterResponseProto\u0012~\n\u0017finishApplicationMaster\u00120.hadoop.yarn.FinishApplicationMasterRequestProto\u001a1.hadoop.yarn.FinishApplicationMasterResponseProto\u0012Q\n\ballocate\u0012!.hadoop.yarn", ".AllocateRequestProto\u001a\".hadoop.yarn.AllocateResponseProtoB?\n\u001corg.apache.hadoop.yarn.protoB\u0019ApplicationMasterProtocol\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ApplicationMasterProtocol.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { YarnServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ApplicationMasterProtocolService implements Service
    {
        protected ApplicationMasterProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ApplicationMasterProtocolService() {
                @Override
                public void registerApplicationMaster(final RpcController controller, final YarnServiceProtos.RegisterApplicationMasterRequestProto request, final RpcCallback<YarnServiceProtos.RegisterApplicationMasterResponseProto> done) {
                    impl.registerApplicationMaster(controller, request, done);
                }
                
                @Override
                public void finishApplicationMaster(final RpcController controller, final YarnServiceProtos.FinishApplicationMasterRequestProto request, final RpcCallback<YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
                    impl.finishApplicationMaster(controller, request, done);
                }
                
                @Override
                public void allocate(final RpcController controller, final YarnServiceProtos.AllocateRequestProto request, final RpcCallback<YarnServiceProtos.AllocateResponseProto> done) {
                    impl.allocate(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ApplicationMasterProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ApplicationMasterProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.registerApplicationMaster(controller, (YarnServiceProtos.RegisterApplicationMasterRequestProto)request);
                        }
                        case 1: {
                            return impl.finishApplicationMaster(controller, (YarnServiceProtos.FinishApplicationMasterRequestProto)request);
                        }
                        case 2: {
                            return impl.allocate(controller, (YarnServiceProtos.AllocateRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationMasterProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationMasterProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void registerApplicationMaster(final RpcController p0, final YarnServiceProtos.RegisterApplicationMasterRequestProto p1, final RpcCallback<YarnServiceProtos.RegisterApplicationMasterResponseProto> p2);
        
        public abstract void finishApplicationMaster(final RpcController p0, final YarnServiceProtos.FinishApplicationMasterRequestProto p1, final RpcCallback<YarnServiceProtos.FinishApplicationMasterResponseProto> p2);
        
        public abstract void allocate(final RpcController p0, final YarnServiceProtos.AllocateRequestProto p1, final RpcCallback<YarnServiceProtos.AllocateResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ApplicationMasterProtocol.getDescriptor().getServices().get(0);
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
                    this.registerApplicationMaster(controller, (YarnServiceProtos.RegisterApplicationMasterRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.finishApplicationMaster(controller, (YarnServiceProtos.FinishApplicationMasterRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.allocate(controller, (YarnServiceProtos.AllocateRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.AllocateRequestProto.getDefaultInstance();
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
                    return YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.AllocateResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ApplicationMasterProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void registerApplicationMaster(final RpcController controller, final YarnServiceProtos.RegisterApplicationMasterRequestProto request, final RpcCallback<YarnServiceProtos.RegisterApplicationMasterResponseProto> done) {
                this.channel.callMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.RegisterApplicationMasterResponseProto.class, YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void finishApplicationMaster(final RpcController controller, final YarnServiceProtos.FinishApplicationMasterRequestProto request, final RpcCallback<YarnServiceProtos.FinishApplicationMasterResponseProto> done) {
                this.channel.callMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.FinishApplicationMasterResponseProto.class, YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void allocate(final RpcController controller, final YarnServiceProtos.AllocateRequestProto request, final RpcCallback<YarnServiceProtos.AllocateResponseProto> done) {
                this.channel.callMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.AllocateResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.AllocateResponseProto.class, YarnServiceProtos.AllocateResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServiceProtos.RegisterApplicationMasterResponseProto registerApplicationMaster(final RpcController controller, final YarnServiceProtos.RegisterApplicationMasterRequestProto request) throws ServiceException {
                return (YarnServiceProtos.RegisterApplicationMasterResponseProto)this.channel.callBlockingMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(final RpcController controller, final YarnServiceProtos.FinishApplicationMasterRequestProto request) throws ServiceException {
                return (YarnServiceProtos.FinishApplicationMasterResponseProto)this.channel.callBlockingMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.AllocateResponseProto allocate(final RpcController controller, final YarnServiceProtos.AllocateRequestProto request) throws ServiceException {
                return (YarnServiceProtos.AllocateResponseProto)this.channel.callBlockingMethod(ApplicationMasterProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.AllocateResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServiceProtos.RegisterApplicationMasterResponseProto registerApplicationMaster(final RpcController p0, final YarnServiceProtos.RegisterApplicationMasterRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(final RpcController p0, final YarnServiceProtos.FinishApplicationMasterRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.AllocateResponseProto allocate(final RpcController p0, final YarnServiceProtos.AllocateRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void registerApplicationMaster(final RpcController p0, final YarnServiceProtos.RegisterApplicationMasterRequestProto p1, final RpcCallback<YarnServiceProtos.RegisterApplicationMasterResponseProto> p2);
            
            void finishApplicationMaster(final RpcController p0, final YarnServiceProtos.FinishApplicationMasterRequestProto p1, final RpcCallback<YarnServiceProtos.FinishApplicationMasterResponseProto> p2);
            
            void allocate(final RpcController p0, final YarnServiceProtos.AllocateRequestProto p1, final RpcCallback<YarnServiceProtos.AllocateResponseProto> p2);
        }
    }
}
