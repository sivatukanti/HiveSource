// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.service;

import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateRequestPBImpl;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocolPB;

@InterfaceAudience.Private
public class ApplicationMasterProtocolPBServiceImpl implements ApplicationMasterProtocolPB
{
    private org.apache.hadoop.yarn.api.ApplicationMasterProtocol real;
    
    public ApplicationMasterProtocolPBServiceImpl(final org.apache.hadoop.yarn.api.ApplicationMasterProtocol impl) {
        this.real = impl;
    }
    
    @Override
    public YarnServiceProtos.AllocateResponseProto allocate(final RpcController arg0, final YarnServiceProtos.AllocateRequestProto proto) throws ServiceException {
        final AllocateRequestPBImpl request = new AllocateRequestPBImpl(proto);
        try {
            final AllocateResponse response = this.real.allocate(request);
            return ((AllocateResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.FinishApplicationMasterResponseProto finishApplicationMaster(final RpcController arg0, final YarnServiceProtos.FinishApplicationMasterRequestProto proto) throws ServiceException {
        final FinishApplicationMasterRequestPBImpl request = new FinishApplicationMasterRequestPBImpl(proto);
        try {
            final FinishApplicationMasterResponse response = this.real.finishApplicationMaster(request);
            return ((FinishApplicationMasterResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
    
    @Override
    public YarnServiceProtos.RegisterApplicationMasterResponseProto registerApplicationMaster(final RpcController arg0, final YarnServiceProtos.RegisterApplicationMasterRequestProto proto) throws ServiceException {
        final RegisterApplicationMasterRequestPBImpl request = new RegisterApplicationMasterRequestPBImpl(proto);
        try {
            final RegisterApplicationMasterResponse response = this.real.registerApplicationMaster(request);
            return ((RegisterApplicationMasterResponsePBImpl)response).getProto();
        }
        catch (YarnException e) {
            throw new ServiceException(e);
        }
        catch (IOException e2) {
            throw new ServiceException(e2);
        }
    }
}
