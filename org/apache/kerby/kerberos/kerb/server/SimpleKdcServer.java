// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.local.LocalKadminImpl;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.util.NetworkUtil;
import org.apache.kerby.kerberos.kerb.client.KrbConfig;
import org.apache.kerby.kerberos.kerb.client.KrbTokenClient;
import org.apache.kerby.kerberos.kerb.client.KrbPkinitClient;
import java.io.File;
import org.apache.kerby.kerberos.kerb.client.Krb5Conf;
import org.apache.kerby.kerberos.kerb.admin.kadmin.local.LocalKadmin;
import org.apache.kerby.kerberos.kerb.client.KrbClient;
import org.slf4j.Logger;

public class SimpleKdcServer extends KdcServer
{
    private static final Logger LOG;
    private final KrbClient krbClnt;
    private LocalKadmin kadmin;
    private Krb5Conf krb5Conf;
    private File workDir;
    private KrbPkinitClient pkinitClient;
    private KrbTokenClient tokenClient;
    
    public SimpleKdcServer() throws KrbException {
        this(new KrbConfig());
        this.setKdcRealm("EXAMPLE.COM");
        this.setKdcHost("localhost");
        this.setKdcPort(NetworkUtil.getServerPort());
    }
    
    public SimpleKdcServer(final KrbConfig krbConfig) {
        this.krbClnt = new KrbClient(krbConfig);
    }
    
    public SimpleKdcServer(final File confDir, final KrbConfig krbConfig) throws KrbException {
        super(confDir);
        this.krbClnt = new KrbClient(krbConfig);
    }
    
    @Override
    public synchronized void setWorkDir(final File workDir) {
        this.workDir = workDir;
    }
    
    public synchronized File getWorkDir() {
        return this.workDir;
    }
    
    @Override
    public synchronized void setKdcRealm(final String realm) {
        super.setKdcRealm(realm);
        this.krbClnt.setKdcRealm(realm);
    }
    
    @Override
    public synchronized void setKdcHost(final String kdcHost) {
        super.setKdcHost(kdcHost);
        this.krbClnt.setKdcHost(kdcHost);
    }
    
    @Override
    public synchronized void setKdcTcpPort(final int kdcTcpPort) {
        super.setKdcTcpPort(kdcTcpPort);
        this.krbClnt.setKdcTcpPort(kdcTcpPort);
        this.setAllowTcp(true);
    }
    
    @Override
    public synchronized void setAllowUdp(final boolean allowUdp) {
        super.setAllowUdp(allowUdp);
        this.krbClnt.setAllowUdp(allowUdp);
    }
    
    @Override
    public synchronized void setAllowTcp(final boolean allowTcp) {
        super.setAllowTcp(allowTcp);
        this.krbClnt.setAllowTcp(allowTcp);
    }
    
    @Override
    public synchronized void setKdcUdpPort(final int kdcUdpPort) {
        super.setKdcUdpPort(kdcUdpPort);
        this.krbClnt.setKdcUdpPort(kdcUdpPort);
        this.setAllowUdp(true);
    }
    
    @Override
    public synchronized void init() throws KrbException {
        super.init();
        (this.kadmin = new LocalKadminImpl(this.getKdcSetting(), this.getIdentityService())).createBuiltinPrincipals();
        try {
            (this.krb5Conf = new Krb5Conf(this)).initKrb5conf();
        }
        catch (IOException e) {
            throw new KrbException("Failed to make krb5.conf", e);
        }
    }
    
    @Override
    public synchronized void start() throws KrbException {
        super.start();
        this.krbClnt.init();
    }
    
    public synchronized KrbClient getKrbClient() {
        return this.krbClnt;
    }
    
    public synchronized KrbPkinitClient getPkinitClient() {
        if (this.pkinitClient == null) {
            this.pkinitClient = new KrbPkinitClient(this.krbClnt);
        }
        return this.pkinitClient;
    }
    
    public synchronized KrbTokenClient getTokenClient() {
        if (this.tokenClient == null) {
            this.tokenClient = new KrbTokenClient(this.krbClnt);
        }
        return this.tokenClient;
    }
    
    public synchronized LocalKadmin getKadmin() {
        return this.kadmin;
    }
    
    public synchronized void createPrincipal(final String principal) throws KrbException {
        this.kadmin.addPrincipal(principal);
    }
    
    public synchronized void createPrincipal(final String principal, final String password) throws KrbException {
        this.kadmin.addPrincipal(principal, password);
    }
    
    public synchronized void createPrincipals(final String... principals) throws KrbException {
        for (final String principal : principals) {
            this.kadmin.addPrincipal(principal);
        }
    }
    
    public synchronized void createAndExportPrincipals(final File keytabFile, final String... principals) throws KrbException {
        this.createPrincipals(principals);
        this.exportPrincipals(keytabFile);
    }
    
    public synchronized void deletePrincipals(final String... principals) throws KrbException {
        for (final String principal : principals) {
            this.deletePrincipal(principal);
        }
    }
    
    public synchronized void deletePrincipal(final String principal) throws KrbException {
        this.kadmin.deletePrincipal(principal);
    }
    
    public synchronized void exportPrincipals(final File keytabFile) throws KrbException {
        this.kadmin.exportKeytab(keytabFile);
    }
    
    public synchronized void exportPrincipal(final String principal, final File keytabFile) throws KrbException {
        this.kadmin.exportKeytab(keytabFile, principal);
    }
    
    @Override
    public synchronized void stop() throws KrbException {
        super.stop();
        try {
            this.krb5Conf.deleteKrb5conf();
        }
        catch (IOException e) {
            SimpleKdcServer.LOG.info("Fail to delete krb5 conf. " + e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SimpleKdcServer.class);
    }
}
