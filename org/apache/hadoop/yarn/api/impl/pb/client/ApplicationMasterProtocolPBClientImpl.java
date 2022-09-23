// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.impl.pb.client;

import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterResponsePBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateResponsePBImpl;
import com.google.protobuf.RpcController;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateRequestPBImpl;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import java.io.IOException;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocolPB;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;

@InterfaceAudience.Private
public class ApplicationMasterProtocolPBClientImpl implements ApplicationMasterProtocol, Closeable
{
    private ApplicationMasterProtocolPB proxy;
    
    public ApplicationMasterProtocolPBClientImpl(final long clientVersion, final InetSocketAddress addr, final Configuration conf) throws IOException {
        RPC.setProtocolEngine(conf, ApplicationMasterProtocolPB.class, ProtobufRpcEngine.class);
        this.proxy = RPC.getProxy(ApplicationMasterProtocolPB.class, clientVersion, addr, conf);
    }
    
    @Override
    public void close() {
        if (this.proxy != null) {
            RPC.stopProxy(this.proxy);
        }
    }
    
    @Override
    public AllocateResponse allocate(final AllocateRequest request) throws YarnException, IOException {
        final YarnServiceProtos.AllocateRequestProto requestProto = ((AllocateRequestPBImpl)request).getProto();
        try {
            return new AllocateResponsePBImpl(this.proxy.allocate(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public FinishApplicationMasterResponse finishApplicationMaster(final FinishApplicationMasterRequest request) throws YarnException, IOException {
        final YarnServiceProtos.FinishApplicationMasterRequestProto requestProto = ((FinishApplicationMasterRequestPBImpl)request).getProto();
        try {
            return new FinishApplicationMasterResponsePBImpl(this.proxy.finishApplicationMaster(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
    
    @Override
    public RegisterApplicationMasterResponse registerApplicationMaster(final RegisterApplicationMasterRequest request) throws YarnException, IOException {
        final YarnServiceProtos.RegisterApplicationMasterRequestProto requestProto = ((RegisterApplicationMasterRequestPBImpl)request).getProto();
        try {
            return new RegisterApplicationMasterResponsePBImpl(this.proxy.registerApplicationMaster(null, requestProto));
        }
        catch (ServiceException e) {
            RPCUtil.unwrapAndThrowException(e);
            return null;
        }
    }
}
