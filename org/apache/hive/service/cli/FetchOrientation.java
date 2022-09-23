// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TFetchOrientation;

public enum FetchOrientation
{
    FETCH_NEXT(TFetchOrientation.FETCH_NEXT), 
    FETCH_PRIOR(TFetchOrientation.FETCH_PRIOR), 
    FETCH_RELATIVE(TFetchOrientation.FETCH_RELATIVE), 
    FETCH_ABSOLUTE(TFetchOrientation.FETCH_ABSOLUTE), 
    FETCH_FIRST(TFetchOrientation.FETCH_FIRST), 
    FETCH_LAST(TFetchOrientation.FETCH_LAST);
    
    private TFetchOrientation tFetchOrientation;
    
    private FetchOrientation(final TFetchOrientation tFetchOrientation) {
        this.tFetchOrientation = tFetchOrientation;
    }
    
    public static FetchOrientation getFetchOrientation(final TFetchOrientation tFetchOrientation) {
        for (final FetchOrientation fetchOrientation : values()) {
            if (tFetchOrientation.equals(fetchOrientation.toTFetchOrientation())) {
                return fetchOrientation;
            }
        }
        return FetchOrientation.FETCH_NEXT;
    }
    
    public TFetchOrientation toTFetchOrientation() {
        return this.tFetchOrientation;
    }
}
