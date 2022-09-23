// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum AdminOption implements KOption
{
    NONE((KOptionInfo)null), 
    ADMIN_REALM(new KOptionInfo("admin-realm", "kdc realm", KOptionType.STR)), 
    ADMIN_HOST(new KOptionInfo("admin-host", "kdc host", KOptionType.STR)), 
    ADMIN_TCP_PORT(new KOptionInfo("admin-tcp-port", "kdc tcp port", KOptionType.INT)), 
    ALLOW_UDP(new KOptionInfo("allow-udp", "allow udp", KOptionType.BOOL)), 
    ALLOW_TCP(new KOptionInfo("allow-tcp", "allow tcp", KOptionType.BOOL)), 
    ADMIN_UDP_PORT(new KOptionInfo("admin-udp-port", "kdc udp port", KOptionType.INT)), 
    CONN_TIMEOUT(new KOptionInfo("conn-timeout", "connection timeout", KOptionType.INT)), 
    LIFE_TIME(new KOptionInfo("life-time", "life time", KOptionType.INT)), 
    START_TIME(new KOptionInfo("start-time", "start time", KOptionType.INT)), 
    RENEWABLE_TIME(new KOptionInfo("renewable_lifetime", "renewable lifetime", KOptionType.INT)), 
    INCLUDE_ADDRESSES(new KOptionInfo("include_addresses", "include addresses")), 
    AS_ENTERPRISE_PN(new KOptionInfo("as-enterprise-pn", "client is enterprise principal name")), 
    CLIENT_PRINCIPAL(new KOptionInfo("client-principal", "Client principal", KOptionType.STR)), 
    USE_PASSWD(new KOptionInfo("using-password", "using password")), 
    USER_PASSWD(new KOptionInfo("user-passwd", "User plain password")), 
    USE_KEYTAB(new KOptionInfo("use-keytab", "use keytab")), 
    USE_DFT_KEYTAB(new KOptionInfo("use-dft-keytab", "use default client keytab (with -k)")), 
    KEYTAB_FILE(new KOptionInfo("keytab-file", "filename of keytab to use", KOptionType.FILE)), 
    KRB5_CACHE(new KOptionInfo("krb5-cache", "K5 cache name", KOptionType.FILE)), 
    SERVICE_PRINCIPAL(new KOptionInfo("service-principal", "service principal", KOptionType.STR)), 
    SERVER_PRINCIPAL(new KOptionInfo("admin-principal", "admin principal", KOptionType.STR)), 
    ARMOR_CACHE(new KOptionInfo("armor-cache", "armor credential cache", KOptionType.STR)), 
    USE_TGT(new KOptionInfo("use-tgt", "use tgt to get service ticket", KOptionType.OBJ)), 
    CONF_DIR(new KOptionInfo("-conf", "conf dir", KOptionType.DIR));
    
    private final KOptionInfo optionInfo;
    
    private AdminOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static AdminOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final AdminOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return AdminOption.NONE;
    }
}
