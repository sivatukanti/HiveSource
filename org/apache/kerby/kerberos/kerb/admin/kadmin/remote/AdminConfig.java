// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import org.apache.kerby.config.ConfigKey;
import org.apache.kerby.kerberos.kerb.common.Krb5Conf;

public class AdminConfig extends Krb5Conf
{
    private static final String LIBDEFAULT = "libdefaults";
    
    public boolean enableDebug() {
        return this.getBoolean(AdminConfigKey.KRB_DEBUG, true, "libdefaults");
    }
    
    public String getAdminHost() {
        return this.getString(AdminConfigKey.ADMIN_HOST, true, "libdefaults");
    }
    
    public int getAdminPort() {
        final Integer kdcPort = this.getInt(AdminConfigKey.ADMIN_PORT, true, "libdefaults");
        if (kdcPort != null) {
            return kdcPort;
        }
        return -1;
    }
    
    public int getAdminTcpPort() {
        final Integer kdcPort = this.getInt(AdminConfigKey.ADMIN_TCP_PORT, true, "libdefaults");
        if (kdcPort != null && kdcPort > 0) {
            return kdcPort;
        }
        return this.getAdminPort();
    }
    
    public boolean allowUdp() {
        return this.getBoolean(AdminConfigKey.ADMIN_ALLOW_UDP, true, "libdefaults") || this.getInt(AdminConfigKey.ADMIN_UDP_PORT, true, "libdefaults") != null || this.getInt(AdminConfigKey.ADMIN_PORT, false, "libdefaults") != null;
    }
    
    public boolean allowTcp() {
        return this.getBoolean(AdminConfigKey.ADMIN_ALLOW_TCP, true, "libdefaults") || this.getInt(AdminConfigKey.ADMIN_TCP_PORT, true, "libdefaults") != null || this.getInt(AdminConfigKey.ADMIN_PORT, false, "libdefaults") != null;
    }
    
    public int getAdminUdpPort() {
        final Integer kdcPort = this.getInt(AdminConfigKey.ADMIN_UDP_PORT, true, "libdefaults");
        if (kdcPort != null && kdcPort > 0) {
            return kdcPort;
        }
        return this.getAdminPort();
    }
    
    public String getAdminRealm() {
        String realm = this.getString(AdminConfigKey.ADMIN_REALM, false, "libdefaults");
        if (realm == null) {
            realm = this.getString(AdminConfigKey.DEFAULT_REALM, false, "libdefaults");
            if (realm == null) {
                realm = (String)AdminConfigKey.ADMIN_REALM.getDefaultValue();
            }
        }
        return realm;
    }
    
    public String getKeyTabFile() {
        return this.getString(AdminConfigKey.KEYTAB_FILE, true, "libdefaults");
    }
    
    public String getProtocol() {
        return this.getString(AdminConfigKey.PROTOCOL, true, "libdefaults");
    }
    
    public String getServerName() {
        return this.getString(AdminConfigKey.SERVER_NAME, true, "libdefaults");
    }
}
