// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.impl;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.client.request.TgsRequest;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.kerberos.kerb.client.request.AsRequest;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import org.apache.kerby.kerberos.kerb.transport.KrbNetwork;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.client.ClientUtil;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.KrbSetting;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.slf4j.Logger;

public class DefaultInternalKrbClient extends AbstractInternalKrbClient
{
    private static final Logger LOG;
    private DefaultKrbHandler krbHandler;
    private KrbTransport transport;
    
    public DefaultInternalKrbClient(final KrbSetting krbSetting) {
        super(krbSetting);
    }
    
    @Override
    public void init() throws KrbException {
        super.init();
        (this.krbHandler = new DefaultKrbHandler()).init(this.getContext());
    }
    
    private void doRequest(final KdcRequest request) throws KrbException {
        final List<String> kdcList = ClientUtil.getKDCList(this.getSetting());
        final Iterator<String> tempKdc = kdcList.iterator();
        if (!tempKdc.hasNext()) {
            throw new KrbException("Cannot get kdc for realm " + this.getSetting().getKdcRealm());
        }
        try {
            this.sendIfPossible(request, tempKdc.next(), this.getSetting(), false);
            DefaultInternalKrbClient.LOG.info("Send to kdc success.");
        }
        catch (Exception first) {
            boolean ok = false;
            while (tempKdc.hasNext()) {
                try {
                    this.sendIfPossible(request, tempKdc.next(), this.getSetting(), true);
                    ok = true;
                    DefaultInternalKrbClient.LOG.info("Send to kdc success.");
                }
                catch (Exception ignore) {
                    DefaultInternalKrbClient.LOG.info("ignore this kdc");
                    continue;
                }
                break;
            }
            if (!ok) {
                if (first instanceof KrbException) {
                    throw (KrbException)first;
                }
                throw new KrbException("The request failed " + first.getMessage(), first);
            }
        }
        finally {
            if (this.transport != null) {
                this.transport.release();
            }
        }
    }
    
    private void sendIfPossible(final KdcRequest request, final String kdcString, final KrbSetting setting, final boolean tryNextKdc) throws KrbException, IOException {
        final TransportPair tpair = ClientUtil.getTransportPair(setting, kdcString);
        final KrbNetwork network = new KrbNetwork();
        network.setSocketTimeout(setting.getTimeout());
        request.setSessionData(this.transport = network.connect(tpair));
        this.krbHandler.handleRequest(request, tryNextKdc);
    }
    
    @Override
    protected TgtTicket doRequestTgt(final AsRequest tgtTktReq) throws KrbException {
        this.doRequest(tgtTktReq);
        return tgtTktReq.getTicket();
    }
    
    @Override
    protected SgtTicket doRequestSgt(final TgsRequest ticketReq) throws KrbException {
        this.doRequest(ticketReq);
        return ticketReq.getSgt();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultInternalKrbClient.class);
    }
}
