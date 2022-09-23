// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

public enum CmsMessageType
{
    UNKNOWN(-1), 
    CMS_SIGN_CLIENT(1), 
    CMS_SIGN_SERVER(3), 
    CMS_ENVEL_SERVER(4);
    
    private int value;
    
    private CmsMessageType(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static CmsMessageType fromValue(final int value) {
        switch (value) {
            case 1: {
                return CmsMessageType.CMS_SIGN_CLIENT;
            }
            case 3: {
                return CmsMessageType.CMS_SIGN_SERVER;
            }
            case 4: {
                return CmsMessageType.CMS_ENVEL_SERVER;
            }
            default: {
                return CmsMessageType.UNKNOWN;
            }
        }
    }
}
