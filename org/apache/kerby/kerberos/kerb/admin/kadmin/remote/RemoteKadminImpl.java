// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.GetprincsRequest;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.RenamePrincipalRequest;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.DeletePrincipalRequest;
import java.util.List;
import java.io.File;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.AdminRequest;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl.DefaultAdminHandler;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.AddPrincipalRequest;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.transport.KrbNetwork;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl.InternalAdminClient;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.admin.kadmin.Kadmin;

public class RemoteKadminImpl implements Kadmin
{
    private static final Logger LOG;
    private InternalAdminClient innerClient;
    private KrbTransport transport;
    
    public RemoteKadminImpl(final InternalAdminClient innerClient) throws KrbException {
        this.innerClient = innerClient;
        TransportPair tpair = null;
        try {
            tpair = AdminUtil.getTransportPair(innerClient.getSetting());
        }
        catch (KrbException e) {
            RemoteKadminImpl.LOG.error("Fail to get transport pair. " + e);
        }
        final KrbNetwork network = new KrbNetwork();
        network.setSocketTimeout(innerClient.getSetting().getTimeout());
        try {
            this.transport = network.connect(tpair);
        }
        catch (IOException e2) {
            throw new KrbException("Failed to create transport", e2);
        }
    }
    
    public InternalAdminClient getInnerClient() {
        return this.innerClient;
    }
    
    @Override
    public String getKadminPrincipal() {
        return KrbUtil.makeKadminPrincipal(this.innerClient.getSetting().getKdcRealm()).getName();
    }
    
    @Override
    public void addPrincipal(final String principal) throws KrbException {
        final AdminRequest adRequest = new AddPrincipalRequest(principal);
        adRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        adminHandler.handleRequest(adRequest);
    }
    
    @Override
    public void addPrincipal(final String principal, final KOptions kOptions) throws KrbException {
        final AdminRequest adRequest = new AddPrincipalRequest(principal, kOptions);
        adRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        adminHandler.handleRequest(adRequest);
    }
    
    @Override
    public void addPrincipal(final String principal, final String password) throws KrbException {
        final AdminRequest addPrincipalRequest = new AddPrincipalRequest(principal, password);
        addPrincipalRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        adminHandler.handleRequest(addPrincipalRequest);
    }
    
    @Override
    public void addPrincipal(final String principal, final String password, final KOptions kOptions) throws KrbException {
    }
    
    @Override
    public void exportKeytab(final File keytabFile, final String principal) throws KrbException {
    }
    
    @Override
    public void exportKeytab(final File keytabFile, final List<String> principals) throws KrbException {
    }
    
    @Override
    public void exportKeytab(final File keytabFile) throws KrbException {
    }
    
    @Override
    public void removeKeytabEntriesOf(final File keytabFile, final String principal) throws KrbException {
    }
    
    @Override
    public void removeKeytabEntriesOf(final File keytabFile, final String principal, final int kvno) throws KrbException {
    }
    
    @Override
    public void removeOldKeytabEntriesOf(final File keytabFile, final String principal) throws KrbException {
    }
    
    @Override
    public void deletePrincipal(final String principal) throws KrbException {
        final AdminRequest deletePrincipalRequest = new DeletePrincipalRequest(principal);
        deletePrincipalRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        adminHandler.handleRequest(deletePrincipalRequest);
    }
    
    @Override
    public void modifyPrincipal(final String principal, final KOptions kOptions) throws KrbException {
    }
    
    @Override
    public void renamePrincipal(final String oldPrincipalName, final String newPrincipalName) throws KrbException {
        final AdminRequest renamePrincipalRequest = new RenamePrincipalRequest(oldPrincipalName, newPrincipalName);
        renamePrincipalRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        adminHandler.handleRequest(renamePrincipalRequest);
    }
    
    @Override
    public List<String> getPrincipals() throws KrbException {
        final AdminRequest grtPrincsRequest = new GetprincsRequest();
        grtPrincsRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        return adminHandler.handleRequestForList(grtPrincsRequest);
    }
    
    @Override
    public List<String> getPrincipals(final String globString) throws KrbException {
        final AdminRequest grtPrincsRequest = new GetprincsRequest(globString);
        grtPrincsRequest.setTransport(this.transport);
        final AdminHandler adminHandler = new DefaultAdminHandler();
        return adminHandler.handleRequestForList(grtPrincsRequest);
    }
    
    @Override
    public void changePassword(final String principal, final String newPassword) throws KrbException {
    }
    
    @Override
    public void updateKeys(final String principal) throws KrbException {
    }
    
    @Override
    public void release() throws KrbException {
    }
    
    static {
        LOG = LoggerFactory.getLogger(RemoteKadminImpl.class);
    }
}
