// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient;

public class ConnectionPoolTimeoutException extends ConnectTimeoutException
{
    public ConnectionPoolTimeoutException() {
    }
    
    public ConnectionPoolTimeoutException(final String message) {
        super(message);
    }
    
    public ConnectionPoolTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
