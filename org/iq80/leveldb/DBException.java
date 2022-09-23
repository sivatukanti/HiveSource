// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

public class DBException extends RuntimeException
{
    public DBException() {
    }
    
    public DBException(final String s) {
        super(s);
    }
    
    public DBException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public DBException(final Throwable throwable) {
        super(throwable);
    }
}
