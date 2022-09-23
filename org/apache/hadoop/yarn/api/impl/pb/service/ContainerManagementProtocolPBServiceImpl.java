// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.service;

import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerStatusesResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerStatusesRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.GetContainerStatusesRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StopContainersResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StopContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StopContainersRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StartContainersResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainersRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.StartContainersRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.ContainerManagementProtocolPB;

@InterfaceAudience.Private
public class ContainerManagementProtocolPBServiceImpl implements ContainerManagementProtocolPB
{
    private org.apache.hadoop.yarn.api.ContainerManagementProtocol real;
    
    public ContainerManagementProtocolPBServiceImpl(final org.apache.hadoop.yarn.api.ContainerManagementProtocol impl) {
        this.real = impl;
    }
    
    @Override
    public YarnServiceProtos.StartContainersResponseProto startContainers(final RpcController arg0, final YarnServiceProtos.StartContainersRequestProto proto) throws ServiceException {
        final StartContainersRequestPBImpl request = new StartContainersRequestPBImpl(proto);
        try {
            final StartContainersResponse response = this.real.startContainers(request);
            return ((StartContainersResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.StopContainersResponseProto stopContainers(final RpcController arg0, final YarnServiceProtos.StopContainersRequestProto proto) throws ServiceException {
        final StopContainersRequestPBImpl request = new StopContainersRequestPBImpl(proto);
        try {
            final StopContainersResponse response = this.real.stopContainers(request);
            return ((StopContainersResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.GetContainerStatusesResponseProto getContainerStatuses(final RpcController arg0, final YarnServiceProtos.GetContainerStatusesRequestProto proto) throws ServiceException {
        final GetContainerStatusesRequestPBImpl request = new GetContainerStatusesRequestPBImpl(proto);
        try {
            final GetContainerStatusesResponse response = this.real.getContainerStatuses(request);
            return ((GetContainerStatusesResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
}
