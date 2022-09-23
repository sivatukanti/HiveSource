// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import com.google.protobuf.BlockingService;
import org.apache.hadoop.ha.proto.ZKFCProtocolProtos;
import org.apache.hadoop.ha.protocolPB.ZKFCProtocolServerSideTranslatorPB;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ha.protocolPB.ZKFCProtocolPB;
import org.apache.hadoop.security.authorize.PolicyProvider;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Evolving
public class ZKFCRpcServer implements ZKFCProtocol
{
    private static final int HANDLER_COUNT = 3;
    private final ZKFailoverController zkfc;
    private RPC.Server server;
    
    ZKFCRpcServer(final Configuration conf, final InetSocketAddress bindAddr, final ZKFailoverController zkfc, final PolicyProvider policy) throws IOException {
        this.zkfc = zkfc;
        RPC.setProtocolEngine(conf, ZKFCProtocolPB.class, ProtobufRpcEngine.class);
        final ZKFCProtocolServerSideTranslatorPB translator = new ZKFCProtocolServerSideTranslatorPB(this);
        final BlockingService service = ZKFCProtocolProtos.ZKFCProtocolService.newReflectiveBlockingService(translator);
        this.server = new RPC.Builder(conf).setProtocol(ZKFCProtocolPB.class).setInstance(service).setBindAddress(bindAddr.getHostName()).setPort(bindAddr.getPort()).setNumHandlers(3).setVerbose(false).build();
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            this.server.refreshServiceAcl(conf, policy);
        }
    }
    
    void start() {
        this.server.start();
    }
    
    public InetSocketAddress getAddress() {
        return this.server.getListenerAddress();
    }
    
    void stopAndJoin() throws InterruptedException {
        this.server.stop();
        this.server.join();
    }
    
    @Override
    public void cedeActive(final int millisToCede) throws IOException, AccessControlException {
        this.zkfc.checkRpcAdminAccess();
        this.zkfc.cedeActive(millisToCede);
    }
    
    @Override
    public void gracefulFailover() throws IOException, AccessControlException {
        this.zkfc.checkRpcAdminAccess();
        this.zkfc.gracefulFailoverToYou();
    }
}
