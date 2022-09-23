// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public enum CryptoProtocolVersion
{
    UNKNOWN("Unknown", 1), 
    ENCRYPTION_ZONES("Encryption zones", 2);
    
    private final String description;
    private final int version;
    private Integer unknownValue;
    private static CryptoProtocolVersion[] supported;
    
    public static CryptoProtocolVersion[] supported() {
        return CryptoProtocolVersion.supported;
    }
    
    private CryptoProtocolVersion(final String description, final int version) {
        this.unknownValue = null;
        this.description = description;
        this.version = version;
    }
    
    public static boolean supports(final CryptoProtocolVersion version) {
        if (version.getVersion() == CryptoProtocolVersion.UNKNOWN.getVersion()) {
            return false;
        }
        for (final CryptoProtocolVersion v : values()) {
            if (v.getVersion() == version.getVersion()) {
                return true;
            }
        }
        return false;
    }
    
    public void setUnknownValue(final int unknown) {
        this.unknownValue = unknown;
    }
    
    public int getUnknownValue() {
        return this.unknownValue;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public String toString() {
        return "CryptoProtocolVersion{description='" + this.description + '\'' + ", version=" + this.version + ", unknownValue=" + this.unknownValue + '}';
    }
    
    static {
        CryptoProtocolVersion.supported = new CryptoProtocolVersion[] { CryptoProtocolVersion.ENCRYPTION_ZONES };
    }
}
