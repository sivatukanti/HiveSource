// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.config.ConfigKey;
import org.apache.kerby.kerberos.kerb.common.Krb5Conf;

public class KrbConfig extends Krb5Conf
{
    private static final String LIBDEFAULT = "libdefaults";
    private static final String REALMS = "realms";
    
    public boolean enableDebug() {
        return this.getBoolean(KrbConfigKey.KRB_DEBUG, true, "libdefaults");
    }
    
    public String getKdcHost() {
        return this.getString(KrbConfigKey.KDC_HOST, true, "libdefaults");
    }
    
    public int getKdcPort() {
        final Integer kdcPort = this.getInt(KrbConfigKey.KDC_PORT, true, "libdefaults");
        if (kdcPort != null) {
            return kdcPort;
        }
        return -1;
    }
    
    public int getKdcTcpPort() {
        final Integer kdcPort = this.getInt(KrbConfigKey.KDC_TCP_PORT, true, "libdefaults");
        if (kdcPort != null && kdcPort > 0) {
            return kdcPort;
        }
        return this.getKdcPort();
    }
    
    public boolean allowUdp() {
        return this.getBoolean(KrbConfigKey.KDC_ALLOW_UDP, true, "libdefaults") || this.getInt(KrbConfigKey.KDC_UDP_PORT, true, "libdefaults") != null || this.getInt(KrbConfigKey.KDC_PORT, false, "libdefaults") != null;
    }
    
    public boolean allowTcp() {
        return this.getBoolean(KrbConfigKey.KDC_ALLOW_TCP, true, "libdefaults") || this.getInt(KrbConfigKey.KDC_TCP_PORT, true, "libdefaults") != null || this.getInt(KrbConfigKey.KDC_PORT, false, "libdefaults") != null;
    }
    
    public int getKdcUdpPort() {
        final Integer kdcPort = this.getInt(KrbConfigKey.KDC_UDP_PORT, true, "libdefaults");
        if (kdcPort != null && kdcPort > 0) {
            return kdcPort;
        }
        return this.getKdcPort();
    }
    
    public String getKdcRealm() {
        String realm = this.getString(KrbConfigKey.KDC_REALM, false, "libdefaults");
        if (realm == null) {
            realm = this.getString(KrbConfigKey.DEFAULT_REALM, false, "libdefaults");
            if (realm == null) {
                realm = (String)KrbConfigKey.KDC_REALM.getDefaultValue();
            }
        }
        return realm;
    }
    
    public boolean isPreauthRequired() {
        return this.getBoolean(KrbConfigKey.PREAUTH_REQUIRED, true, "libdefaults");
    }
    
    public String getTgsPrincipal() {
        return this.getString(KrbConfigKey.TGS_PRINCIPAL, true, "libdefaults");
    }
    
    public long getAllowableClockSkew() {
        return this.getLong(KrbConfigKey.CLOCKSKEW, true, "libdefaults");
    }
    
    public boolean isEmptyAddressesAllowed() {
        return this.getBoolean(KrbConfigKey.EMPTY_ADDRESSES_ALLOWED, true, "libdefaults");
    }
    
    public boolean isForwardableAllowed() {
        return this.getBoolean(KrbConfigKey.FORWARDABLE, true, "libdefaults");
    }
    
    public boolean isPostdatedAllowed() {
        return this.getBoolean(KrbConfigKey.POSTDATED_ALLOWED, true, "libdefaults");
    }
    
    public boolean isProxiableAllowed() {
        return this.getBoolean(KrbConfigKey.PROXIABLE, true, "libdefaults");
    }
    
    public boolean isRenewableAllowed() {
        return this.getBoolean(KrbConfigKey.RENEWABLE_ALLOWED, true, "libdefaults");
    }
    
    public long getMaximumRenewableLifetime() {
        return this.getLong(KrbConfigKey.MAXIMUM_RENEWABLE_LIFETIME, true, "libdefaults");
    }
    
    public long getMaximumTicketLifetime() {
        return this.getLong(KrbConfigKey.MAXIMUM_TICKET_LIFETIME, true, "libdefaults");
    }
    
    public long getMinimumTicketLifetime() {
        return this.getLong(KrbConfigKey.MINIMUM_TICKET_LIFETIME, true, "libdefaults");
    }
    
    public List<EncryptionType> getEncryptionTypes() {
        return this.getEncTypes(KrbConfigKey.PERMITTED_ENCTYPES, true, "libdefaults");
    }
    
    public boolean isPaEncTimestampRequired() {
        return this.getBoolean(KrbConfigKey.PA_ENC_TIMESTAMP_REQUIRED, true, "libdefaults");
    }
    
    public boolean isBodyChecksumVerified() {
        return this.getBoolean(KrbConfigKey.VERIFY_BODY_CHECKSUM, true, "libdefaults");
    }
    
    public String getDefaultRealm() {
        return this.getString(KrbConfigKey.DEFAULT_REALM, true, "libdefaults");
    }
    
    public boolean getDnsLookUpKdc() {
        return this.getBoolean(KrbConfigKey.DNS_LOOKUP_KDC, true, "libdefaults");
    }
    
    public boolean getDnsLookUpRealm() {
        return this.getBoolean(KrbConfigKey.DNS_LOOKUP_REALM, true, "libdefaults");
    }
    
    public boolean getAllowWeakCrypto() {
        return this.getBoolean(KrbConfigKey.ALLOW_WEAK_CRYPTO, true, "libdefaults");
    }
    
    public long getTicketLifetime() {
        return this.getLong(KrbConfigKey.TICKET_LIFETIME, true, "libdefaults");
    }
    
    public long getRenewLifetime() {
        return this.getLong(KrbConfigKey.RENEW_LIFETIME, true, "libdefaults");
    }
    
    public List<EncryptionType> getDefaultTgsEnctypes() {
        return this.getEncTypes(KrbConfigKey.DEFAULT_TGS_ENCTYPES, true, "libdefaults");
    }
    
    public List<EncryptionType> getDefaultTktEnctypes() {
        return this.getEncTypes(KrbConfigKey.DEFAULT_TKT_ENCTYPES, true, "libdefaults");
    }
    
    public List<String> getPkinitAnchors() {
        return Arrays.asList(this.getStringArray(KrbConfigKey.PKINIT_ANCHORS, true, "libdefaults"));
    }
    
    public List<String> getPkinitIdentities() {
        return Arrays.asList(this.getStringArray(KrbConfigKey.PKINIT_IDENTITIES, true, "libdefaults"));
    }
    
    public String getPkinitKdcHostName() {
        return this.getString(KrbConfigKey.PKINIT_KDC_HOSTNAME, true, "libdefaults");
    }
    
    public List<Object> getRealmSectionItems(final String realm, final String key) {
        final Map<String, Object> map = this.getRealmSection(realm);
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Object> items = new ArrayList<Object>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals(key)) {
                items.add(entry.getValue());
            }
        }
        return items;
    }
    
    public Map<String, Object> getRealmSection(final String realm) {
        final Object realms = this.getSection("realms");
        if (realms != null) {
            final Map<String, Object> map = (Map<String, Object>)realms;
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals(realm)) {
                    return entry.getValue();
                }
            }
        }
        return Collections.emptyMap();
    }
}
