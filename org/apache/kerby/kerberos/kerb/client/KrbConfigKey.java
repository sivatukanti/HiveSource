// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.config.ConfigKey;

public enum KrbConfigKey implements ConfigKey
{
    KRB_DEBUG((Object)true), 
    KDC_HOST((Object)"localhost"), 
    KDC_PORT((Object)null), 
    KDC_ALLOW_UDP((Object)false), 
    KDC_ALLOW_TCP((Object)false), 
    KDC_UDP_PORT((Object)null), 
    KDC_TCP_PORT((Object)null), 
    KDC_DOMAIN((Object)"example.com"), 
    KDC_REALM((Object)"EXAMPLE.COM"), 
    TGS_PRINCIPAL((Object)"krbtgt@EXAMPLE.COM"), 
    PREAUTH_REQUIRED((Object)true), 
    CLOCKSKEW((Object)300L), 
    EMPTY_ADDRESSES_ALLOWED((Object)true), 
    PA_ENC_TIMESTAMP_REQUIRED((Object)true), 
    MAXIMUM_TICKET_LIFETIME((Object)86400L), 
    MINIMUM_TICKET_LIFETIME((Object)3600L), 
    MAXIMUM_RENEWABLE_LIFETIME((Object)172800L), 
    FORWARDABLE((Object)true), 
    POSTDATED_ALLOWED((Object)true), 
    PROXIABLE((Object)true), 
    RENEWABLE_ALLOWED((Object)true), 
    VERIFY_BODY_CHECKSUM((Object)true), 
    PERMITTED_ENCTYPES((Object)"aes128-cts-hmac-sha1-96"), 
    DEFAULT_REALM((Object)null), 
    DNS_LOOKUP_KDC((Object)false), 
    DNS_LOOKUP_REALM((Object)false), 
    ALLOW_WEAK_CRYPTO((Object)true), 
    TICKET_LIFETIME((Object)86400L), 
    RENEW_LIFETIME((Object)172800L), 
    DEFAULT_TGS_ENCTYPES((Object)"aes256-cts-hmac-sha1-96 aes128-cts-hmac-sha1-96 des3-cbc-sha1 arcfour-hmac-md5 camellia256-cts-cmac camellia128-cts-cmac des-cbc-crc des-cbc-md5 des-cbc-md4"), 
    DEFAULT_TKT_ENCTYPES((Object)"aes256-cts-hmac-sha1-96 aes128-cts-hmac-sha1-96 des3-cbc-sha1 arcfour-hmac-md5 camellia256-cts-cmac camellia128-cts-cmac des-cbc-crc des-cbc-md5 des-cbc-md4"), 
    PKINIT_ANCHORS((Object)null), 
    PKINIT_IDENTITIES((Object)null), 
    PKINIT_KDC_HOSTNAME;
    
    private Object defaultValue;
    
    private KrbConfigKey() {
        this.defaultValue = null;
    }
    
    private KrbConfigKey(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getPropertyKey() {
        return this.name().toLowerCase();
    }
    
    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }
}
