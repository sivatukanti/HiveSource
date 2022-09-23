// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

public class ServiceException extends RuntimeException
{
    public ServiceException(final Throwable cause) {
        super(cause);
    }
    
    public ServiceException(final String message) {
        super(message);
    }
    
    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
