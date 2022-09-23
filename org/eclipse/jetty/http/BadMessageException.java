// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

public class BadMessageException extends RuntimeException
{
    final int _code;
    final String _reason;
    
    public BadMessageException() {
        this(400, null);
    }
    
    public BadMessageException(final int code) {
        this(code, null);
    }
    
    public BadMessageException(final String reason) {
        this(400, reason);
    }
    
    public BadMessageException(final String reason, final Throwable cause) {
        this(400, reason, cause);
    }
    
    public BadMessageException(final int code, final String reason) {
        super(code + ": " + reason);
        this._code = code;
        this._reason = reason;
    }
    
    public BadMessageException(final int code, final String reason, final Throwable cause) {
        super(code + ": " + reason, cause);
        this._code = code;
        this._reason = reason;
    }
    
    public int getCode() {
        return this._code;
    }
    
    public String getReason() {
        return this._reason;
    }
}
