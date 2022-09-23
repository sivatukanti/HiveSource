// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jettison.json;

public class JSONException extends Exception
{
    private Throwable cause;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
