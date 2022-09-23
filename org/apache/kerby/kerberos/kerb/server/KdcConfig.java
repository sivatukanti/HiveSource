// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.config.ConfigKey;
import org.apache.kerby.kerberos.kerb.common.Krb5Conf;

public class KdcConfig extends Krb5Conf
{
    private static final String KDCDEFAULT = "kdcdefaults";
    
    public boolean enableDebug() {
        return this.getBoolean(KdcConfigKey.KRB_DEBUG, true, "kdcdefaults");
    }
    
    public String getKdcServiceName() {
        return this.getString(KdcConfigKey.KDC_SERVICE_NAME, true, "kdcdefaults");
    }
    
    public String getKdcHost() {
        return this.getString(KdcConfigKey.KDC_HOST, true, "kdcdefaults");
    }
    
    public int getKdcPort() {
        final Integer kdcPort = this.getInt(KdcConfigKey.KDC_PORT, true, "kdcdefaults");
        if (kdcPort != null && kdcPort > 0) {
            return kdcPort;
        }
        return -1;
    }
    
    public int getKdcTcpPort() {
        final Integer kdcTcpPort = this.getInt(KdcConfigKey.KDC_TCP_PORT, true, "kdcdefaults");
        if (kdcTcpPort != null && kdcTcpPort > 0) {
            return kdcTcpPort;
        }
        return this.getKdcPort();
    }
    
    public Boolean allowTcp() {
        return this.getBoolean(KdcConfigKey.KDC_ALLOW_TCP, true, "kdcdefaults") || this.getInt(KdcConfigKey.KDC_TCP_PORT, true, "kdcdefaults") != null || this.getInt(KdcConfigKey.KDC_PORT, false, "kdcdefaults") != null;
    }
    
    public Boolean allowUdp() {
        return this.getBoolean(KdcConfigKey.KDC_ALLOW_UDP, true, "kdcdefaults") || this.getInt(KdcConfigKey.KDC_UDP_PORT, true, "kdcdefaults") != null || this.getInt(KdcConfigKey.KDC_PORT, false, "kdcdefaults") != null;
    }
    
    public int getKdcUdpPort() {
        final Integer kdcUdpPort = this.getInt(KdcConfigKey.KDC_UDP_PORT, true, "kdcdefaults");
        if (kdcUdpPort != null && kdcUdpPort > 0) {
            return kdcUdpPort;
        }
        return this.getKdcPort();
    }
    
    public String getKdcRealm() {
        return this.getString(KdcConfigKey.KDC_REALM, true, "kdcdefaults");
    }
    
    public String getKdcDomain() {
        return this.getString(KdcConfigKey.KDC_DOMAIN, true, "kdcdefaults");
    }
    
    public boolean isPreauthRequired() {
        return this.getBoolean(KdcConfigKey.PREAUTH_REQUIRED, true, "kdcdefaults");
    }
    
    public boolean isAllowTokenPreauth() {
        return this.getBoolean(KdcConfigKey.ALLOW_TOKEN_PREAUTH, true, "kdcdefaults");
    }
    
    public long getAllowableClockSkew() {
        return this.getLong(KdcConfigKey.ALLOWABLE_CLOCKSKEW, true, "kdcdefaults");
    }
    
    public boolean isEmptyAddressesAllowed() {
        return this.getBoolean(KdcConfigKey.EMPTY_ADDRESSES_ALLOWED, true, "kdcdefaults");
    }
    
    public boolean isForwardableAllowed() {
        return this.getBoolean(KdcConfigKey.FORWARDABLE_ALLOWED, true, "kdcdefaults");
    }
    
    public boolean isPostdatedAllowed() {
        return this.getBoolean(KdcConfigKey.POSTDATED_ALLOWED, true, "kdcdefaults");
    }
    
    public boolean isProxiableAllowed() {
        return this.getBoolean(KdcConfigKey.PROXIABLE_ALLOWED, true, "kdcdefaults");
    }
    
    public boolean isRenewableAllowed() {
        return this.getBoolean(KdcConfigKey.RENEWABLE_ALLOWED, true, "kdcdefaults");
    }
    
    public long getMaximumRenewableLifetime() {
        return this.getLong(KdcConfigKey.MAXIMUM_RENEWABLE_LIFETIME, true, "kdcdefaults");
    }
    
    public long getMaximumTicketLifetime() {
        return this.getLong(KdcConfigKey.MAXIMUM_TICKET_LIFETIME, true, "kdcdefaults");
    }
    
    public long getMinimumTicketLifetime() {
        return this.getLong(KdcConfigKey.MINIMUM_TICKET_LIFETIME, true, "kdcdefaults");
    }
    
    public List<EncryptionType> getEncryptionTypes() {
        return this.getEncTypes(KdcConfigKey.ENCRYPTION_TYPES, true, "kdcdefaults");
    }
    
    public boolean isPaEncTimestampRequired() {
        return this.getBoolean(KdcConfigKey.PA_ENC_TIMESTAMP_REQUIRED, true, "kdcdefaults");
    }
    
    public boolean isBodyChecksumVerified() {
        return this.getBoolean(KdcConfigKey.VERIFY_BODY_CHECKSUM, true, "kdcdefaults");
    }
    
    public boolean isRestrictAnonymousToTgt() {
        return this.getBoolean(KdcConfigKey.RESTRICT_ANONYMOUS_TO_TGT, true, "kdcdefaults");
    }
    
    public int getKdcMaxDgramReplySize() {
        return this.getInt(KdcConfigKey.KDC_MAX_DGRAM_REPLY_SIZE, true, "kdcdefaults");
    }
    
    public String getVerifyKeyConfig() {
        return this.getString(KdcConfigKey.TOKEN_VERIFY_KEYS, true, "kdcdefaults");
    }
    
    public String getDecryptionKeyConfig() {
        return this.getString(KdcConfigKey.TOKEN_DECRYPTION_KEYS, true, "kdcdefaults");
    }
    
    public List<String> getIssuers() {
        return Arrays.asList(this.getStringArray(KdcConfigKey.TOKEN_ISSUERS, true, "kdcdefaults"));
    }
    
    public List<String> getPkinitAnchors() {
        return Arrays.asList(this.getString(KdcConfigKey.PKINIT_ANCHORS, true, "kdcdefaults"));
    }
    
    public String getPkinitIdentity() {
        return this.getString(KdcConfigKey.PKINIT_IDENTITY, true, "kdcdefaults");
    }
}
