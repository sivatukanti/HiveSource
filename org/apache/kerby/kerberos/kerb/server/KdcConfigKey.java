// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.config.ConfigKey;

public enum KdcConfigKey implements ConfigKey
{
    KRB_DEBUG((Object)true), 
    KDC_SERVICE_NAME((Object)"KDC-Server"), 
    KDC_IDENTITY_BACKEND, 
    KDC_HOST((Object)"127.0.0.1"), 
    KDC_PORT, 
    KDC_ALLOW_TCP((Object)true), 
    KDC_ALLOW_UDP((Object)true), 
    KDC_UDP_PORT, 
    KDC_TCP_PORT, 
    KDC_DOMAIN((Object)"example.com"), 
    KDC_REALM((Object)"EXAMPLE.COM"), 
    PREAUTH_REQUIRED((Object)true), 
    ALLOW_TOKEN_PREAUTH((Object)true), 
    ALLOWABLE_CLOCKSKEW((Object)300L), 
    EMPTY_ADDRESSES_ALLOWED((Object)true), 
    PA_ENC_TIMESTAMP_REQUIRED((Object)true), 
    MAXIMUM_TICKET_LIFETIME((Object)86400L), 
    MINIMUM_TICKET_LIFETIME((Object)3600L), 
    MAXIMUM_RENEWABLE_LIFETIME((Object)172800L), 
    FORWARDABLE_ALLOWED((Object)true), 
    POSTDATED_ALLOWED((Object)true), 
    PROXIABLE_ALLOWED((Object)true), 
    RENEWABLE_ALLOWED((Object)true), 
    VERIFY_BODY_CHECKSUM((Object)true), 
    ENCRYPTION_TYPES((Object)"aes128-cts-hmac-sha1-96 des3-cbc-sha1-kd"), 
    RESTRICT_ANONYMOUS_TO_TGT((Object)false), 
    KDC_MAX_DGRAM_REPLY_SIZE((Object)4096), 
    TOKEN_VERIFY_KEYS, 
    TOKEN_DECRYPTION_KEYS, 
    TOKEN_ISSUERS, 
    PKINIT_IDENTITY((Object)null), 
    PKINIT_ANCHORS((Object)null);
    
    private Object defaultValue;
    
    private KdcConfigKey() {
        this.defaultValue = null;
    }
    
    private KdcConfigKey(final Object defaultValue) {
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
