// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.KOptionType;
import org.apache.kerby.KOptionInfo;
import org.apache.kerby.KOption;

public enum KdcServerOption implements KOption
{
    NONE((KOptionInfo)null), 
    INNER_KDC_IMPL(new KOptionInfo("inner KDC impl", "inner KDC impl", KOptionType.OBJ)), 
    KDC_REALM(new KOptionInfo("kdc realm", "kdc realm", KOptionType.STR)), 
    KDC_HOST(new KOptionInfo("kdc host", "kdc host", KOptionType.STR)), 
    KDC_PORT(new KOptionInfo("kdc port", "kdc port", KOptionType.INT)), 
    ALLOW_TCP(new KOptionInfo("allow tcp", "allow tcp", KOptionType.BOOL)), 
    KDC_TCP_PORT(new KOptionInfo("kdc tcp port", "kdc tcp port", KOptionType.INT)), 
    ALLOW_UDP(new KOptionInfo("allow udp", "allow udp", KOptionType.BOOL)), 
    KDC_UDP_PORT(new KOptionInfo("kdc udp port", "kdc udp port", KOptionType.INT)), 
    WORK_DIR(new KOptionInfo("work dir", "work dir", KOptionType.DIR)), 
    ENABLE_DEBUG(new KOptionInfo("enable debug", "enable debug", KOptionType.BOOL));
    
    private final KOptionInfo optionInfo;
    
    private KdcServerOption(final KOptionInfo optionInfo) {
        this.optionInfo = optionInfo;
    }
    
    @Override
    public KOptionInfo getOptionInfo() {
        return this.optionInfo;
    }
}
