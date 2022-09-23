// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

public class TSaslTransportException extends TTransportException
{
    public TSaslTransportException() {
    }
    
    public TSaslTransportException(final String message) {
        super(message);
    }
    
    public TSaslTransportException(final Throwable cause) {
        super(cause);
    }
    
    public TSaslTransportException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
