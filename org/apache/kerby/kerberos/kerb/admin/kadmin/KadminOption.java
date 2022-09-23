// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin;

import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum KadminOption implements KOption
{
    NONE((KOptionInfo)null), 
    EXPIRE(new KOptionInfo("-expire", "expire time", KOptionType.DATE)), 
    DISABLED(new KOptionInfo("-disabled", "disabled", KOptionType.BOOL)), 
    LOCKED(new KOptionInfo("-locked", "locked", KOptionType.BOOL)), 
    FORCE(new KOptionInfo("-force", "force", KOptionType.NOV)), 
    KVNO(new KOptionInfo("-kvno", "initial key version number", KOptionType.INT)), 
    SIZE(new KOptionInfo("-size", "principal's numbers", KOptionType.STR)), 
    PW(new KOptionInfo("-pw", "password", KOptionType.STR)), 
    RANDKEY(new KOptionInfo("-randkey", "random key", KOptionType.NOV)), 
    KEEPOLD(new KOptionInfo("-keepold", "keep old passowrd", KOptionType.NOV)), 
    KEYSALTLIST(new KOptionInfo("-e", "key saltlist", KOptionType.STR)), 
    K(new KOptionInfo("-k", "keytab file path", KOptionType.STR)), 
    KEYTAB(new KOptionInfo("-keytab", "keytab file path", KOptionType.STR)), 
    CCACHE(new KOptionInfo("-c", "credentials cache", KOptionType.FILE));
    
    private final KOptionInfo optionInfo;
    
    private KadminOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static KadminOption fromName(final String name) {
        if (name != null) {
            for (final KadminOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(name)) {
                    return ko;
                }
            }
        }
        return KadminOption.NONE;
    }
    
    public static KadminOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final KadminOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return KadminOption.NONE;
    }
}
