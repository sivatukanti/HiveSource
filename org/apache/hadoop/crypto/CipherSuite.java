// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public enum CipherSuite
{
    UNKNOWN("Unknown", 0), 
    AES_CTR_NOPADDING("AES/CTR/NoPadding", 16);
    
    private final String name;
    private final int algoBlockSize;
    private Integer unknownValue;
    
    private CipherSuite(final String name, final int algoBlockSize) {
        this.unknownValue = null;
        this.name = name;
        this.algoBlockSize = algoBlockSize;
    }
    
    public void setUnknownValue(final int unknown) {
        this.unknownValue = unknown;
    }
    
    public int getUnknownValue() {
        return this.unknownValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getAlgorithmBlockSize() {
        return this.algoBlockSize;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        builder.append("name: " + this.name);
        builder.append(", algorithmBlockSize: " + this.algoBlockSize);
        if (this.unknownValue != null) {
            builder.append(", unknownValue: " + this.unknownValue);
        }
        builder.append("}");
        return builder.toString();
    }
    
    public static CipherSuite convert(final String name) {
        final CipherSuite[] values;
        final CipherSuite[] suites = values = values();
        for (final CipherSuite suite : values) {
            if (suite.getName().equals(name)) {
                return suite;
            }
        }
        throw new IllegalArgumentException("Invalid cipher suite name: " + name);
    }
    
    public String getConfigSuffix() {
        final String[] parts = this.name.split("/");
        final StringBuilder suffix = new StringBuilder();
        for (final String part : parts) {
            suffix.append(".").append(StringUtils.toLowerCase(part));
        }
        return suffix.toString();
    }
}
