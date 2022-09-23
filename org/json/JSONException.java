// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

public class JSONException extends Exception
{
    private Throwable cause;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
