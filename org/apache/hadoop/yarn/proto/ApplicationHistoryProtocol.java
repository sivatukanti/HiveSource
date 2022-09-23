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
import org.apache.hadoop.security.proto.SecurityProtos;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Descriptors;

public final class ApplicationHistoryProtocol
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ApplicationHistoryProtocol() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ApplicationHistoryProtocol.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n application_history_client.proto\u0012\u000bhadoop.yarn\u001a\u000eSecurity.proto\u001a\u0019yarn_service_protos.proto2\u00cd\b\n!ApplicationHistoryProtocolService\u0012u\n\u0014getApplicationReport\u0012-.hadoop.yarn.GetApplicationReportRequestProto\u001a..hadoop.yarn.GetApplicationReportResponseProto\u0012f\n\u000fgetApplications\u0012(.hadoop.yarn.GetApplicationsRequestProto\u001a).hadoop.yarn.GetApplicationsResponseProto\u0012\u008a\u0001\n\u001bgetApplicationAttemptReport\u00124.hadoop.yarn.Ge", "tApplicationAttemptReportRequestProto\u001a5.hadoop.yarn.GetApplicationAttemptReportResponseProto\u0012{\n\u0016getApplicationAttempts\u0012/.hadoop.yarn.GetApplicationAttemptsRequestProto\u001a0.hadoop.yarn.GetApplicationAttemptsResponseProto\u0012o\n\u0012getContainerReport\u0012+.hadoop.yarn.GetContainerReportRequestProto\u001a,.hadoop.yarn.GetContainerReportResponseProto\u0012`\n\rgetContainers\u0012&.hadoop.yarn.GetContainersRequestProto\u001a'.hadoop.yar", "n.GetContainersResponseProto\u0012s\n\u0012getDelegationToken\u0012-.hadoop.common.GetDelegationTokenRequestProto\u001a..hadoop.common.GetDelegationTokenResponseProto\u0012y\n\u0014renewDelegationToken\u0012/.hadoop.common.RenewDelegationTokenRequestProto\u001a0.hadoop.common.RenewDelegationTokenResponseProto\u0012|\n\u0015cancelDelegationToken\u00120.hadoop.common.CancelDelegationTokenRequestProto\u001a1.hadoop.common.CancelDelegationTokenResponseProtoB@\n\u001cor", "g.apache.hadoop.yarn.protoB\u001aApplicationHistoryProtocol\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ApplicationHistoryProtocol.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { SecurityProtos.getDescriptor(), YarnServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ApplicationHistoryProtocolService implements Service
    {
        protected ApplicationHistoryProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ApplicationHistoryProtocolService() {
                @Override
                public void getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> done) {
                    impl.getApplicationReport(controller, request, done);
                }
                
                @Override
                public void getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> done) {
                    impl.getApplications(controller, request, done);
                }
                
                @Override
                public void getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> done) {
                    impl.getApplicationAttemptReport(controller, request, done);
                }
                
                @Override
                public void getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> done) {
                    impl.getApplicationAttempts(controller, request, done);
                }
                
                @Override
                public void getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto request, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> done) {
                    impl.getContainerReport(controller, request, done);
                }
                
                @Override
                public void getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto request, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> done) {
                    impl.getContainers(controller, request, done);
                }
                
                @Override
                public void getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> done) {
                    impl.getDelegationToken(controller, request, done);
                }
                
                @Override
                public void renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> done) {
                    impl.renewDelegationToken(controller, request, done);
                }
                
                @Override
                public void cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> done) {
                    impl.cancelDelegationToken(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ApplicationHistoryProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ApplicationHistoryProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.getApplicationReport(controller, (YarnServiceProtos.GetApplicationReportRequestProto)request);
                        }
                        case 1: {
                            return impl.getApplications(controller, (YarnServiceProtos.GetApplicationsRequestProto)request);
                        }
                        case 2: {
                            return impl.getApplicationAttemptReport(controller, (YarnServiceProtos.GetApplicationAttemptReportRequestProto)request);
                        }
                        case 3: {
                            return impl.getApplicationAttempts(controller, (YarnServiceProtos.GetApplicationAttemptsRequestProto)request);
                        }
                        case 4: {
                            return impl.getContainerReport(controller, (YarnServiceProtos.GetContainerReportRequestProto)request);
                        }
                        case 5: {
                            return impl.getContainers(controller, (YarnServiceProtos.GetContainersRequestProto)request);
                        }
                        case 6: {
                            return impl.getDelegationToken(controller, (SecurityProtos.GetDelegationTokenRequestProto)request);
                        }
                        case 7: {
                            return impl.renewDelegationToken(controller, (SecurityProtos.RenewDelegationTokenRequestProto)request);
                        }
                        case 8: {
                            return impl.cancelDelegationToken(controller, (SecurityProtos.CancelDelegationTokenRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationHistoryProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServiceProtos.GetApplicationAttemptsRequestProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
                        }
                        case 6: {
                            return SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
                        }
                        case 7: {
                            return SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
                        }
                        case 8: {
                            return SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationHistoryProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
                        }
                        case 6: {
                            return SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
                        }
                        case 7: {
                            return SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
                        }
                        case 8: {
                            return SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> p2);
        
        public abstract void getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> p2);
        
        public abstract void getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> p2);
        
        public abstract void getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> p2);
        
        public abstract void getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> p2);
        
        public abstract void getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> p2);
        
        public abstract void getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> p2);
        
        public abstract void renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> p2);
        
        public abstract void cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ApplicationHistoryProtocol.getDescriptor().getServices().get(0);
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
                    this.getApplicationReport(controller, (YarnServiceProtos.GetApplicationReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.getApplications(controller, (YarnServiceProtos.GetApplicationsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.getApplicationAttemptReport(controller, (YarnServiceProtos.GetApplicationAttemptReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 3: {
                    this.getApplicationAttempts(controller, (YarnServiceProtos.GetApplicationAttemptsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 4: {
                    this.getContainerReport(controller, (YarnServiceProtos.GetContainerReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 5: {
                    this.getContainers(controller, (YarnServiceProtos.GetContainersRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 6: {
                    this.getDelegationToken(controller, (SecurityProtos.GetDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 7: {
                    this.renewDelegationToken(controller, (SecurityProtos.RenewDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 8: {
                    this.cancelDelegationToken(controller, (SecurityProtos.CancelDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServiceProtos.GetApplicationAttemptsRequestProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
                }
                case 6: {
                    return SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
                }
                case 7: {
                    return SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
                }
                case 8: {
                    return SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
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
                    return YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
                }
                case 6: {
                    return SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
                }
                case 7: {
                    return SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
                }
                case 8: {
                    return SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ApplicationHistoryProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationReportResponseProto.class, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationsResponseProto.class, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationAttemptReportResponseProto.class, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationAttemptsResponseProto.class, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto request, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetContainerReportResponseProto.class, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto request, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetContainersResponseProto.class, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(6), controller, request, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.GetDelegationTokenResponseProto.class, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(7), controller, request, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.RenewDelegationTokenResponseProto.class, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(8), controller, request, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.CancelDelegationTokenResponseProto.class, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServiceProtos.GetApplicationReportResponseProto getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationReportResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationsResponseProto getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationsResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationAttemptReportResponseProto getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationAttemptReportResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationAttemptsResponseProto getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationAttemptsResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetContainerReportResponseProto getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetContainerReportResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetContainersResponseProto getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetContainersResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.GetDelegationTokenResponseProto getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.GetDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(6), controller, request, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.RenewDelegationTokenResponseProto renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.RenewDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(7), controller, request, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.CancelDelegationTokenResponseProto cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.CancelDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationHistoryProtocolService.getDescriptor().getMethods().get(8), controller, request, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServiceProtos.GetApplicationReportResponseProto getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationsResponseProto getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationAttemptReportResponseProto getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationAttemptsResponseProto getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetContainerReportResponseProto getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetContainersResponseProto getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1) throws ServiceException;
            
            SecurityProtos.GetDelegationTokenResponseProto getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1) throws ServiceException;
            
            SecurityProtos.RenewDelegationTokenResponseProto renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1) throws ServiceException;
            
            SecurityProtos.CancelDelegationTokenResponseProto cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> p2);
            
            void getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> p2);
            
            void getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> p2);
            
            void getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> p2);
            
            void getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> p2);
            
            void getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> p2);
            
            void getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> p2);
            
            void renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> p2);
            
            void cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> p2);
        }
    }
}
