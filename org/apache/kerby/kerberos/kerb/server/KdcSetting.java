// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import org.apache.kerby.KOptions;

public class KdcSetting implements ServerSetting
{
    private final KOptions startupOptions;
    private final KdcConfig kdcConfig;
    private final BackendConfig backendConfig;
    
    public KdcSetting(final KOptions startupOptions, final KdcConfig config, final BackendConfig backendConfig) {
        this.startupOptions = startupOptions;
        this.kdcConfig = config;
        this.backendConfig = backendConfig;
    }
    
    public KdcSetting(final KdcConfig kdcConfig, final BackendConfig backendConfig) {
        this(new KOptions(), kdcConfig, backendConfig);
    }
    
    @Override
    public KdcConfig getKdcConfig() {
        return this.kdcConfig;
    }
    
    @Override
    public BackendConfig getBackendConfig() {
        return this.backendConfig;
    }
    
    public String getKdcHost() {
        String kdcHost = this.startupOptions.getStringOption(KdcServerOption.KDC_HOST);
        if (kdcHost == null) {
            kdcHost = this.kdcConfig.getKdcHost();
        }
        return kdcHost;
    }
    
    public int checkGetKdcTcpPort() throws KrbException {
        if (!this.allowTcp()) {
            return -1;
        }
        final int kdcPort = this.getKdcTcpPort();
        if (kdcPort < 1) {
            throw new KrbException("KDC tcp port isn't set or configured");
        }
        return kdcPort;
    }
    
    public int checkGetKdcUdpPort() throws KrbException {
        if (!this.allowUdp()) {
            return -1;
        }
        final int kdcPort = this.getKdcUdpPort();
        if (kdcPort < 1) {
            throw new KrbException("KDC udp port isn't set or configured");
        }
        return kdcPort;
    }
    
    public int getKdcTcpPort() {
        int tcpPort = this.startupOptions.getIntegerOption(KdcServerOption.KDC_TCP_PORT);
        if (tcpPort < 1) {
            tcpPort = this.kdcConfig.getKdcTcpPort();
        }
        if (tcpPort < 1) {
            tcpPort = this.getKdcPort();
        }
        return tcpPort;
    }
    
    public int getKdcPort() {
        int kdcPort = this.startupOptions.getIntegerOption(KdcServerOption.KDC_PORT);
        if (kdcPort < 1) {
            kdcPort = this.kdcConfig.getKdcPort();
        }
        return kdcPort;
    }
    
    public boolean allowTcp() {
        final Boolean allowTcp = this.startupOptions.getBooleanOption(KdcServerOption.ALLOW_TCP, this.kdcConfig.allowTcp());
        return allowTcp;
    }
    
    public boolean allowUdp() {
        final Boolean allowUdp = this.startupOptions.getBooleanOption(KdcServerOption.ALLOW_UDP, this.kdcConfig.allowUdp());
        return allowUdp;
    }
    
    public int getKdcUdpPort() {
        int udpPort = this.startupOptions.getIntegerOption(KdcServerOption.KDC_UDP_PORT);
        if (udpPort < 1) {
            udpPort = this.kdcConfig.getKdcUdpPort();
        }
        if (udpPort < 1) {
            udpPort = this.getKdcPort();
        }
        return udpPort;
    }
    
    @Override
    public String getKdcRealm() {
        String kdcRealm = this.startupOptions.getStringOption(KdcServerOption.KDC_REALM);
        if (kdcRealm == null || kdcRealm.isEmpty()) {
            kdcRealm = this.kdcConfig.getKdcRealm();
        }
        return kdcRealm;
    }
}
