// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient;

public class RedirectException extends ProtocolException
{
    public RedirectException() {
    }
    
    public RedirectException(final String message) {
        super(message);
    }
    
    public RedirectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
