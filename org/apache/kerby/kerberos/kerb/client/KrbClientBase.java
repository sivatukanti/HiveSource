// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.kdc.EncAsRepPart;
import org.apache.kerby.kerberos.kerb.ccache.CredentialCache;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.ccache.Credential;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import org.apache.kerby.kerberos.kerb.client.impl.DefaultInternalKrbClient;
import org.apache.kerby.KOption;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.client.impl.InternalKrbClient;
import org.apache.kerby.KOptions;

public class KrbClientBase
{
    private final KrbConfig krbConfig;
    private final KOptions commonOptions;
    private final KrbSetting krbSetting;
    private InternalKrbClient innerClient;
    private static final Logger LOG;
    
    public KrbClientBase() throws KrbException {
        this.krbConfig = ClientUtil.getDefaultConfig();
        this.commonOptions = new KOptions();
        this.krbSetting = new KrbSetting(this.commonOptions, this.krbConfig);
    }
    
    public KrbClientBase(final KrbConfig krbConfig) {
        this.krbConfig = krbConfig;
        this.commonOptions = new KOptions();
        this.krbSetting = new KrbSetting(this.commonOptions, krbConfig);
    }
    
    public KrbClientBase(final File confDir) throws KrbException {
        this.commonOptions = new KOptions();
        this.krbConfig = ClientUtil.getConfig(confDir);
        this.krbSetting = new KrbSetting(this.commonOptions, this.krbConfig);
    }
    
    public KrbClientBase(final KrbClientBase krbClient) {
        this.commonOptions = krbClient.commonOptions;
        this.krbConfig = krbClient.krbConfig;
        this.krbSetting = krbClient.krbSetting;
        this.innerClient = krbClient.innerClient;
    }
    
    public void setKdcRealm(final String realm) {
        this.commonOptions.add(KrbOption.KDC_REALM, realm);
    }
    
    public void setKdcHost(final String kdcHost) {
        this.commonOptions.add(KrbOption.KDC_HOST, kdcHost);
    }
    
    public void setKdcTcpPort(final int kdcTcpPort) {
        if (kdcTcpPort < 1) {
            throw new IllegalArgumentException("Invalid port");
        }
        this.commonOptions.add(KrbOption.KDC_TCP_PORT, kdcTcpPort);
        this.setAllowTcp(true);
    }
    
    public void setAllowUdp(final boolean allowUdp) {
        this.commonOptions.add(KrbOption.ALLOW_UDP, allowUdp);
    }
    
    public void setAllowTcp(final boolean allowTcp) {
        this.commonOptions.add(KrbOption.ALLOW_TCP, allowTcp);
    }
    
    public void setKdcUdpPort(final int kdcUdpPort) {
        if (kdcUdpPort < 1) {
            throw new IllegalArgumentException("Invalid port");
        }
        this.commonOptions.add(KrbOption.KDC_UDP_PORT, kdcUdpPort);
        this.setAllowUdp(true);
    }
    
    public void setTimeout(final int timeout) {
        this.commonOptions.add(KrbOption.CONN_TIMEOUT, timeout);
    }
    
    public void init() throws KrbException {
        (this.innerClient = new DefaultInternalKrbClient(this.krbSetting)).init();
    }
    
    public KrbSetting getSetting() {
        return this.krbSetting;
    }
    
    public KrbConfig getKrbConfig() {
        return this.krbConfig;
    }
    
    public TgtTicket requestTgt(final KOptions requestOptions) throws KrbException {
        if (requestOptions == null) {
            throw new IllegalArgumentException("Null requestOptions specified");
        }
        return this.innerClient.requestTgt(requestOptions);
    }
    
    public SgtTicket requestSgt(final TgtTicket tgt, final String serverPrincipal) throws KrbException {
        final KOptions requestOptions = new KOptions();
        requestOptions.add(KrbOption.USE_TGT, tgt);
        requestOptions.add(KrbOption.SERVER_PRINCIPAL, serverPrincipal);
        return this.innerClient.requestSgt(requestOptions);
    }
    
    public SgtTicket requestSgt(final KOptions requestOptions) throws KrbException {
        return this.innerClient.requestSgt(requestOptions);
    }
    
    public SgtTicket requestSgt(final File ccFile) throws KrbException {
        final Credential credential = this.getCredentialFromFile(ccFile);
        final String servicePrincipal = credential.getServicePrincipal().getName();
        final TgtTicket tgt = this.getTgtTicketFromCredential(credential);
        final KOptions requestOptions = new KOptions();
        requestOptions.add(KrbKdcOption.RENEW);
        requestOptions.add(KrbOption.USE_TGT, tgt);
        requestOptions.add(KrbOption.SERVER_PRINCIPAL, servicePrincipal);
        final SgtTicket sgtTicket = this.innerClient.requestSgt(requestOptions);
        sgtTicket.setClientPrincipal(tgt.getClientPrincipal());
        return sgtTicket;
    }
    
    public void storeTicket(final TgtTicket tgtTicket, final File ccacheFile) throws KrbException {
        KrbClientBase.LOG.info("Storing the tgt to the credential cache file.");
        if (!ccacheFile.exists()) {
            try {
                if (!ccacheFile.createNewFile()) {
                    throw new KrbException("Failed to create ccache file " + ccacheFile.getAbsolutePath());
                }
                ccacheFile.setReadable(false, false);
                ccacheFile.setReadable(true, true);
                if (!ccacheFile.setWritable(true, true)) {
                    throw new KrbException("Cache file is not readable.");
                }
            }
            catch (IOException e) {
                throw new KrbException("Failed to create ccache file " + ccacheFile.getAbsolutePath(), e);
            }
        }
        if (ccacheFile.exists() && ccacheFile.canWrite()) {
            final CredentialCache cCache = new CredentialCache(tgtTicket);
            try {
                cCache.store(ccacheFile);
            }
            catch (IOException e2) {
                throw new KrbException("Failed to store tgt", e2);
            }
            return;
        }
        throw new IllegalArgumentException("Invalid ccache file, not exist or writable: " + ccacheFile.getAbsolutePath());
    }
    
    public void storeTicket(final SgtTicket sgtTicket, final File ccacheFile) throws KrbException {
        KrbClientBase.LOG.info("Storing the sgt to the credential cache file.");
        if (!ccacheFile.exists()) {
            try {
                if (!ccacheFile.createNewFile()) {
                    throw new KrbException("Failed to create ccache file " + ccacheFile.getAbsolutePath());
                }
                ccacheFile.setReadable(false, false);
                ccacheFile.setReadable(true, true);
                if (!ccacheFile.setWritable(true, true)) {
                    throw new KrbException("Cache file is not readable.");
                }
            }
            catch (IOException e) {
                throw new KrbException("Failed to create ccache file " + ccacheFile.getAbsolutePath(), e);
            }
        }
        if (ccacheFile.exists() && ccacheFile.canWrite()) {
            final CredentialCache cCache = new CredentialCache(sgtTicket);
            try {
                cCache.store(ccacheFile);
            }
            catch (IOException e2) {
                throw new KrbException("Failed to store tgt", e2);
            }
            return;
        }
        throw new IllegalArgumentException("Invalid ccache file, not exist or writable: " + ccacheFile.getAbsolutePath());
    }
    
    public TgtTicket getTgtTicketFromCredential(final Credential cc) {
        final EncAsRepPart encAsRepPart = new EncAsRepPart();
        encAsRepPart.setAuthTime(cc.getAuthTime());
        encAsRepPart.setCaddr(cc.getClientAddresses());
        encAsRepPart.setEndTime(cc.getEndTime());
        encAsRepPart.setFlags(cc.getTicketFlags());
        encAsRepPart.setKey(cc.getKey());
        encAsRepPart.setRenewTill(cc.getRenewTill());
        encAsRepPart.setSname(cc.getServerName());
        encAsRepPart.setSrealm(cc.getServerName().getRealm());
        encAsRepPart.setStartTime(cc.getStartTime());
        final TgtTicket tgtTicket = new TgtTicket(cc.getTicket(), encAsRepPart, cc.getClientName());
        return tgtTicket;
    }
    
    public Credential getCredentialFromFile(final File ccFile) throws KrbException {
        CredentialCache cc;
        try {
            cc = this.resolveCredCache(ccFile);
        }
        catch (IOException e) {
            throw new KrbException("Failed to load armor cache file");
        }
        return cc.getCredentials().iterator().next();
    }
    
    public CredentialCache resolveCredCache(final File ccacheFile) throws IOException {
        final CredentialCache cc = new CredentialCache();
        cc.load(ccacheFile);
        return cc;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KrbClientBase.class);
    }
}
