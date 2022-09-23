// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum TokenOption implements KOption
{
    NONE((KOptionInfo)null), 
    USE_TOKEN(new KOptionInfo("use-id-token", "Using identity token")), 
    USER_ID_TOKEN(new KOptionInfo("user-id-token", "User identity token", KOptionType.STR)), 
    USER_AC_TOKEN(new KOptionInfo("user-ac-token", "User access token", KOptionType.STR));
    
    private final KOptionInfo optionInfo;
    
    private TokenOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
    
    public static TokenOption fromOptionName(final String optionName) {
        if (optionName != null) {
            for (final TokenOption ko : values()) {
                if (ko.optionInfo != null && ko.optionInfo.getName().equals(optionName)) {
                    return ko;
                }
            }
        }
        return TokenOption.NONE;
    }
}
