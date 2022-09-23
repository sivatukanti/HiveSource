// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.KOption;
import org.apache.kerby.KOptions;

public class AdminSetting
{
    private final KOptions commonOptions;
    private final AdminConfig adminConfig;
    
    public AdminSetting(final KOptions commonOptions, final AdminConfig config) {
        this.commonOptions = commonOptions;
        this.adminConfig = config;
    }
    
    public AdminSetting(final AdminConfig config) {
        this.commonOptions = new KOptions();
        this.adminConfig = config;
    }
    
    public AdminConfig getAdminConfig() {
        return this.adminConfig;
    }
    
    public String getKdcRealm() {
        String kdcRealm = this.commonOptions.getStringOption(AdminOption.ADMIN_REALM);
        if (kdcRealm == null || kdcRealm.isEmpty()) {
            kdcRealm = this.adminConfig.getAdminRealm();
        }
        return kdcRealm;
    }
    
    public String getKdcHost() {
        final String kdcHost = this.commonOptions.getStringOption(AdminOption.ADMIN_HOST);
        if (kdcHost == null) {
            return this.adminConfig.getAdminHost();
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
        final int tcpPort = this.commonOptions.getIntegerOption(AdminOption.ADMIN_TCP_PORT);
        if (tcpPort > 0) {
            return tcpPort;
        }
        return this.adminConfig.getAdminTcpPort();
    }
    
    public boolean allowUdp() {
        final Boolean allowUdp = this.commonOptions.getBooleanOption(AdminOption.ALLOW_UDP, this.adminConfig.allowUdp());
        return allowUdp;
    }
    
    public boolean allowTcp() {
        final Boolean allowTcp = this.commonOptions.getBooleanOption(AdminOption.ALLOW_TCP, this.adminConfig.allowTcp());
        return allowTcp;
    }
    
    public int getKdcUdpPort() {
        final int udpPort = this.commonOptions.getIntegerOption(AdminOption.ADMIN_UDP_PORT);
        if (udpPort > 0) {
            return udpPort;
        }
        return this.adminConfig.getAdminUdpPort();
    }
    
    public int getTimeout() {
        final int timeout = this.commonOptions.getIntegerOption(AdminOption.CONN_TIMEOUT);
        if (timeout > 0) {
            return timeout;
        }
        return 1000;
    }
}
