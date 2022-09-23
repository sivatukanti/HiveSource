// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOptionGroup;
import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum KrbOption implements KOption
{
    NONE((KOptionInfo)null), 
    KDC_REALM(new KOptionInfo("kdc-realm", "kdc realm", KOptionType.STR)), 
    KDC_HOST(new KOptionInfo("kdc-host", "kdc host", KOptionType.STR)), 
    KDC_TCP_PORT(new KOptionInfo("kdc-tcp-port", "kdc tcp port", KOptionType.INT)), 
    ALLOW_UDP(new KOptionInfo("allow-udp", "allow udp", KOptionType.BOOL)), 
    ALLOW_TCP(new KOptionInfo("allow-tcp", "allow tcp", KOptionType.BOOL)), 
    KDC_UDP_PORT(new KOptionInfo("kdc-udp-port", "kdc udp port", KOptionType.INT)), 
    CONN_TIMEOUT(new KOptionInfo("conn-timeout", "connection timeout", KOptionType.INT)), 
    LIFE_TIME(new KOptionInfo("-l", "life time", KOptionType.INT)), 
    START_TIME(new KOptionInfo("-s", "start time", KOptionType.INT)), 
    RENEWABLE_TIME(new KOptionInfo("-r", "renewable lifetime", KOptionType.INT)), 
    INCLUDE_ADDRESSES(new KOptionInfo("include_addresses", "include addresses")), 
    AS_ENTERPRISE_PN(new KOptionInfo("as-enterprise-pn", "client is enterprise principal name")), 
    CLIENT_PRINCIPAL(new KOptionInfo("client-principal", "Client principal", KOptionType.STR)), 
    USE_PASSWD(new KOptionInfo("using-password", "using password")), 
    USER_PASSWD(new KOptionInfo("user-passwd", "User plain password")), 
    USE_KEYTAB(new KOptionInfo("-k", "use keytab")), 
    USE_DFT_KEYTAB(new KOptionInfo("-i", "use default client keytab (with -k)")), 
    KEYTAB_FILE(new KOptionInfo("-t", "filename of keytab to use", KOptionType.FILE)), 
    KRB5_CACHE(new KOptionInfo("krb5-cache", "K5 cache name", KOptionType.FILE)), 
    SERVICE_PRINCIPAL(new KOptionInfo("service-principal", "service principal", KOptionType.STR)), 
    SERVER_PRINCIPAL(new KOptionInfo("server-principal", "server principal", KOptionType.STR)), 
    ARMOR_CACHE(new KOptionInfo("armor-cache", "armor credential cache", KOptionType.STR)), 
    TGT(new KOptionInfo("tgt", "tgt ticket", KOptionType.OBJ)), 
    USE_TGT(new KOptionInfo("use-tgt", "use tgt to get service ticket", KOptionType.OBJ)), 
    CONF_DIR(new KOptionInfo("-conf", "conf dir", KrbOptionGroup.KRB, KOptionType.DIR));
    
    private final KOptionInfo optionInfo;
    
    private KrbOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static KrbOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final KrbOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return KrbOption.NONE;
    }
}
