// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionGroup;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum PkinitOption implements KOption
{
    NONE((KOptionInfo)null), 
    USE_PKINIT(new KOptionInfo("use-pkinit", "using pkinit", KrbOptionGroup.PKINIT)), 
    X509_IDENTITY(new KOptionInfo("x509-identities", "X509 user private key and cert", KrbOptionGroup.PKINIT, KOptionType.STR)), 
    X509_PRIVATE_KEY(new KOptionInfo("x509-privatekey", "X509 user private key", KrbOptionGroup.PKINIT, KOptionType.STR)), 
    X509_CERTIFICATE(new KOptionInfo("x509-cert", "X509 user certificate", KrbOptionGroup.PKINIT, KOptionType.STR)), 
    X509_ANCHORS(new KOptionInfo("x509-anchors", "X509 anchors", KrbOptionGroup.PKINIT, KOptionType.STR)), 
    USING_RSA(new KOptionInfo("using-rsa-or-dh", "Using RSA or DH", KrbOptionGroup.PKINIT)), 
    USE_ANONYMOUS(new KOptionInfo("use-pkinit-anonymous", "X509 anonymous", KrbOptionGroup.PKINIT));
    
    private final KOptionInfo optionInfo;
    
    private PkinitOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static PkinitOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final PkinitOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return PkinitOption.NONE;
    }
}
