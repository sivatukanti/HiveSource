// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

public class HiveMetaException extends Exception
{
    public HiveMetaException() {
    }
    
    public HiveMetaException(final String message) {
        super(message);
    }
    
    public HiveMetaException(final Throwable cause) {
        super(cause);
    }
    
    public HiveMetaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
