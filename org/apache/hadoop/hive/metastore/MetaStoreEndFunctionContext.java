// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

public class MetaStoreEndFunctionContext
{
    private final boolean success;
    private final Exception e;
    private final String inputTableName;
    
    public MetaStoreEndFunctionContext(final boolean success, final Exception e, final String inputTableName) {
        this.success = success;
        this.e = e;
        this.inputTableName = inputTableName;
    }
    
    public MetaStoreEndFunctionContext(final boolean success) {
        this(success, null, null);
    }
    
    public boolean isSuccess() {
        return this.success;
    }
    
    public Exception getException() {
        return this.e;
    }
    
    public String getInputTableName() {
        return this.inputTableName;
    }
}
