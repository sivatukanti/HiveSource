// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOptionGroup;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum KrbKdcOption implements KOption
{
    NONE((KOptionInfo)null), 
    FORWARDABLE(new KOptionInfo("-f", "forwardable", KrbOptionGroup.KDC_FLAGS)), 
    NOT_FORWARDABLE(new KOptionInfo("-F", "not forwardable", KrbOptionGroup.KDC_FLAGS)), 
    PROXIABLE(new KOptionInfo("-p", "proxiable", KrbOptionGroup.KDC_FLAGS)), 
    NOT_PROXIABLE(new KOptionInfo("-P", "not proxiable", KrbOptionGroup.KDC_FLAGS)), 
    REQUEST_ANONYMOUS(new KOptionInfo("-n", "request anonymous", KrbOptionGroup.KDC_FLAGS)), 
    VALIDATE(new KOptionInfo("-v", "validate", KrbOptionGroup.KDC_FLAGS)), 
    RENEW(new KOptionInfo("-R", "renew", KrbOptionGroup.KDC_FLAGS)), 
    RENEWABLE(new KOptionInfo("-r", "renewable-life", KrbOptionGroup.KDC_FLAGS)), 
    RENEWABLE_OK(new KOptionInfo("renewable-ok", "renewable ok", KrbOptionGroup.KDC_FLAGS)), 
    CANONICALIZE(new KOptionInfo("-C", "canonicalize", KrbOptionGroup.KDC_FLAGS)), 
    ANONYMOUS(new KOptionInfo("-n", "anonymous", KrbOptionGroup.KDC_FLAGS));
    
    private final KOptionInfo optionInfo;
    
    private KrbKdcOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static KrbKdcOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final KrbKdcOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return KrbKdcOption.NONE;
    }
}
