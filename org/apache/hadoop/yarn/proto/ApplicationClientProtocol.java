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

public final class ApplicationClientProtocol
{
    private static Descriptors.FileDescriptor descriptor;
    
    private ApplicationClientProtocol() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return ApplicationClientProtocol.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n applicationclient_protocol.proto\u0012\u000bhadoop.yarn\u001a\u000eSecurity.proto\u001a\u0019yarn_service_protos.proto2\u00fd\u0013\n ApplicationClientProtocolService\u0012l\n\u0011getNewApplication\u0012*.hadoop.yarn.GetNewApplicationRequestProto\u001a+.hadoop.yarn.GetNewApplicationResponseProto\u0012u\n\u0014getApplicationReport\u0012-.hadoop.yarn.GetApplicationReportRequestProto\u001a..hadoop.yarn.GetApplicationReportResponseProto\u0012l\n\u0011submitApplication\u0012*.hadoop.yarn.SubmitAp", "plicationRequestProto\u001a+.hadoop.yarn.SubmitApplicationResponseProto\u0012k\n\u0014forceKillApplication\u0012(.hadoop.yarn.KillApplicationRequestProto\u001a).hadoop.yarn.KillApplicationResponseProto\u0012l\n\u0011getClusterMetrics\u0012*.hadoop.yarn.GetClusterMetricsRequestProto\u001a+.hadoop.yarn.GetClusterMetricsResponseProto\u0012f\n\u000fgetApplications\u0012(.hadoop.yarn.GetApplicationsRequestProto\u001a).hadoop.yarn.GetApplicationsResponseProto\u0012f\n\u000fgetClus", "terNodes\u0012(.hadoop.yarn.GetClusterNodesRequestProto\u001a).hadoop.yarn.GetClusterNodesResponseProto\u0012]\n\fgetQueueInfo\u0012%.hadoop.yarn.GetQueueInfoRequestProto\u001a&.hadoop.yarn.GetQueueInfoResponseProto\u0012q\n\u0010getQueueUserAcls\u0012-.hadoop.yarn.GetQueueUserAclsInfoRequestProto\u001a..hadoop.yarn.GetQueueUserAclsInfoResponseProto\u0012s\n\u0012getDelegationToken\u0012-.hadoop.common.GetDelegationTokenRequestProto\u001a..hadoop.common.GetDelegati", "onTokenResponseProto\u0012y\n\u0014renewDelegationToken\u0012/.hadoop.common.RenewDelegationTokenRequestProto\u001a0.hadoop.common.RenewDelegationTokenResponseProto\u0012|\n\u0015cancelDelegationToken\u00120.hadoop.common.CancelDelegationTokenRequestProto\u001a1.hadoop.common.CancelDelegationTokenResponseProto\u0012\u008a\u0001\n\u001bmoveApplicationAcrossQueues\u00124.hadoop.yarn.MoveApplicationAcrossQueuesRequestProto\u001a5.hadoop.yarn.MoveApplicationAcrossQueuesRes", "ponseProto\u0012\u008a\u0001\n\u001bgetApplicationAttemptReport\u00124.hadoop.yarn.GetApplicationAttemptReportRequestProto\u001a5.hadoop.yarn.GetApplicationAttemptReportResponseProto\u0012{\n\u0016getApplicationAttempts\u0012/.hadoop.yarn.GetApplicationAttemptsRequestProto\u001a0.hadoop.yarn.GetApplicationAttemptsResponseProto\u0012o\n\u0012getContainerReport\u0012+.hadoop.yarn.GetContainerReportRequestProto\u001a,.hadoop.yarn.GetContainerReportResponseProto\u0012`\n\rgetCont", "ainers\u0012&.hadoop.yarn.GetContainersRequestProto\u001a'.hadoop.yarn.GetContainersResponseProto\u0012t\n\u0011submitReservation\u0012..hadoop.yarn.ReservationSubmissionRequestProto\u001a/.hadoop.yarn.ReservationSubmissionResponseProto\u0012l\n\u0011updateReservation\u0012*.hadoop.yarn.ReservationUpdateRequestProto\u001a+.hadoop.yarn.ReservationUpdateResponseProto\u0012l\n\u0011deleteReservation\u0012*.hadoop.yarn.ReservationDeleteRequestProto\u001a+.hadoop.yarn.Reser", "vationDeleteResponseProto\u0012h\n\u000fgetNodeToLabels\u0012).hadoop.yarn.GetNodesToLabelsRequestProto\u001a*.hadoop.yarn.GetNodesToLabelsResponseProto\u0012u\n\u0014getClusterNodeLabels\u0012-.hadoop.yarn.GetClusterNodeLabelsRequestProto\u001a..hadoop.yarn.GetClusterNodeLabelsResponseProtoB?\n\u001corg.apache.hadoop.yarn.protoB\u0019ApplicationClientProtocol\u0088\u0001\u0001 \u0001\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                ApplicationClientProtocol.descriptor = root;
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { SecurityProtos.getDescriptor(), YarnServiceProtos.getDescriptor() }, assigner);
    }
    
    public abstract static class ApplicationClientProtocolService implements Service
    {
        protected ApplicationClientProtocolService() {
        }
        
        public static Service newReflectiveService(final Interface impl) {
            return new ApplicationClientProtocolService() {
                @Override
                public void getNewApplication(final RpcController controller, final YarnServiceProtos.GetNewApplicationRequestProto request, final RpcCallback<YarnServiceProtos.GetNewApplicationResponseProto> done) {
                    impl.getNewApplication(controller, request, done);
                }
                
                @Override
                public void getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> done) {
                    impl.getApplicationReport(controller, request, done);
                }
                
                @Override
                public void submitApplication(final RpcController controller, final YarnServiceProtos.SubmitApplicationRequestProto request, final RpcCallback<YarnServiceProtos.SubmitApplicationResponseProto> done) {
                    impl.submitApplication(controller, request, done);
                }
                
                @Override
                public void forceKillApplication(final RpcController controller, final YarnServiceProtos.KillApplicationRequestProto request, final RpcCallback<YarnServiceProtos.KillApplicationResponseProto> done) {
                    impl.forceKillApplication(controller, request, done);
                }
                
                @Override
                public void getClusterMetrics(final RpcController controller, final YarnServiceProtos.GetClusterMetricsRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterMetricsResponseProto> done) {
                    impl.getClusterMetrics(controller, request, done);
                }
                
                @Override
                public void getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> done) {
                    impl.getApplications(controller, request, done);
                }
                
                @Override
                public void getClusterNodes(final RpcController controller, final YarnServiceProtos.GetClusterNodesRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterNodesResponseProto> done) {
                    impl.getClusterNodes(controller, request, done);
                }
                
                @Override
                public void getQueueInfo(final RpcController controller, final YarnServiceProtos.GetQueueInfoRequestProto request, final RpcCallback<YarnServiceProtos.GetQueueInfoResponseProto> done) {
                    impl.getQueueInfo(controller, request, done);
                }
                
                @Override
                public void getQueueUserAcls(final RpcController controller, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto request, final RpcCallback<YarnServiceProtos.GetQueueUserAclsInfoResponseProto> done) {
                    impl.getQueueUserAcls(controller, request, done);
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
                
                @Override
                public void moveApplicationAcrossQueues(final RpcController controller, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto request, final RpcCallback<YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto> done) {
                    impl.moveApplicationAcrossQueues(controller, request, done);
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
                public void submitReservation(final RpcController controller, final YarnServiceProtos.ReservationSubmissionRequestProto request, final RpcCallback<YarnServiceProtos.ReservationSubmissionResponseProto> done) {
                    impl.submitReservation(controller, request, done);
                }
                
                @Override
                public void updateReservation(final RpcController controller, final YarnServiceProtos.ReservationUpdateRequestProto request, final RpcCallback<YarnServiceProtos.ReservationUpdateResponseProto> done) {
                    impl.updateReservation(controller, request, done);
                }
                
                @Override
                public void deleteReservation(final RpcController controller, final YarnServiceProtos.ReservationDeleteRequestProto request, final RpcCallback<YarnServiceProtos.ReservationDeleteResponseProto> done) {
                    impl.deleteReservation(controller, request, done);
                }
                
                @Override
                public void getNodeToLabels(final RpcController controller, final YarnServiceProtos.GetNodesToLabelsRequestProto request, final RpcCallback<YarnServiceProtos.GetNodesToLabelsResponseProto> done) {
                    impl.getNodeToLabels(controller, request, done);
                }
                
                @Override
                public void getClusterNodeLabels(final RpcController controller, final YarnServiceProtos.GetClusterNodeLabelsRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterNodeLabelsResponseProto> done) {
                    impl.getClusterNodeLabels(controller, request, done);
                }
            };
        }
        
        public static BlockingService newReflectiveBlockingService(final BlockingInterface impl) {
            return new BlockingService() {
                @Override
                public final Descriptors.ServiceDescriptor getDescriptorForType() {
                    return ApplicationClientProtocolService.getDescriptor();
                }
                
                @Override
                public final Message callBlockingMethod(final Descriptors.MethodDescriptor method, final RpcController controller, final Message request) throws ServiceException {
                    if (method.getService() != ApplicationClientProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.callBlockingMethod() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return impl.getNewApplication(controller, (YarnServiceProtos.GetNewApplicationRequestProto)request);
                        }
                        case 1: {
                            return impl.getApplicationReport(controller, (YarnServiceProtos.GetApplicationReportRequestProto)request);
                        }
                        case 2: {
                            return impl.submitApplication(controller, (YarnServiceProtos.SubmitApplicationRequestProto)request);
                        }
                        case 3: {
                            return impl.forceKillApplication(controller, (YarnServiceProtos.KillApplicationRequestProto)request);
                        }
                        case 4: {
                            return impl.getClusterMetrics(controller, (YarnServiceProtos.GetClusterMetricsRequestProto)request);
                        }
                        case 5: {
                            return impl.getApplications(controller, (YarnServiceProtos.GetApplicationsRequestProto)request);
                        }
                        case 6: {
                            return impl.getClusterNodes(controller, (YarnServiceProtos.GetClusterNodesRequestProto)request);
                        }
                        case 7: {
                            return impl.getQueueInfo(controller, (YarnServiceProtos.GetQueueInfoRequestProto)request);
                        }
                        case 8: {
                            return impl.getQueueUserAcls(controller, (YarnServiceProtos.GetQueueUserAclsInfoRequestProto)request);
                        }
                        case 9: {
                            return impl.getDelegationToken(controller, (SecurityProtos.GetDelegationTokenRequestProto)request);
                        }
                        case 10: {
                            return impl.renewDelegationToken(controller, (SecurityProtos.RenewDelegationTokenRequestProto)request);
                        }
                        case 11: {
                            return impl.cancelDelegationToken(controller, (SecurityProtos.CancelDelegationTokenRequestProto)request);
                        }
                        case 12: {
                            return impl.moveApplicationAcrossQueues(controller, (YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto)request);
                        }
                        case 13: {
                            return impl.getApplicationAttemptReport(controller, (YarnServiceProtos.GetApplicationAttemptReportRequestProto)request);
                        }
                        case 14: {
                            return impl.getApplicationAttempts(controller, (YarnServiceProtos.GetApplicationAttemptsRequestProto)request);
                        }
                        case 15: {
                            return impl.getContainerReport(controller, (YarnServiceProtos.GetContainerReportRequestProto)request);
                        }
                        case 16: {
                            return impl.getContainers(controller, (YarnServiceProtos.GetContainersRequestProto)request);
                        }
                        case 17: {
                            return impl.submitReservation(controller, (YarnServiceProtos.ReservationSubmissionRequestProto)request);
                        }
                        case 18: {
                            return impl.updateReservation(controller, (YarnServiceProtos.ReservationUpdateRequestProto)request);
                        }
                        case 19: {
                            return impl.deleteReservation(controller, (YarnServiceProtos.ReservationDeleteRequestProto)request);
                        }
                        case 20: {
                            return impl.getNodeToLabels(controller, (YarnServiceProtos.GetNodesToLabelsRequestProto)request);
                        }
                        case 21: {
                            return impl.getClusterNodeLabels(controller, (YarnServiceProtos.GetClusterNodeLabelsRequestProto)request);
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getRequestPrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationClientProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getRequestPrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.GetNewApplicationRequestProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.SubmitApplicationRequestProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServiceProtos.KillApplicationRequestProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServiceProtos.GetClusterMetricsRequestProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
                        }
                        case 6: {
                            return YarnServiceProtos.GetClusterNodesRequestProto.getDefaultInstance();
                        }
                        case 7: {
                            return YarnServiceProtos.GetQueueInfoRequestProto.getDefaultInstance();
                        }
                        case 8: {
                            return YarnServiceProtos.GetQueueUserAclsInfoRequestProto.getDefaultInstance();
                        }
                        case 9: {
                            return SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
                        }
                        case 10: {
                            return SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
                        }
                        case 11: {
                            return SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
                        }
                        case 12: {
                            return YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.getDefaultInstance();
                        }
                        case 13: {
                            return YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
                        }
                        case 14: {
                            return YarnServiceProtos.GetApplicationAttemptsRequestProto.getDefaultInstance();
                        }
                        case 15: {
                            return YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
                        }
                        case 16: {
                            return YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
                        }
                        case 17: {
                            return YarnServiceProtos.ReservationSubmissionRequestProto.getDefaultInstance();
                        }
                        case 18: {
                            return YarnServiceProtos.ReservationUpdateRequestProto.getDefaultInstance();
                        }
                        case 19: {
                            return YarnServiceProtos.ReservationDeleteRequestProto.getDefaultInstance();
                        }
                        case 20: {
                            return YarnServiceProtos.GetNodesToLabelsRequestProto.getDefaultInstance();
                        }
                        case 21: {
                            return YarnServiceProtos.GetClusterNodeLabelsRequestProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
                
                @Override
                public final Message getResponsePrototype(final Descriptors.MethodDescriptor method) {
                    if (method.getService() != ApplicationClientProtocolService.getDescriptor()) {
                        throw new IllegalArgumentException("Service.getResponsePrototype() given method descriptor for wrong service type.");
                    }
                    switch (method.getIndex()) {
                        case 0: {
                            return YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance();
                        }
                        case 1: {
                            return YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
                        }
                        case 2: {
                            return YarnServiceProtos.SubmitApplicationResponseProto.getDefaultInstance();
                        }
                        case 3: {
                            return YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance();
                        }
                        case 4: {
                            return YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance();
                        }
                        case 5: {
                            return YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
                        }
                        case 6: {
                            return YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance();
                        }
                        case 7: {
                            return YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance();
                        }
                        case 8: {
                            return YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance();
                        }
                        case 9: {
                            return SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
                        }
                        case 10: {
                            return SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
                        }
                        case 11: {
                            return SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
                        }
                        case 12: {
                            return YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance();
                        }
                        case 13: {
                            return YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
                        }
                        case 14: {
                            return YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
                        }
                        case 15: {
                            return YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
                        }
                        case 16: {
                            return YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
                        }
                        case 17: {
                            return YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance();
                        }
                        case 18: {
                            return YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance();
                        }
                        case 19: {
                            return YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance();
                        }
                        case 20: {
                            return YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance();
                        }
                        case 21: {
                            return YarnServiceProtos.GetClusterNodeLabelsResponseProto.getDefaultInstance();
                        }
                        default: {
                            throw new AssertionError((Object)"Can't get here.");
                        }
                    }
                }
            };
        }
        
        public abstract void getNewApplication(final RpcController p0, final YarnServiceProtos.GetNewApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.GetNewApplicationResponseProto> p2);
        
        public abstract void getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> p2);
        
        public abstract void submitApplication(final RpcController p0, final YarnServiceProtos.SubmitApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.SubmitApplicationResponseProto> p2);
        
        public abstract void forceKillApplication(final RpcController p0, final YarnServiceProtos.KillApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.KillApplicationResponseProto> p2);
        
        public abstract void getClusterMetrics(final RpcController p0, final YarnServiceProtos.GetClusterMetricsRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterMetricsResponseProto> p2);
        
        public abstract void getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> p2);
        
        public abstract void getClusterNodes(final RpcController p0, final YarnServiceProtos.GetClusterNodesRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterNodesResponseProto> p2);
        
        public abstract void getQueueInfo(final RpcController p0, final YarnServiceProtos.GetQueueInfoRequestProto p1, final RpcCallback<YarnServiceProtos.GetQueueInfoResponseProto> p2);
        
        public abstract void getQueueUserAcls(final RpcController p0, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto p1, final RpcCallback<YarnServiceProtos.GetQueueUserAclsInfoResponseProto> p2);
        
        public abstract void getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> p2);
        
        public abstract void renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> p2);
        
        public abstract void cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> p2);
        
        public abstract void moveApplicationAcrossQueues(final RpcController p0, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto p1, final RpcCallback<YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto> p2);
        
        public abstract void getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> p2);
        
        public abstract void getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> p2);
        
        public abstract void getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> p2);
        
        public abstract void getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> p2);
        
        public abstract void submitReservation(final RpcController p0, final YarnServiceProtos.ReservationSubmissionRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationSubmissionResponseProto> p2);
        
        public abstract void updateReservation(final RpcController p0, final YarnServiceProtos.ReservationUpdateRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationUpdateResponseProto> p2);
        
        public abstract void deleteReservation(final RpcController p0, final YarnServiceProtos.ReservationDeleteRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationDeleteResponseProto> p2);
        
        public abstract void getNodeToLabels(final RpcController p0, final YarnServiceProtos.GetNodesToLabelsRequestProto p1, final RpcCallback<YarnServiceProtos.GetNodesToLabelsResponseProto> p2);
        
        public abstract void getClusterNodeLabels(final RpcController p0, final YarnServiceProtos.GetClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterNodeLabelsResponseProto> p2);
        
        public static final Descriptors.ServiceDescriptor getDescriptor() {
            return ApplicationClientProtocol.getDescriptor().getServices().get(0);
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
                    this.getNewApplication(controller, (YarnServiceProtos.GetNewApplicationRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 1: {
                    this.getApplicationReport(controller, (YarnServiceProtos.GetApplicationReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 2: {
                    this.submitApplication(controller, (YarnServiceProtos.SubmitApplicationRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 3: {
                    this.forceKillApplication(controller, (YarnServiceProtos.KillApplicationRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 4: {
                    this.getClusterMetrics(controller, (YarnServiceProtos.GetClusterMetricsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 5: {
                    this.getApplications(controller, (YarnServiceProtos.GetApplicationsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 6: {
                    this.getClusterNodes(controller, (YarnServiceProtos.GetClusterNodesRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 7: {
                    this.getQueueInfo(controller, (YarnServiceProtos.GetQueueInfoRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 8: {
                    this.getQueueUserAcls(controller, (YarnServiceProtos.GetQueueUserAclsInfoRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 9: {
                    this.getDelegationToken(controller, (SecurityProtos.GetDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 10: {
                    this.renewDelegationToken(controller, (SecurityProtos.RenewDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 11: {
                    this.cancelDelegationToken(controller, (SecurityProtos.CancelDelegationTokenRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 12: {
                    this.moveApplicationAcrossQueues(controller, (YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 13: {
                    this.getApplicationAttemptReport(controller, (YarnServiceProtos.GetApplicationAttemptReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 14: {
                    this.getApplicationAttempts(controller, (YarnServiceProtos.GetApplicationAttemptsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 15: {
                    this.getContainerReport(controller, (YarnServiceProtos.GetContainerReportRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 16: {
                    this.getContainers(controller, (YarnServiceProtos.GetContainersRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 17: {
                    this.submitReservation(controller, (YarnServiceProtos.ReservationSubmissionRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 18: {
                    this.updateReservation(controller, (YarnServiceProtos.ReservationUpdateRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 19: {
                    this.deleteReservation(controller, (YarnServiceProtos.ReservationDeleteRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 20: {
                    this.getNodeToLabels(controller, (YarnServiceProtos.GetNodesToLabelsRequestProto)request, RpcUtil.specializeCallback(done));
                }
                case 21: {
                    this.getClusterNodeLabels(controller, (YarnServiceProtos.GetClusterNodeLabelsRequestProto)request, RpcUtil.specializeCallback(done));
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
                    return YarnServiceProtos.GetNewApplicationRequestProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.SubmitApplicationRequestProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServiceProtos.KillApplicationRequestProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServiceProtos.GetClusterMetricsRequestProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
                }
                case 6: {
                    return YarnServiceProtos.GetClusterNodesRequestProto.getDefaultInstance();
                }
                case 7: {
                    return YarnServiceProtos.GetQueueInfoRequestProto.getDefaultInstance();
                }
                case 8: {
                    return YarnServiceProtos.GetQueueUserAclsInfoRequestProto.getDefaultInstance();
                }
                case 9: {
                    return SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
                }
                case 10: {
                    return SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
                }
                case 11: {
                    return SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
                }
                case 12: {
                    return YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.getDefaultInstance();
                }
                case 13: {
                    return YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
                }
                case 14: {
                    return YarnServiceProtos.GetApplicationAttemptsRequestProto.getDefaultInstance();
                }
                case 15: {
                    return YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
                }
                case 16: {
                    return YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
                }
                case 17: {
                    return YarnServiceProtos.ReservationSubmissionRequestProto.getDefaultInstance();
                }
                case 18: {
                    return YarnServiceProtos.ReservationUpdateRequestProto.getDefaultInstance();
                }
                case 19: {
                    return YarnServiceProtos.ReservationDeleteRequestProto.getDefaultInstance();
                }
                case 20: {
                    return YarnServiceProtos.GetNodesToLabelsRequestProto.getDefaultInstance();
                }
                case 21: {
                    return YarnServiceProtos.GetClusterNodeLabelsRequestProto.getDefaultInstance();
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
                    return YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance();
                }
                case 1: {
                    return YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
                }
                case 2: {
                    return YarnServiceProtos.SubmitApplicationResponseProto.getDefaultInstance();
                }
                case 3: {
                    return YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance();
                }
                case 4: {
                    return YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance();
                }
                case 5: {
                    return YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
                }
                case 6: {
                    return YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance();
                }
                case 7: {
                    return YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance();
                }
                case 8: {
                    return YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance();
                }
                case 9: {
                    return SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
                }
                case 10: {
                    return SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
                }
                case 11: {
                    return SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance();
                }
                case 12: {
                    return YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance();
                }
                case 13: {
                    return YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
                }
                case 14: {
                    return YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
                }
                case 15: {
                    return YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
                }
                case 16: {
                    return YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
                }
                case 17: {
                    return YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance();
                }
                case 18: {
                    return YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance();
                }
                case 19: {
                    return YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance();
                }
                case 20: {
                    return YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance();
                }
                case 21: {
                    return YarnServiceProtos.GetClusterNodeLabelsResponseProto.getDefaultInstance();
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
        
        public static final class Stub extends ApplicationClientProtocolService implements Interface
        {
            private final RpcChannel channel;
            
            private Stub(final RpcChannel channel) {
                this.channel = channel;
            }
            
            public RpcChannel getChannel() {
                return this.channel;
            }
            
            @Override
            public void getNewApplication(final RpcController controller, final YarnServiceProtos.GetNewApplicationRequestProto request, final RpcCallback<YarnServiceProtos.GetNewApplicationResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetNewApplicationResponseProto.class, YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationReportResponseProto.class, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void submitApplication(final RpcController controller, final YarnServiceProtos.SubmitApplicationRequestProto request, final RpcCallback<YarnServiceProtos.SubmitApplicationResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.SubmitApplicationResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.SubmitApplicationResponseProto.class, YarnServiceProtos.SubmitApplicationResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void forceKillApplication(final RpcController controller, final YarnServiceProtos.KillApplicationRequestProto request, final RpcCallback<YarnServiceProtos.KillApplicationResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.KillApplicationResponseProto.class, YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getClusterMetrics(final RpcController controller, final YarnServiceProtos.GetClusterMetricsRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterMetricsResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetClusterMetricsResponseProto.class, YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationsResponseProto.class, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getClusterNodes(final RpcController controller, final YarnServiceProtos.GetClusterNodesRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterNodesResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(6), controller, request, YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetClusterNodesResponseProto.class, YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getQueueInfo(final RpcController controller, final YarnServiceProtos.GetQueueInfoRequestProto request, final RpcCallback<YarnServiceProtos.GetQueueInfoResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(7), controller, request, YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetQueueInfoResponseProto.class, YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getQueueUserAcls(final RpcController controller, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto request, final RpcCallback<YarnServiceProtos.GetQueueUserAclsInfoResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(8), controller, request, YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetQueueUserAclsInfoResponseProto.class, YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(9), controller, request, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.GetDelegationTokenResponseProto.class, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(10), controller, request, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.RenewDelegationTokenResponseProto.class, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto request, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(11), controller, request, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, SecurityProtos.CancelDelegationTokenResponseProto.class, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void moveApplicationAcrossQueues(final RpcController controller, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto request, final RpcCallback<YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(12), controller, request, YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.class, YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(13), controller, request, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationAttemptReportResponseProto.class, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto request, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(14), controller, request, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetApplicationAttemptsResponseProto.class, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto request, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(15), controller, request, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetContainerReportResponseProto.class, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto request, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(16), controller, request, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetContainersResponseProto.class, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void submitReservation(final RpcController controller, final YarnServiceProtos.ReservationSubmissionRequestProto request, final RpcCallback<YarnServiceProtos.ReservationSubmissionResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(17), controller, request, YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.ReservationSubmissionResponseProto.class, YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void updateReservation(final RpcController controller, final YarnServiceProtos.ReservationUpdateRequestProto request, final RpcCallback<YarnServiceProtos.ReservationUpdateResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(18), controller, request, YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.ReservationUpdateResponseProto.class, YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void deleteReservation(final RpcController controller, final YarnServiceProtos.ReservationDeleteRequestProto request, final RpcCallback<YarnServiceProtos.ReservationDeleteResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(19), controller, request, YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.ReservationDeleteResponseProto.class, YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getNodeToLabels(final RpcController controller, final YarnServiceProtos.GetNodesToLabelsRequestProto request, final RpcCallback<YarnServiceProtos.GetNodesToLabelsResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(20), controller, request, YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetNodesToLabelsResponseProto.class, YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance()));
            }
            
            @Override
            public void getClusterNodeLabels(final RpcController controller, final YarnServiceProtos.GetClusterNodeLabelsRequestProto request, final RpcCallback<YarnServiceProtos.GetClusterNodeLabelsResponseProto> done) {
                this.channel.callMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(21), controller, request, YarnServiceProtos.GetClusterNodeLabelsResponseProto.getDefaultInstance(), RpcUtil.generalizeCallback(done, YarnServiceProtos.GetClusterNodeLabelsResponseProto.class, YarnServiceProtos.GetClusterNodeLabelsResponseProto.getDefaultInstance()));
            }
        }
        
        private static final class BlockingStub implements BlockingInterface
        {
            private final BlockingRpcChannel channel;
            
            private BlockingStub(final BlockingRpcChannel channel) {
                this.channel = channel;
            }
            
            @Override
            public YarnServiceProtos.GetNewApplicationResponseProto getNewApplication(final RpcController controller, final YarnServiceProtos.GetNewApplicationRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetNewApplicationResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(0), controller, request, YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationReportResponseProto getApplicationReport(final RpcController controller, final YarnServiceProtos.GetApplicationReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationReportResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(1), controller, request, YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.SubmitApplicationResponseProto submitApplication(final RpcController controller, final YarnServiceProtos.SubmitApplicationRequestProto request) throws ServiceException {
                return (YarnServiceProtos.SubmitApplicationResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(2), controller, request, YarnServiceProtos.SubmitApplicationResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.KillApplicationResponseProto forceKillApplication(final RpcController controller, final YarnServiceProtos.KillApplicationRequestProto request) throws ServiceException {
                return (YarnServiceProtos.KillApplicationResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(3), controller, request, YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetClusterMetricsResponseProto getClusterMetrics(final RpcController controller, final YarnServiceProtos.GetClusterMetricsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetClusterMetricsResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(4), controller, request, YarnServiceProtos.GetClusterMetricsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationsResponseProto getApplications(final RpcController controller, final YarnServiceProtos.GetApplicationsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationsResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(5), controller, request, YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetClusterNodesResponseProto getClusterNodes(final RpcController controller, final YarnServiceProtos.GetClusterNodesRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetClusterNodesResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(6), controller, request, YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetQueueInfoResponseProto getQueueInfo(final RpcController controller, final YarnServiceProtos.GetQueueInfoRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetQueueInfoResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(7), controller, request, YarnServiceProtos.GetQueueInfoResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetQueueUserAclsInfoResponseProto getQueueUserAcls(final RpcController controller, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetQueueUserAclsInfoResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(8), controller, request, YarnServiceProtos.GetQueueUserAclsInfoResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.GetDelegationTokenResponseProto getDelegationToken(final RpcController controller, final SecurityProtos.GetDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.GetDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(9), controller, request, SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.RenewDelegationTokenResponseProto renewDelegationToken(final RpcController controller, final SecurityProtos.RenewDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.RenewDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(10), controller, request, SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance());
            }
            
            @Override
            public SecurityProtos.CancelDelegationTokenResponseProto cancelDelegationToken(final RpcController controller, final SecurityProtos.CancelDelegationTokenRequestProto request) throws ServiceException {
                return (SecurityProtos.CancelDelegationTokenResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(11), controller, request, SecurityProtos.CancelDelegationTokenResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto moveApplicationAcrossQueues(final RpcController controller, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto request) throws ServiceException {
                return (YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(12), controller, request, YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationAttemptReportResponseProto getApplicationAttemptReport(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationAttemptReportResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(13), controller, request, YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetApplicationAttemptsResponseProto getApplicationAttempts(final RpcController controller, final YarnServiceProtos.GetApplicationAttemptsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetApplicationAttemptsResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(14), controller, request, YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetContainerReportResponseProto getContainerReport(final RpcController controller, final YarnServiceProtos.GetContainerReportRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetContainerReportResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(15), controller, request, YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetContainersResponseProto getContainers(final RpcController controller, final YarnServiceProtos.GetContainersRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetContainersResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(16), controller, request, YarnServiceProtos.GetContainersResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.ReservationSubmissionResponseProto submitReservation(final RpcController controller, final YarnServiceProtos.ReservationSubmissionRequestProto request) throws ServiceException {
                return (YarnServiceProtos.ReservationSubmissionResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(17), controller, request, YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.ReservationUpdateResponseProto updateReservation(final RpcController controller, final YarnServiceProtos.ReservationUpdateRequestProto request) throws ServiceException {
                return (YarnServiceProtos.ReservationUpdateResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(18), controller, request, YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.ReservationDeleteResponseProto deleteReservation(final RpcController controller, final YarnServiceProtos.ReservationDeleteRequestProto request) throws ServiceException {
                return (YarnServiceProtos.ReservationDeleteResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(19), controller, request, YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetNodesToLabelsResponseProto getNodeToLabels(final RpcController controller, final YarnServiceProtos.GetNodesToLabelsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetNodesToLabelsResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(20), controller, request, YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance());
            }
            
            @Override
            public YarnServiceProtos.GetClusterNodeLabelsResponseProto getClusterNodeLabels(final RpcController controller, final YarnServiceProtos.GetClusterNodeLabelsRequestProto request) throws ServiceException {
                return (YarnServiceProtos.GetClusterNodeLabelsResponseProto)this.channel.callBlockingMethod(ApplicationClientProtocolService.getDescriptor().getMethods().get(21), controller, request, YarnServiceProtos.GetClusterNodeLabelsResponseProto.getDefaultInstance());
            }
        }
        
        public interface BlockingInterface
        {
            YarnServiceProtos.GetNewApplicationResponseProto getNewApplication(final RpcController p0, final YarnServiceProtos.GetNewApplicationRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationReportResponseProto getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.SubmitApplicationResponseProto submitApplication(final RpcController p0, final YarnServiceProtos.SubmitApplicationRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.KillApplicationResponseProto forceKillApplication(final RpcController p0, final YarnServiceProtos.KillApplicationRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetClusterMetricsResponseProto getClusterMetrics(final RpcController p0, final YarnServiceProtos.GetClusterMetricsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationsResponseProto getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetClusterNodesResponseProto getClusterNodes(final RpcController p0, final YarnServiceProtos.GetClusterNodesRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetQueueInfoResponseProto getQueueInfo(final RpcController p0, final YarnServiceProtos.GetQueueInfoRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetQueueUserAclsInfoResponseProto getQueueUserAcls(final RpcController p0, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto p1) throws ServiceException;
            
            SecurityProtos.GetDelegationTokenResponseProto getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1) throws ServiceException;
            
            SecurityProtos.RenewDelegationTokenResponseProto renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1) throws ServiceException;
            
            SecurityProtos.CancelDelegationTokenResponseProto cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto moveApplicationAcrossQueues(final RpcController p0, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationAttemptReportResponseProto getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetApplicationAttemptsResponseProto getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetContainerReportResponseProto getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetContainersResponseProto getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.ReservationSubmissionResponseProto submitReservation(final RpcController p0, final YarnServiceProtos.ReservationSubmissionRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.ReservationUpdateResponseProto updateReservation(final RpcController p0, final YarnServiceProtos.ReservationUpdateRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.ReservationDeleteResponseProto deleteReservation(final RpcController p0, final YarnServiceProtos.ReservationDeleteRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetNodesToLabelsResponseProto getNodeToLabels(final RpcController p0, final YarnServiceProtos.GetNodesToLabelsRequestProto p1) throws ServiceException;
            
            YarnServiceProtos.GetClusterNodeLabelsResponseProto getClusterNodeLabels(final RpcController p0, final YarnServiceProtos.GetClusterNodeLabelsRequestProto p1) throws ServiceException;
        }
        
        public interface Interface
        {
            void getNewApplication(final RpcController p0, final YarnServiceProtos.GetNewApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.GetNewApplicationResponseProto> p2);
            
            void getApplicationReport(final RpcController p0, final YarnServiceProtos.GetApplicationReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationReportResponseProto> p2);
            
            void submitApplication(final RpcController p0, final YarnServiceProtos.SubmitApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.SubmitApplicationResponseProto> p2);
            
            void forceKillApplication(final RpcController p0, final YarnServiceProtos.KillApplicationRequestProto p1, final RpcCallback<YarnServiceProtos.KillApplicationResponseProto> p2);
            
            void getClusterMetrics(final RpcController p0, final YarnServiceProtos.GetClusterMetricsRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterMetricsResponseProto> p2);
            
            void getApplications(final RpcController p0, final YarnServiceProtos.GetApplicationsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationsResponseProto> p2);
            
            void getClusterNodes(final RpcController p0, final YarnServiceProtos.GetClusterNodesRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterNodesResponseProto> p2);
            
            void getQueueInfo(final RpcController p0, final YarnServiceProtos.GetQueueInfoRequestProto p1, final RpcCallback<YarnServiceProtos.GetQueueInfoResponseProto> p2);
            
            void getQueueUserAcls(final RpcController p0, final YarnServiceProtos.GetQueueUserAclsInfoRequestProto p1, final RpcCallback<YarnServiceProtos.GetQueueUserAclsInfoResponseProto> p2);
            
            void getDelegationToken(final RpcController p0, final SecurityProtos.GetDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.GetDelegationTokenResponseProto> p2);
            
            void renewDelegationToken(final RpcController p0, final SecurityProtos.RenewDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.RenewDelegationTokenResponseProto> p2);
            
            void cancelDelegationToken(final RpcController p0, final SecurityProtos.CancelDelegationTokenRequestProto p1, final RpcCallback<SecurityProtos.CancelDelegationTokenResponseProto> p2);
            
            void moveApplicationAcrossQueues(final RpcController p0, final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto p1, final RpcCallback<YarnServiceProtos.MoveApplicationAcrossQueuesResponseProto> p2);
            
            void getApplicationAttemptReport(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptReportResponseProto> p2);
            
            void getApplicationAttempts(final RpcController p0, final YarnServiceProtos.GetApplicationAttemptsRequestProto p1, final RpcCallback<YarnServiceProtos.GetApplicationAttemptsResponseProto> p2);
            
            void getContainerReport(final RpcController p0, final YarnServiceProtos.GetContainerReportRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainerReportResponseProto> p2);
            
            void getContainers(final RpcController p0, final YarnServiceProtos.GetContainersRequestProto p1, final RpcCallback<YarnServiceProtos.GetContainersResponseProto> p2);
            
            void submitReservation(final RpcController p0, final YarnServiceProtos.ReservationSubmissionRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationSubmissionResponseProto> p2);
            
            void updateReservation(final RpcController p0, final YarnServiceProtos.ReservationUpdateRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationUpdateResponseProto> p2);
            
            void deleteReservation(final RpcController p0, final YarnServiceProtos.ReservationDeleteRequestProto p1, final RpcCallback<YarnServiceProtos.ReservationDeleteResponseProto> p2);
            
            void getNodeToLabels(final RpcController p0, final YarnServiceProtos.GetNodesToLabelsRequestProto p1, final RpcCallback<YarnServiceProtos.GetNodesToLabelsResponseProto> p2);
            
            void getClusterNodeLabels(final RpcController p0, final YarnServiceProtos.GetClusterNodeLabelsRequestProto p1, final RpcCallback<YarnServiceProtos.GetClusterNodeLabelsResponseProto> p2);
        }
    }
}
