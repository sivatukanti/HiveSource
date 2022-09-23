// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.kerberos.kerb.server.impl.DefaultInternalKdcServerImpl;
import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.KOption;
import java.io.File;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.impl.InternalKdcServer;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;

public class KdcServer
{
    private final KdcConfig kdcConfig;
    private final BackendConfig backendConfig;
    private final KdcSetting kdcSetting;
    private final KOptions startupOptions;
    private InternalKdcServer innerKdc;
    
    public KdcServer(final KdcConfig kdcConfig, final BackendConfig backendConfig) throws KrbException {
        this.kdcConfig = kdcConfig;
        this.backendConfig = backendConfig;
        this.startupOptions = new KOptions();
        this.kdcSetting = new KdcSetting(this.startupOptions, kdcConfig, backendConfig);
    }
    
    public KdcServer(final File confDir) throws KrbException {
        KdcConfig tmpKdcConfig = KdcUtil.getKdcConfig(confDir);
        if (tmpKdcConfig == null) {
            tmpKdcConfig = new KdcConfig();
        }
        this.kdcConfig = tmpKdcConfig;
        BackendConfig tmpBackendConfig = KdcUtil.getBackendConfig(confDir);
        if (tmpBackendConfig == null) {
            tmpBackendConfig = new BackendConfig();
        }
        tmpBackendConfig.setConfDir(confDir);
        this.backendConfig = tmpBackendConfig;
        this.startupOptions = new KOptions();
        this.kdcSetting = new KdcSetting(this.startupOptions, this.kdcConfig, this.backendConfig);
    }
    
    public KdcServer() {
        this.kdcConfig = new KdcConfig();
        this.backendConfig = new BackendConfig();
        this.startupOptions = new KOptions();
        this.kdcSetting = new KdcSetting(this.startupOptions, this.kdcConfig, this.backendConfig);
    }
    
    public void setKdcRealm(final String realm) {
        this.startupOptions.add(KdcServerOption.KDC_REALM, realm);
    }
    
    public void setKdcHost(final String kdcHost) {
        this.startupOptions.add(KdcServerOption.KDC_HOST, kdcHost);
    }
    
    public void setKdcPort(final int kdcPort) {
        this.startupOptions.add(KdcServerOption.KDC_PORT, kdcPort);
    }
    
    public int getKdcPort() {
        final KOption option = this.startupOptions.getOption(KdcServerOption.KDC_PORT);
        if (option != null) {
            return (int)option.getOptionInfo().getValue();
        }
        return 0;
    }
    
    public void setKdcTcpPort(final int kdcTcpPort) {
        this.startupOptions.add(KdcServerOption.KDC_TCP_PORT, kdcTcpPort);
    }
    
    public int getKdcTcpPort() {
        final KOption option = this.startupOptions.getOption(KdcServerOption.KDC_TCP_PORT);
        if (option != null) {
            return (int)option.getOptionInfo().getValue();
        }
        return 0;
    }
    
    public void setAllowUdp(final boolean allowUdp) {
        this.startupOptions.add(KdcServerOption.ALLOW_UDP, allowUdp);
    }
    
    public void setAllowTcp(final boolean allowTcp) {
        this.startupOptions.add(KdcServerOption.ALLOW_TCP, allowTcp);
    }
    
    public void setKdcUdpPort(final int kdcUdpPort) {
        this.startupOptions.add(KdcServerOption.KDC_UDP_PORT, kdcUdpPort);
    }
    
    public int getKdcUdpPort() {
        final KOption option = this.startupOptions.getOption(KdcServerOption.KDC_UDP_PORT);
        if (option != null) {
            return (int)option.getOptionInfo().getValue();
        }
        return 0;
    }
    
    public void setWorkDir(final File workDir) {
        this.startupOptions.add(KdcServerOption.WORK_DIR, workDir);
    }
    
    public void enableDebug() {
        this.startupOptions.add(KdcServerOption.ENABLE_DEBUG);
    }
    
    public void setInnerKdcImpl(final InternalKdcServer innerKdcImpl) {
        this.startupOptions.add(KdcServerOption.INNER_KDC_IMPL, innerKdcImpl);
    }
    
    public KdcSetting getKdcSetting() {
        return this.kdcSetting;
    }
    
    public KdcConfig getKdcConfig() {
        return this.kdcConfig;
    }
    
    public BackendConfig getBackendConfig() {
        return this.backendConfig;
    }
    
    public IdentityBackend getIdentityService() {
        if (this.innerKdc == null) {
            throw new RuntimeException("Not init yet");
        }
        return this.innerKdc.getIdentityBackend();
    }
    
    public void init() throws KrbException {
        if (this.startupOptions.contains(KdcServerOption.INNER_KDC_IMPL)) {
            this.innerKdc = (InternalKdcServer)this.startupOptions.getOptionValue(KdcServerOption.INNER_KDC_IMPL);
        }
        else {
            this.innerKdc = new DefaultInternalKdcServerImpl(this.kdcSetting);
        }
        this.innerKdc.init();
    }
    
    public void start() throws KrbException {
        if (this.innerKdc == null) {
            throw new RuntimeException("Not init yet");
        }
        this.innerKdc.start();
    }
    
    public void stop() throws KrbException {
        if (this.innerKdc != null) {
            this.innerKdc.stop();
        }
    }
}
