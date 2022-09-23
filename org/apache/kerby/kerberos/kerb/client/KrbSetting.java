// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.KOption;
import org.apache.kerby.KOptions;

public class KrbSetting
{
    private final KOptions commonOptions;
    private final KrbConfig krbConfig;
    
    public KrbSetting(final KOptions commonOptions, final KrbConfig config) {
        this.commonOptions = commonOptions;
        this.krbConfig = config;
    }
    
    public KrbSetting(final KrbConfig config) {
        this.commonOptions = new KOptions();
        this.krbConfig = config;
    }
    
    public KrbConfig getKrbConfig() {
        return this.krbConfig;
    }
    
    public String getKdcRealm() {
        String kdcRealm = this.commonOptions.getStringOption(KrbOption.KDC_REALM);
        if (kdcRealm == null || kdcRealm.isEmpty()) {
            kdcRealm = this.krbConfig.getKdcRealm();
        }
        return kdcRealm;
    }
    
    public String getKdcHost() {
        final String kdcHost = this.commonOptions.getStringOption(KrbOption.KDC_HOST);
        if (kdcHost == null) {
            return this.krbConfig.getKdcHost();
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
        final int tcpPort = this.commonOptions.getIntegerOption(KrbOption.KDC_TCP_PORT);
        if (tcpPort > 0) {
            return tcpPort;
        }
        return this.krbConfig.getKdcTcpPort();
    }
    
    public boolean allowUdp() {
        final Boolean allowUdp = this.commonOptions.getBooleanOption(KrbOption.ALLOW_UDP, this.krbConfig.allowUdp());
        return allowUdp;
    }
    
    public boolean allowTcp() {
        final Boolean allowTcp = this.commonOptions.getBooleanOption(KrbOption.ALLOW_TCP, this.krbConfig.allowTcp());
        return allowTcp;
    }
    
    public int getKdcUdpPort() {
        final int udpPort = this.commonOptions.getIntegerOption(KrbOption.KDC_UDP_PORT);
        if (udpPort > 0) {
            return udpPort;
        }
        return this.krbConfig.getKdcUdpPort();
    }
    
    public int getTimeout() {
        final int timeout = this.commonOptions.getIntegerOption(KrbOption.CONN_TIMEOUT);
        if (timeout > 0) {
            return timeout;
        }
        return 1000;
    }
}
