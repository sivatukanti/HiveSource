// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.io.IOException;

public class HttpException extends IOException
{
    int _status;
    String _reason;
    
    public HttpException(final int status) {
        this._status = status;
        this._reason = null;
    }
    
    public HttpException(final int status, final String reason) {
        this._status = status;
        this._reason = reason;
    }
    
    public HttpException(final int status, final String reason, final Throwable rootCause) {
        this._status = status;
        this._reason = reason;
        this.initCause(rootCause);
    }
    
    public String getReason() {
        return this._reason;
    }
    
    public void setReason(final String reason) {
        this._reason = reason;
    }
    
    public int getStatus() {
        return this._status;
    }
    
    public void setStatus(final int status) {
        this._status = status;
    }
    
    @Override
    public String toString() {
        return "HttpException(" + this._status + "," + this._reason + "," + super.getCause() + ")";
    }
}
