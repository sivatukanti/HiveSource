// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

public class ZooKeeperHiveClientException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public ZooKeeperHiveClientException(final Throwable cause) {
        super(cause);
    }
    
    public ZooKeeperHiveClientException(final String msg) {
        super(msg);
    }
    
    public ZooKeeperHiveClientException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
