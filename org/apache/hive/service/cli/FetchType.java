// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

public enum FetchType
{
    QUERY_OUTPUT((short)0), 
    LOG((short)1);
    
    private final short tFetchType;
    
    private FetchType(final short tFetchType) {
        this.tFetchType = tFetchType;
    }
    
    public static FetchType getFetchType(final short tFetchType) {
        for (final FetchType fetchType : values()) {
            if (tFetchType == fetchType.toTFetchType()) {
                return fetchType;
            }
        }
        return FetchType.QUERY_OUTPUT;
    }
    
    public short toTFetchType() {
        return this.tFetchType;
    }
}
