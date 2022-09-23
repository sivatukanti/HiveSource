// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl;

import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.transport.KrbNetwork;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminUtil;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminHandler;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminSetting;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;

public class DefaultInternalAdminClient extends AbstractInternalAdminClient
{
    private DefaultAdminHandler adminHandler;
    private KrbTransport transport;
    
    public DefaultInternalAdminClient(final AdminSetting krbSetting) {
        super(krbSetting);
    }
    
    public AdminHandler getAdminHanlder() {
        return this.adminHandler;
    }
    
    public KrbTransport getTransport() {
        return this.transport;
    }
    
    @Override
    public void init() throws KrbException {
        super.init();
        (this.adminHandler = new DefaultAdminHandler()).init(this.getContext());
        final TransportPair tpair = AdminUtil.getTransportPair(this.getSetting());
        final KrbNetwork network = new KrbNetwork();
        network.setSocketTimeout(this.getSetting().getTimeout());
        try {
            this.transport = network.connect(tpair);
        }
        catch (IOException e) {
            throw new KrbException("Failed to create transport", e);
        }
    }
}
