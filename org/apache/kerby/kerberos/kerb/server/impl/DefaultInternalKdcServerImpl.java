// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.impl;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.apache.kerby.kerberos.kerb.server.preauth.PreauthHandler;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import org.apache.kerby.kerberos.kerb.server.KdcUtil;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import java.util.concurrent.Executors;
import org.apache.kerby.kerberos.kerb.server.KdcSetting;
import org.apache.kerby.kerberos.kerb.transport.KdcNetwork;
import org.apache.kerby.kerberos.kerb.server.KdcContext;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;

public class DefaultInternalKdcServerImpl extends AbstractInternalKdcServer
{
    private static final Logger LOG;
    private ExecutorService executor;
    private KdcContext kdcContext;
    private KdcNetwork network;
    
    public DefaultInternalKdcServerImpl(final KdcSetting kdcSetting) {
        super(kdcSetting);
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.prepareHandler();
        this.executor = Executors.newCachedThreadPool();
        (this.network = new KdcNetwork() {
            @Override
            protected void onNewTransport(final KrbTransport transport) {
                final DefaultKdcHandler kdcHandler = new DefaultKdcHandler(DefaultInternalKdcServerImpl.this.kdcContext, transport);
                DefaultInternalKdcServerImpl.this.executor.execute(kdcHandler);
            }
        }).init();
        final TransportPair tpair = KdcUtil.getTransportPair(this.getSetting());
        this.network.listen(tpair);
        this.network.start();
    }
    
    private void prepareHandler() {
        (this.kdcContext = new KdcContext(this.getSetting())).setIdentityService(this.getIdentityService());
        final PreauthHandler preauthHandler = new PreauthHandler();
        preauthHandler.init();
        this.kdcContext.setPreauthHandler(preauthHandler);
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (this.network != null) {
            this.network.stop();
        }
        if (this.executor != null) {
            this.executor.shutdown();
            try {
                boolean terminated = false;
                do {
                    terminated = this.executor.awaitTermination(60L, TimeUnit.SECONDS);
                } while (!terminated);
            }
            catch (InterruptedException e) {
                this.executor.shutdownNow();
                DefaultInternalKdcServerImpl.LOG.warn("waitForTermination interrupted");
            }
        }
        DefaultInternalKdcServerImpl.LOG.info("Default Internal kdc server stopped.");
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultInternalKdcServerImpl.class);
    }
}
