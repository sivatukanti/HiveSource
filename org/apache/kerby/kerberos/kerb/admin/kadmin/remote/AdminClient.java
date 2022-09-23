// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import java.util.List;
import org.apache.kerby.kerberos.kerb.admin.kadmin.Kadmin;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl.DefaultInternalAdminClient;
import org.apache.kerby.KOption;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl.InternalAdminClient;
import org.apache.kerby.KOptions;

public class AdminClient
{
    private final AdminConfig adminConfig;
    private final KOptions commonOptions;
    private final AdminSetting adminSetting;
    private InternalAdminClient innerClient;
    
    public AdminClient() throws KrbException {
        this.adminConfig = AdminUtil.getDefaultConfig();
        this.commonOptions = new KOptions();
        this.adminSetting = new AdminSetting(this.commonOptions, this.adminConfig);
    }
    
    public AdminClient(final AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
        this.commonOptions = new KOptions();
        this.adminSetting = new AdminSetting(this.commonOptions, adminConfig);
    }
    
    public AdminClient(final File confDir) throws KrbException {
        this.commonOptions = new KOptions();
        this.adminConfig = AdminUtil.getConfig(confDir);
        this.adminSetting = new AdminSetting(this.commonOptions, this.adminConfig);
    }
    
    public AdminClient(final AdminClient krbClient) {
        this.commonOptions = krbClient.commonOptions;
        this.adminConfig = krbClient.adminConfig;
        this.adminSetting = krbClient.adminSetting;
        this.innerClient = krbClient.innerClient;
    }
    
    public void setAdminRealm(final String realm) {
        this.commonOptions.add(AdminOption.ADMIN_REALM, realm);
    }
    
    public void setKeyTabFile(final File file) {
        this.commonOptions.add(AdminOption.KEYTAB_FILE, file);
    }
    
    public void setKdcHost(final String kdcHost) {
        this.commonOptions.add(AdminOption.ADMIN_HOST, kdcHost);
    }
    
    public void setAdminTcpPort(final int kdcTcpPort) {
        if (kdcTcpPort < 1) {
            throw new IllegalArgumentException("Invalid port");
        }
        this.commonOptions.add(AdminOption.ADMIN_TCP_PORT, kdcTcpPort);
        this.setAllowTcp(true);
    }
    
    public void setAllowUdp(final boolean allowUdp) {
        this.commonOptions.add(AdminOption.ALLOW_UDP, allowUdp);
    }
    
    public void setAllowTcp(final boolean allowTcp) {
        this.commonOptions.add(AdminOption.ALLOW_TCP, allowTcp);
    }
    
    public void setAdminUdpPort(final int adminUdpPort) {
        if (adminUdpPort < 1) {
            throw new IllegalArgumentException("Invalid port");
        }
        this.commonOptions.add(AdminOption.ADMIN_UDP_PORT, adminUdpPort);
        this.setAllowUdp(true);
    }
    
    public void setTimeout(final int timeout) {
        this.commonOptions.add(AdminOption.CONN_TIMEOUT, timeout);
    }
    
    public void init() throws KrbException {
        (this.innerClient = new DefaultInternalAdminClient(this.adminSetting)).init();
    }
    
    public AdminSetting getSetting() {
        return this.adminSetting;
    }
    
    public AdminConfig getAdminConfig() {
        return this.adminConfig;
    }
    
    public void requestAddPrincipal(final String principal) throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        remote.addPrincipal(principal);
    }
    
    public void requestAddPrincipal(final String principal, final String password) throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        remote.addPrincipal(principal, password);
    }
    
    public void requestDeletePrincipal(final String principal) throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        remote.deletePrincipal(principal);
    }
    
    public void requestRenamePrincipal(final String oldPrincipal, final String newPrincipal) throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        remote.renamePrincipal(oldPrincipal, newPrincipal);
    }
    
    public List<String> requestGetprincs() throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        final List<String> principalLists = remote.getPrincipals();
        return principalLists;
    }
    
    public List<String> requestGetprincsWithExp(final String exp) throws KrbException {
        final Kadmin remote = new RemoteKadminImpl(this.innerClient);
        final List<String> principalLists = remote.getPrincipals(exp);
        return principalLists;
    }
}
